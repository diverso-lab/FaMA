/*
	This file is part of FaMaTS.

    FaMaTS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FaMaTS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.

 */
package es.us.isa.Choco.osgi;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.ChocoReasoner.questions.ChocoCoreFeaturesQuestion;
import es.us.isa.ChocoReasoner.questions.ChocoExplainInvalidProductQuestion;
import es.us.isa.ChocoReasoner.questions.ChocoVariabilityQuestion;
import es.us.isa.ChocoReasoner.questions.ChocoVariantFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
import es.us.isa.FAMA.Reasoner.questions.CoreFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ExplainInvalidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariantFeaturesQuestion;

@SuppressWarnings("deprecation")
public class Activator implements BundleActivator {

	
	private List<ServiceRegistration> regs;
	private CommonalityQuestion commonalityQuestionService;
	private VariabilityQuestion variabilityQuestion;
	private DetectErrorsQuestion detectErrorsQuestionService;
	private ExplainErrorsQuestion explainErrorsQuestionService;
	private FilterQuestion filterQuestionService;
	private NumberOfProductsQuestion numberOfProductsQuestionService;
	private ProductsQuestion productsQuestionService;
	private SetQuestion setQuestionService;
	private ValidConfigurationErrorsQuestion validConfigurationErrorsQuestionService;
	private ValidConfigurationQuestion validConfigurationQuestionService;
	private ValidProductQuestion validProductQuestionService;
	private ValidQuestion validQuestionService;
	private ExplainInvalidProductQuestion explainInvalidProduct;
	private CoreFeaturesQuestion coreFeats;
	private VariantFeaturesQuestion variantFeats;
	
	private ChocoReasoner chocoReasonerService;
	private es.us.isa.ChocoReasoner4Exp.ChocoReasoner chocoExp;
	private es.us.isa.ChocoReasoner.attributed.ChocoReasoner chocoAtt;
	private es.us.isa.ChocoReasoner4Exp.attributed.ChocoReasoner chocoExpAtt;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		//Registrar antes las preguntas y luego los razonadores
		//(cargamos las preguntas de un razonador al detectar que este se cargo)
		ServiceRegistration sr;
		regs = new LinkedList<ServiceRegistration>();
		chocoReasonerService = new es.us.isa.ChocoReasoner.ChocoReasoner();
		chocoExp = new es.us.isa.ChocoReasoner4Exp.ChocoReasoner();
		chocoAtt = new es.us.isa.ChocoReasoner.attributed.ChocoReasoner();
		chocoExpAtt = new es.us.isa.ChocoReasoner4Exp.attributed.ChocoReasoner();
		commonalityQuestionService = new es.us.isa.ChocoReasoner.questions.ChocoCommonalityQuestion();
		detectErrorsQuestionService = new es.us.isa.ChocoReasoner.questions.ChocoDetectErrorsQuestion();
		explainErrorsQuestionService = new es.us.isa.ChocoReasoner4Exp.questions.ChocoExplainErrorsQuestion();
		filterQuestionService = new es.us.isa.ChocoReasoner.questions.ChocoFilterQuestion();
		numberOfProductsQuestionService = new es.us.isa.ChocoReasoner.questions.ChocoNumberOfProductsQuestion();
		productsQuestionService = new es.us.isa.ChocoReasoner.questions.ChocoProductsQuestion();
		setQuestionService = new es.us.isa.ChocoReasoner.questions.ChocoSetQuestion();
		validConfigurationErrorsQuestionService = new es.us.isa.ChocoReasoner.questions.ChocoValidConfigurationErrorsQuestion();
		validConfigurationQuestionService = new es.us.isa.ChocoReasoner.questions.ChocoValidConfigurationQuestion();
		validProductQuestionService = new es.us.isa.ChocoReasoner.questions.ChocoValidProductQuestion();
		validQuestionService = new es.us.isa.ChocoReasoner.questions.ChocoValidQuestion();
		explainInvalidProduct = new ChocoExplainInvalidProductQuestion();
		coreFeats = new ChocoCoreFeaturesQuestion();
		variantFeats = new ChocoVariantFeaturesQuestion();
		variabilityQuestion = new ChocoVariabilityQuestion();
		
		//Choco
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("reasonerId", "Choco");
		sr = context.registerService(CommonalityQuestion.class.getCanonicalName(),
				commonalityQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(DetectErrorsQuestion.class.getCanonicalName(),
				detectErrorsQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(FilterQuestion.class.getCanonicalName(),
				filterQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(NumberOfProductsQuestion.class
				.getCanonicalName(), numberOfProductsQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(ProductsQuestion.class.getCanonicalName(),
				productsQuestionService, dictionary);
		regs.add(sr);		
		sr = context.registerService(SetQuestion.class.getCanonicalName(),
				setQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidConfigurationErrorsQuestion.class
				.getCanonicalName(), validConfigurationErrorsQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidConfigurationQuestion.class
				.getCanonicalName(), validConfigurationQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidProductQuestion.class.getCanonicalName(),
				validProductQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidQuestion.class.getCanonicalName(),
				validQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(CoreFeaturesQuestion.class.getCanonicalName(),
				coreFeats, dictionary);
		regs.add(sr);
		sr = context.registerService(VariantFeaturesQuestion.class.getCanonicalName(),
				variantFeats, dictionary);
		regs.add(sr);
		sr = context.registerService(ExplainInvalidProductQuestion.class.getCanonicalName(),
				explainInvalidProduct, dictionary);
		regs.add(sr);
		sr = context.registerService(VariabilityQuestion.class.getCanonicalName(),
				variabilityQuestion, dictionary);
		regs.add(sr);
		
		dictionary = new Hashtable<String, String>();	
		dictionary.put("reasonerId", "Choco");
		dictionary.put("famaType", "reasoner");
		sr = context.registerService(Reasoner.class.getCanonicalName(),
				chocoReasonerService, dictionary);
		regs.add(sr);
		
		//Choco 4 explanations
		dictionary = new Hashtable<String, String>();		
		dictionary.put("reasonerId", "Choco4Exp");
		sr = context.registerService(ExplainErrorsQuestion.class.getCanonicalName(),
				explainErrorsQuestionService, dictionary);
		regs.add(sr);
		
		dictionary = new Hashtable<String, String>();		
		dictionary.put("reasonerId", "Choco4Exp");
		dictionary.put("famaType", "reasoner");
		sr = context.registerService(Reasoner.class.getCanonicalName(),
				chocoExp, dictionary);
		regs.add(sr);
		
		//Choco Attributed
		dictionary = new Hashtable<String, String>();		
		dictionary.put("reasonerId", "ChocoAtt");
		detectErrorsQuestionService = new es.us.isa.ChocoReasoner.attributed.questions.ChocoDetectErrorsQuestion();
		filterQuestionService = new es.us.isa.ChocoReasoner.attributed.questions.ChocoFilterQuestion();
		setQuestionService = new es.us.isa.ChocoReasoner.attributed.questions.ChocoSetQuestion();
		validConfigurationQuestionService = new es.us.isa.ChocoReasoner.attributed.questions.ChocoValidConfigurationQuestion();
		validProductQuestionService = new es.us.isa.ChocoReasoner.attributed.questions.ChocoValidProductQuestion();
		validQuestionService = new es.us.isa.ChocoReasoner.attributed.questions.ChocoValidQuestion();
		sr = context.registerService(DetectErrorsQuestion.class.getCanonicalName(), detectErrorsQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(FilterQuestion.class.getCanonicalName(), filterQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(SetQuestion.class.getCanonicalName(), setQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidConfigurationQuestion.class.getCanonicalName(), validConfigurationQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidProductQuestion.class.getCanonicalName(), validProductQuestionService, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidQuestion.class.getCanonicalName(), validQuestionService, dictionary);
		regs.add(sr);
		
		dictionary = new Hashtable<String, String>();		
		dictionary.put("reasonerId", "ChocoAtt");
		dictionary.put("famaType", "reasoner");
		sr = context.registerService(Reasoner.class.getCanonicalName(), chocoAtt, dictionary);
		regs.add(sr);
		
		
		//Choco Attributed 4 Exp
		dictionary = new Hashtable<String, String>();		
		dictionary.put("reasonerId", "ChocoAtt4Exp");
		explainErrorsQuestionService = new es.us.isa.ChocoReasoner4Exp.attributed.questions.ChocoExplainErrorsQuestion();
		sr = context.registerService(ExplainErrorsQuestion.class.getCanonicalName(), explainErrorsQuestionService, dictionary);
		regs.add(sr);
		
		dictionary = new Hashtable<String, String>();		
		dictionary.put("reasonerId", "ChocoAtt4Exp");
		dictionary.put("famaType", "reasoner");
		sr = context.registerService(Reasoner.class.getCanonicalName(), chocoExpAtt, dictionary);
		regs.add(sr);

	}

	public void stop(BundleContext context) throws Exception {
		Iterator<ServiceRegistration> it = regs.iterator();
		while (it.hasNext()){
			ServiceRegistration sr = it.next();
			sr.unregister();
		}
	}

}
