package generators;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import featureModel.ExcludesDependency;
import featureModel.FeatureGroup;
import featureModel.FeatureModel;
import featureModel.FeatureNode;
import featureModel.RequiresDependency;

public class GraphFeatureModelGenerator implements IFeatureModelGenerator {

	private int name; 

	private Map<String, FeatureNode> featureNodes;
	
	private FeatureModel fm;

	// Graph
	private String graph;

	private Random rand;

	private long seed;

	public GraphFeatureModelGenerator() {
		this.name = 0;
		this.fm=new FeatureModel();

		this.rand = new Random();
		this.rand.setSeed(3);
		
		// =================================== Graph ===============================
		this.graph = "digraph G  { \n edge [dir=none]; \n";
		//this.graph += "node [shape=box, style=filled, fillcolor=lightgray]; \n";	// Make nodes rectangles
		//this.graph += "node [shape=box, width=0.7, height=0.3, style=filled, fillcolor=\"#E3E9FF\"]; \n";
		this.graph += "node [shape=box, width=0.7, height=0.3,style=filled, fillcolor=lightgray]; \n";
		//this.graph += "node [shape=box, width=0.7, height=0.3]; \n";
		//this.graph += "node [style=filled, fillcolor=lightgray]; \n";
		// =========================================================================
		
		this.featureNodes = new HashMap<String, FeatureNode>();
		
		FeatureNode root = new FeatureNode(new String("F0"));
		root.setRelationType(FeatureNode.ROOT);
		this.featureNodes.put("F0", root);
		this.fm.setRoot(root);
	}

	public void setSeed(long seed) {
		this.seed = seed;
		this.rand.setSeed(seed);

	}

	public long getSeed() {
		return this.seed;
	}

	public void generateFeatureModel(int w, int h, int ch, int d) {
		this.generateFeatureModelStore(w, h, ch, 0);
		this.generateDependsExludes(d);
		graph = graph + "\n}";
	}

	private void generateFeatureModelStore(int w, int h, int e, int parent) {

		if (h > 0) {
			int nChildren = rand.nextInt(w + 1);
			for (int j = 0; j < nChildren; j++) {
				int temp = h - 1;
				int relationType = rand.nextInt(RELATIONS);

				switch (relationType) {
				case MANDATORY:
					name++;
					String parentName = "F" + parent;
					String childName = "F" + name;


					// =================================== Graph ===============================
					graph = graph + parentName + " -> " + childName + ":n"
							+ "[arrowhead=\"dot\"]; ";
					// =========================================================================
					
					FeatureNode parentNode = this.featureNodes.get(parentName);
					FeatureNode childNode = new FeatureNode(childName);
					childNode.setRelationType(FeatureNode.MANDATORY);
					parentNode.setChild(childNode);
					this.featureNodes.put(childName, childNode);

					generateFeatureModelStore(w, temp, e, name);
					break;
				case OPTIONAL:
					name++;
					parentName = "F" + parent;
					childName = "F" + name;

				
					// =================================== Graph ===============================
					graph = graph + parentName + " -> " + childName + ":n"
							+ "[arrowhead=\"odot\"]; ";
					// =========================================================================

					parentNode = this.featureNodes.get(parentName);
					childNode = new FeatureNode(childName);
					childNode.setRelationType(FeatureNode.OPTIONAL);
					parentNode.setChild(childNode);
					this.featureNodes.put(childName, childNode);

					generateFeatureModelStore(w, temp, e, name);
					break;

				case OR:
					parentName = "F" + parent;

					FeatureGroup group = new FeatureGroup();
					int nChildrenSet = getSetCardinality(e);
					parentNode = this.featureNodes.get(parentName);
					ArrayList<String> names=new ArrayList<String>();	// User for recursion

					// =================================== Graph ===============================
					graph = graph + "subgraph cluster_" + name + " {\n";
					String graphCon = new String();
					// =========================================================================
					
					for (int i = 0; i < nChildrenSet; i++) {
						name++;
						childName = "F" + name;

						//Store the name for recursion
						names.add(childName);
						
						childNode = new FeatureNode(childName);
						childNode.setRelationType(FeatureNode.GROUPED);
						parentNode.setChild(childNode);
						this.featureNodes.put(childName, childNode);
						group.getFeatures().add(childNode);

						// =================================== Graph ===============================
						graph = graph + childName + ";";
						graphCon = graphCon + parentName + " -> " + childName + ":n" + ";";
						// =========================================================================

					}

					// =================================== Graph ===============================
					graph = graph + "label = \"OR-" + nChildrenSet + "\";}"
							+ graphCon;
					// =========================================================================

					group.setCardMin(1);
					group.setCardMax(nChildrenSet);

					parentNode.addGroup(group);
					
					
					for (Iterator iter = names.iterator(); iter.hasNext();) {
						
						String element = (String) iter.next();
						int setParent = Integer.parseInt(element.substring(1));
						generateFeatureModelStore(w, temp, e, setParent);

					} 

					break;

				case ALTERNATIVE:
					parentName = "F" + parent;

					group = new FeatureGroup();
					
					nChildrenSet = getSetCardinality(e);
					
					parentNode = this.featureNodes.get(parentName);
					names=new ArrayList<String>();	// User for recursion

					// =================================== Graph ===============================
					graph = graph + "subgraph cluster_" + name + " {\n";
					graphCon = new String();
					// =========================================================================

					for (int i = 0; i < nChildrenSet; i++) {
						name++;
						childName = "F" + name;

						//Store the name for recursion
						names.add(childName);

						childNode = new FeatureNode(childName);
						childNode.setRelationType(FeatureNode.GROUPED);
						parentNode.setChild(childNode);
						this.featureNodes.put(childName, childNode);
						group.getFeatures().add(childNode);

						// =================================== Graph ===============================
						graph = graph + childName + ";";
						graphCon = graphCon + parentName + " -> " + childName + ":n" + ";";
						// =========================================================================

					}

					// =================================== Graph ===============================
					graph = graph + "label = \"ALT-" + nChildrenSet + "\";}"
							+ graphCon;
					// =========================================================================

					group.setCardMin(1);
					group.setCardMax(1);

					parentNode.addGroup(group);
					
					for (Iterator iter = names.iterator(); iter.hasNext();) {
						
						String element = (String) iter.next();
						int setParent = Integer.parseInt(element.substring(1));
						generateFeatureModelStore(w, temp, e, setParent);

					} 

					break;
				}
			}
		}

	}

	private int generateDependsExludes(int number) {

		int nbDepende = 0;
		int loops = 0;

		if (number > 0) {

			while (loops < number) {
				FeatureNode f = featureNodes.get("F"
						+ rand.nextInt(this.featureNodes.size()));
				FeatureNode g = featureNodes.get("F"
						+ rand.nextInt(this.featureNodes.size()));

				if (valid(f, g)) {
					if (rand.nextBoolean() == true) {
						
						// =================================== Graph ===============================
						graph = graph + "edge [dir=forward] " + f.getName()
								+ " -> " + g.getName()
								+ "[color=\"blue\",label=\"D\"]; ";
						// =========================================================================

						this.fm.addDependency(
								new RequiresDependency(f, g));

					} else {

						// =================================== Graph ===============================
						graph = graph + "edge [dir=none] " + f.getName()
								+ " -> " + g.getName()
								+ "[color=\"red\",label=\"E\", dir=\"both\"]; ";
						// =========================================================================
						
						this.fm.addDependency(
								new ExcludesDependency(f, g));
						
					}
					nbDepende++;
				}
				loops++;
			}
		}

		return nbDepende;
	}

	private boolean valid(FeatureNode f, FeatureNode g) {
		boolean res = false;
		// valid:
		// - not parent(f,g) && not parent(g,f) && not are the same (are not
		// direct family)
		// - not already related
		// 

		if (!this.fm.areDirectFamily(f, g)
				&& !this.fm.haveRelation(f, g)) {
			res = true;
		}

		return res;

	}

	private String getRelation(ArrayList<String> no) {
		int feature = 0;
		boolean found = true;
		do {
			feature = rand.nextInt(name);
			found = no.contains(new String("F" + feature));
		} while (found);

		return new String("F" + feature);
	}

	private int getSetCardinality(int max) {
		int res = 0;
		do {
			// res = new Random().nextInt(max + 1);
			res = rand.nextInt(max + 1);

		} while (res <= 1);
		return res;
	}

	private int getSetUpperCardinality(int max) {
		int res = 0;
		do {
			// res = new Random().nextInt(max + 1);
			res = rand.nextInt(max + 1);

		} while (res <= 1);
		return res;
	}

	private int getUpperCardinality(int max) {
		int res = 0;
		do {
			res = rand.nextInt(max + 1);

		} while (res <= 1);
		return res;
	}

	public String getGraph() {
		return this.graph;
	}

}

