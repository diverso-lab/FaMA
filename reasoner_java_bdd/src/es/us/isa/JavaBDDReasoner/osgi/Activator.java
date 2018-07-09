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
package es.us.isa.JavaBDDReasoner.osgi;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
import es.us.isa.JavaBDDReasoner.JavaBDDReasoner;
import es.us.isa.JavaBDDReasoner.questions.JavaBDDDefaultCommonalityQuestion;
import es.us.isa.JavaBDDReasoner.questions.JavaBDDFilterQuestion;
import es.us.isa.JavaBDDReasoner.questions.JavaBDDNumberOfProductsQuestion;
import es.us.isa.JavaBDDReasoner.questions.JavaBDDProductsQuestion;
import es.us.isa.JavaBDDReasoner.questions.JavaBDDSetQuestion;
import es.us.isa.JavaBDDReasoner.questions.JavaBDDValidConfigurationQuestion;
import es.us.isa.JavaBDDReasoner.questions.JavaBDDValidProductQuestion;
import es.us.isa.JavaBDDReasoner.questions.JavaBDDValidQuestion;
import es.us.isa.JavaBDDReasoner.questions.JavaBDDVariabilityQuestion;

public class Activator implements BundleActivator {

	private List<ServiceRegistration> regs;
	
	
	public void start(BundleContext context) throws Exception {

		regs = new LinkedList<ServiceRegistration>();
		ServiceRegistration sr;
		JavaBDDReasoner r = new JavaBDDReasoner();
		JavaBDDDefaultCommonalityQuestion jcq = new JavaBDDDefaultCommonalityQuestion();
		JavaBDDFilterQuestion jfq = new JavaBDDFilterQuestion();
		JavaBDDNumberOfProductsQuestion jnpq = new JavaBDDNumberOfProductsQuestion();
		JavaBDDProductsQuestion jpq = new JavaBDDProductsQuestion();
		JavaBDDSetQuestion jsq = new JavaBDDSetQuestion();
		JavaBDDValidConfigurationQuestion jvcq = new JavaBDDValidConfigurationQuestion();
		JavaBDDValidProductQuestion jvpq = new JavaBDDValidProductQuestion();
		JavaBDDValidQuestion jvq = new JavaBDDValidQuestion();
		JavaBDDVariabilityQuestion jvarq = new JavaBDDVariabilityQuestion();
		
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("reasonerId", "JavaBDD");
		sr = context.registerService(CommonalityQuestion.class.getCanonicalName(), jcq, dictionary);
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
		dictionary.put("reasonerId", "JavaBDD");
		dictionary.put("famaType", "reasoner");
		sr = context.registerService(Reasoner.class.getCanonicalName(),r,dictionary);
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
