package es.us.isa.FAMA.shell.commands;

import es.us.isa.FAMA.shell.BaseShell;

public abstract class OperationCommand extends BaseCommand {

	public void execute(BaseShell shell) {
		if (vm == null) {
			shell.showMessage("Error. You should load before a model using load command.");
		} else
			super.execute(shell);
	}

}
