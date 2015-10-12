package es.us.isa.benchmarking.generators;

import es.us.isa.benchmarking.FAMABenchmark;

public class Timer implements Runnable {

	private Experiment exp;
	private String reasoner;
	private String question;
	private FAMABenchmark bench;
	private Thread hilo;
	private boolean imfisnished;

	// Crea el temporizador con una hora por defecto
	public Timer() {
		hilo = new Thread(this);
		imfisnished = false;
		hilo.start();
	}

	public Timer(int time) {
		hilo = new Thread(this);
		imfisnished = false;
		hilo.start();

	}

	public Timer(Experiment exp, String question,
			String reasoner, FAMABenchmark bench) {
		hilo = new Thread(this);
		hilo.setPriority(Thread.MAX_PRIORITY);
		imfisnished = false;
		this.exp = exp;
		this.question = question;
		this.reasoner = reasoner;
		this.bench = bench;
		
		hilo.start();
	}

	@Override
	public void run() {
		bench.executeWithArgs(exp, question, reasoner);
		imfisnished = true;

	}

	public boolean getFinished() {
		return imfisnished;
	}

	public boolean isAlive() {
		return hilo.isAlive();

	}

	public void kill() {
		hilo.destroy();
	}

}
