package main;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.AttRelation;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.Invariant;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.extended.attributes.IntegerAttribute;
import es.us.isa.FAMA.models.featureModel.extended.attributes.Range;
import es.us.isa.FAMA.models.featureModel.extended.attributes.RangeAttribute;
import es.us.isa.FAMA.models.featureModel.extended.attributes.SetAttribute;


public class AttributedFeatureModelTest {
	
	FAMAAttributedFeatureModel famafm;
	
	int MAX = 3;
	
	@Before
	public void setUp(){
		famafm = new FAMAAttributedFeatureModel();
	}
	
	/*
	 * Feature model muy simple. Una raiz con tres hijos mandatory
	 * Los hijos tienen atributos, de tipo Range, con un valor por defecto
	 */
	@Test
	public void test1(){
		AttributedFeature f = new AttributedFeature("root");
		famafm.setRoot(f);
		for (int i = 1; i <= MAX; i++){
			AttributedFeature aux = new AttributedFeature("child-"+i);
			Relation r = new Relation("r-"+i);
			r.addCardinality(new Cardinality(1,1));
			f.addRelation(r);
			r.addDestination(aux);
			for (int j = i; j <= MAX; j++){
				IntegerAttribute att = new RangeAttribute("att"+i+""+j,new Range(0,10),Integer.MIN_VALUE);
				aux.addAttribute(att);
			}
		}
		System.out.println(famafm);
	}
	
	/*
	 * Feature model como el anterior, pero con invariantes
	 */
	//@Test
	public void test2(){
		AttributedFeature f = new AttributedFeature("root");
		famafm.setRoot(f);
		for (int i = 1; i <= MAX; i++){
			AttributedFeature aux = new AttributedFeature("child-"+i);
			Relation r = new Relation("r-"+i);
			r.addCardinality(new Cardinality(1,1));
			f.addRelation(r);
			r.addDestination(aux);
			for (int j = i; j <= MAX; j++){
				IntegerAttribute att = new RangeAttribute("att"+i+""+j,new Range(0,10),Integer.MIN_VALUE);
				aux.addAttribute(att);
			}
		}
		Invariant inv1 = new Invariant("att11 > att12");
		famafm.searchFeatureByName("child-1").addInvariant(inv1);
		Invariant inv2 = new Invariant("att22 != att21");
		famafm.searchFeatureByName("child-2").addInvariant(inv2);
		System.out.println(famafm);
	}
	
	/*
	 * Feature model con invariantes y relaciones entre atributos
	 */
	//@Test
	public void test3(){
		AttributedFeature f = new AttributedFeature("root");
		famafm.setRoot(f);
		for (int i = 1; i <= MAX; i++){
			AttributedFeature aux = new AttributedFeature("child_"+i);
			Relation r = new Relation("r_"+i);
			r.addCardinality(new Cardinality(1,1));
			f.addRelation(r);
			r.addDestination(aux);
			for (int j = i; j <= MAX; j++){
				IntegerAttribute att = new RangeAttribute("att"+i+""+j,new Range(0,10),Integer.MIN_VALUE);
				aux.addAttribute(att);
			}
		}
		//invariantes
		Invariant inv1 = new Invariant("att11 > att12");
		famafm.searchFeatureByName("child_1").addInvariant(inv1);
		Invariant inv2 = new Invariant("att22 != child_2.att23");
		famafm.searchFeatureByName("child_2").addInvariant(inv2);
		
		//relaciones entre atributos
		AttRelation attrel1 = new AttRelation("child_1.att11 = (child_2.att22 * 3) - 9");
		famafm.addAttributeRelationship(attrel1);
		System.out.println(famafm);
	}
	
	/*
	 * Extended Feature Model del JAMES
	 */
	//@Test
	public void test4(){
		//root (james)
		AttributedFeature root = new AttributedFeature("JAMES");
		famafm.setRoot(root);
		Relation r1 = new Relation("r1");
		Relation r2 = new Relation("r2");
		Relation r3 = new Relation("r3");
		Relation r4 = new Relation("r4");
		Relation r5 = new Relation("r5");
		root.addRelation(r1);
		root.addRelation(r2);
		root.addRelation(r3);
		root.addRelation(r4);
		root.addRelation(r5);
		IntegerAttribute cost1 = new RangeAttribute("cost",new Range(0,Integer.MAX_VALUE),0);
		root.addAttribute(cost1);
		
		//User Management
		AttributedFeature userManagement = new AttributedFeature("UserManagement");
		r1.addDestination(userManagement);
		r1.addCardinality(new Cardinality(1,1));
		IntegerAttribute cost2 = new RangeAttribute("cost",new Range(0,Integer.MAX_VALUE),0);
		userManagement.addAttribute(cost2);
		Relation r6 = new Relation("r6");
		userManagement.addRelation(r6);
		
		AttributedFeature db = new AttributedFeature("DB");
		AttributedFeature ldap = new AttributedFeature("LDAP");
		r6.addDestination(db);
		r6.addDestination(ldap);
		r6.addCardinality(new Cardinality(1,1));
		IntegerAttribute cost3 = new SetAttribute("cost", 100, 100);
		IntegerAttribute cost4 = new SetAttribute("cost", 100, 100);
		db.addAttribute(cost3);
		ldap.addAttribute(cost4);
		
		//WS - Interface
		AttributedFeature ws_interface = new AttributedFeature("WS_Interface");
		r2.addDestination(ws_interface);
		r2.addCardinality(new Cardinality(0,1));
		IntegerAttribute cost5 = new SetAttribute("cost", 200, 200);
		ws_interface.addAttribute(cost5);
		
		//GUI
		AttributedFeature gui = new AttributedFeature("GUI");
		r3.addDestination(gui);
		r3.addCardinality(new Cardinality(1,1));
		IntegerAttribute cost6 = new RangeAttribute("cost", new Range(0, Integer.MAX_VALUE), 0);
		gui.addAttribute(cost6);
		Relation r8 = new Relation("r8");
		gui.addRelation(r8);
		r8.addCardinality(new Cardinality(1,2));
		
		AttributedFeature pda = new AttributedFeature("PDA");
		AttributedFeature pc = new AttributedFeature("PC");
		r8.addDestination(pda);
		r8.addDestination(pc);
		IntegerAttribute cost13 = new SetAttribute("cost", 1800, 1800);
		IntegerAttribute cost14 = new SetAttribute("cost", 300, 300);
		pda.addAttribute(cost14);
		pc.addAttribute(cost13);
		
		//Core
		AttributedFeature core = new AttributedFeature("Core");
		r4.addDestination(core);
		r4.addCardinality(new Cardinality(1,1));
		IntegerAttribute cost7 = new SetAttribute("cost", 1500, 1500);
		core.addAttribute(cost7);
		
		//Modules
		AttributedFeature modules = new AttributedFeature("Modules");
		r5.addDestination(modules);
		r5.addCardinality(new Cardinality(1,1));
		IntegerAttribute cost8 = new RangeAttribute("cost", new Range(0, Integer.MAX_VALUE), 0);
		modules.addAttribute(cost8);
		Relation r7 = new Relation("r7");
		modules.addRelation(r7);
		r7.addCardinality(new Cardinality(1,4));
		
		AttributedFeature calendar = new AttributedFeature("Calendar");
		AttributedFeature forum = new AttributedFeature("Forum");
		AttributedFeature congress = new AttributedFeature("CongressManagement");
		AttributedFeature repo = new AttributedFeature("Repository");
		r7.addDestination(calendar);
		r7.addDestination(forum);
		r7.addDestination(congress);
		r7.addDestination(repo);
		IntegerAttribute cost9 = new SetAttribute("cost", 50, 50);
		IntegerAttribute cost10 = new SetAttribute("cost", 300, 300);
		IntegerAttribute cost11 = new SetAttribute("cost", 20, 20);
		IntegerAttribute cost12 = new SetAttribute("cost", 100, 100);
		calendar.addAttribute(cost9);
		forum.addAttribute(cost10);
		congress.addAttribute(cost11);
		repo.addAttribute(cost12);
		
		//attributes relations
		AttRelation attrel1 = new AttRelation("JAMES.cost = UserManagement.cost + WS_Interface.cost" +
				" + GUI.cost + core.cost + modules.cost");
		AttRelation attrel2 = new AttRelation("UserManagement.cost = DB.cost + LDAP.cost");
		AttRelation attrel3 = new AttRelation("Modules.cost = Calendar.cost + Forum.cost " +
				"+ CongressManagement.cost + Repository.cost");
		AttRelation attrel4 = new AttRelation("GUI.cost = PDA.cost + PC.cost");
		famafm.addAttributeRelationship(attrel1);
		famafm.addAttributeRelationship(attrel2);
		famafm.addAttributeRelationship(attrel3);
		famafm.addAttributeRelationship(attrel4);
		
		//cross tree constraints
		famafm.addDependency(new ExcludesDependency(repo,pda));
		famafm.addDependency(new RequiresDependency(congress,repo));
		
		System.out.println(famafm);
		
	}
	
}
