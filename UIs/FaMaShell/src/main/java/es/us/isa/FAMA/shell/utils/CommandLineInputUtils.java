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

package es.us.isa.FAMA.shell.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandLineInputUtils {

	
	public static String getCommandLine(){
		String commandLine = null;
        
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			commandLine = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commandLine;
	}
	
	
	public static String[] parseCommandLine(String commandLine){
	    if (commandLine == null)
	        return new String[0];
	    
	    char separator = ' ';

	    List<String> strings = new ArrayList<String>();
	
	    int startx = 0;
	    int cursor = 0;
	    int length = commandLine.length();
	
	    while (cursor < length)
	    {
	        if (commandLine.charAt(cursor) == separator)
	        {
	            String item = commandLine.substring(startx, cursor);
	            strings.add(item);
	            startx = cursor + 1;
	            cursor++;
	            while((cursor < length) && (commandLine.charAt(cursor) == separator)){
	            	startx = cursor + 1;
	            	cursor++;
	            }
	        }else{	
	        	cursor++;
	        }
	    }
	
	    if (startx < length)
	        strings.add(commandLine.substring(startx));
	
	    return (String[]) strings.toArray(new String[strings.size()]);		
	}

	
}
