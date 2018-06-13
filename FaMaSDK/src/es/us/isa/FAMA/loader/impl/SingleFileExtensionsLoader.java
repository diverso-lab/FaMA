/**
 * 	This file is part of FaMaTS.
 *
 *     FaMaTS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FaMaTS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.us.isa.FAMA.loader.impl;

import java.util.Collection;
import java.util.Map;

import es.us.isa.FAMA.Reasoner.CriteriaSelector;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.Factory.QuestionAbstractFactory;
import es.us.isa.FAMA.loader.ExtensionsLoader;
import es.us.isa.FAMA.models.variabilityModel.parsers.ModelParser;
import es.us.isa.FAMA.models.variabilityModel.parsers.ModelParserImpl;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;


public class SingleFileExtensionsLoader implements ExtensionsLoader{

	//desde los metamodelos
	protected ModelParserImpl mp;
	//desde los razonadores
	protected Map<Reasoner,QuestionAbstractFactory> reasonersMap;
	//desde los razonadores
	protected Map<String,Reasoner> reasonersIdMap;
	//desde los razonadores
	protected Map<String,Class<Question>> questionsMap;
	//interno
	protected CriteriaSelector selector;
	protected Map<String, CriteriaSelector> selectorsMap;
	//interno
	protected Map<String, Class<IVariabilityModelTransform>> transformationsMap;
	
	public SingleFileExtensionsLoader(){
		loadExtensions();
	}
	
	private void loadExtensions() {
		// TODO este metodo parseara
		//los archivos de configuracion que existan para
		//cargar mediante reflexion preguntas, razonadores
		//etc...
		//hacerlo de igual forma que con JarJarLoader,
		//pero sin tener que cargar los jars externos :)
	}

	@Override
	public ModelParser getModelParser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Class<Question>> getQuestionsMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Reasoner> getReasoners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Reasoner> getReasonersIdMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, CriteriaSelector> getSelectorsMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Class<IVariabilityModelTransform>> getTransformationsMap() {
		// TODO Auto-generated method stub
		return null;
	}

}
