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
package edu.umassmed.omega.commons.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.RootPaneContainer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.utilities.OmegaAnalysisRunContainerUtilities;
import edu.umassmed.omega.commons.utilities.OmegaStringUtilities;
import edu.umassmed.omega.data.analysisRunElements.OmegaAlgorithmInformation;
import edu.umassmed.omega.data.analysisRunElements.OmegaAlgorithmSpecification;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaSNRRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrackingMeasuresRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.data.coreElements.OmegaDataset;
import edu.umassmed.omega.data.coreElements.OmegaFrame;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.data.coreElements.OmegaProject;

public class GenericAnalysisInformationPanel extends GenericScrollPane {

	private static final long serialVersionUID = -8599077833612345455L;

	private JTextPane info_txt;

	private final SimpleAttributeSet normal, bold;

	private JButton algoDetails_btt;

	private final GenericAlgorithmDetailsDialog algoInfoDialog;
	private OmegaAlgorithmInformation algoInfo;

	public GenericAnalysisInformationPanel(final RootPaneContainer parent) {
		super(parent);

		this.normal = new SimpleAttributeSet();
		this.bold = new SimpleAttributeSet();
		StyleConstants.setBold(this.bold, true);

		// this.setBorder(new TitledBorder("Information"));

		this.algoInfoDialog = new GenericAlgorithmDetailsDialog(parent);

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		this.info_txt = new JTextPane();
		this.info_txt.setEditable(false);
		this.info_txt.setEditorKit(new GenericWrapEditorKit());
		this.info_txt.setBackground(this.getBackground());
		try {
			this.appendString(OmegaGUIConstants.SIDEPANEL_NO_DETAILS, this.bold);
		} catch (final BadLocationException ex) {
			OmegaLogFileManager.handleCoreException(ex);
		}
		mainPanel.add(this.info_txt, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		this.algoDetails_btt = new JButton(
		        OmegaGUIConstants.ALGORITHM_INFORMATION);
		this.algoDetails_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.algoDetails_btt.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.algoDetails_btt.setEnabled(false);
		buttonPanel.add(this.algoDetails_btt);

		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		this.setViewportView(mainPanel);
	}

	private void addListeners() {
		this.algoDetails_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GenericAnalysisInformationPanel.this.handleShowAlgoDetails();
			}
		});
	}

	private void handleShowAlgoDetails() {
		this.algoInfoDialog.setVisible(true);
	}

	public void resizePanel(final int width, final int height) {
		final int lines = OmegaStringUtilities.countLines(this.info_txt,
		        this.info_txt.getDocument().getLength());
		int neededHeight = lines * 18;
		final int neededWidth = width - 20;
		final int adjHeight = height - 60;
		// System.out.println(height + " VS " + neededHeight);
		if (adjHeight > neededHeight) {
			neededHeight = adjHeight;
		} else {
			// neededWidth -= 20;
			// neededHeight += 17;
		}

		final Dimension panelDim = new Dimension(width, height);
		this.setPreferredSize(panelDim);
		this.setSize(panelDim);
		final Dimension textDim = new Dimension(neededWidth, neededHeight);
		this.info_txt.setPreferredSize(textDim);
		this.info_txt.setSize(textDim);
	}

	private void appendString(final String s, final AttributeSet style)
	        throws BadLocationException {
		final Document doc = this.info_txt.getDocument();
		final int length = doc.getLength();
		doc.insertString(length, s, style);
	}

	private void appendNewline() throws BadLocationException {
		final Document doc = this.info_txt.getDocument();
		final int length = doc.getLength();
		doc.insertString(length, "\n", this.normal);
	}

	private void reset() throws BadLocationException {
		final Document doc = this.info_txt.getDocument();
		final int length = doc.getLength();
		doc.remove(0, length);
	}

	public void update(final OmegaAnalysisRun analysisRun) {
		this.algoInfoDialog.updateAlgorithmInformation(null);
		this.algoDetails_btt.setEnabled(false);
		// updateDialog(null)
		try {
			this.reset();
			if (analysisRun != null) {
				this.algoDetails_btt.setEnabled(true);
				this.algoInfoDialog.updateAlgorithmInformation(analysisRun
						.getAlgorithmSpec().getAlgorithmInfo());
				// updateDialog(algo)
				this.getGenericAnalysisInformation(analysisRun);
				this.appendNewline();
				this.getSpecificElementInformation(analysisRun);
			} else {
				this.appendString(OmegaGUIConstants.SIDEPANEL_NO_DETAILS,
				        this.bold);
			}
		} catch (final BadLocationException ex) {
			OmegaLogFileManager.handleCoreException(ex);
		}

		this.resizePanel(this.getWidth(), this.getHeight());
		this.info_txt.revalidate();
		this.info_txt.repaint();
	}

	private void getGenericAnalysisInformation(
	        final OmegaAnalysisRun analysisRun) throws BadLocationException {
		final SimpleDateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		final long id = analysisRun.getElementID();
		final String clazz = analysisRun.getClass().getSimpleName()
		        .replace("Omega", "");
		this.appendString(clazz, this.bold);
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_ID, this.bold);
		this.appendString(String.valueOf(id), this.normal);
		this.appendNewline();
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_OWNER, this.bold);
		final String name = analysisRun.getExperimenter().getFirstName() + " "
		        + analysisRun.getExperimenter().getLastName();
		this.appendString(name, this.normal);
		this.appendNewline();
		// this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_NAME, this.bold);
		// this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_NOT_NAMED,
		// this.normal);
		// this.appendNewline();
		this.appendString("Algorithm: ", this.bold);
		final OmegaAlgorithmSpecification algoSpec = analysisRun
		        .getAlgorithmSpec();
		final String algoName = algoSpec.getAlgorithmInfo().getName();
		this.appendString(algoName, this.normal);
		this.appendNewline();
		if (algoSpec.getParameters().size() > 0) {
			this.appendString("Parameters: ", this.bold);
			for (final OmegaParameter param : algoSpec.getParameters()) {
				this.appendNewline();
				this.appendString("-", this.normal);
				this.appendString(param.getName(), this.normal);
				this.appendString(": ", this.normal);
				this.appendString(param.getStringValue(), this.normal);
			}
			this.appendNewline();
		}
		this.appendString(OmegaGUIConstants.INFO_EXECUTED, this.bold);
		final String acquiredDate = format.format(analysisRun.getTimeStamps());
		this.appendString(acquiredDate.replace("_", " "), this.normal);
		this.appendNewline();
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_NUM_ANALYSIS,
		        this.bold);
		this.appendString(String.valueOf(OmegaAnalysisRunContainerUtilities
		        .getAnalysisCount(analysisRun)), this.normal);
	}

	private void getSpecificElementInformation(
			final OmegaAnalysisRun analysisRun) throws BadLocationException {
		if (analysisRun instanceof OmegaSNRRun) {
			this.appendAdditionaSNRInformation((OmegaSNRRun) analysisRun);
		} else if (analysisRun instanceof OmegaTrackingMeasuresRun) {
			this.appendAdditionalTMInformation((OmegaTrackingMeasuresRun) analysisRun);
		} else if (analysisRun instanceof OmegaTrajectoriesSegmentationRun) {
			this.appendAdditionalTSInformation((OmegaTrajectoriesSegmentationRun) analysisRun);
		} else if (analysisRun instanceof OmegaTrajectoriesRelinkingRun) {
			this.appendAdditionalTEInformation((OmegaTrajectoriesRelinkingRun) analysisRun);
		} else if (analysisRun instanceof OmegaParticleLinkingRun) {
			this.appendAdditionalPLInformation((OmegaParticleLinkingRun) analysisRun);
		} else if (analysisRun instanceof OmegaParticleDetectionRun) {
			this.appendAdditionalPDInformation((OmegaParticleDetectionRun) analysisRun);
		}
	}

	private void appendAdditionaSNRInformation(final OmegaSNRRun analysisRun) {

	}

	private void appendAdditionalTMInformation(
			final OmegaTrackingMeasuresRun analysisRun) {

	}

	private void appendAdditionalTSInformation(
			final OmegaTrajectoriesSegmentationRun analysisRun) {

	}

	private void appendAdditionalTEInformation(
			final OmegaTrajectoriesRelinkingRun analysisRun) {

	}

	private void appendAdditionalPLInformation(
			final OmegaParticleLinkingRun analysisRun)
					throws BadLocationException {
		this.appendString("Tracks: ", this.bold);
		final String tracks = String.valueOf(analysisRun
		        .getResultingTrajectories().size());
		this.appendString(tracks, this.normal);
	}

	private void appendAdditionalPDInformation(
	        final OmegaParticleDetectionRun analysisRun)
	        throws BadLocationException {
		this.appendString("Mean spots found per frame: ", this.bold);
		int numP = 0;
		int f = 0;
		for (final OmegaFrame frame : analysisRun.getResultingParticles()
		        .keySet()) {
			numP += analysisRun.getResultingParticles().get(frame).size();
			f++;
		}
		final String mean = String.valueOf(numP / f);
		this.appendString(mean, this.normal);
	}

	private void addAdditionalDetectionInformation(final OmegaProject project)
	        throws BadLocationException {
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_NUM_DATASET,
				this.bold);
		this.appendString(String.valueOf(project.getDatasets().size()),
		        this.normal);
		this.appendNewline();
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_NUM_ANALYSIS,
		        this.bold);
		this.appendString(String.valueOf(OmegaAnalysisRunContainerUtilities
		        .getAnalysisCount(project)), this.normal);
	}

	private void addAdditionalDatasetInformation(final OmegaDataset dataset)
	        throws BadLocationException {
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_NUM_IMAGES,
				this.bold);
		this.appendString(String.valueOf(dataset.getImages().size()),
		        this.normal);
		this.appendNewline();
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_NUM_ANALYSIS,
		        this.bold);
		this.appendString(String.valueOf(OmegaAnalysisRunContainerUtilities
		        .getAnalysisCount(dataset)), this.normal);
	}

	private void addAdditionalImageInformation(final OmegaImage image)
	        throws BadLocationException {
		final SimpleDateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		final OmegaImagePixels pixels = image.getDefaultPixels();
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_NUM_ANALYSIS,
		        this.bold);
		this.appendString(String.valueOf(OmegaAnalysisRunContainerUtilities
		        .getAnalysisCount(image)), this.normal);
		this.appendNewline();
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_ACQUIRED, this.bold);
		final String acquiredDate = format.format(image.getAcquisitionDate());
		this.appendString(acquiredDate.replace("_", " "), this.normal);
		this.appendNewline();
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_IMPORTED, this.bold);
		final String importedDate = format.format(image.getImportedDate());
		this.appendString(importedDate.replace("_", " "), this.normal);
		this.appendNewline();
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_DIM_XY, this.bold);
		this.appendString(String.valueOf(pixels.getSizeX()), this.normal);
		this.appendString(" x ", this.normal);
		this.appendString(String.valueOf(pixels.getSizeY()), this.normal);
		this.appendNewline();
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_DIM_ZTC, this.bold);
		final int sizeZ = pixels.getSizeZ();
		final String sizeZs = String.valueOf(sizeZ);
		this.appendString(sizeZs, this.normal);
		this.appendString(" x ", this.normal);
		final int sizeT = pixels.getSizeT();
		final String sizeTs = String.valueOf(sizeT);
		this.appendString(sizeTs, this.normal);
		this.appendString(" x ", this.normal);
		final int sizeC = pixels.getSizeC();
		final String sizeCs = String.valueOf(sizeC);
		this.appendString(sizeCs, this.normal);
		this.appendNewline();
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_PIXELTYPE, this.bold);
		this.appendString(pixels.getPixelsType(), this.normal);
		this.appendNewline();
		final double pixelsSizeX = pixels.getPixelSizeX();
		final double pixelsSizeY = pixels.getPixelSizeY();
		final double pixelsSizeZ = pixels.getPixelSizeZ();
		if ((pixelsSizeX != -1) && (pixelsSizeY != -1)) {
			if (pixelsSizeZ != -1) {
				this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_PIXELSIZES,
						this.bold);
			} else {
				this.appendString(
						OmegaGUIConstants.SIDEPANEL_INFO_PIXELSIZES_Z,
						this.bold);
			}
			final BigDecimal bigX = new BigDecimal(pixelsSizeX).setScale(2,
					RoundingMode.HALF_UP);
			final String pixelsSizeXs = bigX.toString();
			this.appendString(pixelsSizeXs, this.normal);
			this.appendString(" x ", this.normal);
			final BigDecimal bigY = new BigDecimal(pixelsSizeY).setScale(2,
					RoundingMode.HALF_UP);
			final String pixelsSizeYs = bigY.toString();
			this.appendString(pixelsSizeYs, this.normal);
			if (pixelsSizeZ != -1) {
				this.appendString(" x ", this.normal);
				final BigDecimal bigZ = new BigDecimal(pixelsSizeZ).setScale(2,
				        RoundingMode.HALF_UP);
				final String pixelsSizeZs = bigZ.toString();
				this.appendString(pixelsSizeZs, this.normal);
			}
			this.appendNewline();
			// this.appendNewline();
			// this.appendString("Channels: ", this.bold);
		}
	}
}
