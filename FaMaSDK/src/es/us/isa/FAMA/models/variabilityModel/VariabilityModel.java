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
package es.us.isa.FAMA.models.variabilityModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.errors.Observation;
import es.us.isa.FAMA.errors.Observation.ErrorLevel;
import es.us.isa.FAMA.models.variabilityModel.transformations.ITransform;

/**
 * @author Pablo Trinidad
 * @date 05/02/08
 * 
 */

public abstract class VariabilityModel {
	// observer is a set to avoid repeated ITransform instances
	private Set<ITransform> observer;
	
	public VariabilityModel() {
		observer = new HashSet<ITransform>();
	}
	
	public void attach (ITransform transform) {
		observer.add(transform);
	}
	
	public void detach (ITransform transform) {
		observer.remove(transform);
	}
	
	public void transformTo (Reasoner reasoner) {
		for (ITransform t:observer) {
			t.transform(this, reasoner);
		}
	}
	
	public abstract Collection<? extends VariabilityElement> getElements();
	public Collection<Observation> getObservations()  {
		return getObservations(ErrorLevel.INFO);
	}
	
	public Collection<Observation> getObservations(ErrorLevel minLevel)  {
		Collection<Observation> res = new HashSet<Observation>();
		return res;
	}
}
