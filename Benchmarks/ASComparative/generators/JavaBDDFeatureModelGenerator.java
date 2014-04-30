package generators;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

import featureModel.ExcludesDependency;
import featureModel.FeatureGroup;
import featureModel.FeatureNode;
import featureModel.RequiresDependency;
import featureModelRepresentations.*;

import JaCoP.Constraint;



import net.sf.javabdd.BDD;

public class JavaBDDFeatureModelGenerator implements IFeatureModelGenerator {

	private static int experimentNumber = 0; // number to identify any new  experiment

	private int name; // feature Names to be generated

	private JavaBDDFeatureModel fm;

	private Map<String, FeatureNode> featureNodes;

	private Random rand;

	private long seed;
	
	// BDD 
	private BDDFactory B;
	private int varnum; // Number of variables

	public JavaBDDFeatureModelGenerator() {
		this.name = 0;
		this.fm = new JavaBDDFeatureModel();

		// =========== BDD ============
		//B=JFactory.init(1000000,10000);		// JFactory
		
/*		// Set table size to 1MB (1 node=20 bytes)
		B=JFactory.init(52429,10000);
		
		// Set max increase to 1 MB
		B.setMaxIncrease(52429);
		
		// 99% of table must be full to increase the table size
		B.setMinFreeNodes(0.01);*/
		
		
		// Set table size to 100 KB (1 node = 20 bytes)
		B=JFactory.init(5120,10000);
		
		// Set max increase to 100 KB
		B.setMaxIncrease(5120);
		
		// 99% of table must be full to increase the table size
		B.setMinFreeNodes(0.01);
		
		this.fm.setFactory(B);
		// ============================
		
		this.rand = new Random();
		this.rand.setSeed(3);

		this.featureNodes = new HashMap<String, FeatureNode>();
		Date now = new Date();

		experimentNumber++;
		this.fm.setName(now.getDate() + "_" + now.getMonth() + "-"
				+ now.getHours() + "_" + now.getMinutes() + "_"
				+ now.getSeconds() + "_" + experimentNumber);
		
		//============================ BDD ============================
		
		B.setVarNum(1);
		varnum=1;
		
		BDD bdd_root=B.ithVar(0);
		this.fm.addVariable("F0",bdd_root);
		
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
	
					//============================ BDD ============================
					this.varnum++;
					B.setVarNum(varnum);
					
					// Get parent feature
					BDD bdd_parent=this.fm.getVariable(parentName);

					// Create child
					BDD bdd_child=B.ithVar(varnum-1);
					
					// Save child
					this.fm.addVariable(childName,bdd_child);
					
					//Relation
					BDD bdd_mandatory=bdd_parent.apply(bdd_child,BDDFactory.biimp);
					this.fm.addNode(bdd_mandatory);
					
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
					
					//============================= BDD ===========================
					this.varnum++;
					B.setVarNum(varnum);
					
					// Get parent feature
					bdd_parent=this.fm.getVariable(parentName);
					
					// Create child
					bdd_child=B.ithVar(varnum-1);
					
					// Save child
					this.fm.addVariable(childName,bdd_child);
					
					BDD bdd_optional=bdd_child.apply(bdd_parent,BDDFactory.imp);
					this.fm.addNode(bdd_optional);
					
					
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
					
					//============================== BDD =========================
					ArrayList<BDD> childrens=new ArrayList<BDD>();
					// ===========================================================
					
					ArrayList<String> names=new ArrayList<String>(); // For after iteration
					int nChildrenSet = getSetCardinality(e);
					parentNode = this.featureNodes.get(parentName);

					for (int i = 0; i < nChildrenSet; i++) {
						name++;
						childName = "F" + name;
						
						//=========================== BDD =============================
						this.varnum++;
						B.setVarNum(varnum);
												
						// Create child
						bdd_child=B.ithVar(varnum-1);
						
						// Save var
						this.fm.addVariable(childName,bdd_child);
						
						// Save children for use it after bucle
						childrens.add(bdd_child);
						
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
					
					//========================== BDD ==============================
					
					// (children1 or children2 or ...)
					
					BDD or_childrens=B.zero();
					
					Iterator it=childrens.iterator();
					while (it.hasNext())
						or_childrens=or_childrens.apply((BDD)it.next(),BDDFactory.or);
										
					// parent <-> (children1 or children2 or ...)
					
					// Get parent
					bdd_parent=this.fm.getVariable(parentName);
					
					// Relation
					BDD bdd_or_relation=bdd_parent.apply(or_childrens,BDDFactory.biimp);
					this.fm.addNode(bdd_or_relation);
					
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
					
					//========================== BDD ==============================
					childrens=new ArrayList<BDD>();
					//=============================================================
					
					names=new ArrayList<String>();		// For iteration
					nChildrenSet = getSetCardinality(e);
					parentNode = this.featureNodes.get(parentName);


					for (int i = 0; i < nChildrenSet; i++) {
						name++;
						childName = "F" + name;
						
						//=========================== BDD =============================
						this.varnum++;
						B.setVarNum(varnum);
												
						// Create child
						bdd_child=B.ithVar(varnum-1);
						
						// Save var
						this.fm.addVariable(childName,bdd_child);
						
						// Save children for use it after bucle
						childrens.add(bdd_child);
						
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

					
					//========================== BDD ==============================
					
					// Its possible optimize this using B.one(), but now it works!! so...
					
					BDD bdd_alternative=null;
					bdd_parent=this.fm.getVariable(parentName);

					for (int i=0;i<childrens.size();i++)
					{
						BDD tmp_and=null;
						boolean first=true;
						for (int k=0;k<childrens.size();k++)
						{
							if (i!=k)
							{
								BDD children=childrens.get(k);
								BDD notChildren=children.not();
								if (first)
								{
									tmp_and=notChildren;
									first=false;
								}
								else
									tmp_and.andWith(notChildren);
							}	
						}
						
						// Not children1 and not children2 and not childrenN and parent
						BDD tmp_alternative=tmp_and.apply(bdd_parent,BDDFactory.and);     
						
						if (bdd_alternative==null)
							bdd_alternative=childrens.get(i).apply(tmp_alternative,BDDFactory.biimp);
						else
							bdd_alternative.andWith(childrens.get(i).apply(tmp_alternative,BDDFactory.biimp));
					}
					
					// Relation
					this.fm.addNode(bdd_alternative);
					
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
						
						//============================ BDD ============================
						// F requires G =  F->G
						
						// Get features
						BDD bdd_f=this.fm.getVariable(f.getName());
						BDD bdd_g=this.fm.getVariable(g.getName());
						
						// Relation
						BDD bdd_requires=bdd_f.apply(bdd_g,BDDFactory.imp);
						this.fm.addNode(bdd_requires);
						//=============================================================

						this.fm.getFeatureModel().addDependency(
								new RequiresDependency(f, g));
						
						//System.out.println(f.getName() + "==>" + g.getName());

					} else {
		
						//============================ BDD ============================
						
						// F excludes G = No (F and G)
						
						// Get features
						BDD bdd_f=this.fm.getVariable(f.getName());
						BDD bdd_g=this.fm.getVariable(g.getName());
						
						// Relation
						BDD bdd_and=bdd_f.apply(bdd_g,BDDFactory.and);
						BDD bdd_excludes=bdd_and.not();
						this.fm.addNode(bdd_excludes);
						
						//=============================================================

						this.fm.getFeatureModel().addDependency(
								new ExcludesDependency(f, g));
						
						//System.out.println(f.getName() + "<==>" + g.getName());
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

	public JavaBDDFeatureModel getFm() {
		return fm;
	}

}
