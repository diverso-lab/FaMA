package es.us.isa.FAMA.TestSuite;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.*;
import static org.junit.Assert.*;

public class ProductsQuestionTestSrc  {


	Question q;
	GenericFeatureModel fm;
	QuestionTrader qt;
	
	// Features
	private final Feature A = new Feature("A");
	private final Feature B = new Feature("B");
	private final Feature C = new Feature("C");
	private final Feature D = new Feature("D");
	private final Feature E = new Feature("E");
	private final Feature F = new Feature("F");
	private final Feature G = new Feature("G");
	private final Feature H = new Feature("H");
	private final Feature I = new Feature("I");
	@Before
    public void setUp() {}
    @After
    public void tearDown() {}
	
	private void products(String inputName, List<Product> products)
	{
		// Read input
		qt = new QuestionTrader();
		fm = (GenericFeatureModel) qt.openFile("noEFM-test-inputs/" + inputName);
		qt.setVariabilityModel(fm);
		qt.setCriteriaSelector("selected"); // We select a specific solver
		
		// Perform question
		Question q = qt.createQuestion("Products");
		try {
			@SuppressWarnings("unused")
			PerformanceResult pr = qt.ask(q);
		} catch (FAMAException e) {
			e.printStackTrace();
		}
		ProductsQuestion pq = (ProductsQuestion) q;

		// Show result
		int np = (int) pq.getNumberOfProducts();
		Iterator<Product> it = pq.getAllProducts().iterator();
		int i = 0;
		while (it.hasNext()){
			Product p = it.next();
			System.out.print("PRODUCT "+i+":");
			Iterator<GenericFeature> itFeats = p.getFeatures().iterator();
			while (itFeats.hasNext()){
				GenericFeature f = itFeats.next();
				System.out.print(f.getName() + ", ");
			}
//			int jmax = p.getNumberOfFeatures();
//			//System.out.println("Number of features = " + jmax);
//			for (int j = 0; j < jmax; j++){
//				System.out.print(" " + p.getFeature(j).getName());
//			}
			System.out.println("");
			i++;
		}
		
		// Check result
		assertEquals("The operation returns a wrong number of products", pq.getNumberOfProducts(), products.size());
		
		it = pq.getAllProducts().iterator();
		while (it.hasNext()){
			Product p = it.next();
			int j=0;
			boolean found=false;
			while (j<np && !found) {
				Product pe = products.get(j);
				if (p.equals(pe))
					found=true;
				else
					j++;
			}
			assertTrue("The operation returns an unexpected product", found);
		}
		
	}
	
	// RELATIONSHIPS
	

	// Test Case 1
	@Test
	public void testMandatory()
	{
		System.out.println("========= MANDATORY ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(B);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		
		this.products("relationships/mandatory/mandatory.fama",products);
	}
	
	// Test Case 2
	@Test
	public void testOptional()
	{
		System.out.println("========= OPTIONAL ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(B);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		
		this.products("relationships/optional/optional.fama",products);
	}
	
	// Test Case 3
	@Test
	public void testAlternative()
	{
		System.out.println("========= ALTERNATIVE ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(B);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(C);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		
		this.products("relationships/alternative/alternative.fama",products);
	}
	
	
	// Test Case 4
	@Test
	public void testOr()
	{
		System.out.println("========= OR ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(B);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(C);
		
		// Product 3
		Product p3= new Product();
		p3.addFeature(A);
		p3.addFeature(B);
		p3.addFeature(C);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		products.add(p3);
	
		this.products("relationships/or/or.fama",products);
	}
	
	// Test Case 5
	@Test
	public void testExcludes()
	{
		System.out.println("========= EXCLUDES ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(B);
		
		// Product 3
		Product p3= new Product();
		p3.addFeature(A);
		p3.addFeature(C);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		products.add(p3);
		
		this.products("relationships/excludes/excludes.fama",products);
	}
	
	// Test Case 6
	@Test
	public void testRequires()
	{
		System.out.println("========= REQUIRES ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(C);
		
		// Product 3
		Product p3= new Product();
		p3.addFeature(A);
		p3.addFeature(B);
		p3.addFeature(C);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		products.add(p3);
		
		this.products("relationships/requires/requires.fama",products);
	}
	
	
	// COUPLES OF RELATIONSHIPS

	// Test Case 7
	@Test
	public void testMandatoryOptional()
	{
		System.out.println("========= MANDATORY-OPTIONAL ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(B);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(B);
		p2.addFeature(D);
		
		// Product 3
		Product p3= new Product();
		p3.addFeature(A);
		p3.addFeature(B);
		p3.addFeature(C);
		p3.addFeature(E);
		
		// Product 4
		Product p4= new Product();
		p4.addFeature(A);
		p4.addFeature(B);
		p4.addFeature(C);
		p4.addFeature(D);
		p4.addFeature(E);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		products.add(p3);
		products.add(p4);
		
		this.products("relationships/mandatory-optional/mandatory-optional.fama",products);
	}

	// Test Case 8
	@Test
	public void testMandatoryOr()
	{
		System.out.println("========= MANDATORY-OR ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(B);
		p1.addFeature(F);
		p1.addFeature(G);
		p1.addFeature(D);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(B);
		p2.addFeature(C);
		p2.addFeature(F);
		
		// Product 3
		Product p3= new Product();
		p3.addFeature(A);
		p3.addFeature(B);
		p3.addFeature(C);
		p3.addFeature(D);
		p3.addFeature(F);
		p3.addFeature(G);
		
		// Product 4
		Product p4= new Product();
		p4.addFeature(A);
		p4.addFeature(B);
		p4.addFeature(D);
		p4.addFeature(E);
		p4.addFeature(F);
		p4.addFeature(G);
		
		// Product 5
		Product p5= new Product();
		p5.addFeature(A);
		p5.addFeature(B);
		p5.addFeature(D);
		p5.addFeature(E);
		p5.addFeature(G);
		
		// Product 6
		Product p6= new Product();
		p6.addFeature(A);
		p6.addFeature(B);
		p6.addFeature(C);
		p6.addFeature(E);
		p6.addFeature(F);
		
		// Product 7
		Product p7= new Product();
		p7.addFeature(A);
		p7.addFeature(B);
		p7.addFeature(C);
		p7.addFeature(E);
		
		// Product 8
		Product p8= new Product();
		p8.addFeature(A);
		p8.addFeature(B);
		p8.addFeature(C);
		p8.addFeature(D);
		p8.addFeature(E);
		p8.addFeature(F);
		p8.addFeature(G);
		
		// Product 9
		Product p9= new Product();
		p9.addFeature(A);
		p9.addFeature(B);
		p9.addFeature(C);
		p9.addFeature(D);
		p9.addFeature(E);
		p9.addFeature(G);
		
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		products.add(p3);
		products.add(p4);
		products.add(p5);
		products.add(p6);
		products.add(p7);
		products.add(p8);
		products.add(p9);
		
		
		this.products("relationships/mandatory-or/mandatory-or.fama",products);
	}
	
	// Test Case 9
	@Test
	public void testMandatoryAlternative()
	{
		System.out.println("========= MANDATORY-ALTERNATIVE ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(B);
		p1.addFeature(D);
		p1.addFeature(F);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(B);
		p2.addFeature(C);
		p2.addFeature(F);
		p2.addFeature(G);
		
		// Product 3
		Product p3= new Product();
		p3.addFeature(A);
		p3.addFeature(B);
		p3.addFeature(D);
		p3.addFeature(E);
		
		// Product 4
		Product p4= new Product();
		p4.addFeature(A);
		p4.addFeature(B);
		p4.addFeature(C);
		p4.addFeature(E);
		p4.addFeature(G);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		products.add(p3);
		products.add(p4);
		
		this.products("relationships/mandatory-alternative/mandatory-alternative.fama",products);
	}
	
	// Test Case 10
	@Test
	public void testMandatoryRequires()
	{
		System.out.println("========= MANDATORY-REQUIRES ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(B);
		p1.addFeature(C);

		List<Product> products = new ArrayList<Product>();
		products.add(p1);

		this.products("relationships/mandatory-requires/mandatory-requires.fama",products);
	}
	
	// Test Case 11
	@Test
	public void testMandatoryExcludes()
	{
		System.out.println("========= MANDATORY-EXCLUDES ===========");
		
		List<Product> products = new ArrayList<Product>();
		
		this.products("relationships/mandatory-excludes/mandatory-excludes.fama",products);
	}
	
	// Test Case 12
	@Test
	public void testOptionalOr()
	{
		System.out.println("========= OPTIONAL-OR ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(D);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(C);
		p2.addFeature(D);
		p2.addFeature(G);
		
		// Product 3
		Product p3= new Product();
		p3.addFeature(A);
		p3.addFeature(C);
		p3.addFeature(D);
		
		// Product 4
		Product p4= new Product();
		p4.addFeature(A);
		p4.addFeature(C);
		p4.addFeature(G);
		
		// Product 5
		Product p5= new Product();
		p5.addFeature(A);
		p5.addFeature(C);
		
		// Product 6
		Product p6= new Product();
		p6.addFeature(A);
		p6.addFeature(B);
		p6.addFeature(D);
		p6.addFeature(F);
		
		// Product 7
		Product p7= new Product();
		p7.addFeature(A);
		p7.addFeature(B);
		p7.addFeature(C);
		p7.addFeature(D);
		p7.addFeature(F);
		p7.addFeature(G);
		
		// Product 8
		Product p8= new Product();
		p8.addFeature(A);
		p8.addFeature(B);
		p8.addFeature(C);
		p8.addFeature(D);
		p8.addFeature(F);
		
		// Product 9
		Product p9= new Product();
		p9.addFeature(A);
		p9.addFeature(B);
		p9.addFeature(C);
		p9.addFeature(F);
		p9.addFeature(G);
		
		// Product 10
		Product p10= new Product();
		p10.addFeature(A);
		p10.addFeature(B);
		p10.addFeature(C);
		p10.addFeature(F);

		// Product 11
		Product p11= new Product();
		p11.addFeature(A);
		p11.addFeature(B);
		p11.addFeature(D);
		p11.addFeature(E);
		p11.addFeature(F);

		// Product 12
		Product p12= new Product();
		p12.addFeature(A);
		p12.addFeature(B);
		p12.addFeature(D);
		p12.addFeature(E);

		// Product 13
		Product p13= new Product();
		p13.addFeature(A);
		p13.addFeature(B);
		p13.addFeature(C);
		p13.addFeature(D);
		p13.addFeature(E);
		p13.addFeature(F);
		p13.addFeature(G);
		
		// Product 14
		Product p14= new Product();
		p14.addFeature(A);
		p14.addFeature(B);
		p14.addFeature(C);
		p14.addFeature(D);
		p14.addFeature(E);
		p14.addFeature(F);

		// Product 15
		Product p15= new Product();
		p15.addFeature(A);
		p15.addFeature(B);
		p15.addFeature(C);
		p15.addFeature(E);
		p15.addFeature(F);
		p15.addFeature(G);
		
		// Product 16
		Product p16= new Product();
		p16.addFeature(A);
		p16.addFeature(B);
		p16.addFeature(C);
		p16.addFeature(E);
		p16.addFeature(F);

		// Product 17
		Product p17= new Product();
		p17.addFeature(A);
		p17.addFeature(B);
		p17.addFeature(C);
		p17.addFeature(D);
		p17.addFeature(E);
		p17.addFeature(G);
		
		// Product 18
		Product p18= new Product();
		p18.addFeature(A);
		p18.addFeature(B);
		p18.addFeature(C);
		p18.addFeature(D);
		p18.addFeature(E);
		
		// Product 19
		Product p19= new Product();
		p19.addFeature(A);
		p19.addFeature(B);
		p19.addFeature(C);
		p19.addFeature(E);
		p19.addFeature(G);
		
		// Product 20
		Product p20= new Product();
		p20.addFeature(A);
		p20.addFeature(B);
		p20.addFeature(C);
		p20.addFeature(E);
		
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		products.add(p3);
		products.add(p4);
		products.add(p5);
		products.add(p6);
		products.add(p7);
		products.add(p8);
		products.add(p9);
		products.add(p10);
		products.add(p11);
		products.add(p12);
		products.add(p13);
		products.add(p14);
		products.add(p15);
		products.add(p16);
		products.add(p17);
		products.add(p18);
		products.add(p19);
		products.add(p20);
		
		this.products("relationships/optional-or/optional-or.fama",products);
	}
	
	// Test Case 13
	@Test
	public void testOptionalAlternative()
	{
		System.out.println("========= OPTIONAL-ALTERNATIVE ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(D);
		p1.addFeature(G);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(D);
		
		// Product 3
		Product p3= new Product();
		p3.addFeature(A);
		p3.addFeature(C);
		
		// Product 4
		Product p4= new Product();
		p4.addFeature(A);
		p4.addFeature(B);
		p4.addFeature(D);
		p4.addFeature(F);
		p4.addFeature(G);
		
		// Product 5
		Product p5= new Product();
		p5.addFeature(A);
		p5.addFeature(B);
		p5.addFeature(F);
		p5.addFeature(D);
		
		// Product 6
		Product p6= new Product();
		p6.addFeature(A);
		p6.addFeature(B);
		p6.addFeature(C);
		p6.addFeature(F);
		
		// Product 7
		Product p7= new Product();
		p7.addFeature(A);
		p7.addFeature(B);
		p7.addFeature(D);
		p7.addFeature(E);
		p7.addFeature(G);
		
		// Product 8
		Product p8= new Product();
		p8.addFeature(A);
		p8.addFeature(B);
		p8.addFeature(D);
		p8.addFeature(E);
		
		// Product 9
		Product p9= new Product();
		p9.addFeature(A);
		p9.addFeature(B);
		p9.addFeature(C);
		p9.addFeature(E);
	
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		products.add(p3);
		products.add(p4);
		products.add(p5);
		products.add(p6);
		products.add(p7);
		products.add(p8);
		products.add(p9);
		
		this.products("relationships/optional-alternative/optional-alternative.fama",products);
	}
	
	// Test Case 14
	@Test
	public void testOrAlternative()
	{
		System.out.println("========= OR-ALTERNATIVE ===========");
		
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(C);
		p1.addFeature(D);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(C);
		p2.addFeature(D);
		p2.addFeature(E);
		p2.addFeature(H);
		
		// Product 3
		Product p3= new Product();
		p3.addFeature(A);
		p3.addFeature(C);
		p3.addFeature(E);
		p3.addFeature(H);
		
		// Product 4
		Product p4= new Product();
		p4.addFeature(A);
		p4.addFeature(C);
		p4.addFeature(D);
		p4.addFeature(E);
		p4.addFeature(I);
		
		// Product 5
		Product p5= new Product();
		p5.addFeature(A);
		p5.addFeature(C);
		p5.addFeature(E);
		p5.addFeature(I);
		
		// Product 6
		Product p6= new Product();
		p6.addFeature(A);
		p6.addFeature(B);
		p6.addFeature(D);
		p6.addFeature(G);
		
		// Product 7
		Product p7= new Product();
		p7.addFeature(A);
		p7.addFeature(B);
		p7.addFeature(D);
		p7.addFeature(E);
		p7.addFeature(G);
		p7.addFeature(H);

		// Product 8
		Product p8= new Product();
		p8.addFeature(A);
		p8.addFeature(B);
		p8.addFeature(E);
		p8.addFeature(G);
		p8.addFeature(H);
		
		// Product 9
		Product p9= new Product();
		p9.addFeature(A);
		p9.addFeature(B);
		p9.addFeature(D);
		p9.addFeature(E);
		p9.addFeature(G);
		p9.addFeature(I);
		
		// Product 10
		Product p10= new Product();
		p10.addFeature(A);
		p10.addFeature(B);
		p10.addFeature(E);
		p10.addFeature(G);
		p10.addFeature(I);

		// Product 11
		Product p11= new Product();
		p11.addFeature(A);
		p11.addFeature(B);
		p11.addFeature(D);
		p11.addFeature(F);
		p11.addFeature(G);

		// Product 12
		Product p12= new Product();
		p12.addFeature(A);
		p12.addFeature(B);
		p12.addFeature(D);
		p12.addFeature(F);

		// Product 13
		Product p13= new Product();
		p13.addFeature(A);
		p13.addFeature(B);
		p13.addFeature(D);
		p13.addFeature(E);
		p13.addFeature(F);
		p13.addFeature(G);		
		p13.addFeature(H);	

		// Product 14
		Product p14= new Product();
		p14.addFeature(A);
		p14.addFeature(B);
		p14.addFeature(F);
		p14.addFeature(E);
		p14.addFeature(G);
		p14.addFeature(H);	
		
		// Product 15
		Product p15= new Product();
		p15.addFeature(A);
		p15.addFeature(B);
		p15.addFeature(D);
		p15.addFeature(E);
		p15.addFeature(F);
		p15.addFeature(H);	
		
		// Product 16
		Product p16= new Product();
		p16.addFeature(A);
		p16.addFeature(B);
		p16.addFeature(F);
		p16.addFeature(E);
		p16.addFeature(H);

		// Product 17
		Product p17= new Product();
		p17.addFeature(A);
		p17.addFeature(B);
		p17.addFeature(D);
		p17.addFeature(E);
		p17.addFeature(F);
		p17.addFeature(G);	
		p17.addFeature(I);
		
		// Product 18
		Product p18= new Product();
		p18.addFeature(A);
		p18.addFeature(B);
		p18.addFeature(E);
		p18.addFeature(F);
		p18.addFeature(G);	
		p18.addFeature(I);

		// Product 19
		Product p19= new Product();
		p19.addFeature(A);
		p19.addFeature(B);
		p19.addFeature(D);
		p19.addFeature(E);
		p19.addFeature(F);	
		p19.addFeature(I);
		
		// Product 20
		Product p20= new Product();
		p20.addFeature(A);
		p20.addFeature(B);
		p20.addFeature(E);
		p20.addFeature(F);	
		p20.addFeature(I);
		
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		products.add(p3);
		products.add(p4);
		products.add(p5);
		products.add(p6);
		products.add(p7);
		products.add(p8);
		products.add(p9);
		products.add(p10);
		products.add(p11);
		products.add(p12);
		products.add(p13);
		products.add(p14);
		products.add(p15);
		products.add(p16);
		products.add(p17);
		products.add(p18);
		products.add(p19);
		products.add(p20);
		
		this.products("relationships/or-alternative/or-alternative.fama",products);
	}
	
	// Test Case 15
	@Test
	public void testOrRequires()
	{
		System.out.println("========= OR-REQUIRES ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(C);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(B);
		p2.addFeature(C);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		
		this.products("relationships/or-requires/or-requires.fama", products);
	}
	
	// Test Case 16
	@Test
	public void testOrExcludes()
	{
		System.out.println("========= OR-EXCLUDES ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(B);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(C);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		
		this.products("relationships/or-excludes/or-excludes.fama",products);
	}
	
	// Test Case 17
	@Test
	public void testAlternativeRequires()
	{
		System.out.println("========= ALTERNATIVE-REQUIRES ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(C);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		
		this.products("relationships/alternative-requires/alternative-requires.fama",products);
	}
	
	// Test Case 18
	@Test
	public void testAlternativeExcludes()
	{
		System.out.println("========= ALTERNATIVE-EXCLUDES ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(B);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(C);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		
		this.products("relationships/alternative-excludes/alternative-excludes.fama",products);
	}
	
	// Test Case 19
	@Test
	public void testRequiresExcludes()
	{
		System.out.println("========= REQUIRES-EXCLUDES ===========");
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(C);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		
		this.products("relationships/requires-excludes/requires-excludes.fama",products);
	}
	
	// Test Case 20
	@Test
	public void testAllRelationships()
	{
		System.out.println("========= ALL RELATIONSHIPS ===========");
		
		
		// Product 1
		Product p1= new Product();
		p1.addFeature(A);
		p1.addFeature(B);
		p1.addFeature(D);

		// Product 2
		Product p2= new Product();
		p2.addFeature(A);
		p2.addFeature(B);
		p2.addFeature(C);
		p2.addFeature(D);
		p2.addFeature(F);
		
		// Product 3
		Product p3= new Product();
		p3.addFeature(A);
		p3.addFeature(B);
		p3.addFeature(C);
		p3.addFeature(E);
		p3.addFeature(F);
		
		// Product 4
		Product p4= new Product();
		p4.addFeature(A);
		p4.addFeature(B);
		p4.addFeature(C);
		p4.addFeature(E);
		p4.addFeature(F);
		p4.addFeature(G);
		
		// Product 5
		Product p5= new Product();
		p5.addFeature(A);
		p5.addFeature(B);
		p5.addFeature(C);
		p5.addFeature(E);
		
		List<Product> products = new ArrayList<Product>();
		products.add(p1);
		products.add(p2);
		products.add(p3);
		products.add(p4);
		
		this.products("relationships/allrelationships/allrelationships.fama",products);
	}
	
	
}
