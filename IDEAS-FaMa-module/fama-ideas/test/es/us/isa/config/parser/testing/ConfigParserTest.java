package es.us.isa.config.parser.testing;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.config.ConfigParser;
import es.us.isa.FAMA.models.config.ConfigParserResult;
import es.us.isa.FAMA.models.config.ExtendedConfigParser;
import es.us.isa.FAMA.parser.FMFParser;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.util.Tree;

public class ConfigParserTest {

	private ConfigParser parser;
	private FAMAAttributedFeatureModel afm;
	private ConfigParserResult result;
	private FMFParser fmParser;
	
	@Before
	public void setUp() throws Exception {
		fmParser = new FMFParser();
		afm = fmParser.parseModel("test/AmazonEC2Atts.afm");
		parser = new ExtendedConfigParser(afm);
		result = parser.parseConfiguration("test/ConfigFile1.fmc");
		
	
	}

	@Test
	public void test1() {
		assertTrue(result.getErrors().isEmpty());
	}
	
	@Test
	public void test2(){
		assertTrue(result.getConfig() instanceof ExtendedConfiguration);
	}
	
	@Test
	public void test3(){
		ExtendedConfiguration config = (ExtendedConfiguration) result.getConfig();
		assertTrue(config.getAttConfigs().size() == 8);
	}
	
	@Test
	public void test4(){
		ExtendedConfiguration config = (ExtendedConfiguration) result.getConfig();
		Collection<Tree<String>> constraints = config.getAttConfigs();
		Collection<Tree<String>> constraints2 = loadConstraints();
		assertTrue(constraints2.containsAll(constraints));
	}
	
	
	
	private Collection<Tree<String>> loadConstraints(){
		Collection<Tree<String>> constraints2 = new LinkedList<Tree<String>>();
		constraints2.add(fmParser.parseConstraint("WindowsBased;"));
		constraints2.add(fmParser.parseConstraint("NOT Europe;"));
		constraints2.add(fmParser.parseConstraint("OnDemand;"));
		constraints2.add(fmParser.parseConstraint("Computing.totalCost < 500000;"));
		constraints2.add(fmParser.parseConstraint("Use.period == 12;"));
		constraints2.add(fmParser.parseConstraint("Use.usage == 600;"));
		constraints2.add(fmParser.parseConstraint("Instance.cores >= 4;"));
		constraints2.add(fmParser.parseConstraint("Instance.ram >= 6;"));
		
		return constraints2;
	}

}
