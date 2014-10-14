package co.icesi.i2t.Choco3Reasoner.tests;

import solver.Solver;
import solver.constraints.Constraint;
import solver.constraints.IntConstraintFactory;
import solver.variables.IntVar;
import solver.variables.VariableFactory;

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
//		 solver.set(IntStrategyFactory.lexico_LB(new IntVar[]{x, y}));
		 // 5. Launch the resolution process
		 boolean solution = solver.findSolution();
		 System.out.println(x);
		 System.out.println(y);
		 System.out.println(constraint);
		 System.out.println(solution);
	}

}
