package edu.umassmed.omega.core.runnables;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import edu.umassmed.omega.core.OmegaApplication;
import edu.umassmed.omega.core.OmegaMySqlGateway;
import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaProject;

public class OmegaDBLoader extends OmegaDBRunnable {

	private final List<OmegaProject> projectsToLoad;

	public OmegaDBLoader(final OmegaApplication omegaApp,
	        final OmegaMySqlGateway gateway, final GenericMessageDialog dialog,
	        final List<OmegaProject> projects) {
		super(omegaApp, gateway, dialog);
		this.projectsToLoad = projects;
	}

	@Override
	public void run() {
		final int projectsSize = this.projectsToLoad.size();
		int projectLoaded = 0;
		int datasetLoaded = 0;
		int imageLoaded = 0;
		for (final OmegaProject project : this.projectsToLoad) {
			// Load project
			projectLoaded++;
			String msg = "Loading project(s): " + projectLoaded + "/"
			        + projectsSize;
			final int datasetsSize = project.getDatasets().size();
			for (final OmegaDataset dataset : project.getDatasets()) {
				// Load dataset
				datasetLoaded++;
				final int imagesSize = dataset.getImages().size();
				msg += ", dataset(s): " + datasetLoaded + "/" + datasetsSize;
				for (final OmegaImage image : dataset.getImages()) {
					imageLoaded++;
					msg += ", image(s): " + imageLoaded + "/" + imagesSize;
					this.updateMessage(msg);
					try {
						this.getGateway().loadImages(image);
					} catch (final SQLException ex) {
						ex.printStackTrace();
					} catch (final ParseException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		this.notifyProcessEndToApplication();
	}
}
