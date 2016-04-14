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
package co.icesi.i2t.FAMA.TestSuite2;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Test runner class for running test suites for FAMA extensions.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @version 1.0, September 2014
 */
public class TestRunner {

	/**
	 * Runs the given array of test cases or suites. It displays a message
	 * indicating if all tests were successful or that some tests were not
	 * successful. If there was any failures, it shows the failure messages.
	 * 
	 * @param testClasses
	 *            The test cases or suites to be run.
	 */
	public static void runTests(Class<?>[] testClasses) {
		Result result = JUnitCore.runClasses(testClasses);
		if (result.wasSuccessful()) {
			System.out.println("\nAll tests were successful.");
		} else {
			System.out
					.println("\nSome tests were not successful. See failures below:\n");
			for (Failure failure : result.getFailures()) {
				System.out.println("[FAILURE] " + failure.toString());
			}
		}
	}
}
