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
package es.us.isa.FAMA.models.FAMAfeatureModel.transformations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.Reasoner.FeatureModelReasoner;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.ITransform;

public class FeatureModelTransform implements ITransform {
	private FeatureModelReasoner r;
	private FAMAFeatureModel fm;

	public void transform(VariabilityModel vmodel, Reasoner reasoner) {
		this.r = (FeatureModelReasoner) reasoner;
		fm = (FAMAFeatureModel) vmodel;
		r.reset();
		setFeatureModel(fm);
	}

	public void update() {
		// TODO Auto-generated method stub
		// ¿Que intencion tiene este método?
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tdg.SPL.Reasoner.Reasoner#ask(tdg.SPL.Reasoner.Question)
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tdg.SPL.Reasoner.Reasoner#setFeatureModel(es.us.isa.FAMA.featureModel
	 * .FeatureModel)
	 */
	private void setFeatureModel(FAMAFeatureModel fm) {
		this.fm = fm;
		generateVariables(fm);
		generateConstraints(fm);
	}

	private void generateConstraints(FAMAFeatureModel fm) {
		Feature root = fm.getRoot();
		r.addRoot(root);
		generateConstraints(root);
		Iterator<Dependency> it = fm.getDependencies();
		while (it.hasNext()) {
			Dependency dep = it.next();
			if (dep instanceof RequiresDependency)
				r.addRequires(dep, dep.getOrigin(), dep.getDestination());
			else if (dep instanceof ExcludesDependency)
				r.addExcludes(dep, dep.getOrigin(), dep.getDestination());
		}
	}

	private void generateConstraints(Feature f) {
		Iterator<Relation> relations = f.getRelations();
		while (relations.hasNext()) {
			Relation rel = relations.next();
			if (rel.getNumberOfDestination() == 1) {
				if (rel.isMandatory()) {
					r.addMandatory(rel, rel.getDestinationAt(0), f);
				} else if (rel.isOptional()) {
					r.addOptional(rel, rel.getDestinationAt(0), f);
				} else {
					r.addCardinality(rel, rel.getDestinationAt(0), f, rel
							.getCardinalities());
				}
				generateConstraints(rel.getDestinationAt(0));
			} else {
				Collection<GenericFeature> children = new ArrayList<GenericFeature>();
				Iterator<Feature> it = rel.getDestination();
				while (it.hasNext()) {
					Feature child = it.next();
					children.add(child);
					generateConstraints(child);
				}
				Collection<Cardinality> cards = new ArrayList<Cardinality>();
				Iterator<Cardinality> itc = rel.getCardinalities();
				while (itc.hasNext()) {
					cards.add(itc.next());
				}
				r.addSet(rel, f, children, cards);
			}
		}
	}

	private void generateVariables(FAMAFeatureModel fm) {
		Iterator<Feature> it=null;
		if (fm.getOrderingHeuristic() != null) {
			it = fm.getOrderingHeuristic().orderFM(fm).iterator();
		} else {
			 it = fm.getFeatures().iterator();
		}
		while (it.hasNext()) {
				Feature f = (Feature) it.next();
				Relation parentRelation = f.getParent();
				if (parentRelation != null
						&& parentRelation.getNumberOfDestination() == 1
						&& parentRelation.getCardinalities().hasNext()) {
					Iterator<Cardinality> itc = parentRelation
							.getCardinalities();
					Collection<Cardinality> cards = new ArrayList<Cardinality>();
					while (itc.hasNext())
						cards.add(itc.next());
					r.addFeature(f, cards);
				} else {
					ArrayList<Cardinality> cards = new ArrayList<Cardinality>();
					cards.add(new Cardinality(0, 1));
					r.addFeature(f, cards);
				}
			}
		}
	

}
