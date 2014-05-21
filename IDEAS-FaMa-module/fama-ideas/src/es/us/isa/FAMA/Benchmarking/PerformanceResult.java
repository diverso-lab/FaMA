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
package es.us.isa.FAMA.Benchmarking;

import java.util.Map;



/**
 * This class is the class that will recollect the performance results of the reasoner.
 */
public abstract class PerformanceResult {

	protected long time;
	
	public void setTime(long time){
		this.time = time;
	}
	

	public long getTime() {
		return time;
	}
	
	public abstract void addFields(PerformanceResult res);
	public abstract Map<String,String> getResults();
}
