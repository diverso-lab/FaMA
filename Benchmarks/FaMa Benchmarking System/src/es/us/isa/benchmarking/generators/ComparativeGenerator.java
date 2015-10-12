package es.us.isa.benchmarking.generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class ComparativeGenerator implements IRandomVMGenerator {
	public static final int RELATIONS = 4;
	public static final int MANDATORY = 0;
	public static final int OPTIONAL = 1;
	public static final int OR = 2;
	public static final int ALTERNATIVE = 3;

	private static int experimentNumber = 0; // number to identify any new
												// experiment

	private int name; // feature Names to be generated

	private FAMAFeatureModel fm;

	private Map<String, Feature> featureNodes;

	private Random rand;

	private long seed;

	@Override
	public VariabilityModel generate(ICharacteristics c) {
		FMCharacteristics fmChar=(FMCharacteristics) c;
		
		this.generateFeatureModelStore(fmChar.getWidth(), fmChar.getHeight(), fmChar.getChoose(), 0);
		
		this.generateDependsExludes(fmChar.getNumberOfDependencies());
		this.seed=fmChar.getSeed();
		return fm;
	}

	public ComparativeGenerator() {
		this.name = 0;
		this.fm = new FAMAFeatureModel();
		this.rand = new Random();
		this.rand.setSeed(3);
		this.featureNodes = new HashMap<String, Feature>();
		experimentNumber++;

		Feature root = new Feature(new String("F0"));

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

					Feature parentNode = this.featureNodes.get(parentName);
					Feature childNode = new Feature(childName);
					Relation rel = new Relation();
					rel.addDestination(childNode);
					rel.addCardinality(new Cardinality(1, 1));
					parentNode.addRelation(rel);
					this.featureNodes.put(childName, childNode);

					generateFeatureModelStore(w, temp, e, name);
					break;
				case OPTIONAL:
					name++;
					parentName = "F" + parent;
					childName = "F" + name;
					parentNode = this.featureNodes.get(parentName);
					childNode = new Feature(childName);
					Relation rel2 = new Relation();
					rel2.addDestination(childNode);
					rel2.addCardinality(new Cardinality(0, 1));
					parentNode.addRelation(rel2);
					this.featureNodes.put(childName, childNode);

					generateFeatureModelStore(w, temp, e, name);
					break;

				case OR:
					parentName = "F" + parent;
					Relation rel3 = new Relation();

					ArrayList<String> names = new ArrayList<String>(); // For
																		// after
																		// iteration
					int nChildrenSet = getSetCardinality(e);
					parentNode = this.featureNodes.get(parentName);

					for (int i = 0; i < nChildrenSet; i++) {
						name++;
						childName = "F" + name;

						// Store the name for recursion
						names.add(childName);

						childNode = new Feature(childName);
						this.featureNodes.put(childName, childNode);
						rel3.addDestination(childNode);
					}
					rel3.addCardinality(new Cardinality(1, nChildrenSet));

					parentNode.addRelation(rel3);

					for (Iterator<String> iter = names.iterator(); iter.hasNext();) {

						String element = (String) iter.next();
						int setParent = Integer.parseInt(element.substring(1));
						generateFeatureModelStore(w, temp, e, setParent);

					}

					break;

				case ALTERNATIVE:
					parentName = "F" + parent;
					names = new ArrayList<String>(); // For iteration
					nChildrenSet = getSetCardinality(e);
					parentNode = this.featureNodes.get(parentName);
					Relation rel4 = new Relation();
					for (int i = 0; i < nChildrenSet; i++) {
						name++;
						childName = "F" + name;
						// Store the name for recursion
						names.add(childName);
						childNode = new Feature(childName);
						this.featureNodes.put(childName, childNode);
						rel4.addDestination(childNode);
					}
					rel4.addCardinality(new Cardinality(1, 1));
					parentNode.addRelation(rel4);
					for (Iterator<String> iter = names.iterator(); iter.hasNext();) {

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
				Feature f = featureNodes.get("F"
						+ rand.nextInt(featureNodes.size()));
				Feature g = featureNodes.get("F"
						+ rand.nextInt(featureNodes.size()));

				if (valid(f, g)) {
					if (rand.nextBoolean() == true) {
						this.fm.addDependency(new RequiresDependency(f, g));
						// System.out.println(f.getName() + "==>" +
						// g.getName());
					} else {
						this.fm.addDependency(new ExcludesDependency(f, g));
						// System.out.println(f.getName() + "<==>" +
						// g.getName());
					}
					nbDepende++;
					loops++;
				}
			}
		}

		return nbDepende;
	}

	private boolean valid(Feature f, Feature g) {
		boolean res = false;
		// valid:
		// - not parent(f,g) && not parent(g,f) && not are the same (are not
		// direct family)
		// - not already related
		// 

		if (!this.areDirectFamily(f, g) ) {
			res = true;
		}

		return res;

	}
	private boolean areDirectFamily(Feature f, Feature g) {
		boolean res = false;

		if (f == g)
			res = true;
		else {
			Iterator<Relation> itr = f.getRelations();
			while (itr.hasNext()) {
				Iterator<Feature> itf = itr.next().getDestination();
				while (itf.hasNext() && !res) {
					res = res | areDirectFamily(itf.next(), g);
				}
			}
		}

		return res;
	}
	/*
	private String getRelation(ArrayList<String> no) {
		int feature = 0;
		boolean found = true;
		do {
			feature = rand.nextInt(name);
			found = no.contains(new String("F" + feature));
		} while (found);

		return new String("F" + feature);
	}
	 */
	private int getSetCardinality(int max) {
		int res = 0;
		do {
			// res = new Random().nextInt(max + 1);
			res = rand.nextInt(max + 1);

		} while (res <= 1);
		return res;
	}
/*
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
*/
}