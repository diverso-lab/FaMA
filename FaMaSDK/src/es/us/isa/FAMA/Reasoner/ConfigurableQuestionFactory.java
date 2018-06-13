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
package es.us.isa.FAMA.Reasoner;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import es.us.isa.FAMA.Reasoner.Factory.QuestionAbstractFactory;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class ConfigurableQuestionFactory implements QuestionAbstractFactory {

	private ClassLoader loader;
	private Map<String, Class<Question>> questionClasses;
	
	public ConfigurableQuestionFactory() {
		loader = ClassLoader.getSystemClassLoader();
	}
	
	public void setClassLoader(ClassLoader loader) {
		if (loader != null)
			this.loader = loader;
	}
	
	public Question createQuestion(Class<Question> questionClass)  {
		String interfaceName = questionClass.getName();
		Class<Question> implClass = questionClasses.get(interfaceName);
		
		Question res = null;
		if (implClass != null) {
			try {
				res = implClass.newInstance();
				if (!questionClass.isInstance(res))
					res = null;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return res;	

	}
	
	
	public int questionTime(String questionType, VariabilityModel fm) {
		return 0;
	}

	@SuppressWarnings("unchecked")
	public void parseConfigFile(InputStream configStream) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document configDocument = builder.parse( configStream );
			questionClasses = new HashMap<String,Class<Question>>();
			NodeList rootList = configDocument.getChildNodes();
			if (rootList.getLength() == 1) {
				Node root = rootList.item(0);
				if (root.getNodeName().equalsIgnoreCase("reasoner") && root.hasChildNodes()) {
					NodeList questionsNL = root.getChildNodes();
					for (int i = 0; i < questionsNL.getLength(); i++) {
						Node questionNode = questionsNL.item(i);
						if (questionNode.getNodeType() == Node.ELEMENT_NODE &&
							questionNode.getNodeName().equalsIgnoreCase("question")) {
							Node classNode = questionNode.getAttributes().getNamedItem("class");
							Node interfaceNode = questionNode.getAttributes().getNamedItem("interface");
							if (interfaceNode != null && classNode != null) {
								String className = classNode.getNodeValue();
								try {
									//Class<Question> cl = (Class<Question>)Class.forName(className);
									Class<Question> cl = (Class<Question>)loader.loadClass(className);
									questionClasses.put( interfaceNode.getNodeValue(),cl );
								} catch (ClassNotFoundException e) {}
								  catch (ClassCastException e) {}									
							}
						}
					}
				}
				
			}
		} catch (ParserConfigurationException e1){
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
}
