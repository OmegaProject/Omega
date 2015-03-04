package edu.umassmed.omega.core.runnables;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import edu.umassmed.omega.core.OmegaApplication;
import edu.umassmed.omega.core.OmegaMySqlGateway;
import edu.umassmed.omega.data.coreElements.OmegaDataset;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaProject;

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
			final StringBuffer buf = new StringBuffer();
			buf.append("<html>Loading progress");
			buf.append("<br>project(s): ");
			buf.append(projectLoaded);
			buf.append(" of ");
			buf.append(projectsSize);
			final int datasetsSize = project.getDatasets().size();
			datasetLoaded = 0;
			for (final OmegaDataset dataset : project.getDatasets()) {
				// Load dataset
				datasetLoaded++;
				final int imagesSize = dataset.getImages().size();
				buf.append("<br>dataset(s): ");
				buf.append(datasetLoaded);
				buf.append(" of ");
				buf.append(datasetsSize);
				imageLoaded = 0;
				for (final OmegaImage image : dataset.getImages()) {
					imageLoaded++;
					buf.append("<br>image(s): ");
					buf.append(imageLoaded);
					buf.append(" of ");
					buf.append(imagesSize);
					buf.append("</html>");
					this.updateMessage(buf.toString());
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
