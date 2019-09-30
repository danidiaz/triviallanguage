package com.softtek.truffle.tl;

import java.util.StringJoiner;

public class TLReadArgumentNode extends TLExpressionNode {

	int argumentIndex;

	public TLReadArgumentNode(int argumentIndex) {
		this.argumentIndex = argumentIndex;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", TLReadArgumentNode.class.getSimpleName() + "[", "]")
				.add("argumentIndex=" + argumentIndex)
				.toString();
	}
}
