package es.us.isa.FAMA.parser.tests;

import org.junit.Test;


public class ParserAttributedTests extends ParserTests{

	@Test
	public void test1(){
		String path = props.getProperty("attributed");
		exeTests(path);
	}
	
}
