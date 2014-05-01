/**
 * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.benchmarking;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.generator.Characteristics;

public class RandomExperiment extends Experiment{

	private Characteristics ch;
	
	public RandomExperiment(String n, VariabilityModel model, Characteristics ch2) {
		super(n, model);
		ch = ch2;
	}

	public RandomExperiment(String experimentName,
			Characteristics caracteristica) {
		super(experimentName);
		ch = caracteristica;
	
	}
	
	public RandomExperiment(){
		super();
	}
	public RandomExperiment(String p){
		super(p);
	}
	public Characteristics getCharacteristics() {
		return ch;
	}

	public void setCharacteristics(Characteristics ch) {
		this.ch = ch;
	}

	
}

