/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team: 
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli, 
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban, 
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;

import edu.umassmed.omega.commons.eventSystem.OmegaApplicationEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationImageSelectionEvent;
import edu.umassmed.omega.commons.exceptions.OmegaCoreLoadedElementNotFound;
import edu.umassmed.omega.commons.gui.GenericFrame;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.core.OmegaLogFileManager;
import edu.umassmed.omega.dataNew.OmegaLoadedData;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaSidePanel extends GenericPanel {

	private static final long serialVersionUID = -4565126277733287950L;

	private JLabel selected_lbl;
	private JButton arrowLeft_btt, arrowRight_btt;
	private JSlider elements_slider;
	private OmegaElementImagePanel imagePanel;

	private boolean isAttached;

	private final boolean isHandlingEvent;

	private OmegaLoadedData loadedData;
	private List<OmegaAnalysisRun> loadedAnalysisRuns;

	private OmegaGateway gateway;

	private int itemIndex;

	// private JDesktopPane desktopPane;

	public OmegaSidePanel(final RootPaneContainer parent) {
		super(parent);
		this.isAttached = true;
		this.isHandlingEvent = false;
		this.loadedData = null;
		this.gateway = null;

		this.setLayout(new BorderLayout());

		this.itemIndex = -1;
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.imagePanel.updateParentContainer(parent);
	}

	protected void initializePanel() {
		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		// this.desktopPane = new JDesktopPane();
		// this.getViewport().add(this.desktopPane);

		this.imagePanel = new OmegaElementImagePanel(this.getParentContainer());

		this.add(this.imagePanel, BorderLayout.CENTER);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		final Dimension dim = new Dimension(50, 20);

		this.arrowLeft_btt = new JButton("<");
		this.arrowLeft_btt.setPreferredSize(dim);
		this.arrowLeft_btt.setSize(dim);
		this.arrowLeft_btt.setEnabled(false);

		this.arrowRight_btt = new JButton(">");
		this.arrowRight_btt.setPreferredSize(dim);
		this.arrowRight_btt.setSize(dim);
		this.arrowLeft_btt.setEnabled(false);

		this.selected_lbl = new JLabel("No item selected");
		this.selected_lbl.setHorizontalAlignment(SwingConstants.CENTER);

		bottomPanel.add(this.arrowLeft_btt, BorderLayout.WEST);
		bottomPanel.add(this.selected_lbl, BorderLayout.CENTER);
		bottomPanel.add(this.arrowRight_btt, BorderLayout.EAST);

		// this.elements_slider = new JSlider(0, 0, 0);
		// this.elements_slider.setSnapToTicks(true);
		// this.elements_slider.setMajorTickSpacing(1);
		// this.elements_slider.setMinorTickSpacing(1);
		// this.elements_slider.setEnabled(false);
		//
		// bottomPanel.add(this.elements_slider);

		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void addListeners() {
		this.arrowLeft_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaSidePanel.this.manageChangeItem(-1);
			}
		});
		this.arrowRight_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaSidePanel.this.manageChangeItem(1);
			}
		});
		// this.elements_slider.addMouseListener(new MouseAdapter() {
		//
		// @Override
		// public void mouseReleased(final MouseEvent e) {
		// if (!OmegaSidePanel.this.elements_slider.isEnabled())
		// return;
		// final int index = OmegaSidePanel.this.elements_slider
		// .getValue();
		// OmegaSidePanel.this.updateCurrentElement(index);
		// }
		// });
	}

	private void manageChangeItem(final int mover) {
		if (this.itemIndex != -1) {
			this.computeNewItemIndex(mover);
		}
		this.manageItemChanged();
	}

	private void computeNewItemIndex(final int mover) {
		this.itemIndex += mover;
		final int size = this.loadedData.getLoadedDataSize();
		if (this.itemIndex < 1) {
			this.itemIndex = size;
		} else if (this.itemIndex > size) {
			this.itemIndex = 1;
		}
	}

	private void manageItemChanged() {
		this.redrawLabel();
		this.updateCurrentElement(this.itemIndex);
	}

	private void redrawLabel() {
		final StringBuffer buf = new StringBuffer();
		final int size = this.loadedData.getLoadedDataSize();
		if (this.itemIndex != -1) {
			buf.append("Item selected: ");
			buf.append(this.itemIndex);
			buf.append("/");
			buf.append(size);
		} else {
			buf.append("No item selected");
		}
		this.selected_lbl.setText(buf.toString());
		this.selected_lbl.revalidate();
		this.selected_lbl.repaint();
	}

	public boolean isAttached() {
		return this.isAttached;
	}

	public void setAttached(final boolean tof) {
		this.isAttached = tof;
	}

	private OmegaGUIFrame getOmegaGUIFrame() {
		final RootPaneContainer parent = this.getParentContainer();
		OmegaGUIFrame frame = null;
		if (parent instanceof GenericFrame) {
			final GenericFrame genericFrame = (GenericFrame) parent;
			frame = (OmegaGUIFrame) genericFrame.getParent();
		} else {
			frame = (OmegaGUIFrame) parent;
		}

		return frame;
	}

	private void sendApplicationImageSelectionEvent(final OmegaImage img) {
		final OmegaGUIFrame frame = this.getOmegaGUIFrame();
		final OmegaApplicationEvent event = new OmegaApplicationImageSelectionEvent(
		        OmegaApplicationEvent.SOURCE_SIDE_BAR, img);
		frame.sendApplicationEvent(event);
	}

	private void updateCurrentElement(final int index) {
		if (index > 0) {
			OmegaElement element = null;
			try {
				element = OmegaSidePanel.this.loadedData.getElement(index);

			} catch (final OmegaCoreLoadedElementNotFound ex) {
				OmegaLogFileManager.handleCoreException(ex);
				// TODO should i do something here?
				return;
			}
			if (!this.isHandlingEvent) {
				if (element instanceof OmegaImage) {
					this.sendApplicationImageSelectionEvent((OmegaImage) element);
				} else {
					this.sendApplicationImageSelectionEvent(null);
				}
			}
			this.imagePanel.update(element, this.loadedAnalysisRuns,
			        OmegaSidePanel.this.gateway);
		} else {
			this.imagePanel.update(null, this.loadedAnalysisRuns, this.gateway);
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
			this.arrowLeft_btt.setEnabled(true);
			this.arrowRight_btt.setEnabled(true);
			this.itemIndex = 1;
			this.manageItemChanged();
		} else {
			this.arrowLeft_btt.setEnabled(false);
			this.arrowRight_btt.setEnabled(false);
			this.itemIndex = -1;
			this.manageItemChanged();
		}
	}

	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
	        final boolean selection) {
		this.imagePanel.updateTrajectories(trajectories, selection);
	}

	public void selectImage(final OmegaImage image) {
		try {
			this.itemIndex = this.loadedData.getElementIndex(image);
			this.manageItemChanged();
		} catch (final OmegaCoreLoadedElementNotFound ex) {
			OmegaLogFileManager.handleCoreException(ex);
			// TODO should I do something here?
		}
	}

	public void selectParticleDetectionRun(
	        final OmegaParticleDetectionRun analysisRun) {
		this.imagePanel.selectParticleDetectionRun(analysisRun);
	}

	public void selectParticleLinkingRun(
	        final OmegaParticleLinkingRun analysisRun) {
		this.imagePanel.selectParticleLinkingRun(analysisRun);
	}

	public void selectTrajectoriesManagerRun(
	        final OmegaParticleLinkingRun analysisRun) {
		this.imagePanel.selectTrajectoriesManagerRun(analysisRun);
	}
}
