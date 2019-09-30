package com.softtek.truffle.tl;

import com.softtek.truffle.tl.parser.TLParser;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args) {
		System.out.println("Hello World!");

		System.out.println(TLParser.parseAllTopLevelFunctions().parse(" function id (x) : x ;"));
		System.out.println(TLParser.parseAllTopLevelFunctions().parse("function foo () : 2; function id (x) : x ;"));
		System.out.println(TLParser.parseAllTopLevelFunctions().parse(" function foo () : 2 + 3; function id (x) : x ;"));
		System.out.println(TLParser.parseTopLevelFunction().parse("function foo () : 2 + 3;"));
		System.out.println(TLParser.parseTopLevelFunction().parse(" function foo (z) : z + 3;"));
		System.out.println(TLParser.parseTopLevelFunction().parse("function foo (z,yy) : z + 3 + 4 + 5 + 6 + 7 + (z + yy);"));
		System.out.println(TLParser.parseTopLevelFunction().parse(" function foo (z,y, u, v) : z + y + u + v;"));
		System.out.println(TLParser.parseTopLevelFunction().parse("function foo (zz) : 'zoox' + zz; "));
	}
}
