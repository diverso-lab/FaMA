package es.us.isa.FAMA.ReasonersTests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import es.us.isa.FAMA.Exceptions.FAMAException;

import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Observation;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.errors.DeadFeatureObservation;

/**
 * This jUnit test case tests the DetectErrors question for anyone reasoner
 * @author Jes�s
 *
 */
public class DetectErrorsTestSuite extends TestSuite {

	@Test
	public void testDetectErrorsQuestion() throws FAMAException{
		Question q = qt.createQuestion("DetectErrors");
		DetectErrorsQuestion dq = (DetectErrorsQuestion) q; 
		
		/*List<Observation> l = new ArrayList<Observation>();
		l.add(new DeadFeatureObservation((Feature) this.fm.searchFeatureByName("D")));
		dq.setObservations(l);*/
		dq.setObservations(fm.getObservations());
		@SuppressWarnings("unused")
		PerformanceResult pf = qt.ask(dq);
		System.out.println(dq);
		Collection<Error> ce = dq.getErrors();
		System.out.println("\n---- DETECT ERRORS QUESTION TEST ----\n");
		System.out.println("Has the model errors? "+!ce.isEmpty());
	}
	
}
