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
package es.us.isa.Sat4jReasoner.questions;

import java.util.ArrayList;
import java.util.Iterator;


import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;
import es.us.isa.Sat4jReasoner.Sat4jReasoner;

public class Sat4jFilterQuestion extends Sat4jQuestion implements
		FilterQuestion {

	private ArrayList<String> addedFeatures;			// Added Features
	private ArrayList<String> removedFeatures;      	// Removed Features
	private ArrayList<String> addedClauses;         	// Added clauses
	
	public Sat4jFilterQuestion() {
		super();
		addedFeatures = new ArrayList<String>();
		removedFeatures = new ArrayList<String>();
		addedClauses = new ArrayList<String>();
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
		
		Sat4jReasoner sr=(Sat4jReasoner)r;
		
		// ADD CLAUSES
		
		// Added Features
		Iterator<String> it = addedFeatures.iterator();
		while (it.hasNext()) {
			String cnf_var = sr.getCNFVar(it.next());
			String clause = cnf_var + " 0";
			sr.getClauses().add(clause);
			addedClauses.add(clause);
		}
		
		// Removed Features
		it = removedFeatures.iterator();
		while (it.hasNext()) {
			String cnf_var = sr.getCNFVar((String)it.next());
			String clause ="-" + cnf_var + " 0";
			sr.getClauses().add(clause);
			addedClauses.add(clause);
		}
		
		// Create CNF file
		super.preAnswer(r);
	}
		
	public void postAnswer(Reasoner r) {
		//Remove filter
		Iterator<String> it = addedClauses.iterator();
		while (it.hasNext())
			((Sat4jReasoner)r).getClauses().remove(it.next());
	}

}
