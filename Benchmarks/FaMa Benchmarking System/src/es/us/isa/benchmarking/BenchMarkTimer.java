package es.us.isa.benchmarking;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.models.variabilityModel.parsers.WrongFormatException;
import es.us.isa.benchmarking.generators.ExactRandomGenerator;
import es.us.isa.benchmarking.generators.Experiment;
import es.us.isa.benchmarking.generators.FMCharacteristics;
import es.us.isa.benchmarking.generators.Timer;
import es.us.isa.benchmarking.readers.XMLReader;

public class BenchMarkTimer {

	String path4res;
	FileOutputStream os;
	PrintStream ps;

	String[] questions = { "Valid" };
	String[] reasoners = { "Sat4j" };

	Integer[] feats = { 50, 100, 150, 200, 250, 300, 350, 400, 450, 600, 700,
			800, 900, 1000 };
	Float[] ctc = { 0.00f, 0.10f, 0.25f };
	Integer[] ctcInt = { 0, 10, 25 };
	FAMABenchmark bench;

	public BenchMarkTimer(String path4res) throws FileNotFoundException {
		this.path4res = path4res;
		os = new FileOutputStream("myLog.txt");
		ps = new PrintStream(os);
		System.setErr(ps);
		bench = new FAMABenchmark();
	}

	public BenchMarkTimer(String path4res, String[] questions,
			String[] reasoners) throws FileNotFoundException {
		this.path4res = path4res;
		os = new FileOutputStream("myLog.txt");
		ps = new PrintStream(os);
		System.setErr(ps);
		this.questions = questions;
		this.reasoners = reasoners;
		bench = new FAMABenchmark();
	}

	public Collection<Experiment> generate(float percentageOfAlternate,
			float percentageOfMandatories, float percentageOfOptionals,
			float percentageOfOrs) throws InterruptedException, IOException,
			WrongFormatException {

		Collection<Experiment> col = new ArrayList<Experiment>();

		// Generate all Experiments
		ExactRandomGenerator generator = new ExactRandomGenerator();
		generator.setPercentageOfAlternate(percentageOfAlternate);
		generator.setPercentageOfMandatories(percentageOfMandatories);
		generator.setPercentageOfOptionals(percentageOfOptionals);
		generator.setPercentageOfOrs(percentageOfOrs);
		bench.setGenerator(generator);

		for (int feat = 0; feat < feats.length; feat++) {
			for (int cc = 0; cc < ctc.length; cc++) {
				FMCharacteristics chars = new FMCharacteristics(30, 30, 10, -1,
						ctc[cc], feats[feat], 0, feats[feat], -123456);
				// OK, then save the vm`s for later use
				ArrayList<Experiment> colTmp = bench.createSetRandomExperiment(
						10, chars);
				for (int i = 0; i < 10; i++) {
					// Meter el nombre del emperimento y sus avios(que la v se
					// le sume al seed en cada caracteristica(en el generate)).

					colTmp.get(i).setName(
							"" + feats[feat] + "-" + ctcInt[cc] + "-" + i);
					bench.saveVariabilityModel(colTmp.get(1)
							.getVariabilityModel(), path4res + "/"
							+ colTmp.get(i).getName() + ".xml");

				}

				// Add to the colection of exps
				col.addAll(colTmp);
			}
		}
		return col;
	}

	public void start() {

		Timer time = null;
		long startTime;
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		for (int feat = 0; feat < feats.length; feat++) {
			for (int cc = 0; cc < ctc.length; cc++) {
				for (int i = 0; i < 10; i++) {
					// Lets get the fms from the disk
					System.out.println(path4res+"/"+feats[feat]
							+ "-" + ctcInt[cc] + "-" + i + ".xml");
					Experiment exp = bench.loadVariabilityModel(path4res+"\\"+feats[feat]
							+ "-" + ctcInt[cc] + "-" + i + ".xml");
					int errorNo = 0;
					for (int question = 0; question < questions.length; question++) {
						for (int reasoner = 0; reasoner < reasoners.length; reasoner++) {

							startTime = System.currentTimeMillis();

							time = new Timer(exp, questions[question],
									reasoners[reasoner], bench);

							while (time.isAlive()) {// una hora

								if (System.currentTimeMillis() - startTime < 3600000) {
									Thread.yield();
								} else {
									time.kill();
								}

							}

							long stopTime = System.currentTimeMillis();
							System.out.println("El experimento "
									+ exp.getName());
							if (exp.getResults().size() > 0) {
								// Se ha acabado el experimento.
								System.out.println("duró: "
										+ (stopTime - startTime));
							} else {
								System.out.println("dió un error");
								errorNo++;
							}
							if (errorNo > 5) {
								reasoner=reasoners.length;
							}
						}
					}

				}
			}
		}

	}
}
