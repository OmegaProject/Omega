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
package edu.umassmed.omega.plSbalzariniPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNode;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxStatus;

public class PLQueueRunBrowserPanel extends GenericPanel {
	
	private static final long serialVersionUID = -7554854467725521545L;
	
	private final PLPluginPanel plPanel;
	
	private final Map<String, OmegaElement> nodeMap;
	private final Map<String, Integer> nodeIndexes;
	private final DefaultMutableTreeNode root;

	private Integer selectedIndex;
	
	private JTree dataTree;
	
	private boolean adjusting = false;
	
	public PLQueueRunBrowserPanel(final RootPaneContainer parentContainer,
			final PLPluginPanel sptPanel) {
		super(parentContainer);
		
		this.plPanel = sptPanel;
		
		this.root = new DefaultMutableTreeNode();
		this.root.setUserObject(OmegaGUIConstants.PLUGIN_RUN_QUEUE);
		this.nodeMap = new HashMap<String, OmegaElement>();
		this.nodeIndexes = new LinkedHashMap<String, Integer>();
		// this.updateTree(images);
		this.selectedIndex = null;
		
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
		scrollPane.setBorder(new TitledBorder(
				OmegaGUIConstants.PLUGIN_RUN_QUEUE));
		
		this.add(scrollPane, BorderLayout.CENTER);
	}
	
	private void addListeners() {
		this.dataTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent event) {
				PLQueueRunBrowserPanel.this.handleMouseClick(event.getPoint());
			}
		});
		this.dataTree.getModel().addTreeModelListener(new TreeModelListener() {
			@Override
			public void treeNodesChanged(final TreeModelEvent event) {
				PLQueueRunBrowserPanel.this.handleTreeChanged(event);
			}
			
			@Override
			public void treeNodesInserted(final TreeModelEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void treeNodesRemoved(final TreeModelEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void treeStructureChanged(final TreeModelEvent e) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	private void handleTreeChanged(final TreeModelEvent event) {
		if (this.adjusting)
			return;
		this.adjusting = true;
		final TreePath parent = event.getTreePath();
		final Object[] children = event.getChildren();
		final DefaultTreeModel model = (DefaultTreeModel) event.getSource();
		
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

	private void handleMouseClick(final Point clickP) {
		final TreePath path = this.dataTree.getPathForLocation(clickP.x,
				clickP.y);
		this.plPanel.deselectNotListener(this);
		if (path == null) {
			this.plPanel.updateSelectedParticleDetectionRun(null);
			this.plPanel.updateSelectedParticleLinkingRun(null);
			this.deselect();
			return;
		}
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		final String s = node.toString();
		final OmegaElement element = this.nodeMap.get(s);
		final Integer index = this.nodeIndexes.get(s);
		this.selectedIndex = index;
		if (element instanceof OmegaParticleDetectionRun) {
			this.plPanel
					.updateSelectedParticleDetectionRun((OmegaParticleDetectionRun) element);
			this.plPanel.setRemoveButtonEnabled(true);
		}
	}
	
	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
	}
	
	public void updateTree(
			final Map<Integer, Map<OmegaParticleDetectionRun, List<OmegaParameter>>> particlesToProcess) {
		this.dataTree.setRootVisible(true);
		String s = null;
		this.selectedIndex = null;
		final CheckBoxStatus status = CheckBoxStatus.DESELECTED;
		this.root.removeAllChildren();
		((DefaultTreeModel) this.dataTree.getModel()).reload();
		this.nodeMap.clear();
		this.nodeIndexes.clear();
		if (particlesToProcess != null) {
			for (final Integer index : particlesToProcess.keySet()) {
				for (final OmegaParticleDetectionRun particleDetectionRun : particlesToProcess
						.get(index).keySet()) {
					final DefaultMutableTreeNode imageNode = new DefaultMutableTreeNode();
					s = index + ": [" + particleDetectionRun.getElementID()
							+ "] " + particleDetectionRun.getName();
					this.nodeMap.put(s, particleDetectionRun);
					this.nodeIndexes.put(s, index);
					// status = this.loadedData.containsImage(image) ?
					// CheckBoxStatus.SELECTED
					// : CheckBoxStatus.DESELECTED;
					imageNode.setUserObject(new CheckBoxNode(s, status));
					this.root.add(imageNode);
				}
			}
		}
		this.dataTree.expandRow(0);
		this.dataTree.setRootVisible(false);
		this.dataTree.repaint();
	}
	
	public void deselect() {
		this.selectedIndex = null;
		this.dataTree.setSelectionRow(-1);
	}

	public Integer getSelectedIndex() {
		return this.selectedIndex;
	}
}
