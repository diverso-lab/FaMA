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

package es.us.isa.FAMA.models.FAMAfeatureModel.transformations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.AtomicSetTransform;

public class AtomicSet implements AtomicSetTransform {
	
	private int counter;
	
	private int relationCounter;
	
	private VariabilityModel vm = null;
	
	private Map<String, Collection<VariabilityElement>> atomicSets;
	
	private Map<Feature, Feature> featuresToAtomics;

	public AtomicSet(){
		featuresToAtomics = new HashMap<Feature, Feature>();
		atomicSets = new HashMap<String, Collection<VariabilityElement>>();
		vm = null;
		counter = 0;
		relationCounter = 0;
	}
	
	public Map<String, Collection<VariabilityElement>> getAtomicSets()  {
		if (this.vm == null) {
			throw new FAMAException("You should call doTransform and undo " +
					"transform only");
		}
		return this.atomicSets;
	}

	private void computeAS(Feature current, Feature newFeature,
			FAMAFeatureModel currentFM, FAMAFeatureModel newFM, String atomic) {

		featuresToAtomics.put(current, newFeature);
		Iterator<Relation> it = current.getRelations();
		Collection<VariabilityElement> col = atomicSets.get(atomic);

		while (it.hasNext()) {
			Relation rel = it.next();
			if (rel.isMandatory() && rel.getNumberOfDestination() == 1) {
				// mandatory relation
				Feature f = rel.getDestinationAt(0);
				col.add(f);

				computeAS(f, newFeature, currentFM, newFM, atomic);
			} 
			else {
				Iterator<Feature> itF = rel.getDestination();
				// primero determinamos el tipo de relacion
				if (rel.isOptional() && rel.getNumberOfDestination() == 1) {
					// es optional
					counter++;
					Feature f = itF.next();
					String newAtomic = "AtomicSet " + counter;

					// a�adimos el nuevo atomicSet
					Collection<VariabilityElement> atomicSet = new HashSet<VariabilityElement>();
					atomicSet.add(f);
					atomicSets.put(newAtomic, atomicSet);

					Feature atomicFeature = new Feature(newAtomic);
					relationCounter++;
					Relation r = new Relation("R-" + relationCounter);

					r.addDestination(atomicFeature);
					r.addCardinality(new Cardinality(0, 1));
					newFeature.addRelation(r);

					computeAS(f, atomicFeature, currentFM, newFM, newAtomic);
				} 
				else if (rel.getNumberOfDestination() > 1) {
					// es Set o OR
					// tenemos que crear un atomic set por cada hijo
					//el tratamiento es igual ya sea tipo Set o tipo Or
					Iterator<Cardinality> cars = rel.getCardinalities();
					Cardinality c = null;
					relationCounter++;
					Relation r = new Relation("R-" + relationCounter);
					newFeature.addRelation(r);
					while (cars.hasNext()) {
						// a�adimos la cardinalidad
						c = cars.next();
						r.addCardinality(c);
					}
					
					while (itF.hasNext()) {
						Feature f = itF.next();
						counter++;
						String newAtomic = "AtomicSet " + counter;

						// a�adimos el nuevo atomicSet
						Collection<VariabilityElement> atomicSet = new HashSet<VariabilityElement>();
						atomicSet.add(f);
						atomicSets.put(newAtomic, atomicSet);

						Feature atomicFeature = new Feature(newAtomic);
						r.addDestination(atomicFeature);
						computeAS(f, atomicFeature, currentFM, newFM, newAtomic);
					}

				}
			}
		}
	}

	public VariabilityModel doTransform(VariabilityModel vm){
		counter = 0;
		relationCounter = 0;
		this.vm = vm;
		//inicializamos los atomic sets con la raiz
		String atomic1 = "AtomicSet "+counter;
		atomicSets = new HashMap<String, Collection<VariabilityElement>>();
		featuresToAtomics = new HashMap<Feature, Feature>();
		Collection<VariabilityElement> atomicSet = new HashSet<VariabilityElement>();
		FAMAFeatureModel fm = (FAMAFeatureModel) vm;//atomicSet.add()
		atomicSet.add(fm.getRoot());
		atomicSets.put(atomic1, atomicSet);
		
		VariabilityModel vmTransformed = new FAMAFeatureModel(new Feature(atomic1));
		makeAtomicSets(vm,vmTransformed,atomic1);
		
		return vmTransformed;
	}

	private void makeAtomicSets(VariabilityModel vm2,
			VariabilityModel vmTransformed, String atomic1) {
		
		FAMAFeatureModel fm1 = (FAMAFeatureModel) vm2;
		FAMAFeatureModel fm2 = (FAMAFeatureModel) vmTransformed;
		Feature root1 = fm1.getRoot();
		Feature root2 = fm2.getRoot();
		
		//recorre el arbol
		computeAS(root1,root2,fm1,fm2,atomic1);
		//y crea las dependencias
		updateDependencies(fm1, fm2);
		
	}

	private void updateDependencies(FAMAFeatureModel fm1, FAMAFeatureModel fm2) {
		Iterator<Dependency> it =  fm1.getDependencies();
		while (it.hasNext()){
			Dependency c = it.next();
			Dependency aux;
			Feature atomicOrigin = featuresToAtomics.get(c.getOrigin());
			Feature atomicDest = featuresToAtomics.get(c.getDestination());
			if (c instanceof RequiresDependency){
				aux = new RequiresDependency(c.getName());
			}
			else{
				aux = new ExcludesDependency(c.getName());
			}
			aux.setOrigin(atomicOrigin);
			aux.setDestination(atomicDest);
			fm2.addDependency(aux);
		}		
	}

	public VariabilityModel undoTransform() {
		return vm;
	}

	public Product transformProduct(Product origin){

		Product res = new Product();
		Iterator<GenericFeature> it = origin.getFeatures().iterator();
		while(it.hasNext()){
			GenericFeature Pfeat=it.next();
			Iterator<Entry<Feature,Feature>> it2 = featuresToAtomics.entrySet().iterator();
			while(it2.hasNext()){
				Entry<Feature,Feature> next=it2.next();
				if(next.getKey().getName().equals(Pfeat.getName())){
					res.addFeature(next.getValue());
				}
			}
			
		}
		return res;
	}
	
	
}
