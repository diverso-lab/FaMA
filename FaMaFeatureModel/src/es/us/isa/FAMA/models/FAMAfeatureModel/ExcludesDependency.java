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
/*
 * Created on 15-Mar-2005
 */
package es.us.isa.FAMA.models.FAMAfeatureModel;


/**
 * @author trinidad, Manuel Nieto Ucl�s 
 */
public class ExcludesDependency extends Dependency {
	
	public ExcludesDependency(String name){
		super(name);
	}
	
	public ExcludesDependency(Feature origin, Feature destination) {		
		super(origin, destination);
	}
	
	public ExcludesDependency(String name, Feature origin, Feature destination){
		super(name, origin, destination);
	}
	
	public String toString() {
		String res = origin + " excludes " + destination;
		return res;
	}
}
