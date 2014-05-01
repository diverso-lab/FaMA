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
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariantFeaturesQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

public abstract class DefaultVariantFeaturesQuestion implements
		VariantFeaturesQuestion {
	private Collection<Product> products;
	private Collection<GenericFeature> variantFeats = new ArrayList<GenericFeature>();

	public PerformanceResult answer(Reasoner r)  {

		PerformanceResult res = this.performanceResultFactory();
		ProductsQuestion pq= this.productsQuestionFactory();
		res=r.ask(pq);
		products=(Collection<Product>) pq.getAllProducts();
		Iterator<? extends GenericFeature> featsIterator = getAllFeatures().iterator();
		while (featsIterator.hasNext()) {
			GenericFeature feat = featsIterator.next();
			if (!estaEnTodas(feat)&&estaEnAlguna(feat)) {
				variantFeats.add(feat);
			}
		}
		return res;
	}

	private boolean estaEnTodas(GenericFeature feat) {
		boolean res = true;
		Iterator<Product> pIt=products.iterator();
		while(pIt.hasNext()&&res){
			Product p = pIt.next();
			if(!(p.getFeatures().contains(feat))){
				res=false;
			}
		}
		
		return res;
	}

	public Collection<GenericFeature> getVariantFeats(){
		return this.variantFeats;
	}
	
	public boolean estaEnAlguna(GenericFeature feat) {
		boolean res = false;
		Iterator<Product> pIt=products.iterator();
		while(pIt.hasNext()&&!res){
			Product p = pIt.next();
			if(p.getFeatures().contains(feat)){
				res=true;
			}
		}
		
		return res;
	}

	public abstract ProductsQuestion productsQuestionFactory();

	public abstract Collection<? extends GenericFeature> getAllFeatures();

	public abstract PerformanceResult performanceResultFactory();
}
