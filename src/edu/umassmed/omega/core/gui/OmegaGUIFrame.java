/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team: 
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli, 
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban, 
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import edu.umassmed.omega.commons.eventSystem.OmegaApplicationEvent;
import edu.umassmed.omega.commons.genericPlugins.OmegaPlugin;
import edu.umassmed.omega.commons.gui.GenericFrame;
import edu.umassmed.omega.core.OmegaApplication;
import edu.umassmed.omega.dataNew.OmegaLoadedData;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaDBServerInformation;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaLoginCredentials;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaGUIFrame extends JFrame {

	private static final long serialVersionUID = -4775204088456661307L;

	public static double SPLIT_PERCENT = 0.75;
	public static int SPLITPANEL_LEFT = 0;
	public static int SPLITPANEL_RIGHT = 1;
	public static String PROP_PLUGIN = "PluginSelected";
	public static String PROP_TOGGLEWINDOW = "ToggleWindow";

	private final OmegaApplication omegaApp;

	private final List<JFrame> separatedFrames;

	private OmegaTopPanel topPanel;
	private OmegaWorkspacePanel workspacePanel;
	private OmegaSidePanel sidePanel;

	private final OmegaDBPreferencesFrame omegaDbPrefFrame;

	private JMenuBar menu;
	private JMenu fileMenu, windowsMenu, omegaDbMenu;
	private JMenuItem quitMItem;
	private JMenuItem attachEdetachAllWindows;
	private JMenuItem omegaDbOptionsMItem, omegaDbSaveMItem, omegaDbLoadMItem;

	private JSplitPane mainSplitPane;

	private JScrollPane leftScrollPane, rightScrollPane;

	private long pluginSelected;
	private boolean isAttached;

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

		this.fileMenu = new JMenu("File");
		this.quitMItem = new JMenuItem("Quit");
		this.fileMenu.add(this.quitMItem);

		this.windowsMenu = new JMenu("Window");
		this.attachEdetachAllWindows = new JMenuItem("Detach all windows");
		this.windowsMenu.add(this.attachEdetachAllWindows);

		this.omegaDbMenu = new JMenu("Omega DB Options");
		this.omegaDbOptionsMItem = new JMenuItem("Preferences");
		this.omegaDbLoadMItem = new JMenuItem("Load analysis");
		this.omegaDbSaveMItem = new JMenuItem("Save analysis");
		this.omegaDbMenu.add(this.omegaDbOptionsMItem);
		this.omegaDbMenu.add(this.omegaDbLoadMItem);
		this.omegaDbMenu.add(this.omegaDbSaveMItem);

		final JMenu workspaceMenu = this.workspacePanel.getMenu();

		this.menu.add(this.fileMenu);
		this.menu.add(workspaceMenu);
		this.menu.add(this.windowsMenu);
		this.menu.add(this.omegaDbMenu);
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent evt) {
				OmegaGUIFrame.this.mainSplitPane
				        .setDividerLocation(OmegaGUIFrame.SPLIT_PERCENT);
			}
		});
		this.quitMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaGUIFrame.this.quit();
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				OmegaGUIFrame.this.quit();
			}
		});

		this.attachEdetachAllWindows.addActionListener(new ActionListener() {
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
				if (evt.getPropertyName().equals(OmegaGUIFrame.PROP_PLUGIN)) {
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
		this.omegaDbLoadMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaGUIFrame.this.omegaApp.loadAnalysis();
			}
		});
		this.omegaDbSaveMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaGUIFrame.this.omegaApp.saveAnalysis();
			}
		});
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

		this.attachEdetachAllWindows.setText("Attach all windows");
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

		this.validate();
		this.repaint();
	}

	public void attachWindows() {
		this.mainSplitPane.setLeftComponent(this.workspacePanel);
		this.mainSplitPane.setRightComponent(this.sidePanel);

		this.attachEdetachAllWindows.setText("Detach all windows");

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

		this.validate();
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

	public void quit() {
		this.omegaDbPrefFrame.setVisible(false);
		this.omegaApp.saveOptions();
		this.dispose();
		System.exit(0);
	}

	public void updateGUI(final OmegaLoadedData loadedData,
	        final List<OmegaAnalysisRun> loadedAnalysisRuns,
	        final OmegaGateway gateway) {
		this.sidePanel.updateGUI(loadedData, loadedAnalysisRuns, gateway);
	}

	public OmegaDBServerInformation getOmegaDBServerInformation() {
		return this.omegaDbPrefFrame.getOmegaDBServerInformation();
	}

	public OmegaLoginCredentials getOmegaLoginCredentials() {
		return this.omegaDbPrefFrame.getOmegaDBLoginCredentials();
	}

	public void sendApplicationEvent(final OmegaApplicationEvent event) {
		this.omegaApp.handleOmegaApplicationEvent(event);
	}

	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
	        final boolean selection) {
		this.sidePanel.updateTrajectories(trajectories, selection);
	}

	public void selectImage(final OmegaImage image) {
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
}