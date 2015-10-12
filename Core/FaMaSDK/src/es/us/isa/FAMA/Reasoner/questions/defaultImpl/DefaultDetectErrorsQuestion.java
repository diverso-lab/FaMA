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
package es.us.isa.FAMA.Reasoner.questions.defaultImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Observation;
import es.us.isa.FAMA.errors.Observation.ErrorLevel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public abstract class DefaultDetectErrorsQuestion implements
		DetectErrorsQuestion {

	private Collection<Observation> observations;
	private Collection<Error> errors;

	public DefaultDetectErrorsQuestion() {
		super();
		this.observations = null;
		this.errors = new LinkedList<Error>();
	}

	public Collection<Error> getErrors() {
		return errors;
	}

	public void setObservations(Collection<Observation> obs) {
		this.observations = obs;

	}

	public PerformanceResult answer(Reasoner r)  {
		if (observations == null) {
			throw new FAMAParameterException("No observations for detect errors question");
		} else {
			PerformanceResult res = this.performanceResultFactory();
			// iterators can be used as collection is randomly changed while
			// observations are traversed
			while (!observations.isEmpty()) {
				Iterator<Observation> ito = observations.iterator();
				Observation obs = ito.next();
				ito.remove();
				boolean isErroneous = detectError(r, obs, res);
				if (isErroneous) {
					Error error = obs.createError();
					Collection<Observation> discardedObs;
					Collection<Observation> carriedObs;
					if (obs.getErrorLevel() == ErrorLevel.CRITICAL_ERROR) {
						observations = new ArrayList<Observation>();
						errors = new ArrayList<Error>();
						// This path is cause we consider that if a model is
						// void,
						// problems about dead or false optionals doesn't exist.
						discardedObs = new ArrayList<Observation>();
						carriedObs = new ArrayList<Observation>();
					} else {
						discardedObs = obs.getDiscardedObs();
						carriedObs = obs.getCarriedObs();
					}
					errors.add(error);
						// remove all the discarded observations as they are not
					// producing errors...
					observations.removeAll(discardedObs);
					// ...and all the observations of carried errors are also
					// removed and placed into
					// the error list
					Iterator<Observation> itco = carriedObs.iterator();
					while (itco.hasNext()) {
						Observation co = itco.next();
						if (observations.contains(co)) {
							observations.remove(co);
							errors.add(co.createError());
						}
					}
				}

			}
			return res;
		}
	}

	@SuppressWarnings( { "unchecked" })
	private boolean detectError(Reasoner r, Observation obs,
			PerformanceResult res)  {
		if (r == null || res == null) {
			throw new FAMAParameterException("");
		} else {
			ValidQuestion valid = this.validQuestionFactory();
			
			Map<? extends VariabilityElement, Object> valuesMap = obs
					.getObservation();
			Iterator<?> its = valuesMap.entrySet().iterator();
			Configuration conf=new Configuration();
			while (its.hasNext()) {
				try {
					Entry<? extends VariabilityElement, Object> entry = (Entry<? extends VariabilityElement, Object>) its
							.next();
					VariabilityElement ve = entry.getKey();
					int value = (Integer) entry.getValue();
					conf.addElement(ve, value);
				} catch (ClassCastException exc) {
				}

			}

			r.applyStagedConfiguration(conf);
			res.addFields(r.ask(valid));
			r.unapplyStagedConfigurations();
			return !(valid.isValid());
		}
	}

	public String toString() {
		String res = "Errors: ";
		Iterator<Error> ite = errors.iterator();
		while (ite.hasNext()) {
			Error e = ite.next();
			res += e.toString() + "\r\n";
		}
		return res;
	}

	public abstract ValidQuestion validQuestionFactory();

	public abstract PerformanceResult performanceResultFactory();
}
