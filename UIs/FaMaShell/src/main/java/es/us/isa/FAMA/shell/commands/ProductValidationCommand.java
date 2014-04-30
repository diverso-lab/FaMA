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

import es.us.isa.FAMA.Reasoner.questions.ExplainInvalidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.shell.BaseShell;
import es.us.isa.FAMA.shell.utils.CommandLineInputUtils;
import java.util.Collection;

public class ProductValidationCommand extends OperationCommand {
	private Product p;

	protected void concreteExecution(BaseShell shell) {
		ValidProductQuestion vpq = (ValidProductQuestion) qt.createQuestion("ValidProduct");
		if (vpq == null){
			shell.showMessage("Current model does not accept this operation");
		}
		else{
			if (this.p == null) {
				shell.showMessage("Please, type product features");
				String s = CommandLineInputUtils.getCommandLine();
				String[] args = CommandLineInputUtils.parseCommandLine(s);
				if (args.length > 0) {
					this.p = new Product();
					GenericFeatureModel fm = (GenericFeatureModel) vm;
					int i = 0;
					do {
						GenericFeature feat = fm.searchFeatureByName(args[i]);
						if (feat != null) {
							this.p.addFeature(feat);
						} else
							this.syntaxError = (args[i] + " feature not found on the model");
						i++;
						if (i >= args.length)
							break;
					} while (this.syntaxError == null);
				} else {
					this.syntaxError = "Bad syntax. One or more features are necessary for this question";
					shell.showMessage(this.syntaxError);
				}
			}
			
			
			vpq.setProduct(this.p);
			qt.ask(vpq);
			if (vpq.isValid()) {
				shell.showMessage("Valid product");
			} else {
				shell.showMessage("Invalid product");
				shell
						.showMessage("If you want to look for explanations, type (Y)es");
				String s = CommandLineInputUtils.getCommandLine();
				if (s.equalsIgnoreCase("Y")) {
					shell.showMessage("Looking for explanations...");
					ExplainInvalidProductQuestion eipq = (ExplainInvalidProductQuestion) qt
							.createQuestion("ExplainProduct");
					eipq.setInvalidProduct(this.p);
					qt.ask(eipq);
					Collection<GenericFeature> selectFeats = eipq
							.getSelectedFeatures();
					Collection<GenericFeature> deselectFeats = eipq
							.getDeselectedFeatures();
					if (!selectFeats.isEmpty()) {
						shell.showMessage("You have to select these "
								+ selectFeats.size() + " features too:");
						String feats = "";
						for (GenericFeature f : selectFeats) {
							feats = feats + f.getName() + ", ";
						}
						feats = feats.substring(0, feats.length() - 2);
						shell.showMessage(feats);
					}
					if (!deselectFeats.isEmpty()) {
						shell.showMessage("You have to deselect these "
								+ deselectFeats.size() + " features:");
						String feats = "";
						for (GenericFeature f : deselectFeats) {
							feats = feats + f.getName() + ", ";
						}
						feats = feats.substring(0, feats.length() - 2);
						shell.showMessage(feats);
					}
				}

			}
		}
		
		

		this.p = null;
	}

	public void configure(String[] args) {
		if (args.length >= 2) {
			this.p = new Product();
			GenericFeatureModel fm = (GenericFeatureModel) vm;
			for (int i = 1; (i < args.length) && (this.syntaxError == null); i++) {
				GenericFeature feat = fm.searchFeatureByName(args[i]);
				if (feat != null) {
					this.p.addFeature(feat);
					this.syntaxError = null;
				} else {
					this.syntaxError = (args[i] + " feature not found on the model");
				}
			}
		}
	}
}