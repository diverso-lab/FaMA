package es.us.isa.main;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;

public class FAMAMain {

	public static void main(String[] args){
		QuestionTrader qt = new QuestionTrader("single-file");
		VariabilityModel vm = qt.openFile("./AmazonEC2Atts.afm");
		IVariabilityModelTransform transform = qt.createTransform("Extended2Basic");
		
		VariabilityModel fm = transform.doTransform(vm);
		qt.setVariabilityModel(fm);
		System.out.println(fm);
		NumberOfProductsQuestion q = (NumberOfProductsQuestion) qt.createQuestion("#Products");
		qt.ask(q);
		System.out.println("Number of configurations: "+q.getNumberOfProducts());
	}
	
}
