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
package es.us.isa.FAMA.Reasoner.questions.extended.defaultImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.extended.ValidAttributedProductQuestion;
import es.us.isa.FAMA.models.featureModel.AttributedProduct;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public abstract class DefaultValidAttributedProductQuestion implements
		ValidAttributedProductQuestion {

	protected AttributedProduct p;

	protected boolean valid;

	
	public boolean isValid() {
		return valid;
	}

	
	public void setProduct(AttributedProduct p) {
		this.p = p;
	}

	public PerformanceResult answer(Reasoner r) {

		valid = false;
		PerformanceResult res = this.performanceResultFactory();
		Configuration conf= new Configuration();
		ValidQuestion vq = this.validQuestionFactory();
		if (p == null) {
			throw new IllegalArgumentException(
					"ValidProduct: Product not specified");
		} else {
			Collection<GenericAttributedFeature> prodFeats = this.p.getAttFeatures();
			Collection<? extends GenericAttributedFeature> excludeFeats = this
					.getAllFeatures();

			// if prodFeats contains features that are not on the Feature
			// Model,
			// the product is not valid

			if (!excludeFeats.containsAll(prodFeats)) {
				valid = false;
				System.err.println("Those feature are not part of the model");
				Iterator<GenericAttributedFeature> featsIt = prodFeats.iterator();
				while (featsIt.hasNext()) {
					GenericAttributedFeature feat = featsIt.next();
					if (!excludeFeats.contains(feat)) {
						System.err.println(feat.getName());

					}
				}
			} else {
				Collection<GenericAttributedFeature> excludeFeats2 = 
						new ArrayList<GenericAttributedFeature>(excludeFeats);
				excludeFeats2.removeAll(prodFeats);

				Iterator<GenericAttributedFeature> it1 = prodFeats.iterator();
				while (it1.hasNext()) {
					GenericAttributedFeature f = it1.next();
					conf.addElement(f, 1);
					Collection<? extends GenericAttribute> atts = f.getAttributes();
					Iterator<? extends GenericAttribute> itAtts = atts.iterator();
					while (itAtts.hasNext()){
						GenericAttribute a = itAtts.next();
						if (!a.getValue().equals(a.getDefaultValue())){
							//si el atributo no tiene el valor por defecto
							//implica que quiero que tenga uno concreto
							//(estoy filtrandolo)
//							Domain d = a.getDomain();
//							if (d instanceof IntegerDomain){
//								IntegerDomain aux = (IntegerDomain) d;
//								aux.ge
//							}
							//TODO revisar este cambio...
							if (a.getValue() instanceof Integer){
								Integer aux = (Integer) a.getValue();
								conf.addElement(a, aux);
							}	
						}
					}
				}

				Iterator<GenericAttributedFeature> it2 = excludeFeats2.iterator();
				while (it2.hasNext()) {
					GenericAttributedFeature f = it2.next();
					conf.addElement(f, 0);
				}

				r.applyStagedConfiguration(conf);
				res = r.ask(vq);
				if (vq.isValid()) {
					valid = true;
				}
				r.unapplyStagedConfigurations();
			}
		}

		return res;

	}

	public abstract SetQuestion setQuestionFactory();

	public abstract FilterQuestion filterQuestionFactory();

	public abstract ValidQuestion validQuestionFactory();

	public abstract Collection<? extends GenericAttributedFeature> getAllFeatures();

	public abstract PerformanceResult performanceResultFactory();

}
