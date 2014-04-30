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

/**
 * 
 */
package es.us.isa.FAMA.shell.commands;

import es.us.isa.FAMA.shell.BaseShell;
import es.us.isa.FAMA.shell.InvalidCommandException;
import es.us.isa.FAMA.shell.UnknownCommandException;

public class HelpCommand extends BaseCommand {
	private String commandName;

	public HelpCommand() {
		this.commandName = null;
	}

	public void configure(String[] arguments) {
		if (arguments.length > 1) {
			this.commandName = arguments[1];
		} else
			this.commandName = null;
	}

	private void showValidCommands(BaseShell shell) {
		shell.showMessage("Valid commands are: ");
		for (String cname : this.commandFactory.getActiveCommands())
			shell.showMessage(cname + " ");
		shell.showMessage("");
	}

	protected void concreteExecution(BaseShell shell) {
		if (this.commandName == null) {
			shell.showMessage(getHelp());
			showValidCommands(shell);
		} else {
			String[] args = { this.commandName };
			try {
				Command command = this.commandFactory.getCommand(args);
				shell.showMessage(command.getHelp());
			} catch (UnknownCommandException e) {
				shell.showMessage("ERROR: " + this.commandName
						+ " is not a valid command.");
				showValidCommands(shell);
			} catch (InvalidCommandException e) {
				shell.showMessage("ERROR: " + this.commandName
						+ " is not a valid command.");
				showValidCommands(shell);
			}
		}
	}
}