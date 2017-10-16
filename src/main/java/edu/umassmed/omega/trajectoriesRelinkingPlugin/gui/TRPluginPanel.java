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
package edu.umassmed.omega.trajectoriesRelinkingPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
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
import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaConstantsAlgorithmParameters;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainerInterface;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OrphanedAnalysisContainer;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrajectoriesRelinking;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionImage;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventTrajectories;
import edu.umassmed.omega.commons.exceptions.OmegaPluginExceptionStatusPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.gui.GenericStatusPanel;
import edu.umassmed.omega.commons.gui.GenericTrackingResultsPanel;
import edu.umassmed.omega.commons.gui.GenericTrajectoriesBrowserPanel;
import edu.umassmed.omega.commons.gui.GenericTrajectoryInformationPanel;
import edu.umassmed.omega.commons.gui.dialogs.GenericConfirmationDialog;
import edu.umassmed.omega.commons.gui.interfaces.GenericTrajectoriesBrowserContainerInterface;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.trajectoriesRelinkingPlugin.TRConstants;
import edu.umassmed.omega.trajectoriesRelinkingPlugin.actions.RelinkingAction;

public class TRPluginPanel extends GenericPluginPanel implements
		GenericTrajectoriesBrowserContainerInterface {
	
	private static final long serialVersionUID = -5740459087763362607L;
	
	private final OmegaGateway gateway;
	
	private JComboBox<String> images_cmb, particles_cmb, trajectories_cmb,
			trajectoriesRelinking_cmb;
	private JButton save_btt, undo_btt, redo_btt, cancel_btt;
	private boolean popImages, popParticles, popTrajectories, popTrajRelinking,
			isHandlingEvent;
	
	private JMenu tr_mn;
	private JMenuItem save_itm, undo_itm, redo_itm, cancel_itm,
			preferences_itm;
	
	private ActionListener save_al, undo_al, redo_al, cancel_al;
	
	private JTabbedPane tabbedPane;
	
	private GenericTrackingResultsPanel resPanel;
	
	private GenericTrajectoriesBrowserPanel tbPanel;
	private TRPanel trPanel;
	private GenericStatusPanel statusPanel;
	private GenericTrajectoryInformationPanel currentTrajInfoPanel;
	
	private List<OmegaImage> images;
	private OrphanedAnalysisContainer orphanedAnalysis;
	private OmegaAnalysisRunContainerInterface selectedImage;
	private List<OmegaAnalysisRun> loadedAnalysisRuns;
	private final Map<OmegaAnalysisRun, List<RelinkingAction>> actions,
			cancelledActions;
	private final List<OmegaTrajectory> currentlyModifiedTrajectories;
	
	final List<OmegaParticleDetectionRun> particleDetectionRuns;
	private OmegaParticleDetectionRun selectedParticleDetectionRun;
	final List<OmegaParticleLinkingRun> particleLinkingRuns;
	private OmegaParticleLinkingRun selectedParticleLinkingRun;
	final List<OmegaTrajectoriesRelinkingRun> trajRelinkingRuns;
	private OmegaTrajectoriesRelinkingRun selectedTrajRelinkingRun,
			startingPointTrajRelinkingRun;
	
	private JPanel topPanel;
	private JMenuItem hideDataSelection_mItm;
	
	public TRPluginPanel(final RootPaneContainer parent,
			final OmegaPlugin plugin, final OmegaGateway gateway,
			final List<OmegaImage> images,
			final OrphanedAnalysisContainer orphanedAnalysis,
			final List<OmegaAnalysisRun> analysisRuns, final int index) {
		super(parent, plugin, index);
		
		this.gateway = gateway;
		
		this.actions = new LinkedHashMap<>();
		this.cancelledActions = new LinkedHashMap<>();
		
		this.selectedImage = null;
		this.particleDetectionRuns = new ArrayList<>();
		this.selectedParticleDetectionRun = null;
		this.particleLinkingRuns = new ArrayList<>();
		this.selectedParticleLinkingRun = null;
		this.trajRelinkingRuns = new ArrayList<>();
		this.selectedTrajRelinkingRun = null;
		
		this.currentlyModifiedTrajectories = new ArrayList<>();
		
		this.images = images;
		this.orphanedAnalysis = orphanedAnalysis;
		this.loadedAnalysisRuns = analysisRuns;
		
		this.popImages = false;
		this.popParticles = false;
		this.popTrajectories = false;
		this.popTrajRelinking = false;
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
		
		this.tr_mn = new JMenu(OmegaGUIConstants.MENU_EDIT);
		
		this.save_itm = new JMenuItem(OmegaGUIConstants.SAVE);
		this.tr_mn.add(this.save_itm);
		
		this.undo_itm = new JMenuItem(OmegaGUIConstants.UNDO);
		this.tr_mn.add(this.undo_itm);
		this.undo_itm.setEnabled(false);
		
		this.redo_itm = new JMenuItem(OmegaGUIConstants.REDO);
		this.tr_mn.add(this.redo_itm);
		this.redo_itm.setEnabled(false);
		
		this.cancel_itm = new JMenuItem(OmegaGUIConstants.UNDO_ALL);
		this.tr_mn.add(this.cancel_itm);
		this.cancel_itm.setEnabled(false);
		
		// this.preferences_itm = new JMenuItem("Preferences");
		// this.tr_mn.add(this.preferences_itm);
		
		menuBar.add(this.tr_mn);
	}
	
	private void createAndAddWidgets() {
		this.topPanel = new JPanel();
		this.topPanel.setLayout(new GridLayout(4, 1));
		
		final JPanel p1 = new JPanel();
		p1.setLayout(new BorderLayout());
		final JLabel lbl1 = new JLabel(TRConstants.SELECT_IMAGE);
		lbl1.setPreferredSize(OmegaConstants.TEXT_SIZE);
		p1.add(lbl1, BorderLayout.WEST);
		this.images_cmb = new JComboBox<String>();
		this.images_cmb.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.images_cmb.setEnabled(false);
		p1.add(this.images_cmb, BorderLayout.CENTER);
		this.topPanel.add(p1);
		
		final JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		final JLabel lbl2 = new JLabel(TRConstants.SELECT_TRACKS_SPOT);
		lbl2.setPreferredSize(OmegaConstants.TEXT_SIZE);
		p2.add(lbl2, BorderLayout.WEST);
		this.particles_cmb = new JComboBox<String>();
		this.particles_cmb
				.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.particles_cmb.setEnabled(false);
		p2.add(this.particles_cmb, BorderLayout.CENTER);
		this.topPanel.add(p2);
		
		final JPanel p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		final JLabel lbl3 = new JLabel(TRConstants.SELECT_TRACKS_LINKING);
		lbl3.setPreferredSize(OmegaConstants.TEXT_SIZE);
		p3.add(lbl3, BorderLayout.WEST);
		this.trajectories_cmb = new JComboBox<String>();
		this.trajectories_cmb
				.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.trajectories_cmb.setEnabled(false);
		p3.add(this.trajectories_cmb, BorderLayout.CENTER);
		this.topPanel.add(p3);
		
		final JPanel p4 = new JPanel();
		p4.setLayout(new BorderLayout());
		final JLabel lbl4 = new JLabel(TRConstants.SELECT_TRACKS_ADJ);
		lbl4.setPreferredSize(OmegaConstants.TEXT_SIZE);
		p4.add(lbl4, BorderLayout.WEST);
		this.trajectoriesRelinking_cmb = new JComboBox<String>();
		this.trajectoriesRelinking_cmb
				.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.trajectoriesRelinking_cmb.setEnabled(false);
		p4.add(this.trajectoriesRelinking_cmb, BorderLayout.CENTER);
		this.topPanel.add(p4);
		
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
		
		this.tabbedPane.add(TRConstants.BROWSER_TABNAME, browser);
		
		this.trPanel = new TRPanel(this.getParentContainer(), this,
				this.gateway);
		this.tabbedPane.add(TRConstants.EDITOR_TABNAME, this.trPanel);
		
		this.resPanel = new GenericTrackingResultsPanel(
				this.getParentContainer());
		this.tabbedPane.add("Editor results", this.resPanel);
		
		this.add(this.tabbedPane, BorderLayout.CENTER);
		
		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		this.save_btt = new JButton(OmegaGUIConstants.SAVE);
		this.save_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.save_btt.setSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.save_btt);
		
		this.undo_btt = new JButton(OmegaGUIConstants.UNDO);
		this.undo_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.undo_btt.setSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.undo_btt);
		this.undo_btt.setEnabled(false);
		
		this.redo_btt = new JButton(OmegaGUIConstants.REDO);
		this.redo_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.redo_btt.setSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.redo_btt);
		this.redo_btt.setEnabled(false);
		
		this.cancel_btt = new JButton(OmegaGUIConstants.UNDO_ALL);
		this.cancel_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.cancel_btt.setSize(OmegaConstants.BUTTON_SIZE);
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
				
			}
		});
		this.images_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TRPluginPanel.this.selectImage();
			}
		});
		this.particles_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TRPluginPanel.this.selectParticleDetectionRun();
			}
		});
		this.trajectories_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TRPluginPanel.this.selectParticleLinkingRun();
			}
		});
		this.trajectoriesRelinking_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TRPluginPanel.this.selectTrajectoriesRelinkingRun();
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
		this.hideDataSelection_mItm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TRPluginPanel.this.handleHideDataSelection();
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
	
	private void activateNeededButton() {
		final Map<OmegaAnalysisRun, List<RelinkingAction>> actions = this.actions;
		final Map<OmegaAnalysisRun, List<RelinkingAction>> cancelledActions = this.cancelledActions;
		
		OmegaTrajectoriesRelinkingRun currentModification = this.selectedTrajRelinkingRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajRelinkingRun;
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
					TRPluginPanel.this.saveAllActions();
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
					TRPluginPanel.this.undoLastAction();
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
					TRPluginPanel.this.redoLastAction();
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
					TRPluginPanel.this.cancelAllActions();
				}
			};
		}
		return this.cancel_al;
	}
	
	private void saveAllActions() {
		final StringBuffer buf = new StringBuffer();
		buf.append(TRConstants.SAVE_CONFIRM_MSG);
		
		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
				this.getParentContainer(), TRConstants.SAVE_CONFIRM,
				buf.toString(), true);
		dialog.setVisible(true);
		if (!dialog.getConfirmation())
			return;
		
		final List<OmegaTrajectory> resultingTrajectories = new ArrayList<OmegaTrajectory>(
				this.currentlyModifiedTrajectories);
		
		OmegaTrajectoriesRelinkingRun currentModification = this.selectedTrajRelinkingRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajRelinkingRun;
		}
		
		final List<OmegaElement> selection = new ArrayList<OmegaElement>();
		selection.add((OmegaElement) this.selectedImage);
		selection.add(this.selectedParticleDetectionRun);
		selection.add(this.selectedParticleLinkingRun);
		
		final OmegaPluginEvent event = new OmegaPluginEventResultsTrajectoriesRelinking(
				this.getPlugin(), selection, this.selectedParticleLinkingRun,
				resultingTrajectories);
		this.markActionsApplied(currentModification);
		this.getPlugin().fireEvent(event);
		final OmegaTrajectoriesRelinkingRun newRelinking = this.trajRelinkingRuns
				.get(this.trajRelinkingRuns.size() - 1);
		this.trajectoriesRelinking_cmb.setSelectedItem(newRelinking.getName());
	}
	
	private void undoLastAction() {
		final Map<OmegaAnalysisRun, List<RelinkingAction>> actions = this.actions;
		final Map<OmegaAnalysisRun, List<RelinkingAction>> cancelledActions = this.cancelledActions;
		OmegaTrajectoriesRelinkingRun currentModification = this.selectedTrajRelinkingRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajRelinkingRun;
		}
		final List<RelinkingAction> actionList = actions
				.get(currentModification);
		final RelinkingAction lastAction = actionList
				.get(actionList.size() - 1);
		actionList.remove(lastAction);
		if (actionList.isEmpty()) {
			actions.remove(currentModification);
		} else {
			actions.put(currentModification, actionList);
		}
		List<RelinkingAction> cancelledActionList = null;
		if (cancelledActions.containsKey(currentModification)) {
			cancelledActionList = cancelledActions.get(currentModification);
		} else {
			cancelledActionList = new ArrayList<RelinkingAction>();
		}
		cancelledActionList.add(lastAction);
		cancelledActions.put(currentModification, cancelledActionList);
		if (actionList.isEmpty()) {
			this.setEnableUndo(false);
			this.setEnableCancel(false);
			this.resetStartingPointAndCurrentModification();
		} else {
			this.fireEventSelectionCurrentTrajectoriesRelinkingRun();
		}
		this.setEnableRedo(true);
		this.updateCurrentModifiedTrajectories(currentModification);
		this.updateSelectedTrajectories(this.tbPanel,
				lastAction.getModifiedTrajectories(),
				lastAction.getOriginalTrajectories());
		this.updateTrajectories(this.currentlyModifiedTrajectories, false);
		// this.sendEventTrajectories(this.currentlyModifiedTrajectories,
		// false);
	}
	
	private void redoLastAction() {
		final Map<OmegaAnalysisRun, List<RelinkingAction>> actions = this.actions;
		final Map<OmegaAnalysisRun, List<RelinkingAction>> cancelledActions = this.cancelledActions;
		OmegaTrajectoriesRelinkingRun currentModification = this.selectedTrajRelinkingRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajRelinkingRun;
		}
		final List<RelinkingAction> cancelledActionList = cancelledActions
				.get(currentModification);
		final RelinkingAction lastAction = cancelledActionList
				.get(cancelledActionList.size() - 1);
		cancelledActionList.remove(lastAction);
		if (cancelledActionList.isEmpty()) {
			cancelledActions.remove(currentModification);
		} else {
			cancelledActions.put(currentModification, cancelledActionList);
		}
		List<RelinkingAction> actionList = null;
		if (actions.containsKey(currentModification)) {
			actionList = actions.get(currentModification);
		} else {
			actionList = new ArrayList<RelinkingAction>();
		}
		if (actionList.isEmpty()) {
			this.setStartingPointAndCurrentModification(this.selectedTrajRelinkingRun);
		}
		actionList.add(lastAction);
		actions.put(currentModification, actionList);
		if (cancelledActionList.isEmpty()) {
			this.setEnableRedo(false);
		}
		this.setEnableUndo(true);
		this.setEnableCancel(true);
		this.updateCurrentModifiedTrajectories(currentModification);
		this.updateSelectedTrajectories(this.tbPanel,
				lastAction.getOriginalTrajectories(),
				lastAction.getModifiedTrajectories());
		this.updateTrajectories(this.currentlyModifiedTrajectories, false);
		this.fireEventSelectionCurrentTrajectoriesRelinkingRun();
	}
	
	private void cancelAllActions() {
		final StringBuffer buf = new StringBuffer();
		buf.append(TRConstants.CANCEL_CONFIRM_MSG);
		
		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
				this.getParentContainer(), TRConstants.CANCEL_CONFIRM,
				buf.toString(), true);
		dialog.setVisible(true);
		if (!dialog.getConfirmation())
			return;
		OmegaTrajectoriesRelinkingRun currentModification = this.selectedTrajRelinkingRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajRelinkingRun;
		}
		final Map<OmegaAnalysisRun, List<RelinkingAction>> actions = this.actions;
		final Map<OmegaAnalysisRun, List<RelinkingAction>> cancelledActions = this.cancelledActions;
		cancelledActions.put(currentModification,
				actions.get(currentModification));
		actions.remove(currentModification);
		this.resetStartingPointAndCurrentModification();
		this.setEnableUndo(false);
		this.setEnableCancel(false);
		this.setEnableRedo(true);
		this.updateCurrentModifiedTrajectories(currentModification);
		this.updateSelectedTrajectories(this.tbPanel,
				this.tbPanel.getSelectedTrajectories(),
				new ArrayList<OmegaTrajectory>());
		this.updateTrajectories(this.currentlyModifiedTrajectories, false);
		// this.sendEventTrajectories(this.currentlyModifiedTrajectories,
		// false);
	}
	
	private void markActionsApplied(final OmegaParticleLinkingRun analysisRun) {
		if (this.actions.containsKey(analysisRun)) {
			final List<RelinkingAction> actionList = this.actions
					.get(analysisRun);
			for (final RelinkingAction action : actionList) {
				action.setHasBeenApplied(true);
			}
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
	
	private void updateSelectedTrajectories(
			final GenericTrajectoriesBrowserPanel panel,
			final List<OmegaTrajectory> toRemove,
			final List<OmegaTrajectory> toAdd) {
		final int index = panel.removeTrajectoriesFromSelection(toRemove);
		panel.addTrajectoriesToSelection(toAdd, index);
	}
	
	protected void mergeTrajectories(final OmegaTrajectory from,
			final OmegaTrajectory to) {
		final List<OmegaTrajectory> tracks = this.currentlyModifiedTrajectories;
		final String trajName1 = from.getName() + TRConstants.MERGE_APPENDIX
				+ to.getName();
		final Double index = from.getIndex();
		int tmpIndex = tracks.indexOf(from) + 1;
		int increment = 0;
		if (tmpIndex >= tracks.size()) {
			tmpIndex = tracks.size() - 1;
			increment = 1;
		}
		final Double nextIndex = tracks.get(tmpIndex).getIndex() + increment;
		final Double fPart = nextIndex - index;
		final Double addition = fPart / 10;
		final Double newIndex = index + addition;
		final OmegaTrajectory newTraj = new OmegaTrajectory(from.getLength()
				+ to.getLength(), trajName1, newIndex);
		// newTraj.setName(from.getName() + TRConstants.MERGE_APPENDIX
		// + to.getName());
		newTraj.addROIs(from.getROIs());
		newTraj.addROIs(to.getROIs());

		OmegaTrajectoriesRelinkingRun currentModification = this.selectedTrajRelinkingRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajRelinkingRun;
		} else {
			this.setStartingPointAndCurrentModification(this.selectedTrajRelinkingRun);
		}
		
		final List<OmegaTrajectory> original = new ArrayList<OmegaTrajectory>();
		original.add(from);
		original.add(to);
		final List<OmegaTrajectory> modified = new ArrayList<OmegaTrajectory>();
		modified.add(newTraj);
		final RelinkingAction action = new RelinkingAction(original, modified);
		this.addAction(currentModification, action);
		
		this.updateCurrentModifiedTrajectories(currentModification);
		this.updateSelectedTrajectories(this.tbPanel, original, modified);
		this.updateTrajectories(this.currentlyModifiedTrajectories, false);
		// this.sendEventTrajectories(this.currentlyModifiedTrajectories,
		// false);
		this.fireEventSelectionCurrentTrajectoriesRelinkingRun();
		
		// this.updateSelectedTrajectories(this.tbPanel, original, modified);
		// this.updateSelectedTrajectories(this.trPanel, original, modified);
		this.resPanel
				.populateTrajectoriesResults(this.currentlyModifiedTrajectories);
	}
	
	protected void splitTrajectory(final OmegaTrajectory from,
			final int particleIndex) {
		// OmegaTrajectoriesRelinkingRun currentModification =
		// this.selectedTrajRelinkingRun;
		// if (currentModification == null) {
		// currentModification = this.startingPointTrajRelinkingRun;
		// }
		
		final List<OmegaTrajectory> tracks = this.currentlyModifiedTrajectories;
		final int newTrajLength = from.getLength() - particleIndex;
		final String trajName1 = from.getName() + TRConstants.SPLIT_APPENDIX_1;
		final Double index = from.getIndex();
		int tmpIndex = tracks.indexOf(from) + 1;
		int increment = 0;
		if (tmpIndex >= tracks.size()) {
			tmpIndex = tracks.size() - 1;
			increment = 1;
		}
		final Double nextIndex = tracks.get(tmpIndex).getIndex() + increment;
		final Double fPart = nextIndex - index;
		final Double addition = fPart / 10;
		Double newIndex = index + addition;
		final OmegaTrajectory newTraj1 = new OmegaTrajectory(particleIndex,
				trajName1, newIndex);
		// newTraj1.setName(from.getName() + TRConstants.SPLIT_APPENDIX_1);
		newTraj1.setColor(from.getColor());
		for (int i = 0; i < particleIndex; i++) {
			final OmegaROI roi = from.getROIs().get(i);
			newTraj1.addROI(roi);
		}
		
		final String trajName2 = from.getName() + TRConstants.SPLIT_APPENDIX_2;
		newIndex += addition;
		final OmegaTrajectory newTraj2 = new OmegaTrajectory(newTrajLength,
				trajName2, newIndex);
		// newTraj2.setName(from.getName() + TRConstants.SPLIT_APPENDIX_2);
		// newTraj2.setColor(from.getColor());
		for (int i = particleIndex; i < from.getLength(); i++) {
			final OmegaROI roi = from.getROIs().get(i);
			newTraj2.addROI(roi);
		}
		
		OmegaTrajectoriesRelinkingRun currentModification = this.selectedTrajRelinkingRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajRelinkingRun;
		} else {
			this.setStartingPointAndCurrentModification(this.selectedTrajRelinkingRun);
		}
		
		final List<OmegaTrajectory> original = new ArrayList<OmegaTrajectory>();
		original.add(from);
		final List<OmegaTrajectory> modified = new ArrayList<OmegaTrajectory>();
		modified.add(newTraj1);
		modified.add(newTraj2);
		final RelinkingAction action = new RelinkingAction(original, modified);
		this.addAction(currentModification, action);
		
		this.updateCurrentModifiedTrajectories(currentModification);
		this.updateSelectedTrajectories(this.tbPanel, original, modified);
		this.updateTrajectories(this.currentlyModifiedTrajectories, false);
		// this.sendEventTrajectories(this.currentlyModifiedTrajectories,
		// false);
		this.fireEventSelectionCurrentTrajectoriesRelinkingRun();
		
		// this.updateSelectedTrajectories(this.tbPanel, original, modified);
		// this.updateSelectedTrajectories(this.trPanel, original, modified);
		this.resPanel
				.populateTrajectoriesResults(this.currentlyModifiedTrajectories);
	}
	
	private void addAction(
			final OmegaTrajectoriesRelinkingRun currentModification,
			final RelinkingAction action) {
		List<RelinkingAction> actionList = null;
		final Map<OmegaAnalysisRun, List<RelinkingAction>> actions = this.actions;
		
		if (actions.containsKey(currentModification)) {
			actionList = actions.get(currentModification);
		} else {
			actionList = new ArrayList<RelinkingAction>();
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
			this.tbPanel.setImage(null);
			this.trPanel.setImage(null);
		} else {
			this.selectedImage = this.images.get(index);
			this.tbPanel.setImage((OmegaImage) this.selectedImage);
			this.trPanel.setImage((OmegaImage) this.selectedImage);
		}
		if (!this.isHandlingEvent) {
			this.fireEventSelectionImage();
		}
		this.populateParticlesCombo();
		// this.populateTrajectoriesCombo();
		// this.trPanel.setPixelSizes(this.selectedImage.getDefaultPixels()
		// .getPixelSizeX(), this.selectedImage.getDefaultPixels()
		// .getPixelSizeY());
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
						OmegaConstantsAlgorithmParameters.PARAM_RADIUS);
		if ((radius != null)
				&& radius.getClazz().equals(Integer.class.getName())) {
			this.setRadius((int) radius.getValue());
		}
		
		this.populateTrajectoriesRelinkingCombo();
		
		// this.updateActualModifiedTrajectories(this.selectedParticleLinkingRun);
		// this.drawTrajectoriesTable();
	}
	
	private void selectTrajectoriesRelinkingRun() {
		String c = null, z = null;
		if (this.selectedParticleDetectionRun != null) {
			for (final OmegaParameter param : this.selectedParticleDetectionRun
					.getAlgorithmSpec().getParameters()) {
				if (param.getName().equals(
						OmegaConstantsAlgorithmParameters.PARAM_CHANNEL)) {
					c = param.getStringValue();
				} else if (param.getName().equals(
						OmegaConstantsAlgorithmParameters.PARAM_ZSECTION)) {
					z = param.getStringValue();
				}
			}
		}
		this.resPanel.setAnalysisRun(null, c, z);
		if (this.popTrajRelinking)
			return;
		final int index = this.trajectoriesRelinking_cmb.getSelectedIndex();
		// this.selectedTrajRelinkingRun = null;
		if (index == -1) {
			this.resetTrajectories();
			return;
		}
		if (index < this.trajRelinkingRuns.size()) {
			this.selectedTrajRelinkingRun = this.trajRelinkingRuns.get(index);
			// this.startingPointTrajRelinkingRun = null;
		} else {
			if ((this.selectedTrajRelinkingRun != null)
					&& (this.startingPointTrajRelinkingRun == null)) {
				this.startingPointTrajRelinkingRun = this.selectedTrajRelinkingRun;
			}
			// this.setStartingPointAndCurrentModification(this.selectedTrajRelinkingRun);
			this.selectedTrajRelinkingRun = null;
		}
		
		OmegaTrajectoriesRelinkingRun currentModification = this.selectedTrajRelinkingRun;
		if (currentModification == null) {
			currentModification = this.startingPointTrajRelinkingRun;
		}
		
		this.updateCurrentModifiedTrajectories(currentModification);
		this.activateNeededButton();
		
		this.updateSelectedTrajectories(this.tbPanel,
				this.tbPanel.getSelectedTrajectories(),
				new ArrayList<OmegaTrajectory>());
		if (this.selectedTrajRelinkingRun == null) {
			this.updateTrajectories(this.currentlyModifiedTrajectories, false);
			if (!this.isHandlingEvent) {
				this.fireEventSelectionCurrentTrajectoriesRelinkingRun();
			}
			// this.sendEventTrajectories(this.currentlyModifiedTrajectories,
			// false);
			this.resPanel.setAnalysisRun(this.startingPointTrajRelinkingRun, c,
					z);
			this.resPanel
					.populateTrajectoriesResults(this.currentlyModifiedTrajectories);
		} else {
			this.updateTrajectories(
					this.selectedTrajRelinkingRun.getResultingTrajectories(),
					false);
			if (!this.isHandlingEvent) {
				this.fireEventSelectionTrajectoriesRelinkingRun();
			}
			// this.sendEventTrajectories(
			// this.selectedTrajRelinkingRun.getResultingTrajectories(),
			// false);
			this.resPanel.setAnalysisRun(this.selectedTrajRelinkingRun, c, z);
		}
	}
	
	private void updateCurrentModifiedTrajectories(
			final OmegaParticleLinkingRun analysisRun) {
		// TODO maybe to refactor
		// think to move the actual analysis Run selection inside here
		this.currentlyModifiedTrajectories.clear();
		this.currentlyModifiedTrajectories.addAll(analysisRun
				.getResultingTrajectories());
		
		final Map<OmegaAnalysisRun, List<RelinkingAction>> actions = this.actions;
		
		if (actions.containsKey(analysisRun)) {
			for (final RelinkingAction action : actions.get(analysisRun)) {
				if (action.hasBeenApplied()) {
					continue;
				}
				this.currentlyModifiedTrajectories.removeAll(action
						.getOriginalTrajectories());
				this.currentlyModifiedTrajectories.addAll(action
						.getModifiedTrajectories());
			}
		}
		
		Collections.sort(this.currentlyModifiedTrajectories);
		
		// TODO check if its working
		// end refactor
	}
	
	private void setRadius(final int radius) {
		this.tbPanel.setRadius(radius);
		this.trPanel.setRadius(radius);
	}
	
	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.tbPanel.updateParentContainer(parent);
		this.trPanel.updateParentContainer(parent);
		this.resPanel.updateParentContainer(parent);
	}
	
	@Override
	public void onCloseOperation() {
		
	}
	
	public void updateSelectedInformation(
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
		// TODO refactoring
		if (selection) {
			this.trPanel.resetTrajectories();
			this.trPanel.updateTrajectories(trajectories, false);
			this.updateSelectedInformation(trajectories);
			// this.updateSegmentTrajectories(trajectories);
		} else {
			final List<OmegaTrajectory> selectedTraj = this.tbPanel
					.getSelectedTrajectories();
			this.trPanel.updateTrajectories(selectedTraj, selection);
		}
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
			this.repaint();
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
		this.trajectoriesRelinking_cmb
				.addItem(OmegaConstants.OMEGA_RELINKING_CURRENT);
		if (this.trajRelinkingRuns.isEmpty()) {
			this.trajectoriesRelinking_cmb.setEnabled(false);
			this.resetTrajectories();
			this.popTrajRelinking = false;
			return;
		}
		
		this.popTrajRelinking = false;
		if (this.trajectoriesRelinking_cmb.getItemCount() > 1) {
			this.trajectoriesRelinking_cmb.setEnabled(true);
			this.trajectoriesRelinking_cmb.setSelectedIndex(0);
		} else {
			this.trajectoriesRelinking_cmb.setSelectedIndex(-1);
		}
	}
	
	private void fireEventSelectionImage() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionImage(
				this.getPlugin(), this.selectedImage);
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
		// TODO if selected traj is null OmegaApplication is not able to
		// recognize it
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
				this.getPlugin(), this.selectedTrajRelinkingRun);
		this.getPlugin().fireEvent(event);
	}
	
	private void fireEventSelectionCurrentTrajectoriesRelinkingRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionTrajectoriesRelinkingRun(
				this.getPlugin(), this.startingPointTrajRelinkingRun,
				this.currentlyModifiedTrajectories);
		this.getPlugin().fireEvent(event);
	}
	
	protected void fireEventTrajectories(
			final List<OmegaTrajectory> trajectories, final boolean selection) {
		// TODO modified as needed
		final OmegaPluginEvent event = new OmegaPluginEventTrajectories(
				this.getPlugin(), trajectories, selection);
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
	
	public void setGateway(final OmegaGateway gateway) {
		this.tbPanel.setGateway(gateway);
		this.trPanel.setGateway(gateway);
	}
	
	@Override
	public void sendEventTrajectories(
			final List<OmegaTrajectory> selectedTrajectories,
			final boolean selected) {
		if (selected) {
			this.trPanel.updateTrajectories(selectedTrajectories, false);
			this.updateSelectedInformation(selectedTrajectories);
		}
		if (!this.isHandlingEvent) {
			this.fireEventTrajectories(selectedTrajectories, selected);
		}
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
	
	@Override
	public void handleTrajectoryNameChanged() {
		this.repaint();
	}
	
	private void setStartingPointAndCurrentModification(
			final OmegaTrajectoriesRelinkingRun relinkingRun) {
		if (this.startingPointTrajRelinkingRun != null) {
			this.actions.remove(this.startingPointTrajRelinkingRun);
		}
		this.startingPointTrajRelinkingRun = relinkingRun;
		this.trajectoriesRelinking_cmb
				.setSelectedItem(OmegaConstants.OMEGA_RELINKING_CURRENT);
	}
	
	private void resetStartingPointAndCurrentModification() {
		final int index = this.trajRelinkingRuns
				.indexOf(this.startingPointTrajRelinkingRun);
		this.trajectoriesRelinking_cmb.setSelectedIndex(index);
	}
}
