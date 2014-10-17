/**
 *  This file is part of FaMaTS.
 *
 *  FaMaTS is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FaMaTS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.icesi.i2t.Choco3Reasoner.tests.simple.questions;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner;
import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3VariantFeaturesQuestion;
import co.icesi.i2t.FAMA.TestSuite2.TestLoader;
import co.icesi.i2t.FAMA.TestSuite2.reasoners.AbstractReasonerQuestionTestCase;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.GenericFeature;

/**
 * Test case for the Variant Features question in the Choco 3 Reasoner.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @version 1.0, October 2014
 */
public class Choco3VariantFeaturesQuestionTestCase extends
		AbstractReasonerQuestionTestCase {

	/**
	 * Test configuration file path
	 */
	private static final String TEST_CONFIG_FILE = "test-resources/Choco3TestConfig.xml";

	/**
	 * Question name
	 */
	private static final String QUESTION = "Variant";
	
	public Choco3VariantFeaturesQuestionTestCase(String variabilityModelPath,
			String input, String expectedOutput) {
		super(variabilityModelPath, input, expectedOutput);
	}

	/**
	 * Loads the tests for the number of products question. Tests are specified
	 * in an XML file with the information of the variability models to test and
	 * the questions to be asked with their expected output.
	 * 
	 * @return A collection of 3-tuples (feature model, input, expected output).
	 * @throws FileNotFoundException
	 *             If the test configuration file is not found.
	 * @throws Exception
	 *             If any other errors occur.
	 */
	@Parameters
	public static Collection<?> loadTests() throws FileNotFoundException,
			Exception {
		return Arrays.asList(TestLoader.loadQuestionTests(TEST_CONFIG_FILE, QUESTION));
	}
	
	/**
	 * Test method for {@link co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3VariantFeaturesQuestion#getVariantFeats()}.
	 */
	@Test
	public void testGetVariantFeats() {
System.out.println("\n[TEST] Variant Features");
		
		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);
		questionTrader.setVariabilityModel(variabilityModel);
		System.out.println("For model: \"" + variabilityModelPath + "\"");
		
		Choco3VariantFeaturesQuestion choco3VariantFeaturesQuestion = (Choco3VariantFeaturesQuestion) questionTrader.createQuestion(QUESTION);
		
		if (choco3VariantFeaturesQuestion != null) {
			questionTrader.ask(choco3VariantFeaturesQuestion);
			
			Choco3Reasoner choco3Reasoner = null;
			for (String reasonerID : questionTrader.getAvaliableReasoners(QUESTION)) {
				Reasoner reasoner = questionTrader.getReasonerById(reasonerID);
				if (reasoner instanceof Choco3Reasoner) {
					choco3Reasoner = (Choco3Reasoner) reasoner;
				}
			}
			
			if (choco3Reasoner != null) {
				try {
					Comparator<GenericFeature> comparator = new Comparator<GenericFeature>() {
					    public int compare(GenericFeature c1, GenericFeature c2) {
					        return c2.getName().compareTo(c1.getName());
					    }
					};
					
					String[] expectedOutputs = expectedOutput.split(";");
					ArrayList<GenericFeature> expectedVariantFeatures = new ArrayList<GenericFeature>();
					for (int i = 0; i < expectedOutputs.length; i++) {
						Feature expectedVariantFeature = new Feature(expectedOutputs[i]);
						expectedVariantFeatures.add(expectedVariantFeature);
					}
					Collections.sort(expectedVariantFeatures, comparator);
					
					ArrayList<GenericFeature> output = (ArrayList<GenericFeature>) choco3VariantFeaturesQuestion.getVariantFeats();
					Collections.sort(output, comparator);
					
					System.out.println("Expected variant features: " + expectedVariantFeatures);
					System.out.println("Obtained variant features: " + output);
					
					assertEquals(expectedVariantFeatures, output);
					System.out.println("[INFO] Test case passed");
				} catch (AssertionError e) {
					System.out.println("[INFO] Test case failed");
					throw e;
				}
			} else {
				fail("Available reasoner is not supported with Choco 3");
				System.out.println("[INFO] Available reasoner is not supported with Choco 3");
			}
		} else {
			fail("Current reasoner does not accept this operation.");
			System.out.println("[INFO] Current reasoner does not accept this operation.");
		}
	}

}
