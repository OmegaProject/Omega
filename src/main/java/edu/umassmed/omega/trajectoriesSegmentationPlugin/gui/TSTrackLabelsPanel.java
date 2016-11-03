package edu.umassmed.omega.trajectoriesSegmentationPlugin.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;

public class TSTrackLabelsPanel extends GenericPanel {

	private static final long serialVersionUID = 6602253138642159034L;

	private final OmegaPlugin plugin;

	private final int orientation;
	private int panelSize, center;
	private double realSize;
	private final double pixelsSize;

	public TSTrackLabelsPanel(final RootPaneContainer parent,
	        final OmegaPlugin plugin, final int orientation,
	        final double realSize, final double pixelsSize,
	        final int panelSize, final int center) {
		super(parent);

		this.plugin = plugin;

		this.orientation = orientation;

		this.realSize = realSize;
		this.pixelsSize = pixelsSize;

		this.panelSize = panelSize;
		this.center = center;

		this.rescale();
	}

	@Override
	public void paint(final Graphics g) {
		final int width = this.getWidth();
		final int height = this.getHeight();

		final Graphics2D g2D = (Graphics2D) g;

		g2D.setBackground(Color.white);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
		        RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.clearRect(0, 0, width, height);

		this.paintMetrics(g2D);
	}

	private void paintMetrics(final Graphics2D g2D) {
		if (this.center != 0) {
			this.paintMetricFromCenter(g2D);
		} else {
			this.paintMetricsFromZero(g2D);
		}
	}

	private void paintHLabel(final Graphics2D g2D, final int coord,
	        final double val1, final double val2) {
		g2D.drawLine(coord, TSTrackPanel.LABELS_PANEL_SIZE - 10, coord,
		        TSTrackPanel.LABELS_PANEL_SIZE);
		if (coord == 0) {
			g2D.drawString(String.valueOf(val1), coord, 15);
		} else if (coord == this.panelSize) {
			g2D.drawString(String.valueOf(val1), coord - 5, 10);
			if (this.pixelsSize != -1) {
				g2D.drawString("(" + String.valueOf(val2) + ")", coord - 5, 25);
			}
		} else {
			g2D.drawString(String.valueOf(val1), coord - 5, 10);
			if (this.pixelsSize != -1) {
				g2D.drawString("(" + String.valueOf(val2) + ")", coord - 5, 25);
			}
		}
	}

	private void paintVLabel(final Graphics2D g2D, final int coord,
	        final double val1, final double val2) {
		g2D.drawLine(TSTrackPanel.LABELS_PANEL_SIZE - 10, coord,
		        TSTrackPanel.LABELS_PANEL_SIZE, coord);
		if (coord == 0) {
			g2D.drawString(String.valueOf(val1), 5, coord + 10);
		} else if (coord == this.panelSize) {
			g2D.drawString(String.valueOf(val1), 5, coord - 15);
			if (this.pixelsSize != -1) {
				g2D.drawString("(" + String.valueOf(val2) + ")", 5, coord - 5);
			}
		} else {
			g2D.drawString(String.valueOf(val1), 5, coord - 2);
			if (this.pixelsSize != -1) {
				g2D.drawString("(" + String.valueOf(val2) + ")", 5, coord + 12);
			}
		}
	}

	private void paintMetricsFromZero(final Graphics2D g2D) {
		final int numOfLabels = this.panelSize
		        / TSTrackPanel.SPACE_BETWEEN_LABELS;
		final double offsetCoord = (double) this.panelSize
		        / (double) numOfLabels;
		final double offsetVal = this.realSize / numOfLabels;
		for (int i = 0; i <= numOfLabels; i++) {
			try {
				final int coord = new BigDecimal(
						String.valueOf(i * offsetCoord)).setScale(0,
								RoundingMode.HALF_UP).intValue();
				final double val1 = new BigDecimal(
						String.valueOf(offsetVal * i)).setScale(2,
								RoundingMode.HALF_UP).doubleValue();
				final double val2 = new BigDecimal(String.valueOf(val1
						* this.pixelsSize)).setScale(2, RoundingMode.HALF_UP)
						.doubleValue();
				if (this.orientation == 0) {
					this.paintHLabel(g2D, coord, val1, val2);
				} else {
					this.paintVLabel(g2D, coord, val1, val2);
				}
			} catch (final Exception ex) {
				// TODO handled?
				OmegaLogFileManager
				        .handlePluginException(this.plugin, ex, true);
			}
		}
		// System.out.println(this.panelSize + " VS "
		// + (numOfLabels * TSTrackPanel.SPACE_BETWEEN_LABELS));
		// final int lastCoord = this.panelSize;
		// final double lastVal1 = new BigDecimal(String.valueOf(this.realSize))
		// .setScale(2, RoundingMode.HALF_UP).doubleValue();
		// final double lastVal2 = new BigDecimal(String.valueOf(lastVal1
		// * this.pixelsSize)).setScale(2, RoundingMode.HALF_UP)
		// .doubleValue();
		// if (this.orientation == 0) {
		// this.paintHLabel(g2D, lastCoord, lastVal1, lastVal2);
		// } else {
		// this.paintVLabel(g2D, lastCoord, lastVal1, lastVal2);
		// }
	}

	private void paintMetricFromCenter(final Graphics2D g2D) {
		final int spaceBefore = this.center;
		final int spaceAfter = this.panelSize - this.center;
		final int numOfLabelsBefore = spaceBefore
		        / TSTrackPanel.SPACE_BETWEEN_LABELS;
		final int numOfLabelsAfter = spaceAfter
		        / TSTrackPanel.SPACE_BETWEEN_LABELS;
		for (int i = 0; i < numOfLabelsBefore; i++) {

		}

		for (int i = 0; i < numOfLabelsAfter; i++) {

		}
	}

	private void rescale() {
		final Dimension dim;
		if (this.orientation == 1) {
			dim = new Dimension(TSTrackPanel.LABELS_PANEL_SIZE,
			        this.panelSize + 30);
		} else {
			dim = new Dimension(this.panelSize + 30,
			        TSTrackPanel.LABELS_PANEL_SIZE);
		}
		this.setPreferredSize(dim);
		this.setSize(dim);
	}

	public void setSizes(final double realSize, final int panelSize,
	        final int center) {
		this.realSize = realSize;
		this.panelSize = panelSize;
		this.center = center;
		this.rescale();
		this.revalidate();
		this.repaint();
	}

	public void setPanelSize(final int size) {
		this.panelSize = size;
		this.rescale();
		this.revalidate();
		this.repaint();
	}
}
