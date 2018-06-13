/**
 * 	This file is part of FaMaTS.
 *
 *     FaMaTS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FaMaTS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.us.isa.ChocoReasoner.attributed.questions;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.ChocoReasoner.attributed.ChocoQuestion;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CoreFeaturesQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;

public class ChocoCoreFeaturesQuestion extends ChocoQuestion implements
		CoreFeaturesQuestion {

	private List<GenericFeature> coreFeats;
	
	@Override
	public Collection<GenericFeature> getCoreFeats() {
		return coreFeats;
	}
	
	public PerformanceResult answer(Reasoner r){
		ChocoResult result = new ChocoResult();
		coreFeats = new LinkedList<GenericFeature>();
		Solver s = new CPSolver();
		ChocoReasoner choco = (ChocoReasoner) r;
		Model model = choco.getProblem();
		s.read(model);
		
		//utilizando propagacion
		try {
			s.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		
		Collection<GenericAttributedFeature> allFeats = choco.getAllFeatures();
		Map<String,IntegerVariable> vars = choco.getVariables();
		for (GenericAttributedFeature feat:allFeats){
			IntegerVariable v = vars.get(feat.getName());
			IntDomainVar vs = s.getVar(v);
			int upper = vs.getSup();
			int lower = vs.getInf();
			if ((upper == lower) && (upper > 0)){
				coreFeats.add(feat);
			}
		}
		
//		result.fillFields(s);
		return result;
	}

}
