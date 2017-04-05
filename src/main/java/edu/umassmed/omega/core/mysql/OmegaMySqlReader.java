package edu.umassmed.omega.core.mysql;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.AnalysisRunType;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAlgorithmInformation;
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
import edu.umassmed.omega.commons.data.coreElements.OmegaExperimenter;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.coreElements.OmegaPerson;
import edu.umassmed.omega.commons.data.coreElements.OmegaPlane;
import edu.umassmed.omega.commons.data.coreElements.OmegaProject;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaParticle;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationType;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;

public class OmegaMySqlReader extends OmegaMySqlGateway {

	public OmegaMySqlReader() {

	}

	// ****** Generic elements ****** //

	private Long getIDByOmeroID(final long omeroID, final String table,
			final String field) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT ");
		query1.append(field);
		query1.append(" FROM ");
		query1.append(table);
		query1.append(" WHERE ");
		query1.append(OmegaMySqlCostants.OMERO_ID_FIELD);
		query1.append(" = ");
		query1.append(omeroID);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
				.toString());
		final ResultSet results1 = stat1.executeQuery();
		if (!results1.next()) {
			results1.close();
			stat1.close();
			return -1L;
		}
		final int dbID = results1.getInt(field);
		final long id = OmegaMySqlUtilities.getID(dbID);
		results1.close();
		stat1.close();
		return id;
	}

	private List<Long> getAllIDs(final String table, final String field)
			throws SQLException {
		final List<Long> ids = new ArrayList<Long>();
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT ");
		query1.append(field);
		query1.append(" FROM ");
		query1.append(table);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
				.toString());
		final ResultSet results1 = stat1.executeQuery();
		while (results1.next()) {
			final int dbID = results1.getInt(field);
			final long id = OmegaMySqlUtilities.getID(dbID);
			ids.add(id);
		}
		results1.close();
		stat1.close();
		return ids;
	}

	private List<Long> getAllIDFieldByID(final long specificID,
			final String desiredField, final String table,
			final String specificField) throws SQLException {
		final List<Long> ids = new ArrayList<Long>();
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT ");
		query1.append(desiredField);
		query1.append(" FROM ");
		query1.append(table);
		query1.append(" WHERE ");
		query1.append(specificField);
		query1.append(" = ");
		query1.append(specificID);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
				.toString());
		final ResultSet results1 = stat1.executeQuery();
		while (results1.next()) {
			final int dbID = results1.getInt(desiredField);
			final long id = OmegaMySqlUtilities.getID(dbID);
			ids.add(id);
		}
		results1.close();
		stat1.close();
		return ids;
	}

	private List<Long> getAllIDFieldByDoubleID(final long specificID1,
			final String specificField1, final long specificID2,
			final String specificField2, final String table,
			final String desiredField) throws SQLException {
		final List<Long> ids = new ArrayList<Long>();
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT ");
		query1.append(desiredField);
		query1.append(" FROM ");
		query1.append(table);
		query1.append(" WHERE ");
		query1.append(specificField1);
		query1.append(" = ");
		query1.append(specificID1);
		query1.append(" AND ");
		query1.append(specificField2);
		query1.append(" = ");
		query1.append(specificID2);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
				.toString());
		final ResultSet results1 = stat1.executeQuery();
		while (results1.next()) {
			final int dbID = results1.getInt(desiredField);
			final long id = OmegaMySqlUtilities.getID(dbID);
			ids.add(id);
		}
		results1.close();
		stat1.close();
		return ids;
	}

	// private Long getSpecificIDFieldByID(final long selectedID,
	// final String desiredField, final String table,
	// final String selectedField) throws SQLException {
	// final StringBuffer query1 = new StringBuffer();
	// query1.append("SELECT ");
	// query1.append(desiredField);
	// query1.append(" FROM ");
	// query1.append(table);
	// query1.append(" WHERE ");
	// query1.append(selectedField);
	// query1.append(" = ");
	// query1.append(selectedID);
	// final PreparedStatement stat1 = this.connection.prepareStatement(query1
	// .toString());
	// final ResultSet results1 = stat1.executeQuery();
	// if (!results1.next()) {
	// results1.close();
	// stat1.close();
	// return null;
	// }
	// final int specificDBID = results1.getInt(desiredField);
	// final long specificID = OmegaMySqlUtilities.getID(specificDBID);
	// results1.close();
	// stat1.close();
	// return specificID;
	// }

	private ResultSet load(final long id, final String table, final String field)
			throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * ");
		query1.append(" FROM ");
		query1.append(table);
		query1.append(" WHERE ");
		query1.append(field);
		query1.append(" = ");
		query1.append(id);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
				.toString());
		final ResultSet results1 = stat1.executeQuery();
		if (!results1.next()) {
			results1.close();
			stat1.close();
			return null;
		}
		return results1;
	}

	private ResultSet load(final long id, final String table,
			final String field, final Map<String, String> additionalKeys)
					throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * ");
		query1.append(" FROM ");
		query1.append(table);
		query1.append(" WHERE ");
		query1.append(field);
		query1.append(" = ");
		query1.append(id);
		for (final String key : additionalKeys.keySet()) {
			query1.append(" AND ");
			query1.append(key);
			query1.append(" = ");
			query1.append(additionalKeys.get(key));
		}
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
				.toString());
		final ResultSet results1 = stat1.executeQuery();
		if (!results1.next()) {
			results1.close();
			stat1.close();
			return null;
		}
		return results1;
	}

	// ****** ANALYSIS elements ****** //
	public List<Long> getAnalysisAlgorithmSpecificationID(final Long analysisID)
			throws SQLException {
		return this.getAllIDFieldByID(analysisID,
				OmegaMySqlCostants.ALGO_SPEC_ID_FIELD,
				OmegaMySqlCostants.ANALYSIS_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
	}

	public List<Long> getAnalysisExperimenter(final Long analysisID)
			throws SQLException {
		return this.getAllIDFieldByID(analysisID,
				OmegaMySqlCostants.EXPERIMENTER_ID_FIELD,
				OmegaMySqlCostants.ANALYSIS_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
	}

	public List<Long> getAnalysisIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.ANALYSIS_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
	}

	public List<Long> getProjectContainerAnalysisIDs(final Long projectID)
			throws SQLException {
		return this.getAllIDFieldByID(projectID,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD,
				OmegaMySqlCostants.ANALYSIS_PARENT_TABLE,
				OmegaMySqlCostants.PROJECT_ID_FIELD);
	}

	public List<Long> getDatasetContainerAnalysisIDs(final Long datasetID)
			throws SQLException {
		return this.getAllIDFieldByID(datasetID,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD,
				OmegaMySqlCostants.ANALYSIS_PARENT_TABLE,
				OmegaMySqlCostants.DATASET_ID_FIELD);
	}

	public List<Long> getImageContainerAnalysisIDs(final Long imageID)
			throws SQLException {
		return this.getAllIDFieldByID(imageID,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD,
				OmegaMySqlCostants.ANALYSIS_PARENT_TABLE,
				OmegaMySqlCostants.IMAGE_ID_FIELD);
	}

	public List<Long> getImagePixelsContainerAnalysisIDs(
			final Long imagePixelsID) throws SQLException {
		return this.getAllIDFieldByID(imagePixelsID,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD,
				OmegaMySqlCostants.ANALYSIS_PARENT_TABLE,
				OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD);
	}

	public List<Long> getFrameContainerAnalysisIDs(final Long frameID)
			throws SQLException {
		return this.getAllIDFieldByID(frameID,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD,
				OmegaMySqlCostants.ANALYSIS_PARENT_TABLE,
				OmegaMySqlCostants.FRAME_ID_FIELD);
	}

	public List<Long> getAnalysisContainerAnalysisIDs(final Long analysisID)
			throws SQLException {
		return this.getAllIDFieldByID(analysisID,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD,
				OmegaMySqlCostants.ANALYSIS_PARENT_TABLE,
				OmegaMySqlCostants.ANALYSIS_PARENT_ID_FIELD);
	}

	public AnalysisRunType getAnalysisType(final long id) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT ");
		query1.append(OmegaMySqlCostants.TYPE_FIELD);
		query1.append(" FROM ");
		query1.append(OmegaMySqlCostants.ANALYSIS_TABLE);
		query1.append(" WHERE ");
		query1.append(OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		query1.append(" = ");
		query1.append(id);
		final PreparedStatement stat1 = this.connection.prepareStatement(query1
				.toString());
		final ResultSet results1 = stat1.executeQuery();
		if (!results1.next()) {
			results1.getStatement().close();
			results1.close();
			return null;
		}
		final int type = results1.getInt(OmegaMySqlCostants.TYPE_FIELD);
		results1.getStatement().close();
		results1.close();
		return AnalysisRunType.values()[type];
	}

	public OmegaSNRRun loadSNRAnalysis(final long id,
			final OmegaExperimenter experimenter,
			final OmegaRunDefinition algorithmSpecification,
			final Map<OmegaPlane, Double> resultingImageNoise,
			final Map<OmegaPlane, Double> resultingImageBGR,
			final Map<OmegaPlane, Double> resultingImageAverageSNR,
			final Map<OmegaPlane, Double> resultingImageMinimumSNR,
			final Map<OmegaPlane, Double> resultingImageMaximumSNR,
			final Map<OmegaPlane, Double> resultingImageAverageErrorIndexSNR,
			final Map<OmegaPlane, Double> resultingImageMinimumErrorIndexSNR,
			final Map<OmegaPlane, Double> resultingImageMaximumErrorIndexSNR,
			final Map<OmegaROI, Integer> resultingLocalCenterSignal,
			final Map<OmegaROI, Double> resultingLocalMeanSignal,
			final Map<OmegaROI, Integer> resultingLocalParticleArea,
			final Map<OmegaROI, Integer> resultingLocalPeakSignal,
			final Map<OmegaROI, Double> resultingLocalBackground,
			final Map<OmegaROI, Double> resultingLocalNoise,
			final Map<OmegaROI, Double> resultingLocalSNR,
			final Map<OmegaROI, Double> resultingLocalErrorIndexSNR)
					throws SQLException, ParseException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.ANALYSIS_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final DateFormat format = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final String dateS = results1.getString(OmegaMySqlCostants.DATE_FIELD);
		final Date timeStamps = format.parse(dateS);
		results1.getStatement().close();
		results1.close();
		final ResultSet results2 = this.load(id,
				OmegaMySqlCostants.SNR_GLOBAL_GENERIC_VALUES_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		if (results2 == null)
			return null;
		final Double background = results2
				.getDouble(OmegaMySqlCostants.SNR_BACKGROUND);
		final Double noise = results2.getDouble(OmegaMySqlCostants.SNR_NOISE);
		final Double avgSNR = results2
				.getDouble(OmegaMySqlCostants.SNR_AVG_SNR);
		final Double minSNR = results2
				.getDouble(OmegaMySqlCostants.SNR_MIN_SNR);
		final Double maxSNR = results2
				.getDouble(OmegaMySqlCostants.SNR_MAX_SNR);
		final Double avgErrorIndexSNR = results2
				.getDouble(OmegaMySqlCostants.SNR_AVG_ERRORINDEX_SNR);
		final Double minErrorIndexSNR = results2
				.getDouble(OmegaMySqlCostants.SNR_MIN_ERRORINDEX_SNR);
		final Double maxErrorIndexSNR = results2
				.getDouble(OmegaMySqlCostants.SNR_MAX_ERRORINDEX_SNR);
		results2.getStatement().close();
		results2.close();
		final OmegaSNRRun snrRun = new OmegaSNRRun(experimenter,
				algorithmSpecification, timeStamps, name, resultingImageNoise,
				resultingImageBGR, resultingImageAverageSNR,
				resultingImageMinimumSNR, resultingImageMaximumSNR,
				resultingImageAverageErrorIndexSNR,
				resultingImageMinimumErrorIndexSNR,
				resultingImageMaximumErrorIndexSNR, resultingLocalCenterSignal,
				resultingLocalMeanSignal, resultingLocalParticleArea,
				resultingLocalPeakSignal, resultingLocalBackground,
				resultingLocalNoise, resultingLocalSNR,
				resultingLocalErrorIndexSNR, background, noise, avgSNR, minSNR,
				maxSNR, avgErrorIndexSNR, minErrorIndexSNR, maxErrorIndexSNR);
		snrRun.setElementID(id);
		return snrRun;
	}

	public OmegaTrackingMeasuresDiffusivityRun loadDiffusivityMeasuresAnalysis(
			final long id,
			final OmegaExperimenter experimenter,
			final OmegaRunDefinition algorithmSpecification,
			final Map<OmegaTrajectory, List<OmegaSegment>> segments,
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
			final OmegaSNRRun snrRun,
			final OmegaTrackingMeasuresDiffusivityRun diffusivityRun)
					throws SQLException, ParseException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.ANALYSIS_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final DateFormat format = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final String dateS = results1.getString(OmegaMySqlCostants.DATE_FIELD);
		final Date timeStamps = format.parse(dateS);
		results1.getStatement().close();
		results1.close();
		final OmegaTrackingMeasuresDiffusivityRun diffRun = new OmegaTrackingMeasuresDiffusivityRun(
				experimenter, algorithmSpecification, timeStamps, name,
				segments, ny, mu, logMu, deltaT, logDeltaT, gammaD, gammaDLog,
				gammaLog, smssLog, errorsLog, snrRun, diffusivityRun);
		diffRun.setElementID(id);
		return diffRun;
	}

	public OmegaTrackingMeasuresMobilityRun loadMobilityMeasuresAnalysis(
			final long id,
			final OmegaExperimenter experimenter,
			final OmegaRunDefinition algorithmSpecification,
			final Map<OmegaTrajectory, List<OmegaSegment>> segments,
			final Map<OmegaSegment, List<Double>> distancesMap,
			final Map<OmegaSegment, List<Double>> distancesFromOriginMap,
			final Map<OmegaSegment, List<Double>> displacementsFromOriginMap,
			final Map<OmegaSegment, Double> maxDisplacementesFromOriginMap,
			final Map<OmegaSegment, List<Double>> timeTraveledMap,
			final Map<OmegaSegment, List<Double>> confinementRatioMap,
			final Map<OmegaSegment, List<Double[]>> anglesAndDirectionalChangesMap)
					throws SQLException, ParseException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.ANALYSIS_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final DateFormat format = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final String dateS = results1.getString(OmegaMySqlCostants.DATE_FIELD);
		final Date timeStamps = format.parse(dateS);
		results1.getStatement().close();
		results1.close();
		final OmegaTrackingMeasuresMobilityRun mobRun = new OmegaTrackingMeasuresMobilityRun(
				experimenter, algorithmSpecification, timeStamps, name,
				segments, distancesMap, distancesFromOriginMap,
				displacementsFromOriginMap, maxDisplacementesFromOriginMap,
				timeTraveledMap, confinementRatioMap,
				anglesAndDirectionalChangesMap);
		mobRun.setElementID(id);
		return mobRun;
	}

	public OmegaTrackingMeasuresVelocityRun loadVelocityMeasuresAnalysis(
			final long id, final OmegaExperimenter experimenter,
			final OmegaRunDefinition algorithmSpecification,
			final Map<OmegaTrajectory, List<OmegaSegment>> segments,
			final Map<OmegaSegment, List<Double>> localSpeedMap,
			final Map<OmegaSegment, List<Double>> localSpeedFromOriginMap,
			final Map<OmegaSegment, List<Double>> localVelocityFromOriginMap,
			final Map<OmegaSegment, Double> averageCurvilinearSpeedMap,
			final Map<OmegaSegment, Double> averageStraightLineVelocityMap,
			final Map<OmegaSegment, Double> forwardProgressionLinearityMap)
					throws SQLException, ParseException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.ANALYSIS_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final DateFormat format = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final String dateS = results1.getString(OmegaMySqlCostants.DATE_FIELD);
		final Date timeStamps = format.parse(dateS);
		results1.getStatement().close();
		results1.close();
		final OmegaTrackingMeasuresVelocityRun velRun = new OmegaTrackingMeasuresVelocityRun(
				experimenter, algorithmSpecification, timeStamps, name,
				segments, localSpeedMap, localSpeedFromOriginMap,
				localVelocityFromOriginMap, averageCurvilinearSpeedMap,
				averageStraightLineVelocityMap, forwardProgressionLinearityMap);
		velRun.setElementID(id);
		return velRun;
	}

	public OmegaTrackingMeasuresIntensityRun loadIntensityMeasuresAnalysis(
			final long id, final OmegaExperimenter experimenter,
			final OmegaRunDefinition algorithmSpecification,
			final Map<OmegaTrajectory, List<OmegaSegment>> segments,
			final Map<OmegaSegment, Double[]> peakSignalsMap,
			final Map<OmegaSegment, Double[]> centroidSignalsMap,
			final Map<OmegaROI, Double> peakSignalsLocMap,
			final Map<OmegaROI, Double> centroidSignalsLocMap,
			final Map<OmegaSegment, Double[]> meanSignalsMap,
			final Map<OmegaSegment, Double[]> backgroundsMap,
			final Map<OmegaSegment, Double[]> noisesMap,
			final Map<OmegaSegment, Double[]> areasMap,
			final Map<OmegaSegment, Double[]> snrsMap,
			final Map<OmegaROI, Double> meanSignalsLocMap,
			final Map<OmegaROI, Double> backgroundsLocMap,
			final Map<OmegaROI, Double> noisesLocMap,
			final Map<OmegaROI, Double> areasLocMap,
			final Map<OmegaROI, Double> snrsLocMap, final OmegaSNRRun snrRun)
					throws SQLException, ParseException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.ANALYSIS_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final DateFormat format = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final String dateS = results1.getString(OmegaMySqlCostants.DATE_FIELD);
		final Date timeStamps = format.parse(dateS);
		results1.getStatement().close();
		results1.close();
		final OmegaTrackingMeasuresIntensityRun intRun = new OmegaTrackingMeasuresIntensityRun(
				experimenter, algorithmSpecification, timeStamps, name,
				segments, peakSignalsMap, centroidSignalsMap,
				peakSignalsLocMap, centroidSignalsLocMap, backgroundsMap,
				noisesMap, snrsMap, areasMap, meanSignalsMap,
				backgroundsLocMap, noisesLocMap, snrsLocMap, areasLocMap,
				meanSignalsLocMap, snrRun);
		intRun.setElementID(id);
		return intRun;
	}

	public OmegaTrajectoriesSegmentationRun loadSegmentationAnalysis(
			final long id, final OmegaExperimenter experimenter,
			final OmegaRunDefinition algorithmSpecification,
			final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments,
			final OmegaSegmentationTypes segmentationTypes)
					throws SQLException, ParseException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.ANALYSIS_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final DateFormat format = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final String dateS = results1.getString(OmegaMySqlCostants.DATE_FIELD);
		final Date timeStamps = format.parse(dateS);
		results1.getStatement().close();
		results1.close();
		final OmegaTrajectoriesSegmentationRun segmRun = new OmegaTrajectoriesSegmentationRun(
				experimenter, algorithmSpecification, timeStamps, name,
				resultingSegments, segmentationTypes);
		segmRun.setElementID(id);
		return segmRun;
	}

	public OmegaTrajectoriesRelinkingRun loadRelinkingAnalysis(final long id,
			final OmegaExperimenter experimenter,
			final OmegaRunDefinition algorithmSpecification,
			final List<OmegaTrajectory> resultingTrajectories)
					throws SQLException, ParseException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.ANALYSIS_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final DateFormat format = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final String dateS = results1.getString(OmegaMySqlCostants.DATE_FIELD);
		final Date timeStamps = format.parse(dateS);
		results1.getStatement().close();
		results1.close();
		final OmegaTrajectoriesRelinkingRun relinRun = new OmegaTrajectoriesRelinkingRun(
				experimenter, algorithmSpecification, timeStamps, name,
				resultingTrajectories);
		relinRun.setElementID(id);
		return relinRun;
	}

	public OmegaParticleLinkingRun loadLinkingAnalysis(final long id,
			final OmegaExperimenter experimenter,
			final OmegaRunDefinition algorithmSpecification,
			final List<OmegaTrajectory> resultingTrajectories)
					throws SQLException, ParseException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.ANALYSIS_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final DateFormat format = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final String dateS = results1.getString(OmegaMySqlCostants.DATE_FIELD);
		final Date timeStamps = format.parse(dateS);
		results1.getStatement().close();
		results1.close();
		final OmegaParticleLinkingRun linkRun = new OmegaParticleLinkingRun(
				experimenter, algorithmSpecification, timeStamps, name,
				resultingTrajectories);
		linkRun.setElementID(id);
		return linkRun;
	}

	public OmegaParticleDetectionRun loadDetectionAnalysis(final long id,
			final OmegaExperimenter experimenter,
			final OmegaRunDefinition algorithmSpecification,
			final Map<OmegaPlane, List<OmegaROI>> resultingParticles,
			final Map<OmegaROI, Map<String, Object>> resultingParticlesValues)
					throws SQLException, ParseException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.ANALYSIS_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final DateFormat format = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final String dateS = results1.getString(OmegaMySqlCostants.DATE_FIELD);
		final Date timeStamps = format.parse(dateS);
		results1.getStatement().close();
		results1.close();
		final OmegaParticleDetectionRun detRun = new OmegaParticleDetectionRun(
				experimenter, algorithmSpecification, timeStamps, name,
				resultingParticles, resultingParticlesValues);
		detRun.setElementID(id);
		return detRun;
	}

	// ****** SNR elements ****** //

	public Map<Long, Double> loadSNRPlaneNoises(final long analysisID)
			throws SQLException {
		return this.loadSNRPlaneDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_IMAGE_NOISE_TABLE);
	}

	public Map<Long, Double> loadSNRPlaneBGR(final long analysisID)
			throws SQLException {
		return this.loadSNRPlaneDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_IMAGE_BG_TABLE);
	}

	public Map<Long, Double> loadSNRPlaneAverageSNR(final long analysisID)
			throws SQLException {
		return this.loadSNRPlaneDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_IMAGE_AVG_SNR_TABLE);
	}

	public Map<Long, Double> loadSNRPlaneMinSNR(final long analysisID)
			throws SQLException {
		return this.loadSNRPlaneDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_IMAGE_MIN_SNR_TABLE);
	}

	public Map<Long, Double> loadSNRPlaneMaxSNR(final long analysisID)
			throws SQLException {
		return this.loadSNRPlaneDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_IMAGE_MAX_SNR_TABLE);
	}

	public Map<Long, Double> loadSNRPlaneAverageErrorIndexSNR(
			final long analysisID) throws SQLException {
		return this.loadSNRPlaneDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_IMAGE_AVG_ERROR_INDEX_SNR_TABLE);
	}

	public Map<Long, Double> loadSNRPlaneMinErrorIndexSNR(final long analysisID)
			throws SQLException {
		return this.loadSNRPlaneDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_IMAGE_MIN_ERROR_INDEX_SNR_TABLE);
	}

	public Map<Long, Double> loadSNRPlaneMaxErrorIndexSNR(final long analysisID)
			throws SQLException {
		return this.loadSNRPlaneDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_IMAGE_MAX_ERROR_INDEX_SNR_TABLE);
	}

	public Map<Long, Integer> loadSNRROICenterSignal(final long analysisID)
			throws SQLException {
		return this.loadSNRROIIntegerValuesMap(analysisID,
				OmegaMySqlCostants.SNR_LOCAL_CENTER_SIGNAL_TABLE);
	}

	public Map<Long, Double> loadSNRROIMeanSignal(final long analysisID)
			throws SQLException {
		return this.loadSNRROIDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_LOCAL_MEAN_SIGNAL_TABLE);
	}

	public Map<Long, Integer> loadSNRROIArea(final long analysisID)
			throws SQLException {
		return this.loadSNRROIIntegerValuesMap(analysisID,
				OmegaMySqlCostants.SNR_LOCAL_SIGNAL_SIZE_TABLE);
	}

	public Map<Long, Integer> loadSNRROIPeakSignal(final long analysisID)
			throws SQLException {
		return this.loadSNRROIIntegerValuesMap(analysisID,
				OmegaMySqlCostants.SNR_LOCAL_PEAK_SIGNAL_TABLE);
	}

	public Map<Long, Double> loadSNRROIBackground(final long analysisID)
			throws SQLException {
		return this.loadSNRROIDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_LOCAL_BACKGROUND_TABLE);
	}

	public Map<Long, Double> loadSNRROINoise(final long analysisID)
			throws SQLException {
		return this.loadSNRROIDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_LOCAL_NOISE_TABLE);
	}

	public Map<Long, Double> loadSNRROISNR(final long analysisID)
			throws SQLException {
		return this.loadSNRROIDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_LOCAL_SNR_TABLE);
	}

	public Map<Long, Double> loadSNRROIErrorIndexSNR(final long analysisID)
			throws SQLException {
		return this.loadSNRROIDoubleValuesMap(analysisID,
				OmegaMySqlCostants.SNR_LOCAL_SNR_TABLE_ERROR_INDEX);
	}

	private Map<Long, Integer> loadSNRROIIntegerValuesMap(
			final long analysisID, final String table) throws SQLException {
		final ResultSet results1 = this.load(analysisID, table,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		final Map<Long, Integer> valuesMap = new LinkedHashMap<Long, Integer>();
		do {
			final long planeID = results1
					.getLong(OmegaMySqlCostants.ROI_ID_FIELD);
			final int value = results1.getInt(OmegaMySqlCostants.VALUE_FIELD);
			valuesMap.put(planeID, value);
		} while (results1.next());
		// if (results1.next()) {
		//
		// }
		results1.getStatement().close();
		results1.close();
		return valuesMap;
	}

	private Map<Long, Double> loadSNRROIDoubleValuesMap(final long analysisID,
			final String table) throws SQLException {
		final ResultSet results1 = this.load(analysisID, table,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		final Map<Long, Double> valuesMap = new LinkedHashMap<Long, Double>();
		if (results1 == null)
			return valuesMap;
		do {
			final long roiID = results1
					.getLong(OmegaMySqlCostants.ROI_ID_FIELD);
			final double value = results1
					.getDouble(OmegaMySqlCostants.VALUE_FIELD);
			valuesMap.put(roiID, value);
		} while (results1.next());
		// if (results1.next()) {
		//
		// }
		results1.getStatement().close();
		results1.close();
		return valuesMap;
	}

	private Map<Long, Double> loadSNRPlaneDoubleValuesMap(
			final long analysisID, final String table) throws SQLException {
		final ResultSet results1 = this.load(analysisID, table,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		final Map<Long, Double> valuesMap = new LinkedHashMap<Long, Double>();
		if (results1 == null)
			return valuesMap;
		do {
			final long roiID = results1
					.getLong(OmegaMySqlCostants.FRAME_ID_FIELD);
			final double value = results1
					.getDouble(OmegaMySqlCostants.VALUE_FIELD);
			valuesMap.put(roiID, value);
		} while (results1.next());
		// if (results1.next()) {
		//
		// }
		results1.getStatement().close();
		results1.close();
		return valuesMap;
	}

	// ****** Tracking Measures elements ****** //
	public List<Long> getTrackingMeasuresSegmentIDs(
			final long trackingMeasuresID) throws SQLException {
		return this.getAllIDFieldByID(trackingMeasuresID,
				OmegaMySqlCostants.SEGMENT_ID_FIELD,
				OmegaMySqlCostants.TRACKING_MEASURES_SEGMENT_TABLE,
				OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
	}

	public List<Long> getTrackingMeasuresIDs(final long analysisID)
			throws SQLException {
		return this.getAllIDFieldByID(analysisID,
				OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD,
				OmegaMySqlCostants.TRACKING_MEASURES_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
	}

	public List<Long> getTrackingMeasuresDiffusivityParentIDs(
			final long trackingMeasuresID) throws SQLException {
		return this.getAllIDFieldByID(trackingMeasuresID,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD,
				OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_PARENT_TABLE,
				OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
	}

	public List<Long> getTrackingMeasuresSNRIDs(final long trackingMeasuresID)
			throws SQLException {
		return this.getAllIDFieldByID(trackingMeasuresID,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD,
				OmegaMySqlCostants.TRACKING_MEASURES_SNR_TABLE,
				OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
	}

	public Map<Long, Map<Integer, Double>> loadDiffusivityErrorLogMap(
			final long trackingMeasuresID) throws SQLException {
		final ResultSet results1 = this
				.load(trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_ERRORS_LOG_TABLE,
						OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		final Map<Long, Map<Integer, Double>> valuesMap = new LinkedHashMap<Long, Map<Integer, Double>>();
		if (results1 == null)
			return null;
		do {
			final long segmentID = results1
					.getLong(OmegaMySqlCostants.SEGMENT_ID_FIELD);
			final double smss = results1
					.getDouble(OmegaMySqlCostants.SMSS_FIELD);
			final double d = results1.getDouble(OmegaMySqlCostants.D_FIELD);
			Map<Integer, Double> values;
			if (valuesMap.containsKey(segmentID)) {
				values = valuesMap.get(segmentID);
			} else {
				values = new LinkedHashMap<Integer, Double>();
			}
			values.put(1, smss);
			values.put(0, d);
		} while (results1.next());
		// if (results1.next()) {
		//
		// }
		results1.getStatement().close();
		results1.close();
		return valuesMap;
	}

	public Map<Long, Map<Integer, Double>> loadDiffusivitySmssLogMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadSingleIndexTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_SMSS_LOG_TABLE);
	}

	public Map<Long, Map<Integer, Double>> loadDiffusivityGammaLogMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadSingleIndexTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_GAMMA_LOG_TABLE);
	}

	public Map<Long, Map<Integer, Map<Integer, Double>>> loadDiffusivityLogGammaDMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadDoubleIndexTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_GAMMA_D_LOG_TABLE);
	}

	public Map<Long, Map<Integer, Map<Integer, Double>>> loadDiffusivityGammaDMap(
			final long trackingMeasuresID) throws SQLException {
		return this.loadDoubleIndexTrackingMeasuresDoubleValuesMap(
				trackingMeasuresID,
				OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_GAMMA_D_TABLE);
	}

	public Map<Long, Map<Integer, Map<Integer, Double>>> loadDiffusivityLogDeltaTMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadDoubleIndexTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_LOG_DELTA_T_TABLE);
	}

	public Map<Long, Map<Integer, Map<Integer, Double>>> loadDiffusivityDeltaTMap(
			final long trackingMeasuresID) throws SQLException {
		return this.loadDoubleIndexTrackingMeasuresDoubleValuesMap(
				trackingMeasuresID,
				OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_DELTA_T_TABLE);
	}

	public Map<Long, Map<Integer, Map<Integer, Double>>> loadDiffusivityLogMuMap(
			final long trackingMeasuresID) throws SQLException {
		return this.loadDoubleIndexTrackingMeasuresDoubleValuesMap(
				trackingMeasuresID,
				OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_LOG_MU_TABLE);
	}

	public Map<Long, Map<Integer, Map<Integer, Double>>> loadDiffusivityMuMap(
			final long trackingMeasuresID) throws SQLException {
		return this.loadDoubleIndexTrackingMeasuresDoubleValuesMap(
				trackingMeasuresID,
				OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_MU_TABLE);
	}

	public Map<Long, Map<Integer, Double>> loadDiffusivityNyMap(
			final long trackingMeasuresID) throws SQLException {
		return this.loadSingleIndexTrackingMeasuresDoubleValuesMap(
				trackingMeasuresID,
				OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_NY_TABLE);
	}

	public Map<Long, Map<Integer, Double[]>> loadMobilityAnglesAndDirectionChangesMap(
			final long trackingMeasuresID) throws SQLException {
		final ResultSet results1 = this
				.load(trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_ANGLE_DIRECTION_CHANGE_TABLE,
						OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		final Map<Long, Map<Integer, Double[]>> valuesMap = new LinkedHashMap<Long, Map<Integer, Double[]>>();
		if (results1 == null)
			return valuesMap;
		do {
			final long segmentID = results1
					.getLong(OmegaMySqlCostants.SEGMENT_ID_FIELD);
			final int valIndex = results1
					.getInt(OmegaMySqlCostants.INDEX_FIELD);
			Double angle = results1.getDouble(OmegaMySqlCostants.ANGLE_FIELD);
			if (results1.wasNull()) {
				angle = null;
			}
			Double change = results1
					.getDouble(OmegaMySqlCostants.DIRECTIONAL_CHANGE_FIELD);
			if (results1.wasNull()) {
				change = null;
			}
			Map<Integer, Double[]> values;
			if (valuesMap.containsKey(segmentID)) {
				values = valuesMap.get(segmentID);
			} else {
				values = new LinkedHashMap<Integer, Double[]>();
			}
			final Double[] vals = new Double[2];
			vals[0] = angle;
			vals[1] = change;
			values.put(valIndex, vals);
			valuesMap.put(segmentID, values);
		} while (results1.next());
		// if (results1.next()) {
		//
		// }
		results1.getStatement().close();
		results1.close();
		return valuesMap;
	}

	public Map<Long, Map<Integer, Double>> loadMobilityConfinementRatioMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadSingleIndexTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_CONFINMENT_RATIO_TABLE);
	}

	public Map<Long, Map<Integer, Double>> loadMobilityTimeTraveledMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadSingleIndexTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_TOTAL_TIME_TRAVELED_TABLE);
	}

	public Map<Long, Double> loadMobilityMaxDisplacementsFromOriginMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_MAX_DISPLACEMENT_TABLE);
	}

	public Map<Long, Map<Integer, Double>> loadMobilityDisplacementsFromOriginMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadSingleIndexTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_DISPLACEMENT_TABLE);
	}

	public Map<Long, Map<Integer, Double>> loadMobilityDistancesFromOriginMap(
			final long trackingMeasuresID) throws SQLException {
		return this.loadSingleIndexTrackingMeasuresDoubleValuesMap(
				trackingMeasuresID,
				OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_DISTANCE_TABLE);
	}
	
	public Map<Long, Map<Integer, Double>> loadMobilityDistancesMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadSingleIndexTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_DISTANCE_P2P_TABLE);
	}

	public Map<Long, Double> loadVelocityForwardProgressionMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_VELOCITY_FORWARD_PROGRESSION_LINEARITY_TABLE);
	}

	public Map<Long, Double> loadVelocityAverageVelocityMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_VELOCITY_AVERAGE_STRAIGHT_LINE_VELOCITY_TABLE);
	}

	public Map<Long, Double> loadVelocityAverageSpeedMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_VELOCITY_AVERAGE_CURVILINEAR_SPEED_TABLE);
	}

	public Map<Long, Map<Integer, Double>> loadVelocityLocalVelocityFromOriginMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadSingleIndexTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_VELOCITY_LOCAL_VELOCITY_TABLE);
	}

	public Map<Long, Map<Integer, Double>> loadVelocityLocalSpeedFromOriginMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadSingleIndexTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_VELOCITY_LOCAL_SPEED_TABLE);
	}

	public Map<Long, Map<Integer, Double>> loadVelocityLocalSpeedMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadSingleIndexTrackingMeasuresDoubleValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_VELOCITY_LOCAL_SPEED_P2P_TABLE);
	}

	public Map<Long, Double[]> loadIntensitySNRsMap(
			final long trackingMeasuresID) throws SQLException {
		return this.loadTrackingMeasuresValuesMap(trackingMeasuresID,
				OmegaMySqlCostants.TRACKING_MEASURES_INTENSITY_SNR_TABLE);
	}

	public Map<Long, Double[]> loadIntensityAreasMap(
			final long trackingMeasuresID) throws SQLException {
		return this.loadTrackingMeasuresValuesMap(trackingMeasuresID,
				OmegaMySqlCostants.TRACKING_MEASURES_INTENSITY_AREA_TABLE);
	}

	public Map<Long, Double[]> loadIntensityBackgroundMap(
			final long trackingMeasuresID) throws SQLException {
		return this
				.loadTrackingMeasuresValuesMap(
						trackingMeasuresID,
						OmegaMySqlCostants.TRACKING_MEASURES_INTENSITY_BACKGROUND_TABLE);
	}

	public Map<Long, Double[]> loadIntensityNoisesMap(
			final long trackingMeasuresID) throws SQLException {
		return this.loadTrackingMeasuresValuesMap(trackingMeasuresID,
				OmegaMySqlCostants.TRACKING_MEASURES_INTENSITY_NOISE_TABLE);
	}

	public Map<Long, Double[]> loadIntensityMeanSignalsMap(
			final long trackingMeasuresID) throws SQLException {
		return this.loadTrackingMeasuresValuesMap(trackingMeasuresID,
				OmegaMySqlCostants.TRACKING_MEASURES_INTENSITY_MEAN_TABLE);
	}

	public Map<Long, Double[]> loadIntensityCentroidSignalsMap(
			final long trackingMeasuresID) throws SQLException {
		return this.loadTrackingMeasuresValuesMap(trackingMeasuresID,
				OmegaMySqlCostants.TRACKING_MEASURES_INTENSITY_CENTROID_TABLE);
	}

	public Map<Long, Double[]> loadIntensityPeakSignalsMap(
			final long trackingMeasuresID) throws SQLException {
		return this.loadTrackingMeasuresValuesMap(trackingMeasuresID,
				OmegaMySqlCostants.TRACKING_MEASURES_INTENSITY_PEAK_TABLE);
	}
	
	private Map<Long, Double[]> loadTrackingMeasuresValuesMap(
			final long trackingMeasuresID, final String table)
					throws SQLException {
		final ResultSet results1 = this.load(trackingMeasuresID, table,
				OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		final Map<Long, Double[]> valuesMap = new LinkedHashMap<Long, Double[]>();
		if (results1 == null)
			return valuesMap;
		do {
			final Double[] values = new Double[3];
			final long segmentID = results1
					.getLong(OmegaMySqlCostants.SEGMENT_ID_FIELD);
			final Double min = results1
					.getDouble(OmegaMySqlCostants.MIN_VALUE_FIELD);
			if (results1.wasNull()) {
				values[0] = null;
			} else {
				values[0] = min;
			}
			final Double avg = results1
					.getDouble(OmegaMySqlCostants.AVG_VALUE_FIELD);
			if (results1.wasNull()) {
				values[1] = null;
			} else {
				values[1] = avg;
			}
			final Double max = results1
					.getDouble(OmegaMySqlCostants.MAX_VALUE_FIELD);
			if (results1.wasNull()) {
				values[2] = null;
			} else {
				values[2] = max;
			}
			valuesMap.put(segmentID, values);
		} while (results1.next());
		// if (results1.next()) {
		//
		// }
		results1.getStatement().close();
		results1.close();
		return valuesMap;
	}

	private Map<Long, Integer> loadTrackingMeasuresIntegerValuesMap(
			final long trackingMeasuresID, final String table)
					throws SQLException {
		final ResultSet results1 = this.load(trackingMeasuresID, table,
				OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		final Map<Long, Integer> valuesMap = new LinkedHashMap<Long, Integer>();
		if (results1 == null)
			return valuesMap;
		do {
			final long segmentID = results1
					.getLong(OmegaMySqlCostants.SEGMENT_ID_FIELD);
			Integer value = results1.getInt(OmegaMySqlCostants.VALUE_FIELD);
			if (results1.wasNull()) {
				value = null;
			}
			valuesMap.put(segmentID, value);
		} while (results1.next());
		// if (results1.next()) {
		//
		// }
		results1.getStatement().close();
		results1.close();
		return valuesMap;
	}

	private Map<Long, Double> loadTrackingMeasuresDoubleValuesMap(
			final long trackingMeasuresID, final String table)
					throws SQLException {
		final ResultSet results1 = this.load(trackingMeasuresID, table,
				OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		final Map<Long, Double> valuesMap = new LinkedHashMap<Long, Double>();
		do {
			final long segmentID = results1
					.getLong(OmegaMySqlCostants.SEGMENT_ID_FIELD);
			Double value = results1.getDouble(OmegaMySqlCostants.VALUE_FIELD);
			if (results1.wasNull()) {
				value = null;
			}
			valuesMap.put(segmentID, value);
		} while (results1.next());
		// if (results1.next()) {
		//
		// }
		results1.getStatement().close();
		results1.close();
		return valuesMap;
	}

	private Map<Long, Map<Integer, Map<Integer, Double>>> loadDoubleIndexTrackingMeasuresDoubleValuesMap(
			final long trackingMeasuresID, final String table)
					throws SQLException {
		final ResultSet results1 = this.load(trackingMeasuresID, table,
				OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		final Map<Long, Map<Integer, Map<Integer, Double>>> valuesMap = new LinkedHashMap<Long, Map<Integer, Map<Integer, Double>>>();
		do {
			final long segmentID = results1
					.getLong(OmegaMySqlCostants.SEGMENT_ID_FIELD);
			final int valIndex = results1
					.getInt(OmegaMySqlCostants.INDEX_FIELD);
			final int nyIndex = results1
					.getInt(OmegaMySqlCostants.NY_INDEX_FIELD);
			Double value = results1.getDouble(OmegaMySqlCostants.VALUE_FIELD);
			if (results1.wasNull()) {
				value = null;
			}
			Map<Integer, Map<Integer, Double>> indexedValues;
			if (valuesMap.containsKey(segmentID)) {
				indexedValues = valuesMap.get(segmentID);
			} else {
				indexedValues = new LinkedHashMap<Integer, Map<Integer, Double>>();
			}
			Map<Integer, Double> values;
			if (indexedValues.containsKey(nyIndex)) {
				values = indexedValues.get(nyIndex);
			} else {
				values = new LinkedHashMap<Integer, Double>();
			}
			values.put(valIndex, value);
			indexedValues.put(nyIndex, values);
			valuesMap.put(segmentID, indexedValues);
		} while (results1.next());
		// if (results1.next()) {
		//
		// }
		results1.getStatement().close();
		results1.close();
		return valuesMap;
	}

	private Map<Long, Map<Integer, Double>> loadSingleIndexTrackingMeasuresDoubleValuesMap(
			final long trackingMeasuresID, final String table)
					throws SQLException {
		final ResultSet results1 = this.load(trackingMeasuresID, table,
				OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		final Map<Long, Map<Integer, Double>> valuesMap = new LinkedHashMap<Long, Map<Integer, Double>>();
		if (results1 == null)
			return valuesMap;
		do {
			final long segmentID = results1
					.getLong(OmegaMySqlCostants.SEGMENT_ID_FIELD);
			final int valIndex = results1
					.getInt(OmegaMySqlCostants.INDEX_FIELD);
			Double value = results1.getDouble(OmegaMySqlCostants.VALUE_FIELD);
			if (results1.wasNull()) {
				value = null;
			}
			Map<Integer, Double> values;
			if (valuesMap.containsKey(segmentID)) {
				values = valuesMap.get(segmentID);
			} else {
				values = new LinkedHashMap<Integer, Double>();
			}
			values.put(valIndex, value);
			valuesMap.put(segmentID, values);
		} while (results1.next());
		// if (results1.next()) {
		//
		// }
		results1.getStatement().close();
		results1.close();
		return valuesMap;
	}

	// ****** Tracks elements ****** //

	public List<Long> getROIValuesIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.ROI_VALUES_TABLE,
				OmegaMySqlCostants.ROI_VALUES_ID_FIELD);
	}

	public List<Long> getROIValuesIDs(final long roiID, final long analysisID)
			throws SQLException {
		return this.getAllIDFieldByDoubleID(roiID,
				OmegaMySqlCostants.ROI_ID_FIELD, analysisID,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD,
				OmegaMySqlCostants.ROI_VALUES_TABLE,
				OmegaMySqlCostants.ROI_VALUES_ID_FIELD);
	}

	public Map<String, Object> loadROIValues(final List<Long> ids)
			throws SQLException {
		final Map<String, Object> values = new LinkedHashMap<String, Object>();
		for (final Long id : ids) {
			final ResultSet results1 = this.load(id,
					OmegaMySqlCostants.ROI_VALUES_TABLE,
					OmegaMySqlCostants.ROI_VALUES_ID_FIELD);
			if (results1 == null)
				return null;
			final String name = results1
					.getString(OmegaMySqlCostants.NAME_FIELD);
			final String valueS = results1
					.getString(OmegaMySqlCostants.VALUE_FIELD);
			final String clazz = results1
					.getString(OmegaMySqlCostants.TYPE_FIELD);
			results1.getStatement().close();
			results1.close();
			Object value;
			if (clazz.equals(Double.class.getName())) {
				value = Double.valueOf(valueS);
			} else if (clazz.equals(Integer.class.getName())) {
				value = Integer.valueOf(valueS);
			} else {
				value = valueS;
			}
			values.put(name, value);
		}
		return values;
	}

	public List<Long> getParticleIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.PARTICLE_TABLE,
				OmegaMySqlCostants.PARTICLE_ID_FIELD);
	}

	public List<Long> getParticleROIID(final long id) throws SQLException {
		return this.getAllIDFieldByID(id, OmegaMySqlCostants.ROI_ID_FIELD,
				OmegaMySqlCostants.PARTICLE_TABLE,
				OmegaMySqlCostants.PARTICLE_ID_FIELD);
	}

	public List<Long> getParticleIDs(final long analysisID) throws SQLException {
		return this.getAllIDFieldByID(analysisID,
				OmegaMySqlCostants.PARTICLE_ID_FIELD,
				OmegaMySqlCostants.PARTICLE_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
	}

	public OmegaParticle loadParticle(final long id, final int frameIndex,
			final Double physicalX, final Double physicalY) throws SQLException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.PARTICLE_TABLE,
				OmegaMySqlCostants.PARTICLE_ID_FIELD);
		if (results1 == null)
			return null;
		final int roiIntID = results1.getInt(OmegaMySqlCostants.ROI_ID_FIELD);
		final long roiID = OmegaMySqlUtilities.getID(roiIntID);
		final Double peak_intensity = results1
				.getDouble(OmegaMySqlCostants.PEAK_INTENSITY_FIELD);
		final Double centroid_intensity = results1
				.getDouble(OmegaMySqlCostants.CENTROID_INTENSITY_FIELD);
		results1.getDouble(OmegaMySqlCostants.M0_PROV_FIELD);
		results1.getDouble(OmegaMySqlCostants.M2_PROV_FIELD);
		results1.getStatement().close();
		results1.close();
		final ResultSet results2 = this.load(roiID,
				OmegaMySqlCostants.ROI_TABLE, OmegaMySqlCostants.ROI_ID_FIELD);
		if (results2 == null)
			return null;
		final Double posX = results2
				.getDouble(OmegaMySqlCostants.ROI_POS_X_FIELD);
		final Double posY = results2
				.getDouble(OmegaMySqlCostants.ROI_POS_Y_FIELD);
		results2.getStatement().close();
		results2.close();
		Double realX = posX, realY = posY;
		if ((physicalX != null) && (physicalX != -1)) {
			realX *= physicalX;
		}
		if ((physicalY != null) && (physicalY != -1)) {
			realY *= physicalY;
		}
		final OmegaParticle p = new OmegaParticle(frameIndex, posX, posY,
				realX, realY, peak_intensity, centroid_intensity);
		// if (m0 != null) {
		// p.setM0(m0);
		// }
		// if (m2 != null) {
		// p.setM2(m2);
		// }
		p.setElementID(roiID);
		return p;
	}

	public List<Long> getROIIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.ROI_TABLE,
				OmegaMySqlCostants.ROI_ID_FIELD);
	}

	// public List<Long> getROIIDs(final long frameID) throws SQLException {
	// return this.getAllIDsByParentID(frameID,
	// OmegaMySqlCostants.FRAME_ID_FIELD,
	// OmegaMySqlCostants.ROI_TABLE, OmegaMySqlCostants.ROI_ID_FIELD);
	// }

	public List<Long> getROIIDs(final long trajectoryID) throws SQLException {
		return this.getAllIDFieldByID(trajectoryID,
				OmegaMySqlCostants.TRAJECTORY_ID_FIELD,
				OmegaMySqlCostants.TRAJECTORY_ROI_TABLE,
				OmegaMySqlCostants.ROI_ID_FIELD);
	}

	public List<Long> getROIFrameID(final long id) throws SQLException {
		return this.getAllIDFieldByID(id, OmegaMySqlCostants.FRAME_ID_FIELD,
				OmegaMySqlCostants.ROI_TABLE, OmegaMySqlCostants.ROI_ID_FIELD);
	}

	public OmegaROI loadROI(final long id, final int frameIndex,
			final Double physicalX, final Double physicalY) throws SQLException {
		final ResultSet results1 = this.load(id, OmegaMySqlCostants.ROI_TABLE,
				OmegaMySqlCostants.ROI_ID_FIELD);
		if (results1 == null)
			return null;
		final Double posX = results1
				.getDouble(OmegaMySqlCostants.ROI_POS_X_FIELD);
		final Double posY = results1
				.getDouble(OmegaMySqlCostants.ROI_POS_Y_FIELD);
		results1.getStatement().close();
		results1.close();
		Double realX = posX, realY = posY;
		if ((physicalX != null) && (physicalX != -1)) {
			realX *= physicalX;
		}
		if ((physicalY != null) && (physicalY != -1)) {
			realY *= physicalY;
		}
		final OmegaROI roi = new OmegaROI(frameIndex, posX, posY, realX, realY);
		return roi;
	}

	public List<Long> getTrajectoriesIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.TRAJECTORY_TABLE,
				OmegaMySqlCostants.TRAJECTORY_ID_FIELD);
	}

	public List<Long> getTrajectoriesIDs(final long analysisID)
			throws SQLException {
		return this.getAllIDFieldByID(analysisID,
				OmegaMySqlCostants.TRAJECTORY_ID_FIELD,
				OmegaMySqlCostants.ANALYSIS_TRAJECTORY_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
	}

	// FIXME load without particles, particles needs to be added separatly!
	public OmegaTrajectory loadTrajectory(final long id) throws SQLException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.TRAJECTORY_TABLE,
				OmegaMySqlCostants.TRAJECTORY_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final int length = results1
				.getInt(OmegaMySqlCostants.NUMBER_POINTS_FIELD);
		final int color_r = results1.getInt(OmegaMySqlCostants.COLOR_RED_FIELD);
		final int color_g = results1
				.getInt(OmegaMySqlCostants.COLOR_GREEN_FIELD);
		final int color_b = results1
				.getInt(OmegaMySqlCostants.COLOR_BLUE_FIELD);
		results1.getStatement().close();
		results1.close();
		final OmegaTrajectory track = new OmegaTrajectory(length, name);
		// track.setName(name);
		track.setColor(new Color(color_r, color_g, color_b));
		track.setElementID(id);
		return track;
	}

	public List<Long> getSegmentationTypeIDs(final long segmentationTypesID)
			throws SQLException {
		return this.getAllIDFieldByID(segmentationTypesID,
				OmegaMySqlCostants.SEGMENTATION_TYPE_ID_FIELD,
				OmegaMySqlCostants.SEGMENTATION_TYPES_MAP,
				OmegaMySqlCostants.SEGMENTATION_TYPES_ID_FIELD);
	}

	public OmegaSegmentationType loadSegmentationType(final long id)
			throws SQLException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.SEGMENTATION_TYPE_TABLE,
				OmegaMySqlCostants.SEGMENTATION_TYPE_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final String desc = results1
				.getString(OmegaMySqlCostants.DESCRIPTION_FIELD);
		final int value = results1.getInt(OmegaMySqlCostants.VALUE_FIELD);
		final int red = results1.getInt(OmegaMySqlCostants.COLOR_RED_FIELD);
		final int blue = results1.getInt(OmegaMySqlCostants.COLOR_BLUE_FIELD);
		final int green = results1.getInt(OmegaMySqlCostants.COLOR_GREEN_FIELD);
		final Color color = new Color(red, green, blue);
		results1.getStatement().close();
		results1.close();
		final OmegaSegmentationType segmentationType = new OmegaSegmentationType(
				name, value, color, desc);
		segmentationType.setElementID(id);
		return segmentationType;
	}

	public List<Long> getSegmentationTypesID(final long analysisID)
			throws SQLException {
		return this.getAllIDFieldByID(analysisID,
				OmegaMySqlCostants.SEGMENTATION_TYPES_ID_FIELD,
				OmegaMySqlCostants.ANALYSIS_SEGMENTATION_TYPES_MAP,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
	}

	public OmegaSegmentationTypes loadSegmentationTypes(final long id,
			final List<OmegaSegmentationType> segmTypes) throws SQLException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.SEGMENTATION_TYPES_TABLE,
				OmegaMySqlCostants.SEGMENTATION_TYPES_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		results1.getStatement().close();
		results1.close();
		final OmegaSegmentationTypes segmentationTypes = new OmegaSegmentationTypes(
				name, segmTypes);
		segmentationTypes.setElementID(id);
		return segmentationTypes;
	}

	public List<Long> getSegmentIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.SEGMENT_TABLE,
				OmegaMySqlCostants.SEGMENT_ID_FIELD);
	}

	public List<Long> getSegmentIDs(final long analysisID) throws SQLException {
		return this.getAllIDFieldByID(analysisID,
				OmegaMySqlCostants.SEGMENT_ID_FIELD,
				OmegaMySqlCostants.SEGMENT_TABLE,
				OmegaMySqlCostants.ANALYSIS_ID_FIELD);
	}

	public List<Long> getTrajectoriesSegmentID(final long segmentID)
			throws SQLException {
		return this.getAllIDFieldByID(segmentID,
				OmegaMySqlCostants.TRAJECTORY_ID_FIELD,
				OmegaMySqlCostants.SEGMENT_TABLE,
				OmegaMySqlCostants.SEGMENT_ID_FIELD);
	}

	public OmegaSegment loadSegment(final long id,
			final Map<Long, OmegaROI> rois) throws SQLException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.SEGMENT_TABLE,
				OmegaMySqlCostants.SEGMENT_ID_FIELD);
		if (results1 == null)
			return null;
		final int segmType = results1
				.getInt(OmegaMySqlCostants.SEGMENT_TYPE_FIELD);
		final long startingROI_ID = results1
				.getLong(OmegaMySqlCostants.SEGMENT_START_ROI_FIELD);
		final long endingROI_ID = results1
				.getLong(OmegaMySqlCostants.SEGMENT_END_ROI_FIELD);
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		results1.getStatement().close();
		results1.close();
		final OmegaROI start = rois.get(startingROI_ID);
		final OmegaROI end = rois.get(endingROI_ID);
		final OmegaSegment segment = new OmegaSegment(start, end, name);
		segment.setElementID(id);
		segment.setSegmentationType(segmType);

		return segment;
	}

	// ****** Algorithm info, specs and params ****** //

	public List<Long> getAlgorithmSpecificationIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.ALGO_SPEC_TABLE,
				OmegaMySqlCostants.ALGO_SPEC_ID_FIELD);
	}

	public List<Long> getAlgorithmSpecificationInformationID(final long id)
			throws SQLException {
		return this.getAllIDFieldByID(id,
				OmegaMySqlCostants.ALGO_INFO_ID_FIELD,
				OmegaMySqlCostants.ALGO_SPEC_TABLE,
				OmegaMySqlCostants.ALGO_SPEC_ID_FIELD);
	}

	public List<Long> getParameterIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.PARAM_TABLE,
				OmegaMySqlCostants.PARAM_ID_FIELD);
	}

	public List<Long> getParameterIDs(final long algoSpecID)
			throws SQLException {
		return this.getAllIDFieldByID(algoSpecID,
				OmegaMySqlCostants.PARAM_ID_FIELD,
				OmegaMySqlCostants.PARAM_TABLE,
				OmegaMySqlCostants.ALGO_SPEC_ID_FIELD);
	}

	public OmegaParameter loadParameter(final long id) throws SQLException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.PARAM_TABLE,
				OmegaMySqlCostants.PARAM_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final String valueS = results1
				.getString(OmegaMySqlCostants.VALUE_FIELD);
		final String clazz = results1.getString(OmegaMySqlCostants.TYPE_FIELD);
		results1.getStatement().close();
		results1.close();
		Object value;
		if (clazz.equals(Double.class.getName())) {
			value = Double.valueOf(valueS);
		} else if (clazz.equals(Integer.class.getName())) {
			value = Integer.valueOf(valueS);
		} else {
			value = valueS;
		}
		final OmegaParameter param = new OmegaParameter(name, value);
		param.setElementID(id);
		return param;
	}

	public List<Long> getAlgorithmInformationAuthorID(final long id)
			throws SQLException {
		return this.getAllIDFieldByID(id, OmegaMySqlCostants.PERSON_ID_FIELD,
				OmegaMySqlCostants.ALGO_INFO_TABLE,
				OmegaMySqlCostants.ALGO_INFO_ID_FIELD);
	}

	public List<Long> getAlgorithmInformationIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.ALGO_INFO_TABLE,
				OmegaMySqlCostants.ALGO_INFO_ID_FIELD);
	}

	public OmegaAlgorithmInformation loadAlgorithmInformation(final long id,
			final OmegaPerson author) throws SQLException, ParseException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.ALGO_INFO_TABLE,
				OmegaMySqlCostants.ALGO_INFO_ID_FIELD);
		if (results1 == null)
			return null;
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final double version = results1
				.getDouble(OmegaMySqlCostants.VERSION_FIELD);
		final String description = results1
				.getString(OmegaMySqlCostants.DESCRIPTION_FIELD);
		final String publication_date = results1
				.getString(OmegaMySqlCostants.PUBLICATION_DATE_FIELD);
		final SimpleDateFormat formatter = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		final String reference = results1
				.getString(OmegaMySqlCostants.REFERENCE_FIELD);
		final Date publicationDate = formatter.parse(publication_date);
		results1.getStatement().close();
		results1.close();
		final OmegaAlgorithmInformation algoInfo = new OmegaAlgorithmInformation(
				name, version, description, author, publicationDate, reference);
		algoInfo.setElementID(id);
		return algoInfo;
	}

	// ****** OMERO elements ****** //

	public Long getProjectID(final long omeroID) throws SQLException {
		return this.getIDByOmeroID(omeroID, OmegaMySqlCostants.PROJECT_TABLE,
				OmegaMySqlCostants.PROJECT_ID_FIELD);
	}

	public List<Long> getProjectIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.PROJECT_TABLE,
				OmegaMySqlCostants.PROJECT_ID_FIELD);
	}

	public OmegaProject loadProject(final long id) throws SQLException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.PROJECT_TABLE,
				OmegaMySqlCostants.PROJECT_ID_FIELD);
		if (results1 == null)
			return null;
		final int omeroID = results1.getInt(OmegaMySqlCostants.OMERO_ID_FIELD);
		final long omeID = OmegaMySqlUtilities.getID(omeroID);
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		results1.getStatement().close();
		results1.close();
		final OmegaProject project = new OmegaProject(name);
		project.setElementID(id);
		project.setOmeroId(omeID);
		return project;
	}

	public Long getDatasetID(final long omeroID) throws SQLException {
		return this.getIDByOmeroID(omeroID, OmegaMySqlCostants.DATASET_TABLE,
				OmegaMySqlCostants.DATASET_ID_FIELD);
	}

	public List<Long> getDatasetIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.DATASET_TABLE,
				OmegaMySqlCostants.DATASET_ID_FIELD);
	}

	public List<Long> getDatasetIDs(final long projectID) throws SQLException {
		return this.getAllIDFieldByID(projectID,
				OmegaMySqlCostants.PROJECT_ID_FIELD,
				OmegaMySqlCostants.DATASET_TABLE,
				OmegaMySqlCostants.DATASET_ID_FIELD);
	}

	public OmegaDataset loadDataset(final long id) throws SQLException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.DATASET_TABLE,
				OmegaMySqlCostants.DATASET_ID_FIELD);
		if (results1 == null)
			return null;
		final int omeroID = results1.getInt(OmegaMySqlCostants.OMERO_ID_FIELD);
		final long omeID = OmegaMySqlUtilities.getID(omeroID);
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		results1.getStatement().close();
		results1.close();
		final OmegaDataset dataset = new OmegaDataset(name);
		dataset.setElementID(id);
		dataset.setOmeroId(omeID);
		return dataset;
	}

	public Long getImageID(final long omeroID) throws SQLException {
		return this.getIDByOmeroID(omeroID, OmegaMySqlCostants.IMAGE_TABLE,
				OmegaMySqlCostants.IMAGE_ID_FIELD);
	}

	public List<Long> getImageIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.IMAGE_TABLE,
				OmegaMySqlCostants.IMAGE_ID_FIELD);
	}

	public List<Long> getImageIDs(final long datasetID) throws SQLException {
		return this.getAllIDFieldByID(datasetID,
				OmegaMySqlCostants.DATASET_ID_FIELD,
				OmegaMySqlCostants.IMAGE_DATASET_TABLE,
				OmegaMySqlCostants.IMAGE_ID_FIELD);
	}

	public List<Long> getImageExpID(final long id) throws SQLException {
		return this.getAllIDFieldByID(id,
				OmegaMySqlCostants.EXPERIMENTER_ID_FIELD,
				OmegaMySqlCostants.IMAGE_TABLE,
				OmegaMySqlCostants.IMAGE_ID_FIELD);
	}

	public OmegaImage loadImage(final long id, final OmegaExperimenter exp)
			throws SQLException, ParseException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.IMAGE_TABLE,
				OmegaMySqlCostants.IMAGE_ID_FIELD);
		if (results1 == null)
			return null;
		final DateFormat format = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		final int omeroID = results1.getInt(OmegaMySqlCostants.OMERO_ID_FIELD);
		final long omeID = OmegaMySqlUtilities.getID(omeroID);
		// final int experimenterID = results1.getInt("Experimenter_Seq_Id");
		// OmegaMySqlUtilities.getID(experimenterID);
		final String name = results1.getString(OmegaMySqlCostants.NAME_FIELD);
		final String aquDate = results1
				.getString(OmegaMySqlCostants.AQUISITION_DATE_FIELD);
		final Date aDate = format.parse(aquDate);
		final String impDate = results1
				.getString(OmegaMySqlCostants.IMPORT_DATE_FIELD);
		results1.getStatement().close();
		results1.close();
		final Date iDate = format.parse(impDate);
		final OmegaImage image = new OmegaImage(name, exp, aDate, iDate);
		image.setElementID(id);
		image.setOmeroId(omeID);
		return image;

	}

	public Long getImagePixelsID(final long omeroID) throws SQLException {
		return this.getIDByOmeroID(omeroID,
				OmegaMySqlCostants.IMAGEPIXELS_TABLE,
				OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD);
	}

	public List<Long> getImagePixelsIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.IMAGEPIXELS_TABLE,
				OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD);
	}

	public List<Long> getImagePixelsIDs(final long imageID) throws SQLException {
		return this.getAllIDFieldByID(imageID,
				OmegaMySqlCostants.IMAGE_ID_FIELD,
				OmegaMySqlCostants.IMAGEPIXELS_TABLE,
				OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD);
	}

	public OmegaImagePixels loadImagePixels(final long id) throws SQLException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.IMAGEPIXELS_TABLE,
				OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD);
		if (results1 == null)
			return null;
		final int omeroID = results1.getInt(OmegaMySqlCostants.OMERO_ID_FIELD);
		final long omeID = OmegaMySqlUtilities.getID(omeroID);
		final String pixelType = results1
				.getString(OmegaMySqlCostants.PIXELSTYPE_FIELD);
		final double pixelSizeX = results1
				.getDouble(OmegaMySqlCostants.PIXELSSIZE_X_FIELD);
		final double pixelSizeY = results1
				.getDouble(OmegaMySqlCostants.PIXELSSIZE_Y_FIELD);
		final double pixelSizeZ = results1
				.getDouble(OmegaMySqlCostants.PIXELSSIZE_Z_FIELD);
		final int sizeX = results1.getInt(OmegaMySqlCostants.SIZE_X_FIELD);
		final int sizeY = results1.getInt(OmegaMySqlCostants.SIZE_Y_FIELD);
		final int sizeZ = results1.getInt(OmegaMySqlCostants.SIZE_Z_FIELD);
		final int sizeC = results1.getInt(OmegaMySqlCostants.SIZE_C_FIELD);
		final int sizeT = results1.getInt(OmegaMySqlCostants.SIZE_T_FIELD);
		results1.getStatement().close();
		results1.close();
		final ResultSet results2 = this.load(id,
				OmegaMySqlCostants.PIXELS_CHANNEL_TABLE,
				OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD);
		final Map<Integer, String> channelNames = new LinkedHashMap<Integer, String>();
		if (results2 != null) {
			do {
				final int chanID = results2
						.getInt(OmegaMySqlCostants.CHANNEL_ID_FIELD);
				results2.getStatement().close();
				results2.close();
				final ResultSet results3 = this.load(chanID,
						OmegaMySqlCostants.CHANNEL_TABLE,
						OmegaMySqlCostants.CHANNEL_ID_FIELD);
				if (results3 != null) {
					final int index = results3
							.getInt(OmegaMySqlCostants.CHANNEL_INDEX);
					final String name = results3
							.getString(OmegaMySqlCostants.NAME_FIELD);
					results3.getStatement().close();
					results3.close();
					channelNames.put(index, name);
				}
			} while (results2.next());
		}
		final OmegaImagePixels pixels = new OmegaImagePixels(pixelType, sizeX,
				sizeY, sizeZ, sizeC, sizeT, pixelSizeX, pixelSizeY, pixelSizeZ,
				channelNames);
		pixels.setElementID(id);
		pixels.setOmeroId(omeID);
		return pixels;
	}

	public List<Long> getFrameIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.FRAME_TABLE,
				OmegaMySqlCostants.FRAME_ID_FIELD);
	}

	public List<Long> getFrameIDs(final long imagePixelsID) throws SQLException {
		return this.getAllIDFieldByID(imagePixelsID,
				OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD,
				OmegaMySqlCostants.FRAME_TABLE,
				OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD);
	}

	// TODO NEED TO MOVE THIS OUT FROM HERE
	// public OmegaPlane loadFrame(final long id) throws SQLException {
	// final ResultSet results1 = this.load(id,
	// OmegaMySqlCostants.FRAME_TABLE,
	// OmegaMySqlCostants.FRAME_ID_FIELD);
	// if (results1 == null)
	// return null;
	// // final int pixelID = results1.getInt("Pixel_Seq_Id");
	// // OmegaMySqlUtilities.getID(pixelID);
	// final int index = results1.getInt(OmegaMySqlCostants.FRAME_INDEX_FIELD);
	// final int channel = results1.getInt(OmegaMySqlCostants.CHANNEL_FIELD);
	// final int zPlane = results1.getInt(OmegaMySqlCostants.PLANE_FIELD);
	// results1.getStatement().close();
	// results1.close();
	// final OmegaPlane frame = new OmegaPlane(index, channel, zPlane);
	// frame.setElementID(id);
	// return frame;
	// }

	public OmegaPlane loadFrame(final long id) throws SQLException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.FRAME_TABLE,
				OmegaMySqlCostants.FRAME_ID_FIELD);
		if (results1 == null)
			return null;
		final int index = results1.getInt(OmegaMySqlCostants.FRAME_INDEX_FIELD);
		final int channel = results1.getInt(OmegaMySqlCostants.CHANNEL_FIELD);
		final int zPlane = results1.getInt(OmegaMySqlCostants.PLANE_FIELD);
		final OmegaPlane p = new OmegaPlane(index, channel, zPlane);
		p.setElementID(id);
		return p;
	}

	public long getFrameID(final long pixelImageID, final int sizeC,
			final int sizeZ) throws SQLException {
		final Map<String, String> additionalKeys = new LinkedHashMap<String, String>();
		additionalKeys.put(OmegaMySqlCostants.SIZE_C_FIELD,
				String.valueOf(sizeC));
		additionalKeys.put(OmegaMySqlCostants.SIZE_Z_FIELD,
				String.valueOf(sizeZ));
		final ResultSet results1 = this.load(pixelImageID,
				OmegaMySqlCostants.FRAME_TABLE,
				OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD, additionalKeys);
		if (results1 == null)
			return -1;
		final long id = results1.getInt(OmegaMySqlCostants.FRAME_ID_FIELD);
		// final int pixelID = results1.getInt("Pixel_Seq_Id");
		// OmegaMySqlUtilities.getID(pixelID);
		results1.getStatement().close();
		results1.close();
		return id;
	}

	// public Map<Integer, Map<Integer, List<OmegaPlane>>> loadFrames(
	// final OmegaImage image) throws SQLException {
	// final List<Long> frameIDs = this.getFrameIDs(image.getElementID());
	// final Map<Integer, Map<Integer, List<OmegaPlane>>> frameList = new
	// LinkedHashMap<>();
	// final OmegaImagePixels imagePixel = image.getDefaultPixels();
	// for (final long frameID : frameIDs) {
	// final OmegaPlane frame = this.loadFrame(frameID);
	// if (frame == null) {
	// continue;
	// }
	// frame.setParentPixels(imagePixel);
	// Map<Integer, List<OmegaPlane>> subMap = null;
	// List<OmegaPlane> frames = null;
	// final int channel = frame.getChannel();
	// final int zPlane = frame.getZPlane();
	// if (frameList.containsKey(channel)) {
	// subMap = frameList.get(channel);
	// if (subMap.containsKey(zPlane)) {
	// frames = subMap.get(zPlane);
	// } else {
	// frames = new ArrayList<>();
	// }
	// } else {
	// subMap = new LinkedHashMap<>();
	// frames = new ArrayList<>();
	// }
	// frames.add(frame);
	// subMap.put(zPlane, frames);
	// frameList.put(channel, subMap);
	// }
	// for (final Integer channel : frameList.keySet()) {
	// final Map<Integer, List<OmegaPlane>> subMap = frameList
	// .get(channel);
	// for (final Integer zPlane : subMap.keySet()) {
	// final List<OmegaPlane> frames = subMap.get(zPlane);
	// Collections.sort(frames, new Comparator<OmegaPlane>() {
	// @Override
	// public int compare(final OmegaPlane o1, final OmegaPlane o2) {
	// if (o1.getIndex() == o2.getIndex())
	// return 0;
	// else if (o1.getIndex() < o2.getIndex())
	// return -1;
	// return 1;
	// };
	// });
	// image.getDefaultPixels().addFrames(channel, zPlane, frames);
	// }
	// }
	// return frameList;
	// }

	// ****** People ****** //

	public Long getExperimenterID(final long omeroID) throws SQLException {
		return this.getIDByOmeroID(omeroID,
				OmegaMySqlCostants.EXPERIMENTER_TABLE,
				OmegaMySqlCostants.EXPERIMENTER_ID_FIELD);
	}

	public List<Long> getExperimenterIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.EXPERIMENTER_TABLE,
				OmegaMySqlCostants.EXPERIMENTER_ID_FIELD);
	}

	public OmegaExperimenter loadExperimenter(final long id)
			throws SQLException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.EXPERIMENTER_TABLE,
				OmegaMySqlCostants.EXPERIMENTER_ID_FIELD);
		if (results1 == null)
			return null;
		final int personID = results1
				.getInt(OmegaMySqlCostants.PERSON_ID_FIELD);
		final long pID = OmegaMySqlUtilities.getID(personID);
		final int omeroID = results1.getInt(OmegaMySqlCostants.OMERO_ID_FIELD);
		final long omeID = OmegaMySqlUtilities.getID(omeroID);
		results1.getStatement().close();
		results1.close();
		final ResultSet results2 = this.load(pID,
				OmegaMySqlCostants.PERSON_TABLE,
				OmegaMySqlCostants.PERSON_ID_FIELD);
		if (results2 == null)
			return null;
		final String firstName = results2
				.getString(OmegaMySqlCostants.FIRST_NAME_FIELD);
		final String lastName = results2
				.getString(OmegaMySqlCostants.LAST_NAME_FIELD);
		results2.getStatement().close();
		results2.close();
		final OmegaExperimenter exp = new OmegaExperimenter(firstName, lastName);
		exp.setElementID(pID);
		exp.setOmeroId(omeID);
		return exp;
	}

	public List<Long> getPersonIDs() throws SQLException {
		return this.getAllIDs(OmegaMySqlCostants.PERSON_TABLE,
				OmegaMySqlCostants.PERSON_ID_FIELD);
	}

	public OmegaPerson loadPerson(final long id) throws SQLException {
		final ResultSet results1 = this.load(id,
				OmegaMySqlCostants.PERSON_TABLE,
				OmegaMySqlCostants.PERSON_ID_FIELD);
		if (results1 == null)
			return null;
		final String firstName = results1
				.getString(OmegaMySqlCostants.FIRST_NAME_FIELD);
		final String lastName = results1
				.getString(OmegaMySqlCostants.LAST_NAME_FIELD);
		results1.getStatement().close();
		results1.close();
		final OmegaPerson person = new OmegaPerson(firstName, lastName);
		person.setElementID(id);
		return person;
	}
}
