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
package es.us.isa.FAMA.models.FAMAfeatureModel.fileformats;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;




import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



//import es.us.isa.FAMA.models.FAMAfeatureModel.Attribute;
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
import es.us.isa.FAMA.Exceptions.*;

/**
 * @author trinidad, Manuel Nieto Ucl�s
 *
 * Create a Feature Model by a XML document representation.
 */

public class XMLReader implements IReader{
	
	private Collection<String> featureNames;
	
	public VariabilityModel parseStream ( InputStream stream ) throws WrongFormatException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document featureModel = builder.parse( stream );
			
			return this.parse( featureModel );
		} catch (ParserConfigurationException e1){
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		throw new IllegalStateException("Error parsing the file");
	}
	
	public VariabilityModel parseFile ( String uri ) throws WrongFormatException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document featureModel = builder.parse( uri );
			
			return this.parse( featureModel );
		} catch (ParserConfigurationException e1){
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		throw new IllegalStateException("Error parsing the file");
	}
	
	/**
	 * 
	 * */
	public VariabilityModel parseString(String data) throws WrongFormatException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			InputSource stream = new InputSource(new StringReader(data));
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document featureModel = builder.parse( stream );
			
			return this.parse( featureModel );
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		throw new IllegalStateException("Error parsing the file");
	}
	
	/**
	 * parses XML file section for a Feature Model
	 * @return a Feature Model
	 */
	protected VariabilityModel parse(Document xmlDocument) throws WrongFormatException {
		FAMAFeatureModel featureModel = new FAMAFeatureModel();
		
		featureNames = new LinkedList<String>();
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
				}else throw new WrongFormatException("wrong label name: " + nextNode.getNodeName()+nextNode.getNodeValue());
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
		String fName = featureName.getNodeValue();
		Feature f = null;
		if (featureNames.contains(fName)){
			//evitamos nombres de features duplicados
			throw new FAMAException("Duplicated feature name: "+fName);
		}
		else{
			f = new Feature(featureName.getNodeValue());		
			featureNames.add(fName);
		}
		NodeList childNodes = featureNode.getChildNodes();
		for(int i=0; i<childNodes.getLength(); i++){
			Node nextNode = childNodes.item(i);
			if(nextNode.getNodeType() == Node.ELEMENT_NODE)
				if(nextNode.getNodeName().equalsIgnoreCase("setRelation")
				   || nextNode.getNodeName().equalsIgnoreCase("binaryRelation")){
					Relation r = parseRelation(nextNode);
					f.addRelation(r);
				}else
					throw new WrongFormatException("wrong label name: " + nextNode.getNodeName()+nextNode.getNodeValue());
		}
		return f;
	}
	
	/**
	 * parses XML file section for Attribute
	 * @param attributeNode
	 * @return a Attribute
	 * @throws WrongFormatException
	 */
//	protected Attribute parseAttribute(Node attributeNode) throws WrongFormatException{
//		return null;
//	}

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
						throw new WrongFormatException("wrong label name: " + nextNode.getNodeName()+nextNode.getNodeValue());					
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
						throw new WrongFormatException("wrong label name: " + nextNode.getNodeName()+nextNode.getNodeValue());					
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


	public boolean canParse(String fileName) {
		//FIXME cuidado con la ruta relativa del .xsd
		//cambiarla cuando cambiemos algo
//        try {
//        	// 1. Lookup a factory for the W3C XML Schema language
//            SchemaFactory factory = 
//                SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
//            
//            // 2. Compile the schema. 
//            // Here the schema is loaded from a java.io.File, but you could use 
//            // a java.net.URL or a javax.xml.transform.Source instead.
//            //String schemaPath = Activator.getSchema();
//            File schemaLocation = Activator.getSchema();
//            //schemaLocation
////            System.out.println(Activator.getSchema());
//            System.out.println(schemaLocation.exists());
////            File test = new File("");
////            System.out.println(test.getAbsolutePath());
//            //FIXME peta al crear el schema
//            Schema schema = factory.newSchema(schemaLocation);
//        
//            // 3. Get a validator from the schema.
//            Validator validator = schema.newValidator();
//            
//            // 4. Parse the document you want to check.
//            Source source = new StreamSource(fileName);
//            
//            // 5. Check the document
//            validator.validate(source);
//            return true;
//        }
//        catch (Exception ex) {
//            return false;
//        }
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			String extension=fileName.substring(fileName.lastIndexOf('.'));

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document featureModel = builder.parse( fileName );
			return extension.equals(".fama")||extension.equals(".xml");
			//return this.parse( featureModel );
		} catch (Exception e1){
			return false;
		} 
		
	}
}