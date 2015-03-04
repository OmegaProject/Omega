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
package edu.umassmed.omega.core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.eventSystem.OmegaPluginEventListener;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSelectionImage;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSelectionTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSelectionTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventTrajectories;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventAlgorithm;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventDataChanged;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventGateway;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsParticleDetection;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsParticleLinking;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsParticleTracking;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrajectoriesRelinking;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrajectoriesSegmentation;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionImage;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventTrajectories;
import edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import edu.umassmed.omega.commons.plugins.OmegaAlgorithmPlugin;
import edu.umassmed.omega.commons.plugins.OmegaBrowserPlugin;
import edu.umassmed.omega.commons.plugins.OmegaLoaderPlugin;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.commons.plugins.OmegaTrackingMeasuresPlugin;
import edu.umassmed.omega.commons.plugins.OmegaTrajectoriesRelinkingPlugin;
import edu.umassmed.omega.commons.plugins.OmegaTrajectoriesSegmentationPlugin;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaImageConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoadedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoadedDataConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoaderPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaMainDataConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectImagePluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleDetectionRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleLinkingRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesRelinkingRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesSegmentationRunPluginInterface;
import edu.umassmed.omega.commons.utilities.OmegaAlgorithmsUtilities;
import edu.umassmed.omega.core.gui.OmegaGUIFrame;
import edu.umassmed.omega.core.runnables.OmegaDBLoader;
import edu.umassmed.omega.core.runnables.OmegaDBRunnable;
import edu.umassmed.omega.core.runnables.OmegaDBSaver;
import edu.umassmed.omega.core.runnables.OmegaDBUpdater;
import edu.umassmed.omega.core.runnables.OmegaDBWriter;
import edu.umassmed.omega.core.runnables.OmegaTrackingMeasuresAnalizer;
import edu.umassmed.omega.data.OmegaData;
import edu.umassmed.omega.data.OmegaLoadedData;
import edu.umassmed.omega.data.analysisRunElements.OmegaAlgorithmInformation;
import edu.umassmed.omega.data.analysisRunElements.OmegaAlgorithmSpecification;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrackingMeasuresRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.data.coreElements.OmegaDataset;
import edu.umassmed.omega.data.coreElements.OmegaElement;
import edu.umassmed.omega.data.coreElements.OmegaExperimenter;
import edu.umassmed.omega.data.coreElements.OmegaFrame;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.data.coreElements.OmegaProject;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaDBServerInformation;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaLoginCredentials;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.omegaDataBrowserPlugin.OmegaDataBrowserPlugin;
import edu.umassmed.omega.omeroPlugin.OmeroPlugin;
import edu.umassmed.omega.sptSbalzariniPlugin.SPTPlugin;
import edu.umassmed.omega.trackingMeasuresPlugin.TrackingMeasuresPlugin;
import edu.umassmed.omega.trajectoriesRelinkingPlugin.TrajectoriesRelinkingPlugin;
import edu.umassmed.omega.trajectoriesSegmentationPlugin.TrajectoriesSegmentationPlugin;

public class OmegaApplication implements OmegaPluginEventListener {

	public static final boolean ISDEBUG = true;

	private final OmegaGUIFrame gui;

	private final OmegaData omegaData;
	private final OmegaLoadedData loadedData;
	private final List<OmegaAnalysisRun> loadedAnalysisRuns;
	private OmegaGateway gateway;

	private OmegaExperimenter experimenter;

	private short pluginIndex;
	private final Map<String, Long> pluginIndexes;
	private final List<OmegaPlugin> registeredPlugin;
	private final Map<Long, OmegaPlugin> pluginIndexMap;

	private final OmegaLogFileManager logFileManager;

	private final OmegaOptionsFileManager optionsFileManager;
	private final Map<String, Map<String, String>> generalOptions;

	private final OmegaMySqlGateway mysqlGateway;

	private GenericMessageDialog dbDialog;
	private Thread dbThread;

	public OmegaApplication() {
		this.pluginIndexes = new HashMap<>();
		this.registeredPlugin = new ArrayList<>();
		this.pluginIndexMap = new HashMap<>();
		this.pluginIndex = 0;

		this.logFileManager = OmegaLogFileManager.getOmegaLogFileManager(this);
		OmegaLogFileManager.markCoreNewRun();

		this.optionsFileManager = new OmegaOptionsFileManager();
		this.generalOptions = this.optionsFileManager.getGeneralOptions();

		this.mysqlGateway = new OmegaMySqlGateway();

		this.omegaData = new OmegaData();
		this.loadedData = new OmegaLoadedData();
		this.loadedAnalysisRuns = new ArrayList<OmegaAnalysisRun>();
		this.gateway = null;
		this.experimenter = null;

		this.registerCorePlugins();
		OmegaLogFileManager.markPluginsNewRun(this.registeredPlugin);

		this.gui = new OmegaGUIFrame(this);
		this.gui.initialize(this.pluginIndexMap);
		this.gui.setSize(1200, 800);

	}

	private void registerCorePlugins() {
		this.registerPlugin(new OmeroPlugin());
		this.registerPlugin(new SPTPlugin());
		this.registerPlugin(new TrajectoriesRelinkingPlugin());
		this.registerPlugin(new TrajectoriesSegmentationPlugin());
		this.registerPlugin(new TrackingMeasuresPlugin());
		this.registerPlugin(new OmegaDataBrowserPlugin());

		for (final OmegaPlugin plugin : this.registeredPlugin) {
			final String optionsCategory = plugin.getOptionsCategory();
			final Map<String, String> pluginOptions = this.optionsFileManager
			        .getOptions(optionsCategory);
			plugin.addPluginOptions(pluginOptions);
			if (plugin instanceof OmegaMainDataConsumerPluginInterface) {
				((OmegaMainDataConsumerPluginInterface) plugin)
				        .setMainData(this.omegaData);
			}
			if (plugin instanceof OmegaLoadedDataConsumerPluginInterface) {
				((OmegaLoadedDataConsumerPluginInterface) plugin)
				        .setLoadedData(this.loadedData);
			}
			if (plugin instanceof OmegaLoadedAnalysisConsumerPluginInterface) {
				((OmegaLoadedAnalysisConsumerPluginInterface) plugin)
				        .setLoadedAnalysisRun(this.loadedAnalysisRuns);
			}
			if (plugin instanceof OmegaTrajectoriesSegmentationPlugin) {
				((OmegaTrajectoriesSegmentationPlugin) plugin)
				        .setSegmentationTypesList(this.omegaData
				                .getSegmentationTypesList());
			}
		}
	}

	private void registerPlugin(final OmegaPlugin plugin) {
		plugin.addOmegaPluginListener(this);
		final String name = plugin.getName();
		final long index = this.pluginIndex;
		this.pluginIndex++;
		this.pluginIndexMap.put(index, plugin);
		this.pluginIndexes.put(name, index);
		this.registeredPlugin.add(plugin);
		if (plugin instanceof OmegaAlgorithmPlugin) {
			OmegaAlgorithmPluginDetailsDialogManager.registerDialog(index,
			        (OmegaAlgorithmPlugin) plugin);
		}
	}

	protected void showGUI() {
		this.gui.setVisible(true);
		this.gui.reinitializeStrings();
	}

	public void quit() {
		this.gui.quit();
	}

	public OmegaPlugin getPlugin(final long pluginIndex) {
		return this.pluginIndexMap.get(pluginIndex);
	}

	public void addGeneralOptions(final String category,
	        final Map<String, String> options) {
		this.generalOptions.put(category, options);
	}

	public Map<String, String> getGeneralOptions(final String category) {
		if (!this.generalOptions.containsKey(category))
			return new LinkedHashMap<String, String>();
		else
			return this.generalOptions.get(category);
	}

	public void saveOptions() {
		for (final String category : this.generalOptions.keySet()) {
			this.optionsFileManager.addOptions(category,
			        this.generalOptions.get(category));
		}

		for (final OmegaPlugin plugin : this.registeredPlugin) {
			this.optionsFileManager.addOptions(plugin.getOptionsCategory(),
			        plugin.getPluginOptions());
		}
		this.optionsFileManager.saveOptionsToFile();
	}

	private void loadSelectedData(final List<OmegaElement> selectedData) {
		for (final OmegaElement element : selectedData) {
			if ((element instanceof OmegaProject)
			        && !this.loadedData.containsProject((OmegaProject) element)) {
				this.loadedData.addProject((OmegaProject) element);
			} else if ((element instanceof OmegaDataset)
			        && !this.loadedData.containsDataset((OmegaDataset) element)) {
				this.loadedData.addDataset((OmegaDataset) element);
			} else if ((element instanceof OmegaImage)
			        && !this.loadedData.containsImage((OmegaImage) element)) {
				this.loadedData.addImage((OmegaImage) element);
			} else if ((element instanceof OmegaImagePixels)
			        && !this.loadedData
			                .containsImagePixels((OmegaImagePixels) element)) {
				this.loadedData.addImagePixels((OmegaImagePixels) element);
			} else if ((element instanceof OmegaFrame)
			        && !this.loadedData.containsFrame((OmegaFrame) element)) {
				this.loadedData.addFrame((OmegaFrame) element);
			}
		}
	}

	public void handleCoreEvent(final OmegaCoreEvent event) {
		if (event instanceof OmegaCoreEventTrajectories) {
			this.handleCoreEventTrajectories((OmegaCoreEventTrajectories) event);
		} else if (event instanceof OmegaCoreEventSelectionImage) {
			this.handleCoreEventSelectionImage((OmegaCoreEventSelectionImage) event);
		} else if (event instanceof OmegaCoreEventSelectionAnalysisRun) {
			this.handleCoreEventSelectionAnalysisRun((OmegaCoreEventSelectionAnalysisRun) event);
		}
	}

	private void handleCoreEventSelectionImage(
	        final OmegaCoreEventSelectionImage event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin instanceof OmegaSelectImagePluginInterface) {
				((OmegaSelectImagePluginInterface) plugin).selectImage(event
				        .getImage());
			}
		}
	}

	private void handleCoreEventSelectionAnalysisRun(
	        final OmegaCoreEventSelectionAnalysisRun event) {
		final OmegaAnalysisRun analysisRun = event.getAnalysisRun();
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if ((event instanceof OmegaCoreEventSelectionTrajectoriesSegmentationRun)
			        && (plugin instanceof OmegaTrajectoriesSegmentationPlugin)) {
				((OmegaTrajectoriesSegmentationPlugin) plugin)
				        .selectCurrentTrajectoriesSegmentationRun(event
				                .getAnalysisRun());
			} else if ((event instanceof OmegaCoreEventSelectionTrajectoriesRelinkingRun)
			        && (plugin instanceof OmegaTrajectoriesRelinkingPlugin)) {
				((OmegaTrajectoriesRelinkingPlugin) plugin)
				        .selectCurrentTrajectoriesRelinkingRun(event
				                .getAnalysisRun());
			} else if ((analysisRun instanceof OmegaTrajectoriesSegmentationRun)
			        && (plugin instanceof OmegaSelectTrajectoriesSegmentationRunPluginInterface)) {
				((OmegaSelectTrajectoriesSegmentationRunPluginInterface) plugin)
				        .selectTrajectoriesSegmentationRun((OmegaTrajectoriesSegmentationRun) event
				                .getAnalysisRun());
			} else if ((analysisRun instanceof OmegaTrajectoriesRelinkingRun)
			        && (plugin instanceof OmegaSelectTrajectoriesRelinkingRunPluginInterface)) {
				((OmegaSelectTrajectoriesRelinkingRunPluginInterface) plugin)
				        .selectTrajectoriesRelinkingRun((OmegaTrajectoriesRelinkingRun) event
				                .getAnalysisRun());
			} else if ((analysisRun instanceof OmegaParticleLinkingRun)
			        && (plugin instanceof OmegaSelectParticleLinkingRunPluginInterface)) {
				((OmegaSelectParticleLinkingRunPluginInterface) plugin)
				        .selectParticleLinkingRun((OmegaParticleLinkingRun) event
				                .getAnalysisRun());
			} else if ((analysisRun instanceof OmegaParticleDetectionRun)
			        && (plugin instanceof OmegaSelectParticleDetectionRunPluginInterface)) {
				((OmegaSelectParticleDetectionRunPluginInterface) plugin)
				        .selectParticleDetectionRun((OmegaParticleDetectionRun) event
				                .getAnalysisRun());
			}
		}
	}

	private void handleCoreEventTrajectories(
	        final OmegaCoreEventTrajectories event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin instanceof OmegaTrajectoriesRelinkingPlugin) {
				((OmegaTrajectoriesRelinkingPlugin) plugin).updateTrajectories(
				        event.getTrajectories(), event.isSelectionEvent());
			} else if (plugin instanceof OmegaTrajectoriesSegmentationPlugin) {
				((OmegaTrajectoriesSegmentationPlugin) plugin)
				        .updateTrajectories(event.getTrajectories(),
				                event.isSelectionEvent());
			} else if (plugin instanceof OmegaTrackingMeasuresPlugin) {
				((OmegaTrackingMeasuresPlugin) plugin).updateTrajectories(
				        event.getTrajectories(), event.isSelectionEvent());
			}
		}
	}

	@Override
	public void handlePluginEvent(final OmegaPluginEvent event) {
		if (event instanceof OmegaPluginEventGateway) {
			this.handlePluginEventGateway((OmegaPluginEventGateway) event);
		} else if (event instanceof OmegaPluginEventDataChanged) {
			this.handlePluginEventDataChanged((OmegaPluginEventDataChanged) event);
		} else if (event instanceof OmegaPluginEventAlgorithm) {
			this.handlePluginEventAlgorithm((OmegaPluginEventAlgorithm) event);
		} else if (event instanceof OmegaPluginEventSelectionImage) {
			this.handlePluginEventSelectionImage((OmegaPluginEventSelectionImage) event);
		} else if (event instanceof OmegaPluginEventSelectionAnalysisRun) {
			this.handlePluginEventSelectionAnalysisRun((OmegaPluginEventSelectionAnalysisRun) event);
		} else {
			this.handlePluginEventTrajectories((OmegaPluginEventTrajectories) event);
		}
	}

	private void handlePluginEventAlgorithm(
	        final OmegaPluginEventAlgorithm event) {
		final OmegaAlgorithmPlugin source = (OmegaAlgorithmPlugin) event
		        .getSource();
		if (source == null)
			// TODO gestire errore
			return;
		if (this.experimenter == null)
			// TODO gestire errore
			return;
		final OmegaElement element = event.getElement();
		if (!(element instanceof OmegaAnalysisRunContainer))
			// TODO gestire errore
			return;

		final OmegaAlgorithmInformation algoInfo = new OmegaAlgorithmInformation(
		        source.getAlgorithmName(), source.getAlgorithmVersion(),
		        source.getAlgorithmDescription(), source.getAlgorithmAuthor(),
		        source.getAlgorithmPublicationDate());
		final OmegaAlgorithmSpecification algoSpec = new OmegaAlgorithmSpecification(
		        algoInfo);
		if (event.getParameters() != null) {
			for (final OmegaParameter param : event.getParameters()) {
				algoSpec.addParameter(param);
			}
		}

		// TODO separare i parameteri e fare 2 algoSpec diverse
		// capire se necessario

		final OmegaAnalysisRun analysisRun;

		if (event instanceof OmegaPluginEventResultsTrajectoriesSegmentation) {
			analysisRun = this
			        .handlePluginEventAlgorithmResultsParticleSegmentationRun(
			                algoSpec, event);
		} else if (event instanceof OmegaPluginEventResultsTrajectoriesRelinking) {
			analysisRun = this
			        .handlePluginEventAlgorithmResultsParticleRelinkingRun(
			                algoSpec, event);
		} else if (event instanceof OmegaPluginEventResultsParticleTracking) {
			analysisRun = this
			        .handlePluginEventAlgorithmResultsParticleTrackingRun(
			                algoSpec, event);
		} else if (event instanceof OmegaPluginEventResultsParticleLinking) {
			analysisRun = this
			        .handlePluginEventAlgorithmResultsParticleLinkingRun(
			                algoSpec, event);
		} else if (event instanceof OmegaPluginEventResultsParticleDetection) {
			analysisRun = this
			        .handlePluginEventAlgorithmResultsParticleDetectionRun(
			                algoSpec, event);
		} else
			// TODO gestire errore
			return;

		((OmegaAnalysisRunContainer) element).addAnalysisRun(analysisRun);
		this.loadedAnalysisRuns.add(analysisRun);

		this.updateGUI(event.getSource(), true);
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsParticleSegmentationRun(
	        final OmegaAlgorithmSpecification algoSpec,
	        final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrajectoriesSegmentation specificEvent = (OmegaPluginEventResultsTrajectoriesSegmentation) event;
		return new OmegaTrajectoriesSegmentationRun(this.experimenter,
		        algoSpec, specificEvent.getResultingSegments(),
		        specificEvent.getSegmentationTypes());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsParticleRelinkingRun(
	        final OmegaAlgorithmSpecification algoSpec,
	        final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrajectoriesRelinking specificEvent = (OmegaPluginEventResultsTrajectoriesRelinking) event;
		final List<OmegaTrajectory> resultingTrajectories = specificEvent
		        .getResultingTrajectories();

		final OmegaTrajectoriesRelinkingRun relinkingRun = new OmegaTrajectoriesRelinkingRun(
		        this.experimenter, algoSpec, resultingTrajectories);

		final OmegaAlgorithmSpecification defaultSegmentationAlgoSpec = OmegaAlgorithmsUtilities
		        .getDefaultSegmentationAlgorithmSpecification();
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = OmegaAlgorithmsUtilities
		        .createDefaultSegmentation(resultingTrajectories);
		final OmegaAnalysisRun defaultSegmentationRun = new OmegaTrajectoriesSegmentationRun(
		        this.experimenter, defaultSegmentationAlgoSpec,
		        "Default segmentation run", segmentsMap,
		        OmegaSegmentationTypes.getDefaultSegmentationTypes());
		this.loadedAnalysisRuns.add(defaultSegmentationRun);
		relinkingRun.addAnalysisRun(defaultSegmentationRun);

		return relinkingRun;
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsParticleLinkingRun(
	        final OmegaAlgorithmSpecification algoSpec,
	        final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsParticleLinking specificEvent = (OmegaPluginEventResultsParticleLinking) event;
		return new OmegaParticleLinkingRun(this.experimenter, algoSpec,
		        specificEvent.getResultingTrajectories());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsParticleDetectionRun(
	        final OmegaAlgorithmSpecification algoSpec,
	        final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsParticleDetection specificEvent = (OmegaPluginEventResultsParticleDetection) event;
		return new OmegaParticleDetectionRun(this.experimenter, algoSpec,
		        specificEvent.getResultingParticles());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsParticleTrackingRun(
	        final OmegaAlgorithmSpecification algoSpec,
	        final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsParticleTracking specificEvent = (OmegaPluginEventResultsParticleTracking) event;
		final Map<OmegaFrame, List<OmegaROI>> resultingParticles = specificEvent
		        .getResultingParticles();
		// for (final OmegaFrame frame : resultingParticles.keySet()) {
		// System.out.println("FI: " + frame.getIndex());
		// for (final OmegaROI roi : resultingParticles.get(frame)) {
		// System.out.print(roi.getFrameIndex() + "\t");
		// }
		// System.out.println();
		// }
		final List<OmegaTrajectory> resultingTrajectories = specificEvent
		        .getResultingTrajectories();

		final OmegaAnalysisRun particleDetectionRun = new OmegaParticleDetectionRun(
		        this.experimenter, algoSpec, resultingParticles);
		final OmegaAnalysisRun particleLinkingRun = new OmegaParticleLinkingRun(
		        this.experimenter, algoSpec, resultingTrajectories);
		this.loadedAnalysisRuns.add(particleLinkingRun);
		particleDetectionRun.addAnalysisRun(particleLinkingRun);

		if (event.getElement() instanceof OmegaImage) {
			this.handleTrackingMeasures((OmegaImage) event.getElement(),
			        (OmegaParticleLinkingRun) particleLinkingRun);
		}

		final OmegaAlgorithmSpecification defaultRelinkingAlgoSpec = OmegaAlgorithmsUtilities
		        .getDefaultRelinkingAlgorithmSpecification();
		final OmegaAnalysisRun defaultRelinkingRun = new OmegaTrajectoriesRelinkingRun(
		        this.experimenter, defaultRelinkingAlgoSpec,
		        "Default relinking run", resultingTrajectories);
		this.loadedAnalysisRuns.add(defaultRelinkingRun);
		particleLinkingRun.addAnalysisRun(defaultRelinkingRun);

		final OmegaAlgorithmSpecification defaultSegmentationAlgoSpec = OmegaAlgorithmsUtilities
		        .getDefaultSegmentationAlgorithmSpecification();
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = OmegaAlgorithmsUtilities
		        .createDefaultSegmentation(resultingTrajectories);
		final OmegaAnalysisRun defaultSegmentationRun = new OmegaTrajectoriesSegmentationRun(
		        this.experimenter, defaultSegmentationAlgoSpec,
		        "Default segmentation run", segmentsMap,
		        OmegaSegmentationTypes.getDefaultSegmentationTypes());
		this.loadedAnalysisRuns.add(defaultSegmentationRun);
		defaultRelinkingRun.addAnalysisRun(defaultSegmentationRun);

		return particleDetectionRun;
	}

	private void handleTrackingMeasures(final OmegaImage image,
	        final OmegaParticleLinkingRun particleLinkingRun) {
		final OmegaTrackingMeasuresAnalizer trackingMeasuresAnalizer = new OmegaTrackingMeasuresAnalizer(
		        this, particleLinkingRun, image.getDefaultPixels().getSizeT());
		final Thread t = new Thread(trackingMeasuresAnalizer);
		t.setName("trackingMeasuresAnalizer");
		t.start();
	}

	// TODO to be changed somehow
	public void updateTrackingMeasuresAnalizerResults(
	        final OmegaParticleLinkingRun particleLinkingRun,
	        final Map<OmegaTrajectory, Double[]> peakSignalsMap,
	        final Map<OmegaTrajectory, Double[]> meanSignalsMap,
	        final Map<OmegaTrajectory, Double[]> localBackgroundsMap,
	        final Map<OmegaTrajectory, Double[]> localSNRsMap,
	        final Map<OmegaTrajectory, List<Double>> distancesMap,
	        final Map<OmegaTrajectory, List<Double>> displacementsMap,
	        final Map<OmegaTrajectory, Double> maxDisplacementsMap,
	        final Map<OmegaTrajectory, Integer> totalTimeTraveledMap,
	        final Map<OmegaTrajectory, List<Double>> confinementRatioMap,
	        final Map<OmegaTrajectory, List<Double[]>> anglesAndDirectionalChangesMap,
	        final Map<OmegaTrajectory, List<Double>> localSpeedMap,
	        final Map<OmegaTrajectory, List<Double>> localVelocityMap,
	        final Map<OmegaTrajectory, Double> meanSpeedMap,
	        final Map<OmegaTrajectory, Double> meanVelocityMap,
	        final Map<OmegaTrajectory, Double[]> ny,
	        final Map<OmegaTrajectory, Double[][]> mu,
	        final Map<OmegaTrajectory, Double[][]> logMu,
	        final Map<OmegaTrajectory, Double[][]> deltaT,
	        final Map<OmegaTrajectory, Double[][]> logDeltaT,
	        final Map<OmegaTrajectory, Double[][]> gammaD,
	        final Map<OmegaTrajectory, Double[][]> gammaDLog,
	        final Map<OmegaTrajectory, Double[]> gamma,
	        final Map<OmegaTrajectory, Double[]> gammaLog,
	        final Map<OmegaTrajectory, Double[]> smss,
	        final Map<OmegaTrajectory, Double[]> smssLog) {
		final OmegaAlgorithmSpecification defaultTrackingMeasuresSpec = OmegaAlgorithmsUtilities
		        .getDefaultTrackingMeasuresSpecification();
		final OmegaTrackingMeasuresRun trackingMeasuresRun = new OmegaTrackingMeasuresRun(
		        this.experimenter, defaultTrackingMeasuresSpec, peakSignalsMap,
		        meanSignalsMap, localBackgroundsMap, localSNRsMap,
		        distancesMap, displacementsMap, maxDisplacementsMap,
		        totalTimeTraveledMap, confinementRatioMap,
		        anglesAndDirectionalChangesMap, localSpeedMap,
		        localVelocityMap, meanSpeedMap, meanVelocityMap, ny, mu, logMu,
		        deltaT, logDeltaT, gammaD, gammaDLog, gamma, gammaLog, smss,
		        smssLog);
		particleLinkingRun.addAnalysisRun(trackingMeasuresRun);
		this.loadedAnalysisRuns.add(trackingMeasuresRun);
	}

	private void handlePluginEventGateway(final OmegaPluginEventGateway event) {
		// TODO to check why replicated in 2 places
		switch (event.getStatus()) {
		case OmegaPluginEventGateway.STATUS_CREATED:
			this.gateway = ((OmegaLoaderPlugin) event.getSource()).getGateway();
			this.experimenter = null;
			break;
		case OmegaPluginEventGateway.STATUS_DESTROYED:
			this.gateway = null;
			this.experimenter = null;
			break;
		case OmegaPluginEventGateway.STATUS_CONNECTED:
			this.gateway = ((OmegaLoaderPlugin) event.getSource()).getGateway();
			this.experimenter = event.getExperimenter();
			break;
		case OmegaPluginEventGateway.STATUS_DISCONNECTED:
			this.experimenter = null;
			break;
		}

		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin instanceof OmegaLoaderPluginInterface) {
				((OmegaLoaderPluginInterface) plugin).setGateway(this.gateway);
			}
		}
	}

	private void handlePluginEventSelectionImage(
	        final OmegaPluginEventSelectionImage event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (event.getSource().equals(plugin)) {
				continue;
			}
			if (plugin instanceof OmegaSelectImagePluginInterface) {
				((OmegaSelectImagePluginInterface) plugin).selectImage(event
				        .getImage());
			}
		}
		this.gui.selectImage(event.getImage());
	}

	private void handlePluginEventSelectionAnalysisRun(
	        final OmegaPluginEventSelectionAnalysisRun event) {
		this.handlePluginEventSelectionAnalysisRunPlugins(event);
		this.handlePluginEventSelectionAnalysisRunGUI(event);
	}

	private void handlePluginEventSelectionAnalysisRunPlugins(
	        final OmegaPluginEventSelectionAnalysisRun event) {
		final OmegaAnalysisRun analysisRun = event.getAnalysisRun();
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (event.getSource().equals(plugin)) {
				continue;
			}
			if (event instanceof OmegaPluginEventSelectionTrajectoriesSegmentationRun) {
				if (plugin instanceof OmegaTrajectoriesSegmentationPlugin) {
					((OmegaTrajectoriesSegmentationPlugin) plugin)
					        .selectCurrentTrajectoriesSegmentationRun(event
					                .getAnalysisRun());
				}
			} else if (event instanceof OmegaPluginEventSelectionTrajectoriesRelinkingRun) {
				if (plugin instanceof OmegaTrajectoriesRelinkingPlugin) {
					((OmegaTrajectoriesRelinkingPlugin) plugin)
					        .selectCurrentTrajectoriesRelinkingRun(event
					                .getAnalysisRun());
				} else if (plugin instanceof OmegaTrajectoriesSegmentationPlugin) {
					((OmegaTrajectoriesSegmentationPlugin) plugin)
					        .selectTrajectoriesRelinkingRun(null);
				}
			} else if ((analysisRun instanceof OmegaTrajectoriesSegmentationRun)
			        && (plugin instanceof OmegaSelectTrajectoriesSegmentationRunPluginInterface)) {
				((OmegaSelectTrajectoriesSegmentationRunPluginInterface) plugin)
				        .selectTrajectoriesSegmentationRun((OmegaTrajectoriesSegmentationRun) event
				                .getAnalysisRun());
			} else if ((analysisRun instanceof OmegaTrajectoriesRelinkingRun)
			        && (plugin instanceof OmegaSelectTrajectoriesRelinkingRunPluginInterface)) {
				((OmegaSelectTrajectoriesRelinkingRunPluginInterface) plugin)
				        .selectTrajectoriesRelinkingRun((OmegaTrajectoriesRelinkingRun) event
				                .getAnalysisRun());
			} else if ((analysisRun instanceof OmegaParticleLinkingRun)
			        && (plugin instanceof OmegaSelectParticleLinkingRunPluginInterface)) {
				((OmegaSelectParticleLinkingRunPluginInterface) plugin)
				        .selectParticleLinkingRun((OmegaParticleLinkingRun) event
				                .getAnalysisRun());
			} else if ((analysisRun instanceof OmegaParticleDetectionRun)
			        && (plugin instanceof OmegaSelectParticleDetectionRunPluginInterface)) {
				((OmegaSelectParticleDetectionRunPluginInterface) plugin)
				        .selectParticleDetectionRun((OmegaParticleDetectionRun) event
				                .getAnalysisRun());
			}
		}
	}

	private void handlePluginEventSelectionAnalysisRunGUI(
	        final OmegaPluginEventSelectionAnalysisRun event) {
		final OmegaAnalysisRun analysisRun = event.getAnalysisRun();
		if (event instanceof OmegaPluginEventSelectionTrajectoriesSegmentationRun) {
			this.gui.selectCurrentTrajectoriesSegmentationRun(((OmegaPluginEventSelectionTrajectoriesSegmentationRun) event)
			        .getSegmentsMap());
		} else if (event instanceof OmegaPluginEventSelectionTrajectoriesRelinkingRun) {
			this.gui.selectCurrentTrajectoriesRelinkingRun(((OmegaPluginEventSelectionTrajectoriesRelinkingRun) event)
			        .getTrajectories());
		} else if (analysisRun instanceof OmegaTrajectoriesSegmentationRun) {
			this.gui.selectTrajectoriesSegmentationRun((OmegaTrajectoriesSegmentationRun) analysisRun);
		} else if (analysisRun instanceof OmegaTrajectoriesRelinkingRun) {
			this.gui.selectTrajectoriesRelinkingRun((OmegaTrajectoriesRelinkingRun) analysisRun);
		} else if (analysisRun instanceof OmegaParticleLinkingRun) {
			this.gui.selectParticleLinkingRun((OmegaParticleLinkingRun) analysisRun);
		} else if (analysisRun instanceof OmegaParticleDetectionRun) {
			this.gui.selectParticleDetectionRun((OmegaParticleDetectionRun) analysisRun);
		}
	}

	private void handlePluginEventTrajectories(
	        final OmegaPluginEventTrajectories event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin.equals(event.getSource())) {
				continue;
			}
			if (plugin instanceof OmegaTrajectoriesRelinkingPlugin) {
				((OmegaTrajectoriesRelinkingPlugin) plugin).updateTrajectories(
				        event.getTrajectories(), event.isSelectionEvent());
			} else if (plugin instanceof OmegaTrajectoriesSegmentationPlugin) {
				((OmegaTrajectoriesSegmentationPlugin) plugin)
				        .updateTrajectories(event.getTrajectories(),
				                event.isSelectionEvent());
			} else if (plugin instanceof OmegaTrackingMeasuresPlugin) {
				((OmegaTrackingMeasuresPlugin) plugin).updateTrajectories(
				        event.getTrajectories(), event.isSelectionEvent());
			}
		}
		this.gui.updateTrajectories(event.getTrajectories(),
		        event.isSelectionEvent());
	}

	private void handlePluginEventDataChanged(
	        final OmegaPluginEventDataChanged event) {
		if (event.getSource() instanceof OmegaBrowserPlugin) {
			this.updateGUI(event.getSource(), true);
		} else if (event.getSource() instanceof OmegaLoaderPlugin) {
			this.loadSelectedData(event.getSelectedData());
			this.updateGUI(event.getSource(),
			        event.getSelectedData().size() > 0);
		}
	}

	private void updateGUI(final OmegaPlugin source, final boolean dataLoaded) {
		if (dataLoaded) {
			for (final OmegaPlugin plugin : this.registeredPlugin) {
				if ((plugin instanceof OmegaBrowserPlugin)
				        && plugin.equals(source)) {
					continue;
				}
				if (plugin instanceof OmegaImageConsumerPluginInterface) {
					((OmegaImageConsumerPluginInterface) plugin)
					        .setLoadedImages(this.loadedData.getImages());
				}
				if (plugin instanceof OmegaLoadedAnalysisConsumerPluginInterface) {
					((OmegaLoadedAnalysisConsumerPluginInterface) plugin)
					        .setLoadedAnalysisRun(this.loadedAnalysisRuns);
				}
			}
			this.gui.updateGUI(this.loadedData, this.loadedAnalysisRuns,
			        this.gateway);
		}

		for (final OmegaPlugin plugin : this.registeredPlugin) {
			// if (dataLoaded && (plugin instanceof OmegaBrowserPlugin)) {
			// continue;
			// }
			if ((plugin instanceof OmegaBrowserPlugin) && plugin.equals(source)) {
				continue;
			}
			if (plugin instanceof OmegaDataDisplayerPluginInterface) {
				((OmegaDataDisplayerPluginInterface) plugin)
				        .updateDisplayedData();
			}
		}
	}

	public static void main(final String[] args) {
		final OmegaApplication instance = new OmegaApplication();
		instance.showGUI();
	}

	public void loadAnalysis() {
		final OmegaDBServerInformation serverInfo = this.gui
		        .getOmegaDBServerInformation();
		final OmegaLoginCredentials loginCred = this.gui
		        .getOmegaLoginCredentials();
		this.mysqlGateway.setServerInformation(serverInfo);
		this.mysqlGateway.setLoginCredentials(loginCred);

		try {
			this.mysqlGateway.connect();
		} catch (ClassNotFoundException | SQLException ex) {
			OmegaLogFileManager.handleCoreException(ex);
			// TODO manage the case somehow
			return;
		}

		this.dbDialog = new GenericMessageDialog(this.gui,
		        "Loading analysis from Omega server",
		        "Starting loading analysis from Omega Server", false);
		this.dbDialog.setVisible(true);
		this.gui.setEnabled(false);
		final OmegaDBLoader loader = new OmegaDBLoader(this, this.mysqlGateway,
		        this.dbDialog, this.omegaData.getProjects());
		this.dbThread = new Thread(loader);
		this.dbThread.start();

	}

	public void updateTrajectories() {
		final OmegaDBServerInformation serverInfo = this.gui
		        .getOmegaDBServerInformation();
		final OmegaLoginCredentials loginCred = this.gui
		        .getOmegaLoginCredentials();
		this.mysqlGateway.setServerInformation(serverInfo);
		this.mysqlGateway.setLoginCredentials(loginCred);

		try {
			this.mysqlGateway.connect();
		} catch (ClassNotFoundException | SQLException ex) {
			OmegaLogFileManager.handleCoreException(ex);
			// TODO manage the case somehow
			return;
		}

		this.dbDialog = new GenericMessageDialog(this.gui,
		        "Updating analysis in Omega server",
		        "Starting updating analysis in Omega Server", false);
		this.dbDialog.setVisible(true);
		this.gui.setEnabled(false);
		final OmegaDBUpdater updater = new OmegaDBUpdater(this,
		        this.mysqlGateway, this.dbDialog, this.omegaData.getProjects());
		this.dbThread = new Thread(updater);
		this.dbThread.start();

	}

	public void saveAnalysis() {
		final OmegaDBServerInformation serverInfo = this.gui
		        .getOmegaDBServerInformation();
		final OmegaLoginCredentials loginCred = this.gui
		        .getOmegaLoginCredentials();
		this.mysqlGateway.setServerInformation(serverInfo);
		this.mysqlGateway.setLoginCredentials(loginCred);
		final StringBuffer dialogTitleBuf = new StringBuffer();
		dialogTitleBuf.append("Saving analysis to Omega server");
		final StringBuffer dialogMsgBuf = new StringBuffer();

		try {
			this.mysqlGateway.connect();
		} catch (ClassNotFoundException | SQLException ex) {
			OmegaLogFileManager.handleCoreException(ex);
			dialogTitleBuf.append(" error");
			dialogMsgBuf.append("Unable to connect to the Omega server");
			// TODO add the possible errors checking the getCause of the
			// exception
			this.dbDialog = new GenericMessageDialog(this.gui,
			        dialogTitleBuf.toString(), dialogMsgBuf.toString(), true);
			this.dbDialog.setVisible(true);
			this.gui.setEnabled(false);
			return;
		}

		try {
			for (final OmegaSegmentationTypes segmTypes : this.omegaData
			        .getSegmentationTypesList()) {
				final int result = this.mysqlGateway
				        .isSegmentationTypesNameInDBWithDifferentValues(segmTypes);
				if (result == -1) {
					dialogTitleBuf.append(" error");
					dialogMsgBuf.append("SegmentationTypes ");
					dialogMsgBuf.append(segmTypes.getName());
					dialogMsgBuf
					        .append(" already present in db with different types");
					this.dbDialog = new GenericMessageDialog(this.gui,
					        dialogTitleBuf.toString(), dialogMsgBuf.toString(),
					        true);
					this.dbDialog.setVisible(true);
					this.gui.setEnabled(false);
					return;
				}
			}
		} catch (final SQLException ex) {
			OmegaLogFileManager.handleCoreException(ex);
			// TODO manage the case somehow
			return;
		}

		dialogMsgBuf.append("Starting saving analysis to Omega Server");
		this.dbDialog = new GenericMessageDialog(this.gui,
		        dialogTitleBuf.toString(), dialogMsgBuf.toString(), false);
		this.dbDialog.setVisible(true);
		this.gui.setEnabled(false);
		final OmegaDBSaver saver = new OmegaDBSaver(this, this.mysqlGateway,
		        this.dbDialog, this.omegaData.getProjects());
		this.dbThread = new Thread(saver);
		this.dbThread.start();
	}

	public void handleRunnableProcessTermination(final OmegaDBRunnable runnable) {
		this.dbDialog.enableClose();
		this.dbDialog.updateMessage("Operation completed");
		while (this.dbDialog.isVisible()) {
			// WAIT
		}
		this.gui.setEnabled(true);
		this.gui.setVisible(true);
		if (runnable instanceof OmegaDBWriter) {
			try {
				if (((OmegaDBWriter) runnable).isErrorOccured()) {
					this.mysqlGateway.rollback();
				} else {
					this.mysqlGateway.commit();
				}
			} catch (final SQLException ex) {
				OmegaLogFileManager.handleCoreException(ex);
				// TODO manage the case somehow
				return;
			}
		}

		try {
			this.mysqlGateway.disconnect();
		} catch (final SQLException ex) {
			OmegaLogFileManager.handleCoreException(ex);
			// TODO manage the case somehow
			return;
		}

		try {
			this.dbThread.join();
		} catch (final InterruptedException ex) {
			OmegaLogFileManager.handleCoreException(ex);
			// TODO manage the case somehow
			return;
		}

		if (runnable instanceof OmegaDBLoader) {
			this.omegaData.updateSegmentationTypes();
			for (final OmegaPlugin plugin : this.registeredPlugin) {
				if (plugin instanceof OmegaTrajectoriesSegmentationPlugin) {
					final OmegaTrajectoriesSegmentationPlugin tmPlugin = (OmegaTrajectoriesSegmentationPlugin) plugin;
					tmPlugin.updateSegmentationTypesList(new ArrayList<OmegaSegmentationTypes>(
					        this.omegaData.getSegmentationTypesList()));
				}
			}
		}
	}
}
