package com.softtek.truffle.tl.parser;

import java.util.function.Function;

public final class ParseResult<T> {

	/**
	 * <p>A successfully parsed value</p>
	 */
	public final T value;

	/**
	 * <p>Position in the {@link CharSequence} from which any further parsing should start.</p>
	 */
	public final Integer newOffset;

	public ParseResult(T result, Integer newOffset) {
		this.value = result;
		this.newOffset = newOffset;
	}

	public final <R> ParseResult<R> flatMap(Function<T,ParseResult<R>> mapper) {
		return mapper.apply(value);
	}
}
