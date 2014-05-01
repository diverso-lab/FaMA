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
package es.us.isa.benchmarking.readers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.generator.Characteristics;
import es.us.isa.utils.BettyException;

public interface IExperimentReader {
	public Collection<Characteristics> read(String path) throws FileNotFoundException, IOException, FAMAParameterException, BettyException;

}
