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
/*
 * Created on 11-Jan-2005
 * Modified on 19-Apr-2006
 */
package es.us.isa.FAMA.models.FAMAfeatureModel.fileformats;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//import es.us.isa.FAMA.models.FAMAfeatureModel.Attribute;
import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;

/**
 * @author trinidad, Manuel Nieto Ucl�s
 *
 * Create a XML document which represents the feature model.
 */
public class XMLWriter implements IWriter{
	int relnum=0;
	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Parser.IWriter#writeFile(java.lang.String, es.us.isa.FAMA.featureModel.FeatureModel)
	 */
	public void writeFile(String fileName, VariabilityModel vm) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		FAMAFeatureModel fm = (FAMAFeatureModel)vm;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();

			generateDocument (document, fm);
			
			//OutputFormat format  = new OutputFormat( document );
			FileWriter out = new FileWriter(fileName);
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(out);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.transform(source, result);
			/*XMLSerializer serial = new XMLSerializer( out, format );
			serial.asDOMSerializer();
			serial.serialize( document );*/
			out.close();
		}catch(ParserConfigurationException pce){
			pce.printStackTrace();
			throw new Exception(pce);
		}catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
			throw new Exception(fnfe);
		}
	}

	public void generateDocument (Document document, FAMAFeatureModel fm) {
		Element root = document.createElement("feature-model");
		Attr attr = document.createAttribute("xmlns:xsi");
		attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
		root.setAttributeNode(attr);
		attr = document.createAttribute("xsi:noNamespaceSchemaLocation");
		attr.setValue("http://www.tdg-seville.info/benavides/featuremodelling/feature-model.xsd");
		root.setAttributeNode(attr);
		generateDocumentFeature(document,root,fm.getRoot(),"feature");
		generateDocumentDependencies(document, root, fm);
		document.appendChild(root);
	}
	
	public void generateDocumentFeature(Document document, Element parent, Feature f, String type){
		Element current = document.createElement(type);
		
		//	create the name attribute
		Attr name = document.createAttribute("name");
		name.setValue(f.getName());
		current.setAttributeNode(name);
		
		//process the attributes
//		Iterator<Attribute> ita = f.getAttributes();
//		while(ita.hasNext()){
//			Attribute a =  ita.next();
//			generateDocumentAttribute(document, current, a);
//		}

		// process the relations
		Iterator<Relation> itr = f.getRelations();
		while(itr.hasNext()){
			Relation r = (Relation) itr.next();
			if(r.getNumberOfDestination() == 1){
				generateDocumentRelation(document, current, r, "binaryRelation");
			}else{
				generateDocumentRelation(document, current, r, "setRelation");
			}
		}
		
		//	appends the current Element to parent Node
		parent.appendChild(current);
	}
	
//	public void generateDocumentAttribute(Document document, Element parent, Attribute attribute){
//		//TODO attribute
//	}
	
	public void generateDocumentRelation(Document document, Element parent, Relation r, String type){
		Element current = document.createElement(type);
		
		//	create the name attribute
		Attr name = document.createAttribute("name");
//		name.setValue(r.getName());
		name.setValue(relnum+"");
		relnum++;
		current.setAttributeNode(name);
		
		//process the cardinalities
		Iterator<Cardinality> itc = r.getCardinalities();
		while(itc.hasNext()){
			Cardinality c =  itc.next();
			generateDocumentCardinality(document, current, c);
		}

		//process the childs
		Iterator<Feature> itf = r.getDestination();
		while(itf.hasNext()){
			Feature f = itf.next();
			if(type.equals("setRelation")){
				generateDocumentFeature(document, current, f, "groupedFeature");
			}else if(type.equals("binaryRelation")){
				generateDocumentFeature(document, current, f, "solitaryFeature");
			}
		}
		
		//	appends the current Element to parent Node
		parent.appendChild(current);
	}
	
	public void generateDocumentCardinality(Document document, Element parent, Cardinality cardinality){
		Element current = document.createElement("cardinality");
		
		//	create the min attribute
		Attr min = document.createAttribute("min");
		min.setValue(new Integer(cardinality.getMin()).toString());
		current.setAttributeNode(min);
		
		//  create the max attribute
		Attr max = document.createAttribute("max");
		max.setValue(new Integer(cardinality.getMax()).toString());
		current.setAttributeNode(max);
		
		//	appends the current Element to parent Node
		parent.appendChild(current);
	}	
	
	public void generateDocumentDependencies(Document document, Element parent, FAMAFeatureModel fm){
		Iterator<Dependency> it = fm.getDependencies();
		while(it.hasNext()){
			Dependency d =  it.next();
			String type="", sp1="", sp2="";
			if(d instanceof ExcludesDependency){
				type = "excludes";
				sp1 = "feature";
				sp2 = "excludes";
			}else if(d instanceof RequiresDependency){
				type = "requires";
				sp2 = "requires";
				sp1 = "feature";
			}
			Element current = document.createElement(type);

			//	create the name attribute
			Attr name = document.createAttribute("name");
			name.setValue(d.getName());
			current.setAttributeNode(name);
			
			//	create the first attribute
			Attr p1 = document.createAttribute(sp1);
			p1.setValue(d.getOrigin().getName());
			current.setAttributeNode(p1);
			
			//  create the second attribute
			Attr p2 = document.createAttribute(sp2);
			p2.setValue(d.getDestination().getName());
			current.setAttributeNode(p2);
			
			//	appends the current Element to parent Node
			parent.appendChild(current);
		}
	}
}
