package featureModelRepresentations;

import java.util.*;
import featureModel.FeatureModel;

import choco.*;
import choco.integer.*;
import choco.integer.var.IntDomainVar;


public class ChocoFeatureModel implements IFeatureModelRepresentation {

	private ArrayList<IntVar> features;

	private ArrayList<Constraint> constraints;

	private Map<String,Constraint> dependencies;

	private Problem problem;

	private FeatureModel featureModel;

	private String name;

	private String graph;

	private long seed;

	public ChocoFeatureModel() {
		this.features = new ArrayList<IntVar>();
		this.constraints = new ArrayList<Constraint>();
		this.problem = new Problem();
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

	public Problem getProblem() {
		return problem;
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	public ArrayList<IntVar> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<IntVar> vars) {
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
