package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.exceptions.OmegaLoadedElementNotFound;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.dataNew.OmegaLoadedData;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;

public class OmegaSidePanel extends GenericPanel {

	private static final long serialVersionUID = -4565126277733287950L;

	private JSlider elements_slider;
	private OmegaSideSplitPanel splitPanel;

	private boolean isAttached;

	private OmegaLoadedData loadedData;
	private List<OmegaAnalysisRun> loadedAnalysisRuns;

	private OmegaGateway gateway;

	// private JDesktopPane desktopPane;

	public OmegaSidePanel(final RootPaneContainer parent) {
		super(parent);
		this.isAttached = true;
		this.loadedData = null;
		this.gateway = null;

		this.setLayout(new BorderLayout());
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.splitPanel.updateParentContainer(parent);
	}

	protected void initializePanel() {
		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		// this.desktopPane = new JDesktopPane();
		// this.getViewport().add(this.desktopPane);

		this.splitPanel = new OmegaSideSplitPanel(this.getParentContainer());

		this.add(this.splitPanel, BorderLayout.CENTER);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		this.elements_slider = new JSlider(0, 0, 0);
		this.elements_slider.setSnapToTicks(true);
		this.elements_slider.setMajorTickSpacing(1);
		this.elements_slider.setMinorTickSpacing(1);
		this.elements_slider.setEnabled(false);

		bottomPanel.add(this.elements_slider);

		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void addListeners() {
		this.elements_slider.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (!OmegaSidePanel.this.elements_slider.isEnabled())
					return;
				final int index = OmegaSidePanel.this.elements_slider
				        .getValue();
				OmegaSidePanel.this.updateCurrentElement(index);
			}
		});
	}

	public boolean isAttached() {
		return this.isAttached;
	}

	public void setAttached(final boolean tof) {
		this.isAttached = tof;
	}

	private void updateCurrentElement(final int index) {
		if (index > 0) {
			OmegaElement element = null;
			try {
				element = OmegaSidePanel.this.loadedData.getElement(index);

			} catch (final OmegaLoadedElementNotFound ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
				return;
			}
			this.splitPanel.update(element, this.loadedAnalysisRuns,
			        OmegaSidePanel.this.gateway);
		} else {
			this.splitPanel.update(null, this.loadedAnalysisRuns, this.gateway);
		}
	}

	public void updateGUI(final OmegaLoadedData loadedData,
	        final List<OmegaAnalysisRun> loadedAnalysisRuns,
	        final OmegaGateway gateway) {
		this.loadedData = loadedData;
		this.loadedAnalysisRuns = loadedAnalysisRuns;
		this.gateway = gateway;
		final int dataSize = loadedData.getLoadedDataSize();
		if (dataSize > 0) {
			this.elements_slider.setMinimum(1);
			this.elements_slider.setValue(1);
			this.elements_slider.setMaximum(dataSize);
			this.elements_slider.setEnabled(true);
			this.elements_slider.repaint();
			this.updateCurrentElement(1);
		} else {
			this.elements_slider.setMinimum(0);
			this.elements_slider.setValue(0);
			this.elements_slider.setMaximum(0);
			this.elements_slider.setEnabled(false);
			this.elements_slider.repaint();
			this.updateCurrentElement(0);
		}
	}
}
