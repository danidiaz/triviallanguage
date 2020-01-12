package com.softtek.truffle.tl;

// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/TruffleLanguage.html
// TODO actually fill the implementation
// What do I need: 
// - TLContext -> TLContext
// - SLLexicalScope -> TLLexicalScope
// - SLBuiltinNode -> TLBuiltingNode
// - SLLexicalScope -> TLLexicalScope
// - SLFunction -> TLFunction
// - SLBigNumber -> TLBigNumber
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Scope;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

// import com.softtek.truffle.tl.builtins.SLBuiltinNode;
// import com.softtek.truffle.tl.builtins.SLDefineFunctionBuiltin;
// import com.softtek.truffle.tl.builtins.SLNanoTimeBuiltin;
// import com.softtek.truffle.tl.builtins.SLPrintlnBuiltin;
// import com.softtek.truffle.tl.builtins.SLReadlnBuiltin;
// import com.softtek.truffle.tl.builtins.SLStackTraceBuiltin;
// import com.softtek.truffle.tl.nodes.SLEvalRootNode;
// import com.softtek.truffle.tl.nodes.SLTypes;
// import com.softtek.truffle.tl.nodes.controlflow.SLBlockNode;
// import com.softtek.truffle.tl.nodes.controlflow.SLBreakNode;
// import com.softtek.truffle.tl.nodes.controlflow.SLContinueNode;
// import com.softtek.truffle.tl.nodes.controlflow.SLDebuggerNode;
// import com.softtek.truffle.tl.nodes.controlflow.SLIfNode;
// import com.softtek.truffle.tl.nodes.controlflow.SLReturnNode;
// import com.softtek.truffle.tl.nodes.controlflow.SLWhileNode;
// import com.softtek.truffle.tl.nodes.expression.SLAddNode;
// import com.softtek.truffle.tl.nodes.expression.SLBigIntegerLiteralNode;
// import com.softtek.truffle.tl.nodes.expression.SLDivNode;
// import com.softtek.truffle.tl.nodes.expression.SLEqualNode;
// import com.softtek.truffle.tl.nodes.expression.SLFunctionLiteralNode;
// import com.softtek.truffle.tl.nodes.expression.SLInvokeNode;
// import com.softtek.truffle.tl.nodes.expression.SLLessOrEqualNode;
// import com.softtek.truffle.tl.nodes.expression.SLLessThanNode;
// import com.softtek.truffle.tl.nodes.expression.SLLogicalAndNode;
// import com.softtek.truffle.tl.nodes.expression.SLLogicalOrNode;
// import com.softtek.truffle.tl.nodes.expression.SLMulNode;
// import com.softtek.truffle.tl.nodes.expression.SLReadPropertyNode;
// import com.softtek.truffle.tl.nodes.expression.SLStringLiteralNode;
// import com.softtek.truffle.tl.nodes.expression.SLSubNode;
// import com.softtek.truffle.tl.nodes.expression.SLWritePropertyNode;
// import com.softtek.truffle.tl.nodes.local.SLLexicalScope;
// import com.softtek.truffle.tl.nodes.local.SLReadLocalVariableNode;
// import com.softtek.truffle.tl.nodes.local.SLWriteLocalVariableNode;
// import com.softtek.truffle.tl.parser.SLNodeFactory;
// import com.softtek.truffle.tl.parser.SimpleLanguageLexer;
// import com.softtek.truffle.tl.parser.SimpleLanguageParser;
// import com.softtek.truffle.tl.runtime.SLBigNumber;
import com.softtek.truffle.tl.runtime.TLContext;
// import com.softtek.truffle.tl.runtime.SLFunction;
// import com.softtek.truffle.tl.runtime.SLFunctionRegistry;
// import com.softtek.truffle.tl.runtime.SLNull;

@TruffleLanguage.Registration(id = TLLanguage.ID, name = "SL", defaultMimeType = TLLanguage.MIME_TYPE, characterMimeTypes = TLLanguage.MIME_TYPE, contextPolicy = ContextPolicy.SHARED, fileTypeDetectors = TLFileDetector.class)
@ProvidedTags({StandardTags.CallTag.class, StandardTags.StatementTag.class, StandardTags.RootTag.class, StandardTags.RootBodyTag.class, StandardTags.ExpressionTag.class,
                DebuggerTags.AlwaysHalt.class})
public final class TLLanguage extends TruffleLanguage<TLContext> {
    public static volatile int counter;

    public static final String ID = "tl";
    public static final String MIME_TYPE = "application/x-tl";

    public TLLanguage() {
        counter++;
    }

    @Override
    protected TLContext createContext(Env env) {
        throw new java.lang.UnsupportedOperationException();
        //return new TLContext(this, env, new ArrayList<>(EXTERNAL_BUILTINS));
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        throw new java.lang.UnsupportedOperationException();
//        Source source = request.getSource();
//        Map<String, RootCallTarget> functions;
//        /*
//         * Parse the provided source. At this point, we do not have a TLContext yet. Registration of
//         * the functions with the TLContext happens lazily in SLEvalRootNode.
//         */
//        if (request.getArgumentNames().isEmpty()) {
//            functions = SimpleLanguageParser.parseSL(this, source);
//        } else {
//            Source requestedSource = request.getSource();
//            StringBuilder sb = new StringBuilder();
//            sb.append("function main(");
//            String sep = "";
//            for (String argumentName : request.getArgumentNames()) {
//                sb.append(sep);
//                sb.append(argumentName);
//                sep = ",";
//            }
//            sb.append(") { return ");
//            sb.append(request.getSource().getCharacters());
//            sb.append(";}");
//            String language = requestedSource.getLanguage() == null ? ID : requestedSource.getLanguage();
//            Source decoratedSource = Source.newBuilder(language, sb.toString(), request.getSource().getName()).build();
//            functions = SimpleLanguageParser.parseSL(this, decoratedSource);
//        }
//
//        RootCallTarget main = functions.get("main");
//        RootNode evalMain;
//        if (main != null) {
//            /*
//             * We have a main function, so "evaluating" the parsed source means invoking that main
//             * function. However, we need to lazily register functions into the TLContext first, so
//             * we cannot use the original SLRootNode for the main function. Instead, we create a new
//             * SLEvalRootNode that does everything we need.
//             */
//            evalMain = new SLEvalRootNode(this, main, functions);
//        } else {
//            /*
//             * Even without a main function, "evaluating" the parsed source needs to register the
//             * functions into the TLContext.
//             */
//            evalMain = new SLEvalRootNode(this, null, functions);
//        }
//        return Truffle.getRuntime().createCallTarget(evalMain);
    }

    /*
     * Still necessary for the old SL TCK to pass. We should remove with the old TCK. New language
     * should not override this.
     */
    @SuppressWarnings("deprecation")
    @Override
    protected Object findExportedSymbol(TLContext context, String globalName, boolean onlyExplicit) {
        throw new java.lang.UnsupportedOperationException();
        //return context.getFunctionRegistry().lookup(globalName, false);
    }

    @Override
    protected boolean isVisible(TLContext context, Object value) {
        return !InteropLibrary.getFactory().getUncached(value).isNull(value);
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        throw new java.lang.UnsupportedOperationException();
        /*
        if (!(object instanceof TruffleObject)) {
            return false;
        } else if (object instanceof SLBigNumber || object instanceof SLFunction || object instanceof SLNull) {
            return true;
        } else if (TLContext.isSLObject(object)) {
            return true;
        } else {
            return false;
        }
        */
    }

    @Override
    protected String toString(TLContext context, Object value) {
        return toString(value);
    }

    public static String toString(Object value) {
        throw new java.lang.UnsupportedOperationException();
        /*
        try {
            if (value == null) {
                return "ANY";
            }
            InteropLibrary interop = InteropLibrary.getFactory().getUncached(value);
            if (interop.fitsInLong(value)) {
                return Long.toString(interop.asLong(value));
            } else if (interop.isBoolean(value)) {
                return Boolean.toString(interop.asBoolean(value));
            } else if (interop.isString(value)) {
                return interop.asString(value);
            } else if (interop.isNull(value)) {
                return "NULL";
            } else if (interop.isExecutable(value)) {
                if (value instanceof SLFunction) {
                    return ((SLFunction) value).getName();
                } else {
                    return "Function";
                }
            } else if (interop.hasMembers(value)) {
                return "Object";
            } else if (value instanceof SLBigNumber) {
                return value.toString();
            } else {
                return "Unsupported";
            }
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreter();
            throw new AssertionError();
        }
    }
    */
    }

    @Override
    protected Object findMetaObject(TLContext context, Object value) {
        throw new java.lang.UnsupportedOperationException();
        //return getMetaObject(value);
    }

//    public static String getMetaObject(Object value) {
//        if (value == null) {
//            return "ANY";
//        }
//        InteropLibrary interop = InteropLibrary.getFactory().getUncached(value);
//        if (interop.isNumber(value) || value instanceof SLBigNumber) {
//            return "Number";
//        } else if (interop.isBoolean(value)) {
//            return "Boolean";
//        } else if (interop.isString(value)) {
//            return "String";
//        } else if (interop.isNull(value)) {
//            return "NULL";
//        } else if (interop.isExecutable(value)) {
//            return "Function";
//        } else if (interop.hasMembers(value)) {
//            return "Object";
//        } else {
//            return "Unsupported";
//        }
//    }

    @Override
    protected SourceSection findSourceLocation(TLContext context, Object value) {
        throw new java.lang.UnsupportedOperationException();
        /*
        if (value instanceof SLFunction) {
            return ((SLFunction) value).getDeclaredLocation();
        }
        return null;
        */
    }

    @Override
    public Iterable<Scope> findLocalScopes(TLContext context, Node node, Frame frame) {
        throw new java.lang.UnsupportedOperationException();
        /*
        final SLLexicalScope scope = SLLexicalScope.createScope(node);
        return new Iterable<Scope>() {
            @Override
            public Iterator<Scope> iterator() {
                return new Iterator<Scope>() {
                    private SLLexicalScope previousScope;
                    private SLLexicalScope nextScope = scope;

                    @Override
                    public boolean hasNext() {
                        if (nextScope == null) {
                            nextScope = previousScope.findParent();
                        }
                        return nextScope != null;
                    }

                    @Override
                    public Scope next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        Object functionObject = findFunctionObject();
                        Scope vscope = Scope.newBuilder(nextScope.getName(), nextScope.getVariables(frame)).node(nextScope.getNode()).arguments(nextScope.getArguments(frame)).rootInstance(
                                        functionObject).build();
                        previousScope = nextScope;
                        nextScope = null;
                        return vscope;
                    }

                    private Object findFunctionObject() {
                        String name = node.getRootNode().getName();
                        return context.getFunctionRegistry().getFunction(name);
                    }
                };
            }
        };
        */
    }

    @Override
    protected Iterable<Scope> findTopScopes(TLContext context) {
        throw new java.lang.UnsupportedOperationException();
        // return context.getTopScopes();
    }

    /*
    public static TLContext getCurrentContext() {
        return getCurrentContext(TLLanguage.class);
    }

    private static final List<NodeFactory<? extends SLBuiltinNode>> EXTERNAL_BUILTINS = Collections.synchronizedList(new ArrayList<>());

    public static void installBuiltin(NodeFactory<? extends SLBuiltinNode> builtin) {
        EXTERNAL_BUILTINS.add(builtin);
    }
    */

}
