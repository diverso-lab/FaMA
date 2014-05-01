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
package es.us.isa.generator.FM.Evolutionay;

import java.util.List;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;

public interface IChromosome {
	/**
	 * Encoding. For the representation of feature models as chromosomes.
	 * 
	 * @param fm The feature model to be encoded
	 */
	public void encode(FAMAFeatureModel fm);

	
	/**
	 * In this method, the array-based chromosomes are translated back into feature models in order to be evaluated.
	 * @return the decoded feature model.
	 */
	public FAMAFeatureModel decode();

	/**
	 * Return a copy of the chromosome
	 */
	public Chromosome clone();

	/**
	 * Set the fitness of the chromosome
	 * 
	 * @param ff
	 *            the fitness of the chomosome
	 */
	public void setFitnessNumber(double ff);

	/**
	 * Return the encoded list of the cross-tree constraints.
	 * 
	 * @return the list of existing CTC
	 */
	public List<String[]> getCTC();

	/**
	 * Return a list with the encoded feature model tree.
	 * 
	 * @return The list of Features
	 */
	public List<String[]> getTree();

}
