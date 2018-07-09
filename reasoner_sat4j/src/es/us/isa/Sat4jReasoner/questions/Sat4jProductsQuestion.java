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
package es.us.isa.Sat4jReasoner.questions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;
import es.us.isa.Sat4jReasoner.Sat4jReasoner;
import es.us.isa.Sat4jReasoner.Sat4jResult;

public class Sat4jProductsQuestion extends Sat4jQuestion implements
		ProductsQuestion {

	private List<Product> products;
	
	public Sat4jProductsQuestion() {
		products = new ArrayList<Product>();
	}
	
	public Collection<Product> getAllProducts() {
		return products;
	}

	public long getNumberOfProducts() {
		return products.size();
	}
	
	public void preAnswer(Reasoner r) {
		// Create CNF file
		super.preAnswer(r);
	}
	
	public PerformanceResult answer(Reasoner r)  {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		Sat4jResult res = new Sat4jResult();
		InputStream cnfFilePath = ((Sat4jReasoner)r).getStream();
		ISolver solver = SolverFactory.instance().defaultSolver();
        ModelIterator mi = new ModelIterator(solver);
        solver.setTimeout(3600); // 1 hour timeout
        Reader reader = new DimacsReader(mi);
        Sat4jReasoner sr=(Sat4jReasoner)r;
		res.setClauses(sr.getClauses().size());
		res.setVariables((sr.getVariables().size()));
		try {
            // All solutions
            IProblem problem = reader.parseInstance(cnfFilePath);
            long before=System.currentTimeMillis();
            while (problem.isSatisfiable()) {
            	int[] solution=problem.model();
           
            	// Save product
            	this.saveProduct(solution, r);
            	
            }
            long time=System.currentTimeMillis() - before;
	
			// Save results
			res.setTime(time);
			res.fillFields(solver.getStat());
            
            
		} catch (FileNotFoundException e) {
			System.out.println("Sat All Solutions: The file " + cnfFilePath + " wasn�t found.");
			res = null;
		} catch (ParseFormatException e) {
			System.out.println("Sat All Solutions: Parse error in " + cnfFilePath + ": " + e.getMessage() + ".Check the sintax, please");
			res = null;
		} catch (IOException e) {
			System.out.println("Sat All Solutions: IOException: " + e.getMessage());
			res = null;
		} catch (ContradictionException e) {
			System.out.println("Sat All Solutions: UnSatisfiable (trivial)!");
			res = null;
		} catch (TimeoutException e) {
			System.out.println("Sat All Solutions: Timeout, sorry!");
			res = null;
		}
		
		return res;
	}
	
	// Translate a Sat4j solution in a product.
	private void saveProduct(int[] solution, Reasoner r)  {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		Product p= new Product();
		for (int i=0;i<solution.length;i++) {
			if (solution[i] > 0) {
				GenericFeature feature =((Sat4jReasoner)r).getFeature(Integer.toString(i+1));
				
				p.addFeature(feature);
			}
		}
		products.add(p);
	}

	public String toString()  {
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
//					res += (p.getFeature(j)).toString();
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
