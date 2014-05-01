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

import java.util.List;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.generator.Characteristics;
import es.us.isa.generator.IGenerator;
import es.us.isa.utils.BettyException;

/**
 * This class implements the basic methods of the decorators of random FM generators.
 * 
 */
public abstract class AbstractFMGeneratorDecorator implements IGenerator {

	private IGenerator generator;
	
	/**
	 * Constructor. It receives the FM generator to be decorated.
	 */
	public AbstractFMGeneratorDecorator(IGenerator gen) {
		this.generator = gen;
		((AbstractFMGenerator) gen).setHookClass(this);
	}
	
	/**
	 * This is the main method being decorated.
	 */
	public VariabilityModel generateFM(Characteristics ch)
			throws BettyException {
		return generator.generateFM(ch);
	}
	
	
	public void resetGenerator(Characteristics ch) {
		generator.resetGenerator(ch);
	}
	
	// Hook methods
	
	/**
	 * Hook method called just after adding the root feature to the model being generated.
	 */
	protected void updateRoot(FAMAFeatureModel fm, Feature root){}
	
	/**
	 * Hook method called just after adding a new mandatory feature to the model being generated.
	 */
	protected void updateMandatory(Feature parent, Feature child){}
	
	/**
	 * Hook method called just after adding a new optional feature to the model being generated.
	 */
	protected void updateOptional(Feature parent, Feature child){}
	
	/**
	 * Hook method called just after adding an alternative relation to the model being generated.
	 */
	protected void updateAlternative(Feature parent, List<Feature> children){}
	
	/**
	 * Hook method called just after adding an or-relationship to the model being generated.
	 */
	protected void updateOr(Feature parent, List<Feature> children){}
	
	/**
	 * Hook method called just after adding a new excludes constraint to the model being generated.
	 */
	protected void updateExcludes(FAMAFeatureModel fm, Feature origin, Feature destination){}
	
	/**
	 * Hook method called just after adding a new requires constraint to the model being generated.
	 */
	protected void updateRequires(FAMAFeatureModel fm, Feature origin, Feature destination){}
	
	/**
	 * Hook method called just after calling the resetGenerator method in the decorated generator.
	 */
	protected void updateResetGenerator(Characteristics c){}

}
