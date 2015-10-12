package es.us.isa.benchmarking.generators;

import java.util.Iterator;
import java.util.Random;

import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class ExactRandomGenerator implements IRandomVMGenerator {

	public int numberOfFeatures;
	public int numberOfDependencies;

	public float percentageOfOrs = 0.25f;
	public float percentageOfMandatories = 0.25f;
	public float percentageOfOptionals = 0.25f;
	public float percentageOfAlternate = 0.25f;
	protected Random rand;
	protected int featureId;
	protected int relationId;
	private int NoF;
	private int NoD;
	FAMAFeatureModel res = null;

	public ExactRandomGenerator() {

		rand = new Random();
		rand.setSeed(0);
	}

	public void setSeed(int seed) {
		rand.setSeed(seed);
	}

	public VariabilityModel generate(ICharacteristics c) {

		if (c instanceof FMCharacteristics) {
			FMCharacteristics aux = (FMCharacteristics) c;
			numberOfFeatures = 0;
			numberOfDependencies = 0;
			rand = new Random();
			rand.setSeed(0);

			setSeed(aux.getSeed());
			NoF = 0;
			NoD = 0;
			featureId = 0;
			relationId = 0;
			this.numberOfFeatures = aux.getNumberOfFeatures();

			if (aux.getNumberOfDependencies() == -1) {
				numberOfDependencies = Math.round((numberOfFeatures * aux
						.getPercentageOfDependencies()));
			}

			res = generateFM(aux.getChoose(), numberOfDependencies);
			System.out.println("number of Dep is " + NoD
					+ " and the number of Feats is " + NoF);
		}

		return res;

	}

	private FAMAFeatureModel generateFM(int choose, int dependencies) {
		res = new FAMAFeatureModel();
		Feature root = new Feature("root");
		res.setRoot(root);
		generateTree(choose, root);
		generateCroosTreeConstraints(res, dependencies);
		return res;
	}

	private void generateTree(int choose, Feature parent) {
		// if (height >= 1)
		if (NoF < numberOfFeatures) {
			int nChildren = rand.nextInt(choose) + 1;
			for (int i = 0; ((i < nChildren) && (NoF < numberOfFeatures)); i++) {
				int relType = rand.nextInt(4);
				Feature child = null;
				switch (relType) {
				case 0: // mandatory
					child = new Feature(getFeatureId());
					NoF++;
					createCardinality(parent, child, 1, 1);
					generateTree(choose, child);
					break;
				case 1: // optional
					child = new Feature(getFeatureId());
					NoF++;
					createCardinality(parent, child, 0, 1);
					generateTree(choose, child);
					break;
				case 2: // cardinality
					child = new Feature(getFeatureId());
					NoF++;
					createCardinality(parent, child, rand.nextInt(2), rand
							.nextInt(5) + 2); // max cardinality at least 2
					generateTree(choose, child);

					break;
				case 3: // set
					int nChildrenSet = rand.nextInt(choose) + 1;
					if (nChildrenSet <= 0)
						System.out.println("OJO con nChildrenSet!");
					int setCard = rand.nextInt(nChildrenSet) + 1;
					Relation rel = new Relation(getRelationId());
					rel.addCardinality(new Cardinality(1, setCard));
					for (int j = 0; (j < nChildrenSet)
							&& (NoF < this.numberOfFeatures); j++) {
						Feature groupChild = new Feature(getFeatureId());
						NoF++;
						rel.addDestination(groupChild);
						generateTree(choose, groupChild);

					}
					parent.addRelation(rel);
					break;
				default:
					System.out
							.println("Rand(x) genera de 0 a x, no de 0 a x-1!!");
					break;
				}
			}
		}

	}

	protected void createCardinality(Feature parent, Feature child, int i, int j) {
		Relation rel = new Relation(getRelationId());
		rel.addCardinality(new Cardinality(i, j));
		rel.addDestination(child);
		parent.addRelation(rel);
	}

	protected void generateCroosTreeConstraints(FAMAFeatureModel fm,
			int dependencies) {
			if (featureId <= 0)
				System.out.println("OJO con featureId!");
			for (int i = 0; i < dependencies; i++) {
				int f1 = rand.nextInt(featureId) + 1;
				int f2 = rand.nextInt(featureId) + 1;
				Feature f = fm.searchFeatureByName("F" + f1);
				Feature g = fm.searchFeatureByName("F" + f2);
//				if (!areDirectFamily(f, g) && !areDirectFamily(g, f)
//						&& !existsRelation(fm, f, g)) {
					boolean requires = rand.nextBoolean();
					if (requires) {
						fm.addDependency(new RequiresDependency(
								getRelationId(), f, g));
						NoD++;
					} else {
						fm.addDependency(new ExcludesDependency(
								getRelationId(), f, g));
						NoD++;
//					}
				}
			}
		}
	

	private boolean areDirectFamily(Feature f, Feature g) {
		boolean res = false;

		if (f == g) {
			res = true;
		} else {
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

	private boolean existsRelation(FAMAFeatureModel fm, Feature f, Feature g) {
		boolean res = false;
		Iterator<Dependency> itd = fm.getDependencies();

		while (itd.hasNext() && !res) {
			Dependency dep = itd.next();
			if ((dep.getDestination() == f && dep.getOrigin() == g)
					|| dep.getDestination() == g && dep.getOrigin() == f) {
				res = true;
			}
		}

		return res;
	}

	/**
	 * @return
	 * @uml.property name="featureId"
	 */
	protected String getFeatureId() {
		return "F" + String.valueOf(++featureId);
	}

	/**
	 * @return
	 * @uml.property name="relationId"
	 */
	protected String getRelationId() {
		return "R-" + String.valueOf(++relationId);
	}

	public void setPercentageOfOrs(float percentageOfOrs) {
		this.percentageOfOrs = percentageOfOrs;
	}

	public float getPercentageOfMandatories() {
		return percentageOfMandatories;
	}

	public void setPercentageOfMandatories(float percentageOfMandatories) {
		this.percentageOfMandatories = percentageOfMandatories;
	}

	public float getPercentageOfOptionals() {
		return percentageOfOptionals;
	}

	public void setPercentageOfOptionals(float percentageOfOptionals) {
		this.percentageOfOptionals = percentageOfOptionals;
	}

	public float getPercentageOfAlternate() {
		return percentageOfAlternate;
	}

	public void setPercentageOfAlternate(float percentageOfAlternate) {
		this.percentageOfAlternate = percentageOfAlternate;
	}

	public float getPercentageOfOrs() {
		return percentageOfOrs;
	}
}
