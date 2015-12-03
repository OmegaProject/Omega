package main.java.edu.umassmed.omega.core.runnables;

import java.sql.SQLException;
import java.util.List;

import main.java.edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import main.java.edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleLinkingRun;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaDataset;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaProject;
import main.java.edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import main.java.edu.umassmed.omega.core.OmegaApplication;
import main.java.edu.umassmed.omega.core.OmegaMySqlGateway;

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
			final StringBuffer buf = new StringBuffer();
			buf.append("<html>Updating progress");
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
					for (final OmegaAnalysisRun analysisRun : image
							.getAnalysisRuns()) {
						try {
							// this.updateTrajectoriesManagerRun(analysisRun);
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

	// private void updateTrajectoriesManagerRun(final OmegaAnalysisRun
	// analysisRun)
	// throws SQLException {
	// if (analysisRun instanceof OmegaTrajectoriesRelinkingRun) {
	// this.getGateway().updateTrajectoriesRelinkingRun(
	// (OmegaTrajectoriesRelinkingRun) analysisRun);
	// }
	// for (final OmegaAnalysisRun innerAnalysisRun : analysisRun
	// .getAnalysisRuns()) {
	// this.updateTrajectoriesManagerRun(innerAnalysisRun);
	// }
	// }
}
