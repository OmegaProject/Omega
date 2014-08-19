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
package edu.umassmed.omega.commons.genericPlugins;

import edu.umassmed.omega.commons.genericInterfaces.OmegaLoaderPluginInterface;
import edu.umassmed.omega.commons.genericInterfaces.OmegaMainDataConsumerPluginInterface;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;

public abstract class OmegaLoaderPlugin extends OmegaPlugin implements
        OmegaLoaderPluginInterface, OmegaMainDataConsumerPluginInterface {

	private OmegaGateway gateway;
	private OmegaData omegaData;

	public OmegaLoaderPlugin() {
		this(1);
	}

	public OmegaLoaderPlugin(final int numOfPanels) {
		super(numOfPanels);

		this.gateway = null;
		this.omegaData = null;
	}

	@Override
	public OmegaGateway getGateway() {
		return this.gateway;
	}

	@Override
	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public void setMainData(final OmegaData omegaData) {
		this.omegaData = omegaData;
	}

	@Override
	public OmegaData getMainData() {
		return this.omegaData;
	}
}
