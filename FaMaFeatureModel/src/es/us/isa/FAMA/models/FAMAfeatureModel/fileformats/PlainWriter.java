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

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;

public class PlainWriter implements IWriter {
	private String res = "%Relationships\n";
	private Collection<Feature> processedFeatures = new ArrayList<Feature>();

	@Override()
	public void writeFile(String fileName, VariabilityModel vm)
			throws Exception {
		FAMAFeatureModel fm = (FAMAFeatureModel) vm;
		processTree(fm.getRoot());
		processCross(fm);
		FileWriter out = new FileWriter(fileName);
		out.write(res);
		out.flush();
		out.close();
	}

	private void processTree(Feature feat) {
		if (!processedFeatures.contains(feat)&&!isLeaf(feat)) {
			processedFeatures.add(feat);
			res += transformString(feat.toString()) + " : ";
			Iterator<Relation> relIt = feat.getRelations();
			while (relIt.hasNext()) {
				Relation rel = relIt.next();
				Cardinality card = rel.getCardinalities().next();
				Iterator<Feature> childIt = rel.getDestination();

				// Add the relation
				if (rel.isMandatory()) {
					while (childIt.hasNext()) {
						Feature child = childIt.next();
						res += transformString(child.getName()) + " ";
					}
				} else if (rel.isOptional()) {
					while (childIt.hasNext()) {
						Feature child = childIt.next();
						res += "[" + transformString(child.getName()) + "] ";
					}
				} else {
					res += "[" + card.getMin() + "," + card.getMax() + "] {";
					while (childIt.hasNext()) {
						Feature child = childIt.next();
						res += " " + transformString(child.getName());
					}
					res += " } ";
					
				}
				
			}
			res+=";\r\n";
			relIt = feat.getRelations();
			while (relIt.hasNext()) {
				Relation rel = relIt.next();
				Iterator<Feature> childIt = rel.getDestination();
				childIt = rel.getDestination();
				while (childIt.hasNext()) {
					processTree(childIt.next());
				}
			}
		}
	}

	private boolean isLeaf(Feature feat) {
		return feat.getNumberOfRelations() ==0;
	}

	private void processCross(FAMAFeatureModel vm) {
		res += "%Constraints\n";

		Iterator<Dependency> it = vm.getDependencies();

		while (it.hasNext()) {
			Dependency dep = it.next();

			if (dep instanceof RequiresDependency) {
				res +=  transformString(dep.getOrigin().getName())
						+ " REQUIRES " 
						+ transformString(dep.getDestination().getName())
						+ ";\n";
			} else if (dep instanceof ExcludesDependency) {
				res += transformString(dep.getOrigin().toString())
						+ " EXCLUDES "
						+ transformString(dep.getDestination().getName())
						+ ";\n";

			}
		}

	}

	private String transformString(String str) {
//		String res = "";
//		StringTokenizer stTexto = new StringTokenizer(str);
//
//		while (stTexto.hasMoreElements()) {
//
//			String tmp = stTexto.nextElement().toString().toLowerCase();
//			String first = tmp.substring(0, 1);
//			res += (first.toUpperCase() + tmp.substring(1)).replaceAll("\\,",
//					"").replaceAll("\\+", "plus").replaceAll("\\.", "")
//					.replaceAll("\\/", "").replaceAll("\\(", "").replaceAll(
//							"\\)", "").replaceAll("-", "")
//					.replaceAll("\\�", "").replaceAll("\\&", "").replaceAll(
//							":", "").replaceAll("\\'", "").replaceAll("#","");
//		}
//		res="\'"+str+"\'";
		return str;
	}
}
