package es.us.isa.FAMA.Benchmarking;

/**
 * @author  Dani
 */
public abstract class PerformanceResult {
	/**
	 * @uml.property  name="time"
	 */
	long time;
	
	/**
	 * @param time
	 * @uml.property  name="time"
	 */
	public void setTime(long time) {
		this.time = time;
	}
	
	/**
	 * @return
	 * @uml.property  name="time"
	 */
	public long getTime() {
		return time;
	}
}
