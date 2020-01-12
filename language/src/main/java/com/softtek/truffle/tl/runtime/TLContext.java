package com.softtek.truffle.tl.runtime;

// https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/Scope.html
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Scope;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.instrumentation.AllocationReporter;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Layout;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.Source;

import com.softtek.truffle.tl.TLLanguage;
// import com.oracle.truffle.sl.builtins.SLBuiltinNode;
// import com.oracle.truffle.sl.builtins.SLDefineFunctionBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLEvalBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLGetSizeBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLHasSizeBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLHelloEqualsWorldBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLImportBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLIsExecutableBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLIsNullBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLNanoTimeBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLNewObjectBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLPrintlnBuiltin;
// import com.oracle.truffle.sl.builtins.SLPrintlnBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLReadlnBuiltin;
// import com.oracle.truffle.sl.builtins.SLReadlnBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLStackTraceBuiltinFactory;
// import com.oracle.truffle.sl.builtins.SLWrapPrimitiveBuiltinFactory;
// import com.oracle.truffle.sl.nodes.SLExpressionNode;
// import com.oracle.truffle.sl.nodes.SLRootNode;
// import com.oracle.truffle.sl.nodes.local.SLReadArgumentNode;

/**
 * The run-time state of SL during execution. The context is created by the {@link TLLanguage}. It
 * is used, for example, by {@link SLBuiltinNode#getContext() builtin functions}.
 * <p>
 * It would be an error to have two different context instances during the execution of one script.
 * However, if two separate scripts run in one Java VM at the same time, they have a different
 * context. Therefore, the context is not a singleton.
 */
public final class TLContext {

    //private static final Source BUILTIN_SOURCE = Source.newBuilder(TLLanguage.ID, "", "SL builtin").build();
    //static final Layout LAYOUT = Layout.createLayout();

    private final Env env;
    //private final BufferedReader input;
    //private final PrintWriter output;
    //private final SLFunctionRegistry functionRegistry;
    //private final Shape emptyShape;
    private final TLLanguage language;

    //private final AllocationReporter allocationReporter;
    //private final Iterable<Scope> topScopes; // Cache the top scopes

    public TLContext(TLLanguage language, TruffleLanguage.Env env) {
        this.env = env;
        this.language = language;
    }
    
    // original constructor?

//     /**
//      * Return the current Truffle environment.
//      */
//     public Env getEnv() {
//         return env;
//     }
// 
//     /**
//      * Returns the default input, i.e., the source for the {@link SLReadlnBuiltin}. To allow unit
//      * testing, we do not use {@link System#in} directly.
//      */
//     public BufferedReader getInput() {
//         return input;
//     }
// 
//     /**
//      * The default default, i.e., the output for the {@link SLPrintlnBuiltin}. To allow unit
//      * testing, we do not use {@link System#out} directly.
//      */
//     public PrintWriter getOutput() {
//         return output;
//     }
// 
//     /**
//      * Returns the registry of all functions that are currently defined.
//      */
//     public SLFunctionRegistry getFunctionRegistry() {
//         return functionRegistry;
//     }
// 
//     public Iterable<Scope> getTopScopes() {
//         return topScopes;
//     }

//    /**
//     * Adds all builtin functions to the {@link SLFunctionRegistry}. This method lists all
//     * {@link SLBuiltinNode builtin implementation classes}.
//     */
//    private void installBuiltins() {
//        installBuiltin(SLReadlnBuiltinFactory.getInstance());
//        installBuiltin(SLPrintlnBuiltinFactory.getInstance());
//        installBuiltin(SLNanoTimeBuiltinFactory.getInstance());
//        installBuiltin(SLDefineFunctionBuiltinFactory.getInstance());
//        installBuiltin(SLStackTraceBuiltinFactory.getInstance());
//        installBuiltin(SLHelloEqualsWorldBuiltinFactory.getInstance());
//        installBuiltin(SLNewObjectBuiltinFactory.getInstance());
//        installBuiltin(SLEvalBuiltinFactory.getInstance());
//        installBuiltin(SLImportBuiltinFactory.getInstance());
//        installBuiltin(SLGetSizeBuiltinFactory.getInstance());
//        installBuiltin(SLHasSizeBuiltinFactory.getInstance());
//        installBuiltin(SLIsExecutableBuiltinFactory.getInstance());
//        installBuiltin(SLIsNullBuiltinFactory.getInstance());
//        installBuiltin(SLWrapPrimitiveBuiltinFactory.getInstance());
//    }


//     public void installBuiltin(NodeFactory<? extends SLBuiltinNode> factory) {
//         /*
//          * The builtin node factory is a class that is automatically generated by the Truffle DSL.
//          * The signature returned by the factory reflects the signature of the @Specialization
//          *
//          * methods in the builtin classes.
//          */
//         int argumentCount = factory.getExecutionSignature().size();
//         SLExpressionNode[] argumentNodes = new SLExpressionNode[argumentCount];
//         /*
//          * Builtin functions are like normal functions, i.e., the arguments are passed in as an
//          * Object[] array encapsulated in SLArguments. A SLReadArgumentNode extracts a parameter
//          * from this array.
//          */
//         for (int i = 0; i < argumentCount; i++) {
//             argumentNodes[i] = new SLReadArgumentNode(i);
//         }
//         /* Instantiate the builtin node. This node performs the actual functionality. */
//         SLBuiltinNode builtinBodyNode = factory.createNode((Object) argumentNodes);
//         builtinBodyNode.addRootTag();
//         /* The name of the builtin function is specified via an annotation on the node class. */
//         String name = lookupNodeInfo(builtinBodyNode.getClass()).shortName();
//         builtinBodyNode.setUnavailableSourceSection();
// 
//         /* Wrap the builtin in a RootNode. Truffle requires all AST to start with a RootNode. */
//         SLRootNode rootNode = new SLRootNode(language, new FrameDescriptor(), builtinBodyNode, BUILTIN_SOURCE.createUnavailableSection(), name);
// 
//         /* Register the builtin function in our function registry. */
//         getFunctionRegistry().register(name, Truffle.getRuntime().createCallTarget(rootNode));
//     }

//     public static NodeInfo lookupNodeInfo(Class<?> clazz) {
//         if (clazz == null) {
//             return null;
//         }
//         NodeInfo info = clazz.getAnnotation(NodeInfo.class);
//         if (info != null) {
//             return info;
//         } else {
//             return lookupNodeInfo(clazz.getSuperclass());
//         }
//     }

//     /*
//      * Methods for object creation / object property access.
//      */
//     public AllocationReporter getAllocationReporter() {
//         return allocationReporter;
//     }

//    /**
//     * Allocate an empty object. All new objects initially have no properties. Properties are added
//     * when they are first stored, i.e., the store triggers a shape change of the object.
//     */
//    public DynamicObject createObject(AllocationReporter reporter) {
//        DynamicObject object = null;
//        reporter.onEnter(null, 0, AllocationReporter.SIZE_UNKNOWN);
//        object = emptyShape.newInstance();
//        reporter.onReturnValue(object, 0, AllocationReporter.SIZE_UNKNOWN);
//        return object;
//    }

//    public static boolean isSLObject(Object value) {
//        /*
//         * LAYOUT.getType() returns a concrete implementation class, i.e., a class that is more
//         * precise than the base class DynamicObject. This makes the type check faster.
//         */
//        return LAYOUT.getType().isInstance(value) && LAYOUT.getType().cast(value).getShape().getObjectType() == SLObjectType.SINGLETON;
//    }
//
//    /*
//     * Methods for language interoperability.
//     */
//
//    public static Object fromForeignValue(Object a) {
//        if (a instanceof Long || a instanceof SLBigNumber || a instanceof String || a instanceof Boolean) {
//            return a;
//        } else if (a instanceof Character) {
//            return String.valueOf(a);
//        } else if (a instanceof Number) {
//            return fromForeignNumber(a);
//        } else if (a instanceof TruffleObject) {
//            return a;
//        } else if (a instanceof TLContext) {
//            return a;
//        }
//        CompilerDirectives.transferToInterpreter();
//        throw new IllegalStateException(a + " is not a Truffle value");
//    }
//
//    @TruffleBoundary
//    private static long fromForeignNumber(Object a) {
//        return ((Number) a).longValue();
//    }
//
//    public CallTarget parse(Source source) {
//        return env.parsePublic(source);
//    }
//
//    /**
//     * Returns an object that contains bindings that were exported across all used languages. To
//     * read or write from this object the {@link TruffleObject interop} API can be used.
//     */
//    public TruffleObject getPolyglotBindings() {
//        return (TruffleObject) env.getPolyglotBindings();
//    }
//
//    public static TLContext getCurrent() {
//        return TLLanguage.getCurrentContext();
//    }

}