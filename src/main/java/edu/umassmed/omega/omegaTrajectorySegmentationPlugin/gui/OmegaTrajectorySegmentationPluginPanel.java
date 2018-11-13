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
package edu.umassmed.omega.omegaTrajectorySegmentationPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaAlgorithmParameterConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.constants.OmegaGenericConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainerInterface;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OrphanedAnalysisContainer;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrajectoriesSegmentation;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSegments;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionImage;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionOrphaned;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventTrajectories;
import edu.umassmed.omega.commons.exceptions.OmegaPluginExceptionStatusPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.gui.GenericStatusPanel;
import edu.umassmed.omega.commons.gui.GenericTrackingResultsPanel;
import edu.umassmed.omega.commons.gui.GenericTrajectoriesBrowserPanel;
import edu.umassmed.omega.commons.gui.GenericTrajectoryInformationPanel;
import edu.umassmed.omega.commons.gui.dialogs.GenericConfirmationDialog;
import edu.umassmed.omega.commons.gui.interfaces.GenericTrajectoriesBrowserContainerInterface;
import edu.umassmed.omega.commons.pluginArchetypes.OmegaPluginArchetype;
import edu.umassmed.omega.omegaTrajectoryEditingPlugin.OmegaTrajectoryEditingPluginConstants;
import edu.umassmed.omega.omegaTrajectorySegmentationPlugin.OmegaTrajectorySegmentationPluginConstants;
import edu.umassmed.omega.omegaTrajectorySegmentationPlugin.actions.OmegaTrajectorySegmentationAction;

public class OmegaTrajectorySegmentationPluginPanel extends GenericPluginPanel implements
		GenericTrajectoriesBrowserContainerInterface {
	
	private static final long serialVersionUID = -5740459087763362607L;
	
	private final OmegaGateway gateway;
	
	private JComboBox<String> images_cmb, particles_cmb, trajectories_cmb,
			trajectoriesRelinking_cmb, trajectoriesSegmentation_cmb;
	private JButton save_btt, undo_btt, redo_btt, cancel_btt;
	private boolean popImages, popParticles, popTrajectories, popTrajRelinking,
			popTrajSegmentation;
	
	private boolean isHandlingEvent;
	
	private JMenu ts_mn;
	private JMenuItem save_itm, undo_itm, redo_itm, cancel_itm,
			preferences_itm;
	
	private ActionListener save_al, undo_al, redo_al, cancel_al;
	
	private JTabbedPane tabbedPane;
	
	private GenericTrackingResultsPanel resPanel;
	
	private GenericTrajectoriesBrowserPanel tbPanel;
	private OmegaTrajectorySegmentationSegmenterPanel tsPanel;
	private GenericStatusPanel statusPanel;
	private GenericTrajectoryInformationPanel currentTrajInfoPanel;
	private OmegaTrajectorySegmentationPreferencesDialog segmentPreferencesDialog;
	
	private List<OmegaImage> images;
	private OrphanedAnalysisContainer orphanedAnalysis;
	private OmegaAnalysisRunContainerInterface selectedImage;
	private List<OmegaAnalysisRun> loadedAnalysisRuns;
	private final Map<OmegaAnalysisRun, List<OmegaTrajectorySegmentationAction>> actions,
			cancelledActions;
	
	final List<OmegaParticleDetectionRun> particleDetectionRuns;
	private OmegaParticleDetectionRun selectedParticleDetectionRun;
	final List<OmegaParticleLinkingRun> particleLinkingRuns;
	private OmegaParticleLinkingRun selectedParticleLinkingRun;
	final List<OmegaTrajectoriesRelinkingRun> trajRelinkingRuns;
	private OmegaTrajectoriesRelinkingRun selectedTrajRelinkingRun;
	final List<OmegaTrajectoriesSegmentationRun> trajSegmentationRuns;
	private OmegaTrajectoriesSegmentationRun selectedTrajSegmentationRun,
			startingPointTrajSegmentationRun;
	
	private List<OmegaSegmentationTypes> segmTypesList;
	private OmegaSegmentationTypes currentSegmentationTypes;
	private JPanel topPanel;
	private JMenuItem hideDataSelection_mItm;

	private int lineWidth;
	
	public OmegaTrajectorySegmentationPluginPanel(final RootPaneContainer parent,
			final OmegaPluginArchetype plugin, final OmegaGateway gateway,
			final List<OmegaImage> images,
			final OrphanedAnalysisContainer orphanedAnalysis,
			final List<OmegaAnalysisRun> analysisRuns,
			final List<OmegaSegmentationTypes> segmTypesList, final int index,
			final Map<String, String> pluginOptions) {
		super(parent, plugin, index);
		
		this.gateway = gateway;

		for (final String key : pluginOptions.keySet()) {
			if (key.equals(OmegaGenericConstants.PREF_TRACK_LINE_SIZE)) {
				this.lineWidth = Integer.valueOf(pluginOptions
						.get(OmegaGenericConstants.PREF_TRACK_LINE_SIZE));
			}
		}
		
		// TODO probably to refactor and move somewhere else
		// TODO when loading trajectoryAnalyisisRun I should be able to set the
		// correct segmentationTypes related
		this.segmTypesList = segmTypesList;
		this.currentSegmentationTypes = segmTypesList.get(0);
		
		this.actions = new LinkedHashMap<>();
		this.cancelledActions = new LinkedHashMap<OmegaAnalysisRun, List<OmegaTrajectorySegmentationAction>>();
		
		this.selectedImage = null;
		this.particleDetectionRuns = new ArrayList<OmegaParticleDetectionRun>();
		this.selectedParticleDetectionRun = null;
		this.particleLinkingRuns = new ArrayList<OmegaParticleLinkingRun>();
		this.selectedParticleLinkingRun = null;
		this.trajRelinkingRuns = new ArrayList<OmegaTrajectoriesRelinkingRun>();
		this.selectedTrajRelinkingRun = null;
		this.trajSegmentationRuns = new ArrayList<OmegaTrajectoriesSegmentationRun>();
		this.selectedTrajSegmentationRun = null;
		
		this.images = images;
		this.orphanedAnalysis = orphanedAnalysis;
		this.loadedAnalysisRuns = analysisRuns;
		
		this.popImages = false;
		this.popParticles = false;
		this.popTrajectories = false;
		this.popTrajRelinking = false;
		this.popTrajSegmentation = false;
		this.isHandlingEvent = false;
		
		this.setPreferredSize(new Dimension(750, 500));
		this.setLayout(new BorderLayout());
		// this.createMenu();
		this.createAndAddWidgets();
		// this.loadedDataBrowserPanel.updateTree(images);
		this.createMenu();
		
		this.addListeners();
		
		this.populateImagesCombo();
	}
	
	private void createMenu() {
		final JMenuBar menuBar = super.getMenu();
		for (int i = 0; i < menuBar.getMenuCount(); i++) {
			final JMenu menu = menuBar.getMenu(i);
			if (!menu.getText().equals(OmegaGUIConstants.MENU_VIEW)) {
				continue;
			}
			this.hideDataSelection_mItm = new JMenuItem(
					OmegaGUIConstants.MENU_VIEW_HIDE_DATA_SELECTION);
			menu.add(this.hideDataSelection_mItm);
		}
		
		this.ts_mn = new JMenu(OmegaGUIConstants.MENU_EDIT);
		
		this.save_itm = new JMenuItem(OmegaGUIConstants.SAVE);
		this.ts_mn.add(this.save_itm);
		
		this.undo_itm = new JMenuItem(OmegaGUIConstants.UNDO);
		this.ts_mn.add(this.undo_itm);
		this.undo_itm.setEnabled(false);
		
		this.redo_itm = new JMenuItem(OmegaGUIConstants.REDO);
		this.ts_mn.add(this.redo_itm);
		this.redo_itm.setEnabled(false);
		
		this.cancel_itm = new JMenuItem(OmegaGUIConstants.UNDO_ALL);
		this.ts_mn.add(this.cancel_itm);
		this.cancel_itm.setEnabled(false);
		
		// this.preferences_itm = new JMenuItem(OmegaTrajectorySegmentationPluginConstants.PREFERENCES);
		// this.ts_mn.add(this.preferences_itm);
		
		menuBar.add(this.ts_mn);
	}
	
	private void createAndAddWidgets() {
		this.segmentPreferencesDialog = new OmegaTrajectorySegmentationPreferencesDialog(this,
				this.getParentContainer(), this.segmTypesList);
		
		this.topPanel = new JPanel();
		this.topPanel.setLayout(new GridLayout(5, 1));
		
		final JPanel p1 = new JPanel();
		p1.setLayout(new BorderLayout());
		final JLabel lbl1 = new JLabel(OmegaTrajectoryEditingPluginConstants.SELECT_IMAGE);
		lbl1.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		p1.add(lbl1, BorderLayout.WEST);
		this.images_cmb = new JComboBox<String>();
		this.images_cmb
				.setMaximumRowCount(OmegaGUIConstants.COMBOBOX_MAX_OPTIONS);
		this.images_cmb.setEnabled(false);
		p1.add(this.images_cmb, BorderLayout.CENTER);
		this.topPanel.add(p1);
		
		final JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		final JLabel lbl2 = new JLabel(OmegaTrajectoryEditingPluginConstants.SELECT_TRACKS_SPOT);
		lbl2.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		p2.add(lbl2, BorderLayout.WEST);
		this.particles_cmb = new JComboBox<String>();
		this.particles_cmb
				.setMaximumRowCount(OmegaGUIConstants.COMBOBOX_MAX_OPTIONS);
		this.particles_cmb.setEnabled(false);
		p2.add(this.particles_cmb, BorderLayout.CENTER);
		this.topPanel.add(p2);
		
		final JPanel p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		final JLabel lbl3 = new JLabel(OmegaTrajectoryEditingPluginConstants.SELECT_TRACKS_LINKING);
		lbl3.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		p3.add(lbl3, BorderLayout.WEST);
		this.trajectories_cmb = new JComboBox<String>();
		this.trajectories_cmb
				.setMaximumRowCount(OmegaGUIConstants.COMBOBOX_MAX_OPTIONS);
		this.trajectories_cmb.setEnabled(false);
		p3.add(this.trajectories_cmb, BorderLayout.CENTER);
		this.topPanel.add(p3);
		
		final JPanel p4 = new JPanel();
		p4.setLayout(new BorderLayout());
		final JLabel lbl4 = new JLabel(OmegaTrajectoryEditingPluginConstants.SELECT_TRACKS_ADJ);
		lbl4.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		p4.add(lbl4, BorderLayout.WEST);
		this.trajectoriesRelinking_cmb = new JComboBox<String>();
		this.trajectoriesRelinking_cmb
				.setMaximumRowCount(OmegaGUIConstants.COMBOBOX_MAX_OPTIONS);
		this.trajectoriesRelinking_cmb.setEnabled(false);
		p4.add(this.trajectoriesRelinking_cmb, BorderLayout.CENTER);
		this.topPanel.add(p4);
		
		final JPanel p5 = new JPanel();
		p5.setLayout(new BorderLayout());
		final JLabel lbl5 = new JLabel(OmegaTrajectoryEditingPluginConstants.SELECT_TRACKS_SEGM);
		lbl5.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		p5.add(lbl5, BorderLayout.WEST);
		this.trajectoriesSegmentation_cmb = new JComboBox<String>();
		this.trajectoriesSegmentation_cmb
				.setMaximumRowCount(OmegaGUIConstants.COMBOBOX_MAX_OPTIONS);
		this.trajectoriesSegmentation_cmb.setEnabled(false);
		p5.add(this.trajectoriesSegmentation_cmb, BorderLayout.CENTER);
		this.topPanel.add(p5);
		
		this.add(this.topPanel, BorderLayout.NORTH);
		
		this.tabbedPane = new JTabbedPane();
		
		final JPanel browser = new JPanel();
		browser.setLayout(new BorderLayout());
		
		this.tbPanel = new GenericTrajectoriesBrowserPanel(
				this.getParentContainer(), this, this.gateway, true, true);
		browser.add(this.tbPanel, BorderLayout.CENTER);
		
		this.currentTrajInfoPanel = new GenericTrajectoryInformationPanel(
				this.getParentContainer(), this);
		browser.add(this.currentTrajInfoPanel, BorderLayout.SOUTH);
		
		this.tabbedPane.add(OmegaGUIConstants.TAB_TRACK_TRACK_BROWSER, browser);
		
		this.tsPanel = new OmegaTrajectorySegmentationSegmenterPanel(this.getParentContainer(), this,
				this.currentSegmentationTypes, this.lineWidth);
		this.tabbedPane.add(OmegaTrajectorySegmentationPluginConstants.SEGMENTATION_TABNAME, this.tsPanel);
		
		this.resPanel = new GenericTrackingResultsPanel(
				this.getParentContainer());
		this.tabbedPane.add("Segmentation results", this.resPanel);
		
		this.add(this.tabbedPane, BorderLayout.CENTER);
		
		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		this.save_btt = new JButton(OmegaGUIConstants.SAVE);
		this.save_btt.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE);
		this.save_btt.setSize(OmegaGUIConstants.BUTTON_SIZE);
		buttonPanel.add(this.save_btt);
		
		this.undo_btt = new JButton(OmegaGUIConstants.UNDO);
		this.undo_btt.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE);
		this.undo_btt.setSize(OmegaGUIConstants.BUTTON_SIZE);
		buttonPanel.add(this.undo_btt);
		this.undo_btt.setEnabled(false);
		
		this.redo_btt = new JButton(OmegaGUIConstants.REDO);
		this.redo_btt.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE);
		this.redo_btt.setSize(OmegaGUIConstants.BUTTON_SIZE);
		buttonPanel.add(this.redo_btt);
		this.redo_btt.setEnabled(false);
		
		this.cancel_btt = new JButton(OmegaGUIConstants.UNDO_ALL);
		this.cancel_btt.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE);
		this.cancel_btt.setSize(OmegaGUIConstants.BUTTON_SIZE);
		buttonPanel.add(this.cancel_btt);
		this.cancel_btt.setEnabled(false);
		
		bottomPanel.add(buttonPanel, BorderLayout.NORTH);
		
		this.statusPanel = new GenericStatusPanel(1);
		bottomPanel.add(this.statusPanel, BorderLayout.SOUTH);
		
		this.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void addListeners() {
		this.tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent evt) {
				// TODO
			}
		});
		this.images_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTrajectorySegmentationPluginPanel.this.selectImage();
			}
		});
		this.particles_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaTrajectorySegmentationPluginPanel.this.selectParticleDetectionRun();
			}
		});
		this.trajectories_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaTrajectorySegmentationPluginPanel.this.selectParticleLinkingRun();
			}
		});
		this.trajectoriesRelinking_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTrajectorySegmentationPluginPanel.this.selectTrajectoriesRelinkingRun();
			}
		});
		this.trajectoriesSegmentation_cmb
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						OmegaTrajectorySegmentationPluginPanel.this.selectTrajectoriesSegmentationRun();
					}
				});
		this.save_btt.addActionListener(this
				.getSaveNewAllActionsActionListener());
		this.undo_btt.addActionListener(this.getUndoLastActionActionListener());
		this.redo_btt.addActionListener(this.getRedoLastActionActionListener());
		this.cancel_btt.addActionListener(this
				.getCancelAllActionsActionListener());
		this.save_itm.addActionListener(this
				.getSaveNewAllActionsActionListener());
		this.undo_itm.addActionListener(this.getUndoLastActionActionListener());
		this.redo_itm.addActionListener(this.getRedoLastActionActionListener());
		this.cancel_itm.addActionListener(this
				.getCancelAllActionsActionListener());
		// this.preferences_itm.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(final ActionEvent e) {
		// OmegaTrajectorySegmentationPluginPanel.this.openPreferencesDialog();
		// }
		// });
		this.hideDataSelection_mItm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTrajectorySegmentationPluginPanel.this.handleHideDataSelection();
			}
		});
	}
	
	private void handleHideDataSelection() {
		if (this.hideDataSelection_mItm.getText().equals(
				OmegaGUIConstants.MENU_VIEW_HIDE_DATA_SELECTION)) {
			this.remove(this.topPanel);
			this.hideDataSelection_mItm
					.setText(OmegaGUIConstants.MENU_VIEW_SHOW_DATA_SELECTION);
		} else {
			this.add(this.topPanel, BorderLayout.NORTH);
			this.hideDataSelection_mItm
					.setText(OmegaGUIConstants.MENU_VIEW_HIDE_DATA_SELECTION);
		}
		this.revalidate();
		this.repaint();
	}
	
	private void openPreferencesDialog() {
		this.segmentPreferencesDialog.setVisible(true);
	}
	
	private void activateNeededButton() {
		final Map<OmegaAnalysisRun, List<OmegaTrajectorySegmentationAction>> actions = this.actions;
		final Map<OmegaAnalysisRun, List<OmegaTrajectorySegmentationAction>> cancelledActions = this.cancelledActions;
		OmegaTrajectoriesSegmentationRun currentModification = this.selectedTrajSegmentationRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajSegmentationRun;
		}
		if (actions.containsKey(currentModification)) {
			this.setEnableUndo(true);
			this.setEnableCancel(true);
		} else {
			this.setEnableUndo(false);
			this.setEnableCancel(false);
		}
		if (cancelledActions.containsKey(currentModification)) {
			this.setEnableRedo(true);
		} else {
			this.setEnableRedo(false);
		}
	}
	
	private ActionListener getSaveNewAllActionsActionListener() {
		if (this.save_al == null) {
			this.save_al = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					OmegaTrajectorySegmentationPluginPanel.this.saveAllActions();
				}
			};
		}
		return this.save_al;
	}
	
	private ActionListener getUndoLastActionActionListener() {
		if (this.undo_al == null) {
			this.undo_al = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					OmegaTrajectorySegmentationPluginPanel.this.undoLastAction();
				}
			};
		}
		return this.undo_al;
	}
	
	private ActionListener getRedoLastActionActionListener() {
		if (this.redo_al == null) {
			this.redo_al = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					OmegaTrajectorySegmentationPluginPanel.this.redoLastAction();
				}
			};
		}
		return this.redo_al;
	}
	
	private ActionListener getCancelAllActionsActionListener() {
		if (this.cancel_al == null) {
			this.cancel_al = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					OmegaTrajectorySegmentationPluginPanel.this.cancelAllActions();
				}
			};
		}
		return this.cancel_al;
	}
	
	private void saveAllActions() {
		final StringBuffer buf = new StringBuffer();
		buf.append(OmegaTrajectorySegmentationPluginConstants.SAVE_CONFIRM_MSG);
		
		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
				this.getParentContainer(), OmegaTrajectorySegmentationPluginConstants.SAVE_CONFIRM,
				buf.toString(), true);
		dialog.setVisible(true);
		if (!dialog.getConfirmation())
			return;
		
		final List<OmegaElement> selection = new ArrayList<OmegaElement>();
		selection.add((OmegaElement) this.selectedImage);
		selection.add(this.selectedParticleDetectionRun);
		selection.add(this.selectedParticleLinkingRun);
		selection.add(this.selectedTrajRelinkingRun);
		
		final OmegaPluginEvent event = new OmegaPluginEventResultsTrajectoriesSegmentation(
				this.getPlugin(), selection, this.selectedTrajRelinkingRun,
				this.getSegmentsMap(), this.currentSegmentationTypes);
		// TODO BUG HERE : mark actions should be on starting point of this
		// segmentation
		// this.markActionsApplied(this.selectedTrajRelinkingRun);
		// FIXME changed to avoid messing up with subsequent segmentation
		final List<OmegaTrajectory> selectedTracks = new ArrayList<OmegaTrajectory>(
				this.tbPanel.getSelectedTrajectories());
		OmegaTrajectoriesSegmentationRun currentSegmentation = this.selectedTrajSegmentationRun;
		if (currentSegmentation == null) {
			currentSegmentation = this.startingPointTrajSegmentationRun;
		}
		this.markActionsApplied(currentSegmentation);
		// end changed
		this.getPlugin().fireEvent(event);

		//
		final OmegaTrajectoriesSegmentationRun newSegmentation = this.trajSegmentationRuns
				.get(OmegaTrajectorySegmentationPluginPanel.this.trajSegmentationRuns.size() - 1);
		// TODO ACTIONS SHOULD BE MOVED TO THE NEWLY CREATED
		// SEGMENTATION
		// HERE!?? TO BE VERIFIED
		this.moveActionsToNewSegmentation(currentSegmentation, newSegmentation);
		// this.trajectoriesSegmentation_cmb.setSelectedItem(newSegmentation
		// .getName());
		this.updateTrajectories(selectedTracks, true);
		this.fireEventTrajectories(selectedTracks, true);
	}
	
	private void undoLastAction() {
		final Map<OmegaAnalysisRun, List<OmegaTrajectorySegmentationAction>> actions = this.actions;
		final Map<OmegaAnalysisRun, List<OmegaTrajectorySegmentationAction>> cancelledActions = this.cancelledActions;
		OmegaTrajectoriesSegmentationRun currentModification = this.selectedTrajSegmentationRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajSegmentationRun;
		}
		final List<OmegaTrajectorySegmentationAction> actionList = actions
				.get(currentModification);
		final OmegaTrajectorySegmentationAction lastAction = actionList
				.get(actionList.size() - 1);
		actionList.remove(lastAction);
		if (actionList.isEmpty()) {
			actions.remove(currentModification);
		} else {
			actions.put(currentModification, actionList);
		}
		
		List<OmegaTrajectorySegmentationAction> cancelledActionList = null;
		if (cancelledActions.containsKey(currentModification)) {
			cancelledActionList = cancelledActions.get(currentModification);
		} else {
			cancelledActionList = new ArrayList<OmegaTrajectorySegmentationAction>();
		}
		cancelledActionList.add(lastAction);
		cancelledActions.put(currentModification, cancelledActionList);
		
		if (actionList.isEmpty()) {
			this.setEnableUndo(false);
			this.setEnableCancel(false);
			this.resetStartingPointAndCurrentModification();
		} else {
			this.fireEventSelectionCurrentTrajectoriesSegmentationRun();
		}
		this.setEnableRedo(true);
		this.updateCurrentSegmentedTrajectories();
	}
	
	private void redoLastAction() {
		final Map<OmegaAnalysisRun, List<OmegaTrajectorySegmentationAction>> actions = this.actions;
		final Map<OmegaAnalysisRun, List<OmegaTrajectorySegmentationAction>> cancelledActions = this.cancelledActions;
		OmegaTrajectoriesSegmentationRun currentModification = this.selectedTrajSegmentationRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajSegmentationRun;
		}
		final List<OmegaTrajectorySegmentationAction> cancelledActionList = cancelledActions
				.get(currentModification);
		final OmegaTrajectorySegmentationAction lastAction = cancelledActionList
				.get(cancelledActionList.size() - 1);
		cancelledActionList.remove(lastAction);
		if (cancelledActionList.isEmpty()) {
			cancelledActions.remove(currentModification);
		} else {
			cancelledActions.put(currentModification, cancelledActionList);
		}
		List<OmegaTrajectorySegmentationAction> actionList = null;
		if (actions.containsKey(currentModification)) {
			actionList = actions.get(currentModification);
		} else {
			actionList = new ArrayList<OmegaTrajectorySegmentationAction>();
		}
		if (actionList.isEmpty()) {
			this.setStartingPointAndCurrentModification(this.selectedTrajSegmentationRun);
		}
		actionList.add(lastAction);
		actions.put(currentModification, actionList);
		if (cancelledActionList.isEmpty()) {
			this.setEnableRedo(false);
		}
		this.setEnableUndo(true);
		this.setEnableCancel(true);
		this.updateCurrentSegmentedTrajectories();
	}
	
	private void cancelAllActions() {
		final StringBuffer buf = new StringBuffer();
		buf.append(OmegaTrajectorySegmentationPluginConstants.CANCEL_CONFIRM_MSG);
		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
				this.getParentContainer(), OmegaTrajectorySegmentationPluginConstants.CANCEL_CONFIRM,
				buf.toString(), true);
		dialog.setVisible(true);
		if (!dialog.getConfirmation())
			return;
		OmegaTrajectoriesSegmentationRun currentModification = this.selectedTrajSegmentationRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajSegmentationRun;
		}
		final Map<OmegaAnalysisRun, List<OmegaTrajectorySegmentationAction>> actions = this.actions;
		final Map<OmegaAnalysisRun, List<OmegaTrajectorySegmentationAction>> cancelledActions = this.cancelledActions;
		cancelledActions.put(currentModification,
				actions.get(currentModification));
		actions.remove(currentModification);
		this.resetStartingPointAndCurrentModification();
		this.setEnableUndo(false);
		this.setEnableCancel(false);
		this.setEnableRedo(true);
		this.updateCurrentSegmentedTrajectories();
	}
	
	// FIXME changed to avoid messing up with subsequent segmentation
	// private void markActionsApplied(final OmegaParticleLinkingRun
	// analysisRun) {
	private void markActionsApplied(
			final OmegaTrajectoriesSegmentationRun analysisRun) {
		if (this.actions.containsKey(analysisRun)) {
			final List<OmegaTrajectorySegmentationAction> actionList = this.actions
					.get(analysisRun);
			for (final OmegaTrajectorySegmentationAction action : actionList) {
				action.setHasBeenApplied(true);
			}
		}
	}
	
	private void moveActionsToNewSegmentation(
			final OmegaTrajectoriesSegmentationRun currentSegmentation,
			final OmegaTrajectoriesSegmentationRun newSegmentation) {
		if (this.actions.containsKey(currentSegmentation)) {
			final List<OmegaTrajectorySegmentationAction> actionList = this.actions
					.get(currentSegmentation);
			this.actions.remove(currentSegmentation);
			this.actions.put(newSegmentation, actionList);
		}
	}
	
	private void setEnableUndo(final boolean enabled) {
		this.undo_btt.setEnabled(enabled);
		this.undo_itm.setEnabled(enabled);
	}
	
	private void setEnableRedo(final boolean enabled) {
		this.redo_btt.setEnabled(enabled);
		this.redo_itm.setEnabled(enabled);
	}
	
	private void setEnableCancel(final boolean enabled) {
		this.cancel_btt.setEnabled(enabled);
		this.cancel_itm.setEnabled(enabled);
	}
	
	protected void segmentTrajectory(final OmegaTrajectory trajectory,
			final List<OmegaSegment> segmentationResults,
			final OmegaROI startingROI, final OmegaROI endingROI,
			final int segmValue) {
		final String trackName = trajectory.getName();
		this.tsPanel.setSegmentationEnded();
		final List<OmegaSegment> oldSegmentationResults = new ArrayList<OmegaSegment>(
				segmentationResults);
		final List<OmegaSegment> edgesToRemove = new ArrayList<OmegaSegment>();
		final List<OmegaSegment> edgesToAdd = new ArrayList<OmegaSegment>();
		final int startingIndex = startingROI.getFrameIndex();
		final int endingIndex = endingROI.getFrameIndex();
		boolean needCheckIteration = false;
		for (final OmegaSegment edge : segmentationResults) {
			final int edgeStartingIndex = edge.getStartingROI().getFrameIndex();
			final int edgeEndingIndex = edge.getEndingROI().getFrameIndex();
			needCheckIteration = true;
			if ((edgeStartingIndex == startingIndex)
					&& (edgeEndingIndex == endingIndex)) {
				// Case same extremes
				final String name = trackName + "_"
						+ OmegaSegment.DEFAULT_SEGM_NAME + "_"
						+ edgeStartingIndex + "-" + edgeEndingIndex;
				edgesToRemove.add(edge);
				final OmegaSegment newEdge = new OmegaSegment(
						edge.getStartingROI(), edge.getEndingROI(), name);
				newEdge.setSegmentationType(segmValue);
				edgesToAdd.add(newEdge);
				needCheckIteration = false;
			} else if ((edgeStartingIndex > startingIndex)
					&& (edgeEndingIndex < endingIndex)) {
				// Case new edge include old edge
				final String name = trackName + "_"
						+ OmegaSegment.DEFAULT_SEGM_NAME + "_" + startingIndex
						+ "-" + endingIndex;
				final OmegaSegment newEdge = new OmegaSegment(startingROI,
						endingROI, name);
				newEdge.setSegmentationType(segmValue);
				edgesToAdd.add(newEdge);
				edgesToRemove.add(edge);
			} else if ((edgeEndingIndex > startingIndex)
					&& (edgeEndingIndex <= endingIndex)) {
				// Case new edge is at the end
				this.createEdgeAtTheEnd(edge, startingROI, endingROI,
						segmValue, edgesToAdd, trackName);
				edgesToRemove.add(edge);
			} else if ((edgeStartingIndex >= startingIndex)
					&& (edgeStartingIndex < endingIndex)) {
				// Case new edge is at the beginning
				this.createEdgeAtTheBeginning(edge, startingROI, endingROI,
						segmValue, edgesToAdd, trackName);
				edgesToRemove.add(edge);
			} else if ((edgeStartingIndex <= startingIndex)
					&& (edgeEndingIndex >= endingIndex)) {
				// Case new edge is in between another edge
				this.createEdgeInBetween(edge, startingROI, endingROI,
						segmValue, edgesToAdd, trackName);
				edgesToRemove.add(edge);
			} else {
				needCheckIteration = false;
			}
			if (needCheckIteration) {
				break;
			}
		}
		segmentationResults.removeAll(edgesToRemove);
		segmentationResults.addAll(edgesToAdd);
		if (needCheckIteration) {
			for (final OmegaSegment edge : edgesToAdd) {
				this.segmentTrajectory(segmentationResults, edge, trackName);
			}
		}
		
		// TODO fix if same segTypeTraj merge / avoid split etc
		// System.out.println("##########################");
		// System.out.println("Traj: " + trajectory.getName());
		// for (final OmegaSegment edge : segmentationResults) {
		// System.out.println("From " + edge.getStartingROI().getFrameIndex()
		// + " To " + edge.getEndingROI().getFrameIndex()
		// + " segType " + edge.getSegmentationType());
		// }
		// System.out.println("##########################");
		
		edgesToRemove.clear();
		edgesToAdd.clear();
		for (final OmegaSegment edge : oldSegmentationResults) {
			if (!segmentationResults.contains(edge)) {
				edgesToRemove.add(edge);
			}
		}
		for (final OmegaSegment edge : segmentationResults) {
			if (!oldSegmentationResults.contains(edge)) {
				edgesToAdd.add(edge);
			}
		}
		
		OmegaTrajectoriesSegmentationRun currentModification = this.selectedTrajSegmentationRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajSegmentationRun;
		} else {
			this.setStartingPointAndCurrentModification(this.selectedTrajSegmentationRun);
		}
		
		final OmegaTrajectorySegmentationAction action = new OmegaTrajectorySegmentationAction(trajectory,
				edgesToRemove, edgesToAdd);
		this.addAction(currentModification, action);
		
		this.updateCurrentSegmentedTrajectories();
		this.fireEventSelectionCurrentTrajectoriesSegmentationRun();
	}
	
	private void segmentTrajectory(
			final List<OmegaSegment> segmentationResults,
			final OmegaSegment newEdge, final String trackName) {
		final List<OmegaSegment> edgesToAdd = new ArrayList<OmegaSegment>();
		final List<OmegaSegment> edgesToRemove = new ArrayList<OmegaSegment>();
		final int startingIndex = newEdge.getStartingROI().getFrameIndex();
		final int endingIndex = newEdge.getEndingROI().getFrameIndex();
		boolean needCheckIteration = false;
		for (final OmegaSegment edge : segmentationResults) {
			if (edge.equals(newEdge)) {
				continue;
			}
			needCheckIteration = true;
			final int edgeStartingIndex = edge.getStartingROI().getFrameIndex();
			final int edgeEndingIndex = edge.getEndingROI().getFrameIndex();
			if ((edgeStartingIndex > startingIndex)
					&& (edgeEndingIndex < endingIndex)) {
				// Case new edge contains
				edgesToRemove.add(edge);
				needCheckIteration = false;
			} else if ((edgeEndingIndex > startingIndex)
					&& (edgeEndingIndex <= endingIndex)) {
				edgesToRemove.add(edge);
				needCheckIteration = false;
				// Case new edge is at the end
				if (edgeStartingIndex != startingIndex) {
					final String name = trackName + "_"
							+ OmegaSegment.DEFAULT_SEGM_NAME + "_"
							+ edgeStartingIndex + "-"
							+ newEdge.getStartingROI().getFrameIndex();
					final OmegaSegment trunk = new OmegaSegment(
							edge.getStartingROI(), newEdge.getStartingROI(),
							name);
					trunk.setSegmentationType(edge.getSegmentationType());
					edgesToAdd.add(trunk);
					needCheckIteration = true;
				}
			} else if ((edgeStartingIndex >= startingIndex)
					&& (edgeStartingIndex < endingIndex)) {
				// Case new edge is at the beginning
				edgesToRemove.add(edge);
				needCheckIteration = false;
				if (edgeEndingIndex != endingIndex) {
					final String name = trackName + "_"
							+ OmegaSegment.DEFAULT_SEGM_NAME + "_"
							+ newEdge.getEndingROI().getFrameIndex() + "-"
							+ edgeEndingIndex;
					final OmegaSegment trunk = new OmegaSegment(
							newEdge.getEndingROI(), edge.getEndingROI(), name);
					trunk.setSegmentationType(edge.getSegmentationType());
					edgesToAdd.add(trunk);
					needCheckIteration = true;
				}
			}
		}
		segmentationResults.addAll(edgesToAdd);
		segmentationResults.removeAll(edgesToRemove);
		if (needCheckIteration) {
			for (final OmegaSegment edge : edgesToAdd) {
				this.segmentTrajectory(segmentationResults, edge, trackName);
			}
		}
	}
	
	private void createEdgeAtTheBeginning(final OmegaSegment edge,
			final OmegaROI startingROI, final OmegaROI endingROI,
			final int actualSegmentationType,
			final List<OmegaSegment> edgesToAdd, final String trackName) {
		final String name1 = trackName + "_" + OmegaSegment.DEFAULT_SEGM_NAME
				+ "_" + startingROI.getFrameIndex() + "-"
				+ endingROI.getFrameIndex();
		final OmegaSegment newEdge = new OmegaSegment(startingROI, endingROI,
				name1);
		newEdge.setSegmentationType(actualSegmentationType);
		edgesToAdd.add(newEdge);

		final String name2 = trackName + "_" + OmegaSegment.DEFAULT_SEGM_NAME
				+ "_" + endingROI.getFrameIndex() + "-"
				+ edge.getEndingROI().getFrameIndex();
		final OmegaSegment oldEdge = new OmegaSegment(endingROI,
				edge.getEndingROI(), name2);
		oldEdge.setSegmentationType(edge.getSegmentationType());
		edgesToAdd.add(oldEdge);
	}
	
	private void createEdgeAtTheEnd(final OmegaSegment edge,
			final OmegaROI startingROI, final OmegaROI endingROI,
			final int actualSegmentationType,
			final List<OmegaSegment> edgesToAdd, final String trackName) {
		final String name1 = trackName + "_" + OmegaSegment.DEFAULT_SEGM_NAME
				+ "_" + edge.getStartingROI().getFrameIndex() + "-"
				+ startingROI.getFrameIndex();
		final OmegaSegment oldEdge = new OmegaSegment(edge.getStartingROI(),
				startingROI, name1);
		oldEdge.setSegmentationType(edge.getSegmentationType());
		edgesToAdd.add(oldEdge);
		
		final String name2 = trackName + "_" + OmegaSegment.DEFAULT_SEGM_NAME
				+ "_" + startingROI.getFrameIndex() + "-"
				+ endingROI.getFrameIndex();
		final OmegaSegment newEdge = new OmegaSegment(startingROI, endingROI,
				name2);
		newEdge.setSegmentationType(actualSegmentationType);
		edgesToAdd.add(newEdge);
	}
	
	private void createEdgeInBetween(final OmegaSegment edge,
			final OmegaROI startingROI, final OmegaROI endingROI,
			final int actualSegmentationType,
			final List<OmegaSegment> edgesToAdd, final String trackName) {
		final String name1 = trackName + "_" + OmegaSegment.DEFAULT_SEGM_NAME
				+ "_" + edge.getStartingROI().getFrameIndex() + "-"
				+ startingROI.getFrameIndex();
		final OmegaSegment trunk1 = new OmegaSegment(edge.getStartingROI(),
				startingROI, name1);
		trunk1.setSegmentationType(edge.getSegmentationType());
		edgesToAdd.add(trunk1);

		final String name2 = trackName + "_" + OmegaSegment.DEFAULT_SEGM_NAME
				+ "_" + startingROI.getFrameIndex() + "-"
				+ endingROI.getFrameIndex();
		final OmegaSegment newEdge = new OmegaSegment(startingROI, endingROI,
				name2);
		newEdge.setSegmentationType(actualSegmentationType);
		edgesToAdd.add(newEdge);
		
		final String name3 = trackName + "_" + OmegaSegment.DEFAULT_SEGM_NAME
				+ "_" + endingROI.getFrameIndex() + "-"
				+ edge.getEndingROI().getFrameIndex();
		final OmegaSegment trunk2 = new OmegaSegment(endingROI,
				edge.getEndingROI(), name3);
		trunk2.setSegmentationType(edge.getSegmentationType());
		edgesToAdd.add(trunk2);
	}
	
	private void addAction(
			final OmegaTrajectoriesSegmentationRun currentModification,
			final OmegaTrajectorySegmentationAction action) {
		List<OmegaTrajectorySegmentationAction> actionList = null;
		
		final Map<OmegaAnalysisRun, List<OmegaTrajectorySegmentationAction>> actions = this.actions;
		if (actions.containsKey(currentModification)) {
			actionList = actions.get(currentModification);
		} else {
			actionList = new ArrayList<OmegaTrajectorySegmentationAction>();
		}
		actionList.add(action);
		actions.put(currentModification, actionList);
		this.setEnableUndo(true);
		this.setEnableCancel(true);
	}
	
	private void selectImage() {
		if (this.popImages)
			return;
		final int index = this.images_cmb.getSelectedIndex();
		this.selectedImage = null;
		if (index == -1) {
			this.populateParticlesCombo();
			this.resetTrajectories();
			return;
		}
		if ((this.images == null) || (index >= this.images.size())) {
			this.selectedImage = this.orphanedAnalysis;
			this.tbPanel.setImage(null, null, null);
			this.tsPanel.setImage(null);
		} else {
			this.selectedImage = this.images.get(index);
			final OmegaImage img = (OmegaImage) this.selectedImage;
			this.tbPanel.setImage(img, img.getDefaultPixels().getSelectedC(),
					img.getDefaultPixels().getSelectedZ());
			this.tsPanel.setImage(img);
		}
		if (!this.isHandlingEvent) {
			if (this.selectedImage != this.orphanedAnalysis) {
				this.fireEventSelectionImage();
			} else {
				this.fireEventSelectionOrphaned();
			}
		}
		this.populateParticlesCombo();
		// this.populateTrajectoriesCombo();
	}
	
	private void selectParticleDetectionRun() {
		if (this.popParticles)
			return;
		final int index = this.particles_cmb.getSelectedIndex();
		this.selectedParticleDetectionRun = null;
		if (index == -1) {
			this.populateTrajectoriesCombo();
			this.resetTrajectories();
			return;
		}
		this.selectedParticleDetectionRun = this.particleDetectionRuns
				.get(index);
		if (!this.isHandlingEvent) {
			this.fireEventSelectionParticleDetectionRun();
		}
		final OmegaParameter paramC = this.selectedParticleDetectionRun
				.getAlgorithmSpec().getParameter(
						OmegaAlgorithmParameterConstants.PARAM_CHANNEL);
		final OmegaParameter paramZ = this.selectedParticleDetectionRun
				.getAlgorithmSpec().getParameter(
						OmegaAlgorithmParameterConstants.PARAM_ZSECTION);
		OmegaImage img = null;
		Boolean[] channel;
		if (this.selectedImage instanceof OmegaImage) {
			img = (OmegaImage) this.selectedImage;
			channel = new Boolean[img.getDefaultPixels().getSizeC()];
			channel[(Integer) paramC.getValue()] = true;
		} else if (paramC != null) {
			channel = new Boolean[(Integer) paramC.getValue() + 1];
			channel[(Integer) paramC.getValue()] = true;
		} else {
			channel = new Boolean[1];
			channel[0] = true;
		}
		if (paramZ != null) {
			this.tbPanel.setImage(img, channel, (Integer) paramZ.getValue());
		} else {
			this.tbPanel.setImage(img, channel, 0);
		}
		this.populateTrajectoriesCombo();
	}
	
	private void selectParticleLinkingRun() {
		if (this.popTrajectories)
			return;
		final int index = this.trajectories_cmb.getSelectedIndex();
		this.selectedParticleLinkingRun = null;
		if (index == -1) {
			this.populateTrajectoriesRelinkingCombo();
			this.resetTrajectories();
			return;
		}
		this.selectedParticleLinkingRun = this.particleLinkingRuns.get(index);
		if (!this.isHandlingEvent) {
			this.fireEventSelectionParticleLinkingRun();
		}
		final OmegaParameter radius = this.selectedParticleLinkingRun
				.getAlgorithmSpec().getParameter(
						OmegaAlgorithmParameterConstants.PARAM_RADIUS);
		if ((radius != null)
				&& radius.getClazz().equals(Integer.class.getName())) {
			this.setRadius((int) radius.getValue());
		}
		this.populateTrajectoriesRelinkingCombo();
		// this.tbPanel.updateTrajectories(
		// this.selectedParticleLinkingRun.getResultingTrajectories(),
		// false);
	}
	
	private void selectTrajectoriesRelinkingRun() {
		if (this.popTrajRelinking)
			return;
		final int index = this.trajectoriesRelinking_cmb.getSelectedIndex();
		this.selectedTrajRelinkingRun = null;
		if (index == -1) {
			this.populateTrajectoriesSegmentationCombo();
			this.resetTrajectories();
			return;
		}
		if (index < this.trajRelinkingRuns.size()) {
			this.selectedTrajRelinkingRun = this.trajRelinkingRuns.get(index);
		}
		if (!this.isHandlingEvent) {
			this.fireEventSelectionTrajectoriesRelinkingRun();
		}
		this.populateTrajectoriesSegmentationCombo();
		// this.tbPanel
		// .updateTrajectories(this.selectedTrajRelinkingRun
		// .getResultingTrajectories(), false);
	}
	
	private void selectTrajectoriesSegmentationRun() {
		String c = null, z = null;
		if (this.selectedParticleDetectionRun != null) {
			for (final OmegaParameter param : this.selectedParticleDetectionRun
					.getAlgorithmSpec().getParameters()) {
				if (param.getName().equals(
						OmegaAlgorithmParameterConstants.PARAM_CHANNEL)) {
					c = param.getStringValue();
				} else if (param.getName().equals(
						OmegaAlgorithmParameterConstants.PARAM_ZSECTION)) {
					z = param.getStringValue();
				}
			}
		}
		this.resPanel.setAnalysisRun(null, c, z);
		if (this.popTrajSegmentation)
			return;
		final int index = this.trajectoriesSegmentation_cmb.getSelectedIndex();
		
		if (index == -1)
			return;
		
		if (index < this.trajSegmentationRuns.size()) {
			this.selectedTrajSegmentationRun = this.trajSegmentationRuns
					.get(index);
			// this.startingPointTrajSegmentationRun = null;
		} else {
			if ((this.selectedTrajSegmentationRun != null)
					&& (this.startingPointTrajSegmentationRun == null)) {
				this.startingPointTrajSegmentationRun = this.selectedTrajSegmentationRun;
			}
			// this.setStartingPointAndCurrentModification(this.selectedTrajRelinkingRun);
			this.selectedTrajSegmentationRun = null;
		}
		
		if (!this.isHandlingEvent) {
			if (this.selectedTrajSegmentationRun != null) {
				this.fireEventSelectionTrajectoriesSegmentationRun();
			} else {
				this.fireEventSelectionCurrentTrajectoriesSegmentationRun();
			}
		}
		
		this.tbPanel
				.updateTrajectories(this.selectedTrajRelinkingRun
						.getResultingTrajectories(), false);
		if (this.selectedTrajSegmentationRun != null) {
			this.updateSegmentTrajectories(this.tbPanel
					.getSelectedTrajectories());
		}

		// TODO think abt how to manage this point
		// OmegaTrajectoriesSegmentationRun actualModification =
		// this.selectedTrajSegmentationRun;
		// if (actualModification == null) {
		// actualModification = this.selectedParticleLinkingRun;
		// }
		
		this.activateNeededButton();
	}
	
	private void updateCurrentSegmentedTrajectories() {
		String c = null, z = null;
		if (this.selectedParticleDetectionRun != null) {
			for (final OmegaParameter param : this.selectedParticleDetectionRun
					.getAlgorithmSpec().getParameters()) {
				if (param.getName().equals(
						OmegaAlgorithmParameterConstants.PARAM_CHANNEL)) {
					c = param.getStringValue();
				} else if (param.getName().equals(
						OmegaAlgorithmParameterConstants.PARAM_ZSECTION)) {
					z = param.getStringValue();
				}
			}
		}
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = this
				.getSegmentsMap();
		this.tsPanel.updateCurrentSegmentTrajectories(segmentsMap);
		if (this.selectedTrajSegmentationRun == null) {
			this.resPanel.setAnalysisRun(this.startingPointTrajSegmentationRun,
					c, z);
		} else {
			this.resPanel
					.setAnalysisRun(this.selectedTrajSegmentationRun, c, z);
		}
		this.resPanel.populateSegmentsResults(segmentsMap);
	}
	
	private Map<OmegaTrajectory, List<OmegaSegment>> getSegmentsMap() {
		OmegaTrajectoriesSegmentationRun currentSegmentation = this.selectedTrajSegmentationRun;
		if (currentSegmentation == null) {
			currentSegmentation = this.startingPointTrajSegmentationRun;
		}
		final List<OmegaTrajectorySegmentationAction> actions = this.actions
				.get(currentSegmentation);
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = new LinkedHashMap<OmegaTrajectory, List<OmegaSegment>>(
				currentSegmentation.getResultingSegments());
		if (actions == null)
			return segmentsMap;
		for (final OmegaTrajectory traj : segmentsMap.keySet()) {
			// TODO to be revisited
			final List<OmegaTrajectorySegmentationAction> relatedActions = new ArrayList<OmegaTrajectorySegmentationAction>();
			// FIXME actions can be null here?!
			if (actions != null) {
				for (final OmegaTrajectorySegmentationAction action : actions) {
					if (!(action instanceof OmegaTrajectorySegmentationAction)) {
						continue;
					}
					final OmegaTrajectorySegmentationAction segAction = action;
					// FIXME action.hasBeenApplied added to avoid messing up of
					// subsequent segmentation TO BE VERIFIED!
					if (segAction.getTrajectory().equals(traj)
							&& !action.hasBeenApplied()) {
						relatedActions.add(segAction);
					}
				}
			}
			// FIXME commented and using the original segments from the current
			// segmentation avoiding to reusing those already applied TO BE
			// VERIFIED!
			// final List<OmegaSegment> originalSegments =
			// OmegaAlgorithmsUtilities
			// .createDefaultSegmentation(traj);
			final List<OmegaSegment> newSegments = this.applySegmentActions(
					segmentsMap.get(traj), relatedActions);
			segmentsMap.put(traj, newSegments);
		}
		return segmentsMap;
	}
	
	private List<OmegaSegment> applySegmentActions(
			final List<OmegaSegment> originalSegments,
			final List<OmegaTrajectorySegmentationAction> relatedActions) {
		final List<OmegaSegment> newSegments = new ArrayList<OmegaSegment>(
				originalSegments);
		if ((relatedActions != null) && !relatedActions.isEmpty()) {
			// System.out.println("**********************************");
			for (final OmegaTrajectorySegmentationAction action : relatedActions) {
				// System.out.println("TO REMOVE");
				for (final OmegaSegment segm : action.getOriginalEdges()) {
					// System.out.println("From "
					// + segm.getStartingROI().getFrameIndex() + " to "
					// + segm.getEndingROI().getFrameIndex() + " type "
					// + segm.getSegmentationType());
				}
				// System.out.println("TO ADD");
				for (final OmegaSegment segm : action.getModifiedEdges()) {
					// System.out.println("From "
					// + segm.getStartingROI().getFrameIndex() + " to "
					// + segm.getEndingROI().getFrameIndex() + " type "
					// + segm.getSegmentationType());
				}
			}
			// System.out.println("**********************************");
			final List<OmegaSegment> edgesToRemove = new ArrayList<OmegaSegment>();
			for (final OmegaTrajectorySegmentationAction action : relatedActions) {
				edgesToRemove.clear();
				for (final OmegaSegment edge : newSegments) {
					for (final OmegaSegment edgeToRemove : action
							.getOriginalEdges()) {
						final boolean edgeEqual = edge.isEqual(edgeToRemove);
						if (edgeEqual) {
							edgesToRemove.add(edge);
						}
					}
				}
				newSegments.removeAll(edgesToRemove);
				final List<OmegaSegment> edgesToAdd = action.getModifiedEdges();
				newSegments.addAll(edgesToAdd);
			}
		}
		return newSegments;
	}
	
	private void setRadius(final int radius) {
		this.tbPanel.setRadius(radius);
	}
	
	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.tbPanel.updateParentContainer(parent);
		this.tsPanel.updateParentContainer(parent);
		this.segmentPreferencesDialog.updateParentContainer(parent);
	}
	
	@Override
	public void onCloseOperation() {
		
	}
	
	private void updateSelectedInformation(
			final List<OmegaTrajectory> trajectories) {
		this.currentTrajInfoPanel.setSelectedTrajectories(trajectories);
	}
	
	private void resetTrajectories() {
		this.updateTrajectories(null, true);
		this.updateTrajectories(null, false);
		this.updateSelectedInformation(null);
	}
	
	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
			final boolean selection) {
		// TODO modify to keep changes if needed
		this.tbPanel.updateTrajectories(trajectories, selection);
		// TODO refactoring ?
		if (selection) {
			this.updateSegmentTrajectories(trajectories);
		}
	}
	
	private void updateSegmentTrajectories(
			final List<OmegaTrajectory> trajectories) {
		if (trajectories == null) {
			this.updateSelectedInformation(null);
			this.tsPanel.resetSegmentation();
			return;
		}
		this.updateSelectedInformation(trajectories);
		OmegaTrajectoriesSegmentationRun currentSegmentation = this.selectedTrajSegmentationRun;
		if (currentSegmentation == null) {
			currentSegmentation = this.startingPointTrajSegmentationRun;
		}
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = new LinkedHashMap<>();
		final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments = currentSegmentation
				.getResultingSegments();
		for (final OmegaTrajectory traj : trajectories) {
			segmentsMap.put(traj, resultingSegments.get(traj));
		}
		this.tsPanel.createSegmentSingleTrajectoryPanels(segmentsMap);
		// TODO Apply default segmentation here
		this.updateCurrentSegmentedTrajectories();
	}
	
	public void updateCombos(final List<OmegaImage> images,
			final OrphanedAnalysisContainer orphanedAnalysis,
			final List<OmegaAnalysisRun> analysisRuns) {
		this.isHandlingEvent = true;
		this.images = images;
		this.orphanedAnalysis = orphanedAnalysis;
		this.loadedAnalysisRuns = analysisRuns;
		
		this.populateImagesCombo();
		this.isHandlingEvent = false;
	}
	
	private void populateImagesCombo() {
		this.popImages = true;
		this.images_cmb.removeAllItems();
		this.selectedImage = null;
		this.images_cmb.setSelectedIndex(-1);
		if (((this.images == null) || this.images.isEmpty())
				&& this.orphanedAnalysis.isEmpty()) {
			this.images_cmb.setEnabled(false);
			this.populateParticlesCombo();
			this.resetTrajectories();
			this.popImages = false;
			return;
		}
		
		this.images_cmb.setEnabled(true);
		
		if (this.images != null) {
			for (final OmegaImage image : this.images) {
				this.images_cmb.addItem(image.getName());
			}
		}
		this.images_cmb.addItem(OmegaGUIConstants.PLUGIN_ORPHANED_ANALYSES);
		this.popImages = false;
		
		if (this.images_cmb.getItemCount() > 0) {
			this.images_cmb.setSelectedIndex(0);
		} else {
			this.images_cmb.setSelectedIndex(-1);
		}
	}
	
	private void populateParticlesCombo() {
		this.popParticles = true;
		this.particles_cmb.removeAllItems();
		this.particleDetectionRuns.clear();
		this.particles_cmb.setSelectedIndex(-1);
		this.selectedParticleDetectionRun = null;
		
		if ((this.selectedImage == null)) {
			this.particles_cmb.setEnabled(false);
			this.populateTrajectoriesCombo();
			this.resetTrajectories();
			this.popParticles = false;
			return;
		}
		
		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedImage.getAnalysisRuns().contains(analysisRun)
					&& (analysisRun instanceof OmegaParticleDetectionRun)) {
				this.particleDetectionRuns
						.add((OmegaParticleDetectionRun) analysisRun);
				this.particles_cmb.addItem(analysisRun.getName());
			}
		}
		
		if (this.particleDetectionRuns.isEmpty()) {
			this.particles_cmb.setEnabled(false);
			this.populateTrajectoriesCombo();
			this.resetTrajectories();
			this.popParticles = false;
			return;
		}
		
		this.popParticles = false;
		if (this.particles_cmb.getItemCount() > 0) {
			this.particles_cmb.setEnabled(true);
			this.particles_cmb.setSelectedIndex(0);
		} else {
			this.particles_cmb.setSelectedIndex(-1);
		}
	}
	
	private void populateTrajectoriesCombo() {
		this.popTrajectories = true;
		this.trajectories_cmb.removeAllItems();
		this.particleLinkingRuns.clear();
		this.trajectories_cmb.setSelectedIndex(-1);
		this.selectedParticleLinkingRun = null;
		
		if ((this.selectedParticleDetectionRun == null)) {
			this.trajectories_cmb.setEnabled(false);
			this.populateTrajectoriesRelinkingCombo();
			this.resetTrajectories();
			this.popTrajectories = false;
			return;
		}
		
		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedParticleDetectionRun.getAnalysisRuns().contains(
					analysisRun)
					&& (analysisRun instanceof OmegaParticleLinkingRun)) {
				this.particleLinkingRuns
						.add((OmegaParticleLinkingRun) analysisRun);
				this.trajectories_cmb.addItem(analysisRun.getName());
			}
		}
		
		if (this.particleLinkingRuns.isEmpty()) {
			this.trajectories_cmb.setEnabled(false);
			this.populateTrajectoriesRelinkingCombo();
			this.resetTrajectories();
			this.popTrajectories = false;
			return;
		}
		
		this.popTrajectories = false;
		if (this.trajectories_cmb.getItemCount() > 0) {
			this.trajectories_cmb.setEnabled(true);
			this.trajectories_cmb.setSelectedIndex(0);
		} else {
			this.trajectories_cmb.setSelectedIndex(-1);
		}
	}
	
	private void populateTrajectoriesRelinkingCombo() {
		this.popTrajRelinking = true;
		this.trajectoriesRelinking_cmb.removeAllItems();
		this.trajRelinkingRuns.clear();
		this.trajectoriesRelinking_cmb.setSelectedIndex(-1);
		this.selectedTrajRelinkingRun = null;
		
		if (this.selectedParticleLinkingRun == null) {
			this.trajectoriesRelinking_cmb.setEnabled(false);
			this.populateTrajectoriesSegmentationCombo();
			this.resetTrajectories();
			this.popTrajRelinking = false;
			return;
		}
		
		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedParticleLinkingRun.getAnalysisRuns().contains(
					analysisRun)
					&& (analysisRun instanceof OmegaTrajectoriesRelinkingRun)) {
				this.trajRelinkingRuns
						.add((OmegaTrajectoriesRelinkingRun) analysisRun);
				this.trajectoriesRelinking_cmb.addItem(analysisRun.getName());
			}
		}
		if (this.trajRelinkingRuns.isEmpty()) {
			this.trajectoriesRelinking_cmb.setEnabled(false);
			this.populateTrajectoriesSegmentationCombo();
			this.resetTrajectories();
			this.popTrajRelinking = false;
			return;
		}
		
		this.popTrajRelinking = false;
		if (this.trajectoriesRelinking_cmb.getItemCount() > 0) {
			this.trajectoriesRelinking_cmb.setEnabled(true);
			this.trajectoriesRelinking_cmb.setSelectedIndex(0);
		} else {
			this.trajectoriesRelinking_cmb.setSelectedIndex(-1);
		}
	}
	
	private void populateTrajectoriesSegmentationCombo() {
		this.popTrajSegmentation = true;
		this.trajectoriesSegmentation_cmb.removeAllItems();
		this.trajSegmentationRuns.clear();
		this.trajectoriesSegmentation_cmb.setSelectedIndex(-1);
		this.selectedTrajSegmentationRun = null;
		if (this.selectedTrajRelinkingRun == null) {
			this.trajectoriesSegmentation_cmb.setEnabled(false);
			this.resetTrajectories();
			this.popTrajSegmentation = false;
			return;
		}
		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedTrajRelinkingRun.getAnalysisRuns().contains(
					analysisRun)
					&& (analysisRun instanceof OmegaTrajectoriesSegmentationRun)) {
				this.trajSegmentationRuns
						.add((OmegaTrajectoriesSegmentationRun) analysisRun);
				this.trajectoriesSegmentation_cmb
						.addItem(analysisRun.getName());
			}
		}
		this.trajectoriesSegmentation_cmb
				.addItem(OmegaGUIConstants.SEGMENTATION_CURRENT);
		if (this.trajSegmentationRuns.isEmpty()) {
			this.trajectoriesSegmentation_cmb.setEnabled(false);
			this.resetTrajectories();
			this.popTrajSegmentation = false;
			return;
		}
		
		this.popTrajSegmentation = false;
		if (this.trajectoriesSegmentation_cmb.getItemCount() > 1) {
			this.trajectoriesSegmentation_cmb.setEnabled(true);
			this.trajectoriesSegmentation_cmb.setSelectedIndex(0);
		} else {
			this.trajectoriesSegmentation_cmb.setSelectedIndex(-1);
		}
	}

	private void fireEventSelectionOrphaned() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionOrphaned(
				this.getPlugin(), this.orphanedAnalysis);
		this.getPlugin().fireEvent(event);
	}
	
	private void fireEventSelectionImage() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionImage(
				this.getPlugin(), (OmegaImage) this.selectedImage);
		this.getPlugin().fireEvent(event);
	}
	
	private void fireEventSelectionParticleDetectionRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
				this.getPlugin(), this.selectedParticleDetectionRun);
		this.getPlugin().fireEvent(event);
	}
	
	private void fireEventSelectionParticleLinkingRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
				this.getPlugin(), this.selectedParticleLinkingRun);
		this.getPlugin().fireEvent(event);
	}
	
	private void fireEventSelectionTrajectoriesRelinkingRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
				this.getPlugin(), this.selectedTrajRelinkingRun);
		this.getPlugin().fireEvent(event);
	}
	
	private void fireEventSelectionTrajectoriesSegmentationRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
				this.getPlugin(), this.selectedTrajSegmentationRun);
		this.getPlugin().fireEvent(event);
	}
	
	private void fireEventSelectionCurrentTrajectoriesSegmentationRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionTrajectoriesSegmentationRun(
				this.getPlugin(), this.startingPointTrajSegmentationRun,
				this.getSegmentsMap(), true);
		this.getPlugin().fireEvent(event);
	}
	
	protected void fireEventTrajectories(
			final List<OmegaTrajectory> trajectories, final boolean selection) {
		// TODO modified as needed
		final OmegaPluginEvent event = new OmegaPluginEventTrajectories(
				this.getPlugin(), trajectories, selection);
		this.getPlugin().fireEvent(event);
	}
	
	protected void fireEventSegments(
			final Map<OmegaTrajectory, List<OmegaSegment>> segments,
			final boolean selection) {
		// TODO modified as needed
		final OmegaPluginEvent event = new OmegaPluginEventSegments(
				this.getPlugin(), segments, this.currentSegmentationTypes,
				selection);
		this.getPlugin().fireEvent(event);
	}
	
	public void selectImage(final OmegaAnalysisRunContainerInterface image) {
		this.isHandlingEvent = true;
		int index = -1;
		if (this.images != null) {
			index = this.images.indexOf(image);
		}
		if (index == -1) {
			final int count = this.images_cmb.getItemCount() - 1;
			this.images_cmb.setSelectedIndex(count);
		} else {
			this.images_cmb.setSelectedIndex(index);
		}
		this.isHandlingEvent = false;
	}
	
	public void selectParticleDetectionRun(
			final OmegaParticleDetectionRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.particleDetectionRuns.indexOf(analysisRun);
		this.particles_cmb.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}
	
	public void selectParticleLinkingRun(
			final OmegaParticleLinkingRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.particleLinkingRuns.indexOf(analysisRun);
		this.trajectories_cmb.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}
	
	public void selectTrajectoriesRelinkingRun(
			final OmegaTrajectoriesRelinkingRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.trajRelinkingRuns.indexOf(analysisRun);
		this.trajectoriesRelinking_cmb.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}
	
	public void selectTrajectoriesSegmentationRun(
			final OmegaTrajectoriesSegmentationRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.trajSegmentationRuns.indexOf(analysisRun);
		this.trajectoriesSegmentation_cmb.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}
	
	public void setGateway(final OmegaGateway gateway) {
		this.tbPanel.setGateway(gateway);
	}
	
	@Override
	public void updateStatus(final String s) {
		try {
			this.statusPanel.updateStatus(0, s);
		} catch (final OmegaPluginExceptionStatusPanel ex) {
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex,
					true);
		}
	}
	
	public void handleSegmTypesChanged() {
		this.segmentPreferencesDialog.setVisible(false);
		this.segmTypesList.addAll(this.segmentPreferencesDialog
				.getSegmentationTypesList());
		this.currentSegmentationTypes = this.segmentPreferencesDialog
				.getCurrentSegmentationTypes();
		this.tsPanel.setSegmentationTypes(this.currentSegmentationTypes);
	}
	
	public void setSegmentationTypesList(
			final List<OmegaSegmentationTypes> segmTypesList) {
		this.segmTypesList = segmTypesList;
		if (segmTypesList.contains(this.currentSegmentationTypes)) {
			this.segmentPreferencesDialog.setSegmentationTypesList(
					segmTypesList, this.currentSegmentationTypes);
		} else {
			this.segmentPreferencesDialog.setSegmentationTypesList(
					segmTypesList, null);
		}
	}
	
	public Color getSegmentationColor(final int value) {
		Color c = this.currentSegmentationTypes.getSegmentationColor(value);
		if (c == null) {
			c = OmegaSegmentationTypes.NOT_ASSIGNED_COL;
		}
		return c;
	}
	
	public Color getCurrentSegmentationColor(final int value) {
		if (!this.tsPanel.isSegmentOnSpotsSelection())
			return Color.red;
		return this.getSegmentationColor(value);
	}
	
	public void selectSegmentStartingPoint(final OmegaROI startingROI) {
		this.tsPanel.selectStartingROI(startingROI);
	}
	
	public void selectSegmentEndingPoint(final OmegaROI endingROI) {
		this.tsPanel.selectEndingROI(endingROI);
	}
	
	@Override
	public void sendEventTrajectories(
			final List<OmegaTrajectory> selectedTrajectories,
			final boolean selected) {
		this.updateSegmentTrajectories(selectedTrajectories);
		this.fireEventTrajectories(selectedTrajectories, selected);
	}
	
	@Override
	public void handleTrajectoryNameChanged() {
		this.repaint();
	}
	
	private void setStartingPointAndCurrentModification(
			final OmegaTrajectoriesSegmentationRun segmentationRun) {
		if (this.startingPointTrajSegmentationRun != null) {
			this.actions.remove(this.startingPointTrajSegmentationRun);
		}
		this.startingPointTrajSegmentationRun = segmentationRun;
		this.trajectoriesSegmentation_cmb
				.setSelectedItem(OmegaGUIConstants.SEGMENTATION_CURRENT);
	}
	
	private void resetStartingPointAndCurrentModification() {
		final int index = this.trajSegmentationRuns
				.indexOf(this.startingPointTrajSegmentationRun);
		this.trajectoriesSegmentation_cmb.setSelectedIndex(index);
	}

	public void clearTrajectoriesSelection() {
		this.tbPanel.clearTrajectoriesSelection();
		this.updateSegmentTrajectories(null);
	}

	public void setLineWidth(final int lineWidth) {
		this.tsPanel.setLineWidth(lineWidth);
	}
}
