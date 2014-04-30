package es.us.isa.FAMA.ReasonersTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
//TODO Add more test cases classes if it's necessary
@Suite.SuiteClasses(
		{
			CommonalityTestSuite.class,
			VariabilityTestSuite.class,
			DetectErrorsTestSuite.class,
			ValidProductTestSuite.class,
			ValidConfigurationErrorTestSuite.class
		}
)


public class AllTestsSuiteDefault {

}
