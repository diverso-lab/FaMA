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
package es.us.isa.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.GraphVizWriter;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.PlainWriter;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.SPLXWriter;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.X3DWriter;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.XMLWriter;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.benchmarking.writers.AttributedWriter;

/**
 * This is a util class, it allows to save feature models in different formats
 * depending on the extension used
 */
public class FMWriter {
	/**
	 * this class save a fm into the desired format described by the extension
	 * of the path
	 * 
	 * @param fm
	 *            the feature model to be saved
	 * @param path
	 *            The path to be used
	 * @throws Exception
	 */
	public void saveFM(VariabilityModel fm, String path) throws Exception {

		if (fm instanceof FAMAAttributedFeatureModel && path.endsWith(".afm")) {
			AttributedWriter writer = new AttributedWriter();
			writer.writeFile(path, fm);
		} else if(fm instanceof FAMAAttributedFeatureModel && !path.endsWith(".afm")){
			//Syso we should do nothing
		} else {
			if (path.endsWith(".splx")) {
				SPLXWriter writer = new SPLXWriter();
				writer.writeFile(path, fm);
			} else if (path.endsWith(".xml")) {
				XMLWriter writer = new XMLWriter();
				writer.writeFile(path, fm);
			} else if (path.endsWith(".afm")) {
				PlainWriter writer = new PlainWriter();
				writer.writeFile(path, fm);
			} else if (path.endsWith(".dot")) {
				GraphVizWriter writer = new GraphVizWriter();
				writer.writeFile(path, fm);
			} else if (path.endsWith(".x3d")) {
				X3DWriter writer = new X3DWriter();
				writer.writeFile(path, fm);
			} else {
				System.err.println("The file is not supported");
			}
		}
	}

	/**
	 * This class save an attributed feature model in textual format (.afm)
	 * 
	 * @param afm
	 *            the attributed feature model to be saved.
	 * @param path
	 *            The path to be used.
	 */
	public void saveFM(FAMAAttributedFeatureModel afm, String path) {
		AttributedWriter writer = new AttributedWriter();
		try {
			writer.writeFile(path, afm);
		} catch (Exception e) {
			System.err.println("Error when saving the model in afm format: "
					+ e.getMessage());
			e.getStackTrace();

		}
	}

	/**
	 * This method will save a set of products into a path and csv format
	 * 
	 * @param products
	 *            the products to be saved
	 * @param path
	 *            The path were the products are going to be saved
	 * @throws IOException
	 */
	public void saveProducts(Collection<Product> products, String path)
			throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(path));

		Iterator<Product> productsIt = products.iterator();
		while (productsIt.hasNext()) {
			Product product = productsIt.next();
			Iterator<GenericFeature> iterator = product.getFeatures()
					.iterator();
			while (iterator.hasNext()) {
				out.write(iterator.next().getName() + ";");
			}

			out.write("\n");
		}

		out.flush();
		out.close();
	}
}
