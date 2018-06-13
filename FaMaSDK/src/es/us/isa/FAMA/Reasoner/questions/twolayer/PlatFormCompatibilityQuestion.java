package es.us.isa.FAMA.Reasoner.questions.twolayer;

import java.util.Collection;

import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public interface PlatFormCompatibilityQuestion extends TwoLayerQuestion {
	public Collection<GenericFeature> deadFeatures();
	public Collection<GenericFeature> aliveFeatures();
	public void setConfTop(Configuration inittConfig);
	public void setConfBottom(Configuration initbConfig);

}
