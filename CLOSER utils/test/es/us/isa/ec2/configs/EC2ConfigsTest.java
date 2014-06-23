package es.us.isa.ec2.configs;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationSetQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFMUtils;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;
import es.us.isa.FAMA.parser.FMFParser;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.aws.scraper.ec2.EC2ModelValidator;
import es.us.isa.util.Node;
import es.us.isa.util.Tree;

public class EC2ConfigsTest {

	QuestionTrader qt;
	GenericFeatureModel fm;
	Collection<Configuration> configs;
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
				"./ec2-by-date/2014-6-18/current-pricing.html",
				"./ec2-by-date/2014-6-18/prev-gen-pricing.html",
				"./ec2-by-date/2014-6-18/dedicated-pricing.html",
				"./properties");
		validator.countEC2Configs();
		configs = validator.getConfigurations();
	}
	
	
	@Test
	public void testConfigListing(){
		ProductsQuestion q = (ProductsQuestion) qt.createQuestion("Products");
		System.out.println("Analysis started");
		qt.ask(q);
		System.out.println("Analysis finished");
		Collection<Product> auxProducts = (Collection<Product>) q.getAllProducts();
		Collection<Product> fmProducts = FAMAFMUtils.products2SimpleProducts(auxProducts, (FAMAFeatureModel) fm);
		Collection<Product> ec2Products = transform2Products(configs, 
				(FAMAFeatureModel) fm);
		
		System.out.println("Matching started");
		Iterator<Product> it = fmProducts.iterator();
		while (it.hasNext()){
			Product p = it.next();
			boolean removed = ec2Products.remove(p);
			if (removed){
				it.remove();
			}
		}
		System.out.println("Matching finished");
		
		try {
			PrintWriter writer = new PrintWriter("looseConfigs.csv");
			int i = 0;
			writer.write("FM Products\n\n");
			for (Product c:fmProducts){
				i++;
				writer.write(i+";"+c+";\n");
//				System.out.println(i+": "+c);
			}
			
			writer.write("EC2 Products\n\n");
			for (Product c:ec2Products){
				i++;
				writer.write(i+";"+c+";\n");
//				System.out.println(i+": "+c);
			}
			writer.flush();
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private Collection<Product> transform2Products(Collection<Configuration> col, FAMAFeatureModel fm){
		Collection<Product> result = new ArrayList<Product>();
		for (Configuration c:col){
			Product p = validatorConfig2Product((ExtendedConfiguration) c, fm);
			result.add(p);
		}
		return result;
	}
	
	private Product validatorConfig2Product(ExtendedConfiguration c, FAMAFeatureModel fm){
		Collection<Tree<String>> constraints = c.getAttConfigs();
		//just the first constraint
		Tree<String> t = constraints.iterator().next();
		Node<String> node = t.getRootElement();
		Product result = new Product();
		this.config2Product(node, fm, result);
		return result;
	}
	
	private void config2Product(Node<String> n, FAMAFeatureModel fm, Product p){
		if (!n.getData().equals("AND")){
			Feature f = fm.searchFeatureByName(n.getData());
			p.addFeature(f);
		}
		List<Node<String>> children = n.getChildren();
		for (Node<String> child:children){
			config2Product(child, fm, p);
		}
	}

}
