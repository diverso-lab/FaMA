package es.us.isa.cpoptimizer.utils;

public class RankableItem<T> implements Comparable<RankableItem<T>> {

	private T item;
	private double value;
	
	public RankableItem(T item, double value){
		this.item = item;
		this.value = value;
	}
	
	public T getItem() {
		return item;
	}

	public void setItem(T item) {
		this.item = item;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int compareTo(RankableItem<T> arg0) {
		if (value > arg0.getValue()){
			return 1;
		}
		else if (value == arg0.getValue()){
			return 0;
		}
		else{
			return -1;
		}
	}

}
