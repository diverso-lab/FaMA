package generators;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

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

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

public class CNFFeatureModelGenerator implements IFeatureModelGenerator {

	private static int experimentNumber = 0; // number to identify any new  experiment

	private int name; // feature Names to be generated

	private CNFFeatureModel fm;
	
	private int varnum;

	private Map<String, FeatureNode> featureNodes;

	private Random rand;

	private long seed;
	
	public CNFFeatureModelGenerator() {
		this.name = 0;
		this.fm = new CNFFeatureModel();
		
		this.rand = new Random();
		this.rand.setSeed(3);

		this.featureNodes = new HashMap<String, FeatureNode>();
		Date now = new Date();

		experimentNumber++;
		this.fm.setName(now.getDate() + "_" + now.getMonth() + "-"
				+ now.getHours() + "_" + now.getMinutes() + "_"
				+ now.getSeconds() + "_" + experimentNumber);
		
		//============================ CNF ============================
		
		this.fm.addVariable("F0","1");
		this.fm.addClause("1 0");
		this.varnum=1;
		
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
	
					//============================ CNF ============================
					
					// Increment the number of variables
					this.varnum++;
					
					//Get parent feature
					String cnf_parent=this.fm.getVariable(parentName);
					
					// Create child
					String cnf_child=Integer.toString(varnum);
					
					// Save child
					this.fm.addVariable(childName,cnf_child);
					
					// Clauses
					String cnf_mandatory1="-" + cnf_parent + " " + cnf_child + " 0";
					String cnf_mandatory2="-" + cnf_child + " " + cnf_parent + " 0";
					this.fm.addClause(cnf_mandatory1);
					this.fm.addClause(cnf_mandatory2);
									
					//=============================================================
					
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
					
					//============================= CNF ===========================
					
					// Increment the number of variables
					this.varnum++;
					
					//Get parent feature
					cnf_parent=this.fm.getVariable(parentName);
					
					// Create child
					cnf_child=Integer.toString(varnum);
					
					// Save child
					this.fm.addVariable(childName,cnf_child);
					
					// Clause
					String cnf_optional="-" + cnf_child + " " + cnf_parent + " 0";
					this.fm.addClause(cnf_optional);
					
					//=============================================================
					
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
					
					//============================== CNF =========================
					ArrayList<String> childrens=new ArrayList<String>();
					// ===========================================================
					
					ArrayList<String> names=new ArrayList<String>(); // For after iteration
					int nChildrenSet = getSetCardinality(e);
					parentNode = this.featureNodes.get(parentName);

					for (int i = 0; i < nChildrenSet; i++) {
						name++;
						childName = "F" + name;
						
						//=========================== BDD =============================
						
						this.varnum++;
						
						// Create child
						cnf_child=Integer.toString(this.varnum);
						
						// Save child
						this.fm.addVariable(childName,cnf_child);
												
						// Save children for use it after bucle
						childrens.add(cnf_child);
						
						//=============================================================
						
						//Store the name for recursion
						names.add(childName);

						childNode = new FeatureNode(childName);
						childNode.setRelationType(FeatureNode.GROUPED);
						parentNode.setChild(childNode);
						this.featureNodes.put(childName, childNode);
						group.getFeatures().add(childNode);

					}

					group.setCardMin(1);
					group.setCardMax(nChildrenSet);

					parentNode.addGroup(group);
					
					//========================== CNF ==============================
					
					cnf_parent=this.fm.getVariable(parentName);
					
					// (no parent or child1 or child2 ...or childn)
					
					String cnf_or="-" + cnf_parent + " "; 
					Iterator it=childrens.iterator();
					while (it.hasNext())
						cnf_or +=(String)it.next() + " ";
					
					cnf_or += "0";
					this.fm.addClause(cnf_or);
					
					
					// (no child1 or parent) and (no child2 or parent) and ... (no childn or parent) 
					
					it=childrens.iterator();
					while (it.hasNext())
					{
						cnf_or="-" + (String)it.next() + " " + cnf_parent +  " 0";
						this.fm.addClause(cnf_or);
					}
					
					//=============================================================
					
					for (Iterator iter = names.iterator(); iter.hasNext();) {
						
						String element = (String) iter.next();
						int setParent = Integer.parseInt(element.substring(1));
						generateFeatureModelStore(w, temp, e, setParent);

					} 

					break;
					
				case ALTERNATIVE:
					parentName = "F" + parent;
					group = new FeatureGroup();
					
					//========================== CNF ==============================
					childrens=new ArrayList<String>();
					//=============================================================
					
					names=new ArrayList<String>();		// For iteration
					nChildrenSet = getSetCardinality(e);
					parentNode = this.featureNodes.get(parentName);


					for (int i = 0; i < nChildrenSet; i++) {
						name++;
						childName = "F" + name;
						
						//=========================== CNF =============================
						this.varnum++;
						
						// Create child
						cnf_child=Integer.toString(this.varnum);
						
						// Save child
						this.fm.addVariable(childName,cnf_child);
												
						// Save children for use it after bucle
						childrens.add(cnf_child);
						
						//=============================================================
						
						//Store the name for recursion
						names.add(childName);

						childNode = new FeatureNode(childName);
						childNode.setRelationType(FeatureNode.GROUPED);
						parentNode.setChild(childNode);
						this.featureNodes.put(childName, childNode);
						group.getFeatures().add(childNode);

					}

					group.setCardMin(1);
					group.setCardMax(1);

					parentNode.addGroup(group);

					
					//========================== CNF ==============================
					
					cnf_parent=this.fm.getVariable(parentName);
					
					
					// (children1 or children2 or ... or childrenN or no parent)
					
					String cnf_alternative="-" + cnf_parent + " ";
					
					it=childrens.iterator();
					while (it.hasNext())
						cnf_alternative +=(String)it.next() + " ";
					
					cnf_alternative += "0";
					this.fm.addClause(cnf_alternative);
					
					
					// (no child1 or no child2) and ... (no child1 or no childN) and (no child1 or parent) and
					//...and (no childN or no childN-1) and (no childN or parent)
					
					for (int i=0;i<childrens.size();i++)
					{
						for (int k=0;k<childrens.size();k++)
						{
							if (i!=k)
							{
								cnf_alternative="-" + (String)childrens.get(i) + " -" + (String)childrens.get(k) + " 0";
								this.fm.addClause(cnf_alternative);
							}
						}
						
						cnf_alternative="-" + (String)childrens.get(i) + " " + cnf_parent + " 0";
						this.fm.addClause(cnf_alternative);
					}
					
					
					//=============================================================
					
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
		ArrayList<Constraint> dependencies = new ArrayList<Constraint>();

		if (number > 0) {

			while (loops < number) {
				FeatureNode f = featureNodes.get("F"
						+ rand.nextInt(featureNodes.size()));
				FeatureNode g = featureNodes.get("F"
						+ rand.nextInt(featureNodes.size()));

				if (valid(f, g)) {
					if (rand.nextBoolean() == true) {
						
						//============================ CNF ============================
						// F requires G =  F->G
						
						// Get features
						String cnf_f=this.fm.getVariable(f.getName());
						String cnf_g=this.fm.getVariable(g.getName());
						
						// Clause
						String cnf_requires="-" + cnf_f + " " + cnf_g + " 0";
						this.fm.addClause(cnf_requires);
						
						//=============================================================

						this.fm.getFeatureModel().addDependency(
								new RequiresDependency(f, g));

					} else {
		
						//============================ BDD ============================
						
						// F excludes G = No (F and G)
						
						// Get features
						String cnf_f=this.fm.getVariable(f.getName());
						String cnf_g=this.fm.getVariable(g.getName());
						
						// Clause
						String cnf_excludes="-" + cnf_f + " -" + cnf_g + " 0";
						this.fm.addClause(cnf_excludes);
						
						//=============================================================

						this.fm.getFeatureModel().addDependency(
								new ExcludesDependency(f, g));
						
					}
					nbDepende++;
					loops++;
				}
			
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

	public CNFFeatureModel getFm() {
		return fm;
	}

}
