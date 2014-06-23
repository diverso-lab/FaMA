package es.us.isa.ec2.configs;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.aws.scraper.ec2.EC2ModelValidator;

public class EC2NumberOfConfigsTest {
	
	QuestionTrader qt;
	GenericFeatureModel fm;
	EC2ModelValidator validator;
	
	@Before
	public void setUp() throws Exception {
		qt = new QuestionTrader("single-file");
		FAMAAttributedFeatureModel initialFM = (FAMAAttributedFeatureModel) 
				qt.openFile("./test/EC2WithoutEBS.afm");
		IVariabilityModelTransform t = qt.createTransform("Extended2Basic");
		//transform to basic FM
		fm = (GenericFeatureModel) t.doTransform(initialFM);
		qt.setVariabilityModel(fm);
		 validator = new EC2ModelValidator(
					"./ec2-by-date/2014-6-12/current-pricing.html",
					"./ec2-by-date/2014-6-12/prev-gen-pricing.html",
					"./ec2-by-date/2014-6-12/dedicated-pricing.html",
					"./properties");
		
	}

	@Test
	public void testNumberOfConfigs() {
		NumberOfProductsQuestion q = 
				(NumberOfProductsQuestion) qt.createQuestion("#Products");
		qt.ask(q);
		System.out.println("Number of configurations of the EC2 FM: "+q.getNumberOfProducts());
		int realConfigs = validator.countEC2Configs();
		System.out.println("Real EC2 configs: " +realConfigs);
		int modelConfigs = (int)q.getNumberOfProducts();
		assertEquals(realConfigs, modelConfigs);
	}
	

}
