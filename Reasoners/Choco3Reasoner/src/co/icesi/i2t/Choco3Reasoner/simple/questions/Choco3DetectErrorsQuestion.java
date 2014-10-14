/**
 *  This file is part of FaMaTS.
 *
 *  FaMaTS is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FaMaTS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.icesi.i2t.Choco3Reasoner.simple.questions;

import java.util.Collection;

import co.icesi.i2t.Choco3Reasoner.Choco3PerformanceResult;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Question;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultDetectErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Observation;

/**
 * Implementation to solve the detect errors question using the Choco 3 reasoner.
 * This operation looks for errors on a feature model.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoDetectErrorsQuestion Choco 2 implementation for the detect errors question
 * @version 1.0, June 2014
 */
public class Choco3DetectErrorsQuestion extends Choco3Question implements
		DetectErrorsQuestion {
	
	/**
	 * Default implementation for the detect errors question provided by the FAMA SDK.
	 */
	private DefaultDetectErrorsQuestionImpl detectErrorsQuestion;
	/**
	 * An instance to the Choco 3 reasoner used.
	 */
	private Choco3Reasoner choco3Reasoner;

	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion#setObservations(java.util.Collection)
	 */
	public void setObservations(Collection<Observation> observations) {
		this.detectErrorsQuestion.setObservations(observations);
	}

	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion#getErrors()
	 */
	public Collection<Error> getErrors() {
		return this.detectErrorsQuestion.getErrors();
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		this.choco3Reasoner = (Choco3Reasoner) reasoner;
		this.detectErrorsQuestion = new DefaultDetectErrorsQuestionImpl();
//		this.detectErrorsQuestion.preAnswer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#answer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public PerformanceResult answer(Reasoner reasoner) {
		return this.detectErrorsQuestion.answer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#postAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void postAnswer(Reasoner reasoner) {
//		this.detectErrorsQuestion.postAnswer(reasoner);
	}
	
	/**
	 * Default implementation for the detect errors question provided by the FAMA SDK.
	 * Since the default implementation is not complete, some methods have to be
	 * written.
	 *  
	 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
	 * @version 0.1, June 2014
	 */
	class DefaultDetectErrorsQuestionImpl extends DefaultDetectErrorsQuestion {

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.Question#getReasonerClass()
		 */
		public Class<? extends Reasoner> getReasonerClass() {
			return choco3Reasoner.getClass();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultDetectErrorsQuestion#validQuestionFactory()
		 */
		@Override
		public ValidQuestion validQuestionFactory() {
			return new Choco3ValidQuestion();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultDetectErrorsQuestion#performanceResultFactory()
		 */
		@Override
		public PerformanceResult performanceResultFactory() {
			return new Choco3PerformanceResult();
		}
		
	}

}
