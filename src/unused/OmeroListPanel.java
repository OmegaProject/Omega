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
package unused;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import edu.umassmed.omega.omeroPlugin.gui.OmeroPluginPanel;
import edu.umassmed.omega.omeroPlugin.runnable.OmeroListPanelProjectAndDatasetLoader;

public class OmeroListPanel extends GenericPanel {

	private static final long serialVersionUID = -5868897435063007049L;
	private final OmeroGateway gateway;
	private final OmeroPluginPanel pluginPanel;
	private JTree tree;
	private OmeroTreeModel treeModel;
	private OmeroTreeData omeData;
	private OmeroDatasetWrapper actualSelection;

	public OmeroListPanel(final RootPaneContainer parentContainer,
	        final OmeroPluginPanel pluginPanel, final OmeroGateway gateway) {
		super(parentContainer);
		this.gateway = gateway;
		this.omeData = new OmeroTreeData(new ArrayList<ExperimenterData>());
		this.pluginPanel = pluginPanel;
		// this.setPreferredSize(new Dimension(300, 200));
		this.setLayout(new BorderLayout());
		this.createAndAddWidgets();
		this.addListeners();
	}

	public void createAndAddWidgets() {
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
					OmeroListPanel.this.pluginPanel.browseDataset(omeDataset);
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
		this.pluginPanel.updateStatus("Loading projects and datasets");
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
		this.pluginPanel.updateStatus(loadingStatus);
	}
}
