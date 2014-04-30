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
package es.us.isa.FAMA.models.FAMAAttributedfeatureModel.transformations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.Constraint;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.AtomicSetTransform;

//TODO hay que actualizar esta clase tras el refactoring
public class AtomicSet implements AtomicSetTransform {
	
	private int counter;
	
	private int relationCounter;
	
	private VariabilityModel vm = null;
	
	private Map<String, Collection<VariabilityElement>> atomicSets;
	
	private Map<AttributedFeature, AttributedFeature> featuresToAtomics;

	public AtomicSet(){
		featuresToAtomics = new HashMap<AttributedFeature, AttributedFeature>();
		atomicSets = new HashMap<String, Collection<VariabilityElement>>();
		vm = null;
		counter = 0;
		relationCounter = 0;
	}
	
	public Map<String, Collection<VariabilityElement>> getAtomicSets() throws FAMAException {
		if (this.vm == null) {
			throw new FAMAException("You should call doTransform and undo " +
					"transform only");
		}
		return this.atomicSets;
	}

	private void computeAS(AttributedFeature current, AttributedFeature newFeature,
			FAMAAttributedFeatureModel currentFM, FAMAAttributedFeatureModel newFM, String atomic) {

		featuresToAtomics.put(current, newFeature);
		Iterator<Relation> it = current.getRelations();
		Collection<VariabilityElement> col = atomicSets.get(atomic);

		while (it.hasNext()) {
			Relation rel = it.next();
			if (rel.isMandatory() && rel.getNumberOfDestination() == 1) {
				// mandatory relation
				AttributedFeature f = rel.getDestinationAt(0);
				col.add(f);

				computeAS(f, newFeature, currentFM, newFM, atomic);
			} 
			else {
				Iterator<AttributedFeature> itF = rel.getDestination();
				// primero determinamos el tipo de relacion
				if (rel.isOptional() && rel.getNumberOfDestination() == 1) {
					// es optional
					counter++;
					AttributedFeature f = itF.next();
					String newAtomic = "AtomicSet " + counter;

					// a�adimos el nuevo atomicSet
					Collection<VariabilityElement> atomicSet = new HashSet<VariabilityElement>();
					atomicSet.add(f);
					atomicSets.put(newAtomic, atomicSet);

					AttributedFeature atomicFeature = new AttributedFeature(newAtomic);
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
						AttributedFeature f = itF.next();
						counter++;
						String newAtomic = "AtomicSet " + counter;

						// a�adimos el nuevo atomicSet
						Collection<VariabilityElement> atomicSet = new HashSet<VariabilityElement>();
						atomicSet.add(f);
						atomicSets.put(newAtomic, atomicSet);

						AttributedFeature atomicFeature = new AttributedFeature(newAtomic);
						r.addDestination(atomicFeature);
						computeAS(f, atomicFeature, currentFM, newFM, newAtomic);
					}

				}
			}
		}
	}

	public VariabilityModel doTransform(VariabilityModel vm)
			throws FAMAException {
		counter = 0;
		relationCounter = 0;
		this.vm = vm;
		//inicializamos los atomic sets con la raiz
		String atomic1 = "AtomicSet "+counter;
		atomicSets = new HashMap<String, Collection<VariabilityElement>>();
		featuresToAtomics = new HashMap<AttributedFeature, AttributedFeature>();
		Collection<VariabilityElement> atomicSet = new HashSet<VariabilityElement>();
		FAMAAttributedFeatureModel fm = (FAMAAttributedFeatureModel) vm;//atomicSet.add()
		atomicSet.add(fm.getRoot());
		atomicSets.put(atomic1, atomicSet);
		
		VariabilityModel vmTransformed = new FAMAAttributedFeatureModel(new AttributedFeature(atomic1));
		makeAtomicSets(vm,vmTransformed,atomic1);
		
		return vmTransformed;
	}

	private void makeAtomicSets(VariabilityModel vm2,
			VariabilityModel vmTransformed, String atomic1) {
		FAMAAttributedFeatureModel fm1 = (FAMAAttributedFeatureModel) vm2;
		FAMAAttributedFeatureModel fm2 = (FAMAAttributedFeatureModel) vmTransformed;
		AttributedFeature root1 = fm1.getRoot();
		AttributedFeature root2 = fm2.getRoot();
		
		//recorre el arbol
		computeAS(root1,root2,fm1,fm2,atomic1);
		//y crea las dependencias
		updateDependencies(fm1, fm2);
	}

	private void updateDependencies(FAMAAttributedFeatureModel fm1, FAMAAttributedFeatureModel fm2) {
		Iterator<Constraint> it =  fm1.getConstraints().iterator();
		while (it.hasNext()){
			Constraint cn = it.next();
			if (cn instanceof Dependency){
				Dependency c = (Dependency) cn;
				Dependency aux;
				AttributedFeature atomicOrigin = featuresToAtomics.get(c.getOrigin());
				AttributedFeature atomicDest = featuresToAtomics.get(c.getDestination());
				if (c instanceof RequiresDependency){
					aux = new RequiresDependency(c.getName());
				}
				else{
					aux = new ExcludesDependency(c.getName());
				}
				aux.setOrigin(atomicOrigin);
				aux.setDestination(atomicDest);
				fm2.addConstraint(aux);
			}
			
		}		
	}

	public VariabilityModel undoTransform() {
		return vm;
	}

}
