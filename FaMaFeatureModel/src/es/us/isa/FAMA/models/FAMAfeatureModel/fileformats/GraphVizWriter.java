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
import java.util.Iterator;
import java.util.List;

import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;

public class GraphVizWriter implements IWriter {
	String graph="";
	public void writeFile(String fileName, VariabilityModel vm)
			throws Exception {
		FAMAFeatureModel fm=(FAMAFeatureModel)vm;
		
		FileWriter out = new FileWriter(fileName);
		
		graphIt(fm);
		out.write(graph);
		out.flush();
		out.close();
			
		
	}

	private void graphIt(FAMAFeatureModel vm) {
		// =================================== Graph ===============================
		//this.graph += "node [shape=box, style=filled, fillcolor=lightgray]; \n";	// Make nodes rectangles
		//this.graph += "node [shape=box, width=0.7, height=0.3, style=filled, fillcolor=\"#E3E9FF\"]; \n";
		//this.graph += "node [shape=box, width=0.7, height=0.3]; \n";
		//this.graph += "node [style=filled, fillcolor=lightgray]; \n";
		// =========================================================================
		graph="digraph G  { \n edge [dir=none]; \n";
		graph += "node [shape=box, width=0.7, height=0.3,style=filled, fillcolor=lightgray]; \n";

		Feature root=vm.getRoot();
		generateGraphFeature(root);
		generateExcludesRequieres(vm);
		graph+="}";

	}
	private void generateGraphFeature(Feature current) {
		Iterator<Relation> itr = current.getRelations();
		while(itr.hasNext()){
			Relation r = (Relation) itr.next();
			if(r.getNumberOfDestination() == 1){
				generateGraphRelation(current, r, "binaryRelation");
			}else{
				generateGraphRelation(current, r, "setRelation");
			}
		}
	}
	private void generateGraphRelation(Feature current,Relation r,String type) {
		if(type.equals("binaryRelation")){
			Feature dest=r.getDestinationAt(0);
			if(r.isMandatory()){
			// =================================== Graph ===============================
			graph = graph +"\"" +current.getName() + "\"" +" -> " +"\"" + dest.getName() +"\"" + ":n"+ "[arrowhead=\"dot\"]; ";
			// =========================================================================
			}
			if(r.isOptional()){
			// =================================== Graph ===============================
			graph = graph + "\"" +current.getName() + "\"" +" -> " +"\"" + dest.getName() +"\"" + ":n"+ "[arrowhead=\"odot\"]; ";
			// =========================================================================
			}
			generateGraphFeature(dest);

		}
		if(type.equals("setRelation")){
			
			List<Integer> cardinalidades=new ArrayList<Integer>();
			Iterator<Cardinality> cardIt=r.getCardinalities();
			while(cardIt.hasNext()){
				Cardinality card=cardIt.next();
				cardinalidades.add(new Integer(card.getMin()));
				cardinalidades.add(new Integer(card.getMax()));
			}
			
			//It its an OR
			//It is an alternative
			if((cardinalidades.get(cardinalidades.size()-1).intValue()==1)&&cardinalidades.get(0)==1){
				// =================================== Graph ===============================
				graph = graph +"subgraph \"cluster_"+ r.getName() +"\"" +" {\n";
				String graphCon = new String();
				// =========================================================================
				//Then it's needed to see the children
			
				Iterator<Feature> childrenIt=r.getDestination();
				while (childrenIt.hasNext()){
					Feature child=childrenIt.next();
					// =================================== Graph ===============================
					graph = graph + "\"" +child.getName() + "\"" +";";
					graphCon = graphCon + "\"" +current.getName() + "\"" +" -> " +"\"" + child.getName() +"\"" + ":n" + ";";
					// =========================================================================
					generateGraphFeature(child);
				}
				// =================================== Graph ===============================
				graph = graph + "label = \"ALT-" + r.getNumberOfDestination() + "\";}"
						+ graphCon;
				// =========================================================================

			}else{
				// =================================== Graph ===============================
				graph = graph  +"subgraph \"cluster_"  +r.getName() +"\"" + " {\n";
				String graphCon = new String();
				// =========================================================================
				//Then it's needed to see the children
			
				Iterator<Feature> childrenIt=r.getDestination();
				while (childrenIt.hasNext()){
					Feature child=childrenIt.next();
					// =================================== Graph ===============================
					graph = graph + "\"" +child.getName() +"\"" + ";";
					graphCon = graphCon + "\"" +current.getName() + "\"" +" -> " + "\"" +child.getName() +"\"" + ":n" + ";";
					// =========================================================================
					generateGraphFeature(child);
				}
				// =================================== Graph ===============================
				graph = graph + "label = \"OR-" +r.getNumberOfDestination()  + "\";}"
						+ graphCon;
				// =========================================================================

			}
			
			}

		
	}
	private void generateExcludesRequieres(FAMAFeatureModel fm) {
		Iterator<Dependency> it = fm.getDependencies();
		while(it.hasNext()){
			Dependency d =  it.next();
			if(d instanceof ExcludesDependency){
				// =================================== Graph ===============================
				graph = graph + "edge [dir=none] " + "\"" +d.getOrigin().getName()
						+"\"" + " -> " + "\"" +d.getDestination().getName()
						+"\"" + "[color=\"red\",label=\"E\", dir=\"both\"]; ";
				// =========================================================================
				
			}else if(d instanceof RequiresDependency){
				// =================================== Graph ===============================
				graph = graph + "edge [dir=forward] " + "\"" +d.getOrigin().getName()
						+ "\"" +" -> " + "\"" +d.getDestination().getName()
						+ "\"" +"[color=\"blue\",label=\"D\"]; ";
				// =========================================================================

			}		
		}

	}

}
