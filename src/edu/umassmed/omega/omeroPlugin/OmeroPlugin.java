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
package edu.umassmed.omega.omeroPlugin;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventGateway;
import edu.umassmed.omega.commons.exceptions.OmegaCoreExceptionPluginMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.plugins.OmegaLoaderPlugin;
import edu.umassmed.omega.data.OmegaData;
import edu.umassmed.omega.omeroPlugin.gui.OmeroPluginPanel;

public class OmeroPlugin extends OmegaLoaderPlugin {

	private OmeroPluginPanel panel;

	public OmeroPlugin() {
		super(1);
		this.setGateway(new OmeroGateway());
		this.panel = null;
	}

	@Override
	public String getName() {
		return "OMERO image browser";
	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) throws OmegaCoreExceptionPluginMissingData {

		this.fireEvent(new OmegaPluginEventGateway(this,
		        OmegaPluginEventGateway.STATUS_CREATED));

		final OmegaData omegaData = this.getMainData();
		if (omegaData == null)
			throw new OmegaCoreExceptionPluginMissingData(this);

		this.panel = new OmeroPluginPanel(parent, this,
		        (OmeroGateway) this.getGateway(), omegaData, index);

		return this.panel;
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getDescription() {
		return "Default OMEGA image browser";
	}
}
