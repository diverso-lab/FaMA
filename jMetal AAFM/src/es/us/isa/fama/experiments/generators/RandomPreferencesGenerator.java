package es.us.isa.fama.experiments.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import es.us.isa.FAMA.models.domain.Domain;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.soup.preferences.AroundPreference;
import es.us.isa.soup.preferences.DislikesPreference;
import es.us.isa.soup.preferences.HighestPreference;
import es.us.isa.soup.preferences.LikesPreference;
import es.us.isa.soup.preferences.LowestPreference;
import es.us.isa.soup.preferences.Preference;

public class RandomPreferencesGenerator extends PreferencesGenerator {

//	private List<GenericAttributedFeature> features;
	private final static int MAX_PREFERENCES_NUMBER = 10;
	
	@Override
	public Collection<Preference> generatePreferences(int n) {
		return this.generatePreferences(new LinkedList<Preference>(), n);
	}
	
	/**
	 * this method considers the existing preferences to add non contradictory
	 * new preferences
	 */
	public Collection<Preference> generatePreferences(Collection<Preference> prefs, int n){
		List<VariabilityElement> currentPrefElems = new ArrayList<VariabilityElement>();
		for (Preference p:prefs){
			VariabilityElement elem = p.getItem();
			if (elem instanceof GenericAttribute){
				currentPrefElems.add(((GenericAttribute) elem).getFeature());
			}
			else{
				currentPrefElems.add(p.getItem());
			}
			
		}
		List<GenericAttributedFeature> availableFeatures = new LinkedList<GenericAttributedFeature>(fm.getAttributedFeatures());
		availableFeatures.removeAll(currentPrefElems);
		
		Collection<Preference> result = generatePreferencesFromFeatures(n, availableFeatures);
		
		return result;
	}
	
	private Collection<Preference> generatePreferencesFromFeatures(int n, List<GenericAttributedFeature> elems){
//		features = new ArrayList<GenericAttributedFeature>(fm.getAttributedFeatures());
		Collection<Preference> result = new ArrayList<Preference>();
		for (int i = 0; i < n; i++){
			VariabilityElement randomElem = obtainRandomVariabilityElement(elems);
			Preference p = generateRandomPreference(randomElem);
			result.add(p);
			//we ensure that we do not create conflictive set of 
			//preferences for the same user
			elems.remove(randomElem);
		}
		return result;
	}
	
	private VariabilityElement obtainRandomVariabilityElement(List<GenericAttributedFeature> elems){
		VariabilityElement result = null;
		
		Random random = new Random(System.nanoTime());
		int index = random.nextInt(elems.size());
		
		GenericAttributedFeature f = elems.get(index);
		Collection<? extends GenericAttribute> atts = f.getAttributes();
		if (atts.isEmpty()){
			result = f;
		}
		else{
			//randomly we decide if we take just the feature or
			//some of its attributes
			random.setSeed(System.nanoTime());
			int index2 = random.nextInt(atts.size()+1);
			if (index2 == atts.size()){
				// we return the feature
				result = f;
			}
			else{
				List<GenericAttribute> auxList = new ArrayList<GenericAttribute>(atts);
				result = auxList.get(index2);
			}
		}

		return result;
	}
	
	private Preference generateRandomPreference(VariabilityElement elem){
		Preference result = null;
		
		Random randomGenerator = new Random(System.nanoTime());
		//1. check the type of the element
		if (elem instanceof GenericFeature){
			// feature
			// 2 possible preferences: Likes or Dislikes
			int value = randomGenerator.nextInt(2);
			if (value == 0){
				//dislikes
				result = new DislikesPreference(elem);
			}
			else{
				//likes
				result = new LikesPreference(elem);
			}
		}
		else if (elem instanceof GenericAttribute){
			// attribute
			// 3 possible preferences: Lowest, Highest or Around
			int value = randomGenerator.nextInt(3);
			if (value == 0){
				//lowest
				result = new LowestPreference(elem);
			}
			else if (value == 1){
				//highest
				result = new HighestPreference(elem);
			}
			else if (value == 2){
				// around
				double aroundValue = generateAroundValue((GenericAttribute)elem);
				result = new AroundPreference(elem, aroundValue);
			}
			
		}
		return result;
	}

	private double generateAroundValue(GenericAttribute elem){
		Domain dom = elem.getDomain();
		//XXX be careful, this call may generate some performance problems
		Set<Integer> values = dom.getAllIntegerValues();
		List<Integer> aux = new ArrayList<Integer>(values);
		Random randomGenerator = new Random(System.nanoTime());
		int index = randomGenerator.nextInt(aux.size());
		Integer aroundValue = aux.get(index);
		return aroundValue.doubleValue();
	}

	public int getMaxNumberOfPreferences(){
		return MAX_PREFERENCES_NUMBER;
	}
	
}
