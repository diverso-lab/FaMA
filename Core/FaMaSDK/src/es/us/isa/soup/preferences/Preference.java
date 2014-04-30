package es.us.isa.soup.preferences;

import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

public abstract class Preference {

	protected VariabilityElement item;

	public Preference(){
		
	}
	
	public Preference(VariabilityElement item){
		this.item = item;
	}
	
	public VariabilityElement getItem() {
		return item;
	}

	public void setItem(VariabilityElement item) {
		this.item = item;
	}
	
	public boolean equals(Object o){
		boolean result = false;
		if (o.getClass().equals(this.getClass())){
			Preference p = (Preference) o;
			if (p.getItem().equals(item)){
				result = true;
			}
		}
		return result;
	}
	
	public String toString(){
		String result = this.getClass().getSimpleName() + ": "+ this.item.getName();
		return result;
	}
	
	
}
