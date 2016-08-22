package edu.umassmed.omega.core.runnables;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAlgorithmInformation;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainer;
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
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.coreElements.OmegaPlane;
import edu.umassmed.omega.commons.data.coreElements.OmegaProject;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaParticle;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationType;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.gui.dialogs.GenericProgressDialog;
import edu.umassmed.omega.core.OmegaApplication;
import edu.umassmed.omega.core.mysql.OmegaMySqlCostants;
import edu.umassmed.omega.core.mysql.OmegaMySqlReader;
import edu.umassmed.omega.core.mysql.OmegaMySqlWriter;

public class OmegaDBSaver extends OmegaDBWriter {

	private final List<OmegaProject> projectsToSave;

	private List<OmegaElement> toBeSaved;

	private int counter;
	private Map<OmegaElement, Integer> counters;

	private Map<OmegaElement, Integer> elementSizeToSave;

	private final Map<Long, Long> particleROIMap;

	private final OmegaMySqlReader reader;

	private final int saveType;

	public OmegaDBSaver(final OmegaApplication omegaApp,
			final OmegaMySqlWriter writer, final OmegaMySqlReader reader,
			final GenericProgressDialog dialog,
			final List<OmegaProject> projects, final int saveType) {
		super(omegaApp, writer, dialog);
		this.projectsToSave = projects;

		this.reader = reader;

		this.saveType = saveType;

		this.particleROIMap = new LinkedHashMap<Long, Long>();
	}

	@Override
	public void run() {
		this.counter = 0;
		this.updateMessage(null, "Preparing for saving process...");
		this.toBeSaved = new ArrayList<OmegaElement>();
		this.counters = new LinkedHashMap<OmegaElement, Integer>();
		this.elementSizeToSave = new LinkedHashMap<OmegaElement, Integer>();
		// I should load all persons / experimenter BEFORE!!
		// Otherwise I have to check all names in the db
		// Maybe load them all in the OmegaData structure?!
		if (this.saveType == OmegaDBRunnable.LOAD_TYPE_LOADED_IMAGE) {
			try {
				this.prepareElementsToSave();
				this.updateMaxProgress(null, this.toBeSaved.size());
				this.updateMessage(null, "Saving...");
				this.saveElements();
			} catch (final SQLException e) {
				this.setErrorOccured();
				// this.setDialogClosable();
				e.printStackTrace();
			} catch (final ParseException e) {
				this.setErrorOccured();
				// this.setDialogClosable();
				e.printStackTrace();
			}
		} else {

		}
		this.setDialogClosable();
		this.notifyProcessEndToApplication();
	}

	private void saveElements() throws SQLException, ParseException {
		final OmegaMySqlWriter writer = (OmegaMySqlWriter) this.getGateway();
		final List<OmegaElement> parents = new ArrayList<OmegaElement>();
		for (final OmegaProject project : this.projectsToSave) {
			int projectCounter = 0;
			this.counters.put(project, projectCounter);
			parents.add(project);
			this.updateMessage(project, "Saving project " + project.getName()
					+ "...");
			this.updateMaxProgress(project, this.elementSizeToSave.get(project));
			long projectID = project.getElementID();
			if (projectID == -1) {
				projectID = writer.saveProject(project);
				project.setElementID(projectID);
				projectCounter++;
				this.updateCurrentProgress(project, projectCounter);
				this.counters.put(project, projectCounter);
				this.counter++;
				this.updateCurrentProgress(null, this.counter);
			}
			for (final OmegaDataset dataset : project.getDatasets()) {
				int datasetCounter = 0;
				this.counters.put(dataset, datasetCounter);
				parents.add(dataset);
				this.updateMessage(dataset,
						"Saving dataset " + dataset.getName() + "...");
				this.updateMaxProgress(dataset,
				        this.elementSizeToSave.get(dataset));
				long datasetID = dataset.getElementID();
				if (datasetID == -1) {
					datasetID = writer.saveDataset(dataset, projectID);
					dataset.setElementID(datasetID);
					projectCounter++;
					this.updateCurrentProgress(project, projectCounter);
					this.counters.put(project, projectCounter);
					datasetCounter++;
					this.updateCurrentProgress(dataset, datasetCounter);
					this.counters.put(dataset, datasetCounter);
					this.counter++;
					this.updateCurrentProgress(null, this.counter);
				}
				for (final OmegaImage image : dataset.getImages()) {
					int imageCounter = 0;
					this.counters.put(image, imageCounter);
					parents.add(image);
					this.updateMessage(image, "Saving image " + image.getName()
							+ "...");
					this.updateMaxProgress(image,
							this.elementSizeToSave.get(image));
					long experimenterID = image.getExperimenter()
							.getElementID();
					if (experimenterID == -1) {
						final long personID = writer.savePerson(image
								.getExperimenter());
						experimenterID = writer.saveExperimenter(
								image.getExperimenter(), personID);
						image.getExperimenter().setElementID(experimenterID);
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
					long imageID = image.getElementID();
					if (imageID == -1) {
						imageID = writer.saveImage(image, // datasetID,
								experimenterID);
						image.setElementID(imageID);
						writer.saveImageDatasetLink(image, dataset);
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
						if (imagePixelsID == -1) {
							imagePixelsID = writer.saveImagePixels(pixels,
									imageID);
							pixels.setElementID(imagePixelsID);
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
						for (int c = 0; c <= pixels.getSizeC(); c++) {
							for (int z = 0; z <= pixels.getSizeZ(); z++) {
								for (final OmegaPlane frame : pixels.getFrames(
										c, z)) {
									long frameID = frame.getElementID();
									if (frameID == -1) {
										frameID = writer.saveFrame(frame,
												imagePixelsID);
										frame.setElementID(frameID);
									}
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
									this.saveElements(frame,
											OmegaMySqlCostants.FRAME_ID_FIELD,
									        parents);
								}
							}
						}
						this.saveElements(pixels,
								OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD,
						        parents);
					}
					this.saveElements(image, OmegaMySqlCostants.IMAGE_ID_FIELD,
					        parents);
					parents.remove(image);
				}
				// this.updateMessage(dataset,
				// "Saving dataset " + dataset.getName() + "...");
				// this.updateMaxProgress(dataset,
				// this.elementSizeToSave.get(dataset));
				this.saveElements(dataset, OmegaMySqlCostants.DATASET_ID_FIELD,
				        parents);
				parents.remove(dataset);
			}
			// this.updateMessage(project, "Saving project " + project.getName()
			// + "...");
			// this.updateMaxProgress(project,
			// this.elementSizeToSave.get(project));
			this.saveElements(project, OmegaMySqlCostants.PROJECT_ID_FIELD,
			        parents);
			parents.remove(project);
		}
	}

	private void saveElements(final OmegaAnalysisRunContainer container,
	        final String parentElementField, final List<OmegaElement> parents)
	        throws SQLException, ParseException {
		final OmegaMySqlWriter writer = (OmegaMySqlWriter) this.getGateway();
		final long parentElementID = ((OmegaElement) container).getElementID();
		for (final OmegaAnalysisRun analysisRun : container.getAnalysisRuns()) {
			this.updateMessage(analysisRun,
			        "Saving analysis " + analysisRun.getName() + "...");
			final int elements = this.elementSizeToSave.get(analysisRun);
			this.updateMaxProgress(analysisRun, elements);
			int analysisCounter = 0;
			this.counters.put(analysisRun, analysisCounter);
			this.updateCurrentProgress(analysisRun, analysisCounter);
			long experimenterID = analysisRun.getExperimenter().getElementID();
			if (experimenterID == -1) {
				final long personID = writer.savePerson(analysisRun
				        .getExperimenter());
				experimenterID = writer.saveExperimenter(
				        analysisRun.getExperimenter(), personID);
				analysisRun.getExperimenter().setElementID(experimenterID);
				for (final OmegaElement element : parents) {
					int c = this.counters.get(element);
					c++;
					this.counters.put(element, c);
					this.updateCurrentProgress(element, c);
				}
				analysisCounter++;
				this.updateCurrentProgress(analysisRun, analysisCounter);
				this.counters.put(analysisRun, analysisCounter);
				this.counter++;
				this.updateCurrentProgress(null, this.counter);
			}
			final OmegaRunDefinition algoSpec = analysisRun.getAlgorithmSpec();
			final OmegaAlgorithmInformation algoInfo = algoSpec
			        .getAlgorithmInfo();
			long authorID = algoInfo.getAuthor().getElementID();
			if (authorID == -1) {
				authorID = writer.savePerson(algoInfo.getAuthor());
				algoInfo.getAuthor().setElementID(authorID);
				for (final OmegaElement element : parents) {
					int c = this.counters.get(element);
					c++;
					this.counters.put(element, c);
					this.updateCurrentProgress(element, c);
				}
				analysisCounter++;
				this.updateCurrentProgress(analysisRun, analysisCounter);
				this.counters.put(analysisRun, analysisCounter);
				this.counter++;
				this.updateCurrentProgress(null, this.counter);
			}
			long algoInfoID = algoInfo.getElementID();
			if (algoInfoID == -1) {
				algoInfoID = writer
				        .saveAlgorithmInformation(algoInfo, authorID);
				algoInfo.setElementID(algoInfoID);
				for (final OmegaElement element : parents) {
					int c = this.counters.get(element);
					c++;
					this.counters.put(element, c);
					this.updateCurrentProgress(element, c);
				}
				analysisCounter++;
				this.updateCurrentProgress(analysisRun, analysisCounter);
				this.counters.put(analysisRun, analysisCounter);
				this.counter++;
				this.updateCurrentProgress(null, this.counter);
			}
			long algoSpecID = algoSpec.getElementID();
			if (algoSpec.getElementID() == -1) {
				algoSpecID = writer.saveAlgorithmSpecification(algoSpec,
				        algoInfoID);
				algoSpec.setElementID(algoSpecID);
				for (final OmegaElement element : parents) {
					int c = this.counters.get(element);
					c++;
					this.counters.put(element, c);
					this.updateCurrentProgress(element, c);
				}
				analysisCounter++;
				this.updateCurrentProgress(analysisRun, analysisCounter);
				this.counters.put(analysisRun, analysisCounter);
				this.counter++;
				this.updateCurrentProgress(null, this.counter);
			}
			for (final OmegaParameter param : algoSpec.getParameters()) {
				long paramID = param.getElementID();
				if (paramID == -1) {
					paramID = writer.saveParameter(param, algoSpecID);
					param.setElementID(paramID);
					for (final OmegaElement element : parents) {
						int c = this.counters.get(element);
						c++;
						this.counters.put(element, c);
						this.updateCurrentProgress(element, c);
					}
					analysisCounter++;
					this.updateCurrentProgress(analysisRun, analysisCounter);
					this.counters.put(analysisRun, analysisCounter);
					this.counter++;
					this.updateCurrentProgress(null, this.counter);
				}
			}
			long analysisID = analysisRun.getElementID();
			if (analysisID == -1) {
				analysisID = writer.saveAnalysisRun(analysisRun,
				        experimenterID, algoSpecID);
				analysisRun.setElementID(analysisID);
				writer.saveAnalysisRunElementLink(analysisID,
				        parentElementField, parentElementID);
				if (analysisRun instanceof OmegaParticleDetectionRun) {
					final OmegaParticleDetectionRun detRun = (OmegaParticleDetectionRun) analysisRun;
					this.saveParticles(writer, detRun.getResultingParticles(),
					        detRun.getResultingParticlesValues(), analysisID);
				} else if (analysisRun instanceof OmegaParticleLinkingRun) {
					final OmegaParticleLinkingRun linkRun = (OmegaParticleLinkingRun) analysisRun;
					this.saveTrajectories(writer,
					        linkRun.getResultingTrajectories(), analysisID);
				} else if (analysisRun instanceof OmegaTrajectoriesRelinkingRun) {
					final OmegaTrajectoriesRelinkingRun relinkRun = (OmegaTrajectoriesRelinkingRun) analysisRun;
					this.saveTrajectories(writer,
					        relinkRun.getResultingTrajectories(), analysisID);
				} else if (analysisRun instanceof OmegaTrajectoriesSegmentationRun) {
					final OmegaTrajectoriesSegmentationRun segmRun = (OmegaTrajectoriesSegmentationRun) analysisRun;
					long segmTypesID = segmRun.getSegmentationTypes()
					        .getElementID();
					if (segmTypesID == -1) {
						segmTypesID = writer.saveSegmentationTypes(segmRun
						        .getSegmentationTypes());
						writer.saveAnalysisSegmentationTypesLink(analysisID,
								segmTypesID);
					}
					this.saveSegmentationTypes(writer, segmRun
					        .getSegmentationTypes().getTypes(), segmTypesID);
					this.saveSegments(writer, segmRun.getResultingSegments(),
					        segmTypesID, analysisID);
				} else if (analysisRun instanceof OmegaTrackingMeasuresIntensityRun) {
					final OmegaTrackingMeasuresIntensityRun measureIntensityRun = (OmegaTrackingMeasuresIntensityRun) analysisRun;
					final long trackingMeasuresID = writer
					        .saveTrackingMeasuresRun(measureIntensityRun,
					                analysisID);
					this.saveTrackingMeasuresSegmentLink(writer,
					        measureIntensityRun.getSegments(),
					        trackingMeasuresID);
					writer.saveIntensityTrackingMeasuresRun(
					        measureIntensityRun, trackingMeasuresID);
				} else if (analysisRun instanceof OmegaTrackingMeasuresVelocityRun) {
					final OmegaTrackingMeasuresVelocityRun measureVelocityRun = (OmegaTrackingMeasuresVelocityRun) analysisRun;
					final long trackingMeasuresID = writer
					        .saveTrackingMeasuresRun(measureVelocityRun,
					                analysisID);
					this.saveTrackingMeasuresSegmentLink(writer,
					        measureVelocityRun.getSegments(),
					        trackingMeasuresID);
					writer.saveVelocityTrackingMeasuresRun(measureVelocityRun,
					        trackingMeasuresID);
				} else if (analysisRun instanceof OmegaTrackingMeasuresMobilityRun) {
					final OmegaTrackingMeasuresMobilityRun measureMobilityRun = (OmegaTrackingMeasuresMobilityRun) analysisRun;
					final long trackingMeasuresID = writer
					        .saveTrackingMeasuresRun(measureMobilityRun,
					                analysisID);
					this.saveTrackingMeasuresSegmentLink(writer,
					        measureMobilityRun.getSegments(),
					        trackingMeasuresID);
					writer.saveMobilityTrackingMeasuresRun(measureMobilityRun,
					        trackingMeasuresID);
				} else if (analysisRun instanceof OmegaTrackingMeasuresDiffusivityRun) {
					final OmegaTrackingMeasuresDiffusivityRun measureDiffusivityRun = (OmegaTrackingMeasuresDiffusivityRun) analysisRun;
					final long trackingMeasuresID = writer
					        .saveTrackingMeasuresRun(measureDiffusivityRun,
					                analysisID);
					this.saveTrackingMeasuresSegmentLink(writer,
					        measureDiffusivityRun.getSegments(),
					        trackingMeasuresID);
					writer.saveDiffusivityTrackingMeasuresRun(
					        measureDiffusivityRun, trackingMeasuresID);
				} else if (analysisRun instanceof OmegaSNRRun) {
					final OmegaSNRRun snrRun = (OmegaSNRRun) analysisRun;
					writer.saveSNRRun(snrRun);
				} else {
					// ERROR!!!!
				}
				for (final OmegaElement element : parents) {
					int c = this.counters.get(element);
					c++;
					this.counters.put(element, c);
					this.updateCurrentProgress(element, c);
				}
				analysisCounter++;
				this.updateCurrentProgress(analysisRun, analysisCounter);
				this.counters.put(analysisRun, analysisCounter);
				this.counter++;
				this.updateCurrentProgress(null, this.counter);
			}
			// parents.add(analysisRun);
			this.saveElements(analysisRun,
			        OmegaMySqlCostants.ANALYSIS_PARENT_ID_FIELD, parents);
			// parents.remove(analysisRun);
		}
	}

	private void saveParticles(final OmegaMySqlWriter writer,
			final Map<OmegaPlane, List<OmegaROI>> particles,
			final Map<OmegaROI, Map<String, Object>> particleValues,
			final long analysisRunID) throws SQLException {
		for (final OmegaPlane frame : particles.keySet()) {
			for (final OmegaROI roi : particles.get(frame)) {
				final long roiID = writer.saveROI(roi, frame.getElementID());
				writer.saveROIValues(particleValues.get(roi), analysisRunID,
						roiID);
				roi.setElementID(roiID);
				if (roi instanceof OmegaParticle) {
					final long particleID = writer.saveParticle(
							(OmegaParticle) roi, roiID, analysisRunID);
					this.particleROIMap.put(particleID, roiID);
					roi.setElementID(particleID);
				}
			}
		}
	}

	private void saveTrajectories(final OmegaMySqlWriter writer,
	        final List<OmegaTrajectory> tracks, final long analysisRunID)
	        throws SQLException {
		for (final OmegaTrajectory trajectory : tracks) {
			long trajectoryID = trajectory.getElementID();
			// System.out.println(trajectory.getName() + " " + trajectoryID);
			if (trajectoryID == -1) {
				trajectoryID = writer.saveTrajectory(trajectory);
				trajectory.setElementID(trajectoryID);
				// FIXME I SHOULD CHECK THE PRESENCE OF THE LINK BEFORE SAVING?
				// OR IS GONNA JUST OVERRIDE?
				for (final OmegaROI roi : trajectory.getROIs()) {
					final long roiID = roi.getElementID();
					writer.saveTrajectoryROILink(trajectoryID, roiID);
				}
			}
			writer.saveAnalysisRunTrajectoryLink(analysisRunID, trajectoryID);
		}
	}

	private void saveSegmentationTypes(final OmegaMySqlWriter writer,
			final List<OmegaSegmentationType> segmTypes, final long segmTypesID)
	        throws SQLException {
		for (final OmegaSegmentationType segmType : segmTypes) {
			long segmTypeID = segmType.getElementID();
			if (segmTypeID == -1) {
				segmTypeID = writer.saveSegmentationType(segmType);
			}
			segmType.setElementID(segmTypeID);
			writer.saveSegmentationTypesTypeLink(segmTypesID, segmTypeID);
		}
	}

	private void saveSegments(final OmegaMySqlWriter writer,
	        final Map<OmegaTrajectory, List<OmegaSegment>> segments,
	        final long segmTypesID, final long analysisID) throws SQLException {
		for (final OmegaTrajectory trajectory : segments.keySet()) {
			final List<OmegaSegment> trackSegments = segments.get(trajectory);
			for (final OmegaSegment segment : trackSegments) {
				long segmentID = segment.getElementID();
				if (segmentID == -1) {
					long startingROIID = segment.getStartingROI()
					        .getElementID();
					// System.out.print(startingROIID + " - ");
					if (segment.getStartingROI() instanceof OmegaParticle) {
						startingROIID = this.particleROIMap.get(startingROIID);
						// System.out.print(startingROIID);
					}
					// System.out.println();
					long endingROIID = segment.getEndingROI().getElementID();
					// System.out.print(endingROIID + " - ");
					if (segment.getEndingROI() instanceof OmegaParticle) {
						endingROIID = this.particleROIMap.get(endingROIID);
						// System.out.print(endingROIID);
					}
					// System.out.println();
					segmentID = writer.saveSegment(segment,
					        trajectory.getElementID(), analysisID,
					        startingROIID, endingROIID);
					segment.setElementID(segmentID);
				}
			}
		}
	}

	private void saveTrackingMeasuresSegmentLink(final OmegaMySqlWriter writer,
			final Map<OmegaTrajectory, List<OmegaSegment>> segments,
			final long trackingMeasuresID) throws SQLException {
		for (final OmegaTrajectory trajectory : segments.keySet()) {
			final List<OmegaSegment> trackSegments = segments.get(trajectory);
			for (final OmegaSegment segment : trackSegments) {
				final long segmentID = segment.getElementID();
				writer.saveTrackingMeasuresSegmentLink(trackingMeasuresID,
						segmentID);
			}
		}
	}

	private void prepareElementsToSave() throws SQLException {
		for (final OmegaProject project : this.projectsToSave) {
			this.updateMessage(project,
			        "Preparing project " + project.getName() + "...");
			int projectElements = 0;
			long projectID = project.getElementID();
			final long projectOMEID = project.getOmeroId();
			if ((projectID == -1) && (projectOMEID != -1)) {
				projectID = this.reader.getProjectID(projectOMEID);
				project.setElementID(projectID);
			}
			if (projectID == -1) {
				if (!this.toBeSaved.contains(project)) {
					this.toBeSaved.add(project);
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
					datasetID = this.reader.getDatasetID(datasetOMEID);
					dataset.setElementID(datasetID);
				}
				if (datasetID == -1) {
					if (!this.toBeSaved.contains(dataset)) {
						this.toBeSaved.add(dataset);
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
						experimenterID = this.reader
								.getExperimenterID(expOMEID);
						image.getExperimenter().setElementID(experimenterID);
					}
					if (experimenterID == -1) {
						if (!this.toBeSaved.contains(image.getExperimenter())) {
							this.toBeSaved.add(image.getExperimenter());
							imageElements++;
						}
					}
					long imageID = image.getElementID();
					final long imageOMEID = image.getExperimenter()
							.getOmeroId();
					if ((imageID == -1) && (imageOMEID != -1)) {
						imageID = this.reader.getImageID(imageOMEID);
						image.setElementID(imageID);
					}
					if (imageID == -1) {
						if (!this.toBeSaved.contains(image)) {
							this.toBeSaved.add(image);
							imageElements++;
						}
					}
					for (final OmegaImagePixels pixels : image.getPixels()) {
						long imagePixelsID = pixels.getElementID();
						final long imagePixelsOMEID = pixels.getOmeroId();
						if ((imagePixelsID == -1) && (imagePixelsOMEID != -1)) {
							imagePixelsID = this.reader
							        .getImagePixelsID(imagePixelsOMEID);
							pixels.setElementID(imageID);
						}
						if (imagePixelsID == -1) {
							if (!this.toBeSaved.contains(pixels)) {
								this.toBeSaved.add(pixels);
								imageElements++;
							}
						}
						for (int c = 0; c <= pixels.getSizeC(); c++) {
							for (int z = 0; z <= pixels.getSizeZ(); z++) {
								for (final OmegaPlane frame : pixels.getFrames(
										c, z)) {
									if (frame.getElementID() == -1) {
										if (!this.toBeSaved.contains(frame)) {
											this.toBeSaved.add(frame);
											imageElements++;
										}
									}
									imageElements += this
											.prepareElementsToSave(frame);
								}
							}
						}
						imageElements += this.prepareElementsToSave(pixels);
					}
					imageElements += this.prepareElementsToSave(image);
					this.elementSizeToSave.put(image, imageElements);
					datasetElements += imageElements;
				}
				datasetElements += this.prepareElementsToSave(dataset);
				this.elementSizeToSave.put(dataset, datasetElements);
				projectElements += datasetElements;
			}
			projectElements += this.prepareElementsToSave(project);
			this.elementSizeToSave.put(project, projectElements);
		}
		// this.totalNum = this.toBeSaved.size();
	}

	private int prepareElementsToSave(final OmegaAnalysisRunContainer container)
			throws SQLException {
		int containerElements = 0;
		for (final OmegaAnalysisRun analysisRun : container.getAnalysisRuns()) {
			this.updateMessage(analysisRun,
			        "Preparing analysis " + analysisRun.getName() + "...");
			int analysisElements = 0;
			if (analysisRun.getElementID() == -1) {
				if (!this.toBeSaved.contains(analysisRun)) {
					this.toBeSaved.add(analysisRun);
					analysisElements++;
				}
			}
			long experimenterID = analysisRun.getExperimenter().getElementID();
			final long expOMEID = analysisRun.getExperimenter().getOmeroId();
			if ((experimenterID == -1) && (expOMEID != -1)) {
				experimenterID = this.reader.getExperimenterID(expOMEID);
				analysisRun.getExperimenter().setElementID(experimenterID);
			}
			if (experimenterID == -1) {
				if (!this.toBeSaved.contains(analysisRun.getExperimenter())) {
					this.toBeSaved.add(analysisRun.getExperimenter());
					analysisElements++;
				}
			}
			final OmegaRunDefinition algoSpec = analysisRun.getAlgorithmSpec();
			if (algoSpec.getElementID() == -1) {
				if (!this.toBeSaved.contains(algoSpec)) {
					this.toBeSaved.add(algoSpec);
					analysisElements++;
				}
			}
			final OmegaAlgorithmInformation algoInfo = algoSpec
					.getAlgorithmInfo();
			if (algoInfo.getElementID() == -1) {
				if (!this.toBeSaved.contains(algoInfo)) {
					this.toBeSaved.add(algoInfo);
					analysisElements++;
				}
			}
			if (algoInfo.getAuthor().getElementID() == -1) {
				// this.personNum++;
				if (!this.toBeSaved.contains(algoInfo.getAuthor())) {
					this.toBeSaved.add(algoInfo.getAuthor());
					analysisElements++;
				}
			}
			for (final OmegaParameter param : algoSpec.getParameters()) {
				// this.paramNum++;
				if (!this.toBeSaved.contains(param)) {
					this.toBeSaved.add(param);
					analysisElements++;
				}
			}
			this.elementSizeToSave.put(analysisRun, analysisElements);
			analysisElements += this.prepareElementsToSave(analysisRun);
			containerElements += analysisElements;
		}
		return containerElements;
	}

	// Old System
	private void saveProjects() {
		final int projectsSize = this.projectsToSave.size();
		int projectLoaded = 0;
		int datasetLoaded = 0;
		int imageLoaded = 0;

		for (final OmegaProject project : this.projectsToSave) {
			// Load project
			projectLoaded++;
			final StringBuffer buf = new StringBuffer();
			buf.append("<html>Saving progress");
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
					for (final OmegaAnalysisRun analysisRun : image
							.getAnalysisRuns()) {
						// try {
						// this.getGateway().saveAnalysisRun(image,
						// analysisRun);
						// this.saveInnerAnalysis(analysisRun);
						// } catch (final SQLException ex) {
						// this.setErrorOccured();
						// ex.printStackTrace();
						// }
					}
				}
			}
		}
	}

	private void saveInnerAnalysis(final OmegaAnalysisRun analysisRun)
			throws SQLException {
		for (final OmegaAnalysisRun innerAnalysisRun : analysisRun
				.getAnalysisRuns()) {
			new Integer(analysisRun.getElementID().toString());
			// this.getGateway().saveAnalysisRun(id, innerAnalysisRun);
			this.saveInnerAnalysis(innerAnalysisRun);
		}
	}

}
