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
package edu.umassmed.omega.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.OmegaGenericApplication;
import edu.umassmed.omega.commons.OmegaImageManager;
import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.data.OmegaData;
import edu.umassmed.omega.commons.data.OmegaLoadedData;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAlgorithmInformation;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainerInterface;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaRunDefinition;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaSNRRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresDiffusivityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresIntensityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresMobilityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresVelocityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OrphanedAnalysisContainer;
import edu.umassmed.omega.commons.data.coreElements.OmegaDataset;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaExperimenter;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.coreElements.OmegaPerson;
import edu.umassmed.omega.commons.data.coreElements.OmegaPlane;
import edu.umassmed.omega.commons.data.coreElements.OmegaProject;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaDBServerInformation;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaLoginCredentials;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.eventSystem.OmegaPluginEventListener;
import edu.umassmed.omega.commons.eventSystem.OmegaTrajectoryIOEventListener;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSegments;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSelectionImage;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventTrajectories;
import edu.umassmed.omega.commons.eventSystem.events.OmegaImporterEventResultsParticleDetection;
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
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSegments;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionImage;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventTrajectories;
import edu.umassmed.omega.commons.eventSystem.events.OmegaTrajectoryIOEvent;
import edu.umassmed.omega.commons.gui.dialogs.GenericProgressDialog;
import edu.umassmed.omega.commons.plugins.OmegaAlgorithmPlugin;
import edu.umassmed.omega.commons.plugins.OmegaBrowserPlugin;
import edu.umassmed.omega.commons.plugins.OmegaLoaderPlugin;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
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
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectSNRRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectSegmentsInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrackingMeasuresRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesRelinkingRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesSegmentationRunPluginInterface;
import edu.umassmed.omega.commons.trajectoryTool.OmegaDataToolConstants;
import edu.umassmed.omega.commons.trajectoryTool.OmegaTracksExporter;
import edu.umassmed.omega.commons.trajectoryTool.OmegaTracksImporter;
import edu.umassmed.omega.commons.trajectoryTool.gui.OmegaTracksToolTargetSelectorDialog;
import edu.umassmed.omega.commons.utilities.OmegaAlgorithmsUtilities;
import edu.umassmed.omega.commons.utilities.OmegaIOUtility;
import edu.umassmed.omega.core.gui.OmegaGUIFrame;
import edu.umassmed.omega.core.mysql.OmegaMySqlReader;
import edu.umassmed.omega.core.mysql.OmegaMySqlWriter;
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
import edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.TMDPlugin;
import edu.umassmed.omega.trackingMeasuresIntensityPlugin.TMIPlugin;
import edu.umassmed.omega.trackingMeasuresMobilityPlugin.TMMPlugin;
import edu.umassmed.omega.trackingMeasuresVelocityPlugin.TMVPlugin;
import edu.umassmed.omega.trajectoriesRelinkingPlugin.TrajectoriesRelinkingPlugin;
import edu.umassmed.omega.trajectoriesSegmentationPlugin.TrajectoriesSegmentationPlugin;

public class OmegaApplication extends OmegaGenericApplication implements
		OmegaPluginEventListener, OmegaTrajectoryIOEventListener {
	
	public static final boolean ISDEBUG = false;
	
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
	
	private final List<OmegaIOUtility> registeredImporter;
	private final List<OmegaIOUtility> registeredExporter;
	private final int defaultImporterIndex, defaultExporterIndex;
	
	private final OmegaLogFileManager logFileManager;
	private final OmegaImageManager imageManager;
	
	private final OmegaOptionsFileManager optionsFileManager;
	private final Map<String, Map<String, String>> generalOptions;
	
	private final OmegaMySqlWriter mysqlWriter;
	private final OmegaMySqlReader mysqlReader;
	
	private GenericProgressDialog dbDialog;
	private Thread dbThread;
	
	public OmegaApplication() {
		this.registeredImporter = new ArrayList<>();
		this.registeredExporter = new ArrayList<>();
		this.pluginIndexes = new HashMap<>();
		this.registeredPlugin = new ArrayList<>();
		this.pluginIndexMap = new HashMap<>();
		this.pluginIndex = 0;
		this.defaultImporterIndex = 0;
		this.defaultExporterIndex = 0;
		
		this.logFileManager = OmegaLogFileManager.getOmegaLogFileManager(this);
		OmegaLogFileManager.markCoreNewRun();
		
		this.imageManager = OmegaImageManager.getOmegaImageManager(this);
		
		this.optionsFileManager = new OmegaOptionsFileManager();
		this.generalOptions = this.optionsFileManager.getGeneralOptions();
		
		this.mysqlWriter = new OmegaMySqlWriter();
		this.mysqlReader = new OmegaMySqlReader();
		
		this.omegaData = new OmegaData();
		this.loadedData = new OmegaLoadedData();
		this.loadedAnalysisRuns = new ArrayList<OmegaAnalysisRun>();
		this.gateway = null;
		this.experimenter = null;
		
		this.registerImporters();
		this.registerExporters();
		this.registerCorePlugins();
		OmegaLogFileManager.markPluginsNewRun(this.registeredPlugin);
		
		this.gui = new OmegaGUIFrame(this);
		
		this.gui.initialize(this.pluginIndexMap);
		this.gui.setSize(1200, 800);
		
	}
	
	private void registerImporters() {
		this.registerImporter(new OmegaTracksImporter());
		
	}
	
	private void registerExporters() {
		this.registerExporter(new OmegaTracksExporter());
	}
	
	private void registerCorePlugins() {
		this.registerPlugin(new OmeroPlugin());
		// this.registerPlugin(new SPTPlugin());
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
			
			if (plugin instanceof OmegaBrowserPlugin) {
				((OmegaBrowserPlugin) plugin)
						.setTracksImporter((OmegaTracksImporter) this.registeredImporter
								.get(this.defaultImporterIndex));
				((OmegaBrowserPlugin) plugin)
						.setTracksExporter((OmegaTracksExporter) this.registeredExporter
								.get(this.defaultExporterIndex));
			}
		}
	}
	
	private void registerImporter(final OmegaIOUtility tracksIOUtility) {
		tracksIOUtility.addOmegaImporterListener(this);
		// final String name = importer.getName();
		// final long index = this.pluginIndex;
		// this.pluginIndex++;
		// this.pluginIndexMap.put(index, plugin);
		// this.pluginIndexes.put(name, index);
		this.registeredImporter.add(tracksIOUtility);
	}
	
	private void registerExporter(final OmegaIOUtility tracksIOUtility) {
		tracksIOUtility.addOmegaImporterListener(this);
		// final String name = importer.getName();
		// final long index = this.pluginIndex;
		// this.pluginIndex++;
		// this.pluginIndexMap.put(index, plugin);
		// this.pluginIndexes.put(name, index);
		this.registeredExporter.add(tracksIOUtility);
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

	public void dispose() {
		if (this.gateway == null)
			return;
		try {
			this.gateway.disconnect();
		} catch (final Exception ex) {
			OmegaLogFileManager.handleCoreException(ex, false);
		}
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
			} else if ((element instanceof OmegaPlane)
					&& !this.loadedData.containsFrame((OmegaPlane) element)) {
				this.loadedData.addFrame((OmegaPlane) element);
			}
		}
	}
	
	public void handleCoreEvent(final OmegaCoreEvent event) {
		if (event instanceof OmegaCoreEventSegments) {
			this.handleCoreEventSegments((OmegaCoreEventSegments) event);
		} else if (event instanceof OmegaCoreEventTrajectories) {
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
			if ((analysisRun instanceof OmegaTrajectoriesSegmentationRun)
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
			if (plugin instanceof OmegaSelectTrajectoriesInterface) {
				((OmegaSelectTrajectoriesInterface) plugin).updateTrajectories(
						event.getTrajectories(), event.isSelectionEvent());
			}
		}
	}
	
	private void handleCoreEventSegments(final OmegaCoreEventSegments event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin instanceof OmegaSelectSegmentsInterface) {
				((OmegaSelectTrajectoriesInterface) plugin).updateTrajectories(
						new ArrayList<OmegaTrajectory>(event.getSegments()
								.keySet()), event.isSelectionEvent());
				((OmegaSelectSegmentsInterface) plugin).updateSegments(
						event.getSegments(), event.getSegmentationTypes(),
						event.isSelectionEvent());
			}
		}
	}
	
	@Override
	public void handleIOEvent(final OmegaTrajectoryIOEvent event) {
		if (event instanceof OmegaImporterEventResultsParticleTracking) {
			this.handleImporterEventResultsParticleTracking((OmegaImporterEventResultsParticleTracking) event);
		} else if (event instanceof OmegaImporterEventResultsParticleDetection) {
			this.handleImporterEventResultsParticleDetection((OmegaImporterEventResultsParticleDetection) event);
		}
	}
	
	private OmegaRunDefinition handleImporterEventRunDefinition(
			final Map<String, String> analysisData,
			final Map<String, String> paramsData) {
		final String algorithm = analysisData
				.get(OmegaDataToolConstants.ALGORITHM);
		final String author = analysisData.get(OmegaDataToolConstants.AUTHOR);
		final String version = analysisData.get(OmegaDataToolConstants.VERSION);
		final String published = analysisData
				.get(OmegaDataToolConstants.PUBLISHED);
		final String reference = analysisData
				.get(OmegaDataToolConstants.REFERENCE);
		final String description = analysisData
				.get(OmegaDataToolConstants.DESCRIPTION);
		
		final SimpleDateFormat format = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		Date publicationDate;
		try {
			publicationDate = format.parse(published);
		} catch (final ParseException e) {
			// FIXME todo log error
			publicationDate = Calendar.getInstance().getTime();
		}

		final OmegaAlgorithmInformation algoInfo = new OmegaAlgorithmInformation(
				algorithm, version, description, author, publicationDate,
				reference);

		final List<OmegaParameter> params = new ArrayList<OmegaParameter>();
		for (final String s : paramsData.keySet()) {
			final OmegaParameter param = new OmegaParameter(s,
					paramsData.get(s));
			params.add(param);
		}
		final OmegaRunDefinition runDef = new OmegaRunDefinition(algoInfo,
				params);
		return runDef;
	}

	public void handleImporterEventResultsParticleDetection(
			final OmegaImporterEventResultsParticleDetection event) {
		final OmegaAnalysisRunContainerInterface container = event.getContainer();
		final OmegaParticleDetectionRun detRun;
		if (event.getAnalysisData() != null) {
			final String runby = event.getAnalysisData().get(
					OmegaDataToolConstants.RUN_BY);
			final String[] runbyToks = runby.split(" ");
			final OmegaExperimenter exp = new OmegaExperimenter(runbyToks[0],
					runbyToks[1]);
			final String runName = event.getAnalysisData().get(
					OmegaDataToolConstants.RUN_NAME);
			final String runOn = event.getAnalysisData().get(
					OmegaDataToolConstants.RUN_ON);
			final OmegaRunDefinition runDef = this
					.handleImporterEventRunDefinition(event.getAnalysisData(),
							event.getParamData());
			final SimpleDateFormat format = new SimpleDateFormat(
					OmegaConstants.OMEGA_DATE_FORMAT);
			Date runDate;
			try {
				runDate = format.parse(runOn);
			} catch (final ParseException ex) {
				// FIXME log the error
				runDate = Calendar.getInstance().getTime();
			}
			
			detRun = new OmegaParticleDetectionRun(exp, runDef, runDate,
					runName, event.getResultingParticles(),
					event.getResultingParticlesValues());
		} else {
			OmegaExperimenter exp = this.experimenter;
			if (exp == null) {
				exp = OmegaConstants.OMEGA_DEFAULT_EXPERIMENTER;
			}
			detRun = new OmegaParticleDetectionRun(exp,
					OmegaAlgorithmsUtilities.DEFAULT_IMPORTER_SPEC,
					event.getResultingParticles(),
					event.getResultingParticlesValues());
		}

		if (container instanceof OrphanedAnalysisContainer) {
			this.omegaData.addOrphanedAnalysis(detRun);
		} else {
			container.addAnalysisRun(detRun);
		}
		this.loadedAnalysisRuns.add(detRun);
		
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if ((container instanceof OrphanedAnalysisContainer)
					&& (plugin instanceof OmegaOrphanedAnalysisConsumerPluginInterface)) {
				((OmegaOrphanedAnalysisConsumerPluginInterface) plugin)
						.setOrphanedAnalysis(this.omegaData
								.getOrphanedContainer());
			} else if (!(container instanceof OrphanedAnalysisContainer)
					&& (plugin instanceof OmegaLoadedAnalysisConsumerPluginInterface)) {
				((OmegaLoadedAnalysisConsumerPluginInterface) plugin)
						.setLoadedAnalysisRun(this.loadedAnalysisRuns);
			}
			if (plugin instanceof OmegaDataDisplayerPluginInterface) {
				((OmegaDataDisplayerPluginInterface) plugin)
						.updateDisplayedData();
			}
		}
	}
	
	public void handleImporterEventResultsParticleTracking(
			final OmegaImporterEventResultsParticleTracking event) {
		final OmegaAnalysisRunContainerInterface container = event.getContainer();
		final OmegaParticleDetectionRun detRun;
		final OmegaParticleLinkingRun linkRun;
		OmegaExperimenter exp;
		if ((event.getAnalysisData() != null)
				&& !event.getAnalysisData().isEmpty()) {
			final String runby = event.getAnalysisData().get(
					OmegaDataToolConstants.RUN_BY);
			final String[] runbyToks = runby.split(" ");
			exp = new OmegaExperimenter(runbyToks[0], runbyToks[1]);
			final String runName = event.getAnalysisData().get(
					OmegaDataToolConstants.RUN_NAME);
			final String runOn = event.getAnalysisData().get(
					OmegaDataToolConstants.RUN_ON);
			final OmegaRunDefinition runDef = this
					.handleImporterEventRunDefinition(event.getAnalysisData(),
							event.getParamData());
			final SimpleDateFormat format = new SimpleDateFormat(
					OmegaConstants.OMEGA_DATE_FORMAT);
			Date runDate;
			try {
				runDate = format.parse(runOn);
			} catch (final ParseException ex) {
				// FIXME log the error
				runDate = Calendar.getInstance().getTime();
			}
			
			detRun = new OmegaParticleDetectionRun(exp, runDef, runDate,
					runName, event.getResultingParticles(),
					event.getResultingParticlesValues());
			linkRun = new OmegaParticleLinkingRun(exp, runDef, runDate,
					runName, event.getResultingTrajectories());
		} else {
			exp = this.experimenter;
			if (exp == null) {
				exp = OmegaConstants.OMEGA_DEFAULT_EXPERIMENTER;
			}
			detRun = new OmegaParticleDetectionRun(exp,
					OmegaAlgorithmsUtilities.DEFAULT_IMPORTER_SPEC,
					event.getResultingParticles(),
					event.getResultingParticlesValues());
			linkRun = new OmegaParticleLinkingRun(exp,
					OmegaAlgorithmsUtilities.DEFAULT_IMPORTER_SPEC,
					event.getResultingTrajectories());
		}

		detRun.addAnalysisRun(linkRun);
		this.loadedAnalysisRuns.add(linkRun);
		if (container instanceof OrphanedAnalysisContainer) {
			this.omegaData.addOrphanedAnalysis(detRun);
		} else {
			container.addAnalysisRun(detRun);
		}
		this.loadedAnalysisRuns.add(detRun);
		
		final OmegaRunDefinition defaultRelinkingAlgoSpec = OmegaAlgorithmsUtilities
				.getDefaultRelinkingAlgorithmSpecification();
		final OmegaAnalysisRun defaultRelinkingRun = new OmegaTrajectoriesRelinkingRun(
				exp, defaultRelinkingAlgoSpec, "Default relinking run",
				event.getResultingTrajectories());
		this.loadedAnalysisRuns.add(defaultRelinkingRun);
		linkRun.addAnalysisRun(defaultRelinkingRun);
		
		final OmegaRunDefinition defaultSegmentationAlgoSpec = OmegaAlgorithmsUtilities
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
		final double physicalT = 1.0;
		for (final OmegaPlane f : event.getResultingParticles().keySet()) {
			final int index = f.getIndex();
			if (maxT < index) {
				maxT = index;
			}
		}
		
		if (maxT != -1) {
			this.handleTrackingMeasures(
					(OmegaTrajectoriesSegmentationRun) defaultSegmentationRun,
					physicalT, maxT);
		}
		
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if ((container instanceof OrphanedAnalysisContainer)
					&& (plugin instanceof OmegaOrphanedAnalysisConsumerPluginInterface)) {
				((OmegaOrphanedAnalysisConsumerPluginInterface) plugin)
						.setOrphanedAnalysis(this.omegaData
								.getOrphanedContainer());
			} else if (!(container instanceof OrphanedAnalysisContainer)
					&& (plugin instanceof OmegaLoadedAnalysisConsumerPluginInterface)) {
				((OmegaLoadedAnalysisConsumerPluginInterface) plugin)
						.setLoadedAnalysisRun(this.loadedAnalysisRuns);
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
		} else if (event instanceof OmegaPluginEventTrajectories) {
			this.handlePluginEventTrajectories((OmegaPluginEventTrajectories) event);
		} else if (event instanceof OmegaPluginEventSegments) {
			this.handlePluginEventSegments((OmegaPluginEventSegments) event);
		}
	}
	
	private void handlePluginEventAlgorithm(
			final OmegaPluginEventAlgorithm event) {

		final OmegaAlgorithmPlugin source = (OmegaAlgorithmPlugin) event
				.getSource();
		if (source == null)
			// TODO gestire errore
			return;
		if (this.experimenter == null) {
			this.experimenter = OmegaConstants.OMEGA_DEFAULT_EXPERIMENTER;
			// TODO should be handled differently
		}
		final OmegaElement element = event.getElement();
		final List<OmegaElement> selections = event.getSelections();
		if (!(element instanceof OmegaAnalysisRunContainerInterface))
			// TODO gestire errore
			return;
		
		final OmegaAlgorithmInformation algoInfo = source
				.getAlgorithmInformation();
		final OmegaRunDefinition algoSpec = new OmegaRunDefinition(algoInfo);
		if (event.getParameters() != null) {
			for (final OmegaParameter param : event.getParameters()) {
				algoSpec.addParameter(param);
			}
		}
		
		// TODO separare i parameteri e fare 2 algoSpec diverse
		// capire se necessario
		
		final OmegaAnalysisRun analysisRun;
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
		
		((OmegaAnalysisRunContainerInterface) element).addAnalysisRun(analysisRun);
		this.loadedAnalysisRuns.add(analysisRun);
		this.omegaData.consolidateData();
		this.updateGUI(event.getSource(), true);
		// this.updateSelectionAfterAlgorithmResults(selections, analysisRun);
		for (final OmegaElement selection : selections) {
			if ((selection instanceof OmegaImage)
					|| (selection instanceof OrphanedAnalysisContainer)) {
				this.handlePluginEventSelectionImage(new OmegaPluginEventSelectionImage(
						(OmegaAnalysisRunContainerInterface) selection));
				
			} else if (selection instanceof OmegaAnalysisRun) {
				this.handlePluginEventSelectionAnalysisRun(new OmegaPluginEventSelectionAnalysisRun(
						(OmegaAnalysisRun) selection));
				// this.handleCoreEventSelectionAnalysisRun(new
				// OmegaCoreEventSelectionAnalysisRun(
				// (OmegaAnalysisRun) selection));
			}
		}
		this.handlePluginEventSelectionAnalysisRun(new OmegaPluginEventSelectionAnalysisRun(
				analysisRun));
		// this.handleCoreEventSelectionAnalysisRun(new
		// OmegaCoreEventSelectionAnalysisRun(
		// analysisRun));
	}

	private OmegaTrackingMeasuresDiffusivityRun handlePluginEventAlgorithmResultsTrackingMeasuresDiffusivity(
			final OmegaRunDefinition algoSpec,
			final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrackingMeasuresDiffusivity specificEvent = (OmegaPluginEventResultsTrackingMeasuresDiffusivity) event;
		return new OmegaTrackingMeasuresDiffusivityRun(
				this.experimenter,
				algoSpec,
				specificEvent.getResultingSegments(),
				specificEvent.getResultingNy(),
				specificEvent.getResultingMu(),
				specificEvent.getResultingLogMu(),
				specificEvent.getResultingDeltaT(),
				specificEvent.getResultingLogDeltaT(),
				specificEvent.getResultingGammaD(),
				specificEvent.getResultingGammaDFromLog(),
				// specificEvent.getResultingGamma(),
				specificEvent.getResultingGammaFromLog(),
				// specificEvent.getResultingSmss(),
				specificEvent.getResultingSmssFromLog(),
				// specificEvent.getErrors(),
				specificEvent.getErrorsFromLog(),
				specificEvent.getMinimumDetectableODC(),
				specificEvent.getSNRRun(),
				specificEvent.getTrackigMeasuresDiffusivityRun());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsTrackingMeasuresVelocity(
			final OmegaRunDefinition algoSpec,
			final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrackingMeasuresVelocity specificEvent = (OmegaPluginEventResultsTrackingMeasuresVelocity) event;
		return new OmegaTrackingMeasuresVelocityRun(this.experimenter,
				algoSpec, specificEvent.getResultingSegments(),
				specificEvent.getResultingLocalSpeed(),
				specificEvent.getResultingLocalSpeedFromOrigin(),
				specificEvent.getResultingLocalVelocityFromOrigin(),
				specificEvent.getResultingAverageCurvilinearSpeed(),
				specificEvent.getResultingAverageStraightLineVelocity(),
				specificEvent.getResultingForwardProgressionLinearity());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsTrackingMeasuresMobility(
			final OmegaRunDefinition algoSpec,
			final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrackingMeasuresMobility specificEvent = (OmegaPluginEventResultsTrackingMeasuresMobility) event;
		return new OmegaTrackingMeasuresMobilityRun(this.experimenter,
				algoSpec, specificEvent.getResultingSegments(),
				specificEvent.getResultingDistances(),
				specificEvent.getResultingDistancesFromOrigin(),
				specificEvent.getResultingDisplacementsFromOrigin(),
				specificEvent.getResultingMaxDisplacementsFromOrigin(),
				specificEvent.getResultingTimeTraveled(),
				specificEvent.getResultingConfinementRatio(),
				specificEvent.getResultingAnglesAndDirectionalChanges());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsTrackingMeasuresIntensity(
			final OmegaRunDefinition algoSpec,
			final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrackingMeasuresIntensity specificEvent = (OmegaPluginEventResultsTrackingMeasuresIntensity) event;
		return new OmegaTrackingMeasuresIntensityRun(this.experimenter,
				algoSpec, specificEvent.getResultingSegments(),
				specificEvent.getResultingPeakSignals(),
				specificEvent.getResultingCentroidSignals(),
				specificEvent.getResultingPeakSignalsLocal(),
				specificEvent.getResultingCentroidSignalsLocal(),
				specificEvent.getResultingBackgrounds(),
				specificEvent.getResultingNoises(),
				specificEvent.getResultingSNRs(),
				specificEvent.getResultingAreas(),
				specificEvent.getResultingMeanSignals(),
				specificEvent.getResultingBackgroundsLocal(),
				specificEvent.getResultingNoisesLocal(),
				specificEvent.getResultingSNRsLocal(),
				specificEvent.getResultingAreasLocal(),
				specificEvent.getResultingMeanSignalsLocal(),
				specificEvent.getSNRRun());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsSNR(
			final OmegaRunDefinition algoSpec,
			final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsSNR specificEvent = (OmegaPluginEventResultsSNR) event;
		return new OmegaSNRRun(this.experimenter, algoSpec,
				specificEvent.getResultingImageAverageCenterSignal(),
				specificEvent.getResultingImageAveragePeakSignal(),
				specificEvent.getResultingImageAverageMeanSignal(),
				specificEvent.getResultingImageBGR(),
				specificEvent.getResultingImageNoise(),
				specificEvent.getResultingImageAverageSNR(),
				specificEvent.getResultingImageMinimumSNR(),
				specificEvent.getResultingImageMaximumSNR(),
				specificEvent.getResultingImageAverageErrorIndexSNR(),
				specificEvent.getResultingImageMinimumErrorIndexSNR(),
				specificEvent.getResultingImageMaximumErrorIndexSNR(),
				specificEvent.getResultingLocalCenterSignals(),
				specificEvent.getResultingLocalMeanSignals(),
				specificEvent.getResultingLocalSignalSizes(),
				specificEvent.getResultingLocalPeakSignals(),
				specificEvent.getResultingLocalBackgrounds(),
				specificEvent.getResultingLocalNoises(),
				specificEvent.getResultingLocalSNRs(),
				specificEvent.getResultingLocalErrorIndexSNRs(),
				specificEvent.getResultingAverageCenterSignal(),
				specificEvent.getResultingAveragePeakSignal(),
				specificEvent.getResultingAverageMeanSignal(),
				specificEvent.getResultingBackground(),
				specificEvent.getResultingNoise(),
				specificEvent.getResultingAvgSNR(),
				specificEvent.getResultingMaxSNR(),
				specificEvent.getResultingMinSNR(),
				specificEvent.getResultingAvgErrorIndexSNR(),
				specificEvent.getResultingMinErrorIndexSNR(),
				specificEvent.getResultingMaxErrorIndexSNR());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsTrackSegmentationRun(
			final OmegaRunDefinition algoSpec,
			final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrajectoriesSegmentation specificEvent = (OmegaPluginEventResultsTrajectoriesSegmentation) event;
		return new OmegaTrajectoriesSegmentationRun(this.experimenter,
				algoSpec, specificEvent.getResultingSegments(),
				specificEvent.getSegmentationTypes());
	}

	private OmegaAnalysisRun handlePluginEventAlgorithmResultsTrackRelinkingRun(
			final OmegaRunDefinition algoSpec,
			final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsTrajectoriesRelinking specificEvent = (OmegaPluginEventResultsTrajectoriesRelinking) event;
		final List<OmegaTrajectory> resultingTrajectories = specificEvent
				.getResultingTrajectories();

		final OmegaTrajectoriesRelinkingRun relinkingRun = new OmegaTrajectoriesRelinkingRun(
				this.experimenter, algoSpec, resultingTrajectories);

		final OmegaRunDefinition defaultSegmentationAlgoSpec = OmegaAlgorithmsUtilities
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
			final OmegaRunDefinition algoSpec,
			final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsParticleLinking specificEvent = (OmegaPluginEventResultsParticleLinking) event;
		final List<OmegaTrajectory> resultingTrajectories = specificEvent
				.getResultingTrajectories();
		final OmegaParticleLinkingRun particleLinkingRun = new OmegaParticleLinkingRun(
				this.experimenter, algoSpec, resultingTrajectories);

		final OmegaRunDefinition defaultRelinkingAlgoSpec = OmegaAlgorithmsUtilities
				.getDefaultRelinkingAlgorithmSpecification();
		final OmegaAnalysisRun defaultRelinkingRun = new OmegaTrajectoriesRelinkingRun(
				this.experimenter, defaultRelinkingAlgoSpec,
				"Default relinking run", resultingTrajectories);
		this.loadedAnalysisRuns.add(defaultRelinkingRun);
		particleLinkingRun.addAnalysisRun(defaultRelinkingRun);

		final OmegaRunDefinition defaultSegmentationAlgoSpec = OmegaAlgorithmsUtilities
				.getDefaultSegmentationAlgorithmSpecification();
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = OmegaAlgorithmsUtilities
				.createDefaultSegmentation(resultingTrajectories);
		final OmegaAnalysisRun defaultSegmentationRun = new OmegaTrajectoriesSegmentationRun(
				this.experimenter, defaultSegmentationAlgoSpec,
				"Default segmentation run", segmentsMap,
				OmegaSegmentationTypes.getDefaultSegmentationTypes());
		this.loadedAnalysisRuns.add(defaultSegmentationRun);
		defaultRelinkingRun.addAnalysisRun(defaultSegmentationRun);

		if (event.getElement() instanceof OmegaParticleDetectionRun) {
			final OmegaParticleDetectionRun detectionRun = (OmegaParticleDetectionRun) event
					.getElement();
			if (!detectionRun.getResultingParticles().isEmpty()) {
				int maxT = -1;
				double physicalT = 1.0;
				for (final OmegaPlane plane : detectionRun
						.getResultingParticles().keySet()) {
					if (plane.getParentPixels() != null) {
						maxT = plane.getParentPixels().getSizeT();
						if (plane.getParentPixels().getPhysicalSizeT() != -1) {
							physicalT = plane.getParentPixels()
									.getPhysicalSizeT();
						}
						break;
					} else {
						final int index = plane.getIndex();
						if (maxT < index) {
							maxT = index;
						}
					}
				}
				this.handleTrackingMeasures(
						(OmegaTrajectoriesSegmentationRun) defaultSegmentationRun,
						physicalT, maxT);
			}
		} else if (event.getElement() instanceof OmegaImage) {
			final OmegaImage img = (OmegaImage) event.getElement();
			final int maxT = img.getDefaultPixels().getSizeT();
			final OmegaImagePixels pixels = img.getDefaultPixels();
			final Double physicalT;
			if (pixels.getPhysicalSizeT() != -1) {
				physicalT = pixels.getPhysicalSizeT();
			} else {
				physicalT = 1.0;
			}
			this.handleTrackingMeasures(
					(OmegaTrajectoriesSegmentationRun) defaultSegmentationRun,
					physicalT, maxT);
		}
		return particleLinkingRun;
	}
	
	private OmegaAnalysisRun handlePluginEventAlgorithmResultsParticleDetectionRun(
			final OmegaRunDefinition algoSpec,
			final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsParticleDetection specificEvent = (OmegaPluginEventResultsParticleDetection) event;
		return new OmegaParticleDetectionRun(this.experimenter, algoSpec,
				specificEvent.getResultingParticles(),
				specificEvent.getResultingParticlesValues());
	}
	
	private OmegaAnalysisRun handlePluginEventAlgorithmResultsParticleTrackingRun(
			final OmegaRunDefinition algoSpec,
			final OmegaPluginEventAlgorithm event) {
		final OmegaPluginEventResultsParticleTracking specificEvent = (OmegaPluginEventResultsParticleTracking) event;
		final Map<OmegaPlane, List<OmegaROI>> resultingParticles = specificEvent
				.getResultingParticles();
		// for (final OmegaPlane frame : resultingParticles.keySet()) {
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
		
		final OmegaRunDefinition defaultRelinkingAlgoSpec = OmegaAlgorithmsUtilities
				.getDefaultRelinkingAlgorithmSpecification();
		final OmegaAnalysisRun defaultRelinkingRun = new OmegaTrajectoriesRelinkingRun(
				this.experimenter, defaultRelinkingAlgoSpec,
				"Default relinking run", resultingTrajectories);
		this.loadedAnalysisRuns.add(defaultRelinkingRun);
		particleLinkingRun.addAnalysisRun(defaultRelinkingRun);
		
		final OmegaRunDefinition defaultSegmentationAlgoSpec = OmegaAlgorithmsUtilities
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
			final OmegaImagePixels pixels = img.getDefaultPixels();
			Double physicalT = 1.0;
			if (pixels.getPhysicalSizeT() != -1) {
				physicalT = pixels.getPhysicalSizeT();
			}
			this.handleTrackingMeasures(
					(OmegaTrajectoriesSegmentationRun) defaultSegmentationRun,
					physicalT, maxT);
		}
		
		return particleDetectionRun;
	}
	
	private void handleTrackingMeasures(
			final OmegaTrajectoriesSegmentationRun segmentationRun,
			final double physicalT, final int maxT) {
		// TODO FIX HERE calling the separates analizer or making a new common
		// analizer
		final OmegaTrackingMeasuresAnalizer trackingMeasuresAnalizer = new OmegaTrackingMeasuresAnalizer(
				this, segmentationRun, physicalT, maxT);
		final Thread t = new Thread(trackingMeasuresAnalizer);
		t.setName("TrackingMeasuresAnalizer");
		t.start();
	}
	
	// TODO to be changed somehow
	public void updateTrackingMeasuresAnalizerResults(
			final OmegaTrajectoriesSegmentationRun segmentationRun,
			final Map<OmegaSegment, Double[]> peakSignalsMap,
			final Map<OmegaSegment, Double[]> centroidSignalsMap,
			final Map<OmegaROI, Double> peakSignalsLocMap,
			final Map<OmegaROI, Double> centroidSignalsLocMap,
			final Map<OmegaSegment, Double[]> backgroundsMap,
			final Map<OmegaSegment, Double[]> noisesMap,
			final Map<OmegaSegment, Double[]> snrsMap,
			final Map<OmegaSegment, Double[]> areasMap,
			final Map<OmegaSegment, Double[]> meanSignalsMap,
			final Map<OmegaROI, Double> backgroundsLocMap,
			final Map<OmegaROI, Double> noisesLocMap,
			final Map<OmegaROI, Double> snrsLocMap,
			final Map<OmegaROI, Double> areasLocMap,
			final Map<OmegaROI, Double> meanSignalsLocMap,
			final OmegaSNRRun snrRunInt,
			final Map<OmegaSegment, List<Double>> distancesMap,
			final Map<OmegaSegment, List<Double>> distancesFromOriginMap,
			final Map<OmegaSegment, List<Double>> displacementsFromOriginMap,
			final Map<OmegaSegment, Double> maxDisplacementsFromOriginMap,
			final Map<OmegaSegment, List<Double>> timeTraveledMap,
			final Map<OmegaSegment, List<Double>> confinementRatioMap,
			final Map<OmegaSegment, List<Double[]>> anglesAndDirectionalChangesMap,
			final Map<OmegaSegment, List<Double>> localSpeedMap,
			final Map<OmegaSegment, List<Double>> localSpeedFromOriginMap,
			final Map<OmegaSegment, List<Double>> localVelocityFromOriginMap,
			final Map<OmegaSegment, Double> averageCurvilinearSpeedMap,
			final Map<OmegaSegment, Double> averageStraightLineVelocityMap,
			final Map<OmegaSegment, Double> forwardProgressionLinearity,
			final Map<OmegaSegment, Double[]> ny,
			final Map<OmegaSegment, Double[][]> mu,
			final Map<OmegaSegment, Double[][]> logMu,
			final Map<OmegaSegment, Double[][]> deltaT,
			final Map<OmegaSegment, Double[][]> logDeltaT,
			final Map<OmegaSegment, Double[][]> gammaD,
			final Map<OmegaSegment, Double[][]> gammaDLog,
			// final Map<OmegaSegment, Double[]> gamma,
			final Map<OmegaSegment, Double[]> gammaLog,
			// final Map<OmegaSegment, Double[]> smss,
			final Map<OmegaSegment, Double[]> smssLog,
			// final Map<OmegaSegment, Double[]> errors,
			final Map<OmegaSegment, Double[]> errorsLog,
			final Double minDetectableODC, final OmegaSNRRun snrRunDiff,
			final OmegaTrackingMeasuresDiffusivityRun parentDiffusivityRun) {
		OmegaExperimenter exp = this.experimenter;
		if (exp == null) {
			exp = OmegaConstants.OMEGA_DEFAULT_EXPERIMENTER;
		}
		
		final OmegaTrackingMeasuresIntensityRun intensityRun = new OmegaTrackingMeasuresIntensityRun(
				exp,
				OmegaAlgorithmsUtilities
						.getDefaultTrackingMeasuresIntensitySpecification(),
				segmentationRun.getResultingSegments(), peakSignalsMap,
				centroidSignalsMap, peakSignalsLocMap, centroidSignalsLocMap,
				backgroundsMap, noisesMap, snrsMap, areasMap, meanSignalsMap,
				backgroundsLocMap, noisesLocMap, snrsLocMap, areasLocMap,
				meanSignalsLocMap, snrRunInt);
		segmentationRun.addAnalysisRun(intensityRun);
		this.loadedAnalysisRuns.add(intensityRun);
		
		final OmegaTrackingMeasuresMobilityRun mobilityRun = new OmegaTrackingMeasuresMobilityRun(
				exp,
				OmegaAlgorithmsUtilities
						.getDefaultTrackingMeasuresMobilitySpecification(),
				segmentationRun.getResultingSegments(), distancesMap,
				distancesFromOriginMap, displacementsFromOriginMap,
				maxDisplacementsFromOriginMap, timeTraveledMap,
				confinementRatioMap, anglesAndDirectionalChangesMap);
		segmentationRun.addAnalysisRun(mobilityRun);
		this.loadedAnalysisRuns.add(mobilityRun);
		
		final OmegaTrackingMeasuresVelocityRun velocityRun = new OmegaTrackingMeasuresVelocityRun(
				exp,
				OmegaAlgorithmsUtilities
						.getDefaultTrackingMeasuresVelocitySpecification(),
				segmentationRun.getResultingSegments(), localSpeedMap,
				localSpeedFromOriginMap, localVelocityFromOriginMap,
				averageCurvilinearSpeedMap, averageStraightLineVelocityMap,
				forwardProgressionLinearity);
		segmentationRun.addAnalysisRun(velocityRun);
		this.loadedAnalysisRuns.add(velocityRun);
		
		final OmegaTrackingMeasuresDiffusivityRun diffusivityRun = new OmegaTrackingMeasuresDiffusivityRun(
				exp,
				OmegaAlgorithmsUtilities
						.getDefaultTrackingMeasuresDiffusivitySpecification(),
				segmentationRun.getResultingSegments(), ny, mu, logMu, deltaT,
				logDeltaT, gammaD, gammaDLog, /* gamma, */gammaLog, /* smss, */
				smssLog,
				/* errors, */errorsLog, minDetectableODC, snrRunDiff,
				parentDiffusivityRun);
		segmentationRun.addAnalysisRun(diffusivityRun);
		this.loadedAnalysisRuns.add(diffusivityRun);
		
		this.omegaData.consolidateData();
	}
	
	private void handlePluginEventGateway(final OmegaPluginEventGateway event) {
		// TODO to check why replicated in 2 places
		switch (event.getStatus()) {
			case OmegaPluginEventGateway.STATUS_CREATED:
				this.gateway = ((OmegaLoaderPlugin) event.getSource())
						.getGateway();
				this.experimenter = null;
				break;
			case OmegaPluginEventGateway.STATUS_DESTROYED:
				this.gateway = null;
				this.experimenter = null;
				break;
			case OmegaPluginEventGateway.STATUS_CONNECTED:
				this.gateway = ((OmegaLoaderPlugin) event.getSource())
						.getGateway();
				final OmegaExperimenter exp = event.getExperimenter();
				final OmegaExperimenter sameExperimenter = this.omegaData
						.findSameExperiementer(exp);
				if (sameExperimenter != null) {
					if (OmegaLogFileManager.isDebug()) {
						OmegaLogFileManager.appendToCoreLog("Experimenter: "
								+ exp.printName()
								+ " already found, replacing.");
					}
					this.experimenter = sameExperimenter;
				} else {
					final OmegaPerson samePerson = this.omegaData
							.findSamePerson(exp);
					if (samePerson != null) {
						if (OmegaLogFileManager.isDebug()) {
							OmegaLogFileManager
									.appendToCoreLog("Experimenter: "
											+ exp.printName()
											+ " found as person, replacing person with exp.");
						}
						this.omegaData.changePersonWithExperimenter(samePerson,
								exp);
					}
					if (OmegaLogFileManager.isDebug()) {
						OmegaLogFileManager.appendToCoreLog("Experimenter: "
								+ exp.printName() + " not found, adding.");
					}
					this.experimenter = exp;
					this.omegaData.addExperimenter(exp);
				}
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
			if ((event.getSource() != null) && event.getSource().equals(plugin)) {
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
			if ((event.getSource() != null) && event.getSource().equals(plugin)) {
				continue;
			}
			if (event instanceof OmegaPluginEventSelectionTrajectoriesRelinkingRun) {
				if (plugin instanceof OmegaTrajectoriesSegmentationPlugin) {
					((OmegaTrajectoriesSegmentationPlugin) plugin)
							.selectTrajectoriesRelinkingRun(null);
				}
			} else if ((analysisRun instanceof OmegaTrackingMeasuresRun)
					&& (plugin instanceof OmegaSelectTrackingMeasuresRunPluginInterface)) {
				((OmegaSelectTrackingMeasuresRunPluginInterface) plugin)
						.selectTrackingMeasuresRun((OmegaTrackingMeasuresRun) event
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
					&& !(analysisRun instanceof OmegaTrajectoriesRelinkingRun)
					&& (plugin instanceof OmegaSelectParticleLinkingRunPluginInterface)) {
				((OmegaSelectParticleLinkingRunPluginInterface) plugin)
						.selectParticleLinkingRun((OmegaParticleLinkingRun) event
								.getAnalysisRun());
			} else if ((analysisRun instanceof OmegaParticleDetectionRun)
					&& (plugin instanceof OmegaSelectParticleDetectionRunPluginInterface)) {
				((OmegaSelectParticleDetectionRunPluginInterface) plugin)
						.selectParticleDetectionRun((OmegaParticleDetectionRun) event
								.getAnalysisRun());
			} else if ((analysisRun instanceof OmegaSNRRun)
					&& (plugin instanceof OmegaSelectSNRRunPluginInterface)) {
				((OmegaSelectSNRRunPluginInterface) plugin)
						.selectSNRRun((OmegaSNRRun) event.getAnalysisRun());
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
			if (plugin instanceof OmegaSelectTrajectoriesInterface) {
				((OmegaSelectTrajectoriesInterface) plugin).updateTrajectories(
						event.getTrajectories(), event.isSelectionEvent());
			}
		}
		this.gui.updateTrajectories(event.getTrajectories(),
				event.isSelectionEvent());
	}
	
	private void handlePluginEventSegments(final OmegaPluginEventSegments event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin.equals(event.getSource())) {
				continue;
			}
			if (plugin instanceof OmegaSelectSegmentsInterface) {
				((OmegaSelectTrajectoriesInterface) plugin).updateTrajectories(
						new ArrayList<OmegaTrajectory>(event.getSegments()
								.keySet()), event.isSelectionEvent());
				((OmegaSelectSegmentsInterface) plugin).updateSegments(
						event.getSegments(), event.getSegmentationTypes(),
						event.isSelectionEvent());
			}
		}
		this.gui.updateSegments(event.getSegments(),
				event.getSegmentationTypes(), event.isSelectionEvent());
	}
	
	private void handlePluginEventDataChanged(
			final OmegaPluginEventDataChanged event) {
		this.omegaData.consolidateData();
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
		for (final String arg : args) {
			// System.out.println(arg);
			if (arg.equals("-debug")) {
				OmegaLogFileManager.setDebug();
			}
		}
		// if (!OmegaLogFileManager.isDebug()) {
		// try {
		// // re-launch the app itselft with VM option passed
		// Runtime.getRuntime().exec(
		// new String[] { "java", "-d32", "-Xmx1G", "-jar",
		// "omega-0.0.1-SNAPSHOT.jar" });
		// } catch (final IOException ioe) {
		// ioe.printStackTrace();
		// // System.exit(0);
		// }
		// }
		
		final OmegaApplication instance = new OmegaApplication();
		instance.showGUI();
	}
	
	public void loadOrphanedAnalysis() {
		final OmegaDBServerInformation serverInfo = this.gui
				.getOmegaDBServerInformation();
		final OmegaLoginCredentials loginCred = this.gui
				.getOmegaLoginCredentials();
		this.mysqlReader.setServerInformation(serverInfo);
		this.mysqlReader.setLoginCredentials(loginCred);
		
		try {
			this.mysqlReader.connect();
		} catch (ClassNotFoundException | SQLException ex) {
			OmegaLogFileManager.handleCoreException(ex, true);
			// TODO manage the case somehow
			return;
		}
		
		this.dbDialog = new GenericProgressDialog(this.gui,
				"Loading analysis from Omega server",
				"Starting loading analysis from Omega Server", false);
		this.dbDialog.setVisible(true);
		this.gui.setEnabled(false);
		final OmegaDBLoader loader = new OmegaDBLoader(this, this.mysqlReader,
				this.dbDialog, this.omegaData.getProjects(),
				OmegaDBRunnable.LOAD_TYPE_ORPHANED_IMAGE);
		this.dbThread = new Thread(loader);
		this.dbThread.start();
		
	}
	
	public void loadAnalysis() {
		final OmegaDBServerInformation serverInfo = this.gui
				.getOmegaDBServerInformation();
		final OmegaLoginCredentials loginCred = this.gui
				.getOmegaLoginCredentials();
		this.mysqlReader.setServerInformation(serverInfo);
		this.mysqlReader.setLoginCredentials(loginCred);
		
		try {
			this.mysqlReader.connect();
		} catch (ClassNotFoundException | SQLException ex) {
			OmegaLogFileManager.handleCoreException(ex, true);
			// TODO manage the case somehow
			return;
		}
		
		this.dbDialog = new GenericProgressDialog(this.gui,
				"Loading analysis from Omega server",
				"Starting loading analysis from Omega Server", false);
		this.dbDialog.setVisible(true);
		this.gui.setEnabled(false);
		final OmegaDBLoader loader = new OmegaDBLoader(this, this.mysqlReader,
				this.dbDialog, this.omegaData.getProjects(),
				OmegaDBRunnable.LOAD_TYPE_LOADED_IMAGE);
		this.dbThread = new Thread(loader);
		this.dbThread.start();
		
	}
	
	public void updateTrajectories() {
		final OmegaDBServerInformation serverInfo = this.gui
				.getOmegaDBServerInformation();
		final OmegaLoginCredentials loginCred = this.gui
				.getOmegaLoginCredentials();
		this.mysqlWriter.setServerInformation(serverInfo);
		this.mysqlWriter.setLoginCredentials(loginCred);
		
		try {
			this.mysqlWriter.connect();
		} catch (ClassNotFoundException | SQLException ex) {
			OmegaLogFileManager.handleCoreException(ex, true);
			// TODO manage the case somehow
			return;
		}
		
		this.dbDialog = new GenericProgressDialog(this.gui,
				"Updating analysis in Omega server",
				"Starting updating analysis in Omega Server", false);
		this.dbDialog.setVisible(true);
		this.gui.setEnabled(false);
		final OmegaDBUpdater updater = new OmegaDBUpdater(this,
				this.mysqlWriter, this.dbDialog, this.omegaData.getProjects());
		this.dbThread = new Thread(updater);
		this.dbThread.start();
		
	}
	
	public void saveAnalysis() {
		final OmegaDBServerInformation serverInfo = this.gui
				.getOmegaDBServerInformation();
		final OmegaLoginCredentials loginCred = this.gui
				.getOmegaLoginCredentials();
		this.mysqlWriter.setServerInformation(serverInfo);
		this.mysqlWriter.setLoginCredentials(loginCred);
		this.mysqlReader.setServerInformation(serverInfo);
		this.mysqlReader.setLoginCredentials(loginCred);
		final StringBuffer dialogTitleBuf = new StringBuffer();
		dialogTitleBuf.append("Saving analysis to Omega server");
		final StringBuffer dialogMsgBuf = new StringBuffer();
		
		try {
			this.mysqlWriter.connect();
			this.mysqlReader.connect();
		} catch (ClassNotFoundException | SQLException ex) {
			OmegaLogFileManager.handleCoreException(ex, true);
			dialogTitleBuf.append(" error");
			dialogMsgBuf.append("Unable to connect to the Omega server");
			// TODO add the possible errors checking the getCause of the
			// exception
			this.dbDialog = new GenericProgressDialog(this.gui,
					dialogTitleBuf.toString(), dialogMsgBuf.toString(), true);
			this.dbDialog.enableClose();
			this.dbDialog.setVisible(true);
			// this.gui.setEnabled(false);
			return;
		}
		
		// try {
		// for (final OmegaSegmentationTypes segmTypes : this.omegaData
		// .getSegmentationTypesList()) {
		// final int result = this.mysqlWriter
		// .isSegmentationTypesNameInDBWithDifferentValues(segmTypes);
		// if (result == -1) {
		// dialogTitleBuf.append(" error");
		// dialogMsgBuf.append("SegmentationTypes ");
		// dialogMsgBuf.append(segmTypes.getName());
		// dialogMsgBuf
		// .append(" already present in db with different types");
		// this.dbDialog = new GenericMessageDialog(this.gui,
		// dialogTitleBuf.toString(), dialogMsgBuf.toString(),
		// true);
		// this.dbDialog.setVisible(true);
		// this.gui.setEnabled(false);
		// return;
		// }
		// }
		// } catch (final SQLException ex) {
		// OmegaLogFileManager.handleCoreException(ex);
		// // TODO manage the case somehow
		// return;
		// }
		
		dialogMsgBuf.append("Starting saving analysis to Omega Server");
		this.dbDialog = new GenericProgressDialog(this.gui,
				dialogTitleBuf.toString(), dialogMsgBuf.toString(), false);
		this.dbDialog.setVisible(true);
		this.gui.setEnabled(false);
		final OmegaDBSaver saver = new OmegaDBSaver(this, this.mysqlWriter,
				this.mysqlReader, this.dbDialog, this.omegaData.getProjects(),
				OmegaDBRunnable.LOAD_TYPE_LOADED_IMAGE);
		this.dbThread = new Thread(saver);
		this.dbThread.start();
	}
	
	public void handleRunnableProcessTermination(final OmegaDBRunnable runnable) {
		// this.dbDialog.enableClose();
		this.dbDialog.updateTotalMessage("Operation completed");
		this.gui.setEnabled(true);
		this.gui.setVisible(true);
		if (runnable instanceof OmegaDBWriter) {
			try {
				if (((OmegaDBWriter) runnable).isErrorOccured()) {
					this.mysqlWriter.rollback();
				} else {
					this.mysqlWriter.commit();
				}
			} catch (final SQLException ex) {
				OmegaLogFileManager.handleCoreException(ex, true);
				// TODO manage the case somehow
				return;
			}
		}
		
		try {
			if (runnable instanceof OmegaDBWriter) {
				this.mysqlWriter.disconnect();
			}
			this.mysqlReader.disconnect();
		} catch (final SQLException ex) {
			OmegaLogFileManager.handleCoreException(ex, true);
			// TODO manage the case somehow
			return;
		}
		
		try {
			this.dbThread.join();
		} catch (final InterruptedException ex) {
			OmegaLogFileManager.handleCoreException(ex, true);
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
		// dbDialog.enableClose();
		while (this.dbDialog.isVisible()) {
			// WAIT
		}
	}
	
	// public void showTracksImporter() {
	// // TODO to find a better way
	// final OmegaTracksToolTargetSelectorDialog ts = new
	// OmegaTracksToolTargetSelectorDialog(
	// this.gui, this.loadedData.getImages(),
	// this.omegaData.getOrphanedContainer(), this.loadedAnalysisRuns);
	// ts.setImporter(true);
	// ts.setVisible(true);
	// if (!ts.isNext())
	// return;
	// final OmegaAnalysisRunContainerInterface container = ts.getSelectedImage();
	// final OmegaTracksImporter oti = (OmegaTracksImporter)
	// this.registeredImporter
	// .get(0);
	// oti.setContainer(container);
	// oti.showDialog(this.gui);
	// }
	//
	// public void showTracksExporter() {
	// // TODO to find a better way
	// final OmegaTracksToolTargetSelectorDialog ts = new
	// OmegaTracksToolTargetSelectorDialog(
	// this.gui, this.loadedData.getImages(),
	// this.omegaData.getOrphanedContainer(), this.loadedAnalysisRuns);
	// ts.setImporter(false);
	// ts.setVisible(true);
	// if (!ts.isNext())
	// return;
	// final OmegaParticleDetectionRun detectionRun = ts
	// .getSelectedParticleDetectionRun();
	// final OmegaParticleLinkingRun linkingRun = ts
	// .getSelectedParticleLinkingRun();
	// if ((detectionRun == null) || (linkingRun == null))
	// return;
	// // TODO to find a better way
	// final OmegaTracksExporter ote = (OmegaTracksExporter)
	// this.registeredExporter
	// .get(0);
	// ote.setParticles(detectionRun.getResultingParticles());
	// ote.setParticlesValues(detectionRun.getResultingParticlesValues());
	// ote.setTracks(linkingRun.getResultingTrajectories());
	// ote.showDialog(this.gui);
	// }
	//
	// public void exportAllData() {
	// final File f = new File(".//exported//alldata.txt");
	// final StringBuffer buf = this.omegaData.printOmegaData();
	// FileWriter fw;
	// try {
	// f.createNewFile();
	// fw = new FileWriter(f);
	// } catch (final IOException e) {
	// e.printStackTrace();
	// return;
	// }
	// final BufferedWriter bw = new BufferedWriter(fw);
	// try {
	// bw.write(buf.toString());
	// bw.close();
	// fw.close();
	// } catch (final IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	
	public void showDiffusivityExporter() {
		// TODO to find a better way
		final OmegaTracksToolTargetSelectorDialog ts = new OmegaTracksToolTargetSelectorDialog(
				this.gui, this.loadedData.getImages(),
				this.omegaData.getOrphanedContainer(), this.loadedAnalysisRuns);
		ts.setImporter(false);
		ts.setVisible(true);
		if (!ts.isNext())
			return;
		final OmegaParticleDetectionRun detectionRun = ts
				.getSelectedParticleDetectionRun();
		final OmegaParticleLinkingRun linkingRun = ts
				.getSelectedParticleLinkingRun();
		if ((detectionRun == null) || (linkingRun == null))
			return;
		// TODO to find a better way
		final OmegaTrajectoriesRelinkingRun relinkingRun = (OmegaTrajectoriesRelinkingRun) linkingRun
				.getAnalysisRuns().get(0);
		final OmegaTrajectoriesSegmentationRun segmRun = (OmegaTrajectoriesSegmentationRun) relinkingRun
				.getAnalysisRuns().get(0);
		final OmegaTrackingMeasuresDiffusivityRun diffRun = (OmegaTrackingMeasuresDiffusivityRun) segmRun
				.getAnalysisRuns().get(3);
		final File f = new File(".//exported//dAndSmss_omega_test.txt");
		FileWriter fw;
		try {
			f.createNewFile();
			fw = new FileWriter(f);
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}
		final BufferedWriter bw = new BufferedWriter(fw);
		for (final OmegaTrajectory track : diffRun.getSegments().keySet()) {
			for (final OmegaSegment segm : diffRun.getSegments().get(track)) {
				try {
					bw.write(track.getName() + " from "
							+ segm.getStartingROI().getFrameIndex() + " to "
							+ segm.getEndingROI().getFrameIndex() + "\t"
							+ diffRun.getGammaDFromLogResults().get(segm)[2][3]
							+ "\t"
							+ diffRun.getSmssFromLogResults().get(segm)[0]
							+ "\n");
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			bw.close();
			fw.close();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
