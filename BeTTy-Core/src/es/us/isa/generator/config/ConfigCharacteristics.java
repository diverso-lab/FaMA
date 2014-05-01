package es.us.isa.generator.config;

import es.us.isa.generator.config.enums.ConfigValidity;

public class ConfigCharacteristics {

	protected ConfigValidity valid;
	protected boolean full;
	protected FeatureConfigCharacteristics featureConfig;
	
	public ConfigValidity getValid() {
		return valid;
	}
	public void setValid(ConfigValidity valid) {
		this.valid = valid;
	}
	public boolean isFull() {
		return full;
	}
	public void setFull(boolean full) {
		this.full = full;
	}
	public FeatureConfigCharacteristics getFeatureConfig() {
		return featureConfig;
	}
	public void setFeatureConfig(FeatureConfigCharacteristics featureConfig) {
		this.featureConfig = featureConfig;
	}
	
	
	
}
