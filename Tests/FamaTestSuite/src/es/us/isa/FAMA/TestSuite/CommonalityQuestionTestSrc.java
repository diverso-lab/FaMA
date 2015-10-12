package es.us.isa.FAMA.TestSuite;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.*;
import static org.junit.Assert.*;


//unit test
public class CommonalityQuestionTestSrc 
{


	Question q;
	GenericFeatureModel fm;
	QuestionTrader qt;
	
	@Before
    public void setUp() {}
     
    @After
	public void tearDown() {}
	
	private void commonality(String inputName, String feature, long expectedOutput)
	{
		// Read input
		qt = new QuestionTrader();
		fm = (GenericFeatureModel) qt.openFile("noEFM-test-inputs/" + inputName);
		qt.setVariabilityModel(fm);
		qt.setCriteriaSelector("selected"); // We select a specific solver
		
		// Perform question
		Question q = qt.createQuestion("Commonality");
		CommonalityQuestion cq = (CommonalityQuestion) q;
		try {
			cq.setFeature(new Feature(feature));
			PerformanceResult pr = qt.ask(q);
		} catch (FAMAException e) {
			e.printStackTrace();
		}
		
		long com = cq.getCommonality();
		
		// Show result
		System.out.println(cq);
		
		// Check result
		assertEquals("Wrong commonality", "Commonality: " + expectedOutput, cq.toString());
	}
	
	
// RELATIONSHIPS
	

	@Test
	public void testMandatory()
	{
		System.out.println("========= MANDATORY (FEATURE B) ===========");
		this.commonality("relationships/mandatory/mandatory.fama","B",1);
	}
	
	
	@Test
	public void testOptional()
	{
		System.out.println("========= OPTIONAL (FEATURE B) ===========");
		this.commonality("relationships/optional/optional.fama", "B",1);
	}
	
	// Test Case 3
	@Test
	
	public void testAlternative()
	{
		System.out.println("========= ALTERNATIVE (FEATURE B) ===========");
		this.commonality("relationships/alternative/alternative.fama","B",1);
	}
	
	// Test Case 4
	@Test
	public void testOr()
	{
		System.out.println("========= OR (FEATURE B) ===========");
		this.commonality("relationships/or/or.fama","B",2);
	}
	
	// Test Case 5
	@Test
	public void testExcludes()
	{
		System.out.println("========= EXCLUDES (FEATURE B) ===========");
		this.commonality("relationships/excludes/excludes.fama","B",1);
	}
	
	// Test Case 6
	@Test
	public void testRequiresMin()
	{
		System.out.println("========= REQUIRES (FEATURE B) ===========");
		this.commonality("relationships/requires/requires.fama","B",1);
	}
	
	// Test Case 7
	@Test
	public void testRequiresMax()
	{
		System.out.println("========= REQUIRES (FEATURE C) ===========");
		this.commonality("relationships/requires/requires.fama","C",2);
	}
	
	
	// COUPLES OF RELATIONSHIPS

	// Test Case 8
	@Test
	public void testMandatoryOptionalMin()
	{
		System.out.println("========= MANDATORY-OPTIONAL (FEATURE C) ===========");
		this.commonality("relationships/mandatory-optional/mandatory-optional.fama","E",2);
	}
	
	// Test Case 9
	@Test
	public void testMandatoryOptionalMax()
	{
		System.out.println("========= MANDATORY-OPTIONAL (FEATURE B) ===========");
		this.commonality("relationships/mandatory-optional/mandatory-optional.fama","B",4);
	}

	// Test Case 10
	@Test
	public void testMandatoryOrMin()
	{
		System.out.println("========= MANDATORY-OR (FEATURE D) ===========");
		this.commonality("relationships/mandatory-or/mandatory-or.fama", "F", 6);
	}
	
	// Test Case 11
	@Test
	public void testMandatoryOrMax()
	{
		System.out.println("========= MANDATORY-OR (FEATURE B) ===========");
		this.commonality("relationships/mandatory-or/mandatory-or.fama", "B", 9);
	}
	
	// Test Case 12
	@Test
	public void testMandatoryAlternativeMin()
	{
		System.out.println("========= MANDATORY-ALTERNATIVE (FEATURE C) ===========");
		this.commonality("relationships/mandatory-alternative/mandatory-alternative.fama","G",2);
	}
	
	// Test Case 13
	@Test
	public void testMandatoryAlternativeMax()
	{
		System.out.println("========= MANDATORY-ALTERNATIVE (FEATURE B) ===========");
		this.commonality("relationships/mandatory-alternative/mandatory-alternative.fama","B",4);
	}
	
	// Test Case 14
	@Test
	public void testMandatoryRequires()
	{
		System.out.println("========= MANDATORY-REQUIRES (FEATURE B) ===========");
		this.commonality("relationships/mandatory-requires/mandatory-requires.fama","B",1);
	}
	
	
	// Test Case 15
	@Test
	public void testMandatoryExcludes()
	{
		System.out.println("========= MANDATORY-EXCLUDES (FEATURE B) ===========");
		this.commonality("relationships/mandatory-excludes/mandatory-excludes.fama","B",0);
	}
	
	// Test Case 16
	@Test
	public void testOptionalOrMin()
	{
		System.out.println("========= OPTIONAL-OR (FEATURE C) ===========");
		this.commonality("relationships/optional-or/optional-or.fama","G",8);
	}
	
	// Test Case 17
	@Test
	public void testOptionalOrMax()
	{
		System.out.println("========= OPTIONAL-OR (FEATURE B) ===========");
		this.commonality("relationships/optional-or/optional-or.fama","C",16);
	}
	
	// Test Case 18
	@Test
	public void testOptionalAlternativeMin()
	{
		System.out.println("========= OPTIONAL-ALTERNATIVE (FEATURE C) ===========");
		this.commonality("relationships/optional-alternative/optional-alternative.fama","E",3);
	}
	
	// Test Case 19
	@Test
	public void testOptionalAlternativeMax()
	{
		System.out.println("========= OPTIONAL-ALTERNATIVE (FEATURE B) ===========");
		this.commonality("relationships/optional-alternative/optional-alternative.fama","D",6);
	}
	
	// Test Case 20
	@Test
	public void testOrAlternativeMin()
	{
		System.out.println("========= OR-ALTERNATIVE (FEATURE E) ===========");
		this.commonality("relationships/or-alternative/or-alternative.fama","C",5);
	}
	
	// Test Case 21
	@Test
	public void testOrAlternativeMax()
	{
		System.out.println("========= OR-ALTERNATIVE (FEATURE C) ===========");
		this.commonality("relationships/or-alternative/or-alternative.fama","E",16);
	}
	
	// Test Case 22
	@Test
	public void testOrRequiresMin()
	{
		System.out.println("========= OR-REQUIRES (FEATURE B) ===========");
		this.commonality("relationships/or-requires/or-requires.fama","B",1);
	}
	
	// Test Case 23
	@Test
	public void testOrRequiresMax()
	{
		System.out.println("========= OR-REQUIRES (FEATURE C) ===========");
		this.commonality("relationships/or-requires/or-requires.fama","C",2);
	}
	
	// Test Case 24
	@Test
	public void testOrExcludes()
	{
		System.out.println("========= OR-EXCLUDES (Feature C) ===========");
		this.commonality("relationships/or-excludes/or-excludes.fama","C",1);
	}
	
	// Test Case 25
	@Test
	public void testAlternativeRequiresMin()
	{
		System.out.println("========= ALTERNATIVE-REQUIRES (FEATURE B) ===========");
		this.commonality("relationships/alternative-requires/alternative-requires.fama","B",0);
	}
	
	// Test Case 26
	@Test
	public void testAlternativeRequiresMax()
	{
		System.out.println("========= ALTERNATIVE-REQUIRES (FEATURE C) ===========");
		this.commonality("relationships/alternative-requires/alternative-requires.fama","C",1);
	}
	
	// Test Case 27
	@Test
	public void testAlternativeExcludes()
	{
		System.out.println("========= ALTERNATIVE-EXCLUDES (FEATURE B) ===========");
		this.commonality("relationships/alternative-excludes/alternative-excludes.fama","B",1);
	}
	
	// Test Case 28
	@Test
	public void testRequiresExcludesMin()
	{
		System.out.println("========= REQUIRES-EXCLUDES (FEATURE B) ===========");
		this.commonality("relationships/requires-excludes/requires-excludes.fama","B",0);
	}
	
	// Test Case 29
	@Test
	public void testRequiresExcludesMax()
	{
		System.out.println("========= REQUIRES-EXCLUDES (FEATURE C) ===========");
		this.commonality("relationships/requires-excludes/requires-excludes.fama","C",1);
	}
	
	// Test Case 30
	@Test
	public void testAllRelationshipsMin()
	{
		System.out.println("========= ALL RELATIONSHIPS (FEATURE G) ===========");
		this.commonality("relationships/allrelationships/allrelationships.fama","G",1);
	}
	
	// Test Case 31
	@Test
	public void testAllRelationshipsMax()
	{
		System.out.println("========= ALL RELATIONSHIPS (FEATURE B) ===========");
		this.commonality("relationships/allrelationships/allrelationships.fama","B",4);
	}
	
	// Test Case 32
	@Test
	public void testOptionalWithError()
	{
		System.out.println("========= OPTIONAL (NON EXISTENT FEATURE C) ===========");
		this.commonality("relationships/optional/optional.fama","C",0);
	}

	
}