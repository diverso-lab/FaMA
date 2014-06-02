package es.us.isa.config.parser.testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.parser.FMFParser;

public class NewSyntaxTesting {

	private FAMAAttributedFeatureModel afm;
	private FAMAAttributedFeatureModel afmNewSyntax;
	private FMFParser fmParser;
	
	@Before
	public void setUp() throws Exception {
		fmParser = new FMFParser();
		afmNewSyntax = fmParser.parseModel("test/AmazonEC2SugarSyntax.afm");
		afm = fmParser.parseModel("test/AmazonEC2Atts.afm");
	}

	@Test
	public void testTableSyntax(){
		assertEquals(afmNewSyntax.getConstraints().size(), afm.getConstraints().size());
	}

}
