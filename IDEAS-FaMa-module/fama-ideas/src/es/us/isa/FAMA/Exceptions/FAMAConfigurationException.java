package es.us.isa.FAMA.Exceptions;

import java.util.Collection;

public class FAMAConfigurationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5288218766616228267L;
	
	private Collection<String> syntacticErrors;

	public Collection<String> getSyntacticErrors() {
		return syntacticErrors;
	}

	public void setSyntacticErrors(Collection<String> syntacticErrors) {
		this.syntacticErrors = syntacticErrors;
	}
	
	

}
