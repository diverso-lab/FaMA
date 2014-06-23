package es.us.isa.FAMA.Reasoner.questions;

import java.util.Collection;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public interface ValidConfigurationSetQuestion extends Question {

	public void setConfigurationSet(Collection<Configuration> configs);
	
	public Collection<Configuration> getInvalidConfigurations();
	
	public int getNumberOfInvalidConfigs();
	
}
