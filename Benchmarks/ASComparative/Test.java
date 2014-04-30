
import util.Memory;

import experiments.*;

public class Test {

	public static void main(String[] args) throws Exception {

		// Create a new experiment
		Experiment exp = new Experiment();
		exp.setName("Exp01"); // Name
		exp.setW(5);  // Maximun number of children relations for a node
		exp.setH(3);  // Maximun height of the tree
		exp.setE(4);  // 
		exp.setD(51); // Number of dependencies
		exp.setGeneratorSeed(-1814029828);

		FMFacade fm = new FMFacade();
		fm.monitorMemory(false);
		
		fm.generateJacop(false);
		fm.generateSAT(false);
		fm.generateBDD(true);
		fm.generate(exp);
		
		
		fm.getOneSolution();
		fm.getNumberOfSolutionsJavaBDD();
		
		// Remove references
		fm.reset();
		Memory.gc();
		
		/*================= ATOMIC SETS ================ */
		
		fm.monitorMemory(false);
		
		fm.generateJacopAS(true);
		fm.generateSATAS(true);
		fm.generateBDDAS(true);
		fm.generateAS(true);

		fm.generate(exp);
		
		if (!fm.checkAS())
			throw new Exception("ERROR:  Los atomic set no coinciden");
		
		fm.getOneSolutionJacopAS();
		fm.getOneSolutionSATAS();
		fm.getOneSolutionJavaBDDAS();
		fm.getNumberOfSolutionsJavaBDDAS();
		
		// SAVE RESULTS
		fm.saveResults(false);
		fm.saveCSVResults();
		
	
	}
}
