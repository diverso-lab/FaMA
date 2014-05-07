/*
	This file is part of FaMaTS.

    FaMaTS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FaMaTS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.

 */
package es.us.isa.FAMA.models.FAMAfeatureModel.fileformats;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;

public class X3DWriter implements IWriter {

	public void writeFile(String fileName, VariabilityModel vm) throws IOException {
		FileWriter out = new FileWriter(fileName);
		out.write(transform(vm));
		out.flush();
		out.close();
	}
	
	private String transform(VariabilityModel vm) {
		X3DModel x3d = new X3DModel();
		FAMAFeatureModel fm = (FAMAFeatureModel)vm;
		Feature root = fm.getRoot();
		Group rootGroup = transformFeature(root);
		x3d.setRoot(rootGroup);
		rootGroup.setHeight(3.0d);
		x3d.layout();
		return x3d.toString();
	}
	
	private Group transformFeature(Feature f) {
		Group featureGroup = new Group(f.getName());
		Iterator<Relation> itr = f.getRelations();
		while (itr.hasNext()) {
			Relation r = itr.next();
			if (r.getNumberOfDestination()==1) {
				Feature child = r.getDestinationAt(0);
				Group gChild = transformFeature(child);
				if (r.isMandatory())
					gChild.setType(Group.MANDATORY_TYPE);
				else if (r.isOptional())
					gChild.setType(Group.OPTIONAL_TYPE);
				featureGroup.addChild(gChild);
			} else {
				Iterator<Cardinality> itc = r.getCardinalities();
				String cardText = "";
				while (itc.hasNext()) {
					Cardinality c = itc.next();
					cardText += c.toString();
				}
				Group gCard = new Group(cardText);
				gCard.setType(Group.CARDINALITY_NODE_TYPE);
				featureGroup.addChild(gCard);
				Iterator<Feature> itf = r.getDestination();
				while (itf.hasNext()) {
					Feature fSet = itf.next();
					Group gSet = transformFeature(fSet);
					gSet.setType(Group.SET_TYPE);
					gCard.addChild(gSet);
				}
			}
		}
		return featureGroup;
	}

}
