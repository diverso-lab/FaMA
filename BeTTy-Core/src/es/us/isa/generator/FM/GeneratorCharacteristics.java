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

import java.util.HashMap;
import java.util.Map;

import es.us.isa.generator.Characteristics;
import es.us.isa.utils.BettyException;

/**
 * This class represent the user's preferences for the generation. The objects of this class are used as input for the generator.
 */
public class GeneratorCharacteristics implements Characteristics {

	/**
	 * The name given to the model generated (used when saving the data in certain formats)
	 */
	protected String modelName;
	
	/**
	 * The seed used in the generation. This make the generation reproducible, same seed, same result.
	 */
	protected long seed = -1;
	
	/**
	 * Maximum number of products of the feature model to be generated. 
	 */
	protected long maxProducts = -1;
	
	/**
	 * Maximum branching factor. Default value = 10.
	 */
	protected int maxBranchingFactor = 10;
	
	/**
	 * Maximum number of features in set relationships (this includes alternative and or relationships). Default value = 5.
	 */
	protected int maxSetChildren = 5;

	/**
	 * The desired number of features used in the model
	 */
	protected int numberOfFeatures = -1;

	
	/**
	 * Probability of a feature being mandatory.
	 */
	protected float probabilityMandatory = -1;
	
	/**
	 * Probability of a feature being optional.
	 */
	protected float probabilityOptional = -1;
	
	/**
	 * Probability of a feature being in an or-relation.
	 */
	protected float probabilityOr = -1;
	
	/**
	 * Probability of a feature being in an alternative relation.
	 */
	protected float probabilityAlternative = -1;

	/**
	 * The desired percentage of constraints to be generated.
	 */
	protected float percentageCTC = -1;
	

	public GeneratorCharacteristics(String modelName, int seed,
			long maxProducts, int maxBranchingFactor, int height, int maxSetChildren,
			int numberOfFeatures, int probabilityOptionalANDMandatory,
			int probabilityMandatory, int probabilityOptional, int probabilityOr,
			int probabilityAlternative, int percentageCTC
			) throws BettyException {
		super();
		this.setModelName(modelName);
		this.setSeed(seed);
		this.setMaxProducts(maxProducts);
		this.setMaxBranchingFactor( maxBranchingFactor);
		this.setMaxSetChildren( maxSetChildren);
		this.setNumberOfFeatures(numberOfFeatures);
		this.setProbabilityMandatory( probabilityMandatory);
		this.setProbabilityOptional( probabilityOptional);
		this.setProbabilityOr(probabilityOr);
		this.setProbabilityAlternative(probabilityAlternative);
		this.setPercentageCTC(percentageCTC);
	}

	public GeneratorCharacteristics() {
	}

	
	@Override
	public Map<String, String> getCharacteristicsMap() {
		Map<String, String> res = new HashMap<String, String>();
		res.put("Name", modelName);
		res.put("Seed", String.valueOf(seed));
		res.put("MaxProducts", String.valueOf(maxProducts));
		res.put("MaxBranchingFactor", String.valueOf(maxBranchingFactor));
		res.put("MaxSetChildren", String.valueOf(maxSetChildren));
		res.put("NumberOfFeatures", String.valueOf(numberOfFeatures));
		res.put("probabilityMandatory", String.valueOf(probabilityMandatory));
		res.put("ProbabilityOptional", String.valueOf(probabilityOptional));
		res.put("ProbabilityOr", String.valueOf(probabilityOr));
		res.put("ProbabilityAlternative", String.valueOf(probabilityAlternative));
		res.put("PercentageCTC", String.valueOf(percentageCTC));
		return res;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public long getMaxProducts() {
		return maxProducts;
	}

	public void setMaxProducts(long maxProducts) throws BettyException {
		if (maxProducts < 0&&maxProducts!=-1)
			throw new BettyException("Wrong argument. It must be positive");
		this.maxProducts = maxProducts;
	}

	public int getMaxBranchingFactor() {
		return maxBranchingFactor;
	}

	public void setMaxBranchingFactor(int maxBranchingFactor) throws BettyException {
		if (maxBranchingFactor < 0&&maxBranchingFactor!=-1)
			throw new BettyException("Wrong argument. It must be positive");
		this.maxBranchingFactor = maxBranchingFactor;
	}

	public int getMaxSetChildren() {
		return maxSetChildren;
	}

	public void setMaxSetChildren(int maxSetChildren) throws BettyException {
		if (maxSetChildren < 2)
			throw new BettyException("Wrong argument. It must be positive");
		this.maxSetChildren = maxSetChildren;
	}

	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}

	public void setNumberOfFeatures(int numberOfFeatures) throws BettyException {
		if (numberOfFeatures < 0)
			throw new BettyException("Wrong argument. It must be positive");
		this.numberOfFeatures = numberOfFeatures;
	}

	public float getProbabilityMandatory() {
		return probabilityMandatory;
	}

	public void setProbabilityMandatory(float probabilityMandatory) throws BettyException {
		if ((probabilityMandatory < 0)&&(probabilityMandatory!=-1) || probabilityMandatory > 100)
			throw new BettyException("Wrong argument. It must be a value between 0 and 100");
		this.probabilityMandatory = probabilityMandatory;
	}

	public float getProbabilityOptional() {
		return probabilityOptional;
	}

	public void setProbabilityOptional(float probabilityOptional) throws BettyException {
		if ((probabilityOptional < 0&&probabilityOptional!=-1) || probabilityOptional > 100)
			throw new BettyException("Wrong argument. It must be a value between 0 and 100");
		this.probabilityOptional = probabilityOptional;
	}

	public float getProbabilityOr() {
		return probabilityOr;
	}

	public void setProbabilityOr(float probabilityOr) throws BettyException {
		if ((probabilityOr < 0)&&(probabilityOr!=-1) || probabilityOr > 100 )
			throw new BettyException("Wrong argument. It must be a value between 0 and 100");
		this.probabilityOr = probabilityOr;
	}

	public float getProbabilityAlternative() {
		return probabilityAlternative;
	}

	public void setProbabilityAlternative(float probabilityAlternative) throws BettyException {
		if ((probabilityAlternative < 0 &&probabilityAlternative!=-1)|| probabilityAlternative > 100)
			throw new BettyException("Wrong argument. It must be a value between 0 and 100");
		this.probabilityAlternative = probabilityAlternative;
	}

	public float getPercentageCTC() {
		return percentageCTC;
	}

	public void setPercentageCTC(float percentageCTC) throws BettyException {
		if (percentageCTC < 0 || percentageCTC > 100)
			throw new BettyException("Wrong argument. It must be a value between 0 and 100");
		this.percentageCTC = percentageCTC;
	}

	/**
	 * Check that the given characteristics are correct (example: sum of probabilities <= 100)
	 */
	public void checkCharacteristics() throws BettyException {
		
		// Calculate the sum of percentages and the number of percentages provided
		int sumProbabilities = 0;
		int numberProbabilities = 0;
		
		if (this.getProbabilityMandatory() != -1) {
			sumProbabilities += this.getProbabilityMandatory();
			numberProbabilities++;
		}
		
		if (this.getProbabilityOptional() != -1) {
			sumProbabilities += this.getProbabilityOptional();
			numberProbabilities++;
		}
		
		if (this.getProbabilityOr() != -1) {
			sumProbabilities += this.getProbabilityOr();
			numberProbabilities++;
		}
		
		if (this.getProbabilityAlternative() != -1) {
			sumProbabilities += this.getProbabilityAlternative();
			numberProbabilities++;
		}
		
		if (sumProbabilities > 100)
			throw new BettyException("Wrong arguments. The sum of the probabilities can not be greater than 100");
		
		if (numberProbabilities==4 && sumProbabilities < 100)
			throw new BettyException("Wrong arguments. The sum of the probabilities can not be lower than 100");
		
		if (this.getMaxSetChildren() > this.getMaxBranchingFactor())
			throw new BettyException("Wrong arguments. The maximum number of children in a set can not be higher than the maximum branching factor of the tree");
		
		if (this.getProbabilityOr() + this.getProbabilityAlternative() == 100)
			throw new BettyException("Wrong arguments. The sum of the probabilities of or and alternative childen cannot be equal to 100.");
			
	}
	
	public GeneratorCharacteristics clone(){
		
		GeneratorCharacteristics ch= new GeneratorCharacteristics();
		ch.setMaxBranchingFactor(maxBranchingFactor);
		if(maxProducts!=-1){
			ch.setMaxProducts(maxProducts);
		}
		ch.setMaxSetChildren(maxSetChildren);
		ch.setModelName(modelName);
		ch.setNumberOfFeatures(numberOfFeatures);
		ch.setPercentageCTC(percentageCTC);
		ch.setProbabilityAlternative(probabilityAlternative);
		ch.setProbabilityMandatory(probabilityMandatory);
		ch.setProbabilityOptional(probabilityOptional);
		ch.setProbabilityOr(probabilityOr);
		ch.setSeed(seed);
		
		return ch;
	}
	
}
