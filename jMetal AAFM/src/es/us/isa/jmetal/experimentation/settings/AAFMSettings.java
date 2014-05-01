package es.us.isa.jmetal.experimentation.settings;

import es.us.isa.fama.operations.AAFMProblem;
import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.util.JMException;

public abstract class AAFMSettings extends Settings {

	public AAFMSettings(AAFMProblem problem){
		super(problem.getName());
		this.problem_ = problem;
	}

}
