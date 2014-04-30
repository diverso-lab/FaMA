package es.us.isa.FAMA.ReasonersTests;

import java.util.Iterator;

import org.junit.Test;


import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.Exceptions.FAMAException;

public class ValidConfigurationTestSuite extends TestSuite {

	@Test
	public void testValidConfigurationQuestion() throws FAMAException{
		Question q = qt.createQuestion("Products");
		qt.ask(q);
		ProductsQuestion pq = (ProductsQuestion) q;
		System.out.println("----VALID CONFIGURATION QUESTION TEST----");
		long imax = pq.getNumberOfProducts();
		Iterator<Product> it = pq.getAllProducts().iterator();
		int i = 0;
		while (it.hasNext()){
			i++;
			Product p = it.next();
			ValidConfigurationQuestion vpq = (ValidConfigurationQuestion) qt.createQuestion("ValidConfiguration");
			vpq.setProduct(p);
			qt.ask(vpq);
			System.out.println("PRODUCT "+i+" of " + imax + ". Valid: "+vpq.isValid());
		}
		
		Product p = new Product();
		p.addFeature(new Feature("NOTHING"));
		ValidConfigurationQuestion vpq = (ValidConfigurationQuestion) qt.createQuestion("ValidConfiguration");
		vpq.setProduct(p);
		qt.ask(vpq);
		System.out.println("This product mustn't be valid. Is valid?: "+vpq.isValid());
	}
	
}
