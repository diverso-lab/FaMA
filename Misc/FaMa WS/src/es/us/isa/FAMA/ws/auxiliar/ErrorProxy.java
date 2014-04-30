package es.us.isa.FAMA.ws.auxiliar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import es.us.isa.FAMA.errors.Explanation;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;


public class ErrorProxy {

	private Map<String,Object> observations;
	private Collection<ExplanationProxy> explanations;
	
	public ErrorProxy(){
		observations = new HashMap<String, Object>();
		explanations = new LinkedList<ExplanationProxy>();
	}

	public Map<String, Object> getObservations() {
		return observations;
	}

	public Collection<ExplanationProxy> getExplanations() {
		return explanations;
	}
	
	public void addExplanation(ExplanationProxy exp){
		explanations.add(exp);
	}
	
	public void addObservation(String s, Object o){
		observations.put(s, o);
	}
	
	public void setObservations(Map<? extends VariabilityElement,Object> obs){
		Iterator<?> it = obs.entrySet().iterator();
		while (it.hasNext()){
			Entry<? extends VariabilityElement,Object> e = (Entry<? extends VariabilityElement, Object>) it.next();
			observations.put(e.getKey().getName(), e.getValue());
		}
	}
	
	public void setExplanations(Collection<Explanation> exps){
		for (Explanation e:exps){
			ExplanationProxy p = new ExplanationProxy();
			Collection<GenericRelation> rels = e.getRelations();
			for (GenericRelation r: rels){
				p.addRelationship(r.getName());
			}
			explanations.add(p);
		}
		
	}
	
}
