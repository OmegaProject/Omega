package edu.umassmed.omega.commons.exceptions;


public class OmegaCoreException extends Exception {
	private static final long serialVersionUID = 163430947338445235L;

	public OmegaCoreException(final String message) {
		super("Omega Core - " + message);
	}
}
