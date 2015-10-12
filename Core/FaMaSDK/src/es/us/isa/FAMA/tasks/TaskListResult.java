package es.us.isa.FAMA.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class TaskListResult {

	Map<VariabilityModel,List<Task>> map;

	public TaskListResult() {
		super();
		map = new HashMap<VariabilityModel, List<Task>>();
	}
	
	public boolean addVariabilityModel(VariabilityModel vm){
		if (!map.containsKey(vm)){
			map.put(vm, new ArrayList<Task>());
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean removeVariabilityModel(VariabilityModel vm){
		if (map.containsKey(vm)){
			map.remove(vm);
			return true;
		}
		else{
			return false;
		}
	}
	
	public void addResults(VariabilityModel vm, List<Task> tasks){
		if (!map.containsKey(vm)){
			map.put(vm, tasks);
		}
		else{
			List<Task> existingTasks = map.get(vm);
			existingTasks.addAll(tasks);
		}
	}
	
	public void addResult(VariabilityModel vm, Task task){
		if (!map.containsKey(vm)){
			List<Task> auxList = new ArrayList<Task>();
			auxList.add(task);
			map.put(vm, auxList);
		}
		else{
			List<Task> existingTasks = map.get(vm);
			existingTasks.add(task);
		}
	}
	
}
