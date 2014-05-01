package es.us.isa.fama.operations;

import java.util.Collection;
import java.util.LinkedList;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.fama.solvers.ChocoSolver;
import es.us.isa.soup.preferences.User;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayIntSolutionType;
import jmetal.util.JMException;

public class PreferencesREconfigurationProblem extends AAFMProblem {

	public PreferencesREconfigurationProblem(FAMAAttributedFeatureModel fm,
			Collection<User> preferences, Solution c0) {
		
		this.problemName_ = "PreferencesREconfiguration";
		this.fm = fm;
		this.userPreferences = preferences.toArray(new User[1]);
		fmToJMetalVars();
		solutionType_ = new ArrayIntSolutionType(this);
		
		// XXX we include the correctness of the configuration as an additional
		// objective
		this.numberOfObjectives_ = preferences.size() + 1;
		this.numberOfVariables_ = this.features.length + this.attributes.length;
		//we add the previous configuration as a seed
		seeds = new LinkedList<Solution>();
		seeds.add(c0);
		
		// then we ready the solver to check solutions
		solver = new ChocoSolver();
		solver.translate(fm, true);
		this.numberOfConstraints_ = solver.getNumberOfConstraints();
		
	}

}
