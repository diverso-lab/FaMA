/**
 *  This file is part of FaMaTS.
 *
 *  FaMaTS is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FaMaTS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.icesi.i2t.Choco3.osgi;

//import java.util.Hashtable;
//import java.util.LinkedList;
//import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
//import org.osgi.framework.ServiceRegistration;

//import co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3CommonalityQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3CoreFeaturesQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3DeadFeaturesQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3DetectErrorsQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3ExplainErrorsQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3ExplainInvalidProductQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3HomogeneityQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3NumberOfProductsQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3OneProductQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3ProductsQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3UniqueFeaturesQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3ValidConfigurationErrorsQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3ValidConfigurationQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3ValidProductQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3ValidQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3VariabilityQuestion;
//import co.icesi.i2t.Choco3Reasoner.simple.questions.Choco3VariantFeaturesQuestion;
//import es.us.isa.FAMA.Reasoner.Reasoner;
//import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
//import es.us.isa.FAMA.Reasoner.questions.CoreFeaturesQuestion;
//import es.us.isa.FAMA.Reasoner.questions.DeadFeaturesQuestion;
//import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
//import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
//import es.us.isa.FAMA.Reasoner.questions.ExplainInvalidProductQuestion;
//import es.us.isa.FAMA.Reasoner.questions.HomogeneityQuestion;
//import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
//import es.us.isa.FAMA.Reasoner.questions.OneProductQuestion;
//import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
//import es.us.isa.FAMA.Reasoner.questions.UniqueFeaturesQuestion;
//import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationErrorsQuestion;
//import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
//import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
//import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
//import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
//import es.us.isa.FAMA.Reasoner.questions.VariantFeaturesQuestion;

/**
 * 
 * Choco 3 reasoner activator.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.Choco.osgi.Activator Choco 2 reasoner activator.
 * @version 0.1, June 2014
 */
public class Activator implements BundleActivator {

	private static BundleContext context;
	/**
	 * Collection of OSGi service registrations for the Choco 3 reasoner. 
	 */
//	private List<ServiceRegistration<?>> services;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		// Initialize the collection of service registrations.
//		this.services = new LinkedList<ServiceRegistration<?>>();
		
		// Create a dictionary to store the properties of the questions and reasoner implementation types.
//		Hashtable<String, String> properties;
		
		// Create an instance of the Choco 3 reasoner implementation that handles simple feature models.
//		Choco3Reasoner choco3ReasonerSimple = new Choco3Reasoner();
		
		// Create an instance of each of the questions supported by the reasoner.
//		CommonalityQuestion commonalityQuestion = new Choco3CommonalityQuestion();
//		CoreFeaturesQuestion coreFeaturesQuestion = new Choco3CoreFeaturesQuestion();
//		DeadFeaturesQuestion deadFeaturesQuestion = new Choco3DeadFeaturesQuestion();
//		DetectErrorsQuestion detectErrorsQuestion = new Choco3DetectErrorsQuestion();
//		ExplainErrorsQuestion explainErrorsQuestion = new Choco3ExplainErrorsQuestion();
//		ExplainInvalidProductQuestion explainInvalidProductQuestion = new Choco3ExplainInvalidProductQuestion();
//		HomogeneityQuestion homogeneityQuestion = new Choco3HomogeneityQuestion();
//		NumberOfProductsQuestion numberOfProductsQuestion = new Choco3NumberOfProductsQuestion();
//		OneProductQuestion oneProductQuestion = new Choco3OneProductQuestion();
//		ProductsQuestion productsQuestion = new Choco3ProductsQuestion();
//		UniqueFeaturesQuestion uniqueFeaturesQuestion = new Choco3UniqueFeaturesQuestion();
//		ValidConfigurationErrorsQuestion validConfigurationErrorsQuestion = new Choco3ValidConfigurationErrorsQuestion();
//		ValidConfigurationQuestion validConfigurationQuestion = new Choco3ValidConfigurationQuestion();
//		ValidProductQuestion validProductQuestion = new Choco3ValidProductQuestion();
//		ValidQuestion validQuestion = new Choco3ValidQuestion();
//		VariabilityQuestion variabilityQuestion = new Choco3VariabilityQuestion();
//		VariantFeaturesQuestion variantFeaturesQuestion = new Choco3VariantFeaturesQuestion();
		
		// The properties for the previous questions only includes the ID for the Choco 3 reasoner implementation
		// that handles simple feature models.
//		properties = new Hashtable<String, String>();
//		properties.put("reasonerId", "Choco3Simple");
		
		// Questions are registered first, then the reasoner.
		
		// Register the questions as services in the OSGi activator's context and store the service registration for future reference.
//		this.services.add(Activator.context.registerService(CommonalityQuestion.class.getCanonicalName(), commonalityQuestion, properties));
//		this.services.add(Activator.context.registerService(CoreFeaturesQuestion.class.getCanonicalName(), coreFeaturesQuestion, properties));
//		this.services.add(Activator.context.registerService(DeadFeaturesQuestion.class.getCanonicalName(), deadFeaturesQuestion, properties));
//		this.services.add(Activator.context.registerService(DetectErrorsQuestion.class.getCanonicalName(), detectErrorsQuestion, properties));
//		this.services.add(Activator.context.registerService(ExplainErrorsQuestion.class.getCanonicalName(), explainErrorsQuestion, properties));
//		this.services.add(Activator.context.registerService(ExplainInvalidProductQuestion.class.getCanonicalName(), explainInvalidProductQuestion, properties));
//		this.services.add(Activator.context.registerService(HomogeneityQuestion.class.getCanonicalName(), homogeneityQuestion, properties));
//		this.services.add(Activator.context.registerService(NumberOfProductsQuestion.class.getCanonicalName(), numberOfProductsQuestion, properties));
//		this.services.add(Activator.context.registerService(OneProductQuestion.class.getCanonicalName(), oneProductQuestion, properties));
//		this.services.add(Activator.context.registerService(ProductsQuestion.class.getCanonicalName(), productsQuestion, properties));
//		this.services.add(Activator.context.registerService(UniqueFeaturesQuestion.class.getCanonicalName(), uniqueFeaturesQuestion, properties));
//		this.services.add(Activator.context.registerService(ValidConfigurationErrorsQuestion.class.getCanonicalName(), validConfigurationErrorsQuestion, properties));
//		this.services.add(Activator.context.registerService(ValidConfigurationQuestion.class.getCanonicalName(), validConfigurationQuestion, properties));
//		this.services.add(Activator.context.registerService(ValidProductQuestion.class.getCanonicalName(), validProductQuestion, properties));
//		this.services.add(Activator.context.registerService(ValidQuestion.class.getCanonicalName(), validQuestion, properties));
//		this.services.add(Activator.context.registerService(VariabilityQuestion.class.getCanonicalName(), variabilityQuestion, properties));
//		this.services.add(Activator.context.registerService(VariantFeaturesQuestion.class.getCanonicalName(), variantFeaturesQuestion, properties));
		
		// Set the properties for the Choco 3 reasoner implementation that handles simple feature models.
//		properties = new Hashtable<String, String>();
//		properties.put("reasonerId", "Choco3Simple");
//		properties.put("famaType", "reasoner");
		
		// Register the Choco 3 reasoner implementation that handles simple feature models as a service in the
		// OSGi activator's context and store the service registration for future reference.
//		this.services.add(Activator.context.registerService(Reasoner.class.getCanonicalName(), choco3ReasonerSimple, properties));
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		// Unregister all services.
//		for (ServiceRegistration<?> service : this.services) {
//			service.unregister();
//		}
		Activator.context = null;
	}

}
