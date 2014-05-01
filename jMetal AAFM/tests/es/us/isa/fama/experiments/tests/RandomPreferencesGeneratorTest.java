package es.us.isa.fama.experiments.tests;

import org.junit.Before;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.fama.experiments.generators.RandomPreferencesGenerator;

public class RandomPreferencesGeneratorTest extends PreferencesGeneratorTest {

	@Before
	public void setUp(){
		generator = new RandomPreferencesGenerator();
		loadFM();
	}
	
}
