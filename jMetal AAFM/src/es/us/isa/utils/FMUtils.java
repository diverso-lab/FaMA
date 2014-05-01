package es.us.isa.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class FMUtils {
	
	/**
	 * TODO test this method to check that it works fine
	 * 
	 * @param fm
	 * @param attribute
	 * @return
	 */
	public static GenericAttribute searchAttribute(FAMAAttributedFeatureModel fm,
			String attribute) {
		GenericAttribute result = null;
		int index = attribute.indexOf('.');
		String featureName = attribute.substring(0, index);
		String attName = attribute.substring(index + 1);

		Collection<AttributedFeature> c = fm.getAttributedFeatures();
		Iterator<AttributedFeature> it = c.iterator();
		boolean b = false;
		while (it.hasNext() && !b) {
			AttributedFeature f = it.next();
			if (f.getName().equalsIgnoreCase(featureName)) {
				b = true;
				boolean b2 = false;
				Collection<GenericAttribute> atts = f.getAttributes();
				Iterator<GenericAttribute> itAtts = atts.iterator();
				while (itAtts.hasNext() && !b2) {
					GenericAttribute att = itAtts.next();
					if (att.getName().equalsIgnoreCase(attName)) {
						b2 = true;
						result = att;
					}
				}
			}
		}
		return result;

	}
	
	public static String config2String(Configuration c){
		String res = "";
		Map<VariabilityElement,Integer> elements = c.getElements();
		Set<Entry<VariabilityElement,Integer>> entries = elements.entrySet();
		for (Entry<VariabilityElement,Integer> entry:entries){
			VariabilityElement v = entry.getKey();
			if (v instanceof GenericAttribute){
				GenericAttribute att = (GenericAttribute) v;
				res += att.getFullName().trim()+"="+entry.getValue()+" \n";
			}
			else{
				res += v.getName().trim()+"="+entry.getValue()+" \n";
			}
			
		}
		return res;
	}
	
}
