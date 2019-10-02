package com.softtek.truffle.tl.nodes;

import com.softtek.truffle.tl.runtime.TLFunction;

import java.util.List;

public class TLInvokeNode extends TLExpressionNode {

    private final TLFunction function;
    private final List<TLExpressionNode> arguments;

    public TLInvokeNode(TLFunction function, List<TLExpressionNode> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

}
