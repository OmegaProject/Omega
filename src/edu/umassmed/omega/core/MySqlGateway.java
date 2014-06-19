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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;

public class MySqlGateway {

	private static String user = "omega";
	private static String psw = "1234";
	private static String db = "146.189.76.56:3306/omega";

	private Connection connection;
	private final Statement statement;

	private final ResultSet resultSet;

	public MySqlGateway() {
		this.connection = null;
		this.statement = null;
		this.resultSet = null;
	}

	public void connect() throws ClassNotFoundException, SQLException {
		if (this.connection != null)
			// Throw eccezione
			return;
		Class.forName("com.mysql.jdbc.Driver");

		this.connection = DriverManager.getConnection("jdbc:mysql://"
		        + MySqlGateway.db + "?" + "user=" + MySqlGateway.user
		        + "r&password=" + MySqlGateway.psw);
	}

	public void disconnect() throws SQLException {
		this.connection.close();
		this.connection = null;
	}

	public void saveAnalysisRun(final OmegaImage image,
	        final OmegaAnalysisRun analysisRun) throws SQLException {
		this.saveExperimenter(analysisRun.getExperimenter());
		this.saveImage(image);
		this.saveAnalysisRun(analysisRun);
	}

	private void saveAnalysisRun(final OmegaAnalysisRun analysisRun) {

	}

	private void saveImage(final OmegaImage image) throws SQLException {
		final PreparedStatement preparedStatement = this.connection
		        .prepareStatement("insert into IMAGE(bla bla bla) values (?, ?, ?, ?, ?, ?)");
		// Adding the columns name next to the table put the corrispetive value
		// in that column!
		preparedStatement.setLong(1, image.getElementID());
		preparedStatement.setString(2, image.getName());
		preparedStatement.setInt(3, -1);
		preparedStatement.setInt(4, -1);
		preparedStatement.setInt(5, -1);
		preparedStatement.setInt(6, -1);
		// Datasets id ...must be a list
		// preparedStatement.setInt(7, image.getParentDatasets().);

		preparedStatement.executeUpdate();
	}

	private void saveExperimenter(final OmegaExperimenter experimenter)
	        throws SQLException {
		final PreparedStatement preparedStatement = this.connection
		        .prepareStatement("insert into EXPERIMENTER values (?, ?, ?)");
		preparedStatement.setLong(1, experimenter.getElementID());
		preparedStatement.setString(2, experimenter.getFirstName());
		preparedStatement.setString(3, experimenter.getLastName());

		preparedStatement.executeUpdate();
	}
}
