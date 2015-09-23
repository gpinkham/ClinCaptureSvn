package org.akaza.openclinica.exception;

/**
 * CRFReadingException.
 */
@SuppressWarnings("serial")
public class CRFReadingException extends Exception {

	private String message;

	/**
	 * Constructor.
	 */
	public CRFReadingException() {
		message = "";
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            String
	 */
	public CRFReadingException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}
}
