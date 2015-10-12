package es.us.isa.soup.preferences;

import java.util.Collection;

public class User {

	private Collection<Preference> preferences;
	private String name;
	private int weight;
	
	public User(){}
	
	public User(Collection<Preference> preferences, String name) {
		super();
		this.preferences = preferences;
		this.name = name;
	}
	
	public Collection<Preference> getPreferences() {
		return preferences;
	}
	public void setPreferences(Collection<Preference> preferences) {
		this.preferences = preferences;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getWeight(){
		return weight;
	}
	
	public void setWeight(int w){
		this.weight = w;
	}
	
	public boolean equals(Object o){
		boolean result = false;
		
		if (o instanceof User){
			User aux = (User) o;
			if (aux.getName().equals(name) && aux.getWeight() == weight){
				result = true;
			}
		}
		
		return result;
	}
	
	public String toString(){
		String result = this.name+". weight = "+this.weight +". Preferences: ";
		for (Preference p:this.preferences){
			result += p.toString() +". ";
		}
		return result;
	}
}
