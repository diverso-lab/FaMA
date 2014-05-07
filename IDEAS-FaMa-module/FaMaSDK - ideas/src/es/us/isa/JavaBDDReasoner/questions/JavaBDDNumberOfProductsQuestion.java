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

import net.sf.javabdd.BDDFactory;

import es.us.isa.JavaBDDReasoner.*;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.JavaBDDReasoner.JavaBDDQuestion;

public class JavaBDDNumberOfProductsQuestion extends JavaBDDQuestion implements
		NumberOfProductsQuestion {

	/**
	 * @uml.property  name="numberOfProducts"
	 */
	private double numberOfProducts;
	
	public JavaBDDNumberOfProductsQuestion() {
		numberOfProducts = 0;
	}

	/**
	 * @return
	 * @uml.property  name="numberOfProducts"
	 */
	public double getNumberOfProducts() {
		return numberOfProducts;
	}
	
	public PerformanceResult answer(Reasoner r)  {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		JavaBDDResult res = new JavaBDDResult();
		JavaBDDReasoner bddr = (JavaBDDReasoner)r;
		
		long before=System.currentTimeMillis();
		numberOfProducts=bddr.getBDD().satCount();
		long time=System.currentTimeMillis() - before;

		// Save results
		res.setTime(time);
		res.fillFields((BDDFactory) bddr.getBDDFactory());
		
        return res;
	}
	
	public String toString() {
		return "Number of Products (BDD)= " + getNumberOfProducts();
	}
}
