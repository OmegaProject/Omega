package edu.umassmed.omega.sptPlugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.OmegaParticleTrackingPlugin;
import edu.umassmed.omega.commons.exceptions.OmegaMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.dataNew.coreElements.OmegaPerson;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.sptPlugin.gui.SPTPluginPanel;

public class SPTPlugin extends OmegaParticleTrackingPlugin implements
        OmegaDataDisplayerPluginInterface {

	private final List<SPTPluginPanel> panels;

	public SPTPlugin() {
		super();

		this.panels = new ArrayList<SPTPluginPanel>();
	}

	@Override
	public String getAlgorithmDescription() {
		return "Algorithm desc";
	}

	@Override
	public String getAlgorithmName() {
		return "Single particle tracking by Ivo Sbalzarini";
	}

	@Override
	public OmegaPerson getAlgorithmAuthor() {
		return new OmegaPerson(UUID.randomUUID().getMostSignificantBits(),
		        "Ivo", "Sbalzarini");
	}

	@Override
	public Double getAlgorithmVersion() {
		return 1.0;
	}

	@Override
	public Date getAlgorithmPublicationDate() {
		return new GregorianCalendar(1996, 4, 7).getTime();
	}

	@Override
	public String getName() {
		return "Single Particle Tracking";
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) throws OmegaMissingData {
		final SPTPluginPanel panel = new SPTPluginPanel(parent, this,
		        this.getGateway(), this.getLoadedImages(), index);
		this.panels.add(panel);
		return panel;
	}

	@Override
	public void setGateway(final OmegaGateway gateway) {
		super.setGateway(gateway);
		for (final SPTPluginPanel panel : this.panels) {
			panel.setGateway(gateway);
		}
	}

	@Override
	public void updateDisplayedData() {
		for (final SPTPluginPanel panel : this.panels) {
			panel.update(this.getLoadedImages());
		}
	}

}
