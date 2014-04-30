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

import java.util.Collection;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidProductQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.JaCoPReasoner.JaCoPQuestion;
import es.us.isa.JaCoPReasoner.JaCoPReasoner;
import es.us.isa.JaCoPReasoner.JaCoPResult;

public class JaCoPDefaultValidProductQuestion extends JaCoPQuestion implements
		ValidProductQuestion {

	private DefValidProductQuestion vpq;
	
	public JaCoPDefaultValidProductQuestion() {
		super();
		vpq = new DefValidProductQuestion();
	}

	public boolean isValid() {
		return vpq.isValid();
	}

	public void setProduct(Product p)  {
		if(p==null){
			throw new FAMAParameterException("Product:Not specified");
		}
		vpq.setProduct(p);
	}
	
	public PerformanceResult answer(JaCoPReasoner r) {
		if(r==null){
			throw new FAMAParameterException("Reasoner:Not specified");
		}
		JaCoPReasoner jacop = (JaCoPReasoner) r;
		vpq.setAllFeatures(jacop.getAllFeatures());
		return vpq.answer(r);
	}
	
	public String toString(){
		return this.vpq.toString();
	}

	class DefValidProductQuestion extends DefaultValidProductQuestion{

		Collection<GenericFeature> c;
		
		

		@Override
		public Collection<GenericFeature> getAllFeatures() {
			return c;
		}
		
		public void setAllFeatures(Collection<GenericFeature> c){
			this.c = c;
		}

		@Override
		public PerformanceResult performanceResultFactory() {
			return new JaCoPResult();
		}

		
		@Override
		public ValidQuestion validQuestionFactory() {
			return new JaCoPValidQuestion();
		}

		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}
		
	}
}
