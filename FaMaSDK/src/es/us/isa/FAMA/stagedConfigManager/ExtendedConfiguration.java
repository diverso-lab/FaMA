package es.us.isa.FAMA.stagedConfigManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
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
		// generates the config in the plain-text format
		String result = "";
		
		if (!elements.isEmpty()){
			result += "## Features \n";
			for (Entry<VariabilityElement,Integer> e:elements.entrySet()){
				if (e.getKey() instanceof GenericFeature)
				{
					if (e.getValue() == 0)
					{
						result += "NOT "+e.getKey().getName()+";\n";
					}
					else
					{
						result += e.getKey().getName()+";\n";
					}
				}
			}
		}
		
		if (!attValues.isEmpty()){
			result += "## Attributes \n";
			for (Entry<GenericAttribute,Double> e:attValues.entrySet()){
				if (e.getKey() instanceof GenericAttribute)
				{
					result += e.getKey().getName()+" == "+e.getValue()+";\n";
				}
			}
		}
		
		if (!attConfigs.isEmpty()){
			result += "## Complex Constraints \n";
			for (Tree<String> t:attConfigs){
				result += t.toString()+";\n";
			}
		}
		
//		String result = super.toString();
//		Set<Entry<GenericAttribute,Double>> entries = attValues.entrySet();
//		for (Entry<GenericAttribute,Double> e:entries){
//			result+= "\n" +e.getKey().getFullName() + " = "+e.getValue();
//		}
//		for (Tree<String> tree:attConfigs){
//			result += "\n "+tree;
//		}
		return result;
	}
}
