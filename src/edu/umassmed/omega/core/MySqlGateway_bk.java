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
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
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
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class MySqlGateway_bk {

	public static String USER = "omega";
	public static String PSW = "1234";
	public static String HOSTNAME = "146.189.76.56";
	public static String PORT = "3306";
	public static String DB_NAME = "omega";

	private final OmegaDBServerInformation serverInfo;
	private final OmegaLoginCredentials loginCred;

	private Connection connection;

	public MySqlGateway_bk() {
		this.connection = null;
		this.serverInfo = null;
		this.loginCred = null;
	}

	private boolean isConnected() {
		return this.connection != null;
	}

	private void connect() throws ClassNotFoundException, SQLException {
		if ((this.connection != null) || (this.serverInfo == null)
		        || (this.loginCred == null))
			// Throw eccezione
			return;
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

	public void disconnect(final boolean commit) throws SQLException {
		if (commit) {
			this.commit();
		} else {
			this.rollback();
		}
		this.connection.close();
		this.connection = null;
	}

	private int insertAndGetId(final String query) throws SQLException,
	        ClassNotFoundException {
		if (!this.isConnected()) {
			this.connect();
		}

		final Statement stat = this.connection.createStatement();
		final int error = stat.executeUpdate(query,
		        Statement.RETURN_GENERATED_KEYS);

		final ResultSet rs = stat.getGeneratedKeys();
		if ((error != 1) || !rs.next()) {
			this.disconnect(false);
			throw new SQLException("Any generated keys");
		}
		this.disconnect(true);
		return rs.getInt(1);
	}

	private int getDBIdFROMOmegaElementId(final String elementType,
	        final long omegaID) throws SQLException, ClassNotFoundException {
		if (!this.isConnected()) {
			this.connect();
		}

		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM ");
		query.append(elementType);
		query.append(" WHERE Omero_Id = ");
		query.append(omegaID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();

		this.disconnect(false);

		if (results.getRow() == 0)
			return -1;
		final int dbID = results.getInt(0);
		return dbID;
	}

	private void loadDetectionAnalysisRun(final OmegaImage image,
	        final int imageID) throws SQLException, ParseException,
	        ClassNotFoundException {
		if (!this.isConnected()) {
			this.connect();
		}

		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM analysis_run_map WHERE OmegaElement_Seq_Id = ");
		query.append(imageID);
		query.append(" AND OmegaElement_Type = 'image'");

		this.disconnect(false);

		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (results.getRow() == 0)
			return;
		final List<Integer> analysisRunIDs = new ArrayList<Integer>();
		while (results.next()) {
			final int analysisRunID = results.getInt(0);
			analysisRunIDs.add(analysisRunID);
		}

		for (final Integer analysisRunID : analysisRunIDs) {

			if (!this.isConnected()) {
				this.connect();
			}

			final StringBuffer query2 = new StringBuffer();
			query2.append("SELECT * FROM analysis_run WHERE AnalysisRun_Seq_Id = ");
			query2.append(analysisRunID);
			final ResultSet results2 = this.connection.prepareStatement(
			        query.toString()).executeQuery();

			this.disconnect(false);

			if (results2.getRow() == 0) {
				continue;
			}
			while (results2.next()) {
				final String name = results2.getString(1);
				final String publicationDate = results2.getString(2);
				final SimpleDateFormat formatter = new SimpleDateFormat(
				        OmegaConstants.OMEGA_DATE_FORMAT);
				final Date timeStamps = formatter.parse(publicationDate);
				final int experimenterID = results2.getInt(3);
				final int algoSpecID = results2.getInt(5);
				final OmegaExperimenter owner = this
				        .loadExperimenter(experimenterID);
				final OmegaAlgorithmSpecification algoSpec = this
				        .loadAlgorithmSpecification(algoSpecID);
				final Map<OmegaFrame, List<OmegaROI>> resultingParticles = this
				        .loadParticles(analysisRunID, image.getDefaultPixels()
				                .getFrames());

				final OmegaAnalysisRun analysisRun = new OmegaParticleDetectionRun(
				        owner, algoSpec, timeStamps, name, resultingParticles);
				analysisRun.setElementID((long) analysisRunID);
				this.loadLinkingAnalysisRun(analysisRun);
				image.addAnalysisRun(analysisRun);
			}

		}
	}

	private void loadLinkingAnalysisRun(final OmegaAnalysisRun parentAnalysisRun)
	        throws SQLException, ParseException, ClassNotFoundException {
		if (!this.isConnected()) {
			this.connect();
		}

		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM analysis_run WHERE ParentAnalysisRun_Seq_Id = ");
		query.append(parentAnalysisRun.getElementID());
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();

		this.disconnect(false);

		if (results.getRow() == 0)
			return;
		while (results.next()) {
			final int analysisRunID = results.getInt(0);
			final String name = results.getString(1);
			final String publicationDate = results.getString(2);
			final SimpleDateFormat formatter = new SimpleDateFormat(
			        OmegaConstants.OMEGA_DATE_FORMAT);
			final Date timeStamps = formatter.parse(publicationDate);
			final int experimenterID = results.getInt(3);
			final int algoSpecID = results.getInt(5);
			final OmegaExperimenter owner = this
			        .loadExperimenter(experimenterID);
			final OmegaAlgorithmSpecification algoSpec = this
			        .loadAlgorithmSpecification(algoSpecID);
			final List<OmegaTrajectory> resultingTrajectory = this
			        .loadTrajectories(analysisRunID,
			                (OmegaParticleDetectionRun) parentAnalysisRun);

			final OmegaAnalysisRun analysisRun = new OmegaParticleLinkingRun(
			        owner, algoSpec, timeStamps, name, resultingTrajectory);
			analysisRun.setElementID((long) analysisRunID);
			// this.loadLinkingAnalysisRun(analysisRun);
			parentAnalysisRun.addAnalysisRun(analysisRun);
		}
	}

	public int saveAnalysisRun(final OmegaImage image,
	        final OmegaAnalysisRun analysisRun) throws SQLException,
	        ClassNotFoundException {
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
		final int analysisID = this
		        .saveDetectionAnalysisRun((OmegaParticleDetectionRun) analysisRun);
		this.saveElementAnalysisConnection(imageID, "image", analysisID);
		return analysisID;
	}

	public int saveAnalysisRun(final int parentAnalysisRunID,
	        final OmegaAnalysisRun analysisRun) throws SQLException,
	        ClassNotFoundException {
		int analysisID = -1;
		if (analysisRun instanceof OmegaParticleLinkingRun) {
			analysisID = this.saveLinkingAnalysisRun(
			        (OmegaParticleLinkingRun) analysisRun, parentAnalysisRunID);
		}
		return analysisID;
	}

	private void saveElementAnalysisConnection(final int elementID,
	        final String elementType, final int analysisID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO analysis_run_map (AnalysisRun_Seq_Id, OmegaElement_Seq_Id, OmegaElement_Type) VALUES (");
		query.append(analysisID);
		query.append(",");
		query.append(elementID);
		query.append(",");
		query.append(elementType);
		query.append(")");
		this.connection.prepareStatement(query.toString()).executeUpdate();
	}

	private int saveAnalysisRun(final OmegaAnalysisRun analysisRun,
	        final int experimenterID) throws SQLException,
	        ClassNotFoundException {
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
	        throws SQLException, ClassNotFoundException {
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

	private int saveDetectionAnalysisRun(
	        final OmegaParticleDetectionRun particlesDetectionRun)
	        throws SQLException, ClassNotFoundException {
		final OmegaExperimenter experimenter = particlesDetectionRun
		        .getExperimenter();
		final int experimenterID = this.getOrSaveExperimenter(experimenter);
		final int analysisRunID = this.saveAnalysisRun(particlesDetectionRun,
		        experimenterID);
		particlesDetectionRun.setElementID((long) analysisRunID);

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
	        throws SQLException, ClassNotFoundException {
		if (!this.isConnected()) {
			this.connect();
		}

		final Map<OmegaFrame, List<OmegaROI>> particlesMap = new LinkedHashMap<OmegaFrame, List<OmegaROI>>();
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM particle WHERE AnalysisRun_Seq_Id = ");
		query.append(analysisRunID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();

		this.disconnect(false);

		if ((results.getRow() == 0))
			// TODO gestire errore
			return particlesMap;
		while (results.next()) {
			final int roiID = results.getInt(1);
			final double intensity = results.getDouble(3);
			final double probability = results.getDouble(4);

			if (!this.isConnected()) {
				this.connect();
			}

			final StringBuffer query2 = new StringBuffer();
			query2.append("SELECT * FROM roi WHERE ROI_Seq_Id = ");
			query2.append(roiID);
			final ResultSet results2 = this.connection.prepareStatement(
			        query.toString()).executeQuery();

			this.disconnect(false);

			if ((results2.getRow() == 0) || (results2.getRow() > 1)) {
				// TODO Throw error
				continue;
			}
			results2.next();
			final int frameIndex = results2.getInt(1);
			final double x = results2.getDouble(2);
			final double y = results2.getDouble(3);

			final OmegaROI particle = new OmegaParticle(frameIndex, x, y,
			        intensity, probability);
			particle.setElementID((long) roiID);
		}
		return particlesMap;
	}

	private int saveParticle(final OmegaROI roi, final int frameID,
	        final int analysisRunID) throws SQLException,
	        ClassNotFoundException {
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

	private void updateParticle(final OmegaROI roi, final int trajectoryID)
	        throws SQLException, ClassNotFoundException {
		// final StringBuffer query1 = new StringBuffer();
		// query1.append("SELECT * FROM particle WHERE ROI_Seq_Id = '");
		// query1.append(roi.getElementID());
		// query1.append("' FOR UPDATE");
		// this.connection.prepareStatement(query1.toString()).executeUpdate();
		if (!this.isConnected()) {
			this.connect();
		}

		final StringBuffer query = new StringBuffer();
		query.append("UPDATE particle SET Trajectory_Seq_Id = ");
		query.append(trajectoryID);
		query.append(" WHERE ROI_Seq_Id = ");
		query.append(roi.getElementID());
		this.connection.prepareStatement(query.toString()).executeUpdate();

		this.disconnect(false);

		System.gc();
	}

	private int saveLinkingAnalysisRun(
	        final OmegaParticleLinkingRun trajectoriesDetectionRun,
	        final int parentAnalysisRunID) throws SQLException,
	        ClassNotFoundException {
		final OmegaExperimenter experimenter = trajectoriesDetectionRun
		        .getExperimenter();
		final int experimenterID = this.getOrSaveExperimenter(experimenter);
		final int analysisRunID = this.saveAnalysisRun(
		        trajectoriesDetectionRun, experimenterID, parentAnalysisRunID);
		trajectoriesDetectionRun.setElementID((long) analysisRunID);

		final Long startingTime = System.currentTimeMillis();
		final List<Thread> threads = new ArrayList<Thread>();
		int counter = 0;
		for (final OmegaTrajectory trajectory : trajectoriesDetectionRun
		        .getResultingTrajectories()) {
			final Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					int trajectoryID = -1;
					try {
						trajectoryID = MySqlGateway_bk.this.saveTrajectory(
						        trajectory, analysisRunID);
					} catch (final SQLException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					} catch (final ClassNotFoundException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					if (trajectoryID == -1)
						return;
					for (final OmegaROI roi : trajectory.getROIs()) {
						try {
							MySqlGateway_bk.this.updateParticle(roi,
							        trajectoryID);
						} catch (final SQLException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						} catch (final ClassNotFoundException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}
					}
					trajectory.setElementID((long) trajectoryID);
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
				} catch (final InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		final Long totalTime = System.currentTimeMillis() - startingTime;
		System.out.println("TotalTime: " + (totalTime / 1000));

		return analysisRunID;
	}

	private List<OmegaTrajectory> loadTrajectories(final int analysisRunID,
	        final OmegaParticleDetectionRun parentAnalysisRun)
	        throws SQLException {
		final List<OmegaTrajectory> trajectories = new ArrayList<OmegaTrajectory>();
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM trajectory WHERE AnalysisRun_Seq_Id = ");
		query.append(analysisRunID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if ((results.getRow() == 0))
			return trajectories;
		while (results.next()) {
			final int trajectoryID = results.getInt(0);
			final int length = results.getInt(2);
			final OmegaTrajectory trajectory = new OmegaTrajectory(length);
			for (final OmegaFrame frame : parentAnalysisRun
			        .getResultingParticles().keySet()) {
				for (final OmegaROI roi : parentAnalysisRun
				        .getResultingParticles().get(frame)) {
					final OmegaParticle particle = (OmegaParticle) roi;
					final StringBuffer query2 = new StringBuffer();
					query2.append("SELECT * FROM particle WHERE ROI_Seq_Id = ");
					query2.append(particle.getElementID());
					final ResultSet results2 = this.connection
					        .prepareStatement(query.toString()).executeQuery();
					if ((results2.getRow() == 0) || (results2.getRow() > 1)) {
						// TODO manage error
						continue;
					}
					results2.next();
					if (results2.getInt(3) == trajectoryID) {
						trajectory.addROI(roi);
					}
				}
			}
		}
		return trajectories;
	}

	private int saveTrajectory(final OmegaTrajectory trajectory,
	        final int analysisRunID) throws SQLException,
	        ClassNotFoundException {
		final StringBuffer query = new StringBuffer();
		query.append("INSERT INTO trajectory (AnalysisRun_Seq_Id, NumberOfPoints) VALUES (");
		query.append(analysisRunID);
		query.append(",");
		query.append(trajectory.getLength());
		query.append(")");
		final int id = this.insertAndGetId(query.toString());
		return id;
	}

	private OmegaAlgorithmSpecification loadAlgorithmSpecification(
	        final int algoSpecID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM algorithm_specification WHERE AlgorithmSpecification_Seq_Id = ");
		query.append(algoSpecID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if ((results.getRow() == 0) || (results.getRow() > 1))
			// TODO gestire errore
			return null;
		results.next();
		final int algoInfoID = results.getInt(1);
		final OmegaAlgorithmInformation algoInfo = this
		        .loadAlgorithmInformation(algoInfoID);
		final OmegaAlgorithmSpecification algoSpec = new OmegaAlgorithmSpecification(
		        algoInfo);
		algoSpec.setElementID((long) algoSpecID);
		return algoSpec;
	}

	private OmegaAlgorithmInformation loadAlgorithmInformation(
	        final int algoInfoID) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM algorithm_information WHERE AlgorithmInformation_Seq_Id = ");
		query.append(algoInfoID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if ((results.getRow() == 0) || (results.getRow() > 1))
			// TODO gestire errore
			return null;
		results.next();
		final int personID = results.getInt(1);
		final OmegaPerson author = this.loadPerson(personID);
		final String name = results.getString(2);
		final double version = results.getDouble(3);
		final String description = results.getString(4);
		results.getString(5);
		final OmegaAlgorithmInformation algoInfo = new OmegaAlgorithmInformation(
		        name, version, description, author);
		algoInfo.setElementID((long) algoInfoID);
		return algoInfo;
	}

	private int saveAlgorithmSpecANDInfo(final OmegaAnalysisRun analysisRun)
	        throws SQLException, ClassNotFoundException {
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
		if (results.getRow() == 0)
			return -1;
		final int dbID = results.getInt(0);
		return dbID;
	}

	private int saveAlgorithmSpecification(
	        final OmegaAlgorithmSpecification algoSpec, final int infoID)
	        throws SQLException, ClassNotFoundException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("INSERT INTO algorithm_specification (AlgorithmInformation_Seq_Id) VALUES (");
		query1.append(algoSpec.getAlgorithmInfo().getElementID());
		query1.append(")");
		final int id = this.insertAndGetId(query1.toString());
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
		if (results.getRow() == 0)
			return -1;
		final int dbID = results.getInt(0);
		return dbID;
	}

	private int saveAlgorithmInformation(
	        final OmegaAlgorithmInformation algoInfo) throws SQLException,
	        ClassNotFoundException {
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

	private int saveProject(final OmegaProject project) throws SQLException,
	        ClassNotFoundException {
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
	        throws SQLException, ClassNotFoundException {
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
	        final int experimenterID) throws SQLException,
	        ClassNotFoundException {
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

	private int saveImage(final OmegaImage image) throws SQLException,
	        ClassNotFoundException {
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
	        ParseException, ClassNotFoundException {
		final int imageID = this.getDBIdFROMOmegaElementId("image",
		        image.getElementID());
		this.loadFrames(image);
		this.loadDetectionAnalysisRun(image, imageID);
	}

	private int saveImagePixels(final OmegaImagePixels pixels, final int imageID)
	        throws SQLException, ClassNotFoundException {
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
	        throws SQLException, ClassNotFoundException {
		final List<OmegaFrame> frameList = new ArrayList<OmegaFrame>();
		final int pixelsID = this.getDBIdFROMOmegaElementId("pixels", image
		        .getDefaultPixels().getElementID());
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM frame WHERE Pixel_Seq_Id = ");
		query.append(pixelsID);
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (results.getRow() == 0)
			return frameList;
		while (results.next()) {
			final int id = results.getInt(0);
			final int index = results.getInt(3);
			final int channel = results.getInt(4);
			final int zPlane = results.getInt(5);
			final OmegaFrame f = new OmegaFrame((long) id, index, channel,
			        zPlane);
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
	        throws SQLException, ClassNotFoundException {
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
	        throws SQLException, ClassNotFoundException {
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
		if ((results.getRow() == 0) || (results.getRow() > 1))
			// TODO throw error here
			return null;
		results.next();
		final int personID = results.getInt(1);
		final int omeroID = results.getInt(2);
		final StringBuffer query2 = new StringBuffer();
		query2.append("SELECT * FROM person WHERE Person_Seq_Id = ");
		query2.append(personID);
		final ResultSet results2 = this.connection.prepareStatement(
		        query2.toString()).executeQuery();
		if ((results2.getRow() == 0) || (results2.getRow() > 1))
			// TODO throw error here
			return null;
		results2.next();
		final String firstName = results2.getString(1);
		final String lastName = results2.getString(2);
		final OmegaExperimenter exp = new OmegaExperimenter(omeroID, firstName,
		        lastName);
		exp.setElementID((long) experimenterID);
		return exp;
	}

	private int saveExperimenter(final OmegaExperimenter experimenter)
	        throws SQLException, ClassNotFoundException {
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
		final StringBuffer query1 = new StringBuffer();
		query1.append("SELECT * FROM person WHERE Person_Seq_Id = ");
		query1.append(personID);
		final ResultSet results = this.connection.prepareStatement(
		        query1.toString()).executeQuery();
		if ((results.getRow() == 0) || (results.getRow() > 1))
			// TODO throw error here
			return null;
		results.next();
		final String firstName = results.getString(1);
		final String lastName = results.getString(2);
		final OmegaPerson person = new OmegaPerson(firstName, lastName);
		person.setElementID((long) personID);
		return person;
	}

	private int getOrSavePerson(final OmegaPerson person) throws SQLException,
	        ClassNotFoundException {
		int personID = this.getPerson(person);
		if (personID == -1) {
			personID = this.savePerson(person);
			person.setElementID((long) personID);
		}
		return personID;
	}

	private int getPerson(final OmegaPerson person) throws SQLException {
		final StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM person WHERE First_name = '");
		query.append(person.getFirstName());
		query.append("' AND Last_name = '");
		query.append(person.getLastName());
		query.append("'");
		final ResultSet results = this.connection.prepareStatement(
		        query.toString()).executeQuery();
		if (results.getRow() == 0)
			return -1;
		final int dbID = results.getInt(0);
		return dbID;
	}

	private int savePerson(final OmegaPerson person) throws SQLException,
	        ClassNotFoundException {
		final StringBuffer query1 = new StringBuffer();
		query1.append("INSERT INTO person (First_name, Last_name) VALUES ('");
		query1.append(person.getFirstName());
		query1.append("','");
		query1.append(person.getLastName());
		query1.append("')");
		final int personID = this.insertAndGetId(query1.toString());
		return personID;
	}
}
