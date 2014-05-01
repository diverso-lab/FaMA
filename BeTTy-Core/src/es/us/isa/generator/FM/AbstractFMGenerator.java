/**
 * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.generator.FM;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.generator.Characteristics;
import es.us.isa.generator.IGenerator;
import es.us.isa.utils.BettyException;
import es.us.isa.utils.FMStatistics;

/**
 * This class implements the basic methods of a basic random FM generator.
 * 
 */
public abstract class AbstractFMGenerator implements IGenerator {

	/**
	 * This variable is used to add functionality to the internal methods of the class by using hook methods implemented in an external decorator class.
	 * By default is null.
	 */
	protected AbstractFMGeneratorDecorator hookClass = null;
	
	/**
	 * Input constraints used for the generation.
	 */
	protected GeneratorCharacteristics characteristics;

	/**
	 * This variable stores the statistics of the model being generated (e.g.
	 * number of features generated)
	 */
	protected FMStatistics statistics;

	/**
	 * The random class used during the generation.
	 */
	protected Random random;

	/**
	 * Number of features of the feature models to be generated
	 */
	protected int numberOfFeatures;

	/**
	 * Number of cross-tree constraints to be generated
	 */
	protected int numberOfConstraints;

	/**
	 * Probability of a feature being mandatory
	 */
	protected float probMandatory;

	/**
	 * Probability of a feature being optional.
	 */
	protected float probOptional;

	/**
	 * Probability of a feature being in an or-relation
	 */
	protected float probOrChildren;

	/**
	 * Probability of a feature being in an alternative relation
	 */
	protected float probAltChildren;

	/**
	 * Util for naming
	 */
	protected int featureId;
	
	/**
	 * Util for naming
	 */
	protected int relationId;


	/**
	 * This method resets the generation, setting the user's preferences for the
	 * generation
	 * 
	 * @param c
	 *            The characteristics (user's generation preferences) to be used
	 *            in the generator.
	 * @throws BettyException
	 */
	public void resetGenerator(Characteristics c) throws BettyException {
		GeneratorCharacteristics ch = (GeneratorCharacteristics) c;
		statistics = new FMStatistics();
		random = new Random();

		featureId = 0;
		relationId = 0;
		this.characteristics = ch;
		this.numberOfFeatures = -1;
		this.numberOfConstraints = -1;

		// Generate and save the seed
		if (ch.getSeed() == -1)
			ch.setSeed(random.nextInt());

		random.setSeed(ch.getSeed());

		// Check for errors in the characteristics
		ch.checkCharacteristics();

		// Adjust exact number of relationships of each type to be generated.
		// Unspecified percentages are adjusted randomly.
		this.numberOfFeatures = this.characteristics.getNumberOfFeatures();
		this.numberOfConstraints = (int) ((this.characteristics
				.getPercentageCTC() * ch.getNumberOfFeatures()) / 100);

		// Adjust probabilities
		int sumProbabilities = 0;
		int probabilitiesToAdd = 0;
		if (this.characteristics.getProbabilityMandatory() != -1)
			sumProbabilities += this.characteristics.getProbabilityMandatory();
		else
			probabilitiesToAdd++;

		if (this.characteristics.getProbabilityOr() != -1)
			sumProbabilities += this.characteristics.getProbabilityOr();
		else
			probabilitiesToAdd++;

		if (this.characteristics.getProbabilityOptional() != -1)
			sumProbabilities += this.characteristics.getProbabilityOptional();
		else
			probabilitiesToAdd++;

		if (this.characteristics.getProbabilityAlternative() != -1)
			sumProbabilities += this.characteristics
					.getProbabilityAlternative();
		else
			probabilitiesToAdd++;

		if (probabilitiesToAdd != 4) {

			// Percentage of mandatories
			if (this.characteristics.getProbabilityMandatory() != -1)
				this.probMandatory = this.characteristics
						.getProbabilityMandatory();
			else {
				if (probabilitiesToAdd > 1) {
					this.probMandatory = random.nextInt(Math.max(1,
							100 - sumProbabilities));
					sumProbabilities += this.probMandatory;
				} else
					this.probMandatory = 100 - sumProbabilities;

				probabilitiesToAdd--;
			}

			// Percentage of optional
			if (this.characteristics.getProbabilityOptional() != -1)
				this.probOptional = this.characteristics
						.getProbabilityOptional();
			else {
				if (probabilitiesToAdd > 1) {
					this.probOptional = random.nextInt(Math.max(1,
							100 - sumProbabilities));
					sumProbabilities += this.probOptional;
				} else
					this.probOptional = 100 - sumProbabilities;

				probabilitiesToAdd--;
			}

			// Percentage of or
			if (this.characteristics.getProbabilityOr() != -1)
				this.probOrChildren = this.characteristics.getProbabilityOr();
			else {
				if (probabilitiesToAdd > 1) {
					this.probOrChildren = random.nextInt(Math.max(1,
							100 - sumProbabilities));
					sumProbabilities += this.probOrChildren;
				} else
					this.probOrChildren = 100 - sumProbabilities;

				probabilitiesToAdd--;
			}

			// Percentage of alternative
			if (this.characteristics.getProbabilityAlternative() != -1)
				this.probAltChildren = this.characteristics
						.getProbabilityAlternative();
			else {
				if (probabilitiesToAdd > 1) {
					this.probAltChildren = random.nextInt(Math.max(1,
							100 - sumProbabilities));
					sumProbabilities += this.probAltChildren;
				} else
					this.probAltChildren = 100 - sumProbabilities;

				probabilitiesToAdd--;
			}
		} else {
			this.probMandatory = 25;
			this.probOptional = 25;
			this.probOrChildren = 25;
			this.probAltChildren = 25;
		}

		// Avoid mandatory and optional probabilities being zero at the same
		// time
		while (this.probMandatory == 0 && this.probOptional == 0)
			resetGenerator(ch);
		
		// Hook method
		if (hookClass != null)
			hookClass.updateResetGenerator(c);
		
	}
	
	/**
	 * Set a decorator object implementing the hook methods
	 */
	public void setHookClass(AbstractFMGeneratorDecorator gen) {
		this.hookClass = gen;
	}


	/**
	 * @return the next name to be used for a new feature
	 */
	protected String getFeatureId() {
		return "F" + String.valueOf(++featureId);
	}

	/**
	 * @return the next name to be used for a new relation
	 */
	protected String getRelationId() {
		return "R-" + String.valueOf(++relationId);
	}

	/**
	 * Update the statistics of the feature model being
	 * generated when we add a root feature.
	 * 
	 * @param root
	 *            The root feature added.
	 */
	public void addRoot(FAMAFeatureModel fm, Feature root) {
		// Add root
		fm.setRoot(root);
		
		// Update statistics
		statistics.setNoFeatures(statistics.getNoFeatures() + 1);
		
		// Hook method
		if (hookClass != null)
			hookClass.updateRoot(fm, root);

	}

	/**
	 * 
	 * Update the statistics of the feature model being
	 * generated when we add an alternative relationship.
	 * 
	 * @param parent
	 *            The parent feature of the alternative relationship.
	 * @param children
	 *            The child features of the alternative relationship.
	 */
	public Relation addAlternative(Feature parent, List<Feature> children) {
		// Add alternative
		Relation altRelation = new Relation(getRelationId());
		parent.addRelation(altRelation);
		altRelation.addCardinality(new Cardinality(1, 1));

		Iterator<Feature> it = children.iterator();
		while (it.hasNext())
			altRelation.addDestination(it.next());
		
		// Update statistics
		statistics.setNoAlternative(statistics.getNoAlternative() + 1);
		statistics.setNoAlternativeChildren(statistics.getNoAlternativeChildren()+ children.size());
		statistics.setNoFeatures(statistics.getNoFeatures() + children.size());
		
		// Hook method
		if (hookClass != null)
			hookClass.updateAlternative(parent, children);
		
		return altRelation;
	}

	/**
	 * Update the statistics of the feature model being
	 * generated when we add an mandatory relationship
	 * 
	 * @param parent
	 *            The parent feature of the mandatory relationship.
	 * @param child
	 *            The mandatory feature added.
	 */
	public void addMandatory(Feature parent, Feature child) {
		// Add mandatory
		createCardinality(parent, child, 1, 1);
		
		// Update statistics
		statistics.setNoMandatory(statistics.getNoMandatory() + 1);
		statistics.setNoFeatures(statistics.getNoFeatures() + 1);
		
		// Hook method
		if (hookClass != null)
			hookClass.updateMandatory(parent, child);
	}

	/**
	 * Update the statistics of the feature model being
	 * generated when we add an optional relationship
	 * 
	 * @param parent
	 *            The parent feature of the optional relationship.
	 * @param child
	 *            The optional feature added.
	 */
	public void addOptional(Feature parent, Feature child) {
		// Add optional
		createCardinality(parent, child, 0, 1);
		
		// Update statistics
		statistics.setNoOptional(statistics.getNoOptional() + 1);
		statistics.setNoFeatures(statistics.getNoFeatures() + 1);
		
		// HOOK
		if (hookClass != null)
			hookClass.updateOptional(parent, child);
	}

	/**
	 * 
	 * Update the statistics of the feature model being
	 * generated when we add an or relationship.
	 * 
	 * @param parent
	 *            The parent feature of the or-relation.
	 * @param children
	 *            The child features of the or-relation added.
	 */
	public Relation addOr(Feature parent, List<Feature> children) {
		// Add or
		Relation orRelation = new Relation(getRelationId());
		parent.addRelation(orRelation);
		orRelation.addCardinality(new Cardinality(1, children.size()));

		Iterator<Feature> it = children.iterator();
		while (it.hasNext())
			orRelation.addDestination(it.next());
			

		// Update statistics
		statistics.setNoOr(statistics.getNoOr() + 1);
		statistics.setNoFeatures(statistics.getNoFeatures() + children.size());
		statistics.setNoOrChildren(statistics.getNoOrChildren()
				+ children.size());

		// HOOK
		if (hookClass != null)
			hookClass.updateOr(parent, children);
		
		return orRelation;
	}

	/**
	 * 
	 * Update the statistics of the feature model being
	 * generated when an excludes constraint is added.
	 * 
	 * @param origin
	 *            The origin feature.
	 * @param destination
	 *            The destination feature.
	 */
	public void addExcludes(FAMAFeatureModel fm, Feature origin, Feature destination){
		// Add Excludes
		fm.addDependency(new ExcludesDependency(getRelationId(), origin,destination));
		
		// Update statistics
		statistics.setNoCrossTree(statistics.getNoCrossTree() + 1);
		statistics.setNoExcludes(statistics.getNoExcludes() + 1);
		
		// Hook method
		if (hookClass != null)
			hookClass.updateExcludes(fm, origin, destination);
	}

	/**
	 * 
	 * Update the statistics of the feature model being
	 * generated when a requires constraint is added.
	 * 
	 * @param origin
	 *            The origin feature.
	 * @param destination
	 *            The destination feature.
	 */
	public void addRequires(FAMAFeatureModel fm, Feature origin, Feature destination) {
		// Add requires
		fm.addDependency(new RequiresDependency(getRelationId(), origin,destination));
		
		// Update statistics
		statistics.setNoRequires(statistics.getNoRequires() + 1);
		statistics.setNoCrossTree(statistics.getNoCrossTree() + 1);
		
		// Hook method
		if (hookClass != null)
			hookClass.updateRequires(fm, origin, destination);

	}

	/**
	 * This method is used to find out if two features are direct family (i.e.
	 * they are directly related in the tree) For instance, in the following
	 * scheme, f->h->i->g, we could say that f and g are direct family.
	 * 
	 * @param f
	 *            This first feature whose parental relation we want to check.
	 * @param g
	 *            This second feature whose parental relation we want to check.
	 * 
	 * @return a boolean stating whether the input features have any parental
	 *         relation or not.
	 */
	public boolean areDirectFamily(Feature f, Feature g) {
		boolean res = false;

		if (f == g) {
			res = true;
		} else {
			Iterator<Relation> itr = f.getRelations();
			while (itr.hasNext()) {
				Iterator<Feature> itf = itr.next().getDestination();
				while (itf.hasNext() && !res) {
					res = res | areDirectFamily(itf.next(), g);
				}
			}
		}

		return res;
	}

	/**
	 * This method will look for any existing dependency in the generated model
	 * between the input features
	 * 
	 * @param fm
	 *            The feature model where we will look for the dependency
	 * @param f
	 *            Input feature.
	 * @param g
	 *            Input feature.
	 * 
	 * @return a boolean stating whether exits a constraint in fm between f and
	 *         g or not.
	 */
	public boolean existsRelation(FAMAFeatureModel fm, Feature f, Feature g) {
		boolean res = false;
		Iterator<Dependency> itd = fm.getDependencies();

		while (itd.hasNext() && !res) {
			Dependency dep = itd.next();
			if ((dep.getDestination() == f && dep.getOrigin() == g)
					|| dep.getDestination() == g && dep.getOrigin() == f) {
				res = true;
			}
		}

		return res;
	}

	/**
	 * This method create a relation with complex cardinality between two
	 * features
	 * 
	 * @param parent
	 *            The parent feature.
	 * @param child
	 *            The child feature.
	 * @param i
	 *            The minimum value for the cardinality.
	 * @param j
	 *            The maximum value for the cardinality.
	 */
	public void createCardinality(Feature parent, Feature child, int i, int j) {
		Relation rel = new Relation(getRelationId());
		rel.addCardinality(new Cardinality(i, j));
		rel.addDestination(child);
		parent.addRelation(rel);
	}
	
}
