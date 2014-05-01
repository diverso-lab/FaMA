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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.Product;

public class ProductReader {

	String path;
	File file;
	BufferedReader reader;
	StringTokenizer token;

	

	public ProductReader() {
	}

	public Product getProduct(String path) throws IOException{
		this.path = path;
		file = new File(path);
		reader = new BufferedReader(new FileReader(file));
		Product p = new Product();
		if (reader.ready()) {
			token = new StringTokenizer(reader.readLine(), ";");
			while (token.hasMoreElements()) {
				p.addFeature(new Feature((String) token.nextElement()));
			}
		}
		reader.close();
		
		return p;
	}	
	
	
}
