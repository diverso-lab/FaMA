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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.ChocoReasoner.attributed.ChocoQuestion;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.InvalidProductQuestion;
import es.us.isa.FAMA.models.featureModel.AttributedProduct;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;

public class ChocoInvalidProductQuestion extends ChocoQuestion implements
		InvalidProductQuestion {

	private AttributedProduct product;

	private Collection<GenericFeature> feats;

	@Override
	public Collection<GenericFeature> getInvalidFeatures() {
		return feats;
	}

	@Override
	public void setProduct(Product p) {
		if (p instanceof AttributedProduct) {
			product = (AttributedProduct) p;
		} else {
			throw new IllegalArgumentException(
					"The product should be an attributed product");
		}
	}

	public PerformanceResult answer(Reasoner r) {
		feats = new HashSet<GenericFeature>();
		ChocoResult res = new ChocoResult();
		ChocoReasoner choco = (ChocoReasoner) r;
		Model chocoProblem = choco.getProblem();

		// 1. Reificar las features de la configuracion
		// al ser features, ya estan reificadas de por si
		// solo tenemos que a�adirlas al conjunto a maximizar
		Map<String, IntegerVariable> vars = choco.getVariables();
		Collection<GenericAttributedFeature> productFeats = product
				.getAttFeatures();
		Map<IntegerVariable, GenericFeature> configVars = new HashMap<IntegerVariable, GenericFeature>();
		Map<String, IntegerVariable> atts = choco.getAttributesVariables();

		Collection<GenericAttributedFeature> nonSelectedFeats = choco
				.getAllFeatures();
		nonSelectedFeats.removeAll(productFeats);

		// features seleccionadas
		for (GenericAttributedFeature f : productFeats) {
			String s = f.getName();
			IntegerVariable v = vars.get(s);
			configVars.put(v, f);
			// debemos imponer los valores de sus atributos
			Collection<? extends GenericAttribute> featAtts = f.getAttributes();
			for (GenericAttribute att : featAtts) {
				if (att.hasFixedValue()) {
					Integer val = att.getIntegerValue(att.getValue());
					String attName = att.getFullName();
					IntegerVariable attVar = atts.get(attName);
					// si la feature esta seleccionada, el atributo tomara
					// el valor que se le ha fijado
					Constraint c = Choco.implies(Choco.gt(v, 0), Choco.eq(
							attVar, val));
					chocoProblem.addConstraint(c);
				}
			}
		}

		// features no seleccionadas
		for (GenericAttributedFeature f : nonSelectedFeats) {
			String s = f.getName();
			IntegerVariable v = vars.get(s);
			chocoProblem.addConstraint(Choco.eq(v, 0));
		}
		
//		//TODO eliminar esto, es solo pa ver si la configuracion es valida
//		Solver solver1 = new CPSolver();
//		solver1.read(chocoProblem);
//		System.out.println("Is the config valid? " + solver1.solve());

		//El problema esta en la maximizacion, pues se detecta que la configuracion 
		//NO es valida
		IntegerVariable[] varsToMax = configVars.keySet().toArray(
				new IntegerVariable[0]);
		IntegerVariable suma = Choco.makeIntVar("suma", 0, varsToMax.length);
		IntegerExpressionVariable sumatorio = Choco.sum(varsToMax);
		Constraint sumReifieds = Choco.eq(suma, sumatorio);
		chocoProblem.addConstraint(sumReifieds);

		// 2. Maximizar
		Solver solver = new CPSolver();
		solver.read(chocoProblem);
		solver.maximize(solver.getVar(suma), false);

		// TODO inicialmente, solo vamos a devolver
		// las features cuya eliminacion "arreglaria
		// la configuracion", ver que hacer mas adelante

		// cada feature de la configuracion que no este activada
		// la a�adimos al resultado
		Set<Entry<IntegerVariable, GenericFeature>> entries = configVars
				.entrySet();
		for (Entry<IntegerVariable, GenericFeature> entry : entries) {
			IntDomainVar v = solver.getVar(entry.getKey());
			if (v.getVal() == 0) {
				feats.add(entry.getValue());
			}
		}
		
//		for (IntegerVariable var:varsToMax){
//			IntDomainVar v = solver.getVar(var);
//			if (v.getVal() == 0) {
//				feats.add(configVars.get(var));
//			}
//		}

		System.out.println("Valor de suma: "+solver.getVar(suma).getVal());
		
		res.addFields(solver);
		return res;
	}

}
