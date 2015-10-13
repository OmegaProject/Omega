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

import edu.umassmed.omega.commons.OmegaGenericApplication;
import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.data.OmegaData;
import edu.umassmed.omega.commons.data.OmegaLoadedData;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAlgorithmInformation;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAlgorithmSpecification;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaSNRRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresDiffusivityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresIntensityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresMobilityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresVelocityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.data.coreElements.OmegaDataset;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaExperimenter;
import edu.umassmed.omega.commons.data.coreElements.OmegaFrame;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.coreElements.OmegaProject;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaDBServerInformation;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaLoginCredentials;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.data.utilities.OmegaTracksImporter;
import edu.umassmed.omega.commons.eventSystem.OmegaImporterEventListener;
import edu.umassmed.omega.commons.eventSystem.OmegaPluginEventListener;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSelectionImage;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSelectionTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSelectionTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventTrajectories;
import edu.umassmed.omega.commons.eventSystem.events.OmegaImporterEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaImporterEventResultsParticleTracking;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventAlgorithm;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventDataChanged;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventGateway;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventPreviewParticleDetection;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsParticleDetection;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsParticleLinking;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsParticleTracking;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsSNR;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrackingMeasuresDiffusivity;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrackingMeasuresIntensity;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrackingMeasuresMobility;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrackingMeasuresVelocity;
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
import edu.umassmed.omega.commons.plugins.OmegaStatsPlugin;
import edu.umassmed.omega.commons.plugins.OmegaTrajectoriesRelinkingPlugin;
import edu.umassmed.omega.commons.plugins.OmegaTrajectoriesSegmentationPlugin;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaImageConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoadedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoadedDataConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoaderPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaMainDataConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaOrphanedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectImagePluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleDetectionRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleLinkingRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesRelinkingRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesSegmentationRunPluginInterface;
import edu.umassmed.omega.commons.utilities.OmegaAlgorithmsUtilities;
import edu.umassmed.omega.commons.utilities.OmegaImporter;
import edu.umassmed.omega.core.gui.OmegaGUIFrame;
import edu.umassmed.omega.core.runnables.OmegaDBLoader;
import edu.umassmed.omega.core.runnables.OmegaDBRunnable;
import edu.umassmed.omega.core.runnables.OmegaDBSaver;
import edu.umassmed.omega.core.runnables.OmegaDBUpdater;
import edu.umassmed.omega.core.runnables.OmegaDBWriter;
import edu.umassmed.omega.core.runnables.OmegaTrackingMeasuresAnalizer;
import edu.umassmed.omega.omegaDataBrowserPlugin.OmegaDataBrowserPlugin;
import edu.umassmed.omega.omeroPlugin.OmeroPlugin;
import edu.umassmed.omega.plSbalzariniPlugin.PLPlugin;
import edu.umassmed.omega.sdSbalzariniPlugin.SDPlugin;
import edu.umassmed.omega.snrSbalzariniPlugin.SNRPlugin;
import edu.umassmed.omega.sptSbalzariniPlugin.SPTPlugin;
import edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.TMDPlugin;
import edu.umassmed.omega.trackingMeasuresIntensityPlugin.TMIPlugin;
import edu.umassmed.omega.trackingMeasuresMobilityPlugin.TMMPlugin;
import edu.umassmed.omega.trackingMeasuresVelocityPlugin.TMVPlugin;
import edu.umassmed.omega.trajectoriesRelinkingPlugin.TrajectoriesRelinkingPlugin;
import edu.umassmed.omega.trajectoriesSegmentationPlugin.TrajectoriesSegmentationPlugin;

public class OmegaApplication extends OmegaGenericApplication implements
OmegaPluginEventListener, OmegaImporterEventListener {

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

	private final List<OmegaImporter> registeredImporter;

	private final OmegaLogFileManager logFileManager;

	private final OmegaOptionsFileManager optionsFileManager;
	private final Map<String, Map<String, String>> generalOptions;

	private final OmegaMySqlGateway mysqlGateway;

	private GenericMessageDialog dbDialog;
	private Thread dbThread;

	public OmegaApplication() {
		this.registeredImporter = new ArrayList<>();
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
		this.registerImporters();
		OmegaLogFileManager.markPluginsNewRun(this.registeredPlugin);

		this.gui = new OmegaGUIFrame(this);
		this.gui.initialize(this.pluginIndexMap);
		this.gui.setSize(1200, 800);

	}

	private void registerImporters() {
		this.registerImporter(new OmegaTracksImporter());
	}

	private void registerCorePlugins() {
		this.registerPlugin(new OmeroPlugin());
		this.registerPlugin(new SPTPlugin());
		this.registerPlugin(new SDPlugin());
		this.registerPlugin(new PLPlugin());
		this.registerPlugin(new SNRPlugin());
		this.registerPlugin(new TrajectoriesRelinkingPlugin());
		this.registerPlugin(new TrajectoriesSegmentationPlugin());
		// this.registerPlugin(new TrackingMeasuresPlugin());
		this.registerPlugin(new TMIPlugin());
		this.registerPlugin(new TMMPlugin());
		this.registerPlugin(new TMVPlugin());
		this.registerPlugin(new TMDPlugin());
		this.registerPlugin(new OmegaDataBrowserPlugin());

		for (final OmegaPlugin plugin : this.registeredPlugin) {
			final String optionsCategory = plugin.getOptionsCategory();
			final Map<String, String> pluginOptions = this.optionsFileManager
			        .getOptions(optionsCategory);
			plugin.addPluginOptions(pluginOptions);
			if (plugin instanceof OmegaOrphanedAnalysisConsumerPluginInterface) {
				((OmegaOrphanedAnalysisConsumerPluginInterface) plugin)
				        .setOrphanedAnalysis(this.omegaData
				                .getOrphanedContainer());
			}
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

	private void registerImporter(final OmegaImporter importer) {
		importer.addOmegaImporterListener(this);
		// final String name = importer.getName();
		// final long index = this.pluginIndex;
		// this.pluginIndex++;
		// this.pluginIndexMap.put(index, plugin);
		// this.pluginIndexes.put(name, index);
		this.registeredImporter.add(importer);
	}

	private void registerPlugin(final OmegaPlugin plugin) {
		plugin.addOmegaPluginListener(this);
		final String name = plugin.getName();
		final long index = this.pluginIndex;
		this.pluginIndex++;
		this.pluginIndexMap.put(index, plugin);
		this.pluginIndexes.put(name, index);
		this.registeredPlugin.add(plugin);
	}

	protected void showGUI() {
		this.gui.setVisible(true);
		this.gui.reinitializeStrings();
	}

	@Override
	public void quit() {
		this.gui.handleQuit();
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
			} else if (plugin instanceof OmegaStatsPlugin) {
				((OmegaStatsPlugin) plugin).updateTrajectories(
				        event.getTrajectories(), event.isSelectionEvent());
			}
		}
	}

	@Override
	public void handleImporterEvent(final OmegaImporterEvent event) {
		if (event instanceof OmegaImporterEventResultsParticleTracking) {
			this.handleImporterEventResultsParticleTracking((OmegaImporterEventResultsParticleTracking) event);
		}
	}

	public void handleImporterEventResultsParticleTracking(
	        final OmegaImporterEventResultsParticleTracking event) {
		OmegaExperimenter exp = this.experimenter;
		if (exp == null) {
			exp = OmegaConstants.OMEGA_DEFAULT_EXPERIMENTER;
		}
		final OmegaParticleDetectionRun detRun = new OmegaParticleDetectionRun(
		        exp, OmegaAlgorithmsUtilities.DEFAULT_IMPORTER_SPEC,
		        event.getResultingParticles(),
		        event.getResultingParticlesValues());
		final OmegaParticleLinkingRun linkRun = new OmegaParticleLinkingRun(
		        exp, OmegaAlgorithmsUtilities.DEFAULT_IMPORTER_SPEC,
		        event.getResultingTrajectories());

		detRun.addAnalysisRun(linkRun);
		this.loadedAnalysisRuns.add(linkRun);
		this.omegaData.addOrphanedAnalysis(detRun);
		this.loadedAnalysisRuns.add(detRun);

		final OmegaAlgorithmSpecification defaultRelinkingAlgoSpec = OmegaAlgorithmsUtilities
				.getDefaultRelinkingAlgorithmSpecification();
		final OmegaAnalysisRun defaultRelinkingRun = new OmegaTrajectoriesRelinkingRun(
		        exp, defaultRelinkingAlgoSpec, "Default relinking run",
		        event.getResultingTrajectories());
		this.loadedAnalysisRuns.add(defaultRelinkingRun);
		linkRun.addAnalysisRun(defaultRelinkingRun);

		final OmegaAlgorithmSpecification defaultSegmentationAlgoSpec = OmegaAlgorithmsUtilities
				.getDefaultSegmentationAlgorithmSpecification();
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = OmegaAlgorithmsUtilities
				.createDefaultSegmentation(event.getResultingTrajectories());
		final OmegaAnalysisRun defaultSegmentationRun = new OmegaTrajectoriesSegmentationRun(
		        exp, defaultSegmentationAlgoSpec, "Default segmentation run",
		        segmentsMap,
				OmegaSegmentationTypes.getDefaultSegmentationTypes());
		this.loadedAnalysisRuns.add(defaultSegmentationRun);
		defaultRelinkingRun.addAnalysisRun(defaultSegmentationRun);

		int maxT = -1;
		for (final OmegaFrame f : event.getResultingParticles().keySet()) {
			final int index = f.getIndex() + 1;
			if (maxT < index) {
				maxT = index;
			}
		}

		if (maxT != -1) {
			this.handleTrackingMeasures(
			        (OmegaTrajectoriesSegmentationRun) defaultSegmentationRun,
			        maxT, false);
		}

		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin instanceof OmegaOrphanedAnalysisConsumerPluginInterface) {
				((OmegaOrphanedAnalysisConsumerPluginInterface) plugin)
				        .setOrphanedAnalysis(this.omegaData
				                .getOrphanedContainer());
			}
			if (plugin instanceof OmegaDataDisplayerPluginInterface) {
				((OmegaDataDisplayerPluginInterface) plugin)
				        .updateDisplayedData();
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

		final OmegaAlgorithmInformation algoInfo = source
				.getAlgorithmInformation();
		final OmegaAlgorithmSpecification algoSpec = new OmegaAlgorithmSpecification(
				algoInfo);
		if (event.getParameters() != null) {
			for (final OmegaParameter param : event.getParameters()) {
				algoSpec.addParameter(param);
			}
		}

		// TODO separare i parameteri e fare 2 algoSpec diverse
		// capire se necessario

		OmegaAnalysisRun analysisRun;
		if (event instanceof OmegaPluginEventResultsTrackingMeasuresDiffusivity) {
			analysisRun = this
					.handlePluginEventAlgorithmResultsTrackingMeasuresDiffusivity(
							algoSpec, event);
		} else if (event instanceof OmegaPluginEventResultsTrackingMeasuresVelocity) {
			analysisRun = this
					.handlePluginEventAlgorithmResultsTrackingMeasuresVelocity(
							algoSpec, event);
		} else if (event instanceof OmegaPluginEventResultsTrackingMeasuresMobility) {
			analysisRun = this
					.handlePluginEventAlgorithmResultsTrackingMeasuresMobility(
							algoSpec, event);
		} else if (event instanceof OmegaPluginEventResultsTrackingMeasuresIntensity) {
			analysisRun = this
					.handlePluginEventAlgorithmResultsTrackingMeasuresIntensity(
							algoSpec, event);
		} else if (event instanceof OmegaPluginEventResultsSNR) {
			analysisRun = this.handlePluginEventAlgorithmResultsSNR(algoSpec,
					event);
		} else if (event instanceof OmegaPluginEventResultsTrajectoriesSegmentation) {
			analysisRun = this
					.handlePluginEventAlgorithmResultsTrackSegmentationRun(
							algoSpec, event);
		} else if (event instanceof OmegaPluginEventResultsTrajectoriesRelinking) {
			analysisRun = this
					.handlePluginEventAlgorithmResultsTrackRelinkingRun(
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
		} else if (event instanceof OmegaPluginEventPreviewParticleDetection) {
			this.gui.updateParticles(((OmegaPluginEventPreviewParticleDetection) event)
					.getResultingParticles());
			return;
		} else
			// TODO gestire errore
			return;

		((OmegaAnalysisRunContainer) element).addAnalysisRun(analysisRun);
		this.loadedAnalysisRuns.add(analysisRun);

		this.updateGUI(event.getSource(), true);
	}

	private OmegaTrackingMeasuresDiffusivityRun handlePluginEventAlgorithmResultsTrackingMeasuresDiffusivity(
	        final OmegaAlgorithmSpecification algoSpec,
	        final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrackingMeasuresDiffusivity specificEvent = (OmegaPluginEventResultsTrackingMeasuresDiffusivity) event;
		return new OmegaTrackingMeasuresDiffusivityRun(this.experimenter,
		        algoSpec, specificEvent.getResultingSegments(),
				specificEvent.getResultingNy(), specificEvent.getResultingMu(),
				specificEvent.getResultingLogMu(),
				specificEvent.getResultingDeltaT(),
				specificEvent.getResultingLogDeltaT(),
				specificEvent.getResultingGammaD(),
				specificEvent.getResultingGammaDFromLog(),
				specificEvent.getResultingGamma(),
				specificEvent.getResultingGammaFromLog(),
				specificEvent.getResultingSmss(),
				specificEvent.getResultingSmssFromLog());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsTrackingMeasuresVelocity(
	        final OmegaAlgorithmSpecification algoSpec,
	        final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrackingMeasuresVelocity specificEvent = (OmegaPluginEventResultsTrackingMeasuresVelocity) event;
		return new OmegaTrackingMeasuresVelocityRun(this.experimenter,
		        algoSpec, specificEvent.getResultingSegments(),
				specificEvent.getResultingLocalSpeed(),
		        specificEvent.getResultingLocalVelocity(),
				specificEvent.getResultingMeanSpeed(),
				specificEvent.getResultingMeanVelocity());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsTrackingMeasuresMobility(
	        final OmegaAlgorithmSpecification algoSpec,
	        final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrackingMeasuresMobility specificEvent = (OmegaPluginEventResultsTrackingMeasuresMobility) event;
		return new OmegaTrackingMeasuresMobilityRun(this.experimenter,
				algoSpec, specificEvent.getResultingSegments(),
				specificEvent.getResultingDistances(),
		        specificEvent.getResultingDisplacements(),
				specificEvent.getResultingMaxDisplacements(),
				specificEvent.getResultingTotalTimeTraveled(),
				specificEvent.getResultingConfinementRatio(),
				specificEvent.getResultingAnglesAndDirectionalChanges());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsTrackingMeasuresIntensity(
	        final OmegaAlgorithmSpecification algoSpec,
	        final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrackingMeasuresIntensity specificEvent = (OmegaPluginEventResultsTrackingMeasuresIntensity) event;
		return new OmegaTrackingMeasuresIntensityRun(this.experimenter,
				algoSpec, specificEvent.getResultingSegments(),
				specificEvent.getResultingPeakSignals(),
				specificEvent.getResultingMeanSignals(),
				specificEvent.getResultingLocalBackgrounds(),
				specificEvent.getResultingLocalSNRs());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsSNR(
	        final OmegaAlgorithmSpecification algoSpec,
	        final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsSNR specificEvent = (OmegaPluginEventResultsSNR) event;
		return new OmegaSNRRun(this.experimenter, algoSpec,
		        specificEvent.getResultingImageNoise(),
		        specificEvent.getResultingImageBGR(),
		        specificEvent.getResultingLocalCenterSignals(),
		        specificEvent.getResultingLocalMeanSignals(),
		        specificEvent.getResultingLocalSignalSizes(),
		        specificEvent.getResultingLocalPeakSignals(),
		        specificEvent.getResultingLocalNoises(),
		        specificEvent.getResultingLocalSNRs());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsTrackSegmentationRun(
	        final OmegaAlgorithmSpecification algoSpec,
	        final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrajectoriesSegmentation specificEvent = (OmegaPluginEventResultsTrajectoriesSegmentation) event;
		return new OmegaTrajectoriesSegmentationRun(this.experimenter,
		        algoSpec, specificEvent.getResultingSegments(),
		        specificEvent.getSegmentationTypes());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsTrackRelinkingRun(
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
		final List<OmegaTrajectory> resultingTrajectories = specificEvent
				.getResultingTrajectories();
		final OmegaParticleLinkingRun particleLinkingRun = new OmegaParticleLinkingRun(
				this.experimenter, algoSpec, resultingTrajectories);
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
		return particleLinkingRun;
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsParticleDetectionRun(
	        final OmegaAlgorithmSpecification algoSpec,
	        final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsParticleDetection specificEvent = (OmegaPluginEventResultsParticleDetection) event;
		return new OmegaParticleDetectionRun(this.experimenter, algoSpec,
		        specificEvent.getResultingParticles(),
				specificEvent.getResultingParticlesValues());
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
		final Map<OmegaROI, Map<String, Object>> resultingParticlesValues = specificEvent
				.getResultingParticlesValues();

		final OmegaAnalysisRun particleDetectionRun = new OmegaParticleDetectionRun(
				this.experimenter, algoSpec, resultingParticles,
				resultingParticlesValues);
		final OmegaAnalysisRun particleLinkingRun = new OmegaParticleLinkingRun(
				this.experimenter, algoSpec, resultingTrajectories);
		this.loadedAnalysisRuns.add(particleLinkingRun);
		particleDetectionRun.addAnalysisRun(particleLinkingRun);

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

		if (event.getElement() instanceof OmegaImage) {
			final OmegaImage img = (OmegaImage) event.getElement();
			final int maxT = img.getDefaultPixels().getSizeT();
			this.handleTrackingMeasures(
					(OmegaTrajectoriesSegmentationRun) defaultSegmentationRun,
			        maxT, true);
		}

		return particleDetectionRun;
	}

	private void handleTrackingMeasures(
	        final OmegaTrajectoriesSegmentationRun segmentationRun,
	        final int maxT, final boolean hasIntensities) {
		// TODO FIX HERE calling the separates analizer or making a new common
		// analizer
		final OmegaTrackingMeasuresAnalizer trackingMeasuresAnalizer = new OmegaTrackingMeasuresAnalizer(
		        this, segmentationRun, maxT, hasIntensities);
		final Thread t = new Thread(trackingMeasuresAnalizer);
		t.setName("TrackingMeasuresAnalizer");
		t.start();
	}

	// TODO to be changed somehow
	public void updateTrackingMeasuresAnalizerResults(
			final OmegaTrajectoriesSegmentationRun segmentationRun,
			final Map<OmegaSegment, Double[]> peakSignalsMap,
			final Map<OmegaSegment, Double[]> meanSignalsMap,
			final Map<OmegaSegment, Double[]> localBackgroundsMap,
			final Map<OmegaSegment, Double[]> localSNRsMap,
			final Map<OmegaSegment, List<Double>> distancesMap,
			final Map<OmegaSegment, List<Double>> displacementsMap,
			final Map<OmegaSegment, Double> maxDisplacementsMap,
			final Map<OmegaSegment, Integer> totalTimeTraveledMap,
			final Map<OmegaSegment, List<Double>> confinementRatioMap,
			final Map<OmegaSegment, List<Double[]>> anglesAndDirectionalChangesMap,
			final Map<OmegaSegment, List<Double>> localSpeedMap,
			final Map<OmegaSegment, List<Double>> localVelocityMap,
			final Map<OmegaSegment, Double> meanSpeedMap,
			final Map<OmegaSegment, Double> meanVelocityMap,
			final Map<OmegaSegment, Double[]> ny,
			final Map<OmegaSegment, Double[][]> mu,
			final Map<OmegaSegment, Double[][]> logMu,
			final Map<OmegaSegment, Double[][]> deltaT,
			final Map<OmegaSegment, Double[][]> logDeltaT,
			final Map<OmegaSegment, Double[][]> gammaD,
			final Map<OmegaSegment, Double[][]> gammaDLog,
			final Map<OmegaSegment, Double[]> gamma,
			final Map<OmegaSegment, Double[]> gammaLog,
			final Map<OmegaSegment, Double[]> smss,
			final Map<OmegaSegment, Double[]> smssLog) {
		final OmegaAlgorithmSpecification defaultTrackingMeasuresSpec = OmegaAlgorithmsUtilities
				.getDefaultTrackingMeasuresSpecification();
		OmegaExperimenter exp = this.experimenter;
		if (exp == null) {
			exp = OmegaConstants.OMEGA_DEFAULT_EXPERIMENTER;
		}

		final OmegaTrackingMeasuresIntensityRun intensityRun = new OmegaTrackingMeasuresIntensityRun(
				this.experimenter, defaultTrackingMeasuresSpec,
				segmentationRun.getResultingSegments(), peakSignalsMap,
				meanSignalsMap, localBackgroundsMap, localSNRsMap);
		segmentationRun.addAnalysisRun(intensityRun);
		this.loadedAnalysisRuns.add(intensityRun);

		final OmegaTrackingMeasuresMobilityRun mobilityRun = new OmegaTrackingMeasuresMobilityRun(
				this.experimenter, defaultTrackingMeasuresSpec,
				segmentationRun.getResultingSegments(), distancesMap,
				displacementsMap, maxDisplacementsMap, totalTimeTraveledMap,
				confinementRatioMap, anglesAndDirectionalChangesMap);
		segmentationRun.addAnalysisRun(mobilityRun);
		this.loadedAnalysisRuns.add(mobilityRun);

		final OmegaTrackingMeasuresVelocityRun velocityRun = new OmegaTrackingMeasuresVelocityRun(
				this.experimenter, defaultTrackingMeasuresSpec,
				segmentationRun.getResultingSegments(), localSpeedMap,
				localVelocityMap, meanSpeedMap, meanVelocityMap);
		segmentationRun.addAnalysisRun(velocityRun);
		this.loadedAnalysisRuns.add(velocityRun);

		final OmegaTrackingMeasuresDiffusivityRun diffusivityRun = new OmegaTrackingMeasuresDiffusivityRun(
				this.experimenter, defaultTrackingMeasuresSpec,
				segmentationRun.getResultingSegments(), ny, mu, logMu, deltaT,
				logDeltaT, gammaD, gammaDLog, gamma, gammaLog, smss, smssLog);
		segmentationRun.addAnalysisRun(diffusivityRun);
		this.loadedAnalysisRuns.add(diffusivityRun);
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
			} else if (plugin instanceof OmegaStatsPlugin) {
				((OmegaStatsPlugin) plugin).updateTrajectories(
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
			this.gui.updateGUI(this.loadedData,
					this.omegaData.getOrphanedContainer(),
					this.loadedAnalysisRuns, this.gateway);
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

	public void showTracksImporter() {
		// TODO to find a better way
		final OmegaTracksImporter oti = (OmegaTracksImporter) this.registeredImporter
		        .get(0);
		oti.showDialog(this.gui);
	}
}
