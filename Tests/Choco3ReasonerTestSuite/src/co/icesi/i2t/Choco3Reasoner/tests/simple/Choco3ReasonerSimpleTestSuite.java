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
package co.icesi.i2t.Choco3Reasoner.tests.simple;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import co.icesi.i2t.Choco3Reasoner.tests.simple.questions.Choco3CoreFeaturesQuestionTestCase;
import co.icesi.i2t.Choco3Reasoner.tests.simple.questions.Choco3DeadFeaturesQuestionTestCase;
import co.icesi.i2t.Choco3Reasoner.tests.simple.questions.Choco3HomogeneityQuestionTestCase;
import co.icesi.i2t.Choco3Reasoner.tests.simple.questions.Choco3NumberOfProductsQuestionTestCase;
import co.icesi.i2t.Choco3Reasoner.tests.simple.questions.Choco3OneProductQuestionTestCase;
import co.icesi.i2t.Choco3Reasoner.tests.simple.questions.Choco3ProductsQuestionTestCase;
import co.icesi.i2t.Choco3Reasoner.tests.simple.questions.Choco3UniqueFeaturesQuestionTestCase;
import co.icesi.i2t.Choco3Reasoner.tests.simple.questions.Choco3ValidProductQuestionTestCase;
import co.icesi.i2t.Choco3Reasoner.tests.simple.questions.Choco3ValidQuestionTestCase;
import co.icesi.i2t.Choco3Reasoner.tests.simple.questions.Choco3VariabilityQuestionTestCase;
import co.icesi.i2t.Choco3Reasoner.tests.simple.questions.Choco3VariantFeaturesQuestionTestCase;

/**
 * Choco 3 Reasoner test suite. This class runs all the test cases related to
 * the Choco 3 Reasoner.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @version 1.0, September 2014
 */
@RunWith(Suite.class)
@SuiteClasses({ Choco3ReasonerReasonerTestCase.class,
		Choco3NumberOfProductsQuestionTestCase.class,
		Choco3CoreFeaturesQuestionTestCase.class,
		Choco3VariantFeaturesQuestionTestCase.class,
		Choco3ProductsQuestionTestCase.class,
		Choco3VariabilityQuestionTestCase.class,
		Choco3ValidQuestionTestCase.class,
		Choco3ValidProductQuestionTestCase.class,
		Choco3UniqueFeaturesQuestionTestCase.class,
		Choco3OneProductQuestionTestCase.class,
		Choco3DeadFeaturesQuestionTestCase.class,
		Choco3HomogeneityQuestionTestCase.class })
public class Choco3ReasonerSimpleTestSuite {

}
