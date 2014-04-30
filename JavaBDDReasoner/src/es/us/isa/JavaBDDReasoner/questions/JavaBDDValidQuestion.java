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

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

import es.us.isa.JavaBDDReasoner.*;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.JavaBDDReasoner.JavaBDDQuestion;

public class JavaBDDValidQuestion extends JavaBDDQuestion implements
		ValidQuestion {

	/**
	 * @uml.property  name="valid"
	 */
	private boolean valid;
	
	public JavaBDDValidQuestion() {
		valid=false;
	}
	
	/**
	 * @return
	 * @uml.property  name="valid"
	 */
	public boolean isValid() {
		return valid;
	}
	
	public PerformanceResult answer(Reasoner r) {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		JavaBDDResult res = new JavaBDDResult();
		JavaBDDReasoner bddr = (JavaBDDReasoner)r;
		long before=System.currentTimeMillis();
		BDD sol=bddr.getBDD().satOne();
		long time=System.currentTimeMillis() - before;
		
		if (!sol.isZero())
			valid = true;
		
		// Save results
		res.setTime(time);
		
		// Consultar este fallo
		res.fillFields((BDDFactory) bddr.getBDDFactory());
		
		return res;
	}
	
	public String toString() {
		if (valid)
			return "Feature model is valid";
		else
			return "Feature model is not valid";
	}
}
