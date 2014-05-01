package es.us.isa.fama.experiments.tests;

import org.junit.Before;

import es.us.isa.fama.experiments.generators.DefaultPreferencesSelector;

public class DefaultPreferencesGeneratorTest extends PreferencesGeneratorTest {

	@Before
	public void setUp(){
		DefaultPreferencesSelector aux = new DefaultPreferencesSelector();
		generator = aux;
		loadFM();
		aux.populateDefaultPreferences();
	}
	
}
