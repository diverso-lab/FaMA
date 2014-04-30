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

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

/**
 * This class will select the reasoner for a question,
 */
public abstract class CriteriaSelector {
	protected QuestionTrader qt;

	public CriteriaSelector(QuestionTrader qt) {
		this.qt = qt;
	}

	public abstract Question createQuestion(Class<Question> questionInt,
			VariabilityModel vm);

	public abstract void registerResults(Question q, VariabilityModel vm,
			PerformanceResult pr);

	public abstract Question createQuestion(Class<Question> q, Reasoner selectedReasoner,
			VariabilityModel fm);
}
