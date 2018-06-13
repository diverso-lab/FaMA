import java.io.FileNotFoundException;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.twolayer.PlatFormCapabilityQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.stagedConfigManager.Configuration;


public class PAPERMain {
	public static void main(String[] args) throws FileNotFoundException {
		QuestionTrader qt = new QuestionTrader();
		qt.setTwolayerFM(true);
		PlatFormCapabilityQuestion pcq = (PlatFormCapabilityQuestion) qt.createQuestion("PlatformCapability");
		pcq.setInterModelRelationships("./models/1paper_inter_sample_atts.afm");
		VariabilityModel top = qt.openFile("./models/1paper_top_sample_atts.afm");
		VariabilityModel bottom = qt.openFile("./models/1paper_bottom_sample_atts.afm");
		pcq.setTopLayer(top);
		pcq.setBottomLayer(bottom);
		
		Configuration conf = new Configuration();
		conf.addElement(new AttributedFeature("SMS"), 1);
		conf.addElement(new AttributedFeature("VoIP"), 1);
		conf.addElement(new AttributedFeature("Data"), 1);
		conf.addElement(new AttributedFeature("AppFeatures"), 1);
		conf.addElement(new AttributedFeature("Call"), 0);
		conf.addElement(new AttributedFeature("InternetAccess"), 1);
		conf.addElement(new AttributedFeature("Communications"), 1);
		conf.addElement(new AttributedFeature("Voice"), 1);
		conf.addElement(new AttributedFeature("Text"), 1);

		
//		AttributedFeature attributedFeature = new AttributedFeature("InternetAccess");
//		GenericAttribute genericAttribute = new GenericAttribute("datarate",null,null,null);
//		attributedFeature.addAttribute(genericAttribute);
//		conf.addElement(attributedFeature, 1);conf.addElement(genericAttribute,300);
		
		pcq.setConfTop(conf);
		qt.ask(pcq);
		for (Product p : pcq.getBottomConfigurations()) {
			System.out.println(p);
		}
	}
}
