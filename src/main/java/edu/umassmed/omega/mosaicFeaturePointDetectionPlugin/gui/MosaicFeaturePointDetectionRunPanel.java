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
package edu.umassmed.omega.mosaicFeaturePointDetectionPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.constants.OmegaAlgorithmParameterConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.gui.GenericElementInformationPanel;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.GenericTextFieldValidable;
import edu.umassmed.omega.commons.gui.interfaces.GenericElementInformationContainerInterface;

public class MosaicFeaturePointDetectionRunPanel extends GenericPanel implements
		GenericElementInformationContainerInterface {
	
	private static final long serialVersionUID = -2109646064541873817L;
	
	private static final Dimension VALUE_FIELDS_DIM = new Dimension(45, 20);
	private static final Dimension LBL_FIELDS_DIM = new Dimension(120, 20);
	
	private GenericTextFieldValidable radius_txtField, cutoff_txtField,
			percentile_txtField, zSection_txtField;
	
	private JPanel selectionParamPanel, channelsPanel;
	private JCheckBox[] channels;
	private ButtonGroup group;
	private JCheckBox percAbs_checkBox;
	
	private JLabel maxSection_lbl;
	
	private GenericElementInformationPanel infoPanel;
	
	private OmegaGateway gateway;
	private final GenericElementInformationContainerInterface infoContainer;
	
	public MosaicFeaturePointDetectionRunPanel(final RootPaneContainer parent,
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
				OmegaGUIConstants.PLUGIN_INPUT_INFORMATION));
		this.add(this.infoPanel);
		
		final JPanel placeholder = new JPanel();
		this.add(placeholder);
		
		final JScrollPane selectionParamPanel = this
				.createSelectionParamPanel();
		this.add(selectionParamPanel);
		
		final JScrollPane detParamPanel = this.createDetectionParamPanel();
		this.add(detParamPanel);
	}
	
	private JScrollPane createDetectionParamPanel() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		// DetectionPanel
		final JPanel paramDetectionPanel = new JPanel();
		paramDetectionPanel.setLayout(new GridLayout(4, 1));
		
		// Radius
		final JPanel radiusPanel = new JPanel();
		radiusPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel radius_lbl = new JLabel(
				OmegaAlgorithmParameterConstants.PARAM_RADIUS + ":");
		radius_lbl.setPreferredSize(MosaicFeaturePointDetectionRunPanel.LBL_FIELDS_DIM);
		radiusPanel.add(radius_lbl);
		this.radius_txtField = new GenericTextFieldValidable(
				GenericTextFieldValidable.CONTENT_INT);
		this.radius_txtField.setPreferredSize(MosaicFeaturePointDetectionRunPanel.VALUE_FIELDS_DIM);
		radiusPanel.add(this.radius_txtField);
		paramDetectionPanel.add(radiusPanel);
		
		// Cutoff
		final JPanel cutoffPanel = new JPanel();
		cutoffPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel cutoff_lbl = new JLabel(
				OmegaAlgorithmParameterConstants.PARAM_CUTOFF + ":");
		cutoff_lbl.setPreferredSize(MosaicFeaturePointDetectionRunPanel.LBL_FIELDS_DIM);
		cutoffPanel.add(cutoff_lbl);
		this.cutoff_txtField = new GenericTextFieldValidable(
				GenericTextFieldValidable.CONTENT_DOUBLE);
		this.cutoff_txtField.setPreferredSize(MosaicFeaturePointDetectionRunPanel.VALUE_FIELDS_DIM);
		cutoffPanel.add(this.cutoff_txtField);
		paramDetectionPanel.add(cutoffPanel);
		
		// Percentile
		final JPanel percentilePanel = new JPanel();
		percentilePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel percentile_lbl = new JLabel(
				OmegaAlgorithmParameterConstants.PARAM_PERCENTILE + ":");
		percentile_lbl.setPreferredSize(MosaicFeaturePointDetectionRunPanel.LBL_FIELDS_DIM);
		percentilePanel.add(percentile_lbl);
		this.percentile_txtField = new GenericTextFieldValidable(
				GenericTextFieldValidable.CONTENT_DOUBLE);
		this.percentile_txtField.setPreferredSize(MosaicFeaturePointDetectionRunPanel.VALUE_FIELDS_DIM);
		percentilePanel.add(this.percentile_txtField);
		paramDetectionPanel.add(percentilePanel);
		
		final JPanel percentileAbsPanel = new JPanel();
		percentileAbsPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel percentileAbs_lbl = new JLabel(
				OmegaAlgorithmParameterConstants.PARAM_PERCENTILE_ABS + ":");
		percentileAbs_lbl.setPreferredSize(MosaicFeaturePointDetectionRunPanel.LBL_FIELDS_DIM);
		percentileAbsPanel.add(percentileAbs_lbl);
		this.percAbs_checkBox = new JCheckBox();
		this.percAbs_checkBox.setPreferredSize(MosaicFeaturePointDetectionRunPanel.VALUE_FIELDS_DIM);
		percentileAbsPanel.add(this.percAbs_checkBox);
		paramDetectionPanel.add(percentileAbsPanel);
		
		mainPanel.add(paramDetectionPanel, BorderLayout.NORTH);
		mainPanel.add(new JLabel(), BorderLayout.CENTER);

		final JScrollPane sp = new JScrollPane(mainPanel);
		sp.setBorder(new TitledBorder(
				OmegaGUIConstants.PLUGIN_PARAMETERS_DETECTION));

		return sp;
	}
	
	private JScrollPane createSelectionParamPanel() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		this.selectionParamPanel = new JPanel();
		this.selectionParamPanel.setLayout(new BoxLayout(
				this.selectionParamPanel, BoxLayout.Y_AXIS));
		
		final JPanel zSectionPanel = new JPanel();
		zSectionPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel zSectionLbl = new JLabel(
				OmegaAlgorithmParameterConstants.PARAM_ZSECTION
						+ " to analyze:");
		zSectionLbl.setPreferredSize(MosaicFeaturePointDetectionRunPanel.LBL_FIELDS_DIM);
		zSectionPanel.add(zSectionLbl);
		this.zSection_txtField = new GenericTextFieldValidable(
				GenericTextFieldValidable.CONTENT_INT);
		this.zSection_txtField.setPreferredSize(MosaicFeaturePointDetectionRunPanel.VALUE_FIELDS_DIM);
		zSectionPanel.add(this.zSection_txtField);
		this.maxSection_lbl = new JLabel("/ NA");
		zSectionPanel.add(this.maxSection_lbl);
		this.selectionParamPanel.add(zSectionPanel);
		
		// channels panel
		this.channelsPanel = new JPanel();
		this.channelsPanel.setLayout(new GridLayout(1, 1));
		this.channelsPanel
				.setBorder(BorderFactory
						.createTitledBorder(OmegaAlgorithmParameterConstants.PARAM_CHANNEL
								+ " to analyze:"));
		
		this.selectionParamPanel.add(this.channelsPanel);
		this.group = new ButtonGroup();
		
		mainPanel.add(this.selectionParamPanel, BorderLayout.NORTH);
		mainPanel.add(new JLabel(), BorderLayout.CENTER);

		final JScrollPane sp = new JScrollPane(mainPanel);
		sp.setBorder(new TitledBorder(
				OmegaGUIConstants.PLUGIN_PARAMETERS_SELECTION));
		
		return sp;
	}
	
	private void createChannelsPane(final int n,
			final Map<Integer, String> channelNames, final Boolean[] selC) {
		this.selectionParamPanel.remove(this.channelsPanel);
		this.channelsPanel.removeAll();
		if (this.channels != null) {
			for (final JCheckBox checkBox : this.channels) {
				this.group.remove(checkBox);
			}
		}
		// this.channelsPanel.revalidate();
		// this.channelsPanel.repaint();
		this.channelsPanel
				.setBorder(BorderFactory
						.createTitledBorder(OmegaAlgorithmParameterConstants.PARAM_CHANNEL
								+ " to analyze:"));
		this.channelsPanel.setLayout(new GridLayout(n, 1));
		this.channels = new JCheckBox[n];

		int counter = 0;
		int first = -1;
		for (final Boolean bool : selC) {
			if (bool) {
				if (first == -1) {
					first = counter;
				}
			}
			counter++;
		}
		
		for (int i = 0; i < n; i++) {
			final String chanName = channelNames.get(i);
			String chan = String.valueOf(i);
			if (chanName != null) {
				chan += ": " + chanName;
			}
			this.channels[i] = new JCheckBox(chan);
			this.group.add(this.channels[i]);
			
			if (i == first) {
				this.channels[i].setSelected(selC[i]);
			}
			
			this.channelsPanel.add(this.channels[i]);
		}
		
		final Dimension channelsDim = new Dimension(
				this.channelsPanel.getWidth(), 50 * n);
		this.channelsPanel.setPreferredSize(channelsDim);
		this.channelsPanel.setSize(channelsDim);
		
		this.selectionParamPanel.add(this.channelsPanel);
		this.selectionParamPanel.revalidate();
		this.selectionParamPanel.repaint();
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
			errors[0] = OmegaAlgorithmParameterConstants.PARAM_RADIUS + ": "
					+ this.radius_txtField.getError();
		}
		if (!this.cutoff_txtField.isContentValidated()) {
			errors[1] = OmegaAlgorithmParameterConstants.PARAM_CUTOFF + ": "
					+ this.cutoff_txtField.getError();
		}
		if (!this.percentile_txtField.isContentValidated()) {
			errors[2] = OmegaAlgorithmParameterConstants.PARAM_PERCENTILE
					+ ": " + this.percentile_txtField.getError();
		}
		if (!this.zSection_txtField.isContentValidated()) {
			errors[3] = OmegaAlgorithmParameterConstants.PARAM_ZSECTION + ": "
					+ this.zSection_txtField.getError();
		} else {
			final int val = Integer.valueOf(this.zSection_txtField.getText());
			final int maxZ = Integer.valueOf(this.maxSection_lbl.getText()
					.replace("/ ", ""));
			if (val > maxZ) {
				errors[3] = OmegaAlgorithmParameterConstants.PARAM_ZSECTION
						+ ": " + "cannot be bigger than maximum";
			}
		}
		
		return errors;
	}
	
	public void updateImageFields(final OmegaImage image) {
		this.infoPanel.updateContent(image);
		
		if (image == null)
			return;
		
		final int selZ = image.getDefaultPixels().getSelectedZ();
		this.zSection_txtField.setText(String.valueOf(selZ));
		final int z = image.getDefaultPixels().getSizeZ() - 1;
		this.maxSection_lbl.setText("/ " + z);
		
		final Boolean[] selC = image.getDefaultPixels().getSelectedC();
		final int c = image.getDefaultPixels().getSizeC();
		this.createChannelsPane(c, image.getDefaultPixels().getChannelNames(),
				selC);
		
		// TODO UPDATE CHANNEL / TIMEPOINTS
	}
	
	public void updateRunFields(final List<OmegaParameter> parameters) {
		for (final OmegaParameter param : parameters) {
			if (param.getName().equals(
					OmegaAlgorithmParameterConstants.PARAM_RADIUS)) {
				this.radius_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(
					OmegaAlgorithmParameterConstants.PARAM_CUTOFF)) {
				this.cutoff_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(
					OmegaAlgorithmParameterConstants.PARAM_PERCENTILE)) {
				this.percentile_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(
					OmegaAlgorithmParameterConstants.PARAM_PERCENTILE_ABS)) {
				this.percAbs_checkBox.setSelected(Boolean.valueOf(param
						.getStringValue()));
			} else if (param.getName().equals(
					OmegaAlgorithmParameterConstants.PARAM_ZSECTION)) {
				this.zSection_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(
					OmegaAlgorithmParameterConstants.PARAM_CHANNEL)) {
				final int channel = Integer.valueOf(param.getStringValue());
				this.channels[channel].setSelected(true);
			} else {
				// TODO gestire errore
			}
		}
	}
	
	public void updateRunFieldsDefault() {
		this.radius_txtField.setText("3");
		this.cutoff_txtField.setText("0.001");
		this.percentile_txtField.setText("0.500");
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
		params.add(new OmegaParameter(
				OmegaAlgorithmParameterConstants.PARAM_RADIUS, radius));
		final double cutoff = Double.valueOf(this.cutoff_txtField.getText());
		params.add(new OmegaParameter(
				OmegaAlgorithmParameterConstants.PARAM_CUTOFF, cutoff));
		final float percentile = Float.valueOf(this.percentile_txtField
				.getText());
		params.add(new OmegaParameter(
				OmegaAlgorithmParameterConstants.PARAM_PERCENTILE, percentile));
		
		final boolean percentileAbs = this.percAbs_checkBox.isSelected();
		params.add(new OmegaParameter(
				OmegaAlgorithmParameterConstants.PARAM_PERCENTILE_ABS,
				percentileAbs));
		final int section = Integer.valueOf(this.zSection_txtField.getText());
		params.add(new OmegaParameter(
				OmegaAlgorithmParameterConstants.PARAM_ZSECTION, section));
		int channel = 0;
		for (final JCheckBox checkbox : this.channels) {
			if (!checkbox.isSelected()) {
				channel++;
			} else {
				break;
			}
		}
		params.add(new OmegaParameter(
				OmegaAlgorithmParameterConstants.PARAM_CHANNEL, channel));
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
	
	@Override
	public void fireElementChanged() {
		this.infoContainer.fireElementChanged();
	}
}
