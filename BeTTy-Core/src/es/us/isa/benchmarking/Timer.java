/**
 * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.benchmarking;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import es.us.isa.FAMA.models.featureModel.Product;

/**
 * This class if for internal use os the tool, basiclly it creates a thread per
 * questions to allow the benefit of this tool from multicore processors
 */
public class Timer implements Runnable {

	private Experiment exp; // Experiment
	private String reasoner; // Reasoner
	private String question; // Question
	private FAMABenchmark bench; // FAMA benchmark facade
	private Thread thread; // Thread
	private boolean finished; // Indicate whether the execution finished in time
	private long maxTime; // Max time to wait before killing the thread
	private long time; // Elapsed time
	private long memory; // Memory usage
	private long executionTime; // Execution time
	private Product product = null; // Product

	public long getExecutionTime() {
		return executionTime;
	}

	public Timer(Experiment exp, String question, String reasoner,
			FAMABenchmark bench) throws Exception {
		thread = new Thread(this);
		thread.setPriority(Thread.MAX_PRIORITY);
		this.finished = false;
		this.time = -1;
		this.exp = exp;
		this.question = question;
		this.reasoner = reasoner;
		this.bench = bench;
		this.memory = -1;

	}

	@SuppressWarnings("deprecation")
	public void execute() throws InterruptedException {

		// Start the thread
		thread.start();

		// Wait until the thread end or maxTime
		thread.join(maxTime);

		// If the thread didn't end, destroy it
		if (!finished)
			thread.stop();

		thread = null;
	}

	public void run() {

		try {
			// Perform question
			long startTime = getCpuTime();
			Map<String, String> results = bench.executeWithArgs(exp, question,
					reasoner, product);

			time = getCpuTime() - startTime;

			// Save CPU time
			results.put(reasoner + "ElapsedTime", String.valueOf(time));
			exp.addResult(results);
			executionTime = Long.parseLong(results.get(reasoner + ":time"));
			// Experiment finished
			finished = true;

		} catch (Exception e) {
			System.err.println("Error when running the thread");
		}
	}

	// Get CPU time in milliseconds.
	private long getCpuTime() {
		long res = -1;
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		if (bean.isThreadCpuTimeEnabled())
			res = TimeUnit.MILLISECONDS.convert(bean.getThreadCpuTime(thread
					.getId()), TimeUnit.NANOSECONDS);

		return res;
	}

	public boolean hasFinished() {
		return finished;
	}

	public long getElapsedTime() {
		return time;
	}

	public void setTimeOut(long time) {
		maxTime = time;
	}

	public void setProduct(Product p) {
		this.product = p;
	}

	// Return memory usage in bytes
	public long getMemoryUsage() {
		// TODO Auto-generated method stub
		return this.memory / 1024;
	}

}
