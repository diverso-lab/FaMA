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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.javabdd.BDDFactory;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.JavaBDDReasoner.JavaBDDQuestion;
import es.us.isa.JavaBDDReasoner.JavaBDDReasoner;
import es.us.isa.JavaBDDReasoner.JavaBDDResult;

public class JavaBDDProductsQuestion extends JavaBDDQuestion implements
		ProductsQuestion {

	private List<Product> products;
	
	public JavaBDDProductsQuestion() {
		products = new ArrayList<Product>();
	}
	
	public long getNumberOfProducts() {
		return products.size();
	}
	
	public void preAnswer(Reasoner r) {
		super.preAnswer(r);
	}

	public PerformanceResult answer(Reasoner r)  {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		JavaBDDResult res = new JavaBDDResult();
		JavaBDDReasoner bddr = (JavaBDDReasoner)r;
		
		long before=System.currentTimeMillis();	
		Iterator it=bddr.getBDD().allsat().iterator();
		while (it.hasNext()) {
			// Save product
			saveProduct((byte[])it.next(),r);
		}
		long time=System.currentTimeMillis() - before;

		// Save results
		res.setTime(time);
		res.fillFields((BDDFactory) bddr.getBDDFactory());
        
        return res;
	}
	
	private void saveProduct(byte[] sol, Reasoner r)  {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		int n=sol.length;
		Product p=new Product();
		for(int i=0;i<n;i++) {
			if (sol[i]>0) {
				JavaBDDReasoner javabddReasoner = (JavaBDDReasoner)r;
				String varName = javabddReasoner.getBDDVar(i);
				GenericFeature f = javabddReasoner.getFeatureByVarName(varName);
				if (f !=null )
					p.addFeature(f);
			}
			else if (sol[i] == -1) {
				p=null;
				
				// Include feature
				sol[i]=1;
				saveProduct(sol, r);
				
				// Discard feature
				sol[i]=0;
				saveProduct(sol,r);
				
				sol[i]=-1;
				
				break;
			}
		}
		
		if (p!=null) // if a recursive call was not made
			products.add(p);
		
	}
	
	public Collection<Product> getAllProducts() {
		return products;
	}

	public String toString() {
		String res =  "List of Products:\n";
		
		if ( products.size() == 0)
			res = "No products found";
		
		System.out.println("Preparing string...");
		for ( int i = 1;i <= products.size(); i++) {
			Product p = (Product)products.get(i-1);
			int featureNumber = p.getNumberOfFeatures();
			res += "Product " + i + ": {";
			Iterator<GenericFeature> itFeats = p.getFeatures().iterator();
			int j = 0;
			while (itFeats.hasNext()){
				GenericFeature f = itFeats.next();
				res += f.toString();
				j++;
				if ( j != (featureNumber - 1))
					res += ",";
			}
//			for ( int j = 0; j < featureNumber; j++ ) {
//				try {
//					res += p.getFeature(j).toString();
//				} catch (IndexOutOfBoundsException e) {
//					e.printStackTrace();
//				}
//				if ( j != (featureNumber - 1))
//					res += ",";
//			}
			res += "}\n";
			System.out.println("Product " + i + " ready");
		}
		
		System.out.println("String ready");
		return res;
	}

}