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
package edu.umassmed.omega.mosaicOmegaFeaturePointTracker.gui;

import java.awt.BorderLayout;
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
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNode;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxStatus;

public class MosaicOmegaFeaturePointTrackerQueueRunBrowserPanel extends GenericPanel {
	
	private static final long serialVersionUID = -7554854467725521545L;
	
	private final MosaicOmegaFeaturePointTrackerPluginPanel sptPanel;
	
	private final Map<String, OmegaElement> nodeMap;
	private final DefaultMutableTreeNode root;
	
	private JTree dataTree;
	
	private boolean adjusting = false;
	
	public MosaicOmegaFeaturePointTrackerQueueRunBrowserPanel(final RootPaneContainer parentContainer,
			final MosaicOmegaFeaturePointTrackerPluginPanel sptPanel) {
		super(parentContainer);
		
		this.sptPanel = sptPanel;
		
		this.root = new DefaultMutableTreeNode();
		this.root.setUserObject(OmegaGUIConstants.PLUGIN_RUN_QUEUE);
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
		scrollPane.setBorder(new TitledBorder(
				OmegaGUIConstants.PLUGIN_RUN_QUEUE));
		
		this.add(scrollPane, BorderLayout.CENTER);
	}
	
	private void addListeners() {
		this.dataTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent event) {
				MosaicOmegaFeaturePointTrackerQueueRunBrowserPanel.this.handleMouseClick(event.getX(),
						event.getY());
			}
		});
		this.dataTree.getModel().addTreeModelListener(new TreeModelListener() {
			@Override
			public void treeNodesChanged(final TreeModelEvent event) {
				final TreePath parent = event.getTreePath();
				final Object[] children = event.getChildren();
				final DefaultTreeModel model = (DefaultTreeModel) event
						.getSource();
				MosaicOmegaFeaturePointTrackerQueueRunBrowserPanel.this.handleTreeNodeChanged(parent,
						children, model);
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
	
	private void handleMouseClick(final int x, final int y) {
		final TreePath path = MosaicOmegaFeaturePointTrackerQueueRunBrowserPanel.this.dataTree
				.getPathForLocation(x, y);
		if (path == null) {
			this.sptPanel.updateSelectedImage(null);
			this.deselect();
			return;
		}
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		final String s = node.toString();
		final OmegaElement element = this.nodeMap.get(s);
		if (element instanceof OmegaImage) {
			this.sptPanel.updateSelectedImage((OmegaImage) element);
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
	
	public void updateTree(
			final Map<OmegaImage, List<OmegaParameter>> imagesToProcess) {
		this.dataTree.setRootVisible(true);
		String s = null;
		final CheckBoxStatus status = CheckBoxStatus.DESELECTED;
		this.root.removeAllChildren();
		((DefaultTreeModel) this.dataTree.getModel()).reload();
		this.nodeMap.clear();
		if (imagesToProcess != null) {
			for (final OmegaImage image : imagesToProcess.keySet()) {
				final DefaultMutableTreeNode imageNode = new DefaultMutableTreeNode();
				s = "[" + image.getElementID() + "] " + image.getName();
				this.nodeMap.put(s, image);
				// status = this.loadedData.containsImage(image) ?
				// CheckBoxStatus.SELECTED
				// : CheckBoxStatus.DESELECTED;
				imageNode.setUserObject(new CheckBoxNode(s, status));
				this.root.add(imageNode);
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
