package edu.umassmed.omega.dataNew.imageDBConnectionElements;


public abstract class OmegaGateway {

	/** Flag indicating if you are connected or not. */
	private boolean isConnected;

	public OmegaGateway() {
		this.isConnected = false;
	}

	public abstract boolean connect(final OmegaLoginCredentials loginCred,
	        final OmegaServerInformation serverInfo);

	public abstract byte[] getImageData(final Long pixelsID, final int z,
	        final int t, final int c);

	public boolean isConnected() {
		return this.isConnected;
	}

	public void setConnected(final boolean isConnected) {
		this.isConnected = isConnected;
	}

	public abstract int getDefaultZ(final Long pixelsID);

	public abstract void setDefaultZ(final Long pixelsID, int z);

	public abstract int getDefaultT(final Long pixelsID);

	public abstract void setDefaultT(final Long pixelsID, int t);

	public abstract int[] renderAsPackedInt(final Long pixelsID);

	public abstract byte[] renderCompressed(final Long pixelsID);

	public abstract void setActiveChannel(final Long pixelsID, int channel,
	        boolean active);

	public abstract Double computeSizeT(final Long pixelsID, int pixelSizeT,
	        int currentMaxT);

	public abstract void setCompressionLevel(final Long pixelsID,
	        float compression);

	public abstract double getTotalT(final Long pixelsID, final int z,
	        final int t, final int c);

	public abstract int getByteWidth(Long pixelsID);
}
