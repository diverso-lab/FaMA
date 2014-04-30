/*
	This file is part of FaMaTS.

    FaMaTS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FaMaTS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.

 */
package es.us.isa.FAMA.Exceptions;
/**
 * This exception will thrown when a method did not get the expected inputs.
 */
public class FAMAParameterException extends FAMAException {

	private static final long serialVersionUID = -933234655213985094L;
	

	public FAMAParameterException(){
		super();
	}
	
	public FAMAParameterException(String exp){
		super("exp");
	}


}
