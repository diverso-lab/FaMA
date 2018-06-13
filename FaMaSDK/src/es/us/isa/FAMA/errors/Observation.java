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
package es.us.isa.FAMA.errors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;


import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

/**
 * An Observation, is a possible error, it's used to benefit the efficiency searching errors.
 */
public abstract class Observation {
	public enum ErrorLevel {INFO,WARNING,ERROR,CRITICAL_ERROR}
	
	private Collection<Observation> discardedObs;
	private Collection<Observation> carriedObs;
	private Map<VariabilityElement,Object> obsValuesMap;
	private ErrorLevel errorLevel;
	
	public Observation() {
		this(ErrorLevel.ERROR);
	}
	
	protected Observation(ErrorLevel errorLevel) {
		discardedObs = new LinkedList<Observation>();
		carriedObs = new LinkedList<Observation>();
		obsValuesMap = new HashMap<VariabilityElement,Object>();
		this.errorLevel = errorLevel;		
	}
	
	public void addDiscardedObs(Observation obs) {
		discardedObs.add(obs);
	}
	
	public void addCarriedError(Observation obs) {
		carriedObs.add(obs);
	}
	
	public void addObsValue(VariabilityElement ve, Object value) {
		obsValuesMap.put(ve, value);
	}
	
	public Map<? extends VariabilityElement,Object> getObservation() {
		return obsValuesMap;
	}
	
	public ErrorLevel getErrorLevel() {
		return errorLevel;
	}
	
	public Collection<Observation> getDiscardedObs() {
		return discardedObs;
	}

	public Collection<Observation> getCarriedObs() {
		return carriedObs;
	}

	public String toString() {
		String res = "";
		switch(errorLevel) {
			case INFO: res = "Information Observation: {"; break;
			case WARNING: res = "Warning Observation: {"; break;
			case ERROR: res = "Error Observation: {"; break;
			case CRITICAL_ERROR: res = "Critical Error Observation: {"; break;
			default: res = "Observation: {"; break;
		}
		Iterator<Entry<VariabilityElement,Object>> itm = obsValuesMap.entrySet().iterator();
		if (itm.hasNext()) {
			Entry<VariabilityElement,Object> entry = itm.next();
			res += entry.getKey().toString() + "=" + entry.getValue().toString();
		
			while (itm.hasNext()) {
				res += ",";
				entry = itm.next();
				res += entry.getKey().toString() + "=" + entry.getValue().toString();
			}
		}
		res += "}";
		return res;
	}
	
	abstract public Error createError() ;
}
