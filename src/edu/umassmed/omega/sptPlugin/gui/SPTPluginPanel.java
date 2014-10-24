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
package edu.umassmed.omega.sptPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.eventSystem.OmegaMessageEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaParticleTrackingResultsEvent;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.gui.GenericStatusPanel;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanel;
import edu.umassmed.omega.commons.plugins.OmegaAlgorithmPlugin;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaFrame;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaROI;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.sptPlugin.runnable.SPTLoader;
import edu.umassmed.omega.sptPlugin.runnable.SPTMessageEvent;
import edu.umassmed.omega.sptPlugin.runnable.SPTRunnable;
import edu.umassmed.omega.sptPlugin.runnable.SPTRunner;
import edu.umassmed.omega.sptPlugin.runnable.SPTWriter;

public class SPTPluginPanel extends GenericPluginPanel implements
        OmegaMessageDisplayerPanel {

	private static final long serialVersionUID = -5740459087763362607L;

	private JSplitPane mainSplitPane, browserSplitPane;

	private JTabbedPane tabPanel;

	private SPTRunPanel runPanel;

	// private OmeroListPanel projectListPanel;
	private SPTLoadedDataBrowserPanel loadedDataBrowserPanel;
	private SPTQueueRunBrowserPanel queueRunBrowserPanel;

	private JButton addToProcess_butt, removeFromProcess_butt;
	private JButton processBatch_butt, processRealTime_butt;

	private GenericStatusPanel statusPanel;

	private OmegaGateway gateway;

	private OmegaImage selectedImage;
	private OmegaAnalysisRun selectedAnalysisRun;

	private final Map<OmegaImage, List<OmegaParameter>> imagesToProcess;

	private final Map<Thread, SPTRunner> threadsAndRunnables;

	private boolean isRunningBatch;

	public SPTPluginPanel(final RootPaneContainer parent,
	        final OmegaPlugin plugin, final OmegaGateway gateway,
	        final List<OmegaImage> images, final int index) {
		super(parent, plugin, index);

		this.threadsAndRunnables = new HashMap<Thread, SPTRunner>();

		this.imagesToProcess = new HashMap<OmegaImage, List<OmegaParameter>>();

		this.selectedImage = null;

		this.isRunningBatch = false;

		this.gateway = gateway;

		this.setPreferredSize(new Dimension(750, 500));
		this.setLayout(new BorderLayout());
		// this.createMenu();
		this.createAndAddWidgets();
		this.loadedDataBrowserPanel.updateTree(images);
		this.addListeners();

		this.resetStatusMessages();
	}

	private void createMenu() {

	}

	public void createAndAddWidgets() {
		this.loadedDataBrowserPanel = new SPTLoadedDataBrowserPanel(
		        this.getParentContainer(), this);

		this.queueRunBrowserPanel = new SPTQueueRunBrowserPanel(
		        this.getParentContainer(), this);

		final JPanel browserPanel = new JPanel();
		browserPanel.setLayout(new BorderLayout());

		this.browserSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		this.browserSplitPane.setLeftComponent(this.loadedDataBrowserPanel);
		this.browserSplitPane.setRightComponent(this.queueRunBrowserPanel);

		browserPanel.add(this.browserSplitPane, BorderLayout.CENTER);

		final JPanel browserButtonPanel = new JPanel();
		browserButtonPanel.setLayout(new FlowLayout());

		final ImageIcon addIcon = new ImageIcon(
		        OmegaConstants.OMEGA_IMGS_FOLDER + File.separatorChar
		                + "green_plus.png");
		this.addToProcess_butt = new JButton(addIcon);
		this.addToProcess_butt.setPreferredSize(new Dimension(30, 30));

		final ImageIcon removeIcon = new ImageIcon(
		        OmegaConstants.OMEGA_IMGS_FOLDER + File.separatorChar
		                + "red_minus.png");
		this.removeFromProcess_butt = new JButton(removeIcon);
		this.removeFromProcess_butt.setPreferredSize(new Dimension(30, 30));

		this.setAddAndRemoveButtonsEnabled(false);

		browserButtonPanel.add(this.addToProcess_butt);
		browserButtonPanel.add(this.removeFromProcess_butt);

		browserPanel.add(browserButtonPanel, BorderLayout.SOUTH);

		this.tabPanel = new JTabbedPane(SwingConstants.TOP,
		        JTabbedPane.WRAP_TAB_LAYOUT);

		// TODO create panel for parameters
		this.runPanel = new SPTRunPanel(this.getParentContainer(), this.gateway);
		final JScrollPane scrollPaneRun = new JScrollPane(this.runPanel);
		scrollPaneRun
		        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneRun
		        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		this.tabPanel.add("Algorithm Run", scrollPaneRun);

		this.mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.mainSplitPane.setLeftComponent(browserPanel);
		this.mainSplitPane.setRightComponent(this.tabPanel);

		this.add(this.mainSplitPane, BorderLayout.CENTER);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		this.statusPanel = new GenericStatusPanel(4);

		final JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());

		this.processRealTime_butt = new JButton("Process in real time");
		this.processRealTime_butt.setEnabled(false);
		buttonsPanel.add(this.processRealTime_butt);

		this.processBatch_butt = new JButton("Process in background");
		buttonsPanel.add(this.processBatch_butt);

		this.setProcessButtonsEnabled(false);

		bottomPanel.add(buttonsPanel, BorderLayout.NORTH);
		bottomPanel.add(this.statusPanel, BorderLayout.SOUTH);

		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void resetStatusMessages() {
		this.statusPanel.updateStatus(0, "Plugin ready");
		this.statusPanel.updateStatus(1, "Runner service: ready");
		this.statusPanel.updateStatus(1, "Loader service: ready");
		this.statusPanel.updateStatus(1, "Writer service: ready");
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				SPTPluginPanel.this.handleResize();
			}
		});
		this.addToProcess_butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SPTPluginPanel.this.addToProcessList();
			}
		});
		this.removeFromProcess_butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SPTPluginPanel.this.removeFromProcessList();
			}
		});
		this.processBatch_butt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				SPTPluginPanel.this.processBatch();
			}
		});
	}

	private void handleResize() {
		this.browserSplitPane.setDividerLocation(0.5);
		this.mainSplitPane.setDividerLocation(0.25);
	}

	private void addToProcessList() {
		this.updateImagesToProcess(0);
		this.loadedDataBrowserPanel.deselect();
		this.setAddAndRemoveButtonsEnabled(false);
	}

	private void removeFromProcessList() {
		this.updateImagesToProcess(1);
		this.loadedDataBrowserPanel.deselect();
		this.queueRunBrowserPanel.deselect();
		this.setAddAndRemoveButtonsEnabled(false);
	}

	private void processBatch() {
		this.isRunningBatch = true;
		this.setAddAndRemoveButtonsEnabled(false);
		this.setProcessButtonsEnabled(false);
		this.runPanel.setFieldsEnalbed(false);
		final SPTRunner sptRunner = new SPTRunner(this, this.imagesToProcess,
		        this.gateway);
		final Thread runnerT = new Thread(sptRunner);
		this.threadsAndRunnables.put(runnerT, sptRunner);

		runnerT.start();
	}

	private void updateRunnerEnded() {
		final List<Thread> toRemove = new ArrayList<Thread>();
		for (final Thread t : this.threadsAndRunnables.keySet()) {
			final SPTRunner runner = this.threadsAndRunnables.get(t);
			if (runner.isJobCompleted()) {
				final Map<OmegaImage, List<OmegaParameter>> processedImages = runner
				        .getImageParameters();
				final Map<OmegaImage, Map<OmegaFrame, List<OmegaROI>>> resultingParticles = runner
				        .getImageResultingParticles();
				final Map<OmegaImage, List<OmegaTrajectory>> resultingTrajectories = runner
				        .getImageResultingTrajectories();

				for (final OmegaImage image : processedImages.keySet()) {
					final List<OmegaParameter> params = processedImages
					        .get(image);

					final Map<OmegaFrame, List<OmegaROI>> particles = resultingParticles
					        .get(image);
					final List<OmegaTrajectory> trajectories = resultingTrajectories
					        .get(image);
					final OmegaParticleTrackingResultsEvent particleTrackingEvt = new OmegaParticleTrackingResultsEvent(
					        this.getPlugin(), image, params, particles,
					        trajectories);

					this.imagesToProcess.remove(image);
					this.queueRunBrowserPanel.updateTree(this.imagesToProcess);

					this.getPlugin().fireEvent(particleTrackingEvt);
				}
				if (t.isAlive()) {
					try {
						t.join();
					} catch (final InterruptedException e) {
						// TODO gestire
						e.printStackTrace();
					}
					toRemove.add(t);
				}
			}
			this.setEnabled(true);
		}

		for (final Thread t : toRemove) {
			this.threadsAndRunnables.remove(t);
		}

		this.setProcessButtonsEnabled(true);
		this.runPanel.setFieldsEnalbed(true);
		this.resetStatusMessages();
	}

	private void updateImagesToProcess(final int action) {
		switch (action) {
		case 1:
			this.imagesToProcess.remove(this.selectedImage);
			break;
		default:
			final List<OmegaParameter> params = this.runPanel
			        .getSPTParameters();
			if (params == null) {
				final String[] errors = this.runPanel.getParametersError();
				final StringBuffer exceptionError = new StringBuffer();
				for (int index = 0; index < errors.length; index++) {
					final String error = errors[index];
					if (error == null) {
						continue;
					}
					exceptionError.append(error);
					if (index != (errors.length - 1)) {
						exceptionError.append(" & ");
					}
				}
				final StringBuffer buf = new StringBuffer();
				buf.append("Wrong parameters type -> ");
				buf.append(exceptionError.toString());
				this.statusPanel.updateStatus(3, buf.toString());
				break;
				// Lanciare eccezione ho printare errore a schermo
				// throw new OmegaAlgorithmParametersTypeException(
				// this.getPlugin(), exceptionError.toString());
			}
			this.imagesToProcess.put(this.selectedImage, params);
			break;
		}
		this.queueRunBrowserPanel.updateTree(this.imagesToProcess);
		if (this.imagesToProcess.size() == 0) {
			this.setProcessButtonsEnabled(false);
		} else {
			this.setProcessButtonsEnabled(true);
		}
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.loadedDataBrowserPanel.updateParentContainer(parent);
		// this.projectListPanel.updateParentContainer(parent);
	}

	@Override
	public void onCloseOperation() {

	}

	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
		this.runPanel.setGateway(gateway);
	}

	public void updateSelectedAnalysisRun(final OmegaAnalysisRun analysisRun) {
		// TODO capire se serve
		this.selectedAnalysisRun = analysisRun;
		if (analysisRun != null) {
			this.runPanel.updateRunFields(analysisRun.getAlgorithmSpec()
			        .getParameters());
		}
	}

	public void updateSelectedImage(final OmegaImage image) {
		this.selectedImage = image;
		this.setAddAndRemoveButtonsEnabled(false);
		if (this.isRunningBatch)
			return;
		if (image != null) {
			this.runPanel.updateImageFields(image);
			if (this.imagesToProcess.containsKey(this.selectedImage)) {
				this.removeFromProcess_butt.setEnabled(true);
				this.runPanel.updateRunFields(this.imagesToProcess
				        .get(this.selectedImage));
			} else {
				this.addToProcess_butt.setEnabled(true);
				this.runPanel.updateRunFieldsDefault();
			}
		}
	}

	private void setAddAndRemoveButtonsEnabled(final boolean enabled) {
		this.addToProcess_butt.setEnabled(enabled);
		this.removeFromProcess_butt.setEnabled(enabled);
	}

	private void setProcessButtonsEnabled(final boolean enabled) {
		// this.processRealTime_butt.setEnabled(enabled);
		this.processBatch_butt.setEnabled(enabled);
	}

	public void updateTrees(final List<OmegaImage> images) {
		this.loadedDataBrowserPanel.updateTree(images);
		this.queueRunBrowserPanel.updateTree(null);
	}

	public boolean checkIfThisAlgorithm(
	        final OmegaParticleDetectionRun particleDetectionRun) {
		final OmegaAlgorithmPlugin plugin = (OmegaAlgorithmPlugin) this
		        .getPlugin();
		return plugin.checkIfThisAlgorithm(particleDetectionRun);

	}

	@Override
	public void updateMessageStatus(final OmegaMessageEvent evt) {
		final SPTMessageEvent specificEvent = (SPTMessageEvent) evt;
		final SPTRunnable source = specificEvent.getSource();
		if (source instanceof SPTRunner) {
			this.statusPanel.updateStatus(1, evt.getMessage());
			if (((SPTMessageEvent) evt).isEnded()) {
				this.updateRunnerEnded();
			}
		} else if (source instanceof SPTLoader) {
			this.statusPanel.updateStatus(2, evt.getMessage());
		} else if (source instanceof SPTWriter) {
			this.statusPanel.updateStatus(3, evt.getMessage());
		}
	}
}
