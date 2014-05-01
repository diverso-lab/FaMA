package es.us.isa.generator.config;

import java.util.Collection;

import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.generator.config.enums.RelType;

/**
 * This class generates valid configurations, but which present conflicts when merged.
 * We need this kind of generator to test the ASE operation.
 * 
 * @author jesus
 *
 */
public class RelatedConflictiveConfigsGenerator {

	/**
	 * This method generates valid configurations, but which present conflicts when merged.
	 * 
	 * @param fm Feature Model
	 * @param chars characteristics of the configurations to be generated. Valid must be 'true'
	 * @param featureConflicts generate feature conflicts among different configs
	 * @param relConflicts generate relationship conflicts (e.g. selecting excluding features) between different configs
	 * @param n number of configurations to be generated
	 * @return Collection<Configuration> which present conflict among them
	 */
	public Collection<Configuration> 
		generateRelatedConflictiveConfigs(GenericFeatureModel fm, 
				ConfigCharacteristics chars, boolean featureConflicts, 
				RelType relConflicts, int n){
		// TODO
		return null;
	}
	
}
