package es.us.isa.soup.preferences;

import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

public class LikesPreference extends QualitativePreference {

	public LikesPreference(){}
	
	public LikesPreference(VariabilityElement item){
		this.item = item;
	}
	
}
