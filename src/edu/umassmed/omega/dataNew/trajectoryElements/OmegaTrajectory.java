/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team: 
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli, 
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban, 
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.dataNew.trajectoryElements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;

public class OmegaTrajectory extends OmegaElement implements
        Comparable<OmegaTrajectory> {

	private static final String DEFAULT_TRAJ_NAME = "Traj";

	private String trajName;
	private int length;
	private OmegaROI startingROI;
	private OmegaROI endingROI;

	private final List<OmegaROI> ROIs;
	private final List<OmegaLink> links;

	private Color color;
	private boolean isVisible, isColorChanged, isNameChanged,
	        isAnnotationChanged;

	private String annotations;

	public OmegaTrajectory(final int length) {
		super((long) -1);

		this.trajName = OmegaTrajectory.DEFAULT_TRAJ_NAME;

		this.startingROI = null;
		this.endingROI = null;

		this.length = length;

		this.ROIs = new ArrayList<OmegaROI>();
		this.links = new ArrayList<OmegaLink>();

		this.color = Color.yellow;
		this.isVisible = true;
		this.isColorChanged = false;
		this.isNameChanged = false;

		this.annotations = null;
		this.isAnnotationChanged = false;
	}

	public OmegaTrajectory(final OmegaROI startingROI,
	        final OmegaROI endingROI, final int length) {
		super((long) -1);

		this.trajName = OmegaTrajectory.DEFAULT_TRAJ_NAME;

		this.startingROI = startingROI;
		this.endingROI = endingROI;

		this.length = length;

		this.ROIs = new ArrayList<OmegaROI>();
		this.links = new ArrayList<OmegaLink>();

		this.color = Color.yellow;
		this.isVisible = true;
		this.isColorChanged = false;
		this.isNameChanged = false;

		this.annotations = null;
		this.isAnnotationChanged = false;
	}

	public OmegaTrajectory(final OmegaROI startingROI,
	        final OmegaROI endingROI, final int length,
	        final List<OmegaROI> ROIs, final List<OmegaLink> links) {
		super((long) -1);

		this.trajName = OmegaTrajectory.DEFAULT_TRAJ_NAME;

		this.startingROI = startingROI;
		this.endingROI = endingROI;

		this.length = length;

		this.ROIs = ROIs;
		this.links = links;

		this.color = Color.yellow;
		this.isVisible = true;
		this.isColorChanged = false;
		this.isNameChanged = false;

		this.annotations = null;
		this.isAnnotationChanged = false;
	}

	public boolean isNameChanged() {
		return this.isNameChanged;
	}

	public void setNameChanged(final boolean isNameChanged) {
		this.isNameChanged = isNameChanged;
	}

	public void setName(final String name) {
		this.trajName = name;
	}

	public String getName() {
		return this.trajName;
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

	public void recalculateLength() {
		this.length = this.ROIs.size();
	}

	public List<OmegaROI> getROIs() {
		return this.ROIs;
	}

	public void addROI(final OmegaROI ROI) {
		this.ROIs.add(ROI);
	}

	public void addROIs(final List<OmegaROI> ROIs) {
		this.ROIs.addAll(ROIs);
	}

	public List<OmegaLink> getLinks() {
		return this.links;
	}

	public void addLink(final OmegaLink link) {
		this.links.add(link);
	}

	public boolean isColorChanged() {
		return this.isColorChanged;
	}

	public void setColorChanged(final boolean isColorChanged) {
		this.isColorChanged = isColorChanged;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}

	public boolean isVisible() {
		return this.isVisible;
	}

	public void setVisible(final boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isAnnotationsChanged() {
		return this.isAnnotationChanged;
	}

	public void setAnnotationsChanged(final boolean isAnnotationChanged) {
		this.isAnnotationChanged = isAnnotationChanged;
	}

	public void updateAnnotations(final String newAnnotations) {
		this.annotations = newAnnotations;
	}

	public String getAnnotations() {
		return this.annotations;
	}

	public boolean isEqual(final OmegaTrajectory traj) {
		if (this.length != traj.length)
			return false;
		for (final OmegaROI roi : this.ROIs) {
			boolean found = false;
			for (final OmegaROI roi2 : traj.ROIs) {
				if (roi.getFrameIndex() != roi2.getFrameIndex()) {
					continue;
				}
				if ((roi.getX() != roi2.getX()) || (roi.getY() != roi2.getY())) {
					continue;
				}
				found = true;
				break;
			}
			if (!found)
				return false;
		}
		return true;
	}

	@Override
	public int compareTo(final OmegaTrajectory traj) {
		return (this.trajName.compareTo(traj.trajName));
	}

	@Override
	public OmegaTrajectory clone() {
		final OmegaTrajectory newTraj = new OmegaTrajectory(this.length);
		newTraj.setColor(this.color);
		newTraj.addROIs(this.ROIs);
		newTraj.setVisible(this.isVisible);
		return newTraj;
	}
}
