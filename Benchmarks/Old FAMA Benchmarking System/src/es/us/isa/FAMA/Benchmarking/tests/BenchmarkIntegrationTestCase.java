package es.us.isa.FAMA.Benchmarking.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.Characteristics;
import es.us.isa.FAMA.Benchmarking.RandomExperiment;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.Reasoner;

public class BenchmarkIntegrationTestCase {

	private RandomExperiment exp;
	private QuestionTrader qt;
	int width = 5;
	int height = 3;
	int choose = 4;
	int dependencies = 2;
	int numberOfInstances = 10;
	int numberOfFeatures = 30;
	
	@Before
	public void setUp() throws Exception {
		qt = new QuestionTrader();
		exp = new RandomExperiment(qt);
		Iterator<String> itr = qt.getReasonerIds();
		while (itr.hasNext()) {
			String reasonerName = itr.next();
			Reasoner r = qt.getReasonerById(reasonerName);
			Class<Question> cq = qt.getQuestionById("Valid");
			Question q = r.getFactory().createQuestion(cq);
			if (q != null)
				exp.addReasoner(reasonerName, r, q);
		}
		
		exp.addCharacteristics(new Characteristics(width, height, choose, dependencies, numberOfInstances,numberOfFeatures));
	}

	@Test
	public void testBasic() {
		exp.setToFile(true);
		exp.setOutputDirectory("test");
		try {
			exp.execute();
		} catch (IOException e) {
			fail("Error when writing result file");
		}
	}
}
