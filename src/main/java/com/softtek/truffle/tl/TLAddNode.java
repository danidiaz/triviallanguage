package com.softtek.truffle.tl;

import java.util.StringJoiner;

public class TLAddNode extends TLExpressionNode {

	private TLExpressionNode left;

	private TLExpressionNode right;

	public TLAddNode(TLExpressionNode left, TLExpressionNode right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", TLAddNode.class.getSimpleName() + "[", "]")
				.add("left=" + left)
				.add("right=" + right)
				.toString();
	}
}
