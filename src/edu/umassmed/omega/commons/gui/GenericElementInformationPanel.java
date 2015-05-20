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
import edu.umassmed.omega.data.analysisRunElements.OrphanedAnalysisContainer;
import edu.umassmed.omega.data.coreElements.OmegaDataset;
import edu.umassmed.omega.data.coreElements.OmegaElement;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.data.coreElements.OmegaNamedElement;
import edu.umassmed.omega.data.coreElements.OmegaProject;

public class GenericElementInformationPanel extends GenericScrollPane {

	private static final long serialVersionUID = -8599077833612345455L;

	private JTextPane info_txt;

	private final SimpleAttributeSet normal, bold;

	private JButton details_btt;

	GenericElementDetailsDialog detailsDialog;

	public GenericElementInformationPanel(final RootPaneContainer parent) {
		super(parent);

		this.normal = new SimpleAttributeSet();
		this.bold = new SimpleAttributeSet();
		StyleConstants.setBold(this.bold, true);

		// this.setBorder(new TitledBorder("Information"));

		this.detailsDialog = new GenericElementDetailsDialog(parent);

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
		this.details_btt = new JButton("Edit details");
		this.details_btt.setEnabled(false);
		this.details_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.details_btt.setSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.details_btt);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		this.setViewportView(mainPanel);
	}

	private void addListeners() {
		this.details_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GenericElementInformationPanel.this.handleShowDetails();
			}
		});
	}

	private void handleShowDetails() {
		this.detailsDialog.setVisible(true);
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

	public void update(final OmegaElement element) {
		this.details_btt.setEnabled(false);
		this.detailsDialog.updateImage(null);
		try {
			this.reset();
			if ((element != null)
			        && !(element instanceof OrphanedAnalysisContainer)) {
				this.getGenericElementInformation(element);
				this.appendNewline();
				this.getSpecificElementInformation(element);
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

	private void getGenericElementInformation(final OmegaElement element)
	        throws BadLocationException {
		final long id = element.getElementID();
		final String clazz = element.getClass().getSimpleName()
		        .replace("Omega", "");
		this.appendString(clazz, this.bold);
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_ID, this.bold);
		this.appendString(String.valueOf(id), this.normal);
		this.appendNewline();
		// TODO add owner
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_OWNER, this.bold);
		this.appendNewline();
		this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_NAME, this.bold);
		if (element instanceof OmegaNamedElement) {
			this.appendString(((OmegaNamedElement) element).getName(),
			        this.normal);
		} else {
			this.appendString(OmegaGUIConstants.SIDEPANEL_INFO_NOT_NAMED,
					this.normal);
		}
	}

	private void getSpecificElementInformation(final OmegaElement element)
	        throws BadLocationException {
		if (element instanceof OmegaProject) {
			this.addAdditionalProjectInformation((OmegaProject) element);
		} else if (element instanceof OmegaDataset) {
			this.addAdditionalDatasetInformation((OmegaDataset) element);
		} else if (element instanceof OmegaImage) {
			this.addAdditionalImageInformation((OmegaImage) element);
			this.details_btt.setEnabled(true);
			this.detailsDialog.updateImage((OmegaImage) element);
			// TODO throw error?
		}
	}

	private void addAdditionalProjectInformation(final OmegaProject project)
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
	}
}
