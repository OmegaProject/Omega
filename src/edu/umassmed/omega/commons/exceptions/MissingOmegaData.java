package edu.umassmed.omega.commons.exceptions;

import edu.umassmed.omega.commons.OmegaPlugin;

public class MissingOmegaData extends Exception {

	private static final long serialVersionUID = -8169438237589027577L;

	public MissingOmegaData(final OmegaPlugin plugin) {
		super(plugin.getName() + " - missing omega data");
	}
}
