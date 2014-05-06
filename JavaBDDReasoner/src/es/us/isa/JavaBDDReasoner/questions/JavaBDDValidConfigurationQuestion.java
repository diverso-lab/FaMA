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
package es.us.isa.JavaBDDReasoner.questions;

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
import es.us.isa.JavaBDDReasoner.JavaBDDQuestion;
import es.us.isa.JavaBDDReasoner.JavaBDDReasoner;
import es.us.isa.JavaBDDReasoner.JavaBDDResult;

public class JavaBDDValidConfigurationQuestion extends JavaBDDQuestion
		implements ValidConfigurationQuestion {

	private DefValidConfigurationQuestion vcq;

	public JavaBDDValidConfigurationQuestion() {
		super();
		vcq = new DefValidConfigurationQuestion();
	}

	public boolean isValid() {
		return vcq.isValid();
	}

	public PerformanceResult answer(Reasoner r) {
		if (r == null) {
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		JavaBDDReasoner bdd = (JavaBDDReasoner) r;
		vcq.setAllFeatures(bdd.getAllFeatures());
		return vcq.answer(r);
	}

	public String toString() {
		return this.vcq.toString();
	}

	class DefValidConfigurationQuestion extends
			DefaultValidConfigurationQuestion {

		Collection<GenericFeature> c;

		// public FilterQuestion filterQuestionFactory() {
		// return new JavaBDDFilterQuestion();
		// }

		@Override
		public Collection<GenericFeature> getAllFeatures() {
			return this.c;
		}

		public void setAllFeatures(Collection<GenericFeature> c) {
			this.c = c;
		}

		@Override
		public PerformanceResult performanceResultFactory() {
			return new JavaBDDResult();
		}

		// @Override
		// public SetQuestion setQuestionFactory() {
		// return new JavaBDDSetQuestion();
		// }

		@Override
		public ValidQuestion validQuestionFactory() {
			return new JavaBDDValidQuestion();
		}

		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}

	}

	public void setConfiguration(Configuration c) {
		if (c == null) {
			throw new FAMAParameterException("product :Not specified");
		}
		vcq.setConfiguration(c);

	}

}
