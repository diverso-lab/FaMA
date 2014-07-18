// $ANTLR : "TreeParser.g" -> "FaMaTreeParser.java"$

	package es.us.isa.FAMA.order.parser;    
	import java.util.*;	
	import es.us.isa.FAMA.Exceptions.*;
	import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.*;
	import es.us.isa.FAMA.models.featureModel.*;
	import es.us.isa.FAMA.models.featureModel.extended.*;
	import es.us.isa.util.*;
	import es.us.isa.FAMA.models.domain.*;
	import es.us.isa.FAMA.models.domain.RealDomain;
	import es.us.isa.FAMA.order.parser.*;
	import es.us.isa.FAMA.order.*;

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


public class FaMaTreeParser extends antlr.TreeParser       implements FaMaTreeParserTokenTypes
 {

	//Map<String,AST> mapASTFeatures = null;

	Map<String,AttributedFeature> features = new HashMap<String,AttributedFeature>();
	//zona de funciones	
	
	OrderModelParser omp = new OrderModelParser();
	
	Collection<String> errors = new LinkedList<String>();


	
	public OrderModelParserResult createOrderModel(Collection<GenericAttribute> atts, Collection<Constraint> cons){
		OrderModelParserResult res;
		OrderModel om = new OrderModel();
		Iterator<Constraint> itCons = cons.iterator();

		for (Constraint c:cons){
			om.addConstraint(c);
		}
		
		for(GenericAttribute a:atts){
			om.addProperty(a);
		}
		res = new OrderModelParserResult(om,errors);
		return res;
	}
	
	
	public Constraint ASTtoConstraint(AST t, AST name){
		String n = name.getText();
		Tree<String> tree = omp.astToTree(t);
		Constraint res = new ComplexConstraint(tree);
		res.setName(n);
		return res;	
	}
	
	public Domain createEnumeratedDomain(Collection<Object> c){
		Domain d;
		Iterator<Object> it = c.iterator();
		if (it.hasNext()){
			Object aux = it.next();
			if (aux instanceof Integer){
				SetIntegerDomain auxDomain = new SetIntegerDomain();
				d = new SetIntegerDomain();
				Integer i = (Integer)aux;
				auxDomain.addValue(i);
				while (it.hasNext()){
					aux = it.next();
					if (aux instanceof Integer){
						i = (Integer)aux;
						auxDomain.addValue(i);
					}
					else{
						throw new FAMAException("Different types on the attribute domain");	
					}
				}
				d = auxDomain;
			}
			else{
				ObjectDomain auxDomain = new ObjectDomain();
				//d = new ObjectDomain();
				auxDomain.addValue(aux);
				while (it.hasNext()){
					aux = it.next();
					auxDomain.addValue(aux);
				}
				d = auxDomain;
			}
		}
		else{
			d = new SetIntegerDomain();
		}
		return d;
	}
	
	public Range createRange(AST min, AST max){
		int minimo = Integer.parseInt(min.getText());
		int maximo = Integer.parseInt(max.getText());
		Range res = new Range (minimo,maximo);
		return res;	
	}
	
	public Integer astToInteger(AST t){
		Integer res = new Integer(t.getText());
		return res;	
	}
	
	public Float astToFloat(AST t){
		Float res = new Float(t.getText());
		return res;	
	}
	
	public void addAttributes(AttributedFeature f, Collection<GenericAttribute> atts){
		f.addAttributes(atts);
	}
	
	public void addInvariants(AttributedFeature f, Collection<Constraint> cons){
		f.addUncheckedInvariants(cons);	
	}
	
	public RealDomain createRealDomain(AST min, AST max){
		double minimo = Double.parseDouble(min.getText());
		double maximo = Double.parseDouble(max.getText());
		RealDomain res = new RealDomain(minimo,maximo);
		return res;	
	}
public FaMaTreeParser() {
	tokenNames = _tokenNames;
}

	public final OrderModelParserResult  entrada(AST _t) throws RecognitionException {
		OrderModelParserResult res = null;;
		
		AST entrada_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Collection<GenericAttribute> props = new LinkedList<GenericAttribute>(); 
			Collection<Constraint> cons = new LinkedList<Constraint>();
		
		try {      // for error handling
			AST __t262 = _t;
			AST tmp1_AST_in = (AST)_t;
			match(_t,ORDER_MODEL);
			_t = _t.getFirstChild();
			props=seccion_atts(_t);
			_t = _retTree;
			cons=seccion_cons(_t);
			_t = _retTree;
			_t = __t262;
			_t = _t.getNextSibling();
			res = createOrderModel(props,cons);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return res;
	}
	
	public final Collection<GenericAttribute>  seccion_atts(AST _t) throws RecognitionException {
		Collection<GenericAttribute> atts = new LinkedList<GenericAttribute>();;
		
		AST seccion_atts_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		GenericAttribute aux;
		
		try {      // for error handling
			AST __t264 = _t;
			AST tmp2_AST_in = (AST)_t;
			match(_t,ATRIBUTOS);
			_t = _t.getFirstChild();
			{
			_loop266:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==ATRIBUTO)) {
					aux=atributo(_t);
					_t = _retTree;
					atts.add(aux);
				}
				else {
					break _loop266;
				}
				
			} while (true);
			}
			_t = __t264;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return atts;
	}
	
	public final Collection<Constraint>  seccion_cons(AST _t) throws RecognitionException {
		Collection<Constraint> res = new LinkedList<Constraint>();;
		
		AST seccion_cons_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Constraint aux;
		
		try {      // for error handling
			AST __t327 = _t;
			AST tmp3_AST_in = (AST)_t;
			match(_t,CONSTRAINTS);
			_t = _t.getFirstChild();
			{
			_loop329:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CONSTRAINT)) {
					aux=constraint(_t);
					_t = _retTree;
					res.add(aux);
				}
				else {
					break _loop329;
				}
				
			} while (true);
			}
			_t = __t327;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return res;
	}
	
	public final GenericAttribute  atributo(AST _t) throws RecognitionException {
		GenericAttribute att = null;;
		
		AST atributo_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST n = null;
		Domain d;Object defVal, nullVal;
		
		try {      // for error handling
			AST __t268 = _t;
			AST tmp4_AST_in = (AST)_t;
			match(_t,ATRIBUTO);
			_t = _t.getFirstChild();
			n = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			d=dominio_att(_t);
			_t = _retTree;
			defVal=default_value(_t);
			_t = _retTree;
			nullVal=null_value(_t);
			_t = _retTree;
			_t = __t268;
			_t = _t.getNextSibling();
			att = new GenericAttribute(n.getText(),d,nullVal,defVal);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return att;
	}
	
	public final Domain  dominio_att(AST _t) throws RecognitionException {
		Domain d = null;;
		
		AST dominio_att_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t270 = _t;
			AST tmp5_AST_in = (AST)_t;
			match(_t,DOMINIO);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case INTEGER:
			{
				d=dominio_rango(_t);
				_t = _retTree;
				break;
			}
			case ENUM:
			{
				d=dominio_enumerado(_t);
				_t = _retTree;
				break;
			}
			case REAL:
			{
				d=dominio_real(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t270;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return d;
	}
	
	public final Object  default_value(AST _t) throws RecognitionException {
		Object o = null;;
		
		AST default_value_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t292 = _t;
			AST tmp6_AST_in = (AST)_t;
			match(_t,DEF_VALUE);
			_t = _t.getFirstChild();
			o=valor(_t);
			_t = _retTree;
			_t = __t292;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return o;
	}
	
	public final Object  null_value(AST _t) throws RecognitionException {
		Object o = null;;
		
		AST null_value_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t294 = _t;
			AST tmp7_AST_in = (AST)_t;
			match(_t,NULL_VALUE);
			_t = _t.getFirstChild();
			o=valor(_t);
			_t = _retTree;
			_t = __t294;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return o;
	}
	
	public final Domain  dominio_rango(AST _t) throws RecognitionException {
		Domain d = null;
		
		AST dominio_rango_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Collection<Range> ranges;
		
		try {      // for error handling
			AST __t273 = _t;
			AST tmp8_AST_in = (AST)_t;
			match(_t,INTEGER);
			_t = _t.getFirstChild();
			ranges=rangos(_t);
			_t = _retTree;
			_t = __t273;
			_t = _t.getNextSibling();
			d = new RangeIntegerDomain(ranges);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return d;
	}
	
	public final Domain  dominio_enumerado(AST _t) throws RecognitionException {
		Domain d = null;;
		
		AST dominio_enumerado_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Collection<Object> c = new LinkedList<Object>();Object aux;
		
		try {      // for error handling
			AST __t281 = _t;
			AST tmp9_AST_in = (AST)_t;
			match(_t,ENUM);
			_t = _t.getFirstChild();
			AST __t282 = _t;
			AST tmp10_AST_in = (AST)_t;
			match(_t,VALORES);
			_t = _t.getFirstChild();
			{
			int _cnt284=0;
			_loop284:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_0.member(_t.getType()))) {
					aux=valor(_t);
					_t = _retTree;
					c.add(aux);
				}
				else {
					if ( _cnt284>=1 ) { break _loop284; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt284++;
			} while (true);
			}
			_t = __t282;
			_t = _t.getNextSibling();
			_t = __t281;
			_t = _t.getNextSibling();
			d = createEnumeratedDomain(c);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return d;
	}
	
	public final Domain  dominio_real(AST _t) throws RecognitionException {
		Domain d = null;;
		
		AST dominio_real_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t286 = _t;
			AST tmp11_AST_in = (AST)_t;
			match(_t,REAL);
			_t = _t.getFirstChild();
			d=rangoReal(_t);
			_t = _retTree;
			_t = __t286;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return d;
	}
	
	public final Collection<Range>  rangos(AST _t) throws RecognitionException {
		Collection<Range> ranges = new HashSet<Range>();;
		
		AST rangos_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Range aux = null;
		
		try {      // for error handling
			AST __t275 = _t;
			AST tmp12_AST_in = (AST)_t;
			match(_t,RANGOS);
			_t = _t.getFirstChild();
			{
			int _cnt277=0;
			_loop277:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==RANGO)) {
					aux=rango(_t);
					_t = _retTree;
					ranges.add(aux);
				}
				else {
					if ( _cnt277>=1 ) { break _loop277; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt277++;
			} while (true);
			}
			_t = __t275;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return ranges;
	}
	
	public final Range  rango(AST _t) throws RecognitionException {
		Range r = null;;
		
		AST rango_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST min = null;
		AST max = null;
		
		try {      // for error handling
			AST __t279 = _t;
			AST tmp13_AST_in = (AST)_t;
			match(_t,RANGO);
			_t = _t.getFirstChild();
			min = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			max = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			_t = __t279;
			_t = _t.getNextSibling();
			r = createRange(min,max);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return r;
	}
	
	public final Object  valor(AST _t) throws RecognitionException {
		Object o = null;;
		
		AST valor_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST i = null;
		AST r = null;
		AST s = null;
		AST b1 = null;
		AST b2 = null;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LIT_ENTERO:
			{
				i = (AST)_t;
				match(_t,LIT_ENTERO);
				_t = _t.getNextSibling();
				o = astToInteger(i);
				break;
			}
			case LIT_REAL:
			{
				r = (AST)_t;
				match(_t,LIT_REAL);
				_t = _t.getNextSibling();
				o = astToFloat(r);
				break;
			}
			case LIT_STRING:
			{
				s = (AST)_t;
				match(_t,LIT_STRING);
				_t = _t.getNextSibling();
				o = s.getText();
				break;
			}
			case TRUE_VALUE:
			{
				b1 = (AST)_t;
				match(_t,TRUE_VALUE);
				_t = _t.getNextSibling();
				o = b1.getText();
				break;
			}
			case FALSE_VALUE:
			{
				b2 = (AST)_t;
				match(_t,FALSE_VALUE);
				_t = _t.getNextSibling();
				o = b2.getText();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return o;
	}
	
	public final Domain  rangoReal(AST _t) throws RecognitionException {
		Domain d = null;;
		
		AST rangoReal_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST min = null;
		AST max = null;
		
		try {      // for error handling
			AST __t288 = _t;
			AST tmp14_AST_in = (AST)_t;
			match(_t,RANGO);
			_t = _t.getFirstChild();
			min = _t==ASTNULL ? null : (AST)_t;
			val_lit(_t);
			_t = _retTree;
			max = _t==ASTNULL ? null : (AST)_t;
			val_lit(_t);
			_t = _retTree;
			_t = __t288;
			_t = _t.getNextSibling();
			d = createRealDomain(min,max);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return d;
	}
	
	public final void val_lit(AST _t) throws RecognitionException {
		
		AST val_lit_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LIT_REAL:
			{
				AST tmp15_AST_in = (AST)_t;
				match(_t,LIT_REAL);
				_t = _t.getNextSibling();
				break;
			}
			case LIT_ENTERO:
			{
				AST tmp16_AST_in = (AST)_t;
				match(_t,LIT_ENTERO);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final Constraint  constraint(AST _t) throws RecognitionException {
		Constraint c = null;;
		
		AST constraint_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST n = null;
		AST e = null;
		
		try {      // for error handling
			AST __t298 = _t;
			AST tmp17_AST_in = (AST)_t;
			match(_t,CONSTRAINT);
			_t = _t.getFirstChild();
			n = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			e = _t==ASTNULL ? null : (AST)_t;
			expresion(_t);
			_t = _retTree;
			c = ASTtoConstraint(e,n);
			_t = __t298;
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
				AST __t300 = _t;
				AST tmp18_AST_in = (AST)_t;
				match(_t,IFF);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t300;
				_t = _t.getNextSibling();
				break;
			}
			case IMPLIES:
			{
				AST __t301 = _t;
				AST tmp19_AST_in = (AST)_t;
				match(_t,IMPLIES);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t301;
				_t = _t.getNextSibling();
				break;
			}
			case EXCLUDES:
			{
				AST __t302 = _t;
				AST tmp20_AST_in = (AST)_t;
				match(_t,EXCLUDES);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t302;
				_t = _t.getNextSibling();
				break;
			}
			case REQUIRES:
			{
				AST __t303 = _t;
				AST tmp21_AST_in = (AST)_t;
				match(_t,REQUIRES);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t303;
				_t = _t.getNextSibling();
				break;
			}
			case OR:
			{
				AST __t304 = _t;
				AST tmp22_AST_in = (AST)_t;
				match(_t,OR);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t304;
				_t = _t.getNextSibling();
				break;
			}
			case AND:
			{
				AST __t305 = _t;
				AST tmp23_AST_in = (AST)_t;
				match(_t,AND);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t305;
				_t = _t.getNextSibling();
				break;
			}
			case NOT:
			{
				AST __t306 = _t;
				AST tmp24_AST_in = (AST)_t;
				match(_t,NOT);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				_t = __t306;
				_t = _t.getNextSibling();
				break;
			}
			case MAYOR:
			{
				AST __t307 = _t;
				AST tmp25_AST_in = (AST)_t;
				match(_t,MAYOR);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t307;
				_t = _t.getNextSibling();
				break;
			}
			case MENOR:
			{
				AST __t308 = _t;
				AST tmp26_AST_in = (AST)_t;
				match(_t,MENOR);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t308;
				_t = _t.getNextSibling();
				break;
			}
			case MAYOR_IGUAL:
			{
				AST __t309 = _t;
				AST tmp27_AST_in = (AST)_t;
				match(_t,MAYOR_IGUAL);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t309;
				_t = _t.getNextSibling();
				break;
			}
			case MENOR_IGUAL:
			{
				AST __t310 = _t;
				AST tmp28_AST_in = (AST)_t;
				match(_t,MENOR_IGUAL);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t310;
				_t = _t.getNextSibling();
				break;
			}
			case IGUAL:
			{
				AST __t311 = _t;
				AST tmp29_AST_in = (AST)_t;
				match(_t,IGUAL);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t311;
				_t = _t.getNextSibling();
				break;
			}
			case DISTINTO:
			{
				AST __t312 = _t;
				AST tmp30_AST_in = (AST)_t;
				match(_t,DISTINTO);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t312;
				_t = _t.getNextSibling();
				break;
			}
			case MAS:
			{
				AST __t313 = _t;
				AST tmp31_AST_in = (AST)_t;
				match(_t,MAS);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t313;
				_t = _t.getNextSibling();
				break;
			}
			case MENOS:
			{
				AST __t314 = _t;
				AST tmp32_AST_in = (AST)_t;
				match(_t,MENOS);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t314;
				_t = _t.getNextSibling();
				break;
			}
			case MULT:
			{
				AST __t315 = _t;
				AST tmp33_AST_in = (AST)_t;
				match(_t,MULT);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t315;
				_t = _t.getNextSibling();
				break;
			}
			case DIV:
			{
				AST __t316 = _t;
				AST tmp34_AST_in = (AST)_t;
				match(_t,DIV);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t316;
				_t = _t.getNextSibling();
				break;
			}
			case MOD:
			{
				AST __t317 = _t;
				AST tmp35_AST_in = (AST)_t;
				match(_t,MOD);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t317;
				_t = _t.getNextSibling();
				break;
			}
			case POW:
			{
				AST __t318 = _t;
				AST tmp36_AST_in = (AST)_t;
				match(_t,POW);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t318;
				_t = _t.getNextSibling();
				break;
			}
			case MENOS_UNARIO:
			{
				AST __t319 = _t;
				AST tmp37_AST_in = (AST)_t;
				match(_t,MENOS_UNARIO);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				_t = __t319;
				_t = _t.getNextSibling();
				break;
			}
			case SUM:
			{
				AST __t320 = _t;
				AST tmp38_AST_in = (AST)_t;
				match(_t,SUM);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				_t = __t320;
				_t = _t.getNextSibling();
				break;
			}
			case TRUE_VALUE:
			case FALSE_VALUE:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_STRING:
			{
				valor(_t);
				_t = _retTree;
				break;
			}
			case IDENT:
			{
				AST tmp39_AST_in = (AST)_t;
				match(_t,IDENT);
				_t = _t.getNextSibling();
				break;
			}
			case ATRIBUTO:
			case CONFIG_ATTRIBUTE:
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
	
	public final void id_att(AST _t) throws RecognitionException {
		
		AST id_att_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ATRIBUTO:
			{
				id_order_att(_t);
				_t = _retTree;
				break;
			}
			case CONFIG_ATTRIBUTE:
			{
				id_config_att(_t);
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
	
	public final void id_order_att(AST _t) throws RecognitionException {
		
		AST id_order_att_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t323 = _t;
			AST tmp40_AST_in = (AST)_t;
			match(_t,ATRIBUTO);
			_t = _t.getFirstChild();
			AST tmp41_AST_in = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			_t = __t323;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void id_config_att(AST _t) throws RecognitionException {
		
		AST id_config_att_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t325 = _t;
			AST tmp42_AST_in = (AST)_t;
			match(_t,CONFIG_ATTRIBUTE);
			_t = _t.getFirstChild();
			AST tmp43_AST_in = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			_t = __t325;
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
		"ORDER_MODEL",
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
		"TRUE_VALUE",
		"FALSE_VALUE",
		"CONFIG_ATTRIBUTE",
		"SECCION_PROPIEDADES",
		"IDENT",
		"DOSPUNTOS",
		"COMA",
		"PyC",
		"INTEGER",
		"REAL",
		"CORCHETE_ABRIR",
		"LIT_ENTERO",
		"TO",
		"CORCHETE_CERRAR",
		"LIT_REAL",
		"LIT_STRING",
		"SECCION_CONSTRAINTS",
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
		"CONFIGURATION",
		"PUNTO",
		"EXCLUDES",
		"REQUIRES"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 1718087581696L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	}
	
