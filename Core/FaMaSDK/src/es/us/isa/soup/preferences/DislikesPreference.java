package es.us.isa.soup.preferences;

import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

public class DislikesPreference extends QualitativePreference {

	public DislikesPreference(){}
	
	public DislikesPreference(VariabilityElement item){
		this.item = item;
	}
	
}
