package edu.umassmed.omega.core.runnables;

import java.sql.SQLException;
import java.util.List;

import edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import edu.umassmed.omega.core.OmegaApplication;
import edu.umassmed.omega.core.OmegaMySqlGateway;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaTrajectoriesManagerRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaProject;

public class OmegaDBUpdater extends OmegaDBWriter {

	private final List<OmegaProject> projectsToUpdate;

	public OmegaDBUpdater(final OmegaApplication omegaApp,
	        final OmegaMySqlGateway gateway, final GenericMessageDialog dialog,
	        final List<OmegaProject> projects) {
		super(omegaApp, gateway, dialog);
		this.projectsToUpdate = projects;
	}

	@Override
	public void run() {
		final int projectsSize = this.projectsToUpdate.size();
		int projectLoaded = 0;
		int datasetLoaded = 0;
		int imageLoaded = 0;
		for (final OmegaProject project : this.projectsToUpdate) {
			// Load project
			projectLoaded++;
			String msg = "Updating project(s): " + projectLoaded + "/"
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
							this.updateTrajectoriesManagerRun(analysisRun);
							this.updateTrajectories(analysisRun);
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

	private void updateTrajectories(final OmegaAnalysisRun analysisRun)
	        throws SQLException {
		if (analysisRun instanceof OmegaParticleLinkingRun) {
			this.getGateway().updateTrajectories(
			        (OmegaParticleLinkingRun) analysisRun);
		}
		for (final OmegaAnalysisRun innerAnalysisRun : analysisRun
		        .getAnalysisRuns()) {
			this.updateTrajectories(innerAnalysisRun);
		}
	}

	private void updateTrajectoriesManagerRun(final OmegaAnalysisRun analysisRun)
	        throws SQLException {
		if (analysisRun instanceof OmegaTrajectoriesManagerRun) {
			this.getGateway().updateTrajectoriesManagerRun(
			        (OmegaTrajectoriesManagerRun) analysisRun);
		}
		for (final OmegaAnalysisRun innerAnalysisRun : analysisRun
		        .getAnalysisRuns()) {
			this.updateTrajectoriesManagerRun(innerAnalysisRun);
		}
	}
}
