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
package edu.umassmed.omega.data.imageDBConnectionElements;

public class OmegaServerInformation {

	/** The address of the server. */
	private final String hostName;

	/** The default port value. */
	public static final int DEFAULT_PORT = 4064;

	/** The port. */
	private int port;

	public OmegaServerInformation(final String hostName) {
		this(hostName, OmegaServerInformation.DEFAULT_PORT);
	}

	public OmegaServerInformation(final String hostName, final int port) {
		this.hostName = hostName;
		this.port = port;
	}

	/**
	 * Sets the port.
	 * 
	 * @param port
	 *            The value to set.
	 */
	public void setPort(final int port) {
		this.port = port;
	}

	/**
	 * Returns the port.
	 * 
	 * @return See above.
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Returns the address of the server
	 * 
	 * @return See above.
	 */
	public String getHostName() {
		return this.hostName;
	}
}
