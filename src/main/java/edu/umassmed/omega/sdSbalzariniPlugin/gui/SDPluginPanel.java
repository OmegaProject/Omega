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
package main.java.edu.umassmed.omega.sdSbalzariniPlugin.gui;

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

import main.java.edu.umassmed.omega.commons.OmegaLogFileManager;
import main.java.edu.umassmed.omega.commons.constants.OmegaConstants;
import main.java.edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import main.java.edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaFrame;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import main.java.edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import main.java.edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;
import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventPreviewParticleDetection;
import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsParticleDetection;
import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionImage;
import main.java.edu.umassmed.omega.commons.exceptions.OmegaPluginExceptionStatusPanel;
import main.java.edu.umassmed.omega.commons.gui.GenericPluginPanel;
import main.java.edu.umassmed.omega.commons.gui.GenericStatusPanel;
import main.java.edu.umassmed.omega.commons.gui.GenericTrackingResultsPanel;
import main.java.edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import main.java.edu.umassmed.omega.commons.plugins.OmegaAlgorithmPlugin;
import main.java.edu.umassmed.omega.commons.plugins.OmegaPlugin;
import main.java.edu.umassmed.omega.sdSbalzariniPlugin.SDConstants;
import main.java.edu.umassmed.omega.sdSbalzariniPlugin.runnable.SDMessageEvent;
import main.java.edu.umassmed.omega.sdSbalzariniPlugin.runnable.SDRunner2;

public class SDPluginPanel extends GenericPluginPanel implements
OmegaMessageDisplayerPanelInterface {

	private static final long serialVersionUID = -5740459087763362607L;

	private JSplitPane mainSplitPane, browserSplitPane;

	private JTabbedPane tabPanel;

	private SDRunPanel runPanel;
	private GenericTrackingResultsPanel resPanel;

	// private OmeroListPanel projectListPanel;
	private SDLoadedDataBrowserPanel loadedDataBrowserPanel;
	private SDQueueRunBrowserPanel queueRunBrowserPanel;

	private JButton addToProcess_butt, removeFromProcess_butt;
	private JButton processBatch_butt, processPreview_butt;

	private GenericStatusPanel statusPanel;

	private OmegaGateway gateway;

	private OmegaImage selectedImage;
	private OmegaParticleDetectionRun selectedParticleDetectionRun;

	private final Map<OmegaImage, List<OmegaParameter>> imagesToProcess;

	private Thread sdThread;
	// private SDRunner sdRunner;
	private SDRunner2 sdRunner2;

	private boolean isRunningBatch;

	public SDPluginPanel(final RootPaneContainer parent,
			final OmegaPlugin plugin, final OmegaGateway gateway,
			final List<OmegaImage> images, final int index) {
		super(parent, plugin, index);

		this.sdThread = null;
		this.sdRunner2 = null;

		this.imagesToProcess = new HashMap<OmegaImage, List<OmegaParameter>>();

		this.selectedImage = null;
		this.selectedParticleDetectionRun = null;

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
		this.loadedDataBrowserPanel = new SDLoadedDataBrowserPanel(
				this.getParentContainer(), this);

		this.queueRunBrowserPanel = new SDQueueRunBrowserPanel(
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
		this.runPanel = new SDRunPanel(this.getParentContainer(), this.gateway);
		final JScrollPane scrollPaneRun = new JScrollPane(this.runPanel);
		this.tabPanel.add(SDConstants.RUN_DEFINITION, scrollPaneRun);

		this.resPanel = new GenericTrackingResultsPanel(
				this.getParentContainer());
		this.tabPanel.add("Detection Results", this.resPanel);

		this.mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.mainSplitPane.setLeftComponent(browserPanel);
		this.mainSplitPane.setRightComponent(this.tabPanel);

		this.add(this.mainSplitPane, BorderLayout.CENTER);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		this.statusPanel = new GenericStatusPanel(2);

		final JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());

		this.processPreview_butt = new JButton(SDConstants.PREVIEW_BUTTON);
		this.processPreview_butt.setEnabled(false);
		buttonsPanel.add(this.processPreview_butt);

		this.processBatch_butt = new JButton(SDConstants.EXECUTE_BUTTON);
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
				SDPluginPanel.this.handleResize();
			}
		});
		this.addToProcess_butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SDPluginPanel.this.addToProcessList();
			}
		});
		this.removeFromProcess_butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SDPluginPanel.this.removeFromProcessList();
			}
		});
		this.processPreview_butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SDPluginPanel.this.processPreview();
			}
		});
		this.processBatch_butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SDPluginPanel.this.processBatch();
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

	private void processPreview() {
		// TODO add test for more than 1 image (cannot preview more than one)
		this.isRunningBatch = true;
		this.setAddAndRemoveButtonsEnabled(false);
		this.setProcessButtonsEnabled(false);
		this.runPanel.setFieldsEnalbed(false);
		// this.sdRunner = new SDRunner(this, this.imagesToProcess,
		// this.gateway,
		// true, this.getPlugin());
		this.sdRunner2 = new SDRunner2(this, this.imagesToProcess,
		        this.gateway, true, this.getPlugin());
		this.sdThread = new Thread(this.sdRunner2);
		this.sdThread.setName(this.sdRunner2.getClass().getSimpleName());
		OmegaLogFileManager.registerAsExceptionHandlerOnThread(this.sdThread);
		this.sdThread.start();
	}

	private void processBatch() {
		this.isRunningBatch = true;
		this.setAddAndRemoveButtonsEnabled(false);
		this.setProcessButtonsEnabled(false);
		this.runPanel.setFieldsEnalbed(false);
		// this.sdRunner = new SDRunner(this, this.imagesToProcess,
		// this.gateway,
		// false, this.getPlugin());
		this.sdRunner2 = new SDRunner2(this, this.imagesToProcess,
				this.gateway, false, this.getPlugin());
		this.sdThread = new Thread(this.sdRunner2);
		this.sdThread.setName(this.sdRunner2.getClass().getSimpleName());
		OmegaLogFileManager.registerAsExceptionHandlerOnThread(this.sdThread);
		this.sdThread.start();
	}

	private void updateRunnerEnded() {
		if (this.sdRunner2.isJobCompleted()) {
			final Map<OmegaImage, List<OmegaParameter>> processedImages = this.sdRunner2
					.getImageParameters();
			final Map<OmegaImage, Map<OmegaFrame, List<OmegaROI>>> resultingParticles = this.sdRunner2
					.getImageResultingParticles();
			final Map<OmegaImage, Map<OmegaROI, Map<String, Object>>> resultingParticlesValues = this.sdRunner2
					.getImageParticlesAdditionalValues();

			for (final OmegaImage image : processedImages.keySet()) {
				final List<OmegaParameter> params = processedImages.get(image);

				final Map<OmegaFrame, List<OmegaROI>> particles = resultingParticles
						.get(image);
				final Map<OmegaROI, Map<String, Object>> particlesValues = resultingParticlesValues
						.get(image);

				final OmegaPluginEventResultsParticleDetection particleDetectionEvt = new OmegaPluginEventResultsParticleDetection(
						this.getPlugin(), image, params, particles,
						particlesValues);

				this.imagesToProcess.remove(image);
				this.queueRunBrowserPanel.updateTree(this.imagesToProcess);

				this.getPlugin().fireEvent(particleDetectionEvt);
			}
			if (this.sdThread.isAlive()) {
				try {
					this.sdThread.join();
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

	private void updatePreviewEnded() {
		if (this.sdRunner2.isJobCompleted()) {
			final Map<OmegaImage, List<OmegaParameter>> processedImages = this.sdRunner2
					.getImageParameters();
			final Map<OmegaImage, Map<OmegaFrame, List<OmegaROI>>> resultingParticles = this.sdRunner2
					.getImageResultingParticles();

			for (final OmegaImage image : processedImages.keySet()) {
				final List<OmegaParameter> params = processedImages.get(image);

				final Map<OmegaFrame, List<OmegaROI>> particles = resultingParticles
						.get(image);

				if (particles.size() > 1) {
					// TODO problem
					System.out.println("more than 1 frame error!!!");
				} else {
					for (final OmegaFrame frame : particles.keySet()) {
						final OmegaPluginEventPreviewParticleDetection particleDetectionEvt = new OmegaPluginEventPreviewParticleDetection(
								this.getPlugin(), image, params, frame,
						        particles.get(frame));
						this.getPlugin().fireEvent(particleDetectionEvt);
					}
				}

				this.imagesToProcess.remove(image);
				this.queueRunBrowserPanel.updateTree(this.imagesToProcess);

			}
			if (this.sdThread.isAlive()) {
				try {
					this.sdThread.join();
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
				// Lanciare eccezione o printare errore a schermo
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

	private void fireEventSelectionParticleDetectionRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(), this.selectedParticleDetectionRun);
		this.getPlugin().fireEvent(event);
	}

	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
		this.runPanel.setGateway(gateway);
	}

	public void updateSelectedParticleDetectionRun(
	        final OmegaParticleDetectionRun particleDetectionRun) {
		this.resPanel.setAnalysisRun(null);
		this.selectedParticleDetectionRun = particleDetectionRun;
		this.fireEventSelectionParticleDetectionRun();
		this.setAddAndRemoveButtonsEnabled(false);
		if (this.isRunningBatch)
			return;
		if (this.selectedParticleDetectionRun != null) {
			this.runPanel.updateRunFields(this.selectedParticleDetectionRun
			        .getAlgorithmSpec().getParameters());
			this.resPanel.setAnalysisRun(this.selectedParticleDetectionRun);
		}
	}

	public void updateSelectedImage(final OmegaImage image) {
		this.resPanel.setAnalysisRun(null);
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
		this.processPreview_butt.setEnabled(enabled);
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
		final SDMessageEvent specificEvent = (SDMessageEvent) evt;
		specificEvent.getSource();
		final SDMessageEvent sdEvt = (SDMessageEvent) evt;
		try {
			this.statusPanel.updateStatus(1, evt.getMessage());
		} catch (final OmegaPluginExceptionStatusPanel ex) {
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex);
		}
		if (sdEvt.isEnded()) {
			if (sdEvt.isPreview()) {
				this.updatePreviewEnded();
			} else {
				this.updateRunnerEnded();
			}
		}
	}
}
