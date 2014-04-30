package es.us.isa.FAMA.ReasonersTests;
import es.us.isa.FAMA.Exceptions.FAMAException;

import java.util.Iterator;

import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.GenericFeature;


/**
 * This jUnit test case tests the commonality question for anyone reasoner.
 * @author Jes�s
 *
 */
public class CommonalityTestSuite extends TestSuite {

	@Test
	public void testCommonalityQuestion() throws FAMAException{
		this.fm.getFeatures().iterator();
		Question q = null;
		q = qt.createQuestion("Commonality");
		CommonalityQuestion	pq = (CommonalityQuestion) q;
		Iterator<? extends GenericFeature> it = this.fm.getFeatures().iterator();
		System.out.println("---- COMMONALITY QUESTION TEST ----");
		while (it.hasNext()){
			GenericFeature f = it.next();
			pq.setFeature(f);
			@SuppressWarnings("unused")
			PerformanceResult pr = qt.ask(pq);
			Long l = pq.getCommonality();
			System.out.println("\nFeature " + f.getName() + ", Commonality: " + l + "\n");
		}
		
		GenericFeature f = new Feature("falsa");
		pq.setFeature(f);
		qt.ask(pq);
		Long l = pq.getCommonality();
		System.out.println("\nFeature " + f.getName() + " (must be 0), Commonality: " + l + "\n");
		
	}
	
}
