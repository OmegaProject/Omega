package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

public class OmegaExperimenterGroup extends OmegaElement {

	private final List<OmegaExperimenter> leaders;

	private List<OmegaExperimenter> associates;

	public OmegaExperimenterGroup(final Long elementID,
	        final List<OmegaExperimenter> leaders) {
		super(elementID);

		this.leaders = leaders;

		this.associates = new ArrayList<OmegaExperimenter>();
	}

	public OmegaExperimenterGroup(final Long elementID,
	        final List<OmegaExperimenter> leaders,
	        final List<OmegaExperimenter> associates) {
		this(elementID, leaders);

		this.associates = associates;
	}

	public List<OmegaExperimenter> getLeaders() {
		return this.leaders;
	}

	public boolean containsLeader(final long id) {
		for (final OmegaExperimenter leader : this.leaders) {
			if (leader.getElementID() == id)
				return true;
		}
		return false;
	}

	public void addLeader(final OmegaExperimenter leader) {
		this.leaders.add(leader);
	}

	public List<OmegaExperimenter> getAssociates() {
		return this.associates;
	}

	public void addAssociate(final OmegaExperimenter associate) {
		this.associates.add(associate);
	}
}
