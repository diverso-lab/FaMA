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
package es.us.isa.FAMA.models.featureModel;

/**
 * It represents a generic features that can be instantiate in any Variability element.
 */
public abstract class GenericFeature extends BoundedElement {	
	
	/* Others *************************************************************/
	public String toString() {
		return name;
	}

	/*
	 * @Override
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
			GenericFeature f = (GenericFeature)obj;
			return this.name.equalsIgnoreCase(f.getName());
	
	}
	
}
