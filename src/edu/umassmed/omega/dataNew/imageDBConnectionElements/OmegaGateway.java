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
package edu.umassmed.omega.dataNew.imageDBConnectionElements;


public abstract class OmegaGateway {

	/** Flag indicating if you are connected or not. */
	private boolean isConnected;

	public OmegaGateway() {
		this.isConnected = false;
	}

	public abstract boolean connect(final OmegaLoginCredentials loginCred,
	        final OmegaServerInformation serverInfo);

	public abstract byte[] getImageData(final Long pixelsID, final int z,
	        final int t, final int c);

	public boolean isConnected() {
		return this.isConnected;
	}

	public void setConnected(final boolean isConnected) {
		this.isConnected = isConnected;
	}

	public abstract int getDefaultZ(final Long pixelsID);

	public abstract void setDefaultZ(final Long pixelsID, int z);

	public abstract int getDefaultT(final Long pixelsID);

	public abstract void setDefaultT(final Long pixelsID, int t);

	public abstract int[] renderAsPackedInt(final Long pixelsID);

	public abstract byte[] renderCompressed(final Long pixelsID);

	public abstract void setActiveChannel(final Long pixelsID, int channel,
	        boolean active);

	public abstract Double computeSizeT(final Long pixelsID, int pixelSizeT,
	        int currentMaxT);

	public abstract void setCompressionLevel(final Long pixelsID,
	        float compression);

	public abstract double getTotalT(final Long pixelsID, final int z,
	        final int t, final int c);

	public abstract int getByteWidth(Long pixelsID);
}
