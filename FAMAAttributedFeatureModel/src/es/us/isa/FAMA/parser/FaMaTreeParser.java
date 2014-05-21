// $ANTLR : "TreeParser.g" -> "FaMaTreeParser.java"$

	package es.us.isa.FAMA.parser;    
	import java.util.*;	
	import es.us.isa.FAMA.Exceptions.*;
	import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.*;
	import es.us.isa.FAMA.models.featureModel.*;
	import es.us.isa.FAMA.models.featureModel.extended.*;
	import es.us.isa.util.*;
	import es.us.isa.FAMA.models.domain.*;
	import es.us.isa.FAMA.models.domain.RealDomain;
	import es.us.isa.FAMA.parser.*;

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
	
	FMFParser fmp = new FMFParser();
	
	Collection<String> errors = new LinkedList<String>();
	
	//Creamos una feature con su nombre y dominio
	public AttributedFeature createFeature(AST name, Domain d){
		String n = name.getText();
		if (features.get(n) != null){
			//ya existe la feauture, asi que esta duplicada
			errors.add("Duplicated feature detected: "+n);	
		}
		AttributedFeature f = new AttributedFeature(name.getText());
		f.setDomain(d);
		features.put(name.getText(),f);
		return f;
	}
	
	public Relation createRelation(AST relName, AST card, Collection<AttributedFeature> children){
		//TODO en teoria debe funcionar
		Relation res = new Relation(relName.getText());
		Cardinality c = getCardinality(card);
		res.addCardinality(c);
		Iterator<AttributedFeature> it = children.iterator();
		while (it.hasNext()){
			AttributedFeature f = it.next();
			res.addDestination(f);	
		}
		return res;	
	}
	
	//aadimos a una feature todas sus relaciones
	public void addRelations(AttributedFeature f, Collection<Relation> rels){
		Iterator<Relation> it = rels.iterator();
		while (it.hasNext()){
			Relation r = it.next();
			f.addRelation(r);	
			//al aadir la relacion, a esta se le pone como feature padre la feature actual
		}	
	}
	
	public Cardinality getCardinality(AST t){
		String aux = t.getFirstChild().getText();
		int min = Integer.parseInt(aux);
		aux = t.getFirstChild().getNextSibling().getText();
		int max = Integer.parseInt(aux);
		Cardinality res = new Cardinality(min,max);
		return res;	
	}
	
	public TreeParserResult createFeatureModel(AttributedFeature root, Collection<Constraint> cons){
		TreeParserResult res;
		FAMAAttributedFeatureModel fm = new FAMAAttributedFeatureModel(root);
		Iterator<Constraint> it = cons.iterator();
		//TODO
		//aadir metodos a feature model para poder aadir todas las dependencias
		//del tiron, y lo mismo para las relaciones y las features
		while (it.hasNext()){
			Constraint d = it.next();
			fm.addConstraint(d);	
		}
		res = new TreeParserResult(fm,errors);
		return res;
	}
	
	public ExcludesDependency createExcludes(AST relName,AST f1, AST f2){
		//TODO
		AttributedFeature feat1 = features.get(f1.getText());
		AttributedFeature feat2 = features.get(f2.getText());
		ExcludesDependency res = new ExcludesDependency(relName.getText(),feat1,feat2);
		return res;	
	}
	
	public RequiresDependency createRequires(AST relName,AST f1, AST f2){
		//TODO
		AttributedFeature feat1 = features.get(f1.getText());
		AttributedFeature feat2 = features.get(f2.getText());
		RequiresDependency res = new RequiresDependency(relName.getText(),feat1,feat2);
		return res;	
	}
	
	public Constraint ASTtoConstraint(AST t, AST name){
		//TODO checkear que funciona bien
		String n = name.getText();
		Tree<String> tree = fmp.astToTree(t);
		Constraint res;
		if (t.getType() == EXCLUDES){
			AST f1 = t.getFirstChild();
			AST f2 = t.getFirstChild().getNextSibling();
			res = createExcludes(name,f1,f2);
		}
		else if (t.getType() == REQUIRES){
			AST f1 = t.getFirstChild();
			AST f2 = t.getFirstChild().getNextSibling();
			res = createRequires(name,f1,f2);
		}
		else{
			res = new ComplexConstraint(tree);
			res.setName(n);
		}
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

	public final TreeParserResult  entrada(AST _t) throws RecognitionException {
		TreeParserResult res = null;;
		
		AST entrada_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AttributedFeature root; Collection<Constraint> cons = new LinkedList<Constraint>();
		
		try {      // for error handling
			AST __t244 = _t;
			AST tmp1_AST_in = (AST)_t;
			match(_t,FEATURE_MODEL);
			_t = _t.getFirstChild();
			root=seccion_rels(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CONSTRAINTS:
			{
				cons=seccion_cons(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t244;
			_t = _t.getNextSibling();
			res = createFeatureModel(root,cons);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return res;
	}
	
	public final AttributedFeature  seccion_rels(AST _t) throws RecognitionException {
		AttributedFeature root = null;;
		
		AST seccion_rels_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t247 = _t;
			AST tmp2_AST_in = (AST)_t;
			match(_t,SECCION_RELACIONES);
			_t = _t.getFirstChild();
			root=feature(_t);
			_t = _retTree;
			_t = __t247;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return root;
	}
	
	public final Collection<Constraint>  seccion_cons(AST _t) throws RecognitionException {
		Collection<Constraint> res = new LinkedList<Constraint>();;
		
		AST seccion_cons_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Constraint aux;
		
		try {      // for error handling
			AST __t328 = _t;
			AST tmp3_AST_in = (AST)_t;
			match(_t,CONSTRAINTS);
			_t = _t.getFirstChild();
			{
			_loop330:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CONSTRAINT)) {
					aux=constraint(_t);
					_t = _retTree;
					res.add(aux);
				}
				else {
					break _loop330;
				}
				
			} while (true);
			}
			_t = __t328;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return res;
	}
	
	public final AttributedFeature  feature(AST _t) throws RecognitionException {
		AttributedFeature feat = null;;
		
		AST feature_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST f = null;
		Collection<Relation> rels;Collection<GenericAttribute> atts;
		Collection<Constraint> invs;Domain d;
		
		try {      // for error handling
			AST __t249 = _t;
			AST tmp4_AST_in = (AST)_t;
			match(_t,FEATURE);
			_t = _t.getFirstChild();
			f = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			d=dom(_t);
			_t = _retTree;
			feat = createFeature(f,d);
			atts=atributos(_t);
			_t = _retTree;
			addAttributes(feat,atts);
			rels=relaciones(_t);
			_t = _retTree;
			addRelations(feat,rels);
			invs=invariantes(_t);
			_t = _retTree;
			addInvariants(feat,invs);
			_t = __t249;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return feat;
	}
	
	public final RangeIntegerDomain  dom(AST _t) throws RecognitionException {
		RangeIntegerDomain d = new RangeIntegerDomain();;
		
		AST dom_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST min = null;
		AST max = null;
		Range r;
		
		try {      // for error handling
			AST __t251 = _t;
			AST tmp5_AST_in = (AST)_t;
			match(_t,DOMINIO);
			_t = _t.getFirstChild();
			min = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			max = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			r = createRange(min,max);
				d.addRange(r);
			_t = __t251;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return d;
	}
	
	public final Collection<GenericAttribute>  atributos(AST _t) throws RecognitionException {
		Collection<GenericAttribute> atts = new LinkedList<GenericAttribute>();
		
		AST atributos_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		GenericAttribute aux;
		
		try {      // for error handling
			AST __t253 = _t;
			AST tmp6_AST_in = (AST)_t;
			match(_t,ATRIBUTOS);
			_t = _t.getFirstChild();
			{
			_loop255:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==ATRIBUTO)) {
					aux=atributo(_t);
					_t = _retTree;
					atts.add(aux);
				}
				else {
					break _loop255;
				}
				
			} while (true);
			}
			_t = __t253;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return atts;
	}
	
	public final Collection<Relation>  relaciones(AST _t) throws RecognitionException {
		Collection<Relation> rels = new LinkedList<Relation>();;
		
		AST relaciones_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Relation aux;
		
		try {      // for error handling
			AST __t287 = _t;
			AST tmp7_AST_in = (AST)_t;
			match(_t,RELACIONES);
			_t = _t.getFirstChild();
			{
			_loop289:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==RELACION)) {
					aux=relacion(_t);
					_t = _retTree;
					rels.add(aux);
				}
				else {
					break _loop289;
				}
				
			} while (true);
			}
			_t = __t287;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return rels;
	}
	
	public final Collection<Constraint>  invariantes(AST _t) throws RecognitionException {
		Collection<Constraint> invs = new LinkedList<Constraint>();;
		
		AST invariantes_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Constraint aux;
		
		try {      // for error handling
			AST __t293 = _t;
			AST tmp8_AST_in = (AST)_t;
			match(_t,INVARIANTES);
			_t = _t.getFirstChild();
			{
			_loop295:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CONSTRAINT)) {
					aux=constraint(_t);
					_t = _retTree;
					invs.add(aux);
				}
				else {
					break _loop295;
				}
				
			} while (true);
			}
			_t = __t293;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return invs;
	}
	
	public final GenericAttribute  atributo(AST _t) throws RecognitionException {
		GenericAttribute att = null;;
		
		AST atributo_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST n = null;
		Domain d;Object defVal, nullVal;
		
		try {      // for error handling
			AST __t257 = _t;
			AST tmp9_AST_in = (AST)_t;
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
			_t = __t257;
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
			AST __t259 = _t;
			AST tmp10_AST_in = (AST)_t;
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
			_t = __t259;
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
			AST __t281 = _t;
			AST tmp11_AST_in = (AST)_t;
			match(_t,DEF_VALUE);
			_t = _t.getFirstChild();
			o=valor(_t);
			_t = _retTree;
			_t = __t281;
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
			AST __t283 = _t;
			AST tmp12_AST_in = (AST)_t;
			match(_t,NULL_VALUE);
			_t = _t.getFirstChild();
			o=valor(_t);
			_t = _retTree;
			_t = __t283;
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
			AST __t262 = _t;
			AST tmp13_AST_in = (AST)_t;
			match(_t,INTEGER);
			_t = _t.getFirstChild();
			ranges=rangos(_t);
			_t = _retTree;
			_t = __t262;
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
			AST __t270 = _t;
			AST tmp14_AST_in = (AST)_t;
			match(_t,ENUM);
			_t = _t.getFirstChild();
			AST __t271 = _t;
			AST tmp15_AST_in = (AST)_t;
			match(_t,VALORES);
			_t = _t.getFirstChild();
			{
			int _cnt273=0;
			_loop273:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==LIT_ENTERO||_t.getType()==LIT_REAL||_t.getType()==LIT_STRING)) {
					aux=valor(_t);
					_t = _retTree;
					c.add(aux);
				}
				else {
					if ( _cnt273>=1 ) { break _loop273; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt273++;
			} while (true);
			}
			_t = __t271;
			_t = _t.getNextSibling();
			_t = __t270;
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
			AST __t275 = _t;
			AST tmp16_AST_in = (AST)_t;
			match(_t,REAL);
			_t = _t.getFirstChild();
			d=rangoReal(_t);
			_t = _retTree;
			_t = __t275;
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
			AST __t264 = _t;
			AST tmp17_AST_in = (AST)_t;
			match(_t,RANGOS);
			_t = _t.getFirstChild();
			{
			int _cnt266=0;
			_loop266:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==RANGO)) {
					aux=rango(_t);
					_t = _retTree;
					ranges.add(aux);
				}
				else {
					if ( _cnt266>=1 ) { break _loop266; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt266++;
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
		return ranges;
	}
	
	public final Range  rango(AST _t) throws RecognitionException {
		Range r = null;;
		
		AST rango_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST min = null;
		AST max = null;
		
		try {      // for error handling
			AST __t268 = _t;
			AST tmp18_AST_in = (AST)_t;
			match(_t,RANGO);
			_t = _t.getFirstChild();
			min = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			max = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			_t = __t268;
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
			AST __t277 = _t;
			AST tmp19_AST_in = (AST)_t;
			match(_t,RANGO);
			_t = _t.getFirstChild();
			min = _t==ASTNULL ? null : (AST)_t;
			val_lit(_t);
			_t = _retTree;
			max = _t==ASTNULL ? null : (AST)_t;
			val_lit(_t);
			_t = _retTree;
			_t = __t277;
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
				AST tmp20_AST_in = (AST)_t;
				match(_t,LIT_REAL);
				_t = _t.getNextSibling();
				break;
			}
			case LIT_ENTERO:
			{
				AST tmp21_AST_in = (AST)_t;
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
	
	public final Relation  relacion(AST _t) throws RecognitionException {
		Relation r = null;;
		
		AST relacion_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST n = null;
		AST c = null;
		Collection<AttributedFeature> children;
		
		try {      // for error handling
			AST __t291 = _t;
			AST tmp22_AST_in = (AST)_t;
			match(_t,RELACION);
			_t = _t.getFirstChild();
			n = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			c = _t==ASTNULL ? null : (AST)_t;
			card(_t);
			_t = _retTree;
			children=features(_t);
			_t = _retTree;
			_t = __t291;
			_t = _t.getNextSibling();
			r = createRelation(n,c,children);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return r;
	}
	
	public final void card(AST _t) throws RecognitionException {
		
		AST card_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t326 = _t;
			AST tmp23_AST_in = (AST)_t;
			match(_t,CARDINALIDAD);
			_t = _t.getFirstChild();
			AST tmp24_AST_in = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			AST tmp25_AST_in = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			_t = __t326;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final Collection<AttributedFeature>  features(AST _t) throws RecognitionException {
		Collection<AttributedFeature> feats = new LinkedList<AttributedFeature>();;
		
		AST features_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AttributedFeature aux;
		
		try {      // for error handling
			AST __t322 = _t;
			AST tmp26_AST_in = (AST)_t;
			match(_t,FEATURES);
			_t = _t.getFirstChild();
			{
			_loop324:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==FEATURE)) {
					aux=feature(_t);
					_t = _retTree;
					feats.add(aux);
				}
				else {
					break _loop324;
				}
				
			} while (true);
			}
			_t = __t322;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return feats;
	}
	
	public final Constraint  constraint(AST _t) throws RecognitionException {
		Constraint c = null;;
		
		AST constraint_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST n = null;
		AST e = null;
		
		try {      // for error handling
			AST __t297 = _t;
			AST tmp27_AST_in = (AST)_t;
			match(_t,CONSTRAINT);
			_t = _t.getFirstChild();
			n = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			e = _t==ASTNULL ? null : (AST)_t;
			expresion(_t);
			_t = _retTree;
			c = ASTtoConstraint(e,n);
			_t = __t297;
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
				AST __t299 = _t;
				AST tmp28_AST_in = (AST)_t;
				match(_t,IFF);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t299;
				_t = _t.getNextSibling();
				break;
			}
			case IMPLIES:
			{
				AST __t300 = _t;
				AST tmp29_AST_in = (AST)_t;
				match(_t,IMPLIES);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t300;
				_t = _t.getNextSibling();
				break;
			}
			case EXCLUDES:
			{
				AST __t301 = _t;
				AST tmp30_AST_in = (AST)_t;
				match(_t,EXCLUDES);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t301;
				_t = _t.getNextSibling();
				break;
			}
			case REQUIRES:
			{
				AST __t302 = _t;
				AST tmp31_AST_in = (AST)_t;
				match(_t,REQUIRES);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t302;
				_t = _t.getNextSibling();
				break;
			}
			case OR:
			{
				AST __t303 = _t;
				AST tmp32_AST_in = (AST)_t;
				match(_t,OR);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t303;
				_t = _t.getNextSibling();
				break;
			}
			case AND:
			{
				AST __t304 = _t;
				AST tmp33_AST_in = (AST)_t;
				match(_t,AND);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t304;
				_t = _t.getNextSibling();
				break;
			}
			case NOT:
			{
				AST __t305 = _t;
				AST tmp34_AST_in = (AST)_t;
				match(_t,NOT);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				_t = __t305;
				_t = _t.getNextSibling();
				break;
			}
			case MAYOR:
			{
				AST __t306 = _t;
				AST tmp35_AST_in = (AST)_t;
				match(_t,MAYOR);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t306;
				_t = _t.getNextSibling();
				break;
			}
			case MENOR:
			{
				AST __t307 = _t;
				AST tmp36_AST_in = (AST)_t;
				match(_t,MENOR);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t307;
				_t = _t.getNextSibling();
				break;
			}
			case MAYOR_IGUAL:
			{
				AST __t308 = _t;
				AST tmp37_AST_in = (AST)_t;
				match(_t,MAYOR_IGUAL);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t308;
				_t = _t.getNextSibling();
				break;
			}
			case MENOR_IGUAL:
			{
				AST __t309 = _t;
				AST tmp38_AST_in = (AST)_t;
				match(_t,MENOR_IGUAL);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t309;
				_t = _t.getNextSibling();
				break;
			}
			case IGUAL:
			{
				AST __t310 = _t;
				AST tmp39_AST_in = (AST)_t;
				match(_t,IGUAL);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t310;
				_t = _t.getNextSibling();
				break;
			}
			case DISTINTO:
			{
				AST __t311 = _t;
				AST tmp40_AST_in = (AST)_t;
				match(_t,DISTINTO);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t311;
				_t = _t.getNextSibling();
				break;
			}
			case MAS:
			{
				AST __t312 = _t;
				AST tmp41_AST_in = (AST)_t;
				match(_t,MAS);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t312;
				_t = _t.getNextSibling();
				break;
			}
			case MENOS:
			{
				AST __t313 = _t;
				AST tmp42_AST_in = (AST)_t;
				match(_t,MENOS);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t313;
				_t = _t.getNextSibling();
				break;
			}
			case MULT:
			{
				AST __t314 = _t;
				AST tmp43_AST_in = (AST)_t;
				match(_t,MULT);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t314;
				_t = _t.getNextSibling();
				break;
			}
			case DIV:
			{
				AST __t315 = _t;
				AST tmp44_AST_in = (AST)_t;
				match(_t,DIV);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t315;
				_t = _t.getNextSibling();
				break;
			}
			case MOD:
			{
				AST __t316 = _t;
				AST tmp45_AST_in = (AST)_t;
				match(_t,MOD);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t316;
				_t = _t.getNextSibling();
				break;
			}
			case POW:
			{
				AST __t317 = _t;
				AST tmp46_AST_in = (AST)_t;
				match(_t,POW);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t317;
				_t = _t.getNextSibling();
				break;
			}
			case MENOS_UNARIO:
			{
				AST __t318 = _t;
				AST tmp47_AST_in = (AST)_t;
				match(_t,MENOS_UNARIO);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				_t = __t318;
				_t = _t.getNextSibling();
				break;
			}
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
				AST tmp48_AST_in = (AST)_t;
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
	
	public final void id_att(AST _t) throws RecognitionException {
		
		AST id_att_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t320 = _t;
			AST tmp49_AST_in = (AST)_t;
			match(_t,ATRIBUTO);
			_t = _t.getFirstChild();
			AST tmp50_AST_in = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			AST tmp51_AST_in = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			_t = __t320;
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
		"SECCION_RELACIONES",
		"DOSPUNTOS",
		"PyC",
		"IDENT",
		"CORCHETE_ABRIR",
		"CORCHETE_CERRAR",
		"LIT_ENTERO",
		"COMA",
		"LLAVE_ABRIR",
		"LLAVE_CERRAR",
		"SECCION_ATRIBUTOS",
		"PUNTO",
		"INTEGER",
		"REAL",
		"TO",
		"LIT_REAL",
		"LIT_STRING",
		"SECCION_CONSTRAINTS",
		"EXCLUDES",
		"REQUIRES",
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
		"SUM"
	};
	
	}
	
