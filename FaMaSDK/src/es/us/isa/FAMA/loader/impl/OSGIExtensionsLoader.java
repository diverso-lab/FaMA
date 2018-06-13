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
package es.us.isa.FAMA.loader.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import es.us.isa.FAMA.Reasoner.CriteriaSelector;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.loader.ExtensionsLoader;
import es.us.isa.FAMA.models.variabilityModel.parsers.ModelParser;
import es.us.isa.FAMA.models.variabilityModel.parsers.ModelParserImpl;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;
/**
 * This class is the extensions loader that FaMa uses when running 
 * not in to an OSGi environment
 */
public class OSGIExtensionsLoader implements ExtensionsLoader {

	private  Map<String, Class<Question>> questionClasses;

	private  Map<String, Reasoner> reasonersIdMap;

	private  Map<String, CriteriaSelector> selectors;

	private  Map<String, Class<IVariabilityModelTransform>> transformations;

	private  ModelParserImpl mp;

	private Collection<Reasoner> reasoners;
	
	private static OSGIExtensionsLoader instance;
	
	
	
	private OSGIExtensionsLoader() {
		questionClasses = new HashMap<String, Class<Question>>();
		reasonersIdMap = new HashMap<String, Reasoner>();
		selectors = new HashMap<String, CriteriaSelector>();
		transformations = new HashMap<String, Class<IVariabilityModelTransform>>();
		mp = new ModelParserImpl();
		reasoners = new LinkedList<Reasoner>();
	}

	public static synchronized OSGIExtensionsLoader getInstance(){
		if (instance == null){
			instance = new OSGIExtensionsLoader();
		}
		
		return instance;
		
	}
	
	public Map<String, Class<Question>> getQuestionsMap() {
		//interfaces de las preguntas que ofrecemos
		//deben ser registradas en FaMaQuestions
		
		//clase padre -> Question
//		return Activator.getQuestionClasses();
		return questionClasses;
	}

	public Collection<Reasoner> getReasoners() {
		//razonadores (con sus preguntas dentro)
		//deben ser registrados cada razonador concreto
		
		//clase padre -> Reasoner
//		return Activator.getReasoners();
		return reasoners;
	}

	public Map<String, Reasoner> getReasonersIdMap() {
		//al consumir los razonadores, pillamos el atributo
		//que nos diga su id
		
		//clase padre -> Reasoner (se carga junto al anterior)
//		return Activator.getReasonersIdMap();
		return reasonersIdMap;
	}

	public Map<String, CriteriaSelector> getSelectorsMap() {
		//cargamos todos los que haya disponebles, y pillamos
		//el atributo con su id
		
		//clase padre -> CriteriaSelector
//		return Activator.getSelectors();
		return selectors;
	}

	public Map<String, Class<IVariabilityModelTransform>> getTransformationsMap() {
		//cargamos todos los que haya disponebles, y pillamos
		//el atributo con su id
		
		//clase padre -> IVariabilityModelTransform
//		return Activator.getTransformations();
		return transformations;
	}

	public ModelParser getModelParser() {
		//cargamos todos los que haya disponebles, y pillamos
		//el atributo con su id
		
		//claseS padreS -> IReader e IWriter
//		return Activator.getModelParser();
		return mp;
	}

	public void addQuestionClass(String classId, Class<Question> cq){
		questionClasses.put(classId, cq);
	}
	
	public void addReasoner(String id, Reasoner r){
		reasoners.add(r);
		reasonersIdMap.put(id, r);
	}
	
	public void addCriteriaSelector(String id, CriteriaSelector s){
		selectors.put(id, s);
	}
	
	public void addTransformation(String id,Class<IVariabilityModelTransform> ct ){
		transformations.put(id, ct);
	}

}
