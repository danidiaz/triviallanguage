package com.softtek.truffle.tl.runtime;

import com.softtek.truffle.tl.nodes.TLExpressionNode;
import com.softtek.truffle.tl.nodes.TLNode;

import java.util.List;
import java.util.StringJoiner;

public class TLFunction extends TLNode {

	private final int startPos;
	private final int endPost;

	private final String name;
	private final List<String> parameter;
	private final TLExpressionNode body;

	public TLFunction(int startPos, int endPost, String name, List<String> parameter, TLExpressionNode body) {
		this.startPos = startPos;
		this.endPost = endPost;
		this.name = name;
		this.parameter = parameter;
		this.body = body;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", TLFunction.class.getSimpleName() + "[", "]")
				.add("startPos=" + startPos)
				.add("endPost=" + endPost)
				.add("name='" + name + "'")
				.add("parameter=" + parameter)
				.add("body=" + body)
				.toString();
	}
}
