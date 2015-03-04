package edu.umassmed.omega.commons.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.utilities.OmegaStringUtilities;

public class GenericTrajectoriesBrowserLabelsPanel extends GenericPanel {

	private static final long serialVersionUID = 313531450859107197L;

	private final boolean isShowEnabled;

	private boolean hasPhysicalSizeT;

	public GenericTrajectoriesBrowserLabelsPanel(
	        final RootPaneContainer parent, final boolean isShowEnabled) {
		super(parent);

		final int height = GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
		final int width = ((GenericTrajectoriesBrowserPanel.TRAJECTORY_NAME_SPACE_MODIFIER + 1) * GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT) + 3;
		final Dimension dim = new Dimension(width, height);
		this.setPreferredSize(dim);
		this.setSize(dim);

		this.isShowEnabled = isShowEnabled;
		this.hasPhysicalSizeT = false;
	}

	private void drawLabels(final Graphics2D g2D) {
		final int space = GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
		if (this.isShowEnabled) {
			g2D.drawString("Shown", 0, this.getHeight() - (space / 5));
		}
		g2D.drawString("ID", space, this.getHeight() - (space / 5));
		g2D.drawString("Name", space * 2, this.getHeight() - (space / 5));

		final int xPosTp = this.getWidth()
		        - OmegaStringUtilities.getStringSize(this.getGraphics(),
		                this.getFont(), "Timepoints").width;
		final int xPosSec = this.getWidth()
		        - OmegaStringUtilities.getStringSize(this.getGraphics(),
		                this.getFont(), "(Sec)").width;
		if (this.hasPhysicalSizeT) {
			g2D.drawString("Timepoints", xPosTp, this.getHeight() / 5);
			g2D.drawString("(Sec)", xPosSec, this.getHeight() / 3);
		} else {
			g2D.drawString("Timepoints", xPosTp, this.getHeight() / 2);
		}
	}

	@Override
	public void paint(final Graphics g) {
		final Graphics2D g2D = (Graphics2D) g;
		g2D.setBackground(Color.white);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
		        RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.clearRect(0, 0, this.getWidth(), this.getHeight());

		this.drawLabels(g2D);
	}

	public void setHasPhysicalSizeT(final boolean hasPhysicalSizeT) {
		this.hasPhysicalSizeT = hasPhysicalSizeT;
	}
}
