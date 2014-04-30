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
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CommandFactory
{
  private static Map<String, Class> commandRegistry = new HashMap();
  private Map<String, Command> commandInstances;
  private Map<String, String> commandsHelp;
  private Set<String> activeCommands;

  public void loadCommand(String commandName, Command command)
  {
    Class commandClass = command.getClass();
    this.commandInstances.put(commandName, command);
    loadCommand(commandName, commandClass.getCanonicalName());
  }

  public static void loadCommand(String commandName, String className)
  {
    try
    {
      Class commandClass = Class.forName(className);
      commandRegistry.put(commandName, commandClass);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
  }

  public static void loadCommand(String className)
  {
    try
    {
      Class commandClass = Class.forName(className);

      Command command = (Command)commandClass.newInstance();

      commandRegistry.put(command.getName(), commandClass);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (InstantiationException e) {
      e.printStackTrace();
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public CommandFactory(String[] commandNames) throws UnknownCommandException
  {
    this();
    for (String commandName : commandNames)
      if (commandRegistry.containsKey(commandName))
        this.activeCommands.add(commandName);
      else throw new UnknownCommandException();
  }

  public CommandFactory(String configFile)
  {
    this();
    loadConfigFile(configFile);
  }

  private CommandFactory() {
    this.commandInstances = new HashMap();
    this.activeCommands = new HashSet();
    this.commandsHelp = new HashMap();
  }

  public Set<String> getActiveCommands()
  {
    return this.activeCommands;
  }

  public void addCommand(String commandName) throws UnknownCommandException {
    if ((commandRegistry.containsKey(commandName)) || (this.commandInstances.containsKey(commandName)))
      this.activeCommands.add(commandName);
    else throw new UnknownCommandException(); 
  }

  public void removeCommand(String commandName, boolean removeinstances) throws UnknownCommandException, InvalidCommandException
  {
    if ((commandRegistry.containsKey(commandName)) || (this.commandInstances.containsKey(commandName))) {
      if (this.activeCommands.contains(commandName)) {
        this.activeCommands.remove(commandName);
        if (removeinstances)
          this.commandInstances.remove(commandName); 
      } else {
        throw new InvalidCommandException();
      }
    } else throw new UnknownCommandException(); 
  }

  public Command getCommand(String[] arguments) throws UnknownCommandException, InvalidCommandException
  {
    String commandName = null;
    Command command = null;
    if ((arguments != null) && 
      (arguments.length > 0)) {
      commandName = arguments[0];
    }
    if (this.activeCommands.contains(commandName)) {
      if (commandRegistry.containsKey(commandName)) {
        if (this.commandInstances.containsKey(commandName)) {
          command = (Command)this.commandInstances.get(commandName);
        } else {
          Class commandClass = (Class)commandRegistry.get(commandName);
          try
          {
            command = (Command)commandClass.newInstance();
            command.setCommandFactory(this);
            command.setHelp((String)this.commandsHelp.get(commandName));
            command.setName(commandName);
            this.commandInstances.put(commandName, command);
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InstantiationException e) {
            e.printStackTrace();
          }
        }
      } else throw new UnknownCommandException(); 
    }
    else throw new InvalidCommandException();

    if (command != null)
      command.configure(arguments);
    return command;
  }

  private void loadConfigFile(String path) {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try
    {
      DocumentBuilder db = dbf.newDocumentBuilder();

      Document dom = db.parse(path);
      NodeList rootList = dom.getChildNodes();
      Node root = rootList.item(0);
      NodeList nl1 = root.getChildNodes();
      for (int i = 0; i < nl1.getLength(); i++) {
        Node n1 = nl1.item(i);
        if (n1.getNodeType() == 1) {
          if (n1.getNodeName().equals("commands"))
          {
            NodeList commandsList = n1.getChildNodes();
            for (int j = 0; j < commandsList.getLength(); j++) {
              Node commandNode = commandsList.item(j);
              if ((commandNode.getNodeType() != 1) || 
                (!commandNode.getNodeName().equals("command"))) {
                continue;
              }
              NamedNodeMap atts = commandNode.getAttributes();
              String id = atts.getNamedItem("id").getTextContent();
              String path2class = atts.getNamedItem("path").getTextContent();
              String aux = atts.getNamedItem("active").getTextContent();
              boolean active = Boolean.parseBoolean(aux);

              NodeList nl2 = commandNode.getChildNodes();
              String help = "";
              for (int k = 0; k < nl2.getLength(); k++) {
                Node helpNode = nl2.item(k);
                if ((helpNode.getNodeType() != 1) || 
                  (!helpNode.getNodeName().equals("help"))) continue;
                help = helpNode.getTextContent();
              }

              loadCommand(id, path2class);
              if (active) {
                this.activeCommands.add(id);
              }
              this.commandsHelp.put(id, help);
            }
          }
          else {
            n1.getNodeName().equals("errors");
          }
        }
      }

    }
    catch (ParserConfigurationException pce)
    {
      pce.printStackTrace();
    } catch (SAXException se) {
      se.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}