package es.us.isa.FAMA.ReasonersTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
//TODO Add more test cases classes if it's necessary
@Suite.SuiteClasses(
		{
			NumberOfProductsTestSuite.class,
			ProductsTestSuite.class,
			ValidTestSuite.class,
			CommonalityTestSuite.class,
			VariabilityTestSuite.class,
			FilterTestSuite.class,
			DetectErrorsTestSuite.class,
			ValidProductTestSuite.class,
			ValidConfigurationErrorTestSuite.class
		}
)


public class AllTestSuitesJaCoP {
	//empty class
}
