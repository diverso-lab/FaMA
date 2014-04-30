package es.us.isa.FAMA.Benchmarking;

import java.util.ArrayList;
import java.util.Collection;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;


public class FixedFeaturesRandomGenerator extends RandomGenerator {

	/**
	 * @author   Dani
	 */
	private class FeatureHeightPair {
		Feature f;
		int height;
		public FeatureHeightPair(Feature f,int height) {
			this.f = f;
			this.height = height;
		}
		public String toString() {
			return "[" + f.getName() + "," + height + "]";
		}
	}
	
	public FixedFeaturesRandomGenerator() {
		super();
	}

	/* (non-Javadoc)
	 * @see tdg.SPL.Benchmarking.RandomGenerator#generate(tdg.SPL.Benchmarking.Characteristics)
	 */
	@Override
	public Collection<VariabilityModel> generate(Characteristics c) {
		Collection<VariabilityModel> res;
		if (c.getNumberOfFeatures() > 0) {
			res = new ArrayList<VariabilityModel>();
			if (c.getSeed() != -1)
				this.setSeed(c.getSeed());
			for (int i = 0; i < c.getNumberOfInstances(); i++) {
				featureId = 0;
				relationId = 0;
				VariabilityModel fm = generateFM(c.getWidth(),c.getHeight(),c.getChoose(),c.getDependencies(),c.getNumberOfFeatures());
				// method above return null when has not been able to set all the features. It uses no backtracks
				if (fm != null) {
					res.add(fm);
					System.out.println("Instancia " + i);
				} else {
					i--;
				}
			}
		}
		else {
			res = super.generate(c);
		}
		return res;
	}

	private VariabilityModel generateFM(int width, int height,int choose, int dependencies, int features) {
		FAMAFeatureModel res = new FAMAFeatureModel();
		Feature root = new Feature("root");
		res.setRoot(root);
		
		// this stack will store the current leaves of the feature model
		// so every loop it will take one of them randomly and expand it with
		// the characteristics attached.
		ArrayList<FeatureHeightPair> fStack = new ArrayList<FeatureHeightPair>();
		fStack.add(new FeatureHeightPair(root,0));
		while (features >0) {
			if (fStack.size() == 0)
				return null;
			int pos = rand.nextInt(fStack.size());
			FeatureHeightPair pair = fStack.remove(pos);
			Feature parent = pair.f;
			int nChildren = Math.min(rand.nextInt(width)+1,features);
			for (int i = 0; i < nChildren && features > 0; i++) {
				int relType = rand.nextInt(4);
				Feature child = null;
				switch (relType) {
				case 0: // mandatory
					child = new Feature(getFeatureId());
					createCardinality(parent,child,1,1);
					if ((pair.height+1) < height)
						fStack.add(new FeatureHeightPair(child,pair.height+1));
					break;
				case 1:	// optional
					child = new Feature(getFeatureId());
					createCardinality(parent,child,0,1);
					if ((pair.height+1) < height)
						fStack.add(new FeatureHeightPair(child,pair.height+1));
					break;
				case 2: // cardinality
					child = new Feature(getFeatureId());
					createCardinality(parent,child,rand.nextInt(2),rand.nextInt(5)+2); // max cardinality at least 2
					if ((pair.height+1) < height)
						fStack.add(new FeatureHeightPair(child,pair.height+1));
					break;
				case 3: // set
					int nChildrenSet = Math.min(rand.nextInt(choose) + 1,features);
					int setCard = rand.nextInt(nChildrenSet) + 1;
					Relation rel = new Relation(getRelationId());
					rel.addCardinality(new Cardinality(1,setCard));
					for (int j = 0; j < nChildrenSet; j++) {					
						Feature groupChild = new Feature(getFeatureId());
						rel.addDestination(groupChild);
						if ((pair.height+1) < height)
							fStack.add(new FeatureHeightPair(groupChild,pair.height+1));
					}
					parent.addRelation(rel);
					features -= (nChildrenSet - 1); // some lines below one more is substracted
					break;
				default:
					System.out.println("Rand(x) genera de 0 a x, no de 0 a x-1!!");
					break;
				}
				features--;
			}
		}
		
		generateCroosTreeConstraints(res,dependencies);
		return res;
	}
}
