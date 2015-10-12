package es.us.isa.FAMA.ReasonersTests;

import java.util.Iterator;

import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.Exceptions.FAMAException;

/**
 * This jUnit test case tests the Products question for anyone reasoner
 * @author Jes�s
 *
 */
public class ProductsTestSuite extends TestSuite {

	@Test
	public void testProductsQuestion() throws FAMAException{
		Question q = qt.createQuestion("Products");
		@SuppressWarnings("unused")
		PerformanceResult pr = qt.ask(q);
		ProductsQuestion pq = (ProductsQuestion) q;
		System.out.println("----PRODUCTS QUESTION TEST----");
		//long imax = pq.getNumberOfProducts();
		Iterator<Product> it = pq.getAllProducts().iterator();
		int i = 0;
		while (it.hasNext()){
			i++;
			Product p = it.next();
			System.out.print("PRODUCT "+i+": ");
			Iterator<GenericFeature> itFeats = p.getFeatures().iterator();
			while (itFeats.hasNext()){
				GenericFeature f = itFeats.next();
				System.out.print(f.getName() + ", ");
			}
//			int jmax = p.getNumberOfFeatures();
//			for (int j = 0; j < jmax; j++){
//				System.out.print(p.getFeature(j).getName() + ", ");
//			}
			System.out.println("");
		}
		
	}
	
}
