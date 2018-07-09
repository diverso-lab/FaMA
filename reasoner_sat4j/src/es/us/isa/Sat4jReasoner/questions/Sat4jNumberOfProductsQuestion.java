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

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.SolutionCounter;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.Sat4jReasoner.*;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;

public class Sat4jNumberOfProductsQuestion extends Sat4jQuestion implements
		NumberOfProductsQuestion {

	/**
	 * @uml.property  name="numberOfProducts"
	 */
	private long numberOfProducts;
	
	public Sat4jNumberOfProductsQuestion() {
		numberOfProducts = 0;
	}

	/**
	 * @return
	 * @uml.property  name="numberOfProducts"
	 */
	public double getNumberOfProducts() {
		return numberOfProducts;
	}
	
	public PerformanceResult answer(Reasoner r)  {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		Sat4jResult res = new Sat4jResult();
		InputStream cnfFilePath = ((Sat4jReasoner)r).getStream();
		ISolver solver = SolverFactory.instance().defaultSolver();
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(solver);
		Sat4jReasoner sr=(Sat4jReasoner)r;
		res.setClauses(sr.getClauses().size());
		res.setVariables(sr.getVariables().size());
		try {

        	// Number of products
            @SuppressWarnings("unused")
			IProblem problem = reader.parseInstance(cnfFilePath);
        	SolutionCounter solutionCounter = new SolutionCounter(solver);

        	long before=System.currentTimeMillis();
        	numberOfProducts=solutionCounter.countSolutions();
        	long time=System.currentTimeMillis() - before;
        	
			// Save results
			res.setTime(time);
			res.fillFields(solver.getStat());
            
            
		} catch (FileNotFoundException e) {
			System.out.println("SATNumberofProductsQuestion : The file " + cnfFilePath + " wasn�t found.");
			res = null;
		} catch (ParseFormatException e) {
			System.out.println("SATNumberofProductsQuestion : Parse error in " + cnfFilePath + ": " + e.getMessage() + ".Check the sintax, please");
			res = null;
		} catch (IOException e) {
			System.out.println("SATNumberofProductsQuestion : IOException: " + e.getMessage());
			res = null;
		} catch (ContradictionException e) {
			System.out.println("SATNumberofProductsQuestion : Unsatisfiable (trivial)!");
			res = null;
		} catch (TimeoutException e) {
			System.out.println("SATNumberofProductsQuestion : Timeout, sorry!");
			res = null;
		}
		
		return res;
	}
	
	public String toString() {
		return "Number of Products (SAT)= " + getNumberOfProducts();
	}


}
