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

import es.us.isa.FAMA.shell.BaseShell;
import es.us.isa.FAMA.shell.CommandFactory;

public abstract interface Command
{
  public abstract String getName();

  public abstract void setName(String paramString);

  public abstract String getHelp();

  public abstract void setHelp(String paramString);

  public abstract void configure(String[] paramArrayOfString);

  public abstract void execute(BaseShell paramBaseShell);

  public abstract void setCommandFactory(CommandFactory paramCommandFactory);
}