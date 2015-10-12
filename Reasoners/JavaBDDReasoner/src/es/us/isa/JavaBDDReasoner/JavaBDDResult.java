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
package es.us.isa.JavaBDDReasoner;

import java.util.HashMap;
import java.util.Map;

import net.sf.javabdd.BDDFactory;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;

/**
 * @author  Dani
 */
public class JavaBDDResult extends PerformanceResult {
	/**
	 * @uml.property  name="numVar"
	 */
	private int numVar;
	/**
	 * @uml.property  name="numNodes"
	 */
	private int numNodes;
	private int cache_size;
	private int nodes_table_size;
	
	private final static String header = "JavaBDD:";
	
	public JavaBDDResult() {
		numVar=-1;
		numNodes=-1;
		cache_size=-1;
		nodes_table_size=-1;
	}
	
	public void fillFields(BDDFactory f) {
		this.numVar = f.varNum();
		this.numNodes= f.getNodeNum();
		this.cache_size= f.getCacheSize();
		this.nodes_table_size= f.getNodeTableSize();
	}
	
	@Override
	public void addFields(PerformanceResult r) {
		JavaBDDResult res = (JavaBDDResult) r;
		this.numVar += res.getNumVar();
		this.numNodes += res.getNumNodes();
		this.cache_size += res.getCacheSize();
		this.nodes_table_size += res.getNodesTableSize();
	}
	
	/**
	 * @return
	 * @uml.property  name="numVar"
	 */
	public int getNumVar() {
		return numVar;
	}
	
	/**
	 * @return
	 * @uml.property  name="numNodes"
	 */
	public int getNumNodes() {
		return numNodes;
	}
	
	public int getCacheSize() {
		return cache_size;
	}
	
	public int getNodesTableSize() {
		return nodes_table_size;
	}
	
	public String toString() {
		String res = "Main BDD Statistics:\n" +
				     "Time (ms): " + String.valueOf(this.getTime()) + "\n" +
				     "Var number: " + String.valueOf(numVar) + "\n" +
				     "Nodes Number: " + String.valueOf(numNodes) + "\n" +
				     "Cache Size: " + String.valueOf(cache_size) + "\n" +
				     "Nodes Table Size: " + String.valueOf(nodes_table_size);
		
		return res;
	}

	@Override
	public Map<String, String> getResults() {
		Map<String, String> res = new HashMap<String, String>();
		res.put(header+"time", String.valueOf(time));
		res.put(header+"numVar", String.valueOf(numVar));
		res.put(header+"numNodes", String.valueOf(numNodes));
		res.put(header+"cache_size", String.valueOf(cache_size));
		res.put(header+"nodes_table_size", String.valueOf(nodes_table_size));
		return res;
	}
}
