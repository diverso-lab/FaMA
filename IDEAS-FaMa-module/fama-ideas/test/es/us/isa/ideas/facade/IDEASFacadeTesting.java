package es.us.isa.ideas.facade;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.ideas.FAMAAnalyserDelegate;
import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.common.AppResponse.Status;

public class IDEASFacadeTesting {

	private FAMAAnalyserDelegate analyser;
	private String fmContent;
	private String configContent;
	
	@Before
	public void setUp() throws Exception {
		analyser = FAMAAnalyserDelegate.getInstance();
		fmContent = this.loadContent("test/AmazonEC2Atts.afm");
		configContent = this.loadContent("test/ConfigFile1.fmc");
	}

	@Test
	public void testFM() {
		AppResponse resp = analyser.analyseForIDEAS(fmContent, FAMAAnalyserDelegate.NUMBER_OF_CONFIGS_OP, ".afm");
		System.out.println(resp.getMessage());
		assertEquals(resp.getStatus(), Status.OK);
	}
	
	@Test
	public void testConfig() {
		AppResponse resp = analyser.analyseForIDEAS(fmContent, FAMAAnalyserDelegate.OPTIMAL_OP, ".afm",configContent);
		System.out.println(resp.getMessage());
		assertEquals(resp.getStatus(), Status.ERROR);
	}

	private String loadContent(String path){
		String result = "";
		try {
			 FileReader fileReader = new FileReader(new File(path));
			 BufferedReader br = new BufferedReader(fileReader);
			 String aux;
			 while ((aux=br.readLine()) != null){
				 result += aux + "\n";
			 }
			 br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
