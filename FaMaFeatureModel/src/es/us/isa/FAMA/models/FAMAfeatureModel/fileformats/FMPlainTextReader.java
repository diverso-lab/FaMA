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

package es.us.isa.FAMA.models.FAMAfeatureModel.fileformats;

import java.io.File;

import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.plain.FaMaPlainTextParser;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IReader;

public class FMPlainTextReader implements IReader {

	private FaMaPlainTextParser parser;
	
	public FMPlainTextReader() {
		parser = new FaMaPlainTextParser();
	}
	
	@Override
	public boolean canParse(String fileName) {
		String extension = fileName.substring(fileName.lastIndexOf('.'));
		File f = new File(fileName);
		boolean b = (f.exists() && (extension.equalsIgnoreCase(".fmf") || extension.equalsIgnoreCase(".fm")));
		return b;
	}

	@Override
	public VariabilityModel parseFile(String fileName) throws Exception {
		return parser.parseModel(fileName);
	}

	@Override
	public VariabilityModel parseString(String data) throws Exception {
		return parser.parseModelFromString(data);
	}

}
