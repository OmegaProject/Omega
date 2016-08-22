package edu.umassmed.omega.trackingMeasuresVelocityPlugin.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrackingMeasuresVelocity;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.commons.runnable.AnalyzerEvent;
import edu.umassmed.omega.commons.runnable.OmegaVelocityAnalyzer;

public class TMVRunPanel extends GenericPanel implements
OmegaMessageDisplayerPanelInterface {

	private static final long serialVersionUID = -1925743064869248360L;

	private JButton run_btt;
	private final TMVPluginPanel pluginPanel;

	private OmegaVelocityAnalyzer analyzer;

	public TMVRunPanel(final RootPaneContainer parent,
	        final TMVPluginPanel pluginPanel) {
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
				TMVRunPanel.this.startIntensityAnalyzer();
			}
		});
	}

	private void startIntensityAnalyzer() {
		if (this.pluginPanel == null)
			return;
		// TODO Stop if thread already running (disable button would be better)
		final int maxT = this.pluginPanel.getSelectedImage().getDefaultPixels()
		        .getSizeT();
		this.analyzer = new OmegaVelocityAnalyzer(this, maxT,
		        this.pluginPanel.getSegments());
		this.analyzer.run();
	}

	@Override
	public void updateMessageStatus(final OmegaMessageEvent evt) {
		final AnalyzerEvent siEvt = (AnalyzerEvent) evt;
		if (siEvt.needDialog()) {
			final GenericMessageDialog gd = new GenericMessageDialog(
			        this.getParentContainer(), "Velocity Analyzer Warning",
			        evt.getMessage(), false);
			gd.setVisible(true);
		}
		this.pluginPanel.updateStatus(evt.getMessage());
		if (siEvt.isEnded()) {
			final OmegaPluginEventResultsTrackingMeasuresVelocity rtmiEvent = new OmegaPluginEventResultsTrackingMeasuresVelocity(
			        this.pluginPanel.getPlugin(),
			        this.pluginPanel.getSelectedSegmentationRun(),
			        new ArrayList<OmegaParameter>(),
			        this.analyzer.getSegments(),
			        this.analyzer.getLocalSpeedResults(),
			        this.analyzer.getLocalVelocityResults(),
			        this.analyzer.getAverageCurvilinearSpeedResults(),
			        this.analyzer.getAverageStraightLineVelocityResults(),
			        this.analyzer.getForwardProgressionLinearityResults());
			this.pluginPanel.getPlugin().fireEvent(rtmiEvent);
		}
	}
}
