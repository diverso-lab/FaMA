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

import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;

public class SPLXWriter implements IWriter {
	private Collection<Feature> processedFeatures ;
	private String res;
	private String name="";
	private String description="";
	private String creator="";
	private String email="";
	private String date="";
	private String department="";
	private String organization="";
	private String address="";
	private String phone="";
	private String website="";
	private String reference="";
	@Override
	public void writeFile(String fileName, VariabilityModel vm)
			throws Exception {
		res="";
		processedFeatures = new ArrayList<Feature>();
		res += "<feature_model name=\""+name+"\">\n<meta>\n<data name=\"description\">"+description+"</data>\n<data name=\"creator\">"+creator+"</data>\n<data name=\"email\">"+email+"</data>\n<data name=\"date\">"+date+"</data>\n<data name=\"department\">"+department+"</data>\n<data name=\"organization\">"+organization+"</data>\n<data name=\"address\">"+address+"</data>\n<data name=\"phone\">"+phone+"</data>\n<data name=\"website\">"+website+"</data>\n<data name=\"reference\">"+reference+"</data>\n</meta><feature_tree>\n";

		FAMAFeatureModel fm = (FAMAFeatureModel) vm;
		// Creamos el arbol.
		Feature root = fm.getRoot();

		res += "r: " + root.getName()+" ("+root.getName()+")";
		processFeature(root, 1);
		res += "</feature_tree>\n<constraints>\n";
		processCross(fm);
		res += "</constraints>\n</feature_model>";

		FileWriter out = new FileWriter(fileName);
		out.write(res);
		out.flush();
		out.close();
	}

	private void processCross(FAMAFeatureModel vm) {
		Iterator<Dependency> it = vm.getDependencies();
		int i = 1;
		while (it.hasNext()) {
			Dependency dep = it.next();
			String name = dep.getName();

			if (name.equals("")) {
				name = "c" + i;
				i++;
			}

			if (dep instanceof RequiresDependency) {
				res += dep.getName() + ": ~" + dep.getOrigin() + " or "
						+ dep.getDestination();
			} else if (dep instanceof ExcludesDependency) {
				res += dep.getName() + ": ~" + dep.getOrigin() + " or ~"
						+ dep.getDestination();

			}
			res += "\n";
		}

	}

	private void processFeature(Feature feat, int tab) {
		if (!processedFeatures.contains(feat)) {
			res += "\n";
			processedFeatures.add(feat);
			Iterator<Relation> relIt = feat.getRelations();
			while (relIt.hasNext()) {
				Relation rel = relIt.next();
				Cardinality card = rel.getCardinalities().next();
				Iterator<Feature> childIt = rel.getDestination();

				for (int i = 0; i < tab; i++) {
					res += "\t";
				}

				// Add the relation
				if (rel.isMandatory()) {
					res += ":m ";
					while (childIt.hasNext()) {
						Feature child = childIt.next();
						res += child.getName()+" ("+child.getName()+")";
						processFeature(child, tab + 1);
					}
				} else if (rel.isOptional()) {
					res += ":o ";
					while (childIt.hasNext()) {
						Feature child = childIt.next();
						res += child.getName()+" ("+child.getName()+")";
						processFeature(child, tab + 1);
					}
				} else {
					res += ":g [" + card.getMin() + "," + card.getMax() + "]\n";
					while (childIt.hasNext()) {
						for (int i = 0; i < tab + 1; i++) {
							res += "\t";
						}
						Feature child = childIt.next();
						res += ": " + child.getName()+" ("+child.getName()+")";
						processFeature(child, tab + 2);//FIXME check it
					}
				}

			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

}
