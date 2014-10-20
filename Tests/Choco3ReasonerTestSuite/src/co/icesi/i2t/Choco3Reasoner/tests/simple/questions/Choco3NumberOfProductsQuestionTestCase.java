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

import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3NumberOfProductsQuestion;
import co.icesi.i2t.FAMA.TestSuite2.reasoners.AbstractReasonerQuestionTestCase;
import co.icesi.i2t.FAMA.TestSuite2.TestLoader;

/**
 * Test case for the Number of products question in the Choco 3 Reasoner.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @version 1.0, September 2014
 */
public class Choco3NumberOfProductsQuestionTestCase extends
		AbstractReasonerQuestionTestCase {

	/**
	 * Test configuration file path
	 */
	private static final String TEST_CONFIG_FILE = "test-resources/Choco3TestConfig.xml";

	/**
	 * Question name
	 */
	private static final String QUESTION = "#Products";
	
	/**
	 * Rounding delta needed when comparing two double variables.
	 */
	private static final double DELTA = 0.1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * co.icesi.i2t.FAMA.TestSuite2.reasoners#AbstractReasonerQuestionTestCase(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	public Choco3NumberOfProductsQuestionTestCase(String variabilityModel, String input,
			String expectedOutput) {
		super(variabilityModel, input, expectedOutput);
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
	 * Test method for
	 * {@link co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3NumberOfProductsQuestion#getNumberOfProducts()}
	 * .
	 */
	@Test
	public void testGetNumberOfProducts() {
		System.out.println("\n[TEST] Number of Products");
		
		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);
		questionTrader.setVariabilityModel(variabilityModel);
		System.out.println("For model: \"" + variabilityModelPath + "\"");
		
		Choco3NumberOfProductsQuestion choco3NumberOfProductsQuestion = (Choco3NumberOfProductsQuestion) questionTrader.createQuestion(QUESTION);
		
		if (choco3NumberOfProductsQuestion != null) {
			try {
				questionTrader.ask(choco3NumberOfProductsQuestion);
				
				double output = choco3NumberOfProductsQuestion.getNumberOfProducts();
				
				if (!expectedOutput.equals("")) {
					System.out.println("Expected number of products: " + expectedOutput);
					System.out.println("Obtained number of products: " + output);
					
					assertEquals(Double.parseDouble(expectedOutput), output, DELTA);
					System.out.println("[INFO] Test case passed");
				} else {
					System.out.println("[INFO] No expected output for test case.");
					System.out.println("Obtained number of products: " + output);
					System.out.println("[INFO] Test case ignored");
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
