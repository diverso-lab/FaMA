package es.us.isa.FAMA.ReasonersTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
//also work for choco
@RunWith(Suite.class)
@Suite.SuiteClasses(
		{
			NumberOfProductsTestSuite.class,
			ProductsTestSuite.class,
			CommonalityTestSuite.class,
			ValidTestSuite.class,
			FilterTestSuite.class,
			ValidProductTestSuite.class,
			ValidConfigurationErrorTestSuite.class
		}
)

public class AllTestSuitesSat4j {
	//empty class
}
