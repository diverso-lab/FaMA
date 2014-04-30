package es.us.isa.FAMA.ReasonersTests;

import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Exceptions.FAMAException;

/**
 * This jUnit test case tests the Valid question for anyone reasoner
 * @author Jes�s
 *
 */
public class ValidTestSuite extends TestSuite {

	@Test
	public void testValidQuestion() throws FAMAException{
		Question q = qt.createQuestion("Valid");
		@SuppressWarnings("unused")
		PerformanceResult pr = qt.ask(q);
		ValidQuestion	pq = (ValidQuestion) q;
		System.out.println("---- VALID MODEL QUESTION TEST ----");
		System.out.println("Is the model valid? "+pq.isValid());
	}
	
}
