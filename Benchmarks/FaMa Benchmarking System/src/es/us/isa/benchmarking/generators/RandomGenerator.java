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

public class RandomGenerator implements IRandomVMGenerator{
	
	protected Random rand;
	/**
	 * @uml.property  name="featureId"
	 */
	protected int featureId;
	/**
	 * @uml.property  name="relationId"
	 */
	protected int relationId;
	
	protected int totalFeatures;
	
	public RandomGenerator() {
		totalFeatures = 0;
		rand = new Random();
		rand.setSeed(0);
	}
	
	public void setSeed(int seed) {
		rand.setSeed(seed);
	}
	
	public VariabilityModel generate (ICharacteristics c) {
		
		//ArrayList<VariabilityModel> res = new ArrayList<VariabilityModel>();
		VariabilityModel res = null;
		
		if (c instanceof FMCharacteristics){
			FMCharacteristics aux = (FMCharacteristics)c;
			if (aux.getSeed() != -1)
				setSeed(aux.getSeed());
			
			featureId = 0;
			relationId = 0;
			res = generateFM(aux.getWidth(), aux.getHeight(), 
					aux.getChoose(), aux.getNumberOfDependencies(), 
					aux.getPercentageOfDependencies());
		}
		
		return res;
		
	}

	private GenericFeatureModel generateFM(int width, int height, int choose, int dependencies, 
									float percentDepend) {
		FAMAFeatureModel res = new FAMAFeatureModel();
		Feature root = new Feature("root");
		res.setRoot(root);
		generateTree(width,height,choose,root);
		if (dependencies == -1){
			dependencies = (int) (percentDepend*totalFeatures);
		}
		generateCroosTreeConstraints(res,dependencies);
		return res;
	}

	private void generateTree(int width, int height, int choose, Feature parent) {
		if (height >= 1) {
			int nChildren = rand.nextInt(width) + 1;
			totalFeatures+=nChildren;
			for (int i = 0; i < nChildren;i++) {
				int relType = rand.nextInt(4);
				Feature child = null;
				switch (relType) {
				case 0: // mandatory
					child = new Feature(getFeatureId());
					createCardinality(parent,child,1,1);
					generateTree(width,height-1,choose,child);
					break;
				case 1:	// optional
					child = new Feature(getFeatureId());
					createCardinality(parent,child,0,1);
					generateTree(width,height-1,choose,child);
					break;
				case 2: // cardinality
					child = new Feature(getFeatureId());
					createCardinality(parent,child,rand.nextInt(2),rand.nextInt(5)+2); // max cardinality at least 2
					generateTree(width,height-1,choose,child);
					break;
				case 3: // set
					int nChildrenSet = rand.nextInt(choose) + 1;
					if (nChildrenSet <= 0)
						System.out.println("OJO con nChildrenSet!");
					int setCard = rand.nextInt(nChildrenSet) + 1;
					Relation rel = new Relation(getRelationId());
					rel.addCardinality(new Cardinality(1,setCard));
					for (int j = 0; j < nChildrenSet; j++) {					
						Feature groupChild = new Feature(getFeatureId());
						rel.addDestination(groupChild);
						generateTree(width,height-1,choose,groupChild);
					}
					parent.addRelation(rel);
					break;
				default:
					System.out.println("Rand(x) genera de 0 a x, no de 0 a x-1!!");
					break;
				}
			}
		}
	}

	protected void createCardinality(Feature parent, Feature child, int i, int j) {
		Relation rel = new Relation(getRelationId());
		rel.addCardinality(new Cardinality(i,j));
		rel.addDestination(child);
		parent.addRelation(rel);
	}

	protected void generateCroosTreeConstraints(FAMAFeatureModel fm, int dependencies) {
		if (featureId <= 0)
			System.out.println("OJO con featureId!");
		for (int i = 0; i < dependencies; i++) {
			int f1 = rand.nextInt(featureId)+1;
			int f2 = rand.nextInt(featureId)+1;
			Feature f = fm.searchFeatureByName("F"+f1);
			Feature g = fm.searchFeatureByName("F"+f2);
			if (!areDirectFamily(f,g) && !areDirectFamily(g,f) && !existsRelation(fm,f,g)) {
				boolean requires = rand.nextBoolean();
				if (requires) {
					fm.addDependency(new RequiresDependency(getRelationId(),f,g));
				} else {
					fm.addDependency(new ExcludesDependency(getRelationId(),f,g));				
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
					res = res | areDirectFamily(itf.next(),g);
				}
			}
		}
		
		return res;
	}

	private boolean existsRelation(FAMAFeatureModel fm,Feature f, Feature g) {
		boolean res = false;
		Iterator<Dependency> itd = fm.getDependencies();
		
		while (itd.hasNext() && !res) {
			Dependency dep = itd.next();
			if ( (dep.getDestination() == f && dep.getOrigin() == g) ||
				  dep.getDestination() == g && dep.getOrigin() == f) {
				res = true;
			}
		}
		
		return res;
	}

	/**
	 * @return
	 * @uml.property  name="featureId"
	 */
	protected String getFeatureId() {
		return "F" + String.valueOf(++featureId);
	}
	
	/**
	 * @return
	 * @uml.property  name="relationId"
	 */
	protected String getRelationId() {
		return "R-" + String.valueOf(++relationId);
	}

}
