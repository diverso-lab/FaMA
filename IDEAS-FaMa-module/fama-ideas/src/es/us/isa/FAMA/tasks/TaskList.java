package es.us.isa.FAMA.tasks;

import java.util.List;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class TaskList {

	private List<Task> tasks;
	private List<VariabilityModel> models;
	
	public TaskList(){}
	
	public TaskList(List<Task> tasks, List<VariabilityModel> models) {
		super();
		this.tasks = tasks;
		this.models = models;
	}
	
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	public List<VariabilityModel> getModels() {
		return models;
	}
	public void setModels(List<VariabilityModel> models) {
		this.models = models;
	}
	
	
	
}
