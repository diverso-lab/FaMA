package es.us.isa.generator.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.generator.config.enums.ConfigValidity;
import es.us.isa.generator.config.enums.FeatureState;
import es.us.isa.generator.config.validation.ConfigAnalyzer;


// TODO refactor this class when implementation is finished

/**
 * This class generates, given a FM, a set of configurations of such FM.
 * 
 * @author jesus
 *
 */
public class ConfigGenerator {
	
	protected ConfigCharacteristics chars;
	
	protected GenericFeatureModel fm;
	
	protected ConfigAnalyzer ana;
	
	
	public ConfigGenerator(){
		resetConfigGenerator();
	}
	
	private void resetConfigGenerator() {
		chars = null;
		fm = null;
		ana = null;
	}

	public ConfigAnalyzer getAnalyzer() {
		return ana;
	}

	public void setAnalyzer(ConfigAnalyzer ana) {
		this.ana = ana;
	}

	public Collection<Configuration> generate(GenericFeatureModel fm, ConfigCharacteristics chars, int n){
		// TODO
		this.chars = chars;
		this.fm = fm;
		Collection<Configuration> res = generateConfigurations(n);
		return res;
	}
	
	
	
	private Collection<Configuration> generateConfigurations(int n){
		// TODO
		Collection<Configuration> res = new LinkedList<Configuration>();
		if (chars.getValid() == ConfigValidity.VALID){
			//we need an AAFM tool??
			if (chars.isFull()){
				
			}
			else{
				
			}
		}
		else if (chars.getValid() == ConfigValidity.INVALID){
			
		}
		else if (chars.getValid() == ConfigValidity.TREE_VALID){
			//we use Sergio's algorithm
		}
		else if (chars.getValid() == ConfigValidity.TREE_INVALID){
			//we use Sergio's algorithm
		}
		else if (chars.getValid() == ConfigValidity.CTC_VALID){
			
		}
		else if (chars.getValid() == ConfigValidity.CTC_INVALID){
			//look for CTCs, propagate and configure properly. 
			//if the FM has not dead features should be enough
		}
		else if (chars.getValid() == ConfigValidity.RANDOM){
			//totally random
			int max = chars.getFeatureConfig().getMax(), min = chars.getFeatureConfig().getMin(); 
			int normalizedDomain = max - min;
			Random r = new Random();
			
			for (int i = 0; i<n; i++){
				int auxRandom = r.nextInt(normalizedDomain);
				int randomInt = auxRandom + min;
				Configuration conf = getRandomConfig(randomInt, chars.featureConfig.getFeasibleStates());
				res.add(conf);
			}
			
		}

		
		return res;
	}
	
	/**
	 * This method returns a random feature of the current FM
	 * @return
	 */
	private GenericFeature getRandomFeature(){
		// XXX be careful with the performance of this method. i'm not sure
		// if it is the best way to obtain a random feature
		// check the performance
		Collection<? extends GenericFeature> feats = fm.getFeatures();
		int size = feats.size();
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(size);
		List<? extends GenericFeature> list = new ArrayList<GenericFeature>(feats);
		GenericFeature f = list.get(index);
		return f;
	}
	
	/**
	 * This method returns a product of the current FM where the feature f is selected.
	 * @param f
	 * @return
	 */
	private Configuration getSingleProduct(GenericFeature f){
		Configuration c = new Configuration();
		c.addElement(f, 1);
		Configuration result = ana.oneProduct(c);
		return result;
	}
	
	
	// HOW CAN I REMOVE LEAF FEATURES TO OBTAIN A PARTIAL CONFIGURATION?
	// SHOULD IT BE AN ANALYSIS OPERATION?	
	
	/**
	 * This method removes decisions in given subtrees to turn a product into a 
	 * partial configuration
	 * 
	 * @param product base product for this partial configuration
	 * @param criterion how to modify the product
	 * @return
	 */
	private Configuration makeItPartial(Configuration product, Map<GenericFeature,Integer> criterion){
		Map<VariabilityElement, Integer> elements = product.getElements();
		
	}
	
	private Configuration walkFM(GenericFeature feature, Configuration product){
		fm.get
//		Integer val = product.getElements().get(feature);
//		if (val != null){
//			
//		}
	}
	
	/**
	 * This method returns a pseudo-random configuration for the current FM. 
	 * 
	 * @param n configuration size in term of features
	 * @param states a list with the allowed states to configure features (SELECTED, REMOVED)
	 * @return
	 */
	private Configuration getRandomConfig(int n, List<FeatureState> states){
		// TODO Test me!!
		
		Configuration result = new Configuration();
		List<GenericFeature> features = new ArrayList<GenericFeature>(fm.getFeatures());
		int size = features.size();
		int confSize;
		if (n >= size){
			confSize = size;
		}
		else{
			confSize = n;
		}
		
		int lowerBound = size - confSize;
		
		int[] available_states = new int[states.size()];
		int i = 0;
		for (FeatureState st:states){
			if (st == FeatureState.SELECTED){
				available_states[i] = 1;
			}
			else if (st == FeatureState.REMOVED){
				available_states[i] = 0;
			}
			i++;
		}
		
		
		for (i = size; i > lowerBound; i--){
			Random random = new Random();
			int index = random.nextInt(i);
			GenericFeature f = features.remove(index);
			index = random.nextInt(available_states.length);
			int value = available_states[index];
			result.addElement(f, value);
		}
		return result;
		
	}
	
	
	
	
}
