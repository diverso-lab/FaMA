package es.us.isa.FAMA.Reasoner.questions;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;

public interface OptimisingConfigurationQuestion extends Question {
	
	public ExtendedConfiguration getOptimalConfiguration();
	
	public void minimise(String attName);
	
	public void maximise(String attName);
	
//	public void setInitialConfig(ComplexConfiguration config);
	
	public void setConfiguration(ExtendedConfiguration config);
	
	public void setTimeLimit(int miliseconds);
	
}
