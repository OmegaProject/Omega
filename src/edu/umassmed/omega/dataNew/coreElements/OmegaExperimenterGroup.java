package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

public class OmegaExperimenterGroup extends OmegaElement {

	private final OmegaExperimenter leader;

	private List<OmegaExperimenter> associates;

	public OmegaExperimenterGroup(final Long elementID,
	        final OmegaExperimenter leader) {
		super(elementID);

		this.leader = leader;

		this.associates = new ArrayList<OmegaExperimenter>();
	}

	public OmegaExperimenterGroup(final Long elementID,
	        final OmegaExperimenter leader,
	        final List<OmegaExperimenter> associates) {
		this(elementID, leader);

		this.associates = associates;
	}

	public OmegaExperimenter getLeader() {
		return this.leader;
	}

	public List<OmegaExperimenter> getAssociates() {
		return this.associates;
	}

	public void addAssociate(final OmegaExperimenter associate) {
		this.associates.add(associate);
	}
}
