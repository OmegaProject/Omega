package edu.umassmed.omega.dataNew.trajectoryElements;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;

public class OmegaTrajectory extends OmegaElement {

	private final int length;
	private OmegaROI startingROI;
	private OmegaROI endingROI;

	private final List<OmegaROI> ROIs;
	private final List<OmegaLink> links;

	public OmegaTrajectory(final int length) {
		super();

		this.startingROI = null;
		this.endingROI = null;

		this.length = length;

		this.ROIs = new ArrayList<OmegaROI>();
		this.links = new ArrayList<OmegaLink>();
	}

	public OmegaTrajectory(final OmegaROI startingROI,
	        final OmegaROI endingROI, final int length) {
		super();

		this.startingROI = startingROI;
		this.endingROI = endingROI;

		this.length = length;

		this.ROIs = new ArrayList<OmegaROI>();
		this.links = new ArrayList<OmegaLink>();
	}

	public OmegaTrajectory(final OmegaROI startingROI,
	        final OmegaROI endingROI, final int length,
	        final List<OmegaROI> ROIs, final List<OmegaLink> links) {
		super();

		this.startingROI = startingROI;
		this.endingROI = endingROI;

		this.length = length;

		this.ROIs = ROIs;
		this.links = links;
	}

	public OmegaTrajectory(final Long elementID, final int length) {
		super(elementID);

		this.startingROI = null;
		this.endingROI = null;

		this.length = length;

		this.ROIs = new ArrayList<OmegaROI>();
		this.links = new ArrayList<OmegaLink>();
	}

	public OmegaTrajectory(final Long elementID, final OmegaROI startingROI,
	        final OmegaROI endingROI, final int length) {
		super(elementID);

		this.startingROI = startingROI;
		this.endingROI = endingROI;

		this.length = length;

		this.ROIs = new ArrayList<OmegaROI>();
		this.links = new ArrayList<OmegaLink>();
	}

	public OmegaTrajectory(final Long elementID, final OmegaROI startingROI,
	        final OmegaROI endingROI, final int length,
	        final List<OmegaROI> ROIs, final List<OmegaLink> links) {
		super(elementID);

		this.startingROI = startingROI;
		this.endingROI = endingROI;

		this.length = length;

		this.ROIs = ROIs;
		this.links = links;
	}

	public OmegaROI getStartingROI() {
		return this.startingROI;
	}

	public void setStartingROI(final OmegaROI startingPoint) {
		this.startingROI = startingPoint;
	}

	public OmegaROI getEndingROI() {
		return this.endingROI;
	}

	public void setEndingROI(final OmegaROI endingPoint) {
		this.endingROI = endingPoint;
	}

	public int getLength() {
		return this.length;
	}

	public List<OmegaROI> getROIs() {
		return this.ROIs;
	}

	public void addROI(final OmegaROI ROI) {
		this.ROIs.add(ROI);
	}

	public List<OmegaLink> getLinks() {
		return this.links;
	}

	public void addLink(final OmegaLink link) {
		this.links.add(link);
	}
}
