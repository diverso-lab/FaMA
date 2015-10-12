package main;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.AttRelation;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.Invariant;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAAttritubedfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.models.featureModel.extended.attributes.IntegerAttribute;
import es.us.isa.FAMA.models.featureModel.extended.attributes.Range;
import es.us.isa.FAMA.models.featureModel.extended.attributes.RangeAttribute;
import es.us.isa.FAMA.models.featureModel.extended.attributes.SetAttribute;

public class EFMTests {

	private QuestionTrader qt;

	private GenericAttributedFeatureModel fm;

	private static final int COTA_SUP = 50000;

	@Before
	public void setUp() {
		qt = new QuestionTrader();

	}

	@Test
	public void test1() {
		fm = this.getJamesFeatureModel();
		qt.setVariabilityModel(fm);
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		try {
			qt.ask(vq);
			System.out.println("Is the model valid (must be true)? "
					+ vq.isValid());
		} catch (FAMAException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2() {
		fm = this.getJamesFeatureModel();
		qt.setVariabilityModel(fm);
		SetQuestion sq = (SetQuestion) qt.createQuestion("Set");
		FilterQuestion fq = (FilterQuestion) qt.createQuestion("Filter");
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		try {
			fq.addValue(fm.searchFeatureByName("JAMES").searchAttributeByName(
					"cost"), 0);
			sq.addQuestion(fq);
			sq.addQuestion(vq);
			qt.ask(sq);
			System.out.println("Is the model valid (must be false)? "
					+ vq.isValid());
		} catch (FAMAException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3() {
		fm = this.getLittleFeatureModel();
		qt.setVariabilityModel(fm);
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		try {
			qt.ask(vq);
			System.out.println("Is the model valid (must be true)? "
					+ vq.isValid());
		} catch (FAMAException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4() {
		fm = this.getLittleFeatureModel();
		qt.setVariabilityModel(fm);
		SetQuestion sq = (SetQuestion) qt.createQuestion("Set");
		FilterQuestion fq = (FilterQuestion) qt.createQuestion("Filter");
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		try {
			fq.addValue(fm.searchFeatureByName("B").searchAttributeByName(
					"cost"), 400);
			fq.addValue(fm.searchFeatureByName("C").searchAttributeByName(
					"cost"), 400);
			sq.addQuestion(fq);
			sq.addQuestion(vq);
			qt.ask(sq);
			System.out.println("Is the model valid (must be false)? "
					+ vq.isValid());
		} catch (FAMAException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test5() {
		fm = this.getAnotherLittleFeatureModel();
		qt.setVariabilityModel(fm);
		SetQuestion sq = (SetQuestion) qt.createQuestion("Set");
		FilterQuestion fq = (FilterQuestion) qt.createQuestion("Filter");
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		try {
			fq.addValue(fm.searchFeatureByName("B"), 1);
			fq.addValue(fm.searchFeatureByName("C"), 1);
			sq.addQuestion(fq);
			sq.addQuestion(vq);
			qt.ask(sq);
			System.out.println("Is the model valid (must be false)? "
					+ vq.isValid());
		} catch (FAMAException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test6() {
		fm = this.getTunningFeatureModel();
		qt.setVariabilityModel(fm);
		SetQuestion sq = (SetQuestion) qt.createQuestion("Set");
		FilterQuestion fq = (FilterQuestion) qt.createQuestion("Filter");
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		try {
			fq.addValue(fm.searchFeatureByName("AC"), 1);
			fq.addValue(fm.searchFeatureByName("CAL"), 1);
			fq.addValue(fm.searchFeatureByName("LLANTAS"), 4);

			sq.addQuestion(fq);
			sq.addQuestion(vq);
			qt.ask(sq);
			System.out.println("Is the model valid (must be false)? "
					+ vq.isValid());
		} catch (FAMAException e) {
			e.printStackTrace();
		}
	}

	private GenericAttributedFeatureModel getJamesFeatureModel() {
		FAMAAttributedFeatureModel famafm = new FAMAAttributedFeatureModel();
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
		IntegerAttribute cost1 = new RangeAttribute("cost", new Range(0,
				COTA_SUP), 0);
		root.addAttribute(cost1);
		// root.
		root.addInvariant(new Invariant("JAMES.cost > 0"));

		// User Management
		AttributedFeature userManagement = new AttributedFeature(
				"UserManagement");
		r1.addDestination(userManagement);
		r1.addCardinality(new Cardinality(1, 1));
		IntegerAttribute cost2 = new RangeAttribute("cost", new Range(0,
				COTA_SUP), 0);
		userManagement.addAttribute(cost2);
		Relation r6 = new Relation("r6");
		userManagement.addRelation(r6);

		AttributedFeature db = new AttributedFeature("DB");
		AttributedFeature ldap = new AttributedFeature("LDAP");
		r6.addDestination(db);
		r6.addDestination(ldap);
		r6.addCardinality(new Cardinality(1, 1));
		IntegerAttribute cost3 = new SetAttribute("cost", 100, 100);
		IntegerAttribute cost4 = new SetAttribute("cost", 100, 100);
		db.addAttribute(cost3);
		ldap.addAttribute(cost4);

		// WS - Interface
		AttributedFeature ws_interface = new AttributedFeature("WS_Interface");
		r2.addDestination(ws_interface);
		r2.addCardinality(new Cardinality(0, 1));
		IntegerAttribute cost5 = new SetAttribute("cost", 200, 200);
		ws_interface.addAttribute(cost5);

		// GUI
		AttributedFeature gui = new AttributedFeature("GUI");
		r3.addDestination(gui);
		r3.addCardinality(new Cardinality(1, 1));
		IntegerAttribute cost6 = new RangeAttribute("cost", new Range(0,
				COTA_SUP), 0);
		gui.addAttribute(cost6);
		Relation r8 = new Relation("r8");
		gui.addRelation(r8);
		r8.addCardinality(new Cardinality(1, 2));

		AttributedFeature pda = new AttributedFeature("PDA");
		AttributedFeature pc = new AttributedFeature("PC");
		r8.addDestination(pda);
		r8.addDestination(pc);
		IntegerAttribute cost13 = new SetAttribute("cost", 1800, 1800);
		IntegerAttribute cost14 = new SetAttribute("cost", 300, 300);
		pda.addAttribute(cost14);
		pc.addAttribute(cost13);

		// Core
		AttributedFeature core = new AttributedFeature("Core");
		r4.addDestination(core);
		r4.addCardinality(new Cardinality(1, 1));
		IntegerAttribute cost7 = new SetAttribute("cost", 1500, 1500);
		core.addAttribute(cost7);

		// Modules
		AttributedFeature modules = new AttributedFeature("Modules");
		r5.addDestination(modules);
		r5.addCardinality(new Cardinality(1, 1));
		IntegerAttribute cost8 = new RangeAttribute("cost", new Range(0,
				COTA_SUP), 0);
		modules.addAttribute(cost8);
		Relation r7 = new Relation("r7");
		modules.addRelation(r7);
		r7.addCardinality(new Cardinality(1, 4));

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

		// attributes relations
		AttRelation attrel1 = new AttRelation(
				"JAMES.cost = UserManagement.cost + WS_Interface.cost"
						+ " + GUI.cost + Core.cost + Modules.cost");
		AttRelation attrel2 = new AttRelation(
				"UserManagement.cost = DB.cost + LDAP.cost");
		AttRelation attrel3 = new AttRelation(
				"Modules.cost = Calendar.cost + Forum.cost "
						+ "+ CongressManagement.cost + Repository.cost");
		AttRelation attrel4 = new AttRelation("GUI.cost = PDA.cost + PC.cost");
		famafm.addAttributeRelationship(attrel1);
		famafm.addAttributeRelationship(attrel2);
		famafm.addAttributeRelationship(attrel3);
		famafm.addAttributeRelationship(attrel4);

		// cross tree constraints
		famafm.addDependency(new ExcludesDependency(repo, pda));
		famafm.addDependency(new RequiresDependency(congress, repo));

		return famafm;
	}

	private GenericAttributedFeatureModel getLittleFeatureModel() {
		FAMAAttributedFeatureModel famafm = new FAMAAttributedFeatureModel();
		AttributedFeature root = new AttributedFeature("A");
		famafm.setRoot(root);
		Relation r1 = new Relation("r1");
		Relation r2 = new Relation("r2");
		root.addRelation(r1);
		root.addRelation(r2);
		RangeAttribute att1 = new RangeAttribute("cost",
				new Range(0, COTA_SUP), 0);
		root.addAttribute(att1);
		root.addInvariant(new Invariant("A.cost < 800"));

		AttributedFeature b = new AttributedFeature("B");
		r1.addDestination(b);
		r1.addCardinality(new Cardinality(0, 1));
		RangeAttribute att2 = new RangeAttribute("cost", new Range(200, 400), 0);
		b.addAttribute(att2);

		AttributedFeature c = new AttributedFeature("C");
		r2.addDestination(c);
		r2.addCardinality(new Cardinality(1, 1));
		RangeAttribute att3 = new RangeAttribute("cost", new Range(0, 500), 0);
		c.addAttribute(att3);

		AttRelation attrel1 = new AttRelation("A.cost = B.cost + C.cost");
		famafm.addAttributeRelationship(attrel1);

		return famafm;
	}

	private GenericAttributedFeatureModel getTunningFeatureModel() {
		FAMAAttributedFeatureModel famafm = new FAMAAttributedFeatureModel();
		AttributedFeature root = new AttributedFeature("CAR");
		famafm.setRoot(root);
		Relation r1 = new Relation("r1");
		Relation r2 = new Relation("r2");
		Relation r3 = new Relation("r3");
		Relation r4 = new Relation("r4");
		root.addRelation(r1);
		root.addRelation(r2);
		root.addRelation(r3);
		root.addRelation(r4);
		IntegerAttribute cost1 = new RangeAttribute("cost", new Range(5000,
				10000), 0);
		root.addAttribute(cost1);
		// root.
		root.addInvariant(new Invariant("CAR.cost > 0"));

		// CC
		AttributedFeature CC = new AttributedFeature("CC");
		IntegerAttribute cccost = new RangeAttribute("cost", new Range(3000,
				4000), 0);
		CC.addAttribute(cccost);
		r1.addDestination(CC);
		r1.addCardinality(new Cardinality(1, 1));
		// LLANTAS
		AttributedFeature LLANTAS = new AttributedFeature("LLANTAS");
		IntegerAttribute llantasCost = new RangeAttribute("cost", new Range(
				1000, 2000), 0);
		LLANTAS.addAttribute(llantasCost);
		r2.addDestination(LLANTAS);
		r2.addCardinality(new Cardinality(1, 4));

		// AC&&CAL
		AttributedFeature AC = new AttributedFeature("AC");
		AttributedFeature CAL = new AttributedFeature("CAL");
		IntegerAttribute CALCost = new RangeAttribute("cost", new Range(0, 50),
				0);
		IntegerAttribute ACCost = new RangeAttribute("cost", new Range(1000,
				2000), 0);
		AC.addAttribute(ACCost);
		CAL.addAttribute(CALCost);
		r3.addDestination(CAL);
		r3.addDestination(AC);
		r3.addCardinality(new Cardinality(0, 2));
		// CRISTALES TINTADOS
		AttributedFeature TC = new AttributedFeature("TC");
		SetAttribute TCCost = new SetAttribute("cost", 100, 0);
		TCCost.addAllowedValue(200);
		LLANTAS.addAttribute(TCCost);
		r4.addDestination(TC);
		r4.addCardinality(new Cardinality(1, 3));

		return famafm;
	}

	private GenericAttributedFeatureModel getAnotherLittleFeatureModel() {
		FAMAAttributedFeatureModel famafm = new FAMAAttributedFeatureModel();
		AttributedFeature root = new AttributedFeature("A");
		famafm.setRoot(root);
		Relation r1 = new Relation("r1");
		Relation r2 = new Relation("r2");
		root.addRelation(r1);
		root.addRelation(r2);
		RangeAttribute att1 = new RangeAttribute("cost",
				new Range(0, COTA_SUP), 0);
		root.addAttribute(att1);
		root.addInvariant(new Invariant("A.cost < 5"));

		AttributedFeature b = new AttributedFeature("B");
		r1.addDestination(b);
		r1.addCardinality(new Cardinality(0, 1));
		SetAttribute att2 = new SetAttribute("cost", 3, 0);
		b.addAttribute(att2);

		AttributedFeature c = new AttributedFeature("C");
		r2.addDestination(c);
		r2.addCardinality(new Cardinality(0, 1));
		SetAttribute att3 = new SetAttribute("cost", 4, 0);
		c.addAttribute(att3);

		AttRelation attrel1 = new AttRelation("A.cost = B.cost + C.cost");
		famafm.addAttributeRelationship(attrel1);

		return famafm;
	}

}
