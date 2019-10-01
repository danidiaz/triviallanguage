package com.softtek.truffle.tl.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <p>A rudimentary parser combinator library.</p>
 *
 * @param <T> The result type of the parser.
 */
public final class Parser<T> {

    /**
     * <p>An internal functional interface that receives as arguments values related to the current state of the parse,
     * and returns the corresponding {@link ParseResult}</p>.
     *
     * @param <X> the type of the parsed result.
     */
    @FunctionalInterface
    private interface ParseFunction<X> {
        /**
         * @param symbols A symbol table in which top-level definitions can be declared. Values are {@link Object}s
         *                instead of some generic type because the generic type would complicate the
         *                {@link Parser} interface and worsen type inference.
         * @param cs      A {@link CharSequence} containing the text that must be parsed.
         * @param offset  The position in the {@link CharSequence} from which to start the parse.
         * @return The result of the parse.
         */
        ParseResult<X> apply(Map<String, Object> symbols, CharSequence cs, Integer offset);
    }

    private ParseFunction<T> parseFunction;

    private static <T> Parser<T> from(ParseFunction<T> parseFunction) {
        return new Parser<T>(parseFunction);
    }

    private Parser(ParseFunction<T> parseFunction) {
        this.parseFunction = parseFunction;
    }

    public T parse(CharSequence cs) {
        return parseFunction.apply(new HashMap<>(), cs, 0).value;
    }

    public static Parser<Void> putSymbol(CharSequence name, Object value) {
        return from((symbols, cs, offset) -> {
            symbols.put(name.toString(), value);
            return new ParseResult<Void>(null, offset);
        });
    }

    /**
     * @param name         Symbol name.
     * @param desiredClass Runtime representation of the expected class of the value.
     * @param <T>          Expected class of the value.
     * @return The symbol's value.
     * @throws ParseException If the symbol is not registered or has an incompatible type.
     */
    public static <T> Parser<T> getSymbol(CharSequence name, Class<T> desiredClass) {
        return from((symbols, cs, offset) -> {
            final Object value = symbols.get(name.toString());
            if (value == null) {
                throw new ParseException(String.format("Could not find symbol %s.", name));
            }
            if (!desiredClass.isInstance(value)) {
                throw new ParseException(String.format("Value belongs to unexpected class %s.", desiredClass));
            }
            return new ParseResult<T>(desiredClass.cast(value), offset);
        });
    }

    public Parser<T> andThenRegister(Function<T, String> nameGetter) {
        return this.andThen(result ->
            putSymbol(nameGetter.apply(result), result)
                .discardAndThen(pure(result)));
    }

    public static Parser<Integer> getCurrentPos() {
        return from((symbols, cs, offset) -> new ParseResult<Integer>(offset, offset));
    }

    public static <R> Parser<R> withStartAndEndPos(Parser<BiFunction<Integer, Integer, R>> wantsPositions) {
        return skipBlanksAndThen(
            getCurrentPos().andThen(startPos ->
                wantsPositions.andThen(positionConsumer ->
                    getCurrentPos().andThen(endPos ->
                        pure(positionConsumer.apply(startPos, endPos))))));
    }

    public static <R> Parser<R> pure(R pureValue) {
        return from((symbols, cs, offset) -> new ParseResult<R>(pureValue, offset));
    }

    public static Parser<Void> unit() {
        return pure(null);
    }

    public final <R> Parser<R> andThen(Function<T, Parser<R>> mapper) {
        return new Parser<R>((symbols, cs, offset) -> {
            final ParseResult<T> innerResult = parseFunction.apply(symbols, cs, offset);
            return mapper.apply(innerResult.value).parseFunction.apply(symbols, cs, innerResult.newOffset);
        });
    }

    public final <R> Parser<R> discardAndThen(Parser<R> after) {
        return this.andThen(x -> after);
    }


    public final <R> Parser<T> andThenDiscard(Parser<R> after) {
        return this.andThen(x -> after.discardAndThen(pure(x)));
    }

    public final Parser<T> orElse(Parser<T> alternative) {
        return new Parser<T>((symbols, cs, offset) -> {
            try {
                return parseFunction.apply(symbols, cs, offset);
            } catch (ParseException oopsFirstBranchFailed) {
                return alternative.parseFunction.apply(symbols, cs, offset);
            }
        });
    }

    public final Parser<List<T>> many() {
        return from((symbols, cs, offset) -> {
            final List<T> results = new ArrayList<>();
            Integer currentOffset = offset;
            try {
                while (true) {
                    final ParseResult<T> currentResult =
                        parseFunction.apply(symbols, cs, currentOffset);
                    results.add(currentResult.value);
                    currentOffset = currentResult.newOffset;
                }
            } catch (ParseException oopsFailedToParseSome) {
                return new ParseResult<>(results, currentOffset);
            }
        });
    }

    public Parser<List<T>> sepBy(Parser<Void> separator) {
        return this.sepBy1(separator).orElse(pure(new ArrayList<>()));
    }

    public Parser<List<T>> sepBy1(Parser<Void> separator) {
        return this.andThen(first -> separator
            .discardAndThen(this)
            .many()
            .andThen(rest -> {
                final List<T> result = new ArrayList<>();
                result.add(first);
                result.addAll(rest);
                return pure(result);
            }));
    }

    public static Parser<Void> parseEOF() {
        return from((symbols, cs, offset) -> {
            if (cs.length() > offset) {
                throw new ParseException("expected EOF");
            }
            return new ParseResult<Void>(null, offset);
        });
    }

    public static Parser<Void> skipBlanks() {
        return from((symbols, cs, offset) -> {
            int index = offset;
            for (; index < cs.length(); index++) {
                if (!Character.isSpaceChar(cs.charAt(index))) {
                    break;
                }
            }
            return new ParseResult<Void>(null, index);
        });
    }

    public static <R> Parser<R> skipBlanksAndThen(Parser<R> next) {
        return skipBlanks().discardAndThen(next);
    }

    public Parser<T> surroundedBy(Parser<Void> before, Parser<Void> after) {
        return before.discardAndThen(this.andThenDiscard(after));
    }

    public static Parser<Void> parseChar(char c) {
        return from((symbols, cs, offset) -> {
            if (cs.length() <= offset) {
                throw new ParseException("Unexpected EOF");
            }
            if (cs.charAt(offset) != c) {
                throw new ParseException(String.format("Could not parse char %c", c));
            }
            return new ParseResult<Void>(null, offset + 1);
        });
    }

    public static Parser<Void> parseKeyword(CharSequence keyword) {
        return skipBlanksAndThen(from((symbols, cs, offset) -> {
            if (cs.length() - offset < keyword.length()) {
                throw new ParseException("Could not parse keyword.");
            }
            int index = offset;
            int keywordIndex = 0;
            for (; keywordIndex < keyword.length(); keywordIndex++, index++) {
                if (keyword.charAt(keywordIndex) != cs.charAt(index)) {
                    throw new ParseException("Could not parse keyword.");
                }
            }
            return new ParseResult<Void>(null, index);
        }));
    }

    public static Parser<CharSequence> parseIdentifier() {
        return skipBlanksAndThen(from((symbols, cs, offset) -> {
            int index = offset;
            for (; index < cs.length(); index++) {
                if (!Character.isLetter(cs.charAt(index))) {
                    break;
                }
            }
            if (index == offset) {
                throw new ParseException("Identifier excepted");
            }
            return new ParseResult<CharSequence>(cs.subSequence(offset, index), index);
        }));
    }

    public static Parser<CharSequence> parseQuotedString() {
        return skipBlanksAndThen(from((symbols, cs, offset) -> {
            if (cs.length() <= offset) {
                throw new ParseException("Unexpected EOF");
            }
            if (cs.charAt(offset) != '\'') {
                throw new ParseException("Opening quote not found.");
            }
            int index = offset + 1;
            for (; index < cs.length(); index++) {
                if (cs.charAt(index) == '\'') {
                    break;
                }
            }
            if (cs.charAt(index) != '\'') {
                throw new ParseException("Could not find closing quote.");
            }
            return new ParseResult<CharSequence>(cs.subSequence(offset + 1, index), index + 1);
        }));
    }

    public static Parser<Integer> parseInteger() {
        return skipBlanksAndThen(from((symbols, cs, offset) -> {
            int index = offset;
            for (; index < cs.length(); index++) {
                if (!Character.isDigit(cs.charAt(index))) {
                    break;
                }
            }
            if (index == offset) {
                throw new ParseException("Could not find starting digit.");
            }
            final Integer parsedInteger = Integer.parseInt(cs.subSequence(offset, index).toString());
            return new ParseResult<Integer>(parsedInteger, index);
        }));
    }

}
