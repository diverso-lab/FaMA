package fama_consumer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class Activator implements BundleActivator {

	private ServiceReference sr = null;
	
	public void start(BundleContext context) throws Exception {
		String className = QuestionTrader.class.getCanonicalName();
		sr = context.getServiceReference(className);
		QuestionTrader qt = (QuestionTrader) context.getService(sr);
		if (qt != null){
			System.out.println("FaMa load successful");
			consumeFaMa(qt);
		}
	}
	
	private void consumeFaMa(QuestionTrader qt){
		VariabilityModel fm = qt.openFile("test.xml");
		qt.setVariabilityModel(fm);
		
		////////// VALID QUESTION + NUMBER PRODUCTS QUESTION ///////////
		Question aux = qt.createQuestion("Valid");
		ValidQuestion vq = (ValidQuestion) aux;
		qt.ask(vq);
		if (vq.isValid()) {
			Question aux2 = qt.createQuestion("#Products");
			NumberOfProductsQuestion npq = (NumberOfProductsQuestion)aux2;
			qt.ask(npq);
			System.out.println("The number of products is: "
					+ npq.getNumberOfProducts());
		} else {
			System.out.println("Your feature model is not valid");
		}
	}
	
	public void stop(BundleContext context) throws Exception {
		if (sr != null){
			context.ungetService(sr);
		}
	}

}
