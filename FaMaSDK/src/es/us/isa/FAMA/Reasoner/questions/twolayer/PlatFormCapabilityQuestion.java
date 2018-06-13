package es.us.isa.FAMA.Reasoner.questions.twolayer;

import java.util.Collection;

import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public interface PlatFormCapabilityQuestion extends TwoLayerQuestion {
	public Collection<Product> getBottomConfigurations();
	public void setConfTop(Configuration initConfig);
}
