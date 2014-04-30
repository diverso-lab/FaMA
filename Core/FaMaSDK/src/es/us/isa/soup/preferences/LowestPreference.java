package es.us.isa.soup.preferences;

import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

public class LowestPreference extends QuantitativePreference {

	public LowestPreference(){}
	
	public LowestPreference(VariabilityElement item){
		this.item = item;
	}
	
	
}
