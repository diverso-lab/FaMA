package es.us.isa.transformation.testing;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.Extended2BasicModel;
import es.us.isa.FAMA.parser.FMFParser;

public class TransformationTests {

	private FAMAAttributedFeatureModel afm;
	private Extended2BasicModel transformer;
	
	@Before
	public void setUp(){
		FMFParser parser = new FMFParser();
		afm = parser.parseModel("tests/AmazonEC2Atts.afm");
		transformer = new Extended2BasicModel();
	}

	@Test
	public void test() { 
		FAMAFeatureModel result = (FAMAFeatureModel) transformer.doTransform(afm);
		//TODO what should i check here???
		
		System.out.println(result);
	}
	
	@Test
	public void test2(){
		System.out.println("hola");
	}
	
}
