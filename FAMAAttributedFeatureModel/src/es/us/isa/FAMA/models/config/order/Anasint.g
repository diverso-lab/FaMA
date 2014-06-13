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
	package es.us.isa.FAMA.models.config.order;       
	import java.util.*;	
	import es.us.isa.FAMA.parser.*;
}

class ConfigAnasint extends Parser;

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
	CONFIGURACION;
	ORDER;
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
		AST relName = #[IDENT,aux];
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
		AST res =  #(#[CONSTRAINT,"Constraint"],name,e);
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
		
}



//--------------------------------------------
//------------------ PARSER ------------------
//--------------------------------------------


entrada returns [Collection<String> result = null;]: 
SECCION_PEDIDO orderCons:lista_constraints lista:lista_configs
{## = #(#[ORDER,"Order"],orderCons,lista);};

lista_configs: (config)+;

config: SECCION_CONFIGURACION lista:lista_constraints
	{## = #(#[CONFIGURACION,"Configuration"],lista);};

lista_constraints: (declaracion_expresion)+;


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
