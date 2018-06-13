package es.us.isa.ChocoReasoner.twolayer;
/**
 * This file is not part of FaMa FW, and actually is not open source, the distribution of this piece of software is not allowed yet every rights owns to José Galindo, mail malawito@gmail.com for more info.
 */

import static choco.Choco.and;
import static choco.Choco.constant;
import static choco.Choco.div;
import static choco.Choco.eq;
import static choco.Choco.geq;
import static choco.Choco.gt;
import static choco.Choco.ifOnlyIf;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import choco.cp.model.CPModel;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.TwoLayerReasoner;
import es.us.isa.FAMA.Reasoner.questions.twolayer.TwoLayerQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.featureModel.KeyWords;
import es.us.isa.FAMA.models.featureModel.extended.ConstantIntConverter;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.models.featureModel.extended.StringDomainIntConverter;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.util.Node;
import es.us.isa.util.Tree;

public class ChocoReasoner extends TwoLayerReasoner {
	public es.us.isa.ChocoReasoner.attributed.ChocoReasoner topLayerReasoner= new  es.us.isa.ChocoReasoner.attributed.ChocoReasoner();
	public es.us.isa.ChocoReasoner.attributed.ChocoReasoner bottomLayerReasoner= new  es.us.isa.ChocoReasoner.attributed.ChocoReasoner();
	protected Collection<Constraint> crossModelCTC;
	protected Model problem;
	
	public Map<String, GenericAttributedFeature> features;
	public Map<String, IntegerVariable> variables;
	public Map<String, GenericAttribute> atts;
	public Map<String, IntegerVariable> attVars;
	public Map<String, Constraint> dependencies;
	public Map<String, IntegerExpressionVariable> setRelations;
	protected List<Constraint> configConstraints;
	protected ChocoParser chocoParser;
	protected ConstantIntConverter constantIntConverter;

	public ChocoReasoner(){
		super();
		problem=new CPModel();
		features = new HashMap<String, GenericAttributedFeature>();
		variables = new HashMap<String, IntegerVariable>();
		atts = new HashMap<String, GenericAttribute>();
		attVars = new HashMap<String, IntegerVariable>();
		problem = new CPModel();
		dependencies = new HashMap<String, Constraint>();
		setRelations = new HashMap<String, IntegerExpressionVariable>();
		configConstraints = new ArrayList<Constraint>();
		chocoParser = new ChocoParser();
		constantIntConverter = new ConstantIntConverter();
		constantIntConverter.addIntConverter(new StringDomainIntConverter());
	}
		
	@Override
	public void createProblem(Question q) {
		TwoLayerQuestion tlq= (TwoLayerQuestion)q;
		
		
		tlq.getBottomLayer().transformTo(bottomLayerReasoner);
		tlq.getTopLayer().transformTo(topLayerReasoner);
		Iterator<IntegerVariable> intVarIterator = topLayerReasoner.getProblem().getIntVarIterator();
		while(intVarIterator.hasNext()){
			problem.addVariable(intVarIterator.next());			
		}		 
		intVarIterator = bottomLayerReasoner.getProblem().getIntVarIterator();
		while(intVarIterator.hasNext()){
			problem.addVariable(intVarIterator.next());			
		}
		Iterator<Constraint> constraintIterator = topLayerReasoner.getProblem().getConstraintIterator();
		while(constraintIterator.hasNext()){
			problem.addConstraint(constraintIterator.next());	
		}
		constraintIterator = bottomLayerReasoner.getProblem().getConstraintIterator();
		while(constraintIterator.hasNext()){
			problem.addConstraint(constraintIterator.next());	
		}
		  
		
		this.atts.putAll(bottomLayerReasoner.getAtts());
		this.atts.putAll(topLayerReasoner.getAtts());
		this.features.putAll(bottomLayerReasoner.getFeatures());
		this.features.putAll(topLayerReasoner.getFeatures());
		this.attVars.putAll(bottomLayerReasoner.getAttVars());
		this.attVars.putAll(topLayerReasoner.getAttVars());
		this.variables.putAll(bottomLayerReasoner.getVariables());
		this.variables.putAll(topLayerReasoner.getVariables());
		this.dependencies.putAll(topLayerReasoner.getDependencies());
		this.dependencies.putAll(bottomLayerReasoner.getDependencies());
		this.setRelations.putAll(topLayerReasoner.getSetRelations());
		this.setRelations.putAll(bottomLayerReasoner.getSetRelations());	
		
		String interModelRelationsips = tlq.getInterModelRelationsips();
		try {
			BufferedReader ctcreader= new BufferedReader(new FileReader(interModelRelationsips));
			String linea=ctcreader.readLine();
			while(linea!=null){
				Constraint translateToConstraint = chocoParser.translateToConstraint(tlq.getBottomLayer().getConstraint(linea));
				problem.addConstraint(translateToConstraint);
				linea=ctcreader.readLine();	
			}
		} catch (IOException e) {
			System.err.println("Error parsing the cros-model contraints");
			e.printStackTrace();
		}
	}

	

	@Override
	public void addConfigurations(Collection<Configuration> cc) {
		for(Configuration conf : cc){
			applyStagedConfiguration(conf);
		}
	}

	@Override
	public void unapplyStagedConfigurations() {
		Iterator<Constraint> it = this.configConstraints.iterator();
		while (it.hasNext()) {
			Constraint cons = it.next();
			problem.removeConstraint(cons);
			it.remove();
		}
	}

	@Override
	public PerformanceResult ask(Question q) {
		return ((ChocoQuestion)q).answer(this);
		
	}

	@Override
	public Map<String, Object> getHeusistics() {
		//TODO
		return null;
	}

	@Override
	public void setHeuristic(Object obj) {
		//TODO
	}

	@Override
	public void applyStagedConfiguration(Configuration conf) {

		Iterator<Entry<VariabilityElement, Integer>> it = conf.getElements()
				.entrySet().iterator();

		Map<String, IntegerVariable> vars = this.variables;
		Map<String, IntegerExpressionVariable> rels = this.setRelations;
		Map<String, IntegerVariable> atts = this.attVars;
		while (it.hasNext()) {
			Entry<VariabilityElement, Integer> e = it.next();
			VariabilityElement v = e.getKey();
			int arg1 = e.getValue().intValue();
			Constraint aux;
			// the constraint is created to not have a solution for the problem
			IntegerVariable errorVar = makeIntVar("error", 0, 0,
					"cp:no_decision");
			Constraint error = eq(1, errorVar);
			if (v instanceof GenericAttributedFeature) {
				IntegerVariable arg0 = vars.get(v.getName());
				if (!this.features.values().contains((GenericAttributedFeature) v)) {
					if (e.getValue() == 0) {
						System.err.println("The feature " + v.getName()
								+ " do not exist on the model");
					} else {
						problem.addConstraint(error);
						this.configConstraints.add(error);
						System.err.println("The feature " + v.getName()
								+ " do not exist, and can not be added");
					}
				} else {
					aux = eq(arg0, arg1);
					problem.addConstraint(aux);
					this.configConstraints.add(aux);
				}

			} else if (v instanceof GenericRelation) {
				IntegerExpressionVariable arg0 = rels.get(v.getName());
				if (!this.setRelations.keySet().contains(v.getName())) {
					if (e.getValue() == 0) {
						System.err.println("The relation " + v.getName()
								+ "do not exist already in to the model");
					} else {
						problem.addConstraint(error);
						this.configConstraints.add(error);
						System.err.println("The relation " + v.getName()
								+ "do not exist, and can not be added");
					}
				} else {
					aux = eq(arg0, arg1);
					problem.addConstraint(aux);
					this.configConstraints.add(aux);
				}
			} else if (v instanceof GenericAttribute) {
				GenericAttribute attAux = (GenericAttribute) v;
				String attName = attAux.getFeature().getName() + "."
						+ v.getName();
				IntegerVariable arg0 = atts.get(attName);
				if (!this.atts.values().contains((GenericAttribute) v)) {
					if (e.getValue() == 0) {
						System.err.println("The attribute " + v.getName()
								+ " do not exist on the model");
					} else {
						problem.addConstraint(error);
						this.configConstraints.add(error);
						System.err.println("The attribute " + v.getName()
								+ " do not exist, and can not be added");
					}
				} else {
					aux = eq(arg0, arg1);
					problem.addConstraint(aux);
					this.configConstraints.add(aux);
				}
			} else {
				System.err.println("Type of the Variability element "
						+ v.getName() + " not recognized");
			}
		}

	}

	
	protected class ChocoParser {

		private String featName;

		public ChocoParser() {
			// count = 0;
			featName = null;
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
					//GenericAttributedFeature feature = searchFeatureByName(data);
					IntegerVariable feat = variables.get(data);
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
			}
			else if (isAttribute(tree)) {
				String attName = getAttributeName(tree);
				res = attVars.get(attName);
			}
			else {
				//es una constante, usamos el intConverter
				//XXX asi en teoria debe funcionar :)
				Integer i = constantIntConverter.translate2Integer(tree.getData());
				if (i != null){
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
				Object res = atts.get(aux);
				return (res != null);
			}

		}

		private boolean isFeature(Node<String> n) {
			String s = n.getData();
			GenericFeature searchFeatureByName = searchFeatureByName(s);
			return (searchFeatureByName != null);
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
		
//		private boolean isStringConstant(Node<String> n) {
//			if (!isFeature(n) && !isVersionConstant(n)){
//				return true;
//			}
//			return false;
//		}
		
//		private boolean isVersionConstant(Node<String> n) {
//			boolean b = true;
//			StringTokenizer st = new StringTokenizer(n.getData());
//			if (b = (st.countTokens() == 3)){
//				String s1 = st.nextToken();
//				String s2 = st.nextToken();
//				String s3 = st.nextToken();
//				b = b && isInteger(s1);
//				b = b && isInteger(s2);
//				b = b && isInteger(s3);
//				
//			}
//			return b;
//		}
		
//		private boolean isInteger(String s){
//			try {
//				Integer.parseInt(s);
//				return true;
//			} catch (NumberFormatException e) {
//				return false;
//			}
//		}

	}


	public Model getProblem() {
		return this.problem;
	}
	public Map<String, IntegerVariable> getAttributesVariables() {
		return attVars;
	}
	public GenericFeature searchFeatureByName(String id) {
		for(GenericFeature f: features.values()){
			if(f.getName().equalsIgnoreCase(id)){
				return f;
			}
		}
		
		return null;
	}
	public void unapplyStaged(){
		for(Constraint c:this.configConstraints){
			problem.removeConstraint(c);
		}
	}
}
