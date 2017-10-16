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
package edu.umassmed.omega.sdSbalzariniPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstantsAlgorithmParameters;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainerInterface;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaNamedElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaPlane;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventPreviewParticleDetection;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsParticleDetection;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionImage;
import edu.umassmed.omega.commons.exceptions.OmegaPluginExceptionStatusPanel;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.gui.GenericStatusPanel;
import edu.umassmed.omega.commons.gui.GenericTrackingResultsPanel;
import edu.umassmed.omega.commons.gui.interfaces.GenericElementInformationContainerInterface;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.commons.plugins.OmegaAlgorithmPlugin;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.commons.utilities.OmegaFileUtilities;
import edu.umassmed.omega.sdSbalzariniPlugin.SDConstants;
import edu.umassmed.omega.sdSbalzariniPlugin.runnable.SDMessageEvent;
import edu.umassmed.omega.sdSbalzariniPlugin.runnable.SDRunner2;

public class SDPluginPanel extends GenericPluginPanel implements
		OmegaMessageDisplayerPanelInterface,
		GenericElementInformationContainerInterface {
	
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
	
	private final Map<Integer, Map<OmegaImage, List<OmegaElement>>> selections;
	private final Map<Integer, Map<OmegaImage, List<OmegaParameter>>> imagesToProcess;
	
	private Thread sdThread;
	// private SDRunner sdRunner;
	private SDRunner2 sdRunner2;
	
	private boolean isRunningBatch, isHandlingEvent;
	
	public SDPluginPanel(final RootPaneContainer parent,
			final OmegaPlugin plugin, final OmegaGateway gateway,
			final List<OmegaImage> images,
			final List<OmegaAnalysisRun> loadedAnalysisRuns, final int index) {
		super(parent, plugin, index);
		
		this.sdThread = null;
		this.sdRunner2 = null;
		
		this.imagesToProcess = new LinkedHashMap<Integer, Map<OmegaImage, List<OmegaParameter>>>();
		this.selections = new LinkedHashMap<Integer, Map<OmegaImage, List<OmegaElement>>>();
		
		this.selectedImage = null;
		this.selectedParticleDetectionRun = null;
		
		this.isRunningBatch = false;
		this.isHandlingEvent = false;
		
		this.gateway = gateway;
		
		this.setPreferredSize(new Dimension(750, 500));
		this.setLayout(new BorderLayout());
		// this.createMenu();
		this.createAndAddWidgets();
		this.loadedDataBrowserPanel.updateTree(images, loadedAnalysisRuns);
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
		
		final InputStream s1 = OmegaFileUtilities
				.getImageFilename("green_plus.png");
		ImageIcon addIcon = null;
		try {
			addIcon = new ImageIcon(ImageIO.read(s1));
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.addToProcess_butt = new JButton(addIcon);
		this.addToProcess_butt.setPreferredSize(new Dimension(30, 30));
		final InputStream s2 = OmegaFileUtilities
				.getImageFilename("red_minus.png");
		ImageIcon removeIcon = null;
		try {
			removeIcon = new ImageIcon(ImageIO.read(s2));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.removeFromProcess_butt = new JButton(removeIcon);
		this.removeFromProcess_butt.setPreferredSize(new Dimension(30, 30));
		
		this.setAddAndRemoveButtonsEnabled(false);
		
		browserButtonPanel.add(this.addToProcess_butt);
		browserButtonPanel.add(this.removeFromProcess_butt);
		
		browserPanel.add(browserButtonPanel, BorderLayout.SOUTH);
		
		this.tabPanel = new JTabbedPane(SwingConstants.TOP,
				JTabbedPane.WRAP_TAB_LAYOUT);
		
		// TODO create panel for parameters
		this.runPanel = new SDRunPanel(this.getParentContainer(), this.gateway,
				this);
		// final JScrollPane scrollPaneRun = new JScrollPane(this.runPanel);
		this.tabPanel.add(OmegaGUIConstants.PLUGIN_RUN_DEFINITION,
				this.runPanel);
		
		this.resPanel = new GenericTrackingResultsPanel(
				this.getParentContainer());
		this.tabPanel.add("Detector Results", this.resPanel);
		
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
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex,
					true);
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
		final List<OmegaPluginEvent> events = new ArrayList<OmegaPluginEvent>();
		if (!this.sdRunner2.isJobCompleted())
			return;
		final Map<Integer, Map<OmegaImage, List<OmegaParameter>>> processedImages = this.sdRunner2
				.getImageParameters();
		final Map<Integer, Map<OmegaImage, Map<OmegaPlane, List<OmegaROI>>>> resultingParticles = this.sdRunner2
				.getImageResultingParticles();
		final Map<Integer, Map<OmegaImage, Map<OmegaROI, Map<String, Object>>>> resultingParticlesValues = this.sdRunner2
				.getImageParticlesAdditionalValues();

		for (final Integer index : processedImages.keySet()) {
			for (final OmegaImage image : processedImages.get(index).keySet()) {
				final List<OmegaParameter> params = processedImages.get(index)
						.get(image);
				final List<OmegaElement> selection = this.selections.get(index)
						.get(image);

				final Map<OmegaPlane, List<OmegaROI>> particles = resultingParticles
						.get(index).get(image);
				final Map<OmegaROI, Map<String, Object>> particlesValues = resultingParticlesValues
						.get(index).get(image);

				final OmegaPluginEventResultsParticleDetection particleDetectionEvt = new OmegaPluginEventResultsParticleDetection(
						this.getPlugin(), selection, image, params, particles,
						particlesValues);
				events.add(particleDetectionEvt);
				this.imagesToProcess.remove(index);
				this.selections.remove(index);
				this.queueRunBrowserPanel.updateTree(this.imagesToProcess);
			}
		}
		if (this.sdThread.isAlive()) {
			try {
				this.sdThread.join();
			} catch (final InterruptedException ex) {
				OmegaLogFileManager.handlePluginException(this.getPlugin(), ex,
						true);
			}
		}
		// this.setEnabled(true);

		this.setProcessButtonsEnabled(true);
		this.runPanel.setFieldsEnalbed(true);
		this.resetStatusMessages();
		this.isRunningBatch = false;

		for (final OmegaPluginEvent event : events) {
			this.getPlugin().fireEvent(event);
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException ex) {
				OmegaLogFileManager.handlePluginException(this.getPlugin(), ex,
						true);
				// ex.printStackTrace();
			}
		}
	}
	
	private void updatePreviewEnded() {
		final List<OmegaPluginEvent> events = new ArrayList<OmegaPluginEvent>();
		if (!this.sdRunner2.isJobCompleted())
			return;
		final Map<Integer, Map<OmegaImage, List<OmegaParameter>>> processedImages = this.sdRunner2
				.getImageParameters();
		final Map<Integer, Map<OmegaImage, Map<OmegaPlane, List<OmegaROI>>>> resultingParticles = this.sdRunner2
				.getImageResultingParticles();

		for (final Integer index : processedImages.keySet()) {
			for (final OmegaImage image : processedImages.get(index).keySet()) {
				final List<OmegaParameter> params = processedImages.get(index)
						.get(image);
				final List<OmegaElement> selection = this.selections.get(index)
						.get(image);

				final Map<OmegaPlane, List<OmegaROI>> particles = resultingParticles
						.get(index).get(image);

				if (particles.size() > 1) {
					// TODO problem
					// System.out.println("more than 1 frame error!!!");
				} else {
					for (final OmegaPlane plane : particles.keySet()) {
						final List<OmegaROI> rois = particles.get(plane);
						final OmegaPluginEventPreviewParticleDetection particleDetectionEvt = new OmegaPluginEventPreviewParticleDetection(
								this.getPlugin(), selection, image, params,
								plane, rois);
						events.add(particleDetectionEvt);
					}
				}
				this.imagesToProcess.remove(index);
				this.selections.remove(index);
				this.queueRunBrowserPanel.updateTree(this.imagesToProcess);

			}
		}
		if (this.sdThread.isAlive()) {
			try {
				this.sdThread.join();
			} catch (final InterruptedException ex) {
				OmegaLogFileManager.handlePluginException(this.getPlugin(), ex,
						true);
			}
		}
		// this.setEnabled(true);
		
		this.setProcessButtonsEnabled(true);
		this.runPanel.setFieldsEnalbed(true);
		this.resetStatusMessages();
		this.isRunningBatch = false;
		
		for (final OmegaPluginEvent event : events) {
			this.getPlugin().fireEvent(event);
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException ex) {
				OmegaLogFileManager.handlePluginException(this.getPlugin(), ex,
						true);
				// ex.printStackTrace();
			}
		}
	}
	
	private void updateImagesToProcess(final int action) {
		switch (action) {
			case 1:
				final Integer selectedIndex = this.queueRunBrowserPanel
						.getSelectedIndex();
				this.imagesToProcess.remove(selectedIndex);
				this.selections.remove(selectedIndex);
				// this.imagesToProcess.remove(this.selectedImage);
				// this.selections.remove(this.selectedImage);
				break;
			default:
				final List<OmegaParameter> params = this.runPanel
						.getParameters();
				if (params != null) {
					final String[] errors = this.runPanel.getParametersError();
					final StringBuffer exceptionError = new StringBuffer();
					int countErrors = 0;
					for (int index = 0; index < errors.length; index++) {
						final String error = errors[index];
						if (error == null) {
							continue;
						}
						countErrors++;
						exceptionError.append(error);
						if (index != (errors.length - 1)) {
							exceptionError.append(" & ");
						}
					}
					if (countErrors > 0) {
						final StringBuffer buf = new StringBuffer();
						buf.append("Error in parameters -> ");
						buf.append(exceptionError.toString());
						try {
							this.statusPanel.updateStatus(1, buf.toString());
						} catch (final OmegaPluginExceptionStatusPanel ex) {
							OmegaLogFileManager.handlePluginException(
									this.getPlugin(), ex, true);
						}
						break;
					}
					// Lanciare eccezione o printare errore a schermo
					// throw new OmegaAlgorithmParametersTypeException(
					// this.getPlugin(), exceptionError.toString());
				}
				final int index = this.imagesToProcess.size();
				final Map<OmegaImage, List<OmegaParameter>> imageAndParams = new LinkedHashMap<OmegaImage, List<OmegaParameter>>();
				imageAndParams.put(this.selectedImage, params);
				this.imagesToProcess.put(index, imageAndParams);
				final List<OmegaElement> selection = new ArrayList<OmegaElement>();
				selection.add(this.selectedImage);
				final Map<OmegaImage, List<OmegaElement>> imageAndSelections = new LinkedHashMap<OmegaImage, List<OmegaElement>>();
				imageAndSelections.put(this.selectedImage, selection);
				this.selections.put(index, imageAndSelections);
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
		this.resPanel.updateParentContainer(parent);
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
		String c = null, z = null;
		if (this.selectedParticleDetectionRun != null) {
			for (final OmegaParameter param : ((OmegaAnalysisRun) this.selectedParticleDetectionRun)
					.getAlgorithmSpec().getParameters()) {
				if (param.getName().equals(
						OmegaConstantsAlgorithmParameters.PARAM_CHANNEL)) {
					c = param.getStringValue();
				} else if (param.getName().equals(
						OmegaConstantsAlgorithmParameters.PARAM_ZSECTION)) {
					z = param.getStringValue();
				}
			}
		}
		this.resPanel.setAnalysisRun(null, c, z);
		this.selectedParticleDetectionRun = particleDetectionRun;
		this.setAddAndRemoveButtonsEnabled(false);
		if (this.selectedParticleDetectionRun != null) {
			if (!this.isHandlingEvent) {
				this.fireEventSelectionParticleDetectionRun();
			}
			if (this.isRunningBatch)
				return;
			this.runPanel.updateRunFields(this.selectedParticleDetectionRun
					.getAlgorithmSpec().getParameters());
			this.resPanel.setAnalysisRun(this.selectedParticleDetectionRun, c,
					z);
		}
	}
	
	protected void updateSelectedImage(final OmegaImage image) {
		this.resPanel.setAnalysisRun(null, null, null);
		this.selectedImage = image;
		this.runPanel.updateImageFields(null);
		this.runPanel.updateRunFieldsDefault();
		this.setAddAndRemoveButtonsEnabled(false);
		if (image != null) {
			if (!this.isHandlingEvent) {
				this.fireEventSelectionImage();
			}
			if (this.isRunningBatch)
				return;
			this.runPanel.updateImageFields(image);
			// if (this.imagesToProcess.containsKey(this.selectedImage)) {
			// //this.removeFromProcess_butt.setEnabled(true);
			// this.runPanel.updateRunFields(this.imagesToProcess
			// .get(this.selectedImage));
			// } else {
			// this.addToProcess_butt.setEnabled(true);
			// this.runPanel.updateRunFieldsDefault();
			// }
			// this.addToProcess_butt.setEnabled(true);
		}
	}

	public void selectImage(final OmegaAnalysisRunContainerInterface image) {
		this.isHandlingEvent = true;
		this.updateSelectedImage(null);
		if (image instanceof OmegaImage) {
			// this.updateSelectedImage((OmegaImage) image);
			this.loadedDataBrowserPanel
					.selectTreeElement((OmegaNamedElement) image);
			this.updateSelectedImage((OmegaImage) image);
		}
		this.isHandlingEvent = false;
	}

	public void selectParticleDetectionRun(
			final OmegaParticleDetectionRun analysisRun) {
		this.isHandlingEvent = true;
		// TODO select particle detection run in list
		this.loadedDataBrowserPanel.selectTreeElement(analysisRun);
		this.updateSelectedParticleDetectionRun(analysisRun);
		this.isHandlingEvent = false;
	}
	
	protected void setAddAndRemoveButtonsEnabled(final boolean enabled) {
		this.addToProcess_butt.setEnabled(enabled);
		this.removeFromProcess_butt.setEnabled(enabled);
	}

	protected void setAddButtonEnabled(final boolean enabled) {
		this.addToProcess_butt.setEnabled(enabled);
	}

	protected void setRemoveButtonEnabled(final boolean enabled) {
		this.removeFromProcess_butt.setEnabled(enabled);
	}
	
	private void setProcessButtonsEnabled(final boolean enabled) {
		// this.processRealTime_butt.setEnabled(enabled);
		this.processBatch_butt.setEnabled(enabled);
		this.processPreview_butt.setEnabled(enabled);
	}
	
	public void updateTrees(final List<OmegaImage> images,
			final List<OmegaAnalysisRun> loadedAnalysisRuns) {
		this.loadedDataBrowserPanel.updateTree(images, loadedAnalysisRuns);
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
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex,
					true);
		}
		if (sdEvt.isEnded()) {
			if (sdEvt.isPreview()) {
				this.updatePreviewEnded();
			} else {
				this.updateRunnerEnded();
			}
		}
	}

	@Override
	public void fireElementChanged() {
		this.fireEventSelectionImage();
	}

	public void deselectNotListener(final GenericPanel panel) {
		if (panel instanceof SDLoadedDataBrowserPanel) {
			this.queueRunBrowserPanel.deselect();
		} else {
			this.loadedDataBrowserPanel.deselect();
		}
	}
}
