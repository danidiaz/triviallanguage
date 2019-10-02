package com.softtek.truffle.tl.nodes;

import java.util.Objects;
import java.util.StringJoiner;

final public class TLStringLiteralNode extends TLExpressionNode {

	final String value;

	public TLStringLiteralNode(String value) {
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
		TLStringLiteralNode tlString = (TLStringLiteralNode) o;
		return value.equals(tlString.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", TLStringLiteralNode.class.getSimpleName() + "[", "]")
				.add("value='" + value + "'")
				.toString();
	}
}
