package es.us.isa.FAMA.TestSuite;



import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.models.FAMAfeatureModel.errors.DeadFeatureError;
import es.us.isa.FAMA.models.FAMAfeatureModel.errors.VoidFMError;
import es.us.isa.FAMA.models.featureModel.*;

public class DeadFeaturesQuestionSrc{
	
	Question q;
	GenericFeatureModel fm;
	QuestionTrader qt;
	@Before
	public void setUp(){}
    @After
    public void tearDown() {}
	
	private void deadFeatures(String inputName, Set<String> expectedFeatures)
	{
		// Read input
		qt = new QuestionTrader();
		fm = (GenericFeatureModel) qt.openFile("noEFM-test-inputs/error-guessing/" + inputName);
		qt.setVariabilityModel(fm);
		qt.setCriteriaSelector("selected"); // We select a specific solver
		
		// Perform question
		DetectErrorsQuestion q = (DetectErrorsQuestion) qt.createQuestion("DetectErrors");
		try {
			q.setObservations(fm.getObservations());
			qt.ask(q);
		} catch (FAMAException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		/*
		// Save dead features and check if the model is void
		Collection<Error> errors = q.getErrors();
		Iterator<Error> it = errors.iterator();
		boolean modelVoid = false;
		Set<String> deadFeatures=new HashSet<String>();
		while (it.hasNext()){
			Error e = it.next();
			if (e instanceof DeadFeatureError)
				deadFeatures.add(((DeadFeatureError)e).getDeadFeature().getName());
			else if (e instanceof VoidFMError)
				modelVoid = true;
		}*/
		
		
		boolean modelVoid = false;
		Set<String> deadFeatures=new HashSet<String>();
		String[] errors = q.toString().split("\\s+");
		
		
		// Check if it is void
		if (errors.length>=2)
			if (errors[1].equalsIgnoreCase("void"))
				modelVoid=true;
		
		// Save dead features if any
		for(int i=1;i<errors.length;i++) {
			deadFeatures.add(errors[i]);
		}
		
		//Show results
		System.out.println(q);
		
		// Check result
		if (modelVoid)
			assertEquals("Wrong output", expectedFeatures, null);
		else
			assertEquals("Wrong output", expectedFeatures, deadFeatures);
		
	}
	
	
	// Test Case 1
	@Test
	public void testDeadFeaturesCase1()
	{
		System.out.println("========= DEAD FEATURE - CASE 1  ===========");
		
		Set<String> features = new HashSet<String>();
		features.add("D");
		
		this.deadFeatures("dead-features/case1/df-case1.fama",features);
	}
	
	// Test Case 2
	@Test
	public void testDeadFeaturesCase2()
	{
		System.out.println("========= DEAD FEATURE - CASE 2  ===========");
		
		Set<String> features = new HashSet<String>();
		features.add("E");
		
		this.deadFeatures("dead-features/case2/df-case2.fama",features);
	}
	
	// Test Case 3
	@Test
	public void testDeadFeaturesCase3()
	{
		System.out.println("========= DEAD FEATURE - CASE 3  ===========");
		
		Set<String> features = new HashSet<String>();
		features.add("D");
		
		this.deadFeatures("dead-features/case3/df-case3.fama",features);
	}
	
	// Test Case 4
	@Test
	public void testDeadFeaturesCase4()
	{
		System.out.println("========= DEAD FEATURE - CASE 4  ===========");
		
		Set<String> features = new HashSet<String>();
		features.add("C");
		
		this.deadFeatures("dead-features/case4/df-case4.fama",features);
	}
	
	
	// Test Case 5
	@Test
	public void testDeadFeaturesCase5()
	{
		System.out.println("========= DEAD FEATURE - CASE 5  ===========");
		
		this.deadFeatures("dead-features/case5/df-case5.fama",null);
	}
	
	
	// Test Case 6
	@Test
	public void testDeadFeaturesCase6()
	{
		System.out.println("========= DEAD FEATURE - CASE 6  ===========");
		
		Set<String> features = new HashSet<String>();
		features.add("B");
		
		this.deadFeatures("dead-features/case6/df-case6.fama",features);
	}
	
	
	// Test Case 7
	@Test
	public void testDeadFeaturesCase7()
	{
		System.out.println("========= DEAD FEATURE - CASE 7  ===========");
		// null = void
		this.deadFeatures("dead-features/case7/df-case7.fama",null);
	}
	
	
	// Test Case 8
	@Test
	public void testDeadFeaturesCase8()
	{
		System.out.println("========= DEAD FEATURE - CASE 8  ===========");
		
		Set<String> features = new HashSet<String>();
		features.add("B");
		
		this.deadFeatures("dead-features/case8/df-case8.fama",features);
	}
	
}
