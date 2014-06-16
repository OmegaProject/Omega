package edu.umassmed.omega.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.umassmed.omega.commons.OmegaAlgorithmPlugin;
import edu.umassmed.omega.commons.OmegaBrowserPlugin;
import edu.umassmed.omega.commons.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.OmegaDataManagerPlugin;
import edu.umassmed.omega.commons.OmegaLoaderPlugin;
import edu.umassmed.omega.commons.OmegaParticleTrackingPlugin;
import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.commons.eventSystem.OmegaAlgorithmPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaDataChangedEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaGatewayEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaParticleDetectionResultsEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaParticleLinkingResultsEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaParticleTrackingResultsEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaPluginListener;
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
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.omegaDataBrowserPlugin.OmegaDataBrowserPlugin;
import edu.umassmed.omega.omeroPlugin.OmeroPlugin;
import edu.umassmed.omega.sptPlugin.SPTPlugin;

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

	public OmegaApplication() {
		this.pluginIndexes = new HashMap<String, Long>();
		this.registeredPlugin = new HashMap<Long, OmegaPlugin>();
		this.pluginIndex = 0;

		this.optionsFileManager = new OmegaOptionsFileManager();

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

		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			final String optionsCategory = plugin.getOptionsCategory();
			plugin.addPluginOptions(this.optionsFileManager
			        .getOptions(optionsCategory));
			if (plugin instanceof OmegaDataManagerPlugin) {
				((OmegaDataManagerPlugin) plugin).setMainData(this.omegaData);
			}
			if (plugin instanceof OmegaBrowserPlugin) {
				((OmegaBrowserPlugin) plugin).setLoadedData(this.loadedData);
				((OmegaBrowserPlugin) plugin)
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

	public void saveOptions() {
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
		        UUID.randomUUID().getMostSignificantBits(),
		        source.getAlgorithmName(), source.getAlgorithmVersion(),
		        source.getAlgorithmDescription(), source.getAlgorithmAuthor(),
		        source.getAlgorithmPublicationDate());
		final OmegaAlgorithmSpecification algoSpec = new OmegaAlgorithmSpecification(
		        UUID.randomUUID().getMostSignificantBits(), algoInfo);
		for (final OmegaParameter param : event.getParameters()) {
			algoSpec.addParameters(param);
		}

		// TODO separare i parameteri e fare 2 algoSpec diverse

		OmegaAnalysisRun analysisRun;
		if (event instanceof OmegaParticleTrackingResultsEvent) {
			analysisRun = new OmegaParticleDetectionRun(UUID.randomUUID()
			        .getMostSignificantBits(), this.experimenter, algoSpec,
			        ((OmegaParticleTrackingResultsEvent) event)
			                .getResultingParticles());
			final OmegaAnalysisRun subAnalysisRun = new OmegaParticleLinkingRun(
			        UUID.randomUUID().getMostSignificantBits(),
			        this.experimenter, algoSpec,
			        ((OmegaParticleTrackingResultsEvent) event)
			                .getResultingTrajectories());
			this.loadedAnalysisRuns.add(subAnalysisRun);
			analysisRun.addAnalysisRun(subAnalysisRun);
		} else if (event instanceof OmegaParticleDetectionResultsEvent) {
			analysisRun = new OmegaParticleDetectionRun(UUID.randomUUID()
			        .getMostSignificantBits(), this.experimenter, algoSpec,
			        ((OmegaParticleDetectionResultsEvent) event)
			                .getResultingParticles());
		} else if (event instanceof OmegaParticleLinkingResultsEvent) {
			analysisRun = new OmegaParticleLinkingRun(UUID.randomUUID()
			        .getMostSignificantBits(), this.experimenter, algoSpec,
			        ((OmegaParticleLinkingResultsEvent) event)
			                .getResultingTrajectories());
		} else
			// TODO gestire errore
			return;

		((OmegaAnalysisRunContainer) element).addAnalysisRun(analysisRun);
		this.loadedAnalysisRuns.add(analysisRun);

		this.updateGUI(false);
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

	private void handleOmegaLoaderPluginDataChangedEvent(
	        final OmegaDataChangedEvent event) {
		// TODO integrare dati caricati
		// final OmegaData loadedData = event.getLoadedData();
		// this.omegaData.mergeData(loadedData);
		this.loadSelectedData(event.getSelectedData());
		this.updateGUI(event.getSelectedData().size() > 0);
	}

	private void handleOmegaBrowserPluginDataChangedEvent(
	        final OmegaDataChangedEvent event) {
		this.updateGUI(true);
	}

	private void updateGUI(final boolean dataLoaded) {
		if (dataLoaded) {
			for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
				if (plugin instanceof OmegaParticleTrackingPlugin) {
					((OmegaParticleTrackingPlugin) plugin)
					        .setLoadedImages(this.loadedData.getImages());
				}
			}
			this.gui.updateGUI(this.loadedData, this.loadedAnalysisRuns,
			        this.gateway);
		}

		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			if (dataLoaded && (plugin instanceof OmegaBrowserPlugin)) {
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
}
