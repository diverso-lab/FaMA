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
package es.us.isa.JaCoPReasoner.questions;

import java.util.Collection;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultDetectErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Observation;
import es.us.isa.JaCoPReasoner.JaCoPQuestion;
import es.us.isa.JaCoPReasoner.JaCoPReasoner;
import es.us.isa.JaCoPReasoner.JaCoPResult;

public class JaCoPDefaultDetectErrorsQuestion extends JaCoPQuestion implements
		DetectErrorsQuestion {

	private DefDetectErrorsQuestion deq;
	
	public JaCoPDefaultDetectErrorsQuestion() {
		super();
		deq = new DefDetectErrorsQuestion();
	}

	public Collection<Error> getErrors() {
		return deq.getErrors();
	}

	public void setObservations(Collection<Observation> arg0) {
		deq.setObservations(arg0);
	}
	
	@Override
	public PerformanceResult answer(JaCoPReasoner r) {
		if(r==null){
			throw new FAMAParameterException("Reasoner:Not specified");
		}
		return deq.answer(r);
	}
	
	@Override
	public String toString(){
		return this.deq.toString();
	}
	
	class DefDetectErrorsQuestion extends DefaultDetectErrorsQuestion{

		@Override
		public PerformanceResult performanceResultFactory() {
			return new JaCoPResult();
		}

		
		@Override
		public ValidQuestion validQuestionFactory() {
			return new JaCoPValidQuestion();
		}

		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}
		
	}

}
