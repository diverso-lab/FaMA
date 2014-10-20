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

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3CommonalityQuestion;
import co.icesi.i2t.FAMA.TestSuite2.TestLoader;
import co.icesi.i2t.FAMA.TestSuite2.reasoners.AbstractReasonerQuestionTestCase;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

/**
 * Test case for the Commonality question in the Choco 3 Reasoner.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @version 1.0, October 2014
 */
public class Choco3CommonalityQuestionTestCase extends
		AbstractReasonerQuestionTestCase {

	/**
	 * Test configuration file path
	 */
	private static final String TEST_CONFIG_FILE = "test-resources/Choco3TestConfig.xml";

	/**
	 * Question name
	 */
	private static final String QUESTION = "Commonality";

	/**
	 * Rounding delta needed when comparing two double variables.
	 */
	private static final double DELTA = 0.1;

	public Choco3CommonalityQuestionTestCase(String variabilityModelPath,
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
	 * Test method for {@link co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3CommonalityQuestion#getCommonality()}.
	 * 
	 * @throws Exception When any exception occurs during the execution of the test case.
	 */
	@Test
	public void testGetCommonality() throws Exception {
		System.out.println("\n[TEST] Commonality");
		
		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);
		questionTrader.setVariabilityModel(variabilityModel);
		System.out.println("For model: \"" + variabilityModelPath + "\"");
		
		Choco3CommonalityQuestion choco3CommonalityQuestion = (Choco3CommonalityQuestion) questionTrader.createQuestion(QUESTION);
		
		if (choco3CommonalityQuestion != null) {
			if (!input.equals("")) {
				try {
					String[] inputFeatures = input.split(":");
					Configuration inputConfiguration = new Configuration();
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
					System.out.println("For configuration:\n" + inputConfiguration);
					choco3CommonalityQuestion.setConfiguration(inputConfiguration);
					
					questionTrader.ask(choco3CommonalityQuestion);
					
					double output = choco3CommonalityQuestion.getCommonality();
					
					if (!expectedOutput.equals("")) {
						System.out.println("Expected commonality: " + expectedOutput);
						System.out.println("Obtained commonality: " + output);
						
						assertEquals(Double.parseDouble(expectedOutput), output, DELTA);
						System.out.println("[INFO] Test case passed");
					} else {
						System.out.println("[INFO] No expected output for test case.");
						System.out.println("Obtained commonality: " + output);
						System.out.println("[INFO] Test case ignored");
					}
				} catch (AssertionError e) {
					System.out.println("[INFO] Test case failed. Cause: " + e.getMessage());
					throw e;
				} catch (Exception e) {
					System.out.println("[INFO] Test case failed. Cause: " + e.getMessage());
					// TODO Remove
					e.printStackTrace();
					throw e;
				}
			} else {
				System.out.println("[INFO] No input for test case.");
				System.out.println("[INFO] Test case ignored");
			}
		} else {
			fail("Current reasoner does not accept this operation.");
			System.out.println("[INFO] Current reasoner does not accept this operation.");
		}
	}

}
