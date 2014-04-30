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
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultDetectErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Observation;

public class ChocoDetectErrorsQuestion extends ChocoQuestion implements DetectErrorsQuestion {

	private ChocoDefDetectErrorsQuestion deq;
	
	public ChocoDetectErrorsQuestion(){
		super();
		deq = new ChocoDefDetectErrorsQuestion();
	}
	
	
	public Collection<Error> getErrors() {
		return deq.getErrors();
	}

	
	public void setObservations(Collection<Observation> observations)
			{
		deq.setObservations(observations);
	}
	
	public PerformanceResult answer(Reasoner r){
		return deq.answer(r);
	}
	
	class ChocoDefDetectErrorsQuestion extends DefaultDetectErrorsQuestion{

	

		
		public PerformanceResult performanceResultFactory() {
			return new ChocoResult();
		}

		

		
		public ValidQuestion validQuestionFactory() {
			return new ChocoValidQuestion();
		}


		public Class<? extends Reasoner> getReasonerClass(){
			return ChocoReasoner.class;
		}
		
	}


}
