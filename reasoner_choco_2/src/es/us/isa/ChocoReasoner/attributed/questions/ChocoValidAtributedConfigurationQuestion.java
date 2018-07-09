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
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidConfigurationQuestion;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class ChocoValidAtributedConfigurationQuestion extends ChocoQuestion implements
		ValidConfigurationQuestion {

	ChocoDefValidConfigurationQuestion vcq;
	
	public ChocoValidAtributedConfigurationQuestion(){
		vcq = new ChocoDefValidConfigurationQuestion();
	}
	
	
	public boolean isValid() {
		return vcq.isValid();
	}

	
	public void setConfiguration(Configuration p) {
		vcq.setConfiguration(p);
	}
	
	public PerformanceResult answer(Reasoner r){
		ChocoReasoner choco = (ChocoReasoner)r;
		vcq.setFeatures(choco.getAllFeatures());
		return vcq.answer(choco);
	}
	
	class ChocoDefValidConfigurationQuestion extends DefaultValidConfigurationQuestion{

		private Collection<GenericAttributedFeature> c;
		
//		
//		public FilterQuestion filterQuestionFactory() {
//			return new ChocoFilterQuestion();
//		}
		
		public void setFeatures(Collection<GenericAttributedFeature> c){
			this.c = c;
		}

		
		public Collection<GenericAttributedFeature> getAllFeatures() {
			return c;
		}

		
		public PerformanceResult performanceResultFactory() {
			return new ChocoResult();
		}

//		
//		public SetQuestion setQuestionFactory() {
//			return new ChocoSetQuestion();
//		}

		
		public ValidQuestion validQuestionFactory() {
			return new ChocoValidQuestion();
		}

		
		public Class<? extends Reasoner> getReasonerClass(){
			return ChocoReasoner.class;
		}
		
	}

}
