// $ANTLR : "Anasint.g" -> "ConfigAnasint.java"$

	package es.us.isa.FAMA.models.config;       
	import java.util.*;	
	import es.us.isa.FAMA.parser.*;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class ConfigAnasint extends antlr.LLkParser       implements ConfigAnasintTokenTypes
 {



//seran necesarias estructuras de datos, pues en primer lugar
//construimos el modelo sin atributos, y luego se los insertamos

//en el propio anasint realizaremos las resoluciones de nombres
//que sean necesarias (sobre todo en declaraciones de atributos y CTC)

	//map con todas las features declaradas hasta el momento, mapeadas por nombre
	Map<String,AST> mapFeatures = new HashMap<String,AST>();
	
	//map feature (string) -> atributos (collection<atributo>)
	Map<String,Collection<Atributo>> attributes = new HashMap<String,Collection<Atributo>>();
	
	//contador para ir nombrando las relaciones
	int contRels = 1;
	
	//cadena para ir nombrando las relaciones
	String relConst = "decision-";
	
	//errores :D
	Collection<String> errors = new LinkedList<String>();
	
	//flag usado en la definicion de invariantes (un ident dentro de una
	//invariante no es una feature, sino un atributo)
	boolean flag = true;
	
	//al procesar un conjunto de invariantes, feature a la que pertenecen estas
	String currentFeature;
	
	public Collection<String> getSyntaxErrors(){
		return errors;	
	}
	
	public void setFeatures(Map<String,AST> feats){
		mapFeatures = feats;
	}
	
	public void setAttributes(Map<String,Collection<Atributo>> atts){
		attributes = atts;	
	}
	

	public AST nextRelationName(){
		String aux = relConst+contRels;
		AST relName = astFactory.create(IDENT,aux);
		contRels++;
		return relName;
	}
	

	
	public boolean existsFeature(String f){
		return (mapFeatures.get(f) != null);
	}

	
	
	public void resuelveNombre(AST t){
		//t puede ser feature (flag = true) o atributo (flag = false, si se
		//esta definiendo una invariante)
		//en caso de ser atributo, usamos currentFeature (tiene la feature
		//actual que esta definiendo invariantes)
		String s = t.getText();
		int line = t.getLine();
		if (flag){
			AST dec = mapFeatures.get(s);
			if (dec == null){
				//error, feature no declarada
				errors.add("Error at line "+line+": feature "+s+" is not declared");	
			}
		}
		else{
			if (!existsAtt(currentFeature,s)){
				errors.add("Error at line "+line+": attribute "+currentFeature+"."+s+" is not declared");	
			}	
		}
	}
	
	 // Cambiar tambien el tree parser
	
	public AST constraintExpresion(AST e){
		//TODO
		AST name = nextRelationName();
		AST res =  (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(CONSTRAINT,"Constraint")).add(name).add(e));
		return res;
	}

	
	public void resuelveNombreAtributo(AST att){
		String fName = att.getFirstChild().getText();
		String attName = att.getFirstChild().getNextSibling().getText();
		int line = att.getFirstChild().getLine();
		if (mapFeatures.get(fName) == null){
			errors.add("Error at line "+line+": feature "+fName+" is not declared");	
		}
		if (!existsAtt(fName,attName)){
			errors.add("Error at line "+line+": attribute "+fName+"."+attName+" is not declared");	
		}	
	}
	
	public boolean existsAtt(String f, String a){
		Collection<Atributo> atts = attributes.get(f);
		if (atts != null){
			Atributo aux = new Atributo(a,0);//atributo espureo, para simplificar la busqueda
			return atts.contains(aux);	
		}
		return false;
	}
		

protected ConfigAnasint(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public ConfigAnasint(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected ConfigAnasint(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public ConfigAnasint(TokenStream lexer) {
  this(lexer,2);
}

public ConfigAnasint(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final Collection<String>  conjunto_constraints() throws RecognitionException, TokenStreamException {
		Collection<String> result = null;;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conjunto_constraints_AST = null;
		AST cons_AST = null;
		
		try {      // for error handling
			AST tmp1_AST = null;
			tmp1_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp1_AST);
			match(SECCION_CONFIGURACION);
			lista_constraints();
			cons_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			conjunto_constraints_AST = (AST)currentAST.root;
			conjunto_constraints_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CONFIGURACION,"Configuration")).add(cons_AST)); result = errors;
			currentAST.root = conjunto_constraints_AST;
			currentAST.child = conjunto_constraints_AST!=null &&conjunto_constraints_AST.getFirstChild()!=null ?
				conjunto_constraints_AST.getFirstChild() : conjunto_constraints_AST;
			currentAST.advanceChildToEnd();
			conjunto_constraints_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = conjunto_constraints_AST;
		return result;
	}
	
	public final void lista_constraints() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_constraints_AST = null;
		
		try {      // for error handling
			{
			int _cnt1355=0;
			_loop1355:
			do {
				if ((_tokenSet_1.member(LA(1)))) {
					declaracion_expresion();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt1355>=1 ) { break _loop1355; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt1355++;
			} while (true);
			}
			lista_constraints_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = lista_constraints_AST;
	}
	
	public final void declaracion_expresion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_expresion_AST = null;
		AST e_AST = null;
		
		try {      // for error handling
			expresion();
			e_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			match(PyC);
			declaracion_expresion_AST = (AST)currentAST.root;
			declaracion_expresion_AST = constraintExpresion(e_AST);
			currentAST.root = declaracion_expresion_AST;
			currentAST.child = declaracion_expresion_AST!=null &&declaracion_expresion_AST.getFirstChild()!=null ?
				declaracion_expresion_AST.getFirstChild() : declaracion_expresion_AST;
			currentAST.advanceChildToEnd();
			declaracion_expresion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		returnAST = declaracion_expresion_AST;
	}
	
	public final void expresion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_AST = null;
		
		try {      // for error handling
			expresion_nivel_1();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case IFF:
			case IMPLIES:
			{
				{
				switch ( LA(1)) {
				case IFF:
				{
					AST tmp3_AST = null;
					tmp3_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp3_AST);
					match(IFF);
					break;
				}
				case IMPLIES:
				{
					AST tmp4_AST = null;
					tmp4_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp4_AST);
					match(IMPLIES);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				expresion_nivel_1();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case PyC:
			case PARENTESIS_CERRAR:
			case COMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			expresion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = expresion_AST;
	}
	
	public final void expresion_nivel_1() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_1_AST = null;
		
		try {      // for error handling
			expresion_nivel_2();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop1362:
			do {
				if ((LA(1)==OR)) {
					AST tmp5_AST = null;
					tmp5_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp5_AST);
					match(OR);
					expresion_nivel_2();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1362;
				}
				
			} while (true);
			}
			expresion_nivel_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		returnAST = expresion_nivel_1_AST;
	}
	
	public final void expresion_nivel_2() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_2_AST = null;
		
		try {      // for error handling
			expresion_nivel_3();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop1365:
			do {
				if ((LA(1)==AND)) {
					AST tmp6_AST = null;
					tmp6_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp6_AST);
					match(AND);
					expresion_nivel_3();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1365;
				}
				
			} while (true);
			}
			expresion_nivel_2_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		returnAST = expresion_nivel_2_AST;
	}
	
	public final void expresion_nivel_3() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_3_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NOT:
			{
				{
				AST tmp7_AST = null;
				tmp7_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp7_AST);
				match(NOT);
				expresion_nivel_3();
				astFactory.addASTChild(currentAST, returnAST);
				}
				expresion_nivel_3_AST = (AST)currentAST.root;
				break;
			}
			case MENOS:
			case ABS:
			case SIN:
			case COS:
			case PARENTESIS_ABRIR:
			case MAX:
			case MIN:
			case SUM:
			case IDENT:
			case LIT_REAL:
			case LIT_ENTERO:
			case LIT_STRING:
			{
				expresion_nivel_4();
				astFactory.addASTChild(currentAST, returnAST);
				expresion_nivel_3_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		returnAST = expresion_nivel_3_AST;
	}
	
	public final void expresion_nivel_4() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_4_AST = null;
		
		try {      // for error handling
			expresion_nivel_5();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case MAYOR:
			case MENOR:
			case MAYOR_IGUAL:
			case MENOR_IGUAL:
			case IGUAL:
			case DISTINTO:
			{
				{
				switch ( LA(1)) {
				case MAYOR:
				{
					AST tmp8_AST = null;
					tmp8_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp8_AST);
					match(MAYOR);
					break;
				}
				case MENOR:
				{
					AST tmp9_AST = null;
					tmp9_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp9_AST);
					match(MENOR);
					break;
				}
				case MAYOR_IGUAL:
				{
					AST tmp10_AST = null;
					tmp10_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp10_AST);
					match(MAYOR_IGUAL);
					break;
				}
				case MENOR_IGUAL:
				{
					AST tmp11_AST = null;
					tmp11_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp11_AST);
					match(MENOR_IGUAL);
					break;
				}
				case IGUAL:
				{
					AST tmp12_AST = null;
					tmp12_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp12_AST);
					match(IGUAL);
					break;
				}
				case DISTINTO:
				{
					AST tmp13_AST = null;
					tmp13_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp13_AST);
					match(DISTINTO);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				expresion_nivel_5();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case PyC:
			case IFF:
			case IMPLIES:
			case OR:
			case AND:
			case PARENTESIS_CERRAR:
			case COMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			expresion_nivel_4_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		returnAST = expresion_nivel_4_AST;
	}
	
	public final void expresion_nivel_5() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_5_AST = null;
		
		try {      // for error handling
			exp_mult();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop1374:
			do {
				if ((LA(1)==MAS||LA(1)==MENOS)) {
					{
					switch ( LA(1)) {
					case MAS:
					{
						AST tmp14_AST = null;
						tmp14_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp14_AST);
						match(MAS);
						break;
					}
					case MENOS:
					{
						AST tmp15_AST = null;
						tmp15_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp15_AST);
						match(MENOS);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					exp_mult();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1374;
				}
				
			} while (true);
			}
			expresion_nivel_5_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		returnAST = expresion_nivel_5_AST;
	}
	
	public final void exp_mult() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exp_mult_AST = null;
		
		try {      // for error handling
			expresion_unaria();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop1378:
			do {
				if (((LA(1) >= MULT && LA(1) <= POW))) {
					{
					switch ( LA(1)) {
					case MULT:
					{
						AST tmp16_AST = null;
						tmp16_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp16_AST);
						match(MULT);
						break;
					}
					case DIV:
					{
						AST tmp17_AST = null;
						tmp17_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp17_AST);
						match(DIV);
						break;
					}
					case MOD:
					{
						AST tmp18_AST = null;
						tmp18_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp18_AST);
						match(MOD);
						break;
					}
					case POW:
					{
						AST tmp19_AST = null;
						tmp19_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp19_AST);
						match(POW);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					expresion_unaria();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1378;
				}
				
			} while (true);
			}
			exp_mult_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		returnAST = exp_mult_AST;
	}
	
	public final void expresion_unaria() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_unaria_AST = null;
		AST j_AST = null;
		AST e_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MENOS:
			{
				match(MENOS);
				exp_func();
				j_AST = (AST)returnAST;
				expresion_unaria_AST = (AST)currentAST.root;
				expresion_unaria_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MENOS_UNARIO,"Unary Minus")).add(j_AST));
				currentAST.root = expresion_unaria_AST;
				currentAST.child = expresion_unaria_AST!=null &&expresion_unaria_AST.getFirstChild()!=null ?
					expresion_unaria_AST.getFirstChild() : expresion_unaria_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case ABS:
			case SIN:
			case COS:
			case PARENTESIS_ABRIR:
			case MAX:
			case MIN:
			case SUM:
			case IDENT:
			case LIT_REAL:
			case LIT_ENTERO:
			case LIT_STRING:
			{
				exp_func();
				e_AST = (AST)returnAST;
				expresion_unaria_AST = (AST)currentAST.root;
				expresion_unaria_AST = e_AST;
				currentAST.root = expresion_unaria_AST;
				currentAST.child = expresion_unaria_AST!=null &&expresion_unaria_AST.getFirstChild()!=null ?
					expresion_unaria_AST.getFirstChild() : expresion_unaria_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = expresion_unaria_AST;
	}
	
	public final void exp_func() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exp_func_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case PARENTESIS_ABRIR:
			case IDENT:
			case LIT_REAL:
			case LIT_ENTERO:
			case LIT_STRING:
			{
				exp_base();
				astFactory.addASTChild(currentAST, returnAST);
				exp_func_AST = (AST)currentAST.root;
				break;
			}
			case ABS:
			case SIN:
			case COS:
			case MAX:
			case MIN:
			case SUM:
			{
				func_compleja();
				astFactory.addASTChild(currentAST, returnAST);
				exp_func_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = exp_func_AST;
	}
	
	public final void exp_base() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exp_base_AST = null;
		AST a_AST = null;
		Token  b = null;
		AST b_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LIT_REAL:
			{
				AST tmp21_AST = null;
				tmp21_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp21_AST);
				match(LIT_REAL);
				exp_base_AST = (AST)currentAST.root;
				break;
			}
			case LIT_ENTERO:
			{
				AST tmp22_AST = null;
				tmp22_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp22_AST);
				match(LIT_ENTERO);
				exp_base_AST = (AST)currentAST.root;
				break;
			}
			case LIT_STRING:
			{
				AST tmp23_AST = null;
				tmp23_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp23_AST);
				match(LIT_STRING);
				exp_base_AST = (AST)currentAST.root;
				break;
			}
			case PARENTESIS_ABRIR:
			{
				match(PARENTESIS_ABRIR);
				expresion();
				astFactory.addASTChild(currentAST, returnAST);
				match(PARENTESIS_CERRAR);
				exp_base_AST = (AST)currentAST.root;
				break;
			}
			default:
				if ((LA(1)==IDENT) && (LA(2)==PUNTO)) {
					att();
					a_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					resuelveNombreAtributo(a_AST);
					exp_base_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==IDENT) && (_tokenSet_9.member(LA(2)))) {
					b = LT(1);
					b_AST = astFactory.create(b);
					astFactory.addASTChild(currentAST, b_AST);
					match(IDENT);
					resuelveNombre(b_AST);
					exp_base_AST = (AST)currentAST.root;
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = exp_base_AST;
	}
	
	public final void func_compleja() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST func_compleja_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MAX:
			case MIN:
			case SUM:
			{
				func_multiparam();
				astFactory.addASTChild(currentAST, returnAST);
				func_compleja_AST = (AST)currentAST.root;
				break;
			}
			case ABS:
			case SIN:
			case COS:
			{
				func_uniparam();
				astFactory.addASTChild(currentAST, returnAST);
				func_compleja_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = func_compleja_AST;
	}
	
	public final void func_multiparam() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST func_multiparam_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case MAX:
			{
				AST tmp26_AST = null;
				tmp26_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp26_AST);
				match(MAX);
				break;
			}
			case MIN:
			{
				AST tmp27_AST = null;
				tmp27_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp27_AST);
				match(MIN);
				break;
			}
			case SUM:
			{
				AST tmp28_AST = null;
				tmp28_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp28_AST);
				match(SUM);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(PARENTESIS_ABRIR);
			expresion();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop1387:
			do {
				if ((LA(1)==COMA)) {
					match(COMA);
					expresion();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop1387;
				}
				
			} while (true);
			}
			match(PARENTESIS_CERRAR);
			func_multiparam_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = func_multiparam_AST;
	}
	
	public final void func_uniparam() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST func_uniparam_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case ABS:
			{
				AST tmp32_AST = null;
				tmp32_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp32_AST);
				match(ABS);
				break;
			}
			case SIN:
			{
				AST tmp33_AST = null;
				tmp33_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp33_AST);
				match(SIN);
				break;
			}
			case COS:
			{
				AST tmp34_AST = null;
				tmp34_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp34_AST);
				match(COS);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(PARENTESIS_ABRIR);
			expresion();
			astFactory.addASTChild(currentAST, returnAST);
			match(PARENTESIS_CERRAR);
			func_uniparam_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = func_uniparam_AST;
	}
	
	public final void att() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST att_AST = null;
		Token  f = null;
		AST f_AST = null;
		Token  a = null;
		AST a_AST = null;
		
		try {      // for error handling
			f = LT(1);
			f_AST = astFactory.create(f);
			match(IDENT);
			AST tmp37_AST = null;
			tmp37_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp37_AST);
			match(PUNTO);
			a = LT(1);
			a_AST = astFactory.create(a);
			astFactory.addASTChild(currentAST, a_AST);
			match(IDENT);
			att_AST = (AST)currentAST.root;
			att_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ATRIBUTO,"Attribute")).add(f_AST).add(a_AST));
			currentAST.root = att_AST;
			currentAST.child = att_AST!=null &&att_AST.getFirstChild()!=null ?
				att_AST.getFirstChild() : att_AST;
			currentAST.advanceChildToEnd();
			att_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = att_AST;
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
		"PUNTO"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 278626146378055680L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 278626146378055682L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 9570149342380032L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 9570150147686400L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 9570151221428224L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 9570153368911872L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 9570694534791168L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 9572343802232832L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 9605329151066112L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	
	}
