package es.us.isa.fama.operations;

import java.util.Collection;
import java.util.LinkedList;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.util.JMException;

public abstract class AlgorithmWithSeeds extends Algorithm {

	protected Collection<Solution> seeds;

	public AlgorithmWithSeeds(Problem p) {
		super(p);
		seeds = new LinkedList<Solution>();
		if (p instanceof AAFMProblem){
			seeds.addAll(((AAFMProblem) p).getSeeds());
		}
	}

	public Collection<Solution> getSeeds() {
		return seeds;
	}

	public void setSeeds(Collection<Solution> seeds) {
		this.seeds = seeds;
	}

	protected void evaluate(Solution s) throws JMException {
		//XXX internally we're invoking evaluateConstraints in the evaluate method
		problem_.evaluate(s);
	}

	protected Solution processSeed(Solution s) throws ClassNotFoundException {
		Solution result = new Solution(problem_);
		result.setDecisionVariables(s.getDecisionVariables());
		return result;
	}

}
