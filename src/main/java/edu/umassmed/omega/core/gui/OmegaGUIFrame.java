/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School Alessandro
 * Rigano (Program in Molecular Medicine) Caterina Strambio De Castillia
 * (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team:
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli,
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban,
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts: Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.OmegaLoadedData;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainerInterface;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OrphanedAnalysisContainer;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaDBServerInformation;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaLoginCredentials;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEvent;
import edu.umassmed.omega.commons.gui.GenericFrame;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.core.OmegaApplication;

public class OmegaGUIFrame extends JFrame {

	private static final long serialVersionUID = -4775204088456661307L;

	private static double SPLIT_PERCENT = 0.75;

	private final OmegaApplication omegaApp;

	private final List<JFrame> separatedFrames;

	private OmegaTopPanel topPanel;
	private OmegaWorkspacePanel workspacePanel;
	private OmegaSidePanel sidePanel;

	private final OmegaDBPreferencesFrame omegaDbPrefFrame;

	private JMenuBar menu;
	private JMenu fileMenu, viewMenu, editMenu;
	private JMenuItem omegaDbSaveMItem, omegaDbLoadMItem,
			omegaDbLoadOrphanedMItem, aboutMItem, quitMItem;
	// private JMenuItem tracksImporterMItem, tracksExporterMItem,
	// diffExporterMItem, dataExporterMItem;
	private JMenuItem sepUnifyInterfaceMItem;
	private JMenuItem omegaDbOptionsMItem, omegaDbUpdateTrajectoriesMItem;

	private JSplitPane mainSplitPane;

	private JScrollPane leftScrollPane, rightScrollPane;

	private long pluginSelected;
	private boolean isAttached;

	private int previousLoc;
	private Dimension oldSplitPaneDimension;
	private double dividerLocation;

	public OmegaGUIFrame(final OmegaApplication omegaApp) {
		this.omegaApp = omegaApp;

		this.setTitle("OMEGA - Open Microscopy Environment inteGrated Analysis");
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());

		this.separatedFrames = new ArrayList<JFrame>();
		this.isAttached = true;

		// this.omegaDbPrefFrame = new OmegaDBPreferencesFrame(this,
		// omegaApp.getMySqlGateway());
		this.omegaDbPrefFrame = new OmegaDBPreferencesFrame(this);

		this.previousLoc = -1;
		this.oldSplitPaneDimension = null;
		this.dividerLocation = 0.75;

		this.createAndAddWidgets();
		this.createMenu();
		this.setJMenuBar(this.menu);

		this.addListeners();
	}

	public void initialize(final Map<Long, OmegaPlugin> registeredPlugin) {
		this.topPanel.initializePanel(registeredPlugin);
		this.workspacePanel.initializePanel();
		this.sidePanel.initializePanel();
	}

	public void reinitializeStrings() {
		this.topPanel.reinitializeStrings();
		// this.workspacePanel.reinitializeStrings();
		// this.sidePanel.reinitializeStrings();
	}

	private void createAndAddWidgets() {
		this.topPanel = new OmegaTopPanel(this);
		this.getContentPane().add(this.topPanel, BorderLayout.NORTH);

		this.mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		this.getContentPane().add(this.mainSplitPane, BorderLayout.CENTER);

		this.workspacePanel = new OmegaWorkspacePanel(this);
		this.leftScrollPane = new JScrollPane(this.workspacePanel);
		this.mainSplitPane.setLeftComponent(this.leftScrollPane);

		this.sidePanel = new OmegaSidePanel(this);
		this.rightScrollPane = new JScrollPane(this.sidePanel);
		this.mainSplitPane.setRightComponent(this.rightScrollPane);

		this.setSplitPanelDividerLocation(OmegaGUIFrame.SPLIT_PERCENT);

		// Display the window.
	}

	private void createMenu() {
		this.menu = new JMenuBar();
		this.fileMenu = new JMenu(OmegaGUIConstants.MENU_FILE);
		// this.tracksImporterMItem = new JMenuItem(
		// OmegaGUIConstants.MENU_FILE_IMPORT_TRACKS);
		// this.tracksImporterMItem
		// .setToolTipText(OmegaGUIConstants.MENU_FILE_IMPORT_TRACKS_TT);
		// this.fileMenu.add(this.tracksImporterMItem);
		// this.tracksExporterMItem = new JMenuItem(
		// OmegaGUIConstants.MENU_FILE_EXPORT_TRACKS);
		// this.tracksExporterMItem
		// .setToolTipText(OmegaGUIConstants.MENU_FILE_EXPORT_TRACKS_TT);
		// this.fileMenu.add(this.tracksExporterMItem);
		// this.diffExporterMItem = new JMenuItem(
		// OmegaGUIConstants.MENU_FILE_EXPORT_DIFF);
		// this.diffExporterMItem
		// .setToolTipText(OmegaGUIConstants.MENU_FILE_EXPORT_DIFF_TT);
		// this.fileMenu.add(this.diffExporterMItem);
		// this.dataExporterMItem = new JMenuItem(
		// OmegaGUIConstants.MENU_FILE_EXPORT_DATA);
		// this.dataExporterMItem
		// .setToolTipText(OmegaGUIConstants.MENU_FILE_EXPORT_DATA_TT);
		// this.fileMenu.add(this.dataExporterMItem);
		this.fileMenu.add(new JSeparator());
		this.omegaDbLoadMItem = new JMenuItem(OmegaGUIConstants.MENU_FILE_LOAD);
		this.omegaDbLoadMItem
				.setToolTipText(OmegaGUIConstants.MENU_FILE_LOAD_TT);
		this.fileMenu.add(this.omegaDbLoadMItem);
		this.omegaDbLoadOrphanedMItem = new JMenuItem(
				OmegaGUIConstants.MENU_FILE_LOAD_ORPHANED);
		this.omegaDbLoadOrphanedMItem
				.setToolTipText(OmegaGUIConstants.MENU_FILE_LOAD_ORPHANED_TT);
		this.fileMenu.add(this.omegaDbLoadOrphanedMItem);
		this.omegaDbSaveMItem = new JMenuItem(OmegaGUIConstants.MENU_FILE_SAVE);
		this.omegaDbSaveMItem
				.setToolTipText(OmegaGUIConstants.MENU_FILE_SAVE_TT);
		this.fileMenu.add(this.omegaDbSaveMItem);
		this.fileMenu.add(new JSeparator());
		this.aboutMItem = new JMenuItem(OmegaGUIConstants.MENU_FILE_ABOUT);
		this.fileMenu.add(this.aboutMItem);
		this.fileMenu.add(new JSeparator());
		this.quitMItem = new JMenuItem(OmegaGUIConstants.MENU_FILE_QUIT);
		this.fileMenu.add(this.quitMItem);

		this.editMenu = new JMenu(OmegaGUIConstants.MENU_EDIT);
		this.omegaDbOptionsMItem = new JMenuItem(
				OmegaGUIConstants.MENU_EDIT_DB_PREF);
		this.omegaDbUpdateTrajectoriesMItem = new JMenuItem(
				"Update Trajectories");
		this.editMenu.add(this.omegaDbOptionsMItem);
		this.editMenu.add(this.omegaDbUpdateTrajectoriesMItem);

		this.viewMenu = new JMenu(OmegaGUIConstants.MENU_VIEW);
		this.sepUnifyInterfaceMItem = new JMenuItem(
				OmegaGUIConstants.MENU_VIEW_SEPARATE);
		this.viewMenu.add(this.sepUnifyInterfaceMItem);

		final JMenu workspaceMenu = this.workspacePanel.getMenu();
		this.viewMenu.add(workspaceMenu);

		this.menu.add(this.fileMenu);
		// this.menu.add(workspaceMenu);
		this.menu.add(this.viewMenu);
		this.menu.add(this.editMenu);
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				OmegaGUIFrame.this.handleResize();
			}
		});
		this.mainSplitPane.addPropertyChangeListener(
				JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new PropertyChangeListener() {
					
					@Override
					public void propertyChange(final PropertyChangeEvent evt) {
						final JSplitPane source = (JSplitPane) evt.getSource();
						OmegaGUIFrame.this.handleSplitChange(source.getSize());
					}
				});
		// this.tracksImporterMItem.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(final ActionEvent e) {
		// OmegaGUIFrame.this.omegaApp.showTracksImporter();
		// }
		// });
		// this.tracksExporterMItem.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(final ActionEvent e) {
		// OmegaGUIFrame.this.omegaApp.showTracksExporter();
		// }
		// });
		// this.diffExporterMItem.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(final ActionEvent e) {
		// OmegaGUIFrame.this.omegaApp.showDiffusivityExporter();
		// }
		// });
		// this.dataExporterMItem.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(final ActionEvent e) {
		// OmegaGUIFrame.this.omegaApp.exportAllData();
		// }
		// });
		this.omegaDbLoadMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaGUIFrame.this.omegaApp.loadAnalysis();
			}
		});
		this.omegaDbLoadOrphanedMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaGUIFrame.this.omegaApp.loadOrphanedAnalysis();
			}
		});
		this.omegaDbSaveMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaGUIFrame.this.omegaApp.saveAnalysis();
			}
		});
		this.aboutMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaGUIFrame.this.handleAbout();
			}
		});
		this.quitMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaGUIFrame.this.handleQuit();
			}
		});
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				OmegaGUIFrame.this.handleQuit();
			}
		});

		this.sepUnifyInterfaceMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (OmegaGUIFrame.this.isAttached) {
					OmegaGUIFrame.this.detachWindows();
				} else {
					OmegaGUIFrame.this.attachWindows();
				}
			}
		});

		this.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(
						OmegaGUIConstants.EVENT_PROPERTY_PLUGIN)) {
					final OmegaPlugin plugin = OmegaGUIFrame.this.omegaApp
							.getPlugin((long) evt.getNewValue());
					OmegaGUIFrame.this.pluginSelected = (long) evt
							.getNewValue();
					OmegaGUIFrame.this.workspacePanel.showPlugin(plugin);
				}
			}
		});

		this.omegaDbOptionsMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaGUIFrame.this.showOmegaDBPreferencesPanel();
			}
		});
		this.omegaDbUpdateTrajectoriesMItem
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						OmegaGUIFrame.this.omegaApp.updateTrajectories();
					}
				});
	}

	private void handleResize() {
		this.mainSplitPane.setDividerLocation(this.dividerLocation);
	}

	private void handleSplitChange(final Dimension dimension) {
		if (!this.isAttached)
			return;
		boolean resize = true;
		if (this.oldSplitPaneDimension != null) {
			final boolean widthEqual = this.oldSplitPaneDimension.width == dimension.width;
			final boolean heightEqual = this.oldSplitPaneDimension.height == dimension.height;
			resize = !widthEqual || !heightEqual;
		}
		if (!resize) {
			final int loc = this.mainSplitPane.getDividerLocation();
			if (this.previousLoc != -1) {
				final int diff = Math.abs(loc - this.previousLoc);
				if (diff <= 15)
					return;
			}
			this.previousLoc = loc;
			final int width = this.mainSplitPane.getWidth();
			double tmp = (loc * 100) / width;
			tmp /= 100;
			this.dividerLocation = tmp;
		}
		// TODO applicare eventuali modifiche al side panel
		this.oldSplitPaneDimension = dimension;
	}

	public void showOmegaDBPreferencesPanel() {
		Point parentLocOnScren = null;
		Dimension parentSize = null;
		parentLocOnScren = this.getLocationOnScreen();
		parentSize = this.getSize();
		final int x = parentLocOnScren.x;
		final int y = parentLocOnScren.y;
		final int xOffset = (parentSize.width / 2)
				- (this.omegaDbPrefFrame.getSize().width / 2);
		final int yOffset = (parentSize.height / 2)
				- (this.omegaDbPrefFrame.getSize().height / 2);
		final Point dialogPos = new Point(x + xOffset, y + yOffset);
		this.omegaDbPrefFrame.setLocation(dialogPos);
		this.omegaDbPrefFrame.validate();
		this.omegaDbPrefFrame.repaint();
		this.omegaDbPrefFrame.setVisible(true);
	}

	protected void setSplitPanelDividerLocation(final Double percentage) {
		this.mainSplitPane.setDividerLocation(OmegaGUIFrame.SPLIT_PERCENT);
	}

	protected Dimension getDimensionDifference() {
		final Dimension windowDim = this.getSize();
		final Dimension spDim = this.sidePanel.getSize();
		final Dimension wpDim = this.workspacePanel.getSize();

		final Dimension diff = new Dimension();
		diff.width = windowDim.width - spDim.width - wpDim.width;
		diff.height = windowDim.height - spDim.height - wpDim.height;

		return diff;
	}

	protected Point getSideFramePosition() {
		final Point windowPos = this.getLocationOnScreen();
		final Dimension windowDim = this.getSize();
		final Dimension spDim = this.sidePanel.getSize();

		final Point workspaceFramePos = new Point();
		workspaceFramePos.x += windowPos.x + (windowDim.width - spDim.width);
		workspaceFramePos.y += windowPos.y + (windowDim.height - spDim.height)
				+ 9;
		return workspaceFramePos;
	}

	protected Point getWorkspaceFramePosition() {
		final Point windowPos = this.getLocationOnScreen();
		final Dimension windowDim = this.getSize();
		final Dimension wpDim = this.workspacePanel.getSize();

		final Point sideFramePos = new Point();
		sideFramePos.x += windowPos.x;
		sideFramePos.y += windowPos.y + (windowDim.height - wpDim.height) + 9;
		return sideFramePos;
	}

	protected Dimension getWorkspaceFrameDimension() {
		final Dimension wpDim = this.workspacePanel.getSize();

		wpDim.width += 28;

		return wpDim;
	}

	protected Dimension getDetachedNewDimension() {
		final Dimension windowDim = this.getSize();
		final Dimension wpDim = this.workspacePanel.getSize();

		windowDim.height -= wpDim.height + 28;

		return windowDim;
	}

	protected Dimension getAttachedNewDimension() {
		final Dimension windowDim = this.getSize();
		final Dimension wpDim = this.workspacePanel.getSize();

		windowDim.height += wpDim.height + 28;

		return windowDim;
	}

	public void detachWindows() {
		this.mainSplitPane.setLeftComponent(null);
		this.mainSplitPane.setRightComponent(null);
		// this.leftScrollPane.remove(this.workspacePanel);
		// this.rightScrollPane.remove(this.sidePanel);

		this.sepUnifyInterfaceMItem.setText(OmegaGUIConstants.MENU_VIEW_UNIFY);
		final GenericFrame sideFrame = new GenericFrame(this, this.sidePanel,
				"Sidebar", this.getSideFramePosition(),
				this.sidePanel.getSize());
		sideFrame.doNothingOnClose();
		this.separatedFrames.add(sideFrame);
		this.sidePanel.setAttached(false);
		this.sidePanel.updateParentContainer(sideFrame);

		final GenericFrame workspaceFrame = new GenericFrame(this,
				this.workspacePanel, "Workspace",
				this.getWorkspaceFramePosition(),
				this.getWorkspaceFrameDimension());
		workspaceFrame.doNothingOnClose();
		this.separatedFrames.add(workspaceFrame);
		this.workspacePanel.setAttached(false);
		this.workspacePanel.updateParentContainer(workspaceFrame);

		this.isAttached = false;
		this.setSize(this.getDetachedNewDimension());

		this.revalidate();
		this.repaint();
	}

	public void attachWindows() {
		this.mainSplitPane.setLeftComponent(this.workspacePanel);
		this.mainSplitPane.setRightComponent(this.sidePanel);

		this.sepUnifyInterfaceMItem
				.setText(OmegaGUIConstants.MENU_VIEW_SEPARATE);

		for (final JFrame frame : this.separatedFrames) {
			frame.getContentPane().removeAll();
			frame.dispose();
		}

		this.sidePanel.setAttached(true);
		this.sidePanel.updateParentContainer(this);

		this.workspacePanel.setAttached(true);
		this.workspacePanel.updateParentContainer(this);

		this.separatedFrames.clear();
		this.isAttached = true;
		this.setSize(this.getAttachedNewDimension());

		this.mainSplitPane.setDividerLocation(this.dividerLocation);
		this.revalidate();
		this.repaint();
	}

	public Map<String, String> getGeneralOptions(final String category) {
		return this.omegaApp.getGeneralOptions(category);
	}

	public void addGeneralOptions(final String category,
			final Map<String, String> options) {
		this.omegaApp.addGeneralOptions(category, options);
	}

	public boolean isAttached() {
		return this.isAttached;
	}

	public void setAttached(final boolean tof) {
		this.isAttached = tof;
	}

	public void handleAbout() {

	}

	public void handleQuit() {
		this.omegaDbPrefFrame.setVisible(false);
		this.omegaApp.saveOptions();
		this.omegaApp.dispose();
		this.dispose();
		System.exit(0);
	}

	public void updateGUI(final OmegaLoadedData loadedData,
			final OrphanedAnalysisContainer orphanedAnalysis,
			final List<OmegaAnalysisRun> loadedAnalysisRuns,
			final OmegaGateway gateway) {
		this.sidePanel.updateGUI(loadedData, orphanedAnalysis,
				loadedAnalysisRuns, gateway);
	}

	public OmegaDBServerInformation getOmegaDBServerInformation() {
		return this.omegaDbPrefFrame.getOmegaDBServerInformation();
	}

	public OmegaLoginCredentials getOmegaLoginCredentials() {
		return this.omegaDbPrefFrame.getOmegaDBLoginCredentials();
	}

	public void sendCoreEvent(final OmegaCoreEvent event) {
		this.omegaApp.handleCoreEvent(event);
	}
	
	public void clearSelections() {
		this.sidePanel.clearSelections();
	}

	public void updateSegments(
			final Map<OmegaTrajectory, List<OmegaSegment>> segments,
			final OmegaSegmentationTypes segmTypes, final boolean selection) {
		this.sidePanel.updateSegments(segments, segmTypes, selection);
	}

	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
			final boolean selection) {
		this.sidePanel.updateTrajectories(trajectories, selection);
	}

	public void updateParticles(final List<OmegaROI> particles) {
		this.sidePanel.updateParticles(particles);
	}

	public void selectImage(final OmegaAnalysisRunContainerInterface image) {
		this.sidePanel.selectImage(image);
	}

	public void selectParticleDetectionRun(
			final OmegaParticleDetectionRun analysisRun) {
		this.sidePanel.selectParticleDetectionRun(analysisRun);
	}

	public void selectParticleLinkingRun(
			final OmegaParticleLinkingRun analysisRun) {
		this.sidePanel.selectParticleLinkingRun(analysisRun);
	}

	public void selectTrajectoriesRelinkingRun(
			final OmegaTrajectoriesRelinkingRun analysisRun) {
		this.sidePanel.selectTrajectoriesRelinkingRun(analysisRun);
	}

	public void selectCurrentTrajectoriesRelinkingRun(
			final List<OmegaTrajectory> trajectories) {
		this.sidePanel.selectCurrentTrajectoriesRelinking(trajectories);
	}

	public void selectTrajectoriesSegmentationRun(
			final OmegaTrajectoriesSegmentationRun analysisRun) {
		this.sidePanel.selectTrajectoriesSegmentationRun(analysisRun);
	}

	public void selectCurrentTrajectoriesSegmentationRun(
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		this.sidePanel.selectCurrentTrajectoriesSegmentationRun(segmentsMap);
	}
}