package es.us.isa.FAMA.TestSuite.junit;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import es.us.isa.FAMA.TestSuite.CommonalityQuestionTestSrc;
import es.us.isa.FAMA.TestSuite.DeadFeaturesQuestionSrc;
import es.us.isa.FAMA.TestSuite.NumberOfProductsQuestionTestSrc;
import es.us.isa.FAMA.TestSuite.ProductsQuestionTestSrc;
import es.us.isa.FAMA.TestSuite.ValidProductQuestionTestSrc;
import es.us.isa.FAMA.TestSuite.ValidQuestionTestSrc;
import es.us.isa.FAMA.TestSuite.VariabilityQuestionTestSrc;

@RunWith(Suite.class)
@Suite.SuiteClasses(
		{
		(DeadFeaturesQuestionSrc.class),
		(ValidQuestionTestSrc.class),
		(NumberOfProductsQuestionTestSrc.class),
		(ValidProductQuestionTestSrc.class),
		(ProductsQuestionTestSrc.class),
		(VariabilityQuestionTestSrc.class),
		(CommonalityQuestionTestSrc.class),
		

})
public class TestSuite extends junit.framework.TestSuite {}