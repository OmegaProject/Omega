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
package edu.umassmed.omega.plSbalzariniPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.constants.OmegaConstantsAlgorithmParameters;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.gui.GenericAnalysisInformationPanel;
import edu.umassmed.omega.commons.gui.GenericComboBox;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.GenericTextFieldValidable;
import edu.umassmed.omega.plSbalzariniPlugin.PLConstants;

public class PLRunPanel extends GenericPanel {
	
	private static final long serialVersionUID = -2109646064541873817L;
	
	private static final Dimension VALUE_FIELDS_DIM = new Dimension(45, 20);
	private static final Dimension LBL_FIELDS_DIM = new Dimension(120, 20);
	
	private GenericTextFieldValidable displacement_txtField,
			linkrange_txtField, objectFeature_txtField, minPoints_txtField,
			dynamics_txtField;
	
	private GenericAnalysisInformationPanel infoPanel;
	private GenericComboBox<String> dynamics_combo, optimizer_combo;
	
	private OmegaGateway gateway;
	
	public PLRunPanel(final RootPaneContainer parent, final OmegaGateway gateway) {
		super(parent);
		
		this.gateway = gateway;
		
		this.setLayout(new GridLayout(2, 2));
		
		this.createAndAddWidgets();
	}
	
	private void createAndAddWidgets() {
		this.infoPanel = new GenericAnalysisInformationPanel(
				this.getParentContainer());
		this.infoPanel.setBorder(new TitledBorder(
				OmegaGUIConstants.PLUGIN_INPUT_INFORMATION));
		this.add(this.infoPanel);
		
		final JPanel placeholderPanel = new JPanel();
		this.add(placeholderPanel);
		
		final JScrollPane createSelectionParamPanel = this
				.createSelectionParamPanel();
		this.add(createSelectionParamPanel);
		
		final JScrollPane linkParamPanel = this.createLinkingParamPanel();
		this.add(linkParamPanel);
	}
	
	public JScrollPane createLinkingParamPanel() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		// Linking panel
		final JPanel paramLinkingPanel = new JPanel();
		paramLinkingPanel.setLayout(new GridLayout(7, 1));
		
		// Linkrange
		final JPanel linkrangePanel = new JPanel();
		linkrangePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel linkrange_lbl = new JLabel(
				OmegaConstantsAlgorithmParameters.PARAM_LINKRANGE + ":");
		linkrange_lbl.setPreferredSize(PLRunPanel.LBL_FIELDS_DIM);
		linkrangePanel.add(linkrange_lbl);
		this.linkrange_txtField = new GenericTextFieldValidable(
				GenericTextFieldValidable.CONTENT_INT);
		this.linkrange_txtField.setPreferredSize(PLRunPanel.VALUE_FIELDS_DIM);
		linkrangePanel.add(this.linkrange_txtField);
		paramLinkingPanel.add(linkrangePanel);
		
		// Displacement
		final JPanel displacementPanel = new JPanel();
		displacementPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel displacement_lbl = new JLabel(
				OmegaConstantsAlgorithmParameters.PARAM_DISPLACEMENT + ":");
		displacement_lbl.setPreferredSize(PLRunPanel.LBL_FIELDS_DIM);
		displacementPanel.add(displacement_lbl);
		this.displacement_txtField = new GenericTextFieldValidable(
				GenericTextFieldValidable.CONTENT_DOUBLE);
		this.displacement_txtField
				.setPreferredSize(PLRunPanel.VALUE_FIELDS_DIM);
		displacementPanel.add(this.displacement_txtField);
		paramLinkingPanel.add(displacementPanel);
		
		// Mov type
		final JPanel movTypePanel = new JPanel();
		movTypePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel dynamics_lbl = new JLabel(
				OmegaConstantsAlgorithmParameters.PARAM_MOVTYPE + ":");
		dynamics_lbl.setPreferredSize(PLRunPanel.LBL_FIELDS_DIM);
		movTypePanel.add(dynamics_lbl);
		this.dynamics_combo = new GenericComboBox<String>(
				this.getParentContainer());
		this.dynamics_combo.addItem(PLConstants.PARAM_MOVTYPE_BROWNIAN);
		this.dynamics_combo.addItem(PLConstants.PARAM_MOVTYPE_STRAIGHT);
		this.dynamics_combo.addItem(PLConstants.PARAM_MOVTYPE_COSVEL);
		this.dynamics_combo.setPreferredSize(PLRunPanel.LBL_FIELDS_DIM);
		movTypePanel.add(this.dynamics_combo);
		paramLinkingPanel.add(movTypePanel);

		// Feature
		final JPanel objectFeaturePanel = new JPanel();
		objectFeaturePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel objectFeature_lbl = new JLabel(
				OmegaConstantsAlgorithmParameters.PARAM_OBJFEATURE + ":");
		objectFeature_lbl.setPreferredSize(PLRunPanel.LBL_FIELDS_DIM);
		objectFeaturePanel.add(objectFeature_lbl);
		this.objectFeature_txtField = new GenericTextFieldValidable(
				GenericTextFieldValidable.CONTENT_FLOAT);
		this.objectFeature_txtField
				.setPreferredSize(PLRunPanel.VALUE_FIELDS_DIM);
		objectFeaturePanel.add(this.objectFeature_txtField);
		paramLinkingPanel.add(objectFeaturePanel);
		
		// Dynamics
		final JPanel dynamicsPanel = new JPanel();
		dynamicsPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel radius_lbl = new JLabel(
				OmegaConstantsAlgorithmParameters.PARAM_DYNAMICS + ":");
		radius_lbl.setPreferredSize(PLRunPanel.LBL_FIELDS_DIM);
		dynamicsPanel.add(radius_lbl);
		this.dynamics_txtField = new GenericTextFieldValidable(
				GenericTextFieldValidable.CONTENT_FLOAT);
		this.dynamics_txtField.setPreferredSize(PLRunPanel.VALUE_FIELDS_DIM);
		dynamicsPanel.add(this.dynamics_txtField);
		paramLinkingPanel.add(dynamicsPanel);
		
		// Optimizer
		final JPanel optimizerPanel = new JPanel();
		optimizerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel optimizer_lbl = new JLabel(
				OmegaConstantsAlgorithmParameters.PARAM_OPTIMIZER + ":");
		optimizer_lbl.setPreferredSize(PLRunPanel.LBL_FIELDS_DIM);
		optimizerPanel.add(optimizer_lbl);
		this.optimizer_combo = new GenericComboBox<String>(
				this.getParentContainer());
		this.optimizer_combo.addItem(PLConstants.PARAM_OPTIMIZER_GREEDY);
		this.optimizer_combo.addItem(PLConstants.PARAM_OPTIMIZER_HUNGARIAN);
		this.optimizer_combo.setPreferredSize(PLRunPanel.LBL_FIELDS_DIM);
		optimizerPanel.add(this.optimizer_combo);
		paramLinkingPanel.add(optimizerPanel);
		
		// Min points
		final JPanel minPointsPanel = new JPanel();
		minPointsPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel minPointsLbl = new JLabel(
				OmegaConstantsAlgorithmParameters.PARAM_MINPOINTS + ":");
		minPointsLbl.setPreferredSize(PLRunPanel.LBL_FIELDS_DIM);
		minPointsPanel.add(minPointsLbl);
		this.minPoints_txtField = new GenericTextFieldValidable(
				GenericTextFieldValidable.CONTENT_INT);
		this.minPoints_txtField.setPreferredSize(PLRunPanel.VALUE_FIELDS_DIM);
		minPointsPanel.add(this.minPoints_txtField);
		paramLinkingPanel.add(minPointsPanel);
		
		mainPanel.add(paramLinkingPanel, BorderLayout.NORTH);
		mainPanel.add(new JLabel(), BorderLayout.CENTER);
		
		final JScrollPane sp = new JScrollPane(mainPanel);
		sp.setBorder(new TitledBorder(
				OmegaGUIConstants.PLUGIN_PARAMETERS_LINKING));
		
		return sp;
	}
	
	public JScrollPane createLinkingAdvParamPanel() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		final JPanel parametersPanel = new JPanel();
		parametersPanel.setLayout(new GridLayout(3, 1));
		
		mainPanel.add(parametersPanel, BorderLayout.NORTH);
		mainPanel.add(new JLabel(), BorderLayout.CENTER);

		final JScrollPane sp = new JScrollPane(mainPanel);
		sp.setBorder(new TitledBorder(
				OmegaGUIConstants.PLUGIN_PARAMETERS_LINKING_ADVANCED));
		
		return sp;
	}
	
	private JScrollPane createSelectionParamPanel() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		final JScrollPane sp = new JScrollPane(mainPanel);
		sp.setBorder(new TitledBorder(
				OmegaGUIConstants.PLUGIN_PARAMETERS_SELECTION));

		return sp;
	}
	
	public boolean areParametersValidated() {
		return this.displacement_txtField.isContentValidated()
				&& this.linkrange_txtField.isContentValidated()
				&& this.objectFeature_txtField.isContentValidated()
				&& this.dynamics_txtField.isContentValidated()
				&& this.minPoints_txtField.isContentValidated();
	}
	
	public String[] getParametersError() {
		final String[] errors = new String[5];
		for (int i = 0; i < 5; i++) {
			errors[i] = null;
		}
		if (!this.displacement_txtField.isContentValidated()) {
			errors[0] = OmegaConstantsAlgorithmParameters.PARAM_DISPLACEMENT
					+ ": " + this.displacement_txtField.getError();
		}
		if (!this.linkrange_txtField.isContentValidated()) {
			errors[1] = OmegaConstantsAlgorithmParameters.PARAM_LINKRANGE
					+ ": " + this.linkrange_txtField.getError();
		}
		if (!this.objectFeature_txtField.isContentValidated()) {
			errors[2] = OmegaConstantsAlgorithmParameters.PARAM_OBJFEATURE
					+ ": " + this.linkrange_txtField.getError();
		}
		if (!this.dynamics_txtField.isContentValidated()) {
			errors[3] = OmegaConstantsAlgorithmParameters.PARAM_DYNAMICS + ": "
					+ this.linkrange_txtField.getError();
		}
		if (!this.minPoints_txtField.isContentValidated()) {
			errors[4] = OmegaConstantsAlgorithmParameters.PARAM_MINPOINTS
					+ ": " + this.minPoints_txtField.getError();
		}
		
		return errors;
	}
	
	public void updateAnalysisFields(final OmegaAnalysisRun analysisRun) {
		this.infoPanel.update(analysisRun);
		
		// TODO UPDATE CHANNEL / TIMEPOINTS
	}
	
	public void updateRunFields(final List<OmegaParameter> parameters) {
		for (final OmegaParameter param : parameters) {
			if (param.getName().equals(
					OmegaConstantsAlgorithmParameters.PARAM_DISPLACEMENT)) {
				this.displacement_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(
					OmegaConstantsAlgorithmParameters.PARAM_LINKRANGE)) {
				this.linkrange_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(
					OmegaConstantsAlgorithmParameters.PARAM_MOVTYPE)) {
				this.dynamics_combo.setSelectedItem(param.getStringValue());
			} else if (param.getName().equals(
					OmegaConstantsAlgorithmParameters.PARAM_OBJFEATURE)) {
				this.objectFeature_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(
					OmegaConstantsAlgorithmParameters.PARAM_DYNAMICS)) {
				this.dynamics_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(
					OmegaConstantsAlgorithmParameters.PARAM_OPTIMIZER)) {
				this.optimizer_combo.setSelectedItem(param.getStringValue());
			} else if (param.getName().equals(
					OmegaConstantsAlgorithmParameters.PARAM_MINPOINTS)) {
				this.minPoints_txtField.setText(param.getStringValue());
			} else {
				// TODO gestire errore
			}
		}
	}
	
	public void updateRunFieldsDefault() {
		this.displacement_txtField.setText("10");
		this.linkrange_txtField.setText("5");
		this.dynamics_combo.setSelectedItem(PLConstants.PARAM_MOVTYPE_BROWNIAN);
		this.minPoints_txtField.setText("25");
		this.objectFeature_txtField.setText("1");
		this.dynamics_txtField.setText("1");
		this.optimizer_combo
				.setSelectedItem(PLConstants.PARAM_OPTIMIZER_GREEDY);
	}
	
	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
	}
	
	public List<OmegaParameter> getParameters() {
		if (!this.areParametersValidated())
			return null;
		final List<OmegaParameter> params = new ArrayList<OmegaParameter>();
		final float displacement = Float.valueOf(this.displacement_txtField
				.getText());
		params.add(new OmegaParameter(
				OmegaConstantsAlgorithmParameters.PARAM_DISPLACEMENT,
				displacement));
		final int linkrange = Integer
				.valueOf(this.linkrange_txtField.getText());
		params.add(new OmegaParameter(
				OmegaConstantsAlgorithmParameters.PARAM_LINKRANGE, linkrange));
		final String movType = (String) this.dynamics_combo.getSelectedItem();
		params.add(new OmegaParameter(
				OmegaConstantsAlgorithmParameters.PARAM_MOVTYPE, movType));
		
		final float objectFeature = Float.valueOf(this.objectFeature_txtField
				.getText());
		params.add(new OmegaParameter(
				OmegaConstantsAlgorithmParameters.PARAM_OBJFEATURE,
				objectFeature));
		final float dynamics = Float.valueOf(this.dynamics_txtField.getText());
		params.add(new OmegaParameter(
				OmegaConstantsAlgorithmParameters.PARAM_DYNAMICS, dynamics));
		final String optimizer = (String) this.optimizer_combo
				.getSelectedItem();
		params.add(new OmegaParameter(
				OmegaConstantsAlgorithmParameters.PARAM_OPTIMIZER, optimizer));
		final int minPoints = Integer
				.valueOf(this.minPoints_txtField.getText());
		params.add(new OmegaParameter(
				OmegaConstantsAlgorithmParameters.PARAM_MINPOINTS, minPoints));
		return params;
	}
	
	public void setFieldsEnalbed(final boolean enabled) {
		this.displacement_txtField.setEnabled(enabled);
		this.linkrange_txtField.setEnabled(enabled);
		this.dynamics_combo.setEnabled(enabled);
		this.objectFeature_txtField.setEnabled(enabled);
		this.dynamics_txtField.setEnabled(enabled);
		this.optimizer_combo.setEnabled(enabled);
		this.minPoints_txtField.setEnabled(enabled);
	}
	
	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.infoPanel.updateParentContainer(parent);
	}
}
