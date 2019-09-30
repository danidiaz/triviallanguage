package com.softtek.truffle.tl.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <p>A rudimentary parser combinator library.</p>
 *
 * @param <T> The result type of the parser.
 */
public final class Parser<T> {

	/**
	 * <p>{@link Function} that takes as parameters a {@link CharSequence} and an {@link Integer} that
	 * represents the position within the {@link CharSequence} from which we should start parsing.</p>
	 */
	private BiFunction<CharSequence, Integer, ParseResult<T>> innerFunction;

	public static <T> Parser<T> from(BiFunction<CharSequence, Integer, ParseResult<T>> innerFunction) {
		return new Parser<T>(innerFunction);
	}

	private Parser(BiFunction<CharSequence, Integer, ParseResult<T>> innerFunction) {
		this.innerFunction = innerFunction;
	}

	public T parse(CharSequence cs) {
		return innerFunction.apply(cs,0).value;
	}

	public static Parser<Integer> getCurrentPos() {
		return from((cs,offset) -> new ParseResult<Integer>(offset,offset));
	}

	public static <R> Parser<R> withStartAndEndPos(Parser<BiFunction<Integer,Integer,R>> wantsPositions) {
		return skipBlanksAndThen(
				getCurrentPos().andThen(startPos ->
						wantsPositions.andThen(positionConsumer ->
								getCurrentPos().andThen(endPos ->
										pure(positionConsumer.apply(startPos, endPos))))));
	}

	public static <R> Parser<R> pure(R pureValue) {
		return from((cs,offset) -> new ParseResult<R>(pureValue, offset));
	}

	public static Parser<Void> unit() {
		return pure(null);
	}

	public final <R> Parser<R> andThen(Function<T, Parser<R>> mapper) {
		return new Parser<R>((cs,offset) -> {
			final ParseResult<T> innerResult = innerFunction.apply(cs,offset);
			return mapper.apply(innerResult.value).innerFunction.apply(cs,innerResult.newOffset);
		});
	}

	public final <R> Parser<R> discardAndThen(Parser<R> after) {
		return this.andThen(x -> after);
	}


	public final <R> Parser<T> andThenDiscard(Parser<R> after) {
		return this.andThen(x -> after.discardAndThen(pure(x)));
	}

	public final Parser<T> orElse(Parser<T> alternative) {
		return new Parser<T>((cs,offset) -> {
			try {
				return innerFunction.apply(cs,offset);
			} catch (ParseException oopsFirstBranchFailed) {
				return alternative.innerFunction.apply(cs,offset);
			}
		});
	}

	public final Parser<List<T>> many() {
		return from((cs,offset) -> {
			final List<T> results = new ArrayList<>();
			Integer currentOffset = offset;
			try {
				while (true) {
					final ParseResult<T> currentResult =
							innerFunction.apply(cs,currentOffset);
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
		return from((cs,offset) -> {
			if (cs.length() > offset) {
				throw new ParseException("expected EOF");
			}
			return new ParseResult<Void>(null, offset);
		});
	}

	public static Parser<Void> skipBlanks() {
		return from((cs,offset) -> {
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
		return from((cs,offset) -> {
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
		return skipBlanksAndThen(from((cs,offset) -> {
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
		return skipBlanksAndThen(from((cs,offset) -> {
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
		return skipBlanksAndThen(from((cs,offset) -> {
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
		return skipBlanksAndThen(from((cs,offset) -> {
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
