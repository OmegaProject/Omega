package edu.umassmed.omega.commons.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.RootPaneContainer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEventTBLoader;
import edu.umassmed.omega.commons.gui.dialogs.GenericConfirmationDialog;
import edu.umassmed.omega.commons.gui.interfaces.GenericTrajectoriesBrowserContainerInterface;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.commons.utilities.OmegaColorManagerUtilities;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class GenericTrajectoriesBrowserPanel extends GenericPanel implements
        OmegaMessageDisplayerPanelInterface {
	private static final long serialVersionUID = -4914198379223290679L;

	protected static final int SPOT_SIZE_DEFAULT = 30;
	protected static final int SPOT_SPACE_DEFAULT = 50;
	protected static final int TRAJECTORY_NAME_SPACE_MODIFIER = 4;
	protected static final int TRAJECTORY_SQUARE_BORDER = 3;

	private final GenericTrajectoriesBrowserContainerInterface tbContainer;

	private GenericTrajectoriesBrowserHeaderPanel tbHeaderPanel;
	private JScrollPane tbHeaderScrollPane;
	private GenericTrajectoriesBrowserTrajectoriesPanel tbTrajectoriesPanel;
	private JScrollPane tbTrajectoriesScrollPane;
	private GenericTrajectoriesBrowserNamesPanel tbNamesPanel;
	private JScrollPane tbNamesScrollPane;
	private GenericTrajectoriesBrowserLabelsPanel tbLabelsPanel;

	private final List<OmegaTrajectory> trajectories;
	private final List<OmegaTrajectory> selectedTrajectories;

	private JPopupMenu tbMenu;
	private JMenuItem showParticles_itm, generateRandomColors_itm,
	        chooseColor_itm;

	private OmegaGateway gateway;
	private OmegaImage img;

	private OmegaTrajectory selectedTraj;
	private OmegaROI selectedParticle;

	private int radius, numOfTraj, maxTrajLength;

	private int sizeT;
	private boolean isSpotsEnabled;

	private final boolean isShowEnabled;

	private final boolean isSelectionEnabled;

	public GenericTrajectoriesBrowserPanel(final RootPaneContainer parent,
	        final GenericTrajectoriesBrowserContainerInterface tbContainer,
	        final OmegaGateway gateway, final boolean showEnabled,
	        final boolean selectionEnabled) {
		super(parent);

		this.tbContainer = tbContainer;

		this.trajectories = new ArrayList<OmegaTrajectory>();
		this.selectedTrajectories = new ArrayList<OmegaTrajectory>();

		this.selectedTraj = null;
		this.selectedParticle = null;

		this.gateway = gateway;
		this.img = null;

		this.isSpotsEnabled = true;
		this.isShowEnabled = showEnabled;
		this.isSelectionEnabled = selectionEnabled;

		this.radius = 4;
		this.numOfTraj = 0;
		this.maxTrajLength = 0;
		this.sizeT = 0;

		this.createPopupMenu();

		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());

		this.tbLabelsPanel = new GenericTrajectoriesBrowserLabelsPanel(
		        this.getParentContainer(), this.isShowEnabled);
		topPanel.add(this.tbLabelsPanel, BorderLayout.WEST);

		this.tbHeaderPanel = new GenericTrajectoriesBrowserHeaderPanel(
		        this.getParentContainer(), this);
		this.tbHeaderScrollPane = new JScrollPane(this.tbHeaderPanel);
		this.tbHeaderScrollPane
		        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		this.tbHeaderScrollPane
		        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		topPanel.add(this.tbHeaderScrollPane, BorderLayout.CENTER);

		this.add(topPanel, BorderLayout.NORTH);

		this.tbNamesPanel = new GenericTrajectoriesBrowserNamesPanel(
		        this.getParentContainer(), this, this.isSelectionEnabled,
		        this.isShowEnabled);
		this.tbNamesScrollPane = new JScrollPane(this.tbNamesPanel);
		this.tbNamesScrollPane
		        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		this.tbNamesScrollPane
		        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(this.tbNamesScrollPane, BorderLayout.WEST);

		this.tbTrajectoriesPanel = new GenericTrajectoriesBrowserTrajectoriesPanel(
		        this.getParentContainer(), this, this.gateway,
		        this.isSelectionEnabled);
		this.tbTrajectoriesScrollPane = new JScrollPane(
		        this.tbTrajectoriesPanel);
		this.add(this.tbTrajectoriesScrollPane, BorderLayout.CENTER);
	}

	private void createPopupMenu() {
		this.tbMenu = new JPopupMenu();
		this.showParticles_itm = new JMenuItem(
				OmegaGUIConstants.TRACK_BROWSER_HIDE_SPOT_THUMB);
		this.generateRandomColors_itm = new JMenuItem(
		        OmegaGUIConstants.RANDOM_COLORS);
		this.chooseColor_itm = new JMenuItem(OmegaGUIConstants.CHOSE_COLOR);
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				GenericTrajectoriesBrowserPanel.this.handleResize();
			}
		});
		this.tbTrajectoriesPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(final MouseEvent evt) {
				GenericTrajectoriesBrowserPanel.this.handleMouseIn(evt
				        .getPoint());
			}

			@Override
			public void mouseExited(final MouseEvent evt) {
				GenericTrajectoriesBrowserPanel.this.handleMouseOut();
			}
		});
		this.tbTrajectoriesPanel
		        .addMouseMotionListener(new MouseMotionAdapter() {
			        @Override
			        public void mouseMoved(final MouseEvent evt) {
				        GenericTrajectoriesBrowserPanel.this
				                .handleMouseMovement(evt.getPoint());
			        }
		        });
		this.tbTrajectoriesScrollPane.getHorizontalScrollBar()
		        .addAdjustmentListener(new AdjustmentListener() {
			        @Override
			        public void adjustmentValueChanged(final AdjustmentEvent evt) {
				        GenericTrajectoriesBrowserPanel.this
				                .handleHorizontalScrollBarChanged();
			        }
		        });
		this.tbTrajectoriesScrollPane.getVerticalScrollBar()
		        .addAdjustmentListener(new AdjustmentListener() {
			        @Override
			        public void adjustmentValueChanged(final AdjustmentEvent evt) {
				        GenericTrajectoriesBrowserPanel.this
				                .handleVerticalScrollBarChanged();
			        }
		        });
		this.tbTrajectoriesPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				GenericTrajectoriesBrowserPanel.this.handleMouseClick(
				        evt.getPoint(), SwingUtilities.isRightMouseButton(evt),
				        evt.isControlDown());
			}
		});
		this.tbNamesPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				GenericTrajectoriesBrowserPanel.this.handleMouseClick(
				        evt.getPoint(), SwingUtilities.isRightMouseButton(evt),
				        evt.isControlDown());
			}
		});
		this.showParticles_itm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				GenericTrajectoriesBrowserPanel.this.handleShowSpotsThumbnail();
			}
		});
		this.generateRandomColors_itm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GenericTrajectoriesBrowserPanel.this
				        .handleGenerateRandomColors();
			}
		});
		this.chooseColor_itm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GenericTrajectoriesBrowserPanel.this.handlePickSingleColor();
			}
		});
	}

	private void handleResize() {
		this.tbTrajectoriesPanel.setPanelSize();
		this.tbNamesPanel.setPanelSize();
		this.tbHeaderPanel.setPanelSize();
	}

	protected void handleMouseOut() {

	}

	protected void handleMouseIn(final Point pos) {

	}

	protected void handleMouseMovement(final Point pos) {

	}

	private void handleHorizontalScrollBarChanged() {
		final int value = this.tbTrajectoriesScrollPane
		        .getHorizontalScrollBar().getValue();
		this.tbHeaderScrollPane.getHorizontalScrollBar().setValue(value);
	}

	private void handleVerticalScrollBarChanged() {
		final int value = this.tbTrajectoriesScrollPane.getVerticalScrollBar()
		        .getValue();
		this.tbNamesScrollPane.getVerticalScrollBar().setValue(value);
	}

	protected void handleMouseClick(final Point clickP,
	        final boolean isRightButton, final boolean isCtrlDown) {
		final OmegaTrajectory oldTraj = this.selectedTraj;
		this.resetClickReferences();
		this.findSelectedTrajectory(clickP);
		if (isRightButton) {
			if (this.selectedTraj != null) {
				this.findSelectedParticle(clickP);
			}
			this.createTrajectoryMenu();
			this.showTrajectoryMenu(clickP);
			this.selectedTrajectories.clear();
		} else {
			if (this.selectedTraj != null) {
				this.checkIfCheckboxAndSelect(clickP);
			}
			if (!isCtrlDown) {
				this.selectedTrajectories.clear();
			}
		}

		// FIXME there is a bug when right click on a track already selected, it
		// becomes unselected e when picked a item in the menu the methods does
		// not find selectedtraj reference!
		if ((this.selectedTraj != null)) {
			if (isRightButton || (this.selectedTraj != oldTraj)) {
				this.selectedTrajectories.add(this.selectedTraj);
			}
		} else {
			if (!isRightButton) {
				this.selectedTraj = null;
			}
			// this.selectedTraj = null;
		}
		this.sendEventTrajectories(this.selectedTrajectories, true);
		this.repaint();
	}

	protected void resetClickReferences() {
		this.selectedTraj = null;
		this.selectedParticle = null;
	}

	protected void findSelectedTrajectory(final Point clickP) {
		this.tbTrajectoriesPanel.findSelectedTrajectory(clickP);
	}

	protected void checkIfCheckboxAndSelect(final Point clickP) {
		this.tbNamesPanel.checkIfCheckboxAndSelect(clickP);
	}

	protected void findSelectedParticle(final Point clickP) {
		this.tbTrajectoriesPanel.findSelectedParticle(clickP);
	}

	protected void createTrajectoryMenu() {
		this.tbMenu.removeAll();

		final StringBuffer buf = new StringBuffer();
		int frameIndex = -1;
		if (this.selectedTraj != null) {
			buf.append("Track ");
			buf.append(this.selectedTraj.getName());
			if (this.selectedParticle != null) {
				frameIndex = this.selectedParticle.getFrameIndex() + 1;
				buf.append(" - Frame ");
				buf.append(frameIndex);
			}
			this.tbMenu.add(new JLabel(buf.toString()));
		}
		if (this.tbMenu.getComponentCount() > 0) {
			this.tbMenu.add(new JSeparator());
		}
		this.tbMenu.add(this.showParticles_itm);
		this.tbMenu.add(new JSeparator());
		this.tbMenu.add(this.generateRandomColors_itm);
		if (this.selectedTraj != null) {
			this.tbMenu.add(this.chooseColor_itm);
		}
	}

	protected void showTrajectoryMenu(final Point clickP) {
		this.tbMenu.show(this.tbTrajectoriesPanel, clickP.x, clickP.y);
	}

	private void handleShowSpotsThumbnail() {
		if (this.isSpotsEnabled) {
			this.showParticles_itm
			.setText(OmegaGUIConstants.TRACK_BROWSER_SHOW_SPOT_THUMB);
			this.isSpotsEnabled = !this.isSpotsEnabled;
		} else {
			this.showParticles_itm
			.setText(OmegaGUIConstants.TRACK_BROWSER_HIDE_SPOT_THUMB);
			this.isSpotsEnabled = !this.isSpotsEnabled;
		}
		this.repaint();
	}

	private void handleGenerateRandomColors() {
		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
		        this.getParentContainer(),
				OmegaGUIConstants.TRACK_RANDOM_COLOR_CONFIRM,
		        OmegaGUIConstants.TRACK_RANDOM_COLOR_CONFIRM_MSG, true);
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
		this.sendEventTrajectories(this.trajectories, false);
	}

	private void handlePickSingleColor() {
		final StringBuffer buf1 = new StringBuffer();
		buf1.append(OmegaGUIConstants.TRACK_CHOSE_COLOR_DIALOG_MSG);
		buf1.append(this.selectedTraj.getName());

		final Color c = OmegaColorManagerUtilities.openPaletteColor(this,
		        buf1.toString(), this.selectedTraj.getColor());

		final StringBuffer buf2 = new StringBuffer();
		buf2.append(OmegaGUIConstants.TRACK_CHOSE_COLOR_CONFIRM_MSG);
		buf2.append(this.selectedTraj.getName());
		buf2.append("?");

		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
		        this.getParentContainer(),
		        OmegaGUIConstants.TRACK_CHOSE_COLOR_CONFIRM, buf2.toString(),
		        true);
		dialog.setVisible(true);
		if (!dialog.getConfirmation())
			return;

		this.selectedTraj.setColor(c);
		this.selectedTraj.setColorChanged(true);
		this.repaint();
		final List<OmegaTrajectory> trajectories = new ArrayList<OmegaTrajectory>();
		trajectories.addAll(this.trajectories);
		this.sendEventTrajectories(trajectories, false);
	}

	protected void addToPaint(final Graphics2D g2D) {

	}

	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
	        final boolean selection) {
		if (this.img == null) {
			int maxT = -1;
			for (final OmegaTrajectory traj : trajectories) {
				if (maxT < traj.getLength()) {
					maxT = traj.getLength();
				}
			}
			this.sizeT = maxT;
		}
		if (selection) {
			this.resetClickReferences();
			this.selectedTrajectories.clear();
			if (trajectories != null) {
				this.selectedTrajectories.addAll(trajectories);
				if (!trajectories.isEmpty()) {
					this.centerOnTrajectory(trajectories.get(0));
				}
			}
		} else {
			this.trajectories.clear();
			if (trajectories != null) {
				this.trajectories.addAll(trajectories);
				this.tbTrajectoriesPanel.setPanelSize();
			}
			this.setTrajectoriesValues();
		}
		// TODO try to simplify the paint method dividing it for single
		// trajectory or something similar its useless to repaint everything
		// each time
		this.handleResize();
		this.repaint();
	}

	private void centerOnTrajectory(final OmegaTrajectory traj) {
		final Point p = this.tbTrajectoriesPanel.findTrajectoryLocation(traj);
		if (p != null) {
			final int xPos = p.x
			        - (this.tbTrajectoriesScrollPane.getWidth() / 2);
			final int yPos = p.y
			        - (this.tbTrajectoriesScrollPane.getHeight() / 2);
			this.tbTrajectoriesScrollPane.getVerticalScrollBar().setValue(yPos);
			this.tbTrajectoriesScrollPane.getHorizontalScrollBar().setValue(
			        xPos);
		}
	}

	public void addTrajectoriesToSelection(
	        final List<OmegaTrajectory> trajectories, final int index) {
		this.selectedTrajectories.addAll(index, trajectories);
	}

	public int removeTrajectoriesFromSelection(
	        final List<OmegaTrajectory> trajectories) {
		if (trajectories.isEmpty())
			return this.selectedTrajectories.size();
		int index = this.selectedTrajectories.indexOf(trajectories.get(0));
		this.selectedTrajectories.removeAll(trajectories);
		if (index < 0) {
			index = 0;
		}
		return index;
	}

	private void setTrajectoriesValues() {
		this.numOfTraj = this.trajectories.size();
		if (this.trajectories.isEmpty()) {
			this.maxTrajLength = 0;
			return;
		}
		for (final OmegaTrajectory trajectory : this.trajectories) {
			if (this.maxTrajLength < trajectory.getLength()) {
				this.maxTrajLength = trajectory.getLength();
			}
		}
	}

	public void setRadius(final int radius) {
		this.radius = radius;
	}

	protected int getRadius() {
		return this.radius;
	}

	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
		this.tbTrajectoriesPanel.setGateway(gateway);
		final OmegaImagePixels pixels = this.img.getDefaultPixels();
		final Double physicalSizeT = gateway.computeSizeT(
		        pixels.getElementID(), pixels.getSizeT(), pixels.getSizeT());
		this.tbHeaderPanel.setPhysicalSizeT(physicalSizeT);
		this.tbLabelsPanel.setHasPhysicalSizeT(physicalSizeT != null);
	}

	public void setImage(final OmegaImage image) {
		if (image == null) {
			this.sizeT = -1;
			this.tbHeaderPanel.setPhysicalSizeT(null);
		} else {
			this.img = image;
			final OmegaImagePixels pixels = this.img.getDefaultPixels();
			this.sizeT = pixels.getSizeT();
			final Double physicalSizeT = this.gateway.computeSizeT(
			        pixels.getElementID(), this.sizeT, this.sizeT);
			this.tbHeaderPanel.setPhysicalSizeT(physicalSizeT);
			this.tbLabelsPanel.setHasPhysicalSizeT(physicalSizeT != null);
		}
		this.tbTrajectoriesPanel.setImage(image);
	}

	@Override
	public void updateMessageStatus(final OmegaMessageEvent evt) {
		final OmegaMessageEventTBLoader specificEvt = (OmegaMessageEventTBLoader) evt;
		this.updateStatus(specificEvt.getMessage());
		if (specificEvt.isRepaint()) {
			this.tbTrajectoriesPanel.loadBufferedImages();
		}
	}

	protected Point getClickPosition() {
		return this.tbTrajectoriesPanel.getClickPosition();
	}

	protected OmegaTrajectory getSelectedTrajectory() {
		return this.selectedTraj;
	}

	protected void setSelectedTrajectory(final OmegaTrajectory selectedTraj) {
		this.selectedTraj = selectedTraj;
	}

	protected OmegaROI getSelectedParticle() {
		return this.selectedParticle;
	}

	protected void setSelectedParticle(final OmegaROI selectedParticle) {
		this.selectedParticle = selectedParticle;
	}

	public List<OmegaTrajectory> getSelectedTrajectories() {
		return this.selectedTrajectories;
	}

	protected JPopupMenu getMenu() {
		return this.tbMenu;
	}

	protected int getSizeT() {
		return this.sizeT;
	}

	protected int getMaxTrajectoryLength() {
		return this.maxTrajLength;
	}

	protected int getNumberOfTrajectories() {
		return this.numOfTraj;
	}

	protected List<OmegaTrajectory> getTrajectories() {
		return this.trajectories;
	}

	protected boolean isShowParticles() {
		return this.isSpotsEnabled;
	}

	public void setShowParticles(final boolean showParticles) {
		this.isSpotsEnabled = showParticles;
	}

	protected void sendEventTrajectories(
	        final List<OmegaTrajectory> selectedTrajectories,
	        final boolean selected) {
		this.tbContainer.sendEventTrajectories(selectedTrajectories, selected);
	}

	protected void updateStatus(final String message) {
		this.tbContainer.updateStatus(message);
	}

	public Dimension getTrajectoriesPanelSize() {
		final Dimension size = this.getSize();
		final Dimension headerSize = this.tbHeaderPanel.getSize();
		return new Dimension(size.width - headerSize.width, size.height
		        - headerSize.height - 25);
	}
}
