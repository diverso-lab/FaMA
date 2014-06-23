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

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultVariabilityQuestion;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;
import es.us.isa.Sat4jReasoner.Sat4jReasoner;
import es.us.isa.Sat4jReasoner.Sat4jResult;

public class Sat4jVariabilityQuestion extends Sat4jQuestion implements
		VariabilityQuestion {

	private DefVariabilityQuestion vq;
	
	public Sat4jVariabilityQuestion(){
		vq = new DefVariabilityQuestion();
	}
	
	
	public double getVariability() {
		// TODO Auto-generated method stub
		return vq.getVariability();
	}
	
	public PerformanceResult answer(Reasoner r) {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		Sat4jReasoner c = (Sat4jReasoner) r;
		vq.setNumberOfFeatures(c.getAllFeatures().size());
		return vq.answer(r);
	}
	
	public String toString(){
		return this.vq.toString();
	}

	class DefVariabilityQuestion extends DefaultVariabilityQuestion{

		private long nFeatures;
		
		
		public double getNumberOfFeatures() {
			return nFeatures;
		}
		
		public void setNumberOfFeatures(long n){
			nFeatures = n;
		}

		
		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}

		
		public NumberOfProductsQuestion numberOfProductsQuestionFactory() {
			return new Sat4jNumberOfProductsQuestion();
		}

		
		public PerformanceResult performanceResultFactory() {
			return new Sat4jResult();
		}
		
	}
	
}
