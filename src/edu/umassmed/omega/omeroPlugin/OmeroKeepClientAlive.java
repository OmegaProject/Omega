package edu.umassmed.omega.omeroPlugin;

/**
 * Keeps the services alive.
 */
class OmeroKeepClientAlive implements Runnable {

	/** Reference to the gateway. */
	private final OmeroGateway gateway;

	/**
	 * Creates a new instance.
	 * 
	 * @param gateway
	 *            Reference to the gateway. Mustn't be <code>null</code>.
	 */
	public OmeroKeepClientAlive(final OmeroGateway gateway) {
		if (gateway == null)
			throw new IllegalArgumentException("No gateway specified.");
		this.gateway = gateway;
	}

	/** Runs. */
	@Override
	public void run() {
		try {
			synchronized (this.gateway) {
				this.gateway.keepSessionAlive();
			}
		} catch (final Throwable t) {
			// TODO Manage exception
		}
	}
}
