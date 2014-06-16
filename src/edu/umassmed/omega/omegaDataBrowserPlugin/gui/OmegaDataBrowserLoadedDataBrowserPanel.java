package edu.umassmed.omega.omegaDataBrowserPlugin.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.lf5.viewer.categoryexplorer.TreeModelAdapter;

import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNode;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNodeEditor;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNodeRenderer;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxStatus;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.dataNew.OmegaLoadedData;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaProject;

public class OmegaDataBrowserLoadedDataBrowserPanel extends GenericPanel {

	private static final long serialVersionUID = -7554854467725521545L;

	private final OmegaDataBrowserPluginPanel browserPanel;

	private final Map<String, OmegaElement> nodeMap;
	private final DefaultMutableTreeNode root;
	private final OmegaLoadedData loadedData;

	private OmegaDataBrowserLoadedDataOptionsPanel optionsPanel;

	private JTree dataTree;

	private boolean adjusting = false;

	public OmegaDataBrowserLoadedDataBrowserPanel(

	final RootPaneContainer parentContainer,
	        final OmegaDataBrowserPluginPanel browserPanel,
	        final OmegaData data, final OmegaLoadedData loadedData) {
		super(parentContainer);

		this.loadedData = loadedData;
		this.browserPanel = browserPanel;

		this.root = new DefaultMutableTreeNode();
		this.root.setUserObject("Loaded data");
		this.nodeMap = new HashMap<String, OmegaElement>();

		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();
		this.addListeners();

		this.updateTree(data);
	}

	private void createAndAddWidgets() {
		this.optionsPanel = new OmegaDataBrowserLoadedDataOptionsPanel(
		        this.getParentContainer());

		this.add(this.optionsPanel, BorderLayout.NORTH);

		this.dataTree = new JTree(this.root);
		// this.dataTreeBrowser.setRootVisible(false);
		final CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
		this.dataTree.setCellRenderer(renderer);
		this.dataTree.setCellEditor(new CheckBoxNodeEditor());

		this.dataTree.expandRow(0);
		this.dataTree.setRootVisible(false);
		this.dataTree.setEditable(true);

		final JScrollPane scrollPane = new JScrollPane(this.dataTree);
		scrollPane.setBorder(new TitledBorder("Loaded data"));

		this.add(scrollPane, BorderLayout.CENTER);
	}

	private void addListeners() {
		this.dataTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent event) {
				final TreePath path = OmegaDataBrowserLoadedDataBrowserPanel.this.dataTree
				        .getPathForLocation(event.getX(), event.getY());
				if (path == null)
					return;
				final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				        .getLastPathComponent();
				final String s = node.toString();
				final OmegaElement element = OmegaDataBrowserLoadedDataBrowserPanel.this.nodeMap
				        .get(s);
				if (element instanceof OmegaAnalysisRunContainer) {
					OmegaDataBrowserLoadedDataBrowserPanel.this.browserPanel
					        .setSelectedAnalysisContainer((OmegaAnalysisRunContainer) element);
				}
			}
		});
		this.dataTree.getModel().addTreeModelListener(new TreeModelAdapter() {
			@Override
			public void treeNodesChanged(final TreeModelEvent event) {
				if (OmegaDataBrowserLoadedDataBrowserPanel.this.adjusting)
					return;
				OmegaDataBrowserLoadedDataBrowserPanel.this.adjusting = true;
				final TreePath parent = event.getTreePath();
				final Object[] children = event.getChildren();
				final DefaultTreeModel model = (DefaultTreeModel) event
				        .getSource();

				DefaultMutableTreeNode node;
				CheckBoxNode c; // = (CheckBoxNode)node.getUserObject();
				if ((children != null) && (children.length == 1)) {
					node = (DefaultMutableTreeNode) children[0];
					c = (CheckBoxNode) node.getUserObject();
					DefaultMutableTreeNode n = (DefaultMutableTreeNode) parent
					        .getLastPathComponent();
					if (OmegaDataBrowserLoadedDataBrowserPanel.this.optionsPanel
					        .isAutoSelectRelatives()) {
						while (n != null) {
							OmegaDataBrowserLoadedDataBrowserPanel.this
							        .updateParentUserObject(n);
							final DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) n
							        .getParent();
							if (tmp == null) {
								break;
							} else {
								n = tmp;
							}
						}
					}
					model.nodeChanged(n);
				} else {
					node = (DefaultMutableTreeNode) model.getRoot();
					c = (CheckBoxNode) node.getUserObject();
				}
				if (OmegaDataBrowserLoadedDataBrowserPanel.this.optionsPanel
				        .isAutoSelectRelatives()) {
					OmegaDataBrowserLoadedDataBrowserPanel.this
					        .updateAllChildrenUserObject(node, c.getStatus());
				}
				model.nodeChanged(node);

				OmegaDataBrowserLoadedDataBrowserPanel.this.adjusting = false;

				OmegaDataBrowserLoadedDataBrowserPanel.this.updateLoadedData(
				        node, c.getStatus());
			}
		});
	}

	private void updateParentUserObject(final DefaultMutableTreeNode parent) {
		if (parent.getUserObject() instanceof String)
			return;
		final String label = ((CheckBoxNode) parent.getUserObject()).getLabel();
		int selectedCount = 0;
		int indeterminateCount = 0;
		final Enumeration children = parent.children();
		while (children.hasMoreElements()) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) children
			        .nextElement();
			final CheckBoxNode check = (CheckBoxNode) node.getUserObject();
			if (check.getStatus() == CheckBoxStatus.INDETERMINATE) {
				indeterminateCount++;
				break;
			}
			if (check.getStatus() == CheckBoxStatus.SELECTED) {
				selectedCount++;
			}
		}
		if (indeterminateCount > 0) {
			parent.setUserObject(new CheckBoxNode(label));
		} else if (selectedCount == 0) {
			final CheckBoxStatus status = CheckBoxStatus.DESELECTED;
			parent.setUserObject(new CheckBoxNode(label, status));
			OmegaDataBrowserLoadedDataBrowserPanel.this.updateLoadedData(
			        parent, status);
		} else if (selectedCount == parent.getChildCount()) {
			final CheckBoxStatus status = CheckBoxStatus.SELECTED;
			parent.setUserObject(new CheckBoxNode(label, status));
			OmegaDataBrowserLoadedDataBrowserPanel.this.updateLoadedData(
			        parent, status);
		} else {
			parent.setUserObject(new CheckBoxNode(label));
		}
	}

	private void updateAllChildrenUserObject(final DefaultMutableTreeNode root,
	        final CheckBoxStatus status) {
		final Enumeration breadth = root.breadthFirstEnumeration();
		while (breadth.hasMoreElements()) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) breadth
			        .nextElement();
			if (root == node) {
				continue;
			}
			final CheckBoxNode check = (CheckBoxNode) node.getUserObject();
			node.setUserObject(new CheckBoxNode(check.getLabel(), status));

			OmegaDataBrowserLoadedDataBrowserPanel.this.updateLoadedData(node,
			        status);
		}
	}

	private void updateLoadedData(final DefaultMutableTreeNode node,
	        final CheckBoxStatus status) {
		final String s = node.toString();
		final OmegaElement element = OmegaDataBrowserLoadedDataBrowserPanel.this.nodeMap
		        .get(s);
		if (status == CheckBoxStatus.SELECTED) {
			OmegaDataBrowserLoadedDataBrowserPanel.this.loadedData
			        .addElement(element);
		} else if (status == CheckBoxStatus.DESELECTED) {
			OmegaDataBrowserLoadedDataBrowserPanel.this.loadedData
			        .removeElement(element);
		}
		this.browserPanel.fireDataChangedEvent();
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.optionsPanel.updateParentContainer(this.getParentContainer());
	}

	public void updateTree(final OmegaData data) {
		this.dataTree.setRootVisible(true);

		String s = null;
		CheckBoxStatus status = null;
		this.root.removeAllChildren();
		((DefaultTreeModel) this.dataTree.getModel()).reload();
		this.nodeMap.clear();
		for (final OmegaProject project : data.getProjects()) {
			final DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode();
			s = "[" + project.getElementID() + "] " + project.getName();
			this.nodeMap.put(s, project);
			status = this.loadedData.containsProject(project) ? CheckBoxStatus.SELECTED
			        : CheckBoxStatus.DESELECTED;
			projectNode.setUserObject(new CheckBoxNode(s, status));
			for (final OmegaDataset dataset : project.getDatasets()) {
				final DefaultMutableTreeNode datasetNode = new DefaultMutableTreeNode();

				s = "[" + dataset.getElementID() + "] " + dataset.getName();
				this.nodeMap.put(s, dataset);
				status = this.loadedData.containsDataset(dataset) ? CheckBoxStatus.SELECTED
				        : CheckBoxStatus.DESELECTED;
				datasetNode.setUserObject(new CheckBoxNode(s, status));
				for (final OmegaImage image : dataset.getImages()) {
					final DefaultMutableTreeNode imageNode = new DefaultMutableTreeNode();

					s = "[" + image.getElementID() + "] " + image.getName();
					this.nodeMap.put(s, image);
					status = this.loadedData.containsImage(image) ? CheckBoxStatus.SELECTED
					        : CheckBoxStatus.DESELECTED;
					imageNode.setUserObject(new CheckBoxNode(s, status));
					datasetNode.add(imageNode);
				}
				projectNode.add(datasetNode);
			}
			this.root.add(projectNode);
		}
		this.dataTree.expandRow(0);
		this.dataTree.setRootVisible(false);
		this.dataTree.repaint();
	}
}
