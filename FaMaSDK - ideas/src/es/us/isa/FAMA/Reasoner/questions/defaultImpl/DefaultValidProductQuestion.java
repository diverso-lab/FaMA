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



import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public abstract class DefaultValidProductQuestion implements
		ValidProductQuestion {

	private boolean valid;

	private Product p;

	public void setProduct(Product p)  {

		if (p == null) {
			throw new FAMAParameterException("");
		} else {
			this.p = p;
		}
	}

	public boolean isValid() {
		return valid;
	}

	public PerformanceResult answer(Reasoner r)  {
		if (r == null) {
			throw new FAMAParameterException("");
		} else {
			valid = false;
			PerformanceResult res = this.performanceResultFactory();
			Configuration conf = new Configuration();
			ValidQuestion vq = this.validQuestionFactory();
			if (p == null) {
				throw new IllegalArgumentException(
						"ValidProduct: Product not specified");
			} else {
				Collection<GenericFeature> prodFeats = this.p.getFeatures();
				Collection<? extends GenericFeature> excludeFeats = this.getAllFeatures();

				// if prodFeats contains features that are not on the Feature
				// Model,
				// the product is not valid

				if (!excludeFeats.containsAll(prodFeats)) {
					valid = false;
					System.err.println("Those feature are not part of the model");
					Iterator<GenericFeature> featsIt = prodFeats.iterator();
					while (featsIt.hasNext()) {
						VariabilityElement feat = featsIt.next();
						if (!excludeFeats.contains(feat)) {
							System.err.println(feat.getName());

						}
					}
				} else {
					Collection<GenericFeature> excludeFeats2 = new ArrayList<GenericFeature>(
							excludeFeats);
					excludeFeats2.removeAll(prodFeats);

					Iterator<GenericFeature> it1 = prodFeats.iterator();
					while (it1.hasNext()) {
						VariabilityElement f = it1.next();
						conf.addElement(f, 1);
					}

					Iterator<GenericFeature> it2 = excludeFeats2.iterator();
					while (it2.hasNext()) {
						GenericFeature f = it2.next();
						conf.addElement(f, 0);
						
					}

					r.applyStagedConfiguration(conf);
					res=r.ask(vq);
					
					if (vq.isValid()) {
						valid = true;
					}
					r.unapplyStagedConfigurations();
				}
			}

			return res;
		}
	}



	public abstract ValidQuestion validQuestionFactory();

	public abstract Collection<? extends GenericFeature> getAllFeatures();

	public abstract PerformanceResult performanceResultFactory();

}
