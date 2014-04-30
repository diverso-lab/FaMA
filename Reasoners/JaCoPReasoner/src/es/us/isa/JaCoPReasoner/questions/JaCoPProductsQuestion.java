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
package es.us.isa.JaCoPReasoner.questions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import JaCoP.core.FDV;
import JaCoP.core.FDstore;
import JaCoP.core.Variable;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMax;

import JaCoP.search.Search;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleSelect;
import JaCoP.search.SolutionListener;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.JaCoPReasoner.JaCoPQuestion;
import es.us.isa.JaCoPReasoner.JaCoPReasoner;
import es.us.isa.JaCoPReasoner.JaCoPResult;

/**
 * @author   Dani, malawito
 */
public class JaCoPProductsQuestion extends JaCoPQuestion implements ProductsQuestion {
	private List<Product> products;
	
	public JaCoPProductsQuestion() {
		products = new LinkedList<Product>();
	}
	
	public long getNumberOfProducts() {
		return products.size();
	}

	public Collection<Product> getAllProducts() {
		return products;
	}

	public List<Product> getProducts(){
		return products;
	}
	
	
	/* (non-Javadoc)
	 * @see tdg.SPL.Reasoner.JaCoP.JaCoPQuestion#preAnswer(tdg.SPL.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(JaCoPReasoner r) {
		products = new LinkedList<Product>();
	}

	/* (non-Javadoc)
	 * @see tdg.SPL.Reasoner.JaCoP.JaCoPQuestion#answer(tdg.SPL.Reasoner.Reasoner)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PerformanceResult answer(JaCoPReasoner jacop)  {
		if(jacop==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		JaCoPResult res = new JaCoPResult();
		if (jacop.consistency()) {
			FDstore store = jacop.getStore();
			ArrayList<FDV> vars = jacop.getVariables();
			
			
			store.consistency();
			//store.print();
			Search sa = new DepthFirstSearch();

			sa.getSolutionListener().searchAll(true);
			sa.getSolutionListener().recordSolutions(true);
			
			SelectChoicePoint select = new SimpleSelect(vars.toArray(new FDV[1]),heuristics,
					 new IndomainMax() );
			
			
			sa.labeling(store,select);
			sa.setOptimize(true);
			//sa.printAllSolutions();
			
			
			SolutionListener sl=sa.getSolutionListener();
			
			Map mapa=new HashMap();
			Variable[] variables=sa.getVariables();
			int[][] resultados=sl.getSolutions();
			for(int i=0;i<variables.length;i++){
				List results = new ArrayList();
				for(int j=0;j<sl.solutionsNo();j++){
					results.add(resultados[j][i]);
				}
				mapa.put(variables[i],results);
			}
			processMap(jacop,mapa,sl.solutionsNo());
			res.fillFields(sa);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	private void processMap(JaCoPReasoner jacop,Map sols,long numSols)  {
		if((jacop==null||sols==null)){
			throw new FAMAParameterException("Reasoner or sols :Not specified");
		}
		// first step is caching every variable oand its correspondent features instance from the fm
		Map <Variable,GenericFeature> varFeat = new HashMap<Variable,GenericFeature>();
		Iterator it1 = sols.keySet().iterator();
		while (it1.hasNext()) {
			//WHY ARE THERE ANY RELATIONS INSIDE sols?
			Variable var = (Variable)it1.next();
			GenericFeature f = jacop.searchFeatureByName(var.id());
			if (f != null){
				varFeat.put(var,f);
			}
			
		}
		
		// now we are calculating the products in each product. Each key in the map is a variable
		// each one has a list of values. The first position in the list is the value the variable takes
		// for the first solution. The second position, the value for the second solution and so on.
		for (long i = 0; i < numSols; i++) {
			Iterator it2 = sols.entrySet().iterator();
			Product p = new Product();
			while (it2.hasNext()) {
				Map.Entry e = (Map.Entry)it2.next();
				int card = ((Integer)((List)e.getValue()).get((int)i)).intValue();
				while ( card > 0 ) {
					GenericFeature f = varFeat.get(e.getKey());
					if (f != null){
						p.addFeature(f);
					}
					
					card--;
				}
			}
			products.add(p);
		}
	}
	
	public String toString() {
		String res =  "List of Products:\n";
		
		if ( products.size() == 0)
			res = "No products found";
		
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
//					res += ((GenericFeature)p.getFeature(j)).toString();
//				} catch (IndexOutOfBoundsException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if ( j != (featureNumber - 1))
//					res += ",";
//			}
			res += "}\n";
		}
		
		return res;
	}
}
