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
package es.us.isa.ChocoReasoner.questions;

import java.util.Collection;

import es.us.isa.ChocoReasoner.ChocoQuestion;
import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultCommonalityQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class ChocoCommonalityQuestion extends ChocoQuestion implements
		CommonalityQuestion {

	private DefCommonalityQuestion cq;
	
	public ChocoCommonalityQuestion() {
		super();
		cq = new DefCommonalityQuestion();
	}

	
	public double getCommonality() {
		return cq.getCommonality();
	}

	
	
	
	public void preAnswer(Reasoner r){
		this.cq.preAnswer(r);
	}
	
	public PerformanceResult answer(Reasoner r) throws FAMAException{
		
		ChocoReasoner choco = (ChocoReasoner) r;
		this.cq.setFeatures(choco.getAllFeatures());
		return cq.answer(r);
		
	}
	
	public String toString(){
		return this.cq.toString();
	}
	
	class DefCommonalityQuestion extends DefaultCommonalityQuestion{

		Collection<GenericFeature> c;
		
		

		
		public NumberOfProductsQuestion numberOfProductsQuestionFactory() {
			return new ChocoNumberOfProductsQuestion();
		}

		
		public PerformanceResult performanceResultFactory() {
			return new ChocoResult();
		}

		

		
		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}

		public void setFeatures(Collection<GenericFeature> c){
			this.c = c;
		}
		
		
		public boolean isValid(GenericFeature f) {
			return c.contains(f);
		}
		
	}

	@Override
	public void setConfiguration(Configuration conf) {
		this.cq.setConfiguration(conf);
		
	}

}
