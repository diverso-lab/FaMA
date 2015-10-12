package main;

import java.util.Iterator;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;

public class ProductsAndValidProductExample {

	public static void main(String[] args){

		QuestionTrader qt = new QuestionTrader();
		GenericFeatureModel fm = (GenericFeatureModel) qt.openFile("fm-samples/HIS.xml");
		qt.setVariabilityModel(fm);
		Question q = qt.createQuestion("Products");
		qt.ask(q);
		ProductsQuestion pq = (ProductsQuestion) q;
		long imax = pq.getNumberOfProducts();
		Iterator<? extends GenericProduct> it = pq.getAllProducts().iterator();
		int i = 0;
		while (it.hasNext()){
			i++;
			Product p = (Product) it.next();
			ValidProductQuestion vpq = (ValidProductQuestion) qt.createQuestion("ValidProduct");
			vpq.setProduct(p);
			qt.ask(vpq);
			System.out.print("PRODUCT "+i+" of " + imax + ".\nFeatures: ");
			Iterator<GenericFeature> it2 = p.getFeatures().iterator();
			while (it2.hasNext()){
				System.out.print(it2.next().getName() + ", ");
			}
			System.out.println("\nValid: "+vpq.isValid());
		}

	}
	
}
