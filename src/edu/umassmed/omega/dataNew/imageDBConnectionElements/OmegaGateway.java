package edu.umassmed.omega.dataNew.imageDBConnectionElements;

public abstract class OmegaGateway {

	/** Flag indicating if you are connected or not. */
	private boolean isConnected;

	public OmegaGateway() {
		this.isConnected = false;
	}

	public abstract boolean connect(final OmegaLoginCredentials loginCred,
	        final OmegaServerInformation serverInfo);

	public abstract byte[] getImageData(final long id, final int z,
	        final int t, final int c);

	public boolean isConnected() {
		return this.isConnected;
	}

	public void setConnected(final boolean isConnected) {
		this.isConnected = isConnected;
	}
}
