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

import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
import es.us.isa.FAMA.shell.BaseShell;

public class VariabilityCommand extends OperationCommand {
	protected void concreteExecution(BaseShell shell) {
		VariabilityQuestion vq = (VariabilityQuestion) qt
				.createQuestion("Variability");
		if (vq == null){
			shell.showMessage("Current model does not accept this operation");
		}
		else{
			qt.ask(vq);
			shell.showMessage("Model variability: " + vq.getVariability());
		}
		
	}

	public void configure(String[] args) {
		if (args.length > 1) {
			this.syntaxError = "Bad syntax error. This operation does not requires parameters";
		} else
			this.syntaxError = null;
	}
}