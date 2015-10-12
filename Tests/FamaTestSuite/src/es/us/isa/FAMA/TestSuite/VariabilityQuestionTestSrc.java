package es.us.isa.FAMA.TestSuite;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
import es.us.isa.FAMA.models.featureModel.*;
import static org.junit.Assert.*;



//unit test
public class VariabilityQuestionTestSrc 
{


	Question q;
	GenericFeatureModel fm;
	QuestionTrader qt;
	
	@Before
    public void setUp() {}
    @After 
    public void tearDown() {}
	
	private void variability(String inputName, float expectedOutput)
	{
		// Read input
		qt = new QuestionTrader();
		fm = (GenericFeatureModel) qt.openFile("noEFM-test-inputs/" + inputName);
		qt.setVariabilityModel(fm);
		qt.setCriteriaSelector("selected"); // We select a specific solver
		
		// Perform question
		Question q = (VariabilityQuestion) qt.createQuestion("Variability");
		try {
			PerformanceResult pr = qt.ask(q);
		} catch (FAMAException e) {
			e.printStackTrace();
		}
		
		VariabilityQuestion vq = (VariabilityQuestion) q;
		
		// Show result
		System.out.println(vq);
		
		// Check result
		assertEquals("Wrong variability", "Variability: " + expectedOutput , vq.toString());
	}
	
	
	// RELATIONSHIPS
	

	// Test Case 1
	@Test
	public void testMandatory()
	{
		System.out.println("========= MANDATORY ===========");
		this.variability("relationships/mandatory/mandatory.fama",(float)1/3);
	}
	
	// Test Case 2
	@Test
	public void testOptional()
	{
		System.out.println("========= OPTIONAL ===========");
		this.variability("relationships/optional/optional.fama",(float)2/3);
	}
	
	// Test Case 3
	@Test
	public void testAlternative()
	{
		System.out.println("========= ALTERNATIVE ===========");
		this.variability("relationships/alternative/alternative.fama",(float)2/7);
	}
	
	// Test Case 4
	@Test
	public void testOr()
	{
		System.out.println("========= OR ===========");
		this.variability("relationships/or/or.fama",(float)3/7);
	}
	
	// Test Case 5
	@Test
	public void testExcludes()
	{
		System.out.println("========= EXCLUDES ===========");
		this.variability("relationships/excludes/excludes.fama",(float)3/7);
	}
	
	// Test Case 6
	@Test
	public void testRequires()
	{
		System.out.println("========= REQUIRES ===========");
		this.variability("relationships/requires/requires.fama",(float)3/7);
	}
	
	
	// COUPLES OF RELATIONSHIPS

	// Test Case 7
	@Test
	public void testMandatoryOptional()
	{
		System.out.println("========= MANDATORY-OPTIONAL ===========");
		this.variability("relationships/mandatory-optional/mandatory-optional.fama",(float)4/31);
	}

	// Test Case 8
	@Test
	public void testMandatoryOr()
	{
		System.out.println("========= MANDATORY-OR ===========");
		this.variability("relationships/mandatory-or/mandatory-or.fama",(float)9/127);
	}
	
	// Test Case 9
	@Test
	public void testMandatoryAlternative()
	{
		System.out.println("========= MANDATORY-ALTERNATIVE ===========");
		this.variability("relationships/mandatory-alternative/mandatory-alternative.fama",(float)4/127);
	}
	
	// Test Case 10
	@Test
	public void testMandatoryRequires()
	{
		System.out.println("========= MANDATORY-REQUIRES ===========");
		this.variability("relationships/mandatory-requires/mandatory-requires.fama",(float)1/7);
	}
	
	// Test Case 11
	@Test
	public void testMandatoryExcludes()
	{
		System.out.println("========= MANDATORY-EXCLUDES ===========");
		this.variability("relationships/mandatory-excludes/mandatory-excludes.fama",(float)0/7);
	}
	
	// Test Case 12
	@Test
	public void testOptionalOr()
	{
		System.out.println("========= OPTIONAL-OR ===========");
		this.variability("relationships/optional-or/optional-or.fama",(float)20/127);
	}
	
	// Test Case 13
	@Test
	public void testOptionalAlternative()
	{
		System.out.println("========= OPTIONAL-ALTERNATIVE ===========");
		this.variability("relationships/optional-alternative/optional-alternative.fama",(float)9/127);
	}
	
	// Test Case 14
	@Test
	public void testOrAlternative()
	{
		System.out.println("========= OR-ALTERNATIVE ===========");
		this.variability("relationships/or-alternative/or-alternative.fama",(float)20/511);
	}
	
	// Test Case 15
	@Test
	public void testOrRequires()
	{
		System.out.println("========= OR-REQUIRES ===========");
		this.variability("relationships/or-requires/or-requires.fama",(float) 2/7);
	}
	
	// Test Case 16
	@Test
	public void testOrExcludes()
	{
		System.out.println("========= OR-EXCLUDES ===========");
		this.variability("relationships/or-excludes/or-excludes.fama",(float)2/7);
	}
	
	// Test Case 17
	@Test
	public void testAlternativeRequires()
	{
		System.out.println("========= ALTERNATIVE-REQUIRES ===========");
		this.variability("relationships/alternative-requires/alternative-requires.fama",(float)1/7);
	}
	
	// Test Case 18
	@Test
	public void testAlternativeExcludes()
	{
		System.out.println("========= ALTERNATIVE-EXCLUDES ===========");
		this.variability("relationships/alternative-excludes/alternative-excludes.fama",(float)2/7);
	}
	
	// Test Case 19
	@Test
	public void testRequiresExcludes()
	{
		System.out.println("========= REQUIRES-EXCLUDES ===========");
		this.variability("relationships/requires-excludes/requires-excludes.fama",(float)2/7);
	}
	
	// Test Case 20
	@Test
	public void testAllRelationships()
	{
		System.out.println("========= ALL RELATIONSHIPS ===========");
		this.variability("relationships/allrelationships/allrelationships.fama",(float)4/127);
	}
	
	
}
