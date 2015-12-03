package main.java.edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;

import main.java.edu.umassmed.omega.commons.constants.OmegaConstants;
import main.java.edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import main.java.edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import main.java.edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import main.java.edu.umassmed.omega.commons.data.analysisRunElements.OmegaSNRRun;
import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;
import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrackingMeasuresDiffusivity;
import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import main.java.edu.umassmed.omega.commons.gui.GenericComboBox;
import main.java.edu.umassmed.omega.commons.gui.GenericPanel;
import main.java.edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import main.java.edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import main.java.edu.umassmed.omega.commons.libraries.OmegaDiffusivityLibrary;
import main.java.edu.umassmed.omega.commons.runnable.AnalyzerEvent;
import main.java.edu.umassmed.omega.commons.runnable.OmegaDiffusivityAnalyzer;
import main.java.edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.TMDConstants;

public class TMDRunPanel extends GenericPanel implements
        OmegaMessageDisplayerPanelInterface {

	private static final long serialVersionUID = -1925743064869248360L;

	private static final Dimension VALUE_FIELDS_DIM = new Dimension(45, 20);
	private static final Dimension LBL_FIELDS_DIM = new Dimension(120, 20);

	private GenericComboBox<String> windowVal_cmb, logOption_cmb;
	private GenericComboBox<String> errorOption_cmb, snrAnalysis_cmb;

	private JButton run_btt;
	private final TMDPluginPanel pluginPanel;

	private OmegaDiffusivityAnalyzer analyzer;

	private boolean popSNR;
	private boolean isHandlingEvent;

	private List<OmegaAnalysisRun> loadedAnalysisRuns;
	final List<OmegaSNRRun> snrRuns;
	private OmegaSNRRun selectedSNRRun;

	public TMDRunPanel(final RootPaneContainer parent,
	        final TMDPluginPanel pluginPanel,
	        final List<OmegaAnalysisRun> analysisRuns) {
		super(parent);
		this.pluginPanel = pluginPanel;
		this.setLayout(new BorderLayout());

		this.loadedAnalysisRuns = analysisRuns;

		this.snrRuns = new ArrayList<>();
		this.selectedSNRRun = null;

		this.popSNR = false;
		this.isHandlingEvent = false;

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 2));
		// final JLabel lbl = new JLabel("");
		// mainPanel.add(lbl, BorderLayout.CENTER);
		this.add(mainPanel, BorderLayout.CENTER);

		final JPanel parametersDiffPanel = new JPanel();
		parametersDiffPanel.setLayout(new GridLayout(2, 1));
		parametersDiffPanel.setBorder(new TitledBorder(
				TMDConstants.PARAMETER_TMD));

		final JPanel windowPanel = new JPanel();
		windowPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel radius_lbl = new JLabel(
				TMDConstants.PARAMETER_DIFFUSIVITY_WINDOW + ":");
		radius_lbl.setPreferredSize(OmegaConstants.TEXT_SIZE);
		windowPanel.add(radius_lbl);
		this.windowVal_cmb = new GenericComboBox<String>(
				this.getParentContainer());
		this.windowVal_cmb.addItem(TMDConstants.PARAMETER_DIFFUSIVITY_WINDOW_3);
		this.windowVal_cmb.addItem(TMDConstants.PARAMETER_DIFFUSIVITY_WINDOW_5);
		this.windowVal_cmb
		        .addItem(TMDConstants.PARAMETER_DIFFUSIVITY_WINDOW_10);
		this.windowVal_cmb.setPreferredSize(TMDRunPanel.LBL_FIELDS_DIM);
		windowPanel.add(this.windowVal_cmb);
		parametersDiffPanel.add(windowPanel);

		final JPanel logOptionPanel = new JPanel();
		logOptionPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel logOption_lbl = new JLabel(
				TMDConstants.PARAMETER_DIFFUSIVITY_LOG_OPTION + ":");
		logOption_lbl.setPreferredSize(OmegaConstants.TEXT_SIZE);
		logOptionPanel.add(logOption_lbl);
		this.logOption_cmb = new GenericComboBox<String>(
				this.getParentContainer());
		this.logOption_cmb
		.addItem(TMDConstants.PARAMETER_DIFFUSIVITY_LOG_OPTION_LOG_ONLY);
		this.logOption_cmb
		.addItem(TMDConstants.PARAMETER_DIFFUSIVITY_LOG_OPTION_LOG_AND_LINEAR);
		this.logOption_cmb
		.addItem(TMDConstants.PARAMETER_DIFFUSIVITY_LOG_OPTION_LINEAR_ONLY);
		this.logOption_cmb.setPreferredSize(TMDRunPanel.LBL_FIELDS_DIM);
		logOptionPanel.add(this.logOption_cmb);
		parametersDiffPanel.add(logOptionPanel);

		mainPanel.add(parametersDiffPanel);

		final JPanel parametersErrPanel = new JPanel();
		parametersErrPanel.setLayout(new GridLayout(2, 1));
		parametersErrPanel.setBorder(new TitledBorder(
				TMDConstants.PARAMETER_TMDE));

		final JPanel errorOptionPanel = new JPanel();
		errorOptionPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel errorOption_lbl = new JLabel(
				TMDConstants.PARAMETER_ERROR_OPTION + ":");
		errorOption_lbl.setPreferredSize(OmegaConstants.TEXT_SIZE);
		errorOptionPanel.add(errorOption_lbl);
		this.errorOption_cmb = new GenericComboBox<String>(
		        this.getParentContainer());
		this.errorOption_cmb.setPreferredSize(TMDRunPanel.LBL_FIELDS_DIM);
		this.errorOption_cmb
		        .addItem(TMDConstants.PARAMETER_ERROR_OPTION_ENABLED);
		this.errorOption_cmb.addItem(TMDConstants.PARAMETER_ERROR_OPTION_ONLY);
		this.errorOption_cmb
		        .addItem(TMDConstants.PARAMETER_ERROR_OPTION_DISABLED);
		errorOptionPanel.add(this.errorOption_cmb);
		parametersErrPanel.add(errorOptionPanel);

		final JPanel snrRunPanel = new JPanel();
		snrRunPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel snrRun_lbl = new JLabel(TMDConstants.PARAMETER_ERROR_SNR
		        + ":");
		snrRun_lbl.setPreferredSize(OmegaConstants.TEXT_SIZE);
		snrRunPanel.add(snrRun_lbl);
		this.snrAnalysis_cmb = new GenericComboBox<String>(
		        this.getParentContainer());
		this.snrAnalysis_cmb.setPreferredSize(OmegaConstants.LARGE_TEXT_SIZE);
		snrRunPanel.add(this.snrAnalysis_cmb);
		parametersErrPanel.add(snrRunPanel);

		mainPanel.add(parametersErrPanel);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		this.run_btt = new JButton("Run");
		bottomPanel.add(this.run_btt);
		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void addListeners() {
		this.run_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMDRunPanel.this.startIntensityAnalyzer();
			}
		});
		this.snrAnalysis_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				TMDRunPanel.this.selectSNRRun();
			}
		});
	}

	private void startIntensityAnalyzer() {
		if (this.pluginPanel == null)
			return;
		// TODO Stop if thread already running (disable button would be better)
		// FIXME OPTION SHOULD BE GIVIN BY GUY HERE!!!!
		this.analyzer = new OmegaDiffusivityAnalyzer(this,
		        this.pluginPanel.getSegments(), this.getParameters(),
				this.selectedSNRRun,
		        this.pluginPanel.getSelectedTrackingMeasuresDiffusivityRun());
		this.analyzer.run();
	}

	public boolean areParametersValidated() {
		return true;
	}

	public String[] getParametersError() {
		return null;
	}

	public void updateAnalysisFields(final OmegaAnalysisRun analysisRun) {

	}

	public void updateRunFields(final List<OmegaParameter> parameters) {
		for (final OmegaParameter param : parameters) {
			if (param.getName().equals(
			        TMDConstants.PARAMETER_DIFFUSIVITY_WINDOW)) {
				this.windowVal_cmb.setSelectedItem(param.getStringValue());
			} else if (param.getName().equals(
			        TMDConstants.PARAMETER_DIFFUSIVITY_LOG_OPTION)) {
				this.logOption_cmb.setSelectedItem(param.getStringValue());
			} else if (param.getName().equals(
			        TMDConstants.PARAMETER_ERROR_OPTION)) {
				this.errorOption_cmb.setSelectedItem(param.getValue());
			} else if (param.getName().equals(TMDConstants.PARAMETER_ERROR_SNR)) {
				// SNR ANALYSIS HOW TO PASS IT ?
			} else {
				// TODO gestire errore
			}
		}
	}

	public void updateRunFieldsDefault() {

	}

	public List<OmegaParameter> getParameters() {
		if (!this.areParametersValidated())
			return null;
		final List<OmegaParameter> params = new ArrayList<OmegaParameter>();
		final int window = Integer.valueOf((String) this.windowVal_cmb
		        .getSelectedItem());
		params.add(new OmegaParameter(
		        TMDConstants.PARAMETER_DIFFUSIVITY_WINDOW, window));
		final String logOption = (String) this.logOption_cmb.getSelectedItem();
		params.add(new OmegaParameter(
		        TMDConstants.PARAMETER_DIFFUSIVITY_LOG_OPTION, logOption));
		final String errorOption = (String) this.errorOption_cmb
				.getSelectedItem();
		params.add(new OmegaParameter(TMDConstants.PARAMETER_ERROR_OPTION,
				errorOption));
		return params;
	}

	@Override
	public void updateMessageStatus(final OmegaMessageEvent evt) {
		final AnalyzerEvent siEvt = (AnalyzerEvent) evt;
		if (siEvt.needDialog()) {
			final GenericMessageDialog gd = new GenericMessageDialog(
					this.getParentContainer(), "Diffusivity Analyzer Warning",
					evt.getMessage(), false);
			gd.setVisible(true);
			gd.enableClose();
		}
		this.pluginPanel.updateStatus(evt.getMessage());
		if (siEvt.isEnded()) {
			final OmegaPluginEventResultsTrackingMeasuresDiffusivity rtmiEvent = new OmegaPluginEventResultsTrackingMeasuresDiffusivity(
			        this.pluginPanel.getPlugin(),
			        this.pluginPanel.getSelectedSegmentationRun(),
			        this.analyzer.getParameters(), this.analyzer.getSegments(),
			        this.analyzer.getNyResults(), this.analyzer.getMuResults(),
			        this.analyzer.getLogMuResults(),
			        this.analyzer.getDeltaTResults(),
			        this.analyzer.getLogDeltaTResults(),
			        this.analyzer.getGammaDResults(),
			        this.analyzer.getGammaDFromLogResults(),
			        this.analyzer.getGammaResults(),
			        this.analyzer.getGammaFromLogResults(),
			        this.analyzer.getSmssResults(),
			        this.analyzer.getSmssFromLogResults(),
					this.analyzer.getErrors(),
					this.analyzer.getErrorsFromLog(),
					this.analyzer.getSNRRun(),
					this.analyzer.getTrackiMeasuresDiffusivityRun());
			this.pluginPanel.getPlugin().fireEvent(rtmiEvent);
		}
	}

	protected void populateSNRCombo() {
		this.popSNR = true;
		this.snrAnalysis_cmb.removeAllItems();
		this.snrRuns.clear();
		this.snrAnalysis_cmb.setSelectedIndex(-1);
		this.selectedSNRRun = null;

		final OmegaParticleDetectionRun particleDetRun = this.pluginPanel
		        .getSelectedParticleDetectionRun();

		if (particleDetRun == null) {
			this.snrAnalysis_cmb.setEnabled(false);
			return;
		}

		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if ((analysisRun instanceof OmegaSNRRun)
					&& particleDetRun.getAnalysisRuns().contains(analysisRun)) {
				this.snrRuns.add((OmegaSNRRun) analysisRun);
				this.snrAnalysis_cmb.addItem(analysisRun.getName());
			}
		}
		if (this.snrRuns.isEmpty()) {
			this.snrAnalysis_cmb.setEnabled(false);
			this.popSNR = false;
			return;
		}

		this.popSNR = false;
		if (this.snrAnalysis_cmb.getItemCount() > 0) {
			this.snrAnalysis_cmb.setEnabled(true);
			this.snrAnalysis_cmb.setSelectedIndex(0);
		} else {
			this.snrAnalysis_cmb.setSelectedIndex(-1);
		}
	}

	private void selectSNRRun() {
		if (this.popSNR)
			return;
		final int index = this.snrAnalysis_cmb.getSelectedIndex();
		this.selectedSNRRun = null;
		if (index == -1)
			return;
		this.selectedSNRRun = this.snrRuns.get(index);
		if (!this.isHandlingEvent) {
			this.fireEventSelectionSNRRun();
		}

		final Double minD = OmegaDiffusivityLibrary
		        .computeMinimumDetectableD(this.selectedSNRRun
		                .getResultingImageMinimumSNR());
		System.out.println(minD);
	}

	public void selectSNRRun(final OmegaAnalysisRun analysisRun) {
		this.isHandlingEvent = true;
		int index = -1;
		if (this.snrRuns != null) {
			index = this.snrRuns.indexOf(analysisRun);
		}
		if (index == -1) {
			this.snrAnalysis_cmb.setSelectedItem(this.snrAnalysis_cmb
			        .getItemCount());
		} else {
			this.snrAnalysis_cmb.setSelectedIndex(index);
		}
		this.isHandlingEvent = false;
	}

	private void fireEventSelectionSNRRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
				this.pluginPanel.getPlugin(), this.selectedSNRRun);
		this.pluginPanel.getPlugin().fireEvent(event);
	}

	public void updateCombos(final List<OmegaAnalysisRun> analysisRuns) {
		this.loadedAnalysisRuns = analysisRuns;
	}
}
