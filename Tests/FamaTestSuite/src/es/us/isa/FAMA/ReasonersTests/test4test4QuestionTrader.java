package es.us.isa.FAMA.ReasonersTests;


import org.junit.Test;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;

public class test4test4QuestionTrader extends TestSuite{
	@Test
	public void test() throws FAMAException{
		QuestionTrader qt =new QuestionTrader();
		VariabilityModel vm=qt.openFile("HIS.xml");
		
		qt.writeFile("test.dot", vm);
		System.out.println("break");
	}

}
