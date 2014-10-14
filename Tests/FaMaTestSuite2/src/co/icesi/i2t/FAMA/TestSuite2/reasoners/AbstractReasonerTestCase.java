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
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

/**
 * Abstract test case for testing FAMA reasoners.
 * 
 * NOTE: All extending classes need to implement the following method:
 * <code>@Parameters public static Collection<?> loadTests()</code>. This method
 * should use the TestLoader class.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @version 1.0, September 2014
 */
@RunWith(Parameterized.class)
public abstract class AbstractReasonerTestCase {

	/**
	 * Question trader.
	 */
	protected QuestionTrader questionTrader;
	/**
	 * Variability model path.
	 */
	protected String variabilityModelPath;
	/**
	 * Variability model.
	 */
	protected VariabilityModel variabilityModel;

	/**
	 * Constructor to support a parameterized test. Every time the JUnit runner
	 * triggers the test case, it will pass a variability model. The set of
	 * variability models is defined in the loadTests() method of particular
	 * test cases.
	 * 
	 * @param variabilityModelPath
	 *            The variability model to be used in the test.
	 */
	public AbstractReasonerTestCase(String variabilityModelPath) {
		super();
		this.variabilityModelPath = variabilityModelPath;
	}

	/**
	 * Sets up the question trader instance needed to perform the tests.
	 * 
	 * @throws Exception
	 *             If any errors occur.
	 */
	@Before
	public void setUp() throws Exception {
		// Initialize the question trader.
		questionTrader = new QuestionTrader();
	}

	/**
	 * Releases all resources used during the test.
	 * 
	 * @throws Exception
	 *             If any error occurs.
	 */
	@After
	public void tearDown() throws Exception {
		variabilityModelPath = null;
		variabilityModel = null;
	}

}
