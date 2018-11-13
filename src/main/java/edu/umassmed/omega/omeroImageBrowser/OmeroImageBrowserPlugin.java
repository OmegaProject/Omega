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
package edu.umassmed.omega.omeroImageBrowser;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.data.OmegaData;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventGateway;
import edu.umassmed.omega.commons.exceptions.OmegaCoreExceptionPluginMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.pluginArchetypes.OmegaLoaderPluginArchetype;
import edu.umassmed.omega.omero.commons.OmeroGateway;
import edu.umassmed.omega.omeroImageBrowser.gui.OmeroImageBrowserPluginPanel;

public class OmeroImageBrowserPlugin extends OmegaLoaderPluginArchetype {

	private OmeroImageBrowserPluginPanel panel;

	public OmeroImageBrowserPlugin() {
		super(1);
		this.setGateway(new OmeroGateway());
		this.panel = null;
	}

	@Override
	public String getName() {
		return OmeroImageBrowserPluginConstants.PLUGIN_NAME;
	}

	@Override
	public String getShortName() {
		return OmeroImageBrowserPluginConstants.PLUGIN_SNAME;
	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) throws OmegaCoreExceptionPluginMissingData {

		this.fireEvent(new OmegaPluginEventGateway(this,
		        OmegaPluginEventGateway.STATUS_CREATED));

		final OmegaData omegaData = this.getMainData();
		if (omegaData == null)
			throw new OmegaCoreExceptionPluginMissingData(this);

		this.panel = new OmeroImageBrowserPluginPanel(parent, this,
		        (OmeroGateway) this.getGateway(), omegaData, index);

		return this.panel;
	}

	@Override
	public void run() {
		//
	}

	@Override
	public void setGateway(final OmegaGateway gateway) {
		super.setGateway(gateway);
		for (final GenericPluginPanel panel : this.getPanels()) {
			((OmeroImageBrowserPluginPanel) panel).setGateway((OmeroGateway) gateway);
		}
	}

	@Override
	public String getDescription() {
		return OmeroImageBrowserPluginConstants.PLUGIN_DESC;
	}
}
