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

package es.us.isa.FAMA.shell.commands;

import es.us.isa.FAMA.Reasoner.questions.VariantFeaturesQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.shell.BaseShell;
import java.util.Collection;

public class VariantFeaturesCommand extends OperationCommand {
	protected void concreteExecution(BaseShell shell) {
		VariantFeaturesQuestion vfq = (VariantFeaturesQuestion) qt
				.createQuestion("VariantFeatures");
		if (vfq == null){
			shell.showMessage("Current model does not accept this operation");
		}
		else{
			qt.ask(vfq);
			Collection<GenericFeature> col = vfq.getVariantFeats();
			String res = "Variant features: ";
			for (GenericFeature f : col) {
				res = res + f.getName() + ", ";
			}
			res = res.substring(0, res.length() - 2);
			shell.showMessage(res);
		}
		
	}

	public void configure(String[] args) {
		if (args.length > 1) {
			this.syntaxError = "Bad syntax error. This operation does not requires parameters";
		} else
			this.syntaxError = null;
	}
}