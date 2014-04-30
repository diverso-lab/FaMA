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
package es.us.isa.JaCoPReasoner.attributed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import JaCoP.constraints.And;
import JaCoP.constraints.Constraint;
import JaCoP.constraints.Eq;
import JaCoP.constraints.IfThen;
import JaCoP.constraints.IfThenElse;
import JaCoP.constraints.In;
import JaCoP.constraints.Not;
import JaCoP.constraints.Or;
import JaCoP.constraints.PrimitiveConstraint;
import JaCoP.constraints.Sum;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XeqY;
import JaCoP.constraints.XexpYeqZ;
import JaCoP.constraints.XgtC;
import JaCoP.constraints.XgtY;
import JaCoP.constraints.XgteqY;
import JaCoP.constraints.XltY;
import JaCoP.constraints.XlteqY;
import JaCoP.constraints.XmulCeqZ;
import JaCoP.constraints.XmulYeqZ;
import JaCoP.constraints.XneqY;
import JaCoP.constraints.XplusYeqZ;
import JaCoP.core.BoundDomain;
import JaCoP.core.Domain;
import JaCoP.core.FDV;
import JaCoP.core.FDstore;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Variable;
import JaCoP.search.ComparatorVariable;
import JaCoP.search.LargestDomain;
import JaCoP.search.LargestMin;
import JaCoP.search.MaxRegret;
import JaCoP.search.MinDomainOverDegree;
import JaCoP.search.MostConstrainedDynamic;
import JaCoP.search.MostConstrainedStatic;
import JaCoP.search.SmallestDomain;
import JaCoP.search.SmallestMax;
import JaCoP.search.SmallestMin;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.AttributedFeatureModelReasoner;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.models.domain.IntegerDomain;
import es.us.isa.FAMA.models.domain.ObjectDomain;
import es.us.isa.FAMA.models.domain.Range;
import es.us.isa.FAMA.models.domain.RangeIntegerDomain;
import es.us.isa.FAMA.models.domain.SetIntegerDomain;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.featureModel.KeyWords;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.util.Node;
import es.us.isa.util.Tree;

public class JaCoPReasoner extends AttributedFeatureModelReasoner {
	
	protected Map<String, GenericFeature> features;
	protected GenericFeature root;
	protected FDstore store;
	protected Map<String, FDV> variables;
	protected ArrayList<Constraint> constraints;
	protected Map<String, Constraint> relationConstraintMap;
	protected boolean consistent;
	protected ComparatorVariable heuristics;
	protected static Map<String, ComparatorVariable> heuristicsMap;
	protected Stack<Configuration> configStack;
	protected Map<String, FDV> attVars;
	protected JaCoPParser jParser;
	
	public JaCoPReasoner() {
		reset();
		heuristicsMap = new HashMap<String, ComparatorVariable>();
		heuristicsMap.put("MCD", new MostConstrainedDynamic());
		heuristicsMap.put("MCS", new MostConstrainedStatic());
		heuristicsMap.put("LD", new LargestDomain());
		heuristicsMap.put("LM", new LargestMin());
		heuristicsMap.put("MR", new MaxRegret());
		heuristicsMap.put("MDOD", new MinDomainOverDegree());
		heuristicsMap.put("SDOM", new SmallestDomain());
		heuristicsMap.put("SMAX", new SmallestMax());
		heuristicsMap.put("SMIN", new SmallestMin());
	}

	@Override
	public void reset() {
		store = new FDstore();
		variables = new HashMap<String, FDV>();
		constraints = new ArrayList<Constraint>();
		relationConstraintMap = new HashMap<String, Constraint>();
		consistent = true;
		root = null;
		features = new HashMap<String, GenericFeature>();
		configStack = new Stack<Configuration>();
		attVars = new HashMap<String, FDV>();
		jParser = new JaCoPParser();
	}

	@Override
	protected void addCardinality_(GenericRelation rel,
			GenericAttributedFeature child, GenericAttributedFeature parent,
			Iterator<Cardinality> cardinalities) {
		
		PrimitiveConstraint cons = createCardinality(rel, child, parent, cardinalities);
		store.impose(cons);

	}
	
	
	
	

	@Override
	protected void addExcludes_(GenericRelation rel,
			GenericAttributedFeature origin,
			GenericAttributedFeature destination) {
		
		PrimitiveConstraint cons = createExcludes(rel, origin, destination);
		store.impose(cons);

	}

	@Override
	protected void addFeature_(GenericAttributedFeature f,
			Collection<Cardinality> cards) {

		Iterator<Cardinality> cardIt = cards.iterator();
		FDV featVar = new FDV(store, f.getName(), 0, 1);
		while (cardIt.hasNext()) {
			Cardinality card = cardIt.next();
			featVar.addDom(card.getMin(), card.getMax());
		}
		features.put(f.getName(), f);
		variables.put(f.getName(), featVar);
		
		//ahora los atributos
		Collection<? extends GenericAttribute> atts = f.getAttributes();
		Iterator<? extends GenericAttribute> itAtts = atts.iterator();
		while (itAtts.hasNext()){
			GenericAttribute att = itAtts.next();
			String attName = f.getName() + "." + att.getName();
			FDV attVar = new FDV(store, attName);
			//le incluimos ahora el dominio, segun el tipo de atributo
			es.us.isa.FAMA.models.domain.Domain d = att.getDomain();
			Integer intNullValue = 0;
			JaCoP.core.Domain attDomain = new BoundDomain();
			if (d instanceof IntegerDomain){
				intNullValue = (Integer)att.getNullValue();
				attVar.addDom(intNullValue, intNullValue);
				if (d instanceof RangeIntegerDomain){
					RangeIntegerDomain rangeDom = (RangeIntegerDomain)d;
					Iterator<Range> itRanges = rangeDom.getRanges().iterator();
					while (itRanges.hasNext()){
						Range r = itRanges.next();
						attDomain.addDom(r.getMin(), r.getMax());
						//attVar.addDom(r.getMin(), r.getMax());
					}
					
				}
				else if (d instanceof SetIntegerDomain){
//					SetIntegerDomain setDom = (SetIntegerDomain)d;
					Iterator<Integer> itValues = d.getAllIntegerValues().iterator();
					while (itValues.hasNext()){
						Integer elem = itValues.next();
						//FIXME un poco cutre, intentar mejorarlo
						attDomain.addDom(elem, elem);
//						attVar.addDom(elem, elem);
					}
				}
				else{
					throw new FAMAException("Unknown domain type for "+attName+" attribute");
				}
				
			}
			else if (d instanceof ObjectDomain){
				Object nullValue = att.getNullValue();
				intNullValue = att.getIntegerValue(nullValue);
				attVar.addDom(intNullValue, intNullValue);
//				ObjectDomain objDom = (ObjectDomain) d;
				Iterator<Integer> itValues = d.getAllIntegerValues().iterator();
				while (itValues.hasNext()){
					Integer elem = itValues.next();
					//FIXME un poco cutre, intentar mejorarlo
					attDomain.addDom(elem,elem);
//					attVar.addDom(elem, elem);
				}
			}
			else{
				throw new FAMAException("Unknown domain type for "+attName+" attribute");
			}
			
			attVar.addDom(attDomain);
			this.attVars.put(attName, attVar);
			Constraint domainConstraint = new IfThenElse(new XgtC(featVar,0),new In(attVar,attDomain),new XeqC(attVar,intNullValue));
			store.impose(domainConstraint);
			
			Iterator<? extends es.us.isa.FAMA.models.featureModel.Constraint> itInv = 
					f.getInvariants().iterator();
			while (itInv.hasNext()) {
				es.us.isa.FAMA.models.featureModel.Constraint inv = itInv.next();
				Constraint c = jParser.translateToInvariant(inv.getAST(),f.getName());
				store.impose(c);
			}
			
		}

	}
	
//	private void addAttributes(GenericAttributedFeature f) {
//		
//	}

	@Override
	protected void addMandatory_(GenericRelation rel,
			GenericAttributedFeature child, GenericAttributedFeature parent) {
		
		PrimitiveConstraint cons = createMandatory(rel, child, parent);
		store.impose(cons);

	}

	@Override
	protected void addOptional_(GenericRelation rel,
			GenericAttributedFeature child, GenericAttributedFeature parent) {
		
		PrimitiveConstraint cons = createOptional(rel, child, parent);
		store.impose(cons);

	}

	@Override
	protected void addRequires_(GenericRelation rel,
			GenericAttributedFeature origin,
			GenericAttributedFeature destination) {
		
		PrimitiveConstraint cons = createRequires(rel, origin, destination);
		store.impose(cons);

	}

	@Override
	protected void addRoot_(GenericAttributedFeature feature) {
		
		Variable root = variables.get(feature.getName());
		PrimitiveConstraint cons = createRoot(root);
		constraints.add(cons);
		store.impose(cons);

	}

	@Override
	protected void addSet_(GenericRelation rel,
			GenericAttributedFeature parent,
			Collection<GenericAttributedFeature> children,
			Collection<Cardinality> cardinalities) {

		PrimitiveConstraint cons = createSet(rel, parent, children, cardinalities);
		store.impose(cons);
		
	}
	
	protected PrimitiveConstraint createRoot(Variable root) {
		PrimitiveConstraint constraint = new XeqC(root, 1);
		return constraint;
	}

	protected FDV createFeature(GenericFeature f,
			Collection<Cardinality> cards, FDstore st) {
		Iterator<Cardinality> cardIt = cards.iterator();
		FDV var = new FDV(st, f.getName(), 0, 1);
		while (cardIt.hasNext()) {
			Cardinality card = cardIt.next();
			var.addDom(card.getMin(), card.getMax());
		}
		return var;
	}

	protected PrimitiveConstraint createMandatory(GenericRelation rel,
			GenericFeature child, GenericFeature parent) {
		Variable childVar = variables.get(child.getName());
		Variable parentVar = variables.get(parent.getName());
		// esta forma de representar el mandatory, ¿es correcta?
		// PrimitiveConstraint constraint = new XeqY(childVar,parentVar);
		// esta forma es mas correcta
		PrimitiveConstraint constraint = new Eq(new XeqC(parentVar, 1),
				new XeqC(childVar, 1));
		saveConstraint(rel, constraint);
		return constraint;
	}

	protected PrimitiveConstraint createOptional(GenericRelation rel,
			GenericFeature child, GenericFeature parent) {
		
		Variable childVar = variables.get(child.getName());
		Variable parentVar = variables.get(parent.getName());
		PrimitiveConstraint constraint = new IfThen(new XeqC(parentVar, 0),
				new XeqC(childVar, 0));
		saveConstraint(rel, constraint);
		return constraint;
		
	}

	protected PrimitiveConstraint createSet(GenericRelation rel,
				GenericFeature parent, Collection<GenericAttributedFeature> children,
				Collection<Cardinality> cardinalities) {
			
			String setVarName = rel.getName();
			FDV parentVar = variables.get(parent.getName());
			// create the sum variable
			FDV setVar = new FDV(store, setVarName);
			setVar.addDom(0, children.size());
			Domain domain = new IntervalDomain();
			Iterator<Cardinality> itc = cardinalities.iterator();
			while (itc.hasNext()) {
				Cardinality card = itc.next();
				domain.addDom(card.getMin(), card.getMax());
			}
			// guardamos como una variable mas la suma de los hijos
			variables.put(rel.getName(), setVar);
	
			// create a set of variables to sum them
			ArrayList<Variable> varsList = new ArrayList<Variable>();
			Iterator<GenericAttributedFeature> it = children.iterator();
			while (it.hasNext()) {
				varsList.add(variables.get(it.next().getName()));
			}
	
			// creates the sum constraint with the cardinality variable
			Constraint suma = new Sum(varsList, setVar);
			// it is not a primitive constraint and cannot be reified
			constraints.add(suma);
			store.impose(suma);
	
			// creates the arraylist of constraints for the And constraint
			ArrayList<PrimitiveConstraint> localConstraints = new ArrayList<PrimitiveConstraint>();
			it = children.iterator();
			while (it.hasNext()) {
				Variable childVar = variables.get(it.next().getName());
				localConstraints.add(new XeqC(childVar, 0));
			}
	
			//done 4 David Benavides
	//		PrimitiveConstraint constraintG = new IfThen(new XeqC(setVar, 0),
	//				new XeqC(parentVar, 0));
	//		constraints.add(constraintG);
	//		store.impose(constraintG);
			// relationConstraintMap.put(constraintG,rel);
	
			// adding the second constraint to the store
			PrimitiveConstraint constraint = new IfThenElse(new XgtC(parentVar, 0),
					new In(setVar, domain), new And(localConstraints));
			saveConstraint(rel, constraint);
			return constraint;
			
		}

	protected PrimitiveConstraint createCardinality(GenericRelation rel,
				GenericFeature child, GenericFeature parent,
				Iterator<Cardinality> cardinalities) {
	
			Variable childVar = variables.get(child.getName());
			Variable parentVar = variables.get(parent.getName());
	
			Domain domain = new IntervalDomain();
			if (!cardinalities.hasNext())
				domain.addDom(0, 0);
			while (cardinalities.hasNext()) {
				Cardinality card = cardinalities.next();
				domain.addDom(card.getMin(), card.getMax());
			}
	
			//constraint done 4 David Benavides
	//		PrimitiveConstraint constraintG = new IfThen(new XeqC(childVar, 0),
	//				new XeqC(parentVar, 0));
	//		constraints.add(constraintG);
	//		// relationConstraintMap.put(constraintG,rel);
	//		store.impose(constraintG);
	
			PrimitiveConstraint constraint = new IfThenElse(new XeqC(parentVar, 0),
					new XeqC(childVar, 0), new In(childVar, domain));
			saveConstraint(rel, constraint);
			return constraint;
		}

	protected PrimitiveConstraint createExcludes(GenericRelation rel,
			GenericFeature origin, GenericFeature destination) {
	
		Variable originVar = variables.get(origin.getName());
		Variable destVar = variables.get(destination.getName());
		PrimitiveConstraint constraint = new IfThen(new XgtC(originVar, 0),
				new XeqC(destVar, 0));
		saveConstraint(rel, constraint);
		return constraint;
	
	}

	protected PrimitiveConstraint createRequires(GenericRelation rel,
			GenericFeature origin, GenericFeature destination) {

		Variable originVar = variables.get(origin.getName());
		Variable destVar = variables.get(destination.getName());
		PrimitiveConstraint constraint = new IfThen(new XgtC(originVar, 0),
				new XgtC(destVar, 0));
		saveConstraint(rel, constraint);
		return constraint;

	}
	
	protected void saveConstraint(GenericRelation rel,
			PrimitiveConstraint constraint) {
		
		constraints.add(constraint);
		relationConstraintMap.put(rel.getName(), constraint);
		
	}
	
	public ArrayList<FDV> getVariables() {
		ArrayList<FDV> res = new ArrayList<FDV>();
		Iterator<Entry<String, FDV>> it = variables.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, FDV> elem = it.next();
			res.add(elem.getValue());
		}
		return res;
	}
	
	public boolean consistency() {
		if (consistent)
			consistent = store.consistency();
		return consistent;
	}
	
	public FDstore getStore() {
		return store;
	}

	public GenericFeature searchFeatureByName(String id) {
		return features.get(id);
	}

	public GenericFeature getRoot() {
		return root;
	}

	public Collection<GenericFeature> getAllFeatures() {
		return this.features.values();
	}

	public int getStoreLevel() {
		return store.level;
	}


	@Override
	public void applyStagedConfiguration(Configuration conf) {
		
		if (!conf.getElements().isEmpty()) {
			configStack.push(conf);
			FDstore store = this.getStore();
			int storeLevel = store.level;
			// and apply consistency to make the changes available to the next level
			consistent = this.consistency();
			if (consistent) {
				store.setLevel(storeLevel + 1);

				// now we impose the new constraints
				Iterator<Entry<VariabilityElement, Integer>> it = conf
						.getElements().entrySet().iterator();
				ArrayList<FDV> vars = this.getVariables();
				while (it.hasNext()) {
					boolean varFound = false;
					Entry<VariabilityElement, Integer> e = it.next();
					VariabilityElement f = e.getKey();
					if (f instanceof GenericAttribute){
						GenericAttribute att = (GenericAttribute) f;
						String attName = att.getFeature().getName()+"."+att.getName();
						FDV var = attVars.get(attName);
						if (var != null){
							store.impose(new XeqC(var, e.getValue().intValue()));
							varFound = true;
						}
					}
					else{
						Iterator<FDV> it2 = vars.iterator();
						FDV v;
						while (it2.hasNext() && !varFound) {
							v = it2.next();
							if (v.id().equalsIgnoreCase(f.getName())) {
								store.impose(new XeqC(v, e.getValue().intValue()));
								varFound = true;
							}
						}
					}
					
					if (!varFound && e.getValue().intValue() == 0) {
						// la variable no esta en el modelo y no se quiere
						// aï¿½adir
						System.err.println("The feature " + f.getName()
								+ " do not exist on the model");
					}
					if (!varFound && e.getValue().intValue() == 1) {
						// la variable no esta en el modelo y se quiere aï¿½adir
						System.err
								.println("The feature "
										+ f.getName()
										+ " do not exist on the model, and can not be added");
						Variable a = new Variable(store, f.getName(), 0, 0);
						store.impose(new XeqC(a, 1));

					}
				}
			}
		}

	}

	@Override
	public PerformanceResult ask(Question q) {
		
		PerformanceResult res;
		JaCoPQuestion jq = (JaCoPQuestion)q;
		if (heuristics != null)
			jq.setHeuristics(heuristics);
		jq.preAnswer(this);
		res = jq.answer(this);
		jq.postAnswer(this);
		consistent = true;
		return res;
		
	}

	@Override
	public void unapplyStagedConfigurations() {
		
		if (consistent && !configStack.empty()) {
			//int size = configStack.size();
			int level = store.level;
			store.removeLevel(store.level);
			store.setLevel(level - 1);
			configStack.pop();
		}
		
	}
	
	@Override
	public void addConstraint(es.us.isa.FAMA.models.featureModel.Constraint c) {
		Constraint rel = createConstraint(c);
		store.impose(rel);
	}
	
	public JaCoP.constraints.Constraint createConstraint(es.us.isa.FAMA.models.featureModel.Constraint c){
		JaCoP.constraints.Constraint relation = jParser.translateToConstraint(c.getAST());
		constraints.add(relation);
		return relation;
	}
	
	protected class JaCoPParser{
		
		private String featName;
		
		private int cont;
		
		public JaCoPParser(){
			cont = 0;
			featName = null;
		}
		
		public Constraint translateToConstraint(Tree<String> ast){
			cont = 0;
			Constraint res = null;
			Node<String> n = ast.getRootElement();
			res = translateLogical(n);
			return res;
		}
		
		public Constraint translateToInvariant(Tree<String> ast, String featInvariant){
			featName = featInvariant;
			cont = 0;
			Constraint res = null;
			Node<String> n = ast.getRootElement();
			res = translateLogical(n);
			return res;
		}
		
		private PrimitiveConstraint translateLogical(Node<String> tree){
			//constraints logicas:
			//AND, OR, NOT, IMPLIES, IFF, REQUIRES, EXCLUDES
			//LOGICO -> LOGICO
			PrimitiveConstraint res = null;
			String data = tree.getData();
			List<Node<String>> children = tree.getChildren();
			int n = children.size();
			if (n == 2){
				if (data.equals(KeyWords.AND)){
					PrimitiveConstraint e1 = translateLogical(children.get(0));
					PrimitiveConstraint e2 = translateLogical(children.get(1));
					PrimitiveConstraint[] cSet = {e1,e2};
					res = new And(cSet);
				}
				else if (data.equals(KeyWords.OR)){
					PrimitiveConstraint e1 = translateLogical(children.get(0));
					PrimitiveConstraint e2 = translateLogical(children.get(1));
					PrimitiveConstraint[] cSet = {e1,e2};
					res = new Or(cSet);
				}
				else if (data.equals(KeyWords.IMPLIES) || data.equals(KeyWords.REQUIRES)){
					PrimitiveConstraint e1 = translateLogical(children.get(0));
					PrimitiveConstraint e2 = translateLogical(children.get(1));
					res = new IfThen(e1,e2);
				}
				else if (data.equals(KeyWords.IFF)){
					PrimitiveConstraint e1 = translateLogical(children.get(0));
					PrimitiveConstraint e2 = translateLogical(children.get(1));
					res = new Eq(e1,e2);
				}
				else if (data.equals(KeyWords.EXCLUDES)){
					//tendremos una feature > 0 a cada lado,
					//asi que hacemos un implies negando la parte dcha
					// (feat > 0) implies (not (feat > 0))
					PrimitiveConstraint e1 = translateLogical(children.get(0));
					PrimitiveConstraint aux = translateLogical(children.get(1));
					PrimitiveConstraint e2 = new Not(aux);
					res = new IfThen(e1,e2);
				}
				else{
					res = translateRelational(tree);
				}
			}
			else if (n == 1){
				if (data.equals(KeyWords.NOT)){
					PrimitiveConstraint e1 = translateLogical(children.get(0));
					res = new Not(e1);
				}
			}
			else{
				if (isFeature(tree)){
					FDV feat = variables.get(data);
					res = new XgtC(feat,0);
				}
			}
			return res;
		}
		
		private PrimitiveConstraint translateRelational(Node<String> tree){
			//TODO mejorar este metodo, de tal manera que para constantes
			//enteras no se creen variables artificiales, sino que se usen
			//las constantes enteras
			//constraints relaciones:
			//>, >=, <, <=, ==, !=
			//ENTERO -> LOGICO
			PrimitiveConstraint res = null;
			String data = tree.getData();
			List<Node<String>> children = tree.getChildren();
			FDV e1 = translateInteger(children.get(0));
			FDV e2 = translateInteger(children.get(1));
			if (data.equals(KeyWords.GREATER)){
				res = new XgtY(e1,e2);
			}
			else if (data.equals(KeyWords.GREATER_EQUAL)){
				res = new XgteqY(e1,e2);
			}
			else if (data.equals(KeyWords.LESS)){
				res = new XltY(e1,e2);
			}
			else if (data.equals(KeyWords.LESS_EQUAL)){
				res = new XlteqY(e1,e2);
			}
			else if (data.equals(KeyWords.EQUAL)){
				res = new XeqY(e1,e2);
			}
			else if (data.equals(KeyWords.NON_EQUAL)){
				res = new XneqY(e1,e2);
			}
			return res;
		}
		
		private FDV translateInteger(Node<String> tree){
			//TODO
			//constraints enteras:
			//ENTERO -> ENTERO
			FDV res = null;
			String data = tree.getData();
			List<Node<String>> children = tree.getChildren();
			if (data.equals(KeyWords.PLUS)){
				FDV e1 = translateInteger(children.get(0));
				FDV e2 = translateInteger(children.get(1));
				res = createAuxVar();
				PrimitiveConstraint c = new XplusYeqZ(e1,e2,res);
				store.impose(c);
			}
			else if (data.equals(KeyWords.MINUS)){
				//TODO lanzar excepcion, operacion no permitida
			}
			else if (data.equals(KeyWords.MULT)){
				FDV e1 = translateInteger(children.get(0));
				FDV e2 = translateInteger(children.get(1));
				res = createAuxVar();
				Constraint c = new XmulYeqZ(e1,e2,res);
				store.impose(c);
				//res = mult(e1,e2);
			}
			else if (data.equals(KeyWords.DIV)){
				//TODO lanzar excepcion, operacion no permitida
			}
			else if (data.equals(KeyWords.MOD)){
				//TODO lanzar excepcion, operacion no permitida
			}
			else if (data.equals(KeyWords.POW)){
				FDV e1 = translateInteger(children.get(0));
				FDV e2 = translateInteger(children.get(1));
				res = createAuxVar();
				Constraint c = new XexpYeqZ(e1, e2, res);
				store.impose(c);
//				res = power(e1,e2);
			}
			else if (data.equals(KeyWords.UNARY_MINUS)){
				//TODO lanzar excepcion, operacion no permitida

			}
			else if (isConstant(tree)){
				// TODO por ahora, solo permitiremos constraints
				// con constantes enteras
				int value = Integer.parseInt(data);
				res = createAuxConstantVar(value);
			}
			else if (isAttribute(tree)){
				String attName = getAttributeName(tree);
				res = attVars.get(attName);
			}
			return res;
		}

		private FDV createAuxVar() {
			//FIXME cuidado con ese dominio!!
			FDV res;
			res = new FDV(store, "@aux"+cont,Integer.MIN_VALUE,Integer.MAX_VALUE);
			cont++;
			return res;
		}
		
		private FDV createAuxConstantVar(int n){
			FDV res;
			res = new FDV(store, "@aux"+cont, n, n);
			cont++;
			return res;
		}
		
		//private
		
		private String getAttributeName(Node<String> n) {
			String res = null;
			if (featName == null){
				String s = n.getData();
				boolean b = s.equals(KeyWords.ATTRIBUTE);
				if (b && (n.getNumberOfChildren() == 2)) {
					List<Node<String>> list = n.getChildren();
					res = list.get(0).getData() + "." + list.get(1).getData();
				}
			}
			else{
				res = featName+"."+n.getData();
			}
			
			return res;
		}

		private boolean isAttribute(Node<String> n) {
			if (featName == null){
				return n.getData().equals(KeyWords.ATTRIBUTE);
			}
			else{
				String aux = featName+"."+n.getData();
				Object res = attVars.get(aux);
				return (res != null);
			}
			
		}

		private boolean isFeature(Node<String> n) {
			String s = n.getData();
			return (features.get(s) != null);
		}

		private boolean isConstant(Node<String> n) {
			// TODO por ahora, solo permitiremos constraints con constantes enteras
			String s = n.getData();
			try {
				Integer.parseInt(s);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		
	}

	@Override
	public Map<String, Object> getHeusistics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHeuristic(Object obj) {
		// TODO Auto-generated method stub
		
	}

}
