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

/**
 * @author Vanni Galli
 */
public class OmegaLoginCredentials {

	/** The name of the user. */
	private final String userName;

	/** The password of the user. */
	private final String password;

	/**
	 * Creates a new instance.
	 * 
	 * @param userName
	 *            The user name.
	 * @param password
	 *            The password.
	 * @param hostname
	 *            The name of the server.
	 */
	public OmegaLoginCredentials(final String username, final String password) {
		this.userName = username;
		this.password = password;
	}

	/**
	 * Returns the name of the user.
	 * 
	 * @return See above.
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * Returns the address of the server
	 * 
	 * @return See above.
	 */
	public String getPassword() {
		return this.password;
	}
}
