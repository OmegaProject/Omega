package edu.umassmed.omega.commons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.umassmed.omega.commons.eventSystem.OmegaBrowserPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaBrowserPluginListener;

public abstract class OmegaBrowserPlugin extends OmegaPlugin {
	private final List<OmegaBrowserPluginListener> listeners = new ArrayList<OmegaBrowserPluginListener>();

	// private final OmegaGateway gateway;
	// TODO Loaded data

	public OmegaBrowserPlugin() {
		super(1);
	}

	public synchronized void addOmegaBrowserPluginListener(
	        final OmegaBrowserPluginListener listener) {
		this.listeners.add(listener);
	}

	public synchronized void removeOmegaBrowserPluginEventListener(
	        final OmegaBrowserPluginListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public synchronized void fireEvent() {
		final OmegaBrowserPluginEvent event = new OmegaBrowserPluginEvent(null,
		        null);
		final Iterator<OmegaBrowserPluginListener> i = this.listeners
		        .iterator();
		while (i.hasNext()) {
			i.next().handleOmegaBrowserPluginEvent(event);
		}
	}

	public synchronized void fireEvent(final OmegaBrowserPluginEvent event) {
		final Iterator<OmegaBrowserPluginListener> i = this.listeners
		        .iterator();
		while (i.hasNext()) {
			i.next().handleOmegaBrowserPluginEvent(event);
		}
	}

	// TODO getLoadedData
	public abstract void fireUpdate();
}
