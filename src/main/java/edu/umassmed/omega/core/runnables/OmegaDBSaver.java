package main.java.edu.umassmed.omega.core.runnables;

import java.sql.SQLException;
import java.util.List;

import main.java.edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaDataset;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaProject;
import main.java.edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import main.java.edu.umassmed.omega.core.OmegaApplication;
import main.java.edu.umassmed.omega.core.OmegaMySqlGateway;

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
			final StringBuffer buf = new StringBuffer();
			buf.append("<html>Saving progress");
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
