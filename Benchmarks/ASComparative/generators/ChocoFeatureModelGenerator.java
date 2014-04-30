
package generators;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import featureModel.ExcludesDependency;
import featureModel.FeatureGroup;
import featureModel.FeatureNode;
import featureModel.RequiresDependency;
import featureModelRepresentations.*;

import choco.*;
import choco.integer.*;

public class ChocoFeatureModelGenerator implements IFeatureModelGenerator {

	private static Map<String, IntVar> vars = new HashMap<String, IntVar>();

	private static int experimentNumber = 0; // number to identify any new experiment

	private int name; // feature Names to be generated

	private ChocoFeatureModel fm;

	private Map<String, FeatureNode> featureNodes;

	private Random rand;

	private long seed;

	public ChocoFeatureModelGenerator() {
		this.name = 0;
		this.fm = new ChocoFeatureModel();

		this.rand = new Random();
		this.rand.setSeed(3);

		this.featureNodes = new HashMap<String, FeatureNode>();
		
		Date now = new Date();
		experimentNumber++;
		this.fm.setName(now.getDate() + "_" + now.getMonth() + "-"
				+ now.getHours() + "_" + now.getMinutes() + "_"
				+ now.getSeconds() + "_" + experimentNumber);

		
		// ============================ Choco ========================
	
		IntVar VarRoot =this.fm.getProblem().makeBoundIntVar("F0", 0, 1);
		
		vars.put("F0", VarRoot);
		
		//Add root feature to variables
		this.fm.getFeatures().add(VarRoot);
		
		//Add a new constraint: VarRoot is root.
		this.fm.getConstraints().add(this.fm.getProblem().eq(VarRoot, 1));
		
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

		IntVar c=null,p=null;
		String parentName="",childName="";
		
		
		if (h > 0) {
			int nChildren = rand.nextInt(w + 1);
			for (int j = 0; j < nChildren; j++) {
				int temp = h - 1;
				int relationType = rand.nextInt(RELATIONS);

				switch (relationType) {
				case MANDATORY:
					name++;
					parentName = "F" + parent;
					childName = "F" + name;

					//============================ Choco ========================
						
					// Get the parent feature
					p=vars.get(parentName);
					
					// Create the new feature
					c=this.fm.getProblem().makeBoundIntVar(childName,0,1);
						
					// Save (name,var)
					vars.put(childName,c);
					
					// Add the feature to fm
					this.fm.getFeatures().add(c);
					
					// Add the constraint (mandatory)
					this.fm.getConstraints().add(this.fm.getProblem().eq(c,p));

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

					
					//============================ Choco ========================
					
					// Get the parent feature
					p=vars.get(parentName);
					
					// Create the new feature
					c=this.fm.getProblem().makeBoundIntVar(childName,0,1);
					
					// Save (name,var)
					vars.put(childName,c);
					
					// Add the feature
					this.fm.getFeatures().add(c);
					
					// Add the constraint 
					this.fm.getConstraints().add(this.fm.getProblem().ifThen(
							this.fm.getProblem().eq(p, 0), this.fm.getProblem().eq(c, 0)));

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
					
					//============================ Choco ========================	
					
					ArrayList<IntVar> varsChildren = new ArrayList<IntVar>();
					ArrayList<Constraint> localConstraints = new ArrayList<Constraint>();

					// Get the parent
					p = vars.get(parentName);

					//=============================================================
					
					ArrayList<String> names=new ArrayList<String>(); // For after iteration
					int nChildrenSet = getSetCardinality(e);
					parentNode = this.featureNodes.get(parentName);

					for (int i = 0; i < nChildrenSet; i++) {
						name++;
						childName = "F" + name;

						//============================ Choco ========================

						c=this.fm.getProblem().makeBoundIntVar(childName,0,1);
						vars.put(childName,c);
						
						varsChildren.add(c);
						this.fm.getFeatures().add(c);
						localConstraints.add(this.fm.getProblem().eq(c, 0));
						
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
					
					//============================ Choco ========================
										
					Constraint [] elementsNO = (Constraint[]) localConstraints.toArray(new Constraint[localConstraints.size()]);
					Constraint no = this.fm.getProblem().ifThen(this.fm.getProblem().eq(p, 0), this.fm.getProblem().and(elementsNO));

					IntExp sum = this.fm.getProblem().sum((IntVar[]) varsChildren.toArray(new IntVar[varsChildren.size()]));
					
					
					Constraint minCard = this.fm.getProblem().ifThen(this.fm.getProblem().gt(p, 0), this.fm.getProblem().geq(sum, 1));
					Constraint maxCard = this.fm.getProblem().ifThen(this.fm.getProblem().gt(p, 0), this.fm.getProblem().leq(sum,nChildrenSet));

					//Constraint and = this.fm.getProblem().and((Constraint[]) localConstraints.toArray(new Constraint[localConstraints.size()]));
					//Constraint no = pB.ifThen(pB.eq(vars.get(parent), 0), and);
					
					// Modification to make consistency in Choco
					Constraint consistency =this.fm.getProblem().ifThen(this.fm.getProblem().eq(sum, 0), this.fm.getProblem().eq(p, 0));
					
					this.fm.getConstraints().add(minCard);
					this.fm.getConstraints().add(maxCard);
					this.fm.getConstraints().add(no);
					this.fm.getConstraints().add(consistency);
					
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
					
					//============================ Choco ========================		
					varsChildren = new ArrayList<IntVar>();
					localConstraints = new ArrayList<Constraint>();

					// Get the parent
					p = vars.get(parentName);
				
					//=============================================================
					
					names=new ArrayList<String>();		// For iteration
					nChildrenSet = getSetCardinality(e);
					parentNode = this.featureNodes.get(parentName);

					for (int i = 0; i < nChildrenSet; i++) {
						name++;
						childName = "F" + name;

						//============================ Choco ========================
						
						c=this.fm.getProblem().makeBoundIntVar(childName,0,1);
						vars.put(childName,c);
						
						varsChildren.add(c);
						this.fm.getFeatures().add(c);
						localConstraints.add(this.fm.getProblem().eq(c, 0));
						
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
					
					//============================ Choco ========================
										
					elementsNO = (Constraint[]) localConstraints.toArray(new Constraint[localConstraints.size()]);
					no = this.fm.getProblem().ifThen(this.fm.getProblem().eq(p, 0), this.fm.getProblem().and(elementsNO));

					sum = this.fm.getProblem().sum((IntVar[]) varsChildren.toArray(new IntVar[varsChildren.size()]));
					
					
					minCard = this.fm.getProblem().ifThen(this.fm.getProblem().gt(p, 0), this.fm.getProblem().geq(sum, 1));
					maxCard = this.fm.getProblem().ifThen(this.fm.getProblem().gt(p, 0), this.fm.getProblem().leq(sum,1));

					//Constraint and = this.fm.getProblem().and((Constraint[]) localConstraints.toArray(new Constraint[localConstraints.size()]));
					//Constraint no = pB.ifThen(pB.eq(vars.get(parent), 0), and);
					
					// Modification to make consistency in Choco
					consistency =this.fm.getProblem().ifThen(this.fm.getProblem().eq(sum, 0), this.fm.getProblem().eq(p, 0));
					
					this.fm.getConstraints().add(minCard);
					this.fm.getConstraints().add(maxCard);
					this.fm.getConstraints().add(no);
					this.fm.getConstraints().add(consistency);
					
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
				FeatureNode frand = featureNodes.get("F"
						+ rand.nextInt(fm.getFeatures().size()));
				FeatureNode grand = featureNodes.get("F"
						+ rand.nextInt(fm.getFeatures().size()));
				
				IntVar f=vars.get(frand.getName());
				IntVar g=vars.get(grand.getName());
				

				if (valid(frand, grand)) {
					if (rand.nextBoolean() == true) {
						
						//============================ Choco ========================	
						
						Constraint r1=this.fm.getProblem().ifThen(this.fm.getProblem().gt(f, 0), this.fm.getProblem().gt(g, 0));
						dependencies.add(r1);
						
						// Modification to make consistency in Choco
						Constraint r2=this.fm.getProblem().ifThen(this.fm.getProblem().eq(g, 0), this.fm.getProblem().eq(f, 0));
						dependencies.add(r2);

						//=============================================================

						this.fm.getFeatureModel().addDependency(
								new RequiresDependency(frand, grand));

					} else {
						
						//============================ Choco ========================
						
						// OJO: Si se emplea la operación AND se eleva una excepción
						
						Constraint e1 = this.fm.getProblem().ifThen(this.fm.getProblem().gt(f, 0), this.fm.getProblem().eq(g, 0));
						dependencies.add(e1);
						
						// Modification to make consistency in Choco
						Constraint e2 = this.fm.getProblem().ifThen(this.fm.getProblem().gt(g, 0), this.fm.getProblem().eq(f, 0));
						dependencies.add(e2);

						//=============================================================

						this.fm.getFeatureModel().addDependency(
								new ExcludesDependency(frand, grand));
						
					}
					nbDepende++;
				}
				loops++;
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

	public ChocoFeatureModel getFm() {
		return fm;
	}
}
