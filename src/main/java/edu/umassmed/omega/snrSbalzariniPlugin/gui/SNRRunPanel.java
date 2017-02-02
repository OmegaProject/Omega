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
package edu.umassmed.omega.snrSbalzariniPlugin.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.gui.GenericAnalysisInformationPanel;
import edu.umassmed.omega.commons.gui.GenericComboBox;
import edu.umassmed.omega.commons.gui.GenericElementInformationPanel;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.GenericTextFieldValidable;
import edu.umassmed.omega.commons.gui.interfaces.GenericElementInformationContainerInterface;
import edu.umassmed.omega.snrSbalzariniPlugin.SNRConstants;

public class SNRRunPanel extends GenericPanel implements
GenericElementInformationContainerInterface {
	
	private static final long serialVersionUID = -2109646064541873817L;
	
	private static final Dimension VALUE_FIELDS_DIM = new Dimension(45, 20);
	private static final Dimension LBL_FIELDS_DIM = new Dimension(120, 20);
	
	private GenericTextFieldValidable radius_txtField, threshold_txtField;
	private GenericComboBox<String> snrMethod_cmb;
	
	private GenericElementInformationPanel elementInfoPanel;
	private GenericAnalysisInformationPanel analysisInfoPanel;
	private OmegaGateway gateway;
	private final GenericElementInformationContainerInterface infoContainer;
	
	public SNRRunPanel(final RootPaneContainer parent,
	        final OmegaGateway gateway,
			final GenericElementInformationContainerInterface infoContainer) {
		super(parent);
		
		this.gateway = gateway;
		this.infoContainer = infoContainer;
		
		this.setLayout(new GridLayout(2, 2));
		
		this.createAndAddWidgets();
	}
	
	private void createAndAddWidgets() {
		this.elementInfoPanel = new GenericElementInformationPanel(
				this.getParentContainer(), this);
		this.elementInfoPanel.setBorder(new TitledBorder(
				OmegaGUIConstants.SIDEPANEL_TABS_GENERAL));
		this.add(this.elementInfoPanel);
		
		this.analysisInfoPanel = new GenericAnalysisInformationPanel(
				this.getParentContainer());
		this.analysisInfoPanel.setBorder(new TitledBorder(
				OmegaGUIConstants.SIDEPANEL_TABS_GENERAL));
		this.add(this.analysisInfoPanel);
		
		final JPanel parametersPanel = new JPanel();
		parametersPanel.setLayout(new GridLayout(3, 1));
		parametersPanel.setBorder(new TitledBorder(SNRConstants.PARAMETER_SNR));
		
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
		
		final JPanel snrMethodPanel = new JPanel();
		snrMethodPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel snrMethod_lbl = new JLabel(SNRConstants.PARAM_SNR_METHOD
		        + ":");
		snrMethod_lbl.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		snrMethodPanel.add(snrMethod_lbl);
		this.snrMethod_cmb = new GenericComboBox<String>(
		        this.getParentContainer());
		this.snrMethod_cmb.setPreferredSize(SNRRunPanel.LBL_FIELDS_DIM);
		this.snrMethod_cmb
		.addItem(SNRConstants.PARAM_SNR_METHOD_BHATTACHARYYA_POISSON);
		this.snrMethod_cmb
		.addItem(SNRConstants.PARAM_SNR_METHOD_BHATTACHARYYA_GAUSSIAN);
		this.snrMethod_cmb.addItem(SNRConstants.PARAM_SNR_METHOD_SBALZARINI);
		snrMethodPanel.add(this.snrMethod_cmb);
		parametersPanel.add(snrMethodPanel);
		
		this.add(parametersPanel);
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
	
	public void updateImageFields(final OmegaElement element) {
		this.elementInfoPanel.update(element);
	}
	
	public void updateAnalysisFields(final OmegaAnalysisRun analysisRun) {
		this.analysisInfoPanel.update(analysisRun);
	}
	
	public void updateRunFields(final List<OmegaParameter> parameters) {
		for (final OmegaParameter param : parameters) {
			if (param.getName().equals(SNRConstants.PARAM_RADIUS)) {
				this.radius_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SNRConstants.PARAM_THRESHOLD)) {
				this.threshold_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SNRConstants.PARAM_SNR_METHOD)) {
				this.snrMethod_cmb.setSelectedItem(param.getValue());
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
		final String snrMethod = (String) this.snrMethod_cmb.getSelectedItem();
		params.add(new OmegaParameter(SNRConstants.PARAM_SNR_METHOD, snrMethod));
		return params;
	}
	
	public void setFieldsEnalbed(final boolean enabled) {
		this.radius_txtField.setEnabled(enabled);
		this.threshold_txtField.setEnabled(enabled);
		this.snrMethod_cmb.setEnabled(enabled);
	}
	
	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.analysisInfoPanel.updateParentContainer(parent);
		this.elementInfoPanel.updateParentContainer(parent);
		this.snrMethod_cmb.updateParentContainer(parent);
	}

	@Override
	public void fireElementChanged() {
		this.infoContainer.fireElementChanged();
	}
}
