package es.us.isa.fama.operations;

import java.util.Collection;
import java.util.Map;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.ArrayIntSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.fama.solvers.ChocoSolver;
import es.us.isa.soup.preferences.AroundPreference;
import es.us.isa.soup.preferences.DislikesPreference;
import es.us.isa.soup.preferences.HighestPreference;
import es.us.isa.soup.preferences.LikesPreference;
import es.us.isa.soup.preferences.LowestPreference;
import es.us.isa.soup.preferences.Preference;
import es.us.isa.soup.preferences.User;

public class PreferencesConfigurationProblem extends AAFMProblem {

	// public PreferencesConfigurationProblem(String solutionType, )

	public PreferencesConfigurationProblem(FAMAAttributedFeatureModel fm,
			Collection<User> preferences, int n) {
		// XXX is this part complete?
		this.problemName_ = "PreferencesConfiguration";
		this.fm = fm;
		this.userPreferences = preferences.toArray(new User[1]);
		fmToJMetalVars();
		solutionType_ = new ArrayIntSolutionType(this);
		solver = new ChocoSolver();
		// first we obtain the seeds
		solver.translate(fm, false);
		this.numberOfVariables_ = this.features.length + this.attributes.length;
		// XXX we include the correctness of the configuration as an additional
		// objective
		this.numberOfObjectives_ = preferences.size() + 1;
		this.seeds = solver.computeSeeds(this, n);
		this.numberOfConstraints_ = solver.getNumberOfConstraints();
		// then we ready the solver to check solutions
		solver.translate(fm, true);
		
	}

}
