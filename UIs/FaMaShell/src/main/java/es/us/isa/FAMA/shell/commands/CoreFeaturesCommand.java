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

import java.util.Collection;

import es.us.isa.FAMA.Reasoner.questions.CoreFeaturesQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.shell.BaseShell;

public class CoreFeaturesCommand extends OperationCommand {

	@Override
	protected void concreteExecution(BaseShell shell) {
		// TODO asegurarme que ese es el nombre de la operacion
		CoreFeaturesQuestion cfq = (CoreFeaturesQuestion) qt.createQuestion("CoreFeatures");
		if (cfq == null){
			shell.showMessage("Current model does not accept this operation");
		}
		else{
			qt.ask(cfq);
			Collection<GenericFeature> col = cfq.getCoreFeats();
			String res = "Core features: ";
			for (GenericFeature f:col){
				res += f.getName()+", ";
			}
			res = res.substring(0,res.length() - 2);
			shell.showMessage(res);
		}
		
	}

	@Override
	public void configure(String[] args) {
		// TODO unificar los mensajes de error
		if (args.length > 1) {
		      this.syntaxError = "Bad syntax error. This operation does not requires parameters";
		    }
		    else
		      this.syntaxError = null;
	}

}
