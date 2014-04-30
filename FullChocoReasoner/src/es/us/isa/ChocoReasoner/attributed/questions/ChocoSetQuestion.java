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
package es.us.isa.ChocoReasoner.attributed.questions;

import java.util.ArrayList;
import java.util.List;

import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.ChocoReasoner.attributed.ChocoQuestion;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;

public class ChocoSetQuestion extends ChocoQuestion implements SetQuestion{

	private List<ChocoQuestion> questionsList=new ArrayList<ChocoQuestion>();
	
	public void addQuestion(Question q) {
		if ( q instanceof ChocoQuestion )
			questionsList.add((ChocoQuestion) q);
	}
	
	public void preAnswer(Reasoner r)  {
		for ( int i = questionsList.size() - 1; i >= 0; i--) {
			((ChocoQuestion)questionsList.get(i)).preAnswer(r);
		}
	}
	
	public PerformanceResult answer(Reasoner r) {
		if(r==null){
			throw new FAMAException("Reasoner not present");
		}else{
		ChocoResult res = null;
		for ( int i = 0; i < questionsList.size(); i++) {
			ChocoResult pr = (ChocoResult)((ChocoQuestion)questionsList.get(i)).answer(r);
			if (pr != null) { 
				if (res == null) {
					res = pr;
				} else {
					res.addFields(pr);
				}
			}
		}
		return res;
		}
	}
	
	@Override
	public void postAnswer(Reasoner r)  {
		for ( int i = 0; i < questionsList.size(); i++) {
			((ChocoQuestion)questionsList.get(i)).postAnswer(r);
		}
	}
	
}
