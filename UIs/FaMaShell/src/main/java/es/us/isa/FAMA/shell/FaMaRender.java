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
package es.us.isa.FAMA.shell;

import es.us.isa.FAMA.shell.commands.Command;
import es.us.isa.FAMA.shell.utils.CommandLineInputUtils;

public class FaMaRender implements ShellRender {
	private String prompt;
	private CommandFactory commandFactory;

	public FaMaRender() {
		this.prompt = "$";

		this.commandFactory = new CommandFactory("commands.xml");
	}

	public Command getCommand() {
		Command command = null;
		String commandLine = null;
		String[] parsedCommandLine = (String[]) null;
		do {
			printPrompt();

			commandLine = CommandLineInputUtils.getCommandLine();

			parsedCommandLine = CommandLineInputUtils
					.parseCommandLine(commandLine);
			try {
				command = this.commandFactory.getCommand(parsedCommandLine);
			} catch (UnknownCommandException e) {
				showMessage("Unknown command: " + commandLine);
			} catch (InvalidCommandException e) {
				showMessage("Invalid command: " + commandLine);
			}
		}

		while (command == null);

		return command;
	}

	private void printPrompt() {
		System.out.print(this.prompt + ">");
	}

	public String getPrompt() {
		return this.prompt;
	}

	public void print(String s) {
		System.out.print(s);
	}

	public void printWellcome() {
		println("Wellcome to the mock shell :)");
	}

	public void println(String s) {
		System.out.println(s);
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public void showMessage(String message) {
		println(message);
	}
}