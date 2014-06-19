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
package edu.umassmed.omega.omeroPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import omero.ServerError;
import pojos.DatasetData;
import pojos.ExperimenterData;
import pojos.ProjectData;
import edu.umassmed.omega.omeroPlugin.OmeroGateway;
import edu.umassmed.omega.omeroPlugin.gui.OmeroListPanel;

public class OmeroListPanelProjectAndDatasetLoader implements Runnable {

	private final OmeroListPanel listPanel;
	private final OmeroGateway gateway;
	private final ExperimenterData expData;

	private int projectLoaded;
	private int projectToLoad;

	private final Map<ProjectData, List<DatasetData>> datas;

	public OmeroListPanelProjectAndDatasetLoader(
	        final OmeroListPanel listPanel, final OmeroGateway gateway,
	        final ExperimenterData expData) {
		this.listPanel = listPanel;
		this.gateway = gateway;
		this.expData = expData;

		this.datas = new HashMap<ProjectData, List<DatasetData>>();

		this.projectToLoad = 0;
		this.projectLoaded = 0;
	}

	@Override
	public void run() {
		try {
			final List<ProjectData> projects = this.gateway
			        .getProjects(this.expData);
			this.projectToLoad = projects.size();
			for (final ProjectData proj : projects) {
				final int currentlyLoading = 1 + this.projectLoaded;
				this.updateLoadingStatus(currentlyLoading);
				final List<DatasetData> datasets = this.gateway
				        .getDatasets(proj);
				this.datas.put(proj, datasets);
				this.projectLoaded++;
			}
		} catch (final ServerError e) {
			e.printStackTrace();
		}

		this.updateLoadingStatus(this.projectLoaded);
	}

	private void updateLoadingStatus(final int currentlyLoading) {
		final String loadingStatus = currentlyLoading + "/"
		        + this.projectToLoad + "...loaded \n project(s) for "
		        + this.expData.getFirstName() + " "
		        + this.expData.getLastName();
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					OmeroListPanelProjectAndDatasetLoader.this.listPanel
					        .updateLoadingStatus(loadingStatus);
					if (OmeroListPanelProjectAndDatasetLoader.this.projectLoaded == OmeroListPanelProjectAndDatasetLoader.this.projectToLoad) {
						OmeroListPanelProjectAndDatasetLoader.this.listPanel
						        .updateOmeData(
						                OmeroListPanelProjectAndDatasetLoader.this.expData,
						                OmeroListPanelProjectAndDatasetLoader.this.datas);
					}
				}
			});
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}
