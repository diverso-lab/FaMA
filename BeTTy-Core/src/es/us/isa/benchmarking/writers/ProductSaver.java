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
package es.us.isa.benchmarking.writers;

import java.io.FileWriter;
import java.io.IOException;

import es.us.isa.FAMA.models.featureModel.Product;

public class ProductSaver {

	String path;

	public ProductSaver(String path) {
		this.path = path;
	}

	public ProductSaver() {
		// TODO Auto-generated constructor stub
	}

	
	public void saveProduct(Product p,String path) throws IOException {
		FileWriter out = new FileWriter(path);
		out.write(p.toString());
		System.out.println("generated product: "+p.toString());
		out.flush();
		out.close();
	}

}

 