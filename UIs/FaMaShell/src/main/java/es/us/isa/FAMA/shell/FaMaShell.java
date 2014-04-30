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
import es.us.isa.FAMA.shell.commands.ExitAppCommand;


public class FaMaShell implements BaseShell {

	private ShellRender render;
	
	public FaMaShell(){
		render = new FaMaRender();
	}
	
	public FaMaShell(ShellRender render){
		this.render = render;
	}
	
	@Override
	public ShellRender getShellRender() {
		return render;
	}

	@Override
	public void run() {
		Command c = null;
		showMessage("Welcome to FaMa shell");
		
		while (!(c instanceof ExitAppCommand)){
			c = render.getCommand();
			c.execute(this);
		}
		
		showMessage("Bye!");
	}

	@Override
	public void showMessage(String message) {
		render.showMessage(message);
	}

}
