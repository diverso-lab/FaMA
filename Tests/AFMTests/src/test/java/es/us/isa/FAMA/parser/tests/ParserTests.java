package es.us.isa.FAMA.parser.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Before;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;



public class ParserTests {

	protected QuestionTrader qt;
	protected Properties props;

	public ParserTests() {
		super();
	}

	protected void exeTests(String dir) {
		File[] files = getFilesPath(dir);
		for (int i = 0; i < files.length; i++){
			File aux = files[i];
			if (aux.isFile()){
//				GenericAttributedFeatureModel fm = parser.parseModel(aux.getAbsolutePath());
				GenericAttributedFeatureModel fm = (GenericAttributedFeatureModel) qt.openFile(aux.getAbsolutePath());
				System.out.println();
				System.out.println(aux.getName()+"\n");
				System.out.println(fm);
				System.out.println();
			}
		}
	}

	protected File[] getFilesPath(String dir) {
		File [] res;
		File f = new File(dir);
		if (f.isDirectory()){
			res = f.listFiles();
		}
		else{
			res = new File[0];
		}
		return res;
	}

	@Before
	public void setup() {
		qt = new QuestionTrader();
		props = new Properties();
		try {
			props.load(new FileInputStream("ParserTests.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}