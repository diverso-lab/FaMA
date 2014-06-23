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
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidConfigurationQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;
import es.us.isa.Sat4jReasoner.Sat4jReasoner;
import es.us.isa.Sat4jReasoner.Sat4jResult;

public class Sat4jValidConfigurationQuestion extends Sat4jQuestion implements
		ValidConfigurationQuestion {

	private DefValidConfigurationQuestion vcq;
	
	public Sat4jValidConfigurationQuestion() {
		super();
		vcq = new DefValidConfigurationQuestion();
	}

	
	public boolean isValid() {
		return vcq.isValid();
	}

	
	public void setConfiguration(Configuration p)  {
		if(p==null){
			throw new FAMAParameterException("Product :Not specified");
		}
		vcq.setConfiguration(p);
	}
	
	public PerformanceResult answer(Reasoner r) {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		Sat4jReasoner sat = (Sat4jReasoner) r;
		vcq.setAllFeatures(sat.getAllFeatures());
		return this.vcq.answer(r);
	}
	
	public String toString(){
		return this.vcq.toString();
	}
	
	class DefValidConfigurationQuestion extends DefaultValidConfigurationQuestion{

		Collection<GenericFeature> c;
		
//		
//		public FilterQuestion filterQuestionFactory() {
//			return new Sat4jFilterQuestion();
//		}

		
		public Collection<GenericFeature> getAllFeatures() {
			return this.c;
		}
		
		public void setAllFeatures(Collection<GenericFeature> c){
			this.c = c;
		}

		
		public PerformanceResult performanceResultFactory() {
			return new Sat4jResult();
		}

//		
//		public SetQuestion setQuestionFactory() {
//			return new Sat4jSetQuestion();
//		}

		
		public ValidQuestion validQuestionFactory() {
			return new Sat4jValidQuestion();
		}

		
		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}
		
	}

}
