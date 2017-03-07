package edu.umassmed.omega.trackingMeasuresMobilityPlugin.gui;

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
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrackingMeasuresMobility;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.commons.runnable.AnalyzerEvent;
import edu.umassmed.omega.commons.runnable.OmegaMobilityAnalyzer;

public class TMMRunPanel extends GenericPanel implements
        OmegaMessageDisplayerPanelInterface {
	
	private static final long serialVersionUID = -1925743064869248360L;
	
	private JButton run_btt;
	private final TMMPluginPanel pluginPanel;
	
	private OmegaMobilityAnalyzer analyzer;
	
	public TMMRunPanel(final RootPaneContainer parent,
			final TMMPluginPanel pluginPanel) {
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
				TMMRunPanel.this.startMobilityAnalyzer();
			}
		});
	}
	
	private void startMobilityAnalyzer() {
		if (this.pluginPanel == null)
			return;
		// TODO Stop if thread already running (disable button would be better)
		final int maxT = this.pluginPanel.getSelectedImage().getDefaultPixels()
				.getSizeT();
		this.analyzer = new OmegaMobilityAnalyzer(this, maxT,
				this.pluginPanel.getSegments());
		this.run_btt.setEnabled(false);
		this.analyzer.run();
	}
	
	@Override
	public void updateMessageStatus(final OmegaMessageEvent evt) {
		final AnalyzerEvent siEvt = (AnalyzerEvent) evt;
		if (siEvt.needDialog()) {
			final GenericMessageDialog gd = new GenericMessageDialog(
					this.getParentContainer(), "Mobility Analyzer Warning",
					evt.getMessage(), false);
			gd.setVisible(true);
		}
		this.pluginPanel.updateStatus(evt.getMessage());
		if (siEvt.isEnded()) {
			final OmegaPluginEventResultsTrackingMeasuresMobility rtmiEvent = new OmegaPluginEventResultsTrackingMeasuresMobility(
			        this.pluginPanel.getPlugin(),
			        this.pluginPanel.getSelectedSegmentationRun(),
			        new ArrayList<OmegaParameter>(),
			        this.analyzer.getSegments(),
			        this.analyzer.getDistancesResults(),
			        this.analyzer.getDisplacementsResults(),
			        this.analyzer.getMaxDisplacementsResults(),
			        this.analyzer.getTotalTimeTraveledResults(),
			        this.analyzer.getConfinementRatioResults(),
			        this.analyzer.getAnglesAndDirectionalChangesResults());
			this.pluginPanel.getPlugin().fireEvent(rtmiEvent);
			this.run_btt.setEnabled(true);
		}
	}
}
