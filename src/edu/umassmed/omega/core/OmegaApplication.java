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

import edu.umassmed.omega.commons.eventSystem.OmegaAlgorithmPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationImageSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationParticleDetectionRunSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationParticleLinkingRunSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationTrajectoriesEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationTrajectoriesManagerRunSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaDataChangedEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaGatewayEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaParticleDetectionResultsEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaParticleLinkingResultsEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaParticleTrackingResultsEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaPluginListener;
import edu.umassmed.omega.commons.eventSystem.OmegaTMPluginImageSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaTMPluginParticleDetectionRunSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaTMPluginParticleLinkingRunSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaTMPluginTrajectoriesEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaTMPluginTrajectoriesManagerRunSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaTrajectoriesManagerResultsEvent;
import edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import edu.umassmed.omega.commons.plugins.OmegaAlgorithmPlugin;
import edu.umassmed.omega.commons.plugins.OmegaBrowserPlugin;
import edu.umassmed.omega.commons.plugins.OmegaLoaderPlugin;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.commons.plugins.OmegaTrajectoriesManagerPlugin;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaImageConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoadedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoadedDataConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoaderPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaMainDataConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectImagePluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleDetectionRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleLinkingRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesManagerRunPluginInterface;
import edu.umassmed.omega.core.gui.OmegaGUIFrame;
import edu.umassmed.omega.core.runnables.OmegaDBLoader;
import edu.umassmed.omega.core.runnables.OmegaDBRunnable;
import edu.umassmed.omega.core.runnables.OmegaDBSaver;
import edu.umassmed.omega.core.runnables.OmegaDBUpdater;
import edu.umassmed.omega.core.runnables.OmegaDBWriter;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.dataNew.OmegaLoadedData;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAlgorithmInformation;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAlgorithmSpecification;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaTrajectoriesManagerRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;
import edu.umassmed.omega.dataNew.coreElements.OmegaFrame;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaImagePixels;
import edu.umassmed.omega.dataNew.coreElements.OmegaProject;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaDBServerInformation;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaLoginCredentials;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.omegaDataBrowserPlugin.OmegaDataBrowserPlugin;
import edu.umassmed.omega.omeroPlugin.OmeroPlugin;
import edu.umassmed.omega.sptPlugin.SPTPlugin;
import edu.umassmed.omega.trajectoryManagerPlugin.TrajectoriesManagerPlugin;

public class OmegaApplication implements OmegaPluginListener {

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

		this.logFileManager = OmegaLogFileManager.getOmegaLogFileManager();

		this.optionsFileManager = new OmegaOptionsFileManager();
		this.generalOptions = this.optionsFileManager.getGeneralOptions();

		this.mysqlGateway = new OmegaMySqlGateway();

		this.omegaData = new OmegaData();
		this.loadedData = new OmegaLoadedData();
		this.loadedAnalysisRuns = new ArrayList<OmegaAnalysisRun>();
		this.gateway = null;
		this.experimenter = null;

		this.registerCorePlugins();

		OmegaLogFileManager.markNewRun(this.registeredPlugin);

		this.gui = new OmegaGUIFrame(this);
		this.gui.initialize(this.pluginIndexMap);
		this.gui.setSize(1200, 800);

	}

	private void registerCorePlugins() {
		this.registerPlugin(new OmeroPlugin());
		this.registerPlugin(new SPTPlugin());
		this.registerPlugin(new TrajectoriesManagerPlugin());
		this.registerPlugin(new OmegaDataBrowserPlugin());

		for (final OmegaPlugin plugin : this.registeredPlugin) {
			final String optionsCategory = plugin.getOptionsCategory();
			plugin.addPluginOptions(this.optionsFileManager
			        .getOptions(optionsCategory));
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
			if (plugin instanceof OmegaTrajectoriesManagerPlugin) {
				((OmegaTrajectoriesManagerPlugin) plugin)
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
	}

	protected void showGUI() {
		this.gui.setVisible(true);
		final List<Integer> bla = new ArrayList<>();
		bla.get(2);
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

	public void handleOmegaApplicationEvent(final OmegaApplicationEvent event) {
		if (event instanceof OmegaApplicationTrajectoriesEvent) {
			this.handleOmegaApplicationTrajectoriesEvent((OmegaApplicationTrajectoriesEvent) event);
		} else if (event instanceof OmegaApplicationImageSelectionEvent) {
			this.handleOmegaApplicationImageSelectionEvent((OmegaApplicationImageSelectionEvent) event);
		} else if (event instanceof OmegaApplicationParticleDetectionRunSelectionEvent) {
			this.handleOmegaApplicationParticleDetectionRunSelectionEvent((OmegaApplicationParticleDetectionRunSelectionEvent) event);
		} else if (event instanceof OmegaApplicationParticleLinkingRunSelectionEvent) {
			this.handleOmegaApplicationParticleLinkingRunSelectionEvent((OmegaApplicationParticleLinkingRunSelectionEvent) event);
		} else if (event instanceof OmegaApplicationTrajectoriesManagerRunSelectionEvent) {
			this.handleOmegaApplicationTrajectoriesManagerRunSelectionEvent((OmegaApplicationTrajectoriesManagerRunSelectionEvent) event);
		}
	}

	private void handleOmegaApplicationImageSelectionEvent(
	        final OmegaApplicationImageSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin instanceof OmegaSelectImagePluginInterface) {
				((OmegaSelectImagePluginInterface) plugin).selectImage(event
				        .getImage());
			}
		}
	}

	private void handleOmegaApplicationParticleDetectionRunSelectionEvent(
	        final OmegaApplicationParticleDetectionRunSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin instanceof OmegaSelectParticleDetectionRunPluginInterface) {
				((OmegaSelectParticleDetectionRunPluginInterface) plugin)
				        .selectParticleDetectionRun(event.getAnalysisRun());
			}
		}
	}

	private void handleOmegaApplicationParticleLinkingRunSelectionEvent(
	        final OmegaApplicationParticleLinkingRunSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin instanceof OmegaSelectParticleLinkingRunPluginInterface) {
				((OmegaSelectParticleLinkingRunPluginInterface) plugin)
				        .selectParticleLinkingRun(event.getAnalysisRun());
			}
		}
	}

	private void handleOmegaApplicationTrajectoriesManagerRunSelectionEvent(
	        final OmegaApplicationTrajectoriesManagerRunSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin instanceof OmegaSelectTrajectoriesManagerRunPluginInterface) {
				((OmegaSelectTrajectoriesManagerRunPluginInterface) plugin)
				        .selectTrajectoriesManagerRun(event.getAnalysisRun());
			}
		}
	}

	private void handleOmegaApplicationTrajectoriesEvent(
	        final OmegaApplicationTrajectoriesEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin instanceof OmegaTrajectoriesManagerPlugin) {
				((OmegaTrajectoriesManagerPlugin) plugin).updateTrajectories(
				        event.getTrajectories(), event.isSelectionEvent());
			}
		}
	}

	@Override
	public void handleOmegaPluginEvent(final OmegaPluginEvent event) {
		final OmegaPlugin plugin = event.getSource();
		if (plugin instanceof OmegaLoaderPlugin) {
			if (event instanceof OmegaGatewayEvent) {
				this.handleOmegaLoaderPluginGatewayEvent((OmegaGatewayEvent) event);
			} else if (event instanceof OmegaDataChangedEvent) {
				this.handleOmegaLoaderPluginDataChangedEvent((OmegaDataChangedEvent) event);
			}
		} else if (plugin instanceof OmegaBrowserPlugin) {
			this.handleOmegaBrowserPluginDataChangedEvent((OmegaDataChangedEvent) event);
		} else if (plugin instanceof OmegaAlgorithmPlugin) {
			if (event instanceof OmegaAlgorithmPluginEvent) {
				this.handleOmegaAlgorithmPluginEvent((OmegaAlgorithmPluginEvent) event);
			} else if (plugin instanceof OmegaTrajectoriesManagerPlugin) {
				if (event instanceof OmegaTMPluginImageSelectionEvent) {
					this.handleOmegaTMPluginImageSelectionEvent((OmegaTMPluginImageSelectionEvent) event);
				} else if (event instanceof OmegaTMPluginParticleDetectionRunSelectionEvent) {
					this.handleOmegaTMPluginParticleDetectionRunSelectionEvent((OmegaTMPluginParticleDetectionRunSelectionEvent) event);
				} else if (event instanceof OmegaTMPluginParticleLinkingRunSelectionEvent) {
					this.handleOmegaTMPluginParticleLinkingRunSelectionEvent((OmegaTMPluginParticleLinkingRunSelectionEvent) event);
				} else if (event instanceof OmegaTMPluginTrajectoriesManagerRunSelectionEvent) {
					this.handleOmegaTMPluginTrajectoriesManagerRunSelectionEvent((OmegaTMPluginTrajectoriesManagerRunSelectionEvent) event);
				} else {
					this.handleOmegaTMPluginTrajectoriesEvent((OmegaTMPluginTrajectoriesEvent) event);
				}
			}
		}
	}

	private void handleOmegaAlgorithmPluginEvent(
	        final OmegaAlgorithmPluginEvent event) {
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

		OmegaAnalysisRun analysisRun;
		if (event instanceof OmegaParticleTrackingResultsEvent) {
			analysisRun = new OmegaParticleDetectionRun(this.experimenter,
			        algoSpec,
			        ((OmegaParticleTrackingResultsEvent) event)
			                .getResultingParticles());
			final OmegaAnalysisRun subAnalysisRun = new OmegaParticleLinkingRun(
			        this.experimenter, algoSpec,
			        ((OmegaParticleTrackingResultsEvent) event)
			                .getResultingTrajectories());
			this.loadedAnalysisRuns.add(subAnalysisRun);
			analysisRun.addAnalysisRun(subAnalysisRun);
		} else if (event instanceof OmegaParticleDetectionResultsEvent) {
			analysisRun = new OmegaParticleDetectionRun(this.experimenter,
			        algoSpec,
			        ((OmegaParticleDetectionResultsEvent) event)
			                .getResultingParticles());
		} else if (event instanceof OmegaTrajectoriesManagerResultsEvent) {
			final OmegaTrajectoriesManagerResultsEvent specificEvent = (OmegaTrajectoriesManagerResultsEvent) event;
			if (specificEvent.getOriginal() == null) {
				analysisRun = new OmegaTrajectoriesManagerRun(
				        this.experimenter, algoSpec,
				        specificEvent.getResultingTrajectories(),
				        specificEvent.getResultingSegments(),
				        specificEvent.getSegmentationTypes());
			} else {
				analysisRun = specificEvent.getOriginal();
				((OmegaAnalysisRunContainer) element)
				        .removeAnalysisRun(analysisRun);
				final OmegaTrajectoriesManagerRun tmRun = (OmegaTrajectoriesManagerRun) analysisRun;
				final List<OmegaTrajectory> trajectories = specificEvent
				        .getResultingTrajectories();
				if (trajectories != null) {
					tmRun.updateTrajectories(trajectories);
				}
				final Map<OmegaTrajectory, List<OmegaSegment>> segments = specificEvent
				        .getResultingSegments();
				if (segments != null) {
					tmRun.updateSegments(segments);
				}
				final OmegaSegmentationTypes segmTypes = specificEvent
				        .getSegmentationTypes();
				if (segmTypes != null) {
					tmRun.updateSegmentationTypes(segmTypes);
				}
				if ((segmTypes != null) || (segments != null)
				        || (trajectories != null)) {
					tmRun.updateTimeStamps();
				}
			}
		} else if (event instanceof OmegaParticleLinkingResultsEvent) {
			analysisRun = new OmegaParticleLinkingRun(this.experimenter,
			        algoSpec,
			        ((OmegaParticleLinkingResultsEvent) event)
			                .getResultingTrajectories());
		} else
			// TODO gestire errore
			return;

		((OmegaAnalysisRunContainer) element).addAnalysisRun(analysisRun);
		this.loadedAnalysisRuns.add(analysisRun);

		this.updateGUI(event.getSource(), true);
	}

	private void handleOmegaLoaderPluginGatewayEvent(
	        final OmegaGatewayEvent event) {
		// TODO to check why replicated in 2 places
		switch (event.getStatus()) {
		case OmegaGatewayEvent.STATUS_CREATED:
			this.gateway = ((OmegaLoaderPlugin) event.getSource()).getGateway();
			this.experimenter = null;
			break;
		case OmegaGatewayEvent.STATUS_DESTROYED:
			this.gateway = null;
			this.experimenter = null;
			break;
		case OmegaGatewayEvent.STATUS_CONNECTED:
			this.gateway = ((OmegaLoaderPlugin) event.getSource()).getGateway();
			this.experimenter = event.getExperimenter();
			break;
		case OmegaGatewayEvent.STATUS_DISCONNECTED:
			this.experimenter = null;
			break;
		}

		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (plugin instanceof OmegaLoaderPluginInterface) {
				((OmegaLoaderPluginInterface) plugin).setGateway(this.gateway);
			}
		}
	}

	private void handleOmegaTMPluginImageSelectionEvent(
	        final OmegaTMPluginImageSelectionEvent event) {
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

	private void handleOmegaTMPluginParticleDetectionRunSelectionEvent(
	        final OmegaTMPluginParticleDetectionRunSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (event.getSource().equals(plugin)) {
				continue;
			}
			if (plugin instanceof OmegaSelectParticleDetectionRunPluginInterface) {
				((OmegaSelectParticleDetectionRunPluginInterface) plugin)
				        .selectParticleDetectionRun(event.getAnalysisRun());
			}
		}
		this.gui.selectParticleDetectionRun(event.getAnalysisRun());
	}

	private void handleOmegaTMPluginParticleLinkingRunSelectionEvent(
	        final OmegaTMPluginParticleLinkingRunSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (event.getSource().equals(plugin)) {
				continue;
			}
			if (plugin instanceof OmegaSelectParticleLinkingRunPluginInterface) {
				((OmegaSelectParticleLinkingRunPluginInterface) plugin)
				        .selectParticleLinkingRun(event.getAnalysisRun());
			}
		}
		this.gui.selectParticleLinkingRun(event.getAnalysisRun());
	}

	private void handleOmegaTMPluginTrajectoriesManagerRunSelectionEvent(
	        final OmegaTMPluginTrajectoriesManagerRunSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin) {
			if (event.getSource().equals(plugin)) {
				continue;
			}
			if (plugin instanceof OmegaSelectTrajectoriesManagerRunPluginInterface) {
				((OmegaSelectTrajectoriesManagerRunPluginInterface) plugin)
				        .selectTrajectoriesManagerRun(event.getAnalysisRun());
			}
		}
		this.gui.selectTrajectoriesManagerRun(event.getAnalysisRun());
	}

	private void handleOmegaTMPluginTrajectoriesEvent(
	        final OmegaTMPluginTrajectoriesEvent event) {
		this.gui.updateTrajectories(event.getTrajectories(),
		        event.isSelectionEvent());
	}

	private void handleOmegaLoaderPluginDataChangedEvent(
	        final OmegaDataChangedEvent event) {
		this.loadSelectedData(event.getSelectedData());
		this.updateGUI(event.getSource(), event.getSelectedData().size() > 0);
	}

	private void handleOmegaBrowserPluginDataChangedEvent(
	        final OmegaDataChangedEvent event) {
		this.updateGUI(event.getSource(), true);
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
		} catch (ClassNotFoundException | SQLException ex) {
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
				if (plugin instanceof OmegaTrajectoriesManagerPlugin) {
					final OmegaTrajectoriesManagerPlugin tmPlugin = (OmegaTrajectoriesManagerPlugin) plugin;
					tmPlugin.updateSegmentationTypesList(new ArrayList<OmegaSegmentationTypes>(
					        this.omegaData.getSegmentationTypesList()));
				}
			}
		}
	}
}
