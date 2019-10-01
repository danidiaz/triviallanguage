package com.softtek.truffle.tl.parser;

import com.softtek.truffle.tl.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.softtek.truffle.tl.parser.Parser.*;

public class TLParser {

	public static Parser<List<TLFunction>> parseAllTopLevelFunctions() {
		return parseTopLevelFunction()
				.andThenRegister(TLFunction::getName)
				.many()
				.andThenDiscard(skipBlanks().andThenDiscard(parseEOF()));
	}

	public static Parser<TLFunction> parseTopLevelFunction() {
		return withStartAndEndPos(
				parseKeyword("function")
						.discardAndThen(parseFunctionName())
						.andThen(functionName ->
								parseArgumentList()
										.andThen(arguments ->
												parseFunctionBody().
														andThen(body ->
																pure((startPos, endPos) ->
																		new TLFunction(
																				startPos,
																				endPos,
																				functionName.toString(),
																				toStringList(arguments),
																				body))))));
	}

	public static Parser<CharSequence> parseFunctionName() {
		return parseIdentifier();
	}

	public static Parser<List<CharSequence>> parseArgumentList() {
		return parseIdentifier()
				.sepBy(skipBlanksAndThen(parseChar(',')))
				.surroundedBy(
						skipBlanksAndThen(parseChar('(')),
						skipBlanksAndThen(parseChar(')'))
				);
	}

	public static Parser<List<TLExpressionNode>> parseInvocationArgumentList() {
		return parseExpression()
				.sepBy(skipBlanksAndThen(parseChar(',')))
				.surroundedBy(
						skipBlanksAndThen(parseChar('(')),
						skipBlanksAndThen(parseChar(')'))
				);
	}

	public static Parser<TLExpressionNode> parseFunctionBody() {
		return skipBlanksAndThen(parseChar(':'))
				.discardAndThen(parseExpression())
				.andThenDiscard(skipBlanksAndThen(parseChar(';')));
	}

	public static Parser<TLExpressionNode> parseExpression() {
		final Parser<TLExpressionNode> integerLiteral = withPluses(parseIntegerLiteral());
		final Parser<TLExpressionNode> stringLiteral = withPluses(parseStringLiteral());
		final Parser<TLExpressionNode> invocation = withPluses(parseInvocation());
		final Parser<TLExpressionNode> variableOccurrence = withPluses(parseVariableOccurrence());
		final Parser<TLExpressionNode> parenthesized = withPluses(parseParenthesizedExpression());

		return integerLiteral.orElse(stringLiteral.orElse(invocation.orElse(variableOccurrence.orElse(parenthesized))));
	}

	public static Parser<TLExpressionNode> parseInvocation() {
		return parseIdentifier().andThen(identifier ->
				getSymbol(identifier,TLFunction.class).andThen(function ->
						parseInvocationArgumentList()
								.andThen(arguments -> pure(new TLInvokeNode(function, arguments)))
				));
	}

	public static Parser<TLExpressionNode> parseVariableOccurrence() {
		return parseIdentifier()
				.andThen(identifier -> pure(new TLReadArgumentNode(0)));
	}

	/**
	 * <p>Auxiliary function necessary to avoid direct left-recursion when building composite expressions.</p>
	 *
	 * <p>See <a href=https://en.wikipedia.org/wiki/Left_recursion#Removing_direct_left_recursion">Removing direct left
	 * recursion on Wikipedia.</a></p>
	 */
	public static Parser<TLExpressionNode> withPluses(Parser<TLExpressionNode> first) {
		return first.andThen(lit ->  parsePluses().andThen(pluses -> pure(assembleSum(lit,pluses))));
	}

	public static Parser<List<TLExpressionNode>> parsePluses() {
		return skipBlanksAndThen(parseChar('+'))
				.discardAndThen(parseExpression())
				.many();
	}

	public static Parser<TLExpressionNode> parseStringLiteral() {
		return parseQuotedString()
				.andThen(quoted -> pure(new TLStringLiteralNode(quoted.toString())));
	}

	public static Parser<TLExpressionNode> parseIntegerLiteral() {
		return parseInteger()
				.andThen(i -> pure(new TLIntegerLiteralNode(i)));
	}

	public static Parser<TLExpressionNode> parseParenthesizedExpression() {
		// unit() + andThen() are necessary to avoid stack overflow when building the parser,
		// due to a cycle in the calls.
		return unit()
				.andThen(v -> parseExpression())
				.surroundedBy(
						skipBlanksAndThen(parseChar('(')),
						skipBlanksAndThen(parseChar(')'))
				);
	}

	public static TLExpressionNode assembleSum(
			TLExpressionNode initial,
			List<TLExpressionNode> rest) {
		if (rest.isEmpty()) {
			return initial;
		}

		final Deque<TLExpressionNode> deque = new LinkedList<>(rest);
		final TLExpressionNode second = deque.removeFirst();

		TLExpressionNode currentSum =
			new TLAddNode(initial,second);
		for (TLExpressionNode tlExpressionNode : deque) {
			currentSum = new TLAddNode(currentSum,tlExpressionNode);
		}
		return currentSum;
	}

	public static List<String> toStringList(List<CharSequence> seq) {
		return seq.stream().map(CharSequence::toString).collect(Collectors.toList());
	}
}
