package featureModelRepresentations;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import featureModel.FeatureModel;

import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.BDD;
import net.sf.javabdd.JFactory;

public class JavaBDDFeatureModel implements IFeatureModelRepresentation {

	private String name;					// Name
	private long seed;						// Seed
	private BDDFactory factory;				// JFactory
	private Map<String, BDD> variables;		// Variables
	private ArrayList<BDD> nodes;			// SubTrees 
	private FeatureModel featureModel;		// FeatureModel
	
	public JavaBDDFeatureModel() {
		this.variables=new HashMap<String,BDD>();
		this.nodes=new ArrayList<BDD>();
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
	
	public BDDFactory getFactory() {
		return this.factory;
	}
	
	public void setFactory(BDDFactory factory) {
		this.factory=factory;
	}
	
	public FeatureModel getFeatureModel() {
		return featureModel;
	}
	
	public void setFeatureModel(FeatureModel featureModel) {
		this.featureModel = featureModel;
	}
	
	public Map<String,BDD> getVariables() {
		return this.variables;
	}
	
	public void addVariable(String name, BDD var) {
		this.variables.put(name,var);
	}
	
	public BDD getVariable(String name) {
		return this.variables.get(name);
	}
	
	
	public ArrayList<BDD> getNodes() {
		return this.nodes;
	}
	
	public void addNode(BDD node) {
		this.nodes.add(node);
	}

}
