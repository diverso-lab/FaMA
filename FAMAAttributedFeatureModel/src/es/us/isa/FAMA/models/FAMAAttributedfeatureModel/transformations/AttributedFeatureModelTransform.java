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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.Reasoner.AttributedFeatureModelReasoner;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.Relation;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.Constraint;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.extended.ConstantIntConverter;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.ITransform;

//TODO ver que hay que hacer con esta clase para permitir compatibilidad
//con los antiguos feature models
public class AttributedFeatureModelTransform implements ITransform {
	
	private AttributedFeatureModelReasoner r;
	private FAMAAttributedFeatureModel fm;
	
	public void transform(VariabilityModel vmodel, Reasoner reasoner) {
		r = (AttributedFeatureModelReasoner)reasoner;
		fm = (FAMAAttributedFeatureModel)vmodel;
		//TODO meter aqui el ConstantIntConverter
		ConstantIntConverter conv = fm.getConstantIntConverter();
		if (conv != null){
			r.setConstantIntConverter(fm.getConstantIntConverter());
		}
		r.reset();
		setFeatureModel(fm);
	}

	public void update() {
		// TODO Auto-generated method stub

	}
	
	/* (non-Javadoc)
	 * @see tdg.SPL.Reasoner.Reasoner#ask(tdg.SPL.Reasoner.Question)
	 */

	/* (non-Javadoc)
	 * @see tdg.SPL.Reasoner.Reasoner#setFeatureModel(es.us.isa.FAMA.featureModel.FeatureModel)
	 */
	private void setFeatureModel(FAMAAttributedFeatureModel fm) {
		this.fm = fm;
		generateVariables(fm);
		AttributedFeature root = fm.getRoot();
		r.addRoot(root);
		generateConstraints(root);
		Iterator<Constraint> it = fm.getConstraints().iterator();
		while (it.hasNext()){
			Constraint c = it.next();
			r.addConstraint(c);
		}
	}
	
//	private void generateConstraints(FAMAAttributedFeatureModel fm) {
//		AttributedFeature root = fm.getRoot();
//		r.addRoot(root);
//		generateConstraints(root);
//		Iterator<Constraint> it = fm.getConstraints().iterator();
//		while (it.hasNext()) {
//			Constraint dep = it.next();
//			if (dep instanceof RequiresDependency){
//				Dependency d = (Dependency)dep;
//				r.addRequires(dep,d.getOrigin(),d.getDestination());
//			}
//			else if (dep instanceof ExcludesDependency){
//				Dependency d = (Dependency)dep;
//				r.addExcludes(dep,d.getOrigin(),d.getDestination());
//			}
//				
//		}
//	}
//
	private void generateConstraints(AttributedFeature f) {
		Iterator<Relation> relations = f.getRelations();
		while(relations.hasNext()) {
			Relation rel = relations.next();
			if (rel.getNumberOfDestination() == 1) {
				if (rel.isMandatory()) {
					r.addMandatory(rel,rel.getDestinationAt(0),f);
				} else if (rel.isOptional()) {
					r.addOptional(rel,rel.getDestinationAt(0),f);
				} else {
					r.addCardinality(rel,rel.getDestinationAt(0),f,rel.getCardinalities());
				}
				generateConstraints(rel.getDestinationAt(0));
			}
			else {
				Collection<GenericFeature> children = new ArrayList<GenericFeature>();
				Iterator<AttributedFeature> it = rel.getDestination();
				while (it.hasNext()) {
					AttributedFeature child = it.next();
					children.add(child);
					generateConstraints(child);
				}
				Collection<Cardinality> cards = new ArrayList<Cardinality>();
				Iterator<Cardinality> itc = rel.getCardinalities();
				while (itc.hasNext()) {
					cards.add(itc.next());
				}
				r.addSet(rel,f,children,cards);
			}
		}
	}
	
	private void generateVariables(FAMAAttributedFeatureModel fm) {
		Iterator<AttributedFeature> it = fm.getAttributedFeatures().iterator();
		while (it.hasNext()) {
			AttributedFeature f = (AttributedFeature)it.next();
			Relation parentRelation = f.getParent();
			if (parentRelation != null && parentRelation.getNumberOfDestination() == 1 &&
				parentRelation.getCardinalities().hasNext()) {
				Iterator<Cardinality> itc = parentRelation.getCardinalities();
				Collection<Cardinality> cards = new ArrayList<Cardinality>();
				while (itc.hasNext())
					cards.add(itc.next());
				r.addFeature(f,cards);
			}
			else {
				ArrayList <Cardinality> cards = new ArrayList <Cardinality>();
				cards.add(new Cardinality(0,1));
				r.addFeature(f,cards);
			}
		}
	}


}
