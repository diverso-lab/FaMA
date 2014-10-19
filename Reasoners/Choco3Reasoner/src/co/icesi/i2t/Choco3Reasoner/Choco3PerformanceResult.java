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
package co.icesi.i2t.Choco3Reasoner;

import java.util.HashMap;
import java.util.Map;

import solver.Solver;
import solver.search.measure.IMeasures;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;

/**
 * Choco 3 performance result implementation.
 * Four measures are retrieved: node count, current depth, backtrack count
 * and time count.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.ChocoResult Choco 2 performance result implementation.
 * @version 1.0, June 2014
 */
public class Choco3PerformanceResult extends PerformanceResult {

	/**
	 * Performance result header indicating the reasoner it belongs to.
	 */
	private final static String HEADER = "Choco3:";
	/**
	 * The depth of the search tree.
	 */
	private long depth;
	/**
	 * The node count in the search tree.
	 */
	private long nodes;
	/**
	 * The trace count in the search tree.
	 */
	private long backtracks;
	
	/**
	 * Builds a new Choco 3 performance result instance.
	 */
	public Choco3PerformanceResult() {
		this.depth = 0;
		this.nodes = 0;
		this.backtracks = 0;
		this.time = 0;
	}
	
	/**
	 * Retrieves the four measures recorded from the Choco 3 solver while the 
	 * reasoner was solving a question and adds them to the previous stored measures.
	 * 
	 * @param solver The Choco 3 solver.
	 */
	public void addFields(Solver solver) {
		// Retrieve the measures recorded from the solver.
		IMeasures measures = solver.getMeasures();
		if (measures.getNodeCount() > 0) {
			// Get the node count.
			this.nodes += measures.getNodeCount();
		}
		if (measures.getCurrentDepth() > 0) {
			// Get the current depth of the search tree.
			this.depth += measures.getCurrentDepth();
		}
		if (measures.getBackTrackCount() > 0) {
			// Get the backtrack count.
			this.backtracks += measures.getBackTrackCount();
		}
		if (measures.getTimeCount() > 0) {
			// Get the solution time count in seconds (included initial propagation time)
			this.time += measures.getTimeCount();
		}
	}

	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Benchmarking.PerformanceResult#addFields(es.us.isa.FAMA.Benchmarking.PerformanceResult)
	 */
	@Override
	public void addFields(PerformanceResult performanceResult) {
		Choco3PerformanceResult choco3PerformanceResult = (Choco3PerformanceResult) performanceResult;
		if (choco3PerformanceResult.nodes > 0) {
			this.nodes = choco3PerformanceResult.nodes;
		}
		if (choco3PerformanceResult.depth > 0) {
			this.depth = choco3PerformanceResult.depth;
		}
		if (choco3PerformanceResult.backtracks > 0) {
			this.backtracks = choco3PerformanceResult.backtracks;
		}
		if (choco3PerformanceResult.time > 0) {
			this.time = choco3PerformanceResult.time;
		}
	}

	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Benchmarking.PerformanceResult#getResults()
	 */
	@Override
	public Map<String, String> getResults() {
		Map<String, String> results = new HashMap<String, String>();
		results.put(HEADER + "time", String.valueOf(this.time));
		results.put(HEADER + "depth", String.valueOf(this.depth));
		results.put(HEADER + "nodes", String.valueOf(this.nodes));
		results.put(HEADER + "backtracks", String.valueOf(this.backtracks));
		return results;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Depth: " + this.depth + "\n" + 
				"Nodes: " + this.nodes + "\n" + 
				"Backtracks: " + this.backtracks + "\n" + 
				"Time: " + this.time;
	}
	
	

}
