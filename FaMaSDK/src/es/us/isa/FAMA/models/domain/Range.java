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
package es.us.isa.FAMA.models.domain;


/**
 * Represents a Range
 */
public class Range {

	private float max;
	
	private float min;

	private float delta;
	
	public Range(float min, float max) {
		this.max = max;
		this.min = min;
	}

	public int getMax() {
		return Float.floatToIntBits(max);
	}

	public int getMin() {
		return Float.floatToIntBits(min);
	}

	public float getMaxFloat() {
		return max;
	}

	public float getMinFloat() {
		return min;
	}
	
	public void setMax(float max) {
		this.max = max;
	}

	public void setMin(float min) {
		this.min = min;
	}
	
	public boolean isInRange(float i){
		if (i <= max && i >= min){
			return true;
		}
		else{
			return false;
		}
	}

	public float getDelta() {
		return delta;
	}

	public void setDelta(float delta) {
		this.delta = delta;
	}
	
	
}
