package edu.umassmed.omega.dataNew.connection;

public class OmegaServerInformation {

	/** The address of the server. */
	private final String hostName;

	/** The default port value. */
	public static final int DEFAULT_PORT = 4064;

	/** The port. */
	private int port;

	public OmegaServerInformation(final String hostName) {
		this(hostName, OmegaServerInformation.DEFAULT_PORT);
	}

	public OmegaServerInformation(final String hostName, final int port) {
		this.hostName = hostName;
		this.port = port;
	}

	/**
	 * Sets the port.
	 * 
	 * @param port
	 *            The value to set.
	 */
	public void setPort(final int port) {
		this.port = port;
	}

	/**
	 * Returns the port.
	 * 
	 * @return See above.
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Returns the address of the server
	 * 
	 * @return See above.
	 */
	public String getHostName() {
		return this.hostName;
	}
}
