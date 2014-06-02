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
header{
	package es.us.isa.FAMA.parser;       
	import java.util.*;	
	import es.us.isa.FAMA.parser.*;
}

class Anasint extends Parser;

options{
	k = 2;
	buildAST = true;	
}

tokens{
	
	//TODO ir metiendo aqui todos los tokens que sean necesarios
	FEATURE_MODEL;
	FEATURE;
	FEATURES;
	CONSTRAINTS;
	CONSTRAINT;
	DOMINIO;
	DEF_VALUE;
	NULL_VALUE;
	ATRIBUTOS;
	ATRIBUTO;
	RELACION;
	CARDINALIDAD;
	RELACIONES;
	INVARIANTES;
	INVARIANTE;
	RANGO;
	LITERAL;
	RANGOS;
	VALORES;
	ENUM;
	MENOS_UNARIO;
	//VALORES LOGICOS
	TRUE_VALUE;
	FALSE_VALUE;
}


//--------------------------------------------------
//-------------- SECCION DE FUNCIONES --------------
//--------------------------------------------------
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
	String relConst = "rel-";
	
	//errores :D
	Collection<String> errors = new LinkedList<String>();
	
	//flag usado en la definicion de invariantes (un ident dentro de una
	//invariante no es una feature, sino un atributo)
	boolean flag = true;
	
	//al procesar un conjunto de invariantes, feature a la que pertenecen estas
	String currentFeature;
	
	// Helper structures for table constraints
	Map<Integer,AST> attsTableLeft, attsTableRight;
	
	public AST AST_feature(AST f){
		String featName = f.getText();
		AST declaracion_feature = mapFeatures.get(featName);
		if (declaracion_feature == null){
			AST dominio = #(#[DOMINIO,"Domain"],#[LIT_ENTERO,"0"],#[LIT_ENTERO,"1"]);
			AST atributos = #[ATRIBUTOS,"Attributes"];
			AST relaciones = #[RELACIONES,"Relationships"];
			AST invariantes = #[INVARIANTES,"Invariants"];
			declaracion_feature = #(#[FEATURE,"Feature"],f,dominio,atributos,relaciones,invariantes);
			mapFeatures.put(featName,declaracion_feature);
		}
		//else {
		//	errors.add("Duplicated feature detected: "+featName);	
		//}	
		return declaracion_feature;
	}

	public AST AST_feature_relacion(AST dec_feat, AST relaciones){
		dec_feat.getFirstChild().getNextSibling().getNextSibling().getNextSibling().setFirstChild(relaciones);
		return dec_feat;
	}
	
	public AST AST_relacion_simple_card(AST cardinality, AST feature){
		//TODO realizar comprobacion de que el rango de la cardinalidad esta entre 0 y 1
		AST relName = nextRelationName();
		AST rel = #(#[RELACION,"Relationship"],relName,cardinality,#(#[FEATURES,"Features"],feature));
		return rel;
	}
	
	public AST AST_relacion_grupo(AST cardinality, AST lista_features){
		AST relName = nextRelationName();
		AST rel = #(#[RELACION,"Relationship"],relName,cardinality,#(#[FEATURES,"Features"],lista_features));
		return rel;
	}
	
	public AST AST_relacion_binaria(AST feature, int min, int max){
		AST auxMin = #[LIT_ENTERO,String.valueOf(min)];
		AST auxMax = #[LIT_ENTERO,String.valueOf(max)];
		AST card = #(#[CARDINALIDAD,"Cardinality"],auxMin,auxMax);
		AST res = AST_relacion_simple_card(card,feature);
		return res;
	}
	
	public AST nextRelationName(){
		String aux = relConst+contRels;
		AST relName = #[IDENT,aux];
		contRels++;
		return relName;
	}
	
	public void addAttribute(AST feature, AST att){
		String fName = feature.getText();
		AST f = mapFeatures.get(fName);
		if (f != null){
			//AST rels = f.getFirstChild().getNextSibling().getNextSibling();
			//att.setNextSibling(rels);
			//f.getFirstChild().getNextSibling().setNextSibling(att);
			AST atts = f.getFirstChild().getNextSibling().getNextSibling();
			atts.addChild(att);
			String nombreAtt = att.getFirstChild().getText();
			AST tipoAtt = att.getFirstChild().getNextSibling().getFirstChild();
			Atributo a;
			if (tipoAtt.getType() == INTEGER){
				a = new Atributo(nombreAtt,Atributo.ENTERO);	
			}
			else if (tipoAtt.getType() == REAL){
				a = new Atributo(nombreAtt,Atributo.REAL);	
			}
			else{
				a = new Atributo(nombreAtt,Atributo.ENUMERADO);
			}
			Collection<Atributo> currentAtts = attributes.get(fName);
			if (currentAtts == null){
				currentAtts = new ArrayList<Atributo>();
			
			}
			currentAtts.add(a);
			attributes.put(fName,currentAtts);
		}
		else{
			String error = "Error at line "+feature.getLine()+": feature "+fName+" does not exist";
			errors.add(error);
		}
		
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
		AST aux = #(rel,f1,f2);
		AST res = constraintExpresion(aux);
		return res;	
	}
	
	public void setInvariantes(AST f, AST invs){
		String fName = f.getText();
		AST feature = mapFeatures.get(fName);
		if (feature != null){
			AST i = feature.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
		i.addChild(invs);
		}
		else{
			int line = f.getLine();
			String error = "Error at line "+line+": feature "+fName+" is not declared";
		}
		
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
		AST res =  #(#[CONSTRAINT,"Constraint"],name,e);
		return res;
	}
	
	public AST invarianteExpresion(AST e){
		//TODO
		AST name = nextRelationName();
		AST res =  #(#[INVARIANTE,"Invariant"],name,e);
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
	
	public void processHeader(List<AST> left, List<AST> right){
		//TODO here we have to identify the position of the attributes to create the constraints
		// in the table
		attsTableLeft = new HashMap<Integer,AST>();
		attsTableRight = new HashMap<Integer,AST>();
		int index = 0;
		for (AST a: left){
			if (a.getType() == ATRIBUTO){
				attsTableLeft.put(index,a);
			}
			index++;
		}
		index = 0;
		for (AST a: right){
			if (a.getType() == ATRIBUTO){
				attsTableRight.put(index,a);
			}
			index++;
		}
	}
	
	public AST createTableConstraint(List<AST> left, List<AST> right){
		//TODO I have to create a synthetic IMPLIES constraint (left IMPLIES right)
		//AST res =  #(#[CONSTRAINT,"Constraint"],name,e);
		AST resLeft = createAnd(left,0,attsTableLeft);
		AST resRight = createAnd(right,0,attsTableRight);
		
		AST aux = #(#[IMPLIES,"IMPLIES"],resLeft,resRight);
		AST res = constraintExpresion(aux);
		
		return res;
	}
	
	public AST createAnd(List<AST> list, int index,Map<Integer,AST> attsTableIndex){
		if (list.size() == 1){
			return createTableExpr(list.get(0),index,attsTableIndex);
		}
		else{
			AST aux = createTableExpr(list.remove(0),index,attsTableIndex);
			AST result = #(#[AND,"AND"],aux,createAnd(list,index+1,attsTableIndex));
			return result;
		}
	}
	
	public AST createTableExpr(AST a, int index, Map<Integer,AST> attsTableIndex){
		if (a.getType() ==  IDENT){
			// feature
			return getRowExprValue(a);
		}
		else if (a.getType() == MULT){
			// kleene's star
			return #[TRUE_VALUE,"TRUE"];
		}
		else{
			//XXX another kind of literal. we have to create an EQ constraint
			AST att = getRowAttribute(index,attsTableIndex);
			AST value = getRowExprValue(a);
			AST result =  #(#[IGUAL,"=="],att,value);
			return result;
		}
	}
	
	public AST getRowAttribute(int index, Map<Integer,AST> attsTableIndex){
		//XXX be careful here copying the attribute
		AST aux = attsTableIndex.get(index);
		String rootName = aux.getFirstChild().getText();
		String attName = aux.getFirstChild().getNextSibling().getText();
		AST result =  #(#[ATRIBUTO,"Attribute"],#[IDENT,rootName],#[IDENT,attName]);
		return result;
	}
	
	public AST getRowExprValue(AST a){
		//XXX to avoid bad experiences when appending branches of the AST, 
		//we create a new AST
		AST result = #[a.getType(),a.getText()];
		return result;
	}
		
}

//--------------------------------------------
//------------- FEATURES ---------------------
//--------------------------------------------

entrada returns [Collection<String> e = null;]: conjunto_relaciones (conjunto_atributos)? (conjunto_constraints)? EOF!
	{## = #(#[FEATURE_MODEL,"Feature Model"],##);
	//feats = mapFeatures;
	e = errors;};

conjunto_relaciones: SECCION_RELACIONES^ declaraciones_feature;

declaraciones_feature!: r:declaracion_feature (declaracion_feature)*
					{## = #r;};

declaracion_feature!: f:feature DOSPUNTOS! r:lista_relaciones PyC!
		{## = AST_feature_relacion(#f,#r); };

feature!: f:IDENT {## = AST_feature(#f);};

lista_relaciones: (relacion)+;

relacion!: (cardinalidad) => r:relacion_cardinalidad
							{## = #r;}
						   | mf:mandatory_feature
						   {## = AST_relacion_binaria(#mf,1,1);}
						   | of:optional_feature
						   {## = AST_relacion_binaria(#of,0,1);}
				;
						
relacion_cardinalidad!: card:cardinalidad 
					  ( f:feature {## = AST_relacion_simple_card(#card,#f);} 
					   | 
					   l:lista_features {## = AST_relacion_grupo(#card,#l);} );						
						   
mandatory_feature: feature;

optional_feature: CORCHETE_ABRIR! feature CORCHETE_CERRAR!;

cardinalidad!: CORCHETE_ABRIR! min:LIT_ENTERO COMA! max:LIT_ENTERO CORCHETE_CERRAR!
		{## = #(#[CARDINALIDAD,"Cardinality"],#[min],#[max]);};

lista_features: LLAVE_ABRIR! feature (feature)+ LLAVE_CERRAR!;



//--------------------------------------------
//----------------- ATRIBUTOS ----------------
//--------------------------------------------

conjunto_atributos!: SECCION_ATRIBUTOS (declaracion_atributo)+;

declaracion_atributo! {AST aux;}: f:IDENT PUNTO! att:IDENT DOSPUNTOS! d:dominio_att COMA! dv:defaultValue COMA! nv:nullValue PyC!
	{aux = #(#[ATRIBUTO,"Attribute"],#[att],d,dv,nv);
	addAttribute(#f,aux);};

//nombre_att: IDENT PUNTO! IDENT;

dominio_att: tipo | enumerado;

tipo: tipoInt | tipoReal;

tipoInt: i:INTEGER r:rangosInt {## = #(#[DOMINIO,"DominioInt"],#(#[i],#r));};

tipoReal: d:REAL r:rangoReal {## = #(#[DOMINIO,"DominioReal"],#(#[d],#r));};

rangosInt: (rangoInt)+ {## = #([RANGOS,"Ranges"],##);};

rangoInt!: CORCHETE_ABRIR! min:LIT_ENTERO TO! max:LIT_ENTERO CORCHETE_CERRAR!
	{## = #(#[RANGO,"Range"],min,max);}
	;
	
rangoReal!: CORCHETE_ABRIR! min:lit_num TO! max:lit_num CORCHETE_CERRAR!
	{## = #(#[RANGO,"Range"],min,max);}
	;

enumerado: CORCHETE_ABRIR!  lista:lista_enum CORCHETE_CERRAR!
	{## = #(#[DOMINIO,"Dominio"],#(#[ENUM,"Enum"],#(#[VALORES,"Valores"],#lista)));};

lista_enum : literal (COMA! literal)*;

defaultValue: l:literal {## = #(#[DEF_VALUE,"DefaultValue"],#l);};

nullValue: l:literal {## = #(#[NULL_VALUE,"NullValue"],#l);};

literal: LIT_REAL
		| LIT_ENTERO
		| LIT_STRING;
		
lit_num:  LIT_REAL
		| LIT_ENTERO;



//--------------------------------------------
//----------------- CONSTRAINTS --------------
//--------------------------------------------


//CTC constraints
//valdra con el arbol que se forma de la manera actual?
conjunto_constraints: SECCION_CONSTRAINTS cons:lista_constraints
	{## = #(#[CONSTRAINTS,"Constraints"],cons);};

lista_constraints: (constraint)+;

//constraint: (exclusion | inclusion) PyC!;



constraint: (declaracion_invariantes | declaracion_expresion | dependencia_simple | tabla);

dependencia_simple: (exclusion | inclusion) PyC!;

exclusion!: f1:IDENT d:EXCLUDES f2:IDENT {checkFeatures(#f1,#f2);
										## = createDependency(#d,#f1,#f2);};

inclusion!: f1:IDENT d:REQUIRES f2:IDENT {checkFeatures(#f1,#f2);
										 ## = createDependency(#d,#f1,#f2);};

declaracion_invariantes!: {flag = false;} f:IDENT {currentFeature = f.getText();} 
						LLAVE_ABRIR! invs:lista_invariantes LLAVE_CERRAR!
						{setInvariantes(#f,#invs);flag=true;};

lista_invariantes: (declaracion_invariante)+;

declaracion_invariante: e:declaracion_expresion; 
			//{## = invarianteExpresion(#e);};

declaracion_expresion: e:expresion PyC!{## = constraintExpresion(#e);};

expresion : 
   expresion_nivel_1 (( IFF^ | IMPLIES^) expresion_nivel_1)?  
   ;
   
expresion_nivel_1 : 
  expresion_nivel_2 (OR^ expresion_nivel_2)*  
  ;
  
expresion_nivel_2 : 
  expresion_nivel_3 (AND^ expresion_nivel_3)*  
  ;

expresion_nivel_3 : 
   (NOT^ expresion_nivel_3)
   | expresion_nivel_4 
   ;  

expresion_nivel_4: expresion_nivel_5 
	((MAYOR^ | MENOR^ | MAYOR_IGUAL^ | MENOR_IGUAL^ | IGUAL^ | DISTINTO^)
	expresion_nivel_5)?
	;
	
expresion_nivel_5: exp_mult ((MAS^ | MENOS^) exp_mult)*;

//invariante: exp (MAYOR^ | MENOR^ | MAYOR_IGUAL^ | MENOR_IGUAL^ | IGUAL^ | DISTINTO^) exp;
	//{## = #(#[INVARIANTE,"INVARIANTE"],##);};

//att_constraint: ATT ASIG^ exp;
	//{## = #(#[RELACION,"RELACION"],##);};

//exp: exp_mult ((MAS^ | MENOS^) exp_mult)*;

exp_mult: expresion_unaria ((MULT^ | DIV^ | MOD^ | POW^) expresion_unaria)*;

expresion_unaria! :
	MENOS! j:exp_func
	{##=#(#[MENOS_UNARIO,"Unary Minus"],#j);}
	| e:exp_func {## = #e;}
	;


exp_func: exp_base | func_compleja;

func_compleja: func_multiparam | func_uniparam;

func_uniparam: (ABS^ | SIN^ | COS^) PARENTESIS_ABRIR! expresion PARENTESIS_CERRAR!;

func_multiparam: (MAX^ | MIN^ | SUM^) PARENTESIS_ABRIR! expresion (COMA! expresion)* PARENTESIS_CERRAR!;

exp_base: a:att {resuelveNombreAtributo(#a);}
		 | b:IDENT {resuelveNombre(#b);}//feature, o atributo dentro de una invariante
		 | LIT_REAL 
		 | LIT_ENTERO 
		 | LIT_STRING 
		 | PARENTESIS_ABRIR! expresion PARENTESIS_CERRAR!
		 ;

att: f:IDENT !PUNTO a:IDENT {## = #(#[ATRIBUTO,"Attribute"],#f,#a);};

tabla: INICIO_TABLA! cabecera_tabla cuerpo_tabla FIN_TABLA!;

//cabecera_tabla!: left:(att | IDENT)+ IMPLICACION_TABLA right:(att | IDENT)+ PyC! {processHeader(#left,#right);};

cabecera_tabla!{List<AST> left = new LinkedList<AST>(), right = new LinkedList<AST>();}:
 (aux:IDENT {left.add(#aux);})+ IMPLIES (aux2:exp_cabecera {right.add(#aux2);})+ PyC 
{processHeader(left,right);}; 

//cabecera_tabla: (aux:IDENT)+ IMPLICACION_TABLA (aux2:exp_cabecera)+ PyC; 

exp_cabecera: (att | IDENT);

cuerpo_tabla: (fila_tabla)+;

//fila_tabla!{List<AST> left = new LinkedList<AST>(), right = new LinkedList<AST>();}:
// (aux:IDENT {left.add(#aux);})+ IMPLIES (aux2:exp_tabla{right.add(#aux2);})+ PyC 
//{## = createTableConstraint(left,right);};

fila_tabla!{List<AST> left = new LinkedList<AST>(), right = new LinkedList<AST>();}:
 (aux:exp_tabla {left.add(#aux);})+ IMPLIES (aux2:exp_tabla{right.add(#aux2);})+ PyC 
{## = createTableConstraint(left,right);};  

exp_tabla: b:IDENT {resuelveNombre(#b);}//feature, o atributo dentro de una invariante
		 | LIT_REAL 
		 | LIT_ENTERO 
		 | LIT_STRING 
		 | clausura_kleene;
		 
clausura_kleene: MULT;
		 

