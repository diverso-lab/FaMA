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
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidProductQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;
import es.us.isa.Sat4jReasoner.Sat4jReasoner;
import es.us.isa.Sat4jReasoner.Sat4jResult;

public class Sat4jValidProductQuestion extends Sat4jQuestion implements
		ValidProductQuestion {

	private DefValidProductQuestion vpq;

	public Sat4jValidProductQuestion() {
		super();
		this.vpq = new DefValidProductQuestion();
	}

	
	public boolean isValid() {
		return vpq.isValid();
	}

	
	public void setProduct(Product p) {
		if (p == null) {
			throw new FAMAParameterException("Product :Not specified");
		}
		vpq.setProduct(p);
	}

	public PerformanceResult answer(Reasoner r) {
		if (r == null) {
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		Sat4jReasoner sat = (Sat4jReasoner) r;
		vpq.setAllFeatures(sat.getAllFeatures());
		return this.vpq.answer(r);
	}

	public String toString() {
		return this.vpq.toString();
	}

	class DefValidProductQuestion extends DefaultValidProductQuestion {

		Collection<GenericFeature> feats;

		
		public Collection<GenericFeature> getAllFeatures() {
			return this.feats;
		}

		public void setAllFeatures(Collection<GenericFeature> f) {
			this.feats = f;
		}

		
		public PerformanceResult performanceResultFactory() {
			return new Sat4jResult();
		}

		
		public ValidQuestion validQuestionFactory() {
			return new Sat4jValidQuestion();
		}

		
		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}

	}

}
