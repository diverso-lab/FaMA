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
import es.us.isa.FAMA.Reasoner.questions.DeadFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultDeadFeaturesQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;

/**
 * Implementation to solve the dead features question using the Choco 3 reasoner.
 * This operation calculates features that are not present in any product that can be 
 * derived from the feature model with the specified constraints.
 * A feature is dead if it cannot appear in any product due to wrong definitions of 
 * cross-tree constraints.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoDeadFeaturesQuestion Choco 2 implementation for the dead features question.
 * @version 1.0, June 2014
 */
public class Choco3DeadFeaturesQuestion extends Choco3Question implements
		DeadFeaturesQuestion {

	/**
	 * Default implementation for the dead features question provided by the FAMA SDK.
	 */
	private DefaultDeadFeaturesQuestionImpl deadFeaturesQuestion;
	/**
	 * An instance to the Choco 3 reasoner used.
	 */
	private Choco3Reasoner choco3Reasoner;
	/**
	 * Collection of all dead features in the feature model.
	 * A dead feature is a feature that is not present in any product that can be 
	 * derived from the feature model with the specified constraints.
	 */
	private Collection<GenericFeature> deadFeatures;
	
	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.DeadFeaturesQuestion#getDeadFeatures()
	 */
	public Collection<GenericFeature> getDeadFeatures() {
		return this.deadFeatures;
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		this.choco3Reasoner = (Choco3Reasoner) reasoner;
		this.deadFeaturesQuestion = new DefaultDeadFeaturesQuestionImpl();
//		this.deadFeaturesQuestion.preAnswer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#answer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public PerformanceResult answer(Reasoner reasoner) {
		PerformanceResult performanceResult = this.deadFeaturesQuestion.answer(reasoner);
		this.deadFeatures = this.deadFeaturesQuestion.getDeadFeatures();
		return performanceResult;
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#postAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void postAnswer(Reasoner reasoner) {
//		this.deadFeaturesQuestion.postAnswer(reasoner);
	}
	
	/**
	 * Default implementation for the dead features question provided by the FAMA SDK.
	 * Since the default implementation is not complete, some methods have to be
	 * written.
	 *  
	 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
	 * @version 0.1, June 2014
	 */
	class DefaultDeadFeaturesQuestionImpl extends DefaultDeadFeaturesQuestion {

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.Question#getReasonerClass()
		 */
		public Class<? extends Reasoner> getReasonerClass() {
			return choco3Reasoner.getClass();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultDeadFeaturesQuestion#performanceResultFactory()
		 */
		@Override
		public PerformanceResult performanceResultFactory() {
			return new Choco3PerformanceResult();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultDeadFeaturesQuestion#getAllFeatures()
		 */
		@Override
		public Collection<GenericFeature> getAllFeatures() {
			return choco3Reasoner.getAllFeatures();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultDeadFeaturesQuestion#productsQuestionFactory()
		 */
		@Override
		public ProductsQuestion productsQuestionFactory() {
			return new Choco3ProductsQuestion();
		}
		
	}

}
