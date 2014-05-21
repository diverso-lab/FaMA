package es.us.isa.soup.preferences;

import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

public class HighestPreference extends QuantitativePreference {

	public HighestPreference(){}
	
	public HighestPreference(VariabilityElement item){
		this.item = item;
	}
	
}
