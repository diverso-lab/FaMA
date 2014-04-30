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
package es.us.isa.FAMA.models.FAMAAttributedfeatureModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import es.us.isa.FAMA.errors.Observation;
import es.us.isa.FAMA.errors.Observation.ErrorLevel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.errors.DeadFeatureObservation;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.errors.FalseMandatoryObservation;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.errors.VoidFMObservation;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.errors.WrongCardinalityObservation;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.transformations.AttributedFeatureModelTransform;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.Constraint;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.util.Node;

/**
 * @author   trinidad.
 */
public class FAMAAttributedFeatureModel extends GenericAttributedFeatureModel{
	
	protected AttributedFeature root;
	
	//protected List<Dependency> dependencies;
	
	protected List<Constraint> constraints;
	
	public FAMAAttributedFeatureModel(){
		super();
		//TODO aqui habra que tocar algo de la transformacion
		super.attach(new AttributedFeatureModelTransform());
		root = null;
		//dependencies = new ArrayList<Dependency>();
		constraints = new ArrayList<Constraint>();
	}
	
	public FAMAAttributedFeatureModel(AttributedFeature root){
		this();
		this.root = root;
	}
	
	public FAMAAttributedFeatureModel(FAMAAttributedFeatureModel fm) {
		this();
		this.root = fm.getRoot();
	}
	
	/**
	 * @return
	 * @uml.property  name="root"
	 */
	public AttributedFeature getRoot() {
		return root;
	}
	
	/**
	 * @param root
	 * @uml.property  name="root"
	 */
	public void setRoot(AttributedFeature root) {
		this.root = root;
	}
	
	public int getFeaturesNumber() {
		int res = 0;
		if(root != null)
			res = getFeaturesNumber(root);
		return res;
	}
	
	private int getFeaturesNumber(AttributedFeature f){
		int res = 1;
		Iterator<Relation> it = f.getRelations();
		while(it.hasNext()){
			Relation r = it.next();
			Iterator<AttributedFeature> it2 = r.getDestination();
			while(it2.hasNext()){
				AttributedFeature f2 = it2.next();
				res += getFeaturesNumber(f2); 
			}
		}		
		return res;
	}
		
	public Collection<AttributedFeature> getAttributedFeatures() {
		Collection<AttributedFeature> res = new HashSet<AttributedFeature>();
		getFeatures(res, root);
		return res;
	}
	
	private void getFeatures(Collection<AttributedFeature> c, AttributedFeature f){
		c.add(f);
		Iterator<Relation> it = f.getRelations();
		while(it.hasNext()){
			Relation r = it.next();
			Iterator<AttributedFeature> it2 = r.getDestination();
			while(it2.hasNext()){
				AttributedFeature f2 =  it2.next();
				getFeatures(c,f2); 
			}
		}
	}
	
	public int getNumberOfLevels () {
		Integer res = new Integer(0);
		getNumberOfLevels(res, 0, root);
		return res.intValue() + 1;
	}
	
	private void getNumberOfLevels(Integer maxlevel, int level, AttributedFeature f){
		level++;
		if(maxlevel.intValue() < level) 
			maxlevel = new Integer(level);
		Iterator<Relation> it = f.getRelations();
		while(it.hasNext()){
			Relation r = it.next();
			Iterator<AttributedFeature> it2 = r.getDestination();
			while(it2.hasNext()){
				AttributedFeature f2 = it2.next();
				getNumberOfLevels(maxlevel, level, f2);
			}
		}
	}
	
//	public void addDependency (Dependency d) {
//		dependencies.add(d);
//	}
//	
	public void addConstraint(Constraint c){
		constraints.add(c);
	}

	/**
	 * @return
	 * @uml.property  name="dependencies"
	 */
//	public Iterator<Dependency> getDependencies () {
//		return dependencies.iterator();
//	}
//	
//	public int getNumberOfDependencies () {
//		return dependencies.size();
//	}
	
	public int getNumberOfConstraints(){
		return constraints.size();
	}
	
	public Collection<Constraint> getConstraints(){
		return constraints;
	}
	
	public AttributedFeature searchFeatureByName (String name) {
		AttributedFeature res = null;
		if(root != null)
			res = searchFeatureByName (name, root);
		return res;
	}
	
	private AttributedFeature searchFeatureByName(String name, AttributedFeature f){
		AttributedFeature res = null;
		boolean encontrado = false;
		if ( f.getName().equalsIgnoreCase(name) ) {
			res = f;
		}else{			
			Iterator<Relation> it = f.getRelations();
			while(it.hasNext() && !encontrado){
				Relation r = it.next();
				Iterator<AttributedFeature> it2 = r.getDestination();
				while(it2.hasNext() && !encontrado){
					AttributedFeature f2 = it2.next();
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
	
	private Relation searchRelationByName(String name, AttributedFeature f){
		Relation res = null;
		boolean encontrado = false;		
					
		Iterator<Relation> it = f.getRelations();
		while(it.hasNext() && !encontrado){
			Relation r =  it.next();
			
			if(r.getName().equals(name)){
				encontrado = true;
				res = r;
			}
				
			Iterator<AttributedFeature> it2 = r.getDestination();
			while(it2.hasNext() && !encontrado){
				AttributedFeature f2 = it2.next();
				res = searchRelationByName(name, f2);
				if (res != null)
					encontrado = true;
			}
		}			
		
		return res;
	}
	
	public String toString() {
		String res = "Feature model:\n" + root.fmToString();
		
		Iterator<Constraint> it1 = constraints.iterator();
		if (it1.hasNext()){
			res+= "Dependencies:\n";
		}
		while (it1.hasNext()){
			res+=it1.next()+"\n";
		}
		
//		Iterator<Constraint> it2 = constraints.iterator();
//		if (it2.hasNext()){
//			res+= "Attributes relations:\n";
//		}
//		while (it2.hasNext()){
//			res+=it2.next()+"\n";
//		}
		
		return res;
	}

	@Override
	public Collection<GenericRelation> getRelations() {
		Collection<GenericRelation> res = new HashSet<GenericRelation>();
		getRelations(res, root);
		res.addAll(constraints);
		return res;
	}

	private void getRelations(Collection<GenericRelation> c, AttributedFeature f) {
		Iterator<Relation> it = f.getRelations();
		while(it.hasNext()){
			Relation r = (Relation) it.next();
			c.add(r);
			Iterator<AttributedFeature> it2 = r.getDestination();
			while(it2.hasNext()){
				AttributedFeature f2 = (AttributedFeature) it2.next();
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

	//TODO modificar este metodo para incluir observaciones propias de los
	//extended feature models
	private void getObservations(Collection<Observation> res, AttributedFeature f, ErrorLevel level) {
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
			Iterator<AttributedFeature> it2 = r.getDestination();
			Collection<Observation> carriedObs = new LinkedList<Observation>();
			while(it2.hasNext()){
				AttributedFeature f2 = (AttributedFeature) it2.next();
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
	
	public void addAttributeRelationship(ComplexConstraint r){
		if (isValidAttRelation(r)){
			constraints.add(r);
		}
		else{
			throw new IllegalArgumentException("Illegal Attribute Relationship: "+r);
		}
		
	}

	private boolean isValidAttRelation(ComplexConstraint r) {
		Node<String> n = r.getAST().getRootElement();
		return isValidAttRelation(n);
	}

	private boolean isValidAttRelation(Node<String> n) {
		boolean res = true;
		if (n.getNumberOfChildren() == 0){
			//caso base
			res = isValidAttribute(n.getData()) || checkIfNumber(n.getData());
		}
		else{
			//comprobamos la validez de cada uno de los hijos
			Iterator<Node<String>> it = n.getChildren().iterator();
			while (it.hasNext() && res){
				Node<String> node = it.next();
				res = res && isValidAttRelation(node);
			}
		}
		return res;
	}

	private boolean isValidAttribute(String data) {
		int index = data.indexOf('.');
		if (index > 0){
			//notacion feature.attribute
			String feat = data.substring(0,index);
			//eliminamos el '.'
			String att = data.substring(index + 1);
			AttributedFeature f = this.searchFeatureByName(feat);
			if (f != null){
				return (f.searchAttributeByName(att) != null);
			}
		}
		return false;
	}
	
	private boolean checkIfNumber(String in) {
        try {
            Integer.parseInt(in);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

}
