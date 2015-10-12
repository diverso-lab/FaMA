package es.us.isa.FAMA.AtomicSets.tests;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.models.FAMAfeatureModel.transformations.AtomicSet;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.AtomicSetTransform;


public class AtomicSetTestCase {

	AtomicSetTransform at;
	
	QuestionTrader qt;
	
	@Before
	public void setup(){
		qt = new QuestionTrader();
	}
	
	@Test
	public void test1(){
		VariabilityModel model = qt.openFile("files/HIS.xml");
		at = new AtomicSet();
		try {
			VariabilityModel transform = at.doTransform(model);
			qt.writeFile("files/HIS-atomic-set.xml", transform);
		} catch (FAMAException e) {
			e.printStackTrace();
		}
	}
	
}
