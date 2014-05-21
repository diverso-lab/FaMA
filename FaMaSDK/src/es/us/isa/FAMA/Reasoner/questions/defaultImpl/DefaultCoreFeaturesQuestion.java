/**
 * 	This file is part of FaMaTS.
 *
 *     FaMaTS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FaMaTS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.FAMA.Reasoner.questions.defaultImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CoreFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

public abstract class DefaultCoreFeaturesQuestion implements
		CoreFeaturesQuestion {
	private Collection<? extends GenericProduct> products;
	private Collection<GenericFeature> coreFeats = new ArrayList<GenericFeature>();

	public PerformanceResult answer(Reasoner r)  {

		PerformanceResult res = this.performanceResultFactory();
		ProductsQuestion pq= this.productsQuestionFactory();
		res=r.ask(pq);
		products=pq.getAllProducts();
		if(products.size()>0){
		Iterator<? extends GenericFeature> featsIterator = getAllFeatures()
				.iterator();
		while (featsIterator.hasNext()) {
			GenericFeature feat = featsIterator.next();
			if (estaEnTodas(feat)) {
				coreFeats.add(feat);
			}
		}}
		return res;
	}

	private boolean estaEnTodas(GenericFeature feat) {
		boolean res = true;
		Iterator<? extends GenericProduct> pIt = products.iterator();
		
		while(pIt.hasNext()&&res){
			GenericProduct p = pIt.next();
			
			Collection<VariabilityElement> features = p.getElements();
			if(!features.contains(feat)){
				res=false;
			}
		}
		
		
		return res;
	}
	public Collection<GenericFeature> getCoreFeats(){
		return this.coreFeats;
	}
	public abstract ProductsQuestion productsQuestionFactory();

	public abstract Collection<? extends GenericFeature> getAllFeatures();

	public abstract PerformanceResult performanceResultFactory();
}
