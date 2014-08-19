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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.eventSystem.OmegaAlgorithmPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationBufferedImageEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationImageSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationParticleDetectionRunSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationParticleLinkingRunSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationTrajectoriesEvent;
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
import edu.umassmed.omega.commons.genericInterfaces.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.genericInterfaces.OmegaImageConsumerPluginInterface;
import edu.umassmed.omega.commons.genericInterfaces.OmegaLoadedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.genericInterfaces.OmegaLoadedDataConsumerPluginInterface;
import edu.umassmed.omega.commons.genericInterfaces.OmegaMainDataConsumerPluginInterface;
import edu.umassmed.omega.commons.genericPlugins.OmegaAlgorithmPlugin;
import edu.umassmed.omega.commons.genericPlugins.OmegaBrowserPlugin;
import edu.umassmed.omega.commons.genericPlugins.OmegaLoaderPlugin;
import edu.umassmed.omega.commons.genericPlugins.OmegaParticleTrackingPlugin;
import edu.umassmed.omega.commons.genericPlugins.OmegaPlugin;
import edu.umassmed.omega.commons.genericPlugins.OmegaTrajectoryManagerPlugin;
import edu.umassmed.omega.core.gui.OmegaGUIFrame;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.dataNew.OmegaLoadedData;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAlgorithmInformation;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAlgorithmSpecification;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;
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
import edu.umassmed.omega.omegaDataBrowserPlugin.OmegaDataBrowserPlugin;
import edu.umassmed.omega.omeroPlugin.OmeroPlugin;
import edu.umassmed.omega.sptPlugin.SPTPlugin;
import edu.umassmed.omega.trajectoryManagerPlugin.TrajectoryManagerPlugin;

public class OmegaApplication implements OmegaPluginListener {

	private final OmegaGUIFrame gui;

	private final OmegaData omegaData;
	private final OmegaLoadedData loadedData;
	private final List<OmegaAnalysisRun> loadedAnalysisRuns;
	private OmegaGateway gateway;

	private OmegaExperimenter experimenter;

	private short pluginIndex;
	private final Map<String, Long> pluginIndexes;
	private final Map<Long, OmegaPlugin> registeredPlugin;

	private final OmegaOptionsFileManager optionsFileManager;
	private final Map<String, Map<String, String>> generalOptions;

	private final MySqlGateway mysqlGateway;

	public OmegaApplication() {
		this.pluginIndexes = new HashMap<String, Long>();
		this.registeredPlugin = new HashMap<Long, OmegaPlugin>();
		this.pluginIndex = 0;

		this.optionsFileManager = new OmegaOptionsFileManager();
		this.generalOptions = this.optionsFileManager.getGeneralOptions();

		this.mysqlGateway = new MySqlGateway();

		// TODO load data here
		this.omegaData = new OmegaData();
		this.loadedData = new OmegaLoadedData();
		this.loadedAnalysisRuns = new ArrayList<OmegaAnalysisRun>();
		this.gateway = null;
		this.experimenter = null;

		this.registerCorePlugins();

		this.gui = new OmegaGUIFrame(this);
		this.gui.initialize(this.registeredPlugin);
		this.gui.setSize(1200, 800);

	}

	private void registerCorePlugins() {
		this.registerPlugin(new OmeroPlugin());
		this.registerPlugin(new OmegaDataBrowserPlugin());
		this.registerPlugin(new SPTPlugin());
		this.registerPlugin(new TrajectoryManagerPlugin());

		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
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
		}
	}

	private void registerPlugin(final OmegaPlugin plugin) {
		plugin.addOmegaPluginListener(this);
		final String name = plugin.getName();
		final long index = this.pluginIndex;
		this.pluginIndex++;
		this.registeredPlugin.put(index, plugin);
		this.pluginIndexes.put(name, index);
	}

	protected void showGUI() {
		this.gui.setVisible(true);
	}

	public OmegaPlugin getPlugin(final long pluginIndex) {
		return this.registeredPlugin.get(pluginIndex);
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

		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			this.optionsFileManager.addOptions(plugin.getOptionsCategory(),
			        plugin.getPluginOptions());
		}
		this.optionsFileManager.saveOptionsToFile();
	}

	protected Map<Long, OmegaPlugin> getPlugins() {
		return this.registeredPlugin;
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
		} else if (event instanceof OmegaApplicationBufferedImageEvent) {
			this.handleOmegaApplicationBufferedImageEvent((OmegaApplicationBufferedImageEvent) event);
		}
	}

	private void handleOmegaApplicationBufferedImageEvent(
	        final OmegaApplicationBufferedImageEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			if (plugin instanceof OmegaTrajectoryManagerPlugin) {
				((OmegaTrajectoryManagerPlugin) plugin).setBufferedImage(event
				        .getBufferedImage());
			}
		}
	}

	private void handleOmegaApplicationImageSelectionEvent(
	        final OmegaApplicationImageSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			if (plugin instanceof OmegaTrajectoryManagerPlugin) {
				((OmegaTrajectoryManagerPlugin) plugin).selectImage(event
				        .getImage());
			}
		}
	}

	private void handleOmegaApplicationParticleDetectionRunSelectionEvent(
	        final OmegaApplicationParticleDetectionRunSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			if (plugin instanceof OmegaTrajectoryManagerPlugin) {
				((OmegaTrajectoryManagerPlugin) plugin)
				        .selectParticleDetectionRun(event.getAnalysisRun());
			}
		}
	}

	private void handleOmegaApplicationParticleLinkingRunSelectionEvent(
	        final OmegaApplicationParticleLinkingRunSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			if (plugin instanceof OmegaTrajectoryManagerPlugin) {
				((OmegaTrajectoryManagerPlugin) plugin)
				        .selectParticleLinkingRun(event.getAnalysisRun());
			}
		}
	}

	private void handleOmegaApplicationTrajectoriesEvent(
	        final OmegaApplicationTrajectoriesEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			if (plugin instanceof OmegaTrajectoryManagerPlugin) {
				((OmegaTrajectoryManagerPlugin) plugin).updateTrajectories(
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
			this.handleOmegaAlgorithmPluginEvent((OmegaAlgorithmPluginEvent) event);
		} else if (plugin instanceof OmegaTrajectoryManagerPlugin) {
			if (event instanceof OmegaTMPluginImageSelectionEvent) {
				this.handleOmegaTMPluginImageSelectionEvent((OmegaTMPluginImageSelectionEvent) event);
			} else if (event instanceof OmegaTMPluginParticleDetectionRunSelectionEvent) {
				this.handleOmegaTMPluginParticleDetectionRunSelectionEvent((OmegaTMPluginParticleDetectionRunSelectionEvent) event);
			} else if (event instanceof OmegaTMPluginParticleLinkingRunSelectionEvent) {
				this.handleOmegaTMPluginParticleLinkingRunSelectionEvent((OmegaTMPluginParticleLinkingRunSelectionEvent) event);
			} else {
				this.handleOmegaTMPluginTrajectoriesEvent((OmegaTMPluginTrajectoriesEvent) event);
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
		for (final OmegaParameter param : event.getParameters()) {
			algoSpec.addParameters(param);
		}

		// TODO separare i parameteri e fare 2 algoSpec diverse

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

		this.updateGUI(event.getSource(), false);
	}

	private void handleOmegaLoaderPluginGatewayEvent(
	        final OmegaGatewayEvent event) {
		// TODO to check why replicated in 2 places
		switch (event.getStatus()) {
		case OmegaGatewayEvent.STATUS_CREATED:
			this.experimenter = null;
			this.gateway = ((OmegaLoaderPlugin) event.getSource()).getGateway();
			for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
				if (plugin instanceof OmegaParticleTrackingPlugin) {
					((OmegaParticleTrackingPlugin) plugin)
					        .setGateway(this.gateway);
				}
			}
			return;
		case OmegaGatewayEvent.STATUS_DESTROYED:
			this.gateway = null;
			this.experimenter = null;
			for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
				if (plugin instanceof OmegaParticleTrackingPlugin) {
					((OmegaParticleTrackingPlugin) plugin).setGateway(null);
				}
			}
			return;
		case OmegaGatewayEvent.STATUS_CONNECTED:
			this.gateway = ((OmegaLoaderPlugin) event.getSource()).getGateway();
			for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
				if (plugin instanceof OmegaParticleTrackingPlugin) {
					((OmegaParticleTrackingPlugin) plugin)
					        .setGateway(this.gateway);
				}
			}
			this.experimenter = event.getExperimenter();
			return;
		case OmegaGatewayEvent.STATUS_DISCONNECTED:
			this.experimenter = null;
			return;
		}
	}

	private void handleOmegaTMPluginImageSelectionEvent(
	        final OmegaTMPluginImageSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			if (event.getSource().equals(plugin)) {
				continue;
				// TODO add select image method where needed
			}
		}
		this.gui.selectImage(event.getImage());
	}

	private void handleOmegaTMPluginParticleDetectionRunSelectionEvent(
	        final OmegaTMPluginParticleDetectionRunSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			if (event.getSource().equals(plugin)) {
				continue;
				// TODO add select image method where needed
			}
		}
		this.gui.selectParticleDetectionRun(event.getAnalysisRun());
	}

	private void handleOmegaTMPluginParticleLinkingRunSelectionEvent(
	        final OmegaTMPluginParticleLinkingRunSelectionEvent event) {
		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			if (event.getSource().equals(plugin)) {
				continue;
				// TODO add select image method where needed
			}
		}
		this.gui.selectParticleLinkingRun(event.getAnalysisRun());
	}

	private void handleOmegaTMPluginTrajectoriesEvent(
	        final OmegaTMPluginTrajectoriesEvent event) {
		this.gui.updateTrajectories(event.getTrajectories(),
		        event.isSelectionEvent());
	}

	private void handleOmegaLoaderPluginDataChangedEvent(
	        final OmegaDataChangedEvent event) {
		// TODO integrare dati caricati
		// final OmegaData loadedData = event.getLoadedData();
		// this.omegaData.mergeData(loadedData);
		this.loadSelectedData(event.getSelectedData());
		this.updateGUI(event.getSource(), event.getSelectedData().size() > 0);
	}

	private void handleOmegaBrowserPluginDataChangedEvent(
	        final OmegaDataChangedEvent event) {
		this.updateGUI(event.getSource(), true);
	}

	private void updateGUI(final OmegaPlugin source, final boolean dataLoaded) {
		if (dataLoaded) {
			for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
				if (plugin.equals(source)) {
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

		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			// if (dataLoaded && (plugin instanceof OmegaBrowserPlugin)) {
			// continue;
			// }
			if (plugin.equals(source)) {
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
			// TODO unable to connect case to manage
			ex.printStackTrace();
			return;
		}

		for (final OmegaProject project : this.omegaData.getProjects()) {
			// Load project
			for (final OmegaDataset dataset : project.getDatasets()) {
				// Load dataset
				for (final OmegaImage image : dataset.getImages()) {
					try {
						this.mysqlGateway.loadImages(image);
					} catch (final SQLException ex) {
						ex.printStackTrace();
					} catch (final ParseException ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		try {
			this.mysqlGateway.disconnect();
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void saveAnalysis() {
		final OmegaDBServerInformation serverInfo = this.gui
		        .getOmegaDBServerInformation();
		final OmegaLoginCredentials loginCred = this.gui
		        .getOmegaLoginCredentials();
		this.mysqlGateway.setServerInformation(serverInfo);
		this.mysqlGateway.setLoginCredentials(loginCred);

		try {
			this.mysqlGateway.connect();
		} catch (ClassNotFoundException | SQLException ex) {
			// TODO unable to connect case to manage
			ex.printStackTrace();
			return;
		}

		boolean error = false;
		try {
			for (final OmegaProject project : this.omegaData.getProjects()) {
				for (final OmegaDataset dataset : project.getDatasets()) {
					for (final OmegaImage image : dataset.getImages()) {
						for (final OmegaAnalysisRun analysisRun : image
						        .getAnalysisRuns()) {
							this.mysqlGateway.saveAnalysisRun(image,
							        analysisRun);
							this.saveInnerAnalysis(analysisRun);
						}
					}
				}
			}
		} catch (final SQLException ex) {
			error = true;
			ex.printStackTrace();
		}

		try {
			if (error) {
				this.mysqlGateway.rollback();
			} else {
				this.mysqlGateway.commit();
			}
			this.mysqlGateway.disconnect();
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveInnerAnalysis(final OmegaAnalysisRun analysisRun)
	        throws SQLException {
		for (final OmegaAnalysisRun innerAnalysisRun : analysisRun
		        .getAnalysisRuns()) {
			final Integer id = new Integer(analysisRun.getElementID()
			        .toString());
			this.mysqlGateway.saveAnalysisRun(id, innerAnalysisRun);
			this.saveInnerAnalysis(innerAnalysisRun);
		}
	}
}
