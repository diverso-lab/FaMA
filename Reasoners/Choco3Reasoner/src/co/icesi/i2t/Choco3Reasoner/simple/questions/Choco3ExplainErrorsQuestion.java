/**
 * 
 */
package co.icesi.i2t.Choco3Reasoner.simple.questions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import solver.constraints.Constraint;
import co.icesi.i2t.Choco3Reasoner.Choco3PerformanceResult;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Question;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.errors.Error;

/**
 * Implementation to solve the explain errors question using the Choco 3 reasoner.
 * When a feature model has errors, this operation looks for explanations for those errors. 
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoExplainErrorFMDIAG Choco 2 implementation for the explain errors question
 * @version 0.1, June 2014
 */
public class Choco3ExplainErrorsQuestion extends Choco3Question implements
		ExplainErrorsQuestion {

	/**
	 * The collection of errors in the feature model.
	 */
	private Collection<Error> errors;
	/**
	 * The collection of relations in the CSO.
	 */
	private Map<String, Constraint> relations;
	
	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion#setErrors(java.util.Collection)
	 */
	public void setErrors(Collection<Error> errors) {
		this.errors = errors;
	}

	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion#getErrors()
	 */
	public Collection<Error> getErrors() {
		return this.errors;
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		this.relations = new HashMap<String, Constraint>();
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#answer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public PerformanceResult answer(Reasoner reasoner) {
		// Cast the reasoner into a Choco 3 Reasoner instance
		Choco3Reasoner choco3Reasoner = (Choco3Reasoner) reasoner;
		// TODO Implement explain errors answer
		// Create and return performance result
		Choco3PerformanceResult performanceResult = new Choco3PerformanceResult();
		return performanceResult;
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#postAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void postAnswer(Reasoner reasoner) {
		// Not needed,
	}

}
