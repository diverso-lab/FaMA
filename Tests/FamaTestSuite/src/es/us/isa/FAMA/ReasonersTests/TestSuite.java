package es.us.isa.FAMA.ReasonersTests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;

/**
 * Abstract class with the common operations for all test
 * @author Jesús
 *
 */
public abstract class TestSuite {

	Question q;
	
	GenericFeatureModel fm;
	
	QuestionTrader qt;
	
	
	@Before
	public void setUp(){
		//TODO Put here the common operations for all test setUp
		qt = new QuestionTrader();
		fm = this.getFeatureModel();
		qt.setVariabilityModel(fm);
		//Verify that this CriteriaSelector (SelectedCriteriaSelector) is in its package and in FAMAConfig.xml
		qt.setCriteriaSelector("selected");
	}
	
	public GenericFeatureModel getFeatureModel(){
		//Reasoner r = null;
		Properties p = new Properties();
		String rName = null;
		try {
			p.load(new FileInputStream("reasoner.properties"));
			rName = p.getProperty("fmpath");
			//r = qt.getReasonerById(rName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GenericFeatureModel fm = (GenericFeatureModel) qt.openFile(rName);
		return fm;
	}
	
	@After
	public void cleanUp(){
		//TODO Put here the common operations for all test cleanUp
	}
	
	
	
	
	
}
