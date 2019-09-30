# TrivialLanguage

This is a simpler version of Truffle's [SimpleLanguage](https://github.com/graalvm/simplelanguage), showcasing fewer of Truffle's capabilities.

It is intended to show the absolute basics of Truffle.

## Differences with SimpleLanguage

- Only string and int literals allowed.
    - No bools
    - No function literals.
    - No nulls.
- No suport for objects.
- Sum (+) is the only binary operation (works on both Strings and Ints, like in
  SimpleLanguage).
- No suport for local variables other than function arguments.
- Function definitions are only allowed at the top level.
- Functions can't be redefined.
    - This makes `TLFunction` a bit simpler than [SLFunction](https://github.com/danidiaz/simplelanguage/blob/master/language/src/main/java/com/oracle/truffle/sl/runtime/SLFunction.java).
        - It receives the [RootNode](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/nodes/RootNode.html) in the constructor itself.
- Function bodies can only consist of single expressions.
    - There aren't any statements or looping constructs.
    - We don't have to catch control flow exceptions thrown by return nodes.
        - There's no need of a
          [SLFunctionBodyNode](https://github.com/danidiaz/simplelanguage/blob/master/language/src/main/java/com/oracle/truffle/sl/nodes/controlflow/SLFunctionBodyNode.java)
          analogue for catching [control flow
          exceptions](https://github.com/danidiaz/simplelanguage/blob/master/language/src/main/java/com/oracle/truffle/sl/nodes/controlflow/SLReturnException.java) 

