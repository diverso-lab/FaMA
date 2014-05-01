/**
 * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.utils;

public class BettyException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int FATAL = 1;
	public static int WARNING = 2;
	
	public int severity = 0;
	
	public BettyException() {
	}

	public BettyException(String message) {
		super(message);
	}
	
	public int getSeverity() {
		return this.severity;
	}
	
	public void setSeverity(int s) {
		this.severity = s;
	}
	

}
