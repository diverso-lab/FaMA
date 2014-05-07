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

package es.us.isa.FAMA.main;

import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import spitz.ayal.jarjar.JarJarClassLoader;

public class FaMaMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FaMaMain main = new FaMaMain();
		main.loadUI("FaMaConfig.xml");
		
	}
	
	private void loadUI(String configFile){
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document configDocument = builder.parse(configFile);
			if (configDocument ==null){
				System.err.println("Please put a config file for FaMa");
				throw new IllegalArgumentException("config file not found");
			}
			if (configDocument == null) return;
			NodeList rootList = configDocument.getChildNodes();
			if (rootList.getLength() == 1) {
				Node root = rootList.item(0);
				if (root.getNodeName().equalsIgnoreCase("questionTrader") && root.hasChildNodes()) {
					NodeList childNodes = root.getChildNodes();
					for (int i = 0; i < childNodes.getLength(); i++){
						Node n = childNodes.item(i);
						if (n.getNodeType() == Node.ELEMENT_NODE 
								&& n.getNodeName().equals("UserInterface")){
							NamedNodeMap atts = n.getAttributes();
							Node fileNode = atts.getNamedItem("file");
							Node mainClassNode = atts.getNamedItem("mainclass");
							
							if (fileNode != null && mainClassNode != null){
								ClassLoader loader = new JarJarClassLoader(fileNode.getTextContent());
								String mainClassName = mainClassNode.getTextContent();
								
								Class mainClass = loader.loadClass(mainClassName);
								Method m = mainClass.getMethod("main", String[].class);
								Object instance = mainClass.newInstance();
								m.invoke(instance, new Object[1]);
							}
							
						}
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}				
		
	}
	

}
