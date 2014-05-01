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
package es.us.isa.FAMA.Reasoner.questions.defaultImpl;



import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;

public abstract class DefaultVariabilityQuestion implements VariabilityQuestion {

	private double vFactor;

	public double getVariability() {
		return this.vFactor;
	}

	public PerformanceResult answer(Reasoner r)  {
		if (r == null) {
			throw new FAMAParameterException("");
		} else {
			PerformanceResult pr = this.performanceResultFactory();
			NumberOfProductsQuestion npq = this
					.numberOfProductsQuestionFactory();
			r.ask(npq);
			double n = (double)npq.getNumberOfProducts();
			double f = (double)this.getNumberOfFeatures();
			double aux = (double) Math.pow(2.0d, f) - 1.0d;
			this.vFactor = (double) n / aux;
			return pr;
		}
	}

	public abstract NumberOfProductsQuestion numberOfProductsQuestionFactory();

	public abstract PerformanceResult performanceResultFactory();

	public abstract double getNumberOfFeatures();

}
