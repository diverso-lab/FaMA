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
package es.us.isa.generator;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.utils.BettyException;
/**
 * Interface of all FM generators.
 */
public interface IGenerator {
	
	/**
	 * Generate a variability model with the characteristic specified as input.
	 * 
	 * @param ch
	 *            The preferences of the user for the generation.
	 *            
	 * @return a variability model.
	 * @throws BettyException 
	 */
	VariabilityModel generateFM(Characteristics ch) throws BettyException;
	void resetGenerator(Characteristics ch) throws BettyException;
}
