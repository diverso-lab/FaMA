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
package co.icesi.i2t.FAMA.TestSuite2.reasoners;

import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Abstract test case for testing FAMA reasoner questions.
 * 
 * NOTE: All extending classes need to implement the following method:
 * <code>@Parameters public static Collection<?> loadTests()</code>. This method
 * should use the TestLoader class.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @version 1.0, September 2014
 */
@RunWith(Parameterized.class)
public abstract class AbstractReasonerQuestionTestCase extends
		AbstractReasonerTestCase {

	/**
	 * Input required by the question.
	 */
	protected String input;
	/**
	 * Expected output of the question for the variability model.
	 */
	protected String expectedOutput;

	/**
	 * Constructor to support a parameterized test. Every time the JUnit runner
	 * triggers the test case, it will pass a variability model, required inputs
	 * and the expected output of the question for the given variability model.
	 * The set of variability models, inputs and expected outputs is defined in
	 * the loadTests() method of particular test cases.
	 * 
	 * @param variabilityModelPath
	 *            The variability model to be used in the test.
	 * @param input
	 *            The input required by the question.
	 * @param expectedOutput
	 *            The expected output of the question for the given variability
	 *            model and input.
	 */
	public AbstractReasonerQuestionTestCase(String variabilityModelPath,
			String input, String expectedOutput) {
		super(variabilityModelPath);
		this.input = input;
		this.expectedOutput = expectedOutput;
	}

	/**
	 * Releases all resources used during the test.
	 * 
	 * @throws Exception
	 *             If any error occurs.
	 */
	@After
	public void tearDown() throws Exception {
		input = null;
		expectedOutput = null;
	}
}
