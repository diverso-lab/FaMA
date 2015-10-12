package es.us.isa.FAMA.TestSuite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.*;
import static org.junit.Assert.*;

public class ValidProductQuestionTestSrc  {


	Question q;
	GenericFeatureModel fm;
	QuestionTrader qt;

	
	private void validProduct(String inputName, Product p, boolean expectedOutput)
	{
		// Read input
		qt = new QuestionTrader();
		fm = (GenericFeatureModel) qt.openFile("noEFM-test-inputs/" + inputName);
		qt.setVariabilityModel(fm);
		qt.setCriteriaSelector("selected"); // We select a specific solver
		
		// Perform question
		Question q = qt.createQuestion("ValidProduct");
		ValidProductQuestion vpq = (ValidProductQuestion) q;
		
		try {
			// Set product to check
			vpq.setProduct(p);
			
			// Ask question
			PerformanceResult pr = qt.ask(vpq);
		} catch (FAMAException e) {
			e.printStackTrace();
		}
		
		//Show result
		System.out.println(vpq);
		
		// Check result
		if (expectedOutput)
			assertEquals("Wrong answer to the question isValidProduct", "Valid product", vpq.toString());
		else
			assertEquals("Wrong answer to the question isValidProduct", "Non valid product", vpq.toString());
		
	}
	
	

	// RELATIONSHIPS
	

	// Test Case 1
	@Test
	public void testMandatoryValid()
	{
		System.out.println("========= MANDATORY (Valid) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		
		this.validProduct("relationships/mandatory/mandatory.fama",p,true);
	}
	
	// Test Case 2
	@Test
	public void testMandatoryNonValid()
	{
		System.out.println("========= MANDATORY (Non valid) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/mandatory/mandatory.fama",p,false);
	}
	
	
	// Test Case 3
	@Test
	public void testOptionalValidMin()
	{
		System.out.println("========= OPTIONAL (Valid - min features)  ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		this.validProduct("relationships/optional/optional.fama",p,true);
	}
	
	// Test Case 4
	@Test
	public void testOptionalValidMax()
	{
		System.out.println("========= OPTIONAL (Valid - max features)  ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		
		this.validProduct("relationships/optional/optional.fama",p,true);
	}
	
	// Test Case 5
	@Test
	public void testAlternativeValid()
	{
		System.out.println("========= ALTERNATIVE (Valid) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		
		this.validProduct("relationships/alternative/alternative.fama",p,true);
	}
	
	// Test Case 6
	@Test
	public void testAlternativeNonValidMin()
	{
		System.out.println("========= ALTERNATIVE (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/alternative/alternative.fama",p,false);
	}
	
	// Test Case 7
	@Test
	public void testAlternativeNonValidMax()
	{
		System.out.println("========= ALTERNATIVE (Non valid -  max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/alternative/alternative.fama",p,false);
	}
	
	// Test Case 8
	@Test
	public void testOrValidMin()
	{
		System.out.println("========= OR (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		
		this.validProduct("relationships/or/or.fama",p,true);
	}
	
	// Test Case 9
	@Test
	public void testOrValidMax()
	{
		System.out.println("========= OR (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/or/or.fama",p,true);
	}
	
	// Test Case 10
	@Test
	public void testOrNonValid()
	{
		System.out.println("========= OR (Non valid) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/or/or.fama",p,false);
	}
	
	// Test Case 11
	@Test
	public void testExcludesValidMin()
	{
		System.out.println("========= EXCLUDES (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/excludes/excludes.fama",p,true);
	}
	
	// Test Case 12
	@Test
	public void testExcludesValidMax()
	{
		System.out.println("========= EXCLUDES (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/excludes/excludes.fama",p,true);
	}
	
	// Test Case 13
	@Test
	public void testExcludesNonValid()
	{
		System.out.println("========= EXCLUDES (Non valid) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/excludes/excludes.fama",p,false);
	}
	
	// Test Case 14
	@Test
	public void testRequiresValidMin()
	{
		System.out.println("========= REQUIRES (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/requires/requires.fama",p,true);
	}
	
	// Test Case 15
	@Test
	public void testRequiresValidMax()
	{
		System.out.println("========= REQUIRES (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/requires/requires.fama",p,true);
	}
	
	// Test Case 16
	@Test
	public void testRequiresNonValid()
	{
		System.out.println("========= REQUIRES (Non valid) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		
		this.validProduct("relationships/requires/requires.fama",p,false);
	}
	
	
	// COUPLES OF RELATIONSHIPS

	// Test Case 17
	@Test
	public void testMandatoryOptionalValidMin()
	{
		System.out.println("========= MANDATORY-OPTIONAL (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		
		this.validProduct("relationships/mandatory-optional/mandatory-optional.fama",p,true);
	}
	
	// Test Case 18
	@Test
	public void testMandatoryOptionalValidMax()
	{
		System.out.println("========= MANDATORY-OPTIONAL (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("D"));
		p.addFeature(new Feature("E"));
		
		this.validProduct("relationships/mandatory-optional/mandatory-optional.fama",p,true);
	}
	
	// Test Case 19
	@Test
	public void testMandatoryOptionalNonValidMin()
	{
		System.out.println("========= MANDATORY-OPTIONAL (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/mandatory-optional/mandatory-optional.fama",p,false);
	}
	
	
	// Test Case 20
	@Test
	public void testMandatoryOptionalNonValidMax()
	{
		System.out.println("========= MANDATORY-OPTIONAL (Non valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("D"));
		
		this.validProduct("relationships/mandatory-optional/mandatory-optional.fama",p,false);
	}
	
	
	// Test Case 21
	@Test
	public void testMandatoryOrValidMin()
	{
		System.out.println("========= MANDATORY-OR (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("F"));
		
		this.validProduct("relationships/mandatory-or/mandatory-or.fama",p,true);
	}
	
	// Test Case 22
	@Test
	public void testMandatoryOrValidMax()
	{
		System.out.println("========= MANDATORY-OR (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("D"));
		p.addFeature(new Feature("E"));
		p.addFeature(new Feature("F"));
		p.addFeature(new Feature("G"));
		
		this.validProduct("relationships/mandatory-or/mandatory-or.fama",p,true);
	}
	
	// Test Case 23
	@Test
	public void testMandatoryOrNonValidMin()
	{
		System.out.println("========= MANDATORY-OR (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/mandatory-or/mandatory-or.fama",p,false);
	}
	
	// Test Case 24
	@Test
	public void testMandatoryOrNonValidMax()
	{
		System.out.println("========= MANDATORY-OR (Non valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("D"));
		p.addFeature(new Feature("G"));
		
		this.validProduct("relationships/mandatory-or/mandatory-or.fama",p,false);
	}
	
	// Test Case 25
	@Test
	public void testMandatoryAlternativeValidMin()
	{
		System.out.println("========= MANDATORY-ALTERNATIVE (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("D"));
		p.addFeature(new Feature("E"));
		
		this.validProduct("relationships/mandatory-alternative/mandatory-alternative.fama",p,true);
	}
	
	// Test Case 26
	@Test
	public void testMandatoryAlternativeValidMax()
	{
		System.out.println("========= MANDATORY-ALTERNATIVE (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("E"));
		p.addFeature(new Feature("G"));
		
		this.validProduct("relationships/mandatory-alternative/mandatory-alternative.fama",p,true);
	}
	
	// Test Case 27
	@Test
	public void testMandatoryAlternativeNonValidMin()
	{
		System.out.println("========= MANDATORY-ALTERNATIVE (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/mandatory-alternative/mandatory-alternative.fama",p,false);
	}
	
	// Test Case 28
	@Test
	public void testMandatoryAlternativeNonValidMax()
	{
		System.out.println("========= MANDATORY-ALTERNATIVE (Non valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("D"));
		p.addFeature(new Feature("E"));
		p.addFeature(new Feature("F"));
		p.addFeature(new Feature("G"));
		
		this.validProduct("relationships/mandatory-alternative/mandatory-alternative.fama",p,false);
	}
	
	// Test Case 29
	@Test
	public void testMandatoryRequiresValid()
	{
		System.out.println("========= MANDATORY-REQUIRES (Valid) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/mandatory-requires/mandatory-requires.fama",p,true);
	}
	
	// Test Case 30
	@Test
	public void testMandatoryRequiresNonValidMin()
	{
		System.out.println("========= MANDATORY-REQUIRES (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/mandatory-requires/mandatory-requires.fama",p,false);
	}
	
	// Test Case 31
	@Test
	public void testMandatoryRequiresNonValidMax()
	{
		System.out.println("========= MANDATORY-REQUIRES (Non valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		
		this.validProduct("relationships/mandatory-requires/mandatory-requires.fama",p,false);
	}
	
	// Test Case 32
	@Test
	public void testMandatoryExcludesNonValidMin()
	{
		System.out.println("========= MANDATORY-EXCLUDES (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/mandatory-excludes/mandatory-excludes.fama",p,false);
	}
	
	// Test Case 33
	@Test
	public void testMandatoryExcludesNonValidMax()
	{
		System.out.println("========= MANDATORY-EXCLUDES (Non valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/mandatory-excludes/mandatory-excludes.fama",p,false);
	}
	
	// Test Case 34
	@Test
	public void testOptionalOrValidMin()
	{
		System.out.println("========= OPTIONAL-OR (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/optional-or/optional-or.fama",p,true);
	}
	
	// Test Case 35
	@Test
	public void testOptionalOrValidMax()
	{
		System.out.println("========= OPTIONAL-OR (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("D"));
		p.addFeature(new Feature("E"));
		p.addFeature(new Feature("F"));
		p.addFeature(new Feature("G"));
		
		this.validProduct("relationships/optional-or/optional-or.fama",p,true);
	}
	
	// Test Case 36
	@Test
	public void testOptionalOrNonValidMin()
	{
		System.out.println("========= OPTIONAL-OR (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));

		this.validProduct("relationships/optional-or/optional-or.fama",p,false);
	}
	
	// Test Case 37
	@Test
	public void testOptionalOrNonValidMax()
	{
		System.out.println("========= OPTIONAL-OR (Non valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("D"));
		p.addFeature(new Feature("G"));

		this.validProduct("relationships/optional-or/optional-or.fama",p,false);
	}
	
	// Test Case 38
	@Test
	public void testOptionalAlternativeValidMin()
	{
		System.out.println("========= OPTIONAL-ALTERNATIVE (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/optional-alternative/optional-alternative.fama",p,true);
	}
	
	// Test Case 39
	@Test
	public void testOptionalAlternativeValidMax()
	{
		System.out.println("========= OPTIONAL-ALTERNATIVE (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("D"));
		p.addFeature(new Feature("F"));
		p.addFeature(new Feature("G"));
		
		this.validProduct("relationships/optional-alternative/optional-alternative.fama",p,true);
	}
	
	// Test Case 40
	@Test
	public void testOptionalAlternativeNonValidMin()
	{
		System.out.println("========= OPTIONAL-ALTERNATIVE (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/optional-alternative/optional-alternative.fama",p,false);
	}
	
	// Test Case 41
	@Test
	public void testOptionalAlternativeNonValidMax()
	{
		System.out.println("========= OPTIONAL-ALTERNATIVE (Non valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("D"));
		p.addFeature(new Feature("E"));
		p.addFeature(new Feature("F"));
		p.addFeature(new Feature("G"));
		
		this.validProduct("relationships/optional-alternative/optional-alternative.fama",p,false);
	}
	
	// Test Case 42
	@Test
	public void testOrAlternativeValidMin()
	{
		System.out.println("========= OR-ALTERNATIVE (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("D"));
		
		this.validProduct("relationships/or-alternative/or-alternative.fama",p,true);
	}
	
	// Test Case 43
	@Test
	public void testOrAlternativeValidMax()
	{
		System.out.println("========= OR-ALTERNATIVE (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("D"));
		p.addFeature(new Feature("E"));
		p.addFeature(new Feature("F"));
		p.addFeature(new Feature("G"));
		p.addFeature(new Feature("H"));
		
		this.validProduct("relationships/or-alternative/or-alternative.fama",p,true);
	}
	
	// Test Case 44
	@Test
	public void testOrAlternativeNonValidMin()
	{
		System.out.println("========= OR-ALTERNATIVE (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/or-alternative/or-alternative.fama",p,false);
	}
	
	// Test Case 45
	@Test
	public void testOrAlternativeNonValidMax()
	{
		System.out.println("========= OR-ALTERNATIVE (Non valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("D"));
		p.addFeature(new Feature("E"));
		p.addFeature(new Feature("F"));
		p.addFeature(new Feature("G"));
		p.addFeature(new Feature("H"));
		p.addFeature(new Feature("I"));
		
		this.validProduct("relationships/or-alternative/or-alternative.fama",p,false);
	}
	
	// Test Case 46
	@Test
	public void testOrRequiresValidMin()
	{
		System.out.println("========= OR-REQUIRES (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/or-requires/or-requires.fama",p,true);
	}
	
	
	// Test Case 47
	@Test
	public void testOrRequiresValidMax()
	{
		System.out.println("========= OR-REQUIRES (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/or-requires/or-requires.fama",p,true);
	}
	
	// Test Case 48
	@Test
	public void testOrRequiresNonValidMin()
	{
		System.out.println("========= OR-REQUIRES (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/or-requires/or-requires.fama",p,false);
	}
	
	// Test Case 49
	@Test
	public void testOrRequiresNonValidMax()
	{
		System.out.println("========= OR-REQUIRES (Non valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		
		this.validProduct("relationships/or-requires/or-requires.fama",p,false);
	}
	
	// Test Case 50
	@Test
	public void testOrExcludes()
	{
		System.out.println("========= OR-EXCLUDES (Valid) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		
		this.validProduct("relationships/or-excludes/or-excludes.fama",p,true);
	}
	
	// Test Case 51
	@Test
	public void testOrExcludesNonValidMin()
	{
		System.out.println("========= OR-EXCLUDES (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/or-excludes/or-excludes.fama",p,false);
	}
	
	// Test Case 52
	@Test
	public void testOrExcludesNonValidMax()
	{
		System.out.println("========= OR-EXCLUDES (Non valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/or-excludes/or-excludes.fama",p,false);
	}
	
	// Test Case 53
	@Test
	public void testAlternativeRequiresValid()
	{
		System.out.println("========= ALTERNATIVE-REQUIRES (Valid) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/alternative-requires/alternative-requires.fama",p,true);
	}
	
	// Test Case 54
	@Test
	public void testAlternativeRequiresNonValidMin()
	{
		System.out.println("========= ALTERNATIVE-REQUIRES (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/alternative-requires/alternative-requires.fama",p,false);
	}
	
	// Test Case 55
	@Test
	public void testAlternativeRequiresNonValidMax()
	{
		System.out.println("========= ALTERNATIVE-REQUIRES (Non valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/alternative-requires/alternative-requires.fama",p,false);
	}
	
	// Test Case 56
	@Test
	public void testAlternativeExcludesValid()
	{
		System.out.println("========= ALTERNATIVE-EXCLUDES (Valid) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		
		this.validProduct("relationships/alternative-excludes/alternative-excludes.fama",p,true);
	}
	
	// Test Case 57
	@Test
	public void testAlternativeExcludesNonValidMin()
	{
		System.out.println("========= ALTERNATIVE-EXCLUDES (Non Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/alternative-excludes/alternative-excludes.fama",p,false);
	}
	
	// Test Case 58
	@Test
	public void testAlternativeExcludesNonValidMax()
	{
		System.out.println("========= ALTERNATIVE-EXCLUDES (Non Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/alternative-excludes/alternative-excludes.fama",p,false);
	}
	
	
	// Test Case 59
	@Test
	public void testRequiresExcludesValidMin()
	{
		System.out.println("========= REQUIRES-EXCLUDES (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/requires-excludes/requires-excludes.fama",p,true);
	}
	
	
	// Test Case 60
	@Test
	public void testRequiresExcludesValidMax()
	{
		System.out.println("========= REQUIRES-EXCLUDES (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/requires-excludes/requires-excludes.fama",p,true);
	}
	
	// Test Case 61
	@Test
	public void testRequiresExcludesNonValidMin()
	{
		System.out.println("========= REQUIRES-EXCLUDES (Non valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		
		this.validProduct("relationships/requires-excludes/requires-excludes.fama",p,false);
	}
	
	// Test Case 62
	@Test
	public void testRequiresExcludesNonValidMax()
	{
		System.out.println("========= REQUIRES-EXCLUDES (Non valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		
		this.validProduct("relationships/requires-excludes/requires-excludes.fama",p,false);
	}
	
	
	// Test Case 63
	@Test
	public void testAllRelationshipsValidMin()
	{
		System.out.println("========= ALL RELATIONSHIPS (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("D"));
		
		this.validProduct("relationships/allrelationships/allrelationships.fama",p,true);
	}
	
	// Test Case 64
	@Test
	public void testAllRelationshipsValidMax()
	{
		System.out.println("========= ALL RELATIONSHIPS (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("E"));
		p.addFeature(new Feature("F"));
		p.addFeature(new Feature("G"));
		
		this.validProduct("relationships/allrelationships/allrelationships.fama",p,true);
	}
	
	// Test Case 65
	@Test
	public void testAllRelationshipsNonValidMin()
	{
		System.out.println("========= ALL RELATIONSHIPS (Valid - min features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		
		this.validProduct("relationships/allrelationships/allrelationships.fama",p,false);
	}
	
	// Test Case 66
	@Test
	public void testAllRelationshipsNonValidMax()
	{
		System.out.println("========= ALL RELATIONSHIPS (Valid - max features) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("B"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("D"));
		p.addFeature(new Feature("E"));
		p.addFeature(new Feature("F"));
		p.addFeature(new Feature("G"));
		
		this.validProduct("relationships/allrelationships/allrelationships.fama",p,false);
	}
	
	// Errors
	
	// Test Case 67
	@Test
	public void testOptionalNoRoot() {
		
		System.out.println("========= OPTIONAL (ROOT FEATURE NOT SELECTED) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("B"));
		
		this.validProduct("relationships/optional/optional.fama",p,false);
	}
	
	// Test Case 68
	@Test
	public void testOptionalNonExistentFeature() {
		
		System.out.println("========= OPTIONAL (NON EXISTENT FEATURE) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("H"));
		
		this.validProduct("relationships/optional/optional.fama",p,false);
	}
	
	// ERROR GUESSING
	
	// Test Case 69
	 
	public void testOptionalAlternative() {
		
		System.out.println("========= OPTIONAL-ALTERNATIVE (Test whether child features can be selected without their parent ) ===========");
		
		Product p = new Product();
		p.addFeature(new Feature("A"));
		p.addFeature(new Feature("C"));
		p.addFeature(new Feature("D"));
		
		this.validProduct("error-guessing/optional-alternativeValidP/optional-alternativeVP.fama",p,false);
	}
	
}
