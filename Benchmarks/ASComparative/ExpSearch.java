import util.UtilProp;
import experiments.ExperimentSearch;

public class ExpSearch {


	public static void main(String[] args) {
		
		ExperimentSearch expsearch=new ExperimentSearch(50,300,500);
		
		// Read the path in which the experiments will be saved
		String path=UtilProp.getProperty("experimentsCSVPath");
		
		expsearch.search(path);

	}

}
