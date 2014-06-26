package es.us.isa.adhoc.ec2.cpopt;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntExpr;
import ilog.concert.IloIntTupleSet;
import ilog.concert.IloIntVar;
import ilog.concert.IloObjective;
import ilog.concert.IloSolution;
import ilog.cp.IloCP;
import ilog.cp.IloSearchPhase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.domain.Domain;
import es.us.isa.FAMA.models.domain.IntegerDomain;
import es.us.isa.FAMA.models.domain.ObjectDomain;
import es.us.isa.FAMA.models.domain.Range;
import es.us.isa.FAMA.models.domain.RangeIntegerDomain;
import es.us.isa.FAMA.models.domain.RealDomain;
import es.us.isa.FAMA.models.domain.SetIntegerDomain;
import es.us.isa.FAMA.models.featureModel.Constraint;
import es.us.isa.FAMA.models.featureModel.KeyWords;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.parser.FMFParser;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.aws.aux.table.ConstraintTable;
import es.us.isa.aws.scraper.ec2.AmazonEC2Scraper;
import es.us.isa.util.Node;
import es.us.isa.util.Tree;

public class CPOptEC2Reasoner {

	public static final int REAL_PRECISION = 1000;

	// ConfigPoint => (FeatureValue => IntegerValue)
	private Map<String, List<String>> configPointValues;

	// XXX Feature => <ConfigPoint,Collection<Integer>>
	// dada una feature, devuelve a que punto de configuracion
	// pertenece, y cual es su valor (o cuales son sus valores si es abstracta)
	private Map<String, Pair<String, Collection<Integer>>> featureValues;

	private FAMAAttributedFeatureModel fm;
	
	private CPOptParser constraintParser;
	private Map<String, IloIntVar> allVars;
	private Map<String, IloIntVar> configPointVars;
	private Map<String, IloIntVar> attVars;
	private IloCP cp;
//	private IloSolution preSolvingState;
	
	private Collection<String> realValuedAttributes;
	
	private AmazonEC2Scraper scraper;

	private long analysisTime;

	public CPOptEC2Reasoner(AmazonEC2Scraper scraper){
		reset();
		this.scraper = scraper;
	}
	
	private void reset() {
		cp = new IloCP();
		allVars = new HashMap<String, IloIntVar>();
		configPointVars = new HashMap<String, IloIntVar>();
		attVars = new HashMap<String, IloIntVar>();
		constraintParser = new CPOptParser();
		
		configPointValues = new HashMap<String, List<String>>();
		featureValues = new HashMap<String, Pair<String, Collection<Integer>>>();
		
		realValuedAttributes = new HashSet<String>();
	}

	public void mapEC2Model(FAMAAttributedFeatureModel fm) {
		// 0. parse the model from the file
		this.fm = fm;

		try {
			// 1. get the 6 main configuration points
			String[] configPoints = new String[] { "OS", "Instance", "Use",
					"Location", "Dedication", "EBS" };
			for (int i = 0; i < configPoints.length; i++) {
				AttributedFeature f = fm.searchFeatureByName(configPoints[i]);
				// 2. for each main config point, create its variable and its attributes
				addConfigPoint(f);
			}

			// 3. add root attributes
			AttributedFeature root = fm.getRoot();
			Collection<GenericAttribute> atts = root.getAttributes();
			for (GenericAttribute att : atts) {
				this.addAttribute(att);
			}

			// 4. parse standard constraints
			Collection<Constraint> stdConstraints = fm.getConstraints();
			for (Constraint c : stdConstraints) {
				IloConstraint res = constraintParser.translateToConstraint(c
						.getAST());
				cp.add(res);
			}

			// 5. parse characteristics and pricing as IloIntTupleSets
			// we get the constraints directly from the ec2 pages
			
			scraper.parseEC2Constraints("./NewAmazonEC2Constrains.txt");
			List<ConstraintTable> tables = new LinkedList<ConstraintTable>();
			tables.add(scraper.getCharsTable());
			tables.add(scraper.getPricingTable());

			for (ConstraintTable t : tables) {
				String[] header = t.getHeader();
				IloIntTupleSet ts = cp.intTable(header.length);
				List<IloIntVar> tupleVars = new ArrayList<IloIntVar>();
				for (String s : header) {
					tupleVars.add(allVars.get(s));
				}

				String[][] combinations = t.getCombinations();
				for (int i = 0; i < combinations.length; i++) {
					int[] tupleValues = new int[combinations[i].length];
					for (int j = 0; j < combinations[i].length; j++) {
						int valAux = getTableValue(combinations[i][j],header[j]);
						tupleValues[j] = valAux;
					}
					cp.addTuple(ts, tupleValues);
				}
				IloIntVar[] varArray = tupleVars.toArray(new IloIntVar[1]);
				IloConstraint tuple = cp.allowedAssignments(varArray, ts);
				cp.add(tuple);
			}
			
			// Finally, now we propagate and save the state
//			cp.propagate();
//			preSolvingState = saveState(cp);
		} catch (IloException e) {
			e.printStackTrace();
		}

	}

	private IloIntVar addConfigPoint(AttributedFeature f) throws IloException {
		IloIntVar var = null;
		
		List<AttributedFeature> subfeatures = f.getAllSubfeatures();
		List<AttributedFeature> leaves = f.getLeafSubfeatures();
		List<AttributedFeature> abstractFeatures = new LinkedList<AttributedFeature>(
				subfeatures);
		abstractFeatures.removeAll(leaves);
		
		if (f.getParent().isOptional()) {
			// from 0 to n if it's optional
			var = cp.intVar(0, leaves.size());
		} else {
			// from 1 to n if it's mandatory
			var = cp.intVar(1, leaves.size());
		}
		allVars.put(f.getName(), var);
		configPointVars.put(f.getName(), var);
		cp.add(var);
		// XXX storing the value for every leaf feature
		int auxCont = 1;
		List<String> listOfFeatures = new ArrayList<String>();
		for (AttributedFeature a : leaves) {
			listOfFeatures.add(a.getName());
			Pair<String, Collection<Integer>> pair = new Pair<String, Collection<Integer>>();
			pair.setK(f.getName());
			Collection<Integer> aux = new LinkedList<Integer>();
			aux.add(auxCont);
			pair.setV(aux);
			featureValues.put(a.getName(), pair);
			auxCont++;
		}
		configPointValues.put(f.getName(), listOfFeatures);

		// XXX storing the values for the abstract features
		for (AttributedFeature a : abstractFeatures) {
			Collection<AttributedFeature> subfeats = a
					.getLeafSubfeatures();
			Collection<Integer> aux = new LinkedList<Integer>();
			for (AttributedFeature subfeat : subfeats) {
				Collection<Integer> values = featureValues.get(subfeat.getName()).getV();
				aux.addAll(values);
			}
			Pair<String, Collection<Integer>> pair = new Pair<String, Collection<Integer>>();
			pair.setK(f.getName());
			pair.setV(aux);
			featureValues.put(a.getName(), pair);
		}
		
		Collection<GenericAttribute> atts = f.getAttributes();
		for (GenericAttribute att : atts) {
			this.addAttribute(att);
			if (f.getParent().isOptional()){
				// then the attribute may be not present
				IloIntVar aVar = attVars.get(att.getFullName());
				IloConstraint domConstraint = cp.imply(cp.eq(var, 0), cp.eq(aVar, 0));;
				cp.add(domConstraint);
			}
		}
		
		return var;
	}

	private IloSolution saveState(IloCP cp2) throws IloException {
		IloSolution sol = cp2.solution();
		for (IloIntVar v:allVars.values()){
			sol.add(v);
		}
		cp2.store(sol);
		return sol;
	}

	private int getTableValue(String string, String header) {
		Integer val = null;
		if (configPointVars.containsKey(header)){
			//config point
			Collection<Integer> values = this.featureValues.get(string).getV();
			if (values.size() == 1){
				val = values.iterator().next();
			}
		}
		else{
			// attribute
			boolean isInteger = true;
			try {
				val = Integer.parseInt(string);
			} catch (NumberFormatException e) {
				isInteger = false;
			}
			if (!isInteger) {
				Double aux = Double.parseDouble(string);
				val = translate2IntValue(aux);
			}
			if (realValuedAttributes.contains(header)) {
				// we need to translate it into an integer
				try {
					Double aux = Double.parseDouble(string);
					val = translate2IntValue(aux);
				} catch (NumberFormatException e) {}
			} else {
				try {
					val = Integer.parseInt(string);
				} catch (NumberFormatException e) {
					isInteger = false;
				}
			}
			
		}
		return val;
	}
	
	private int translate2IntValue(double d){
		double aux = d*REAL_PRECISION;
		int result = (int) aux;
		return result;
	}

	private void addAttribute(GenericAttribute a) throws IloException {
		Domain d = a.getDomain();
		IloIntVar var = null;
		if (d instanceof IntegerDomain) {
			if (d instanceof RangeIntegerDomain) {
				RangeIntegerDomain rangeDomain = (RangeIntegerDomain) d;
				Set<Range> ranges = rangeDomain.getRanges();
				// XXX we consider just the first range
				Range range = ranges.iterator().next();
				var = cp.intVar(range.getMin(), range.getMax(), a.getFullName());
				allVars.put(a.getFullName(), var);
				attVars.put(a.getFullName(), var);
			} else if (d instanceof SetIntegerDomain) {
				SetIntegerDomain setDomain = (SetIntegerDomain) d;
				Set<Integer> vals = setDomain.getAllIntegerValues();
				int[] arrayVals = new int[vals.size()];
				int index = 0;
				for (Integer val : vals) {
					arrayVals[index] = val;
					index++;
				}

				var = cp.intVar(arrayVals, a.getFullName());
				allVars.put(a.getFullName(), var);
				attVars.put(a.getFullName(), var);
			}
		} else if (d instanceof RealDomain) {
			RealDomain rd = (RealDomain) d;
			int lowerBound = translate2IntValue(rd.getLowerBound());
			int upperBound = translate2IntValue(rd.getUpperBound());
			// var = cp.numVar(rd.getLowerBound(), rd.getUpperBound(),
			// a.getFullName());
			var = cp.intVar(lowerBound, upperBound);
			allVars.put(a.getFullName(), var);
			attVars.put(a.getFullName(), var);
			//XXX it's a real attribute, so be careful when obtaining its value
			realValuedAttributes.add(a.getFullName());
		} else if (d instanceof ObjectDomain) {
			SetIntegerDomain setDomain = (SetIntegerDomain) d;
			Set<Integer> vals = setDomain.getAllIntegerValues();
			int[] arrayVals = new int[vals.size()];
			int index = 0;
			for (Integer val : vals) {
				arrayVals[index] = val;
				index++;
			}

			var = cp.intVar(arrayVals, a.getFullName());
			allVars.put(a.getFullName(), var);
			attVars.put(a.getFullName(), var);

		}
		// atts.put(a.getFullName(), a);
		// XXX we consider 0 as the default value of every attribute
		// IloConstraint aux = cp.imply(cp.eq(featureVar, 0), cp.eq(var, 0));
		// cp.add(aux);
		cp.add(var);
	}

	public long getAnalysisTime(){
		return analysisTime;
	}
	
	public ExtendedConfiguration optimise(ExtendedConfiguration conf, String optAtt) {
		ExtendedConfiguration optConfig = null;
		try {
			// 0. restore state
//			cp.restore(preSolvingState);
			
			// 1. map configuration
			IloConstraint confConstraint = mapConfiguration(conf);
			cp.add(confConstraint);
//			boolean b = true;
			boolean b = cp.propagate();
			
			// 2. if propagation returns true, we optimise
			if (b) {
				IloIntVar[] decisionVars = this.configPointVars.values().toArray(new IloIntVar[1]);
				IloSearchPhase search = cp.searchPhase(decisionVars);
				
				IloIntVar optVar = attVars.get(optAtt);
				IloObjective obj = cp.minimize(optVar);
				cp.add(obj);
				
//				cp.setParameter(IloCP.IntParam.Presolve, IloCP.ParameterValues.Off);
				
				System.out.println("Starting optimisation");
				long initTime = System.currentTimeMillis();
				b = cp.solve(search);
				analysisTime = System.currentTimeMillis() - initTime;
				System.out.println("End of the optimisation");
				
				
				if (b) {
					
					optConfig = new ExtendedConfiguration();
					// map back from CPLEX to the FM
					Set<Entry<String, IloIntVar>> featEntrySet = configPointVars
							.entrySet();
					for (Entry<String, IloIntVar> e : featEntrySet) {
						try {
							int val = (int) cp.getValue(e.getValue());
							List<String> valuesList = this.configPointValues
									.get(e.getKey());
							// XXX 0 implies the config point is inactive
							// the first value is 1
							if (val > 0) {
								int listIndex = val - 1;
								String configPointValue = valuesList
										.get(listIndex);
								VariabilityElement v = fm
										.searchFeatureByName(configPointValue);
								optConfig.addElement(v, 1);
							}
						} catch (Exception exception) {
							System.err.println(e.getKey() + " => "
									+ exception.getMessage());
						}
					}

					Set<Entry<String, IloIntVar>> attEntrySet = attVars
							.entrySet();
					for (Entry<String, IloIntVar> e : attEntrySet) {
						try {
							int val = (int) cp.getValue(e.getValue());
							GenericAttribute v = fm.searchAttributeByName(e
									.getKey());

							if (realValuedAttributes.contains(e.getKey())) {
								double realVal = translate2RealValue(val);
								optConfig.addAttValue(v, realVal);
							} else {
								optConfig.addAttValue(v, val);
							}
						} catch (Exception exception) {
							System.err.println(e.getKey() + " => "
									+ exception.getMessage());
						}
					}
				}
				cp.remove(obj);
			}
			// try to reuse the IloCP object
			cp.remove(confConstraint);
		} catch (IloException e) {
			e.printStackTrace();
		}
		return optConfig;
	}

	private double translate2RealValue(int val) {
		double result = (double)val;
		result = result/REAL_PRECISION;
		return result;
	}

	private IloConstraint mapConfiguration(ExtendedConfiguration conf) throws IloException {
		// XXX for now, we just map the tree constraint.
		// we do not map the explicit assignments to attributes or features
		// stored in the maps of the Configuration
		Collection<Tree<String>> col = conf.getAttConfigs();
		IloConstraint[] array = new IloConstraint[col.size()];
		int i = 0;
		for (Tree<String> t:col){
			array[i] = constraintParser.translateToConstraint(t);
			i++;
		}
		IloConstraint result = cp.and(array);
		return result;
	}

	protected class CPOptParser {

		private String featName;

		public CPOptParser() {
			// count = 0;
			featName = null;
		}

		public IloConstraint translateToInvariant(Tree<String> ast,
				String featInvariant) {
			featName = featInvariant;
			IloConstraint res = null;
			Node<String> n = ast.getRootElement();
			try {
				res = translateLogical(n);
			} catch (IloException e) {
				e.printStackTrace();
			}
			return res;
		}

		public IloConstraint translateToConstraint(Tree<String> ast) {
			featName = null;
			IloConstraint res = null;
			Node<String> n = ast.getRootElement();
			try {
				res = translateLogical(n);
			} catch (IloException e) {
				e.printStackTrace();
			}
			return res;
		}

		private IloConstraint translateLogical(Node<String> tree)
				throws IloException {
			// constraints logicas:
			// AND, OR, NOT, IMPLIES, IFF, REQUIRES, EXCLUDES
			// LOGICO -> LOGICO
			IloConstraint res = null;
			String data = tree.getData();
			List<Node<String>> children = tree.getChildren();
			int n = children.size();
			if (n == 2) {
				if (data.equals(KeyWords.AND)) {
					IloConstraint e1 = translateLogical(children.get(0));
					IloConstraint e2 = translateLogical(children.get(1));
					res = cp.and(e1, e2);
				} else if (data.equals(KeyWords.OR)) {
					IloConstraint e1 = translateLogical(children.get(0));
					IloConstraint e2 = translateLogical(children.get(1));
					res = cp.or(e1, e2);
				} else if (data.equals(KeyWords.IMPLIES)
						|| data.equals(KeyWords.REQUIRES)) {
					IloConstraint e1 = translateLogical(children.get(0));
					IloConstraint e2 = translateLogical(children.get(1));
					res = cp.imply(e1, e2);
				} else if (data.equals(KeyWords.IFF)) {
					IloConstraint e1 = translateLogical(children.get(0));
					IloConstraint e2 = translateLogical(children.get(1));
					res = cp.and(cp.imply(e1, e2), cp.imply(e2, e1));// ifOnlyIf(e1,
																		// e2);
				} else if (data.equals(KeyWords.EXCLUDES)) {
					// tendremos una feature > 0 a cada lado,
					// asi que hacemos un implies negando la parte dcha
					// (feat > 0) implies (not (feat > 0))
					IloConstraint e1 = translateLogical(children.get(0));
					IloConstraint aux = translateLogical(children.get(1));
					IloConstraint e2 = cp.not(aux);
					res = cp.imply(e1, e2);
				} else {
					res = translateRelational(tree);
				}
			} else if (n == 1) {
				if (data.equals(KeyWords.NOT)) {
					IloConstraint e1 = translateLogical(children.get(0));
					res = cp.not(e1);
				}

			} else {
				if (isFeature(tree)) {
					// 1 get the configuration point
					Pair<String, Collection<Integer>> pair = featureValues
							.get(data);
					// 2 get config point var
					IloIntVar cPoint = configPointVars.get(pair.getK());
					// 2 set the value (or values)
					IloConstraint[] auxArray = new IloConstraint[pair.getV()
							.size()];
					int auxCont = 0;
					for (Integer i : pair.getV()) {
						auxArray[auxCont] = cp.eq(cPoint, i);
						auxCont++;
					}
					if (auxArray.length > 1) {
						res = cp.or(auxArray);
					} else {
						res = auxArray[0];
					}

				} else if (data.equals(KeyWords.TRUE)) {
					res = cp.trueConstraint();
				} else if (data.equals(KeyWords.FALSE)) {
					res = cp.falseConstraint();
				}
			}
			return res;
		}

		private IloConstraint translateRelational(Node<String> tree)
				throws IloException {
			// constraints relaciones:
			// >, >=, <, <=, ==, !=
			// ENTERO -> LOGICO
			IloConstraint res = null;
			String data = tree.getData();
			List<Node<String>> children = tree.getChildren();
			IloIntExpr e1 = translateExpression(children.get(0));
			IloIntExpr e2 = translateExpression(children.get(1));
			if (data.equals(KeyWords.GREATER)) {
				if (e1 instanceof IloIntExpr && e2 instanceof IloIntExpr) {
					res = cp.gt((IloIntExpr) e1, (IloIntExpr) e2);
				} else {
					res = cp.not(cp.le(e1, e2));
				}
			} else if (data.equals(KeyWords.GREATER_EQUAL)) {
				if (e1 instanceof IloIntExpr && e2 instanceof IloIntExpr) {
					res = cp.ge((IloIntExpr) e1, (IloIntExpr) e2);
				} else {
					res = cp.ge(e1, e2);
				}
			} else if (data.equals(KeyWords.LESS)) {
				if (e1 instanceof IloIntExpr && e2 instanceof IloIntExpr) {
					res = cp.lt((IloIntExpr) e1, (IloIntExpr) e2);
				} else {
					res = cp.not(cp.ge(e1, e2));
				}
			} else if (data.equals(KeyWords.LESS_EQUAL)) {
				if (e1 instanceof IloIntExpr && e2 instanceof IloIntExpr) {
					res = cp.le((IloIntExpr) e1, (IloIntExpr) e2);
				} else {
					res = cp.le(e1, e2);
				}
			} else if (data.equals(KeyWords.EQUAL)) {
				if (e1 instanceof IloIntExpr && e2 instanceof IloIntExpr) {
					res = cp.eq((IloIntExpr) e1, (IloIntExpr) e2);
				} else {
					res = cp.eq(e1, e2);
				}
			} else if (data.equals(KeyWords.NON_EQUAL)) {
				if (e1 instanceof IloIntExpr && e2 instanceof IloIntExpr) {
					res = cp.neq((IloIntExpr) e1, (IloIntExpr) e2);
				} else {
					res = cp.not(cp.eq(e1, e2));
				}
			}

			return res;
		}

		private IloIntExpr translateExpression(Node<String> tree)
				throws IloException {
			// constraints enteras:
			// ENTERO -> ENTERO
			IloIntExpr res = null;
			String data = tree.getData();
			List<Node<String>> children = tree.getChildren();
			if (data.equals(KeyWords.PLUS)) {
				IloIntExpr e1 = translateExpression(children.get(0));
				IloIntExpr e2 = translateExpression(children.get(1));
				res = cp.sum(e1, e2);
			} else if (data.equals(KeyWords.MINUS)) {
				IloIntExpr e1 = translateExpression(children.get(0));
				IloIntExpr e2 = translateExpression(children.get(1));
				res = cp.diff(e1, e2);
			} else if (data.equals(KeyWords.MULT)) {
				IloIntExpr e1 = translateExpression(children.get(0));
				IloIntExpr e2 = translateExpression(children.get(1));
				res = cp.prod(e1, e2);
			} else if (data.equals(KeyWords.DIV)) {
				IloIntExpr e1 = translateExpression(children.get(0));
				IloIntExpr e2 = translateExpression(children.get(1));
				if (e1 instanceof IloIntExpr && e2 instanceof IloIntExpr) {
					res = cp.div((IloIntExpr) e1, (IloIntExpr) e2);
				}

				// } else if (data.equals(KeyWords.MOD)) {
				// IntegerExpressionVariable e1 =
				// translateInteger(children.get(0));
				// IntegerExpressionVariable e2 =
				// translateInteger(children.get(1));
				// res = mod(e1, e2);
			} 
//			else if (data.equals(KeyWords.POW)) {
//				IloIntExpr e1 = translateExpression(children.get(0));
//				IloIntExpr e2 = translateExpression(children.get(1));
//				res = cp.power(e1, e2);
//			}
			else if (data.equals(KeyWords.UNARY_MINUS)) {
				IloIntExpr e1 = translateExpression(children.get(0));
				res = cp.negative(e1);
			} else if (isIntegerConstant(tree)) {
				int value = Integer.parseInt(data);
				// IntegerVariable aux1 = makeIntVar("@aux" + count, value,
				// value);
				// IntegerVariable aux1 = Choco.makeConstantVar("@aux" + count,
				// value);
				IloIntExpr aux1 = cp.constant(value);
				// hara falta una constraint para el valor?
				res = aux1;
				// count++;
			} else if (isRealConstant(tree)) {
				double auxValue = Double.parseDouble(data);
				int value = translate2IntValue(auxValue);
				IloIntExpr aux1 = cp.constant(value);
				res = aux1;
			} else if (isAttribute(tree)) {
				String attName = getAttributeName(tree);
				res = attVars.get(attName);
			}
			// else {
			// // es una constante, usamos el intConverter
			// Integer i = constantIntConverter.translate2Integer(tree
			// .getData());
			// if (i != null) {
			// res = cp.constant(i);
			// }
			// }
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
				Object res = attVars.get(aux);
				return (res != null);
			}

		}

		private boolean isFeature(Node<String> n) {
			String s = n.getData();
			return (featureValues.get(s) != null);
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

		private boolean isRealConstant(Node<String> n) {
			String s = n.getData();
			try {
				Double.parseDouble(s);
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

	}

}
