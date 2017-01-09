package edu.umassmed.omega.core.mysql;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAlgorithmInformation;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaRunDefinition;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaSNRRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresDiffusivityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresIntensityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresMobilityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresVelocityRun;
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

public class OmegaMySqlWriter extends OmegaMySqlGateway {
	
	public OmegaMySqlWriter() {
		
	}
	
	private long insertAndGetId(final String query) throws SQLException {
		final Statement stat = this.connection.createStatement();
		final int error = stat.executeUpdate(query,
				Statement.RETURN_GENERATED_KEYS);
		final ResultSet results = stat.getGeneratedKeys();
		if ((error != 1) || !results.next()) {
			results.close();
			stat.close();
			// this.rollback();
			throw new SQLException("Get id insert failed: no generated keys!");
		}
		// this.commit();
		final int dbID = results.getInt(1);
		results.close();
		stat.close();
		final long id = OmegaMySqlUtilities.getID(dbID);
		return id;
	}
	
	private void insert(final String query) throws SQLException {
		final Statement stat = this.connection.createStatement();
		final int count = stat.executeUpdate(query);
		if (count <= 0) {
			stat.close();
			// this.rollback();
			throw new SQLException("Normal insert failed!");
		}
		// this.commit();
		stat.close();
	}
	
	// ****** SNR elements ****** //
	public long saveSNRRun(final OmegaSNRRun snrRun) throws SQLException {
		final long id = snrRun.getElementID();
		this.saveROISNR(OmegaMySqlCostants.SNR_LOCAL_CENTER_SIGNAL_TABLE,
				snrRun.getResultingLocalCenterSignals(), id);
		this.saveROISNR(OmegaMySqlCostants.SNR_LOCAL_MEAN_SIGNAL_TABLE,
				snrRun.getResultingLocalMeanSignals(), id);
		this.saveROISNR(OmegaMySqlCostants.SNR_LOCAL_PEAK_SIGNAL_TABLE,
				snrRun.getResultingLocalPeakSignals(), id);
		this.saveROISNR(OmegaMySqlCostants.SNR_LOCAL_SIGNAL_SIZE_TABLE,
				snrRun.getResultingLocalParticleArea(), id);
		this.saveROISNR(OmegaMySqlCostants.SNR_LOCAL_NOISE_TABLE,
				snrRun.getResultingLocalNoises(), id);
		this.saveROISNR(OmegaMySqlCostants.SNR_LOCAL_SNR_TABLE,
				snrRun.getResultingLocalSNRs(), id);
		this.saveROISNR(OmegaMySqlCostants.SNR_LOCAL_SNR_TABLE_ERROR_INDEX,
				snrRun.getResultingLocalErrorIndexSNRs(), id);
		this.saveFrameSNR(OmegaMySqlCostants.SNR_IMAGE_MIN_SNR_TABLE,
				snrRun.getResultingImageMinimumSNR(), id);
		this.saveFrameSNR(
		        OmegaMySqlCostants.SNR_IMAGE_MIN_ERROR_INDEX_SNR_TABLE,
				snrRun.getResultingImageMinimumErrorIndexSNR(), id);
		this.saveFrameSNR(OmegaMySqlCostants.SNR_IMAGE_AVG_SNR_TABLE,
				snrRun.getResultingImageAverageSNR(), id);
		this.saveFrameSNR(
		        OmegaMySqlCostants.SNR_IMAGE_AVG_ERROR_INDEX_SNR_TABLE,
				snrRun.getResultingImageAverageErrorIndexSNR(), id);
		this.saveFrameSNR(OmegaMySqlCostants.SNR_IMAGE_MAX_SNR_TABLE,
				snrRun.getResultingImageMaximumSNR(), id);
		this.saveFrameSNR(
		        OmegaMySqlCostants.SNR_IMAGE_MAX_ERROR_INDEX_SNR_TABLE,
				snrRun.getResultingImageMaximumErrorIndexSNR(), id);
		this.saveFrameSNR(OmegaMySqlCostants.SNR_IMAGE_NOISE_TABLE,
				snrRun.getResultingImageNoise(), id);
		this.saveFrameSNR(OmegaMySqlCostants.SNR_IMAGE_BG_TABLE,
				snrRun.getResultingImageBGR(), id);
		return id;
	}
	
	private void saveROISNR(final String tableID,
	        final Map<OmegaROI, ? extends Number> roiValues,
			final long analysisRunID) throws SQLException {
		for (final OmegaROI roi : roiValues.keySet()) {
			final StringBuffer query = new StringBuffer();
			query.append("INSERT INTO ");
			query.append(tableID);
			query.append(" (");
			query.append(OmegaMySqlCostants.ANALYSIS_ID_FIELD);
			query.append(",");
			query.append(OmegaMySqlCostants.ROI_ID_FIELD);
			query.append(",");
			query.append(OmegaMySqlCostants.SNR_VALUE_FIELD);
			query.append(") VALUES (");
			query.append(analysisRunID);
			query.append(",");
			query.append(roi.getElementID());
			query.append(",");
			query.append(roiValues.get(roi));
			query.append(")");
			this.insert(query.toString());
		}
	}
	
	private void saveFrameSNR(final String tableID,
			final Map<OmegaPlane, ? extends Number> frameValues,
			final long analysisRunID) throws SQLException {
		for (final OmegaPlane frame : frameValues.keySet()) {
			final StringBuffer query = new StringBuffer();
			query.append("INSERT INTO ");
			query.append(tableID);
			query.append(" (");
			query.append(OmegaMySqlCostants.ANALYSIS_ID_FIELD);
			query.append(",");
			query.append(OmegaMySqlCostants.FRAME_ID_FIELD);
			query.append(",");
			query.append(OmegaMySqlCostants.SNR_VALUE_FIELD);
			query.append(") VALUES (");
			query.append(analysisRunID);
			query.append(",");
			query.append(frame.getElementID());
			query.append(",");
			query.append(frameValues.get(frame));
			query.append(")");
			this.insert(query.toString());
		}
	}
	
	// ****** Tracking measures elements ****** //
	public long saveTrackingMeasuresRun(
	        final OmegaTrackingMeasuresRun trackingMeasuresRun,
	        final long analysisRunID) throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		query.append(",");
		// query.append(OmegaMySqlCostants.SEGMENT_ID_FIELD);
		// query.append(",");
		query.append(OmegaMySqlCostants.TYPE_FIELD);
		query.append(") VALUES (");
		query.append(analysisRunID);
		query.append(",");
		query.append(trackingMeasuresRun.getMeasureType().ordinal());
		query.append(")");
		// query.append(",'");
		// query.append(trackingMeasuresRun.getName());
		// query.append("')");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	public void saveTrackingMeasuresSegmentLink(final long trackingMeasuresID,
			final long segmentID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_SEGMENT_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SEGMENT_ID_FIELD);
		query.append(") VALUES (");
		query.append(trackingMeasuresID);
		query.append(",");
		query.append(segmentID);
		query.append(")");
		this.insert(query.toString());
	}
	
	public long saveDiffusivityTrackingMeasuresRun(
	        final OmegaTrackingMeasuresDiffusivityRun trackingMeasuresRun,
	        final long trackingMeasuresID) throws SQLException {
		final OmegaTrackingMeasuresDiffusivityRun diffRun = trackingMeasuresRun
				.getTrackingMeasuresDiffusivityRun();
		if (diffRun != null) {
			if (diffRun.getElementID() == -1) {
				// TODO error
			}
			this.saveTrackingMeasuresDiffusivityParent(trackingMeasuresID,
					diffRun.getElementID());
		}
		final OmegaSNRRun snrRun = trackingMeasuresRun.getSNRRun();
		if (snrRun != null) {
			if (snrRun.getElementID() == -1) {
				// TODO error
			}
			this.saveTrackingMeasuresDiffusivitySNR(trackingMeasuresID,
					snrRun.getElementID());
		}
		final Map<OmegaSegment, Double[]> gammaLogs = trackingMeasuresRun
		        .getGammaFromLogResults();
		// final Map<OmegaSegment, Double[]> gamma = trackingMeasuresRun
		// .getGammaResults();
		final Map<OmegaSegment, Double[][]> gammaDLogs = trackingMeasuresRun
		        .getGammaDFromLogResults();
		final Map<OmegaSegment, Double[][]> gammaD = trackingMeasuresRun
		        .getGammaDResults();
		final Map<OmegaSegment, Double[][]> deltaT = trackingMeasuresRun
		        .getDeltaTResults();
		final Map<OmegaSegment, Double[][]> logDeltaT = trackingMeasuresRun
		        .getLogDeltaTResults();
		final Map<OmegaSegment, Double[]> ny = trackingMeasuresRun
		        .getNyResults();
		final Map<OmegaSegment, Double[][]> mu = trackingMeasuresRun
		        .getMuResults();
		final Map<OmegaSegment, Double[][]> logMu = trackingMeasuresRun
				.getLogMuResults();
		// final Map<OmegaSegment, Double[]> smss = trackingMeasuresRun
		// .getSmssResults();
		final Map<OmegaSegment, Double[]> smssLog = trackingMeasuresRun
		        .getSmssFromLogResults();
		// final Map<OmegaSegment, Double[]> errors = trackingMeasuresRun
		// .getErrorsResults();
		final Map<OmegaSegment, Double[]> errorsLog = trackingMeasuresRun
		        .getErrosFromLogResults();
		
		for (final OmegaSegment segment : gammaLogs.keySet()) {
			final long segmentID = segment.getElementID();
			final Double[] segmentGammaLogs = gammaLogs.get(segment);
			// final Double[] segmentGamma = gamma.get(segment);
			final Double[][] segmentGammaDLogs = gammaDLogs.get(segment);
			final Double[][] segmentGammaD = gammaD.get(segment);
			final Double[][] segmentDeltaT = deltaT.get(segment);
			final Double[][] segmentLogDeltaT = logDeltaT.get(segment);
			final Double[] segmentNy = ny.get(segment);
			final Double[][] segmentMu = mu.get(segment);
			final Double[][] segmentLogMu = logMu.get(segment);
			// final Double[] segmentSmss = smss.get(segment);
			final Double[] segmentSmssLog = smssLog.get(segment);
			// if (errors != null) {
			// segmentErrors = errors.get(segment);
			// }
			Double[] segmentErrorsLog = null;
			if (errorsLog != null) {
				segmentErrorsLog = errorsLog.get(segment);
			}
			// for (int i = 0; i < segmentGamma.length; i++) {
			// this.saveTrackingMeasuresIndexValue(
			// trackingMeasuresID,
			// segmentID,
			// i,
			// segmentGamma[i],
			// OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_GAMMA_TABLE);
			// }
			for (int i = 0; i < segmentGammaD.length; i++) {
				for (int k = 0; k < segmentGammaD[i].length; k++) {
					// for (final Double element : segmentNy) {
					// if (element >= segmentGammaD[i].length) {
					// continue;
					// }
					// final int localNy = new BigDecimal(element).intValue();
					this.saveTrackingMeasuresNyIndexValue(
							trackingMeasuresID,
							segmentID,
							k,
							i,
							segmentGammaD[i][k],
							OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_GAMMA_D_TABLE);
				}
			}
			for (int i = 0; i < segmentGammaLogs.length; i++) {
				this.saveTrackingMeasuresIndexValue(
				        trackingMeasuresID,
				        segmentID,
				        i,
				        segmentGammaLogs[i],
				        OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_GAMMA_LOG_TABLE);
			}
			for (int i = 0; i < segmentGammaDLogs.length; i++) {
				for (int k = 0; k < segmentGammaDLogs[i].length; k++) {
					// for (final Double element : segmentNy) {
					// if (element >= segmentGammaDLogs[i].length) {
					// continue;
					// }
					// final int localNy = new BigDecimal(element).intValue();
					this.saveTrackingMeasuresNyIndexValue(
							trackingMeasuresID,
							segmentID,
							k,
							i,
							segmentGammaDLogs[i][k],
							OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_GAMMA_D_LOG_TABLE);
				}
			}
			for (int i = 0; i < segmentLogDeltaT.length; i++) {
				for (int k = 0; k < segmentLogDeltaT[i].length; k++) {
					// for (final Double element : segmentNy) {
					// if (element >= segmentLogDeltaT[i].length) {
					// continue;
					// }
					// final int localNy = new BigDecimal(element).intValue();
					this.saveTrackingMeasuresNyIndexValue(
							trackingMeasuresID,
							segmentID,
							k,
							i,
							segmentLogDeltaT[i][k],
							OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_LOG_DELTA_T_TABLE);
				}
			}
			for (int i = 0; i < segmentDeltaT.length; i++) {
				for (int k = 0; k < segmentDeltaT[i].length; k++) {
					// for (final Double element : segmentNy) {
					// if (element >= segmentDeltaT[i].length) {
					// continue;
					// }
					// final int localNy = new BigDecimal(element).intValue();
					this.saveTrackingMeasuresNyIndexValue(
							trackingMeasuresID,
							segmentID,
							k,
							i,
							segmentDeltaT[i][k],
							OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_DELTA_T_TABLE);
				}
			}
			for (int i = 0; i < segmentLogMu.length; i++) {
				for (int k = 0; k < segmentDeltaT[i].length; k++) {
					// for (final Double element : segmentNy) {
					// if (element >= segmentLogMu[i].length) {
					// continue;
					// }
					// final int localNy = new BigDecimal(element).intValue();
					this.saveTrackingMeasuresNyIndexValue(
							trackingMeasuresID,
							segmentID,
							k,
							i,
							segmentLogMu[i][k],
							OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_LOG_MU_TABLE);
				}
			}
			for (int i = 0; i < segmentMu.length; i++) {
				for (int k = 0; k < segmentDeltaT[i].length; k++) {
					// for (final Double element : segmentNy) {
					// if (element >= segmentMu[i].length) {
					// continue;
					// }
					// final int localNy = new BigDecimal(element).intValue();
					this.saveTrackingMeasuresNyIndexValue(
							trackingMeasuresID,
							segmentID,
							k,
							i,
							segmentMu[i][k],
							OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_MU_TABLE);
				}
			}
			for (int i = 0; i < segmentNy.length; i++) {
				this.saveTrackingMeasuresIndexValue(
				        trackingMeasuresID,
				        segmentID,
				        i,
				        segmentNy[i],
				        OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_NY_TABLE);
			}
			for (int i = 0; i < segmentSmssLog.length; i++) {
				this.saveTrackingMeasuresIndexValue(
				        trackingMeasuresID,
				        segmentID,
				        i,
				        segmentSmssLog[i],
				        OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_SMSS_LOG_TABLE);
			}
			// for (int i = 0; i < segmentSmss.length; i++) {
			// this.saveTrackingMeasuresIndexValue(
			// trackingMeasuresID,
			// segmentID,
			// i,
			// segmentSmss[i],
			// OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_SMSS_TABLE);
			// }
			// if (segmentErrors != null) {
			// this.saveTrackingMeasuresErrors(
			// trackingMeasuresID,
			// segmentID,
			// segmentErrors[0],
			// segmentErrors[1],
			// OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_ERRORS_TABLE);
			// }
			if (segmentErrorsLog != null) {
				this.saveTrackingMeasuresErrors(
				        trackingMeasuresID,
				        segmentID,
				        segmentErrorsLog[0],
				        segmentErrorsLog[1],
				        OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_ERRORS_LOG_TABLE);
			}
		}
		return trackingMeasuresID;
	}
	
	public long saveMobilityTrackingMeasuresRun(
	        final OmegaTrackingMeasuresMobilityRun trackingMeasuresRun,
	        final long trackingMeasuresID) throws SQLException {
		final Map<OmegaSegment, List<Double>> distances = trackingMeasuresRun
				.getDistancesResults();
		final Map<OmegaSegment, List<Double>> displacements = trackingMeasuresRun
				.getDisplacementsResults();
		final Map<OmegaSegment, Double> maxDisplacement = trackingMeasuresRun
				.getMaxDisplacementsResults();
		final Map<OmegaSegment, Integer> totalTimeTraveled = trackingMeasuresRun
				.getTotalTimeTraveledResults();
		final Map<OmegaSegment, List<Double>> confinementRatios = trackingMeasuresRun
		        .getConfinementRatioResults();
		final Map<OmegaSegment, List<Double[]>> angleAndDirectionalChange = trackingMeasuresRun
				.getAnglesAndDirectionalChangesResults();
		for (final OmegaSegment segment : distances.keySet()) {
			final long segmentID = segment.getElementID();
			final List<Double> segmentDistances = distances.get(segment);
			final List<Double> segmentDisplacements = displacements
					.get(segment);
			final Double segmentMaxDisplacement = maxDisplacement.get(segment);
			final Integer segmentTotalTimeTraveled = totalTimeTraveled
					.get(segment);
			final List<Double> segmentConfinementRatios = confinementRatios
			        .get(segment);
			final List<Double[]> segmentAngleAndDirectionalChange = angleAndDirectionalChange
					.get(segment);
			for (int i = 0; i < segmentDistances.size(); i++) {
				this.saveTrackingMeasuresIndexValue(
						trackingMeasuresID,
						segmentID,
						i,
						segmentDistances.get(i),
						OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_DISTANCE_TABLE);
			}
			for (int i = 0; i < segmentDisplacements.size(); i++) {
				this.saveTrackingMeasuresIndexValue(
						trackingMeasuresID,
						segmentID,
						i,
						segmentDisplacements.get(i),
						OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_DISPLACEMENT_TABLE);
			}
			this.saveTrackingMeasuresValue(
					trackingMeasuresID,
					segmentID,
					segmentMaxDisplacement,
					OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_MAX_DISPLACEMENT_TABLE);
			this.saveTrackingMeasuresValue(
					trackingMeasuresID,
					segmentID,
					segmentTotalTimeTraveled,
					OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_TOTAL_TIME_TRAVELED_TABLE);
			for (int i = 0; i < segmentConfinementRatios.size(); i++) {
				this.saveTrackingMeasuresIndexValue(
						trackingMeasuresID,
						segmentID,
						i,
						segmentConfinementRatios.get(i),
						OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_CONFINMENT_RATIO_TABLE);
			}
			for (int i = 0; i < segmentAngleAndDirectionalChange.size(); i++) {
				final Double[] localAngleAndDirectionalChange = segmentAngleAndDirectionalChange
						.get(i);
				this.saveTrackingMeasuresAngleValues(
						trackingMeasuresID,
						segmentID,
						i,
						localAngleAndDirectionalChange[0],
						localAngleAndDirectionalChange[1],
						OmegaMySqlCostants.TRACKING_MEASURES_MOBILITY_ANGLE_DIRECTION_CHANGE_TABLE);
			}
		}
		return trackingMeasuresID;
	}
	
	public long saveVelocityTrackingMeasuresRun(
	        final OmegaTrackingMeasuresVelocityRun trackingMeasuresRun,
	        final long trackingMeasuresID) throws SQLException {
		final Map<OmegaSegment, List<Double>> localVelocities = trackingMeasuresRun
		        .getLocalVelocityResults();
		final Map<OmegaSegment, Double> averageStraightLineVelocities = trackingMeasuresRun
		        .getAverageStraightLineVelocityMapResults();
		final Map<OmegaSegment, List<Double>> localSpeeds = trackingMeasuresRun
		        .getLocalSpeedResults();
		final Map<OmegaSegment, Double> averageCurilinearSpeeds = trackingMeasuresRun
		        .getAverageCurvilinearSpeedMapResults();
		final Map<OmegaSegment, Double> forwardProgressionLinearities = trackingMeasuresRun
		        .getForwardProgressionLinearityMapResults();
		for (final OmegaSegment segment : localVelocities.keySet()) {
			final long segmentID = segment.getElementID();
			final List<Double> segmentVelocities = localVelocities.get(segment);
			final List<Double> segmentSpeeds = localSpeeds.get(segment);
			final Double segmentAverageStraightLineVelocity = averageStraightLineVelocities
			        .get(segment);
			final Double segmentAverageCurvilinearSpeed = averageCurilinearSpeeds
			        .get(segment);
			final Double segmentForwardProgressionLinearity = forwardProgressionLinearities
			        .get(segment);
			for (int i = 0; i < segmentVelocities.size(); i++) {
				this.saveTrackingMeasuresIndexValue(
						trackingMeasuresID,
						segmentID,
						i,
						segmentVelocities.get(i),
						OmegaMySqlCostants.TRACKING_MEASURES_VELOCITY_LOCAL_VELOCITY_TABLE);
			}
			for (int i = 0; i < segmentSpeeds.size(); i++) {
				this.saveTrackingMeasuresIndexValue(
						trackingMeasuresID,
						segmentID,
						i,
						segmentSpeeds.get(i),
						OmegaMySqlCostants.TRACKING_MEASURES_VELOCITY_LOCAL_SPEED_TABLE);
			}
			this.saveTrackingMeasuresValue(
					trackingMeasuresID,
					segmentID,
					segmentAverageStraightLineVelocity,
					OmegaMySqlCostants.TRACKING_MEASURES_VELOCITY_AVERAGE_STRAIGHT_LINE_VELOCITY_TABLE);
			this.saveTrackingMeasuresValue(
					trackingMeasuresID,
					segmentID,
					segmentAverageCurvilinearSpeed,
					OmegaMySqlCostants.TRACKING_MEASURES_VELOCITY_AVERAGE_CURVILINEAR_SPEED_TABLE);
			this.saveTrackingMeasuresValue(
					trackingMeasuresID,
					segmentID,
					segmentForwardProgressionLinearity,
					OmegaMySqlCostants.TRACKING_MEASURES_VELOCITY_FORWARD_PROGRESSION_LINEARITY_TABLE);
		}
		return trackingMeasuresID;
	}
	
	public long saveIntensityTrackingMeasuresRun(
	        final OmegaTrackingMeasuresIntensityRun trackingMeasuresRun,
	        final long trackingMeasuresID) throws SQLException {
		final Map<OmegaSegment, Double[]> peakSignals = trackingMeasuresRun
				.getPeakSignalsResults();
		final Map<OmegaSegment, Double[]> meanSignals = trackingMeasuresRun
				.getMeanSignalsResults();
		final Map<OmegaSegment, Double[]> centroidSignals = trackingMeasuresRun
				.getCentroidSignalsResults();
		for (final OmegaSegment segment : peakSignals.keySet()) {
			final long segmentID = segment.getElementID();
			final Double[] segmentPeakSignals = peakSignals.get(segment);
			final Double[] segmentMeanSignals = meanSignals.get(segment);
			final Double[] segmentCentroiSignal = centroidSignals.get(segment);
			this.saveTrackingMeasuresValues(
			        trackingMeasuresID,
					segmentID,
					segmentPeakSignals[0],
			        segmentPeakSignals[1],
			        segmentPeakSignals[2],
					OmegaMySqlCostants.TRACKING_MEASURES_INTENSITY_CENTROID_TABLE);
			this.saveTrackingMeasuresValues(
			        trackingMeasuresID,
					segmentID,
					segmentMeanSignals[0],
			        segmentMeanSignals[1],
			        segmentMeanSignals[2],
					OmegaMySqlCostants.TRACKING_MEASURES_INTENSITY_CENTROID_TABLE);
			this.saveTrackingMeasuresValues(
			        trackingMeasuresID,
			        segmentID,
			        segmentCentroiSignal[0],
			        segmentCentroiSignal[1],
			        segmentCentroiSignal[2],
			        OmegaMySqlCostants.TRACKING_MEASURES_INTENSITY_CENTROID_TABLE);
		}
		return trackingMeasuresID;
	}
	
	private void saveTrackingMeasuresDiffusivityParent(
			final long trackingMeasuresID, final long analysisRunID)
					throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_PARENT_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		query.append(") VALUES (");
		query.append(trackingMeasuresID);
		query.append(",");
		query.append(analysisRunID);
		query.append(")");
		// final long id = this.insertAndGetId(query.toString());
		// return id;
		this.insert(query.toString());
	}
	
	private void saveTrackingMeasuresDiffusivitySNR(
			final long trackingMeasuresID, final long analysisRunID)
					throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_DIFFUSIVITY_SNR_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		query.append(") VALUES (");
		query.append(trackingMeasuresID);
		query.append(",");
		query.append(analysisRunID);
		query.append(")");
		// final long id = this.insertAndGetId(query.toString());
		// return id;
		this.insert(query.toString());
	}
	
	private void saveTrackingMeasuresErrors(final long trackingMeasuresID,
	        final long segmentID, final Double d, final Double smss,
			final String table) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(table);
		query.append(" (");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SEGMENT_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.D_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SMSS_FIELD);
		query.append(") VALUES (");
		query.append(trackingMeasuresID);
		query.append(",");
		query.append(segmentID);
		query.append(",");
		Double errD = d;
		if ((errD != null) && !Double.isFinite(errD)) {
			errD = null;
		}
		query.append(errD);
		query.append(",");
		Double errSMSS = smss;
		if ((errSMSS != null) && !Double.isFinite(errSMSS)) {
			errSMSS = null;
		}
		query.append(errSMSS);
		query.append(")");
		// final long id = this.insertAndGetId(query.toString());
		// return id;
		this.insert(query.toString());
	}
	
	private void saveTrackingMeasuresValue(final long trackingMeasuresID,
	        final long segmentID, final Double value, final String table)
					throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(table);
		query.append(" (");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SEGMENT_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SNR_VALUE_FIELD);
		query.append(") VALUES (");
		query.append(trackingMeasuresID);
		query.append(",");
		query.append(segmentID);
		query.append(",");
		query.append(value);
		query.append(")");
		// final long id = this.insertAndGetId(query.toString());
		// return id;
		this.insert(query.toString());
	}
	
	private void saveTrackingMeasuresValue(final long trackingMeasuresID,
	        final long segmentID, final Integer value, final String table)
					throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(table);
		query.append(" (");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SEGMENT_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SNR_VALUE_FIELD);
		query.append(") VALUES (");
		query.append(trackingMeasuresID);
		query.append(",");
		query.append(segmentID);
		query.append(",");
		query.append(value);
		query.append(")");
		// final long id = this.insertAndGetId(query.toString());
		// return id;
		this.insert(query.toString());
	}
	
	private void saveTrackingMeasuresValues(final long trackingMeasuresID,
	        final long segmentID, final Double min, final Double avg,
			final Double max, final String table) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(table);
		query.append(" (");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SEGMENT_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.MIN_VALUE_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.AVG_VALUE_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.MAX_VALUE_FIELD);
		query.append(") VALUES (");
		query.append(trackingMeasuresID);
		query.append(",");
		query.append(segmentID);
		query.append(",");
		query.append(min);
		query.append(",");
		query.append(avg);
		query.append(",");
		query.append(max);
		query.append(")");
		// final long id = this.insertAndGetId(query.toString());
		// return id;
		this.insert(query.toString());
	}
	
	private void saveTrackingMeasuresIndexValue(final long trackingMeasuresID,
	        final long segmentID, final Integer index, final Double value,
	        final String table) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(table);
		query.append(" (");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SEGMENT_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.INDEX_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SNR_VALUE_FIELD);
		query.append(") VALUES (");
		query.append(trackingMeasuresID);
		query.append(",");
		query.append(segmentID);
		query.append(",");
		query.append(index);
		query.append(",");
		Double val = value;
		if ((val != null) && !Double.isFinite(val)) {
			val = null;
		}
		query.append(val);
		query.append(")");
		// final long id = this.insertAndGetId(query.toString());
		// return id;
		this.insert(query.toString());
	}
	
	private void saveTrackingMeasuresNyIndexValue(
			final long trackingMeasuresID, final long segmentID,
			final int index, final Integer nyIndex, final Double value,
	        final String table) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(table);
		query.append(" (");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SEGMENT_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.INDEX_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.NY_INDEX_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SNR_VALUE_FIELD);
		query.append(") VALUES (");
		query.append(trackingMeasuresID);
		query.append(",");
		query.append(segmentID);
		query.append(",");
		query.append(index);
		query.append(",");
		query.append(nyIndex);
		query.append(",");
		Double val = value;
		if ((val != null) && !Double.isFinite(val)) {
			val = null;
		}
		query.append(val);
		query.append(")");
		// final long id = this.insertAndGetId(query.toString());
		// return id;
		this.insert(query.toString());
	}
	
	private void saveTrackingMeasuresAngleValues(final long trackingMeasuresID,
	        final long segmentID, final Integer index, final Double angle,
			final Double directionChange, final String table)
					throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(table);
		query.append(" (");
		query.append(OmegaMySqlCostants.TRACKING_MEASURES_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SEGMENT_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.INDEX_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.ANGLE_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.DIRECTIONAL_CHANGE_FIELD);
		query.append(") VALUES (");
		query.append(trackingMeasuresID);
		query.append(",");
		query.append(segmentID);
		query.append(",");
		query.append(index);
		query.append(",");
		Double ang = angle;
		if ((ang != null) && !Double.isFinite(ang)) {
			ang = null;
		}
		query.append(ang);
		query.append(",");
		Double chg = directionChange;
		if ((chg != null) && !Double.isFinite(chg)) {
			chg = null;
		}
		query.append(chg);
		query.append(")");
		// final long id = this.insertAndGetId(query.toString());
		// return id;
		this.insert(query.toString());
	}
	
	// ****** Analysis elements ****** //
	public long saveAnalysisRun(final OmegaAnalysisRun analysisRun,
	        final long experimenterID, final long algoSpecID)
	        throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.ANALYSIS_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.EXPERIMENTER_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.ALGO_SPEC_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.NAME_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.DATE_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.TYPE_FIELD);
		query.append(") VALUES (");
		query.append(experimenterID);
		query.append(",");
		query.append(algoSpecID);
		query.append(",'");
		query.append(analysisRun.getName());
		query.append("','");
		final DateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		if (analysisRun.getTimeStamps() != null) {
			query.append(format.format(analysisRun.getTimeStamps()));
		} else {
			query.append(String.valueOf(null));
		}
		query.append("',");
		query.append(analysisRun.getType().ordinal());
		query.append(")");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	public void saveAnalysisRunElementLink(final long analysisRunID,
			final String parentElementField, final long parentElementID)
					throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.ANALYSIS_PARENT_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		query.append(",");
		query.append(parentElementField);
		query.append(") VALUES (");
		query.append(analysisRunID);
		query.append(",");
		query.append(parentElementID);
		query.append(")");
		this.insert(query.toString());
	}
	
	public void saveAnalysisSegmentationTypesLink(final long analysisID,
	        final long segmentationTypesID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.ANALYSIS_SEGMENTATION_TYPES_MAP);
		query.append(" (");
		query.append(OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SEGMENTATION_TYPES_ID_FIELD);
		query.append(") VALUES (");
		query.append(analysisID);
		query.append(",");
		query.append(segmentationTypesID);
		query.append(")");
		this.insert(query.toString());
	}
	
	public long saveSegmentationTypes(final OmegaSegmentationTypes segmTypes)
			throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.SEGMENTATION_TYPES_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.NAME_FIELD);
		query.append(") VALUES ('");
		query.append(segmTypes.getName());
		query.append("')");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	public long saveSegmentationType(final OmegaSegmentationType segmType)
			throws SQLException {
		final Color color = segmType.getColor();
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.SEGMENTATION_TYPE_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.NAME_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.VALUE_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.COLOR_RED_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.COLOR_BLUE_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.COLOR_GREEN_FIELD);
		query.append(") VALUES ('");
		query.append(segmType.getName());
		query.append("',");
		query.append(segmType.getValue());
		query.append(",");
		query.append(color.getRed());
		query.append(",");
		query.append(color.getBlue());
		query.append(",");
		query.append(color.getGreen());
		query.append(")");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	public void saveSegmentationTypesTypeLink(final long segmTypesID,
			final long segmTypeID) throws SQLException {
		final StringBuffer query2 = new StringBuffer();
		query2.append("INSERT INTO ");
		query2.append(OmegaMySqlCostants.SEGMENTATION_TYPES_MAP);
		query2.append(" (");
		query2.append(OmegaMySqlCostants.SEGMENTATION_TYPES_ID_FIELD);
		query2.append(",");
		query2.append(OmegaMySqlCostants.SEGMENTATION_TYPE_ID_FIELD);
		query2.append(") VALUES (");
		query2.append(segmTypesID);
		query2.append(",");
		query2.append(segmTypeID);
		query2.append(")");
		this.insert(query2.toString());
	}
	
	// ****** Tracks elements ****** //
	public void saveTrajectoryROILink(final long trajectoryID, final long roiID)
			throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.TRAJECTORY_ROI_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.TRAJECTORY_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.ROI_ID_FIELD);
		query.append(") VALUES (");
		query.append(trajectoryID);
		query.append(",");
		query.append(roiID);
		query.append(")");
		this.insert(query.toString());
	}
	
	public long saveROI(final OmegaROI roi, final long frameID)
			throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.ROI_TABLE);
		query.append("(");
		query.append(OmegaMySqlCostants.FRAME_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.ROI_POS_X_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.ROI_POS_Y_FIELD);
		query.append(") VALUES (");
		query.append(frameID);
		query.append(",");
		query.append(roi.getX());
		query.append(",");
		query.append(roi.getY());
		query.append(")");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	public Map<String, Long> saveROIValues(final Map<String, Object> roiValues,
			final long analysisID, final long roiID) throws SQLException {
		final Map<String, Long> ids = new LinkedHashMap<String, Long>();
		for (final String s : roiValues.keySet()) {
			final Object obj = roiValues.get(s);
			final StringBuffer query2 = new StringBuffer();
			query2.append("INSERT INTO ");
			query2.append(OmegaMySqlCostants.ROI_VALUES_TABLE);
			query2.append("(");
			query2.append(OmegaMySqlCostants.ANALYSIS_ID_FIELD);
			query2.append(",");
			query2.append(OmegaMySqlCostants.ROI_ID_FIELD);
			query2.append(",");
			query2.append(OmegaMySqlCostants.NAME_FIELD);
			query2.append(",");
			query2.append(OmegaMySqlCostants.VALUE_FIELD);
			query2.append(",");
			query2.append(OmegaMySqlCostants.TYPE_FIELD);
			query2.append(") VALUES (");
			query2.append(analysisID);
			query2.append(",");
			query2.append(roiID);
			query2.append(",'");
			query2.append(s);
			query2.append("','");
			query2.append(String.valueOf(obj));
			query2.append("','");
			query2.append(obj.getClass().getName());
			query2.append("')");
			final long id = this.insertAndGetId(query2.toString());
			ids.put(s, id);
		}
		return ids;
	}
	
	public long saveParticle(final OmegaParticle particle, final long roiID,
			final long analysisRunID) throws SQLException {
		final StringBuffer query2 = new StringBuffer();
		query2.append("INSERT INTO ");
		query2.append(OmegaMySqlCostants.PARTICLE_TABLE);
		query2.append(" (");
		query2.append(OmegaMySqlCostants.ROI_ID_FIELD);
		query2.append(",");
		query2.append(OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		query2.append(",");
		query2.append(OmegaMySqlCostants.PEAK_INTENSITY_FIELD);
		query2.append(",");
		query2.append(OmegaMySqlCostants.CENTROID_INTENSITY_FIELD);
		// query2.append(",");
		// query2.append(OmegaMySqlCostants.M0_PROV_FIELD);
		// query2.append(",");
		// query2.append(OmegaMySqlCostants.M2_PROV_FIELD);
		query2.append(") VALUES (");
		query2.append(roiID);
		query2.append(",");
		query2.append(analysisRunID);
		query2.append(",");
		query2.append(particle.getPeakIntensity());
		query2.append(",");
		query2.append(particle.getCentroidIntensity());
		// query2.append(",");
		// query2.append(particle.getM0());
		// query2.append(",");
		// query2.append(particle.getM2());
		query2.append(")");
		final long id = this.insertAndGetId(query2.toString());
		return id;
	}
	
	public void saveAnalysisRunTrajectoryLink(final long analysisRunID,
			final long trackID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.ANALYSIS_TRAJECTORY_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.TRAJECTORY_ID_FIELD);
		query.append(") VALUES (");
		query.append(analysisRunID);
		query.append(",");
		query.append(trackID);
		query.append(")");
		this.insert(query.toString());
	}
	
	public long saveTrajectory(final OmegaTrajectory track) throws SQLException {
		final StringBuffer query2 = new StringBuffer();
		query2.append("INSERT INTO ");
		query2.append(OmegaMySqlCostants.TRAJECTORY_TABLE);
		query2.append(" (");
		query2.append(OmegaMySqlCostants.NAME_FIELD);
		query2.append(",");
		query2.append(OmegaMySqlCostants.NUMBER_POINTS_FIELD);
		query2.append(",");
		query2.append(OmegaMySqlCostants.COLOR_RED_FIELD);
		query2.append(",");
		query2.append(OmegaMySqlCostants.COLOR_GREEN_FIELD);
		query2.append(",");
		query2.append(OmegaMySqlCostants.COLOR_BLUE_FIELD);
		query2.append(",");
		query2.append(OmegaMySqlCostants.ANNOTATION_FILED);
		query2.append(") VALUES ('");
		query2.append(track.getName());
		query2.append("',");
		query2.append(track.getLength());
		query2.append(",");
		query2.append(track.getColor().getRed());
		query2.append(",");
		query2.append(track.getColor().getGreen());
		query2.append(",");
		query2.append(track.getColor().getBlue());
		query2.append(",'");
		query2.append(track.getAnnotations());
		query2.append("')");
		final long id = this.insertAndGetId(query2.toString());
		return id;
	}
	
	public long saveSegment(final OmegaSegment segment,
			final long trajectoryID, final long analysisRunID,
	        final long startingROIID, final long endingROIID)
					throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.SEGMENT_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.ANALYSIS_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.TRAJECTORY_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SEGMENT_TYPE_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SEGMENT_START_ROI_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SEGMENT_END_ROI_FIELD);
		query.append(") VALUES (");
		query.append(analysisRunID);
		query.append(",");
		query.append(trajectoryID);
		query.append(",");
		query.append(segment.getSegmentationType());
		query.append(",");
		query.append(startingROIID);
		query.append(",");
		query.append(endingROIID);
		query.append(")");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	// ****** Algorithm info, specs and params ****** //
	
	public long saveAlgorithmSpecification(final OmegaRunDefinition algoSpec,
			final long algoInfoID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.ALGO_SPEC_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.ALGO_INFO_ID_FIELD);
		query.append(") VALUES (");
		query.append(algoInfoID);
		query.append(")");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	public long saveParameter(final OmegaParameter param, final long algoSpecID)
			throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.PARAM_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.ALGO_SPEC_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.NAME_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.VALUE_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.TYPE_FIELD);
		query.append(") VALUES (");
		query.append(algoSpecID);
		query.append(",'");
		query.append(param.getName());
		query.append("','");
		query.append(String.valueOf(param.getValue()));
		query.append("','");
		query.append(param.getClazz());
		query.append("')");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	public long saveAlgorithmInformation(
	        final OmegaAlgorithmInformation algoInfo, final long personID)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.ALGO_INFO_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.PERSON_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.NAME_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.VERSION_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.DESCRIPTION_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.PUBLICATION_DATE_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.REFERENCE_FIELD);
		query.append(") VALUES (");
		query.append(personID);
		query.append(",'");
		query.append(algoInfo.getName());
		query.append("',");
		query.append(algoInfo.getVersion());
		query.append(",'");
		query.append(algoInfo.getDescription());
		query.append("','");
		final DateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		if (algoInfo.getPublicationData() != null) {
			final String s = format.format(algoInfo.getPublicationData());
			query.append(s);
		} else {
			query.append(String.valueOf(null));
		}
		query.append("','");
		query.append(algoInfo.getReference());
		query.append("')");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	// ****** OMERO elements ****** //
	
	public long saveProject(final OmegaProject project) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.PROJECT_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.OMERO_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.NAME_FIELD);
		query.append(") VALUES (");
		query.append(project.getOmeroId());
		query.append(",'");
		query.append(project.getName());
		query.append("')");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	public long saveDataset(final OmegaDataset dataset, final long projectID)
			throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.DATASET_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.OMERO_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.PROJECT_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.NAME_FIELD);
		query.append(") VALUES (");
		query.append(dataset.getOmeroId());
		query.append(",");
		query.append(projectID);
		query.append(",'");
		query.append(dataset.getName());
		query.append("')");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	public void saveImageDatasetLink(final OmegaImage image,
			final OmegaDataset dataset) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.IMAGE_DATASET_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.IMAGE_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.IMAGE_OMERO_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.DATASET_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.DATASET_OMERO_ID_FIELD);
		query.append(") VALUES (");
		query.append(image.getElementID());
		query.append(",");
		query.append(image.getOmeroId());
		query.append(",");
		query.append(dataset.getElementID());
		query.append(",");
		query.append(dataset.getOmeroId());
		query.append(")");
		this.insert(query.toString());
	}
	
	public long saveImage(final OmegaImage image, // final long datasetID,
			final long experimenterID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.IMAGE_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.OMERO_ID_FIELD);
		query.append(",");
		// query.append(OmegaMySqlCostants.DATASET_ID_FIELD);
		// query.append(",");
		query.append(OmegaMySqlCostants.EXPERIMENTER_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.NAME_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.AQUISITION_DATE_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.IMPORT_DATE_FIELD);
		query.append(") VALUES (");
		query.append(image.getOmeroId());
		query.append(",");
		// query.append(datasetID);
		// query.append(",");
		query.append(experimenterID);
		query.append(",'");
		query.append(image.getName());
		query.append("','");
		final DateFormat format = new SimpleDateFormat(
				OmegaConstants.OMEGA_DATE_FORMAT);
		if (image.getAcquisitionDate() != null) {
			query.append(format.format(image.getAcquisitionDate()));
		} else {
			query.append(String.valueOf(null));
		}
		query.append("','");
		if (image.getImportedDate() != null) {
			query.append(format.format(image.getImportedDate()));
		} else {
			query.append(String.valueOf(null));
		}
		query.append("')");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	public long saveImagePixels(final OmegaImagePixels pixels,
			final long imageID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.IMAGEPIXELS_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.OMERO_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.IMAGE_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.PIXELSSIZE_X_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.PIXELSSIZE_Y_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.PIXELSSIZE_Z_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SIZE_X_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SIZE_Y_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SIZE_Z_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SIZE_T_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.SIZE_C_FIELD);
		query.append(") VALUES (");
		query.append(pixels.getOmeroId());
		query.append(",");
		query.append(imageID);
		query.append(",");
		query.append(pixels.getPhysicalSizeX());
		query.append(",");
		query.append(pixels.getPhysicalSizeY());
		query.append(",");
		query.append(pixels.getPhysicalSizeZ());
		query.append(",");
		query.append(pixels.getSizeX());
		query.append(",");
		query.append(pixels.getSizeY());
		query.append(",");
		query.append(pixels.getSizeZ());
		query.append(",");
		query.append(pixels.getSizeT());
		query.append(",");
		query.append(pixels.getSizeC());
		query.append(")");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	public long saveChannel(final Integer index, final String name)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.CHANNEL_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.CHANNEL_INDEX);
		query.append(",");
		query.append(OmegaMySqlCostants.NAME_FIELD);
		query.append(") VALUES (");
		query.append(index);
		query.append(",'");
		query.append(name);
		query.append("')");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	public void savePixelsChannelLink(final OmegaImagePixels pixels,
			final long channelId) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.PIXELS_CHANNEL_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.CHANNEL_ID_FIELD);
		query.append(") VALUES (");
		query.append(pixels.getElementID());
		query.append(",");
		query.append(channelId);
		query.append(")");
		this.insert(query.toString());
	}
	
	public long saveFrame(final OmegaPlane frame, final long pixelsID)
			throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.FRAME_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.IMAGEPIXELS_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.FRAME_INDEX_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.CHANNEL_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.PLANE_FIELD);
		query.append(") VALUES (");
		query.append(pixelsID);
		query.append(",");
		query.append(frame.getIndex());
		query.append(",");
		query.append(frame.getChannel());
		query.append(",");
		query.append(frame.getZPlane());
		query.append(")");
		final long id = this.insertAndGetId(query.toString());
		return id;
	}
	
	// ****** People ****** //
	
	public long saveExperimenter(final OmegaExperimenter experimenter,
			final long personID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.EXPERIMENTER_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.OMERO_ID_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.PERSON_ID_FIELD);
		query.append(") VALUES (");
		query.append(experimenter.getOmeroId());
		query.append(",");
		query.append(personID);
		query.append(")");
		final long experimenterID = this.insertAndGetId(query.toString());
		return experimenterID;
	}
	
	public long savePerson(final OmegaPerson person) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO ");
		query.append(OmegaMySqlCostants.PERSON_TABLE);
		query.append(" (");
		query.append(OmegaMySqlCostants.FIRST_NAME_FIELD);
		query.append(",");
		query.append(OmegaMySqlCostants.LAST_NAME_FIELD);
		query.append(") VALUES ('");
		query.append(person.getFirstName());
		query.append("','");
		query.append(person.getLastName());
		query.append("')");
		final long personID = this.insertAndGetId(query.toString());
		return personID;
	}
}
