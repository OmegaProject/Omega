package edu.umassmed.omega.commons.exceptions;

import edu.umassmed.omega.commons.OmegaPlugin;

public class OmegaAlgorithmParametersTypeException extends Exception {
	private static final long serialVersionUID = -8169438237589027577L;

	public OmegaAlgorithmParametersTypeException(final OmegaPlugin plugin,
	        final String errors) {
		super(plugin.getName() + " - wrong parameter(s) type:\n" + errors);
	}
}
