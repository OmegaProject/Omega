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
package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;

import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.utilities.OmegaAnalysisRunContainerUtilities;
import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaImagePixels;
import edu.umassmed.omega.dataNew.coreElements.OmegaNamedElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaProject;

public class OmegaElementInformationsPanel extends GenericPanel {

	private static final long serialVersionUID = -8599077833612345455L;

	private JLabel info_lbl;

	public OmegaElementInformationsPanel(final RootPaneContainer parent) {
		super(parent);

		this.setLayout(new BorderLayout());

		// this.setBorder(new TitledBorder("Information"));

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		this.info_lbl = new JLabel();
		this.info_lbl.setVerticalAlignment(SwingConstants.CENTER);
		this.info_lbl.setText("Nothing selected");
		this.add(this.info_lbl, BorderLayout.CENTER);
	}

	private void addListeners() {
		// TODO should i do something here?
	}

	public void update(final OmegaElement element) {
		if (element != null) {
			final StringBuffer infoBuf = new StringBuffer();
			infoBuf.append("<html><table>");
			infoBuf.append(this.getGenericElementInformation(element)
			        .toString());
			infoBuf.append("<br>");
			infoBuf.append(this.getSpecificElementInformation(element)
			        .toString());
			infoBuf.append("</table>");
			this.info_lbl.setText(infoBuf.toString());
		} else {
			this.info_lbl.setText("Nothing selected");
		}
		this.info_lbl.revalidate();
		this.info_lbl.repaint();
	}

	private StringBuffer getGenericElementInformation(final OmegaElement element) {
		final StringBuffer buf = new StringBuffer();
		final String clazz = element.getClass().getSimpleName()
		        .replace("Omega", "");
		final long id = element.getElementID();
		String name = null;
		if (element instanceof OmegaNamedElement) {
			name = ((OmegaNamedElement) element).getName();
			buf.append("<tr><td colspan = 2>Name: ");
			buf.append(name);
			buf.append("</td></tr>");
		}
		buf.append("<tr><td>Item type: ");
		buf.append(clazz);
		buf.append("</td><td>Id: ");
		buf.append(id);
		buf.append("</td></tr>");
		return buf;
	}

	private StringBuffer getSpecificElementInformation(
	        final OmegaElement element) {
		if (element instanceof OmegaProject)
			return this.addAdditionalProjectInformation((OmegaProject) element);
		else if (element instanceof OmegaDataset)
			return this.addAdditionalDatasetInformation((OmegaDataset) element);
		else if (element instanceof OmegaImage)
			return this.addAdditionalImageInformation((OmegaImage) element);
		return new StringBuffer();
		// TODO throw error?
	}

	private StringBuffer addAdditionalProjectInformation(
	        final OmegaProject project) {
		final StringBuffer buf = new StringBuffer();
		buf.append("<tr><td>Dataset: ");
		buf.append(project.getDatasets().size());
		buf.append("</td><td>Analysis: ");
		buf.append(OmegaAnalysisRunContainerUtilities.getAnalysisCount(project));
		buf.append("</td></tr>");
		return buf;
	}

	private StringBuffer addAdditionalDatasetInformation(
	        final OmegaDataset dataset) {
		final StringBuffer buf = new StringBuffer();
		buf.append("<tr><td>Images: ");
		buf.append(dataset.getImages().size());
		buf.append("</td><td>Analysis: ");
		buf.append(OmegaAnalysisRunContainerUtilities.getAnalysisCount(dataset));
		buf.append("</td></tr>");
		return buf;
	}

	private StringBuffer addAdditionalImageInformation(final OmegaImage image) {
		final OmegaImagePixels pixels = image.getDefaultPixels();
		final StringBuffer buf = new StringBuffer();
		buf.append("<tr><td>Analysis: ");
		buf.append(OmegaAnalysisRunContainerUtilities.getAnalysisCount(image));
		buf.append("</td></tr>");
		buf.append("<tr><td>Width: ");
		buf.append(pixels.getSizeX());
		buf.append("</td><td>Height: ");
		buf.append(pixels.getSizeY());
		buf.append("</td></tr>");
		buf.append("<tr><td>Planes (Z): ");
		buf.append(pixels.getSizeZ());
		buf.append("</td><td>Time (T): ");
		buf.append(pixels.getSizeT());
		buf.append("</td></tr>");
		buf.append("<tr></tr>");

		buf.append("<tr><td>Pixels size X: ");
		buf.append(pixels.getPixelSizeX());
		buf.append("</td><td>Pixels size Y: ");
		buf.append(pixels.getPixelSizeY());
		buf.append("</td></tr>");
		buf.append("<tr><td>Pixels size Z: ");
		buf.append(pixels.getPixelSizeZ());
		buf.append("</td></tr>");
		return buf;
	}
}
