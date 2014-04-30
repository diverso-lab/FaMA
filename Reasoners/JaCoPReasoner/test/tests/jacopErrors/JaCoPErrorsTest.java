/**
 * 	This file is part of FaMaTS.
 *
 *     FaMaTS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FaMaTS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */

///**
// * 	This file is part of FaMaTS.
// *
// *     FaMaTS is free software: you can redistribute it and/or modify
// *     it under the terms of the GNU Lesser General Public License as published by
// *     the Free Software Foundation, either version 3 of the License, or
// *     (at your option) any later version.
// *
// *     FaMaTS is distributed in the hope that it will be useful,
// *     but WITHOUT ANY WARRANTY; without even the implied warranty of
// *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *     GNU Lesser General Public License for more details.
// *
// *     You should have received a copy of the GNU Lesser General Public License
// *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package tests.jacopErrors;
//
//import org.junit.Test;
//
//import JaCoP.constraints.IfThen;
//import JaCoP.constraints.PrimitiveConstraint;
//import JaCoP.constraints.XeqC;
//import JaCoP.core.FDV;
//import JaCoP.core.FDstore;
//import JaCoP.core.Variable;
//import JaCoP.search.DepthFirstSearch;
//import JaCoP.search.IndomainMin;
//import JaCoP.search.MostConstrainedDynamic;
//import JaCoP.search.Search;
//import JaCoP.search.SelectChoicePoint;
//import JaCoP.search.SimpleSelect;
//
//
//public class JaCoPErrorsTest {
//
//	@Test
//	public void test1(){
//		FDstore store = new FDstore();
//		FDV var1 = new FDV(store, "var1", 0, 1);
//		FDV var2 = new FDV(store, "var2", 0, 1);
//		
//		PrimitiveConstraint constraint1 = new XeqC(var1, 1);
//		PrimitiveConstraint constraint2 = new IfThen(new XeqC(var2, 1),
//				new XeqC(var1, 1));
//		PrimitiveConstraint constraint3 = new XeqC(var2, 1);
//		store.impose(constraint1);
//		store.impose(constraint2);
//		store.setLevel(store.level + 1);
//		store.impose(constraint3);
//		
//		boolean valid = store.consistency();
//		if (valid){
//			Search sa = new DepthFirstSearch();
//			FDV[] vars = {var1,var2};
//			SelectChoicePoint select = new SimpleSelect(vars,new MostConstrainedDynamic(), new IndomainMin());
//			valid = sa.labeling(store,select);
//		}
//		System.out.println(valid);
//	}
//	
//}
