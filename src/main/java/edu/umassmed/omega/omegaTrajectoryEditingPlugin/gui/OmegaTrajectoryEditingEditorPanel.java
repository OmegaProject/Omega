package edu.umassmed.omega.omegaTrajectoryEditingPlugin.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.gui.GenericTrajectoriesBrowserPanel;
import edu.umassmed.omega.commons.gui.dialogs.GenericConfirmationDialog;
import edu.umassmed.omega.omegaTrajectoryEditingPlugin.OmegaTrajectoryEditingPluginConstants;

public class OmegaTrajectoryEditingEditorPanel extends GenericTrajectoriesBrowserPanel {

	private static final long serialVersionUID = 7974224617691972332L;

	private OmegaTrajectory mergeTrajectory;
	private OmegaROI mergeParticle, previousParticle, nextParticle;

	private final OmegaTrajectoryEditingPluginPanel pluginPanel;

	private JMenuItem splitTrajMenuItem, mergeTrajMenuItem;

	private boolean isMerging;

	private boolean isMouseIn;
	private Point mousePosition;

	public OmegaTrajectoryEditingEditorPanel(final RootPaneContainer parent,
			final OmegaTrajectoryEditingPluginPanel pluginPanel, final OmegaGateway gateway) {
		super(parent, pluginPanel, gateway, false, false);

		this.pluginPanel = pluginPanel;

		this.isMerging = false;
		this.isMouseIn = false;
		this.mousePosition = null;

		this.createPopupMenu();

		this.addListeners();
	}

	private void createPopupMenu() {
		this.splitTrajMenuItem = new JMenuItem(OmegaTrajectoryEditingPluginConstants.SPLIT_ACTION);
		this.mergeTrajMenuItem = new JMenuItem(OmegaTrajectoryEditingPluginConstants.MERGE_ACTION);
	}

	private void addListeners() {
		this.splitTrajMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaTrajectoryEditingEditorPanel.this.splitTrajectory();
				// OmegaTrajectoryEditingEditorPanel.this.sendPluginTrajectoriesEvent(
				// OmegaTrajectoryEditingEditorPanel.this.getTrajectories(), false);
			}
		});
		this.mergeTrajMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaTrajectoryEditingEditorPanel.this.mergeTrajectory();
				// OmegaTrajectoryEditingEditorPanel.this.sendPluginTrajectoriesEvent(
				// OmegaTrajectoryEditingEditorPanel.this.getTrajectories(), false);
			}
		});
	}

	@Override
	protected void handleMouseOut() {
		this.isMouseIn = false;
		this.mousePosition = null;
	}

	@Override
	protected void handleMouseIn(final Point pos) {
		this.isMouseIn = true;
		this.mousePosition = pos;
	}

	@Override
	protected void handleMouseMovement(final Point pos) {
		if (!this.isMerging)
			return;
		this.mousePosition = pos;
		this.repaint();
	}

	@Override
	public void addToPaint(final Graphics2D g2D) {
		if (this.isMerging && this.isMouseIn) {
			g2D.setColor(Color.red);
			g2D.drawLine(this.getClickPosition().x, this.getClickPosition().y,
					this.mousePosition.x, this.mousePosition.y);
		}
	}

	@Override
	protected void handleMouseClick(final Point clickP,
			final boolean isRightButton, final boolean isCtrlDown,
			final boolean isTrackPanel) {
		final OmegaTrajectory oldTraj = this.getSelectedTrajectory();
		this.resetClickReferences();
		this.findSelectedTrajectory(clickP);
		boolean selected = false;
		if (isRightButton) {
			if (this.getSelectedTrajectory() != null) {
				this.findSelectedParticle(clickP);
			}
			this.createTrajectoryMenu();
			this.showTrajectoryMenu(clickP, isTrackPanel);
			this.getSelectedTrajectories().clear();
		} else {
			if (this.getSelectedTrajectory() != null) {
				selected = this.selectIfCheckbox(clickP);
			}
			if (!isCtrlDown) {
				this.getSelectedTrajectories().clear();
			}
			if (this.isMerging) {
				this.mergeTrajectory = null;
				this.mergeParticle = null;
				this.isMerging = false;
			}
		}
		
		if (selected) {
			this.resetClickReferences();
			this.getSelectedTrajectories().clear();
		}
		
		if ((this.getSelectedTrajectory() != null)) {
			if (isRightButton || (this.getSelectedTrajectory() != oldTraj)) {
				this.getSelectedTrajectories()
						.add(this.getSelectedTrajectory());
				// this.sendEventTrajectories(this.getSelectedTrajectories(),
				// false);
			} else {
				this.resetClickReferences();
				this.getSelectedTrajectories().clear();
			}
		} else {
			if (!isRightButton) {
				this.setSelectedTrajectory(null);
			}
		}
		this.repaint();

		this.pluginPanel.updateSelectedInformation(this
				.getSelectedTrajectories());
	}

	@Override
	protected void createTrajectoryMenu() {
		super.createTrajectoryMenu();
		final OmegaTrajectory selectedTraj = this.getSelectedTrajectory();
		final OmegaROI selectedParticle = this.getSelectedParticle();
		int frameIndex = -1;
		if (selectedTraj != null) {
			if (selectedParticle != null) {
				frameIndex = selectedParticle.getFrameIndex();
			}
		}
		this.getMenu().add(new JSeparator());
		if (!this.isMerging) {
			this.mergeTrajMenuItem.setText(OmegaTrajectoryEditingPluginConstants.MERGE_ACTION_START);
		} else {
			this.mergeTrajMenuItem.setText(OmegaTrajectoryEditingPluginConstants.MERGE_ACTION_END);
		}
		if (this.previousParticle != null) {
			if ((this.nextParticle == null) && (frameIndex < super.getSizeT())) {

				this.getMenu().add(this.mergeTrajMenuItem);
			}
			if (!this.isMerging) {
				this.getMenu().add(this.splitTrajMenuItem);
			}
		} else if ((this.nextParticle != null)
				&& (selectedParticle.getFrameIndex() > 0)) {
			this.getMenu().add(this.mergeTrajMenuItem);
		}
	}

	@Override
	protected void findSelectedParticle(final Point clickP) {
		super.findSelectedParticle(clickP);
		this.findPreviousAndNextParticle();
	}

	private void findPreviousAndNextParticle() {
		final List<OmegaROI> rois = this.getSelectedTrajectory().getROIs();
		for (int i = 0; i < rois.size(); i++) {
			final OmegaROI trajROI = rois.get(i);
			if (trajROI == this.getSelectedParticle()) {
				if (i > 0) {
					this.previousParticle = rois.get(i - 1);
				} else {
					this.previousParticle = null;
				}
				if (i < (rois.size() - 1)) {
					this.nextParticle = rois.get(i + 1);
				} else {
					this.nextParticle = null;
				}
				break;
			}
		}
	}

	@Override
	public void addTrajectoriesToSelection(
			final List<OmegaTrajectory> trajectories, final int index) {
		this.getTrajectories().addAll(index, trajectories);
	}

	@Override
	public int removeTrajectoriesFromSelection(
			final List<OmegaTrajectory> trajectories) {
		final int index = this.getTrajectories().indexOf(trajectories.get(0));
		this.getTrajectories().removeAll(trajectories);
		return index;
	}

	public void resetTrajectories() {
		this.getTrajectories().clear();
	}

	@Override
	protected void resetClickReferences() {
		super.resetClickReferences();
		this.previousParticle = null;
		this.nextParticle = null;
	}

	private boolean showConfirmationDialog(final String title,
			final String label) {
		final RootPaneContainer parentContainer = this.getParentContainer();
		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
				parentContainer, title, label, true);
		dialog.setVisible(true);
		return dialog.getConfirmation();
	}

	private void mergeTrajectory() {
		final OmegaTrajectory selectedTraj = this.getSelectedTrajectory();
		final OmegaROI selectedParticle = this.getSelectedParticle();
		if (this.isMerging) {
			final String traj1Name = this.mergeTrajectory.getName();
			final int traj1NameIndex = traj1Name.indexOf("_") + 1;
			final String traj1Index = traj1Name.substring(traj1NameIndex,
					traj1Name.length());
			final int traj1Length = this.mergeTrajectory.getLength();

			final String traj2Name = selectedTraj.getName();
			final int traj2NameIndex = traj2Name.indexOf("_") + 1;
			final String traj2Index = traj2Name.substring(traj2NameIndex,
					traj2Name.length());
			final int traj2Length = selectedTraj.getLength();

			final StringBuffer buf = new StringBuffer();
			buf.append(OmegaTrajectoryEditingPluginConstants.MERGE_CONFIRM_MSG1);
			buf.append(traj1Index);
			buf.append(OmegaTrajectoryEditingPluginConstants.CONFIRM_MSG_AND);
			buf.append(traj2Index);
			buf.append(OmegaTrajectoryEditingPluginConstants.MERGE_CONFIRM_MSG2);
			buf.append(OmegaTrajectoryEditingPluginConstants.MERGE_CONFIRM_MSG3);
			buf.append(traj1Length + traj2Length);
			buf.append(OmegaTrajectoryEditingPluginConstants.MERGE_CONFIRM_MSG4);

			if (!this.showConfirmationDialog(OmegaTrajectoryEditingPluginConstants.MERGE_CONFIRM,
					buf.toString()))
				return;

			OmegaTrajectory from, to;
			if (selectedParticle.getFrameIndex() < this.mergeParticle
					.getFrameIndex()) {
				from = selectedTraj;
				to = this.mergeTrajectory;
			} else if (selectedParticle.getFrameIndex() > this.mergeParticle
					.getFrameIndex()) {
				from = this.mergeTrajectory;
				to = selectedTraj;
			} else
				// Throw error on the dialog
				return;

			// to.addROIs(from.getROIs());
			// to.recalculateLength();
			// this.trajectories.remove(from);
			this.pluginPanel.mergeTrajectories(from, to);
			this.mergeTrajectory = null;
			this.mergeParticle = null;
			this.setSelectedTrajectory(to);
			this.repaint();
			this.isMerging = false;
		} else {
			this.mergeTrajectory = selectedTraj;
			this.mergeParticle = selectedParticle;
			this.isMerging = true;
		}
	}

	private void splitTrajectory() {
		final OmegaTrajectory selectedTraj = this.getSelectedTrajectory();
		final OmegaROI selectedParticle = this.getSelectedParticle();

		final int particleIndex = selectedParticle.getFrameIndex();
		final int numParticles = selectedTraj.getROIs().indexOf(
				selectedParticle);
		final int newTrajLength = selectedTraj.getLength() - numParticles;

		final String trajName = selectedTraj.getName();
		final int trajNameIndex = trajName.indexOf("_") + 1;
		final String trajIndex = trajName.substring(trajNameIndex,
				trajName.length());

		final StringBuffer buf = new StringBuffer();
		buf.append(OmegaTrajectoryEditingPluginConstants.SPLIT_CONFIRM_MSG1);
		buf.append(trajIndex);
		buf.append(OmegaTrajectoryEditingPluginConstants.SPLIT_CONFIRM_MSG2);
		buf.append(particleIndex);
		buf.append(OmegaTrajectoryEditingPluginConstants.SPLIT_CONFIRM_MSG3);
		buf.append(numParticles);
		buf.append(OmegaTrajectoryEditingPluginConstants.CONFIRM_MSG_AND);
		buf.append(newTrajLength);
		buf.append(OmegaTrajectoryEditingPluginConstants.SPLIT_CONFIRM_MSG4);

		if (!this.showConfirmationDialog(OmegaTrajectoryEditingPluginConstants.SPLIT_CONFIRM,
				buf.toString()))
			return;

		// final OmegaTrajectory newTraj = new OmegaTrajectory(newTrajLength);
		// newTraj.setColor(Color.black);
		// for (int i = actualParticleIndex; i < this.actualTraj.getLength();
		// i++) {
		// final OmegaROI roi = this.actualTraj.getROIs().get(i);
		// newTraj.addROI(roi);
		// }
		// this.actualTraj.getROIs().removeAll(newTraj.getROIs());
		// this.actualTraj.recalculateLength();
		// this.trajectories.add(trajIndex + 1, newTraj);
		this.pluginPanel.splitTrajectory(selectedTraj, numParticles);

		this.repaint();
	}
}
