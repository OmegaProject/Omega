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
package edu.umassmed.omega.snrSbalzariniPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsSNR;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionImage;
import edu.umassmed.omega.commons.exceptions.OmegaPluginExceptionStatusPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.gui.GenericStatusPanel;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.commons.plugins.OmegaAlgorithmPlugin;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.core.OmegaLogFileManager;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaSNRRun;
import edu.umassmed.omega.data.coreElements.OmegaFrame;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.snrSbalzariniPlugin.runnable.SNRMessageEvent;
import edu.umassmed.omega.snrSbalzariniPlugin.runnable.SNRRunner;

public class SNRPluginPanel extends GenericPluginPanel implements
        OmegaMessageDisplayerPanelInterface {

	private static final long serialVersionUID = -5740459087763362607L;

	private final OmegaGateway gateway;

	private JComboBox<String> images_cmb;

	// private OmeroListPanel projectListPanel;
	private SNRLoadedDataBrowserPanel loadedDataBrowserPanel;
	private SNRQueueRunBrowserPanel queueRunBrowserPanel;

	private JButton addToProcess_butt, removeFromProcess_butt;
	private JButton processBatch_butt, processRealTime_butt;

	private boolean popImages;

	private final boolean popParticles;

	private boolean isHandlingEvent;

	private JSplitPane mainSplitPane, browserSplitPane;
	private JTabbedPane tabbedPane;
	private GenericStatusPanel statusPanel;
	private SNRRunPanel runPanel;

	private List<OmegaImage> images;

	private OmegaImage selectedImage;
	private OmegaParticleDetectionRun selectedParticleDetectionRun;
	private OmegaSNRRun selectedSNRRun;

	private List<OmegaAnalysisRun> loadedAnalysisRuns;

	private final Map<OmegaParticleDetectionRun, List<OmegaParameter>> particlesToProcess;

	private Thread snrThread;
	private SNRRunner snrRunner;

	private boolean isRunningBatch;

	public SNRPluginPanel(final RootPaneContainer parent,
	        final OmegaPlugin plugin, final OmegaGateway gateway,
	        final List<OmegaImage> images,
	        final List<OmegaAnalysisRun> analysisRuns, final int index) {
		super(parent, plugin, index);

		this.gateway = gateway;

		this.particlesToProcess = new LinkedHashMap<>();

		this.images = images;
		this.loadedAnalysisRuns = analysisRuns;

		this.popImages = false;
		this.popParticles = false;
		this.isHandlingEvent = false;

		this.selectedImage = null;
		this.selectedParticleDetectionRun = null;

		this.setPreferredSize(new Dimension(750, 500));
		this.setLayout(new BorderLayout());
		// this.createMenu();
		this.createAndAddWidgets();
		// this.loadedDataBrowserPanel.updateTree(images);
		this.createMenu();

		this.addListeners();

		this.populateImagesCombo();
	}

	private void createMenu() {

	}

	private void createAndAddWidgets() {
		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(4, 1));
		this.images_cmb = new JComboBox<String>();
		this.images_cmb.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.images_cmb.setEnabled(false);
		topPanel.add(this.images_cmb);

		this.loadedDataBrowserPanel = new SNRLoadedDataBrowserPanel(
		        this.getParentContainer(), this);

		this.queueRunBrowserPanel = new SNRQueueRunBrowserPanel(
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

		this.tabbedPane = new JTabbedPane(SwingConstants.TOP,
		        JTabbedPane.WRAP_TAB_LAYOUT);

		// TODO create panel for parameters
		this.runPanel = new SNRRunPanel(this.getParentContainer(), this.gateway);
		final JScrollPane scrollPaneRun = new JScrollPane(this.runPanel);
		scrollPaneRun
		        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneRun
		        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		this.tabbedPane.add("Algorithm Run", scrollPaneRun);

		this.mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.mainSplitPane.setLeftComponent(browserPanel);
		this.mainSplitPane.setRightComponent(this.tabbedPane);

		this.add(this.mainSplitPane, BorderLayout.CENTER);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		this.statusPanel = new GenericStatusPanel(2);

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

	private void addListeners() {
		this.tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent evt) {

			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				SNRPluginPanel.this.handleResize();
			}
		});
		this.images_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SNRPluginPanel.this.selectImage();
			}
		});
		this.addToProcess_butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SNRPluginPanel.this.addToProcessList();
			}
		});
		this.removeFromProcess_butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SNRPluginPanel.this.removeFromProcessList();
			}
		});
		this.processBatch_butt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				SNRPluginPanel.this.processBatch();
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
		this.snrRunner = new SNRRunner(this, this.particlesToProcess,
		        this.gateway);
		this.snrThread = new Thread(this.snrRunner);
		this.snrThread.setName(this.snrRunner.getClass().getSimpleName());
		OmegaLogFileManager.registerAsExceptionHandlerOnThread(this.snrThread);
		this.snrThread.start();
	}

	private void updateImagesToProcess(final int action) {
		switch (action) {
		case 1:
			this.particlesToProcess.remove(this.selectedParticleDetectionRun);
			break;
		default:
			final List<OmegaParameter> params = this.runPanel.getParameters();
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
					this.statusPanel.updateStatus(1, buf.toString());
				} catch (final OmegaPluginExceptionStatusPanel ex) {
					OmegaLogFileManager.handlePluginException(this.getPlugin(),
					        ex);
				}
				break;
				// TODO Lanciare eccezione o printare errore a schermo
				// throw new OmegaAlgorithmParametersTypeException(
				// this.getPlugin(), exceptionError.toString());
			}
			this.particlesToProcess.put(this.selectedParticleDetectionRun,
			        params);
			break;
		}
		this.queueRunBrowserPanel.updateTree(this.particlesToProcess);
		if (this.particlesToProcess.size() == 0) {
			this.setProcessButtonsEnabled(false);
		} else {
			this.setProcessButtonsEnabled(true);
		}
	}

	private void updateRunnerEnded() {
		if (this.snrRunner.isJobCompleted()) {
			final Map<OmegaParticleDetectionRun, List<OmegaParameter>> processedParticles = this.snrRunner
			        .getParticleToProcess();

			final Map<OmegaParticleDetectionRun, Map<OmegaFrame, Double>> imageNoises = this.snrRunner
			        .getResultingImageNoise();
			final Map<OmegaParticleDetectionRun, Map<OmegaFrame, Double>> imageBackgrounds = this.snrRunner
			        .getResultingImageBackground();
			final Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>> localCentralSignals = this.snrRunner
			        .getResultingLocalCentralSignals();
			final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> localMeanSignals = this.snrRunner
			        .getResultingLocalMeanSignals();
			final Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>> localSignalSizes = this.snrRunner
			        .getResultingLocalSignalSizes();
			final Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>> localPeakSignals = this.snrRunner
			        .getResultingLocalPeakSignals();
			final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> localNoises = this.snrRunner
			        .getResultingLocalNoises();
			final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> localSNRs = this.snrRunner
			        .getResultingLocalSNRs();

			for (final OmegaParticleDetectionRun spotDetectionRun : processedParticles
			        .keySet()) {
				final List<OmegaParameter> params = processedParticles
				        .get(spotDetectionRun);

				final Map<OmegaFrame, Double> noises = imageNoises
				        .get(spotDetectionRun);
				final Map<OmegaFrame, Double> backgrounds = imageBackgrounds
				        .get(spotDetectionRun);
				final Map<OmegaROI, Integer> lCentralSignals = localCentralSignals
				        .get(spotDetectionRun);
				final Map<OmegaROI, Double> lMeanSignals = localMeanSignals
				        .get(spotDetectionRun);
				final Map<OmegaROI, Integer> lSignalSizes = localSignalSizes
				        .get(spotDetectionRun);
				final Map<OmegaROI, Integer> lPeakSignals = localPeakSignals
				        .get(spotDetectionRun);
				final Map<OmegaROI, Double> lNoises = localNoises
				        .get(spotDetectionRun);
				final Map<OmegaROI, Double> lSNRs = localSNRs
				        .get(spotDetectionRun);

				final OmegaPluginEventResultsSNR snrResultsEvt = new OmegaPluginEventResultsSNR(
				        this.getPlugin(), spotDetectionRun, params, noises,
				        backgrounds, lCentralSignals, lMeanSignals,
				        lSignalSizes, lPeakSignals, lNoises, lSNRs);

				this.particlesToProcess.remove(spotDetectionRun);
				this.queueRunBrowserPanel.updateTree(this.particlesToProcess);

				this.getPlugin().fireEvent(snrResultsEvt);
			}
			if (this.snrThread.isAlive()) {
				try {
					this.snrThread.join();
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

	private void resetStatusMessages() {
		try {
			this.statusPanel.updateStatus(0, "Plugin ready");
		} catch (final OmegaPluginExceptionStatusPanel ex) {
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex);
		}
	}

	private void selectImage() {
		if (this.popImages)
			return;
		final int index = this.images_cmb.getSelectedIndex();
		this.selectedImage = null;
		if (index == -1) {
			this.loadedDataBrowserPanel.updateTree(null);
			this.queueRunBrowserPanel.updateTree(null);
			return;
		}
		this.selectedImage = this.images.get(index);
		if (!this.isHandlingEvent) {
			this.fireEventSelectionImage();
		}
		this.runPanel.updateImageFields(this.selectedImage);

		final List<OmegaAnalysisRun> analysisRuns = new ArrayList<>();
		for (final OmegaAnalysisRun analysisRun : this.selectedImage
		        .getAnalysisRuns()) {
			if (!(analysisRun instanceof OmegaParticleDetectionRun)) {
				continue;
			}
			if (this.loadedAnalysisRuns.contains(analysisRun)) {
				analysisRuns.add(analysisRun);
			}
		}

		this.loadedDataBrowserPanel.updateTree(analysisRuns);
		this.queueRunBrowserPanel.updateTree(null);
		// this.populateTrajectoriesCombo();
		// this.trPanel.setPixelSizes(this.selectedImage.getDefaultPixels()
		// .getPixelSizeX(), this.selectedImage.getDefaultPixels()
		// .getPixelSizeY());
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
	}

	@Override
	public void onCloseOperation() {

	}

	public void updateCombos(final List<OmegaImage> images,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this.isHandlingEvent = true;
		this.images = images;
		this.loadedAnalysisRuns = analysisRuns;

		this.populateImagesCombo();
		this.isHandlingEvent = false;
	}

	private void populateImagesCombo() {
		this.popImages = true;
		this.images_cmb.removeAllItems();
		this.selectedImage = null;

		if ((this.images == null) || this.images.isEmpty()) {
			this.images_cmb.setEnabled(false);
			this.loadedDataBrowserPanel.updateTree(null);
			this.queueRunBrowserPanel.updateTree(null);
			this.repaint();
			return;
		}

		this.images_cmb.setEnabled(true);

		for (final OmegaImage image : this.images) {
			this.images_cmb.addItem(image.getName());
		}
		this.popImages = false;

		if (this.images_cmb.getItemCount() > 0) {
			this.images_cmb.setSelectedIndex(0);
		} else {
			this.images_cmb.setSelectedIndex(-1);
		}
	}

	private void fireEventSelectionImage() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionImage(
		        this.getPlugin(), this.selectedImage);
		this.getPlugin().fireEvent(event);
	}

	private void fireEventSelectionParticleDetectionRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(), this.selectedParticleDetectionRun);
		this.getPlugin().fireEvent(event);
	}

	private void fireEventSelectionSNRRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(), this.selectedSNRRun);
		this.getPlugin().fireEvent(event);
	}

	public void selectImage(final OmegaImage image) {
		this.isHandlingEvent = true;
		final int index = this.images.indexOf(image);
		this.images_cmb.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void selectParticleDetectionRun(
	        final OmegaParticleDetectionRun analysisRun) {
		this.isHandlingEvent = true;
		// TODO select particle detection run in list
		// final int index = this.particleDetectionRuns.indexOf(analysisRun);
		// .setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void selectSNRRun(final OmegaSNRRun analysisRun) {
		this.isHandlingEvent = true;
		// TODO select snr run in list
		// final int index = this.particleDetectionRuns.indexOf(analysisRun);
		// .setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void setGateway(final OmegaGateway gateway) {

	}

	public void updateStatus(final String s) {
		try {
			this.statusPanel.updateStatus(0, s);
		} catch (final OmegaPluginExceptionStatusPanel ex) {
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex);
		}
	}

	public void updateSelectedSNRRun(final OmegaSNRRun snrRun) {
		// TODO capire se serve
		this.selectedSNRRun = snrRun;
		this.fireEventSelectionSNRRun();
		if (snrRun != null) {
			// TODO update SNR fields?
			this.runPanel.updateRunFields(snrRun.getAlgorithmSpec()
			        .getParameters());
		}
	}

	public void updateSelectedParticleDetectionRun(
	        final OmegaParticleDetectionRun particleDetectionRun) {
		this.selectedParticleDetectionRun = particleDetectionRun;
		this.fireEventSelectionParticleDetectionRun();
		this.setAddAndRemoveButtonsEnabled(false);
		if (this.isRunningBatch)
			return;
		if (this.selectedParticleDetectionRun != null) {
			// TODO update SNR fields?
			// this.runPanel.updateImageFields(particleDetectionRun);
			if (this.particlesToProcess.containsKey(this.selectedImage)) {
				this.removeFromProcess_butt.setEnabled(true);
				this.runPanel.updateRunFields(this.particlesToProcess
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

	public boolean checkIfThisAlgorithm(final OmegaSNRRun snrRun) {
		final OmegaAlgorithmPlugin plugin = (OmegaAlgorithmPlugin) this
		        .getPlugin();
		return plugin.checkIfThisAlgorithm(snrRun);
	}

	private void updateSNRRunnerMessageStatus(final String msg,
	        final boolean ended) {
		try {
			this.statusPanel.updateStatus(1, msg);
		} catch (final OmegaPluginExceptionStatusPanel ex) {
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex);
		}
		if (ended) {
			this.updateRunnerEnded();
		}
	}

	@Override
	public void updateMessageStatus(final OmegaMessageEvent evt) {
		final SNRMessageEvent specificEvent = (SNRMessageEvent) evt;
		this.updateSNRRunnerMessageStatus(specificEvent.getMessage(),
		        specificEvent.isEnded());
	}
}
