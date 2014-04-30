package es.us.isa.FAMA.TestSuite;import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.models.featureModel.*;

import static org.junit.Assert.*;


//unit test
public class NumberOfProductsQuestionTestSrc 
{


	Question q;
	GenericFeatureModel fm;
	QuestionTrader qt;
	
	@Before
    public void setUp() {}
    @After 
    public void tearDown() {}
	
	private void numberOfProducts(String inputName, long expectedOutput)
	{
		// Read input
		qt = new QuestionTrader();
		fm = (GenericFeatureModel) qt.openFile("noEFM-test-inputs/" + inputName);
		qt.setVariabilityModel(fm);
		qt.setCriteriaSelector("selected"); // We select a specific solver
		
		// Perform question
		Question q = qt.createQuestion("#Products");
		try {
			@SuppressWarnings("unused")
			PerformanceResult pr = qt.ask(q);
		} catch (FAMAException e) {
			e.printStackTrace();
		}
		NumberOfProductsQuestion npq = (NumberOfProductsQuestion) q;
		
		// Show result 
		System.out.println(npq);
		
		// Check result
		assertEquals("Wrong number of products", "The number of products is " + expectedOutput, npq.toString());
	}
	
	
	// RELATIONSHIPS
	

	// Test Case 1
	@Test
	public void testMandatory()
	{
		System.out.println("========= MANDATORY ===========");
		this.numberOfProducts("relationships/mandatory/mandatory.fama",1);
	}
	
	// Test Case 2
	@Test
	public void testOptional()
	{
		System.out.println("========= OPTIONAL ===========");
		this.numberOfProducts("relationships/optional/optional.fama",2);
	}
	
	// Test Case 3
	@Test
	public void testAlternative()
	{
		System.out.println("========= ALTERNATIVE ===========");
		this.numberOfProducts("relationships/alternative/alternative.fama",2);
	}
	
	// Test Case 4
	@Test
	public void testOr()
	{
		System.out.println("========= OR ===========");
		this.numberOfProducts("relationships/or/or.fama",3);
	}
	
	// Test Case 5
	@Test
	public void testExcludes()
	{
		System.out.println("========= EXCLUDES ===========");
		this.numberOfProducts("relationships/excludes/excludes.fama",3);
	}
	
	// Test Case 6
	@Test
	public void testRequires()
	{
		System.out.println("========= REQUIRES ===========");
		this.numberOfProducts("relationships/requires/requires.fama",3);
	}
	
	
	// COUPLES OF RELATIONSHIPS

	// Test Case 7
	@Test
	public void testMandatoryOptional()
	{
		System.out.println("========= MANDATORY-OPTIONAL ===========");
		this.numberOfProducts("relationships/mandatory-optional/mandatory-optional.fama",4);
	}

	// Test Case 8
	@Test
	public void testMandatoryOr()
	{
		System.out.println("========= MANDATORY-OR ===========");
		this.numberOfProducts("relationships/mandatory-or/mandatory-or.fama",9);
	}
	
	// Test Case 9
	@Test
	public void testMandatoryAlternative()
	{
		System.out.println("========= MANDATORY-ALTERNATIVE ===========");
		this.numberOfProducts("relationships/mandatory-alternative/mandatory-alternative.fama",4);
	}
	
	// Test Case 10
	@Test
	public void testMandatoryRequires()
	{
		System.out.println("========= MANDATORY-REQUIRES ===========");
		this.numberOfProducts("relationships/mandatory-requires/mandatory-requires.fama",1);
	}
	
	// Test Case 11
	@Test
	public void testMandatoryExcludes()
	{
		System.out.println("========= MANDATORY-EXCLUDES ===========");
		this.numberOfProducts("relationships/mandatory-excludes/mandatory-excludes.fama",0);
	}
	
	// Test Case 12
	@Test
	public void testOptionalOr()
	{
		System.out.println("========= OPTIONAL-OR ===========");
		this.numberOfProducts("relationships/optional-or/optional-or.fama",20);
	}
	
	// Test Case 13
	@Test
	public void testOptionalAlternative()
	{
		System.out.println("========= OPTIONAL-ALTERNATIVE ===========");
		this.numberOfProducts("relationships/optional-alternative/optional-alternative.fama",9);
	}
	
	// Test Case 14
	@Test
	public void testOrAlternative()
	{
		System.out.println("========= OR-ALTERNATIVE ===========");
		this.numberOfProducts("relationships/or-alternative/or-alternative.fama",20);
	}
	
	// Test Case 15
	@Test
	public void testOrRequires()
	{
		System.out.println("========= OR-REQUIRES ===========");
		this.numberOfProducts("relationships/or-requires/or-requires.fama",2);
	}
	
	// Test Case 16
	@Test
	public void testOrExcludes()
	{
		System.out.println("========= OR-EXCLUDES ===========");
		this.numberOfProducts("relationships/or-excludes/or-excludes.fama",2);
	}
	
	// Test Case 17
	@Test
	public void testAlternativeRequires()
	{
		System.out.println("========= ALTERNATIVE-REQUIRES ===========");
		this.numberOfProducts("relationships/alternative-requires/alternative-requires.fama",1);
	}
	
	// Test Case 18
	@Test
	public void testAlternativeExcludes()
	{
		System.out.println("========= ALTERNATIVE-EXCLUDES ===========");
		this.numberOfProducts("relationships/alternative-excludes/alternative-excludes.fama",2);
	}
	
	// Test Case 19
	@Test
	public void testRequiresExcludes()
	{
		System.out.println("========= REQUIRES-EXCLUDES ===========");
		this.numberOfProducts("relationships/requires-excludes/requires-excludes.fama",2);
	}
	
	// Test Case 20
	@Test
	public void testAllRelationships()
	{
		System.out.println("========= ALL RELATIONSHIPS ===========");
		this.numberOfProducts("relationships/allrelationships/allrelationships.fama",4);
	}
}
