package es.us.isa.FAMA.BenchmarkTests;

import org.junit.Test;
import es.us.isa.benchmarking.FAMABenchmark;

public class BenchFacadeTest {
	@Test
	public void test1() throws Exception {
		FAMABenchmark b = new FAMABenchmark("files/test/fixedchars.csv",
				"files/test/famaFile.csv", "files/test/famaFile.csv",
				"files/test/results.csv");
		b.execute();
	}
}
