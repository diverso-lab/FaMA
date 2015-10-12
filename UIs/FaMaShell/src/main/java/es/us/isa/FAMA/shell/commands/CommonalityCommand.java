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

import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.shell.BaseShell;
import es.us.isa.FAMA.shell.utils.CommandLineInputUtils;

public class CommonalityCommand extends OperationCommand {

	// TODO probar este comando

	private GenericFeature f;

	public CommonalityCommand() {
		f = null;
	}

	@Override
	protected void concreteExecution(BaseShell shell) {
		CommonalityQuestion cq = (CommonalityQuestion) qt.createQuestion("Commonality");
		if (cq == null){
			shell.showMessage("Current model does not accept this operation");
		}
		else{
			String s = "";
			if (f == null) {
				// pedir feature
				shell.showMessage("Please, type a feature from the model");
				s = CommandLineInputUtils.getCommandLine();
				GenericFeatureModel fm = (GenericFeatureModel) vm;
				f = fm.searchFeatureByName(s);
			}

			if (f == null) {
				// si despues de esto sigue siendo null...
				shell.showMessage("Feature " + s + " is not in the model");
			} else {
				
				cq.setFeature(f);
				qt.ask(cq);
				shell.showMessage(f.getName() + "'s commonality: "
						+ cq.getCommonality());
			}
		}
	}

	@Override
	public void configure(String[] args) {
		if (args.length == 2) {
			GenericFeatureModel fm = (GenericFeatureModel) vm;
			this.f = fm.searchFeatureByName(args[1]);
			if (this.f == null) {
				this.syntaxError = ("Feature " + args[1] + " is not in the model");
			} else {
				this.syntaxError = null;
			}

		} else if (args.length > 2) {
			this.syntaxError = "Bad syntax error. Only one parameter is required: a feature";
		}
	}

}
