package es.us.isa.fama.solvers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.Relation;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.Constraint;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.fama.operations.AAFMProblem;
import jmetal.core.Solution;

public abstract class Solver {

	protected int violations;
	
	protected FAMAAttributedFeatureModel fm;
	
	public Solver(){
		violations = 0;
	}
	
	public void translate(FAMAAttributedFeatureModel fm, boolean reify){
		this.reset();
		this.fm = fm;
		generateVariables(fm.getFeatures());
		addRoot(fm.getRoot());
		generateConstraints(fm.getRoot());
		Iterator<Constraint> it = fm.getConstraints().iterator();
		while (it.hasNext()){
			Constraint c = it.next();
			this.addComplexConstraint(c);
		}
		if (reify){
			extraMapping();
		}
		recordSolverState();
	}
	
	protected  abstract void recordSolverState();

	protected abstract void reset();
	
	protected abstract void addComplexConstraint(Constraint c);

	protected abstract void addRoot(GenericFeature f);
	
	protected abstract void generateVariables(Collection<? extends GenericFeature> features);

	protected abstract void extraMapping();

	public abstract void checkSolution(Solution s);
	
	public abstract Collection<Solution> computeSeeds(AAFMProblem problem, int n);
	
	public abstract FAMAAttributedFeatureModel getAtomicSets();
	
	public int getViolations(){
		return violations;
	}
	
	/**
	 * template method. each solver maps it as it best considers
	 * @param f
	 */
	private void generateConstraints(AttributedFeature f) {
		Iterator<Relation> relations = f.getRelations();
		while(relations.hasNext()) {
			Relation rel = relations.next();
			if (rel.getNumberOfDestination() == 1) {
				if (rel.isMandatory()) {
					this.addMandatory(rel,rel.getDestinationAt(0),f);
				} else if (rel.isOptional()) {
					this.addOptional(rel,rel.getDestinationAt(0),f);
				} else {
					this.addCardinality(rel,rel.getDestinationAt(0),f,rel.getCardinalities());
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
				this.addSet(rel,f,children,cards);
			}
		}
	}

	protected abstract void addSet(Relation rel, AttributedFeature f,
			Collection<GenericFeature> children, Collection<Cardinality> cards);

	protected abstract void addCardinality(Relation rel, AttributedFeature destinationAt,
			AttributedFeature f, Iterator<Cardinality> cardinalities);

	protected abstract void addOptional(Relation rel, AttributedFeature destinationAt,
			AttributedFeature f);

	protected abstract void addMandatory(Relation rel, AttributedFeature destinationAt,
			AttributedFeature f);
	
	
	public abstract int getNumberOfVariables();
	
	public abstract int getNumberOfConstraints();
	
	public abstract ExtendedConfiguration solution2Configuration(Solution s);
}
