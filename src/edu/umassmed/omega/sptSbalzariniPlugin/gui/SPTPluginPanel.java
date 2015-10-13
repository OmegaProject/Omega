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
package edu.umassmed.omega.sptSbalzariniPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
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
import javax.swing.SwingConstants;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.coreElements.OmegaFrame;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsParticleTracking;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionImage;
import edu.umassmed.omega.commons.exceptions.OmegaPluginExceptionStatusPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.gui.GenericStatusPanel;
import edu.umassmed.omega.commons.gui.GenericTrackingResultsPanel;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.commons.plugins.OmegaAlgorithmPlugin;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.sptSbalzariniPlugin.SPTConstants;
import edu.umassmed.omega.sptSbalzariniPlugin.runnable.SPTLoader;
import edu.umassmed.omega.sptSbalzariniPlugin.runnable.SPTMessageEvent;
import edu.umassmed.omega.sptSbalzariniPlugin.runnable.SPTRunnable;
import edu.umassmed.omega.sptSbalzariniPlugin.runnable.SPTRunner;
import edu.umassmed.omega.sptSbalzariniPlugin.runnable.SPTWriter;

public class SPTPluginPanel extends GenericPluginPanel implements
OmegaMessageDisplayerPanelInterface {

	private static final long serialVersionUID = -5740459087763362607L;

	private JSplitPane mainSplitPane, browserSplitPane;

	private JTabbedPane tabPanel;

	private SPTRunPanel runPanel;
	private GenericTrackingResultsPanel resDetectionPanel, resLinkingPanel;

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

	private Thread sptThread;
	private SPTRunner sptRunner;

	private boolean isRunningBatch;

	public SPTPluginPanel(final RootPaneContainer parent,
			final OmegaPlugin plugin, final OmegaGateway gateway,
			final List<OmegaImage> images, final int index) {
		super(parent, plugin, index);

		this.sptThread = null;
		this.sptRunner = null;

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
		this.tabPanel.add(SPTConstants.RUN_DEFINITION, scrollPaneRun);

		this.resDetectionPanel = new GenericTrackingResultsPanel(
		        this.getParentContainer());
		this.tabPanel.add("Detection results", this.resDetectionPanel);

		this.resLinkingPanel = new GenericTrackingResultsPanel(
		        this.getParentContainer());
		this.tabPanel.add("Linking results", this.resLinkingPanel);

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
		// buttonsPanel.add(this.processRealTime_butt);

		this.processBatch_butt = new JButton(SPTConstants.EXECUTE_BUTTON);
		buttonsPanel.add(this.processBatch_butt);

		this.setProcessButtonsEnabled(false);

		bottomPanel.add(buttonsPanel, BorderLayout.NORTH);
		bottomPanel.add(this.statusPanel, BorderLayout.SOUTH);

		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void resetStatusMessages() {
		try {
			this.statusPanel.updateStatus(0, "Plugin ready");
			this.statusPanel.updateStatus(1, "Runner service: ready");
			this.statusPanel.updateStatus(1, "Loader service: ready");
			this.statusPanel.updateStatus(1, "Writer service: ready");
		} catch (final OmegaPluginExceptionStatusPanel ex) {
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex);
		}
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
		this.sptRunner = new SPTRunner(this, this.imagesToProcess, this.gateway);
		this.sptThread = new Thread(this.sptRunner);
		this.sptThread.setName(this.sptRunner.getClass().getSimpleName());
		OmegaLogFileManager.registerAsExceptionHandlerOnThread(this.sptThread);
		this.sptThread.start();
	}

	private void updateRunnerEnded() {
		if (this.sptRunner.isJobCompleted()) {
			final Map<OmegaImage, List<OmegaParameter>> processedImages = this.sptRunner
					.getImageParameters();
			final Map<OmegaImage, Map<OmegaFrame, List<OmegaROI>>> resultingParticles = this.sptRunner
					.getImageResultingParticles();
			final Map<OmegaImage, List<OmegaTrajectory>> resultingTrajectories = this.sptRunner
					.getImageResultingTrajectories();
			final Map<OmegaImage, Map<OmegaROI, Map<String, Object>>> resultingParticlesValues = this.sptRunner
			        .getImageResultingParticlesValues();

			for (final OmegaImage image : processedImages.keySet()) {
				final List<OmegaParameter> params = processedImages.get(image);

				final Map<OmegaFrame, List<OmegaROI>> particles = resultingParticles
						.get(image);
				final List<OmegaTrajectory> trajectories = resultingTrajectories
						.get(image);
				final Map<OmegaROI, Map<String, Object>> particlesValues = resultingParticlesValues
						.get(image);
				// TODO additional value to complete
				final OmegaPluginEventResultsParticleTracking particleTrackingEvt = new OmegaPluginEventResultsParticleTracking(
						this.getPlugin(), image, params, particles,
						trajectories, particlesValues);

				this.imagesToProcess.remove(image);
				this.queueRunBrowserPanel.updateTree(this.imagesToProcess);

				this.getPlugin().fireEvent(particleTrackingEvt);
			}
			if (this.sptThread.isAlive()) {
				try {
					this.sptThread.join();
				} catch (final InterruptedException e) {
					// TODO gestire
					e.printStackTrace();
				}
			}
		}
		this.setEnabled(true);

		this.setProcessButtonsEnabled(true);
		this.runPanel.setFieldsEnalbed(true);
		this.resetStatusMessages();
		this.isRunningBatch = false;
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
				try {
					this.statusPanel.updateStatus(3, buf.toString());
				} catch (final OmegaPluginExceptionStatusPanel ex) {
					OmegaLogFileManager.handlePluginException(this.getPlugin(),
							ex);
				}
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
		this.queueRunBrowserPanel.updateParentContainer(parent);
		this.runPanel.updateParentContainer(parent);
		// this.projectListPanel.updateParentContainer(parent);
	}

	@Override
	public void onCloseOperation() {

	}

	private void fireEventSelectionImage() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionImage(
		        this.getPlugin(), this.selectedImage);
		this.getPlugin().fireEvent(event);
	}

	private void fireEventSelectionParticleTrackingRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(), this.selectedAnalysisRun);
		this.getPlugin().fireEvent(event);
	}

	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
		this.runPanel.setGateway(gateway);
	}

	public void updateSelectedAnalysisRun(final OmegaAnalysisRun analysisRun) {
		// TODO capire se serve
		this.resDetectionPanel.setAnalysisRun(null);
		this.resLinkingPanel.setAnalysisRun(null);
		this.selectedAnalysisRun = analysisRun;
		this.fireEventSelectionParticleTrackingRun();
		this.setAddAndRemoveButtonsEnabled(false);
		if (this.isRunningBatch)
			return;
		if (analysisRun != null) {
			this.runPanel.updateRunFields(analysisRun.getAlgorithmSpec()
			        .getParameters());
			this.resDetectionPanel.setAnalysisRun(this.selectedAnalysisRun);
			for (final OmegaAnalysisRun linkingRun : this.selectedAnalysisRun
			        .getAnalysisRuns()) {
				if (!this.checkIfThisAlgorithm(linkingRun)
				        || !this.selectedAnalysisRun.getTimeStamps().equals(
				                linkingRun.getTimeStamps())) {
					continue;
				}
				this.resLinkingPanel.setAnalysisRun(linkingRun);
				break;
			}
		}
	}

	public void updateSelectedImage(final OmegaImage image) {
		this.resDetectionPanel.setAnalysisRun(null);
		this.resLinkingPanel.setAnalysisRun(null);
		this.selectedImage = image;
		this.fireEventSelectionImage();
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

	public boolean checkIfThisAlgorithm(final OmegaAnalysisRun analysisRun) {
		final OmegaAlgorithmPlugin plugin = (OmegaAlgorithmPlugin) this
				.getPlugin();
		return plugin.checkIfThisAlgorithm(analysisRun);

	}

	@Override
	public void updateMessageStatus(final OmegaMessageEvent evt) {
		final SPTMessageEvent specificEvent = (SPTMessageEvent) evt;
		final SPTRunnable source = specificEvent.getSource();
		if (source instanceof SPTRunner) {
			this.updateSPTRunnerMessageStatus(evt);
		} else if (source instanceof SPTLoader) {
			this.updateSPTLoaderMessageStatus(evt);
		} else if (source instanceof SPTWriter) {
			this.updateSPTWriterMessageStatus(evt);
		}
	}

	private void updateSPTWriterMessageStatus(final OmegaMessageEvent evt) {
		try {
			this.statusPanel.updateStatus(3, evt.getMessage());
		} catch (final OmegaPluginExceptionStatusPanel ex) {
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex);
		}
	}

	private void updateSPTLoaderMessageStatus(final OmegaMessageEvent evt) {
		try {
			this.statusPanel.updateStatus(2, evt.getMessage());
		} catch (final OmegaPluginExceptionStatusPanel ex) {
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex);
		}
	}

	private void updateSPTRunnerMessageStatus(final OmegaMessageEvent evt) {
		try {
			this.statusPanel.updateStatus(1, evt.getMessage());
		} catch (final OmegaPluginExceptionStatusPanel ex) {
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex);
		}
		if (((SPTMessageEvent) evt).isEnded()) {
			this.updateRunnerEnded();
		}
	}
}
