package com.softtek.truffle.tl.parser;

import org.junit.Test;

public class TrivialLanguageParserTest {

	@Test
	public void testParses() throws Exception {
        System.out.println(TrivialLanguageParser.parseAllTopLevelFunctions().parse(" function id (x) : x ;"));
        System.out.println(TrivialLanguageParser.parseAllTopLevelFunctions().parse("function foo () : 2; function id (x) : x ;"));
        System.out.println(TrivialLanguageParser.parseAllTopLevelFunctions().parse(" function foo () : 2 + 3; function id (x) : x ;"));
        System.out.println(TrivialLanguageParser.parseAllTopLevelFunctions().parse("function foo (zz) : zz; function bar (a) : a + foo(23) ; "));
        System.out.println(TrivialLanguageParser.parseAllTopLevelFunctions().parse("function foo (zz) : zz; function bar (a) : a + foo(foo(23)) ; "));
        System.out.println(TrivialLanguageParser.parseTopLevelFunction().parse("function foo () : 2 + 3;"));
        System.out.println(TrivialLanguageParser.parseTopLevelFunction().parse(" function foo (z) : z + 3;"));
        System.out.println(TrivialLanguageParser.parseTopLevelFunction().parse("function foo (z,yy) : z + 3 + 4 + 5 + 6 + 7 + (z + yy);"));
        System.out.println(TrivialLanguageParser.parseTopLevelFunction().parse(" function foo (z,y, u, v) : z + y + u + v;"));
        System.out.println(TrivialLanguageParser.parseTopLevelFunction().parse("function foo (zz) : 'zoox' + zz; "));
	}
}
