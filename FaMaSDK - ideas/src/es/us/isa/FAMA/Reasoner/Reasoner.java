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
package es.us.isa.FAMA.Reasoner;

import java.util.Map;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Reasoner.Factory.QuestionAbstractFactory;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

/**
 * This class is the representation of a reasoner
 */
public abstract class Reasoner {
	
	private QuestionAbstractFactory cFactory;
	
	public Reasoner() {
		
	}
	
	public QuestionAbstractFactory getFactory() {
		return cFactory;
	}
	
	public void setFactory(QuestionAbstractFactory f){
		cFactory = f;
	}
	/**
	 * 
	 * @param The question to be answered
	 * @return a Performance Result with benchmarks of the reasoner
	 */
	abstract public PerformanceResult ask (Question q)  ;
	abstract public void unapplyStagedConfigurations();
	abstract public void applyStagedConfiguration(Configuration conf);
	abstract public Map<String, Object> getHeusistics();
	abstract public void setHeuristic(Object obj);
	
}
