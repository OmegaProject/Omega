package edu.umassmed.omega.commons.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class GenericTrajectoriesBrowserNamesPanel extends GenericPanel {

	private static final long serialVersionUID = 313531450859107197L;

	private final GenericTrajectoriesBrowserPanel tbPanel;

	private final List<Point> checkboxes;
	private final JCheckBox cb;

	private final boolean isSelectionEnabled, isShowEnabled;

	public GenericTrajectoriesBrowserNamesPanel(final RootPaneContainer parent,
	        final GenericTrajectoriesBrowserPanel tbPanel,
	        final boolean isSelectionEnabled, final boolean isShowEnabled) {
		super(parent);
		this.tbPanel = tbPanel;

		this.checkboxes = new ArrayList<Point>();
		this.cb = new JCheckBox();
		this.cb.setSelectedIcon(new ImageIcon(OmegaConstants.OMEGA_IMGS_FOLDER
		        + File.separatorChar + "checkbox_selected.png"));
		this.cb.setIcon(new ImageIcon(OmegaConstants.OMEGA_IMGS_FOLDER
		        + File.separatorChar + "checkbox_deselected.png"));

		this.isSelectionEnabled = isSelectionEnabled;
		this.isShowEnabled = isShowEnabled;
	}

	protected void checkIfCheckboxAndSelect(final Point clickP) {
		final OmegaTrajectory selectedTraj = this.tbPanel
		        .getSelectedTrajectory();
		final int adjX = this.cb.getSelectedIcon().getIconWidth();
		final int adjY = this.cb.getSelectedIcon().getIconHeight();
		for (final Point p : this.checkboxes) {
			if ((clickP.x < (p.x + adjX)) && (clickP.x > p.x)
			        && (clickP.y < (p.y + adjY)) && (clickP.y > p.y)) {
				selectedTraj.setVisible(!selectedTraj.isVisible());
				return;
			}
		}
	}

	private void drawCheckboxes(final Graphics2D g2D) {
		final int space = GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
		this.checkboxes.clear();
		for (int y = 0; y < this.tbPanel.getNumberOfTrajectories(); y++) {
			final int yPos = (space * y) + 5 + (space / 2);
			final OmegaTrajectory traj = this.tbPanel.getTrajectories().get(y);
			Icon icon;
			if (traj.isVisible()) {
				icon = this.cb.getSelectedIcon();
			} else {
				icon = this.cb.getIcon();
				// actualIcon = UIManager.getLookAndFeel()
				// .getDisabledSelectedIcon(cb, checkedIcon);
			}
			final int adjY = yPos - (icon.getIconHeight() / 2) - 5;
			icon.paintIcon(this.cb, g2D, 5, adjY);
			this.checkboxes.add(new Point(5, adjY));
		}
	}

	private void drawIDsAndNames(final Graphics2D g2D) {
		final int space = GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
		for (int y = 0; y < this.tbPanel.getNumberOfTrajectories(); y++) {
			final int xPos = space;
			final int yPos = (space * y) + 5 + (space / 2);
			final OmegaTrajectory traj = this.tbPanel.getTrajectories().get(y);
			final long id = traj.getElementID();
			String idS = String.valueOf(id);
			if (id == -1) {
				idS = "NA";
			}
			g2D.drawString(idS, xPos, yPos);
			g2D.drawString(traj.getName(), xPos * 2, yPos);
		}
	}

	private void drawSelectedTrajectoryBackground(final Graphics2D g2D) {
		for (int y = 0; y < this.tbPanel.getNumberOfTrajectories(); y++) {
			final OmegaTrajectory traj = this.tbPanel.getTrajectories().get(y);
			final int yPos = GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT
			        * y;
			final int adjY = yPos;
			if (this.tbPanel.getSelectedTrajectories().contains(traj)) {
				g2D.setBackground(OmegaConstants
				        .getDefaultSelectionBackgroundColor());
				g2D.clearRect(0, adjY, this.getWidth(),
				        GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT);
				g2D.setBackground(Color.white);
			}
		}
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

		if (this.isSelectionEnabled) {
			this.drawSelectedTrajectoryBackground(g2D);
		}

		if (this.isShowEnabled) {
			this.drawCheckboxes(g2D);
		}

		this.drawIDsAndNames(g2D);
	}

	protected void setPanelSize() {
		final int numOfTraj = this.tbPanel.getNumberOfTrajectories();
		int height = this.getParent().getHeight();
		if (numOfTraj > 0) {
			int heightTmp = numOfTraj
			        * GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
			heightTmp += 40;
			if (height < heightTmp) {
				height = heightTmp;
			}
		}
		final int width = (GenericTrajectoriesBrowserPanel.TRAJECTORY_NAME_SPACE_MODIFIER + 1)
		        * GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
		final Dimension dim = new Dimension(width, height);
		this.setPreferredSize(dim);
		this.setSize(dim);
	}
}
