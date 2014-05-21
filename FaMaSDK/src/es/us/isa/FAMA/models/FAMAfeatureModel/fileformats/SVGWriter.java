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

import java.awt.Point;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.Constraint;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;

public class SVGWriter implements IWriter {
	String graph="";
	Map<Feature,Point> positions;
	int max_pixels_x;
	int actual_y;
	public void writeFile(String fileName, VariabilityModel vm)
			throws Exception {
		FAMAFeatureModel fm=(FAMAFeatureModel)vm;
		positions=new HashMap<Feature, Point>();
		FileWriter out = new FileWriter(fileName);
		graph+="<html><body><svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\r\n";
		graph+="<defs> <marker refY=\"50\" refX=\"50\" markerHeight=\"5\" markerWidth=\"5\" viewBox=\"0 0 100 100\" se_type=\"rightarrow\" orient=\"auto\" markerUnits=\"strokeWidth\" id=\"se_marker_end_svg_1\">  <path stroke-width=\"10\" stroke=\"#000000\" fill=\"#000000\" d=\"m100,50l-100,40l30,-40l-30,-40z\"/> </marker> <marker refY=\"50\" refX=\"50\" markerHeight=\"5\" markerWidth=\"5\" viewBox=\"0 0 100 100\" se_type=\"leftarrow\" orient=\"auto\" markerUnits=\"strokeWidth\" id=\"se_marker_start_svg_1\">   <path stroke-width=\"10\" stroke=\"#000000\" fill=\"#000000\" d=\"m0,50l100,40l-30,-40l30,-40z\"/>  </marker> </defs>";
		graphIt(fm);
		graph+="</svg></body></html>";
		out.write(graph);
		out.flush();
		out.close();
			
		
	}

	private void graphIt(FAMAFeatureModel fm) {
		int max_x,max_y;
		actual_y=20;
		//We need to know the position of every feature, for that, we need the size of the paint
		max_y=depth(fm.getRoot());
		
		Collection<Feature> tmpCol= new ArrayList<Feature>();
		tmpCol.add(fm.getRoot());
		max_x=width(tmpCol);
		System.out.println("The maximum x is "+max_x);
		max_pixels_x=(max_x*max_y*100/2)+(max_x*20);//ancho de la feat + ancho del hueco.
		//printFeature(fm.getRoot(),max_pixels_x/2, actual_y);
		//graph+="<line x1=\"0\" y1=\"0\" x2=\""+max_pixels_x+"\" y2=\"0\"style=\"stroke:rgb(255,0,0);stroke-width:2\"/>\r\n";
		recorreArbol(fm.getRoot(),0,max_pixels_x,actual_y);
		imprimeRelations(fm.getRelations());
		imprimeDependencies(fm.getDependencies());
	}
	
	private void imprimeDependencies(Iterator<Constraint> iterator) {
		while(iterator.hasNext()){
			Dependency dep = (Dependency) iterator.next();
			Point parentPoint = this.positions.get(dep.getOrigin());
			Point childPoint = this.positions.get(dep.getDestination());
			if(dep instanceof RequiresDependency){
				graph+="<path marker-start=\"url(#se_marker_start_svg_1)\" marker-end=\"url(#se_marker_end_svg_1)\" id=\"quadcurveABC\" d=\"M " + (parentPoint.x+35)+" "+ (parentPoint.y+45)+ " Q " +(Math.abs((childPoint.y-3*parentPoint.y)+35))+" "+(Math.abs((childPoint.x-parentPoint.x)+35))+" "+ (childPoint.x+35) +" "+(childPoint.y+45)+" \" stroke=\"black\" stroke-width=\"2\" fill=\"none\" style=\"stroke-dasharray: 9, 5;\"/>\r\n";
			}else if(dep instanceof ExcludesDependency){
				graph+="<path marker-end=\"url(#se_marker_end_svg_1)\" id=\"quadcurveABC\" d=\"M " + (parentPoint.x+35)+" "+ (parentPoint.y+45)+ " Q " +(Math.abs((childPoint.y-3*parentPoint.y)+35))+" "+(Math.abs((childPoint.x-parentPoint.x)+35))+" "+ (childPoint.x+35) +" "+(childPoint.y+45)+" \" stroke=\"black\" stroke-width=\"2\" fill=\"none\" style=\"stroke-dasharray: 9, 5;\" />\r\n";
			}
		}		
	}

	private void imprimeRelations(Collection<GenericRelation> relations) {
		for(GenericRelation rel : relations){
			Relation r =(Relation)rel;
			if(r.isMandatory()){
				Point parentPoint = this.positions.get(r.getParent());
				Point childPoint = this.positions.get(r.getDestinationAt(0));
				graph+="<line x1=\""+(parentPoint.x+35)+"\" y1=\""+(parentPoint.y+45)+"\" x2=\""+(35+childPoint.x)+"\" y2=\""+childPoint.y+"\"style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\r\n";
				graph+="<circle cx=\""+(35+childPoint.x)+"\" cy=\""+childPoint.y+"\" r=\"5\" stroke=\"black\" stroke-width=\"2\" fill=\"black\"/>";
			}else if(r.isOptional()){
				Point parentPoint = this.positions.get(r.getParent());
				Point childPoint = this.positions.get(r.getDestinationAt(0));
				graph+="<line x1=\""+(parentPoint.x+35)+"\" y1=\""+(parentPoint.y+45)+"\" x2=\""+(35+childPoint.x)+"\" y2=\""+childPoint.y+"\"style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\r\n";
				graph+="<circle cx=\""+(35+childPoint.x)+"\" cy=\""+childPoint.y+"\" r=\"5\" stroke=\"black\" stroke-width=\"2\" fill=\"white\"/>";
		
			}else {
				Point parentPoint = this.positions.get(r.getParent());
				Iterator<Feature> destination = r.getDestination();
				while(destination.hasNext()){
					Point childPoint = this.positions.get(destination.next());
					graph+="<line x1=\""+(parentPoint.x+35)+"\" y1=\""+(parentPoint.y+45)+"\" x2=\""+(35+childPoint.x)+"\" y2=\""+childPoint.y+"\"style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\r\n";
					String cardsStr="";
					Iterator<Cardinality> cardinalities = r.getCardinalities();
					while(cardinalities.hasNext()){cardsStr+=cardinalities.next().toString();}
					graph+="<text x=\""+(parentPoint.x+35)+"\" y=\""+(parentPoint.y+65)+"\" text-anchor=\"middle\" alignment-baseline=\"middle\">" +cardsStr + "</text>";
					graph+="<line x1=\""+(parentPoint.x-40)+"\" y1=\""+(parentPoint.y+75)+"\" x2=\""+(parentPoint.x+110)+"\" y2=\""+(parentPoint.y+75)+"\"style=\"stroke:rgb(0,0,0);stroke-width:2\"/>\r\n";

				}
			}
		}
		
	}

	private void recorreArbol(Feature f,int x_0,int x_1, int reference_y) {
		
		printFeature(f, x_0+((x_1-x_0)/2)-35, reference_y );

		int i=0;
		Collection<Feature> childs = f.getChilds();
		for(Feature child : childs){
			int nclids=childs.size();
			int sizePerChild=(x_1-x_0)/nclids;
			recorreArbol(child,(x_0+(i*sizePerChild)),(x_0+((i+1)*sizePerChild)) , reference_y+90);
			System.out.println("feat "+child.getName()+" "+(x_0+(i*sizePerChild))+"_"+(x_0+((i+1)*sizePerChild)));
			i++;
		}
		
	}

	private void printFeature(Feature f,int x, int y){
		graph+="<rect x=\""+x+"\" y=\""+y+"\" width=\"70\" height=\"45\" rx=\"2\" ry=\"2\" style=\"fill:white;stroke-width:2;stroke:rgb(0,0,0)\" /><text x=\""+(x+35)+"\" y=\""+(y+25)+"\" text-anchor=\"middle\" alignment-baseline=\"middle\">" +f.getName() + "</text>\r\n";
		positions.put(f,new Point(x, y));
	}


	private Integer width(Collection<Feature> col)
	{
		Collection<Feature> chOfch= new LinkedList<Feature>();
		for(Feature f :col){
			chOfch.addAll(f.getChilds());
		}
		int numberOfchilds=chOfch.size();
		int nChOfch=0;
		if(numberOfchilds>0){nChOfch=width(chOfch);};
		if(numberOfchilds<nChOfch){
			return nChOfch;
		}else{
			return numberOfchilds;
		}
		
		
	}
	
	
	private Integer depth(Feature o)
	{
	   Collection<Integer> resint= new LinkedList<Integer>();
	   resint.add(0);
	   for(Feature f: o.getChilds()){
		   resint.add(depth(f));
	   }
	   return((Collections.max(resint))+1);
	}
	
}
