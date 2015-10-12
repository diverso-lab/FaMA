package solvers.results;

import java.util.ArrayList;
import java.util.Collection;

public interface IResult {
	
	public long getTimeOneSolution();
	public void setTimeOneSolution(long timeOneSolution);
	public double getNumberOfSolutions();
	public void setNumberOfSolutions(double numberOfSolutions);
	public long getTimeAllSolutions();
	public void setTimeAllSolutions(long timeAllSolutions);
	public long getTimeNumberOfSolutions();
	public void setTimeNumberOfSolutions(long timeNumberOfSolutions);
	public long getMemoryUsageOneSolution();
	public void setMemoryUsageOneSolution(long mem);
	public long getMemoryUsageNumberOfSolutions();
	public void setMemoryUsageNumberOfSolutions(long mem);
	public long getMemoryUsageAllSolutions();
	public void setMemoryUsageAllSolutions(long mem);
	public String printResults();

}