package efmtests;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.extended.ValidAttributedProductQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;

public class Efmtest {

	/**
	 * @param args
	 */
	
	
	
	public static void main(String[] args) {
		QuestionTrader qt = new QuestionTrader();
		FAMAAttributedFeatureModel afm= (FAMAAttributedFeatureModel) qt.openFile("./EFM-test-inputs/FMF-test1.afm");
		qt.setVariabilityModel(afm);
//		ValidQuestion vaq=(ValidQuestion) qt.createQuestion("Valid");
		NumberOfProductsQuestion pq=(NumberOfProductsQuestion) qt.createQuestion("#Products");
		
		qt.ask(pq);
		System.out.println(pq.getNumberOfProducts());
//		System.out.println(vaq.isValid());
	}

}
