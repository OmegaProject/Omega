package edu.umassmed.omega.core.runnables;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.data.analysisRunElements.AnalysisRunType;
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
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresVelocityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.data.coreElements.OmegaDataset;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaExperimenter;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.coreElements.OmegaPlane;
import edu.umassmed.omega.commons.data.coreElements.OmegaProject;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaParticle;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationType;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.gui.dialogs.GenericProgressDialog;
import edu.umassmed.omega.core.OmegaApplication;
import edu.umassmed.omega.core.mysql.OmegaMySqlCostants;
import edu.umassmed.omega.core.mysql.OmegaMySqlReader;

public class OmegaDBLoader extends OmegaDBRunnable {

	private final List<OmegaProject> projectsToLoad;

	private int counter;
	private final Map<Object, Integer> counters;
	private final List<Long> projectsToBeLoaded, datasetsToBeLoaded,
			imagesToBeLoaded, pixelsToBeLoaded, analysisToBeLoaded,
			experimentersToBeLoaded, personsToBeLoaded, algoInfosToBeLoaded,
			framesToBeLoaded, algoSpecsToBeLoaded;
	private final Map<Long, Integer> projectElementSizeToLoad,
			datasetElementSizeToLoad, imageElementSizeToLoad,
			analysisElementSizeToLoad;
	private final int loadType;

	private final Map<Long, OmegaPlane> loadedPlanes;
	private final Map<Long, OmegaROI> loadedROIs;
	private final Map<Long, OmegaTrajectory> loadedTracks;
	private final Map<Long, OmegaSegment> loadedSegments;
	private final Map<Long, OmegaSegmentationTypes> loadedSegmentationTypes;
	private final Map<Long, OmegaSegmentationType> loadedSegmentationType;
	private final Map<Long, OmegaAnalysisRun> loadedAnalysisRun;

	public OmegaDBLoader(final OmegaApplication omegaApp,
			final OmegaMySqlReader reader, final GenericProgressDialog dialog,
			final List<OmegaProject> projects, final int loadType) {
		super(omegaApp, reader, dialog);
		this.projectsToLoad = projects;

		this.loadType = loadType;

		this.projectsToBeLoaded = new ArrayList<Long>();
		this.datasetsToBeLoaded = new ArrayList<Long>();
		this.imagesToBeLoaded = new ArrayList<Long>();
		this.pixelsToBeLoaded = new ArrayList<Long>();
		this.analysisToBeLoaded = new ArrayList<Long>();
		this.experimentersToBeLoaded = new ArrayList<Long>();
		this.personsToBeLoaded = new ArrayList<Long>();
		this.algoInfosToBeLoaded = new ArrayList<Long>();
		this.framesToBeLoaded = new ArrayList<Long>();
		this.algoSpecsToBeLoaded = new ArrayList<Long>();
		this.counters = new LinkedHashMap<Object, Integer>();

		this.projectElementSizeToLoad = new LinkedHashMap<Long, Integer>();
		this.datasetElementSizeToLoad = new LinkedHashMap<Long, Integer>();
		this.imageElementSizeToLoad = new LinkedHashMap<Long, Integer>();
		this.analysisElementSizeToLoad = new LinkedHashMap<Long, Integer>();

		this.loadedPlanes = new LinkedHashMap<Long, OmegaPlane>();
		this.loadedROIs = new LinkedHashMap<Long, OmegaROI>();
		this.loadedTracks = new LinkedHashMap<Long, OmegaTrajectory>();
		this.loadedSegments = new LinkedHashMap<Long, OmegaSegment>();
		this.loadedSegmentationTypes = new LinkedHashMap<Long, OmegaSegmentationTypes>();
		this.loadedSegmentationType = new LinkedHashMap<Long, OmegaSegmentationType>();
		this.loadedAnalysisRun = new LinkedHashMap<Long, OmegaAnalysisRun>();
	}

	@Override
	public void run() {
		this.counter = 0;
		this.updateMessage(null, "Preparing for loading process...");
		if (this.loadType == OmegaDBRunnable.LOAD_TYPE_LOADED_IMAGE) {
			try {
				this.prepareElementsToLoad();
				int size = 0;
				for (final Long id : this.projectElementSizeToLoad.keySet()) {
					size += this.projectElementSizeToLoad.get(id);
				}
				// final int size = this.projectsToBeLoaded.size()
				// + this.datasetsToBeLoaded.size()
				// + this.imagesToBeLoaded.size()
				// + this.pixelsToBeLoaded.size()
				// + this.analysisToBeLoaded.size()
				// + this.experimentersToBeLoaded.size()
				// + this.framesToBeLoaded.size()
				// + this.algoInfosToBeLoaded.size()
				// + this.algoSpecsToBeLoaded.size()
				// + this.personsToBeLoaded.size();
				this.updateMaxProgress(null, size);
				this.updateMessage(null, "Loading...");
				this.loadElements();
			} catch (final SQLException e) {
				// this.setErrorOccured();
				// this.setDialogClosable();
				e.printStackTrace();
			} catch (final ParseException e) {
				// this.setErrorOccured();
				// this.setDialogClosable();
			}
		} else {

		}
		this.setDialogClosable();
		this.notifyProcessEndToApplication();
	}

	private void loadElements() throws SQLException, ParseException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<OmegaElement> parents = new ArrayList<OmegaElement>();
		if (this.projectsToLoad.isEmpty()) {
			this.updateMessage(0, "Nothing to load");
			this.updateMaxProgress(0, 1);
			this.updateCurrentProgress(0, 1);
			this.updateMessage(1, "Nothing to load");
			this.updateMaxProgress(1, 1);
			this.updateCurrentProgress(1, 1);
			this.updateMessage(2, "Nothing to load");
			this.updateMaxProgress(2, 1);
			this.updateCurrentProgress(2, 1);
			this.updateMessage(3, "Nothing to load");
			this.updateMaxProgress(3, 1);
			this.updateCurrentProgress(3, 1);
			this.updateMessage(4, "Nothing to load");
			this.updateMaxProgress(4, 1);
			this.updateCurrentProgress(4, 1);
			return;
		}
		for (final OmegaProject project : this.projectsToLoad) {
			int projectCounter = 0;
			this.counters.put(project, projectCounter);
			parents.add(project);
			this.updateMessage(project, "Loading project " + project.getName()
					+ "...");
			long projectID = project.getElementID();
			final long projectOMEID = project.getOmeroId();
			if (projectID == -1) {
				projectID = reader.getProjectID(projectOMEID);
				if (this.projectsToBeLoaded.contains(projectID)) {
					project.setElementID(projectID);
					this.updateMaxProgress(project,
							this.projectElementSizeToLoad.get(projectID));
					projectCounter++;
					this.updateCurrentProgress(project, projectCounter);
					this.counters.put(project, projectCounter);
					this.counter++;
					this.updateCurrentProgress(null, this.counter);
				}
			}
			for (final OmegaDataset dataset : project.getDatasets()) {
				int datasetCounter = 0;
				this.counters.put(dataset, datasetCounter);
				parents.add(dataset);
				this.updateMessage(dataset,
						"Loading dataset " + dataset.getName() + "...");
				long datasetID = dataset.getElementID();
				final long datasetOMEID = dataset.getOmeroId();
				if (datasetID == -1) {
					datasetID = reader.getDatasetID(datasetOMEID);
					if (this.datasetsToBeLoaded.contains(datasetID)) {
						dataset.setElementID(datasetID);
						this.updateMaxProgress(dataset,
								this.datasetElementSizeToLoad.get(datasetID));
						projectCounter++;
						this.updateCurrentProgress(project, projectCounter);
						this.counters.put(project, projectCounter);
						datasetCounter++;
						this.updateCurrentProgress(dataset, datasetCounter);
						this.counters.put(dataset, datasetCounter);
						this.counter++;
						this.updateCurrentProgress(null, this.counter);
					}
				}
				for (final OmegaImage image : dataset.getImages()) {
					int imageCounter = 0;
					this.counters.put(image, imageCounter);
					parents.add(image);
					long imageID = image.getElementID();
					final long imageOMEID = image.getOmeroId();
					if ((imageID == -1)) {
						imageID = reader.getImageID(imageOMEID);
					}
					this.updateMessage(image,
							"Loading image " + image.getName() + "...");
					this.updateMaxProgress(image,
							this.imageElementSizeToLoad.get(imageID));
					long experimenterID = image.getExperimenter()
							.getElementID();
					final long expOMEID = image.getExperimenter().getOmeroId();
					if (experimenterID == -1) {
						experimenterID = reader.getExperimenterID(expOMEID);
						if (this.experimentersToBeLoaded
								.contains(experimenterID)) {
							// final long personID =
							// reader.getExperimenterPersonID(experimenterID);
							image.getExperimenter()
									.setElementID(experimenterID);
							projectCounter++;
							this.updateCurrentProgress(project, projectCounter);
							this.counters.put(project, projectCounter);
							datasetCounter++;
							this.updateCurrentProgress(dataset, datasetCounter);
							this.counters.put(dataset, datasetCounter);
							imageCounter++;
							this.updateCurrentProgress(image, imageCounter);
							this.counters.put(image, imageCounter);
							this.counter++;
							this.updateCurrentProgress(null, this.counter);
						}
					}
					if (this.imagesToBeLoaded.contains(imageID)) {
						image.setElementID(imageID);
						// writer.saveImageDatasetLink(image, dataset);
						projectCounter++;
						this.updateCurrentProgress(project, projectCounter);
						this.counters.put(project, projectCounter);
						datasetCounter++;
						this.updateCurrentProgress(dataset, datasetCounter);
						this.counters.put(dataset, datasetCounter);
						imageCounter++;
						this.updateCurrentProgress(image, imageCounter);
						this.counters.put(image, imageCounter);
						this.counter++;
						this.updateCurrentProgress(null, this.counter);
					}
					for (final OmegaImagePixels pixels : image.getPixels()) {
						long imagePixelsID = pixels.getElementID();
						final long imagePixelsOMEID = pixels.getOmeroId();
						if (imagePixelsID == -1) {
							imagePixelsID = reader
									.getImagePixelsID(imagePixelsOMEID);
							if (this.pixelsToBeLoaded.contains(imagePixelsID)) {
								pixels.setElementID(imagePixelsID);
								projectCounter++;
								this.updateCurrentProgress(project,
										projectCounter);
								this.counters.put(project, projectCounter);
								datasetCounter++;
								this.updateCurrentProgress(dataset,
										datasetCounter);
								this.counters.put(dataset, datasetCounter);
								imageCounter++;
								this.updateCurrentProgress(image, imageCounter);
								this.counters.put(image, imageCounter);
								this.counter++;
								this.updateCurrentProgress(null, this.counter);
							}
						}
						final List<Long> frameIDs = new ArrayList<Long>();
						// TODO modificare come vengono gestiti i frame
						// ad esempio caricarli tutti e poi assegnare gli id a
						// quelli gia presenti?
						for (int c = 0; c <= pixels.getSizeC(); c++) {
							for (int z = 0; z <= pixels.getSizeZ(); z++) {
								for (final OmegaPlane frame : pixels.getFrames(
										c, z)) {
									long frameID = frame.getElementID();
									if (frameID == -1) {
										frameID = reader.getFrameID(
												imagePixelsID, c, z);
										frame.setElementID(frameID);
									}
									frameIDs.add(frameID);
									projectCounter++;
									this.updateCurrentProgress(project,
											projectCounter);
									this.counters.put(project, projectCounter);
									datasetCounter++;
									this.updateCurrentProgress(dataset,
											datasetCounter);
									this.counters.put(dataset, datasetCounter);
									imageCounter++;
									this.updateCurrentProgress(image,
											imageCounter);
									this.counters.put(image, imageCounter);
									this.counter++;
									this.updateCurrentProgress(null,
											this.counter);
									this.loadFrameElements(
											frame.getElementID(), frame,
											OmegaMySqlCostants.FRAME_ID_FIELD,
											parents);
								}
							}
						}

						this.loadImagePixelsElements(pixels.getElementID(),
								pixels,
								OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD,
								parents);
					}
					this.loadImageElements(image.getElementID(), image,
							OmegaMySqlCostants.IMAGE_ID_FIELD, parents);
					parents.remove(image);
				}
				// this.updateMessage(dataset,
				// "Saving dataset " + dataset.getName() + "...");
				// this.updateMaxProgress(dataset,
				// this.elementSizeToSave.get(dataset));
				this.loadDatasetElements(dataset.getElementID(), dataset,
						OmegaMySqlCostants.DATASET_ID_FIELD, parents);
				parents.remove(dataset);
			}
			// this.updateMessage(project, "Saving project " + project.getName()
			// + "...");
			// this.updateMaxProgress(project,
			// this.elementSizeToSave.get(project));
			this.loadProjectElements(project.getElementID(), project,
					OmegaMySqlCostants.PROJECT_ID_FIELD, parents);
			parents.remove(project);
		}
	}

	private void loadProjectElements(final Long containerID,
			final OmegaAnalysisRunContainerInterface container,
			final String parentElementField, final List<OmegaElement> parents)
			throws SQLException, ParseException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<Long> analysisIDs = reader
				.getProjectContainerAnalysisIDs(containerID);
		this.loadElements(containerID, container, analysisIDs,
				parentElementField, parents);
	}

	private void loadDatasetElements(final Long containerID,
			final OmegaAnalysisRunContainerInterface container,
			final String parentElementField, final List<OmegaElement> parents)
			throws SQLException, ParseException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<Long> analysisIDs = reader
				.getDatasetContainerAnalysisIDs(containerID);
		this.loadElements(containerID, container, analysisIDs,
				parentElementField, parents);
	}

	private void loadImageElements(final Long containerID,
			final OmegaAnalysisRunContainerInterface container,
			final String parentElementField, final List<OmegaElement> parents)
			throws SQLException, ParseException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<Long> analysisIDs = reader
				.getImageContainerAnalysisIDs(containerID);
		this.loadElements(containerID, container, analysisIDs,
				parentElementField, parents);
	}

	private void loadImagePixelsElements(final Long containerID,
			final OmegaAnalysisRunContainerInterface container,
			final String parentElementField, final List<OmegaElement> parents)
			throws SQLException, ParseException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<Long> analysisIDs = reader
				.getImagePixelsContainerAnalysisIDs(containerID);
		this.loadElements(containerID, container, analysisIDs,
				parentElementField, parents);
	}

	private void loadFrameElements(final Long containerID,
			final OmegaAnalysisRunContainerInterface container,
			final String parentElementField, final List<OmegaElement> parents)
			throws SQLException, ParseException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<Long> analysisIDs = reader
				.getFrameContainerAnalysisIDs(containerID);
		this.loadElements(containerID, container, analysisIDs,
				parentElementField, parents);
	}

	private void loadAnalysisElements(final Long containerID,
			final OmegaAnalysisRunContainerInterface container,
			final String parentElementField, final List<OmegaElement> parents)
			throws SQLException, ParseException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<Long> analysisIDs = reader
				.getAnalysisContainerAnalysisIDs(containerID);
		this.loadElements(containerID, container, analysisIDs,
				parentElementField, parents);
	}

	private void loadElements(final long containerID,
			final OmegaAnalysisRunContainerInterface container,
			final List<Long> analysisIDs, final String parentElementField,
			final List<OmegaElement> parents) throws SQLException,
			ParseException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		for (int i = 0; i < analysisIDs.size(); i++) {
			final long analysisID = analysisIDs.get(i);
			this.updateMessage(1, "Loading analysis " + analysisID + "...");
			final int elements = this.analysisElementSizeToLoad.get(analysisID);
			this.updateMaxProgress(1, elements);
			int analysisCounter = 0;
			this.counters.put(analysisID, analysisCounter);
			this.updateCurrentProgress(1, analysisCounter);
			final List<Long> expIDs = reader
					.getAnalysisExperimenter(analysisID);
			Long experimenterID;
			if (expIDs.size() > 1) {
				// TODO error
			}
			experimenterID = expIDs.get(0);
			OmegaExperimenter exp = null;
			if ((experimenterID != -1)
					&& this.experimentersToBeLoaded.contains(experimenterID)) {
				exp = reader.loadExperimenter(experimenterID);
				exp.setElementID(experimenterID);
				for (final OmegaElement element : parents) {
					int c = this.counters.get(element);
					c++;
					this.counters.put(element, c);
					this.updateCurrentProgress(element, c);
				}
				analysisCounter++;
				this.updateCurrentProgress(1, analysisCounter);
				this.counters.put(analysisID, analysisCounter);
				this.counter++;
				this.updateCurrentProgress(null, this.counter);
			}
			if (exp == null) {
				// TODO error
			}
			final List<Long> algoSpecIDs = reader
					.getAnalysisAlgorithmSpecificationID(analysisID);
			if (algoSpecIDs.size() > 1) {
				// TODO error
			}
			final long algoSpecID = algoSpecIDs.get(0);
			final List<Long> algoInfoIDs = reader
					.getAlgorithmSpecificationInformationID(algoSpecID);
			if (algoInfoIDs.size() > 1) {
				// TODO error
			}
			final long algoInfoID = algoInfoIDs.get(0);
			// final List<Long> authorIDs = reader
			// .getAlgorithmInformationAuthorID(algoInfoID);
			// if (authorIDs.size() > 1) {
			// // TODO error
			// }
			// final long authorID = authorIDs.get(0);
			// OmegaPerson author = null;
			// if ((authorID != -1) &&
			// this.personsToBeLoaded.contains(authorID)) {
			// author = reader.loadPerson(authorID);
			// for (final OmegaElement element : parents) {
			// int c = this.counters.get(element);
			// c++;
			// this.counters.put(element, c);
			// this.updateCurrentProgress(element, c);
			// }
			// analysisCounter++;
			// this.updateCurrentProgress(1, analysisCounter);
			// this.counters.put(analysisID, analysisCounter);
			// this.counter++;
			// this.updateCurrentProgress(null, this.counter);
			// }
			// if (author == null) {
			// // TODO ERROR THROW EXCEPTION
			// }
			OmegaAlgorithmInformation algoInfo = null;
			if ((algoInfoID != -1)
					&& this.algoInfosToBeLoaded.contains(algoInfoID)) {
				algoInfo = reader.loadAlgorithmInformation(algoInfoID);
				for (final OmegaElement element : parents) {
					int c = this.counters.get(element);
					c++;
					this.counters.put(element, c);
					this.updateCurrentProgress(element, c);
				}
				analysisCounter++;
				this.updateCurrentProgress(1, analysisCounter);
				this.counters.put(analysisID, analysisCounter);
				this.counter++;
				this.updateCurrentProgress(null, this.counter);
			}
			if (algoInfo == null) {
				// TODO ERROR THROW EXCEPTION
			}
			OmegaRunDefinition algoSpec = null;
			final List<Long> paramsID = reader.getParameterIDs(algoSpecID);
			final List<OmegaParameter> params = new ArrayList<OmegaParameter>();
			for (final Long paramID : paramsID) {
				final OmegaParameter param = reader.loadParameter(paramID);
				params.add(param);
			}
			algoSpec = new OmegaRunDefinition(algoInfo, params);
			algoSpec.setElementID(algoSpecID);
			for (final OmegaElement element : parents) {
				int c = this.counters.get(element);
				c++;
				this.counters.put(element, c);
				this.updateCurrentProgress(element, c);
			}
			analysisCounter++;
			this.updateCurrentProgress(1, analysisCounter);
			this.counters.put(analysisID, analysisCounter);
			this.counter++;
			this.updateCurrentProgress(null, this.counter);
			final AnalysisRunType type = reader.getAnalysisType(analysisID);
			OmegaAnalysisRun analysisRun = null;
			if (this.loadedAnalysisRun.containsKey(analysisID)) {
				analysisRun = this.loadedAnalysisRun.get(analysisID);
			} else {
				if (type == AnalysisRunType.OmegaSNRRun) {
					analysisRun = this.loadSNRRun(reader, container,
							analysisID, exp, algoSpec);
				} else if (type == AnalysisRunType.OmegaTrackingMeasuresDiffusivityRun) {
					analysisRun = this.loadTrackingMeasuresDiffusivityRun(
							reader, container, analysisID, exp, algoSpec);
				} else if (type == AnalysisRunType.OmegaTrackingMeasuresMobilityRun) {
					analysisRun = this.loadTrackingMeasuresMobilityRun(reader,
							container, analysisID, exp, algoSpec);
				} else if (type == AnalysisRunType.OmegaTrackingMeasuresVelocityRun) {
					analysisRun = this.loadTrackingMeasuresVelocityRun(reader,
							container, analysisID, exp, algoSpec);
				} else if (type == AnalysisRunType.OmegaTrackingMeasuresIntensityRun) {
					analysisRun = this.loadTrackingMeasuresIntensityRun(reader,
							container, analysisID, exp, algoSpec);
				} else if (type == AnalysisRunType.OmegaTrajectoriesSegmentationRun) {
					analysisRun = this.loadTrajectoriesSegmentationRun(reader,
							container, analysisID, exp, algoSpec);
				} else if (type == AnalysisRunType.OmegaTrajectoriesRelinkingRun) {
					analysisRun = this.loadTrajectoriesRelinkingRun(reader,
							container, analysisID, exp, algoSpec);
				} else if (type == AnalysisRunType.OmegaParticleLinkingRun) {
					analysisRun = this.loadParticleLinkingRun(reader,
							container, analysisID, exp, algoSpec);

				} else if (type == AnalysisRunType.OmegaParticleDetectionRun) {
					analysisRun = this.loadParticleDetectionRun(reader,
							container, analysisID, exp, algoSpec);

				} else {

				}
				this.loadedAnalysisRun.put(analysisID, analysisRun);
			}
			for (final OmegaElement element : parents) {
				int c = this.counters.get(element);
				c++;
				this.counters.put(element, c);
				this.updateCurrentProgress(element, c);
			}
			analysisCounter++;
			this.updateCurrentProgress(1, analysisCounter);
			this.counters.put(analysisID, analysisCounter);
			this.counter++;
			this.updateCurrentProgress(null, this.counter);
			container.addAnalysisRun(analysisRun);
			// parents.add(analysisRun);
			this.loadAnalysisElements(analysisRun.getElementID(), analysisRun,
					OmegaMySqlCostants.ANALYSIS_ID_FIELD, parents);
			// parents.remove(analysisRun);
		}
	}

	private OmegaSNRRun loadSNRRun(final OmegaMySqlReader reader,
			final OmegaAnalysisRunContainerInterface container, final long analysisID,
			final OmegaExperimenter exp, final OmegaRunDefinition algoSpec)
			throws SQLException, ParseException {
		final Map<Long, Double> resultingImageAvgCenterSignal = reader
				.loadSNRPlaneAvgCenterSignal(analysisID);
		final Map<OmegaPlane, Double> resultingImageAvgCenterSignalMap = this
				.transformPlaneDoubleValuesMap(resultingImageAvgCenterSignal);
		final Map<Long, Double> resultingImageAvgPeakSignal = reader
				.loadSNRPlaneAvgPeakSignal(analysisID);
		final Map<OmegaPlane, Double> resultingImageAvgPeakSignalMap = this
				.transformPlaneDoubleValuesMap(resultingImageAvgPeakSignal);
		final Map<Long, Double> resultingImageAvgMeanSignal = reader
				.loadSNRPlaneAvgMeanSignal(analysisID);
		final Map<OmegaPlane, Double> resultingImageAvgMeanSignalMap = this
				.transformPlaneDoubleValuesMap(resultingImageAvgMeanSignal);
		final Map<Long, Double> resultingImageBGR = reader
				.loadSNRPlaneBGR(analysisID);
		final Map<OmegaPlane, Double> resultingImageBGRMap = this
				.transformPlaneDoubleValuesMap(resultingImageBGR);
		final Map<Long, Double> resultingImageNoise = reader
				.loadSNRPlaneNoises(analysisID);
		final Map<OmegaPlane, Double> resultingImageNoiseMap = this
				.transformPlaneDoubleValuesMap(resultingImageNoise);
		final Map<Long, Double> resultingImageAverageSNR = reader
				.loadSNRPlaneAverageSNR(analysisID);
		final Map<OmegaPlane, Double> resultingImageAverageSNRMap = this
				.transformPlaneDoubleValuesMap(resultingImageAverageSNR);
		final Map<Long, Double> resultingImageMinimumSNR = reader
				.loadSNRPlaneMinSNR(analysisID);
		final Map<OmegaPlane, Double> resultingImageMinimumSNRMap = this
				.transformPlaneDoubleValuesMap(resultingImageMinimumSNR);
		final Map<Long, Double> resultingImageMaximumSNR = reader
				.loadSNRPlaneMaxSNR(analysisID);
		final Map<OmegaPlane, Double> resultingImageMaximumSNRMap = this
				.transformPlaneDoubleValuesMap(resultingImageMaximumSNR);
		final Map<Long, Double> resultingImageAverageErrorIndexSNR = reader
				.loadSNRPlaneAverageErrorIndexSNR(analysisID);
		final Map<OmegaPlane, Double> resultingImageAverageErrorIndexSNRMap = this
				.transformPlaneDoubleValuesMap(resultingImageAverageErrorIndexSNR);
		final Map<Long, Double> resultingImageMinimumErrorIndexSNR = reader
				.loadSNRPlaneMinErrorIndexSNR(analysisID);
		final Map<OmegaPlane, Double> resultingImageMinimumErrorIndexSNRMap = this
				.transformPlaneDoubleValuesMap(resultingImageMinimumErrorIndexSNR);
		final Map<Long, Double> resultingImageMaximumErrorIndexSNR = reader
				.loadSNRPlaneMaxErrorIndexSNR(analysisID);
		final Map<OmegaPlane, Double> resultingImageMaximumErrorIndexSNRMap = this
				.transformPlaneDoubleValuesMap(resultingImageMaximumErrorIndexSNR);
		final Map<Long, Integer> resultingLocalCenterSignal = reader
				.loadSNRROICenterSignal(analysisID);
		final Map<OmegaROI, Integer> resultingLocalCenterSignalMap = this
				.transformROIIntegerValuesMap(resultingLocalCenterSignal);
		final Map<Long, Double> resultingLocalMeanSignal = reader
				.loadSNRROIMeanSignal(analysisID);
		final Map<OmegaROI, Double> resultingLocalMeanSignalMap = this
				.transformROIDoubleValuesMap(resultingLocalMeanSignal);
		final Map<Long, Integer> resultingLocalParticleArea = reader
				.loadSNRROIArea(analysisID);
		final Map<OmegaROI, Integer> resultingLocalParticleAreaMap = this
				.transformROIIntegerValuesMap(resultingLocalParticleArea);
		final Map<Long, Integer> resultingLocalPeakSignal = reader
				.loadSNRROIPeakSignal(analysisID);
		final Map<OmegaROI, Integer> resultingLocalPeakSignalMap = this
				.transformROIIntegerValuesMap(resultingLocalPeakSignal);
		final Map<Long, Double> resultingLocalBackground = reader
				.loadSNRROIBackground(analysisID);
		final Map<OmegaROI, Double> resultingLocalBackgroundMap = this
				.transformROIDoubleValuesMap(resultingLocalBackground);
		final Map<Long, Double> resultingLocalNoise = reader
				.loadSNRROINoise(analysisID);
		final Map<OmegaROI, Double> resultingLocalNoiseMap = this
				.transformROIDoubleValuesMap(resultingLocalNoise);
		final Map<Long, Double> resultingLocalSNR = reader
				.loadSNRROISNR(analysisID);
		final Map<OmegaROI, Double> resultingLocalSNRMap = this
				.transformROIDoubleValuesMap(resultingLocalSNR);
		final Map<Long, Double> resultingLocalErrorIndexSNR = reader
				.loadSNRROIErrorIndexSNR(analysisID);
		final Map<OmegaROI, Double> resultingLocalErrorIndexSNRMap = this
				.transformROIDoubleValuesMap(resultingLocalErrorIndexSNR);
		return reader.loadSNRAnalysis(analysisID, exp, algoSpec,
				resultingImageAvgCenterSignalMap,
				resultingImageAvgPeakSignalMap, resultingImageAvgMeanSignalMap,
				resultingImageBGRMap, resultingImageNoiseMap,
				resultingImageAverageSNRMap, resultingImageMinimumSNRMap,
				resultingImageMaximumSNRMap,
				resultingImageAverageErrorIndexSNRMap,
				resultingImageMinimumErrorIndexSNRMap,
				resultingImageMaximumErrorIndexSNRMap,
				resultingLocalCenterSignalMap, resultingLocalMeanSignalMap,
				resultingLocalParticleAreaMap, resultingLocalPeakSignalMap,
				resultingLocalBackgroundMap, resultingLocalNoiseMap,
				resultingLocalSNRMap, resultingLocalErrorIndexSNRMap);
	}

	private Map<OmegaROI, Integer> transformROIIntegerValuesMap(
			final Map<Long, Integer> loadedValuesMap) {
		final Map<OmegaROI, Integer> valuesMap = new LinkedHashMap<OmegaROI, Integer>();
		for (final Long roiID : loadedValuesMap.keySet()) {
			if (!this.loadedROIs.containsKey(roiID)) {
				// TODO error
				continue;
			}
			final OmegaROI roi = this.loadedROIs.get(roiID);
			final Integer value = loadedValuesMap.get(roiID);
			valuesMap.put(roi, value);
		}
		return valuesMap;
	}

	private Map<OmegaROI, Double> transformROIDoubleValuesMap(
			final Map<Long, Double> loadedValuesMap) {
		final Map<OmegaROI, Double> valuesMap = new LinkedHashMap<OmegaROI, Double>();
		for (final Long roiID : loadedValuesMap.keySet()) {
			if (!this.loadedROIs.containsKey(roiID)) {
				// TODO error
				continue;
			}
			final OmegaROI roi = this.loadedROIs.get(roiID);
			final Double value = loadedValuesMap.get(roiID);
			valuesMap.put(roi, value);
		}
		return valuesMap;
	}

	private Map<OmegaPlane, Double> transformPlaneDoubleValuesMap(
			final Map<Long, Double> loadedValuesMap) {
		final Map<OmegaPlane, Double> valuesMap = new LinkedHashMap<OmegaPlane, Double>();
		for (final Long planeID : loadedValuesMap.keySet()) {
			if (!this.loadedPlanes.containsKey(planeID)) {
				// TODO error
				continue;
			}
			final OmegaPlane plane = this.loadedPlanes.get(planeID);
			final Double value = loadedValuesMap.get(planeID);
			valuesMap.put(plane, value);
		}
		return valuesMap;
	}

	private OmegaTrackingMeasuresDiffusivityRun loadTrackingMeasuresDiffusivityRun(
			final OmegaMySqlReader reader,
			final OmegaAnalysisRunContainerInterface container, final long analysisID,
			final OmegaExperimenter exp, final OmegaRunDefinition algoSpec)
			throws SQLException, ParseException {
		final List<Long> trackingMeasuresIDs = reader
				.getTrackingMeasuresIDs(analysisID);
		if (trackingMeasuresIDs.size() > 1) {
			// TODO error
		}
		final Long trackingMeasuresID = trackingMeasuresIDs.get(0);
		final List<Long> segmentIDs = reader
				.getTrackingMeasuresSegmentIDs(trackingMeasuresID);
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = new LinkedHashMap<OmegaTrajectory, List<OmegaSegment>>();
		for (final Long segmentID : segmentIDs) {
			if (!this.loadedSegments.containsKey(segmentID)) {
				// TODO error
			}
			final List<Long> tracksIDs = reader
					.getTrajectoriesSegmentID(segmentID);
			if (tracksIDs.size() > 1) {
				// TODO error
			}
			final Long trackID = tracksIDs.get(0);
			if (!this.loadedTracks.containsKey(trackID)) {
				// TODO error
			}
			final OmegaTrajectory track = this.loadedTracks.get(trackID);
			final OmegaSegment segment = this.loadedSegments.get(segmentID);
			List<OmegaSegment> segments;
			if (segmentsMap.containsKey(track)) {
				segments = segmentsMap.get(track);
			} else {
				segments = new ArrayList<OmegaSegment>();
			}
			segments.add(segment);
			segmentsMap.put(track, segments);
		}
		// TODO controllare come vengono salvati eventuale snr e diffusivity
		OmegaTrackingMeasuresDiffusivityRun diffusivityRun = null;
		final List<Long> parentDiffusivityIDs = reader
				.getTrackingMeasuresDiffusivityParentIDs(trackingMeasuresID);
		if (parentDiffusivityIDs.size() > 1) {
			// TODO error
		}
		if (!parentDiffusivityIDs.isEmpty()) {
			final Long parentDiffusivityID = parentDiffusivityIDs.get(0);
			if (this.loadedAnalysisRun.containsKey(parentDiffusivityID)) {
				diffusivityRun = (OmegaTrackingMeasuresDiffusivityRun) this.loadedAnalysisRun
						.get(parentDiffusivityID);
			}
		}
		OmegaSNRRun snrRun = null;
		final List<Long> snrIDs = reader
				.getTrackingMeasuresSNRIDs(trackingMeasuresID);
		if (snrIDs.size() > 1) {
			// TODO error
		}
		if (!snrIDs.isEmpty()) {
			final Long snrID = snrIDs.get(0);
			if (this.loadedAnalysisRun.containsKey(snrID)) {
				snrRun = (OmegaSNRRun) this.loadedAnalysisRun.get(snrID);
			}
		}
		final Map<Long, Map<Integer, Double>> nyMap = reader
				.loadDiffusivityNyMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Map<Integer, Double>>> muMap = reader
				.loadDiffusivityMuMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Map<Integer, Double>>> logMuMap = reader
				.loadDiffusivityLogMuMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Map<Integer, Double>>> deltaTMap = reader
				.loadDiffusivityDeltaTMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Map<Integer, Double>>> logDeltaTMap = reader
				.loadDiffusivityLogDeltaTMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Map<Integer, Double>>> gammaDMap = reader
				.loadDiffusivityGammaDMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Map<Integer, Double>>> logGammaDMap = reader
				.loadDiffusivityLogGammaDMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Double>> gammaMap = reader
				.loadDiffusivityGammaLogMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Double>> smssLogMap = reader
				.loadDiffusivitySmssLogMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Double>> errorLogMap = reader
				.loadDiffusivityErrorLogMap(trackingMeasuresID);
		final Map<OmegaSegment, Double[]> nySegmentMap = this
				.transformIndexedValuesMapToSegmentValuesArrayMap(segmentsMap,
						nyMap);
		final Map<OmegaSegment, Double[][]> muSegmentMap = this
				.transformDoubleIndexedValuesMapToSegmentValuesArrayMap(
						segmentsMap, muMap);
		final Map<OmegaSegment, Double[][]> logMuSegmentMap = this
				.transformDoubleIndexedValuesMapToSegmentValuesArrayMap(
						segmentsMap, logMuMap);
		final Map<OmegaSegment, Double[][]> deltaTSegmentMap = this
				.transformDoubleIndexedValuesMapToSegmentValuesArrayMap(
						segmentsMap, deltaTMap);
		final Map<OmegaSegment, Double[][]> logDeltaTSegmentMap = this
				.transformDoubleIndexedValuesMapToSegmentValuesArrayMap(
						segmentsMap, logDeltaTMap);
		final Map<OmegaSegment, Double[][]> gammaDSegmentMap = this
				.transformDoubleIndexedValuesMapToSegmentValuesArrayMap(
						segmentsMap, gammaDMap);
		final Map<OmegaSegment, Double[][]> logGammaDSegmentMap = this
				.transformDoubleIndexedValuesMapToSegmentValuesArrayMap(
						segmentsMap, logGammaDMap);
		final Map<OmegaSegment, Double[]> gammaSegmentMap = this
				.transformIndexedValuesMapToSegmentValuesArrayMap(segmentsMap,
						gammaMap);
		final Map<OmegaSegment, Double[]> smssLogSegmentMap = this
				.transformIndexedValuesMapToSegmentValuesArrayMap(segmentsMap,
						smssLogMap);
		Map<OmegaSegment, Double[]> errorLogSegmentMap = null;
		if (errorLogMap != null) {
			errorLogSegmentMap = this
					.transformIndexedValuesMapToSegmentValuesArrayMap(
							segmentsMap, errorLogMap);
		} else {
			errorLogSegmentMap = new LinkedHashMap<OmegaSegment, Double[]>();
		}
		final Double minDetectableODC = reader
				.loadDiffusivityMinimumDetectableODC(trackingMeasuresID);
		return reader.loadDiffusivityMeasuresAnalysis(analysisID, exp,
				algoSpec, segmentsMap, nySegmentMap, muSegmentMap,
				logMuSegmentMap, deltaTSegmentMap, logDeltaTSegmentMap,
				gammaDSegmentMap, logGammaDSegmentMap, gammaSegmentMap,
				smssLogSegmentMap, errorLogSegmentMap, minDetectableODC,
				snrRun, diffusivityRun);
	}

	private OmegaTrackingMeasuresMobilityRun loadTrackingMeasuresMobilityRun(
			final OmegaMySqlReader reader,
			final OmegaAnalysisRunContainerInterface container, final long analysisID,
			final OmegaExperimenter exp, final OmegaRunDefinition algoSpec)
			throws SQLException, ParseException {
		final List<Long> trackingMeasuresIDs = reader
				.getTrackingMeasuresIDs(analysisID);
		if (trackingMeasuresIDs.size() > 1) {
			// TODO error
		}
		final Long trackingMeasuresID = trackingMeasuresIDs.get(0);
		final List<Long> segmentIDs = reader
				.getTrackingMeasuresSegmentIDs(trackingMeasuresID);
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = new LinkedHashMap<OmegaTrajectory, List<OmegaSegment>>();
		for (final Long segmentID : segmentIDs) {
			if (!this.loadedSegments.containsKey(segmentID)) {
				// TODO error
			}
			final List<Long> tracksIDs = reader
					.getTrajectoriesSegmentID(segmentID);
			if (tracksIDs.size() > 1) {
				// TODO error
			}
			final Long trackID = tracksIDs.get(0);
			if (!this.loadedTracks.containsKey(trackID)) {
				// TODO error
			}
			final OmegaTrajectory track = this.loadedTracks.get(trackID);
			final OmegaSegment segment = this.loadedSegments.get(segmentID);
			List<OmegaSegment> segments;
			if (segmentsMap.containsKey(track)) {
				segments = segmentsMap.get(track);
			} else {
				segments = new ArrayList<OmegaSegment>();
			}
			segments.add(segment);
			segmentsMap.put(track, segments);
		}
		final Map<Long, Map<Integer, Double>> distancesMap = reader
				.loadMobilityDistancesMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Double>> distancesFromOriginMap = reader
				.loadMobilityDistancesFromOriginMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Double>> displacementsFromOriginMap = reader
				.loadMobilityDisplacementsFromOriginMap(trackingMeasuresID);
		final Map<Long, Double> maxDisplacementsFromOriginMap = reader
				.loadMobilityMaxDisplacementsFromOriginMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Double>> timeTraveledMap = reader
				.loadMobilityTimeTraveledMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Double>> confinementRatioMap = reader
				.loadMobilityConfinementRatioMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Double[]>> anglesAndDirectionalChangesMap = reader
				.loadMobilityAnglesAndDirectionChangesMap(trackingMeasuresID);
		final Map<OmegaSegment, List<Double>> distancesSegmentMap = this
				.transformIndexedDoubleValuesMapToSegmentValuesListMap(
						segmentsMap, distancesMap);
		final Map<OmegaSegment, List<Double>> distancesFromOriginSegmentMap = this
				.transformIndexedDoubleValuesMapToSegmentValuesListMap(
						segmentsMap, distancesFromOriginMap);
		final Map<OmegaSegment, List<Double>> displacementsFromOriginSegmentMap = this
				.transformIndexedDoubleValuesMapToSegmentValuesListMap(
						segmentsMap, displacementsFromOriginMap);
		final Map<OmegaSegment, Double> maxDisplacementFromOriginSegmentMap = this
				.transformSingleDoubleValuesMapToSegmentValuesMap(segmentsMap,
						maxDisplacementsFromOriginMap);
		final Map<OmegaSegment, List<Double>> timeTraveledSegmentMap = this
				.transformIndexedDoubleValuesMapToSegmentValuesListMap(
						segmentsMap, timeTraveledMap);
		// final Map<OmegaSegment, Integer> totalTimeTraveledSegmentMap = this
		// .transformSingleIntegerValuesMapToSegmentValuesMap(segmentsMap,
		// totalTimeTraveledMap);
		final Map<OmegaSegment, List<Double>> confinementRatioSegmentMap = this
				.transformIndexedDoubleValuesMapToSegmentValuesListMap(
						segmentsMap, confinementRatioMap);
		final Map<OmegaSegment, List<Double[]>> anglesAndDirectionalChangesSegmentMap = this
				.transformIndexedDoubleValuesArrayMapToSegmentValuesListMap(
						segmentsMap, anglesAndDirectionalChangesMap);
		return reader.loadMobilityMeasuresAnalysis(analysisID, exp, algoSpec,
				segmentsMap, distancesSegmentMap,
				distancesFromOriginSegmentMap,
				displacementsFromOriginSegmentMap,
				maxDisplacementFromOriginSegmentMap, timeTraveledSegmentMap,
				confinementRatioSegmentMap,
				anglesAndDirectionalChangesSegmentMap);
	}

	private OmegaTrackingMeasuresVelocityRun loadTrackingMeasuresVelocityRun(
			final OmegaMySqlReader reader,
			final OmegaAnalysisRunContainerInterface container, final long analysisID,
			final OmegaExperimenter exp, final OmegaRunDefinition algoSpec)
			throws SQLException, ParseException {
		final List<Long> trackingMeasuresIDs = reader
				.getTrackingMeasuresIDs(analysisID);
		if (trackingMeasuresIDs.size() > 1) {
			// TODO error
		}
		final Long trackingMeasuresID = trackingMeasuresIDs.get(0);
		final List<Long> segmentIDs = reader
				.getTrackingMeasuresSegmentIDs(trackingMeasuresID);
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = new LinkedHashMap<OmegaTrajectory, List<OmegaSegment>>();
		for (final Long segmentID : segmentIDs) {
			if (!this.loadedSegments.containsKey(segmentID)) {
				// TODO error
			}
			final List<Long> tracksIDs = reader
					.getTrajectoriesSegmentID(segmentID);
			if (tracksIDs.size() > 1) {
				// TODO error
			}
			final Long trackID = tracksIDs.get(0);
			if (!this.loadedTracks.containsKey(trackID)) {
				// TODO error
			}
			final OmegaTrajectory track = this.loadedTracks.get(trackID);
			final OmegaSegment segment = this.loadedSegments.get(segmentID);
			List<OmegaSegment> segments;
			if (segmentsMap.containsKey(track)) {
				segments = segmentsMap.get(track);
			} else {
				segments = new ArrayList<OmegaSegment>();
			}
			segments.add(segment);
			segmentsMap.put(track, segments);
		}
		final Map<Long, Map<Integer, Double>> localSpeedMap = reader
				.loadVelocityLocalSpeedFromOriginMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Double>> localSpeedFromOriginMap = reader
				.loadVelocityLocalSpeedFromOriginMap(trackingMeasuresID);
		final Map<Long, Map<Integer, Double>> localVelocityFromOriginMap = reader
				.loadVelocityLocalVelocityFromOriginMap(trackingMeasuresID);
		final Map<Long, Double> averageCurvilinearSpeedMap = reader
				.loadVelocityAverageSpeedMap(trackingMeasuresID);
		final Map<Long, Double> averageStraightLineVelocityMap = reader
				.loadVelocityAverageVelocityMap(trackingMeasuresID);
		final Map<Long, Double> forwardProgressionLinearityMap = reader
				.loadVelocityForwardProgressionMap(trackingMeasuresID);
		final Map<OmegaSegment, List<Double>> localSpeedSegmentMap = this
				.transformIndexedDoubleValuesMapToSegmentValuesListMap(
						segmentsMap, localSpeedMap);
		final Map<OmegaSegment, List<Double>> localSpeedFromOriginSegmentMap = this
				.transformIndexedDoubleValuesMapToSegmentValuesListMap(
						segmentsMap, localSpeedFromOriginMap);
		final Map<OmegaSegment, List<Double>> localVelocityFromOriginSegmentMap = this
				.transformIndexedDoubleValuesMapToSegmentValuesListMap(
						segmentsMap, localVelocityFromOriginMap);
		final Map<OmegaSegment, Double> averageCurvilinearSpeedSegmentMap = this
				.transformSingleDoubleValuesMapToSegmentValuesMap(segmentsMap,
						averageCurvilinearSpeedMap);
		final Map<OmegaSegment, Double> averageStraightLineVelocitySegmentMap = this
				.transformSingleDoubleValuesMapToSegmentValuesMap(segmentsMap,
						averageStraightLineVelocityMap);
		final Map<OmegaSegment, Double> forwardProgressionLinearitySegmentMap = this
				.transformSingleDoubleValuesMapToSegmentValuesMap(segmentsMap,
						forwardProgressionLinearityMap);
		return reader.loadVelocityMeasuresAnalysis(analysisID, exp, algoSpec,
				segmentsMap, localSpeedSegmentMap,
				localSpeedFromOriginSegmentMap,
				localVelocityFromOriginSegmentMap,
				averageCurvilinearSpeedSegmentMap,
				averageStraightLineVelocitySegmentMap,
				forwardProgressionLinearitySegmentMap);
	}

	private OmegaTrackingMeasuresIntensityRun loadTrackingMeasuresIntensityRun(
			final OmegaMySqlReader reader,
			final OmegaAnalysisRunContainerInterface container, final long analysisID,
			final OmegaExperimenter exp, final OmegaRunDefinition algoSpec)
			throws SQLException, ParseException {
		final List<Long> trackingMeasuresIDs = reader
				.getTrackingMeasuresIDs(analysisID);
		if (trackingMeasuresIDs.size() > 1) {
			// TODO error
		}
		final Long trackingMeasuresID = trackingMeasuresIDs.get(0);
		final List<Long> segmentIDs = reader
				.getTrackingMeasuresSegmentIDs(trackingMeasuresID);
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = new LinkedHashMap<OmegaTrajectory, List<OmegaSegment>>();
		for (final Long segmentID : segmentIDs) {
			if (!this.loadedSegments.containsKey(segmentID)) {
				// TODO error
			}
			final List<Long> tracksIDs = reader
					.getTrajectoriesSegmentID(segmentID);
			if (tracksIDs.size() > 1) {
				// TODO error
			}
			final Long trackID = tracksIDs.get(0);
			if (!this.loadedTracks.containsKey(trackID)) {
				// TODO error
			}
			final OmegaTrajectory track = this.loadedTracks.get(trackID);
			final OmegaSegment segment = this.loadedSegments.get(segmentID);
			List<OmegaSegment> segments;
			if (segmentsMap.containsKey(track)) {
				segments = segmentsMap.get(track);
			} else {
				segments = new ArrayList<OmegaSegment>();
			}
			segments.add(segment);
			segmentsMap.put(track, segments);
		}
		OmegaSNRRun snrRun = null;
		final List<Long> snrIDs = reader
				.getTrackingMeasuresSNRIDs(trackingMeasuresID);
		if (snrIDs.size() > 1) {
			// TODO error
		}
		if (!snrIDs.isEmpty()) {
			final Long snrID = snrIDs.get(0);
			if (this.loadedAnalysisRun.containsKey(snrID)) {
				snrRun = (OmegaSNRRun) this.loadedAnalysisRun.get(snrID);
			}
		}
		final Map<OmegaROI, Double> peakSignalsLocalMap = new LinkedHashMap<OmegaROI, Double>();
		final Map<OmegaROI, Double> centroidSignalsLocalMap = new LinkedHashMap<OmegaROI, Double>();
		final Map<OmegaROI, Double> meanSignalsLocalMap = new LinkedHashMap<OmegaROI, Double>();
		final Map<OmegaROI, Double> areasLocalMap = new LinkedHashMap<OmegaROI, Double>();
		final Map<OmegaROI, Double> backgroundsLocalMap = new LinkedHashMap<OmegaROI, Double>();
		final Map<OmegaROI, Double> noisesLocalMap = new LinkedHashMap<OmegaROI, Double>();
		final Map<OmegaROI, Double> snrsLocalMap = new LinkedHashMap<OmegaROI, Double>();
		if (snrRun != null) {
			final Map<OmegaROI, Integer> peakSignalsMap = snrRun
					.getResultingLocalPeakSignals();
			final Map<OmegaROI, Integer> centroidSignalsMap = snrRun
					.getResultingLocalCenterSignals();
			final Map<OmegaROI, Double> meanSignalsMap = snrRun
					.getResultingLocalMeanSignals();
			final Map<OmegaROI, Integer> areasMap = snrRun
					.getResultingLocalParticleArea();
			final Map<OmegaROI, Double> backgroundMap = snrRun
					.getResultingLocalBackgrounds();
			final Map<OmegaROI, Double> noisesMap = snrRun
					.getResultingLocalNoises();
			final Map<OmegaROI, Double> snrsMap = snrRun
					.getResultingLocalSNRs();
			
			for (final OmegaTrajectory track : segmentsMap.keySet()) {
				for (final OmegaROI roi : track.getROIs()) {
					peakSignalsLocalMap.put(roi,
							(double) peakSignalsMap.get(roi));
					centroidSignalsLocalMap.put(roi,
							(double) centroidSignalsMap.get(roi));
					meanSignalsLocalMap.put(roi, meanSignalsMap.get(roi));
					areasLocalMap.put(roi, (double) areasMap.get(roi));
					backgroundsLocalMap.put(roi, backgroundMap.get(roi));
					noisesLocalMap.put(roi, noisesMap.get(roi));
					snrsLocalMap.put(roi, snrsMap.get(roi));
				}
			}
		} else {
			for (final OmegaTrajectory track : segmentsMap.keySet()) {
				for (final OmegaROI roi : track.getROIs()) {
					peakSignalsLocalMap.put(roi, null);
					centroidSignalsLocalMap.put(roi, null);
					meanSignalsLocalMap.put(roi, null);
					areasLocalMap.put(roi, null);
					backgroundsLocalMap.put(roi, null);
					noisesLocalMap.put(roi, null);
					snrsLocalMap.put(roi, null);
				}
			}
		}
		final Map<Long, Double[]> peakSignalsMap = reader
				.loadIntensityPeakSignalsMap(trackingMeasuresID);
		final Map<Long, Double[]> centroidSignalsMap = reader
				.loadIntensityCentroidSignalsMap(trackingMeasuresID);
		final Map<Long, Double[]> meanSignalsMap = reader
				.loadIntensityMeanSignalsMap(trackingMeasuresID);
		final Map<Long, Double[]> backgroundsMap = reader
				.loadIntensityBackgroundMap(trackingMeasuresID);
		final Map<Long, Double[]> noisesMap = reader
				.loadIntensityNoisesMap(trackingMeasuresID);
		final Map<Long, Double[]> areasMap = reader
				.loadIntensityAreasMap(trackingMeasuresID);
		final Map<Long, Double[]> snrsMap = reader
				.loadIntensitySNRsMap(trackingMeasuresID);
		final Map<OmegaSegment, Double[]> peakSignalsSegmentMap = this
				.transformValuesMapToSegmentValuesArrayMap(segmentsMap,
						peakSignalsMap);
		final Map<OmegaSegment, Double[]> centroidSignalsSegmentMap = this
				.transformValuesMapToSegmentValuesArrayMap(segmentsMap,
						centroidSignalsMap);
		final Map<OmegaSegment, Double[]> meanSignalsSegmentMap = this
				.transformValuesMapToSegmentValuesArrayMap(segmentsMap,
						meanSignalsMap);
		final Map<OmegaSegment, Double[]> backgroundsSegmentMap = this
				.transformValuesMapToSegmentValuesArrayMap(segmentsMap,
						backgroundsMap);
		final Map<OmegaSegment, Double[]> noisesSegmentMap = this
				.transformValuesMapToSegmentValuesArrayMap(segmentsMap,
						noisesMap);
		final Map<OmegaSegment, Double[]> areasSegmentMap = this
				.transformValuesMapToSegmentValuesArrayMap(segmentsMap,
						areasMap);
		final Map<OmegaSegment, Double[]> snrsSegmentMap = this
				.transformValuesMapToSegmentValuesArrayMap(segmentsMap, snrsMap);
		return reader.loadIntensityMeasuresAnalysis(analysisID, exp, algoSpec,
				segmentsMap, peakSignalsSegmentMap, centroidSignalsSegmentMap,
				peakSignalsLocalMap, centroidSignalsLocalMap,
				meanSignalsSegmentMap, backgroundsSegmentMap, noisesSegmentMap,
				areasSegmentMap, snrsSegmentMap, meanSignalsLocalMap,
				backgroundsLocalMap, noisesLocalMap, areasLocalMap,
				snrsLocalMap, snrRun);
	}

	private Map<OmegaSegment, Integer> transformSingleIntegerValuesMapToSegmentValuesMap(
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final Map<Long, Integer> loadedValuesMap) {
		final Map<OmegaSegment, Integer> segmentValuesMap = new LinkedHashMap<OmegaSegment, Integer>();
		for (final OmegaTrajectory track : segmentsMap.keySet()) {
			final List<OmegaSegment> segments = segmentsMap.get(track);
			for (final OmegaSegment segment : segments) {
				final long id = segment.getElementID();
				if (!loadedValuesMap.containsKey(id)) {
					continue;
				}
				segmentValuesMap.put(segment, loadedValuesMap.get(id));
			}
		}
		return segmentValuesMap;
	}

	private Map<OmegaSegment, Double> transformSingleDoubleValuesMapToSegmentValuesMap(
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final Map<Long, Double> loadedValuesMap) {
		final Map<OmegaSegment, Double> segmentValuesMap = new LinkedHashMap<OmegaSegment, Double>();
		for (final OmegaTrajectory track : segmentsMap.keySet()) {
			final List<OmegaSegment> segments = segmentsMap.get(track);
			for (final OmegaSegment segment : segments) {
				final long id = segment.getElementID();
				if (!loadedValuesMap.containsKey(id)) {
					continue;
				}
				segmentValuesMap.put(segment, loadedValuesMap.get(id));
			}
		}
		return segmentValuesMap;
	}

	private Map<OmegaSegment, List<Double[]>> transformIndexedDoubleValuesArrayMapToSegmentValuesListMap(
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final Map<Long, Map<Integer, Double[]>> loadedValuesMap) {
		final Map<OmegaSegment, List<Double[]>> segmentValuesMap = new LinkedHashMap<OmegaSegment, List<Double[]>>();
		for (final OmegaTrajectory track : segmentsMap.keySet()) {
			final List<OmegaSegment> segments = segmentsMap.get(track);
			for (final OmegaSegment segment : segments) {
				final long id = segment.getElementID();
				if (!loadedValuesMap.containsKey(id)) {
					continue;
				}
				final Map<Integer, Double[]> values = loadedValuesMap.get(id);
				final List<Double[]> valuesList = new ArrayList<Double[]>(
						values.size());
				for (final int index : values.keySet()) {
					valuesList.add(index, values.get(index));
				}
				segmentValuesMap.put(segment, valuesList);
			}
		}
		return segmentValuesMap;
	}

	private Map<OmegaSegment, List<Double>> transformIndexedDoubleValuesMapToSegmentValuesListMap(
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final Map<Long, Map<Integer, Double>> loadedValuesMap) {
		final Map<OmegaSegment, List<Double>> segmentValuesMap = new LinkedHashMap<OmegaSegment, List<Double>>();
		for (final OmegaTrajectory track : segmentsMap.keySet()) {
			final List<OmegaSegment> segments = segmentsMap.get(track);
			for (final OmegaSegment segment : segments) {
				final long id = segment.getElementID();
				if (!loadedValuesMap.containsKey(id)) {
					continue;
				}
				final Map<Integer, Double> values = loadedValuesMap.get(id);
				final List<Double> valuesList = new ArrayList<Double>(
						values.size());
				for (final int index : values.keySet()) {
					valuesList.add(index, values.get(index));
				}
				segmentValuesMap.put(segment, valuesList);
			}
		}
		return segmentValuesMap;
	}

	private Map<OmegaSegment, Double[][]> transformDoubleIndexedValuesMapToSegmentValuesArrayMap(
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final Map<Long, Map<Integer, Map<Integer, Double>>> loadedValuesMap) {
		final Map<OmegaSegment, Double[][]> segmentValuesMap = new LinkedHashMap<OmegaSegment, Double[][]>();
		for (final OmegaTrajectory track : segmentsMap.keySet()) {
			final List<OmegaSegment> segments = segmentsMap.get(track);
			for (final OmegaSegment segment : segments) {
				final long id = segment.getElementID();
				if (!loadedValuesMap.containsKey(id)) {
					continue;
				}
				final Map<Integer, Map<Integer, Double>> valuesMap = loadedValuesMap
						.get(id);
				final Double[][] valuesArray = new Double[valuesMap.size()][];
				for (final int nyIndex : valuesMap.keySet()) {
					final Map<Integer, Double> values = valuesMap.get(nyIndex);
					final Double[] valuesArray2 = new Double[values.size()];
					for (final int valIndex : values.keySet()) {
						valuesArray2[valIndex] = values.get(valIndex);
					}
					valuesArray[nyIndex] = valuesArray2;
				}
				segmentValuesMap.put(segment, valuesArray);
			}
		}
		return segmentValuesMap;
	}
	
	private Map<OmegaSegment, Double[]> transformValuesMapToSegmentValuesArrayMap(
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final Map<Long, Double[]> loadedValuesMap) {
		final Map<OmegaSegment, Double[]> segmentValuesMap = new LinkedHashMap<OmegaSegment, Double[]>();
		for (final OmegaTrajectory track : segmentsMap.keySet()) {
			final List<OmegaSegment> segments = segmentsMap.get(track);
			for (final OmegaSegment segment : segments) {
				final long id = segment.getElementID();
				if (!loadedValuesMap.containsKey(id)) {
					continue;
				}
				final Double[] values = loadedValuesMap.get(id);
				segmentValuesMap.put(segment, values);
			}
		}
		return segmentValuesMap;
	}

	private Map<OmegaSegment, Double[]> transformIndexedValuesMapToSegmentValuesArrayMap(
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final Map<Long, Map<Integer, Double>> loadedValuesMap) {
		final Map<OmegaSegment, Double[]> segmentValuesMap = new LinkedHashMap<OmegaSegment, Double[]>();
		for (final OmegaTrajectory track : segmentsMap.keySet()) {
			final List<OmegaSegment> segments = segmentsMap.get(track);
			for (final OmegaSegment segment : segments) {
				final long id = segment.getElementID();
				if (!loadedValuesMap.containsKey(id)) {
					continue;
				}
				final Map<Integer, Double> values = loadedValuesMap.get(id);
				final Double[] valuesArray = new Double[values.size()];
				for (final int index : values.keySet()) {
					valuesArray[index] = values.get(index);
				}
				segmentValuesMap.put(segment, valuesArray);
			}
		}
		return segmentValuesMap;
	}

	private OmegaTrajectoriesSegmentationRun loadTrajectoriesSegmentationRun(
			final OmegaMySqlReader reader,
			final OmegaAnalysisRunContainerInterface container, final long analysisID,
			final OmegaExperimenter exp, final OmegaRunDefinition algoSpec)
			throws SQLException, ParseException {
		final List<Long> segmentsID = reader.getSegmentIDs(analysisID);
		final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments = new LinkedHashMap<OmegaTrajectory, List<OmegaSegment>>();
		// for (final Long key : this.loadedROIs.keySet()) {
		// System.out.println(key);
		// }
		for (final Long id : segmentsID) {
			OmegaSegment segment;
			OmegaTrajectory track;
			final List<Long> tracksID = reader.getTrajectoriesSegmentID(id);
			if (tracksID.size() > 1) {
				// TODO ERROR
			}
			if (!this.loadedTracks.containsKey(tracksID.get(0))) {
				// TODO ERROR
			}
			track = this.loadedTracks.get(tracksID.get(0));
			if (this.loadedSegments.containsKey(id)) {
				segment = this.loadedSegments.get(id);
			} else {
				// TODO creare metodo in reader per prendere gli il track ID da
				// un segment dopodiche prendere le particle della track e
				// prendere le particle dalle loaded con gli id
				segment = reader.loadSegment(id, this.loadedROIs);
				this.loadedSegments.put(id, segment);
			}
			List<OmegaSegment> segments;
			if (resultingSegments.containsKey(track)) {
				segments = resultingSegments.get(track);
			} else {
				segments = new ArrayList<OmegaSegment>();
			}
			if (!segments.contains(segment)) {
				segments.add(segment);
			}
			resultingSegments.put(track, segments);
		}
		final List<Long> segmentationTypesIDs = reader
				.getSegmentationTypesID(analysisID);
		if (segmentationTypesIDs.size() > 1) {
			// TODO ERROR
		}
		final Long segmTypesID = segmentationTypesIDs.get(0);
		OmegaSegmentationTypes segmentationTypes;
		if (this.loadedSegmentationTypes.containsKey(segmTypesID)) {
			segmentationTypes = this.loadedSegmentationTypes.get(segmTypesID);
		} else {
			final List<Long> segmTypeIDs = reader
					.getSegmentationTypeIDs(segmTypesID);
			final List<OmegaSegmentationType> segmTypes = new ArrayList<OmegaSegmentationType>();
			for (final Long segmTypeID : segmTypeIDs) {
				OmegaSegmentationType segmType;
				if (!this.loadedSegmentationType.containsKey(segmTypeID)) {
					segmType = reader.loadSegmentationType(segmTypeID);
					this.loadedSegmentationType.put(segmTypeID, segmType);
				} else {
					segmType = this.loadedSegmentationType.get(segmTypeID);
				}
				segmTypes.add(segmType);
			}
			segmentationTypes = reader.loadSegmentationTypes(
					segmentationTypesIDs.get(0), segmTypes);
			this.loadedSegmentationTypes.put(segmTypesID, segmentationTypes);
		}
		return reader.loadSegmentationAnalysis(analysisID, exp, algoSpec,
				resultingSegments, segmentationTypes);
	}

	private OmegaTrajectoriesRelinkingRun loadTrajectoriesRelinkingRun(
			final OmegaMySqlReader reader,
			final OmegaAnalysisRunContainerInterface container, final long analysisID,
			final OmegaExperimenter exp, final OmegaRunDefinition algoSpec)
			throws SQLException, ParseException {
		final List<Long> trackIDs = reader.getTrajectoriesIDs(analysisID);
		final List<OmegaTrajectory> resultingTrajectories = new ArrayList<OmegaTrajectory>();
		for (final Long id : trackIDs) {
			final OmegaTrajectory track;
			if (this.loadedTracks.containsKey(id)) {
				track = this.loadedTracks.get(id);
			} else {
				track = reader.loadTrajectory(id);
				final List<Long> roiIDs = reader.getROIIDs(id);
				for (final Long roiID : roiIDs) {
					final OmegaROI roi = this.loadedROIs.get(roiID);
					if (roi != null) {
						track.addROI(roi);
					}
				}
				this.loadedTracks.put(id, track);
			}
			resultingTrajectories.add(track);
		}
		return reader.loadRelinkingAnalysis(analysisID, exp, algoSpec,
				resultingTrajectories);
	}

	private OmegaParticleLinkingRun loadParticleLinkingRun(
			final OmegaMySqlReader reader,
			final OmegaAnalysisRunContainerInterface container, final long analysisID,
			final OmegaExperimenter exp, final OmegaRunDefinition algoSpec)
			throws SQLException, ParseException {
		final List<Long> trackIDs = reader.getTrajectoriesIDs(analysisID);
		final List<OmegaTrajectory> resultingTrajectories = new ArrayList<OmegaTrajectory>();
		for (final Long id : trackIDs) {
			final OmegaTrajectory track;
			if (this.loadedTracks.containsKey(id)) {
				track = this.loadedTracks.get(id);
			} else {
				track = reader.loadTrajectory(id);
				final List<Long> roiIDs = reader.getROIIDs(id);
				for (final Long roiID : roiIDs) {
					final OmegaROI roi = this.loadedROIs.get(roiID);
					if (roi != null) {
						track.addROI(roi);
					}
				}
				this.loadedTracks.put(id, track);
			}
			resultingTrajectories.add(track);
		}
		return reader.loadLinkingAnalysis(analysisID, exp, algoSpec,
				resultingTrajectories);
	}

	private OmegaParticleDetectionRun loadParticleDetectionRun(
			final OmegaMySqlReader reader,
			final OmegaAnalysisRunContainerInterface container, final long analysisID,
			final OmegaExperimenter exp, final OmegaRunDefinition algoSpec)
			throws SQLException, ParseException {
		// TODO NEED TO LOAD FRAMES HERE
		final List<Long> particleIDs = reader.getParticleIDs(analysisID);
		for (final Long particleID : particleIDs) {
			final List<Long> roiIDs = reader.getParticleROIID(particleID);
			if (roiIDs.size() > 1) {
				// TODO error
			}
			final Long roiID = roiIDs.get(0);
			final List<Long> frameIDs = reader.getROIFrameID(roiID);
			if (frameIDs.size() > 1) {
				// TODO error
			}
			final Long frameID = frameIDs.get(0);
			if (!this.loadedPlanes.containsKey(frameID)) {
				final OmegaPlane plane = reader.loadFrame(frameID);
				this.loadedPlanes.put(frameID, plane);
				if (container instanceof OmegaImage) {
					((OmegaImage) container).getDefaultPixels().addFrame(
							plane.getChannel(), plane.getZPlane(), plane);
				} else if (container instanceof OmegaImagePixels) {
					((OmegaImagePixels) container).addFrame(plane.getChannel(),
							plane.getZPlane(), plane);
				} else {
					// TODO error
				}
			}
		}
		final Map<OmegaPlane, List<OmegaROI>> resultingParticles = this
				.loadParticles(reader, analysisID, this.loadedPlanes);
		final Map<OmegaROI, Map<String, Object>> resultingParticlesValues = this
				.loadParticleValues(reader, analysisID, resultingParticles);
		return reader.loadDetectionAnalysis(analysisID, exp, algoSpec,
				resultingParticles, resultingParticlesValues);
	}

	private Map<OmegaPlane, List<OmegaROI>> loadParticles(
			final OmegaMySqlReader reader, final long analysisRunID,
			final Map<Long, OmegaPlane> frames) throws SQLException {
		final Map<OmegaPlane, List<OmegaROI>> particleMap = new LinkedHashMap<OmegaPlane, List<OmegaROI>>();
		final List<Long> particleIDs = reader.getParticleIDs(analysisRunID);
		for (final Long id : particleIDs) {
			final List<Long> roiIDs = reader.getParticleROIID(id);
			if (roiIDs.size() > 1) {
				// TODO error
			}
			final Long roiID = roiIDs.get(0);
			final List<Long> frameIDs = reader.getROIFrameID(roiID);
			if (frameIDs.size() > 1) {
				// TODO error
			}
			final Long frameID = frameIDs.get(0);
			OmegaPlane frame = null;
			if (frames.containsKey(frameID)) {
				frame = frames.get(frameID);
			}
			if (frame == null)
				// TODO ERROR
				return null;
			OmegaROI roi = null;
			if (this.loadedROIs.containsKey(roiID)) {
				roi = this.loadedROIs.get(roiID);
			} else {
				final OmegaImagePixels pixels = frame.getParentPixels();
				final Double physicalX = pixels.getPhysicalSizeX();
				final Double physicalY = pixels.getPhysicalSizeY();
				roi = reader.loadParticle(id, frame.getIndex(), physicalX,
						physicalY);
				this.loadedROIs.put(roiID, roi);
			}
			List<OmegaROI> particles;
			if (particleMap.containsKey(frame)) {
				particles = particleMap.get(frame);
			} else {
				particles = new ArrayList<OmegaROI>();
			}
			particles.add(roi);
			particleMap.put(frame, particles);
		}
		return particleMap;
	}

	private Map<OmegaROI, Map<String, Object>> loadParticleValues(
			final OmegaMySqlReader reader, final Long analysisID,
			final Map<OmegaPlane, List<OmegaROI>> particles)
			throws SQLException {
		final Map<OmegaROI, Map<String, Object>> particleValues = new LinkedHashMap<OmegaROI, Map<String, Object>>();
		for (final OmegaPlane plane : particles.keySet()) {
			for (final OmegaROI roi : particles.get(plane)) {
				Map<String, Object> values;
				// if(particleValues.containsKey(roi)) {
				// values = particleValues.get(roi);
				// } else {
				// values = new LinkedHashMap<String, Object>();
				// }
				Long id = roi.getElementID();
				if (roi instanceof OmegaParticle) {
					final List<Long> roiIDs = reader.getParticleROIID(roi
							.getElementID());
					if (roiIDs.size() > 1) {
						// TODO error
					}
					id = roiIDs.get(0);
				}
				final List<Long> valueIDs = reader.getROIValuesIDs(id,
						analysisID);
				values = reader.loadROIValues(valueIDs);
				particleValues.put(roi, values);
			}
		}
		return particleValues;
	}

	private void prepareElementsToLoad() throws SQLException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		for (final OmegaProject project : this.projectsToLoad) {
			this.updateMessage(project,
					"Preparing project " + project.getName() + "...");
			int projectElements = 0;
			long projectID = project.getElementID();
			final long projectOMEID = project.getOmeroId();
			if ((projectID == -1) && (projectOMEID != -1)) {
				projectID = reader.getProjectID(projectOMEID);
				if ((projectID != -1)
						&& !this.projectsToBeLoaded.contains(projectID)) {
					this.projectsToBeLoaded.add(projectID);
					projectElements++;
				}
			}
			for (final OmegaDataset dataset : project.getDatasets()) {
				this.updateMessage(dataset,
						"Preparing dataset " + dataset.getName() + "...");
				int datasetElements = 0;
				long datasetID = dataset.getElementID();
				final long datasetOMEID = dataset.getOmeroId();
				if ((datasetID == -1) && (datasetOMEID != -1)) {
					datasetID = reader.getDatasetID(datasetOMEID);
					if ((datasetID != -1)
							&& !this.datasetsToBeLoaded.contains(datasetID)) {
						this.datasetsToBeLoaded.add(datasetID);
						datasetElements++;
					}
				}
				for (final OmegaImage image : dataset.getImages()) {
					this.updateMessage(image,
							"Preparing image " + image.getName() + "...");
					int imageElements = 0;
					long experimenterID = image.getExperimenter()
							.getElementID();
					final long expOMEID = image.getExperimenter().getOmeroId();
					if ((experimenterID == -1) && (expOMEID != -1)) {
						experimenterID = reader.getExperimenterID(expOMEID);
						// image.getExperimenter().setElementID(experimenterID);
						if ((experimenterID != -1)
								&& !this.experimentersToBeLoaded
										.contains(experimenterID)) {
							this.experimentersToBeLoaded.add(experimenterID);
							imageElements++;
						}
					}
					long imageID = image.getElementID();
					final long imageOMEID = image.getOmeroId();
					if ((imageID == -1) && (imageOMEID != -1)) {
						imageID = reader.getImageID(imageOMEID);
						// image.setElementID(imageID);
						if ((imageID != -1)
								&& !this.imagesToBeLoaded.contains(imageID)) {
							this.imagesToBeLoaded.add(imageID);
							imageElements++;
						}
					}
					for (final OmegaImagePixels imagePixels : image.getPixels()) {
						long imagePixelsID = imagePixels.getElementID();
						final long imagePixelsOMEID = imagePixels.getOmeroId();
						if ((imagePixelsID == -1) && (imagePixelsOMEID != -1)) {
							imagePixelsID = reader
									.getImagePixelsID(imagePixelsOMEID);
							// imagePixels.setElementID(imagePixelsID);
							if ((imagePixelsID != -1)
									&& !this.pixelsToBeLoaded
											.contains(imagePixelsID)) {
								this.pixelsToBeLoaded.add(imagePixelsID);
								imageElements++;
							}
						}
						for (int c = 0; c <= imagePixels.getSizeC(); c++) {
							for (int z = 0; z <= imagePixels.getSizeZ(); z++) {
								for (final OmegaPlane frame : imagePixels
										.getFrames(c, z)) {
									final long frameID = frame.getElementID();
									if (frameID == -1) {
										this.framesToBeLoaded.add(frameID);
									} else {
										this.loadedPlanes.put(frameID, frame);
									}
									imageElements += this
											.prepareFrameElementsToLoad(frameID);
									imageElements++;
								}
							}
						}
						// final List<Long> frameIDs = reader
						// .getFrameIDs(imagePixelsID);
						// for (final Long frameID : frameIDs) {
						// if (!this.framesToBeLoaded.contains(frameID)) {
						// this.framesToBeLoaded.add(frameID);
						// }
						// imageElements += this
						// .prepareFrameElementsToLoad(frameID);
						// imageElements++;
						// }
						imageElements += this
								.prepareImagePixelsElementsToLoad(imagePixelsID);
					}
					imageElements += this.prepareImageElementsToLoad(imageID);
					this.imageElementSizeToLoad.put(imageID, imageElements);
					datasetElements += imageElements;
				}
				datasetElements += this.prepareDatasetElementsToLoad(datasetID);
				this.datasetElementSizeToLoad.put(datasetID, datasetElements);
				projectElements += datasetElements;
			}
			projectElements += this.prepareProjectElementsToLoad(projectID);
			this.projectElementSizeToLoad.put(projectID, projectElements);
		}
	}

	private int prepareProjectElementsToLoad(final Long containerID)
			throws SQLException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<Long> analysisIDs = reader
				.getProjectContainerAnalysisIDs(containerID);
		return this.prepareElementsToLoad(containerID, analysisIDs);
	}

	private int prepareDatasetElementsToLoad(final Long containerID)
			throws SQLException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<Long> analysisIDs = reader
				.getDatasetContainerAnalysisIDs(containerID);
		return this.prepareElementsToLoad(containerID, analysisIDs);
	}

	private int prepareImageElementsToLoad(final Long containerID)
			throws SQLException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<Long> analysisIDs = reader
				.getImageContainerAnalysisIDs(containerID);
		return this.prepareElementsToLoad(containerID, analysisIDs);
	}

	private int prepareImagePixelsElementsToLoad(final Long containerID)
			throws SQLException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<Long> analysisIDs = reader
				.getImagePixelsContainerAnalysisIDs(containerID);
		return this.prepareElementsToLoad(containerID, analysisIDs);
	}

	private int prepareFrameElementsToLoad(final Long containerID)
			throws SQLException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<Long> analysisIDs = reader
				.getFrameContainerAnalysisIDs(containerID);
		return this.prepareElementsToLoad(containerID, analysisIDs);
	}

	private int prepareAnalysisElementsToLoad(final Long containerID)
			throws SQLException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		final List<Long> analysisIDs = reader
				.getAnalysisContainerAnalysisIDs(containerID);
		return this.prepareElementsToLoad(containerID, analysisIDs);
	}

	private int prepareElementsToLoad(final Long containerID,
			final List<Long> analysisIDs) throws SQLException {
		final OmegaMySqlReader reader = (OmegaMySqlReader) this.getGateway();
		int containerElements = 0;
		for (final Long analysisID : analysisIDs) {
			this.updateMessage(1,
					"Preparing analysis " + String.valueOf(analysisID) + "...");
			int analysisElements = 1;
			if (!this.analysisToBeLoaded.contains(analysisID)) {
				this.analysisToBeLoaded.add(analysisID);
			}
			final List<Long> expIDs = reader
					.getAnalysisExperimenter(analysisID);
			if (expIDs.size() > 1) {
				// TODO error
			}
			final long experimenterID = expIDs.get(0);
			analysisElements++;
			if (!this.experimentersToBeLoaded.contains(experimenterID)) {
				this.experimentersToBeLoaded.add(experimenterID);
			}
			final List<Long> algoSpecIDs = reader
					.getAnalysisAlgorithmSpecificationID(analysisID);
			if (algoSpecIDs.size() > 1) {
				// TODO error
			}
			final long algoSpecID = algoSpecIDs.get(0);
			analysisElements++;
			if (!this.algoSpecsToBeLoaded.contains(algoSpecID)) {
				this.algoSpecsToBeLoaded.add(algoSpecID);
			}
			final List<Long> algoInfoIDs = reader
					.getAlgorithmSpecificationInformationID(algoSpecID);
			if (algoInfoIDs.size() > 1) {
				// TODO error
			}
			final long algoInfoID = algoInfoIDs.get(0);
			analysisElements++;
			if (!this.algoInfosToBeLoaded.contains(algoInfoID)) {
				this.algoInfosToBeLoaded.add(algoInfoID);
			}
			// final List<Long> authorIDs = reader
			// .getAlgorithmInformationAuthorID(algoInfoID);
			// if (authorIDs.size() > 1) {
			// // TODO error
			// }
			// final long authorID = authorIDs.get(0);
			// analysisElements++;
			// if (!this.personsToBeLoaded.contains(authorID)) {
			// this.personsToBeLoaded.add(authorID);
			// }
			this.analysisElementSizeToLoad.put(analysisID, analysisElements);
			analysisElements += this.prepareAnalysisElementsToLoad(analysisID);
			containerElements += analysisElements;
		}
		return containerElements;
	}

	// Old system
	private void oldRun() {
		final int projectsSize = this.projectsToLoad.size();
		int projectLoaded = 0;
		int datasetLoaded = 0;
		int imageLoaded = 0;
		for (final OmegaProject project : this.projectsToLoad) {
			// Load project
			projectLoaded++;
			final StringBuffer buf = new StringBuffer();
			buf.append("<html>Loading progress");
			buf.append("<br>project(s): ");
			buf.append(projectLoaded);
			buf.append(" of ");
			buf.append(projectsSize);
			final int datasetsSize = project.getDatasets().size();
			datasetLoaded = 0;
			for (final OmegaDataset dataset : project.getDatasets()) {
				// Load dataset
				datasetLoaded++;
				final int imagesSize = dataset.getImages().size();
				buf.append("<br>dataset(s): ");
				buf.append(datasetLoaded);
				buf.append(" of ");
				buf.append(datasetsSize);
				imageLoaded = 0;
				for (final OmegaImage image : dataset.getImages()) {
					imageLoaded++;
					buf.append("<br>image(s): ");
					buf.append(imageLoaded);
					buf.append(" of ");
					buf.append(imagesSize);
					buf.append("</html>");
					this.updateMessage(null, buf.toString());
					// try {
					// this.getGateway().loadImages(image);
					// } catch (final SQLException ex) {
					// ex.printStackTrace();
					// } catch (final ParseException ex) {
					// ex.printStackTrace();
					// }
				}
			}
		}
		this.notifyProcessEndToApplication();

	}
}
