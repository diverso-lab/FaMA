package es.us.isa.FAMA.ReasonersTests;

import org.junit.Test;


import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
import es.us.isa.FAMA.Exceptions.FAMAException;

public class VariabilityTestSuite extends TestSuite {

	@Test
	public void testVariabilityQuestion() throws FAMAException{
		VariabilityQuestion q = (VariabilityQuestion) qt.createQuestion("Variability");
		qt.ask(q);
		float vFactor = q.getVariability();
		
		System.out.println("------ VARIABILITY QUESTION ------");
		System.out.println("Variability factor of the feature model: "+vFactor);
	}
	
}
