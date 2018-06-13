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
package es.us.isa.ChocoReasoner;

import java.util.HashMap;
import java.util.Map;

import choco.kernel.solver.Solver;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;

public class ChocoResult extends PerformanceResult {

	protected int depth = 0;
	protected int nodes = 0;
	protected long backtracks = 0;

	private final static String header = "Choco:";

	public ChocoResult() {
	}

	public void fillFields(Solver s) {
		AbstractGlobalSearchStrategy S2 = s.getSearchStrategy();
		if (S2.getNodeCount() > 0) {
			nodes = S2.getNodeCount();
		}
//		S2.get
//		if (S2.getLoggingMaxDepth() > 0) {
//			depth = S2.getLoggingMaxDepth();
//		}
		if (S2.getTimeCount() > 0) {
			time = S2.getTimeCount();
		}
		if (S2.getBackTrackCount() > 0) {
			backtracks = S2.getBackTrackCount();
		}
	}

	public void addFields(Solver s) {
		AbstractGlobalSearchStrategy S2 = s.getSearchStrategy();
		if (S2.getNodeCount() > 0) {
			nodes += S2.getNodeCount();
		}
//		if (S2.getLoggingMaxDepth() > 0) {
//			depth += S2.getLoggingMaxDepth();
//		}
		if (S2.getTimeCount() > 0) {
			time += S2.getTimeCount();
		}
		if (S2.getBackTrackCount() > 0) {
			backtracks += S2.getBackTrackCount();
		}
	}

	public void addFields(PerformanceResult res) {
		ChocoResult c = (ChocoResult) res;
		if (c.nodes > 0) {
			nodes = c.nodes;
		}
		if (c.depth > 0) {
			depth = c.depth;
		}
		if (c.time > 0) {
			time = c.time;
		}
		if (c.backtracks > 0) {
			backtracks = c.backtracks;
		}

	}

	@Override
	public String toString() {
		return "Depth: " + depth + ". Nodes: " + nodes + ". Backtracks: "
				+ backtracks + ". Time: " + time;
	}

	@Override
	public Map<String, String> getResults() {
		Map<String, String> res = new HashMap<String, String>();
		res.put(header + "time", String.valueOf(time));
		res.put(header + "depth", String.valueOf(depth));
		res.put(header + "nodes", String.valueOf(nodes));
		res.put(header + "backtracks", String.valueOf(backtracks));
		return res;
	}

}
