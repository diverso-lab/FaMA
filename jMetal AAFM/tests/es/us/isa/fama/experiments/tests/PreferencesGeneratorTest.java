package es.us.isa.fama.experiments.tests;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.fama.experiments.generators.DefaultPreferencesSelector;
import es.us.isa.fama.experiments.generators.PreferencesGenerator;
import es.us.isa.soup.preferences.Preference;

public abstract class PreferencesGeneratorTest {

	protected PreferencesGenerator generator;
//	private final static int MAX_PREFERENCES = 500;
	
	
	protected void loadFM(){
		AttributedReader reader = new AttributedReader();
		FAMAAttributedFeatureModel fm;
		try {
			fm = (FAMAAttributedFeatureModel) reader.parseFile("./inputs/LeroDaaSIntegers.afm");
			generator.setFm(fm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testNumber(){
		int n = generator.generatePreferences(0).size();
		assertTrue(n == 0);
		Random r = new Random(System.nanoTime());
		int aux = r.nextInt(generator.getMaxNumberOfPreferences());
		n = generator.generatePreferences(aux).size();
		assertTrue(n == aux);
		n = generator.generatePreferences(generator.getMaxNumberOfPreferences()).size();
		assertTrue(n == generator.getMaxNumberOfPreferences());
	}
	
	@Test
	public void testDifferences(){
		//XXX a set doesn't contain duplicated elements
		//so we add the preferences to a set to ensure
		//that they are different
		Set<Preference> preferences = new HashSet<Preference>();
		preferences.addAll(generator.generatePreferences(2));
		assertTrue(preferences.size() == 2);
		
		Random r = new Random(System.nanoTime());
		preferences = new HashSet<Preference>();
		int aux = r.nextInt(generator.getMaxNumberOfPreferences());
		preferences.addAll(generator.generatePreferences(aux));
		assertTrue(preferences.size() == aux);
		
		preferences = new HashSet<Preference>();
		preferences.addAll(generator.generatePreferences(generator.getMaxNumberOfPreferences()));
		assertTrue(preferences.size() == generator.getMaxNumberOfPreferences());
	}
	
	@Test
	public void testNull(){
		Collection<Preference> prefs = generator.generatePreferences(15);
		for (Preference p:prefs){
			assertTrue(p.getItem()!=null); 
		}
	}

}
