package edu.umassmed.omega.omeroPlugin.gui;

import java.util.EventListener;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import edu.umassmed.omega.omeroPlugin.data.OmeroDatasetWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroExperimenterWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroProjectWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroTreeData;

public class OmeroTreeModel implements TreeModel {

	protected EventListenerList listeners;
	private OmeroTreeData data;

	public OmeroTreeModel(final OmeroTreeData data) {
		this.listeners = new EventListenerList();
		this.data = data;
	}

	public void updateData(final OmeroTreeData data) {
		final OmeroTreeData oldRoot = this.data;
		this.data = data;
		this.fireTreeStructureChanged(oldRoot);
	}

	@Override
	public Object getRoot() {
		return this.data;
	}

	@Override
	public Object getChild(final Object parent, final int index) {
		if (parent instanceof OmeroTreeData)
			return this.data.getExperimenters().get(index);
		else if (parent instanceof OmeroExperimenterWrapper) {
			final OmeroExperimenterWrapper omeExp = (OmeroExperimenterWrapper) parent;
			return omeExp.getProjects().get(index);
		} else if (parent instanceof OmeroProjectWrapper) {
			final OmeroProjectWrapper omeProj = (OmeroProjectWrapper) parent;
			return omeProj.getDatasets().get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(final Object parent) {
		if (parent instanceof OmeroTreeData)
			return this.data.getExperimenters().size();
		else if (parent instanceof OmeroExperimenterWrapper) {
			final OmeroExperimenterWrapper omeExp = (OmeroExperimenterWrapper) parent;
			return omeExp.getProjects().size();
		} else if (parent instanceof OmeroProjectWrapper) {
			final OmeroProjectWrapper omeProj = (OmeroProjectWrapper) parent;
			return omeProj.getDatasets().size();
		}
		return 0;
	}

	@Override
	public boolean isLeaf(final Object node) {
		if (node instanceof OmeroDatasetWrapper)
			return true;
		return false;
	}

	@Override
	public void valueForPathChanged(final TreePath path, final Object newValue) {
		// TODO
	}

	@Override
	public int getIndexOfChild(final Object parent, final Object child) {
		if (parent instanceof OmeroTreeData)
			return this.data.getExperimenters().indexOf(child);
		else if (parent instanceof OmeroExperimenterWrapper) {
			final OmeroExperimenterWrapper omeExp = (OmeroExperimenterWrapper) parent;
			return omeExp.getProjects().indexOf(child);
		} else if (parent instanceof OmeroProjectWrapper) {
			final OmeroProjectWrapper omeProj = (OmeroProjectWrapper) parent;
			return omeProj.getDatasets().indexOf(child);
		}
		return -1;
	}

	@Override
	public void addTreeModelListener(final TreeModelListener listener) {
		this.listeners.add(TreeModelListener.class, listener);
	}

	@Override
	public void removeTreeModelListener(final TreeModelListener listener) {
		this.listeners.remove(TreeModelListener.class, listener);
	}

	protected void fireTreeStructureChanged(final OmeroTreeData oldRoot) {
		final TreeModelEvent event = new TreeModelEvent(this,
		        new Object[] { oldRoot });
		final EventListener[] listeners = this.listeners
		        .getListeners(TreeModelListener.class);
		for (final EventListener listener : listeners) {
			((TreeModelListener) listener).treeStructureChanged(event);
		}
	}
}
