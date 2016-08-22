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
package edu.umassmed.omega.core.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaDBServerInformation;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaLoginCredentials;

public class OmegaMySqlGateway {

	public static String USER = "omega";
	public static String PSW = "0m3g4";
	public static String HOSTNAME = "deduve.umassmed.edu";
	public static String PORT = "3306";
	public static String DB_NAME = "omega";

	protected Connection connection;
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
}
