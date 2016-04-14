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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.StringTokenizer;

import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.Product;

public class ProductWriterReader {

	public void saveProduct(String path,Product product) {
		FileOutputStream out; // declare a file output object
		PrintStream p; // declare a print stream object

		try {
			// Create a new file output stream
			// connected to "myfile.txt"
			out = new FileOutputStream(path);

			// Connect print stream to the output stream
			p = new PrintStream(out);

			p.println(product.toString());
			p.flush();
			p.close();
		} catch (Exception e) {
			System.err.println("Error writing to file");
		}
	}

	public Product restoreProduct(String path) {
		Product p = new Product();
		try {
			// Open the file that is the first
			// command line parameter
			BufferedReader bf = new BufferedReader(new FileReader("datos.txt"));
			String readedLine = bf.readLine();
			StringTokenizer tokenizer = new StringTokenizer(readedLine,";");
			while(tokenizer.hasMoreTokens()){
				String token = tokenizer.nextToken();
				p.addFeature(new Feature(token));
			}


		} catch (Exception e) {
			System.err.println("File input error");
		}
		return p;
	}
}
