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
package main.java.edu.umassmed.omega.sdSbalzariniPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;

import main.java.edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import main.java.edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import main.java.edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import main.java.edu.umassmed.omega.commons.gui.GenericElementInformationPanel;
import main.java.edu.umassmed.omega.commons.gui.GenericPanel;
import main.java.edu.umassmed.omega.commons.gui.GenericTextFieldValidable;
import main.java.edu.umassmed.omega.sdSbalzariniPlugin.SDConstants;
import main.java.edu.umassmed.omega.sptSbalzariniPlugin.SPTConstants;

public class SDRunPanel extends GenericPanel {

	private static final long serialVersionUID = -2109646064541873817L;

	private static final Dimension VALUE_FIELDS_DIM = new Dimension(45, 20);
	private static final Dimension LBL_FIELDS_DIM = new Dimension(120, 20);

	private GenericTextFieldValidable radius_txtField, cutoff_txtField,
	        percentile_txtField, zSection_txtField;

	private JPanel additionaParamPanel, channelsPanel;
	private JCheckBox[] channels;
	private ButtonGroup group;
	private JCheckBox percAbs_checkBox;

	private JLabel maxSection_lbl;

	private GenericElementInformationPanel infoPanel;

	private OmegaGateway gateway;

	public SDRunPanel(final RootPaneContainer parent, final OmegaGateway gateway) {
		super(parent);

		this.gateway = gateway;

		this.setLayout(new GridLayout(2, 2));

		this.createAndAddWidgets();
	}

	private void createAndAddWidgets() {
		this.infoPanel = new GenericElementInformationPanel(
		        this.getParentContainer());
		this.infoPanel.setBorder(new TitledBorder(
		        OmegaGUIConstants.SIDEPANEL_TABS_GENERAL));
		this.add(this.infoPanel);

		final JPanel additionalParamPanel = this.createAdditionalParamPanel();
		this.add(additionalParamPanel);

		final JPanel detParamPanel = this.createDetectionParamPanel();
		this.add(detParamPanel);
	}

	private JPanel createDetectionParamPanel() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(new TitledBorder(SPTConstants.PARAMETER_DETECTION));

		// DetectionPanel
		final JPanel paramDetectionPanel = new JPanel();
		paramDetectionPanel.setLayout(new GridLayout(4, 1));

		// Radius
		final JPanel radiusPanel = new JPanel();
		radiusPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel radius_lbl = new JLabel(SDConstants.PARAM_RADIUS + ":");
		radius_lbl.setPreferredSize(SDRunPanel.LBL_FIELDS_DIM);
		radiusPanel.add(radius_lbl);
		this.radius_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_INT);
		this.radius_txtField.setPreferredSize(SDRunPanel.VALUE_FIELDS_DIM);
		radiusPanel.add(this.radius_txtField);
		paramDetectionPanel.add(radiusPanel);

		// Cutoff
		final JPanel cutoffPanel = new JPanel();
		cutoffPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel cutoff_lbl = new JLabel(SDConstants.PARAM_CUTOFF + ":");
		cutoff_lbl.setPreferredSize(SDRunPanel.LBL_FIELDS_DIM);
		cutoffPanel.add(cutoff_lbl);
		this.cutoff_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_DOUBLE);
		this.cutoff_txtField.setPreferredSize(SDRunPanel.VALUE_FIELDS_DIM);
		cutoffPanel.add(this.cutoff_txtField);
		paramDetectionPanel.add(cutoffPanel);

		// Percentile
		final JPanel percentilePanel = new JPanel();
		percentilePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel percentile_lbl = new JLabel(SDConstants.PARAM_PERCENTILE
		        + ":");
		percentile_lbl.setPreferredSize(SDRunPanel.LBL_FIELDS_DIM);
		percentilePanel.add(percentile_lbl);
		this.percentile_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_DOUBLE);
		this.percentile_txtField.setPreferredSize(SDRunPanel.VALUE_FIELDS_DIM);
		percentilePanel.add(this.percentile_txtField);
		paramDetectionPanel.add(percentilePanel);

		final JPanel percentileAbsPanel = new JPanel();
		percentileAbsPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel percentileAbs_lbl = new JLabel(
		        SDConstants.PARAM_PERCENTILE_ABS + ":");
		percentileAbs_lbl.setPreferredSize(SDRunPanel.LBL_FIELDS_DIM);
		percentileAbsPanel.add(percentileAbs_lbl);
		this.percAbs_checkBox = new JCheckBox();
		this.percAbs_checkBox.setPreferredSize(SDRunPanel.VALUE_FIELDS_DIM);
		percentileAbsPanel.add(this.percAbs_checkBox);
		paramDetectionPanel.add(percentileAbsPanel);

		mainPanel.add(paramDetectionPanel, BorderLayout.NORTH);
		mainPanel.add(new JLabel(), BorderLayout.CENTER);

		return mainPanel;
	}

	private JPanel createAdditionalParamPanel() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(new TitledBorder(SDConstants.PARAMETER_ADVANCED));

		this.additionaParamPanel = new JPanel();
		this.additionaParamPanel.setLayout(new BoxLayout(
		        this.additionaParamPanel, BoxLayout.Y_AXIS));

		final JPanel zSectionPanel = new JPanel();
		zSectionPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel zSectionLbl = new JLabel(SDConstants.PARAM_ZSECTION + ":");
		zSectionLbl.setPreferredSize(SDRunPanel.LBL_FIELDS_DIM);
		zSectionPanel.add(zSectionLbl);
		this.zSection_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_INT);
		this.zSection_txtField.setPreferredSize(SDRunPanel.VALUE_FIELDS_DIM);
		zSectionPanel.add(this.zSection_txtField);
		this.maxSection_lbl = new JLabel("/ NA");
		zSectionPanel.add(this.maxSection_lbl);
		this.additionaParamPanel.add(zSectionPanel);

		// channels panel
		this.channelsPanel = new JPanel();
		this.channelsPanel.setLayout(new GridLayout(1, 1));

		this.additionaParamPanel.add(this.channelsPanel);
		this.group = new ButtonGroup();

		mainPanel.add(this.additionaParamPanel, BorderLayout.NORTH);
		mainPanel.add(new JLabel(), BorderLayout.CENTER);

		return mainPanel;
	}

	private void createChannelsPane(final int n, final boolean[] selC) {
		this.additionaParamPanel.remove(this.channelsPanel);
		this.channelsPanel.removeAll();
		if (this.channels != null) {
			for (final JCheckBox checkBox : this.channels) {
				this.group.remove(checkBox);
			}
		}
		// this.channelsPanel.revalidate();
		// this.channelsPanel.repaint();
		this.channelsPanel.setBorder(null);
		this.channelsPanel.setLayout(new GridLayout(n, 1));
		this.channels = new JCheckBox[n];

		for (int i = 0; i < n; i++) {
			this.channels[i] = new JCheckBox("Channel " + i);
			this.group.add(this.channels[i]);

			this.channels[i].setSelected(selC[i]);

			this.channelsPanel.add(this.channels[i]);
		}

		final Dimension channelsDim = new Dimension(
		        this.channelsPanel.getWidth(), 50 * n);
		this.channelsPanel.setPreferredSize(channelsDim);
		this.channelsPanel.setSize(channelsDim);

		this.additionaParamPanel.add(this.channelsPanel);
	}

	public boolean areParametersValidated() {
		return this.radius_txtField.isContentValidated()
		        && this.cutoff_txtField.isContentValidated()
		        && this.percentile_txtField.isContentValidated();
	}

	public String[] getParametersError() {
		final String[] errors = new String[4];
		for (int i = 0; i < 4; i++) {
			errors[i] = null;
		}
		if (!this.radius_txtField.isContentValidated()) {
			errors[0] = SDConstants.PARAM_RADIUS + ": "
			        + this.radius_txtField.getError();
		}
		if (!this.cutoff_txtField.isContentValidated()) {
			errors[1] = SDConstants.PARAM_CUTOFF + ": "
			        + this.cutoff_txtField.getError();
		}
		if (!this.percentile_txtField.isContentValidated()) {
			errors[2] = SDConstants.PARAM_PERCENTILE + ": "
			        + this.percentile_txtField.getError();
		}
		if (!this.zSection_txtField.isContentValidated()) {
			errors[3] = SDConstants.PARAM_ZSECTION + ": "
			        + this.zSection_txtField.getError();
		}

		return errors;
	}

	public void updateImageFields(final OmegaImage image) {
		this.infoPanel.update(image);

		final int selZ = image.getDefaultPixels().getSelectedZ();
		this.zSection_txtField.setText(String.valueOf(selZ));
		final int z = image.getDefaultPixels().getSizeZ() - 1;
		this.maxSection_lbl.setText("/ " + z);

		final boolean[] selC = image.getDefaultPixels().getSelectedC();
		final int c = image.getDefaultPixels().getSizeC();
		this.createChannelsPane(c, selC);

		// TODO UPDATE CHANNEL / TIMEPOINTS
	}

	public void updateRunFields(final List<OmegaParameter> parameters) {
		for (final OmegaParameter param : parameters) {
			if (param.getName().equals(SDConstants.PARAM_RADIUS)) {
				this.radius_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SDConstants.PARAM_CUTOFF)) {
				this.cutoff_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SDConstants.PARAM_PERCENTILE)) {
				this.percentile_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SDConstants.PARAM_PERCENTILE_ABS)) {
				this.percAbs_checkBox.setSelected((boolean) param.getValue());
			} else if (param.getName().equals(SDConstants.PARAM_ZSECTION)) {
				this.zSection_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SDConstants.PARAM_CHANNEL)) {
				final int channel = (int) param.getValue();
				this.channels[channel].setSelected(true);
			} else {
				// TODO gestire errore
			}
		}
	}

	public void updateRunFieldsDefault() {
		this.radius_txtField.setText("3");
		this.cutoff_txtField.setText("3.0");
		this.percentile_txtField.setText("0.1");
		this.percAbs_checkBox.setSelected(false);
	}

	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
	}

	public List<OmegaParameter> getParameters() {
		if (!this.areParametersValidated())
			return null;
		final List<OmegaParameter> params = new ArrayList<OmegaParameter>();
		final int radius = Integer.valueOf(this.radius_txtField.getText());
		params.add(new OmegaParameter(SDConstants.PARAM_RADIUS, radius));
		final double cutoff = Double.valueOf(this.cutoff_txtField.getText());
		params.add(new OmegaParameter(SDConstants.PARAM_CUTOFF, cutoff));
		final float percentile = Float.valueOf(this.percentile_txtField
		        .getText());
		params.add(new OmegaParameter(SDConstants.PARAM_PERCENTILE, percentile));

		final boolean percentileAbs = this.percAbs_checkBox.isSelected();
		params.add(new OmegaParameter(SDConstants.PARAM_PERCENTILE_ABS,
		        percentileAbs));
		final int section = Integer.valueOf(this.zSection_txtField.getText());
		params.add(new OmegaParameter(SDConstants.PARAM_ZSECTION, section));
		int channel = 0;
		for (final JCheckBox checkbox : this.channels) {
			if (!checkbox.isSelected()) {
				channel++;
			} else {
				break;
			}
		}
		params.add(new OmegaParameter(SDConstants.PARAM_CHANNEL, channel));
		return params;
	}

	public void setFieldsEnalbed(final boolean enabled) {
		this.radius_txtField.setEnabled(enabled);
		this.cutoff_txtField.setEnabled(enabled);
		this.percentile_txtField.setEnabled(enabled);
		this.percAbs_checkBox.setEnabled(enabled);
		this.zSection_txtField.setEnabled(enabled);
		for (final JCheckBox chan : this.channels) {
			chan.setEnabled(enabled);
		}
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.infoPanel.updateParentContainer(parent);
	}
}
