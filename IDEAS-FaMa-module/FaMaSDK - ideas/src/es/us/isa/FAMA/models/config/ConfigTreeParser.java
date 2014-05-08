// $ANTLR : "TreeParser.g" -> "ConfigTreeParser.java"$

	package es.us.isa.FAMA.models.config;    
	import java.util.*;	
	import es.us.isa.FAMA.Exceptions.*;
	import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.*;
	import es.us.isa.FAMA.models.featureModel.*;
	import es.us.isa.FAMA.models.featureModel.extended.*;
	import es.us.isa.util.*;
	import es.us.isa.FAMA.models.domain.*;
	import es.us.isa.FAMA.models.domain.RealDomain;
	import es.us.isa.FAMA.parser.*;
	import es.us.isa.FAMA.models.config.*;
	import es.us.isa.FAMA.stagedConfigManager.*;

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;


public class ConfigTreeParser extends antlr.TreeParser       implements ConfigTreeParserTokenTypes
 {

	//Map<String,AST> mapASTFeatures = null;

	Map<String,AttributedFeature> features = new HashMap<String,AttributedFeature>();
	//zona de funciones	
		
	Collection<String> errors = new LinkedList<String>();
	
	
	public Constraint ASTtoConstraint(AST t, AST name){
		//TODO checkear que funciona bien
		String n = name.getText();
		Tree<String> tree = astToTree(t);
		Constraint res;
		res = new ComplexConstraint(tree);
		res.setName(n);
		return res;	
	}
	
	public Tree<String> astToTree(AST ast) {
		Node<String> root = new Node<String>();
		Tree<String> t = new Tree<String>(root);
		walkTree(ast, root);
		return t;
	}
	
	private void walkTree(AST ast, Node<String> node) {

		node.setData(ast.getText());
		int children = ast.getNumberOfChildren();
		if (children > 0) {
			AST child = ast.getFirstChild();
			Node<String> n = new Node<String>();
			node.addChild(n);
			walkTree(child, n);
			for (int i = 1; i < children; i++) {
				AST aux = child.getNextSibling();
				n = new Node<String>();
				node.addChild(n);
				walkTree(aux, n);
			}
		}

	}

public ConfigTreeParser() {
	tokenNames = _tokenNames;
}

	public final Configuration  entrada(AST _t) throws RecognitionException {
		Configuration res = null;;
		
		AST entrada_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Collection<Tree<String>> cons = new LinkedList<Tree<String>>(); ExtendedConfiguration auxRes = new ExtendedConfiguration();
		
		try {      // for error handling
			AST __t1322 = _t;
			AST tmp1_AST_in = (AST)_t;
			match(_t,CONFIGURACION);
			_t = _t.getFirstChild();
			cons=constraints(_t);
			_t = _retTree;
			_t = __t1322;
			_t = _t.getNextSibling();
			auxRes.setAttConfigs(cons); res = auxRes;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return res;
	}
	
	public final Collection<Tree<String>>  constraints(AST _t) throws RecognitionException {
		Collection<Tree<String>> constraints = new LinkedList<Tree<String>>();;
		
		AST constraints_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Constraint aux = null;
		
		try {      // for error handling
			{
			int _cnt1325=0;
			_loop1325:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CONSTRAINT)) {
					aux=constraint(_t);
					_t = _retTree;
					constraints.add(aux.getAST());
				}
				else {
					if ( _cnt1325>=1 ) { break _loop1325; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt1325++;
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return constraints;
	}
	
	public final Constraint  constraint(AST _t) throws RecognitionException {
		Constraint c = null;;
		
		AST constraint_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST n = null;
		AST e = null;
		
		try {      // for error handling
			AST __t1327 = _t;
			AST tmp2_AST_in = (AST)_t;
			match(_t,CONSTRAINT);
			_t = _t.getFirstChild();
			n = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			e = _t==ASTNULL ? null : (AST)_t;
			expresion(_t);
			_t = _retTree;
			c = ASTtoConstraint(e,n);
			_t = __t1327;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return c;
	}
	
	public final void expresion(AST _t) throws RecognitionException {
		
		AST expresion_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case IFF:
			{
				AST __t1329 = _t;
				AST tmp3_AST_in = (AST)_t;
				match(_t,IFF);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1329;
				_t = _t.getNextSibling();
				break;
			}
			case IMPLIES:
			{
				AST __t1330 = _t;
				AST tmp4_AST_in = (AST)_t;
				match(_t,IMPLIES);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1330;
				_t = _t.getNextSibling();
				break;
			}
			case EXCLUDES:
			{
				AST __t1331 = _t;
				AST tmp5_AST_in = (AST)_t;
				match(_t,EXCLUDES);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1331;
				_t = _t.getNextSibling();
				break;
			}
			case REQUIRES:
			{
				AST __t1332 = _t;
				AST tmp6_AST_in = (AST)_t;
				match(_t,REQUIRES);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1332;
				_t = _t.getNextSibling();
				break;
			}
			case OR:
			{
				AST __t1333 = _t;
				AST tmp7_AST_in = (AST)_t;
				match(_t,OR);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1333;
				_t = _t.getNextSibling();
				break;
			}
			case AND:
			{
				AST __t1334 = _t;
				AST tmp8_AST_in = (AST)_t;
				match(_t,AND);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1334;
				_t = _t.getNextSibling();
				break;
			}
			case NOT:
			{
				AST __t1335 = _t;
				AST tmp9_AST_in = (AST)_t;
				match(_t,NOT);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				_t = __t1335;
				_t = _t.getNextSibling();
				break;
			}
			case MAYOR:
			{
				AST __t1336 = _t;
				AST tmp10_AST_in = (AST)_t;
				match(_t,MAYOR);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1336;
				_t = _t.getNextSibling();
				break;
			}
			case MENOR:
			{
				AST __t1337 = _t;
				AST tmp11_AST_in = (AST)_t;
				match(_t,MENOR);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1337;
				_t = _t.getNextSibling();
				break;
			}
			case MAYOR_IGUAL:
			{
				AST __t1338 = _t;
				AST tmp12_AST_in = (AST)_t;
				match(_t,MAYOR_IGUAL);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1338;
				_t = _t.getNextSibling();
				break;
			}
			case MENOR_IGUAL:
			{
				AST __t1339 = _t;
				AST tmp13_AST_in = (AST)_t;
				match(_t,MENOR_IGUAL);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1339;
				_t = _t.getNextSibling();
				break;
			}
			case IGUAL:
			{
				AST __t1340 = _t;
				AST tmp14_AST_in = (AST)_t;
				match(_t,IGUAL);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1340;
				_t = _t.getNextSibling();
				break;
			}
			case DISTINTO:
			{
				AST __t1341 = _t;
				AST tmp15_AST_in = (AST)_t;
				match(_t,DISTINTO);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1341;
				_t = _t.getNextSibling();
				break;
			}
			case MAS:
			{
				AST __t1342 = _t;
				AST tmp16_AST_in = (AST)_t;
				match(_t,MAS);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1342;
				_t = _t.getNextSibling();
				break;
			}
			case MENOS:
			{
				AST __t1343 = _t;
				AST tmp17_AST_in = (AST)_t;
				match(_t,MENOS);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1343;
				_t = _t.getNextSibling();
				break;
			}
			case MULT:
			{
				AST __t1344 = _t;
				AST tmp18_AST_in = (AST)_t;
				match(_t,MULT);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1344;
				_t = _t.getNextSibling();
				break;
			}
			case DIV:
			{
				AST __t1345 = _t;
				AST tmp19_AST_in = (AST)_t;
				match(_t,DIV);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1345;
				_t = _t.getNextSibling();
				break;
			}
			case MOD:
			{
				AST __t1346 = _t;
				AST tmp20_AST_in = (AST)_t;
				match(_t,MOD);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1346;
				_t = _t.getNextSibling();
				break;
			}
			case POW:
			{
				AST __t1347 = _t;
				AST tmp21_AST_in = (AST)_t;
				match(_t,POW);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t1347;
				_t = _t.getNextSibling();
				break;
			}
			case MENOS_UNARIO:
			{
				AST __t1348 = _t;
				AST tmp22_AST_in = (AST)_t;
				match(_t,MENOS_UNARIO);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				_t = __t1348;
				_t = _t.getNextSibling();
				break;
			}
			case LIT_REAL:
			case LIT_ENTERO:
			case LIT_STRING:
			{
				valor(_t);
				_t = _retTree;
				break;
			}
			case IDENT:
			{
				AST tmp23_AST_in = (AST)_t;
				match(_t,IDENT);
				_t = _t.getNextSibling();
				break;
			}
			case ATRIBUTO:
			{
				id_att(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void valor(AST _t) throws RecognitionException {
		
		AST valor_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LIT_ENTERO:
			{
				AST tmp24_AST_in = (AST)_t;
				match(_t,LIT_ENTERO);
				_t = _t.getNextSibling();
				break;
			}
			case LIT_REAL:
			{
				AST tmp25_AST_in = (AST)_t;
				match(_t,LIT_REAL);
				_t = _t.getNextSibling();
				break;
			}
			case LIT_STRING:
			{
				AST tmp26_AST_in = (AST)_t;
				match(_t,LIT_STRING);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void id_att(AST _t) throws RecognitionException {
		
		AST id_att_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t1350 = _t;
			AST tmp27_AST_in = (AST)_t;
			match(_t,ATRIBUTO);
			_t = _t.getFirstChild();
			AST tmp28_AST_in = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			AST tmp29_AST_in = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			_t = __t1350;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"FEATURE_MODEL",
		"FEATURE",
		"FEATURES",
		"CONSTRAINTS",
		"CONSTRAINT",
		"DOMINIO",
		"DEF_VALUE",
		"NULL_VALUE",
		"ATRIBUTOS",
		"ATRIBUTO",
		"RELACION",
		"CARDINALIDAD",
		"RELACIONES",
		"INVARIANTES",
		"INVARIANTE",
		"RANGO",
		"LITERAL",
		"RANGOS",
		"VALORES",
		"ENUM",
		"MENOS_UNARIO",
		"CONFIGURACION",
		"SECCION_CONFIGURACION",
		"PyC",
		"IFF",
		"IMPLIES",
		"OR",
		"AND",
		"NOT",
		"MAYOR",
		"MENOR",
		"MAYOR_IGUAL",
		"MENOR_IGUAL",
		"IGUAL",
		"DISTINTO",
		"MAS",
		"MENOS",
		"MULT",
		"DIV",
		"MOD",
		"POW",
		"ABS",
		"SIN",
		"COS",
		"PARENTESIS_ABRIR",
		"PARENTESIS_CERRAR",
		"MAX",
		"MIN",
		"SUM",
		"COMA",
		"IDENT",
		"LIT_REAL",
		"LIT_ENTERO",
		"LIT_STRING",
		"PUNTO",
		"EXCLUDES",
		"REQUIRES"
	};
	
	}
	
