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
package es.us.isa.FAMA.osgi.activator;


import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import es.us.isa.FAMA.Reasoner.CriteriaSelector;
import es.us.isa.FAMA.Reasoner.DefaultCriteriaSelector;
import es.us.isa.FAMA.Reasoner.OSGIQuestionFactory;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
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
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariantFeaturesQuestion;
import es.us.isa.FAMA.loader.impl.OSGIExtensionsLoader;
import es.us.isa.FAMA.models.variabilityModel.parsers.IReader;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;
import es.us.isa.FAMA.models.variabilityModel.parsers.ModelParserImpl;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;


public class Activator implements BundleActivator,ServiceListener {

	private Map<String, Class<Question>> questionClasses;
	
	private List<ServiceRegistration> regs;
	
	private BundleContext context;
	
	public void start(BundleContext context) throws Exception {
		this.context = context;
		regs = new LinkedList<ServiceRegistration>();
		questionClasses = new HashMap<String, Class<Question>>();
		ServiceRegistration sr;
		loadQuestions();
		registerCriteriaSelectors(context);
	
		consumeReasoners(context);
		consumeReadersAndWriters(context);
		consumeCriteriaSelectors(context);
		consumeTransformations(context);
		
		//QuestionTrader como servicio
		QuestionTrader qt = new QuestionTrader("OSGI");
		Hashtable<String,String> d = null;
		d = new Hashtable<String,String>();
		d.put("famaType", "facade");
		sr = context.registerService(QuestionTrader.class.getCanonicalName(), qt, d);
		regs.add(sr);
		//Listener para estar pendiente de los servicios que se registren
		//para fama
		String filter = "(|(famaType=reader)(famaType=writer)(famaType=reasoner)" +
			"(famaType=transform)(famaType=selector))";
		context.addServiceListener(this, filter);

	}


	private void registerCriteriaSelectors(BundleContext context) {
		
		ServiceRegistration sr;
		Hashtable<String,String> d = new Hashtable<String,String>();
		d.put("id", "default");
		d.put("famaType", "selector");
		sr = context.registerService(CriteriaSelector.class.getCanonicalName(), 
				new DefaultCriteriaSelector(null), d);
		regs.add(sr);
		
	}
	
	@SuppressWarnings("unchecked")
	private void loadQuestions(){
		
		questionClasses = new HashMap<String, Class<Question>>();
		Class<? extends Question> clazz = ProductsQuestion.class;
		questionClasses.put("Products", (Class<Question>) clazz);
		clazz = NumberOfProductsQuestion.class;
		questionClasses.put("#Products", (Class<Question>) clazz);
		clazz = ValidQuestion.class;
		questionClasses.put("Valid", (Class<Question>) clazz);
		clazz = CommonalityQuestion.class;
		questionClasses.put("Commonality", (Class<Question>) clazz);
		clazz = VariabilityQuestion.class;
		questionClasses.put("Variability", (Class<Question>) clazz);
		clazz = ValidProductQuestion.class;
		questionClasses.put("ValidProduct", (Class<Question>) clazz);
		clazz = ValidConfigurationQuestion.class;
		questionClasses.put("ValidConfiguration", (Class<Question>) clazz);
		clazz = DetectErrorsQuestion.class;
		questionClasses.put("DetectErrors", (Class<Question>) clazz);
		clazz = FilterQuestion.class;
		questionClasses.put("Filter", (Class<Question>) clazz);
		clazz = SetQuestion.class;
		questionClasses.put("Set", (Class<Question>) clazz);
		clazz = ExplainErrorsQuestion.class;
		questionClasses.put("Explanations", (Class<Question>) clazz);
		clazz = ExplainInvalidProductQuestion.class;
		questionClasses.put("ExplainProduct", (Class<Question>) clazz);
		clazz = CoreFeaturesQuestion.class;
		questionClasses.put("CoreFeatures", (Class<Question>) clazz);
		clazz = VariantFeaturesQuestion.class;
		questionClasses.put("VariantFeatures", (Class<Question>) clazz);
		OSGIExtensionsLoader.getInstance().getQuestionsMap().putAll(questionClasses);
		
	}

	private void consumeTransformations(BundleContext context) {
		
		try {
			 
			// en teoria, no necesita filtro
			String filter = null;
			ServiceReference[] sr = context.getAllServiceReferences(IVariabilityModelTransform.class
							.getCanonicalName(), filter);
			if (sr != null){
				for (int i = 0; i < sr.length; i++) {
					this.consumeTransform(sr[i]);
				}
			}
			
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

	}

	private void consumeCriteriaSelectors(BundleContext context) {
		
		try {
			// en teoria, no necesita filtro
			String filter = null;
			ServiceReference[] sr = context.getAllServiceReferences(
					CriteriaSelector.class.getCanonicalName(), filter);
			if (sr != null){
				for (int i = 0; i < sr.length; i++) {
					consumeSelector(sr[i]);
				}
			}

		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

	}

	private void consumeReadersAndWriters(BundleContext context) {
		
		try {
			// en teoria, no necesita filtro
			String filter = null;
			ServiceReference[] sr = context.getAllServiceReferences(
					IReader.class.getCanonicalName(), filter);
			if (sr != null){
				for (int i = 0; i < sr.length; i++) {
					consumeReader(sr[i]);
 				}

				// en teoria, no necesita filtro
				filter = null;
				sr = context.getAllServiceReferences(IWriter.class.getName(),
						filter);
				for (int i = 0; i < sr.length; i++) {
					consumeWriter(sr[i]);
				}
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

	}

	private void consumeReasoners(BundleContext context) {
		
		try {
			// en teoria, no necesita filtro
			// para que nos devuelva los razonadores
			String filter = null;
			ServiceReference[] sr = context.getAllServiceReferences(
					Reasoner.class.getCanonicalName(), filter);
			if (sr != null){
				for (int i = 0; i < sr.length; i++) {
					// cargar los razonadores
					consumeReasoner(sr[i]);
				}
			}
			
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		
	}	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		Iterator<ServiceRegistration> it = regs.iterator();
		while (it.hasNext()){
			ServiceRegistration sr = it.next();
			sr.unregister();
		}
	}

	public void serviceChanged(ServiceEvent arg0) {
		
		if (arg0.getType() == ServiceEvent.REGISTERED){
			ServiceReference sr = arg0.getServiceReference();
			String famaType = (String)sr.getProperty("famaType");
			if (famaType.equals("reasoner")){
				consumeReasoner(sr);
			}
			else if(famaType.equals("reader")){
				consumeReader(sr);
			}
			else if (famaType.equals("writer")){
				consumeWriter(sr);
			}
			else if (famaType.equals("selector")){
				consumeSelector(sr);
			}
			else if (famaType.equals("transform")){
				consumeTransform(sr);
			}
			else{
				throw new IllegalArgumentException("famaType value not valid");
			}
		}
		
	}

	private void consumeTransform(ServiceReference sr) {
		
//		BundleContext context = sr.getBundle().getBundleContext();
		IVariabilityModelTransform t = (IVariabilityModelTransform)
		context.getService(sr);
		Class<IVariabilityModelTransform> clazz = 
			 (Class<IVariabilityModelTransform>) t.getClass();
		String id = (String) sr.getProperty("id");
		OSGIExtensionsLoader.getInstance().addTransformation(id, clazz);
		
	}

	private void consumeSelector(ServiceReference sr) {
		
//		BundleContext context = sr.getBundle().getBundleContext();
		CriteriaSelector cs = (CriteriaSelector) context.getService(sr);
		String id = (String) sr.getProperty("id");
		OSGIExtensionsLoader.getInstance().addCriteriaSelector(id, cs);
		
	}

	private void consumeWriter(ServiceReference sr) {
		
//		BundleContext context = sr.getBundle().getBundleContext();
		IWriter w = (IWriter) context.getService(sr);
		String id = (String) sr.getProperty("id");
		ModelParserImpl aux = (ModelParserImpl) OSGIExtensionsLoader.getInstance().getModelParser();
		aux.addWriter(w, id);
		
	}

	private void consumeReader(ServiceReference sr) {
		
		ModelParserImpl aux = (ModelParserImpl) OSGIExtensionsLoader.getInstance().getModelParser();
//		BundleContext context = sr.getBundle().getBundleContext();
		IReader r = (IReader) context.getService(sr);
		String id = (String) sr.getProperty("id");
		aux.addReader(r, id);
		String extensions = (String) sr.getProperty("extensions");
		if (extensions != null){
			StringTokenizer stk = new StringTokenizer(extensions,",");
			while (stk.hasMoreTokens()){
				String ext = stk.nextToken();
				aux.addReaderType(ext, id);
			}
		}	
		
	}

	private void consumeReasoner(ServiceReference sr){
		
//		BundleContext context = sr.getBundle().getBundleContext();
		Reasoner r = (Reasoner) context.getService(sr);
		String reasonerId = (String) sr.getProperty("reasonerId");
		OSGIQuestionFactory qf = new OSGIQuestionFactory();
		// y las preguntas que implementan

		// para cada una de las posibles preguntas
		Iterator<Entry<String, Class<Question>>> it = questionClasses
				.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Class<Question>> aux = it.next();
			//String questionId = aux.getKey();
			Class<Question> questionClass = aux.getValue();
			// con este filtro, obligamos a que sea del razonador
			// especificado
			String questionFilter = "(reasonerId=" + reasonerId + ")";
			// vemos si el razonador la tiene registrada
			ServiceReference[] refs;
			try {
				refs = context.getAllServiceReferences(
						questionClass.getCanonicalName(), questionFilter);
				if (refs != null && refs.length > 0) {
					 Question questionImpl = 
						 (Question)context.getService(refs[0]);
					 Class<Question> questionImplClass = 
						 (Class<Question>)questionImpl.getClass();
					 qf.addQuestion(questionClass.getName(), questionImplClass);
				}
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}		
		}
		r.setFactory(qf);
		OSGIExtensionsLoader.getInstance().addReasoner(reasonerId, r);
		
	}
	

}
