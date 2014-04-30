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
	package es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.plain;	
	import java.util.*;	
	
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
	RELACION;
	CARDINALIDAD;
	RELACIONES;
	RANGO;
	LITERAL;
	RANGOS;
	VALORES;
	ENUM;
	MENOS_UNARIO;
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
			AST dominio = #(#[DOMINIO,"Domain"],#[LIT_ENTERO,"0"],#[LIT_ENTERO,"1"]);
			AST relaciones = #[RELACIONES,"Relationships"];
			declaracion_feature = #(#[FEATURE,"Feature"],f,dominio,relaciones);
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
		AST relName = #[IDENT,s];
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
		AST rel = #(#[RELACION,"Relationship"],relName,cardinality,#(#[FEATURES,"Features"],feature));
		return rel;
	}
	
	public AST AST_relacion_grupo(AST cardinality, AST lista_features){
		//AST relName = nextRelationName();
		AST relName = createRelName(lista_features);
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
	
	
	
	
	 // Cambiar tambien el tree parser
	
	public AST constraintExpresion(AST e){
		//TODO
		AST name = nextRelationName();
		AST res =  #(#[CONSTRAINT,"Constraint"],name,e);
		return res;
	}

		
}

//--------------------------------------------
//------------- FEATURES ---------------------
//--------------------------------------------

entrada returns [Collection<String> e = null;]: conjunto_relaciones (conjunto_constraints)? EOF!
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
//----------------- CONSTRAINTS --------------
//--------------------------------------------


//CTC constraints
//valdra con el arbol que se forma de la manera actual?
conjunto_constraints: SECCION_CONSTRAINTS cons:lista_constraints
	{## = #(#[CONSTRAINTS,"Constraints"],cons);};

lista_constraints: (constraint)+;

//constraint: (exclusion | inclusion) PyC!;


constraint:  (exclusion | inclusion) PyC!;



exclusion!: f1:IDENT d:EXCLUDES f2:IDENT {checkFeatures(#f1,#f2);
										## = createDependency(#d,#f1,#f2);};

inclusion!: f1:IDENT d:REQUIRES f2:IDENT {checkFeatures(#f1,#f2);
										 ## = createDependency(#d,#f1,#f2);};

						