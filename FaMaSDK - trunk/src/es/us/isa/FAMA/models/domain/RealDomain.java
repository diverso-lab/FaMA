package es.us.isa.FAMA.models.domain;

import java.util.HashSet;
import java.util.Set;

public class RealDomain extends Domain {

	private double upperBound;
	private double lowerBound;
	
	public RealDomain(double lower, double upper){
		upperBound = upper;
		lowerBound = lower;
	}
	
	@Override
	public Set<Integer> getAllIntegerValues() {
		return new HashSet<Integer>();
	}

	@Override
	public Object getValue(int i) {
		return i;
	}

	@Override
	public Integer getIntegerValue(Object o) {
		if (o instanceof Double){
			return (int)Math.round((Double)o);
		}
		else{
			return null;
		}
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}
	
	

}
