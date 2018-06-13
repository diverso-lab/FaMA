package es.us.isa.FAMA.Reasoner.questions.twolayer;

import java.util.Collection;

import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public interface PlatFormMigrationQuestion extends TwoLayerQuestion {
	public Collection<GenericFeature> getDiscartedFeatures();
	public Collection<GenericFeature> getAddedFeatures();
	public void setConfTop(Configuration confTop);
	public void setConfBottom1(Configuration confBottom1);
	public void setConfBottom2(Configuration confBottom2);

}
