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
package es.us.isa.JavaBDDReasoner.questions;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.JavaBDDReasoner.JavaBDDQuestion;
import es.us.isa.JavaBDDReasoner.JavaBDDReasoner;

public class JavaBDDFilterQuestion extends JavaBDDQuestion implements
		FilterQuestion {

	private ArrayList<String> addedFeatures;		// Added Features
	private ArrayList<String> removedFeatures;      // Removed Features
	private BDD bdd;
	
	public JavaBDDFilterQuestion() {
		super();
		addedFeatures = new ArrayList<String>();
		removedFeatures = new ArrayList<String>();
	}

	public void addValue(VariabilityElement ve, int value) {
		if (!addedFeatures.contains(ve.getName()) && !removedFeatures.contains(ve.getName())){
			if (value > 0){
				addedFeatures.add(ve.getName());
			}
			else{
				removedFeatures.add(ve.getName());
			}
		}
	}

	public void removeValue(VariabilityElement ve) {
		if (!addedFeatures.remove(ve.getName())){
			removedFeatures.remove(ve.getName());
		}
	}
	
	public void preAnswer(Reasoner r) {
		
		JavaBDDReasoner bddr = (JavaBDDReasoner)r; 
		
		bdd = bddr.getBDD();
		
		// Add features
		Iterator it=addedFeatures.iterator();
		while (it.hasNext()) {
			BDD one = ((BDDFactory) bddr.getBDDFactory()).one();
			BDD var = bddr.getBDDVar((String) it.next());
			BDD filter=one.apply(var,((BDDFactory)bddr.getBDDFactory()).biimp);
			BDD bdd_aux=bddr.getBDD();
			bdd_aux = bdd_aux.apply(filter, BDDFactory.and);
			bddr.setBDD(bdd_aux);
		}
		
		// Remove features
		it=removedFeatures.iterator();
		while (it.hasNext()) {
			BDD one = ((BDDFactory) bddr.getBDDFactory()).zero();
			BDD var = bddr.getBDDVar((String) it.next());
			BDD filter=one.apply(var,((BDDFactory)bddr.getBDDFactory()).biimp);
			BDD bdd_aux=bddr.getBDD();
			bdd_aux = bdd_aux.apply(filter, BDDFactory.and);
			bddr.setBDD(bdd_aux);
		}
	}
	
	public void postAnswer(Reasoner r) {
		// Restore previous BDD
		((JavaBDDReasoner)r).setBDD(bdd);
	}

}
