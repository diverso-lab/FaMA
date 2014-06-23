package es.us.isa.FAMA.models.variabilityModel.transformations;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.ComplexConstraint;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.Constraint;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;

public class Extended2BasicModel implements IVariabilityModelTransform {

	private VariabilityModel sourceModel;
	
	@Override
	public VariabilityModel doTransform(VariabilityModel vm) {
		if (vm instanceof FAMAAttributedFeatureModel){
			sourceModel = vm;
			FAMAAttributedFeatureModel afm = (FAMAAttributedFeatureModel) vm;
			return transform(afm);
		}
		else{
			throw new IllegalArgumentException("This operation requires an extended feature model");
		}
	}

	private VariabilityModel transform(FAMAAttributedFeatureModel vm) {
		FAMAFeatureModel fm = walkAttributedTree(vm.getRoot());
		Collection<Constraint> constraints = extractConstraints(vm.getConstraints());
		for (Constraint c:constraints){
			fm.addConstraint(c);
		}
		return fm;
	}

	private Collection<Constraint> extractConstraints(
			Collection<Constraint> constraints) {
		Collection<Constraint> result = new LinkedList<>();
		for (Constraint c: constraints){
			if (!c.toString().contains(".")){
				// if the constraint does not have a dot (attributes always have a dot)
				Constraint aux = new ComplexConstraint(c.getAST());
				result.add(aux);
			}
		}
		return result;
	}

	private FAMAFeatureModel walkAttributedTree(AttributedFeature f) {
		FAMAFeatureModel fm = new FAMAFeatureModel();
		fm.setRoot(walkAttributedFeature(f));
		return fm;
	}
	

	private Feature walkAttributedFeature(AttributedFeature f) {
		Feature feat = new Feature(f.getName());
		Iterator<Relation> it = f.getRelations();
		while(it.hasNext()){
			Relation attRel = it.next();
			es.us.isa.FAMA.models.FAMAfeatureModel.Relation basicRel = 
					new es.us.isa.FAMA.models.FAMAfeatureModel.Relation();
			
			//we set name and parent
			basicRel.setName(attRel.getName());
			basicRel.setParent(feat);
			
			//we add cardinalities
			Iterator<Cardinality> itCards = attRel.getCardinalities();
			while (itCards.hasNext()){
				basicRel.addCardinality(itCards.next());
			}
			
			//we add destinations
			Iterator<AttributedFeature> itDestinations = attRel.getDestination();
			while (itDestinations.hasNext()){
				basicRel.addDestination(walkAttributedFeature(itDestinations.next()));
			}

			feat.addRelation(basicRel);
		}
		return feat;
	}

	@Override
	public VariabilityModel undoTransform() {
		return sourceModel;
	}

}
