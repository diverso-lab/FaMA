package main;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class FAMAFirstTime {

	public static void main(String[] args){
		
		//The main class is instantiated 
		QuestionTrader qt = new QuestionTrader();
		
		//A feature model is loaded 
		VariabilityModel fm = qt.openFile("fm-samples/test.fama");
		qt.setVariabilityModel(fm);
		
		////////// VALID QUESTION + NUMBER PRODUCTS QUESTION ///////////
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		qt.ask(vq);
		if (vq.isValid()) {
			NumberOfProductsQuestion npq = (NumberOfProductsQuestion) qt
					.createQuestion("#Products");
			qt.ask(npq);
			System.out.println("The number of products is: "
					+ npq.getNumberOfProducts());
		} else {
			System.out.println("Your feature model is not valid");
		}



	}

}
