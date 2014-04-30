package es.us.isa.FAMA.ws.auxiliar;

import java.util.Collection;
import java.util.LinkedList;

public class ExplanationProxy {

	private Collection<String> relationships;

	public ExplanationProxy() {
		this.relationships = new LinkedList<String>();
	}

	public Collection<String> getRelationships() {
		return relationships;
	}
	
	public void addRelationship(String s){
		relationships.add(s);
	}
	
}
