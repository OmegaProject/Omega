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
package edu.umassmed.omega.omegaDataBrowserPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNode;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNodeEditor;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNodeRenderer;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxStatus;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;

public class OmegaDataBrowserAnalysisBrowserPanel extends GenericPanel {

	private static final long serialVersionUID = 5212368402335926305L;

	private final OmegaDataBrowserPluginPanel browserPanel;

	private final Map<String, OmegaElement> nodeMap;
	private final DefaultMutableTreeNode root;
	private final List<OmegaAnalysisRun> loadedAnalysisRun;

	private final Class<? extends OmegaAnalysisRun> clazz;

	private JTree dataTree;

	public OmegaDataBrowserAnalysisBrowserPanel(final RootPaneContainer parent,
	        final OmegaDataBrowserPluginPanel browserPanel,
	        final Class<? extends OmegaAnalysisRun> clazz,
	        final OmegaAnalysisRunContainer selectedAnalysisContainer,
	        final List<OmegaAnalysisRun> loadedAnalysisRun) {
		super(parent);

		this.loadedAnalysisRun = loadedAnalysisRun;
		this.browserPanel = browserPanel;

		this.clazz = clazz;

		this.root = new DefaultMutableTreeNode();
		this.root.setUserObject("Loaded data");
		this.nodeMap = new HashMap<String, OmegaElement>();

		this.setPreferredSize(new Dimension(200, 500));
		this.setSize(new Dimension(200, 500));
		this.setLayout(new BorderLayout());
		this.createAndAddWidgets();

		this.updateTree(selectedAnalysisContainer);
	}

	private void createAndAddWidgets() {
		this.dataTree = new JTree(this.root);
		// this.dataTreeBrowser.setRootVisible(false);
		final CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
		this.dataTree.setCellRenderer(renderer);
		this.dataTree.setCellEditor(new CheckBoxNodeEditor());

		this.dataTree.expandRow(0);
		this.dataTree.setRootVisible(false);
		this.dataTree.setEditable(true);

		final JScrollPane scrollPane = new JScrollPane(this.dataTree);
		scrollPane.setBorder(new TitledBorder(this.clazz.getSimpleName()
		        + " data"));

		this.add(scrollPane, BorderLayout.CENTER);
	}

	public void updateTree(
	        final OmegaAnalysisRunContainer selectedAnalysisContainer) {
		this.dataTree.setRootVisible(true);

		String s = null;
		CheckBoxStatus status = null;
		this.root.removeAllChildren();
		((DefaultTreeModel) this.dataTree.getModel()).reload();
		this.nodeMap.clear();
		if (selectedAnalysisContainer != null) {
			for (final OmegaAnalysisRun analysisRun : selectedAnalysisContainer
			        .getAnalysisRuns()) {
				if (!this.clazz.isInstance(analysisRun)) {
					continue;
				}
				final DefaultMutableTreeNode node = new DefaultMutableTreeNode();
				s = "[" + analysisRun.getElementID() + "] "
				        + analysisRun.getName();
				this.nodeMap.put(s, analysisRun);
				status = this.loadedAnalysisRun.contains(analysisRun) ? CheckBoxStatus.SELECTED
				        : CheckBoxStatus.DESELECTED;
				node.setUserObject(new CheckBoxNode(s, status));
				this.root.add(node);
			}
		}

		// this.setPreferredSize(this.dataTree.getSize());
		this.dataTree.expandRow(0);
		this.dataTree.setRootVisible(false);
		this.dataTree.repaint();
	}
}
