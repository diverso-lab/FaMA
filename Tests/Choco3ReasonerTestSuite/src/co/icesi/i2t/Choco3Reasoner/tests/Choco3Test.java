package co.icesi.i2t.Choco3Reasoner.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.us.isa.FAMA.models.featureModel.Product;
import solver.Solver;
import solver.constraints.Constraint;
import solver.constraints.IntConstraintFactory;
import solver.search.solution.AllSolutionsRecorder;
import solver.search.solution.Solution;
import solver.variables.IntVar;
import solver.variables.Variable;
import solver.variables.VariableFactory;
import solver.variables.impl.IntervalIntVarImpl;

public class Choco3Test {

	public static void main(String[] args) {
		// 1. Create a Solver
		Solver solver = new Solver("my first problem");
		// 2. Create variables through the variable factory
		IntVar x = VariableFactory.bounded("X", 0, 5, solver);
		IntVar y = VariableFactory.bounded("Y", 0, 5, solver);
		// 3. Create and post constraints by using constraint factories
		Constraint constraint = IntConstraintFactory.arithm(x, "+", y, "<", 5);
		solver.post(constraint);
		// 4. Define the search strategy
		// solver.set(IntStrategyFactory.lexico_LB(new IntVar[]{x, y}));
		// 5. Launch the resolution process
		solver.set(new AllSolutionsRecorder(solver));
		long foundSolution = solver.findAllSolutions();
		System.out.println(x);
		System.out.println(y);
		System.out.println(constraint);
		System.out.println(foundSolution);
		System.out.println(solver);
		Collection<String> variables = new ArrayList<String>();
		AllSolutionsRecorder recorder = (AllSolutionsRecorder) solver.getSolutionRecorder();
		System.out.println("LS: " + recorder.getLastSolution());
		System.out.println(solver.getSolutionRecorder());
		List<Solution> solutions = solver.getSolutionRecorder().getSolutions();
		System.out.println(solutions.size());
		System.out.println(solutions);
		for (Solution solution : solutions) {
			System.out.println(solution);
			for (int i = 0; i < solver.getNbVars(); i++) {
				Variable variable = solver.getVar(i);
				if (variable instanceof IntVar) {
					if (solution.getIntVal((IntVar) variable) > 0) {
						variables.add(variable.getName());
					}
				}
			}
		}
		System.out.println(variables);
	}
}
