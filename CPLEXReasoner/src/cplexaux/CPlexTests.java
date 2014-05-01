package cplexaux;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cp.IloCP;

public class CPlexTests {

	public static void main(String[] args) {
		IloCP cp = new IloCP();
		
		try {
			IloIntVar v1 = cp.intVar(0, 10);
			IloIntVar v2 = cp.intVar(0, 10);
			
			cp.add(cp.eq(cp.sum(v1,v2), 15));
			cp.add(cp.maximize(v1));
			
			int i = 0;
//			cp.solve();
			cp.startNewSearch();
			
			while(cp.next()){
				int val = (int)cp.getValue(v1);
				System.out.println("Sol "+i+": "+val);
//				cp.add(cp.neq(v2, val));
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
		
	}

}
