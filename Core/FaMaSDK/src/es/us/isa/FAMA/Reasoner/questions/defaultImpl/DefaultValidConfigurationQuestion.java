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



import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public abstract class DefaultValidConfigurationQuestion implements
		ValidConfigurationQuestion {

	private boolean valid;

	private Configuration c;

	public boolean isValid() {
		return valid;
	}

	public void setConfiguration(Configuration c)  {
		if (c == null) {
			throw new FAMAParameterException("");
		} else {
			this.c = c;

		}
	}

	public PerformanceResult answer(Reasoner r)  {
		if (r == null) {
			throw new FAMAParameterException("");
		} else {
			valid = false;
			PerformanceResult res = this.performanceResultFactory();
			ValidQuestion vq = this.validQuestionFactory();

			

			if (this.getAllFeatures().containsAll(c.getElements().keySet())) {
				

				r.applyStagedConfiguration(c);
				res = r.ask(vq);
				if (vq.isValid()) {
					valid = true;
				}

				r.unapplyStagedConfigurations();
			}

			return res;
		}
	}

	public abstract ValidQuestion validQuestionFactory();

	public abstract Collection<? extends VariabilityElement> getAllFeatures();

	public abstract PerformanceResult performanceResultFactory();

}
