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

import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.shell.BaseShell;
import java.util.Collection;

public class ProductsCommand extends OperationCommand {
	
	protected void concreteExecution(BaseShell shell) {
		ProductsQuestion pq = (ProductsQuestion) qt.createQuestion("Products");
		if (pq == null){
			shell.showMessage("Current model does not accept this operation");
		}
		else{
			qt.ask(pq);
			Collection<? extends GenericProduct> products = pq.getAllProducts();
			int i = 1;
			for (GenericProduct p : products) {
				String productFeatures = "Product " + i + ": ";
				Collection<VariabilityElement> elems = p.getElements();
				for (VariabilityElement elem : elems) {
					productFeatures = productFeatures + elem + " ";
				}
				shell.showMessage(productFeatures);
				i++;
			}
		}
		
	}

	public void configure(String[] args) {
		if (args.length > 1) {
			this.syntaxError = "Bad syntax. No parameters are necessary for this command";
		} else
			this.syntaxError = null;
	}
}