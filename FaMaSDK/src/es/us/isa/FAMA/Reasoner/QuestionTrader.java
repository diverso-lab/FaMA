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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;

import es.us.isa.FAMA.loader.ExtensionsLoader;
import es.us.isa.FAMA.loader.ExtensionsLoaderFactory;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.ModelParser;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

/**
 * This class is the facade of FaMaTS
 */
public class QuestionTrader {

	protected ExtensionsLoader extLoad;
	protected ModelParser mp;
	protected VariabilityModel fm;
	protected Collection<Reasoner> reasoners;
	protected Map<String, Reasoner> reasonersIdMap;
	protected Map<String, Class<Question>> questionsMap;
	protected CriteriaSelector selector;
	protected Map<String, CriteriaSelector> selectorsMap;
	protected Map<String, Class<IVariabilityModelTransform>> transformationsMap;
	protected ExtensionsLoaderFactory extFactory;
	protected Queue<Configuration> configurations;
	private Reasoner selectedReasoner;

	/**
	 * Default constructor, it uses the file FAMAconfig.xml to know the
	 * questions, reasoners, readers/writers and transformations.
	 */
	public QuestionTrader() {
		this("FaMaConfig.xml");
		selectedReasoner = null;
	}

	/**
	 * This constructor is used to select another FaMaConfigFile, also it's
	 * needed to use OSGI String if you want to use OSGi
	 * 
	 * @param str
	 *            The path for the FaMaConfig file.
	 */
	public QuestionTrader(String str) {
		selectedReasoner = null;
		extFactory = new ExtensionsLoaderFactory();
		extLoad = extFactory.createExtensionsLoader(str, this);
		reasoners = extLoad.getReasoners();
		selectorsMap = extLoad.getSelectorsMap();
		questionsMap = extLoad.getQuestionsMap();
		reasonersIdMap = extLoad.getReasonersIdMap();
		transformationsMap = extLoad.getTransformationsMap();
		configurations = new LinkedList<Configuration>();
		selector = new DefaultCriteriaSelector(this);
		mp = extLoad.getModelParser();
	}

	/**
	 * This will, answer the desired question using the selected reasoner, if no
	 * reasoner selected it will use the one selected by criteria selector.
	 * 
	 * @param q
	 *            The question to be answered
	 * @return A performanceResult with the results of the question
	 * @see PerformanceResult
	 */
	public PerformanceResult ask(Question q) {

		PerformanceResult res = null;
		if (q != null) {
			Class<? extends Reasoner> reasonerClass = q.getReasonerClass();
			if (selectedReasoner == null) {
				Iterator<Reasoner> itr = reasoners.iterator();
				while (itr.hasNext() && res == null) {
					Reasoner r = itr.next();
					if (reasonerClass.isInstance(r)) {
						fm.transformTo(r);
						Iterator<Configuration> configIterator = configurations
								.iterator();
						while (configIterator.hasNext()) {
							r.applyStagedConfiguration(configIterator.next());
						}
						res = r.ask(q);
						r.unapplyStagedConfigurations();
						selector.registerResults(q, fm, res);
					}
				}
			} else {
				if (reasonerClass.isInstance(selectedReasoner)) {
					fm.transformTo(selectedReasoner);
					Iterator<Configuration> configIterator = configurations
							.iterator();
					while (configIterator.hasNext()) {
						selectedReasoner
								.applyStagedConfiguration(configIterator.next());
					}
					res = selectedReasoner.ask(q);
					selectedReasoner.unapplyStagedConfigurations();
					selector.registerResults(q, fm, res);
				}
			}
		}
		return res;

	}

	/**
	 * This method, will return a set of heuristics to be applied to a selected
	 * reasoner.
	 * 
	 * @param r
	 *            the reasoner that we, want to know the heuristics.
	 * @return A Map that associates the name of the heuristic and the classes
	 *         that implements they.
	 */

	public Map<String, Object> getHeuristics(String r) {
		Map<String, Object> res = reasonersIdMap.get(r).getHeusistics();
		return res;
	}

	/**
	 * This method will return a collection with all available reasoners that
	 * implements a selected question.
	 * 
	 * @param q
	 *            The question, which is intended to know the implemented
	 *            reasoners.
	 * @return a Collection of the reasoners that implements a question.
	 */

	public Collection<String> getAvaliableReasoners(String qt) {
		Collection<String> res = new LinkedList<String>();

		Class<Question> q = questionsMap.get(qt);

		Iterator<Reasoner> itr = reasoners.iterator();
		while (itr.hasNext()) {
			Reasoner r = itr.next();
			Question createdQuestion = r.getFactory().createQuestion(q);
			if (createdQuestion != null) {
				Iterator<Entry<String, Reasoner>> iterator = reasonersIdMap.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<String, Reasoner> entry = iterator.next();
					if(entry.getValue().getClass().getName().equals(r.getClass().getName())){
						res.add(entry.getKey());
					}
				}
				
			}
		}
		return res;
	}

	/**
	 * This method will assign a concrete heuristic to the reasoner
	 * 
	 * @param heuristic
	 *            the desired heuristic
	 * @throws FAMAParameterException
	 *             when no specified reasoner.
	 */
	public void setHeuristics(String heuristic) {
		if (selectedReasoner == null) {
			throw new FAMAParameterException(
					"Firstly, you must select a reasoner");
		}
		Object obj = selectedReasoner.getHeusistics().get(heuristic);
		selectedReasoner.setHeuristic(obj);
	}

	/**
	 * This methot create the desired question, this question shoud be first in
	 * FaMaConfigFile. For OSGi, use the same name that we provide in our
	 * example.
	 * 
	 * @param questionType
	 *            the name of the desired question
	 * @return the class of the question.
	 */
	public Question createQuestion(String questionType) {
		// Receive a question name and return its
		// associated interface

		Class<Question> q = questionsMap.get(questionType);

		if (q != null) {
			if (selectedReasoner != null) {
				return selector.createQuestion(q, selectedReasoner, fm);

			} else {
				return selector.createQuestion(q, fm);
			}
		}

		return null;

	}



	/**
	 * will set the variability model that fama will use.
	 * 
	 * @param fm
	 *            the variability model that fama will use.
	 */
	public void setVariabilityModel(VariabilityModel fm) {
		this.fm = fm;
	}

	/**
	 * 
	 * @return the ids of all reasoners founded
	 */
	public Iterator<String> getReasonerIds() {
		return this.reasonersIdMap.keySet().iterator();
	}

	/**
	 * Will return the reasoner with a specified id
	 * 
	 * @param id
	 *            the string that identify the reasoner.
	 * @return the reasoner class.
	 */
	public Reasoner getReasonerById(String id) {
		return reasonersIdMap.get(id);
	}

	/**
	 * Will return all id, of all questions marked in to FAMAConfig,xml
	 * 
	 * @return
	 */
	public Iterator<String> getQuestionsIds() {
		return this.questionsMap.keySet().iterator();
	}

	/**
	 * Will return the question this specified id
	 * 
	 * @param id
	 *            The id of the question
	 * @return the question with the selected id
	 * 
	 */
	public Class<Question> getQuestionById(String id) {
		return questionsMap.get(id);
	}

	/**
	 * Will return the available selectors.
	 * 
	 * @return A set of string with the name of the criteria.
	 * @see CriteriaSelector
	 */
	public Iterator<String> getCriteriaSelectorNames() {
		return this.selectorsMap.keySet().iterator();
	}

	/**
	 * @param selectorName
	 *            the name of the criteria selector that we want.
	 * @return the criteria selector.
	 */
	public CriteriaSelector getCriteriaSelector(String selectorName) {
		return this.selectorsMap.get(selectorName);
	}

	/**
	 * Will force fama to use the criteriaSelector marked
	 * 
	 * @param criteriaName
	 *            the name of the criteria selected
	 * @return true if was possible to assign the selected criteria selector.
	 */
	public boolean setCriteriaSelector(String criteriaName) {
		CriteriaSelector newSelector = selectorsMap.get(criteriaName);
		boolean res = (newSelector != null);
		if (res) {
			this.selector = newSelector;
		}
		return res;
	}

	/**
	 * This method will use the apropiated reader(based in extension) to open
	 * the file and return the variabilitymodel.
	 * 
	 * @param filename
	 *            path to the file we want to open
	 * @return The variabilityModel
	 */
	public VariabilityModel openFile(String filename) {
		return mp.read(filename);
	}

	/**
	 * This method will save the variability model using the specified writer(by
	 * extension).
	 * 
	 * @param filename
	 *            the file that will be created when the method finish his
	 *            execution.
	 * @param vm
	 *            The variabilityModel we want to save.
	 */
	public void writeFile(String filename, VariabilityModel vm) {
		mp.write(vm, filename);
	}

	/**
	 * This method is used to create a transformation between 2 models, it id
	 * should be in the FaMaConfig file.
	 * 
	 * @param id
	 *            the id of the transformation
	 * @return The transformation.
	 */
	public IVariabilityModelTransform createTransform(String id) {
		IVariabilityModelTransform res = null;
		Class<IVariabilityModelTransform> c = transformationsMap.get(id);

		if (c != null) {
			try {
				res = c.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return res;
	}

	/**
	 * Add a staged configuration to be aplied before ask.
	 * 
	 * @see this.ask().
	 * @param conf
	 *            The configuration to be added.
	 */
	public void addStagedConfiguration(Configuration conf) {
		this.configurations.offer(conf);
	}

	/**
	 * @return the variabilityModel that previously have been added to the
	 *         facade(Question Trader)
	 */
	public VariabilityModel getVariabilityModel() {
		return this.fm;
	}

	/**
	 * @return the reasoner that previously have been selected.
	 */
	public Reasoner getSelectedReasoner() {
		return selectedReasoner;
	}

	/**
	 * Set a Reasoner as mandatories, then when we ask a question, this will be
	 * the reasoner used.
	 * 
	 * @param selectedReasoner
	 *            The reasoner that we want
	 */
	public void setSelectedReasoner(String selectedReasoner) {
		this.selectedReasoner = reasonersIdMap.get(selectedReasoner);
	}
	public void removeStagedConfigurations(){
		configurations = new LinkedList<Configuration>();
	}}
