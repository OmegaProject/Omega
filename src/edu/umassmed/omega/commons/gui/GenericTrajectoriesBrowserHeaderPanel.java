package edu.umassmed.omega.commons.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.RootPaneContainer;

public class GenericTrajectoriesBrowserHeaderPanel extends GenericPanel {

	private static final long serialVersionUID = 243781326858865956L;

	private final GenericTrajectoriesBrowserPanel tbPanel;

	private Double physicalSizeT;

	public GenericTrajectoriesBrowserHeaderPanel(
	        final RootPaneContainer parent,
	        final GenericTrajectoriesBrowserPanel tbPanel) {
		super(parent);
		this.tbPanel = tbPanel;

		this.physicalSizeT = null;
	}

	private void drawAlternateTimeframeBackground(final Graphics2D g2D) {
		final int space = GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
		final int border = GenericTrajectoriesBrowserPanel.TRAJECTORY_SQUARE_BORDER;
		for (Integer x = 0; x < this.tbPanel.getSizeT(); x++) {
			Color bg;
			if ((x % 2) == 0) {
				bg = Color.gray;
			} else {
				bg = Color.white;
			}
			g2D.setBackground(bg);
			final int xPos = ((space * x) - border);
			final int width = space;
			final int height = this.getHeight();
			g2D.clearRect(xPos, 0, width, height);
		}
		g2D.setBackground(Color.white);
	}

	private void drawStrings(final Graphics2D g2D) {
		final int space = GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
		final int border = GenericTrajectoriesBrowserPanel.TRAJECTORY_SQUARE_BORDER;
		final Font font = g2D.getFont();
		final AffineTransform fontAT = new AffineTransform();
		// fontAT.rotate(Math.toRadians(-45));
		final Font newFont = font.deriveFont(fontAT);
		g2D.setFont(newFont);
		for (int x = 1; x <= this.tbPanel.getSizeT(); x++) {
			final String Xs = String.valueOf(x);
			final int adj = this.computeAdjustment(Xs);
			final int xPos = (((space * x) - adj) - (border * 2)) - (space / 2);
			if (this.physicalSizeT != null) {
				final int yPos = space / 5;
				g2D.drawString(Xs, xPos, yPos);
				final double tD = x * this.physicalSizeT;
				final BigDecimal t = new BigDecimal(String.valueOf(tD))
				        .setScale(2, RoundingMode.HALF_UP);
				final String tS = "(" + t.toString() + ")";
				final int adj2 = this.computeAdjustment(tS);
				final int xPos2 = (((space * x) - adj2) - (border * 2))
				        - (space / 2);
				g2D.drawString(tS, xPos2, yPos * 3);
			} else {
				final int yPos = space / 2;
				g2D.drawString(Xs, xPos, yPos);
			}
		}
		g2D.setFont(font);
	}

	private int computeAdjustment(final String string) {
		boolean needToAdd = false;
		int length = string.length();
		if (string.contains("(") || string.contains(")")) {
			length -= 3;
			needToAdd = true;
		}
		if (length > 1) {
			int adj = 3 * length;
			if (needToAdd) {
				adj += 3;
			}
			return adj;
		}
		return 0;
	}

	@Override
	public void paint(final Graphics g) {
		this.setPanelSize();
		final Graphics2D g2D = (Graphics2D) g;
		g2D.setBackground(Color.white);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
		        RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.clearRect(0, 0, this.getWidth(), this.getHeight());

		this.drawAlternateTimeframeBackground(g2D);

		this.drawStrings(g2D);
	}

	protected void setPanelSize() {
		final int sizeT = this.tbPanel.getSizeT();
		int width = this.getParent().getWidth();
		if (sizeT > 0) {
			int widthTmp = sizeT
			        * GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
			widthTmp += 20;
			if (width < widthTmp) {
				width = widthTmp;
			}
		}
		final int height = GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
		final Dimension dim = new Dimension(width, height);
		this.setPreferredSize(dim);
		this.setSize(dim);
	}

	public void setPhysicalSizeT(final Double physicalSizeT) {
		this.physicalSizeT = physicalSizeT;
	}
}
