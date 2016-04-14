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
package co.icesi.i2t.Choco3Reasoner.tests;

import co.icesi.i2t.Choco3Reasoner.tests.simple.Choco3ReasonerSimpleTestSuite;
import co.icesi.i2t.FAMA.TestSuite2.TestRunner;

/**
 * Test runner for testing the Choco 3 Reasoner.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @version 1.0, September 2014
 */
public class Choco3ReasonerTestRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestRunner.runTests(new Class<?>[] { Choco3ReasonerSimpleTestSuite.class });
	}

}
