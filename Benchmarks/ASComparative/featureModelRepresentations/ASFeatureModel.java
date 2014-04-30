package featureModelRepresentations;

import java.util.*;

import featureModel.*;

import JaCoP.*;

public class ASFeatureModel implements IFeatureModelRepresentation {

	private FeatureModel featureModel;		 // Feature Model
	private Map<String,List<String>> as;
	private String name;
	private long seed;
	
	private long time_compute_as=-1;

	public ASFeatureModel() {
		this.featureModel = new FeatureModel();
		this.as = new HashMap<String,List<String>>();
		this.name = new String();
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

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public long getSeed() {
		return this.seed;
	}
	
	public Map<String,List<String>> getAS() {
		return as;
	}
	
	public void generateAS() {
		
		long before = System.currentTimeMillis();
		
		FeatureNode root= this.featureModel.getRoot();
		this.addAtomicSet(root.getName(),root.getName());
		computeAS(root,root.getName());
		
		this.time_compute_as=System.currentTimeMillis() - before;
	}
	
	private void computeAS(FeatureNode f, String current_set_name) {
		
		Iterator it= f.getSubfeatures().iterator();
		while (it.hasNext())
		{
			FeatureNode g= (FeatureNode)it.next();
			if (g.getRelationType() == FeatureNode.MANDATORY)
			{
				this.addAtomicSet(current_set_name,g.getName());
				computeAS(g,current_set_name);
			} else
			{
				this.addAtomicSet(g.getName(),g.getName());
				computeAS(g,g.getName());
			}
		}
	}
	
	private void addAtomicSet(String set_name, String feature_name)
	{
		List<String> features;
		features = this.as.get(set_name);
		if (features==null) // New atomic set
		{
			features = new ArrayList<String>();
			features.add(feature_name);
			this.as.put(set_name,features);
		}else    // Existing atomic set
		{
			features.add(feature_name);
		}
	}
	
	public String printResults() {

		String res;
		res = "**************** FEATURE MODEL AS *******************\n";
		res += "NUMBER OF FEATURES: " + this.featureModel.getFeaturesNumber() + "\n";
		res += "NUMBER OF DEPENDENCIES: " + this.featureModel.getNumberOfDependencies() + "\n";
		res += "ATOMIC SETS: " + this.as.toString() + "\n"; 
		res += "NUMBER OF ATOMIC SETS: " + this.as.size() + "\n";
		res += "TIME TO COMPUTE THE ATOMIC SETS: " + this.time_compute_as + "\n";
		
		return res;
	}
	
	// Return an ArrayList of strings
	public ArrayList<String> printCSVResults() {

		ArrayList<String> results = new ArrayList<String>();
	
		// Number of atomic sets
		results.add(Integer.toString(this.as.size()));
		
		// Time to compute the atomic sets
		results.add(Long.toString(this.time_compute_as));
		
		return results;
	}
}
