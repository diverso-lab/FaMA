package featureModelRepresentations;

import java.util.*;

import featureModel.*;

import JaCoP.*;

public class JacopFeatureModel implements IFeatureModelRepresentation {

	private ArrayList<FDV> features;

	private ArrayList<Constraint> constraints;

	private Map<String,Constraint> dependencies;

	private FDstore store;

	private FeatureModel featureModel;

	private String name;

	private String graph;

	private long seed;

	public JacopFeatureModel() {
		this.features = new ArrayList<FDV>();
		this.constraints = new ArrayList<Constraint>();
		this.store = new FDstore();
		this.featureModel = new FeatureModel();
		this.name = new String();
		this.dependencies = new HashMap<String,Constraint>();
	}

	public ArrayList<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(ArrayList<Constraint> constraints) {
		this.constraints = constraints;
	}

	public FDstore getStore() {
		return store;
	}

	public void setStore(FDstore store) {
		this.store = store;
	}

	public ArrayList<FDV> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<FDV> vars) {
		// TODO Auto-generated method stub

	}

	public FeatureModel getFeatureModel() {
		return featureModel;
	}

	public void setFeatureModel(FeatureModel featureModel) {
		this.featureModel = featureModel;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = new String(name);
	}

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = new String(graph);
	}

	public void setSeed(long seed) {
		this.seed = seed;

	}

	public long getSeed() {
		return this.seed;

	}

	public void setDependencies(Map<String,Constraint> dependencies) {

		this.dependencies = dependencies;
	}

	public void setDependency(String name, Constraint dependency) {

		this.dependencies.put(name,dependency);

	}

	public Map<String,Constraint> getDependencies() {
		return dependencies;
	}

}
