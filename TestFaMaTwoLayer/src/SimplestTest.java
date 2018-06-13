import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.twolayer.PlatFormCapabilityQuestion;
import es.us.isa.FAMA.Reasoner.questions.twolayer.PlatFormCompatibilityQuestion;
import es.us.isa.FAMA.Reasoner.questions.twolayer.PlatFormFunctionalityQuestion;
import es.us.isa.FAMA.Reasoner.questions.twolayer.PlatFormMigrationQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class SimplestTest {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {


		System.out.println("#-----------------------#");
		System.out.println("Capability without atts");
		Capability();
		
		System.out.println("#-----------------------#");
		System.out.println("Capability with atts");
		Capability_attributes();
		
		System.out.println("#-----------------------#");
		System.out.println("Compatibility without atts");
		Compatibility();
		
		System.out.println("#-----------------------#");
		System.out.println("Functionality without atts");
		Functionality();
		
		System.out.println("#-----------------------#");
		System.out.println("Migration without atts");
		Migration();
	}

	private static void Capability() throws FileNotFoundException {
		QuestionTrader qt = new QuestionTrader();
		qt.setTwolayerFM(true);
		PlatFormCapabilityQuestion pcq = (PlatFormCapabilityQuestion) qt.createQuestion("PlatformCapability");

		pcq.setInterModelRelationships("./models/inter_sample.afm");
		VariabilityModel top = qt.openFile("./models/top_sample.afm");
		VariabilityModel bottom = qt.openFile("./models/bottom_sample.afm");

		pcq.setTopLayer(top);
		pcq.setBottomLayer(bottom);
		
		Configuration conf = new Configuration();
		conf.addElement(new AttributedFeature("A"), 1);
		AttributedFeature attributedFeature = new AttributedFeature("B");
		conf.addElement(attributedFeature, 1);

		pcq.setConfTop(conf);
		qt.ask(pcq);
//		for (Product p : pcq.getBottomConfigurations()) {
//			System.out.println(p);
//		}
	}

	private static void Capability_attributes() throws FileNotFoundException {
		QuestionTrader qt = new QuestionTrader();
		qt.setTwolayerFM(true);
		PlatFormCapabilityQuestion pcq = (PlatFormCapabilityQuestion) qt.createQuestion("PlatformCapability");
		pcq.setInterModelRelationships("./models/inter_sample_atts.afm");
		VariabilityModel top = qt.openFile("./models/top_sample_atts.afm");
		VariabilityModel bottom = qt.openFile("./models/bottom_sample_atts.afm");
		pcq.setTopLayer(top);
		pcq.setBottomLayer(bottom);
		
		Configuration conf = new Configuration();
		conf.addElement(new AttributedFeature("A"), 1);
		AttributedFeature attributedFeature = new AttributedFeature("B");
		GenericAttribute genericAttribute = new GenericAttribute("b1",null,null,null);
		attributedFeature.addAttribute(genericAttribute);
		conf.addElement(attributedFeature, 1);conf.addElement(genericAttribute,10);
		pcq.setConfTop(conf);
		qt.ask(pcq);
		for (Product p : pcq.getBottomConfigurations()) {
			System.out.println(p);
		}
	}
	private static void Compatibility() {
		QuestionTrader qt = new QuestionTrader();
		qt.setTwolayerFM(true);
		PlatFormCompatibilityQuestion pcq = (PlatFormCompatibilityQuestion) qt
				.createQuestion("PlatformCompatibility");
		pcq.setInterModelRelationships("./models/inter_sample.afm");
		VariabilityModel top = qt.openFile("./models/top_sample.afm");
		VariabilityModel bottom = qt.openFile("./models/bottom_sample.afm");
		pcq.setTopLayer(top);
		pcq.setBottomLayer(bottom);
		
		Configuration conf = new Configuration();
		conf.addElement(new AttributedFeature("A"), 1);
		AttributedFeature attributedFeature = new AttributedFeature("B");
		conf.addElement(attributedFeature, 1);
		conf.addElement(new AttributedFeature("R"), 1);

		pcq.setConfTop(conf);

		Configuration conf2=new Configuration();
		conf2.addElement(new AttributedFeature("C"), 1);
		conf2.addElement(new AttributedFeature("E"), 1);
		conf2.addElement(new AttributedFeature("X"), 1);
		conf2.addElement(new AttributedFeature("Y"), 1);

		conf2.addElement(new AttributedFeature("D"), 0);
		conf2.addElement(new AttributedFeature("F"), 0);

		pcq.setConfBottom(conf2);
		qt.ask(pcq);
//		System.out.println("Alive features");
//		for (GenericFeature f : pcq.aliveFeatures()) {
//			System.out.println(f);
//		}
//		System.out.println("Dead features");
//		for (GenericFeature f : pcq.deadFeatures()) {
//			System.out.println(f);
//		}
	}

	private static void Functionality() {
		QuestionTrader qt = new QuestionTrader();
		qt.setTwolayerFM(true);
		PlatFormFunctionalityQuestion pcq = (PlatFormFunctionalityQuestion) qt
				.createQuestion("PlatformFunctionality");
		pcq.setInterModelRelationships("./models/inter_sample.afm");
		VariabilityModel top = qt.openFile("./models/top_sample.afm");
		VariabilityModel bottom = qt.openFile("./models/bottom_sample.afm");
		pcq.setTopLayer(top);
		pcq.setBottomLayer(bottom);
		Configuration conf = new Configuration();
		conf.addElement(new AttributedFeature("C"), 1);
		conf.addElement(new AttributedFeature("E"), 1);
		conf.addElement(new AttributedFeature("X"), 1);
		conf.addElement(new AttributedFeature("Y"), 1);
		
		conf.addElement(new AttributedFeature("D"), 0);
		conf.addElement(new AttributedFeature("F"), 0);
		
		
		pcq.setBottom(conf);
		qt.ask(pcq);
		for (Product p : pcq.getTopConfigurations()) {
			System.out.println(p);
		}
	}

	private static void Migration() {
		QuestionTrader qt = new QuestionTrader();
		qt.setTwolayerFM(true);
		PlatFormMigrationQuestion pcq = (PlatFormMigrationQuestion) qt
				.createQuestion("PlatformMigration");
		pcq.setInterModelRelationships("./models/inter_sample.afm");
		VariabilityModel top = qt.openFile("./models/top_sample.afm");
		VariabilityModel bottom = qt.openFile("./models/bottom_sample.afm");
		pcq.setTopLayer(top);
		pcq.setBottomLayer(bottom);
		Configuration topc =new Configuration();
		topc.addElement(new AttributedFeature("A"), 1);
		topc.addElement(new AttributedFeature("B"), 1);

		Configuration bottom1c =new Configuration();
		bottom1c.addElement(new AttributedFeature("C"), 1);
		bottom1c.addElement(new AttributedFeature("D"), 1);
		bottom1c.addElement(new AttributedFeature("E"), 1);
		bottom1c.addElement(new AttributedFeature("X"), 1);
		bottom1c.addElement(new AttributedFeature("Y"), 1);
		
		Configuration bottom2c =new Configuration();
		bottom2c.addElement(new AttributedFeature("C"), 1);
		bottom2c.addElement(new AttributedFeature("E"), 1);
		bottom2c.addElement(new AttributedFeature("X"), 1);
		bottom2c.addElement(new AttributedFeature("Y"), 1);
		bottom2c.addElement(new AttributedFeature("D"), 0);

		pcq.setConfBottom1(bottom1c);
		pcq.setConfBottom2(bottom2c);
		pcq.setConfTop(topc);
		qt.ask(pcq);
		
	}
}
