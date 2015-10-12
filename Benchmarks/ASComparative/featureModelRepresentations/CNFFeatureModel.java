package featureModelRepresentations;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import featureModel.FeatureModel;

import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.BDD;
import net.sf.javabdd.JFactory;

public class CNFFeatureModel implements IFeatureModelRepresentation {

	private String name;					// Name
	private long seed;						// Seed
	private Map<String,String> variables;	// Variables
	private ArrayList<String> clauses ;		// Clauses 
	private FeatureModel featureModel;		// FeatureModel
	
	public CNFFeatureModel() {
		this.variables=new HashMap<String,String>();
		this.clauses=new ArrayList<String>();
		this.featureModel=new FeatureModel();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}

	public void setSeed(long seed) {
		this.seed=seed;
	}

	public long getSeed() {
		return seed;
	}
		
	public FeatureModel getFeatureModel() {
		return featureModel;
	}
	
	public void setFeatureModel(FeatureModel featureModel) {
		this.featureModel = featureModel;
	}
	
	public Map<String,String> getVariables() {
		return this.variables;
	}
	
	public void addVariable(String name, String var) {
		this.variables.put(name,var);
	}
	
	public String getVariable(String name) {
		return this.variables.get(name);
	}
	
	
	public ArrayList<String> getClauses() {
		return this.clauses;
	}
	
	public void addClause(String clause) {
		this.clauses.add(clause);
	}

}
