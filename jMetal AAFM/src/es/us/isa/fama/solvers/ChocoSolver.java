package es.us.isa.fama.solvers;

import static choco.Choco.and;
import static choco.Choco.constant;
import static choco.Choco.div;
import static choco.Choco.eq;
import static choco.Choco.geq;
import static choco.Choco.gt;
import static choco.Choco.ifOnlyIf;
import static choco.Choco.ifThenElse;
import static choco.Choco.implies;
import static choco.Choco.leq;
import static choco.Choco.lt;
import static choco.Choco.makeIntVar;
import static choco.Choco.minus;
import static choco.Choco.mod;
import static choco.Choco.mult;
import static choco.Choco.neg;
import static choco.Choco.neq;
import static choco.Choco.not;
import static choco.Choco.or;
import static choco.Choco.plus;
import static choco.Choco.power;
import static choco.Choco.sum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;
import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomain;
import choco.kernel.solver.variables.integer.IntDomainVar;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.Relation;
import es.us.isa.FAMA.models.domain.IntegerDomain;
import es.us.isa.FAMA.models.domain.ObjectDomain;
import es.us.isa.FAMA.models.domain.Range;
import es.us.isa.FAMA.models.domain.RangeIntegerDomain;
import es.us.isa.FAMA.models.domain.SetIntegerDomain;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.KeyWords;
import es.us.isa.FAMA.models.featureModel.extended.ConstantIntConverter;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.models.featureModel.extended.StringDomainIntConverter;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.fama.operations.AAFMProblem;
import es.us.isa.util.Node;
import es.us.isa.util.Tree;
import es.us.isa.utils.FMUtils;

public class ChocoSolver extends Solver {

	private int worldState;
	
	//fm elements
	private Map<String, IntegerVariable> featuresMap;
	private Map<String, IntegerVariable> attsMap;
	private List<Constraint> constraints;
	private List<Constraint> reifyConstraints;
	private ChocoParser chocoParser;

	//choco elements
	private Model model;
	private CPSolver solver;

	//jmetal checking elements
	private IntegerVariable[] features;
	private IntegerVariable[] attributes;
	// one for every constraint, to check how many constraints are violated
	private IntegerVariable[] reifyVars;

	public ChocoSolver() {
		super();
		
	}
	
	protected void reset(){
		model = new CPModel();
		featuresMap = new HashMap<String, IntegerVariable>();
		attsMap = new HashMap<String, IntegerVariable>();
		chocoParser = new ChocoParser();
		constraints = new ArrayList<Constraint>();
		reifyConstraints = new ArrayList<Constraint>();
	}
	
	private void newSolution2Choco(Solution s){
		
//		Constraint[] conf = new Constraint[features.length + attributes.length];

		Variable[] aux = s.getDecisionVariables();
		ArrayInt jMetalVars = (ArrayInt) aux[0];
		
//		Binary feats = (Binary) jMetalVars[0];
//		ArrayInt atts = (ArrayInt) jMetalVars[1];
		int numberOfFeatures = features.length;
		for (int i = 0; i < numberOfFeatures; i++) {
			// XXX BE CAREFUL! WHICH ONE IS THE FIRST AND WHICH ONE THE
			// LAST?????
			// IF THERE IS ANY STRANGE ERROR, COME HERE
			int val = 0;
			try {
				val = jMetalVars.getValue(i);
			} catch (JMException e) {
				//XXX be careful here
				e.printStackTrace();
			}
			IntDomainVar solverVar = solver.getVar(features[i]);
			try {
				solverVar.setVal(val);
			} catch (ContradictionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			conf[i] = Choco.eq(features[i], val);
		}// for

		// int max = features.length + attributes.length;
		int numberOfAtts = attributes.length;
		int totalSize = numberOfFeatures + numberOfAtts;
		for (int i = numberOfFeatures; i < totalSize; i++) {
			try {
				int val = jMetalVars.getValue(i);
				IntDomainVar solverVar = solver.getVar(attributes[i-numberOfFeatures]);
				solverVar.setVal(val);
//				conf[i] = Choco.eq(attributes[i-numberOfFeatures], val);
			} catch (JMException e) {
				e.printStackTrace();
			} catch (ContradictionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// for

	}
	
	@Override
	public void checkSolution(Solution s) {
		//TODO esto es optimizable. tengo que encontrar la forma de que el unico cambio
		//entre chequeos de solucion sea a–adir la restriccion de la configuracion actual
//		solver.getEnvironment().worldPush();
		worldState = solver.getEnvironment().getWorldIndex();
		solver.getEnvironment().worldPush();
//		solver.wor
		
		newSolution2Choco(s);
//		Constraint sol = solution2Choco(s);
		
		//TODO 1.add the constraint to the solver using solver.postCut(SConstraint c)
//		SConstraint ssol = solver.makeSConstraint(sol);
//		solver.postCut(ssol);
		
//		model.addConstraint(sol);
//		solver.read(model);
//		solver.postCut(cc);
//		solver.addConstraint(sol);
//		try {
//			// XXX using propagation should be enough
//			solver.propagate();
//			solver.solve();
//		} catch (ContradictionException e) {
//			e.printStackTrace();
//		}
		try {
			solver.propagate();
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		solver.solve();
		//and after this, I have to check how many constraints have
		//been violated
		int counter = 0;
		for (int i = 0; i < reifyVars.length; i++){
			IntDomainVar solverVar = solver.getVar(reifyVars[i]);
			if (solverVar.getVal() == 0){
				counter++;
			}
		}
		this.violations = counter;
		
		solver.resetSearchStrategy();
		solver.worldPopUntil(worldState);
//		solver.worldPopUntil(worldState);
	}

	private Constraint solution2Choco(Solution s) {
		// TODO
		Constraint[] conf = new Constraint[features.length + attributes.length];

		Variable[] aux = s.getDecisionVariables();
		ArrayInt jMetalVars = (ArrayInt) aux[0];
		
//		Binary feats = (Binary) jMetalVars[0];
//		ArrayInt atts = (ArrayInt) jMetalVars[1];
		int numberOfFeatures = features.length;
		for (int i = 0; i < numberOfFeatures; i++) {
			// XXX BE CAREFUL! WHICH ONE IS THE FIRST AND WHICH ONE THE
			// LAST?????
			// IF THERE IS ANY STRANGE ERROR, COME HERE
			int val = 0;
			try {
				val = jMetalVars.getValue(i);
			} catch (JMException e) {
				//XXX be careful here
				e.printStackTrace();
			}
//			int val;
//			if (b) {
//				val = 1;
//			} else {
//				val = 0;
//			}
			conf[i] = Choco.eq(features[i], val);
		}// for

		// int max = features.length + attributes.length;
		int numberOfAtts = attributes.length;
		int totalSize = numberOfFeatures + numberOfAtts;
		for (int i = numberOfFeatures; i < totalSize; i++) {
			try {
				int val = jMetalVars.getValue(i);
				conf[i] = Choco.eq(attributes[i-numberOfFeatures], val);
			} catch (JMException e) {
				e.printStackTrace();
			}
		}// for

		Constraint result = Choco.and(conf);
		return result;
	}

	/**
	 * XXX
	 * Reifying variables, to determine how many constraints have been violated
	 */
	@Override
	protected void extraMapping() {
		reifyVars = new IntegerVariable[constraints.size()];
		reifyConstraints = new ArrayList<Constraint>();
		
//		CPSolver solver = new CPSolver();
		int i = 0;
		for (Constraint c : constraints) {
			reifyVars[i] = Choco.makeBooleanVar("reified" + i, Options.V_NO_DECISION);
			Constraint aux = Choco.ifOnlyIf(Choco.eq(reifyVars[i], 1), c);
			reifyConstraints.add(aux);
			model.addConstraint(aux);
			i++;
		}// for

//		solver.read(model);
	}

	@Override
	protected void addRoot(GenericFeature f) {
		Constraint rootConstraint = Choco.eq(this.featuresMap.get(f.getName()), 1);
		constraints.add(rootConstraint);
	}
	
	@Override
	protected void addSet(Relation rel, AttributedFeature parent,
			Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {
		Cardinality card = null;
		// This constraint should be as ifThenElse(A>0;sum(B,C) in
		// {n,m};B=0,C=0)
		// Save the parent to check the value
		IntegerVariable parentVar = featuresMap.get(parent.getName());

		// Save the cardninality if exist from the parameter cardinalities
		SortedSet<Integer> cardValues = new TreeSet<Integer>();
		Iterator<Cardinality> itc = cardinalities.iterator();
		while (itc.hasNext()) {
			card = itc.next();
			for (int i = card.getMin(); i <= card.getMax(); i++)
				cardValues.add(i);
		}
		int[] cardValuesArray = new int[cardValues.size()];
		Iterator<Integer> itcv = cardValues.iterator();
		int pos = 0;
		int min = Integer.MAX_VALUE, max = 0;
		while (itcv.hasNext()) {
			cardValuesArray[pos] = itcv.next();
			if (cardValuesArray[pos] < min){
				min = cardValuesArray[pos];
			}
			if (cardValuesArray[pos] > max){
				max = cardValuesArray[pos];
			}
			pos++;
		}

//		IntegerVariable cardinalityVar = makeIntVar(rel.getName() + "_card",
//				cardValuesArray, Options.V_NO_DECISION);// cp:no_decision
		// XXX I've commented the line below. this variable is supposed to be
		// added
		// when adding the constraint
		// problem.addVariable(cardinalityVar);
		// Save all children to have the posiblitily of sum them
		ArrayList<IntegerVariable> varsList = new ArrayList<IntegerVariable>();
		Iterator<? extends GenericFeature> it = children.iterator();

		while (it.hasNext()) {
			varsList.add(featuresMap.get(it.next().getName()));
		}

		// creates the sum constraint with the cardinality variable
		// If parent var is equal to 0 then he sum of children has to be 0
		IntegerVariable[] aux = {};
		aux = varsList.toArray(aux);

		// If parent is greater than 0, then apply the restriction
		// ifThenElse(A>0;sum(B,C) in {n,m};B=0,C=0)
//		Constraint setConstraint = ifThenElse(gt(parentVar, 0),
//				eq(sum(aux), cardinalityVar), eq(sum(aux), 0));
		Constraint setConstraint = ifThenElse(gt(parentVar, 0),
				Choco.and(Choco.geq(Choco.sum(aux), min),Choco.leq(Choco.sum(aux), max)), eq(sum(aux), 0));
		constraints.add(setConstraint);
	}

	@Override
	protected void addCardinality(Relation rel, AttributedFeature child,
			AttributedFeature parent, Iterator<Cardinality> cardinalities) {
		IntegerVariable childVar = featuresMap.get(child.getName());
		IntegerVariable parentVar = featuresMap.get(parent.getName());

		SortedSet<Integer> cardValues = new TreeSet<Integer>();
		Iterator<Cardinality> itc = cardinalities;
		while (itc.hasNext()) {
			Cardinality card = itc.next();
			for (int i = card.getMin(); i <= card.getMax(); i++)
				cardValues.add(i);
		}
		int[] cardValuesArray = new int[cardValues.size()];
		Iterator<Integer> itcv = cardValues.iterator();
		int pos = 0;
		while (itcv.hasNext()) {
			cardValuesArray[pos] = itcv.next();
			pos++;
		}
		IntegerVariable cardinalityVar = makeIntVar(rel.getName() + "_card",
				cardValuesArray, Options.V_NO_DECISION);
		Constraint cardConstraint = ifThenElse(gt(parentVar, 0),
				eq(childVar, cardinalityVar), eq(childVar, 0));
		constraints.add(cardConstraint);
	}

	@Override
	protected void addOptional(Relation rel, AttributedFeature child,
			AttributedFeature parent) {
		IntegerVariable childVar = featuresMap.get(child.getName());
		IntegerVariable parentVar = featuresMap.get(parent.getName());
		Constraint optionalConstraint = implies(eq(parentVar, 0),
				eq(childVar, 0));
		constraints.add(optionalConstraint);

	}

	@Override
	protected void addMandatory(Relation rel, AttributedFeature child,
			AttributedFeature parent) {
		IntegerVariable childVar = featuresMap.get(child.getName());
		IntegerVariable parentVar = featuresMap.get(parent.getName());
		Constraint mandatoryConstraint = ifOnlyIf(eq(parentVar, 1),
				eq(childVar, 1));
		constraints.add(mandatoryConstraint);

	}

	/**
	 * Method to generate features and attributes variables
	 */
	@Override
	protected void generateVariables(
			Collection<? extends GenericFeature> features) {
		List<IntegerVariable> aux = new ArrayList<IntegerVariable>();
		List<IntegerVariable> auxAtts = new ArrayList<IntegerVariable>();
		for (GenericFeature f : features) {
//			IntegerVariable v = Choco.makeBooleanVar(f.getName(), Options.V_NO_DECISION);
			IntegerVariable v = Choco.makeBooleanVar(f.getName());
			featuresMap.put(f.getName(), v);
			aux.add(v);
			if (f instanceof GenericAttributedFeature) {
				Collection<? extends GenericAttribute> featAtts = ((GenericAttributedFeature) f)
						.getAttributes();
				for (GenericAttribute genAtt : featAtts) {
					IntegerVariable attVar = addAttribute(f, genAtt);
					auxAtts.add(attVar);
					attsMap.put(attVar.getName(),attVar);
				}
			}
		}

		this.features = aux.toArray(new IntegerVariable[1]);
		this.attributes = auxAtts.toArray(new IntegerVariable[1]);
	}

	/**
	 * Method to add attributes
	 * 
	 * @param f
	 * @param featAtts
	 */
	private IntegerVariable addAttribute(GenericFeature f, GenericAttribute att) {
		IntegerVariable attVar = null;
		String attName = f.getName() + "." + att.getName();
		es.us.isa.FAMA.models.domain.Domain d = att.getDomain();
		Object nullValue = att.getNullValue();
		Integer intNullVal = 0;
		Constraint domConstraint = null;
		if (d instanceof IntegerDomain) {
			intNullVal = att.getIntegerValue(nullValue);
			if (d instanceof RangeIntegerDomain) {
				RangeIntegerDomain rangeDom = (RangeIntegerDomain) d;
				Iterator<Range> itRanges = rangeDom.getRanges().iterator();
				int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;

				while (itRanges.hasNext()) {
					Range r = itRanges.next();
					if (r.getMin() < min) {
						min = r.getMin();
					}
					if (r.getMax() > max) {
						max = r.getMax();
					}
				}

				if (intNullVal > max) {
					max = intNullVal;
				} else if (intNullVal < min) {
					min = intNullVal;
				}
				// creamos la vble con el rango
//				attVar = makeIntVar(attName, min, max,
//						Options.V_NO_DECISION);
				attVar = makeIntVar(attName, min, max);
				domConstraint = Choco.and(Choco.geq(attVar, min),Choco.leq(attVar, max));
			} else if (d instanceof SetIntegerDomain) {
				SetIntegerDomain setDom = (SetIntegerDomain) d;
				List<Integer> allowedVals = new LinkedList<Integer>();
				allowedVals.add(intNullVal);
				allowedVals.addAll(setDom.getAllIntegerValues());
				
				// attVar = makeIntVar(attName,
				// allowedVals,"cp:bound","cp:no_decision");
				int[] valsArray = new int[allowedVals.size()];
				Iterator<Integer> itValues = allowedVals.iterator();
				int i = 0;
				while (itValues.hasNext()) {
					valsArray[i] = itValues.next();
					i++;
				}
				// attVar = makeIntVar(attName,
				// allowedVals,"cp:bound","cp:no_decision");
//				attVar = makeIntVar(attName, valsArray, Options.V_ENUM,
//						Options.V_NO_DECISION);
				attVar = makeIntVar(attName, valsArray, Options.V_ENUM);
				domConstraint = Choco.and(Choco.geq(attVar, valsArray[0]),
						Choco.leq(attVar, valsArray[valsArray.length - 1]));
			}
		} else if (d instanceof ObjectDomain) {
			intNullVal = att.getIntegerValue(nullValue);
			ObjectDomain objDom = (ObjectDomain) d;
			List<Integer> allowedVals = new LinkedList<Integer>();
			allowedVals.add(intNullVal);
			allowedVals.addAll(objDom.getAllIntegerValues());
			
			int[] valsArray = new int[allowedVals.size()];
			Iterator<Integer> itValues = allowedVals.iterator();
			int i = 0;
			while (itValues.hasNext()) {
				valsArray[i] = itValues.next();
				i++;
			}
			// attVar = makeIntVar(attName,
			// allowedVals,"cp:bound","cp:no_decision");
//			attVar = makeIntVar(attName, valsArray, Options.V_ENUM, Options.V_NO_DECISION);
			attVar = makeIntVar(attName, valsArray, Options.V_ENUM);
			domConstraint = Choco.and(Choco.geq(attVar, valsArray[0]),
					Choco.leq(attVar, valsArray[valsArray.length - 1]));
		} else {
			throw new FAMAException("Unknown domain type");
		}
		// aï¿½adimos la IntegerVariable
		// atts.put(attName, att);
		// problem.addVariable(attVar);

		IntegerVariable varFeat = this.featuresMap.get(f.getName());
		Constraint nullValueConstraint = Choco.ifOnlyIf(Choco.eq(varFeat, 0), Choco.eq(attVar, intNullVal));
		constraints.add(nullValueConstraint);
		
		return attVar;
	}

	@Override
	protected void addComplexConstraint(
			es.us.isa.FAMA.models.featureModel.Constraint c) {
		Constraint relation = chocoParser.translateToConstraint(c.getAST());
		constraints.add(relation);
	}

	protected class ChocoParser {

		private ConstantIntConverter converter;
		private String featName;

		public ChocoParser() {
			// count = 0;
			featName = null;
			converter = new ConstantIntConverter();
			//XXX be careful with this StringDomainConverter. you may
			//have problems translating constants into integers. try to avoid
			//them and just use standard integer domains [Min,Max]
			//for the attributes
			converter.addIntConverter(new StringDomainIntConverter());
		}

		public Constraint translateToInvariant(Tree<String> ast,
				String featInvariant) {
			featName = featInvariant;
			Constraint res = null;
			Node<String> n = ast.getRootElement();
			res = translateLogical(n);
			return res;
		}

		public Constraint translateToConstraint(Tree<String> ast) {
			featName = null;
			Constraint res = null;
			Node<String> n = ast.getRootElement();
			res = translateLogical(n);
			return res;
		}

		private Constraint translateLogical(Node<String> tree) {
			// constraints logicas:
			// AND, OR, NOT, IMPLIES, IFF, REQUIRES, EXCLUDES
			// LOGICO -> LOGICO
			Constraint res = null;
			String data = tree.getData();
			List<Node<String>> children = tree.getChildren();
			int n = children.size();
			if (n == 2) {
				if (data.equals(KeyWords.AND)) {
					Constraint e1 = translateLogical(children.get(0));
					Constraint e2 = translateLogical(children.get(1));
					res = and(e1, e2);
				} else if (data.equals(KeyWords.OR)) {
					Constraint e1 = translateLogical(children.get(0));
					Constraint e2 = translateLogical(children.get(1));
					res = or(e1, e2);
				} else if (data.equals(KeyWords.IMPLIES)
						|| data.equals(KeyWords.REQUIRES)) {
					Constraint e1 = translateLogical(children.get(0));
					Constraint e2 = translateLogical(children.get(1));
					res = implies(e1, e2);
				} else if (data.equals(KeyWords.IFF)) {
					Constraint e1 = translateLogical(children.get(0));
					Constraint e2 = translateLogical(children.get(1));
					res = ifOnlyIf(e1, e2);
				} else if (data.equals(KeyWords.EXCLUDES)) {
					// tendremos una feature > 0 a cada lado,
					// asi que hacemos un implies negando la parte dcha
					// (feat > 0) implies (not (feat > 0))
					Constraint e1 = translateLogical(children.get(0));
					Constraint aux = translateLogical(children.get(1));
					Constraint e2 = not(aux);
					res = implies(e1, e2);
				} else {
					res = translateRelational(tree);
				}
			} else if (n == 1) {
				if (data.equals(KeyWords.NOT)) {
					Constraint e1 = translateLogical(children.get(0));
					res = not(e1);
				}
			} else {
				if (isFeature(tree)) {
					IntegerVariable feat = featuresMap.get(data);
					res = gt(feat, 0);
				}
			}
			return res;
		}

		private Constraint translateRelational(Node<String> tree) {
			// constraints relaciones:
			// >, >=, <, <=, ==, !=
			// ENTERO -> LOGICO
			Constraint res = null;
			String data = tree.getData();
			List<Node<String>> children = tree.getChildren();
			IntegerExpressionVariable e1 = translateInteger(children.get(0));
			IntegerExpressionVariable e2 = translateInteger(children.get(1));
			if (data.equals(KeyWords.GREATER)) {
				res = gt(e1, e2);
			} else if (data.equals(KeyWords.GREATER_EQUAL)) {
				res = geq(e1, e2);
			} else if (data.equals(KeyWords.LESS)) {
				res = lt(e1, e2);
			} else if (data.equals(KeyWords.LESS_EQUAL)) {
				res = leq(e1, e2);
			} else if (data.equals(KeyWords.EQUAL)) {
				res = eq(e1, e2);
			} else if (data.equals(KeyWords.NON_EQUAL)) {
				res = neq(e1, e2);
			}
			return res;
		}

		private IntegerExpressionVariable translateInteger(Node<String> tree) {
			// constraints enteras:
			// ENTERO -> ENTERO
			IntegerExpressionVariable res = null;
			String data = tree.getData();
			List<Node<String>> children = tree.getChildren();
			if (data.equals(KeyWords.PLUS)) {
				IntegerExpressionVariable e1 = translateInteger(children.get(0));
				IntegerExpressionVariable e2 = translateInteger(children.get(1));
				res = plus(e1, e2);
			} else if (data.equals(KeyWords.MINUS)) {
				IntegerExpressionVariable e1 = translateInteger(children.get(0));
				IntegerExpressionVariable e2 = translateInteger(children.get(1));
				res = minus(e1, e2);
			} else if (data.equals(KeyWords.MULT)) {
				IntegerExpressionVariable e1 = translateInteger(children.get(0));
				IntegerExpressionVariable e2 = translateInteger(children.get(1));
				res = mult(e1, e2);
			} else if (data.equals(KeyWords.DIV)) {
				IntegerExpressionVariable e1 = translateInteger(children.get(0));
				IntegerExpressionVariable e2 = translateInteger(children.get(1));
				res = div(e1, e2);
			} else if (data.equals(KeyWords.MOD)) {
				IntegerExpressionVariable e1 = translateInteger(children.get(0));
				IntegerExpressionVariable e2 = translateInteger(children.get(1));
				res = mod(e1, e2);
			} else if (data.equals(KeyWords.POW)) {
				IntegerExpressionVariable e1 = translateInteger(children.get(0));
				IntegerExpressionVariable e2 = translateInteger(children.get(1));
				res = power(e1, e2);
			} else if (data.equals(KeyWords.UNARY_MINUS)) {
				IntegerExpressionVariable e1 = translateInteger(children.get(0));
				res = neg(e1);
			} else if (isIntegerConstant(tree)) {
				// TODO por ahora, solo permitiremos constraints
				// con constantes enteras
				int value = Integer.parseInt(data);
				// IntegerVariable aux1 = makeIntVar("@aux" + count, value,
				// value);
				// IntegerVariable aux1 = Choco.makeConstantVar("@aux" + count,
				// value);
				IntegerVariable aux1 = constant(value);
				// hara falta una constraint para el valor?
				res = aux1;
				// count++;
			} else if (isAttribute(tree)) {
				String attName = getAttributeName(tree);
				res = attsMap.get(attName);
			} else {
				// es una constante, usamos el intConverter
				// XXX asi en teoria debe funcionar :)
				Integer i = converter.translate2Integer(tree
						.getData());
				if (i != null) {
					res = constant(i);
				}
			}
			return res;
		}

		private String getAttributeName(Node<String> n) {
			String res = null;
			if (featName == null) {
				String s = n.getData();
				boolean b = s.equals(KeyWords.ATTRIBUTE);
				if (b && (n.getNumberOfChildren() == 2)) {
					List<Node<String>> list = n.getChildren();
					res = list.get(0).getData() + "." + list.get(1).getData();
				}
			} else {
				res = featName + "." + n.getData();
			}

			return res;
		}

		private boolean isAttribute(Node<String> n) {
			if (featName == null) {
				return n.getData().equals(KeyWords.ATTRIBUTE);
			} else {
				String aux = featName + "." + n.getData();
				Object res = attsMap.get(aux);
				return (res != null);
			}

		}

		private boolean isFeature(Node<String> n) {
			String s = n.getData();
			return (featuresMap.get(s) != null);
		}

		private boolean isIntegerConstant(Node<String> n) {
			String s = n.getData();
			try {
				Integer.parseInt(s);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

		private boolean isStringConstant(Node<String> n) {
			if (!isFeature(n) && !isVersionConstant(n)) {
				return true;
			}
			return false;
		}

		private boolean isVersionConstant(Node<String> n) {
			boolean b = true;
			StringTokenizer st = new StringTokenizer(n.getData());
			if (b = (st.countTokens() == 3)) {
				String s1 = st.nextToken();
				String s2 = st.nextToken();
				String s3 = st.nextToken();
				b = b && isInteger(s1);
				b = b && isInteger(s2);
				b = b && isInteger(s3);

			}
			return b;
		}

		private boolean isInteger(String s) {
			try {
				Integer.parseInt(s);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

	}// ChocoParser

	@Override
	public int getNumberOfVariables() {
		return features.length + attributes.length;
	}

	@Override
	public int getNumberOfConstraints() {
		return constraints.size();
//		return reifyVars.length;
	}


	/**
	 * Method to compute a valid seed (solution) of the FM.
	 * in this way, metaheuristic algorithms should converge faster
	 */
	@Override
	public Collection<Solution> computeSeeds(AAFMProblem problem, int n) {
		// XXX be careful with the state of problem & solver
		List<Solution> result = new LinkedList<Solution>();
		solver = new CPSolver();
		model.addVariables(features);
		model.addVariables(attributes);
		model.addConstraints(constraints.toArray(new Constraint[1]));
		solver.read(model);
		boolean b = solver.solve();
		
		Solution sol = extractSolutions(problem, b);
		
		result.add(sol);
		
		for (int i = 1; i < n; i++){
			b = solver.nextSolution();
			sol = extractSolutions(problem, b);
			result.add(sol);
		}
		
		//XXX reset the model before exit
		model = new CPModel();
		return result;
	}

	private Solution extractSolutions(AAFMProblem problem, boolean b) {
		Variable[] vars = new Variable[1];
		int totalSize = features.length + attributes.length;
		ArrayInt arrayInt = new ArrayInt(totalSize,problem);
		vars[0] = arrayInt;
		
		if (b){
			for (int i = 0; i < features.length;i++){
				int val = solver.getVar(features[i]).getVal();
				try {
					arrayInt.setValue(i, val);
				} catch (JMException e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < attributes.length;i++){
				int val = solver.getVar(attributes[i]).getVal();
				try {
					arrayInt.setValue(i+features.length,val);
				} catch (JMException e) {
					e.printStackTrace();
				}
			}
		}
		Solution sol = new Solution();
		sol.setDecisionVariables(vars);
		return sol;
	}

	
	

	@Override
	public FAMAAttributedFeatureModel getAtomicSets() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExtendedConfiguration solution2Configuration(Solution s){
		ExtendedConfiguration result = new ExtendedConfiguration();
		Variable[] aux = s.getDecisionVariables();
		ArrayInt jMetalVars = (ArrayInt) aux[0];

		int numberOfFeatures = features.length;
		for (int i = 0; i < numberOfFeatures; i++) {
			int val = 0;
			try {
				val = jMetalVars.getValue(i);
			} catch (JMException e) {
				//XXX be careful here
				e.printStackTrace();
			}
			GenericFeature f = fm.searchFeatureByName(features[i].getName());
			result.addElement(f, val);
		}// for

		int numberOfAtts = attributes.length;
		int totalSize = numberOfFeatures + numberOfAtts;
		for (int i = numberOfFeatures; i < totalSize; i++) {
			try {
				int val = jMetalVars.getValue(i);
				GenericAttribute att = FMUtils.searchAttribute(fm, attributes[i-numberOfFeatures].getName());
				result.addElement(att, val);
			} catch (JMException e) {
				e.printStackTrace();
			}
		}// for

		return result;
	}

	@Override
	protected void recordSolverState() {
		solver = new CPSolver();
		solver.read(model);
		try {
			solver.propagate();
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		worldState = solver.getEnvironment().getWorldIndex();
//		solver.worldPush();
	}
	

}// ChocoSolver
