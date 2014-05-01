/**
 * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.benchmarking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
/**
 * This class represents an experiment
 */
public class Experiment {

	private String name;
	private VariabilityModel vm;
	private ArrayList<Map<String,String>> results;
	public Experiment(){
		this("No name experiment",null);
	}
	/**
	 * Creates a new experiment with the desired name
	 * @param experimentName the name of the experiment
	 */
	public Experiment(String experimentName) {
		this(experimentName,null);
	}
	
	
	/**
	 * Creates a new experiment using a specified name and a variability model
	 * @param n The name of the experiment
	 * @param model the variabilityModel
	 */
	public Experiment(String n, VariabilityModel model){
		results = new ArrayList<Map<String,String>>();
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
	/**
	 * 
	 * @return return the results after an execution-
	 */
	public Collection<Map<String,String>> getResults() {
		return results;
	}
	/**
	 * 
	 * @param n the result number n
	 * @return a map this the n restult
	 */
	public Map<String,String> getResults(int n){
		return results.get(n);
	}
	public Map<String,String> getLastResults(){
		return results.get(results.size()-1);
	}
	public void addResult(Map<String,String> result){
		results.add(result);
	}
	
	public void addResults(Collection<Map<String,String>> res){
		results.addAll(res);
	}

	
	
	
}
