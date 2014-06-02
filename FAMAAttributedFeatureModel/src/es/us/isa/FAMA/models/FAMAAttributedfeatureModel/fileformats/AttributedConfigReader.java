package es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats;

import es.us.isa.FAMA.Exceptions.FAMAConfigurationException;
import es.us.isa.FAMA.models.config.ConfigParserResult;
import es.us.isa.FAMA.models.config.ExtendedConfigParser;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IConfigReader;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class AttributedConfigReader implements IConfigReader {

	public Configuration parseConfiguration(VariabilityModel vm, String fileToPath) throws FAMAConfigurationException {
		ExtendedConfigParser configParser;
		Configuration result = null;
		if (!(vm instanceof GenericAttributedFeatureModel)) {
			throw new IllegalArgumentException(
					"The expected Variability Model is not an Extended Feature Model");
		} else {
			configParser = new ExtendedConfigParser(
					(GenericAttributedFeatureModel) vm);
			ConfigParserResult aux = configParser.parseConfiguration(fileToPath);
			result = aux.getConfig();
		}
		return result;
	}

}
