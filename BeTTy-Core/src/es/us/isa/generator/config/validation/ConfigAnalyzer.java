package es.us.isa.generator.config.validation;

import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.generator.config.enums.ConfigValidity;

/**
 * This class should be extended by an adapter class which uses an
 * AAFM tool
 * @author jesus
 *
 */
public abstract class ConfigAnalyzer {

	abstract public ConfigValidity validate(Configuration c);
	
	abstract public Configuration oneProduct(Configuration c);
	
	abstract public Configuration propagate(Configuration c);
	
}
