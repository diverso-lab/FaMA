package es.us.isa.soup.preferences;

import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

public class AroundPreference extends QuantitativePreference {
	
	private Double value;

	public AroundPreference(){}
	
	public AroundPreference(VariabilityElement item, Double value){
		this.item = item;
		this.value = value;
	}
	
	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	public boolean equals(Object o){
		boolean result = false;
		
		if (o instanceof AroundPreference){
			AroundPreference aux = (AroundPreference) o;
			if (aux.getItem().equals(item) && aux.getValue().equals(value)){
				result = true;
			}
		}
		
		return result;
	}
	
	public String toString(){
		String result = super.toString() + ","+this.value;
		return result;
	}
}
