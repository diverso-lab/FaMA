package es.us.isa.FAMA.ReasonersTests;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Explanation;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.Exceptions.FAMAException;

public class ExplanationsTestSuite {

	@Test
	public void test1() throws FAMAException{
		QuestionTrader qt = new QuestionTrader();
		GenericFeatureModel fm = (GenericFeatureModel) qt.openFile("test-inputs/error-guessing/dead-features/case4/df-case4.fama");
		qt.setVariabilityModel(fm);//test-inputs\error-guessing\dead-features\case4\df-case4.fama

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
		
		ExplainErrorsQuestion eeq = (ExplainErrorsQuestion) qt.createQuestion("Explanations");
		eeq.setErrors(ce);
		qt.ask(eeq);
		ce = eeq.getErrors();
		Iterator<Error> itE = ce.iterator();
		while (itE.hasNext()){
			Error e = itE.next();
			System.out.println("Error: "+e.toString());
			Collection<Explanation> colExp = e.getExplanations();
			Iterator<Explanation> itExp = colExp.iterator();
			while (itExp.hasNext()){
				Explanation explanation = itExp.next();
				Collection<GenericRelation> relations = explanation.getRelations();
				Iterator<GenericRelation> itRel = relations.iterator();
				while (itRel.hasNext()){
					GenericRelation rel = itRel.next();
					System.out.println(rel.getName());
				}
			}
		}
	}
	
}
