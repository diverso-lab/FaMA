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

import java.util.Collection;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultCommonalityQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;
import es.us.isa.Sat4jReasoner.Sat4jReasoner;
import es.us.isa.Sat4jReasoner.Sat4jResult;

public class Sat4jDefaultCommonalityQuestion extends Sat4jQuestion implements
		CommonalityQuestion {

	private DefCommonalityQuestion cq;
	
	public Sat4jDefaultCommonalityQuestion() {
		super();
		cq = new DefCommonalityQuestion();
	}

	
	public double getCommonality() {
		return this.cq.getCommonality();
	}

	
	
	
	public void preAnswer(Reasoner r){
		this.cq.preAnswer(r);
	}
	
	public PerformanceResult answer(Reasoner r) {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		Sat4jReasoner sat = (Sat4jReasoner) r;
		this.cq.setFeatures(sat.getAllFeatures());
		return this.cq.answer(r);
	}
	
	public String toString(){
		return this.cq.toString();
	}
	
	class DefCommonalityQuestion extends DefaultCommonalityQuestion{

		private Collection<GenericFeature> c;
		
		

		
		public NumberOfProductsQuestion numberOfProductsQuestionFactory() {
			return new Sat4jNumberOfProductsQuestion();
		}

		
		public PerformanceResult performanceResultFactory() {
			return new Sat4jResult();
		}

		

		
		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}

		public void setFeatures(Collection<GenericFeature> c){
			this.c = c;
		}
		
		
		public boolean isValid(GenericFeature f) {
			return this.c.contains(f);
		}
		
	}

	@Override
	public void setConfiguration(Configuration conf) {
		this.cq.setConfiguration(conf);
	}

}
