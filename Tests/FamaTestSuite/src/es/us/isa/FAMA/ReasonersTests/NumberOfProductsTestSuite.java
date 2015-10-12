package es.us.isa.FAMA.ReasonersTests;

import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Exceptions.FAMAException;

/**
 * This jUnit test case tests the NumberOfProducts question for anyone reasoner
 * @author Jes�s
 *
 */
public class NumberOfProductsTestSuite extends TestSuite {

	@Test
	public void testNumberOfProductsQuestion() throws FAMAException{
		Question q = qt.createQuestion("#Products");
		@SuppressWarnings("unused")
		PerformanceResult pr = qt.ask(q);
		NumberOfProductsQuestion pq = (NumberOfProductsQuestion) q;
		double np = pq.getNumberOfProducts();
		
		System.out.println("-----NUMBER OF PRODUCTS QUESTION TEST------");
		System.out.println("Number of Products: " + np);
		
		//With this, if np is different to expectedValue, the test will failure
		//Remember to run the test with the -enableassertions on the VM arguments
		//if (np != expectedValue){
		//	assert false;
		//}
	}
	
}
