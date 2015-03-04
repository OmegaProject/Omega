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
package unused.trajectoryManagerPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import unused.trajectoryManagerPlugin.actions.TMAction;
import unused.trajectoryManagerPlugin.actions.TMAdjustAction;
import unused.trajectoryManagerPlugin.actions.TMSegmentAction;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaConstantsAlgorithmParameters;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventResultsTrajectoriesRelinking;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionImage;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventTrajectories;
import edu.umassmed.omega.commons.exceptions.OmegaPluginExceptionStatusPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.gui.GenericStatusPanel;
import edu.umassmed.omega.commons.gui.dialogs.GenericConfirmationDialog;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.commons.utilities.OmegaAnalysisRunContainerUtilities;
import edu.umassmed.omega.core.OmegaLogFileManager;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class TMPluginPanel extends GenericPluginPanel {

	private static final long serialVersionUID = -5740459087763362607L;

	private final OmegaGateway gateway;

	private JComboBox<String> images_combo, particles_combo,
	        trajectories_combo, trajectoriesManager_combo;
	private JButton saveNew_butt, saveOverride_butt, undo_butt, redo_butt,
	        cancel_butt;
	private boolean popImages, popParticles, popTrajectories, popTrajManager,
	        isHandlingEvent, isAdjustSelected;

	private JMenu tm_menu;
	private JMenuItem saveNew_mItem, saveOverride_mItem, undo_mItem,
	        redo_mItem, cancel_mItem, preferences_mItem;

	private ActionListener saveNew_al, saveOverride_al, undo_al, redo_al,
	        cancel_al;

	private JTabbedPane tabbedPane;

	private TMAdjustTrajectoriesPanel trajectoriesAdjPanel;
	private JScrollPane trajectoriesAdjScrollPane;
	private TMSegmentTrajectoriesPanel trajectoriesSegmPanel;
	private GenericStatusPanel statusPanel;
	private TMCurrentTrajectoryInformation currentTrajInfoPanel;
	private TMSegmentPreferencesDialog segmentPreferencesDialog;

	private List<OmegaImage> images;

	private OmegaImage selectedImage;

	private List<OmegaSegmentationTypes> segmTypesList;
	private OmegaSegmentationTypes actualSegmentationTypes;

	private List<OmegaAnalysisRun> loadedAnalysisRuns;
	private final Map<OmegaAnalysisRun, List<TMAction>> adjustActions,
	        cancelledAdjustActions;
	private final Map<OmegaAnalysisRun, List<TMAction>> segmentActions,
	        cancelledSegmentActions;
	private final List<OmegaTrajectory> actualModifiedTrajectories;

	final List<OmegaParticleDetectionRun> particleDetectionRuns;
	private OmegaParticleDetectionRun selectedParticleDetectionRun;
	final List<OmegaParticleLinkingRun> particleLinkingRuns;
	private OmegaParticleLinkingRun selectedParticleLinkingRun;
	final List<OmegaTrajectoriesRelinkingRun> trajManagerRuns;
	private OmegaTrajectoriesRelinkingRun selectedTrajManagerRun;

	public TMPluginPanel(final RootPaneContainer parent,
	        final OmegaPlugin plugin, final OmegaGateway gateway,
	        final List<OmegaImage> images,
	        final List<OmegaAnalysisRun> analysisRuns,
	        final List<OmegaSegmentationTypes> segmTypesList, final int index) {
		super(parent, plugin, index);

		this.gateway = gateway;

		// TODO probably to refactor and move somewhere else
		// TODO when loading trajectoryAnalyisisRun I should be able to set the
		// correct segmentationTypes related
		this.segmTypesList = segmTypesList;
		this.actualSegmentationTypes = segmTypesList.get(0);

		this.adjustActions = new LinkedHashMap<>();
		this.cancelledAdjustActions = new LinkedHashMap<>();
		this.segmentActions = new LinkedHashMap<>();
		this.cancelledSegmentActions = new LinkedHashMap<>();

		this.selectedImage = null;
		this.particleDetectionRuns = new ArrayList<>();
		this.selectedParticleDetectionRun = null;
		this.particleLinkingRuns = new ArrayList<>();
		this.selectedParticleLinkingRun = null;
		this.trajManagerRuns = new ArrayList<>();
		this.selectedTrajManagerRun = null;

		this.actualModifiedTrajectories = new ArrayList<>();

		this.images = images;
		this.loadedAnalysisRuns = analysisRuns;

		this.popImages = false;
		this.popParticles = false;
		this.popTrajectories = false;
		this.popTrajManager = false;
		this.isHandlingEvent = false;
		this.isAdjustSelected = true;

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
		final JMenuBar menu = super.getMenu();

		this.tm_menu = new JMenu("Edit");

		this.saveNew_mItem = new JMenuItem("Save new");
		this.tm_menu.add(this.saveNew_mItem);

		this.saveOverride_mItem = new JMenuItem("Save override");
		this.tm_menu.add(this.saveOverride_mItem);
		this.saveOverride_mItem.setEnabled(false);

		this.undo_mItem = new JMenuItem("Undo last action");
		this.tm_menu.add(this.undo_mItem);
		this.undo_mItem.setEnabled(false);

		this.redo_mItem = new JMenuItem("Redo last action");
		this.tm_menu.add(this.redo_mItem);
		this.redo_mItem.setEnabled(false);

		this.cancel_mItem = new JMenuItem("Cancel all actions");
		this.tm_menu.add(this.cancel_mItem);
		this.cancel_mItem.setEnabled(false);

		this.preferences_mItem = new JMenuItem("Preferences");
		this.tm_menu.add(this.preferences_mItem);

		menu.add(this.tm_menu);
	}

	private void createAndAddWidgets() {
		this.segmentPreferencesDialog = new TMSegmentPreferencesDialog(this,
		        this.getParentContainer(), this.segmTypesList);

		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(4, 1));
		this.images_combo = new JComboBox<String>();
		this.images_combo.setEnabled(false);
		topPanel.add(this.images_combo);

		this.particles_combo = new JComboBox<String>();
		this.particles_combo.setEnabled(false);
		topPanel.add(this.particles_combo);

		this.trajectories_combo = new JComboBox<String>();
		this.trajectories_combo.setEnabled(false);
		topPanel.add(this.trajectories_combo);

		this.trajectoriesManager_combo = new JComboBox<String>();
		this.trajectoriesManager_combo.setEnabled(false);
		topPanel.add(this.trajectoriesManager_combo);

		this.add(topPanel, BorderLayout.NORTH);

		this.tabbedPane = new JTabbedPane();

		this.trajectoriesAdjPanel = new TMAdjustTrajectoriesPanel(
		        this.getParentContainer(), this, this.gateway);
		this.trajectoriesAdjScrollPane = new JScrollPane(
		        this.trajectoriesAdjPanel);
		this.tabbedPane.add("Manul adjustment", this.trajectoriesAdjScrollPane);

		this.trajectoriesSegmPanel = new TMSegmentTrajectoriesPanel(
		        this.getParentContainer(), this, this.actualSegmentationTypes);
		this.tabbedPane.add("Segmentation", this.trajectoriesSegmPanel);

		this.add(this.tabbedPane, BorderLayout.CENTER);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		final JPanel bottomSubPanel = new JPanel();
		bottomSubPanel.setLayout(new BorderLayout());

		this.currentTrajInfoPanel = new TMCurrentTrajectoryInformation(
		        this.getParentContainer(), this);
		bottomSubPanel.add(this.currentTrajInfoPanel, BorderLayout.NORTH);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		this.saveNew_butt = new JButton("Save new");
		this.saveNew_butt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.saveNew_butt);

		this.saveOverride_butt = new JButton("Save ovveride");
		this.saveOverride_butt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.saveOverride_butt);
		this.saveOverride_butt.setEnabled(false);

		this.undo_butt = new JButton("Undo");
		this.undo_butt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.undo_butt);
		this.undo_butt.setEnabled(false);

		this.redo_butt = new JButton("Redo");
		this.redo_butt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.redo_butt);
		this.redo_butt.setEnabled(false);

		this.cancel_butt = new JButton("Cancel");
		this.cancel_butt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.cancel_butt);
		this.cancel_butt.setEnabled(false);

		bottomSubPanel.add(buttonPanel, BorderLayout.SOUTH);
		bottomPanel.add(bottomSubPanel, BorderLayout.NORTH);

		this.statusPanel = new GenericStatusPanel(1);
		bottomPanel.add(this.statusPanel, BorderLayout.SOUTH);

		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void addListeners() {
		this.tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent evt) {
				final Component selected = TMPluginPanel.this.tabbedPane
				        .getSelectedComponent();
				if (selected.equals(TMPluginPanel.this.trajectoriesAdjPanel)) {
					TMPluginPanel.this.isAdjustSelected = true;
				} else {
					TMPluginPanel.this.isAdjustSelected = false;
				}
				TMPluginPanel.this.activateNeededButton();
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				super.componentResized(evt);
				TMPluginPanel.this.trajectoriesAdjScrollPane.repaint();
				TMPluginPanel.this.trajectoriesSegmPanel.repaint();
			}
		});
		this.images_combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMPluginPanel.this.selectImage();
			}
		});
		this.particles_combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMPluginPanel.this.selectParticleDetectionRun();
			}
		});
		this.trajectories_combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMPluginPanel.this.selectParticleLinkingRun();
			}
		});
		this.trajectoriesManager_combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMPluginPanel.this.selectTrajectoriesManagerRun();
			}
		});
		this.saveNew_butt.addActionListener(this
		        .getSaveNewAllActionsActionListener());
		this.saveOverride_butt.addActionListener(this
		        .getSaveOverrideAllActionsActionListener());
		this.undo_butt
		        .addActionListener(this.getUndoLastActionActionListener());
		this.redo_butt
		        .addActionListener(this.getRedoLastActionActionListener());
		this.cancel_butt.addActionListener(this
		        .getCancelAllActionsActionListener());
		this.saveNew_mItem.addActionListener(this
		        .getSaveNewAllActionsActionListener());
		this.saveOverride_mItem.addActionListener(this
		        .getSaveOverrideAllActionsActionListener());
		this.undo_mItem.addActionListener(this
		        .getUndoLastActionActionListener());
		this.redo_mItem.addActionListener(this
		        .getRedoLastActionActionListener());
		this.cancel_mItem.addActionListener(this
		        .getCancelAllActionsActionListener());
		this.preferences_mItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMPluginPanel.this.openPreferencesDialog();
			}
		});
	}

	private void openPreferencesDialog() {
		this.segmentPreferencesDialog.setVisible(true);
	}

	private void activateNeededButton() {
		OmegaParticleLinkingRun actualModification = this.selectedTrajManagerRun;
		if (actualModification == null) {
			actualModification = this.selectedParticleLinkingRun;
			this.setEnableSaveOverride(false);
		} else {
			this.setEnableSaveOverride(true);
		}
		Map<OmegaAnalysisRun, List<TMAction>> actions = null;
		Map<OmegaAnalysisRun, List<TMAction>> cancelledActions = null;
		if (this.isAdjustSelected) {
			actions = this.adjustActions;
			cancelledActions = this.cancelledAdjustActions;
		} else {
			actions = this.segmentActions;
			cancelledActions = this.cancelledSegmentActions;
		}

		if (actions.containsKey(actualModification)) {
			this.setEnableUndo(true);
			this.setEnableCancel(true);
		} else {
			this.setEnableUndo(false);
			this.setEnableCancel(false);
		}
		if (cancelledActions.containsKey(actualModification)) {
			this.setEnableRedo(true);
		} else {
			this.setEnableRedo(false);
		}
	}

	private ActionListener getSaveNewAllActionsActionListener() {
		if (this.saveNew_al == null) {
			this.saveNew_al = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					TMPluginPanel.this.saveNewAllActions();
				}
			};
		}
		return this.saveNew_al;
	}

	private ActionListener getSaveOverrideAllActionsActionListener() {
		if (this.saveOverride_al == null) {
			this.saveOverride_al = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					TMPluginPanel.this.saveOverrideAllActions();
				}
			};
		}
		return this.saveOverride_al;
	}

	private ActionListener getUndoLastActionActionListener() {
		if (this.undo_al == null) {
			this.undo_al = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					TMPluginPanel.this.undoLastAction();
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
					TMPluginPanel.this.redoLastAction();
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
					TMPluginPanel.this.cancelAllActions();
				}
			};
		}
		return this.cancel_al;
	}

	private void saveNewAllActions() {
		final StringBuffer buf = new StringBuffer();
		buf.append("Do you want to save all actual changes in a new analysis?");

		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
		        this.getParentContainer(), "Save changes", buf.toString(), true);
		dialog.setVisible(true);
		if (!dialog.getConfirmation())
			return;

		final List<OmegaTrajectory> resultingTrajectories = new ArrayList<OmegaTrajectory>(
		        this.actualModifiedTrajectories);

		final OmegaPluginEvent event = new OmegaPluginEventResultsTrajectoriesRelinking(
		        this.getPlugin(), this.selectedParticleLinkingRun,
		        resultingTrajectories, this.getSegmentsMap(),
		        this.actualSegmentationTypes);
		this.markActionsApplied(this.selectedParticleLinkingRun);
		this.getPlugin().fireEvent(event);
	}

	private void saveOverrideAllActions() {
		if (this.selectedTrajManagerRun == null)
			return;
		// TODO throw error

		final StringBuffer buf = new StringBuffer();
		buf.append("Do you want to save all actual changes in the current analysis?");

		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
		        this.getParentContainer(), "Save changes", buf.toString(), true);
		dialog.setVisible(true);
		if (!dialog.getConfirmation())
			return;

		List<OmegaTrajectory> resultingTrajectories = null;
		Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = this
		        .getSegmentsMap();
		OmegaSegmentationTypes segmTypes = null;

		final boolean isTrajectoriesChanged = OmegaAnalysisRunContainerUtilities
		        .isTrajectoriesListEqual(this.actualModifiedTrajectories,
		                this.selectedTrajManagerRun.getResultingTrajectories());
		if (isTrajectoriesChanged) {
			resultingTrajectories = new ArrayList<OmegaTrajectory>(
			        this.actualModifiedTrajectories);
		}

		final boolean isSegmentsChanged = OmegaAnalysisRunContainerUtilities
		        .isSegmentMapEqual(segmentsMap,
		                this.selectedTrajManagerRun.getResultingSegments());
		if (!isSegmentsChanged) {
			segmentsMap = null;
		}

		final boolean isSegmentationTypesChanged = this.actualSegmentationTypes
		        .isEqual(this.selectedTrajManagerRun.getSegmentationTypes());
		if (isSegmentationTypesChanged) {
			segmTypes = this.actualSegmentationTypes;
		}

		final OmegaPluginEvent event = new OmegaTrajectoriesManagerResultsEvent(
		        this.getPlugin(), this.selectedParticleLinkingRun,
		        resultingTrajectories, segmentsMap, segmTypes,
		        this.selectedTrajManagerRun);
		this.markActionsApplied(this.selectedTrajManagerRun);
		this.getPlugin().fireEvent(event);
	}

	private void undoLastAction() {
		OmegaParticleLinkingRun actualModification = this.selectedTrajManagerRun;
		if (actualModification == null) {
			actualModification = this.selectedParticleLinkingRun;
		}

		Map<OmegaAnalysisRun, List<TMAction>> actions = null;
		Map<OmegaAnalysisRun, List<TMAction>> cancelledActions = null;
		if (this.isAdjustSelected) {
			actions = this.adjustActions;
			cancelledActions = this.cancelledAdjustActions;
		} else {
			actions = this.segmentActions;
			cancelledActions = this.cancelledSegmentActions;
		}

		final List<TMAction> actionList = actions.get(actualModification);

		final TMAction lastAction = actionList.get(actionList.size() - 1);
		actionList.remove(lastAction);

		if (actions.isEmpty()) {
			actions.remove(actualModification);
		} else {
			actions.put(actualModification, actionList);
		}

		List<TMAction> cancelledActionList = null;
		if (cancelledActions.containsKey(actualModification)) {
			cancelledActionList = cancelledActions.get(actualModification);
		} else {
			cancelledActionList = new ArrayList<TMAction>();
		}
		cancelledActionList.add(lastAction);

		cancelledActions.put(actualModification, cancelledActionList);

		if (actionList.isEmpty()) {
			this.setEnableUndo(false);
			this.setEnableCancel(false);
		}
		this.setEnableRedo(true);

		if (this.isAdjustSelected) {
			this.updateActualModifiedTrajectories(actualModification);
			this.drawTrajectoriesTable();
		} else {
			this.updateActualSegmentedTrajectories();
		}
	}

	private void redoLastAction() {
		OmegaParticleLinkingRun actualModification = this.selectedTrajManagerRun;
		if (actualModification == null) {
			actualModification = this.selectedParticleLinkingRun;
		}

		Map<OmegaAnalysisRun, List<TMAction>> actions = null;
		Map<OmegaAnalysisRun, List<TMAction>> cancelledActions = null;
		if (this.isAdjustSelected) {
			actions = this.adjustActions;
			cancelledActions = this.cancelledAdjustActions;
		} else {
			actions = this.segmentActions;
			cancelledActions = this.cancelledSegmentActions;
		}

		final List<TMAction> cancelledActionList = cancelledActions
		        .get(actualModification);

		final TMAction lastAction = cancelledActionList.get(cancelledActionList
		        .size() - 1);
		cancelledActionList.remove(lastAction);

		if (cancelledActionList.isEmpty()) {
			cancelledActions.remove(actualModification);
		} else {
			cancelledActions.put(actualModification, cancelledActionList);
		}

		List<TMAction> actionList = null;
		if (actions.containsKey(actualModification)) {
			actionList = actions.get(actualModification);
		} else {
			actionList = new ArrayList<TMAction>();
		}
		actionList.add(lastAction);

		actions.put(actualModification, actionList);

		if (cancelledActionList.isEmpty()) {
			this.setEnableRedo(false);
		}
		this.setEnableUndo(true);
		this.setEnableCancel(true);

		if (this.isAdjustSelected) {
			this.updateActualModifiedTrajectories(actualModification);
			this.drawTrajectoriesTable();
		} else {
			this.updateActualSegmentedTrajectories();
		}
	}

	private void cancelAllActions() {
		OmegaParticleLinkingRun actualModification = this.selectedTrajManagerRun;
		if (actualModification == null) {
			actualModification = this.selectedParticleLinkingRun;
		}

		final StringBuffer buf = new StringBuffer();
		buf.append("Do you want to cancel all actual changes?");

		final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
		        this.getParentContainer(), "Cancel all changes",
		        buf.toString(), true);
		dialog.setVisible(true);
		if (!dialog.getConfirmation())
			return;

		Map<OmegaAnalysisRun, List<TMAction>> actions = null;
		Map<OmegaAnalysisRun, List<TMAction>> cancelledActions = null;
		if (this.isAdjustSelected) {
			actions = this.adjustActions;
			cancelledActions = this.cancelledAdjustActions;
		} else {
			actions = this.segmentActions;
			cancelledActions = this.cancelledSegmentActions;
		}

		cancelledActions.put(actualModification,
		        actions.get(actualModification));
		actions.remove(actualModification);

		this.setEnableUndo(false);
		this.setEnableCancel(false);
		this.setEnableRedo(true);

		if (this.isAdjustSelected) {
			this.updateActualModifiedTrajectories(actualModification);
			this.drawTrajectoriesTable();
		} else {
			this.updateActualSegmentedTrajectories();
		}
	}

	private void markActionsApplied(final OmegaParticleLinkingRun analysisRun) {
		if (this.adjustActions.containsKey(analysisRun)) {
			final List<TMAction> actionList = this.adjustActions
			        .get(analysisRun);
			for (final TMAction action : actionList) {
				action.setHasBeenApplied(true);
			}
		}
		if (this.segmentActions.containsKey(analysisRun)) {
			final List<TMAction> actionList = this.segmentActions
			        .get(analysisRun);
			for (final TMAction action : actionList) {
				action.setHasBeenApplied(true);
			}
		}
	}

	private void setEnableSaveOverride(final boolean enabled) {
		this.saveOverride_butt.setEnabled(enabled);
		this.saveOverride_mItem.setEnabled(enabled);
	}

	private void setEnableUndo(final boolean enabled) {
		this.undo_butt.setEnabled(enabled);
		this.undo_mItem.setEnabled(enabled);
	}

	private void setEnableRedo(final boolean enabled) {
		this.redo_butt.setEnabled(enabled);
		this.redo_mItem.setEnabled(enabled);
	}

	private void setEnableCancel(final boolean enabled) {
		this.cancel_butt.setEnabled(enabled);
		this.cancel_mItem.setEnabled(enabled);
	}

	protected void segmentTrajectory(final OmegaTrajectory trajectory,
	        final List<OmegaSegment> segmentationResults,
	        final OmegaROI startingROI, final OmegaROI endingROI,
	        final String segmName) {
		final int segmValue = this.actualSegmentationTypes
		        .getSegmentationValue(segmName);
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
				edgesToRemove.add(edge);
				final OmegaSegment newEdge = new OmegaSegment(
				        edge.getStartingROI(), edge.getEndingROI());
				newEdge.setSegmentationType(segmValue);
				edgesToAdd.add(newEdge);
				needCheckIteration = false;
			} else if ((edgeStartingIndex > startingIndex)
			        && (edgeEndingIndex < endingIndex)) {
				// Case new edge include old edge
				final OmegaSegment newEdge = new OmegaSegment(startingROI,
				        endingROI);
				newEdge.setSegmentationType(segmValue);
				edgesToAdd.add(newEdge);
				edgesToRemove.add(edge);
			} else if ((edgeEndingIndex > startingIndex)
			        && (edgeEndingIndex <= endingIndex)) {
				// Case new edge is at the end
				edgesToAdd.addAll(this.createEdgeAtTheEnd(edge, startingROI,
				        endingROI, segmValue));
				edgesToRemove.add(edge);
			} else if ((edgeStartingIndex >= startingIndex)
			        && (edgeStartingIndex < endingIndex)) {
				// Case new edge is at the beginning
				edgesToAdd.addAll(this.createEdgeAtTheBeginning(edge,
				        startingROI, endingROI, segmValue));
				edgesToRemove.add(edge);
			} else if ((edgeStartingIndex <= startingIndex)
			        && (edgeEndingIndex >= endingIndex)) {
				// Case new edge is in between another edge
				edgesToAdd.addAll(this.createEdgeInBetween(edge, startingROI,
				        endingROI, segmValue));
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
				this.segmentTrajectory(segmentationResults, edge);
			}
		}

		// TODO fix if same segTypeTraj merge / avoid split etc
		System.out.println("##########################");
		System.out.println("Traj: " + trajectory.getName());
		for (final OmegaSegment edge : segmentationResults) {
			System.out.println("From " + edge.getStartingROI().getFrameIndex()
			        + " To " + edge.getEndingROI().getFrameIndex()
			        + " segType " + edge.getSegmentationType());
		}
		System.out.println("##########################");

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

		final TMAction action = new TMSegmentAction(trajectory, edgesToRemove,
		        edgesToAdd);
		this.addAction(action);

		this.updateActualSegmentedTrajectories();
	}

	private void segmentTrajectory(
	        final List<OmegaSegment> segmentationResults,
	        final OmegaSegment newEdge) {
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
					final OmegaSegment trunk = new OmegaSegment(
					        edge.getStartingROI(), newEdge.getStartingROI());
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
					final OmegaSegment trunk = new OmegaSegment(
					        newEdge.getEndingROI(), edge.getEndingROI());
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
				this.segmentTrajectory(segmentationResults, edge);
			}
		}
	}

	private List<OmegaSegment> createEdgeAtTheBeginning(
	        final OmegaSegment edge, final OmegaROI startingROI,
	        final OmegaROI endingROI, final int actualSegmentationType) {
		final List<OmegaSegment> edgeToAdd = new ArrayList<OmegaSegment>();

		final OmegaSegment oldEdge = new OmegaSegment(endingROI,
		        edge.getEndingROI());
		oldEdge.setSegmentationType(edge.getSegmentationType());
		edgeToAdd.add(oldEdge);

		final OmegaSegment newEdge = new OmegaSegment(startingROI, endingROI);
		newEdge.setSegmentationType(actualSegmentationType);
		edgeToAdd.add(newEdge);

		return edgeToAdd;
	}

	private List<OmegaSegment> createEdgeAtTheEnd(final OmegaSegment edge,
	        final OmegaROI startingROI, final OmegaROI endingROI,
	        final int actualSegmentationType) {
		final List<OmegaSegment> edgeToAdd = new ArrayList<OmegaSegment>();

		final OmegaSegment oldEdge = new OmegaSegment(edge.getStartingROI(),
		        startingROI);
		oldEdge.setSegmentationType(edge.getSegmentationType());
		edgeToAdd.add(oldEdge);

		final OmegaSegment newEdge = new OmegaSegment(startingROI, endingROI);
		newEdge.setSegmentationType(actualSegmentationType);
		edgeToAdd.add(newEdge);

		return edgeToAdd;
	}

	private List<OmegaSegment> createEdgeInBetween(final OmegaSegment edge,
	        final OmegaROI startingROI, final OmegaROI endingROI,
	        final int actualSegmentationType) {
		final List<OmegaSegment> edgeToAdd = new ArrayList<OmegaSegment>();

		final OmegaSegment trunk1 = new OmegaSegment(edge.getStartingROI(),
		        startingROI);
		trunk1.setSegmentationType(edge.getSegmentationType());
		edgeToAdd.add(trunk1);

		final OmegaSegment trunk2 = new OmegaSegment(endingROI,
		        edge.getEndingROI());
		trunk2.setSegmentationType(edge.getSegmentationType());
		edgeToAdd.add(trunk2);

		final OmegaSegment newEdge = new OmegaSegment(startingROI, endingROI);
		newEdge.setSegmentationType(actualSegmentationType);
		edgeToAdd.add(newEdge);

		return edgeToAdd;
	}

	protected void mergeTrajectories(final OmegaTrajectory from,
	        final OmegaTrajectory to) {
		final OmegaTrajectory newTraj = new OmegaTrajectory(from.getLength()
		        + to.getLength());
		newTraj.setName(from.getName() + "_M_" + to.getName());
		newTraj.addROIs(from.getROIs());
		newTraj.addROIs(to.getROIs());

		final List<OmegaTrajectory> original = new ArrayList<OmegaTrajectory>();
		original.add(from);
		original.add(to);
		final List<OmegaTrajectory> modified = new ArrayList<OmegaTrajectory>();
		modified.add(newTraj);
		final TMAction action = new TMAdjustAction(original, modified);
		this.addAction(action);

		OmegaParticleLinkingRun actualModification = this.selectedTrajManagerRun;
		if (actualModification == null) {
			actualModification = this.selectedParticleLinkingRun;
		}
		this.updateActualModifiedTrajectories(actualModification);
		this.drawTrajectoriesTable();
	}

	protected void splitTrajectory(final OmegaTrajectory from,
	        final int particleIndex) {
		final int newTrajLength = from.getLength() - particleIndex;
		final OmegaTrajectory newTraj1 = new OmegaTrajectory(particleIndex);
		newTraj1.setName(from.getName() + "_S1");
		newTraj1.setColor(from.getColor());
		for (int i = 0; i < particleIndex; i++) {
			final OmegaROI roi = from.getROIs().get(i);
			newTraj1.addROI(roi);
		}

		final OmegaTrajectory newTraj2 = new OmegaTrajectory(newTrajLength);
		newTraj2.setName(from.getName() + "_S2");
		newTraj2.setColor(from.getColor());
		for (int i = particleIndex; i < from.getLength(); i++) {
			final OmegaROI roi = from.getROIs().get(i);
			newTraj2.addROI(roi);
		}

		final List<OmegaTrajectory> original = new ArrayList<OmegaTrajectory>();
		original.add(from);
		final List<OmegaTrajectory> modified = new ArrayList<OmegaTrajectory>();
		modified.add(newTraj1);
		modified.add(newTraj2);
		final TMAction action = new TMAdjustAction(original, modified);
		this.addAction(action);

		OmegaParticleLinkingRun actualModification = this.selectedTrajManagerRun;
		if (actualModification == null) {
			actualModification = this.selectedParticleLinkingRun;
		}
		this.updateActualModifiedTrajectories(actualModification);
		this.drawTrajectoriesTable();
	}

	private void addAction(final TMAction action) {
		List<TMAction> actionList = null;
		OmegaParticleLinkingRun actualModification = this.selectedTrajManagerRun;
		if (actualModification == null) {
			actualModification = this.selectedParticleLinkingRun;
		}

		Map<OmegaAnalysisRun, List<TMAction>> actions = null;
		if (this.isAdjustSelected) {
			actions = this.adjustActions;
		} else {
			actions = this.segmentActions;
		}

		if (actions.containsKey(actualModification)) {
			actionList = actions.get(actualModification);
		} else {
			actionList = new ArrayList<TMAction>();
		}
		actionList.add(action);
		actions.put(actualModification, actionList);

		if (actualModification instanceof OmegaTrajectoriesManagerRun) {
			this.setEnableSaveOverride(true);
		} else {
			this.setEnableSaveOverride(false);
		}
		this.setEnableUndo(true);
		this.setEnableCancel(true);
	}

	private void selectImage() {
		if (this.popImages)
			return;
		final int index = this.images_combo.getSelectedIndex();
		this.selectedImage = null;
		if (index == -1)
			return;
		this.selectedImage = this.images.get(index);
		this.trajectoriesAdjPanel.setImage(this.selectedImage);
		if (!this.isHandlingEvent) {
			this.fireTMPluginImageSelectionEvent();
		}
		this.populateParticlesCombo();
		this.populateTrajectoriesCombo();
		this.trajectoriesSegmPanel.setPixelSizes(this.selectedImage
		        .getDefaultPixels().getPixelSizeX(), this.selectedImage
		        .getDefaultPixels().getPixelSizeY());
	}

	private void selectParticleDetectionRun() {
		if (this.popParticles)
			return;
		final int index = this.particles_combo.getSelectedIndex();
		this.selectedParticleDetectionRun = null;
		if (index == -1)
			return;
		this.selectedParticleDetectionRun = this.particleDetectionRuns
		        .get(index);
		if (!this.isHandlingEvent) {
			this.fireTMPluginParticleDetectionRunSelectionEvent();
		}
		this.populateTrajectoriesCombo();
	}

	private void selectParticleLinkingRun() {
		if (this.popTrajectories)
			return;
		final int index = this.trajectories_combo.getSelectedIndex();
		this.selectedParticleLinkingRun = null;
		if (index == -1)
			return;
		this.selectedParticleLinkingRun = this.particleLinkingRuns.get(index);
		if (!this.isHandlingEvent) {
			this.fireTMPluginParticleLinkingRunSelectionEvent();
		}
		final OmegaParameter radius = this.selectedParticleLinkingRun
		        .getAlgorithmSpec().getParameter(
		                OmegaConstantsAlgorithmParameters.PARAM_RADIUS);
		if ((radius != null)
		        && radius.getClazz().equals(Integer.class.getName())) {
			this.setRadius((int) radius.getValue());
		}

		this.populateTrajectoriesManagerCombo();

		// this.updateActualModifiedTrajectories(this.selectedParticleLinkingRun);
		// this.drawTrajectoriesTable();
	}

	private void selectTrajectoriesManagerRun() {
		if (this.popTrajManager)
			return;
		final int index = this.trajectoriesManager_combo.getSelectedIndex();
		this.selectedTrajManagerRun = null;
		if (index == -1)
			return;

		if (index < this.trajManagerRuns.size()) {
			this.selectedTrajManagerRun = this.trajManagerRuns.get(index);
		}

		if (!this.isHandlingEvent) {
			this.fireTMPluginTrajectoriesManagerRunSelectionEvent();
		}

		OmegaParticleLinkingRun actualModification = this.selectedTrajManagerRun;
		if (actualModification == null) {
			actualModification = this.selectedParticleLinkingRun;
		}
		this.updateActualModifiedTrajectories(actualModification);

		this.activateNeededButton();

		this.drawTrajectoriesTable();
	}

	private void updateActualSegmentedTrajectories() {
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = this
		        .getSegmentsMap();
		this.trajectoriesSegmPanel.updateActualSegmentTrajectories(segmentsMap);
	}

	private Map<OmegaTrajectory, List<OmegaSegment>> getSegmentsMap() {
		OmegaParticleLinkingRun actualModification = this.selectedTrajManagerRun;
		if (actualModification == null) {
			actualModification = this.selectedParticleLinkingRun;
		}
		final List<TMAction> actions = this.segmentActions
		        .get(actualModification);
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = new LinkedHashMap<OmegaTrajectory, List<OmegaSegment>>();
		for (final OmegaTrajectory traj : this.actualModifiedTrajectories) {
			final List<TMSegmentAction> relatedActions = new ArrayList<TMSegmentAction>();
			if (actions != null) {
				for (final TMAction action : actions) {
					if (!(action instanceof TMSegmentAction)) {
						continue;
					}
					final TMSegmentAction segAction = (TMSegmentAction) action;
					if (segAction.getTrajectory().equals(traj)) {
						relatedActions.add(segAction);
					}
				}
			}
			final List<OmegaSegment> originalSegments = this
			        .applyDefaultSegmentation(traj);
			final List<OmegaSegment> newSegments = this.applySegmentActions(
			        originalSegments, relatedActions);
			segmentsMap.put(traj, newSegments);
		}
		return segmentsMap;
	}

	private List<OmegaSegment> applySegmentActions(
	        final List<OmegaSegment> originalSegments,
	        final List<TMSegmentAction> relatedActions) {
		final List<OmegaSegment> newSegments = new ArrayList<OmegaSegment>(
		        originalSegments);
		if ((relatedActions != null) && !relatedActions.isEmpty()) {
			System.out.println("**********************************");
			for (final TMSegmentAction action : relatedActions) {
				System.out.println("TO REMOVE");
				for (final OmegaSegment segm : action.getOriginalEdges()) {
					System.out.println("From "
					        + segm.getStartingROI().getFrameIndex() + " to "
					        + segm.getEndingROI().getFrameIndex() + " type "
					        + segm.getSegmentationType());
				}
				System.out.println("TO ADD");
				for (final OmegaSegment segm : action.getModifiedEdges()) {
					System.out.println("From "
					        + segm.getStartingROI().getFrameIndex() + " to "
					        + segm.getEndingROI().getFrameIndex() + " type "
					        + segm.getSegmentationType());
				}
			}
			System.out.println("**********************************");
			final List<OmegaSegment> edgesToRemove = new ArrayList<OmegaSegment>();
			for (final TMSegmentAction action : relatedActions) {
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

	private List<OmegaSegment> applyDefaultSegmentation(
	        final OmegaTrajectory traj) {
		final List<OmegaSegment> segments = new ArrayList<OmegaSegment>();
		final OmegaROI startingPoint = traj.getROIs().get(0);
		final OmegaROI endingPoint = traj.getROIs().get(traj.getLength() - 1);
		final OmegaSegment edge = new OmegaSegment(startingPoint, endingPoint);
		edge.setSegmentationType(OmegaSegmentationTypes.NOT_ASSIGNED_VAL);
		segments.add(edge);
		return segments;
	}

	private void updateActualModifiedTrajectories(
	        final OmegaParticleLinkingRun analysisRun) {
		// TODO maybe to refactor
		// think to move the actual analysis Run selection inside here
		this.actualModifiedTrajectories.clear();
		this.actualModifiedTrajectories.addAll(analysisRun
		        .getResultingTrajectories());

		Map<OmegaAnalysisRun, List<TMAction>> actions = null;
		if (this.isAdjustSelected) {
			actions = this.adjustActions;
		} else {
			actions = this.segmentActions;
		}

		if (actions.containsKey(analysisRun)) {
			for (final TMAction action : actions.get(analysisRun)) {
				if (action.hasBeenApplied()) {
					continue;
				}
				if (action instanceof TMAdjustAction) {
					final TMAdjustAction adjust = (TMAdjustAction) action;
					this.actualModifiedTrajectories.removeAll(adjust
					        .getOriginalTrajectories());
					this.actualModifiedTrajectories.addAll(adjust
					        .getModifiedTrajectories());
				}
			}
		}

		Collections.sort(this.actualModifiedTrajectories);

		// TODO check if its working
		this.fireTMPluginTrajectoriesEvent(this.actualModifiedTrajectories,
		        false);
		// end refactor
	}

	private void setRadius(final int radius) {
		this.trajectoriesAdjPanel.setRadius(radius);
		this.trajectoriesSegmPanel.setRadius(radius);
	}

	private void drawTrajectoriesTable() {
		this.updateAdjustTrajectories(this.actualModifiedTrajectories, false);
		// trajectoriesPanel.updateTrajectories(this.selectedParticleLinkingRun);
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.trajectoriesAdjPanel.updateParentContainer(parent);
		this.trajectoriesSegmPanel.updateParentContainer(parent);
		this.segmentPreferencesDialog.updateParentContainer(parent);
	}

	@Override
	public void onCloseOperation() {

	}

	private void updateSelectedInformation(
	        final List<OmegaTrajectory> trajectories) {
		this.currentTrajInfoPanel.setSelectedTrajectories(trajectories);
	}

	public void updateAdjustTrajectories(
	        final List<OmegaTrajectory> trajectories, final boolean selection) {
		// TODO modify to keep changes if needed
		this.trajectoriesAdjPanel.updateTrajectories(trajectories, selection);
		// TODO refactoring ?
		if (selection) {
			this.updateSegmentTrajectories(trajectories);
		}
	}

	public void updateSegmentTrajectories(
	        final List<OmegaTrajectory> trajectories) {
		this.updateSelectedInformation(trajectories);
		this.trajectoriesSegmPanel
		        .createSegmentSingleTrajectoryPanels(trajectories);
		// TODO Apply default segmentation here
		this.updateActualSegmentedTrajectories();
	}

	public void updateCombos(final List<OmegaImage> images,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this.images = images;
		this.loadedAnalysisRuns = analysisRuns;

		this.populateImagesCombo();
	}

	private void populateImagesCombo() {
		this.popImages = true;
		this.images_combo.removeAllItems();
		this.selectedImage = null;

		if ((this.images == null) || this.images.isEmpty()) {
			this.images_combo.setEnabled(false);
			return;

		}
		this.images_combo.setEnabled(true);

		for (final OmegaImage image : this.images) {
			this.images_combo.addItem(image.getName());
		}
		this.popImages = false;
		this.images_combo.setSelectedIndex(0);
	}

	private void populateParticlesCombo() {
		this.popParticles = true;
		this.particles_combo.removeAllItems();
		this.particleDetectionRuns.clear();
		this.particles_combo.setSelectedIndex(-1);
		this.selectedParticleDetectionRun = null;

		if ((this.selectedImage == null)) {
			this.particles_combo.setEnabled(false);
			return;
		}

		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedImage.getAnalysisRuns().contains(analysisRun)
			        && (analysisRun instanceof OmegaParticleDetectionRun)) {
				this.particleDetectionRuns
				        .add((OmegaParticleDetectionRun) analysisRun);
				this.particles_combo.addItem(analysisRun.getName());
			}
		}

		if (this.particleDetectionRuns.isEmpty()) {
			this.particles_combo.setEnabled(false);
			return;
		}

		this.particles_combo.setEnabled(true);
		this.popParticles = false;
		this.particles_combo.setSelectedIndex(0);
	}

	private void populateTrajectoriesCombo() {
		this.popTrajectories = true;
		this.trajectories_combo.removeAllItems();
		this.particleLinkingRuns.clear();
		this.trajectories_combo.setSelectedIndex(-1);
		this.selectedParticleLinkingRun = null;

		if ((this.selectedParticleDetectionRun == null)) {
			this.trajectories_combo.setEnabled(false);
			return;
		}

		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedParticleDetectionRun.getAnalysisRuns().contains(
			        analysisRun)) {
				this.particleLinkingRuns
				        .add((OmegaParticleLinkingRun) analysisRun);
				this.trajectories_combo.addItem(analysisRun.getName());
			}
		}

		if (this.particleLinkingRuns.isEmpty()) {
			this.trajectories_combo.setEnabled(false);
			return;
		}

		this.trajectories_combo.setEnabled(true);
		this.popTrajectories = false;
		this.trajectories_combo.setSelectedIndex(0);
	}

	private void populateTrajectoriesManagerCombo() {
		this.popTrajManager = true;
		this.trajectoriesManager_combo.removeAllItems();
		this.trajManagerRuns.clear();
		this.trajectoriesManager_combo.setSelectedIndex(-1);
		this.selectedTrajManagerRun = null;

		if (this.selectedParticleLinkingRun == null) {
			this.trajectoriesManager_combo.setEnabled(false);
			return;
		}

		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedParticleLinkingRun.getAnalysisRuns().contains(
			        analysisRun)) {
				this.trajManagerRuns
				        .add((OmegaTrajectoriesManagerRun) analysisRun);
				this.trajectoriesManager_combo.addItem(analysisRun.getName());
			}
		}
		this.trajectoriesManager_combo.addItem("Current trajectories");

		this.trajectoriesManager_combo.setEnabled(true);
		this.popTrajManager = false;
		this.trajectoriesManager_combo.setSelectedIndex(0);
	}

	private void fireTMPluginImageSelectionEvent() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionImage(
		        this.getPlugin(), this.selectedImage);
		this.getPlugin().fireEvent(event);
	}

	private void fireTMPluginParticleDetectionRunSelectionEvent() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(), this.selectedParticleDetectionRun);
		this.getPlugin().fireEvent(event);
	}

	private void fireTMPluginParticleLinkingRunSelectionEvent() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionParticleLinkingRun(
		        this.getPlugin(), this.selectedParticleLinkingRun);
		this.getPlugin().fireEvent(event);
	}

	private void fireTMPluginTrajectoriesManagerRunSelectionEvent() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionTrajectoriesRelinkingRun(
		        this.getPlugin(), this.selectedTrajManagerRun);
		this.getPlugin().fireEvent(event);
	}

	protected void fireTMPluginTrajectoriesEvent(
	        final List<OmegaTrajectory> trajectories, final boolean selection) {
		// TODO modified as needed
		final OmegaPluginEvent event = new OmegaPluginEventTrajectories(
		        this.getPlugin(), trajectories, selection);
		this.getPlugin().fireEvent(event);
	}

	public void selectImage(final OmegaImage image) {
		this.isHandlingEvent = true;
		final int index = this.images.indexOf(image);
		this.images_combo.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void selectParticleDetectionRun(
	        final OmegaParticleDetectionRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.particleLinkingRuns.indexOf(analysisRun);
		this.particles_combo.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void selectParticleLinkingRun(
	        final OmegaParticleLinkingRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.particleLinkingRuns.indexOf(analysisRun);
		this.trajectories_combo.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void selectTrajectoriesRelinkingRun(
	        final OmegaTrajectoriesRelinkingRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.trajManagerRuns.indexOf(analysisRun);
		this.trajectoriesManager_combo.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void setGateway(final OmegaGateway gateway) {
		this.trajectoriesAdjPanel.setGateway(gateway);
	}

	public void updateStatus(final String s) {
		try {
			this.statusPanel.updateStatus(0, s);
		} catch (final OmegaPluginExceptionStatusPanel ex) {
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex);
		}
	}

	public void handleTrajNameChanged() {
		this.repaint();
	}

	public void handleSegmTypesChanged() {
		this.segmentPreferencesDialog.setVisible(false);
		this.segmTypesList.addAll(this.segmentPreferencesDialog
		        .getSegmentationTypesList());
		this.actualSegmentationTypes = this.segmentPreferencesDialog
		        .getActualSegmentationTypes();
		this.trajectoriesSegmPanel
		        .setSegmentationTypes(this.actualSegmentationTypes);
	}

	public void setSegmentationTypesList(
	        final List<OmegaSegmentationTypes> segmTypesList) {
		this.segmTypesList = segmTypesList;
		if (segmTypesList.contains(this.actualSegmentationTypes)) {
			this.segmentPreferencesDialog.setSegmentationTypesList(
			        segmTypesList, this.actualSegmentationTypes);
		} else {
			this.segmentPreferencesDialog.setSegmentationTypesList(
			        segmTypesList, null);
		}
	}

	public Color getSegmentationColor(final int value) {
		Color c = this.actualSegmentationTypes.getSegmentationColor(value);
		if (c == null) {
			c = OmegaSegmentationTypes.NOT_ASSIGNED_COL;
		}
		return c;
	}

	public void selectSegmentStartingPoint(final OmegaROI startingROI) {
		this.trajectoriesSegmPanel.selectStartingROI(startingROI);
	}

	public void selectSegmentEndingPoint(final OmegaROI endingROI) {
		this.trajectoriesSegmPanel.selectEndingROI(endingROI);
	}
}
