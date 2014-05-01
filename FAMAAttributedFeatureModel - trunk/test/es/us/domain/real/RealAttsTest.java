//package es.us.domain.real;
//
//import static org.junit.Assert.*;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
//import es.us.isa.FAMA.models.domain.RealDomain;
//import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
//import es.us.isa.FAMA.parser.FMFParser;
//
//public class RealAttsTest {
//
//	private FMFParser parser;
//	
//	@Before
//	public void setUp(){
//		parser = new FMFParser();
//	}
//	
//	@Test
//	public void testRealIaaS() {
//		testCloudModel("./test-files/Computing.afm");
//	}
//	
//	@Test
//	public void testRealAttAWS() {
//		testCloudModel("./test-files/AmazonEC2Atts.afm");
//	}
//	
//	@Test
//	public void testRealAttAzure() {
//		testCloudModel("./test-files/AzureComputing.afm");
//	}
//	
//	@Test
//	public void testRealAttRackspace() {
//		testCloudModel("./test-files/RackspaceComputing.afm");
//	}
//	
//	@Test
//	public void testRealAtt() {
//		FAMAAttributedFeatureModel fm = parser.parseModel("./test-files/FMF-test2.afm");
//		System.out.println(fm);
//		GenericAttribute att = fm.searchAttributeByName("Wifi.cost");
//		assertTrue(att.getDomain() instanceof RealDomain);
//		
//	}
//	
//	private void testCloudModel(String path){
//		FAMAAttributedFeatureModel fm = parser.parseModel(path);
////		FAMAAttributedFeatureModel fm = parser.parseModel("./test-files/AmazonEC2.afm");
////		System.out.println(fm);
//		GenericAttribute att = fm.searchAttributeByName("VM.costHour");
//		assertTrue(att.getDomain() instanceof RealDomain);
//	}
//
//}
