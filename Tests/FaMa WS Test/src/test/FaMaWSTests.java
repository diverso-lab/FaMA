package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.ws.FaMaWSPortType;
import es.us.isa.FAMA.ws.FaMaWSPortTypeProxy;
import es.us.isa.FAMA.ws.auxiliar.ErrorProxy;
import es.us.isa.FAMA.ws.auxiliar.ExplanationProxy;
import es.us.isa.FAMA.ws.auxiliar.ProductProxy;

public class FaMaWSTests {

	private FaMaWSPortType fama;

	@Before
	public void setUp() throws Exception {
		fama = new FaMaWSPortTypeProxy("http://localhost:8082/FaMaWS");
	}

	// BASIC TESTS

	@Test
	public void testValidBasic() throws RemoteException {
		byte[] model = model2bytes(new File("fm-samples/HIS.fm"));
		boolean b;
		b = fama.isValid(model);
		assert b;
	}

	@Test
	public void testNumberOfProducts() throws RemoteException {
		byte[] model = model2bytes(new File("fm-samples/HIS.fm"));
		long res = fama.getNumberOfProducts(model);
		System.out.println(res);
	}
	
	@Test
	public void testProducts() throws RemoteException {
		byte[] model = model2bytes(new File("fm-samples/HIS.fm"));
		ProductProxy[] res = fama.getProducts(model);
		for (ProductProxy p:res){
			String[] features = p.getFeatures();
			for (String f:features){
				System.out.print(f+" ");
			}
			System.out.println();
		}
	}
	
	@Test
	public void testCommonality() throws RemoteException{
		byte[] model = model2bytes(new File("fm-samples/HIS.xml"));
		long res = fama.getCommonality(model, "HIS");
		System.out.println("Commonality = "+res);
	}
	
	@Test
	public void testVariability() throws RemoteException{
		byte[] model = model2bytes(new File("fm-samples/HIS.fm"));
		float res = fama.getVariability(model);
		System.out.println("Variability = "+res);
	}
	
	@Test
	public void testCoreFeatures() throws RemoteException{
		byte[] model = model2bytes(new File("fm-samples/HIS.xml"));
		String[] res = fama.getCoreFeatures(model);
		System.out.println("Core features");
		for (String f:res){
			System.out.println(f);
		}
	}
	
	@Test
	public void testVariantFeatures() throws RemoteException{
		byte[] model = model2bytes(new File("fm-samples/HIS.fm"));
		String[] res = fama.getVariantFeatures(model);
		System.out.println("Variant features");
		for (String f:res){
			System.out.println(f);
		}
	}
	
	
	@Test
	public void testDetectAndExplainErrors() throws RemoteException{
		byte[] model = model2bytes(new File("fm-samples/ErrorsExample.xml"));
		ErrorProxy[] errors = fama.detectAndExplainErrors(model);
		for (ErrorProxy e:errors){
			System.out.println("Error");
			ExplanationProxy[] exps = e.getExplanations();
			for (ExplanationProxy exp:exps){
				String[] rels = exp.getRelationships();
				for (String s:rels){
					System.out.print(s+" ");
				}
				System.out.println();
			}
		}
	}
	
	@Test
	public void validProduct() throws RemoteException{
		byte[] model = model2bytes(new File("fm-samples/test.fama"));
		ProductProxy p = new ProductProxy();
		String[] feats = {"HIS","SUPERVISION_SYSTEM","CONTROL","FIRE",
				"INTRUSION","LIGHT_CONTROL","TEMPERATURE"};
		p.setFeatures(feats);
		boolean b = fama.isValidProduct(model, p); 
		assert b;
	}
	
	@Test
	public void repairProduct() throws RemoteException{
		byte[] model = model2bytes(new File("fm-samples/HIS.fm"));
		ProductProxy p = new ProductProxy();
		String[] feats = {"HIS","SUPERVISION_SYSTEM","CONTROL","FIRE",
				"INTRUSION","LIGHT_CONTROL"};
		p.setFeatures(feats);
		ProductProxy aux = fama.productRepair(model, p); 
		String[] features = aux.getFeatures();
		System.out.println("Product repair");
		for (String s:features){
			System.out.print(s+" ");
		}
	}

	// END BASIC TESTS

	// EXTENDED TESTS

	// END EXTENDED TESTS

	private byte[] model2bytes(File f) {
		int size = (int) f.length();
		byte[] res = new byte[size];
		InputStream in;
		try {
			in = new FileInputStream(f);
			in.read(res);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;

	}

}
