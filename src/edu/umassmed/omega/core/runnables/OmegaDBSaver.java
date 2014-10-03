package edu.umassmed.omega.core.runnables;

import java.sql.SQLException;
import java.util.List;

import edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import edu.umassmed.omega.core.OmegaApplication;
import edu.umassmed.omega.core.OmegaMySqlGateway;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaProject;

public class OmegaDBSaver extends OmegaDBWriter {

	private final List<OmegaProject> projectsToSave;

	public OmegaDBSaver(final OmegaApplication omegaApp,
	        final OmegaMySqlGateway gateway, final GenericMessageDialog dialog,
	        final List<OmegaProject> projects) {
		super(omegaApp, gateway, dialog);
		this.projectsToSave = projects;
	}

	@Override
	public void run() {
		final int projectsSize = this.projectsToSave.size();
		int projectLoaded = 0;
		int datasetLoaded = 0;
		int imageLoaded = 0;

		for (final OmegaProject project : this.projectsToSave) {
			// Load project
			projectLoaded++;
			String msg = "Saving project(s): " + projectLoaded + "/"
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
					for (final OmegaAnalysisRun analysisRun : image
					        .getAnalysisRuns()) {
						try {
							this.getGateway().saveAnalysisRun(image,
							        analysisRun);
							this.saveInnerAnalysis(analysisRun);
						} catch (final SQLException ex) {
							this.setErrorOccured();
							ex.printStackTrace();
						}
					}
				}
			}
		}
		this.notifyProcessEndToApplication();
	}

	private void saveInnerAnalysis(final OmegaAnalysisRun analysisRun)
	        throws SQLException {
		for (final OmegaAnalysisRun innerAnalysisRun : analysisRun
		        .getAnalysisRuns()) {
			final Integer id = new Integer(analysisRun.getElementID()
			        .toString());
			this.getGateway().saveAnalysisRun(id, innerAnalysisRun);
			this.saveInnerAnalysis(innerAnalysisRun);
		}
	}

}
