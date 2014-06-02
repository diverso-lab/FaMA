package es.us.isa.FAMA.models.config;

import java.util.Collection;

import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class ConfigParserResult {

	private Collection<String> errors;
	private Configuration config;
	
	public Collection<String> getErrors() {
		return errors;
	}
	public void setErrors(Collection<String> errors) {
		this.errors = errors;
	}
	public Configuration getConfig() {
		return config;
	}
	public void setConfig(Configuration config) {
		this.config = config;
	}
	
	
	
}
