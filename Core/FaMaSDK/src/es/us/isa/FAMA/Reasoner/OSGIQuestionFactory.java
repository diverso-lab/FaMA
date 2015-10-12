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
package es.us.isa.FAMA.Reasoner;

import java.util.HashMap;
import java.util.Map;

import es.us.isa.FAMA.Reasoner.Factory.QuestionAbstractFactory;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class OSGIQuestionFactory implements QuestionAbstractFactory {

	//implementaciones concretas que hace el reasoner de la clase
	private Map<String, Class<Question>> questionClasses;
	
	
	public OSGIQuestionFactory(){
		questionClasses = new HashMap<String,Class<Question>>();
	}
	
	public void addQuestion(String id, Class<Question> qc){
		questionClasses.put(id, qc);
	}
	
	public Question createQuestion(Class<Question> questionType) {
		Question res = null;
		String interfaceName = questionType.getName();
		Class<Question> clazz = questionClasses.get(interfaceName);
		if (clazz != null){
			try {
				res = clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	public int questionTime(String questionType, VariabilityModel fm) {
		return 0;
	}

}
