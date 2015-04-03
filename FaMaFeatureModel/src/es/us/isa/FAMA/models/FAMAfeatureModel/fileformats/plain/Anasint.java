// $ANTLR : "Anasint.g" -> "Anasint.java"$

	package es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.plain;	
	import java.util.*;	
	

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

public class Anasint extends antlr.LLkParser       implements AnasintTokenTypes
 {



//seran necesarias estructuras de datos, pues en primer lugar
//construimos el modelo sin atributos, y luego se los insertamos

//en el propio anasint realizaremos las resoluciones de nombres
//que sean necesarias (sobre todo en declaraciones de atributos y CTC)

	//map con todas las features declaradas hasta el momento, mapeadas por nombre
	Map<String,AST> mapFeatures = new HashMap<String,AST>();
	
	
	
	//contador para ir nombrando las relaciones
	int contRels = 1;
	
	//cadena para ir nombrando las relaciones
	String relConst = "CTC-";
	
	//errores :D
	Collection<String> errors = new LinkedList<String>();
	
	//flag usado en la definicion de invariantes (un ident dentro de una
	//invariante no es una feature, sino un atributo)
	boolean flag = true;
	
	//al procesar un conjunto de invariantes, feature a la que pertenecen estas
	String currentFeature;
	
	public AST AST_feature(AST f){
		String featName = f.getText();
		AST declaracion_feature = mapFeatures.get(featName);
		if (declaracion_feature == null){
			AST dominio = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOMINIO,"Domain")).add(astFactory.create(LIT_ENTERO,"0")).add(astFactory.create(LIT_ENTERO,"1")));
			AST relaciones = astFactory.create(RELACIONES,"Relationships");
			declaracion_feature = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(FEATURE,"Feature")).add(f).add(dominio).add(relaciones));
			mapFeatures.put(featName,declaracion_feature);
		}
		//else {
		//	errors.add("Duplicated feature detected: "+featName);	
		//}	
		return declaracion_feature;
	}
	
	public AST createRelName(AST features){
		String s = "to-";
		AST nextFeat = features;
		while (nextFeat != null){
			AST aux = nextFeat.getFirstChild();
			s = s + aux.getText() + "-";
			nextFeat = nextFeat.getNextSibling();
		}
		s = s + "rel";
		AST relName = astFactory.create(IDENT,s);
		return relName;
	}

	public AST AST_feature_relacion(AST dec_feat, AST relaciones){
		dec_feat.getFirstChild().getNextSibling().getNextSibling().setFirstChild(relaciones);
		return dec_feat;
	}
	
	public AST AST_relacion_simple_card(AST cardinality, AST feature){
		//TODO realizar comprobacion de que el rango de la cardinalidad esta entre 0 y 1
		//AST relName = nextRelationName();
		AST relName = createRelName(feature);
		AST rel = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(RELACION,"Relationship")).add(relName).add(cardinality).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FEATURES,"Features")).add(feature))));
		return rel;
	}
	
	public AST AST_relacion_grupo(AST cardinality, AST lista_features){
		//AST relName = nextRelationName();
		AST relName = createRelName(lista_features);
		AST rel = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(RELACION,"Relationship")).add(relName).add(cardinality).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FEATURES,"Features")).add(lista_features))));
		return rel;
	}
	
	public AST AST_relacion_binaria(AST feature, int min, int max){
		AST auxMin = astFactory.create(LIT_ENTERO,String.valueOf(min));
		AST auxMax = astFactory.create(LIT_ENTERO,String.valueOf(max));
		AST card = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(CARDINALIDAD,"Cardinality")).add(auxMin).add(auxMax));
		AST res = AST_relacion_simple_card(card,feature);
		return res;
	}
	
	public AST nextRelationName(){
		String aux = relConst+contRels;
		AST relName = astFactory.create(IDENT,aux);
		contRels++;
		return relName;
	}
	
	
	
	public void checkFeatures(AST... features){
		for (int i = 0; i < features.length; i++){
			String featName = features[i].getText();
			if (!existsFeature(featName)){
				String error = "Error at line "+features[i].getLine()+
					": feature "+featName+" does not exist";
				errors.add(error);
			}
		}
	}
	
	public boolean existsFeature(String f){
		return (mapFeatures.get(f) != null);
	}
	
	public AST createDependency(AST rel, AST f1, AST f2){
		//String aux = relConst+contRels;
		//contRels++;
		//AST nombre = #[IDENT,aux];
		//AST nombre = nextRelationName();
		AST aux = (AST)astFactory.make( (new ASTArray(3)).add(rel).add(f1).add(f2));
		AST res = constraintExpresion(aux);
		return res;	
	}
	
	
	
	
	 // Cambiar tambien el tree parser
	
	public AST constraintExpresion(AST e){
		//TODO
		AST name = nextRelationName();
		AST res =  (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(CONSTRAINT,"Constraint")).add(name).add(e));
		return res;
	}

		

protected Anasint(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public Anasint(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected Anasint(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public Anasint(TokenStream lexer) {
  this(lexer,2);
}

public Anasint(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final Collection<String>  entrada() throws RecognitionException, TokenStreamException {
		Collection<String> e = null;;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST entrada_AST = null;
		
		try {      // for error handling
			conjunto_relaciones();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case SECCION_CONSTRAINTS:
			{
				conjunto_constraints();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(Token.EOF_TYPE);
			if ( inputState.guessing==0 ) {
				entrada_AST = (AST)currentAST.root;
				entrada_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FEATURE_MODEL,"Feature Model")).add(entrada_AST));
					//feats = mapFeatures;
					e = errors;
				currentAST.root = entrada_AST;
				currentAST.child = entrada_AST!=null &&entrada_AST.getFirstChild()!=null ?
					entrada_AST.getFirstChild() : entrada_AST;
				currentAST.advanceChildToEnd();
			}
			entrada_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = entrada_AST;
		return e;
	}
	
	public final void conjunto_relaciones() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conjunto_relaciones_AST = null;
		
		try {      // for error handling
			AST tmp2_AST = null;
			tmp2_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp2_AST);
			match(SECCION_RELACIONES);
			declaraciones_feature();
			astFactory.addASTChild(currentAST, returnAST);
			conjunto_relaciones_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = conjunto_relaciones_AST;
	}
	
	public final void conjunto_constraints() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conjunto_constraints_AST = null;
		AST cons_AST = null;
		
		try {      // for error handling
			AST tmp3_AST = null;
			tmp3_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp3_AST);
			match(SECCION_CONSTRAINTS);
			lista_constraints();
			cons_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				conjunto_constraints_AST = (AST)currentAST.root;
				conjunto_constraints_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CONSTRAINTS,"Constraints")).add(cons_AST));
				currentAST.root = conjunto_constraints_AST;
				currentAST.child = conjunto_constraints_AST!=null &&conjunto_constraints_AST.getFirstChild()!=null ?
					conjunto_constraints_AST.getFirstChild() : conjunto_constraints_AST;
				currentAST.advanceChildToEnd();
			}
			conjunto_constraints_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = conjunto_constraints_AST;
	}
	
	public final void declaraciones_feature() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaraciones_feature_AST = null;
		AST r_AST = null;
		
		try {      // for error handling
			declaracion_feature();
			r_AST = (AST)returnAST;
			{
			_loop6:
			do {
				if ((LA(1)==CORCHETE_ABRIR||LA(1)==IDENT)) {
					declaracion_feature();
				}
				else {
					break _loop6;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				declaraciones_feature_AST = (AST)currentAST.root;
				declaraciones_feature_AST = r_AST;
				currentAST.root = declaraciones_feature_AST;
				currentAST.child = declaraciones_feature_AST!=null &&declaraciones_feature_AST.getFirstChild()!=null ?
					declaraciones_feature_AST.getFirstChild() : declaraciones_feature_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = declaraciones_feature_AST;
	}
	
	public final void declaracion_feature() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_feature_AST = null;
		AST f_AST = null;
		AST r_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case CORCHETE_ABRIR:
			{
				match(CORCHETE_ABRIR);
				break;
			}
			case IDENT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			feature();
			f_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case CORCHETE_CERRAR:
			{
				match(CORCHETE_CERRAR);
				break;
			}
			case DOSPUNTOS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(DOSPUNTOS);
			lista_relaciones();
			r_AST = (AST)returnAST;
			match(PyC);
			if ( inputState.guessing==0 ) {
				declaracion_feature_AST = (AST)currentAST.root;
				declaracion_feature_AST = AST_feature_relacion(f_AST,r_AST);
				currentAST.root = declaracion_feature_AST;
				currentAST.child = declaracion_feature_AST!=null &&declaracion_feature_AST.getFirstChild()!=null ?
					declaracion_feature_AST.getFirstChild() : declaracion_feature_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_feature_AST;
	}
	
	public final void feature() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST feature_AST = null;
		Token  f = null;
		AST f_AST = null;
		
		try {      // for error handling
			f = LT(1);
			f_AST = astFactory.create(f);
			match(IDENT);
			if ( inputState.guessing==0 ) {
				feature_AST = (AST)currentAST.root;
				feature_AST = AST_feature(f_AST);
				currentAST.root = feature_AST;
				currentAST.child = feature_AST!=null &&feature_AST.getFirstChild()!=null ?
					feature_AST.getFirstChild() : feature_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_3);
			} else {
			  throw ex;
			}
		}
		returnAST = feature_AST;
	}
	
	public final void lista_relaciones() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_relaciones_AST = null;
		
		try {      // for error handling
			{
			int _cnt13=0;
			_loop13:
			do {
				if ((LA(1)==CORCHETE_ABRIR||LA(1)==IDENT)) {
					relacion();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt13>=1 ) { break _loop13; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt13++;
			} while (true);
			}
			lista_relaciones_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_relaciones_AST;
	}
	
	public final void relacion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relacion_AST = null;
		AST r_AST = null;
		AST mf_AST = null;
		AST of_AST = null;
		
		try {      // for error handling
			boolean synPredMatched16 = false;
			if (((LA(1)==CORCHETE_ABRIR) && (LA(2)==LIT_ENTERO))) {
				int _m16 = mark();
				synPredMatched16 = true;
				inputState.guessing++;
				try {
					{
					cardinalidad();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched16 = false;
				}
				rewind(_m16);
inputState.guessing--;
			}
			if ( synPredMatched16 ) {
				relacion_cardinalidad();
				r_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					relacion_AST = (AST)currentAST.root;
					relacion_AST = r_AST;
					currentAST.root = relacion_AST;
					currentAST.child = relacion_AST!=null &&relacion_AST.getFirstChild()!=null ?
						relacion_AST.getFirstChild() : relacion_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((LA(1)==IDENT)) {
				mandatory_feature();
				mf_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					relacion_AST = (AST)currentAST.root;
					relacion_AST = AST_relacion_binaria(mf_AST,1,1);
					currentAST.root = relacion_AST;
					currentAST.child = relacion_AST!=null &&relacion_AST.getFirstChild()!=null ?
						relacion_AST.getFirstChild() : relacion_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((LA(1)==CORCHETE_ABRIR) && (LA(2)==IDENT)) {
				optional_feature();
				of_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					relacion_AST = (AST)currentAST.root;
					relacion_AST = AST_relacion_binaria(of_AST,0,1);
					currentAST.root = relacion_AST;
					currentAST.child = relacion_AST!=null &&relacion_AST.getFirstChild()!=null ?
						relacion_AST.getFirstChild() : relacion_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		returnAST = relacion_AST;
	}
	
	public final void cardinalidad() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cardinalidad_AST = null;
		Token  min = null;
		AST min_AST = null;
		Token  max = null;
		AST max_AST = null;
		
		try {      // for error handling
			match(CORCHETE_ABRIR);
			min = LT(1);
			min_AST = astFactory.create(min);
			match(LIT_ENTERO);
			match(COMA);
			max = LT(1);
			max_AST = astFactory.create(max);
			match(LIT_ENTERO);
			match(CORCHETE_CERRAR);
			if ( inputState.guessing==0 ) {
				cardinalidad_AST = (AST)currentAST.root;
				cardinalidad_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(CARDINALIDAD,"Cardinality")).add(astFactory.create(min_AST)).add(astFactory.create(max_AST)));
				currentAST.root = cardinalidad_AST;
				currentAST.child = cardinalidad_AST!=null &&cardinalidad_AST.getFirstChild()!=null ?
					cardinalidad_AST.getFirstChild() : cardinalidad_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_6);
			} else {
			  throw ex;
			}
		}
		returnAST = cardinalidad_AST;
	}
	
	public final void relacion_cardinalidad() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relacion_cardinalidad_AST = null;
		AST card_AST = null;
		AST f_AST = null;
		AST l_AST = null;
		
		try {      // for error handling
			cardinalidad();
			card_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case IDENT:
			{
				feature();
				f_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					relacion_cardinalidad_AST = (AST)currentAST.root;
					relacion_cardinalidad_AST = AST_relacion_simple_card(card_AST,f_AST);
					currentAST.root = relacion_cardinalidad_AST;
					currentAST.child = relacion_cardinalidad_AST!=null &&relacion_cardinalidad_AST.getFirstChild()!=null ?
						relacion_cardinalidad_AST.getFirstChild() : relacion_cardinalidad_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case LLAVE_ABRIR:
			{
				lista_features();
				l_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					relacion_cardinalidad_AST = (AST)currentAST.root;
					relacion_cardinalidad_AST = AST_relacion_grupo(card_AST,l_AST);
					currentAST.root = relacion_cardinalidad_AST;
					currentAST.child = relacion_cardinalidad_AST!=null &&relacion_cardinalidad_AST.getFirstChild()!=null ?
						relacion_cardinalidad_AST.getFirstChild() : relacion_cardinalidad_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		returnAST = relacion_cardinalidad_AST;
	}
	
	public final void mandatory_feature() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST mandatory_feature_AST = null;
		
		try {      // for error handling
			feature();
			astFactory.addASTChild(currentAST, returnAST);
			mandatory_feature_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		returnAST = mandatory_feature_AST;
	}
	
	public final void optional_feature() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST optional_feature_AST = null;
		
		try {      // for error handling
			match(CORCHETE_ABRIR);
			feature();
			astFactory.addASTChild(currentAST, returnAST);
			match(CORCHETE_CERRAR);
			optional_feature_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		returnAST = optional_feature_AST;
	}
	
	public final void lista_features() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_features_AST = null;
		
		try {      // for error handling
			match(LLAVE_ABRIR);
			feature();
			astFactory.addASTChild(currentAST, returnAST);
			{
			int _cnt24=0;
			_loop24:
			do {
				if ((LA(1)==IDENT)) {
					feature();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt24>=1 ) { break _loop24; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt24++;
			} while (true);
			}
			match(LLAVE_CERRAR);
			lista_features_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_features_AST;
	}
	
	public final void lista_constraints() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_constraints_AST = null;
		
		try {      // for error handling
			{
			int _cnt28=0;
			_loop28:
			do {
				if ((LA(1)==IDENT)) {
					constraint();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt28>=1 ) { break _loop28; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt28++;
			} while (true);
			}
			lista_constraints_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_constraints_AST;
	}
	
	public final void constraint() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constraint_AST = null;
		
		try {      // for error handling
			{
			if ((LA(1)==IDENT) && (LA(2)==EXCLUDES)) {
				exclusion();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==IDENT) && (LA(2)==REQUIRES)) {
				inclusion();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(PyC);
			constraint_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			} else {
			  throw ex;
			}
		}
		returnAST = constraint_AST;
	}
	
	public final void exclusion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exclusion_AST = null;
		Token  f1 = null;
		AST f1_AST = null;
		Token  d = null;
		AST d_AST = null;
		Token  f2 = null;
		AST f2_AST = null;
		
		try {      // for error handling
			f1 = LT(1);
			f1_AST = astFactory.create(f1);
			match(IDENT);
			d = LT(1);
			d_AST = astFactory.create(d);
			match(EXCLUDES);
			f2 = LT(1);
			f2_AST = astFactory.create(f2);
			match(IDENT);
			if ( inputState.guessing==0 ) {
				exclusion_AST = (AST)currentAST.root;
				checkFeatures(f1_AST,f2_AST);
														exclusion_AST = createDependency(d_AST,f1_AST,f2_AST);
				currentAST.root = exclusion_AST;
				currentAST.child = exclusion_AST!=null &&exclusion_AST.getFirstChild()!=null ?
					exclusion_AST.getFirstChild() : exclusion_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = exclusion_AST;
	}
	
	public final void inclusion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inclusion_AST = null;
		Token  f1 = null;
		AST f1_AST = null;
		Token  d = null;
		AST d_AST = null;
		Token  f2 = null;
		AST f2_AST = null;
		
		try {      // for error handling
			f1 = LT(1);
			f1_AST = astFactory.create(f1);
			match(IDENT);
			d = LT(1);
			d_AST = astFactory.create(d);
			match(REQUIRES);
			f2 = LT(1);
			f2_AST = astFactory.create(f2);
			match(IDENT);
			if ( inputState.guessing==0 ) {
				inclusion_AST = (AST)currentAST.root;
				checkFeatures(f1_AST,f2_AST);
														 inclusion_AST = createDependency(d_AST,f1_AST,f2_AST);
				currentAST.root = inclusion_AST;
				currentAST.child = inclusion_AST!=null &&inclusion_AST.getFirstChild()!=null ?
					inclusion_AST.getFirstChild() : inclusion_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = inclusion_AST;
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
		"RELACION",
		"CARDINALIDAD",
		"RELACIONES",
		"RANGO",
		"LITERAL",
		"RANGOS",
		"VALORES",
		"ENUM",
		"MENOS_UNARIO",
		"CORCHETE_ABRIR",
		"CORCHETE_CERRAR",
		"SECCION_RELACIONES",
		"DOSPUNTOS",
		"PyC",
		"IDENT",
		"LIT_ENTERO",
		"COMA",
		"LLAVE_ABRIR",
		"LLAVE_CERRAR",
		"SECCION_CONSTRAINTS",
		"EXCLUDES",
		"REQUIRES"
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
		long[] data = { 2147483650L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 2216689666L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 1197473792L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 33554432L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 102760448L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 603979776L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 67108866L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	
	}
