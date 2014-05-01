package main.samples.guidedFMGeneration.fitness;

import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.errors.DeadFeatureError;
import es.us.isa.generator.FM.Evolutionay.FitnessFunction;

public class ExplanationsFitness implements FitnessFunction {

	@Override
	public double fitness(FAMAFeatureModel fm) {
		double numberOfexplanations = 0;
		QuestionTrader qt = new QuestionTrader();
		qt.setVariabilityModel(fm);
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		qt.ask(vq);
		// Decided to only allow valid models, as my pc (netbook) hasn't enought
		// power to exec fine
		if (vq.isValid()) {
			DetectErrorsQuestion q = (DetectErrorsQuestion) qt
					.createQuestion("DetectErrors");
			q.setObservations(fm.getObservations());
			qt.ask(q);

			Collection<es.us.isa.FAMA.errors.Error> errors = q.getErrors();
			ExplainErrorsQuestion qe = (ExplainErrorsQuestion) qt
					.createQuestion("Explanations");
			qe.setErrors(errors);
			qt.ask(qe);
			errors = qe.getErrors();

			Iterator<es.us.isa.FAMA.errors.Error> it = errors.iterator();
			while (it.hasNext()) {
				es.us.isa.FAMA.errors.Error e = it.next();
				if (e instanceof DeadFeatureError) {
					numberOfexplanations += e.getExplanations().size();
				}
			}
		}
		System.out.println(numberOfexplanations);
		return numberOfexplanations;
	}

}
