import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import util.Memory;
import util.UtilFile;
import util.UtilProp;

import experiments.*;

public class Test2 {

	public static void main(String[] args) throws Exception {

		ArrayList<Experiment> exps = null;
		ExperimentReader expr = new ExperimentReader();
		String path = UtilProp.getProperty("experimentsCSVPath");
		exps = expr.readExperiments(path); // Read experiments

		// 0%, 5%, 10%, 20% and 25% cross-tree constraints
		ArrayList<Float> constraints= new ArrayList<Float>();
		//constraints.add((float)(0));	// 0%
		//constraints.add((float)(0.05)); // 5%
		//constraints.add((float)(0.1));  // 10%
		//constraints.add((float)(0.15)); // 15%
		//constraints.add((float)(0.2));  // 20%
		constraints.add((float)(0.25)); // 25%
			
		// Starting experiment
		int current_exp=31;
		
		Iterator itc = constraints.iterator();
		while (itc.hasNext()) {
			
			float percentage= ((Float)itc.next()).floatValue(); // Percentage of dependencies
			int i = 0;											// Number of experiment
			Experiment exp = null;
			Iterator ite = exps.iterator();
			while (ite.hasNext()) {
				
				// Get experiment
				exp = (Experiment) ite.next();
					
					if (i>=current_exp) { // We move to the desired experiment
						
						current_exp = 0;  // Once we start with the experiment we disable the selector.
						
						// work out the number of dependencies
						int nf=exp.getFeatureNumber();
						int d = (int) (nf*percentage);
						
						// Experiment 
						exp.setName("Exp" + i + "-" + d);
						exp.setD(d);
						
						System.out.println("Nombre Exp:" + "Exp" + i + "-" + d);
						System.out.println("i: " + i);
						System.out.println("Número features: "  +exp.getFeatureNumber());
						System.out.println("d:" + d);
		
						
						/*================ NO ATOMIC SETS ============= */
						FMFacade fm = new FMFacade();
						fm.monitorMemory(true);
						
						fm.generateJacop(true);
						fm.generateBDD(true);
						fm.generateSAT(true);
						fm.generate(exp);
						
						fm.getOneSolutionJacop();
						fm.getOneSolutionSAT();
						fm.getOneSolutionJavaBDD();
						fm.getNumberOfSolutionsJavaBDD();	
						
						// Remove references
						fm.reset();
						Memory.gc();
						
						/*================= ATOMIC SETS ================ */
						
						fm.monitorMemory(true);
		
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
			
			i++;
			}
			
			// Delete snapshots
			path = UtilProp.getProperty("snapshotsPath");
			if (!UtilFile.deleteFiles(new File(path)))
				throw new Exception("ERROR: No se pudieron eliminar los snapshots");
	
		}
	}
}
