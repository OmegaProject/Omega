/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * AlessANDro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team: 
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli, 
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban, 
 * Ivo Sbalzarini AND Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it AND/or modify
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

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAlgorithmInformation;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAlgorithmSpecification;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
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
import edu.umassmed.omega.dataNew.coreElements.OmegaPerson;
import edu.umassmed.omega.dataNew.coreElements.OmegaProject;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaDBServerInformation;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaLoginCredentials;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaParticle;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaROI;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationType;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaMySqlGateway {

	public static String USER = "omega";
	public static String PSW = "1234";
	public static String HOSTNAME = "146.189.76.56";
	public static String PORT = "3306";
	public static String DB_NAME = "omega";

	private Connection connection;
	private OmegaDBServerInformation serverInfo;
	private OmegaLoginCredentials loginCred;

	public OmegaMySqlGateway() {
		this.connection = null;
		this.serverInfo = null;
		this.loginCred = null;
	}

	public void setServerInformation(final OmegaDBServerInformation serverInfo) {
		this.serverInfo = serverInfo;
	}

	public void setLoginCredentials(final OmegaLoginCredentials loginCred) {
		this.loginCred = loginCred;
	}

	public boolean isConnected() {
		return this.connection != null;
	}

	public void connect() throws ClassNotFoundException, SQLException {
		if (this.connection != null)
			throw new SQLException("Connection already present");
		if (this.serverInfo == null)
			throw new SQLException("Server information not set");
		if (this.loginCred == null)
			throw new SQLException("Login credentials not set");
		Class.forName("com.mysql.jdbc.Driver");

		this.connection = DriverManager.getConnection("jdbc:mysql://"
		        + this.serverInfo.getHostName() + ":"
		        + this.serverInfo.getPort() + "/" + this.serverInfo.getDBName()
		        + "?" + "user=" + this.loginCred.getUserName() + "&password="
		        + this.loginCred.getPassword());
		this.connection.setAutoCommit(false);
	}

	public void commit() throws SQLException {
		this.connection.commit();
	}

	public void rollback() throws SQLException {
		this.connection.rollback();
	}

	public void disconnect() throws SQLException {
		this.connection.close();
		this.connection = null;
	}

	private OmegaElement getElement(final List<OmegaElement> elements,
	        final long id) {
		for (final OmegaElement element : elements)
			if (element.getElementID() == id)
				return element;
		return null;
	}

	private int insertAndGetId(final String query) throws SQLException {
		final Statement stat = this.connection.createStatement();
		final int error = stat.executeUpdate(query,
		        Statement.RETURN_GENERATED_KEYS);
		final ResultSet rs = stat.getGeneratedKeys();
		if ((error != 1) || !rs.next())
			// this.rollback();
			throw new SQLException("Any generated keys");
		// this.commit();
		return rs.getInt(1);
	}

	private int getDBIdFROMOmegaElementId(final String elementType,
	        final long omegaID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM ");
		query.append(elementType);
		query.append(" WHERE Omero_Id = ");
		query.append(omegaID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (!results.next())
			return -1;
		final int dbID = results.getInt(1);
		return dbID;
	}

	private void loadParticleDetectionAnalysisRun(final OmegaImage image,
	        final int imageID) throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM analysis_run_map WHERE OmegaElement_Seq_Id = ");
		query.append(imageID);
		query.append(" AND OmegaElement_Type = 'image'");
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		final List<Integer> analysisRunIDs = new ArrayList<Integer>();
		while (results.next()) {
			final int analysisRunID = results.getInt(1);
			analysisRunIDs.add(analysisRunID);
		}

		final List<Integer> alreadyLoaded = new ArrayList<Integer>();
		for (final int id : analysisRunIDs) {
			if (image.containsAnalysisRun(id)) {
				alreadyLoaded.add(id);
			}
		}
		analysisRunIDs.removeAll(alreadyLoaded);

		final List<OmegaElement> expLoaded = new ArrayList<OmegaElement>();
		final List<OmegaElement> personLoaded = new ArrayList<OmegaElement>();
		final List<OmegaElement> algoInfoLoaded = new ArrayList<OmegaElement>();
		for (final Integer analysisRunID : analysisRunIDs) {
			final StringBuffer query2 = new StringBuffer();
			query2.append("SELECT * FROM analysis_run WHERE AnalysisRun_Seq_Id = ");
			query2.append(analysisRunID);
			final ResultSet results2 = this.connection.prepareStatement(
			        query2.toString()).executeQuery();
			while (results2.next()) {
				final String name = results2.getString(2);
				final String publicationDate = results2.getString(3);
				final SimpleDateFormat formatter = new SimpleDateFormat(
				        OmegaConstants.OMEGA_DATE_FORMAT);
				final Date timeStamps = formatter.parse(publicationDate);
				final int experimenterID = results2.getInt(4);
				final int algoSpecID = results2.getInt(6);
				OmegaExperimenter owner = (OmegaExperimenter) this.getElement(
				        expLoaded, experimenterID);
				if (owner == null) {
					owner = this.loadExperimenter(experimenterID);
					expLoaded.add(owner);
					personLoaded.add(owner);
				}
				final OmegaAlgorithmSpecification algoSpec = this
				        .loadAlgorithmSpecification(algoSpecID, personLoaded,
				                algoInfoLoaded);
				final Map<OmegaFrame, List<OmegaROI>> resultingParticles = this
				        .loadParticles(analysisRunID, image.getDefaultPixels()
				                .getFrames());

				final OmegaAnalysisRun analysisRun = new OmegaParticleDetectionRun(
				        owner, algoSpec, timeStamps, name, resultingParticles);
				analysisRun.setElementID((long) analysisRunID);
				// this.loadParticleLinkingAnalysisRun(analysisRun);
				image.addAnalysisRun(analysisRun);
			}
		}

		for (final OmegaAnalysisRun analysisRun : image.getAnalysisRuns()) {
			if (analysisRun instanceof OmegaParticleDetectionRun) {
				final List<OmegaElement> trajsLoaded = new ArrayList<OmegaElement>();
				this.loadParticleLinkingAnalysisRun(analysisRun, expLoaded,
				        personLoaded, algoInfoLoaded, trajsLoaded);
			}
		}
	}

	private void loadParticleLinkingAnalysisRun(
	        final OmegaAnalysisRun parentAnalysisRun,
	        final List<OmegaElement> expLoaded,
	        final List<OmegaElement> personLoaded,
	        final List<OmegaElement> algoInfoLoaded,
	        final List<OmegaElement> trajsLoaded) throws SQLException,
	        ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM analysis_run WHERE ParentAnalysisRun_Seq_Id = ");
		query.append(parentAnalysisRun.getElementID());
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();

		final OmegaParticleDetectionRun particleDetRun = (OmegaParticleDetectionRun) parentAnalysisRun;
		final Map<OmegaFrame, List<OmegaROI>> particlesMap = particleDetRun
		        .getResultingParticles();
		while (results.next()) {
			final int analysisRunID = results.getInt(1);
			if (parentAnalysisRun.containsAnalysisRun(analysisRunID)) {
				continue;
			}
			final String name = results.getString(2);
			final String publicationDate = results.getString(3);
			final SimpleDateFormat formatter = new SimpleDateFormat(
			        OmegaConstants.OMEGA_DATE_FORMAT);
			final Date timeStamps = formatter.parse(publicationDate);
			final int experimenterID = results.getInt(4);
			final int algoSpecID = results.getInt(6);
			OmegaExperimenter owner = (OmegaExperimenter) this.getElement(
			        expLoaded, experimenterID);
			if (owner == null) {
				owner = this.loadExperimenter(experimenterID);
				expLoaded.add(owner);
				personLoaded.add(owner);
			}
			final OmegaAlgorithmSpecification algoSpec = this
			        .loadAlgorithmSpecification(algoSpecID, personLoaded,
			                algoInfoLoaded);
			final List<OmegaTrajectory> resultingTrajectory = this
			        .loadTrajectories(analysisRunID, particlesMap, trajsLoaded);

			final OmegaAnalysisRun analysisRun = new OmegaParticleLinkingRun(
			        owner, algoSpec, timeStamps, name, resultingTrajectory);
			analysisRun.setElementID((long) analysisRunID);
			// this.loadTrajectoriesManagerAnlysisRun(analysisRun);
			// this.loadLinkingAnalysisRun(analysisRun);
			parentAnalysisRun.addAnalysisRun(analysisRun);
		}

		for (final OmegaAnalysisRun analysisRun : parentAnalysisRun
		        .getAnalysisRuns()) {
			if (analysisRun instanceof OmegaParticleLinkingRun) {
				final List<OmegaElement> segmTypesLoaded = new ArrayList<OmegaElement>();
				this.loadTrajectoriesManagerAnalysisRun(analysisRun,
				        particlesMap, expLoaded, personLoaded, algoInfoLoaded,
				        trajsLoaded, segmTypesLoaded);
			}
		}
	}

	private void loadTrajectoriesManagerAnalysisRun(
	        final OmegaAnalysisRun parentAnalysisRun,
	        final Map<OmegaFrame, List<OmegaROI>> particlesMap,
	        final List<OmegaElement> expLoaded,
	        final List<OmegaElement> personLoaded,
	        final List<OmegaElement> algoInfoLoaded,
	        final List<OmegaElement> trajsLoaded,
	        final List<OmegaElement> segmTypesLoaded) throws SQLException,
	        ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM analysis_run WHERE ParentAnalysisRun_Seq_Id = ");
		query.append(parentAnalysisRun.getElementID());
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		while (results.next()) {
			final int analysisRunID = results.getInt(1);
			if (parentAnalysisRun.containsAnalysisRun(analysisRunID)) {
				continue;
			}
			final String name = results.getString(2);
			final String publicationDate = results.getString(3);
			final SimpleDateFormat formatter = new SimpleDateFormat(
			        OmegaConstants.OMEGA_DATE_FORMAT);
			final Date timeStamps = formatter.parse(publicationDate);
			final int experimenterID = results.getInt(4);
			final int algoSpecID = results.getInt(6);
			OmegaExperimenter owner = (OmegaExperimenter) this.getElement(
			        expLoaded, experimenterID);
			if (owner == null) {
				owner = this.loadExperimenter(experimenterID);
				expLoaded.add(owner);
				personLoaded.add(owner);
			}
			final OmegaAlgorithmSpecification algoSpec = this
			        .loadAlgorithmSpecification(algoSpecID, personLoaded,
			                algoInfoLoaded);
			final int segmTypesID = this.getSegmentationTypesID(analysisRunID);
			OmegaSegmentationTypes segmTypes = null;
			if (segmTypesID != -1) {
				segmTypes = (OmegaSegmentationTypes) this.getElement(
				        segmTypesLoaded, segmTypesID);
				if (segmTypes == null) {
					segmTypes = this.loadSegmentationTypes(segmTypesID);
					segmTypesLoaded.add(segmTypes);
				}
			}

			final List<OmegaTrajectory> resultingTrajectories = this
			        .loadTrajectories(analysisRunID, particlesMap, trajsLoaded);
			final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments = this
			        .loadSegments(analysisRunID, resultingTrajectories,
			                particlesMap);

			final OmegaAnalysisRun analysisRun = new OmegaTrajectoriesManagerRun(
			        owner, algoSpec, timeStamps, name, resultingTrajectories,
			        resultingSegments, segmTypes);
			analysisRun.setElementID((long) analysisRunID);
			// this.loadLinkingAnalysisRun(analysisRun);
			parentAnalysisRun.addAnalysisRun(analysisRun);
		}

		// for (final OmegaAnalysisRun analysisRun : parentAnalysisRun
		// .getAnalysisRuns()) {
		// if (analysisRun instanceof OmegaTrajectoriesManagerRun) {
		// this.loadTrajectoriesManagerAnlysisRun(analysisRun);
		// }
		// }
	}

	public long saveAnalysisRun(final OmegaImage image,
	        final OmegaAnalysisRun analysisRun) throws SQLException {
		final OmegaImagePixels pixels = image.getDefaultPixels();

		int imageID = this.getDBIdFROMOmegaElementId("image",
		        image.getElementID());
		if (imageID == -1) {
			imageID = this.saveImage(image);
		}
		int pixelsID = this.getDBIdFROMOmegaElementId("pixels",
		        pixels.getElementID());
		if (pixelsID == -1) {
			pixelsID = this.saveImagePixels(pixels, imageID);
		}
		for (final OmegaFrame frame : pixels.getFrames()) {
			final int frameID = this.saveFrames(frame, pixelsID);
			frame.setElementID((long) frameID);
		}
		long analysisID = -1;
		if (analysisRun instanceof OmegaParticleDetectionRun) {
			analysisID = this
			        .saveParticleDetectionAnalysisRun((OmegaParticleDetectionRun) analysisRun);
		}
		if (analysisID != -1) {
			this.saveElementAnalysisLinkIfNeeded(imageID, "image", analysisID);
		}
		return analysisID;
	}

	public long saveAnalysisRun(final int parentAnalysisRunID,
	        final OmegaAnalysisRun analysisRun) throws SQLException {
		long analysisID = -1;
		if (analysisRun instanceof OmegaTrajectoriesManagerRun) {
			analysisID = this.saveTrajectoriesManagerAnalysisRun(
			        (OmegaTrajectoriesManagerRun) analysisRun,
			        parentAnalysisRunID);
		} else if (analysisRun instanceof OmegaParticleLinkingRun) {
			analysisID = this.saveParticleLinkingAnalysisRun(
			        (OmegaParticleLinkingRun) analysisRun, parentAnalysisRunID);
		}
		return analysisID;
	}

	public void updateTrajectories(
	        final OmegaParticleLinkingRun particleLinkingRun)
	        throws SQLException {
		if (particleLinkingRun.getElementID() == -1)
			return;
		for (final OmegaTrajectory trajectory : particleLinkingRun
		        .getResultingTrajectories()) {
			if (trajectory.isNameChanged() || trajectory.isColorChanged()) {
				this.updateTrajectory(trajectory);
			}
		}
	}

	public void updateTrajectoriesManagerRun(
	        final OmegaTrajectoriesManagerRun tmRun) throws SQLException {
		if (tmRun.getElementID() == -1)
			return;
		final OmegaSegmentationTypes segmTypes = tmRun.getSegmentationTypes();
		if (segmTypes.isChanged() || segmTypes.isNameChanged()) {
			this.updateTMSegmentationTypes(tmRun);
		}
		if (tmRun.isTrajectoriesModified()) {
			// TODO
			// updateTMTrajectories(tmRun.getResultingTrajectories());
		}
		if (tmRun.isSegmentsModified()) {
			// TODO
			// updateTMSegments(tmRun.getResultingSegments());
		}
	}

	private void saveElementAnalysisLinkIfNeeded(final int elementID,
	        final String elementType, final long analysisID)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM analysis_run_map WHERE AnalysisRun_Seq_Id =");
		query.append(analysisID);
		query.append(" AND  OmegaElement_Seq_Id = ");
		query.append(elementID);
		query.append(" AND OmegaElement_Type = '");
		query.append(elementType);
		query.append("'");
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();

		if (results.next())
			return;

		final StringBuffer query1 = new StringBuffer();
		query1.append("INSERT INTO analysis_run_map (AnalysisRun_Seq_Id, OmegaElement_Seq_Id, OmegaElement_Type) VALUES (");
		query1.append(analysisID);
		query1.append(",");
		query1.append(elementID);
		query1.append(",'");
		query1.append(elementType);
		query1.append("')");
		this.connection.prepareStatement(query1.toString()).executeUpdate();
	}

	private int saveAnalysisRun(final OmegaAnalysisRun analysisRun,
	        final int experimenterID) throws SQLException {
		final int specID = this.saveAlgorithmSpecANDInfo(analysisRun);
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO analysis_run (AnalysisRun_Name, AnalysisRun_Date, Experimenter_Seq_Id, AlgorithmSpecification_Seq_Id) VALUES ('");
		query.append(analysisRun.getName());
		query.append("','");
		final DateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		query.append(format.format(analysisRun.getTimeStamps()));
		query.append("','");
		query.append(experimenterID);
		query.append("','");
		query.append(specID);
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private int saveAnalysisRun(final OmegaAnalysisRun analysisRun,
	        final int experimenterID, final int parentAnalysisRunID)
	        throws SQLException {
		final int specID = this.saveAlgorithmSpecANDInfo(analysisRun);
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO analysis_run (AnalysisRun_Name, AnalysisRun_Date, Experimenter_Seq_Id, ParentAnalysisRun_Seq_Id, AlgorithmSpecification_Seq_Id) VALUES ('");
		query.append(analysisRun.getName());
		query.append("','");
		final DateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		final String formattedDate = format.format(analysisRun.getTimeStamps());
		query.append(formattedDate);
		query.append("',");
		query.append(experimenterID);
		query.append(",");
		query.append(parentAnalysisRunID);
		query.append(",");
		query.append(specID);
		query.append(")");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private long saveParticleDetectionAnalysisRun(
	        final OmegaParticleDetectionRun particlesDetectionRun)
	        throws SQLException {
		long tmpAnalysisRunID = particlesDetectionRun.getElementID();
		if (tmpAnalysisRunID != -1)
			return tmpAnalysisRunID;

		final OmegaExperimenter experimenter = particlesDetectionRun
		        .getExperimenter();
		final int experimenterID = this.getOrSaveExperimenter(experimenter);
		tmpAnalysisRunID = this.saveAnalysisRun(particlesDetectionRun,
		        experimenterID);
		particlesDetectionRun.setElementID(tmpAnalysisRunID);

		final long analysisRunID = tmpAnalysisRunID;

		final Map<OmegaFrame, List<OmegaROI>> particles = particlesDetectionRun
		        .getResultingParticles();
		for (final OmegaFrame frame : particles.keySet()) {
			final List<OmegaROI> rois = particles.get(frame);
			for (final OmegaROI roi : rois) {
				final int frameID = Integer.valueOf(frame.getElementID()
				        .toString());
				final int particleID = this.saveParticle(roi, frameID,
				        analysisRunID);
				roi.setElementID((long) particleID);
			}
		}
		return analysisRunID;
	}

	private Map<OmegaFrame, List<OmegaROI>> loadParticles(
	        final int analysisRunID, final List<OmegaFrame> frames)
	        throws SQLException {
		final Map<OmegaFrame, List<OmegaROI>> particlesMap = new LinkedHashMap<OmegaFrame, List<OmegaROI>>();
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM particle WHERE AnalysisRun_Seq_Id = ");
		query.append(analysisRunID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		while (results.next()) {
			final int roiID = results.getInt(2);
			final double intensity = results.getDouble(5);
			final double probability = results.getDouble(6);

			final StringBuffer query2 = new StringBuffer();
			query2.append("SELECT * FROM roi WHERE ROI_Seq_Id = ");
			query2.append(roiID);
			final ResultSet results2 = this.connection.prepareStatement(
			        query2.toString()).executeQuery();
			if (!results2.next()) {
				// TODO Throw error
				continue;
			}
			final int frameID = results2.getInt(2);

			final StringBuffer query3 = new StringBuffer();
			query3.append("SELECT * FROM frame WHERE Frame_Seq_Id = ");
			query3.append(frameID);
			final ResultSet results3 = this.connection.prepareStatement(
			        query3.toString()).executeQuery();
			if (!results3.next()) {
				// TODO Throw error
				continue;
			}

			final int frameIndex = results3.getInt(4);

			final double x = results2.getDouble(3);
			final double y = results2.getDouble(4);

			List<OmegaROI> particles;
			final OmegaFrame frame = frames.get(frameIndex);
			if (particlesMap.containsKey(frame)) {
				particles = particlesMap.get(frame);
			} else {
				particles = new ArrayList<OmegaROI>();
			}

			final OmegaROI particle = new OmegaParticle(frameIndex, x, y,
			        intensity, probability);
			particle.setElementID((long) roiID);

			particles.add(particle);
			particlesMap.put(frame, particles);
		}

		return particlesMap;
	}

	private int saveParticle(final OmegaROI roi, final int frameID,
	        final long analysisRunID) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("INSERT INTO roi (Frame_Seq_Id, Position_X, Position_Y) VALUES (");
		query1.append(frameID);
		query1.append(",");
		query1.append(roi.getX());
		query1.append(",");
		query1.append(roi.getY());
		query1.append(")");
		final int roiID = this.insertAndGetId(query1.toString());

		final StringBuffer query2 = new StringBuffer();
		query2.append("INSERT INTO particle (ROI_Seq_Id, AnalysisRun_Seq_Id) VALUES (");
		query2.append(roiID);
		query2.append(",");
		query2.append(analysisRunID);
		query2.append(")");
		this.insertAndGetId(query2.toString());
		return roiID;
	}

	private void saveTrajectoryParticleLinkIfNeeded(final int roiID,
	        final int trajectoryID) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM trajectoriesParticlesMap WHERE Trajectory_Seq_Id = ");
		query1.append(trajectoryID);
		query1.append(" AND ROI_Seq_Id = ");
		query1.append(roiID);
		final ResultSet results = this.connection.prepareStatement(
		        query1.toString()).executeQuery();
		if (!results.next()) {
			final StringBuffer query2 = new StringBuffer();
			query2.append("INSERT INTO trajectoriesParticlesMap (Trajectory_Seq_Id, ROI_Seq_Id) VALUES (");
			query2.append(trajectoryID);
			query2.append(",");
			query2.append(roiID);
			query2.append(")");
			this.connection.prepareStatement(query2.toString()).executeUpdate();
		}
	}

	private long saveTrajectoriesManagerAnalysisRun(
	        final OmegaTrajectoriesManagerRun tmRun,
	        final int parentAnalysisRunID) throws SQLException {
		long tmpAnalysisRunID = tmRun.getElementID();
		if (tmpAnalysisRunID != -1)
			return tmpAnalysisRunID;

		final OmegaExperimenter experimenter = tmRun.getExperimenter();
		final int experimenterID = this.getOrSaveExperimenter(experimenter);
		tmpAnalysisRunID = this.saveAnalysisRun(tmRun, experimenterID,
		        parentAnalysisRunID);
		tmRun.setElementID(tmpAnalysisRunID);

		final long analysisRunID = tmpAnalysisRunID;

		final OmegaSegmentationTypes segmTypes = tmRun.getSegmentationTypes();
		final int segmTypesID = this.getOrSaveSegmentationTypes(segmTypes);
		this.saveAnalysisSegmentationTypesLinkIfNeeded(analysisRunID,
		        segmTypesID);

		final Long startingTime = System.currentTimeMillis();
		final List<Thread> threads = new ArrayList<Thread>();
		int counter = 0;
		final List<OmegaTrajectory> trajectories = tmRun
		        .getResultingTrajectories();
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = tmRun
		        .getResultingSegments();
		for (final OmegaTrajectory trajectory : trajectories) {
			final Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					long trajectoryID = trajectory.getElementID();
					try {
						if (trajectoryID == -1) {
							trajectoryID = OmegaMySqlGateway.this
							        .saveTrajectory(trajectory);
						}
						OmegaMySqlGateway.this
						        .saveAnalysisTrajectoryLinkIfNeeded(
						                (int) trajectoryID, analysisRunID);
						for (final OmegaROI roi : trajectory.getROIs()) {
							final long roiID = roi.getElementID();
							OmegaMySqlGateway.this
							        .saveTrajectoryParticleLinkIfNeeded(
							                (int) roiID, (int) trajectoryID);
						}
						final List<OmegaSegment> segments = segmentsMap
						        .get(trajectory);
						for (final OmegaSegment segment : segments) {
							long segmentID = segment.getElementID();
							if (segmentID != -1) {
								continue;
							}
							segmentID = OmegaMySqlGateway.this.saveSegment(
							        analysisRunID, trajectoryID, segment);
						}
					} catch (final SQLException ex) {
						OmegaLogFileManager.handleCoreException(ex);
					}
					trajectory.setElementID(trajectoryID);
				}
			});
			t.setName("SavingTrajectoryAndSegments" + counter);
			counter++;
			threads.add(t);
		}
		for (final Thread t : threads) {
			t.start();
		}
		for (final Thread t : threads) {
			if (t.isAlive()) {
				try {
					t.join();
				} catch (final InterruptedException ex) {
					OmegaLogFileManager.handleCoreException(ex);
				}
			}
		}
		final Long totalTime = System.currentTimeMillis() - startingTime;
		System.out.println("TotalTime: " + (totalTime / 1000));

		return analysisRunID;
	}

	private void saveAnalysisTrajectoryLinkIfNeeded(final int trajectoryID,
	        final long analysisRunID) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM analysisTrajectoriesMap WHERE AnalysisRun_Seq_Id = ");
		query1.append(analysisRunID);
		query1.append(" AND Trajectory_Seq_Id = ");
		query1.append(trajectoryID);
		final ResultSet results = this.connection.prepareStatement(
		        query1.toString()).executeQuery();
		if (!results.next()) {
			final StringBuffer query2 = new StringBuffer();
			query2.append("INSERT INTO analysisTrajectoriesMap (AnalysisRun_Seq_Id, Trajectory_Seq_Id) VALUES (");
			query2.append(analysisRunID);
			query2.append(",");
			query2.append(trajectoryID);
			query2.append(")");
			this.connection.prepareStatement(query2.toString()).executeUpdate();
		}
	}

	private long saveParticleLinkingAnalysisRun(
	        final OmegaParticleLinkingRun particleLinkingRun,
	        final int parentAnalysisRunID) throws SQLException {
		long tmpAnalysisRunID = particleLinkingRun.getElementID();
		if (tmpAnalysisRunID != -1)
			return tmpAnalysisRunID;

		final OmegaExperimenter experimenter = particleLinkingRun
		        .getExperimenter();
		final int experimenterID = this.getOrSaveExperimenter(experimenter);
		tmpAnalysisRunID = this.saveAnalysisRun(particleLinkingRun,
		        experimenterID, parentAnalysisRunID);
		particleLinkingRun.setElementID(tmpAnalysisRunID);

		final long analysisRunID = tmpAnalysisRunID;

		final Long startingTime = System.currentTimeMillis();
		final List<Thread> threads = new ArrayList<Thread>();
		int counter = 0;
		for (final OmegaTrajectory trajectory : particleLinkingRun
		        .getResultingTrajectories()) {
			final Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					long trajectoryID = trajectory.getElementID();
					try {
						if (trajectoryID == -1) {
							trajectoryID = OmegaMySqlGateway.this
							        .saveTrajectory(trajectory);
						}
						OmegaMySqlGateway.this
						        .saveAnalysisTrajectoryLinkIfNeeded(
						                (int) trajectoryID, analysisRunID);
						for (final OmegaROI roi : trajectory.getROIs()) {
							final long roiID = roi.getElementID();
							OmegaMySqlGateway.this
							        .saveTrajectoryParticleLinkIfNeeded(
							                (int) roiID, (int) trajectoryID);
						}
					} catch (final SQLException ex) {
						OmegaLogFileManager.handleCoreException(ex);
					}
					trajectory.setElementID(trajectoryID);
				}
			});
			t.setName("SavingTrajectory" + counter);
			counter++;
			threads.add(t);
		}
		for (final Thread t : threads) {
			t.start();
		}
		for (final Thread t : threads) {
			if (t.isAlive()) {
				try {
					t.join();
				} catch (final InterruptedException ex) {
					OmegaLogFileManager.handleCoreException(ex);
				}
			}
		}
		final Long totalTime = System.currentTimeMillis() - startingTime;
		System.out.println("TotalTime: " + (totalTime / 1000));

		return analysisRunID;
	}

	private List<OmegaTrajectory> loadTrajectories(final int analysisRunID,
	        final Map<OmegaFrame, List<OmegaROI>> particlesMap,
	        final List<OmegaElement> trajsLoaded) throws SQLException {
		final List<OmegaTrajectory> trajectories = new ArrayList<OmegaTrajectory>();
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM analysisTrajectoriesMap WHERE AnalysisRun_Seq_Id = ");
		query1.append(analysisRunID);
		final ResultSet results1 = this.connection.prepareStatement(
		        query1.toString()).executeQuery();
		while (results1.next()) {
			final int trajectoryID = results1.getInt(2);
			final StringBuffer query2 = new StringBuffer();
			query2.append("SELECT * FROM trajectory WHERE Trajectory_Seq_Id = ");
			query2.append(trajectoryID);
			final ResultSet results2 = this.connection.prepareStatement(
			        query2.toString()).executeQuery();
			while (results2.next()) {
				// final int trajectoryID = results2.getInt(1);
				final String name = results2.getString(2);
				final int length = results2.getInt(3);
				final int color_r = results2.getInt(4);
				final int color_g = results2.getInt(5);
				final int color_b = results2.getInt(6);
				OmegaTrajectory trajectory = (OmegaTrajectory) this.getElement(
				        trajsLoaded, trajectoryID);
				if (trajectory == null) {
					trajectory = new OmegaTrajectory(length);
					trajectory.setElementID((long) trajectoryID);
					trajectory.setName(name);
					trajectory.setColor(new Color(color_r, color_g, color_b));
				}
				trajectories.add(trajectory);
				for (final OmegaFrame frame : particlesMap.keySet()) {
					for (final OmegaROI roi : particlesMap.get(frame)) {
						final StringBuffer query3 = new StringBuffer();
						query3.append("SELECT * FROM trajectoriesParticlesMap WHERE ROI_Seq_Id = ");
						query3.append(roi.getElementID());
						final ResultSet results3 = this.connection
						        .prepareStatement(query3.toString())
						        .executeQuery();
						if (!results3.next()) {
							// TODO manage error
							continue;
						}
						if (results3.getInt(1) == trajectoryID) {
							trajectory.addROI(roi);
						}
					}
				}
			}
		}

		Collections.sort(trajectories);
		return trajectories;
	}

	private int saveTrajectory(final OmegaTrajectory trajectory)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO trajectory (Name, NumberOfPoints, Color_Red, Color_Green, Color_Blue) VALUES ('");
		query.append(trajectory.getName());
		query.append("',");
		query.append(trajectory.getLength());
		query.append(",");
		query.append(trajectory.getColor().getRed());
		query.append(",");
		query.append(trajectory.getColor().getGreen());
		query.append(",");
		query.append(trajectory.getColor().getBlue());
		query.append(")");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private void updateTrajectory(final OmegaTrajectory trajectory)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("UPDATE trajectory SET ");
		if (trajectory.isNameChanged()) {
			query.append("Name = '");
			query.append(trajectory.getName());
			query.append("'");
		}

		if (trajectory.isColorChanged()) {
			if (trajectory.isNameChanged()) {
				query.append(", ");
			}
			query.append("Color_Red = ");
			query.append(trajectory.getColor().getRed());
			query.append(", Color_Green = ");
			query.append(trajectory.getColor().getGreen());
			query.append(", Color_Blue = ");
			query.append(trajectory.getColor().getBlue());
		}

		query.append(" WHERE Trajectory_Seq_Id = ");
		query.append(trajectory.getElementID());
		// final int rowCount =
		this.connection.prepareStatement(query.toString()).executeUpdate();
	}

	private List<OmegaROI> getROIs(
	        final Map<OmegaFrame, List<OmegaROI>> particlesMap,
	        final int frameIndex) {
		for (final OmegaFrame frame : particlesMap.keySet()) {
			if (frame.getIndex() == frameIndex)
				return particlesMap.get(frame);
		}
		return null;
	}

	private Map<OmegaTrajectory, List<OmegaSegment>> loadSegments(
	        final int analysisRunID, final List<OmegaTrajectory> trajectories,
	        final Map<OmegaFrame, List<OmegaROI>> particlesMap)
	        throws SQLException {
		final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments = new LinkedHashMap<OmegaTrajectory, List<OmegaSegment>>();
		for (final OmegaTrajectory traj : trajectories) {
			final StringBuffer query = new StringBuffer();
			query.append("SELECT * FROM segment WHERE AnalysisRun_Seq_Id = ");
			query.append(analysisRunID);
			query.append(" AND Trajectory_Seq_Id = ");
			query.append(traj.getElementID());
			final ResultSet results = this.connection.prepareStatement(
			        query.toString()).executeQuery();
			final List<OmegaSegment> segments = new ArrayList<OmegaSegment>();
			while (results.next()) {
				results.getInt(1);
				final int segmentID = results.getInt(1);
				final int segmType = results.getInt(4);
				final int startingROI_ID = results.getInt(5);
				final int endingROI_ID = results.getInt(6);
				final int startingROIFrame = results.getInt(7);
				final int endingROIFrame = results.getInt(8);

				final List<OmegaROI> startingParticles = this.getROIs(
				        particlesMap, startingROIFrame);
				final List<OmegaROI> endingParticles = this.getROIs(
				        particlesMap, endingROIFrame);

				if ((startingParticles == null) || (endingParticles == null)) {
					// TODO error
					continue;
				}

				final OmegaROI startingROI = (OmegaROI) this.getElement(
				        new ArrayList<OmegaElement>(startingParticles),
				        startingROI_ID);
				final OmegaROI endingROI = (OmegaROI) this.getElement(
				        new ArrayList<OmegaElement>(endingParticles),
				        endingROI_ID);

				final OmegaSegment segment = new OmegaSegment(startingROI,
				        endingROI);
				segment.setSegmentationType(segmType);
				segment.setElementID((long) segmentID);
				segments.add(segment);
			}
			resultingSegments.put(traj, segments);
		}
		return resultingSegments;
	}

	private int saveSegment(final long analysisRunID, final long trajectoryID,
	        final OmegaSegment segment) throws SQLException {
		final OmegaROI startingROI = segment.getStartingROI();
		final OmegaROI endingROI = segment.getEndingROI();
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO segment (AnalysisRun_Seq_Id, Trajectory_Seq_Id, SegmentationType, StartingROI_Id, StartingROI_FrameIndex, EndingROI_id, EndingROI_FrameIndex) VALUES (");
		query.append(analysisRunID);
		query.append(",");
		query.append(trajectoryID);
		query.append(",");
		query.append(segment.getSegmentationType());
		query.append(",");
		query.append(startingROI.getElementID());
		query.append(",");
		query.append(startingROI.getFrameIndex());
		query.append(",");
		query.append(endingROI.getElementID());
		query.append(",");
		query.append(endingROI.getFrameIndex());
		query.append(")");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private OmegaAlgorithmSpecification loadAlgorithmSpecification(
	        final int algoSpecID, final List<OmegaElement> personLoaded,
	        final List<OmegaElement> algoInfoLoaded) throws SQLException,
	        ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM algorithm_specification WHERE AlgorithmSpecification_Seq_Id = ");
		query.append(algoSpecID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (!results.next())
			// TODO gestire errore
			return null;
		final int algoInfoID = results.getInt(2);
		OmegaAlgorithmInformation algoInfo = (OmegaAlgorithmInformation) this
		        .getElement(algoInfoLoaded, algoInfoID);
		if (algoInfo == null) {
			algoInfo = this.loadAlgorithmInformation(algoInfoID, personLoaded);
			algoInfoLoaded.add(algoInfo);
		}
		final OmegaAlgorithmSpecification algoSpec = new OmegaAlgorithmSpecification(
		        algoInfo);
		algoSpec.setElementID((long) algoSpecID);
		final List<OmegaParameter> params = this.loadParameters(algoSpecID);
		algoSpec.addParameters(params);
		return algoSpec;
	}

	private List<OmegaParameter> loadParameters(final int algoSpecID)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM parameter WHERE AlgorithmSpecification_Seq_Id = ");
		query.append(algoSpecID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		final List<OmegaParameter> params = new ArrayList<OmegaParameter>();
		while (results.next()) {
			final int id = results.getInt(1);
			final String name = results.getString(3);
			final String value = results.getString(4);
			results.getString(5);
			final OmegaParameter param = new OmegaParameter(name, value);
			param.setElementID((long) id);
			params.add(param);
		}
		return params;
	}

	private OmegaAlgorithmInformation loadAlgorithmInformation(
	        final int algoInfoID, final List<OmegaElement> personLoaded)
	        throws SQLException, ParseException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM algorithm_information WHERE AlgorithmInformation_Seq_Id = ");
		query.append(algoInfoID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (!results.next())
			// TODO gestire errore
			return null;
		final int personID = results.getInt(2);
		OmegaPerson author = (OmegaPerson) this.getElement(personLoaded,
		        personID);
		if (author == null) {
			author = this.loadPerson(personID);
			personLoaded.add(author);
		}
		final String name = results.getString(3);
		final double version = results.getDouble(4);
		final String description = results.getString(5);
		final String publication_date = results.getString(6);
		final SimpleDateFormat formatter = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		final Date publicationDate = formatter.parse(publication_date);
		final OmegaAlgorithmInformation algoInfo = new OmegaAlgorithmInformation(
		        name, version, description, author, publicationDate);
		algoInfo.setElementID((long) algoInfoID);
		return algoInfo;
	}

	private int saveAlgorithmSpecANDInfo(final OmegaAnalysisRun analysisRun)
	        throws SQLException {
		final OmegaAlgorithmSpecification algoSpec = analysisRun
		        .getAlgorithmSpec();
		final OmegaAlgorithmInformation algoInfo = algoSpec.getAlgorithmInfo();
		int infoID = this.getAlgorithmInformation(algoInfo);
		if (infoID == -1) {
			infoID = this.saveAlgorithmInformation(algoInfo);
			algoInfo.setElementID((long) infoID);
		}
		int specID = this.getAlgorithmSpecification(algoSpec);
		if (specID == -1) {
			specID = this.saveAlgorithmSpecification(algoSpec, infoID);
			algoSpec.setElementID((long) specID);
		}
		return specID;
	}

	private int getAlgorithmSpecification(
	        final OmegaAlgorithmSpecification algoSpec) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM algorithm_specification WHERE AlgorithmInformation_Seq_Id = ");
		query.append(algoSpec.getAlgorithmInfo().getElementID());
		// TODO parameter stuff
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (!results.next())
			return -1;
		final int dbID = results.getInt(1);
		return dbID;
	}

	private int saveAlgorithmSpecification(
	        final OmegaAlgorithmSpecification algoSpec, final int infoID)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO algorithm_specification (AlgorithmInformation_Seq_Id) VALUES (");
		query.append(algoSpec.getAlgorithmInfo().getElementID());
		query.append(")");
		final int id = this.insertAndGetId(query.toString());
		for (final OmegaParameter param : algoSpec.getParameters()) {
			final int paramID = this.saveParameter(id, param);
			param.setElementID((long) paramID);
		}
		return id;
	}

	private int saveParameter(final int algoSpecID, final OmegaParameter param)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO parameter (AlgorithmSpecification_Seq_Id, Parameter_name, Parameter_value, Parameter_type) VALUES (");
		query.append(algoSpecID);
		query.append(",'");
		query.append(param.getName());
		query.append("','");
		query.append(String.valueOf(param.getValue()));
		query.append("','");
		query.append(param.getClazz());
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private int getAlgorithmInformation(final OmegaAlgorithmInformation algoInfo)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM algorithm_information WHERE Person_Seq_Id = ");
		query.append(algoInfo.getAuthor().getElementID());
		query.append(" AND Name = '");
		query.append(algoInfo.getName());
		query.append("' AND Version = ");
		query.append(algoInfo.getVersion());
		query.append(" AND Description = '");
		query.append(algoInfo.getDescription());
		query.append("' AND Publication_date = '");
		final DateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		query.append(format.format(algoInfo.getPublicationData()));
		query.append("'");
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (!results.next())
			return -1;
		final int dbID = results.getInt(1);
		return dbID;
	}

	private int saveAlgorithmInformation(
	        final OmegaAlgorithmInformation algoInfo) throws SQLException {
		final OmegaPerson person = algoInfo.getAuthor();
		final int personID = this.getOrSavePerson(person);

		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO algorithm_information (Person_Seq_Id, Name, Version, Description, Publication_date) VALUES (");
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
		final String formattedDate = format.format(algoInfo
		        .getPublicationData());
		query.append(formattedDate);
		query.append("')");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private int saveProject(final OmegaProject project) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO project (Project_Name, Omero_Id) VALUES('");
		query.append(project.getName());
		query.append("',");
		query.append(project.getElementID());
		query.append(")");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private int saveDataset(final OmegaDataset dataset, final int projectID)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO dataset (Dataset_Name, Omero_Id, Project_Seq_Id) VALUES ('");
		query.append(dataset.getName());
		query.append("',");
		query.append(dataset.getElementID());
		query.append(",");
		query.append(projectID);
		query.append(")");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private int saveImage(final OmegaImage image, final int datasetID,
	        final int experimenterID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO image (Image_name, Omero_Id, Dataset_Seq_Id, Experimenter_Seq_Id) VALUES ('");
		query.append(image.getName());
		query.append("',");
		query.append(image.getElementID());
		query.append(",");
		query.append(datasetID);
		query.append(",");
		query.append(experimenterID);
		query.append(")");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private int saveImage(final OmegaImage image) throws SQLException {
		final OmegaDataset dataset = image.getParentDatasets().get(0);
		final OmegaProject project = dataset.getParentProject();
		final OmegaExperimenter experimenter = image.getExperimenter();
		int projectID = this.getDBIdFROMOmegaElementId("project",
		        project.getElementID());
		if (projectID == -1) {
			projectID = this.saveProject(project);
		}
		int datasetID = this.getDBIdFROMOmegaElementId("dataset",
		        dataset.getElementID());
		if (datasetID == -1) {
			datasetID = this.saveDataset(dataset, projectID);
		}
		final int experimenterID = this.getOrSaveExperimenter(experimenter);
		final int id = this.saveImage(image, datasetID, experimenterID);
		return id;
	}

	public void loadImages(final OmegaImage image) throws SQLException,
	        ParseException {
		final int imageID = this.getDBIdFROMOmegaElementId("image",
		        image.getElementID());
		this.loadFrames(image);
		this.loadParticleDetectionAnalysisRun(image, imageID);
	}

	private int saveImagePixels(final OmegaImagePixels pixels, final int imageID)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO pixels (Omero_Id, Image_Seq_Id, PixelSizeX, PixelSizeY, PixelSizeZ, SizeX, SizeY, SizeZ, SizeC, SizeT) VALUES (");
		query.append(pixels.getElementID());
		query.append(",");
		query.append(imageID);
		query.append(",");
		query.append(pixels.getPixelSizeX());
		query.append(",");
		query.append(pixels.getPixelSizeY());
		query.append(",");
		query.append(pixels.getPixelSizeZ());
		query.append(",");
		query.append(pixels.getSizeX());
		query.append(",");
		query.append(pixels.getSizeY());
		query.append(",");
		query.append(pixels.getSizeZ());
		query.append(",");
		query.append(pixels.getSizeC());
		query.append(",");
		query.append(pixels.getSizeT());
		query.append(")");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private List<OmegaFrame> loadFrames(final OmegaImage image)
	        throws SQLException {
		final List<OmegaFrame> frameList = new ArrayList<OmegaFrame>();
		final int pixelsID = this.getDBIdFROMOmegaElementId("pixels", image
		        .getDefaultPixels().getElementID());
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM frame WHERE Pixel_Seq_Id = ");
		query.append(pixelsID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		while (results.next()) {
			final int id = results.getInt(1);
			if (image.getDefaultPixels().containsFrame(id)) {
				continue;
			}
			final int index = results.getInt(4);
			final int channel = results.getInt(5);
			final int zPlane = results.getInt(6);
			final OmegaFrame f = new OmegaFrame((long) id, index, channel,
			        zPlane);
			f.setParentPixels(image.getDefaultPixels());
			frameList.add(f);
		}

		Collections.sort(frameList, new Comparator<OmegaFrame>() {
			@Override
			public int compare(final OmegaFrame o1, final OmegaFrame o2) {
				if (o1.getIndex() == o2.getIndex())
					return 0;
				else if (o1.getIndex() < o2.getIndex())
					return -1;
				return 1;
			};
		});
		image.getDefaultPixels().addFrames(frameList);
		return frameList;
		// // FIXME Reorder, check if there is a better way
		// for (int i = 0; i < frameList.size(); i++) {
		// for (final OmegaFrame f : frameList) {
		// if (f.getIndex() == i) {
		// image.getDefaultPixels().addFrame(f);
		// }
		// }
		// }
	}

	private int saveFrames(final OmegaFrame frame, final int pixelsID)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO frame (Pixel_Seq_Id, Frame_index, Channel, ZPlane) VALUES (");
		query.append(pixelsID);
		query.append(",");
		query.append(frame.getIndex());
		query.append(",");
		query.append(frame.getChannel());
		query.append(",");
		query.append(frame.getZPlane());
		query.append(")");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private int getOrSaveExperimenter(final OmegaExperimenter experimenter)
	        throws SQLException {
		// final StringBuffer queryTest = new StringBuffer();
		// queryTest.append("SELECT * FROM experimenter");
		// final ResultSet resultsTest = this.connection.prepareStatement(
		// queryTest.toString()).executeQuery();
		// if (resultsTest.getRow() == 0)
		// return -1;
		// else {
		// while (resultsTest.next()) {
		// System.out.println("Pid: " + resultsTest.getInt(1)
		// + " OmeroId: " + resultsTest.getInt(2));
		// }
		// }

		int experimenterID = this.getDBIdFROMOmegaElementId("experimenter",
		        experimenter.getOmeroId());
		if (experimenterID == -1) {
			experimenterID = this.saveExperimenter(experimenter);
		}
		return experimenterID;
	}

	private OmegaExperimenter loadExperimenter(final int experimenterID)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM experimenter WHERE Experimenter_Seq_Id = ");
		query.append(experimenterID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (!results.next())
			// TODO throw error here
			return null;
		final int personID = results.getInt(2);
		final int omeroID = results.getInt(3);
		final StringBuffer query2 = new StringBuffer();
		query2.append("SELECT * FROM person WHERE Person_Seq_Id = ");
		query2.append(personID);
		final ResultSet results2 = this.connection.prepareStatement(
		        query2.toString()).executeQuery();
		if (!results2.next())
			// TODO throw error here
			return null;
		final String firstName = results2.getString(1);
		final String lastName = results2.getString(2);
		final OmegaExperimenter exp = new OmegaExperimenter(omeroID, firstName,
		        lastName);
		exp.setElementID((long) experimenterID);
		return exp;
	}

	private int saveExperimenter(final OmegaExperimenter experimenter)
	        throws SQLException {
		final int personID = this.getOrSavePerson(experimenter);
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO experimenter (Person_Seq_Id, Omero_Id) VALUES (");
		query.append(personID);
		query.append(",");
		query.append(experimenter.getOmeroId());
		query.append(")");
		final int experimenterID = this.insertAndGetId(query.toString());
		return experimenterID;
	}

	private OmegaPerson loadPerson(final int personID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM person WHERE Person_Seq_Id = ");
		query.append(personID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (!results.next())
			// TODO throw error here
			return null;
		final String firstName = results.getString(2);
		final String lastName = results.getString(3);
		final OmegaPerson person = new OmegaPerson(firstName, lastName);
		person.setElementID((long) personID);
		return person;
	}

	private int getOrSavePerson(final OmegaPerson person) throws SQLException {
		int personID = this.getPerson(person);
		if (personID == -1) {
			personID = this.savePerson(person);
			person.setElementID((long) personID);
		}
		return personID;
	}

	private int getPerson(final OmegaPerson person) throws SQLException {
		// final StringBuffer queryTest = new StringBuffer();
		// queryTest.append("SELECT * FROM person");
		// final ResultSet resultsTest = this.connection.prepareStatement(
		// queryTest.toString()).executeQuery();
		// if (resultsTest.getRow() == 0)
		// return -1;
		// else {
		// while (resultsTest.next()) {
		// System.out.println("FN: " + resultsTest.getString(1) + " LN: "
		// + resultsTest.getString(2));
		// }
		// }

		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM person WHERE First_name = '");
		query.append(person.getFirstName());
		query.append("' AND Last_name = '");
		query.append(person.getLastName());
		query.append("'");
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (!results.next())
			return -1;
		final int dbID = results.getInt(1);
		return dbID;
	}

	private int savePerson(final OmegaPerson person) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO person (First_name, Last_name) VALUES ('");
		query.append(person.getFirstName());
		query.append("','");
		query.append(person.getLastName());
		query.append("')");
		final int personID = this.insertAndGetId(query.toString());
		return personID;
	}

	private OmegaSegmentationTypes loadSegmentationTypes(
	        final int segmentationTypesID) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM segmentationTypes WHERE SegmentationTypes_Seq_Id = ");
		query1.append(segmentationTypesID);
		final ResultSet results1 = this.connection.prepareStatement(
		        query1.toString()).executeQuery();
		if (!results1.next())
			return null;
		final String name = results1.getString(2);

		final List<OmegaSegmentationType> types = new ArrayList<OmegaSegmentationType>();

		final StringBuffer query2 = new StringBuffer();
		query2.append("SELECT * FROM segmentationTypesMap WHERE SegmentationTypes_Seq_Id = ");
		query2.append(segmentationTypesID);
		final ResultSet results2 = this.connection.prepareStatement(
		        query2.toString()).executeQuery();

		while (!results2.next()) {
			final int segmentationTypeID = results2.getInt(2);
			final StringBuffer query3 = new StringBuffer();
			query3.append("SELECT * FROM segmentationType WHERE SegmentationType_Seq_Id = ");
			query3.append(segmentationTypeID);
			final ResultSet results3 = this.connection.prepareStatement(
			        query3.toString()).executeQuery();
			if (!results3.next()) {
				continue; // TODO throw error
			}

			final String s = results2.getString(2);
			final Integer val = results2.getInt(3);
			final Integer red = results2.getInt(4);
			final Integer blue = results2.getInt(5);
			final Integer green = results2.getInt(6);
			final Color col = new Color(red, green, blue);
			final OmegaSegmentationType segmType = new OmegaSegmentationType(s,
			        val, col);
			types.add(segmType);
		}

		final OmegaSegmentationTypes segmTypes = new OmegaSegmentationTypes(
		        name, types);
		return segmTypes;

	}

	private int getSegmentationTypesID(final int analysisRunID)
	        throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM analysisSegmentationTypesMap WHERE AnalysisRun_Seq_Id = ");
		query1.append(analysisRunID);
		final ResultSet results = this.connection.prepareStatement(
		        query1.toString()).executeQuery();
		if (!results.next())
			return -1;
		return results.getInt(1);
	}

	private void saveAnalysisSegmentationTypesLinkIfNeeded(
	        final long analysisRunID, final long segmTypesID)
	        throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM analysisSegmentationTypesMap WHERE AnalysisRun_Seq_Id = ");
		query1.append(analysisRunID);
		query1.append(" AND SegmentationTypes_Seq_Id = ");
		query1.append(segmTypesID);
		final ResultSet results = this.connection.prepareStatement(
		        query1.toString()).executeQuery();
		if (!results.next()) {
			final StringBuffer query2 = new StringBuffer();
			query2.append("INSERT INTO analysisSegmentationTypesMap (AnalysisRun_Seq_Id, SegmentationTypes_Seq_Id) VALUES (");
			query2.append(analysisRunID);
			query2.append(",");
			query2.append(segmTypesID);
			query2.append(")");
			this.connection.prepareStatement(query2.toString()).executeUpdate();
		}
	}

	private void updateAnalysisSegmentationTypesLinkIfNeeded(
	        final long analysisRunID, final long segmTypesID)
	        throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("DELETE FROM analysisSegmentationTypesMap WHERE AnalysisRun_Seq_Id = ");
		query1.append(analysisRunID);
		this.connection.prepareStatement(query1.toString()).executeUpdate();

		final StringBuffer query2 = new StringBuffer();
		query2.append("INSERT INTO analysisSegmentationTypesMap (AnalysisRun_Seq_Id, SegmentationTypes_Seq_Id) VALUES (");
		query2.append(analysisRunID);
		query2.append(",");
		query2.append(segmTypesID);
		query2.append(")");
		this.connection.prepareStatement(query2.toString()).executeUpdate();
	}

	/**
	 * Check for the given OmegaSegmentationTypes in DB Return 0 if not found, 1
	 * if found equal, -1 if found different
	 * 
	 * @param segmTypes
	 * @return
	 * @throws SQLException
	 */
	// TODO insert throw instead of -1
	public int isSegmentationTypesNameInDBWithDifferentValues(
	        final OmegaSegmentationTypes segmTypes) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM segmentationTypes WHERE Name = '");
		query1.append(segmTypes.getName());
		query1.append("'");
		final ResultSet results1 = this.connection.prepareStatement(
		        query1.toString()).executeQuery();
		if (!results1.next())
			return 0;
		final int segmentationTypesID = results1.getInt(1);

		segmTypes.getTypes();

		final StringBuffer query2 = new StringBuffer();
		query2.append("SELECT * FROM segmentationTypesMap WHERE SegmentationTypes_Seq_Id = ");
		query2.append(segmentationTypesID);
		final ResultSet results2 = this.connection.prepareStatement(
		        query2.toString()).executeQuery();

		while (!results2.next()) {
			final int segmentationTypeID = results2.getInt(2);
			final OmegaSegmentationType segmType = segmTypes
			        .getSegmentationType(segmentationTypeID);
			if (segmType == null)
				return -1;
			final String origName = segmType.getName();
			final Integer origVal = segmType.getValue();
			final Color origCol = segmType.getColor();
			final StringBuffer query3 = new StringBuffer();
			query3.append("SELECT * FROM segmentationType WHERE SegmentationType_Seq_Id = ");
			query3.append(segmentationTypeID);
			final ResultSet results3 = this.connection.prepareStatement(
			        query3.toString()).executeQuery();
			if (!results3.next())
				return 0; // TODO throw error
			final String name = results3.getString(2);
			final Integer val = results2.getInt(3);
			final Integer red = results2.getInt(4);
			final Integer blue = results2.getInt(5);
			final Integer green = results2.getInt(6);
			if (!name.equals(origName) || (val != origVal)
			        || !(origCol.getRed() != red)
			        || (origCol.getBlue() != blue)
			        || (origCol.getGreen() != green))
				return -1;
		}
		return 1;
	}

	private void updateTMSegmentationTypes(
	        final OmegaTrajectoriesManagerRun tmRun) throws SQLException {
		final OmegaSegmentationTypes segmTypes = tmRun.getSegmentationTypes();
		long segmTypesID = segmTypes.getElementID();
		if (segmTypesID == -1) {
			segmTypesID = this.getOrSaveSegmentationTypes(segmTypes);

		} else {
			if (segmTypes.isNameChanged() || segmTypes.isChanged()) {
				this.updateSegmentationTypes(segmTypes);
			}
		}

		this.updateAnalysisSegmentationTypesLinkIfNeeded(tmRun.getElementID(),
		        segmTypesID);
	}

	private void updateSegmentationTypes(final OmegaSegmentationTypes segmTypes)
	        throws SQLException {
		final long segmTypesID = segmTypes.getElementID();
		final StringBuffer query = new StringBuffer();
		if (segmTypes.isNameChanged()) {
			query.append("UPDATE segmentationTypes SET Name = ");
			query.append(segmTypes.getName());
			query.append(" WHERE  = SegmentationTypes_Seq_Id");
			query.append(segmTypesID);
			this.connection.prepareStatement(query.toString()).executeUpdate();
		}

		if (!segmTypes.isChanged())
			return;

		final StringBuffer query2 = new StringBuffer();
		query2.append("SELECT * FROM segmentationTypesMap WHERE SegmentationTypes_Seq_Id = ");
		query2.append(segmTypesID);
		final ResultSet results2 = this.connection.prepareStatement(
		        query2.toString()).executeQuery();

		final List<Integer> idsToRemove = new ArrayList<Integer>();
		while (!results2.next()) {
			final int segmentationTypeID = results2.getInt(2);
			final OmegaSegmentationType segmType = segmTypes
			        .getSegmentationType(segmentationTypeID);
			if (segmType == null) {
				idsToRemove.add(segmentationTypeID);
			}
		}
		for (final Integer segmTypeID : idsToRemove) {
			final StringBuffer query3 = new StringBuffer();
			query3.append("DELETE FROM segmentationTypesMap WHERE SegmentationType_Seq_Id = ");
			query3.append(segmTypeID);
			this.connection.prepareStatement(query3.toString()).executeQuery();
		}

		for (final OmegaSegmentationType segmType : segmTypes.getTypes()) {
			long segmTypeID = segmType.getElementID();
			if (segmTypeID == -1) {
				segmTypeID = this.saveSingleSegmentationType(segmType);
				this.saveSegmentationTypesLinkIfNeeded(segmTypesID, segmTypeID);
			} else {
				if (segmType.isNameChanged() || segmType.isChanged()) {
					this.updateSingleSegmentationType(segmType);
				}
			}
		}
	}

	private int getOrSaveSegmentationTypes(
	        final OmegaSegmentationTypes segmTypes) throws SQLException {
		int segmTypesID = this.getSegmentationTypes(segmTypes);
		if (segmTypesID == -1) {
			segmTypesID = this.saveSegmentationTypes(segmTypes);
			segmTypes.setElementID((long) segmTypesID);
		}
		return segmTypesID;
	}

	private int getSegmentationTypes(final OmegaSegmentationTypes segmTypes)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM segmentationTypes WHERE Name = '");
		query.append(segmTypes.getName());
		query.append("'");
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (!results.next())
			return -1;
		final int dbID = results.getInt(1);
		return dbID;
	}

	private int saveSegmentationTypes(final OmegaSegmentationTypes segmTypes)
	        throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO segmentationTypes (Name) VALUES ('");
		query.append(segmTypes.getName());
		query.append("')");
		final int segmTypesID = this.insertAndGetId(query.toString());

		for (final OmegaSegmentationType segmType : segmTypes.getTypes()) {
			final int segmTypeID = this
			        .getOrSaveSingleSegmentationType(segmType);
			this.saveSegmentationTypesLinkIfNeeded(segmTypesID, segmTypeID);
		}
		return segmTypesID;
	}

	private void saveSegmentationTypesLinkIfNeeded(final long segmTypesID,
	        final long segmTypeID) throws SQLException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM segmentationTypesMap WHERE SegmentationTypes_Seq_Id = ");
		query1.append(segmTypesID);
		query1.append(" AND SegmentationType_Seq_Id = ");
		query1.append(segmTypeID);
		final ResultSet results = this.connection.prepareStatement(
		        query1.toString()).executeQuery();
		if (!results.next()) {
			final StringBuffer query2 = new StringBuffer();
			query2.append("INSERT INTO segmentationTypesMap (SegmentationTypes_Seq_Id, SegmentationType_Seq_Id) VALUES (");
			query2.append(segmTypesID);
			query2.append(",");
			query2.append(segmTypeID);
			query2.append(")");
			this.connection.prepareStatement(query2.toString()).executeUpdate();
		}
	}

	private int getOrSaveSingleSegmentationType(
	        final OmegaSegmentationType segmType) throws SQLException {
		int segmTypeID = this.getSingleSegmentationType(segmType);
		if (segmTypeID == -1) {
			segmTypeID = this.saveSingleSegmentationType(segmType);
			segmType.setElementID((long) segmTypeID);
		}
		return segmTypeID;
	}

	private int getSingleSegmentationType(final OmegaSegmentationType segmType)
	        throws SQLException {
		final Color c = segmType.getColor();
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM segmentationType WHERE Name = '");
		query.append(segmType.getName());
		query.append("' AND Value = ");
		query.append(segmType.getValue());
		query.append(" AND Color_red = ");
		query.append(c.getRed());
		query.append(" AND Color_blue = ");
		query.append(c.getBlue());
		query.append(" AND Color_green = ");
		query.append(c.getGreen());
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (!results.next())
			return -1;
		final int dbID = results.getInt(1);
		return dbID;
	}

	private int saveSingleSegmentationType(final OmegaSegmentationType segmType)
	        throws SQLException {
		final Color color = segmType.getColor();
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO segmentationType (Name, Value, Color_Red, Color_Blue, Color_Green) VALUES ('");
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
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private void updateSingleSegmentationType(
	        final OmegaSegmentationType segmType) throws SQLException {
		final Color color = segmType.getColor();
		final StringBuffer query = new StringBuffer();
		query.append("UPDATE segmentationType SET ");
		if (segmType.isNameChanged()) {
			query.append("Name = '");
			query.append(segmType.getName());
		}
		if (segmType.isNameChanged() && segmType.isChanged()) {
			query.append("', ");
		}
		if (segmType.isChanged()) {
			query.append("Value = ");
			query.append(segmType.getValue());
			query.append(", Color_Red");
			query.append(color.getRed());
			query.append(", Color_Blue");
			query.append(color.getBlue());
			query.append(", Color_Green");
			query.append(color.getGreen());
		}
		query.append(" WHERE SegmentationType_Seq_Id = ");
		query.append(segmType.getElementID());
		this.connection.prepareStatement(query.toString()).executeUpdate();
	}
}
