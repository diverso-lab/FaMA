package es.us.isa.FAMA.TestSuite;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.featureModel.*;
import static org.junit.Assert.*;

public class ValidQuestionTestSrc  {


	Question q;
	GenericFeatureModel fm;
	QuestionTrader qt;
	@Before
    public void setUp() {}
    @After
    public void tearDown() {}
    
	private void validFM(String inputName, boolean expectedOutput)
	{
		// Read input
		qt = new QuestionTrader();
		fm = (GenericFeatureModel) qt.openFile("noEFM-test-inputs/" + inputName);
		qt.setVariabilityModel(fm);
		qt.setCriteriaSelector("selected"); // We select a specific solver
		
		// Perform question
		Question q = qt.createQuestion("Valid");
		try {
			PerformanceResult pr = qt.ask(q);
		} catch (FAMAException e) {
			e.printStackTrace();
		}
		ValidQuestion vq = (ValidQuestion) q;

		// Show result
		System.out.println(vq);
		
		// Check result
		if (expectedOutput)
			assertEquals("Wrong answer", "The model is valid.",vq.toString());
		else
			assertEquals("Wrong answer", "The model is not valid." ,vq.toString());

	}
	
	
	// RELATIONSHIPS
	

	// Test Case 1
	@Test
	public void testMandatory()
	{
		System.out.println("========= MANDATORY ===========");
		this.validFM("relationships/mandatory/mandatory.fama",true);
	}
	
	// Test Case 2
	@Test
	public void testOptional()
	{
		System.out.println("========= OPTIONAL ===========");
		this.validFM("relationships/optional/optional.fama",true);
	}
	
	// Test Case 3
	@Test
	public void testAlternative()
	{
		System.out.println("========= ALTERNATIVE ===========");
		this.validFM("relationships/alternative/alternative.fama",true);
	}
	
	// Test Case 4
	@Test
	public void testOr()
	{
		System.out.println("========= OR ===========");
		this.validFM("relationships/or/or.fama",true);
	}
	
	// Test Case 5
	@Test
	public void testExcludes()
	{
		System.out.println("========= EXCLUDES ===========");
		this.validFM("relationships/excludes/excludes.fama",true);
	}
	
	// Test Case 6
	@Test
	public void testRequires()
	{
		System.out.println("========= REQUIRES ===========");
		this.validFM("relationships/requires/requires.fama",true);
	}
	
	
	// COUPLES OF RELATIONSHIPS

	// Test Case 7
	@Test
	public void testMandatoryOptional()
	{
		System.out.println("========= MANDATORY-OPTIONAL ===========");
		this.validFM("relationships/mandatory-optional/mandatory-optional.fama",true);
	}

	// Test Case 8
	@Test
	public void testMandatoryOr()
	{
		System.out.println("========= MANDATORY-OR ===========");
		this.validFM("relationships/mandatory-or/mandatory-or.fama",true);
	}
	
	// Test Case 9
	@Test
	public void testMandatoryAlternative()
	{
		System.out.println("========= MANDATORY-ALTERNATIVE ===========");
		this.validFM("relationships/mandatory-alternative/mandatory-alternative.fama",true);
	}
	
	// Test Case 10
	@Test
	public void testMandatoryRequires()
	{
		System.out.println("========= MANDATORY-REQUIRES ===========");
		this.validFM("relationships/mandatory-requires/mandatory-requires.fama",true);
	}
	
	// Test Case 11
	@Test
	public void testMandatoryExcludes()
	{
		System.out.println("========= MANDATORY-EXCLUDES ===========");
		this.validFM("relationships/mandatory-excludes/mandatory-excludes.fama",false);
	}
	
	// Test Case 12
	@Test
	public void testOptionalOr()
	{
		System.out.println("========= OPTIONAL-OR ===========");
		this.validFM("relationships/optional-or/optional-or.fama",true);
	}
	
	// Test Case 13
	@Test
	public void testOptionalAlternative()
	{
		System.out.println("========= OPTIONAL-ALTERNATIVE ===========");
		this.validFM("relationships/optional-alternative/optional-alternative.fama",true);
	}
	
	// Test Case 14
	@Test
	public void testOrAlternative()
	{
		System.out.println("========= OR-ALTERNATIVE ===========");
		this.validFM("relationships/or-alternative/or-alternative.fama",true);
	}
	
	// Test Case 15
	@Test
	public void testOrRequires()
	{
		System.out.println("========= OR-REQUIRES ===========");
		this.validFM("relationships/or-requires/or-requires.fama",true);
	}
	
	// Test Case 16
	@Test
	public void testOrExcludes()
	{
		System.out.println("========= OR-EXCLUDES ===========");
		this.validFM("relationships/or-excludes/or-excludes.fama",true);
	}
	
	// Test Case 17
	@Test
	public void testAlternativeRequires()
	{
		System.out.println("========= ALTERNATIVE-REQUIRES ===========");
		this.validFM("relationships/alternative-requires/alternative-requires.fama",true);
	}
	
	// Test Case 18
	@Test
	public void testAlternativeExcludes()
	{
		System.out.println("========= ALTERNATIVE-EXCLUDES ===========");
		this.validFM("relationships/alternative-excludes/alternative-excludes.fama",true);
	}
	
	// Test Case 19
	@Test
	public void testRequiresExcludes()
	{
		System.out.println("========= REQUIRES-EXCLUDES ===========");
		this.validFM("relationships/requires-excludes/requires-excludes.fama",true);
	}
	
	// Test Case 20
	@Test
	public void testAllRelationships()
	{
		System.out.println("========= ALL RELATIONSHIPS ===========");
		this.validFM("relationships/allrelationships/allrelationships.fama",true);
	}
	
	// ERROR GUESSING
	
	// Test Case 21
	@Test
	public void testoptionalAlternative()
	{
		System.out.println("========= OPTIONAL-ALTERNATIVE (Test whether child features can be selected without their parent ) ===========");
		this.validFM("error-guessing/optional-alternativeVM/optional-alternativeVM.fama",false);
	}
	
	
}
