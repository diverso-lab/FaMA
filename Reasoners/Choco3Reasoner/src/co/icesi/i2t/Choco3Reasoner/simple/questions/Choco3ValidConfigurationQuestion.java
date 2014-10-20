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
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidConfigurationQuestion;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

/**
 * Implementation to solve the valid configuration question using the Choco 3 reasoner.
 * This operation analyzes if a configuration is valid or not.
 * A configuration is a non finished product that can need more features to be a valid product.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoValidConfigurationQuestion Choco 2 implementation for the valid configuration question
 * @version 1.0, June 2014
 */
public class Choco3ValidConfigurationQuestion extends Choco3Question implements
		ValidConfigurationQuestion {

	/**
	 * Default implementation for the valid configuration question provided by the FAMA SDK.
	 */
	private DefaultValidConfigurationQuestionImpl validConfigurationQuestion;
	/**
	 * An instance to the Choco 3 reasoner used.
	 */
	private Choco3Reasoner choco3Reasoner;
	/**
	 * A configuration to be evaluated.
	 */
	private Configuration configuration;
	
	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion#setConfiguration(es.us.isa.FAMA.stagedConfigManager.Configuration)
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion#isValid()
	 */
	public boolean isValid() {
		return this.validConfigurationQuestion.isValid();
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		this.choco3Reasoner = (Choco3Reasoner) reasoner;
		this.validConfigurationQuestion = new DefaultValidConfigurationQuestionImpl();
//		this.validConfigurationQuestion.preAnswer(reasoner);
		this.validConfigurationQuestion.setConfiguration(this.configuration);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#answer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public PerformanceResult answer(Reasoner reasoner) {
		return this.validConfigurationQuestion.answer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#postAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void postAnswer(Reasoner reasoner) {
//		this.validConfigurationQuestion.postAnswer(reasoner);
	}
	
	/**
	 * Default implementation for the valid configuration question provided by the FAMA SDK.
	 * Since the default implementation is not complete, some methods have to be
	 * written.
	 *  
	 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
	 * @version 0.1, June 2014
	 */
	class DefaultValidConfigurationQuestionImpl extends DefaultValidConfigurationQuestion {

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.Question#getReasonerClass()
		 */
		public Class<? extends Reasoner> getReasonerClass() {
			return choco3Reasoner.getClass();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidConfigurationQuestion#validQuestionFactory()
		 */
		@Override
		public ValidQuestion validQuestionFactory() {
			return new Choco3ValidQuestion();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidConfigurationQuestion#getAllFeatures()
		 */
		@Override
		public Collection<? extends VariabilityElement> getAllFeatures() {
			return choco3Reasoner.getAllFeatures();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidConfigurationQuestion#performanceResultFactory()
		 */
		@Override
		public PerformanceResult performanceResultFactory() {
			return new Choco3PerformanceResult();
		}
		
	}

}
