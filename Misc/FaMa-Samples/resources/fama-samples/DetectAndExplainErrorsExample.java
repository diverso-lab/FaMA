package main;

import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Explanation;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.GenericRelation;

public class DetectAndExplainErrorsExample {

	public static void main(String[] args){
		
		QuestionTrader qt = new QuestionTrader();
		GenericFeatureModel fm = (GenericFeatureModel) qt.openFile("fm-samples/ErrorsExample.xml");
		qt.setVariabilityModel(fm);
		DetectErrorsQuestion q = (DetectErrorsQuestion) qt.createQuestion("DetectErrors");
		q.setObservations(fm.getObservations());
		qt.ask(q);
		
		Collection<Error> errors = q.getErrors();
		ExplainErrorsQuestion qe = (ExplainErrorsQuestion)qt.createQuestion("Explanations");
		qe.setErrors(errors);
		qt.ask(qe);
		errors = qe.getErrors();
		
		Iterator<Error> it = errors.iterator();
		while (it.hasNext()){
			Error e = it.next();
			Collection<Explanation> explanations = e.getExplanations();
			Iterator<Explanation> itExp = explanations.iterator();
			System.out.println("Explanations for error "+e);
			while (itExp.hasNext()){
				Explanation exp = itExp.next();
				Collection<GenericRelation> relations = exp.getRelations();
				Iterator<GenericRelation> itRel = relations.iterator();
				while (itRel.hasNext()){
					GenericRelation rel = itRel.next();
					System.out.println(rel.getName());
				}
			}
		}
		
	}

}
