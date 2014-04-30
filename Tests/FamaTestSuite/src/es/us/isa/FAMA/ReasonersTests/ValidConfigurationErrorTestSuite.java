package es.us.isa.FAMA.ReasonersTests;

import org.junit.Test;



import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationErrorsQuestion;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.Exceptions.FAMAException;

public class ValidConfigurationErrorTestSuite extends TestSuite {

	@Test
	public void testValidConfigurationQuestion() throws FAMAException {
		
		Product p = new Product();
		p.addFeature(new Feature("FIRE"));
		p.addFeature(new Feature("SERVICES"));
		ValidConfigurationErrorsQuestion vpq = (ValidConfigurationErrorsQuestion) qt.createQuestion("ValidConfigurationErrors");
		vpq.setProduct(p);
		qt.ask(vpq);
	}
	
}
