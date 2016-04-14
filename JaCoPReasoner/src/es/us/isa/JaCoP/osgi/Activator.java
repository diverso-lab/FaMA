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
package src.es.us.isa.JaCoP.osgi;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
import es.us.isa.JaCoPReasoner.JaCoPReasoner;
import es.us.isa.JaCoPReasoner.questions.*;
import es.us.isa.JaCoPReasoner4Exp.questions.JaCoPExplainErrorsQuestion;

public class Activator implements BundleActivator {

	private List<ServiceRegistration> regs;
	
	public void start(BundleContext context) throws Exception {
		
		ServiceRegistration sr;
		regs = new LinkedList<ServiceRegistration>();
		JaCoPReasoner r = new JaCoPReasoner();
		es.us.isa.JaCoPReasoner4Exp.JaCoPReasoner rExp = new es.us.isa.JaCoPReasoner4Exp.JaCoPReasoner();
		JaCoPDefaultCommonalityQuestion jcq = new JaCoPDefaultCommonalityQuestion();
		JaCoPDefaultDetectErrorsQuestion jdeq = new JaCoPDefaultDetectErrorsQuestion();
		JaCoPDefaultValidConfigurationQuestion jvcq = new JaCoPDefaultValidConfigurationQuestion();
		JaCoPDefaultValidProductQuestion jvpq = new JaCoPDefaultValidProductQuestion();
		JaCoPFilterQuestion jfq = new JaCoPFilterQuestion();
		JaCoPNumberOfProductsQuestion jnpq = new JaCoPNumberOfProductsQuestion();
		JaCoPProductsQuestion jpq = new JaCoPProductsQuestion();
		JaCoPSetQuestion jsq = new JaCoPSetQuestion();
		JaCoPValidQuestion jvq = new JaCoPValidQuestion();
		JaCoPVariabilityQuestion jvarq = new JaCoPVariabilityQuestion();
		JaCoPExplainErrorsQuestion jeeq = new JaCoPExplainErrorsQuestion();
		
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("reasonerId", "JaCoP");
		sr = context.registerService(CommonalityQuestion.class.getCanonicalName(), jcq, dictionary);
		regs.add(sr);
		sr = context.registerService(DetectErrorsQuestion.class.getCanonicalName(), jdeq, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidConfigurationQuestion.class.getCanonicalName(), jvcq, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidProductQuestion.class.getCanonicalName(), jvpq, dictionary);
		regs.add(sr);
		sr = context.registerService(FilterQuestion.class.getCanonicalName(), jfq, dictionary);
		regs.add(sr);
		sr = context.registerService(NumberOfProductsQuestion.class.getCanonicalName(), jnpq, dictionary);
		regs.add(sr);
		sr = context.registerService(ProductsQuestion.class.getCanonicalName(), jpq, dictionary);
		regs.add(sr);
		sr = context.registerService(SetQuestion.class.getCanonicalName(), jsq, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidQuestion.class.getCanonicalName(), jvq, dictionary);
		regs.add(sr);
		sr = context.registerService(VariabilityQuestion.class.getCanonicalName(), jvarq, dictionary);
		regs.add(sr);
		
		dictionary = new Hashtable<String, String>();	
		dictionary.put("reasonerId", "JaCoP");
		dictionary.put("famaType", "reasoner");
		sr = context.registerService(Reasoner.class.getCanonicalName(),r,dictionary);
		regs.add(sr);
		
		dictionary = new Hashtable<String, String>();
		dictionary.put("reasonerId", "JaCoP4Exp");
		sr = context.registerService(ExplainErrorsQuestion.class.getCanonicalName(), jeeq, dictionary);
		regs.add(sr);
		
		dictionary = new Hashtable<String, String>();	
		dictionary.put("reasonerId", "JaCoP4Exp");
		dictionary.put("famaType", "reasoner");
		sr = context.registerService(Reasoner.class.getCanonicalName(),rExp,dictionary);
		
	}

	public void stop(BundleContext context) throws Exception {
		Iterator<ServiceRegistration> it = regs.iterator();
		while (it.hasNext()){
			ServiceRegistration sr = it.next();
			sr.unregister();
		}
	}


}
