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
package edu.umassmed.omega.snrSbalzariniPlugin.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.GenericTextFieldValidable;
import edu.umassmed.omega.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.snrSbalzariniPlugin.SNRConstants;

public class SNRRunPanel extends GenericPanel {

	private static final long serialVersionUID = -2109646064541873817L;

	private static final Dimension VALUE_FIELDS_DIM = new Dimension(45, 20);
	private static final Dimension LBL_FIELDS_DIM = new Dimension(120, 20);

	private GenericTextFieldValidable radius_txtField, threshold_txtField;

	private JTextField imgWidthPixels_txtField, imgWidthMicron_txtField,
	        imgWidthPixelSize_txtField;
	private JTextField imgHeightPixels_txtField, imgHeightMicron_txtField,
	        imgHeightPixelSize_txtField;
	private JTextField imgFrames_txtField, imgTime_txtField, imgAvgInterval;
	private JTextField zPlane_txtField, channel_txtField;
	private JTextField minValue_txtField, maxValue_txtField;

	private OmegaGateway gateway;

	public SNRRunPanel(final RootPaneContainer parent,
	        final OmegaGateway gateway) {
		super(parent);

		this.gateway = gateway;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.createAndAddWidgets();
	}

	private void createAndAddWidgets() {
		this.createAndAddImageDetailsPanel();

		final JPanel parametersPanel = new JPanel();
		parametersPanel.setLayout(new GridLayout(1, 2));
		parametersPanel.setBorder(new TitledBorder("Parameters"));

		final JPanel radiusPanel = new JPanel();
		radiusPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel radius_lbl = new JLabel(SNRConstants.PARAM_RADIUS + ":");
		radius_lbl.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		radiusPanel.add(radius_lbl);
		this.radius_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_INT);
		this.radius_txtField.setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		radiusPanel.add(this.radius_txtField);
		parametersPanel.add(radiusPanel);

		final JPanel threshPanel = new JPanel();
		threshPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel thresh_lbl = new JLabel(SNRConstants.PARAM_THRESHOLD + ":");
		thresh_lbl.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		threshPanel.add(thresh_lbl);
		this.threshold_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_DOUBLE);
		this.threshold_txtField.setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		threshPanel.add(this.threshold_txtField);
		parametersPanel.add(threshPanel);

		this.add(parametersPanel);
	}

	private void createAndAddImageDetailsPanel() {
		// DetectionPanel
		final JPanel imgDetailsPanel = new JPanel();
		imgDetailsPanel.setLayout(new BoxLayout(imgDetailsPanel,
		        BoxLayout.Y_AXIS));
		imgDetailsPanel.setBorder(new TitledBorder("Image details"));
		// Width
		final JPanel widthPanel = new JPanel();
		widthPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		final JLabel width_lbl1 = new JLabel("Image width (X):");
		width_lbl1.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		widthPanel.add(width_lbl1);
		this.imgWidthPixels_txtField = new JTextField();
		this.imgWidthPixels_txtField.setEditable(false);
		this.imgWidthPixels_txtField
		        .setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		widthPanel.add(this.imgWidthPixels_txtField);

		final JLabel width_lbl2 = new JLabel("pixels,");
		width_lbl2.setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		widthPanel.add(width_lbl2);
		this.imgWidthMicron_txtField = new JTextField();
		this.imgWidthMicron_txtField.setEditable(false);
		this.imgWidthMicron_txtField
		        .setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		widthPanel.add(this.imgWidthMicron_txtField);

		final JLabel width_lbl3 = new JLabel("µm, pixel size:");
		width_lbl3.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		widthPanel.add(width_lbl3);
		this.imgWidthPixelSize_txtField = new JTextField();
		this.imgWidthPixelSize_txtField.setEditable(false);
		this.imgWidthPixelSize_txtField
		        .setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		widthPanel.add(this.imgWidthPixelSize_txtField);

		final JLabel width_lbl4 = new JLabel("µm/pixel");
		widthPanel.add(width_lbl4);

		imgDetailsPanel.add(widthPanel);

		final JPanel heightPanel = new JPanel();
		heightPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		final JLabel height_lbl1 = new JLabel("Image height (Y):");
		height_lbl1.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		heightPanel.add(height_lbl1);
		this.imgHeightPixels_txtField = new JTextField();
		this.imgHeightPixels_txtField.setEditable(false);
		this.imgHeightPixels_txtField
		        .setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		heightPanel.add(this.imgHeightPixels_txtField);

		final JLabel height_lbl2 = new JLabel("pixels,");
		height_lbl2.setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		heightPanel.add(height_lbl2);
		this.imgHeightMicron_txtField = new JTextField();
		this.imgHeightMicron_txtField.setEditable(false);
		this.imgHeightMicron_txtField
		        .setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		heightPanel.add(this.imgHeightMicron_txtField);

		final JLabel height_lbl3 = new JLabel("µm, pixel size:");
		height_lbl3.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		heightPanel.add(height_lbl3);
		this.imgHeightPixelSize_txtField = new JTextField();
		this.imgHeightPixelSize_txtField.setEditable(false);
		this.imgHeightPixelSize_txtField
		        .setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		heightPanel.add(this.imgHeightPixelSize_txtField);

		final JLabel height_lbl4 = new JLabel("µm/pixel");
		heightPanel.add(height_lbl4);

		imgDetailsPanel.add(heightPanel);

		final JPanel timePanel = new JPanel();
		timePanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		final JLabel time_lbl1 = new JLabel("Image frames (T):");
		time_lbl1.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		timePanel.add(time_lbl1);
		this.imgFrames_txtField = new JTextField();
		this.imgFrames_txtField.setEditable(false);
		this.imgFrames_txtField.setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		timePanel.add(this.imgFrames_txtField);

		final JLabel time_lbl2 = new JLabel("frames,");
		time_lbl2.setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		timePanel.add(time_lbl2);
		this.imgTime_txtField = new JTextField();
		this.imgTime_txtField.setEditable(false);
		this.imgTime_txtField.setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		timePanel.add(this.imgTime_txtField);

		final JLabel time_lbl3 = new JLabel("s, average interval:");
		time_lbl3.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		timePanel.add(time_lbl3);
		this.imgAvgInterval = new JTextField();
		this.imgAvgInterval.setEditable(false);
		this.imgAvgInterval.setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		timePanel.add(this.imgAvgInterval);

		final JLabel time_lbl4 = new JLabel("s/frame");
		timePanel.add(time_lbl4);

		imgDetailsPanel.add(timePanel);

		final JPanel imgDetailsSubPanel = new JPanel();
		imgDetailsSubPanel.setLayout(new GridLayout(1, 2));

		final JPanel planesPanel = new JPanel();
		planesPanel.setLayout(new BoxLayout(planesPanel, BoxLayout.Y_AXIS));

		final JPanel planePanel = new JPanel();
		planePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel plane_lbl = new JLabel("Plane(s) (z):");
		plane_lbl.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		planePanel.add(plane_lbl);
		this.zPlane_txtField = new JTextField();
		this.zPlane_txtField.setEditable(false);
		this.zPlane_txtField.setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		planePanel.add(this.zPlane_txtField);

		planesPanel.add(planePanel);

		final JPanel channelPanel = new JPanel();
		channelPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel channel_lbl = new JLabel("Channel(s) (c):");
		channel_lbl.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		channelPanel.add(channel_lbl);
		this.channel_txtField = new JTextField();
		this.channel_txtField.setEditable(false);
		this.channel_txtField.setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		channelPanel.add(this.channel_txtField);

		planesPanel.add(channelPanel);

		imgDetailsSubPanel.add(planesPanel);

		final JPanel valuesPanel = new JPanel();
		valuesPanel.setLayout(new BoxLayout(valuesPanel, BoxLayout.Y_AXIS));

		final JPanel minValPanel = new JPanel();
		minValPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel minValue_lbl = new JLabel("Min value:");
		minValue_lbl.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		minValPanel.add(minValue_lbl);
		this.minValue_txtField = new JTextField();
		this.minValue_txtField.setEditable(false);
		this.minValue_txtField.setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		minValPanel.add(this.minValue_txtField);

		valuesPanel.add(minValPanel);

		final JPanel maxValPanel = new JPanel();
		maxValPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel maxValue_lbl = new JLabel("Max value:");
		maxValue_lbl.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		maxValPanel.add(maxValue_lbl);
		this.maxValue_txtField = new JTextField();
		this.maxValue_txtField.setEditable(false);
		this.maxValue_txtField.setPreferredSize(SNRRunPanel.VALUE_FIELDS_DIM);
		maxValPanel.add(this.maxValue_txtField);

		valuesPanel.add(maxValPanel);

		imgDetailsSubPanel.add(valuesPanel);

		imgDetailsPanel.add(imgDetailsSubPanel);

		this.add(imgDetailsPanel);
	}

	public boolean areParametersValidated() {
		return this.radius_txtField.isContentValidated()
		        && this.threshold_txtField.isContentValidated();
	}

	public String[] getParametersError() {
		final String[] errors = new String[6];
		for (int i = 0; i < 6; i++) {
			errors[i] = null;
		}
		if (!this.radius_txtField.isContentValidated()) {
			errors[0] = SNRConstants.PARAM_RADIUS + ": "
			        + this.radius_txtField.getError();
		}
		if (!this.threshold_txtField.isContentValidated()) {
			errors[1] = SNRConstants.PARAM_THRESHOLD + ": "
			        + this.threshold_txtField.getError();
		}
		return errors;
	}

	public void updateImageFields(final OmegaImage image) {
		final OmegaImagePixels pixels = image.getDefaultPixels();

		final int sizeX = pixels.getSizeX();
		double pixelsSizeX = pixels.getPixelSizeX();
		this.imgWidthPixels_txtField.setText(String.valueOf(sizeX));
		if (pixelsSizeX == -1) {
			pixelsSizeX = 0;
		}
		this.imgWidthMicron_txtField.setText(String
		        .valueOf(pixelsSizeX * sizeX));
		this.imgWidthPixelSize_txtField.setText(String.valueOf(pixelsSizeX));

		final int sizeY = pixels.getSizeY();
		double pixelsSizeY = pixels.getPixelSizeY();
		this.imgHeightPixels_txtField.setText(String.valueOf(sizeY));
		if (pixelsSizeY == -1) {
			pixelsSizeY = 0;
		}
		this.imgHeightMicron_txtField.setText(String.valueOf(pixelsSizeY
		        * sizeY));
		this.imgHeightPixelSize_txtField.setText(String.valueOf(pixelsSizeY));

		final int sizeT = pixels.getSizeT();
		final int c = pixels.getSelectedC();
		final int z = pixels.getSelectedZ();

		final long id = pixels.getElementID();

		final double pixelsSizeT = this.gateway.getTotalT(id, z, sizeT, c);

		this.imgFrames_txtField.setText(String.valueOf(sizeT));
		this.imgTime_txtField.setText(String.valueOf(pixelsSizeT * sizeT));
		this.imgAvgInterval.setText(String.valueOf(pixelsSizeT));

		this.zPlane_txtField.setText(String.valueOf(z));
		this.channel_txtField.setText(String.valueOf(c));

		final int pixelsBits = this.gateway.getByteWidth(id) * 8;
		final int minVal = 0;
		final int maxVal = (int) Math.pow(2, pixelsBits);

		this.minValue_txtField.setText(String.valueOf(minVal));
		this.maxValue_txtField.setText(String.valueOf(maxVal));
	}

	public void updateRunFields(final List<OmegaParameter> parameters) {
		for (final OmegaParameter param : parameters) {
			if (param.getName().equals(SNRConstants.PARAM_RADIUS)) {
				this.radius_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SNRConstants.PARAM_THRESHOLD)) {
				this.threshold_txtField.setText(param.getStringValue());
			} else {
				// TODO gestire errore
			}
		}
	}

	public void updateRunFieldsDefault() {
		this.radius_txtField.setText("3");
		this.threshold_txtField.setText("0.8");
	}

	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
	}

	public List<OmegaParameter> getParameters() {
		if (!this.areParametersValidated())
			return null;
		final List<OmegaParameter> params = new ArrayList<OmegaParameter>();
		final int radius = Integer.valueOf(this.radius_txtField.getText());
		params.add(new OmegaParameter(SNRConstants.PARAM_RADIUS, radius));
		final double thresh = Double.valueOf(this.threshold_txtField.getText());
		params.add(new OmegaParameter(SNRConstants.PARAM_THRESHOLD, thresh));
		return params;
	}

	public void setFieldsEnalbed(final boolean enabled) {
		this.radius_txtField.setEnabled(enabled);
		this.threshold_txtField.setEnabled(enabled);
	}
}
