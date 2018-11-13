package edu.umassmed.omega.omegaTrackingMeasuresIntensityPlugin.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.constants.OmegaAlgorithmParameterConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaSNRRun;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrackingMeasuresIntensity;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.gui.GenericComboBox;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.commons.runnable.AnalyzerEvent;
import edu.umassmed.omega.commons.runnable.OmegaIntensityAnalyzer;

public class OmegaTrackingMeasuresIntensityRunPanel extends GenericPanel implements
		OmegaMessageDisplayerPanelInterface {

	private static final long serialVersionUID = -1925743064869248360L;

	private JButton run_btt;
	private final OmegaTrackingMeasuresIntensityPluginPanel pluginPanel;

	private OmegaIntensityAnalyzer analyzer;
	
	private JCheckBox snrEnable_ckb;
	private GenericComboBox<String> snrAnalysis_cmb;

	private boolean popSNR;
	private boolean isHandlingEvent;
	
	private List<OmegaAnalysisRun> loadedAnalysisRuns;
	final List<OmegaSNRRun> snrRuns;
	private OmegaSNRRun selectedSNRRun;

	public OmegaTrackingMeasuresIntensityRunPanel(final RootPaneContainer parent,
			final OmegaTrackingMeasuresIntensityPluginPanel pluginPanel,
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
		mainPanel.setLayout(new GridLayout(2, 1));
		final JPanel snrEnablePanel = new JPanel();
		snrEnablePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.snrEnable_ckb = new JCheckBox(
				OmegaAlgorithmParameterConstants.PARAM_SNR_USE);
		this.snrEnable_ckb.setPreferredSize(OmegaGUIConstants.LARGE_TEXT_SIZE);
		snrEnablePanel.add(this.snrEnable_ckb);
		mainPanel.add(snrEnablePanel);

		final JPanel snrRunPanel = new JPanel();
		snrRunPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel snrRun_lbl = new JLabel(
				OmegaAlgorithmParameterConstants.PARAM_ERROR_SNR + ":");
		snrRun_lbl.setPreferredSize(OmegaGUIConstants.LARGE_TEXT_SIZE);
		snrRunPanel.add(snrRun_lbl);
		this.snrAnalysis_cmb = new GenericComboBox<String>(
				this.getParentContainer());
		this.snrAnalysis_cmb
				.setPreferredSize(OmegaGUIConstants.LARGE_TEXT_SIZE);
		snrRunPanel.add(this.snrAnalysis_cmb);
		this.snrAnalysis_cmb.setEnabled(false);
		mainPanel.add(snrRunPanel);

		mainPanel.add(new JLabel(""));
		mainPanel.add(new JLabel(""));
		this.add(mainPanel, BorderLayout.CENTER);

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
				OmegaTrackingMeasuresIntensityRunPanel.this.startIntensityAnalyzer();
			}
		});
		this.snrAnalysis_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				OmegaTrackingMeasuresIntensityRunPanel.this.selectSNRRun();
			}
		});
		this.snrEnable_ckb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTrackingMeasuresIntensityRunPanel.this.enableSNR();
			}
		});
	}
	
	private void enableSNR() {
		if (this.snrEnable_ckb.isSelected()) {
			// this.snrAnalysis_cmb.setSelectedIndex(0);
			this.snrAnalysis_cmb.setEnabled(true);
			this.populateSNRCombo();
			if (this.snrAnalysis_cmb.getItemCount() == 0) {
				this.run_btt.setEnabled(false);
			} else {
				this.run_btt.setEnabled(true);
			}
		} else {
			this.snrAnalysis_cmb.setSelectedIndex(-1);
			this.snrAnalysis_cmb.setEnabled(false);
			this.run_btt.setEnabled(true);
		}
	}

	private void startIntensityAnalyzer() {
		if (this.pluginPanel == null)
			return;
		final List<OmegaElement> selection = new ArrayList<OmegaElement>();
		selection.add(this.pluginPanel.getSelectedImage());
		selection.add(this.pluginPanel.getSelectedParticleDetectionRun());
		selection.add(this.pluginPanel.getSelectedParticleLinkingRun());
		selection.add(this.pluginPanel.getSelectedRelinkingRun());
		selection.add(this.pluginPanel.getSelectedSegmentationRun());
		this.analyzer = new OmegaIntensityAnalyzer(this,
				this.pluginPanel.getSelectedSegmentationRun(),
				this.pluginPanel.getSegments(), this.getParameters(), selection);
		if (this.snrEnable_ckb.isEnabled()) {
			this.analyzer.setSNRRun(this.selectedSNRRun);
		}
		this.run_btt.setEnabled(false);
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
					OmegaAlgorithmParameterConstants.PARAM_SNR_USE)) {
				this.snrEnable_ckb.setSelected(Boolean.valueOf(param
						.getStringValue()));
			} else if (param.getName().equals(
					OmegaAlgorithmParameterConstants.PARAM_ERROR_SNR)) {
				this.snrAnalysis_cmb.setSelectedItem(param.getStringValue());
			} else {
				// TODO gestire errore
			}
		}
	}
	
	public void updateRunFieldsDefault() {
		this.snrEnable_ckb.setSelected(false);
	}

	public List<OmegaParameter> getParameters() {
		if (!this.areParametersValidated())
			return null;
		final List<OmegaParameter> params = new ArrayList<OmegaParameter>();
		params.add(new OmegaParameter(
				OmegaAlgorithmParameterConstants.PARAM_SNR_USE, String
						.valueOf(this.snrEnable_ckb.isSelected())));
		if (this.snrEnable_ckb.isSelected()) {
			params.add(new OmegaParameter(
					OmegaAlgorithmParameterConstants.PARAM_ERROR_SNR,
					this.selectedSNRRun.getName()));
		}
		return params;
	}

	@Override
	public void updateMessageStatus(final OmegaMessageEvent evt) {
		final AnalyzerEvent siEvt = (AnalyzerEvent) evt;
		if (siEvt.needDialog()) {
			final GenericMessageDialog gd = new GenericMessageDialog(
					this.getParentContainer(), "Intensity Analyzer Warning",
					evt.getMessage(), false);
			gd.setVisible(true);
		}
		this.pluginPanel.updateStatus(evt.getMessage());
		if (siEvt.isEnded()) {
			final OmegaPluginEventResultsTrackingMeasuresIntensity rtmiEvent = new OmegaPluginEventResultsTrackingMeasuresIntensity(
					this.pluginPanel.getPlugin(),
					this.analyzer.getSelections(),
					this.analyzer.getTrajectorySegmentationRun(),
					this.analyzer.getParameters(), this.analyzer.getSegments(),
					this.analyzer.getPeakSignalsResults(),
					this.analyzer.getCentroidSignalsResults(),
					this.analyzer.getPeakSignalsLocalResults(),
					this.analyzer.getCentroidSignalsLocalResults(),
					this.analyzer.getMeanSignalsResults(),
					this.analyzer.getBackgroundsResults(),
					this.analyzer.getNoisesResults(),
					this.analyzer.getSNRsResults(),
					this.analyzer.getAreasResults(),
					this.analyzer.getMeanSignalsLocalResults(),
					this.analyzer.getBackgroundsLocalResults(),
					this.analyzer.getNoisesLocalResults(),
					this.analyzer.getSNRsLocalResults(),
					this.analyzer.getAreasLocalResults(),
					this.analyzer.getSNRRun());
			this.pluginPanel.getPlugin().fireEvent(rtmiEvent);
			this.run_btt.setEnabled(true);
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

		if (!this.snrAnalysis_cmb.isEnabled())
			return;
		
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
		this.populateSNRCombo();
	}
	
	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.snrAnalysis_cmb.updateParentContainer(parent);
	}
}
