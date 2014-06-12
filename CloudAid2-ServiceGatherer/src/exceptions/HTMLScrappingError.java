package exceptions;

import enums.HTMLError;

public class HTMLScrappingError extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HTMLError errorCode;
	private String[] errorArguments;

	public HTMLScrappingError(HTMLError error){
		super(error.getMessage());
		errorCode = error;
	}

	public HTMLScrappingError(HTMLError error, String... args){
		super(error.getMessage(args));
		errorCode = error;
		errorArguments = args;
	}

	public HTMLScrappingError(HTMLError error, Throwable cause, String... args){
		super(error.getMessage(args), cause);
		errorCode = error;
		errorArguments = args;
	}	

}
