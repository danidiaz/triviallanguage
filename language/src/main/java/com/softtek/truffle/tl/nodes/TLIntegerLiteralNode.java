package com.softtek.truffle.tl.nodes;

import java.util.Objects;
import java.util.StringJoiner;

final public class TLIntegerLiteralNode extends TLExpressionNode {

	private final int value;

	public TLIntegerLiteralNode(int value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TLIntegerLiteralNode tlInteger = (TLIntegerLiteralNode) o;
		return value == tlInteger.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", TLIntegerLiteralNode.class.getSimpleName() + "[", "]")
				.add("value=" + value)
				.toString();
	}
}
