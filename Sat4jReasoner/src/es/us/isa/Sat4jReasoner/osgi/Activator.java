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
package es.us.isa.Sat4jReasoner.osgi;

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
import es.us.isa.Sat4jReasoner.Sat4jReasoner;
import es.us.isa.Sat4jReasoner.questions.Sat4jDefaultCommonalityQuestion;
import es.us.isa.Sat4jReasoner.questions.Sat4jFilterQuestion;
import es.us.isa.Sat4jReasoner.questions.Sat4jNumberOfProductsQuestion;
import es.us.isa.Sat4jReasoner.questions.Sat4jProductsQuestion;
import es.us.isa.Sat4jReasoner.questions.Sat4jSetQuestion;
import es.us.isa.Sat4jReasoner.questions.Sat4jValidConfigurationQuestion;
import es.us.isa.Sat4jReasoner.questions.Sat4jValidProductQuestion;
import es.us.isa.Sat4jReasoner.questions.Sat4jValidQuestion;
import es.us.isa.Sat4jReasoner.questions.Sat4jVariabilityQuestion;

public class Activator implements BundleActivator {

	private List<ServiceRegistration> regs;
	
	
	public void start(BundleContext context) throws Exception {
		//TODO meter el nuevo razonador reificado
		regs = new LinkedList<ServiceRegistration>();
		ServiceRegistration sr;
		Sat4jReasoner r = new Sat4jReasoner();
		Sat4jDefaultCommonalityQuestion scq = new Sat4jDefaultCommonalityQuestion();
		Sat4jFilterQuestion sfq = new Sat4jFilterQuestion();
		Sat4jNumberOfProductsQuestion snpq = new Sat4jNumberOfProductsQuestion();
		Sat4jProductsQuestion spq = new Sat4jProductsQuestion();
		Sat4jSetQuestion ssq = new Sat4jSetQuestion();
		Sat4jValidConfigurationQuestion svcq = new Sat4jValidConfigurationQuestion();
		Sat4jValidProductQuestion svpq = new Sat4jValidProductQuestion();
		Sat4jValidQuestion svq = new Sat4jValidQuestion();
		Sat4jVariabilityQuestion svarq = new Sat4jVariabilityQuestion();
		
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("reasonerId", "Sat4j");
		sr = context.registerService(CommonalityQuestion.class.getCanonicalName(), scq, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidConfigurationQuestion.class.getCanonicalName(), svcq, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidProductQuestion.class.getCanonicalName(), svpq, dictionary);
		regs.add(sr);
		sr = context.registerService(FilterQuestion.class.getCanonicalName(), sfq, dictionary);
		regs.add(sr);
		sr = context.registerService(NumberOfProductsQuestion.class.getCanonicalName(), snpq, dictionary);
		regs.add(sr);
		sr = context.registerService(ProductsQuestion.class.getCanonicalName(), spq, dictionary);
		regs.add(sr);
		sr = context.registerService(SetQuestion.class.getCanonicalName(), ssq, dictionary);
		regs.add(sr);
		sr = context.registerService(ValidQuestion.class.getCanonicalName(), svq, dictionary);
		regs.add(sr);
		sr = context.registerService(VariabilityQuestion.class.getCanonicalName(), svarq, dictionary);
		regs.add(sr);
		
		dictionary = new Hashtable<String, String>();	
		dictionary.put("reasonerId", "Sat4j");
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
