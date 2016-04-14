/**
 *  This file is part of FaMaTS.
 *
 *  FaMaTS is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FaMaTS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.icesi.i2t.FAMA.TestSuite2;

import java.io.FileNotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test loader class for loading tests for FAMA extensions.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @version 1.0, September 2014
 */
public class TestLoader {

	/**
	 * Loads the tests specified in the given configuration file. Tests are
	 * specified in an XML file with the information of the variability models
	 * to test and the questions to be asked with their required input and
	 * expected output.
	 * 
	 * Note: This method only loads the variability models.
	 * 
	 * @param testsConfigFile
	 *            A configuration file in XML format describing the test
	 *            resources (models, inputs, expected outputs) that will be
	 *            loaded.
	 * @return An array containing the feature model files.
	 * @throws FileNotFoundException
	 *             If the test configuration file is not found.
	 * @throws Exception
	 *             If any other errors occur.
	 */
	public static Object[][] loadReasonerTests(String testsConfigFile)
			throws FileNotFoundException, Exception {
		NodeList testsList = getTestNodes(testsConfigFile);
		Object[][] modelFiles = new Object[testsList.getLength()][1];
		for (int i = 0; i < testsList.getLength(); i++) {
			// Test node
			Node testNode = testsList.item(i);
			String modelFile = getModelFile(testNode);
			modelFiles[i][0] = modelFile;
		}
		return trimTestResourcesMatrix(modelFiles);
	}

	/**
	 * Loads the tests specified in the given configuration file for a given
	 * question. Tests are specified in an XML file with the information of the
	 * variability models to test and the questions to be asked with their
	 * required input and expected output.
	 * 
	 * @param testsConfigFile
	 *            A configuration file in XML format describing the test
	 *            resources (models, inputs, expected outputs) that will be
	 *            loaded.
	 * @param question
	 *            A question a specific test case is going to evaluate.
	 * 
	 * @return A <code>n</code>x3 object matrix, where <code>n</code> is the
	 *         number of test nodes found in the given configuration file. The
	 *         first cell contains the feature model file, the second cell
	 *         contains the input required by the question, and the third cell
	 *         contains the expected output for the question with the given
	 *         model and input.
	 * 
	 * @throws FileNotFoundException
	 *             If the test configuration file is not found.
	 * @throws Exception
	 *             If any other errors occur.
	 */
	public static Object[][] loadQuestionTests(String testsConfigFile,
			String question) throws FileNotFoundException, Exception {
		NodeList testsList = getTestNodes(testsConfigFile);
		Object[][] testsMap = new Object[testsList.getLength()][3];

		for (int i = 0; i < testsList.getLength(); i++) {
			// Test node
			Node testNode = testsList.item(i);

			String modelFile = getModelFile(testNode);

			NodeList testQuestionsList = testNode.getChildNodes();
			String input = null;
			String expectedOutput = null;
			boolean stop = false;

			for (int j = 0; (j < testQuestionsList.getLength()) && !stop; j++) {
				Node questionNode = testQuestionsList.item(j);

				// Question node
				if (questionNode.getNodeType() == Node.ELEMENT_NODE
						&& questionNode.getNodeName().equalsIgnoreCase(
								"question")) {
					NamedNodeMap questionNodeAttributes = questionNode
							.getAttributes();

					// ID attribute
					if (questionNodeAttributes.getNamedItem("id")
							.getTextContent().equals(question)) {

						// Input attribute
						Node inputAttribute = questionNodeAttributes
								.getNamedItem("input");
						if (inputAttribute != null) {
							input = inputAttribute.getTextContent();
						}

						// Expected output attribute
						Node expectedOutputAttribute = questionNodeAttributes
								.getNamedItem("expectedOutput");
						if (expectedOutputAttribute != null) {
							expectedOutput = expectedOutputAttribute
									.getTextContent();
							stop = true;
						}
					}
				}
			}
			if (modelFile != null && expectedOutput != null) {
				testsMap[i][0] = modelFile;
				testsMap[i][1] = input;
				testsMap[i][2] = expectedOutput;
			}
		}
		
		return trimTestResourcesMatrix(testsMap);
	}

	/**
	 * Returns the <code>test</code> nodes in the node <code>tests</code> for
	 * the given test configuration file, if any. Null otherwise.
	 * 
	 * @param testsConfigFile
	 *            A configuration file in XML format describing the test
	 *            resources (models, inputs, expected outputs) that will be
	 *            loaded.
	 * @return The <code>test</code> nodes in the node <code>tests</code> if
	 *         any. Null otherwise.
	 * @throws FileNotFoundException
	 *             If the test configuration file is not found.
	 * @throws Exception
	 *             If any other errors occur.
	 */
	private static NodeList getTestNodes(String testsConfigFile)
			throws FileNotFoundException, Exception {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document testConfigDocument = documentBuilder.parse(testsConfigFile);
		if (testConfigDocument == null) {
			throw new FileNotFoundException(
					"Tests configuration file not found.");
		}
		NodeList testsList = null;
		NodeList rootList = testConfigDocument.getChildNodes();
		if (rootList.getLength() == 1) {

			// Tests node
			Node root = rootList.item(0);
			if (root.getNodeName().equalsIgnoreCase("tests")
					&& root.hasChildNodes()) {
				testsList = root.getChildNodes();
			}
		}
		return testsList;
	}

	/**
	 * Returns the model file path specified in the given <code>test</code>
	 * node.
	 * 
	 * @param testNode
	 *            The <code>test</code> node.
	 * @return The model file path specified in the given <code>test</code>
	 *         node.
	 */
	private static String getModelFile(Node testNode) {
		String modelFile = null;
		if (testNode.getNodeType() == Node.ELEMENT_NODE
				&& testNode.getNodeName().equalsIgnoreCase("test")) {
			NamedNodeMap testNodeAttributes = testNode.getAttributes();

			// Model file attribute
			Node modelFileAttribute = testNodeAttributes
					.getNamedItem("modelFile");
			if (modelFileAttribute != null) {
				modelFile = modelFileAttribute.getTextContent();
			}
		}
		return modelFile;
	}
	
	private static Object[][] trimTestResourcesMatrix(Object[][] testResourcesMatrix) {
		int i = 0;
		for (int j = 0; j < testResourcesMatrix.length; j++) {
			if (testResourcesMatrix[j][0] != null) {
				i++;
			}
		}
		Object[][] trimedTestResourcesMatrix = new Object[i][testResourcesMatrix[0].length];
		i = 0;
		for (int j = 0; j < testResourcesMatrix.length; j++) {
			if (testResourcesMatrix[j][0] != null) {
				for (int k = 0; k < testResourcesMatrix[0].length; k++) {
					trimedTestResourcesMatrix[i][k] = testResourcesMatrix[j][k];
					
				}
				i++;
			}
		}
		return trimedTestResourcesMatrix;
	}

}
