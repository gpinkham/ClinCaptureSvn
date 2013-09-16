package com.clinovo.exception;

public class CodeException extends Exception {

	private static final long serialVersionUID = -8111758287566576660L;
	public CodeException(String message) {
		super(message);
	}
	
	public CodeException(Exception exception) {
		super(exception);
	}

}
