package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;


public class OmegaExperimenter extends OmegaPerson {

	private final List<OmegaExperimenterGroup> groups;

	public OmegaExperimenter(final Long elementID, final String firstName,
	        final String lastName) {
		super(elementID, firstName, lastName);

		this.groups = new ArrayList<OmegaExperimenterGroup>();
	}

	public OmegaExperimenter(final Long elementID, final String firstName,
	        final String lastName, final List<OmegaExperimenterGroup> groups) {
		super(elementID, firstName, lastName);

		this.groups = groups;
	}

	public List<OmegaExperimenterGroup> getGroups() {
		return this.groups;
	}

	public void addGroup(final OmegaExperimenterGroup group) {
		this.groups.add(group);
	}

	public boolean containsGroup(final long id) {
		for (final OmegaExperimenterGroup group : this.groups) {
			if (group.getElementID() == id)
				return true;
		}
		return false;
	}

	public OmegaExperimenterGroup getGroup(final long id) {
		for (final OmegaExperimenterGroup group : this.groups) {
			if (group.getElementID() == id)
				return group;
		}
		return null;
	}
}
