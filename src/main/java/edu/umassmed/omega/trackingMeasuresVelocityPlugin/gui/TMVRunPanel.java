package edu.umassmed.omega.trackingMeasuresVelocityPlugin.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.coreElements.OmegaPlane;
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
				TMVRunPanel.this.startVelocityAnalyzer();
			}
		});
	}
	
	private void startVelocityAnalyzer() {
		if (this.pluginPanel == null)
			return;
		// TODO Stop if thread already running (disable button would be better)
		double physicalT = 1.0;
		int maxT = -1;
		if (this.pluginPanel.getSelectedImage() instanceof OmegaImage) {
			final OmegaImage img = (OmegaImage) this.pluginPanel
					.getSelectedImage();
			final OmegaImagePixels pixels = img.getDefaultPixels();
			maxT = pixels.getSizeT();
			if (pixels.getPhysicalSizeT() != -1) {
				physicalT = pixels.getPhysicalSizeT();
			}
		} else {
			for (final OmegaPlane f : this.pluginPanel
					.getSelectedParticleDetectionRun().getResultingParticles()
					.keySet()) {
				final int index = f.getIndex();
				if (maxT < index) {
					maxT = index;
				}
			}
		}
		final List<OmegaElement> selection = new ArrayList<OmegaElement>();
		selection.add(this.pluginPanel.getSelectedImage());
		selection.add(this.pluginPanel.getSelectedParticleDetectionRun());
		selection.add(this.pluginPanel.getSelectedParticleLinkingRun());
		selection.add(this.pluginPanel.getSelectedRelinkingRun());
		selection.add(this.pluginPanel.getSelectedSegmentationRun());
		this.analyzer = new OmegaVelocityAnalyzer(this, physicalT, maxT,
				this.pluginPanel.getSelectedSegmentationRun(),
				this.pluginPanel.getSegments(), null, selection);
		this.run_btt.setEnabled(false);
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
					this.analyzer.getSelections(),
					this.analyzer.getTrajectorySegmentationRun(),
					new ArrayList<OmegaParameter>(),
					this.analyzer.getSegments(),
					this.analyzer.getLocalSpeedResults(),
					this.analyzer.getLocalSpeedFromOriginResults(),
					this.analyzer.getLocalVelocityFromOriginResults(),
					this.analyzer.getAverageCurvilinearSpeedResults(),
					this.analyzer.getAverageStraightLineVelocityResults(),
					this.analyzer.getForwardProgressionLinearityResults());
			this.pluginPanel.getPlugin().fireEvent(rtmiEvent);
			this.run_btt.setEnabled(true);
		}
	}
}
