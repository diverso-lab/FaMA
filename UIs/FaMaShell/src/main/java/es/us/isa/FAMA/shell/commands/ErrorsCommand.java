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

import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Explanation;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.shell.BaseShell;
import es.us.isa.FAMA.shell.utils.CommandLineInputUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ErrorsCommand extends OperationCommand {
	
	protected void concreteExecution(BaseShell shell) {
		
		DetectErrorsQuestion deq = (DetectErrorsQuestion) qt
				.createQuestion("DetectErrors");
		if (deq == null){
			shell.showMessage("Current model does not accept this operation");
		}
		else{
			shell.showMessage("Looking for errors...");
			GenericFeatureModel model = (GenericFeatureModel) vm;
			deq.setObservations(model.getObservations());
			qt.ask(deq);
			Collection<Error> errors = deq.getErrors();
			if (errors.isEmpty()) {
				shell.showMessage("No error(s) found");
			} else {
				shell.showMessage(errors.size() + " error(s) found:");
				int i = 1;
				for (Error e : errors) {
					String s1 = i + ": "+e.toString();
					shell.showMessage(s1);
					i++;
				}

				shell.showMessage("");
				shell
						.showMessage("Do you want explanations for errors? Type (Y)es in that case");
				String s = CommandLineInputUtils.getCommandLine();
				if (s.equalsIgnoreCase("Y")) {
					shell.showMessage("Looking for explanations...");
					ExplainErrorsQuestion eeq = (ExplainErrorsQuestion) qt
							.createQuestion("Explanations");
					eeq.setErrors(errors);
					qt.ask(eeq);
					errors = eeq.getErrors();
					i = 1;
					for (Error e : errors) {
						Collection<Explanation> exps = e.getExplanations();
						String s1 = i + ": "+e.toString()+" ("+exps.size()+" explanations) -> ";
						for (Explanation exp : exps) {
							Collection<GenericRelation> rels = exp.getRelations();
							s1 = s1 + "[";
							for (GenericRelation rel : rels) {
								s1 = s1 + rel.getName() + ",";
							}

							s1 = s1.substring(0, s1.length() - 1);
							s1 = s1 + "], ";
						}
						s1 = s1.substring(0, s1.length() - 2);
						shell.showMessage(s1);
						i++;
					}
				}
			}
		}
	}

	public void configure(String[] args) {
		if (args.length != 1) {
			this.syntaxError = "Bad syntax. No additional parameters are required for this question";
		} else
			this.syntaxError = null;
	}
}