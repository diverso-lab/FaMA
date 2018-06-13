package es.us.isa.FAMA.Reasoner;

import java.util.Collection;

import es.us.isa.FAMA.stagedConfigManager.Configuration;

public abstract class TwoLayerReasoner extends Reasoner {

	public abstract void createProblem(Question q);
	public abstract void addConfigurations(Collection<Configuration> cc);
	public abstract void unapplyStagedConfigurations();

}
