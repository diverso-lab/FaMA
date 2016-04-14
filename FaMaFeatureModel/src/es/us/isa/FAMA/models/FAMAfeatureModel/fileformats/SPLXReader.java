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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import constraints.PropositionalFormula;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IReader;
import fm.FeatureGroup;
import fm.FeatureModel;
import fm.FeatureTreeNode;
import fm.SolitaireFeature;
import fm.XMLFeatureModel;

public class SPLXReader implements IReader {

	Map<String, Feature> string2features = new HashMap<String, Feature>();
	String fileRoute = "./tests/REAL-FM-6.xml";
	FAMAFeatureModel fm = new FAMAFeatureModel();

	@Override
	public boolean canParse(String fileName) {
		
		return true;
	}

	@Override
	public VariabilityModel parseFile(String fileName) throws Exception {
		this.fileRoute=fileName;
		parse();
		return fm;
	}

	@Override
	public VariabilityModel parseString(String data) throws Exception {
		return null;//FIXME
	}

	public void parse() {

		// Create a FaMaEmpty proyect

		FeatureModel featureModel = new XMLFeatureModel(fileRoute,
				XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
		featureModel.loadModel();

		// A feature model object contains a feature tree and a set of
		// constraints
		// Let's traverse the feature tree first. We start at the
		// root feature in depth first search.
		Feature root = new Feature(featureModel.getRoot().getID());
		string2features.put(featureModel.getRoot().getID(), root);
		fm.setRoot(root);
		traverseDFS(featureModel.getRoot(), root);

		// Now, let's traverse the extra constraints as a CNF formula
		System.out.println("EXTRA CONSTRAINTS ---------------------------");
		traverseConstraints(featureModel);
		System.out.println(fm);
	}

	public void traverseDFS(FeatureTreeNode node, Feature parent) {

		for (int i = 0; i < node.getChildCount(); i++) {
			FeatureTreeNode childNode = (FeatureTreeNode) node.getChildAt(i);
			Feature child = new Feature(childNode.getID());
			string2features.put(childNode.getID(), child);
			if (childNode instanceof SolitaireFeature) {
				// Optional Feature
				Relation rel = new Relation();
				rel.setParent(parent);
				parent.addRelation(rel);

				if (((SolitaireFeature) childNode).isOptional()) {
					rel.addCardinality(new Cardinality(0, 1));

					// Mandatory Feature
				} else {
					rel.addCardinality(new Cardinality(1, 1));
				}
				rel.addDestination(child);
				traverseDFS(childNode, child);
			}

			// Feature Group
			else if (childNode instanceof FeatureGroup) {
				Relation rel = new Relation();
				rel.setParent(parent);
				parent.addRelation(rel);
				int minCardinality = ((FeatureGroup) childNode).getMin();
				int maxCardinality = ((FeatureGroup) childNode).getMax();

				if (maxCardinality == -1) {
					maxCardinality = childNode.getChildCount();
				}// Ellos usan el -1 como un *
				rel.setName(childNode.getID());
				rel.addCardinality(new Cardinality(minCardinality,
						maxCardinality));
				for (int j = 0; j < childNode.getChildCount(); j++) {
					FeatureTreeNode subChildNode = (FeatureTreeNode) childNode
							.getChildAt(j);
					Feature subChild = new Feature(subChildNode.getID());
					string2features.put(subChildNode.getID(), subChild);

					rel.addDestination(subChild);
					traverseDFS(subChildNode, subChild);

				}

			}
		}

	}

	public void traverseConstraints(FeatureModel featureModel) {
		for (PropositionalFormula formula : featureModel.getConstraints()) {

			String contenido = formula.getFormula();
			contenido = contenido.replace(" or ", "\n");
			StringTokenizer tokenizer = new StringTokenizer(contenido);
			int negs = 0;
			Feature feat1=null;
			Feature feat2=null;
			while (tokenizer.hasMoreTokens()) {
				String var = tokenizer.nextToken();
				if(var.contains("~")){
					negs++;
				}
				if(feat1==null){
					String featureName = var.replace("~","").replace("(", "").replace(")", "");
					feat1=string2features.get(featureName);
				}else{
					String featureName = var.replace("~","").replace("(", "").replace(")", "");
					feat2=string2features.get(featureName);
				}
			}
			if(negs==1){
				//REQUIERES
				RequiresDependency rqDep=new RequiresDependency(formula.getName());
				rqDep.setOrigin(feat1);
				rqDep.setDestination(feat2);
				fm.addDependency(rqDep);
			}else if(negs==2){
				//EXCLUDES
				ExcludesDependency exDep=new ExcludesDependency(formula.getName());
				exDep.setOrigin(feat1);
				exDep.setDestination(feat2);
				fm.addDependency(exDep);
			}else if(negs>2){
				System.out.println("Incorrects CTS");
			}
		}
	}

}
