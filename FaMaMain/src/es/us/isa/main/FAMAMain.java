package es.us.isa.main;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;

public class FAMAMain {

	public static void main(String[] args){
		QuestionTrader qt = new QuestionTrader();
		VariabilityModel vm = qt.openFile("./AmazonEC2Atts.afm");
		IVariabilityModelTransform transform = qt.createTransform("Extended2Basic");
		IVariabilityModelTransform t2 = qt.createTransform("AtomicSet");
		
		VariabilityModel fm = transform.doTransform(vm);
		System.out.println(fm);
	}
	
}
