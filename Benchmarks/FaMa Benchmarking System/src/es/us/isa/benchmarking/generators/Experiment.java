package es.us.isa.benchmarking.generators;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class Experiment {

	private String name;
	private VariabilityModel vm;
	private Collection<Map<String,String>> results;
	public Experiment(){
		this("No name experiment",null);
	}
	
	public Experiment(String experimentName) {
		this(experimentName,null);
	}
	
	
	
	public Experiment(String n, VariabilityModel model){
		results = new LinkedList<Map<String,String>>();
		this.name = n;
		this.vm = model;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public VariabilityModel getVariabilityModel() {
		return vm;
	}

	public void setVariabilityModel(VariabilityModel vm) {
		this.vm = vm;
	}
	
	public Collection<Map<String,String>> getResults() {
		return results;
	}
	
	public void addResult(Map<String,String> result){
		results.add(result);
	}
	
	public void addResults(Collection<Map<String,String>> res){
		results.addAll(res);
	}

	
	
	
}
