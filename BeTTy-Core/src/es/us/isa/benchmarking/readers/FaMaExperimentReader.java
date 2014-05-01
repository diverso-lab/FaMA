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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.csvreader.CsvReader;


public class FaMaExperimentReader implements IFaMaExperimentReader {
	private Collection<String> questions = new ArrayList<String>();
	private Collection<String> reasoners = new ArrayList<String>();
	String justReadedString = "";

	public Collection<String> getQuestions(String path) {
		if (!path.equals(justReadedString)) {
			read(path);
			this.justReadedString=path;
	}
		return this.questions;

	}

	@Override
	public Collection<String> getReasoners(String path) {
		if (!path.equals(justReadedString)) {
			read(path);
			this.justReadedString=path;
		}
		return this.reasoners;

	}

	private void read(String patch) {
		reasoners.clear();
		questions.clear();
		File fichero = new File(patch);
		FileReader freader = null;
		try {
			freader = new FileReader(fichero);
		} catch (FileNotFoundException e) {
			System.err.println("File " + patch + " not found");
			e.printStackTrace();
		}
		CsvReader reader = new CsvReader(freader, ';');
		try {
			if (reader.readHeaders()) {
				String[] headers = reader.getHeaders();
				System.out
						.println("------- CABECERAS DEL FICHERO ------------");
				for (int i = 0; i < headers.length; i++) {
					System.out.println(headers[i]);
				}
				System.out
						.println("-----------------------------------------------------");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			while (reader.readRecord()) {

				String reasoner = reader.get("reasoners");
				String question = reader.get("questions");
				reasoners.add(reasoner);
				questions.add(question);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		reader.close();

	}
}
