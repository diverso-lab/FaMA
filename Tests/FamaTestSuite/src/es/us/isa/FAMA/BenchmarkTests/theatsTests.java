package es.us.isa.FAMA.BenchmarkTests;

import java.io.IOException;

import es.us.isa.FAMA.models.variabilityModel.parsers.WrongFormatException;
import es.us.isa.benchmarking.BenchMarkTimer;
import es.us.isa.benchmarking.generators.Timer;


public class theatsTests {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws WrongFormatException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException, WrongFormatException {
		BenchMarkTimer timer= new BenchMarkTimer("./files");
		timer.generate(0.25f, 0.25f, 0.25f, 0.25f);
		timer.start();
	}

}
