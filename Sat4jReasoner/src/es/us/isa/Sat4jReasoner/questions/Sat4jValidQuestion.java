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

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.Sat4jReasoner.*;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;

public class Sat4jValidQuestion extends Sat4jQuestion implements ValidQuestion {
	/**
	 * @uml.property  name="valid"
	 */
	private boolean valid;
	
	public Sat4jValidQuestion() {
		valid = false;
	}
	
	/**
	 * @return
	 * @uml.property  name="valid"
	 */
	public boolean isValid() {
		return valid;
	}
	
	// Answer the question
	public PerformanceResult answer(Reasoner r) {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		Sat4jResult res = new Sat4jResult();
		InputStream cnfFilePath = ((Sat4jReasoner) r).getStream();
		ISolver solver = SolverFactory.instance().defaultSolver();
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(solver);
		Sat4jReasoner sr=(Sat4jReasoner)r;
		res.setClauses(sr.getClauses().size());
		res.setVariables(sr.getVariables().size());
		try {
			IProblem problem = reader.parseInstance(cnfFilePath);
			long before = System.currentTimeMillis();
			
			if (problem.isSatisfiable())
				valid = true;
			
			long time = System.currentTimeMillis() - before;
			
			// Save results
			res.setTime(time);
			res.fillFields(solver.getStat());
			
		} catch (FileNotFoundException e) {
			System.out.println("SatValidQuestion : The file " + cnfFilePath + " wasnt found.");
			res = null;
		} catch (ParseFormatException e) {
			System.out.println("SatValidQuestion : Parse error in " + cnfFilePath + ": " + e.getMessage() + ". Check the sintax, please");
			res = null;
		} catch (IOException e) {
			System.out.println("SatValidQuestion : IOException: " + e.getMessage());
			res = null;
		} catch (ContradictionException e) {
			System.out.println("SatValidQuestion : UnSatisfiable (trivial)!");
			res = new Sat4jResult();
		} catch (TimeoutException e) {
			System.out.println("SatValidQuestion : Timeout, sorry!");
			res = null;
		} 
		
		return res;
	}
	
	public String toString() {
		if (valid)
			return "Feature model is valid";
		else
			return "Feature model is not valid";
	}
}
