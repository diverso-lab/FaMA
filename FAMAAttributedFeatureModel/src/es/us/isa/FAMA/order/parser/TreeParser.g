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
	import es.us.isa.FAMA.Exceptions.*;
	import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.*;
	import es.us.isa.FAMA.models.featureModel.*;
	import es.us.isa.FAMA.models.featureModel.extended.*;
	import es.us.isa.util.*;
	import es.us.isa.FAMA.models.domain.*;
	import es.us.isa.FAMA.models.domain.RealDomain;
	import es.us.isa.FAMA.order.parser.*;
	import es.us.isa.FAMA.order.*;
}

class FaMaTreeParser extends TreeParser;

options{
	importVocab = Anasint;
}

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
}

//-------------------
//--- TREE PARSER ---
//-------------------

entrada returns [OrderModelParserResult res = null;] 
	{Collection<GenericAttribute> props = new LinkedList<GenericAttribute>(); 
	Collection<Constraint> cons = new LinkedList<Constraint>();}: 
			//{mapASTFeatures = mapFeats;}
			#(ORDER_MODEL props=seccion_atts cons=seccion_cons) //regla
			{res = createOrderModel(props,cons);};
			
seccion_atts returns [Collection<GenericAttribute> atts = new LinkedList<GenericAttribute>();]
{GenericAttribute aux;}:
 #(ATRIBUTOS (aux = atributo {atts.add(aux);})*)
 ;



//dom returns [RangeIntegerDomain d = new RangeIntegerDomain();]{Range r;}: 
//	#(DOMINIO min:LIT_ENTERO max:LIT_ENTERO 
//	{r = createRange(min,max);
//	d.addRange(r);});

 
//el dominio de una feature es diferente al de un atributo
atributo returns [GenericAttribute att = null;]{Domain d;Object defVal, nullVal;}: 
	#(ATRIBUTO n:IDENT d = dominio_att defVal = default_value nullVal = null_value)
	{att = new GenericAttribute(n.getText(),d,nullVal,defVal);};

dominio_att returns [Domain d = null;]: #(DOMINIO (d = dominio_rango | d = dominio_enumerado | d = dominio_real));

dominio_rango returns [Domain d = null]{Collection<Range> ranges;}: 
	#(INTEGER ranges = rangos){d = new RangeIntegerDomain(ranges);};

rangos returns [Collection<Range> ranges = new HashSet<Range>();]{Range aux = null;}: 
	#(RANGOS (aux = rango {ranges.add(aux);})+ );

rango returns [Range r = null;]: #(RANGO min:LIT_ENTERO max:LIT_ENTERO) {r = createRange(min,max);};

dominio_enumerado returns [Domain d = null;]
{Collection<Object> c = new LinkedList<Object>();Object aux;}: 
    #(ENUM #(VALORES (aux = valor {c.add(aux);})+ ) ){d = createEnumeratedDomain(c);};

//XXX real domains
dominio_real returns [Domain d = null;]: 
	#(REAL d = rangoReal);
	
rangoReal returns [Domain d = null;]: #(RANGO min:val_lit max:val_lit){d = createRealDomain(min,max);};

val_lit: (LIT_REAL | LIT_ENTERO);

//si es un entero, convertirlo en un Integer
default_value returns [Object o = null;]: #(DEF_VALUE o = valor);

null_value returns [Object o = null;]: #(NULL_VALUE o = valor);

valor returns [Object o = null;]: (i:LIT_ENTERO {o = astToInteger(i);} 
					      | r:LIT_REAL {o = astToFloat(r);} 
					      | s:LIT_STRING {o = s.getText();}
					      | b1:TRUE_VALUE {o = b1.getText();}
					      | b2:FALSE_VALUE {o = b2.getText();});

/*relaciones returns [Collection<Relation> rels = new LinkedList<Relation>();] {Relation aux;}: 
	#(RELACIONES (aux = relacion
	{rels.add(aux);}
	)*);

relacion returns [Relation r = null;] {Collection<AttributedFeature> children;}:
		 #(RELACION n:IDENT c:card children=features)
		 {r = createRelation(n,c,children);};
		 
invariantes returns [Collection<Constraint> invs = new LinkedList<Constraint>();]
{Constraint aux;}:
 #(INVARIANTES (aux = constraint {invs.add(aux);})*);
*/

//aqui debemos usar la traduccion de AST al arbol de FaMa
//invariante returns [Constraint inv = null;]: #(INVARIANTE inv = expr);

constraint returns [Constraint c = null;]: #(CONSTRAINT n:IDENT e:expresion {c = ASTtoConstraint(e,n);});

//expr returns [Constraint c = null;]: e:expresion {c = ASTtoConstraint(e);};

expresion: #(IFF expresion expresion)
	 | #(IMPLIES expresion expresion)
	 | #(EXCLUDES expresion expresion)
	 | #(REQUIRES expresion expresion)
	 | #(OR expresion expresion)
	 | #(AND expresion expresion)
	 | #(NOT expresion)
	 | #(MAYOR expresion expresion)
	 | #(MENOR expresion expresion)
	 | #(MAYOR_IGUAL expresion expresion)
	 | #(MENOR_IGUAL expresion expresion)
	 | #(IGUAL expresion expresion)
	 | #(DISTINTO expresion expresion)
	 | #(MAS expresion expresion)
	 | #(MENOS expresion expresion)
	 | #(MULT expresion expresion)
	 | #(DIV expresion expresion)
	 | #(MOD expresion expresion)
	 | #(POW expresion expresion)
	 | #(MENOS_UNARIO expresion)
	 | #(SUM expresion)
	 | valor
	 | IDENT
	 | id_att
	 ;
	 
	 
id_att: id_order_att | id_config_att;

id_order_att: #(ATRIBUTO IDENT);

id_config_att: #(CONFIG_ATTRIBUTE IDENT);

/*
features returns [Collection<AttributedFeature> feats = new LinkedList<AttributedFeature>();] {AttributedFeature aux;}: 
	#(FEATURES (aux=feature {feats.add(aux);} )*);

card: #(CARDINALIDAD LIT_ENTERO LIT_ENTERO);
*/


seccion_cons returns [Collection<Constraint> res = new LinkedList<Constraint>();]
	{Constraint aux;}:
		 #(CONSTRAINTS (aux=constraint {res.add(aux);})*);

//restriccion returns [Constraint c = null;]: c = expr;

//restriccion returns [Dependency d = null;]:  (d = exclusion | d = inclusion);

//TODO hay que terminar esta parte, y posiblemente, aadirle un nombre a las dependencias
//exclusion returns [ExcludesDependency d = null;]: 
//	#(EXCLUDES n:IDENT f1:IDENT f2:IDENT){d = createExcludes(n,f1,f2);};

//inclusion returns [RequiresDependency d = null;]:
//	 #(IMPLIES n:IDENT f1:IDENT f2:IDENT){d = createRequires(n,f1,f2);};
