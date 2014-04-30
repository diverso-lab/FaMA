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
/*
 * Created on 04-Dec-2004
 */
package es.us.isa.FAMA.models.FAMAfeatureModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import es.us.isa.FAMA.errors.Observation;
import es.us.isa.FAMA.errors.Observation.ErrorLevel;
import es.us.isa.FAMA.models.FAMAfeatureModel.OrderingHeuristics.OrderingHeuristic;
import es.us.isa.FAMA.models.FAMAfeatureModel.errors.DeadFeatureObservation;
import es.us.isa.FAMA.models.FAMAfeatureModel.errors.FalseMandatoryObservation;
import es.us.isa.FAMA.models.FAMAfeatureModel.errors.VoidFMObservation;
import es.us.isa.FAMA.models.FAMAfeatureModel.errors.WrongCardinalityObservation;
import es.us.isa.FAMA.models.FAMAfeatureModel.transformations.FeatureModelTransform;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.GenericRelation;

/**
 * @author   trinidad,jagalindo.
 */
public class FAMAFeatureModel extends GenericFeatureModel{
	
	protected Feature root;
	protected List<Dependency> dependencies;
	private OrderingHeuristic orderingHeuristic=null;
	public FAMAFeatureModel(){
		super();
		super.attach(new FeatureModelTransform());
		root = null;
		dependencies = new ArrayList<Dependency>();
	}
	
	public FAMAFeatureModel(Feature root){
		this();
		this.root = root;
	}
	
	public FAMAFeatureModel(FAMAFeatureModel fm) {
		this();
		this.root = fm.getRoot();
	}
	
	/**
	 * @return
	 * @uml.property  name="root"
	 */
	public Feature getRoot() {
		return root;
	}
	
	/**
	 * @param root
	 * @uml.property  name="root"
	 */
	public void setRoot(Feature root) {
		this.root = root;
	}
	
	public int getFeaturesNumber() {
		int res = 0;
		if(root != null)
			res = 1;
			res = getFeatures().size();
		return res;
	}
	
	private int getFeaturesNumber(Feature f){
		int res = 0;
		Iterator<Relation> it = f.getRelations();
		while(it.hasNext()){
			Relation r = it.next();
			Iterator<Feature> it2 = r.getDestination();
			while(it2.hasNext()){
				Feature f2 = it2.next();
				res += getFeaturesNumber(f2); 
			}
		}		
		return res;
	}
		
	public Collection<Feature> getFeatures() {
		Collection<Feature> res = new ArrayList<Feature>();
		getFeatures(res, root);
		return res;
	}
	
	private void getFeatures(Collection<Feature> c, Feature f){
		if(!c.contains(f)){
		c.add(f);
		}
		Iterator<Relation> it = f.getRelations();
		while(it.hasNext()){
			Relation r = it.next();
			Iterator<Feature> it2 = r.getDestination();
			while(it2.hasNext()){
				Feature f2 =  it2.next();
				getFeatures(c,f2); 
			}
		}
	}
	
	public int getNumberOfLevels () {
		Integer res = new Integer(0);
		getNumberOfLevels(res, 0, root);
		return res.intValue() + 1;
	}
	
	private void getNumberOfLevels(Integer maxlevel, int level, Feature f){
		level++;
		if(maxlevel.intValue() < level) 
			maxlevel = new Integer(level);
		Iterator<Relation> it = f.getRelations();
		while(it.hasNext()){
			Relation r = it.next();
			Iterator<Feature> it2 = r.getDestination();
			while(it2.hasNext()){
				Feature f2 = it2.next();
				getNumberOfLevels(maxlevel, level, f2);
			}
		}
	}
	
	public void addDependency (Dependency d) {
		dependencies.add(d);
	}

	/**
	 * @return
	 * @uml.property  name="dependencies"
	 */
	public Iterator<Dependency> getDependencies () {
		return dependencies.iterator();
	}
	
	public int getNumberOfDependencies () {
		return dependencies.size();
	}
	
	public Feature searchFeatureByName (String name) {
		Feature res = null;
		if(root != null)
			res = searchFeatureByName (name, root);
		return res;
	}
	
	private Feature searchFeatureByName(String name, Feature f){
		Feature res = null;
		boolean encontrado = false;
		if ( f.getName().equalsIgnoreCase(name) ) {
			res = f;
		}else{			
			Iterator<Relation> it = f.getRelations();
			while(it.hasNext() && !encontrado){
				Relation r = it.next();
				Iterator<Feature> it2 = r.getDestination();
				while(it2.hasNext() && !encontrado){
					Feature f2 = it2.next();
					res = searchFeatureByName(name, f2);
					if (res != null)
						encontrado = true;
				}
			}			
		}
		return res;		
	}
	
	public Relation searchRelationByName(String name){
		Relation res = null;
		if(root != null)
			res = searchRelationByName(name, root); 
		return res;
	}
	
	private Relation searchRelationByName(String name, Feature f){
		Relation res = null;
		boolean encontrado = false;		
					
		Iterator<Relation> it = f.getRelations();
		while(it.hasNext() && !encontrado){
			Relation r =  it.next();
			
			if(r.getName().equals(name)){
				encontrado = true;
				res = r;
			}
				
			Iterator<Feature> it2 = r.getDestination();
			while(it2.hasNext() && !encontrado){
				Feature f2 = it2.next();
				res = searchRelationByName(name, f2);
				if (res != null)
					encontrado = true;
			}
		}			
		
		return res;
	}
	
	public String toString() {
		String res = "Feature model: " + root;
		res += "\r\n" + dependencies; 
		return res;
	}

	@Override
	public Collection<GenericRelation> getRelations() {
		Collection<GenericRelation> res = new HashSet<GenericRelation>();
		getRelations(res, root);
		return res;
	}

	private void getRelations(Collection<GenericRelation> c, Feature f) {
		Iterator<Relation> it = f.getRelations();
		while(it.hasNext()){
			Relation r = (Relation) it.next();
			c.add(r);
			Iterator<Feature> it2 = r.getDestination();
			while(it2.hasNext()){
				Feature f2 = (Feature) it2.next();
				getRelations(c,f2); 
			}
		}
	}

	@Override
	public Collection<Observation> getObservations(ErrorLevel level) {
		Collection<Observation> res = new LinkedList<Observation>();
		Observation voidObs = new VoidFMObservation(root);
		if (level.compareTo(ErrorLevel.CRITICAL_ERROR)<=0) {
			res.add(voidObs);
		}
		getObservations(res,root,level);
		// all the observations are discarded in case feature model is void
		Iterator<Observation> ito = res.iterator();
		while (ito.hasNext()) {
			Observation obs = ito.next();
			// all are carried but itself
			if (!(obs instanceof VoidFMObservation))
				voidObs.addDiscardedObs(obs);
		}
		return res;
	}

	private void getObservations(Collection<Observation> res, Feature f, ErrorLevel level) {
		// check if feature is selectable
		Observation dfobs = new DeadFeatureObservation(f);
		if (level.compareTo(ErrorLevel.ERROR)<=0) {
			res.add(dfobs);
		}
		// in a non-mandatory relationship, check if it is false-mandatory
		if (level.compareTo(ErrorLevel.WARNING)<=0) {
			Relation parentRelation = f.getParent();
			// lazy evaluation to avoid NullPointerException for the root feature
			if (parentRelation != null && !isMandatory(parentRelation)) {
				Observation fmfo = new FalseMandatoryObservation(f);
				res.add(fmfo);
				dfobs.addDiscardedObs(fmfo);
			}
		}
		
		Iterator<Relation> it = f.getRelations();
		while(it.hasNext()){
			Relation r = (Relation) it.next();
			// in case we find a set relationship, generate wrong cards obs
			if (level.compareTo(ErrorLevel.WARNING)<=0 && r.getNumberOfDestination() > 1) {
				Iterator<Cardinality> itc = r.getCardinalities();
				while (itc.hasNext()) {
					Cardinality card = itc.next();
					int min = card.getMin();
					int max = card.getMax();
					for (;min<=max;min++) {
						res.add(new WrongCardinalityObservation(r,min));
					}
				}
			}
			Iterator<Feature> it2 = r.getDestination();
			Collection<Observation> carriedObs = new LinkedList<Observation>();
			while(it2.hasNext()){
				Feature f2 = (Feature) it2.next();
				getObservations(carriedObs,f2,level);
			}
			res.addAll(carriedObs);
			// child observations to detect dead features are carried in case parent feature is dead;
			// child obs to detect false-mandatory and wrong cards are discarded
			Iterator<Observation> ito = carriedObs.iterator();
			while (ito.hasNext()) {
				Observation obs = ito.next();
				if (obs instanceof DeadFeatureObservation)
					dfobs.addCarriedError(obs);
				else
					dfobs.addDiscardedObs(obs);
			}
		}
	}

	private boolean isMandatory(Relation parentRelation) {
		boolean res = false;
		if (parentRelation.getNumberOfDestination()==1) {
			Iterator<Cardinality> itc = parentRelation.getCardinalities();
			if (itc.hasNext()) {
				Cardinality card = itc.next();
				if (card.getMin() == 1 && card.getMax() == 1 && !itc.hasNext())
					res = true;
			}
		}
		return res;
	}

	public void setOrderingHeuristic(OrderingHeuristic orderingHeuristic) {
		this.orderingHeuristic = orderingHeuristic;
	}

	public OrderingHeuristic getOrderingHeuristic() {
		return orderingHeuristic;
	}
}
