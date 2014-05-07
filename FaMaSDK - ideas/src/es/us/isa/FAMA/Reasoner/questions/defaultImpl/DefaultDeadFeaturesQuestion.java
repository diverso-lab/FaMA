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
import es.us.isa.FAMA.Reasoner.questions.DeadFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

public abstract class DefaultDeadFeaturesQuestion implements DeadFeaturesQuestion{

	private Collection<Product> products;
	private Collection<GenericFeature> allFeats;
	private Collection<GenericFeature> deadFeatures;
	public PerformanceResult answer(Reasoner r)  {
		System.err.println("If you are using FaMa for Fm, please use Error detection, that's implementation is very inefficientbut works for other VMS");
		PerformanceResult res = this.performanceResultFactory();
		ProductsQuestion pq= this.productsQuestionFactory();
		allFeats=this.getAllFeatures();
		products=(Collection<Product>) pq.getAllProducts();
		deadFeatures = new ArrayList<GenericFeature>();
		Iterator<GenericFeature> it = allFeats.iterator();
		while(it.hasNext()){
			GenericFeature feat = it.next();
			Iterator<Product> pit = products.iterator();
			boolean isDead =true;
			while(pit.hasNext()&&isDead){
				Product p = pit.next();
				if(p.getFeatures().contains(feat)){
					isDead=false;
				}
			}
			if (isDead){deadFeatures.add(feat);} 
				
		}
		return res;
	}

	public abstract PerformanceResult performanceResultFactory();
	public abstract Collection<GenericFeature> getAllFeatures();
	public abstract ProductsQuestion productsQuestionFactory();
	public Collection<GenericFeature> getDeadFeatures(){
		return this.deadFeatures;
	}

}