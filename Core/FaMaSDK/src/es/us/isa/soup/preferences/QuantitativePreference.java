package es.us.isa.soup.preferences;

import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;

public abstract class QuantitativePreference extends Preference {

	//the 'item' should be an attribute or a cardinality
	//quantitative preferences are not valid for features
	
	public String toString(){
		String name ="";
		if (item instanceof GenericAttribute){
			name = ((GenericAttribute) item).getFullName();
		}
		else{
			name = item.getName();
		}
		String result = this.getClass().getSimpleName() + ": "+ name;
		return result;
	}
	
}
