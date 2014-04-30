package fmtest;

import java.util.Iterator;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.featureModel.Product;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QuestionTrader qt = new QuestionTrader();
//		qt.setVariabilityModel(qt.openFile("noEFM-test-inputs/miscTestFiles/200-0-8.xml"));
		qt.setVariabilityModel(qt.openFile("MOF.xml"));

//		qt.setCriteriaSelector("selected");--
		ProductsQuestion nop = (ProductsQuestion) qt.createQuestion("Products");
		qt.ask(nop);
//		System.out.println(nop.getNumberOfProducts());
		Iterator<Product> it = nop.getAllProducts().iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}

}
