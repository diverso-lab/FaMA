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
package es.us.isa.FAMA.loader;

import java.util.Collection;

import java.util.Map;

import es.us.isa.FAMA.Reasoner.CriteriaSelector;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.models.variabilityModel.parsers.ModelParser;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;

/**
 * This class is needed to load the rest of the plugins that FaMa uses.
 */

public interface ExtensionsLoader {

	public Collection<Reasoner> getReasoners();
	
	public Map<String,Reasoner> getReasonersIdMap();
	
	public Map<String,Class<Question>> getQuestionsMap();
	
	public Map<String,Class<IVariabilityModelTransform>> getTransformationsMap();
	
	public Map<String,CriteriaSelector> getSelectorsMap();
	
	public ModelParser getModelParser();
	
}
