/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School Alessandro
 * Rigano (Program in Molecular Medicine) Caterina Strambio De Castillia
 * (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team:
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli,
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban,
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts: Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.sptSbalzariniPlugin.gui;

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

import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.gui.GenericElementInformationPanel;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.GenericTextFieldValidable;
import edu.umassmed.omega.commons.gui.interfaces.GenericElementInformationContainerInterface;
import edu.umassmed.omega.sptSbalzariniPlugin.SPTConstants;

public class SPTRunPanel extends GenericPanel implements
GenericElementInformationContainerInterface {

	private static final long serialVersionUID = -2109646064541873817L;

	private static final Dimension VALUE_FIELDS_DIM = new Dimension(45, 20);
	private static final Dimension LBL_FIELDS_DIM = new Dimension(120, 20);

	private GenericTextFieldValidable radius_txtField, cutoff_txtField,
	        percentile_txtField, displacement_txtField, linkrange_txtField,
	        minPoints_txtField, zSection_txtField;

	private JPanel additionaParamPanel, channelsPanel;
	private JCheckBox[] channels;
	private ButtonGroup group;

	private JLabel maxSection_lbl;

	private GenericElementInformationPanel infoPanel;
	private final GenericElementInformationContainerInterface infoContainer;
	private OmegaGateway gateway;

	public SPTRunPanel(final RootPaneContainer parent,
	        final OmegaGateway gateway,
	        final GenericElementInformationContainerInterface infoContainer) {
		super(parent);

		this.gateway = gateway;
		this.infoContainer = infoContainer;
		
		this.setLayout(new GridLayout(2, 2));

		this.createAndAddWidgets();
	}

	private void createAndAddWidgets() {
		this.infoPanel = new GenericElementInformationPanel(
		        this.getParentContainer(), this);
		this.infoPanel.setBorder(new TitledBorder(
		        OmegaGUIConstants.SIDEPANEL_TABS_GENERAL));
		this.add(this.infoPanel);

		final JPanel additionalParamPanel = this.createAdditionalParamPanel();
		this.add(additionalParamPanel);

		final JPanel detParamPanel = this.createDetectionParamPanel();
		this.add(detParamPanel);

		final JPanel linkParamPanel = this.createLinkingParamPanel();
		this.add(linkParamPanel);
	}

	private JPanel createDetectionParamPanel() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(new TitledBorder(SPTConstants.PARAMETER_DETECTION));

		// DetectionPanel
		final JPanel paramDetectionPanel = new JPanel();
		paramDetectionPanel.setLayout(new GridLayout(3, 1));

		// Radius
		final JPanel radiusPanel = new JPanel();
		radiusPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel radius_lbl = new JLabel(SPTConstants.PARAM_RADIUS + ":");
		radius_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		radiusPanel.add(radius_lbl);
		this.radius_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_INT);
		this.radius_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		radiusPanel.add(this.radius_txtField);
		paramDetectionPanel.add(radiusPanel);

		// Cutoff
		final JPanel cutoffPanel = new JPanel();
		cutoffPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel cutoff_lbl = new JLabel(SPTConstants.PARAM_CUTOFF + ":");
		cutoff_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		cutoffPanel.add(cutoff_lbl);
		this.cutoff_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_DOUBLE);
		this.cutoff_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		cutoffPanel.add(this.cutoff_txtField);
		paramDetectionPanel.add(cutoffPanel);

		// Percentile
		final JPanel percentilePanel = new JPanel();
		percentilePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel percentile_lbl = new JLabel(SPTConstants.PARAM_PERCENTILE
		        + ":");
		percentile_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		percentilePanel.add(percentile_lbl);
		this.percentile_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_DOUBLE);
		this.percentile_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		percentilePanel.add(this.percentile_txtField);
		paramDetectionPanel.add(percentilePanel);

		mainPanel.add(paramDetectionPanel, BorderLayout.NORTH);
		mainPanel.add(new JLabel(), BorderLayout.CENTER);

		return mainPanel;
	}

	private JPanel createLinkingParamPanel() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(new TitledBorder(SPTConstants.PARAMETER_LINKING));

		// Linking panel
		final JPanel paramLinkingPanel = new JPanel();
		paramLinkingPanel.setLayout(new GridLayout(2, 1));

		// Linkrange
		final JPanel linkrangePanel = new JPanel();
		linkrangePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel linkrange_lbl = new JLabel(SPTConstants.PARAM_LINKRANGE
		        + ":");
		linkrange_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		linkrangePanel.add(linkrange_lbl);
		this.linkrange_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_INT);
		this.linkrange_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		linkrangePanel.add(this.linkrange_txtField);
		paramLinkingPanel.add(linkrangePanel);

		// Displacement
		final JPanel displacementPanel = new JPanel();
		displacementPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel displacement_lbl = new JLabel(
		        SPTConstants.PARAM_DISPLACEMENT + ":");
		displacement_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		displacementPanel.add(displacement_lbl);
		this.displacement_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_DOUBLE);
		this.displacement_txtField
		        .setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		displacementPanel.add(this.displacement_txtField);
		paramLinkingPanel.add(displacementPanel);

		mainPanel.add(paramLinkingPanel, BorderLayout.NORTH);
		mainPanel.add(new JLabel(), BorderLayout.CENTER);

		return mainPanel;
	}

	private JPanel createAdditionalParamPanel() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(new TitledBorder(SPTConstants.PARAMETER_ADVANCED));

		this.additionaParamPanel = new JPanel();
		this.additionaParamPanel.setLayout(new BoxLayout(
				this.additionaParamPanel, BoxLayout.Y_AXIS));

		// Min points
		final JPanel minPointsPanel = new JPanel();
		minPointsPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel minPointsLbl = new JLabel(SPTConstants.PARAM_MINPOINTS
		        + ":");
		minPointsLbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		minPointsPanel.add(minPointsLbl);
		this.minPoints_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_INT);
		this.minPoints_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		minPointsPanel.add(this.minPoints_txtField);
		this.additionaParamPanel.add(minPointsPanel);

		final JPanel zSectionPanel = new JPanel();
		zSectionPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel zSectionLbl = new JLabel(SPTConstants.PARAM_ZSECTION + ":");
		zSectionLbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		zSectionPanel.add(zSectionLbl);
		this.zSection_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_INT);
		this.zSection_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
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
		        && this.percentile_txtField.isContentValidated()
		        && this.displacement_txtField.isContentValidated()
		        && this.linkrange_txtField.isContentValidated()
		        && this.minPoints_txtField.isContentValidated()
		        && this.zSection_txtField.isContentValidated();
		// TODO Add validation about z and channels
	}

	public String[] getParametersError() {
		final String[] errors = new String[7];
		for (int i = 0; i < 7; i++) {
			errors[i] = null;
		}
		if (!this.radius_txtField.isContentValidated()) {
			errors[0] = SPTConstants.PARAM_RADIUS + ": "
			        + this.radius_txtField.getError();
		}
		if (!this.cutoff_txtField.isContentValidated()) {
			errors[1] = SPTConstants.PARAM_CUTOFF + ": "
			        + this.cutoff_txtField.getError();
		}
		if (!this.percentile_txtField.isContentValidated()) {
			errors[2] = SPTConstants.PARAM_PERCENTILE + ": "
			        + this.percentile_txtField.getError();
		}
		if (!this.displacement_txtField.isContentValidated()) {
			errors[3] = SPTConstants.PARAM_DISPLACEMENT + ": "
			        + this.displacement_txtField.getError();
		}
		if (!this.linkrange_txtField.isContentValidated()) {
			errors[4] = SPTConstants.PARAM_LINKRANGE + ": "
			        + this.linkrange_txtField.getError();
		}

		if (!this.minPoints_txtField.isContentValidated()) {
			errors[5] = SPTConstants.PARAM_MINPOINTS + ": "
			        + this.minPoints_txtField.getError();
		}

		if (!this.zSection_txtField.isContentValidated()) {
			errors[6] = SPTConstants.PARAM_ZSECTION + ": "
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
			if (param.getName().equals(SPTConstants.PARAM_RADIUS)) {
				this.radius_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SPTConstants.PARAM_CUTOFF)) {
				this.cutoff_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SPTConstants.PARAM_PERCENTILE)) {
				this.percentile_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SPTConstants.PARAM_DISPLACEMENT)) {
				this.displacement_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SPTConstants.PARAM_LINKRANGE)) {
				this.linkrange_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SPTConstants.PARAM_MINPOINTS)) {
				this.minPoints_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SPTConstants.PARAM_ZSECTION)) {
				this.zSection_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SPTConstants.PARAM_CHANNEL)) {
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
		this.displacement_txtField.setText("5");
		this.linkrange_txtField.setText("5");
		this.minPoints_txtField.setText("25");
	}

	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
	}

	public List<OmegaParameter> getSPTParameters() {
		if (!this.areParametersValidated())
			return null;
		final List<OmegaParameter> params = new ArrayList<OmegaParameter>();
		final int radius = Integer.valueOf(this.radius_txtField.getText());
		params.add(new OmegaParameter(SPTConstants.PARAM_RADIUS, radius));
		final double cutoff = Double.valueOf(this.cutoff_txtField.getText());
		params.add(new OmegaParameter(SPTConstants.PARAM_CUTOFF, cutoff));
		final double percentile = Double.valueOf(this.percentile_txtField
		        .getText());
		params.add(new OmegaParameter(SPTConstants.PARAM_PERCENTILE, percentile));
		final int linkrange = Integer
		        .valueOf(this.linkrange_txtField.getText());
		params.add(new OmegaParameter(SPTConstants.PARAM_LINKRANGE, linkrange));
		final double displacement = Double.valueOf(this.displacement_txtField
		        .getText());
		params.add(new OmegaParameter(SPTConstants.PARAM_DISPLACEMENT,
		        displacement));
		final int minPoints = Integer
		        .valueOf(this.minPoints_txtField.getText());
		params.add(new OmegaParameter(SPTConstants.PARAM_MINPOINTS, minPoints));
		final int section = Integer.valueOf(this.zSection_txtField.getText());
		params.add(new OmegaParameter(SPTConstants.PARAM_ZSECTION, section));
		int channel = 0;
		for (final JCheckBox checkbox : this.channels) {
			if (!checkbox.isSelected()) {
				channel++;
			} else {
				break;
			}
		}
		params.add(new OmegaParameter(SPTConstants.PARAM_CHANNEL, channel));
		// TODO CHANNEL
		// final int channel = Integer.valueOf(this.channel_txtField.getText());
		// params.add(new OmegaParameter(SPTConstants.PARAM_CHANNEL, channel));
		return params;
	}

	public void setFieldsEnalbed(final boolean enabled) {
		this.radius_txtField.setEnabled(enabled);
		this.cutoff_txtField.setEnabled(enabled);
		this.percentile_txtField.setEnabled(enabled);
		this.linkrange_txtField.setEnabled(enabled);
		this.displacement_txtField.setEnabled(enabled);
		this.minPoints_txtField.setEnabled(enabled);
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

	@Override
	public void fireElementChanged() {
		this.infoContainer.fireElementChanged();
	}
}
