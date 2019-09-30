package com.softtek.truffle.tl;

import java.util.List;

public class TLInvokeNode extends TLExpressionNode {

    private final TLFunction function;
    private final List<TLExpressionNode> arguments;

    public TLInvokeNode(TLFunction function, List<TLExpressionNode> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

}
