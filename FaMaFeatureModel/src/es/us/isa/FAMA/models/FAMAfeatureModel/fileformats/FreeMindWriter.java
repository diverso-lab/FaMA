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
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.XmlReader;


import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;
import es.us.isa.FAMA.models.variabilityModel.parsers.WrongFormatException;

public class FreeMindWriter implements IWriter {
	private String res = "";
	private Map<Feature, Integer> processedFeatures = new HashMap<Feature, Integer>();
	private int lastint = 0;
	private FAMAFeatureModel fm;
	Random random= new Random(); 
	@Override
	public void writeFile(String fileName, VariabilityModel vm)
			throws Exception {
		res += "<map version=\"0.9.0\">\n<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->";
		fm = (FAMAFeatureModel) vm;
		lastint = 600971715;//to start the ids

		firstPass(fm.getRoot());
		processFeature(fm.getRoot(), "STYLE=\"bubble\"","[1,1]");

		res += "</map>";// close the node and the map
		FileWriter out = new FileWriter(fileName);
		out.write(res);
		out.flush();
		out.close();
	}

	private void firstPass(Feature feat) {
		lastint += 100;
		processedFeatures.put(feat, lastint);
		Iterator<Relation> relIt = feat.getRelations();
		while (relIt.hasNext()) {
			Relation rel = relIt.next();
			Iterator<Feature> childIt = rel.getDestination();
			while (childIt.hasNext()) {
				Feature child = childIt.next();
				firstPass(child);
			}

		}

		
	}

	private void processFeature(Feature feat, String atts,String card) {
		res += "\n";

		res += "<node CREATED=\"1262649931674\" ID=\"ID_"
				+ processedFeatures.get(feat)
				+ "\" MODIFIED=\"1262650295183\" TEXT=\""
				+ card+feat.getName().trim().replaceAll("&", "") + "\"" + atts + ">";
		if (isOriginCTC(feat)) {
			Iterator<Dependency> it = fm.getDependencies();
			while (it.hasNext()) {
				Dependency dep = it.next();
				if (dep.getOrigin().equals(feat)) {
					lastint += 100;
					if(dep instanceof RequiresDependency){
					res += "<arrowlink COLOR=\"#000000\" DESTINATION=\"ID_"
							+ processedFeatures.get(dep.getDestination())
							+ "\" ENDARROW=\"Default\" ID=\"Arrow_ID_"
							+ lastint + "\" STARTARROW=\"None\"/>\n";
					}else if (dep instanceof ExcludesDependency){
					res += "<arrowlink COLOR=\"#000000\" DESTINATION=\"ID_"
								+ processedFeatures.get(dep.getDestination())
								+ "\" ENDARROW=\"Default\" ID=\"Arrow_ID_"
								+ lastint + "\" STARTARROW=\"Default\"/>\n";	
					}
				}
			}

		}
		Iterator<Relation> relIt = feat.getRelations();
		while (relIt.hasNext()) {
			Relation rel = relIt.next();
			String cardinalidad="";
			Iterator<Cardinality> cardIt=rel.getCardinalities();
			while(cardIt.hasNext()){
				cardinalidad+=cardIt.next().toString()+" ";
			}
			
			Iterator<Feature> childIt = rel.getDestination();
			int color =random.nextInt(16777215);
			while (childIt.hasNext()) {
				Feature child = childIt.next();
				processFeature(child, "BACKGROUND_COLOR=\"#"+Integer.toHexString(color)+"\"",cardinalidad);
			}

		}

		res += "</node>";
	}

	private boolean isOriginCTC(Feature feat) {
		Iterator<Dependency> it = fm.getDependencies();
		while (it.hasNext()) {
			if (it.next().getOrigin().equals(feat)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		XMLReader reader = new XMLReader();
		FreeMindWriter writer = new FreeMindWriter();
		FAMAFeatureModel fm = (FAMAFeatureModel) reader.parseFile("./test.xml");
		writer.writeFile("./MetaH-FM.mm", fm);
	}

}
