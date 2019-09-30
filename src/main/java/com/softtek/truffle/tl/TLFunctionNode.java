package com.softtek.truffle.tl;

import java.util.List;
import java.util.StringJoiner;

public class TLFunctionNode extends TLNode {

	private final int startPos;
	private final int endPost;

	private final String name;
	private final List<String> parameter;
	private final TLExpressionNode body;

	public TLFunctionNode(int startPos, int endPost, String name, List<String> parameter, TLExpressionNode body) {
		this.startPos = startPos;
		this.endPost = endPost;
		this.name = name;
		this.parameter = parameter;
		this.body = body;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", TLFunctionNode.class.getSimpleName() + "[", "]")
				.add("startPos=" + startPos)
				.add("endPost=" + endPost)
				.add("name='" + name + "'")
				.add("parameter=" + parameter)
				.add("body=" + body)
				.toString();
	}
}
