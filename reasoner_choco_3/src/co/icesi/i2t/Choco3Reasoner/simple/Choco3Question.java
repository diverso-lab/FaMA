/**
 *  This file is part of FaMaTS.
 *
 *  FaMaTS is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FaMaTS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.icesi.i2t.Choco3Reasoner.simple;

import solver.search.strategy.strategy.AbstractStrategy;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.Reasoner;

/**
 * Super class for all Choco 3 reasoner questions.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.ChocoQuestion Choco 2 abstract question definition.
 * @version 1.0, June 2014
 */
public abstract class Choco3Question implements Question {

	/**
	 * The heuristic that should be used by the reasoner when
	 * answering the question.
	 */
	public AbstractStrategy<?> heuristic;
	
	/**
	 * Returns the class of the Choco 3 reasoner implementation for the simple feature model.
	 * 
	 * @return The class of the Choco 3 reasoner implementation for the simple feature model 
	 */
	public Class<? extends Reasoner> getReasonerClass() {
		return co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner.class;
	}
	
	/**
	 * Prepares the reasoner and other resources needed prior to answering the question.
	 * 
	 * @param reasoner The reasoner instance to solve the question.
	 */
	public abstract void preAnswer(Reasoner reasoner);
	
	/**
	 * Answers the question using the given reasoner.
	 *  
	 * @param reasoner The reasoner instance to solve the question.
	 * @return A performance result.
	 */
	public abstract PerformanceResult answer(Reasoner reasoner);
	
	/**
	 * Releases any resources associated with answering the question.
	 * 
	 * @param reasoner The reasoner instance to solve the question.
	 */
	public abstract void postAnswer(Reasoner reasoner);
	
	/**
	 * Sets a heuristic to use.
	 * 
	 * @param heuristic The heuristic to use.
	 */
	public void setHeuristic(AbstractStrategy<?> heuristic) {
		this.heuristic = heuristic;
	}

}
