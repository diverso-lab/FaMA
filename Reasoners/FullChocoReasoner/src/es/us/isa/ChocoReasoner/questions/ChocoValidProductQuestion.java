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
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidProductQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

public class ChocoValidProductQuestion extends ChocoQuestion implements
		ValidProductQuestion {

	private DefValidProductQuestion vpq;
	
	public ChocoValidProductQuestion(){
		super();
		vpq = new DefValidProductQuestion();
	}
	
	
	public boolean isValid() {
		return vpq.isValid();
	}

	
	public void setProduct(Product p) {
		if(p==null){
			throw new FAMAException("Product: Product not specified");
		}
		vpq.setProduct(p);
	}
	
	public PerformanceResult answer(Reasoner r) throws FAMAException{
		if(r==null){
			throw new FAMAException("Reasoner: Reasoner not specified");
		}
		ChocoReasoner choco = (ChocoReasoner) r;
		this.vpq.setAllFeatures(choco.getAllFeatures());
		return this.vpq.answer(r);
	}

	public String toString(){
		return this.vpq.toString();
	}
	
	class DefValidProductQuestion extends DefaultValidProductQuestion{

		Collection<GenericFeature> c;
		
		

		public void setAllFeatures(Collection<GenericFeature> c){
			this.c = c;
		}
		
		
		public Collection<GenericFeature> getAllFeatures() {
			return c;
		}

		
		public PerformanceResult performanceResultFactory() {
			return new ChocoResult();
		}

		

		
		public ValidQuestion validQuestionFactory() {
			return new ChocoValidQuestion();
		}

		
		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}
		
	}
	
}
