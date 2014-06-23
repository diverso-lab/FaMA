package es.us.isa.util;

import java.util.Collection;
import java.util.LinkedList;

import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class Utils {

	public static Collection<Configuration> products2Configurations(
			Collection<? extends GenericProduct> products, GenericFeatureModel fm) {
		Collection<Configuration> result = new LinkedList<Configuration>();
		Collection<? extends GenericFeature> features = fm.getFeatures();
		
		for (GenericProduct p : products) {
			Configuration conf = new Configuration();
			Collection<VariabilityElement> elements = p.getElements();
			for (GenericFeature f: features){
				if (elements.contains(f)){
					conf.addElement(f, 1);
				}
				else{
					conf.addElement(f, 0);
				}
			}
			result.add(conf);
		}
		return result;
	}


}