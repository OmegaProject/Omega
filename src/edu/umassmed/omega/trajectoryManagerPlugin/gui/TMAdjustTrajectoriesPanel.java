package edu.umassmed.omega.trajectoryManagerPlugin.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.eventSystem.OmegaMessageEvent;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.dialogs.GenericConfirmationDialog;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanel;
import edu.umassmed.omega.commons.utilities.OmegaColorManagerUtilities;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaROI;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.trajectoryManagerPlugin.runnable.TMFrameImagesLoader;
import edu.umassmed.omega.trajectoryManagerPlugin.runnable.TMLoaderMessage;

public class TMAdjustTrajectoriesPanel extends GenericPanel implements
        OmegaMessageDisplayerPanel {

	private final int PARTICLE_SIZE = 30;
	private final int PARTICLE_SPACE = 50;

	private static final long serialVersionUID = -4914198379223290679L;

	private OmegaGateway gateway;
	private OmegaImage img;
	private final List<BufferedImage> buffImages;

	private final TMPluginPanel pluginPanel;
	private final List<OmegaTrajectory> trajectories;

	private final List<OmegaTrajectory> selectedTrajectories;
	private int numOfTraj, maxTrajLength, radius;

	private JPopupMenu trajectoryMenu;
	private JMenuItem splitTrajMenuItem, mergeTrajMenuItem;
	private JMenuItem generateRandomColors, chooseColor;
	private boolean isMerging, isMouseIn;
	private Point clickPosition, mousePosition;

	private final Map<Point, OmegaROI> particlesMap;
	private final Map<Integer, OmegaTrajectory> trajectoriesMap;
	private final List<Point> checkboxes;

	private OmegaTrajectory actualTraj, mergeTrajectory;
	private OmegaROI previousParticle, actualParticle, nextParticle,
	        mergeParticle;
	private final JCheckBox cb;

	private Thread frameLoaderThread;
	private TMFrameImagesLoader frameLoader;

	public TMAdjustTrajectoriesPanel(final RootPaneContainer parent,
	        final TMPluginPanel pluginPanel, final OmegaGateway gateway) {
		super(parent);

		this.gateway = gateway;
		this.img = null;
		this.buffImages = new ArrayList<BufferedImage>();

		this.frameLoaderThread = null;
		this.frameLoader = null;

		this.radius = 4;

		this.pluginPanel = pluginPanel;

		this.numOfTraj = 0;
		this.maxTrajLength = 0;
		this.isMerging = false;
		this.isMouseIn = false;
		this.mousePosition = null;
		this.clickPosition = null;

		this.trajectories = new ArrayList<OmegaTrajectory>();
		this.selectedTrajectories = new ArrayList<OmegaTrajectory>();
		this.particlesMap = new LinkedHashMap<Point, OmegaROI>();
		this.trajectoriesMap = new LinkedHashMap<Integer, OmegaTrajectory>();
		this.checkboxes = new ArrayList<Point>();

		this.cb = new JCheckBox();
		this.cb.setSelectedIcon(new ImageIcon(OmegaConstants.OMEGA_IMGS_FOLDER
		        + File.separatorChar + "checkbox_selected.png"));
		this.cb.setIcon(new ImageIcon(OmegaConstants.OMEGA_IMGS_FOLDER
		        + File.separatorChar + "checkbox_deselected.png"));

		this.createPopupMenu();

		this.addListeners();
	}

	private void createPopupMenu() {
		this.trajectoryMenu = new JPopupMenu();
		this.splitTrajMenuItem = new JMenuItem("Split trajectory");
		this.mergeTrajMenuItem = new JMenuItem("Merge trajectories");
		this.generateRandomColors = new JMenuItem(
		        "Generate random trajectories colors");
		this.chooseColor = new JMenuItem("Choose trajectory color");
	}

	@Override
	public void paint(final Graphics g) {
		if (this.trajectories == null)
			return;
		this.numOfTraj = this.trajectories.size();
		for (final OmegaTrajectory trajectory : this.trajectories) {
			if (this.maxTrajLength < trajectory.getLength()) {
				this.maxTrajLength = trajectory.getLength();
			}
		}

		final int trajNameSpaceModifier = 4;

		int width = (this.maxTrajLength + 1 + trajNameSpaceModifier)
		        * this.PARTICLE_SPACE;
		int height = (this.numOfTraj + 2) * this.PARTICLE_SPACE;
		final Dimension parentDim = this.getParent().getSize();
		if (parentDim.width > width) {
			width = parentDim.width;
		}
		if (parentDim.height > height) {
			height = parentDim.height;
		}
		final Dimension dim = new Dimension(width, height);
		this.setPreferredSize(dim);
		this.setSize(dim);

		final Graphics2D g2D = (Graphics2D) g;
		g2D.setBackground(Color.white);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
		        RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.clearRect(0, 0, width, height);

		for (int y = 1; y <= this.numOfTraj; y++) {
			final OmegaTrajectory traj = this.trajectories.get(y - 1);
			final int yPos = this.PARTICLE_SPACE * y;
			if (this.selectedTrajectories.contains(traj)) {
				g2D.setBackground(OmegaConstants
				        .getDefaultSelectionBackgroundColor());
				g2D.clearRect(0, yPos + (this.PARTICLE_SPACE / 2), width,
				        this.PARTICLE_SPACE);
				g2D.setBackground(Color.white);
			}
		}

		this.checkboxes.clear();
		for (int y = 1; y <= this.numOfTraj; y++) {
			final int xPos = this.PARTICLE_SPACE;
			final int yPos = (this.PARTICLE_SPACE * (y + 1)) + 5;
			if (y > 0) {
				final OmegaTrajectory traj = this.trajectories.get(y - 1);
				g2D.drawString(traj.getName(), xPos, yPos);
				Icon icon;
				if (traj.isVisible()) {
					icon = this.cb.getSelectedIcon();
				} else {
					icon = this.cb.getIcon();
					// actualIcon = UIManager.getLookAndFeel()
					// .getDisabledSelectedIcon(cb, checkedIcon);
				}
				final int adjYPos = yPos - (this.PARTICLE_SIZE / 2);
				icon.paintIcon(this.cb, g2D, 5, adjYPos);
				this.checkboxes.add(new Point(5, adjYPos));
			}
		}

		final Font font = g2D.getFont();
		final AffineTransform fontAT = new AffineTransform();
		fontAT.rotate(Math.toRadians(-45));
		final Font newFont = font.deriveFont(fontAT);
		g2D.setFont(newFont);

		g2D.drawString("Shown",
		        (this.PARTICLE_SIZE - (this.PARTICLE_SIZE / 2)) + 5,
		        this.PARTICLE_SIZE * 2);
		g2D.drawString("Name", (this.PARTICLE_SIZE * 2) + 5,
		        this.PARTICLE_SIZE * 2);

		for (int x = 1; x <= this.maxTrajLength; x++) {
			final int xPos = (this.PARTICLE_SPACE * (x + trajNameSpaceModifier)) + 5;
			final int yPos = this.PARTICLE_SPACE;
			g2D.drawString(String.valueOf(x), xPos, yPos);
		}
		g2D.setFont(font);

		this.trajectoriesMap.clear();
		this.particlesMap.clear();

		for (int x = 1; x <= this.maxTrajLength; x++) {
			BufferedImage bufferedImage = null;
			if (this.buffImages.size() > (x - 1)) {
				bufferedImage = this.buffImages.get(x - 1);
			}
			for (int y = 1; y <= this.numOfTraj; y++) {
				final OmegaTrajectory traj = this.trajectories.get(y - 1);
				final int yPos = this.PARTICLE_SPACE * (y + 1);
				if (!this.trajectoriesMap
				        .containsKey(yPos - this.PARTICLE_SIZE)) {
					this.trajectoriesMap.put(yPos - this.PARTICLE_SIZE, traj);
				}

				if (traj.getROIs().size() <= (x - 1)) {
					continue;
				}
				final OmegaROI roi = traj.getROIs().get(x - 1);
				final int roiIndex = roi.getFrameIndex();
				final int xPos = this.PARTICLE_SPACE
				        * (roiIndex + 1 + trajNameSpaceModifier);
				final int xAdj = xPos - (this.PARTICLE_SIZE / 2);
				final int yAdj = yPos - (this.PARTICLE_SIZE / 2);
				final Point p = new Point(xAdj, yAdj);
				this.particlesMap.put(p, roi);
				if (traj.getColor() == null) {
					// generate random colors
				}
				// TODO add image from buffImage
				if (bufferedImage == null) {
					g2D.setColor(traj.getColor());
					g2D.fillRect(xAdj, yAdj, this.PARTICLE_SIZE,
					        this.PARTICLE_SIZE);
					g2D.setColor(Color.black);
				} else {
					final double xD = roi.getX();
					final double yD = roi.getY();
					final int x1 = new BigDecimal(String.valueOf(xD)).setScale(
					        0, BigDecimal.ROUND_HALF_UP).intValue();
					final int y1 = new BigDecimal(String.valueOf(yD)).setScale(
					        0, BigDecimal.ROUND_HALF_UP).intValue();
					g2D.setColor(traj.getColor());
					g2D.drawRect(xAdj, yAdj, this.PARTICLE_SIZE,
					        this.PARTICLE_SIZE);
					g2D.setColor(Color.black);
					final int xS1 = x1 - this.radius;
					final int yS1 = y1 - this.radius;
					final int xS2 = x1 + this.radius;
					final int yS2 = y1 + this.radius;
					g2D.drawImage(bufferedImage, xAdj + 1, yAdj + 1, xAdj
					        + (this.PARTICLE_SIZE - 1), yAdj
					        + (this.PARTICLE_SIZE - 1), xS1, yS1, xS2, yS2,
					        this);
				}
			}
		}

		if (this.isMerging && this.isMouseIn) {
			g2D.setColor(Color.red);
			g2D.drawLine(this.clickPosition.x, this.clickPosition.y,
			        this.mousePosition.x, this.mousePosition.y);
		}
	}

	private void addListeners() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				TMAdjustTrajectoriesPanel.this.handleMouseClick(evt.getPoint(),
				        SwingUtilities.isRightMouseButton(evt),
				        evt.isControlDown());
			}

			@Override
			public void mouseEntered(final MouseEvent evt) {
				TMAdjustTrajectoriesPanel.this.isMouseIn = true;
				TMAdjustTrajectoriesPanel.this.mousePosition = evt.getPoint();
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				TMAdjustTrajectoriesPanel.this.isMouseIn = false;
				TMAdjustTrajectoriesPanel.this.mousePosition = null;
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(final MouseEvent evt) {
				if (!TMAdjustTrajectoriesPanel.this.isMerging)
					return;
				TMAdjustTrajectoriesPanel.this.mousePosition = evt.getPoint();
				TMAdjustTrajectoriesPanel.this.repaint();
			}
		});
		this.splitTrajMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMAdjustTrajectoriesPanel.this.splitTrajectory();
				TMAdjustTrajectoriesPanel.this.sendTMPluginTrajectoriesEvent(
				        TMAdjustTrajectoriesPanel.this.trajectories, false);
			}
		});
		this.mergeTrajMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMAdjustTrajectoriesPanel.this.mergeTrajectory();
				TMAdjustTrajectoriesPanel.this.sendTMPluginTrajectoriesEvent(
				        TMAdjustTrajectoriesPanel.this.trajectories, false);
			}
		});
		this.generateRandomColors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMAdjustTrajectoriesPanel.this.handleGenerateRandomColors();
			}
		});
		this.chooseColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMAdjustTrajectoriesPanel.this.handlePickSingleColor();
			}
		});
	}

	private void handleGenerateRandomColors() {
		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
		        this.getParentContainer(),
		        "Random colors generation confirmation",
		        "Do you want do generate new random colors for the trajectories?",
		        true);
		dialog.setVisible(true);
		if (!dialog.getConfirmation())
			return;
		final List<Color> colors = OmegaColorManagerUtilities
		        .generateRandomColors(this.trajectories.size());
		for (int i = 0; i < this.trajectories.size(); i++) {
			final OmegaTrajectory traj = this.trajectories.get(i);
			final Color c = colors.get(i);
			traj.setColor(c);
			traj.setColorChanged(true);
		}
		this.repaint();
		this.sendTMPluginTrajectoriesEvent(
		        TMAdjustTrajectoriesPanel.this.trajectories, false);
	}

	private void handlePickSingleColor() {
		final StringBuffer buf1 = new StringBuffer();
		buf1.append("Choose color for trajectory ");
		buf1.append(this.actualTraj.getName());

		final Color c = OmegaColorManagerUtilities.openPaletteColor(this,
		        buf1.toString(), this.actualTraj.getColor());

		final StringBuffer buf2 = new StringBuffer();
		buf2.append("Do you want to color trajectory ");
		buf2.append(this.actualTraj.getName());
		buf2.append("?");

		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
		        this.getParentContainer(), "Choose single color confirmation",
		        buf2.toString(), true);
		dialog.setVisible(true);
		if (!dialog.getConfirmation())
			return;

		this.actualTraj.setColor(c);
		this.actualTraj.setColorChanged(true);
		this.repaint();
		final List<OmegaTrajectory> trajectories = new ArrayList<OmegaTrajectory>();
		trajectories.add(TMAdjustTrajectoriesPanel.this.actualTraj);
		this.sendTMPluginTrajectoriesEvent(trajectories, false);
	}

	private void handleMouseClick(final Point clickP,
	        final boolean isRightButton, final boolean isCtrlDown) {
		this.resetClickReferences();
		this.findActualTrajectory(clickP);
		if (isRightButton) {
			if (this.actualTraj != null) {
				this.findActualParticle(clickP);
			}
			this.showTrajectoryMenu(clickP);
			this.selectedTrajectories.clear();
		} else {
			if (this.actualTraj != null) {
				this.checkIfCheckboxAndSelect(clickP);
			}
			if (!isCtrlDown) {
				this.selectedTrajectories.clear();
			}
			if (this.isMerging) {
				this.mergeTrajectory = null;
				this.mergeParticle = null;
				this.isMerging = false;
			}
		}
		if (this.actualTraj != null) {
			this.selectedTrajectories.add(this.actualTraj);
			this.repaint();
			this.sendTMPluginTrajectoriesEvent(this.selectedTrajectories, true);
		}

		this.pluginPanel.updateSegmentTrajectories(this.selectedTrajectories);
	}

	private void sendTMPluginTrajectoriesEvent(
	        final List<OmegaTrajectory> trajectories, final boolean selection) {
		this.pluginPanel.fireTMPluginTrajectoriesEvent(trajectories, selection);
	}

	private void checkIfCheckboxAndSelect(final Point clickP) {
		for (final Point p : this.checkboxes) {
			if ((clickP.x < (p.x + 20)) && (clickP.x > p.x)
			        && (clickP.y < (p.y + 20)) && (clickP.y > p.y)) {
				this.actualTraj.setVisible(!this.actualTraj.isVisible());
				return;
			}
		}
	}

	private void mergeTrajectory() {
		if (this.isMerging) {
			final String traj1Name = this.mergeTrajectory.getName();
			final int traj1NameIndex = traj1Name.indexOf("_") + 1;
			final String traj1Index = traj1Name.substring(traj1NameIndex,
			        traj1Name.length());
			final int traj1Length = this.mergeTrajectory.getLength();

			final String traj2Name = this.actualTraj.getName();
			final int traj2NameIndex = traj2Name.indexOf("_") + 1;
			final String traj2Index = traj2Name.substring(traj2NameIndex,
			        traj2Name.length());
			final int traj2Length = this.actualTraj.getLength();

			final StringBuffer buf = new StringBuffer();
			buf.append("<html>Trajectories ");
			buf.append(traj1Index);
			buf.append(" and ");
			buf.append(traj2Index);
			buf.append(" will be merged.<br>");
			buf.append("Resulting trajectory will contain ");
			buf.append(traj1Length + traj2Length);
			buf.append(" elements</html>");

			if (!this.showTMConfirmationDialog(
			        "Merge trajectories confirmation", buf.toString()))
				return;

			OmegaTrajectory from, to;
			if (this.actualParticle.getFrameIndex() < this.mergeParticle
			        .getFrameIndex()) {
				from = this.actualTraj;
				to = this.mergeTrajectory;
			} else if (this.actualParticle.getFrameIndex() > this.mergeParticle
			        .getFrameIndex()) {
				from = this.mergeTrajectory;
				to = this.actualTraj;
			} else
				// Throw error on the dialog
				return;

			// to.addROIs(from.getROIs());
			// to.recalculateLength();
			// this.trajectories.remove(from);
			this.pluginPanel.mergeTrajectories(from, to);
			this.mergeTrajectory = null;
			this.mergeParticle = null;
			this.actualTraj = to;
			this.repaint();
			this.isMerging = false;
		} else {
			this.mergeTrajectory = this.actualTraj;
			this.mergeParticle = this.actualParticle;
			this.isMerging = true;
		}

	}

	private void splitTrajectory() {
		final int actualParticleIndex = this.actualTraj.getROIs().indexOf(
		        this.actualParticle);
		final int newTrajLength = this.actualTraj.getLength()
		        - actualParticleIndex;

		final String trajName = this.actualTraj.getName();
		final int trajNameIndex = trajName.indexOf("_") + 1;
		final String trajIndex = trajName.substring(trajNameIndex,
		        trajName.length());

		final StringBuffer buf = new StringBuffer();
		buf.append("<html>Trajectory ");
		buf.append(trajIndex);
		buf.append(" will be split.<br>");
		buf.append("Resulting trajectories will contain respectively ");
		buf.append(actualParticleIndex);
		buf.append(" and ");
		buf.append(newTrajLength);
		buf.append(" elements</html>");

		if (!this.showTMConfirmationDialog("Split trajectory confirmation",
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
		this.pluginPanel.splitTrajectory(this.actualTraj, actualParticleIndex);

		this.repaint();
	}

	private boolean showTMConfirmationDialog(final String title,
	        final String label) {
		final RootPaneContainer parentContainer = this.getParentContainer();
		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
		        parentContainer, title, label, true);
		dialog.setVisible(true);
		return dialog.getConfirmation();
	}

	private void showTrajectoryMenu(final Point clickP) {
		this.trajectoryMenu.removeAll();

		final StringBuffer buf = new StringBuffer();
		int frameIndex = -1;
		if (this.actualTraj != null) {
			buf.append("Trajectory ");
			buf.append(this.trajectories.indexOf(this.actualTraj) + 1);
			if (this.actualParticle != null) {
				frameIndex = this.actualParticle.getFrameIndex() + 1;
				buf.append(" - Frame ");
				buf.append(frameIndex);
			}
			this.trajectoryMenu.add(new JLabel(buf.toString()));
		}

		if (!this.isMerging) {
			this.mergeTrajMenuItem.setText("Merge trajectories start");
		} else {
			this.mergeTrajMenuItem.setText("Merge trajectories end");
		}

		if (this.previousParticle != null) {
			if ((this.nextParticle == null)
			        && (frameIndex < this.maxTrajLength)) {

				this.trajectoryMenu.add(this.mergeTrajMenuItem);
			}
			if (!this.isMerging) {
				this.trajectoryMenu.add(this.splitTrajMenuItem);
			}
		} else if ((this.nextParticle != null)
		        && (this.actualParticle.getFrameIndex() > 0)) {
			this.trajectoryMenu.add(this.mergeTrajMenuItem);
		}

		if (this.trajectoryMenu.getComponentCount() > 1) {
			this.trajectoryMenu.add(new JSeparator(), 1);
		}

		this.trajectoryMenu.add(new JSeparator());
		this.trajectoryMenu.add(this.generateRandomColors);
		if (this.actualTraj != null) {
			this.trajectoryMenu.add(this.chooseColor);
		}

		TMAdjustTrajectoriesPanel.this.trajectoryMenu.show(this, clickP.x,
		        clickP.y);
	}

	private void resetClickReferences() {
		this.actualTraj = null;
		this.actualParticle = null;
		this.previousParticle = null;
		this.nextParticle = null;
	}

	private void findActualTrajectory(final Point clickP) {
		for (final Integer y : this.trajectoriesMap.keySet()) {
			if ((clickP.y > y) && (clickP.y < (y + this.PARTICLE_SPACE))) {
				this.actualTraj = this.trajectoriesMap.get(y);
				return;
			}
		}
	}

	private void findActualParticle(final Point clickP) {
		for (final Point p : this.particlesMap.keySet()) {
			final int pX = p.x + this.PARTICLE_SIZE;
			final int pY = p.y + this.PARTICLE_SIZE;
			if ((clickP.x > p.x) && (clickP.x < pX)) {
				if ((clickP.y > p.y) && (clickP.y < pY)) {
					this.clickPosition = clickP;
					this.actualParticle = this.particlesMap.get(p);
					this.findPreviousAndNextParticle();
					return;
				}
			}
		}
	}

	private void findPreviousAndNextParticle() {
		final List<OmegaROI> rois = this.actualTraj.getROIs();
		for (int i = 0; i < rois.size(); i++) {
			final OmegaROI trajROI = rois.get(i);
			if (trajROI == this.actualParticle) {
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

	// public void updateTrajectories(
	// final OmegaParticleLinkingRun omegaParticleLinkingRun) {
	// this.trajectories = omegaParticleLinkingRun.getResultingTrajectory();
	// this.repaint();
	// }

	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
	        final boolean selection) {
		if (selection) {
			this.resetClickReferences();
			this.selectedTrajectories.clear();
			this.selectedTrajectories.addAll(trajectories);
		} else {
			this.trajectories.clear();
			this.trajectories.addAll(trajectories);
		}
		// TODO try to simplify the paint method dividing it for single
		// trajectory or something similar its useless to repaint everything
		// each time
		this.repaint();
	}

	public void setRadius(final int radius) {
		this.radius = radius;
	}

	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
	}

	public void setImage(final OmegaImage image) {
		this.img = image;
		this.buffImages.clear();
		if ((this.frameLoaderThread != null)
		        && this.frameLoaderThread.isAlive())
			// TODO notificare che non si puo lanciare la nuova thread finche la
			// vecchia e' morta
			return;
		this.frameLoader = new TMFrameImagesLoader(this, this.gateway, this.img);
		this.frameLoaderThread = new Thread(this.frameLoader);
		this.frameLoaderThread.start();
	}

	@Override
	public void updateMessageStatus(final OmegaMessageEvent evt) {
		final TMLoaderMessage specificEvt = (TMLoaderMessage) evt;
		this.pluginPanel.updateStatus(specificEvt.getMessage());
		if (specificEvt.isRepaint()) {
			this.buffImages.clear();
			this.buffImages.addAll(this.frameLoader.getImages());
			this.repaint();
		}
	}
}
