package es.us.isa.FAMA.models.config;

import es.us.isa.FAMA.stagedConfigManager.Configuration;

public interface ConfigParser {

	public ConfigParserResult parseConfiguration(String pathToFile);
	
	public ConfigParserResult parseConfigurationString(String config);
	
}
