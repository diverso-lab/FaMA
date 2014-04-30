package featureModelRepresentations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import featureModel.FeatureModel;

import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.BDD;
import net.sf.javabdd.JFactory;

public class JavaBDDFeatureModelAS implements IFeatureModelRepresentation {

	private String name;					// Name
	private long seed;						// Seed
	private BDDFactory factory;				// JFactory
	private Map<String, BDD> variables;		// Variables
	private Map<String,List> as;    		// Atomic sets
	private ArrayList<BDD> nodes;			// SubTrees 
	private FeatureModel featureModel;		// FeatureModel
	
	public JavaBDDFeatureModelAS() {
		this.variables=new HashMap<String,BDD>();
		this.as= new HashMap<String, List>();
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
	
	public Map<String,List> getAtomicSets() {
		return as;
	}
	
	// Add a new atomic set
	public void addAtomicSet(String set,String feature)
	{
		List<String> features;
		features = this.as.get(set);
		if (features==null) // New atomic set
		{
			features = new ArrayList<String>();
			features.add(feature);
			this.as.put(set,features);
		}else    // Existing atomic set
		{
			features.add(feature);
		}
	}

}
