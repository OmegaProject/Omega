package main.java.edu.umassmed.omega.trackingMeasuresIntensityPlugin.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import main.java.edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;
import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrackingMeasuresIntensity;
import main.java.edu.umassmed.omega.commons.gui.GenericPanel;
import main.java.edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import main.java.edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import main.java.edu.umassmed.omega.commons.runnable.AnalyzerEvent;
import main.java.edu.umassmed.omega.commons.runnable.OmegaIntensityAnalyzer;

public class TMIRunPanel extends GenericPanel implements
OmegaMessageDisplayerPanelInterface {

	private static final long serialVersionUID = -1925743064869248360L;

	private JButton run_btt;
	private final TMIPluginPanel pluginPanel;

	private OmegaIntensityAnalyzer analyzer;

	public TMIRunPanel(final RootPaneContainer parent,
	        final TMIPluginPanel pluginPanel) {
		super(parent);
		this.pluginPanel = pluginPanel;
		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		final JLabel lbl = new JLabel("");
		mainPanel.add(lbl, BorderLayout.CENTER);
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
				TMIRunPanel.this.startIntensityAnalyzer();
			}
		});
	}

	private void startIntensityAnalyzer() {
		if (this.pluginPanel == null)
			return;
		this.analyzer = new OmegaIntensityAnalyzer(this,
		        this.pluginPanel.getSegments());
		this.analyzer.run();
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
					this.pluginPanel.getSelectedSegmentationRun(),
					new ArrayList<OmegaParameter>(),
					this.analyzer.getSegments(),
					this.analyzer.getPeakSignalsResults(),
					this.analyzer.getMeanSignalsResults(),
					this.analyzer.getLocalBackgroundsResults(),
					this.analyzer.getLocalSNRsResults());
			this.pluginPanel.getPlugin().fireEvent(rtmiEvent);
		}
	}
}
