package edu.umassmed.omega.dataNew.trajectoryElements;

import java.awt.Color;

import edu.umassmed.omega.dataNew.coreElements.OmegaNamedElement;

public class OmegaSegmentationType extends OmegaNamedElement {

	public static final String NOT_ASSIGNED = "Not assigned";
	public static final Color NOT_ASSIGNED_COL = Color.black;
	public static final Integer NOT_ASSIGNED_VAL = 0;

	public static final String DIRECTED = "Directed";
	public static final Color DIRECTED_COL = Color.blue;
	public static final Integer DIRECTED_VAL = 1;

	public static final String CONFINED = "Confined";
	public static final Color CONFINED_COL = Color.pink;
	public static final Integer CONFINED_VAL = 2;

	public static final String SUB_DIFFUSIVE = "Sub diffusive";
	public static final Color SUB_DIFFUSIVE_COL = Color.orange;
	public static final Integer SUB_DIFFUSIVE_VAL = 3;

	public static final String DIFFUSIVE = "Diffusive";
	public static final Color DIFFUSIVE_COL = Color.magenta;
	public static final Integer DIFFUSIVE_VAL = 4;

	public static final String SUPER_DIFFUSIVE = "Super diffusive";
	public static final Color SUPER_DIFFUSIVE_COL = Color.gray;
	public static final Integer SUPER_DIFFUSIVE_VAL = 5;

	private Color color;
	private final Integer value;

	private boolean isChanged;

	public OmegaSegmentationType(final String name, final Integer value,
	        final Color color) {
		super(-1, name);
		this.value = value;
		this.color = color;
		this.isChanged = false;
	}

	public boolean isChanged() {
		return this.isChanged;
	}

	public Integer getValue() {
		return this.value;
	}

	public void setColor(final Color col) {
		this.color = col;
		this.isChanged = true;
	}

	public Color getColor() {
		return this.color;
	}

	public boolean isEqual(final OmegaSegmentationType segmType) {
		if (!segmType.getName().equals(this.getName()))
			return false;
		if (segmType.value != this.value)
			return false;
		if (segmType.color.getRed() != this.color.getRed())
			return false;
		if (segmType.color.getGreen() != this.color.getGreen())
			return false;
		if (segmType.color.getBlue() != this.color.getBlue())
			return false;
		return true;
	}
}
