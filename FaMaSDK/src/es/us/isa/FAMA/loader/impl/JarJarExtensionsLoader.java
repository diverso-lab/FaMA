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
package es.us.isa.FAMA.loader.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import es.us.isa.FAMA.Reasoner.ConfigurableQuestionFactory;
import es.us.isa.FAMA.Reasoner.CriteriaSelector;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.loader.ExtensionsLoader;
import es.us.isa.FAMA.models.variabilityModel.parsers.IReader;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;
import es.us.isa.FAMA.models.variabilityModel.parsers.ModelParser;
import es.us.isa.FAMA.models.variabilityModel.parsers.ModelParserImpl;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import spitz.ayal.jarjar.JarJarClassLoader;

import es.us.isa.FAMA.Reasoner.Factory.QuestionAbstractFactory;

/**
 * This class is the extensions loader that FaMa uses when running not in to an OSGi environment
 */
public class JarJarExtensionsLoader implements ExtensionsLoader {

	protected ModelParserImpl mp;
	protected Map<Reasoner,QuestionAbstractFactory> reasonersMap;
	protected Map<String,Reasoner> reasonersIdMap;
	protected Map<String,Class<Question>> questionsMap;
	protected CriteriaSelector selector;
	protected Map<String, CriteriaSelector> selectorsMap;
	protected ClassLoader famaloader;
	protected String configFile;
	protected Map<String, Class<IVariabilityModelTransform>> transformationsMap;
	protected QuestionTrader qt;
	
	public JarJarExtensionsLoader(String configFile, QuestionTrader qt){
		reasonersMap = new HashMap<Reasoner, QuestionAbstractFactory>();
		//reasonersMap = new HashMap<Reasoner, QuestionAbstractFactory>();
		selectorsMap = new HashMap<String, CriteriaSelector>();
		questionsMap = new HashMap<String, Class<Question>>();
		reasonersIdMap = new HashMap<String,Reasoner> ();
		transformationsMap = new HashMap<String, Class<IVariabilityModelTransform>>();
		
		//selector = new DefaultCriteriaSelector(this);
		selectorsMap.put("Default",selector);
		
		mp = null;
		this.configFile = configFile;
		this.qt = qt;
		parseConfigFile();
	}
	
	private void parseConfigFile() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document configDocument = builder.parse(configFile);
			reasonersMap.clear();
			if (configDocument ==null){
				System.err.println("Please put a config file for FaMa");
				throw new IllegalArgumentException("config file not found");
			}
			if (configDocument == null) return;
			NodeList rootList = configDocument.getChildNodes();
			if (rootList.getLength() == 1) {
				Node root = rootList.item(0);
				if (root.getNodeName().equalsIgnoreCase("questionTrader") && root.hasChildNodes()) {
					NodeList questionTraderNL = root.getChildNodes();
					for (int i = 0; i < questionTraderNL.getLength(); i++) {
						Node questionTraderNode = questionTraderNL.item(i);
						if (questionTraderNode.getNodeType() == Node.ELEMENT_NODE) {
							if (questionTraderNode.getNodeName().equalsIgnoreCase("reasoner")) {
								processReasonerNode(questionTraderNode);
							}
							else if (questionTraderNode.getNodeName().equalsIgnoreCase("criteriaSelector")) {
								processCriteriaSelectorNode(questionTraderNode);
							}
							else if (questionTraderNode.getNodeName().equalsIgnoreCase("question")) {
								processQuestionNode(questionTraderNode);
							}
							else if (questionTraderNode.getNodeName().equalsIgnoreCase("models")) {
								processModelsNode(questionTraderNode);
							}
							else if  (questionTraderNode.getNodeName().equalsIgnoreCase("transform")) {
								processTransformNode(questionTraderNode);
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
			
		} 				
	}

	
	
	
	private void processTransformNode(Node questionTraderNode) {
		//�Permitir que pueda estar dentro de un fichero (JarJarLoader)
		//o en otro sitio (sin necesidad de JarJarLoader)?
		Node idNode = questionTraderNode.getAttributes().getNamedItem("id");
		Node classNode = questionTraderNode.getAttributes().getNamedItem("class");
		Node fileNode = questionTraderNode.getAttributes().getNamedItem("file");
		if (idNode != null && classNode != null) {
			if (fileNode != null){
				//mediante JarJarLoader
				try {
					String fileName = fileNode.getNodeValue();
					URL url = new URL("jar:file:"+fileName + "!/");
					url.openConnection();
					ClassLoader loader = new JarJarClassLoader(fileName);
					Class c = loader.loadClass(classNode.getNodeValue());
					if (c != null){
						transformationsMap.put(idNode.getNodeValue(), c );
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			else{
				//como toda la vida
				try {
					Class c = Class.forName(classNode.getNodeValue());
					transformationsMap.put(idNode.getNodeValue(),c);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}

	
	private void processModelsNode(Node modelTraderNode) {
		mp = new ModelParserImpl();
		if (modelTraderNode == null)
			return;
		NodeList rootList = modelTraderNode.getChildNodes();

		for (int j = 0; j < rootList.getLength(); j++) {
			Node modelNode = rootList.item(j);
			if (modelNode.getNodeType() == Node.ELEMENT_NODE) {
				if (modelNode.getNodeName().equalsIgnoreCase("reader")) {
					processReaderNode(modelNode);
				} else if (modelNode.getNodeName().equalsIgnoreCase("writer")) {
					processWriterNode(modelNode);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void processReaderNode(Node readerNode) {
		try {
			Node extNode = readerNode.getAttributes()
					.getNamedItem("extensions");
			Node classNode = readerNode.getAttributes().getNamedItem("class");
			Node fileNode = readerNode.getAttributes().getNamedItem("file");
			if (extNode != null && classNode != null && fileNode != null) {
				String fileName = fileNode.getNodeValue();
				String className = classNode.getNodeValue();
				try {
					//URL url = new URL("jar:file:" + fileName + "!/");
					//url.openConnection();
					ClassLoader loader = new JarJarClassLoader(fileName);
					Class<IReader> rcl = (Class<IReader>) loader
							.loadClass(className);
					if (rcl != null) {
						String readerId = rcl.getName();
						IReader reader = rcl.newInstance();
						mp.addReader(reader, readerId);
						StringTokenizer st = new StringTokenizer(extNode
								.getNodeValue(), ",");
						while (st.hasMoreTokens()) {
							//FIXME cuidado, es posible que no este bien
							mp.addReaderType(st.nextToken(), readerId);
							
						}
					}
				} catch (ClassNotFoundException e) {
				} catch (ClassCastException e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void processWriterNode(Node writerNode) {
		try {
			Node extNode = writerNode.getAttributes()
					.getNamedItem("extensions");
			Node classNode = writerNode.getAttributes().getNamedItem("class");
			Node fileNode = writerNode.getAttributes().getNamedItem("file");
			if (extNode != null && classNode != null && fileNode != null) {
				String fileName = fileNode.getNodeValue();
				String className = classNode.getNodeValue();
				try {
					//URL url = new URL("jar:file:" + fileName + "!/");
					//url.openConnection();
					JarJarClassLoader loader = new JarJarClassLoader(fileName);
					Class<IWriter> wcl = (Class<IWriter>) loader
							.loadClass(className);
					if (wcl != null) {
						String writerId = wcl.getName();
						IWriter writer = wcl.newInstance();
						mp.addWriter(writer, writerId);
						StringTokenizer st = new StringTokenizer(extNode
								.getNodeValue(), ",");
						while (st.hasMoreTokens()) {
							//FIXME cuidado, es posible que no este bien
							mp.addWriterType(st.nextToken(), writerId);
						}
					}
				} catch (ClassNotFoundException e) {
				} catch (ClassCastException e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@SuppressWarnings("unchecked")
	private void processQuestionNode(Node questionTraderNode) {
		try {
			Node idNode = questionTraderNode.getAttributes().getNamedItem("id");
			Node classNode = questionTraderNode.getAttributes().getNamedItem("interface");
//			Node fileNode = questionTraderNode.getAttributes().getNamedItem("file");
			if (idNode != null && classNode != null ) {
				String interfaceName = classNode.getNodeValue();
//				String fileName = fileNode.getNodeValue();
				try {
//					URL url = new URL("jar:file:"+fileName + "!/");
//					url.openConnection();
//					ClassLoader loader = new JarJarClassLoader(fileName);
//					Class<Question> qcl = (Class<Question>) loader.loadClass(interfaceName);
					Class<Question> qcl = (Class<Question>) Class.forName(interfaceName);
					
					if (qcl != null){
						questionsMap.put(idNode.getNodeValue(), qcl );
					}
				} catch (ClassNotFoundException e) {}
				  catch (ClassCastException e) {}									
			}	
		} catch (Exception e) {e.printStackTrace();}
	} 


	@SuppressWarnings("unchecked")
	private void processCriteriaSelectorNode(Node csNode) {
		Node fileNode = csNode.getAttributes().getNamedItem("file");
		Node classNode = csNode.getAttributes().getNamedItem("class");
		Node nameNode = csNode.getAttributes().getNamedItem("name");
		try {
			if (fileNode != null && classNode != null) {
				String className = classNode.getNodeValue();
				String fileName = fileNode.getNodeValue();
				String nameName = nameNode.getNodeValue();
				
				URL url = new URL("jar:file:"+fileName + "!/");
				url.openConnection();
				URL []urlArray = new URL[]{url};
				URLClassLoader loader = new URLClassLoader(urlArray);
				Class<CriteriaSelector>csc = (Class<CriteriaSelector>) loader.loadClass(className);
				if (csc != null) {
					CriteriaSelector cs = csc.newInstance();
					this.selectorsMap.put(nameName,cs);
				}			
			} else if (fileNode == null && classNode != null) {
				String className = classNode.getNodeValue();
				String nameName = nameNode.getNodeValue();
				Class<CriteriaSelector> csc = (Class<CriteriaSelector>) Class.forName(className);
				if (csc != null) {
					Constructor<CriteriaSelector>[] cons = (Constructor<CriteriaSelector>[]) csc.getConstructors();
					CriteriaSelector cs = null;
					// i'm searching for the constructor having a QuestionTrader as a parameter. Instead of
					// checking all the constructors for their parameters type, I just try the newInstance method
					// to work and in case it fails (we're calling another constructor) we capture the 
					// InstantiationException so it makes no noise and keeps on searching for the right constructor
					for (int i = 0; i < cons.length && cs == null; i++) {  
						try {
							cs = cons[i].newInstance(qt);
						} catch (InstantiationException ie) {}
					}
					if (cs != null)
						this.selectorsMap.put(nameName, cs);
				}
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	
	@SuppressWarnings("unchecked")
	private void processReasonerNode(Node reasonerNode) {
		Node fileNode = reasonerNode.getAttributes().getNamedItem("file");
		Node classNode = reasonerNode.getAttributes().getNamedItem("class");
		Node idNode = reasonerNode.getAttributes().getNamedItem("id");
		if (fileNode != null && classNode != null) {
			String className = classNode.getNodeValue();
			String fileName = fileNode.getNodeValue();
			String idName = className;
			if (idNode != null)
				idName = idNode.getNodeValue();
				
			try {
				//URL url = new URL("jar:file:"+ fileName + "!/");
				//url.openConnection();
				ClassLoader loader = new JarJarClassLoader(fileName);
				Class<Reasoner>cl = (Class<Reasoner>) loader.loadClass(className);
				InputStream configStream = loader.getResourceAsStream(idName+"Config.xml");
				if (cl != null) {
					Reasoner reasoner = cl.newInstance();
					ConfigurableQuestionFactory qFact = new ConfigurableQuestionFactory();
					qFact.setClassLoader(loader);
					qFact.parseConfigFile(configStream);
					reasoner.setFactory(qFact);
					//reasoner.setConfigFile(configStream,loader);
					this.reasonersMap.put(reasoner, reasoner.getFactory());
					reasonersIdMap.put(idName, reasoner);
				}
			} catch (Exception e) {e.printStackTrace();}			
		}
	}
	
	public Map<String, Class<Question>> getQuestionsMap() {
		return this.questionsMap;
	}

	public Collection<Reasoner> getReasoners() {
		return this.reasonersMap.keySet();
	}

	public Map<String, Reasoner> getReasonersIdMap() {
		return this.reasonersIdMap;
	}

	public Map<String, CriteriaSelector> getSelectorsMap() {
		return this.selectorsMap;
	}

	public Map<String, Class<IVariabilityModelTransform>> getTransformationsMap() {
		return this.transformationsMap;
	}

	public ModelParser getModelParser() {
		return mp;
	}

}
