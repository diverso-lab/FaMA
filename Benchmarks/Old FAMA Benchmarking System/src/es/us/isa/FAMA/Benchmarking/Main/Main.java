package es.us.isa.FAMA.Benchmarking.Main;

import java.io.IOException;

import es.us.isa.FAMA.Benchmarking.Benchmark;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				Benchmark bm = new Benchmark();
				bm.openBenchmarkFile(args[i]);

				try {
					
					bm.execute();
					System.out.println(args[i] + ": Benchmark executed successfully.");
					
				} catch (IOException e) {
					System.out.println(args[i] + ": Error when writing result file.");
				}
			}
		}
	}

}
