package es.us.isa.config.generator;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.domain.Domain;
import es.us.isa.FAMA.models.domain.RealDomain;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.parser.FMFParser;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.util.Node;
import es.us.isa.util.Tree;

public class ConfigSelectionMethod {

	private FMFParser parser;
	private FAMAAttributedFeatureModel fm;
	private Map<VariabilityElement, EnumeratedVariableLevel> featureLevels;
	private Map<GenericAttribute, DomainVariableLevel> attLevels;
	private Map<GenericAttribute, DomainVariableLevel> attEquals;
	
	public ConfigSelectionMethod(FAMAAttributedFeatureModel fm,
			Map<VariabilityElement, EnumeratedVariableLevel> featureLevels,
			Map<GenericAttribute, DomainVariableLevel> attLevels) {
		super();
		this.fm = fm;
		this.featureLevels = featureLevels;
		this.attLevels = attLevels;
		parser = new FMFParser();
	}

	public Map<VariabilityElement, EnumeratedVariableLevel> getFeatureLevels() {
		return featureLevels;
	}

	public void setFeatureLevels(
			Map<VariabilityElement, EnumeratedVariableLevel> featureLevels) {
		this.featureLevels = featureLevels;
	}

	public Map<GenericAttribute, DomainVariableLevel> getAttLevels() {
		return attLevels;
	}

	public void setAttLevels(Map<GenericAttribute, DomainVariableLevel> attLevels) {
		this.attLevels = attLevels;
	}
	
	public FAMAAttributedFeatureModel getFm() {
		return fm;
	}

	public void setFm(FAMAAttributedFeatureModel fm) {
		this.fm = fm;
	}

	public ExtendedConfiguration generateRandomConfig(){
		ExtendedConfiguration result = new ExtendedConfiguration();
		Set<Entry<VariabilityElement, EnumeratedVariableLevel>> entrySet1 = featureLevels.entrySet();
		for (Entry<VariabilityElement, EnumeratedVariableLevel> e:entrySet1){
			String value = e.getValue().getRandomValue();
			Node<String> node = new Node<String>();
			node.setData(value);
			Tree<String> tree = new Tree<String>();
			tree.setRootElement(node);
			result.addAttConfig(tree);
//			AttributedFeature f = fm.searchFeatureByName(value);
//			result.addElement(f, 1);
		}
		
		Set<Entry<GenericAttribute, DomainVariableLevel>> entrySet2 = attLevels.entrySet();
		for (Entry<GenericAttribute, DomainVariableLevel> e:entrySet2){
			//XXX cuidao aqui. puede haber problemas en el reasoner
			//al convertir de enteros a doubles o viceversa
			Domain d = e.getKey().getDomain();
			String constraint;
			if (d instanceof RealDomain){
				double value = e.getValue().getRandomValue();
				constraint = ""+e.getKey().getFullName() + " >= " + value + ";";
			}
			else{
				int value = e.getValue().getRandomValue();
				constraint = ""+e.getKey().getFullName() + " >= " + value + ";";
			}
			Tree<String> tree = parser.parseConstraint(constraint);
			result.addAttConfig(tree);
		}
		
		return result;
	}
	
	
	
}
