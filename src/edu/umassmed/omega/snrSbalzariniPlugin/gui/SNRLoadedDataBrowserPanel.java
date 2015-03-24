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
package edu.umassmed.omega.snrSbalzariniPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.lf5.viewer.categoryexplorer.TreeModelAdapter;

import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNode;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxStatus;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaSNRRun;
import edu.umassmed.omega.data.coreElements.OmegaElement;

public class SNRLoadedDataBrowserPanel extends GenericPanel {

	private static final long serialVersionUID = -7554854467725521545L;

	private final SNRPluginPanel snrPanel;

	private final Map<String, OmegaElement> nodeMap;
	private final DefaultMutableTreeNode root;

	private JTree dataTree;

	private boolean adjusting = false;

	public SNRLoadedDataBrowserPanel(final RootPaneContainer parentContainer,
	        final SNRPluginPanel snrPanel) {
		super(parentContainer);

		this.snrPanel = snrPanel;

		this.root = new DefaultMutableTreeNode();
		this.root.setUserObject("Loaded data");
		this.nodeMap = new HashMap<String, OmegaElement>();
		// this.updateTree(images);

		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();
		this.addListeners();
	}

	private void createAndAddWidgets() {

		this.dataTree = new JTree(this.root);
		this.dataTree.getSelectionModel().setSelectionMode(
		        TreeSelectionModel.SINGLE_TREE_SELECTION);
		// this.dataTreeBrowser.setRootVisible(false);
		// final CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
		// this.dataTree.setCellRenderer(renderer);
		// this.dataTree.setCellEditor(new CheckBoxNodeEditor());

		this.dataTree.setEditable(false);

		this.dataTree.expandRow(0);
		this.dataTree.setRootVisible(false);
		// this.dataTree.setEditable(true);

		final JScrollPane scrollPane = new JScrollPane(this.dataTree);
		scrollPane.setBorder(new TitledBorder("Loaded data"));

		this.add(scrollPane, BorderLayout.CENTER);
	}

	private void addListeners() {
		this.dataTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent event) {
				SNRLoadedDataBrowserPanel.this.handleMouseClick(event
				        .getPoint());
			}
		});
		this.dataTree.getModel().addTreeModelListener(new TreeModelAdapter() {
			@Override
			public void treeNodesChanged(final TreeModelEvent event) {
				final TreePath parent = event.getTreePath();
				final Object[] children = event.getChildren();
				final DefaultTreeModel model = (DefaultTreeModel) event
				        .getSource();
				SNRLoadedDataBrowserPanel.this.handleTreeNodeChanged(parent,
				        children, model);
			}
		});
	}

	private void handleMouseClick(final Point clickP) {
		final TreePath path = this.dataTree.getPathForLocation(clickP.x,
		        clickP.y);
		if (path == null) {
			this.snrPanel.updateSelectedParticleDetectionRun(null);
			this.deselect();
			return;
		}
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
		        .getLastPathComponent();
		final String s = node.toString();
		final OmegaElement element = this.nodeMap.get(s);
		if (element instanceof OmegaParticleDetectionRun) {
			this.snrPanel
			        .updateSelectedParticleDetectionRun((OmegaParticleDetectionRun) element);
		} else if (element instanceof OmegaSNRRun) {
			final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node
			        .getParent();
			final String parentString = parentNode.toString();
			final OmegaElement parentElement = this.nodeMap.get(parentString);
			this.snrPanel
			        .updateSelectedParticleDetectionRun((OmegaParticleDetectionRun) parentElement);
			this.snrPanel.updateSelectedSNRRun((OmegaSNRRun) element);
		}
	}

	private void handleTreeNodeChanged(final TreePath parent,
	        final Object[] children, final DefaultTreeModel model) {
		if (this.adjusting)
			return;
		this.adjusting = true;

		DefaultMutableTreeNode node;
		CheckBoxNode c; // = (CheckBoxNode)node.getUserObject();
		if ((children != null) && (children.length == 1)) {
			node = (DefaultMutableTreeNode) children[0];
			c = (CheckBoxNode) node.getUserObject();
			final DefaultMutableTreeNode n = (DefaultMutableTreeNode) parent
			        .getLastPathComponent();

			model.nodeChanged(n);
		} else {
			node = (DefaultMutableTreeNode) model.getRoot();
			c = (CheckBoxNode) node.getUserObject();
		}

		model.nodeChanged(node);

		this.adjusting = false;

		c.getStatus();
		// TODO update something here
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
	}

	public void updateTree(final List<OmegaAnalysisRun> analysisRuns) {
		this.dataTree.setRootVisible(true);

		String s = null;
		final CheckBoxStatus status = CheckBoxStatus.DESELECTED;
		this.root.removeAllChildren();
		((DefaultTreeModel) this.dataTree.getModel()).reload();
		this.nodeMap.clear();
		if (analysisRuns != null) {
			for (final OmegaAnalysisRun analysisRun : analysisRuns) {
				if (!(analysisRun instanceof OmegaParticleDetectionRun)) {
					continue;
				}
				final DefaultMutableTreeNode particleDetRunNode = new DefaultMutableTreeNode();
				s = "[" + analysisRun.getElementID() + "] "
				        + analysisRun.getName();
				this.nodeMap.put(s, analysisRun);
				// status = this.loadedData.containsImage(image) ?
				// CheckBoxStatus.SELECTED
				// : CheckBoxStatus.DESELECTED;
				particleDetRunNode.setUserObject(new CheckBoxNode(s, status));

				for (final OmegaAnalysisRun innerAnalysisRun : analysisRun
				        .getAnalysisRuns()) {
					if (!(innerAnalysisRun instanceof OmegaSNRRun)) {
						continue;
					}
					final OmegaSNRRun snrRun = (OmegaSNRRun) innerAnalysisRun;
					// TODO pensare se questo e' il sistema migliore per
					// verificare il corretto funzionamento!
					if (!this.snrPanel.checkIfThisAlgorithm(snrRun)) {
						continue;
					}
					final DefaultMutableTreeNode snrAnalysisRunNode = new DefaultMutableTreeNode();
					s = "[" + snrRun.getElementID() + "] " + snrRun.getName();
					this.nodeMap.put(s, snrRun);
					snrAnalysisRunNode
					        .setUserObject(new CheckBoxNode(s, status));
					particleDetRunNode.add(snrAnalysisRunNode);
				}

				this.root.add(particleDetRunNode);
			}
		}
		this.dataTree.expandRow(0);
		this.dataTree.setRootVisible(false);
		this.dataTree.repaint();
	}

	public void deselect() {
		this.dataTree.setSelectionRow(-1);
	}
}
