package es.us.isa.FAMA.order.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;
import es.us.isa.FAMA.order.OrderModel;
import es.us.isa.util.Node;
import es.us.isa.util.Tree;

public class OrderModelParser {

	public OrderModelParser(){
		
	}
	
	public OrderModel parseOrderModel(String path){
		OrderModel model = null;
		
		try {
			FileInputStream in = new FileInputStream(path);
			Analex an = new Analex(in);
			Anasint as = new Anasint(an);
			model = parseModel(as);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return model;
	}
	
	private OrderModel parseModel(Anasint as) {
		OrderModel res = null;
		try {
			as.setASTNodeClass("es.us.isa.FAMA.parser.MyAST");
			Collection<String> errors;
			errors = as.entrada();
			AST tree = as.getAST();
//			ASTFrame frame = new ASTFrame("AST", tree);
//			frame.setVisible(true);
			if (!errors.isEmpty()) {
				System.out
						.println("Warning, errors detected on Syntactic Analysis");
				showErrors(errors);
			} else {
				FaMaTreeParser sem = new FaMaTreeParser();
				sem.setASTNodeClass("es.us.isa.FAMA.parser.MyAST");
				OrderModelParserResult result = sem.entrada(tree);
				errors = result.getErrors();
				if (!errors.isEmpty()){
					System.out
						.println("Warning, errors detected on Semantic Analysis");
					showErrors(errors);
				}
				else{
					res = result.getModel();
				}
			}
		} catch (RecognitionException e) {
			e.printStackTrace();
		} catch (TokenStreamException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public Tree<String> parseConstraint(String s) {

		Tree<String> res = null;
		try {
			Analex an = new Analex(new StringReader(s));
			Anasint as = new Anasint(an);
			as.expresion();
			AST tree = as.getAST();
			res = astToTree(tree);
		} catch (RecognitionException e) {
			e.printStackTrace();
		} catch (TokenStreamException e) {
			e.printStackTrace();
		}
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
	
	private void showErrors(Collection<String> errors) {

		Iterator<String> it = errors.iterator();
		while (it.hasNext()) {
			String e = it.next();
			System.out.println(e);
		}

	}

}
