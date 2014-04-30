package generators;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import featureModel.Dependency;
import featureModel.ExcludesDependency;
import featureModel.FeatureGroup;
import featureModel.FeatureNode;
import featureModel.RequiresDependency;
import featureModelRepresentations.*;

import JaCoP.And;
import JaCoP.Constraint;
import JaCoP.FD;
import JaCoP.FDV;

import JaCoP.IfThen;
import JaCoP.IfThenElse;
import JaCoP.In;
import JaCoP.Sum;
import JaCoP.XeqC;
import JaCoP.XeqY;
import JaCoP.XgtC;

public class JacopFeatureModelGenerator implements IFeatureModelGenerator {


	private static int experimentNumber = 0; // number to identify any new

	// experiment

	private int name; // feature Names to be generated

	private JacopFeatureModel fm;

	private Map<String, FeatureNode> featureNodes;

	// a graph
	private String graph;

	private Random rand;

	private long seed;

	public JacopFeatureModelGenerator() {
		this.name = 0;
		this.fm = new JacopFeatureModel();

		this.rand = new Random();
		this.rand.setSeed(3);

		this.graph = new String("digraph G { \nedge [dir=none];\n");
		this.featureNodes = new HashMap<String, FeatureNode>();
		Date now = new Date();

		experimentNumber++;
		this.fm.setName(now.getDate() + "_" + now.getMonth() + "-"
				+ now.getHours() + "_" + now.getMinutes() + "_"
				+ now.getSeconds() + "_" + experimentNumber);

		
		// ============================ Jacop ========================
		
		//Create the root feature (variable Jacop)
		FDV varRoot = new FDV(fm.getStore(), "F0", 0, 1);

		//Add root feature to variables
		this.fm.getFeatures().add(varRoot);
		
		//Add a new constraint: varRoot is root.
		this.fm.getConstraints().add(new XeqC(varRoot, 1));

		//=============================================================
		
		FeatureNode root = new FeatureNode(new String("F0"));
		root.setRelationType(FeatureNode.ROOT);
		this.featureNodes.put("F0", root);
		this.fm.getFeatureModel().setRoot(root);
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
		this.fm.setGraph(this.graph);
		this.fm.setSeed(this.seed);
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

					//============================ Jacop ========================
					
					// Get the parent feature
					FDV p = fm.getStore().getFDV(parentName);
					
					// Create a variable: feature
					FDV c = new FDV(fm.getStore(), childName, 0, 1);
					
					// Add the feature 
					fm.getFeatures().add(c);
					
					// Add the constraint (mandatory)
					fm.getConstraints().add(new XeqY(c, p));

					//=============================================================
					
					graph = graph + parentName + " -> " + childName + ";";

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

					
					//============================ Jacop ========================
					
					// Get the parent feature
					p = fm.getStore().getFDV(parentName);
					// Create a variable: feature
					c = new FDV(fm.getStore(), childName, 0, 1);

					// Add the feature 
					fm.getFeatures().add(c);
					
					// Add the constraint (optional)
					fm.getConstraints().add(
							new IfThen(new XeqC(p, 0), new XeqC(c, 0)));
					
					//=============================================================
					
					graph = graph + parentName + " -> " + childName
							+ "[headlabel=\"O\"]; ";

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
					
					//============================ Jacop ========================	
					
					ArrayList<FDV> varsChildren = new ArrayList<FDV>();
					ArrayList<Constraint> localConstraints = new ArrayList<Constraint>();

					p = fm.getStore().getFDV(parentName);

					int nChildrenSet = getSetCardinality(e);
					FD domain = new FD(1, nChildrenSet);
					
					//=============================================================
					
					parentNode = this.featureNodes.get(parentName);

					graph = graph + "subgraph cluster_" + name + " {\n";
					String graphCon = new String();

					for (int i = 0; i < nChildrenSet; i++) {
						name++;
						childName = "F" + name;

						//============================ Jacop ========================
						
						FDV child = new FDV(fm.getStore(), childName, 0, 1);

						varsChildren.add(child);
						localConstraints.add(new XeqC(child, 0));

						fm.getFeatures().add(child);
						
						//=============================================================

						childNode = new FeatureNode(childName);
						childNode.setRelationType(FeatureNode.GROUPED);
						parentNode.setChild(childNode);
						this.featureNodes.put(childName, childNode);
						group.getFeatures().add(childNode);

						graph = graph + childName + ";";
						graphCon = graphCon + parentName + " -> " + childName
								+ ";";

					}

					graph = graph + "label = \"SET" + nChildrenSet + "\";}"
							+ graphCon;

					group.setCardMin(1);
					group.setCardMax(nChildrenSet);

					parentNode.addGroup(group);
					
					//============================ Jacop ========================
					
					FDV sum = new FDV(fm.getStore(), 0, varsChildren.size());

					fm.getConstraints().add(new Sum(varsChildren, sum));
					fm.getConstraints().add(
							new IfThenElse(new XgtC(p, 0), new In(sum, domain),
									new And(localConstraints)));
					// TODO look if this generalizes!!!
					// modification to make consistency in JaCoP
					fm.getConstraints().add(
							new IfThen(new XeqC(sum, 0), new XeqC(p, 0)));
					
					//=============================================================
					
					for (Iterator iter = varsChildren.iterator(); iter
							.hasNext();) {
						// 
						FDV element = (FDV) iter.next();
						String name = element.id();
						int setParent = Integer.parseInt(name.substring(1));
						generateFeatureModelStore(w, temp, e, setParent);
					}

					break;
				case ALTERNATIVE:
					parentName = "F" + parent;

					group = new FeatureGroup();
					
					//============================ Jacop ========================	
					
					varsChildren = new ArrayList<FDV>();
					localConstraints = new ArrayList<Constraint>();

					p = fm.getStore().getFDV(parentName);

					nChildrenSet = getSetCardinality(e);
					domain = new FD(1, 1);
					
					//=============================================================
					
					parentNode = this.featureNodes.get(parentName);

					graph = graph + "subgraph cluster_" + name + " {\n";
					graphCon = new String();

					for (int i = 0; i < nChildrenSet; i++) {
						name++;
						childName = "F" + name;

						//============================ Jacop ========================
						
						FDV child = new FDV(fm.getStore(), childName, 0, 1);

						varsChildren.add(child);
						localConstraints.add(new XeqC(child, 0));

						fm.getFeatures().add(child);
						
						//=============================================================

						childNode = new FeatureNode(childName);
						childNode.setRelationType(FeatureNode.GROUPED);
						parentNode.setChild(childNode);
						this.featureNodes.put(childName, childNode);
						group.getFeatures().add(childNode);

						graph = graph + childName + ";";
						graphCon = graphCon + parentName + " -> " + childName
								+ ";";

					}

					graph = graph + "label = \"SET" + nChildrenSet + "\";}"
							+ graphCon;

					group.setCardMin(1);
					group.setCardMax(1);

					parentNode.addGroup(group);
					
					//============================ Jacop ========================
					
					sum = new FDV(fm.getStore(), 0, varsChildren.size());

					fm.getConstraints().add(new Sum(varsChildren, sum));
					fm.getConstraints().add(
							new IfThenElse(new XgtC(p, 0), new In(sum, domain),
									new And(localConstraints)));
					// TODO look if this generalizes!!!
					// modification to make consistency in JaCoP
					fm.getConstraints().add(
							new IfThen(new XeqC(sum, 0), new XeqC(p, 0)));
					
					//=============================================================
					
					for (Iterator iter = varsChildren.iterator(); iter
							.hasNext();) {
						// 
						FDV element = (FDV) iter.next();
						String name = element.id();
						int setParent = Integer.parseInt(name.substring(1));
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
		ArrayList<Constraint> dependencies = new ArrayList<Constraint>();

		if (number > 0) {

			while (loops < number) {
				FeatureNode f = featureNodes.get("F"
						+ rand.nextInt(fm.getFeatures().size()));
				FeatureNode g = featureNodes.get("F"
						+ rand.nextInt(fm.getFeatures().size()));

				if (valid(f, g)) {
					if (rand.nextBoolean() == true) {
						
						//============================ Jacop ========================	
						
						Constraint r1 = new IfThen(new XgtC(fm.getStore()
								.getFDV(f.getName()), 0), new XgtC(fm
								.getStore().getFDV(g.getName()), 0));
						// TODO look if this generalizes!!!
						// modification to make consistency in JaCoP
						Constraint r2 = new IfThen(new XeqC(fm.getStore()
								.getFDV(g.getName()), 0), new XeqC(fm
								.getStore().getFDV(f.getName()), 0));
						
						Constraint requires = new And(r1,r2);
						
						dependencies.add(requires);
						
						//=============================================================
						
						graph = graph + "edge [dir=forward] " + f.getName()
								+ " -> " + g.getName()
								+ "[color=\"blue\",label=\"D\"]; ";

						this.fm.getFeatureModel().addDependency(
								new RequiresDependency(f, g));

					} else {
						
						//============================ Jacop ========================
						
						Constraint e1 = new IfThen(new XgtC(fm.getStore()
								.getFDV(f.getName()), 0), new XeqC(fm
								.getStore().getFDV(g.getName()), 0));
						// TODO look if this generalizes!!!
						// modification to make consistency in JaCoP
						Constraint e2 = new IfThen(new XgtC(fm.getStore()
								.getFDV(g.getName()), 0), new XeqC(fm
								.getStore().getFDV(f.getName()), 0));
						
						Constraint excludes = new And(e1,e2);
						
						dependencies.add(excludes);
						
						//=============================================================
						
						graph = graph + "edge [dir=none] " + f.getName()
								+ " -> " + g.getName()
								+ "[color=\"red\",label=\"E\"]; ";
						this.fm.getFeatureModel().addDependency(
								new ExcludesDependency(f, g));
				

					}
					nbDepende++;
					loops++;
				}
				

			}
			
			int dName = 0; 

			for (Iterator iter = dependencies.iterator(); iter.hasNext();) {
				Constraint element = (Constraint) iter.next();
				fm.setDependency(new String(""+dName),element);
				dName++;

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

		if (!this.fm.getFeatureModel().areDirectFamily(f, g)
				&& !this.fm.getFeatureModel().haveRelation(f, g)) {
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

	public JacopFeatureModel getFm() {
		return fm;
	}

}
