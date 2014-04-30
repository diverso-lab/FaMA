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
 * Created on 04-Dec-2004
 * 
 */
package es.us.isa.benchmarking.readers;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.us.isa.FAMA.models.FAMAfeatureModel.Attribute;
import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IReader;
import es.us.isa.FAMA.models.variabilityModel.parsers.WrongFormatException;

/**
 * @author trinidad, Manuel Nieto Ucl�s
 *
 * Create a Feature Model by a XML document representation.
 */
public class XMLReader implements IReader{
	
	public VariabilityModel parseFile ( String fileName ) throws WrongFormatException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document featureModel = builder.parse( fileName );

			return this.parse( featureModel );
		} catch (ParserConfigurationException e1){
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 
	 * */
	public VariabilityModel parseString(String data) throws WrongFormatException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document featureModel = builder.parse( data );

			return this.parse( featureModel );
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;
	}
	
	/**
	 * parses XML file section for a Feature Model
	 * @return a Feature Model
	 */
	protected VariabilityModel parse(Document xmlDocument) throws WrongFormatException {
		FAMAFeatureModel featureModel = new FAMAFeatureModel();
		
		//represents a feature list of the document
		NodeList featureList = xmlDocument.getChildNodes();
		if(featureList.getLength() == 0){
			throw new WrongFormatException("No root node exists");
		}
		
		Node root = featureList.item(0);
		if ( !root.getNodeName().equalsIgnoreCase("feature-model") )
			throw new WrongFormatException("feature-model root does not exist");
		
		NodeList childNodes = root.getChildNodes();
		int rootCounter = 0;
		for(int i=0; i<childNodes.getLength(); i++){
			Node nextNode = childNodes.item(i);
			if ( nextNode.getNodeType() == Node.ELEMENT_NODE ){
				if(nextNode.getNodeName().equalsIgnoreCase("feature")){
					rootCounter++;
					Feature rootFeature = parseFeature(nextNode);
					featureModel.setRoot(rootFeature);
				}else if(nextNode.getNodeName().equalsIgnoreCase("requires")
						 || nextNode.getNodeName().equalsIgnoreCase("excludes")){
					Dependency d = parseDependency(nextNode, featureModel);
					featureModel.addDependency(d);
				}else throw new WrongFormatException("wrong label name: " + nextNode.getNodeName());
			}
		}

		if(rootCounter != 1)
			throw new WrongFormatException("More than one root feature found.");
		
		return featureModel;
	}
	
	/**
	 * parses XML file section for Feature
	 * @return a Feature
	 */
	protected Feature parseFeature(Node featureNode) throws WrongFormatException {
		Node featureName = featureNode.getAttributes().getNamedItem("name");
		if (featureName == null)
			throw new WrongFormatException("\"name\" tag does not exist for a feature");
		Feature f = new Feature(featureName.getNodeValue());		
		
		NodeList childNodes = featureNode.getChildNodes();
		for(int i=0; i<childNodes.getLength(); i++){
			Node nextNode = childNodes.item(i);
			if(nextNode.getNodeType() == Node.ELEMENT_NODE)
				if(nextNode.getNodeName().equalsIgnoreCase("setRelation")
				   || nextNode.getNodeName().equalsIgnoreCase("binaryRelation")){
					Relation r = parseRelation(nextNode);
					f.addRelation(r);
				}else
					throw new WrongFormatException("wrong label name: " + nextNode.getNodeName());
		}
		return f;
	}
	
	/**
	 * parses XML file section for Attribute
	 * @param attributeNode
	 * @return a Attribute
	 * @throws WrongFormatException
	 */
	protected Attribute parseAttribute(Node attributeNode) throws WrongFormatException{
		return null;
	}

	/**
	 * parses XML file section for a Relation
	 * @return a Relation
	 */
	protected Relation parseRelation(Node relationNode) throws WrongFormatException {
		Node relationName = relationNode.getAttributes().getNamedItem("name");
		if(relationName == null)
			throw new WrongFormatException("\"name\" tag does not exist for a relation");
		Relation r = null;
		if(relationNode.getNodeName().equalsIgnoreCase("binaryRelation")){
			r = new Relation(relationName.getNodeValue());
			
			NodeList childNodes = relationNode.getChildNodes();
			int numSolitaryFeatures = 0;
			
			for(int i=0; i<childNodes.getLength(); i++){
				Node nextNode = childNodes.item(i);
				if(nextNode.getNodeType() == Node.ELEMENT_NODE)
					if(nextNode.getNodeName().equalsIgnoreCase("solitaryFeature")){
						numSolitaryFeatures ++;
						Feature f = parseFeature(nextNode);
						r.addDestination(f);
					}else if(nextNode.getNodeName().equalsIgnoreCase("cardinality")){
						Cardinality c = parseCardinality(nextNode);
						((Relation)r).addCardinality(c);
					}else
						throw new WrongFormatException("wrong label name: " + nextNode.getNodeName());					
			}
			
			if(numSolitaryFeatures > 1)
				throw new WrongFormatException("incorrect number of childs in a binaryRelation: " + relationNode.getNodeName());
			
		}else if(relationNode.getNodeName().equalsIgnoreCase("setRelation")){
			r = new Relation(relationName.getNodeValue());
			
			NodeList childNodes = relationNode.getChildNodes();
			
			for(int i=0; i<childNodes.getLength(); i++){
				Node nextNode = childNodes.item(i);
				if(nextNode.getNodeType() == Node.ELEMENT_NODE)
					if(nextNode.getNodeName().equalsIgnoreCase("groupedFeature")){
						Feature f = parseFeature(nextNode);
						r.addDestination(f);
					}else if(nextNode.getNodeName().equalsIgnoreCase("cardinality")){
						Cardinality c = parseCardinality(nextNode);
						((Relation)r).addCardinality(c);	
					}else
						throw new WrongFormatException("wrong label name: " + nextNode.getNodeName());					
			}			
		}else
			throw new WrongFormatException("wrong label name: " + relationNode.getNodeName());
		
		return r;
	}
	
	/**
	 * parses XML file section for a Cardinality
	 * @return a Cardinality
	 */
	protected Cardinality parseCardinality(Node cardinalityNode) throws WrongFormatException{		
		Cardinality c = null;		
		
		Node attribute = cardinalityNode.getAttributes().getNamedItem("min");
		String min = attribute.getNodeValue();
		attribute = cardinalityNode.getAttributes().getNamedItem("max");
		String max = attribute.getNodeValue();		
							
		if( min == null || max == null)
			throw new WrongFormatException("Invalid cardinality: " + max + " " + min);
				
		int minValue = Integer.parseInt(min);
		int maxValue = Integer.parseInt(max);
		
		if(minValue < 0 || maxValue < 0)
			throw new WrongFormatException("Invalid cardinality: " + max + " " + min);
		
		c = new Cardinality(minValue, maxValue);
		
		return c;
	}
	
	/**
	 * parses XML file section for a Dependency
	 * @return a Dependency
	 */	
	protected Dependency parseDependency(Node depNode, FAMAFeatureModel fm) throws WrongFormatException {
		Dependency d = null;
		
		if(depNode.getNodeType() == Node.ELEMENT_NODE)
			if(depNode.getNodeName().equalsIgnoreCase("excludes")){
				Node attribute = depNode.getAttributes().getNamedItem("name");
				String name = attribute.getNodeValue();
				attribute = depNode.getAttributes().getNamedItem("feature");				
				String elem1 = attribute.getNodeValue();
				attribute = depNode.getAttributes().getNamedItem("excludes");				
				String elem2 = attribute.getNodeValue();
				Feature node1 = fm.searchFeatureByName(elem1);
				Feature node2 = fm.searchFeatureByName(elem2);					
				if( name == null || node1 == null || node2 == null)
					throw new WrongFormatException("No name or Unknown feature " + node1 + " or " + node2);
				d = new ExcludesDependency(name, node1,node2);
			}else if(depNode.getNodeName().equalsIgnoreCase("requires")){
				Node attribute = depNode.getAttributes().getNamedItem("name");
				String name = attribute.getNodeValue();
				attribute = depNode.getAttributes().getNamedItem("requires");
				String elem1 = (attribute != null) ? attribute.getNodeValue() : "";
				attribute = depNode.getAttributes().getNamedItem("feature");
				String elem2 = (attribute != null) ? attribute.getNodeValue() : "";
				Feature from = fm.searchFeatureByName(elem2);
				Feature to = fm.searchFeatureByName(elem1);
				if ( name == null || from == null || to == null)
					throw new WrongFormatException("No name or Unknown feature " + from + " or " + to);
				d = new RequiresDependency(name, from,to);
			}else
				throw new WrongFormatException("wrong label name: " + depNode.getNodeName());
		
		return d;
	}
}