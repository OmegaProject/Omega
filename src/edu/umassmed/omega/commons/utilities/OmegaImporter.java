package edu.umassmed.omega.commons.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.umassmed.omega.commons.eventSystem.OmegaImporterEventListener;
import edu.umassmed.omega.commons.eventSystem.events.OmegaImporterEvent;

public class OmegaImporter {
	private final List<OmegaImporterEventListener> listeners = new ArrayList<OmegaImporterEventListener>();

	public synchronized void addOmegaImporterListener(
			final OmegaImporterEventListener listener) {
		this.listeners.add(listener);
	}

	public synchronized void removeOmegaImporterEventListener(
			final OmegaImporterEventListener listener) {
		this.listeners.remove(listener);
	}

	public synchronized void fireEvent() {
		final OmegaImporterEvent event = new OmegaImporterEvent(null);
		final Iterator<OmegaImporterEventListener> i = this.listeners
		        .iterator();
		while (i.hasNext()) {
			i.next().handleImporterEvent(event);
		}
	}

	public synchronized void fireEvent(final OmegaImporterEvent event) {
		final Iterator<OmegaImporterEventListener> i = this.listeners
		        .iterator();
		while (i.hasNext()) {
			i.next().handleImporterEvent(event);
		}
	}
}
