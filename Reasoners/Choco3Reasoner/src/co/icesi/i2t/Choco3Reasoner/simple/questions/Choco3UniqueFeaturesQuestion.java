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
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.UniqueFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultUniqueFeaturesQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;

/**
 * Implementation to solve the unique features question using the Choco 3 reasoner.
 * This operation calculates features that are unique to a product that can be 
 * derived from the feature model with the specified constraints.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoUniqueFeaturesQuestion Choco 2 implementation for the unique features question.
 * @version 1.0, June 2014
 */
public class Choco3UniqueFeaturesQuestion extends Choco3Question implements
		UniqueFeaturesQuestion {

	/**
	 * Default implementation for the unique features question provided by the FAMA SDK.
	 */
	private DefaultUniqueFeaturesQuestionImpl uniqueFeaturesQuestion;
	/**
	 * An instance to the Choco 3 reasoner used.
	 */
	private Choco3Reasoner choco3Reasoner;
	/**
	 * Collection of all unique features in the feature model.
	 * A unique feature is a feature that is only found in one of the products 
	 * that can be derived from the feature model with the specified constraints.
	 */
	private Collection<GenericFeature> uniqueFeatures;
	
	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.UniqueFeaturesQuestion#getUniqueFeatures()
	 */
	public Collection<GenericFeature> getUniqueFeatures() {
		return this.uniqueFeatures;
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		this.choco3Reasoner = (Choco3Reasoner) reasoner;
		this.uniqueFeaturesQuestion = new DefaultUniqueFeaturesQuestionImpl();
//		this.uniqueFeaturesQuestion.preAnswer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#answer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public PerformanceResult answer(Reasoner reasoner) {
		this.uniqueFeatures = this.uniqueFeaturesQuestion.getUniqueFeatures();
		return this.uniqueFeaturesQuestion.answer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#postAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void postAnswer(Reasoner reasoner) {
//		this.uniqueFeaturesQuestion.postAnswer(reasoner);
	}
	
	/**
	 * Default implementation for the unique features question provided by the FAMA SDK.
	 * Since the default implementation is not complete, some methods have to be
	 * written.
	 *  
	 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
	 * @version 0.1, June 2014
	 */
	class DefaultUniqueFeaturesQuestionImpl extends DefaultUniqueFeaturesQuestion {

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.Question#getReasonerClass()
		 */
		public Class<? extends Reasoner> getReasonerClass() {
			return choco3Reasoner.getClass();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultUniqueFeaturesQuestion#productsQuestionFactory()
		 */
		@Override
		public ProductsQuestion productsQuestionFactory() {
			return new Choco3ProductsQuestion();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultUniqueFeaturesQuestion#getAllFeatures()
		 */
		@Override
		public Collection<? extends GenericFeature> getAllFeatures() {
			return choco3Reasoner.getAllFeatures();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultUniqueFeaturesQuestion#performanceResultFactory()
		 */
		@Override
		public PerformanceResult performanceResultFactory() {
			return new Choco3PerformanceResult();
		}
		
	}

}
