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
package es.us.isa.FAMA.Reasoner.questions;

import java.util.Collection;



import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.errors.Error;

public interface ExplainErrorsQuestion extends Question {
	
	public void setErrors (Collection<Error> colErrors);
	
	public Collection<Error> getErrors();
	
}
