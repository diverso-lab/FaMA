package es.us.isa.FAMA.tasks;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Question;

public class Task {

	private PerformanceResult performanceResult;
	private Question operation;
	
	public Task(){}
	
	public Task(Question op){
		operation = op;
		performanceResult = null;
	}
	
	public Task(PerformanceResult performanceResult, Question operation) {
		super();
		this.performanceResult = performanceResult;
		this.operation = operation;
	}
	
	public PerformanceResult getPerformanceResult() {
		return performanceResult;
	}
	public void setPerformanceResult(PerformanceResult performanceResult) {
		this.performanceResult = performanceResult;
	}
	public Question getOperation() {
		return operation;
	}
	public void setOperation(Question operation) {
		this.operation = operation;
	}
	
	
}
