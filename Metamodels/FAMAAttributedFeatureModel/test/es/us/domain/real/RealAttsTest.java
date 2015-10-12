//package es.us.domain.real;
//
//import static org.junit.Assert.*;
//
//import org.junit.Test;
//
//import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
//import es.us.isa.FAMA.models.domain.RealDomain;
//import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
//import es.us.isa.FAMA.parser.FMFParser;
//
//public class RealAttsTest {
//
//	@Test
//	public void testRealAtt() {
//		FMFParser parser = new FMFParser();
//		FAMAAttributedFeatureModel fm = parser.parseModel("./test-files/FMF-test2.afm");
//		System.out.println(fm);
//		GenericAttribute att = fm.searchAttributeByName("Wifi.cost");
//		assertTrue(att.getDomain() instanceof RealDomain);
//		
//	}
//
//}
