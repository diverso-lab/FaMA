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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3ValidConfigurationQuestion;
import co.icesi.i2t.FAMA.TestSuite2.TestLoader;
import co.icesi.i2t.FAMA.TestSuite2.reasoners.AbstractReasonerQuestionTestCase;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

/**
 * Test case for the Valid Configuration question in the Choco 3 Reasoner.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @version 1.0, October 2014
 */
public class Choco3ValidConfigurationQuestionTestCase extends
		AbstractReasonerQuestionTestCase {

	/**
	 * Test configuration file path
	 */
	private static final String TEST_CONFIG_FILE = "test-resources/Choco3TestConfig.xml";

	/**
	 * Question name
	 */
	private static final String QUESTION = "ValidConfiguration";

	public Choco3ValidConfigurationQuestionTestCase(
			String variabilityModelPath, String input, String expectedOutput) {
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
	 * Test method for {@link co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3ValidConfigurationQuestion#isValid()}.
	 * @throws Exception When any exception occurs during the execution of the test case.
	 */
	@Test
	public void testIsValid() throws Exception {
		System.out.println("\n[TEST] Valid Configuration");
		
		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);
		questionTrader.setVariabilityModel(variabilityModel);
		System.out.println("For model: \"" + variabilityModelPath + "\"");
		
		// Create the question instance to be tested.
		Choco3ValidConfigurationQuestion choco3ValidConfigurationQuestion = (Choco3ValidConfigurationQuestion) questionTrader.createQuestion(QUESTION);
		
		if (choco3ValidConfigurationQuestion != null) {
			try {
				Configuration inputConfiguration = new Configuration();
				if (!input.equals("")) {
					String[] inputFeatures = input.split(":");
					for (int i = 0; i < inputFeatures.length; i++) {
						String[] inputFeatureConfiguration = inputFeatures[i].split("=");
						if (inputFeatureConfiguration.length == 2) {
							Feature feature = new Feature(inputFeatureConfiguration[0]);
							Integer featureValue = new Integer(Integer.parseInt(inputFeatureConfiguration[1]));
							inputConfiguration.addElement(feature, featureValue);
						} else {
							throw new Exception("[INFO] Input string is not a configuration.");
						}
					}
				} else {
					FAMAFeatureModel featureModel = (FAMAFeatureModel) variabilityModel;
					if (featureModel.getFeaturesNumber() > 0) {
						Random random = new Random();
						int randomFeatureIndex = random.nextInt(featureModel.getFeaturesNumber());
						
						Iterator<Feature> featuresIterator = featureModel.getFeatures().iterator();
						
						for (int i = 0; featuresIterator.hasNext() && i <= randomFeatureIndex; i++) {
							Feature feature = (Feature) featuresIterator.next();
							inputConfiguration.addElement(feature, 1);
						}
					} else {
						fail("The model has no features.");
						System.out.println("The model has no features.");
					}
				}
				
				System.out.println("For configuration:\n" + inputConfiguration);
				choco3ValidConfigurationQuestion.setConfiguration(inputConfiguration);
				
				// Ask the question.
				questionTrader.ask(choco3ValidConfigurationQuestion);
				
				// Retrieve the result.
				boolean output = choco3ValidConfigurationQuestion.isValid();
				
				if (!expectedOutput.equals("")) {
					System.out.println("Expected is valid configuration: " + expectedOutput);
					System.out.println("Obtained is valid configuration: " + output);
					
					// Compare the result against an expected output value.
					assertEquals(Boolean.parseBoolean(expectedOutput), output);
					System.out.println("[INFO] Test case passed");
				} else {
					System.out.println("[INFO] No expected output for test case.");
					System.out.println("Obtained is valid configuration: " + output);
					System.out.println("[INFO] Test case passed");
				}
			} catch (AssertionError e) {
				System.out.println("[INFO] Test case failed. Cause: " + e.getMessage());
				throw e;
			} catch (Exception e) {
				System.out.println("[INFO] Test case failed. Cause: " + e.getMessage());
				throw e;
			}
		} else {
			fail("Current reasoner does not accept this operation.");
			System.out.println("[INFO] Current reasoner does not accept this operation.");
		}
	}

}
