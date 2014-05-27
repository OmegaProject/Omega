package edu.umassmed.omega.commons.exceptions;

public class OmegaLoadedElementNotFound extends Exception {

	private static final long serialVersionUID = -8169438237589027577L;

	public OmegaLoadedElementNotFound(final String details) {
		super("Omega core - loaded elements not found \n" + details + "\n");
	}
}
