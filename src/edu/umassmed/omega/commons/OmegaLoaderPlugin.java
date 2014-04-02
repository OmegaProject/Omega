package edu.umassmed.omega.commons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.umassmed.omega.commons.eventSystem.OmegaLoaderPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaLoaderPluginListener;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;

public abstract class OmegaLoaderPlugin extends OmegaPlugin {
	private final List<OmegaLoaderPluginListener> listeners = new ArrayList<OmegaLoaderPluginListener>();

	private final OmegaGateway gateway;

	public OmegaLoaderPlugin(final OmegaGateway gateway) {
		super(1);

		this.gateway = gateway;
	}

	public synchronized void addOmegaLoaderPluginListener(
	        final OmegaLoaderPluginListener listener) {
		this.listeners.add(listener);
	}

	public synchronized void removeOmegaLoaderPluginEventListener(
	        final OmegaLoaderPluginListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public synchronized void fireEvent() {
		final OmegaLoaderPluginEvent event = new OmegaLoaderPluginEvent();
		final Iterator<OmegaLoaderPluginListener> i = this.listeners.iterator();
		while (i.hasNext()) {
			i.next().handleOmegaLoaderPluginEvent(event);
		}
	}

	public synchronized void fireEvent(final OmegaLoaderPluginEvent event) {
		final Iterator<OmegaLoaderPluginListener> i = this.listeners.iterator();
		while (i.hasNext()) {
			i.next().handleOmegaLoaderPluginEvent(event);
		}
	}

	public OmegaGateway getGateway() {
		return this.gateway;
	}
}
