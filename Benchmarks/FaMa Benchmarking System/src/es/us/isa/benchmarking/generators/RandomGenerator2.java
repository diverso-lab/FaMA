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

public class RandomGenerator2 implements IRandomVMGenerator {

	public int NoF = 0;
	public int NoD = 0;
	public boolean definePercent=true;

	private float percentageOfRequieres =0.25f;


	private float percentageOfMandatory = 0.25f;
	private float percentageOfExcludes = 0.25f;
	private float percentageOfOptional = 0.25f;
	

	protected Random rand,rand2;
	/**
	 * @uml.property name="featureId"
	 */
	protected int featureId;
	/**
	 * @uml.property name="relationId"
	 */
	protected int relationId;

	protected int numberOfFeatures = 0;

	public RandomGenerator2() {
		rand = new Random();
		rand2 = new Random();
		rand.setSeed(0);
		rand2.setSeed(0);
	}

	public void setSeed(int seed) {
		rand.setSeed(seed);
	}

	public VariabilityModel generate(ICharacteristics c) {

		// ArrayList<VariabilityModel> res = new ArrayList<VariabilityModel>();
		VariabilityModel res = null;

		if (c instanceof FMCharacteristics) {
			FMCharacteristics aux = (FMCharacteristics) c;
			if (aux.getSeed() != -1)
				setSeed(aux.getSeed());
			numberOfFeatures = aux.getNumberOfFeatures();
			
			if (numberOfFeatures == 0) {
				this.numberOfFeatures = Integer.MAX_VALUE;
			}
			featureId = 0;
			relationId = 0;
			res = generateFM(aux.getWidth(), aux.getHeight(), aux.getChoose(),
					aux.getNumberOfDependencies(), aux
							.getPercentageOfDependencies());
			System.out.println("number of Dep is " + NoD
					+ " and the number of Feats is " + NoF);
		}

		return res;

	}

	private GenericFeatureModel generateFM(int width, int height, int choose,
			int dependencies, float percentDepend) {
		FAMAFeatureModel res = new FAMAFeatureModel();
		Feature root = new Feature("root");
		res.setRoot(root);
		generateTree(width, height, choose, root);
		if (dependencies == -1) {
			dependencies = (int) (percentDepend * numberOfFeatures);
		}
		generateCroosTreeConstraints(res, dependencies);
		return res;
	}

	private void generateTree(int width, int height, int choose, Feature parent) {
		if ((height >= 1) && (NoF < numberOfFeatures)) {
			int nChildren = rand.nextInt(width) + 1;
			for (int i = 0; (i < nChildren) && (NoF < numberOfFeatures); i++) {
				int relType = giveMeRel("int");
				Feature child = null;
				switch (relType) {
				case 0: // mandatory
					child = new Feature(getFeatureId());
					NoF++;
					createCardinality(parent, child, 1, 1);
					generateTree(width, height - 1, choose, child);
					break;
				case 1: // optional
					child = new Feature(getFeatureId());
					NoF++;
					createCardinality(parent, child, 0, 1);
					generateTree(width, height - 1, choose, child);
					break;
				case 2: // cardinality
					child = new Feature(getFeatureId());
					NoF++;
					createCardinality(parent, child, rand.nextInt(2), rand
							.nextInt(5) + 2); // max cardinality at least 2
					generateTree(width, height - 1, choose, child);
					break;
				case 3: // set
					int nChildrenSet = rand.nextInt(choose) + 1;
					if (nChildrenSet <= 0)
						System.out.println("OJO con nChildrenSet!");
					int setCard = rand.nextInt(nChildrenSet) + 1;
					Relation rel = new Relation(getRelationId());
					rel.addCardinality(new Cardinality(1, setCard));
					for (int j = 0; (j < nChildrenSet)
							&& (NoF < numberOfFeatures); j++) {
						Feature groupChild = new Feature(getFeatureId());
						NoF++;
						rel.addDestination(groupChild);
						generateTree(width, height - 1, choose, groupChild);
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

	private int giveMeRel(String str) {
		int res=0;
		
		if(str.equals("int")){
			
			int randTmp=rand.nextInt(100);
			
			if(randTmp<percentageOfMandatory*100){//mandatoy
				res=0;
			}
			if(randTmp>=percentageOfMandatory*100&&randTmp<percentageOfOptional*100){//optional
				res=1;
			}
			if(randTmp>=50&randTmp<75){//cardinality
				res=2;
			}
			if(randTmp>=75&&randTmp<100){//sets
				res=3;
			}

		}
		if(str.equals("boolean")){
			int randTmp=rand2.nextInt(50);
			if(randTmp<percentageOfExcludes*100){//excludes
				res=0;
			}
			if(randTmp>=percentageOfExcludes*100&&randTmp<percentageOfRequieres*100){//requieres
				res=1;
			}
			
		}
		
		return res;
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
			if (!areDirectFamily(f, g) && !areDirectFamily(g, f)
					&& !existsRelation(fm, f, g)) {
				int requires = giveMeRel("boolean");
				if (requires==0) {
					fm.addDependency(new RequiresDependency(getRelationId(), f,
							g));
					NoD++;
				} else {
					fm.addDependency(new ExcludesDependency(getRelationId(), f,
							g));
					NoD++;
				}
			}
		}
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
	public float getPercentageOfRequieres() {
		return percentageOfRequieres;
	}

	public void setPercentageOfRequieres(float percentageOfRequieres) {
		this.percentageOfRequieres = percentageOfRequieres;
	}

	public float getPercentageOfMandatory() {
		return percentageOfMandatory;
	}

	public void setPercentageOfMandatory(float percentageOfMandatory) {
		this.percentageOfMandatory = percentageOfMandatory;
	}

	public float getPercentageOfExcludes() {
		return percentageOfExcludes;
	}

	public void setPercentageOfExcludes(float percentageOfExcludes) {
		this.percentageOfExcludes = percentageOfExcludes;
	}

	public float getPercentageOfOptional() {
		return percentageOfOptional;
	}

	public void setPercentageOfOptional(float percentageOfOptional) {
		this.percentageOfOptional = percentageOfOptional;
	}
	/**
	 * @return
	 * @uml.property name="relationId"
	 */
	protected String getRelationId() {
		return "R-" + String.valueOf(++relationId);
	}

}
