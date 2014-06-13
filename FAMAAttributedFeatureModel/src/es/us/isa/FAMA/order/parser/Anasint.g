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
	package es.us.isa.FAMA.order.parser;       
	import java.util.*;	
}

class Anasint extends Parser;

options{
	k = 2;
	buildAST = true;	
}

tokens{
	
	//TODO ir metiendo aqui todos los tokens que sean necesarios
	ORDER_MODEL;
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
	//CONFIG_ATTRIBUTE
	CONFIG_ATTRIBUTE;
}


//--------------------------------------------------
//-------------- SECCION DE FUNCIONES --------------
//--------------------------------------------------
{


//seran necesarias estructuras de datos, pues en primer lugar
//construimos el modelo sin atributos, y luego se los insertamos

//en el propio anasint realizaremos las resoluciones de nombres
//que sean necesarias (sobre todo en declaraciones de atributos y CTC)


	//attributes
	Collection<String> attributes = new LinkedList<String>();
		
	//contador para ir nombrando las relaciones
	int contRels = 1;
	
	//cadena para ir nombrando las relaciones
	String relConst = "rel-";
	
	//errores :D
	Collection<String> errors = new LinkedList<String>();
	
	public AST nextRelationName(){
		String aux = relConst+contRels;
		AST relName = #[IDENT,aux];
		contRels++;
		return relName;
	}
	
	public void addAttribute(AST att){
		AST attNameAST = att.getFirstChild();
		String nombreAtt = attNameAST.getText();
		if (!attributes.contains(nombreAtt)){
			attributes.add(nombreAtt);
		}
		else{
			//ERROR
			String error = "Error at line "+attNameAST.getLine()+": attribute "+nombreAtt+" has been previously declared.";
			errors.add(error);
		}
		
	}
	
	
	 // Cambiar tambien el tree parser
	
	public AST constraintExpresion(AST e){
		//TODO
		AST name = nextRelationName();
		AST res =  #(#[CONSTRAINT,"Constraint"],name,e);
		return res;
	}
	
	public void checkAtt(AST a){
		String attName = a.getText();
		boolean b = attributes.contains(attName);
		if (!b){
			errors.add("Attribute "+attName+" (line "+a.getLine()+") has not been declared");
		}
	}
		
}

//--------------------------------------------
//---------- ANALIZADOR SINTACTICO -----------
//--------------------------------------------


entrada returns [Collection<String> e = null;]: conjunto_atributos conjunto_constraints EOF!
	{## = #(#[ORDER_MODEL,"Order Model"],##);
	//feats = mapFeatures;
	e = errors;};


//--------------------------------------------
//--------------- PROPIEDADES ----------------
//--------------------------------------------

conjunto_atributos: SECCION_PROPIEDADES^ atts:lista_atributos {## = #([ATRIBUTOS,"Atributos"],atts);};

lista_atributos: (declaracion_atributo)+;

declaracion_atributo! {AST aux;}: att:IDENT DOSPUNTOS! d:dominio_att COMA! dv:defaultValue COMA! nv:nullValue PyC!
	{aux = #(#[ATRIBUTO,"Attribute"],#att,#d,#dv,#nv);
	addAttribute(aux);
	## = aux;};

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


constraint: declaracion_expresion;


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

func_uniparam: (ABS^ | SIN^ | COS^) PARENTESIS_ABRIR! exp_base PARENTESIS_CERRAR!;

func_multiparam: (MAX^ | MIN^ | SUM^) PARENTESIS_ABRIR! exp_base (COMA! expresion)* PARENTESIS_CERRAR!;

exp_base: a:att 
 		 | LIT_REAL 
		 | LIT_ENTERO 
		 | LIT_STRING 
		 | PARENTESIS_ABRIR! expresion PARENTESIS_CERRAR!
		 ;

att: order_att | config_att;

order_att: a:IDENT {checkAtt(#a); ## = #(#[ATRIBUTO,"Attribute"], #a);};

config_att: CONFIGURATION! PUNTO! i:IDENT {## = #(#[CONFIG_ATTRIBUTE,"CONFIG_ATTRIBUTE"],#i);};

		 

