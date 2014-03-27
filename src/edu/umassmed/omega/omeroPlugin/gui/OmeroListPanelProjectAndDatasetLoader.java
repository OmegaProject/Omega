package edu.umassmed.omega.omeroPlugin.gui;

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

	public void updateLoadingStatus(final int currentlyLoading) {
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
