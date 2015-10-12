package es.us.isa.FAMA.stagedConfigManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.util.Tree;
/**
 * Extended configuration for attributes and cardinalities.
 * Just arithmetic operators are allowed (>=, > , <=, <, ==, !=)
 * @author jesus
 *
 */

public class ExtendedConfiguration extends Configuration{
	
	private Collection<Tree<String>> attConfigs;
	
	private Map<GenericAttribute,Double> attValues;
	
	public ExtendedConfiguration(){
		super();
		attConfigs = new LinkedList<Tree<String>>();
		attValues = new HashMap<GenericAttribute, Double>();
	}

	public void addAttConfig(Tree<String> config){
		attConfigs.add(config);
	}
	
	public Collection<Tree<String>> getAttConfigs() {
		return attConfigs;
	}

	public void setAttConfigs(Collection<Tree<String>> attConfigs) {
		this.attConfigs = attConfigs;
	}
	
	public void addAttValue(GenericAttribute att, double val){
		attValues.put(att, val);
	}
	
	public Map<GenericAttribute, Double> getAttValues() {
		return attValues;
	}

	public void setAttValues(Map<GenericAttribute, Double> attValues) {
		this.attValues = attValues;
	}

	public String toString(){
		String result = super.toString();
		Set<Entry<GenericAttribute,Double>> entries = attValues.entrySet();
		for (Entry<GenericAttribute,Double> e:entries){
			result+= "\n" +e.getKey().getFullName() + " = "+e.getValue();
		}
		for (Tree<String> tree:attConfigs){
			result += "\n "+tree;
		}
		return result;
	}
}
