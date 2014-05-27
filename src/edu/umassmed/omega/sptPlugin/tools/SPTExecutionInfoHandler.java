package edu.umassmed.omega.sptPlugin.tools;

import edu.umassmed.omega.dataNew.coreElements.OmegaImage;

public class SPTExecutionInfoHandler {

	// private OmeroParametersHandler omeroParameters = null;
	private OmegaImage image = null;
	private String radius = "";
	private String cutOff = "";
	private String percentile = "";
	private String displacement = "";
	private String linkRange = "";

	public SPTExecutionInfoHandler(final OmegaImage image, final String radius,
	        final String cutOff, final String percentile,
	        final String displacement, final String linkRange) {
		this.image = image;
		this.radius = radius;
		this.cutOff = cutOff;
		this.percentile = percentile;
		this.displacement = displacement;
		this.linkRange = linkRange;
	}

	public OmegaImage getImage() {
		return this.image;
	}

	public void setImage(final OmegaImage image) {
		this.image = image;
	}

	public String getRadius() {
		return this.radius;
	}

	public void setRadius(final String radius) {
		this.radius = radius;
	}

	public String getCutOff() {
		return this.cutOff;
	}

	public void setCutOff(final String cutOff) {
		this.cutOff = cutOff;
	}

	public String getPercentile() {
		return this.percentile;
	}

	public void setPercentile(final String percentile) {
		this.percentile = percentile;
	}

	public String getDisplacement() {
		return this.displacement;
	}

	public void setDisplacement(final String displacement) {
		this.displacement = displacement;
	}

	public String getLinkRange() {
		return this.linkRange;
	}

	public void setLinkRange(final String linkRange) {
		this.linkRange = linkRange;
	}
}
