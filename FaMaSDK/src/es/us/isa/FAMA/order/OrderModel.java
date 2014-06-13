package es.us.isa.FAMA.order;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import es.us.isa.FAMA.models.featureModel.Constraint;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;

public class OrderModel {

	
	private Map<String, GenericAttribute> properties;
	private Collection<Constraint> constraints;

	
	public OrderModel(){
		
		properties = new HashMap<String, GenericAttribute>();
		constraints = new LinkedList<Constraint>();
	}
	
	public GenericAttribute getProperty(String name){
		return properties.get(name);
	}
	
	public void addProperty(GenericAttribute att){
		properties.put(att.getName(), att);
	}
	
	public Collection<GenericAttribute> getAllProperties(){
		return properties.values();
	}
	
	
	public void addConstraint(Constraint c){
		constraints.add(c);
	}
	
	public Collection<Constraint> getConstraints(){
		return constraints;
	}

}
