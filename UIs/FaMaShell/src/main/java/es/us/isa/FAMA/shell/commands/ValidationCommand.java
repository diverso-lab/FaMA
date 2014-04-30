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
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.errors.Explanation;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.shell.BaseShell;
import es.us.isa.FAMA.shell.utils.CommandLineInputUtils;

/**
 * This command unificates ValidQuestion, DetectErrorsQuestion and
 * ExplainErrorsQuestion on a single interactive operation
 * 
 * @author Jesus
 * 
 */
public class ValidationCommand extends OperationCommand {

	// TODO probarle a ver si rula bien

	@Override
	protected void concreteExecution(BaseShell shell) {
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		if (vq == null){
			shell.showMessage("Current model does not accept this operation");
		}
		else{
			qt.ask(vq);
			if (vq.isValid()) {
				shell.showMessage("Model is valid");
			} else {
				shell
						.showMessage("Model is NOT valid. "
								+ "Do you want to look for errors? Type (Y)es in that case");
				String s = CommandLineInputUtils.getCommandLine();
				if (s.equalsIgnoreCase("Y")) {
					shell.showMessage("Looking for errors...");
					DetectErrorsQuestion deq = (DetectErrorsQuestion) qt
							.createQuestion("DetectErrors");
					GenericFeatureModel model = (GenericFeatureModel) vm;
					deq.setObservations(model.getObservations());
					qt.ask(deq);
					Collection<es.us.isa.FAMA.errors.Error> errors = deq
							.getErrors();
					shell.showMessage(errors.size() + " errors found:");
					int i = 1;
					for (es.us.isa.FAMA.errors.Error e : errors) {
						String s1 = i + ": ";
						Map<? extends VariabilityElement, Object> obs = e
								.getObservation().getObservation();
						Set entrySet = obs.entrySet();
						for (Object obj : entrySet) {
							Entry<? extends VariabilityElement, Object> entry = (Entry<? extends VariabilityElement, Object>) obj;
							s1 += entry.getKey().getName() + " = "
									+ entry.getValue().toString() + ", ";
						}
						s1 = s1.substring(0, s1.length() - 2);
						shell.showMessage(s1);
						i++;
					}
					shell.showMessage("");
					shell
							.showMessage("Do you want explanations for errors? Type (Y)es in that case");
					s = CommandLineInputUtils.getCommandLine();
					if (s.equalsIgnoreCase("Y")) {
						shell.showMessage("Looking for explanations...");
						ExplainErrorsQuestion eeq = (ExplainErrorsQuestion) qt
								.createQuestion("Explanations");
						eeq.setErrors(errors);
						qt.ask(eeq);
						errors = eeq.getErrors();
						i = 1;
						for (es.us.isa.FAMA.errors.Error e : errors) {
							String s1 = i + ": ";
							Map<? extends VariabilityElement, Object> obs = e
									.getObservation().getObservation();
							Set entrySet = obs.entrySet();
							for (Object obj : entrySet) {
								Entry<? extends VariabilityElement, Object> entry = (Entry<? extends VariabilityElement, Object>) obj;
								s1 += entry.getKey().getName() + " = "
										+ entry.getValue().toString() + ", ";
							}
							s1 += " -> ";
							Collection<Explanation> exps = e.getExplanations();
							for (Explanation exp : exps) {
								Collection<GenericRelation> rels = exp
										.getRelations();
								s1 += "[";
								for (GenericRelation rel : rels) {
									s1 += rel.getName() + ",";
								}
								// nos cargamos la ultima ','
								s1 = s1.substring(0, s1.length() - 1);
								s1 += "], ";
							}

							s1 = s1.substring(0, s1.length() - 2);
							shell.showMessage(s1);
							i++;
						}
					}
				}
			}
		}
		
	}

	@Override
	public void configure(String[] args) {
		if (args.length != 1) {
			this.syntaxError = "Bad syntax. No additional parameters are required for this question";
		} else
			this.syntaxError = null;
	}

}
