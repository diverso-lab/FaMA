package es.us.isa.FAMA.ReasonersTests;

import java.util.Iterator;

import org.junit.Test;



import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.Exceptions.FAMAException;

public class ValidProductTestSuite extends TestSuite {

	@Test
	public void testValidProductQuestion() throws FAMAException{
		Question q = qt.createQuestion("Products");
		qt.ask(q);
		ProductsQuestion pq = (ProductsQuestion) q;
		System.out.println("----VALID PRODUCT QUESTION TEST----");
		long imax = pq.getNumberOfProducts();
		Iterator<Product> it = pq.getAllProducts().iterator();
		int i = 0;
		while (it.hasNext()){
			i++;
			Product p = it.next();
			ValidProductQuestion vpq = (ValidProductQuestion) qt.createQuestion("ValidProduct");
			vpq.setProduct(p);
			qt.ask(vpq);
			System.out.println("PRODUCT "+i+" of " + imax + ". Valid: "+vpq.isValid());
		}
		
		Product p = new Product();
		p.addFeature(this.fm.searchFeatureByName("FIRE"));
		ValidProductQuestion vpq = (ValidProductQuestion) qt.createQuestion("ValidProduct");
		vpq.setProduct(p);
		qt.ask(vpq);
		System.out.println("This product mustn't be valid. Is valid?: "+vpq.isValid());
	}
	
}
