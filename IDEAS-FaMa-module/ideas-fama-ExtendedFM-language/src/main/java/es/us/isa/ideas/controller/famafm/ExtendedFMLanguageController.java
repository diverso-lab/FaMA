package es.us.isa.ideas.controller.famafm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.us.isa.FAMA.ideas.FAMAAnalyserDelegate;
import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.common.AppResponse.Status;
import es.us.isa.ideas.module.controller.BaseLanguageController;


@Controller
@RequestMapping("/language")
public class ExtendedFMLanguageController extends BaseLanguageController {

	
	private final static String EXTENSION = ".afm";
	
	@RequestMapping(value = "/operation/{id}/execute", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse executeOperation(String id, String content, String fileUri) {
		/**
		 * XXX List of operations:
		 * - Valid FM
		 * - Number of products
		 */
		
		FAMAAnalyserDelegate analyser = FAMAAnalyserDelegate.getInstance();
		AppResponse response = analyser.analyseForIDEAS(content, id, EXTENSION);
		response.setFileUri(fileUri);
		
		return response;
	}

	@RequestMapping(value = "/format/{format}/checkLanguage", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse checkLanguage(String id, String content, String fileUri) {
		//TODO
		AppResponse appResponse = new AppResponse();
		
		boolean problems = false;

		//System.out.println("CheckSyntax: " + res );
		appResponse.setFileUri(fileUri);
		
		if (problems)
			appResponse.setStatus(Status.OK_PROBLEMS);
		else
			appResponse.setStatus(Status.OK);
		
		
		return appResponse;
	}

	@RequestMapping(value = "/convert", method = RequestMethod.POST)
	@ResponseBody
	public AppResponse convertFormat(String currentFormat, String desiredFormat, String fileUri, String content) {
		//TODO
		AppResponse appResponse = new AppResponse();
		
		return appResponse;
	}
	
	

}
