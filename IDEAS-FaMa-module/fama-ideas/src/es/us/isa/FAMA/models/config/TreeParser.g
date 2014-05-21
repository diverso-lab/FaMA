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
}

class ConfigTreeParser extends TreeParser;

options{
	importVocab = ConfigAnasint;
}


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

}

entrada returns [Configuration res = null;] 
	{Collection<Tree<String>> cons = new LinkedList<Tree<String>>(); ExtendedConfiguration auxRes = new ExtendedConfiguration();}: 
			#(CONFIGURACION cons = constraints) //regla
			{auxRes.setAttConfigs(cons); res = auxRes;};
			
constraints returns[Collection<Tree<String>> constraints = new LinkedList<Tree<String>>();]{Constraint aux = null;}: 
(aux = constraint {constraints.add(aux.getAST());})+;

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
	 | valor
	 | IDENT
	 | id_att
	 ;
	 
id_att: #(ATRIBUTO IDENT IDENT);

valor : LIT_ENTERO | LIT_REAL | LIT_STRING;
