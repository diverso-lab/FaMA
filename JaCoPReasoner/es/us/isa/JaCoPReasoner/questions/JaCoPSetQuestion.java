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
package es.us.isa.JaCoPReasoner.questions;

import java.util.ArrayList;
import java.util.List;

import JaCoP.search.ComparatorVariable;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.JaCoPReasoner.JaCoPQuestion;
import es.us.isa.JaCoPReasoner.JaCoPReasoner;
import es.us.isa.JaCoPReasoner.JaCoPResult;

public class JaCoPSetQuestion extends JaCoPQuestion implements SetQuestion {

	private List<JaCoPQuestion> questionsList;
	
	public JaCoPSetQuestion () {
		questionsList = new ArrayList<JaCoPQuestion>();
	}
	
	/* (non-Javadoc)
	 * @see tdg.SPL.Reasoner.SetQuestion#addQuestion(tdg.SPL.Reasoner.Question)
	 */
	public void addQuestion(Question q) {
		if ( q instanceof JaCoPQuestion )
			questionsList.add((JaCoPQuestion) q);
	}

	/* (non-Javadoc)
	 * @see tdg.SPL.Reasoner.CSPSolver.CSPQuestion#preReason(tdg.SPL.Reasoner.Reasoner)
	 */
	public void preAnswer(JaCoPReasoner r) {
		for ( int i = questionsList.size() - 1; i >= 0; i--) {
			((JaCoPQuestion)questionsList.get(i)).preAnswer(r);
		}
	}

	public PerformanceResult answer(JaCoPReasoner r)  {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		JaCoPResult res = null;
		for ( int i = 0; i < questionsList.size(); i++) {
			JaCoPResult pr = (JaCoPResult)((JaCoPQuestion)questionsList.get(i)).answer(r);
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
	
	public void postAnswer(JaCoPReasoner r) {
		for ( int i = 0; i < questionsList.size(); i++) {
			((JaCoPQuestion)questionsList.get(i)).postAnswer(r);
		}
	}

	/* (non-Javadoc)
	 * @see tdg.SPL.Reasoner.JaCoP.JaCoPQuestion#setHeuristics(JaCoP.SelectVariable)
	 */
	public void setHeuristics(ComparatorVariable heuristics) {
		for ( int i = 0; i < questionsList.size(); i++) {
			((JaCoPQuestion)questionsList.get(i)).setHeuristics(heuristics);
		}
	}


}
