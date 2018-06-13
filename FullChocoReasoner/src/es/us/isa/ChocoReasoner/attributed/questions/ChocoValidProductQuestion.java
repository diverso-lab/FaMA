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
package es.us.isa.ChocoReasoner.attributed.questions;

import java.util.Collection;

import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.ChocoReasoner.attributed.ChocoQuestion;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidProductQuestion;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;

public class ChocoValidProductQuestion extends ChocoQuestion implements
		ValidProductQuestion {

	ChocoDefValidProductQuestion vpq;
	
	public ChocoValidProductQuestion(){
		super();
		vpq = new ChocoDefValidProductQuestion();
	}
	
	
	public boolean isValid() {
		return vpq.isValid();
	}

	
	public void setProduct(Product p){
		vpq.setProduct(p);
	}
	
	
	public PerformanceResult answer(Reasoner r){
		ChocoReasoner choco = (ChocoReasoner)r;
		vpq.setFeatures(choco.getAllFeatures());
		return vpq.answer(choco);
	}
	
	class ChocoDefValidProductQuestion extends DefaultValidProductQuestion{

		private Collection<GenericAttributedFeature> c;
		
		

		public void setFeatures(Collection<GenericAttributedFeature> c){
			this.c = c;
		}
		
		
		public Collection<GenericAttributedFeature> getAllFeatures() {
			return c;
		}

		
		public PerformanceResult performanceResultFactory() {
			return new ChocoResult();
		}

		
		public ValidQuestion validQuestionFactory() {
			return new ChocoValidQuestion();
		}

		
		public Class<? extends Reasoner> getReasonerClass(){
			return  ChocoReasoner.class;
		}
		
	}

}
