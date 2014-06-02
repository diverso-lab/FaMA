package es.us.isa.FAMA.models.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import antlr.CommonAST;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.parser.Atributo;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.util.Node;
import es.us.isa.util.Tree;

public class ExtendedConfigParser implements ConfigParser {

	private Map<String, AST> mapFeatures;
	private Map<String, Collection<Atributo>> attributes;
	private GenericAttributedFeatureModel afm;

	public ExtendedConfigParser(GenericAttributedFeatureModel model) {
		// model.s
		this.afm = model;
		mapFeatures = new HashMap<String, AST>();
		attributes = new HashMap<String, Collection<Atributo>>();
		doPreParse();
	}

	public ConfigParserResult parseConfiguration(String pathToFile) {
		ConfigParserResult result = new ConfigParserResult();
		FileInputStream in;
		try {
			in = new FileInputStream(pathToFile);
			ConfigAnalex an = new ConfigAnalex(in);
			result = parseConfiguration(an);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		return result;
	}

	public ConfigParserResult parseConfigurationString(String config) {
		StringReader reader = new StringReader(config);
		ConfigAnalex analex = new ConfigAnalex(reader);
		ConfigParserResult result = parseConfiguration(analex);
		return result;
	}

	private ConfigParserResult parseConfiguration(ConfigAnalex an) {
		ExtendedConfiguration config = null;
		ConfigParserResult result = new ConfigParserResult();
		try {
			ConfigAnasint as = new ConfigAnasint(an);
			as.setASTNodeClass("es.us.isa.FAMA.parser.MyAST");
			as.setAttributes(attributes);
			as.setFeatures(mapFeatures);
			Collection<String> errors = as.conjunto_constraints();
			ConfigTreeParser treeparser = new ConfigTreeParser();
			treeparser.setASTNodeClass("es.us.isa.FAMA.parser.MyAST");
			config = (ExtendedConfiguration) treeparser.entrada(as.getAST());
			result.setErrors(errors);
			result.setConfig(config);
		} catch (RecognitionException e) {
			e.printStackTrace();
		} catch (TokenStreamException e) {
			e.printStackTrace();
		}

		return result;
	}

	private void doPreParse() {
		Collection<? extends GenericAttributedFeature> feats = afm
				.getAttributedFeatures();
		for (GenericAttributedFeature f : feats) {
			mapFeatures.put(f.getName(), new CommonAST());
			Collection<? extends GenericAttribute> atts = f.getAttributes();
			Collection<Atributo> atributos = new LinkedList<Atributo>();
			for (GenericAttribute att : atts) {
				// XXX att type does not matter
				atributos.add(new Atributo(att.getName(), 1));
			}
			attributes.put(f.getName(), atributos);
		}
	}

	public Tree<String> astToTree(AST ast) {
		Node<String> root = new Node<String>();
		Tree<String> t = new Tree<String>(root);
		walkTree(ast, root);
		return t;
	}

	private void walkTree(AST ast, Node<String> node) {

		node.setData(ast.getText());
		int children = ast.getNumberOfChildren();
		if (children > 0) {
			AST child = ast.getFirstChild();
			Node<String> n = new Node<String>();
			node.addChild(n);
			walkTree(child, n);
			for (int i = 1; i < children; i++) {
				AST aux = child.getNextSibling();
				n = new Node<String>();
				node.addChild(n);
				walkTree(aux, n);
			}
		}

	}
}
