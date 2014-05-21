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

package es.us.isa.FAMA.Reasoner;

import java.util.Iterator;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

/**
 * The default criteria selector will select any implementation of a question,
 * that means any reasoner for a question.
 */
public class DefaultCriteriaSelector extends CriteriaSelector {

	public DefaultCriteriaSelector(QuestionTrader qt) {
		super(qt);
	}

	public Question createQuestion(Class<Question> questionClass,
			VariabilityModel vm) {
		// TODO tocar aqui para integrar el extended feature model
		// probar que el criteria selector sigue funcionando correctamente
		Question res = null;
		if (qt != null) {
			Iterator<String> reasoners = qt.getReasonerIds();
			while (reasoners.hasNext() && res == null) {
				String id = reasoners.next();
				Reasoner r = qt.getReasonerById(id);
				if (vm instanceof GenericAttributedFeatureModel) {
					if (r instanceof AttributedFeatureModelReasoner) {
						res = r.getFactory().createQuestion(questionClass);
					}
				} else if (!(r instanceof AttributedFeatureModelReasoner)) {
					res = r.getFactory().createQuestion(questionClass);
				}
			}

		}
		return res;
	}

	public void registerResults(Question q, VariabilityModel fm,
			PerformanceResult pr) {

	}

	@Override
	public Question createQuestion(Class<Question> q, Reasoner reasoner,
			VariabilityModel fm) {
		Question res = null;
		if (qt != null) {
			res = reasoner.getFactory().createQuestion(q);
		}

		return res;
	}
}
