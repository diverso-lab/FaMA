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
package es.us.isa.JavaBDDReasoner.questions;

import java.util.ArrayList;
import java.util.List;

import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.JavaBDDReasoner.JavaBDDResult;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.JavaBDDReasoner.JavaBDDQuestion;

public class JavaBDDSetQuestion extends JavaBDDQuestion implements SetQuestion {

private List<JavaBDDQuestion> questionsList;
	
	
	public JavaBDDSetQuestion() {
		questionsList = new ArrayList<JavaBDDQuestion>();
	}
	
	public void addQuestion(Question q) {
		if ( q instanceof JavaBDDQuestion )
			questionsList.add((JavaBDDQuestion) q);
	}
	
	public void preAnswer(Reasoner r) {
		for ( int i = questionsList.size() - 1; i >= 0; i--) {
			((JavaBDDQuestion)questionsList.get(i)).preAnswer(r);
		}
	}

	public PerformanceResult answer(Reasoner r)  {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		JavaBDDResult res = null;
		for ( int i = 0; i < questionsList.size(); i++) {
			JavaBDDResult sr = (JavaBDDResult)((JavaBDDQuestion)questionsList.get(i)).answer(r);
			if (res == null)
				res = sr;
			else
				if (sr != null) { 
					res.addFields(sr);
				}
		}
		return res;
	}
	
	public void postAnswer(Reasoner r) {
		for ( int i = 0; i < questionsList.size(); i++) {
			((JavaBDDQuestion)questionsList.get(i)).postAnswer(r);
		}
	}

}
