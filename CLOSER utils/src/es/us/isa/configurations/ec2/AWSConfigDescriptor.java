package es.us.isa.configurations.ec2;

import java.util.Collection;
import java.util.Map;

public class AWSConfigDescriptor {

	// subtrees to configure
	private Map<String,FeatureType> configAreas;
	
	// atts to configure
	private Collection<String> atts;

	public Map<String, FeatureType> getConfigAreas() {
		return configAreas;
	}

	public void setConfigAreas(Map<String, FeatureType> configAreas) {
		this.configAreas = configAreas;
	}

	public Collection<String> getAtts() {
		return atts;
	}

	public void setAtts(Collection<String> atts) {
		this.atts = atts;
	}
	
	

}
