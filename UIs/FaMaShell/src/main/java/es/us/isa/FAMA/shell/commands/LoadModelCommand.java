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

import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.shell.BaseShell;
import java.io.File;

public class LoadModelCommand extends BaseCommand {
	private String modelPath;

	public LoadModelCommand() {
		this.modelPath = null;
	}

	public void configure(String[] args) {
		if (args.length == 2) {
			File f = new File(args[1]);
			if (f.exists()) {
				this.modelPath = args[1];
				this.syntaxError = null;
			} else {
				this.syntaxError = ("Error, " + args[1] + " does not exist");
			}
		} else {
			this.syntaxError = ("Bad syntax error: " + args.length + " parameters given");
		}
	}

	protected void concreteExecution(BaseShell shell) {
		shell.showMessage("Loading model...");
		vm = qt.openFile(this.modelPath);
		qt.setVariabilityModel(vm);
		if (vm instanceof GenericAttributedFeatureModel){
			shell.showMessage("Attributed feature model loaded");
		}
		else{
			shell.showMessage("Feature model loaded");
//			shell.showMessage("For this model, you have available these operations:");
		}
//		shell.showMessage("Loaded!!");
		this.modelPath = null;
	}
}