package es.us.isa.FAMA.parser;

import es.us.isa.util.ConstraintLoader;
import es.us.isa.util.Tree;

public class FaMaAttsConstraintLoader implements ConstraintLoader {

	public Tree<String> parseConstraint(String stringConstraint) {
		FMFParser parser = new FMFParser();
		Tree<String> res = parser.parseConstraint(stringConstraint);
		return res;
	}

}
