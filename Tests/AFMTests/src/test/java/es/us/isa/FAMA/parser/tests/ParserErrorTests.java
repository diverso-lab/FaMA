package es.us.isa.FAMA.parser.tests;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;

public class ParserErrorTests {

	private QuestionTrader qt;
	
	@Before
	public void setUp(){
		qt = new QuestionTrader();
	}
	
	@Test
	public void attParserDuplicatedName(){
		GenericAttributedFeatureModel afm = (GenericAttributedFeatureModel) qt.openFile("src/test/resources/errors/att-name-error.fm");
	}
	
	@Test (expected = FAMAException.class)
	public void simpleParserDuplicatedName(){
		GenericFeatureModel fm = (GenericFeatureModel) qt.openFile("src/test/resources/errors/name-error.xml");
	}
	
}
