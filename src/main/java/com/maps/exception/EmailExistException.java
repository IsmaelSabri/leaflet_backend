package com.maps.exception;

public class EmailExistException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4686143472759085499L;

	public EmailExistException(String message) {
		super(message);
	}
}