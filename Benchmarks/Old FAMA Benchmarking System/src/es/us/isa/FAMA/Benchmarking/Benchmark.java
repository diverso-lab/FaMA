package es.us.isa.FAMA.Benchmarking;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.Reasoner;

public class Benchmark {
	// private String resultFile;
	private QuestionTrader qt;
	private Collection<Experiment> exps;
	private Map<String, ReasonerQuestionPair> rqMap;

	public Benchmark() {
		qt = new QuestionTrader();
		exps = new LinkedList<Experiment>();
		rqMap = new HashMap<String, ReasonerQuestionPair>();
	}

	public void openBenchmarkFile(String fileName) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		Document experimentDoc = builder.parse(fileName);
		NodeList experimentList = experimentDoc.getChildNodes();
		if (experimentList.getLength() == 0) {
			throw new Exception("No root node exists");
		}

		Node root = null;
		for (int i = 0; i < experimentList.getLength(); i++) {
			root = experimentList.item(i);
			if (!root.getNodeName().equalsIgnoreCase("experiment"))
				root = null;
		}

		if (root == null)
			throw new Exception("No experiment root node has been found");

		/*
		 * Node resultFileNode =
		 * root.getAttributes().getNamedItem("resultFile"); if (resultFileNode
		 * == null) throw new
		 * Exception("A result file must be provided in experiment node");
		 * 
		 * resultFile = resultFileNode.getNodeValue();
		 */

		NodeList experimentNodes = root.getChildNodes();
		for (int i = 0; i < experimentNodes.getLength(); i++) {
			Node expNode = experimentNodes.item(i);
			String nodeType = expNode.getNodeName();
			if (nodeType.equalsIgnoreCase("question")) {
				processQuestionNode(expNode);
			} else if (nodeType.equalsIgnoreCase("executeDirectory")) {
				processExecuteDirectoryNode(expNode);
			} else if (nodeType.equalsIgnoreCase("executeRandom")) {
				processExecuteRandomNode(expNode);
			}
		}
	}

	public void execute() throws IOException {
		Iterator<Experiment> ite = exps.iterator();
		while (ite.hasNext()) {
			Experiment exp = ite.next();
			// exp.setResultFile(resultFile);
			Iterator<Entry<String, ReasonerQuestionPair>> itr = rqMap
					.entrySet().iterator();
			while (itr.hasNext()) {
				Entry<String, ReasonerQuestionPair> entry = itr.next();
				String colTitle = entry.getKey();
				Reasoner r = entry.getValue().getReasoner();
				Question q = entry.getValue().getQuestion();
				exp.addReasoner(colTitle, r, q);
			}
			exp.execute();
		}
	}

	private void processQuestionNode(Node expNode) {
		String questionId = null;
		Node questionIdNode = expNode.getAttributes().getNamedItem("id");
		if (questionIdNode != null)
			questionId = questionIdNode.getNodeValue();
		NodeList reasonerList = expNode.getChildNodes();
		if (questionId != null) {
			Class<Question> questionClass = qt.getQuestionById(questionId);
			if (questionClass != null) {
				for (int i = 0; i < reasonerList.getLength(); i++) {
					Node reasonerNode = reasonerList.item(i);
					NamedNodeMap reasonerAtts = reasonerNode.getAttributes();
					String colTitle = null;
					String reasonerId = null;
					Node colTitleNode = reasonerAtts.getNamedItem("colTitle");
					Node idNode = reasonerAtts.getNamedItem("id");
					if (idNode != null) {
						reasonerId = idNode.getNodeValue();
						Reasoner r = qt.getReasonerById(reasonerId);
						if (colTitleNode != null)
							colTitle = colTitleNode.getNodeValue();
						else
							colTitle = reasonerId;
						if (r != null) {
							Question q = r.getFactory().createQuestion(
									questionClass);
							this.addReasoner(colTitle, r, q);
						}
					}

				}
			}
		}
	}

	private void addReasoner(String colTitle, Reasoner r, Question q) {
		rqMap.put(colTitle, new ReasonerQuestionPair(r, q));

	}

	private void processExecuteDirectoryNode(Node expNode) {
		DirectoryExperiment exp = new DirectoryExperiment(qt);
		NamedNodeMap atts = expNode.getAttributes();
		Node dirNode = atts.getNamedItem("directory");
		// resultFile is a mandatory attribute as it has no sense to execute a
		// full directory
		// without writing the results in a file (it just consumes CPU time :D)
		Node resultFileNode = atts.getNamedItem("resultFile");
		if (dirNode != null && resultFileNode != null) {
			String dirName = dirNode.getNodeValue();
			String resultFile = resultFileNode.getNodeValue();
			exp.setDirectory(dirName);
			exp.setResultFile(resultFile);
			exps.add(exp);
		}
	}

	private void processExecuteRandomNode(Node expNode) {
		NodeList childNodes = expNode.getChildNodes();
		RandomExperiment exp = new RandomExperiment(qt);
		NamedNodeMap atts = expNode.getAttributes();
		// resultFile attribute is optional for random experiments, as it can be
		// used
		// to automatically generate feature models.
		Node resultFileNode = atts.getNamedItem("resultFile");
		if (resultFileNode != null) {
			exp.setResultFile(resultFileNode.getNodeValue());
		}
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			String nodeName = childNode.getNodeName();
			if (nodeName.equalsIgnoreCase("output")) {
				NamedNodeMap outputAtts = childNode.getAttributes();
				Node formatNode = outputAtts.getNamedItem("format");
				Node outputDirNode = outputAtts.getNamedItem("directory");
				String format = "xml";
				String outputDir = null;
				if (formatNode != null)
					format = formatNode.getNodeValue();
				if (outputDirNode != null)
					outputDir = outputDirNode.getNodeValue();
				else {
					java.util.Date date = new java.util.Date();
					outputDir = String.valueOf(date.getTime());
				}
				exp.setOutputDirectory(outputDir);
				exp.setToFile(true);
				exp.setFileExtension(format);
			} else if (nodeName.equalsIgnoreCase("random")) {
				NamedNodeMap randomAtts = childNode.getAttributes();
				Node widthNode = randomAtts.getNamedItem("width");
				Node heightNode = randomAtts.getNamedItem("height");
				Node chooseNode = randomAtts.getNamedItem("choose");
				Node dependenciesNode = randomAtts.getNamedItem("dependencies");
				Node instancesNode = randomAtts.getNamedItem("instances");
				Node seedNode = randomAtts.getNamedItem("seed");
				Node featuresNode = randomAtts.getNamedItem("features");
				int width, height, choose, dependencies, instances;
				int seed = -1;
				int features = 0;
				// optional fields
				if (featuresNode != null)
					features = Integer.parseInt(featuresNode.getNodeValue());
				if (seedNode != null)
					seed = Integer.parseInt(seedNode.getNodeValue());
				// mandatory fields
				if (widthNode != null) {
					width = Integer.parseInt(widthNode.getNodeValue());
					if (heightNode != null) {
						height = Integer.parseInt(heightNode.getNodeValue());
						if (chooseNode != null) {
							choose = Integer
									.parseInt(chooseNode.getNodeValue());
							if (dependenciesNode != null) {
								dependencies = Integer
										.parseInt(dependenciesNode
												.getNodeValue());
								if (instancesNode != null) {
									instances = Integer.parseInt(instancesNode
											.getNodeValue());
									Characteristics chars = null;
									if (seed == -1)
										chars = new Characteristics(width,
												height, choose, dependencies,
												instances, features);
									else
										chars = new Characteristics(width,
												height, choose, dependencies,
												instances, features, seed);
									exp.addCharacteristics(chars);
								}
							}
						}
					}
				}
			}

			exps.add(exp);
		}
	}

	private class ReasonerQuestionPair {
		Reasoner r;
		Question q;

		ReasonerQuestionPair(Reasoner r, Question q) {
			this.r = r;
			this.q = q;
		}

		public Reasoner getReasoner() {
			return r;
		}

		public Question getQuestion() {
			return q;
		}
	}

}
