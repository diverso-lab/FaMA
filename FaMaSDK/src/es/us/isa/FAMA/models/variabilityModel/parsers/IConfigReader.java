package es.us.isa.FAMA.models.variabilityModel.parsers;

import es.us.isa.FAMA.Exceptions.FAMAConfigurationException;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public interface IConfigReader {

	public Configuration parseConfiguration(VariabilityModel vm, String fileToPath) throws FAMAConfigurationException;
	
}
