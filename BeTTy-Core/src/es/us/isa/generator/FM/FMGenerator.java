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

import java.util.ArrayList;
import java.util.List;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.generator.Characteristics;
import es.us.isa.utils.BettyException;

/**
 * Class for the random generation of feature model using an iterative algorithm
 */
public class FMGenerator extends AbstractFMGenerator {

	private List<Feature> features;
	private final int MANDATORY = 0;
	private final int OPTIONAL = 1;
	private final int OR = 2;
	private final int ALTERNATIVE = 3;

	public VariabilityModel generateFM(Characteristics ch) {
		super.resetGenerator(ch);

		FAMAFeatureModel fm = new FAMAFeatureModel();
		Feature root = new Feature("root");
		addRoot(fm, root);

		// Generate Tree
		generateTree(fm.getRoot());

		// Generate CTC
		generateCroosTreeConstraints(fm);

		return fm;
	}

	/**
	 * Generate the tree (cross-tree constraints are ignored at this point)
	 * 
	 * @param root
	 *            Root feature.
	 * @throws BettyException
	 * 
	 * @throws BeTTyException
	 */
	protected void generateTree(Feature root) {

		// Reset list of features
		features = new ArrayList<Feature>();

		// Add root
		features.add(root);

		while (statistics.getNoFeatures() < numberOfFeatures) {

			// Select current parent feature
			int currentIndex = random.nextInt(features.size());
			Feature current_feature = features.get(currentIndex);

			// Number of child feature to be generated (From 1 to width)
			int nChildren = Math.max(1, Math.min(
					random.nextInt((int) characteristics
							.getMaxBranchingFactor() + 1),
					this.numberOfFeatures - this.statistics.getNoFeatures()));

			int orChildren = 0;
			int altChildren = 0;

			// Generate children
			for (int i = 0; i < nChildren; i++) {
				int relType = getRandomRelationship();
				Feature child = null;
				switch (relType) {
				case MANDATORY:

					child = new Feature(getFeatureId());
					features.add(child);
					addMandatory(current_feature, child);

					break;

				case OPTIONAL:

					child = new Feature(getFeatureId());
					features.add(child);
					addOptional(current_feature, child);

					break;

				case OR:
					orChildren++;

					break;

				case ALTERNATIVE:
					altChildren++;

					break;
				default:
					break;
				}
			}

			if (orChildren >= 2)
				createOr(current_feature, orChildren);

			if (altChildren >= 2)
				createAlternative(current_feature, altChildren);

			// Remove current parent feature from the list to avoid adding more
			// children to it.
			if (features.size() != 1)
				features.remove(currentIndex);
		}
	}

	private void createOr(Feature parent, int nChildren) {

		// Create n random relations depending on the number of feature to add
		// and the maximum number of features allowed in set relations
		int childrenToAdd = nChildren;
		while (childrenToAdd >= 2) {
			int maxChildren = Math.min(
					this.characteristics.getMaxSetChildren(), childrenToAdd);
			int noChildren = Math.max(2, this.random.nextInt(maxChildren + 1));
			childrenToAdd -= noChildren;
			if (childrenToAdd == 1
					&& noChildren < this.characteristics.getMaxSetChildren())
				noChildren++;

			// Create or
			List<Feature> childrenOr = new ArrayList<Feature>();
			Feature child = null;
			for (int j = 0; j < noChildren; j++) {
				String name = getFeatureId();
				child = new Feature(name);
				childrenOr.add(child);
				features.add(child);

			}

			Relation orRelation = addOr(parent, childrenOr);

			// Change the relation to a random position (to avoid all or
			// relationship to be inserted at the end of the branch)
			int nRel = parent.getNumberOfRelations();
			int newPosition = random.nextInt(nRel);
			Relation r1 = parent.getRelationAt(newPosition);
			Relation r = r1;
			r1 = orRelation;
			orRelation = r;
		}
	}

	private void createAlternative(Feature parent, int nChildren) {

		// Create n random relations depending on the number of feature to add
		// and the maximum number of features allowed in set relations
		int childrenToAdd = nChildren;
		while (childrenToAdd >= 2) {
			int maxChildren = Math.min(
					this.characteristics.getMaxSetChildren(), childrenToAdd);
			int noChildren = Math.max(2, this.random.nextInt(maxChildren + 1));
			childrenToAdd -= noChildren;
			if (childrenToAdd == 1
					&& noChildren < this.characteristics.getMaxSetChildren())
				noChildren++;

			// Create alternative
			List<Feature> childrenAlt = new ArrayList<Feature>();
			Feature child = null;
			for (int j = 0; j < noChildren; j++) {
				String name = getFeatureId();
				child = new Feature(name);
				childrenAlt.add(child);
				features.add(child);
			}

			Relation altRelation = addAlternative(parent, childrenAlt);

			// Change the relation to a random position (to avoid all
			// alternative relationship to be inserted at the end positions)
			int nRel = parent.getNumberOfRelations();
			int newPosition = random.nextInt(nRel);
			Relation r1 = parent.getRelationAt(newPosition);
			Relation r = r1;
			r1 = altRelation;
			altRelation = r;
		}
	}

	/**
	 * Generate the cross-tree constraints fulfilling the following rules: (i)
	 * The origin and destination features of any constraint can not not be
	 * directly related in the tree, and (2) Any pair of features can only share
	 * a single constraint.
	 * 
	 * @param fm
	 *            Feature model in which the constraints will be added.
	 */
	protected void generateCroosTreeConstraints(FAMAFeatureModel fm) {
		int i = 0;
		int maxtries = 0;
		int realNumberOfCTC = 0;

		while (i < this.numberOfConstraints) {
			boolean added=false;
			maxtries = this.numberOfFeatures * 100;
			for (int t = 0; t < maxtries && !added; t++) {
				int f1 = random.nextInt(featureId) + 1;
				int f2 = random.nextInt(featureId) + 1;
				Feature f = fm.searchFeatureByName("F" + f1);
				Feature g = fm.searchFeatureByName("F" + f2);
				if (!areDirectFamily(f, g) && !areDirectFamily(g, f)
						&& !existsRelation(fm, f, g)) {
					if (random.nextBoolean()) {
						addRequires(fm, f, g);
					} else {
						addExcludes(fm, f, g);
					}
					added=true;
					realNumberOfCTC++;
					// We need to add that clause, to avoid very rare error when
					// models with few features
				} 
			}
			i++;
		
		}
		
		
		if (this.numberOfConstraints != realNumberOfCTC) {
			this.numberOfConstraints = realNumberOfCTC;
			System.out
					.println("Some constraints were not added. Please increase then number of features or reduce the percentage of CTCs.");
		}
	}

	// Determines the type of the next relationship to be generated
	protected int getRandomRelationship() {

		int p = 0;
		p = random.nextInt(100);
		if (p >= 0 && p < probMandatory)
			return MANDATORY;
		else if (p >= probMandatory && p < (probMandatory + probOptional))
			return OPTIONAL;
		else if (p >= (probMandatory + probOptional)
				&& p < (probMandatory + probOptional + probOrChildren))
			return OR;
		else
			return ALTERNATIVE;

	}

}