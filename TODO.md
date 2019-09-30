# TODO

- Rework SLFunction?
- Add equivalent of SLRootNode
- Add equivalent of SLFunctionBodyNode (but maybe not necessary in our case? We don't have to catch control flow exceptions...)
- Add equivalent of SLInvokeNode

# NOTES

- [RootNode](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/nodes/RootNode.html)

   > Represents the root node in a Truffle AST. The root node is a node that allows to be executed using a frame instance created by the framework. Please note that the RootNode should not be executed directly but using CallTarget.call(Object...). The structure of the frame is provided by the frame descriptor passed in the constructor. A root node has always a null parent and cannot be replaced.

- [RootCallTarget](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/RootCallTarget.html)

  > Represents the target of a call to a RootNode, i.e., to another tree of nodes. Instances of this class can be created using TruffleRuntime.createCallTarget(RootNode).

- The link beween the [SLRootNode](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/nodes/RootNode.html) and the [SLFunctionBodyNode](https://github.com/danidiaz/simplelanguage/blob/master/language/src/main/java/com/oracle/truffle/sl/nodes/controlflow/SLFunctionBodyNode.java) is done in the [SLNodeFactory](https://github.com/danidiaz/simplelanguage/blob/master/language/src/main/java/com/oracle/truffle/sl/parser/SLNodeFactory.java).

- Do I need a separate `SLNodeFactory`?
