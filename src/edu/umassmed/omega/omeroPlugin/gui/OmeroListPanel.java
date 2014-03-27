package edu.umassmed.omega.omeroPlugin.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.RootPaneContainer;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import omero.ServerError;

import org.apache.log4j.lf5.viewer.categoryexplorer.TreeModelAdapter;

import pojos.DatasetData;
import pojos.ExperimenterData;
import pojos.ProjectData;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.omeroPlugin.OmeroGateway;
import edu.umassmed.omega.omeroPlugin.data.OmeroDatasetWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroTreeData;

public class OmeroListPanel extends GenericPanel {

	private static final long serialVersionUID = -5868897435063007049L;
	private final OmeroGateway gateway;
	private final OmeroBrowserPanel browsePanel;
	private JLabel loadingStatus;
	private JTree tree;
	private OmeroTreeModel treeModel;
	private OmeroTreeData omeData;
	private OmeroDatasetWrapper actualSelection;

	public OmeroListPanel(final RootPaneContainer parentContainer,
	        final OmeroBrowserPanel browsePanel, final OmeroGateway gateway) {
		super(parentContainer);
		this.gateway = gateway;
		this.omeData = new OmeroTreeData(new ArrayList<ExperimenterData>());
		this.browsePanel = browsePanel;
		// this.setPreferredSize(new Dimension(300, 200));
		this.setLayout(new BorderLayout());
		this.createAndAddWidgets();
		this.addListeners();
	}

	public void createAndAddWidgets() {
		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		this.loadingStatus = new JLabel("Nothing to load");
		topPanel.add(this.loadingStatus);

		this.add(topPanel, BorderLayout.NORTH);

		this.treeModel = new OmeroTreeModel(this.omeData);
		this.tree = new JTree(this.treeModel);
		this.add(this.tree, BorderLayout.CENTER);
	}

	private void addListeners() {
		this.treeModel.addTreeModelListener(new TreeModelAdapter() {
			@Override
			public void treeStructureChanged(final TreeModelEvent e) {
				OmeroListPanel.this.repaint();
				OmeroListPanel.this.validate();

			}
		});

		this.tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(final TreeSelectionEvent evt) {
				final int[] selectedRows = OmeroListPanel.this.tree
				        .getSelectionRows();
				if (selectedRows.length == 0)
					return;
				final int selection = selectedRows[0];
				if (selectedRows.length > 1) {
					OmeroListPanel.this.tree.setSelectionRow(selection);
				}
				if (!(OmeroListPanel.this.tree.getSelectionPath()
				        .getLastPathComponent() instanceof OmeroDatasetWrapper))
					return;
				final OmeroDatasetWrapper omeDataset = (OmeroDatasetWrapper) OmeroListPanel.this.tree
				        .getSelectionPath().getLastPathComponent();

				if (OmeroListPanel.this.actualSelection != omeDataset) {
					OmeroListPanel.this.browsePanel.browseDataset(omeDataset);
				}
			}
		});
	}

	public void resetExperimenterData() {
		final OmeroTreeModel model = (OmeroTreeModel) this.tree.getModel();
		this.omeData = new OmeroTreeData(new ArrayList<ExperimenterData>());
		model.updateData(this.omeData);
	}

	public void addExperimenterData(final ExperimenterData experimenterData)
	        throws ServerError {
		final String loadingStatus = "Loading projects and datasets";
		this.loadingStatus.setText(loadingStatus);
		final OmeroListPanelProjectAndDatasetLoader loader = new OmeroListPanelProjectAndDatasetLoader(
		        this, this.gateway, experimenterData);
		final Thread t = new Thread(loader);
		t.start();
	}

	public void updateOmeData(final ExperimenterData expData,
	        final Map<ProjectData, List<DatasetData>> datas) {
		final OmeroTreeModel model = (OmeroTreeModel) OmeroListPanel.this.tree
		        .getModel();
		OmeroListPanel.this.omeData.addExperimenter(expData);
		OmeroListPanel.this.omeData.setProjects(expData,
		        new ArrayList(datas.keySet()));
		for (final ProjectData proj : datas.keySet()) {
			OmeroListPanel.this.omeData.setDatasets(expData, proj,
			        datas.get(proj));
		}
		model.updateData(OmeroListPanel.this.omeData);
	}

	public void removeExperimenterData(final ExperimenterData experimenterData) {
		final OmeroTreeModel model = (OmeroTreeModel) this.tree.getModel();
		this.omeData.removeExperimenter(experimenterData);
		model.updateData(this.omeData);
	}

	public void updateLoadingStatus(final String loadingStatus) {
		this.loadingStatus.setText(loadingStatus);
	}
}
