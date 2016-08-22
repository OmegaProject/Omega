/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team:
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli,
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban,
 * Ivo ErrorIndex and Mario Valle.
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaSNRRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OrphanedAnalysisContainer;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaPlane;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
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
import edu.umassmed.omega.commons.utilities.OmegaFileUtilities;
import edu.umassmed.omega.snrSbalzariniPlugin.SNRConstants;
import edu.umassmed.omega.snrSbalzariniPlugin.runnable.SNRMessageEvent;
import edu.umassmed.omega.snrSbalzariniPlugin.runnable.SNRRunner;
import edu.umassmed.omega.trajectoriesRelinkingPlugin.TRConstants;

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
	private OrphanedAnalysisContainer orphanedAnalysis;

	private OmegaAnalysisRunContainer selectedImage;
	private OmegaParticleDetectionRun selectedParticleDetectionRun;
	private OmegaSNRRun selectedSNRRun;

	private List<OmegaAnalysisRun> loadedAnalysisRuns;

	private final Map<OmegaParticleDetectionRun, List<OmegaParameter>> particlesToProcess;

	private Thread snrThread;
	private SNRRunner snrRunner;

	private boolean isRunningBatch;

	private JPanel topPanel;
	private JMenuItem hideDataSelection_mItm;

	public SNRPluginPanel(final RootPaneContainer parent,
			final OmegaPlugin plugin, final OmegaGateway gateway,
			final List<OmegaImage> images,
			final OrphanedAnalysisContainer orphanedAnalysis,
			final List<OmegaAnalysisRun> analysisRuns, final int index) {
		super(parent, plugin, index);

		this.gateway = gateway;

		this.particlesToProcess = new LinkedHashMap<>();

		this.images = images;
		this.orphanedAnalysis = orphanedAnalysis;
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
		final JMenuBar menuBar = this.getMenu();
		for (int i = 0; i < menuBar.getMenuCount(); i++) {
			final JMenu menu = menuBar.getMenu(i);
			if (!menu.getText().equals(OmegaGUIConstants.MENU_VIEW)) {
				continue;
			}
			this.hideDataSelection_mItm = new JMenuItem(
			        OmegaGUIConstants.MENU_VIEW_HIDE_DATA_SELECTION);
			menu.add(this.hideDataSelection_mItm);
		}
	}

	private void createAndAddWidgets() {
		this.topPanel = new JPanel();
		this.topPanel.setLayout(new GridLayout(1, 1));

		final JPanel p1 = new JPanel();
		p1.setLayout(new BorderLayout());
		final JLabel lbl1 = new JLabel(TRConstants.SELECT_IMAGE);
		lbl1.setPreferredSize(OmegaConstants.TEXT_SIZE);
		p1.add(lbl1, BorderLayout.WEST);
		this.images_cmb = new JComboBox<String>();
		this.images_cmb.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.images_cmb.setEnabled(false);
		p1.add(this.images_cmb, BorderLayout.CENTER);
		this.topPanel.add(p1);

		this.add(this.topPanel, BorderLayout.NORTH);

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
		final String s1 = OmegaFileUtilities.getImageFilename("green_plus.jpg");
		final ImageIcon addIcon = new ImageIcon(s1);
		this.addToProcess_butt = new JButton(addIcon);
		this.addToProcess_butt.setPreferredSize(new Dimension(30, 30));
		final String s2 = OmegaFileUtilities.getImageFilename("red_minus.jpg");
		final ImageIcon removeIcon = new ImageIcon(s2);
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

		this.tabbedPane.add(SNRConstants.RUN_DEFINITION, scrollPaneRun);

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
		// buttonsPanel.add(this.processRealTime_butt);

		this.processBatch_butt = new JButton(SNRConstants.EXECUTE_BUTTON);
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
		this.hideDataSelection_mItm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SNRPluginPanel.this.handleHideDataSelection();
			}
		});
	}

	private void handleHideDataSelection() {
		if (this.hideDataSelection_mItm.getText().equals(
		        OmegaGUIConstants.MENU_VIEW_HIDE_DATA_SELECTION)) {
			this.remove(this.topPanel);
			this.hideDataSelection_mItm
			        .setText(OmegaGUIConstants.MENU_VIEW_SHOW_DATA_SELECTION);
		} else {
			this.add(this.topPanel, BorderLayout.NORTH);
			this.hideDataSelection_mItm
			        .setText(OmegaGUIConstants.MENU_VIEW_HIDE_DATA_SELECTION);
		}
		this.revalidate();
		this.repaint();
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

			final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageNoises = this.snrRunner
					.getResultingImageNoise();
			final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageBackgrounds = this.snrRunner
					.getResultingImageBackground();
			final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageAvgSNR = this.snrRunner
					.getResultingImageAverageSNR();
			final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageMinSNR = this.snrRunner
					.getResultingImageMinimumSNR();
			final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageMaxSNR = this.snrRunner
					.getResultingImageMaximumSNR();
			final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageAvgErrorIndexSNR = this.snrRunner
					.getResultingImageAverageErrorIndexSNR();
			final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageMinErrorIndexSNR = this.snrRunner
					.getResultingImageMinimumErrorIndexSNR();
			final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageMaxErrorIndexSNR = this.snrRunner
					.getResultingImageMaximumErrorIndexSNR();
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
			final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> localErrorIndexSNRs = this.snrRunner
					.getResultingLocalErrorIndexSNRs();

			for (final OmegaParticleDetectionRun spotDetectionRun : processedParticles
					.keySet()) {
				final List<OmegaParameter> params = processedParticles
						.get(spotDetectionRun);
				final Map<OmegaPlane, Double> noises = imageNoises
						.get(spotDetectionRun);
				final Map<OmegaPlane, Double> backgrounds = imageBackgrounds
						.get(spotDetectionRun);
				final Map<OmegaPlane, Double> averageSNR = imageAvgSNR
						.get(spotDetectionRun);
				final Map<OmegaPlane, Double> minimumSNR = imageMinSNR
						.get(spotDetectionRun);
				final Map<OmegaPlane, Double> maximumSNR = imageMaxSNR
						.get(spotDetectionRun);
				final Map<OmegaPlane, Double> averageErrorIndexSNR = imageAvgErrorIndexSNR
						.get(spotDetectionRun);
				final Map<OmegaPlane, Double> minimumErrorIndexSNR = imageMinErrorIndexSNR
						.get(spotDetectionRun);
				final Map<OmegaPlane, Double> maximumErrorIndexSNR = imageMaxErrorIndexSNR
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
				final Map<OmegaROI, Double> lErrorIndexSNRs = localErrorIndexSNRs
						.get(spotDetectionRun);

				final OmegaPluginEventResultsSNR snrResultsEvt = new OmegaPluginEventResultsSNR(
						this.getPlugin(), spotDetectionRun, params, noises,
						backgrounds, averageSNR, minimumSNR, maximumSNR,
				        averageErrorIndexSNR, minimumErrorIndexSNR,
						maximumErrorIndexSNR, lCentralSignals, lMeanSignals,
						lSignalSizes, lPeakSignals, lNoises, lSNRs,
						lErrorIndexSNRs);

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
		if ((this.images == null) || (index >= this.images.size())) {
			this.selectedImage = this.orphanedAnalysis;
		} else {
			this.selectedImage = this.images.get(index);
		}
		if (!this.isHandlingEvent) {
			this.fireEventSelectionImage();
		}
		if (!this.isHandlingEvent) {
			this.fireEventSelectionImage();
		}
		this.runPanel.updateImageFields((OmegaElement) this.selectedImage);

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
		this.loadedDataBrowserPanel.updateParentContainer(parent);
		this.queueRunBrowserPanel.updateParentContainer(parent);
		this.runPanel.updateParentContainer(parent);
	}

	@Override
	public void onCloseOperation() {

	}

	public void updateCombos(final List<OmegaImage> images,
			final OrphanedAnalysisContainer orphanedAnalysis,
			final List<OmegaAnalysisRun> analysisRuns) {
		this.isHandlingEvent = true;
		this.images = images;
		this.orphanedAnalysis = orphanedAnalysis;
		this.loadedAnalysisRuns = analysisRuns;

		this.populateImagesCombo();
		this.isHandlingEvent = false;
	}

	private void populateImagesCombo() {
		this.popImages = true;
		this.images_cmb.removeAllItems();
		this.selectedImage = null;
		this.images_cmb.setSelectedIndex(-1);
		if (((this.images == null) || this.images.isEmpty())
		        && this.orphanedAnalysis.isEmpty()) {
			this.images_cmb.setEnabled(false);
			this.loadedDataBrowserPanel.updateTree(null);
			this.queueRunBrowserPanel.updateTree(null);
			this.repaint();
			this.popImages = false;
			return;
		}

		this.images_cmb.setEnabled(true);

		if (this.images != null) {
			for (final OmegaImage image : this.images) {
				this.images_cmb.addItem(image.getName());
			}
		}
		this.images_cmb.addItem(OmegaGUIConstants.PLUGIN_ORPHANED_ANALYSES);
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

	public void selectImage(final OmegaAnalysisRunContainer image) {
		this.isHandlingEvent = true;
		int index = -1;
		if (this.images != null) {
			index = this.images.indexOf(image);
		}
		if (index == -1) {
			this.images_cmb.setSelectedItem(this.images_cmb.getItemCount());
		} else {
			this.images_cmb.setSelectedIndex(index);
		}
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
			this.runPanel
			.updateAnalysisFields(this.selectedParticleDetectionRun);
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
