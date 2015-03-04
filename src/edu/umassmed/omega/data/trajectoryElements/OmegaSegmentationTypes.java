package edu.umassmed.omega.data.trajectoryElements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.data.coreElements.OmegaNamedElement;

public class OmegaSegmentationTypes extends OmegaNamedElement {

	public static final String DEFAULT_NAME = "Default segmentation types";

	public static final String NOT_ASSIGNED = OmegaSegmentationType.NOT_ASSIGNED;
	public static final Color NOT_ASSIGNED_COL = OmegaSegmentationType.NOT_ASSIGNED_COL;
	public static final Integer NOT_ASSIGNED_VAL = OmegaSegmentationType.NOT_ASSIGNED_VAL;
	public static OmegaSegmentationType NOT_ASSIGNED_TYPE = null;

	public static final String DIRECTED = OmegaSegmentationType.DIRECTED;
	public static final Color DIRECTED_COL = OmegaSegmentationType.DIRECTED_COL;
	public static final Integer DIRECTED_VAL = OmegaSegmentationType.DIRECTED_VAL;

	public static final String CONFINED = OmegaSegmentationType.CONFINED;
	public static final Color CONFINED_COL = OmegaSegmentationType.CONFINED_COL;
	public static final Integer CONFINED_VAL = OmegaSegmentationType.CONFINED_VAL;

	public static final String SUB_DIFFUSIVE = OmegaSegmentationType.SUB_DIFFUSIVE;
	public static final Color SUB_DIFFUSIVE_COL = OmegaSegmentationType.SUB_DIFFUSIVE_COL;
	public static final Integer SUB_DIFFUSIVE_VAL = OmegaSegmentationType.SUB_DIFFUSIVE_VAL;

	public static final String DIFFUSIVE = OmegaSegmentationType.DIFFUSIVE;
	public static final Color DIFFUSIVE_COL = OmegaSegmentationType.DIFFUSIVE_COL;
	public static final Integer DIFFUSIVE_VAL = OmegaSegmentationType.DIFFUSIVE_VAL;

	public static final String SUPER_DIFFUSIVE = OmegaSegmentationType.SUPER_DIFFUSIVE;
	public static final Color SUPER_DIFFUSIVE_COL = OmegaSegmentationType.SUPER_DIFFUSIVE_COL;
	public static final Integer SUPER_DIFFUSIVE_VAL = OmegaSegmentationType.SUPER_DIFFUSIVE_VAL;

	private static OmegaSegmentationTypes defaultSegmTypes;

	private final List<OmegaSegmentationType> types;

	private boolean isChanged;

	public OmegaSegmentationTypes(final String name) {
		super(-1, name);
		this.types = new ArrayList<OmegaSegmentationType>();
		this.isChanged = false;
	}

	public OmegaSegmentationTypes(final String name,
	        final List<OmegaSegmentationType> types) {
		this(name);
		this.types.addAll(types);
	}

	public boolean isEqual(final OmegaSegmentationTypes segmTypes) {
		if (!this.getName().equals(segmTypes.getName()))
			return false;
		if (this.types.size() != segmTypes.types.size())
			return false;
		for (final OmegaSegmentationType segmType : this.types) {
			boolean found = false;
			for (final OmegaSegmentationType segmType2 : segmTypes.types) {
				if (!segmType.isEqual(segmType2)) {
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

	public boolean isChanged() {
		return this.isChanged;
	}

	public void setNewTypes(final List<OmegaSegmentationType> types) {
		types.clear();
		types.addAll(types);
		this.isChanged = true;
	}

	public List<OmegaSegmentationType> getTypes() {
		return this.types;
	}

	public OmegaSegmentationType getSegmentationType(final long segmTypeID) {
		for (final OmegaSegmentationType segmType : this.types) {
			if (segmType.getElementID() == segmTypeID)
				return segmType;
		}
		return null;
	}

	public Integer getSegmentationValue(final String segmName) {
		for (final OmegaSegmentationType segmType : this.types) {
			if (segmType.getName().equals(segmName))
				return segmType.getValue();
		}
		return null;
	}

	public Color getSegmentationColor(final Integer value) {
		for (final OmegaSegmentationType segmType : this.types) {
			if (segmType.getValue() == value)
				return segmType.getColor();
		}
		return null;
	}

	public static OmegaSegmentationTypes getDefaultSegmentationTypes() {
		if (OmegaSegmentationTypes.defaultSegmTypes != null)
			return OmegaSegmentationTypes.defaultSegmTypes;
		return OmegaSegmentationTypes.createDefaultSegmentationTypes();
	}

	public static OmegaSegmentationType getDefaultNotAssigned() {
		if (OmegaSegmentationTypes.NOT_ASSIGNED_TYPE == null) {
			final OmegaSegmentationType notAss = new OmegaSegmentationType(
			        OmegaSegmentationTypes.NOT_ASSIGNED,
			        OmegaSegmentationTypes.NOT_ASSIGNED_VAL,
			        OmegaSegmentationTypes.NOT_ASSIGNED_COL);
			OmegaSegmentationTypes.NOT_ASSIGNED_TYPE = notAss;
		}
		return OmegaSegmentationTypes.NOT_ASSIGNED_TYPE;
	}

	private static OmegaSegmentationTypes createDefaultSegmentationTypes() {
		final String name = OmegaSegmentationTypes.DEFAULT_NAME;
		final OmegaSegmentationTypes defaultSegType = new OmegaSegmentationTypes(
		        name);
		defaultSegType.types
		        .add(OmegaSegmentationTypes.getDefaultNotAssigned());

		final OmegaSegmentationType dir = new OmegaSegmentationType(
		        OmegaSegmentationTypes.DIRECTED,
		        OmegaSegmentationTypes.DIRECTED_VAL,
		        OmegaSegmentationTypes.DIRECTED_COL);
		defaultSegType.types.add(dir);

		final OmegaSegmentationType con = new OmegaSegmentationType(
		        OmegaSegmentationTypes.CONFINED,
		        OmegaSegmentationTypes.CONFINED_VAL,
		        OmegaSegmentationTypes.CONFINED_COL);
		defaultSegType.types.add(con);

		final OmegaSegmentationType sub = new OmegaSegmentationType(
		        OmegaSegmentationTypes.SUB_DIFFUSIVE,
		        OmegaSegmentationTypes.SUB_DIFFUSIVE_VAL,
		        OmegaSegmentationTypes.SUB_DIFFUSIVE_COL);
		defaultSegType.types.add(sub);

		final OmegaSegmentationType dif = new OmegaSegmentationType(
		        OmegaSegmentationTypes.DIFFUSIVE,
		        OmegaSegmentationTypes.DIFFUSIVE_VAL,
		        OmegaSegmentationTypes.DIFFUSIVE_COL);
		defaultSegType.types.add(dif);

		final OmegaSegmentationType sup = new OmegaSegmentationType(
		        OmegaSegmentationTypes.SUPER_DIFFUSIVE,
		        OmegaSegmentationTypes.SUPER_DIFFUSIVE_VAL,
		        OmegaSegmentationTypes.SUPER_DIFFUSIVE_COL);
		defaultSegType.types.add(sup);
		return defaultSegType;
	}
}
