package edu.umassmed.omega.core.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.commons.gui.GenericPanel;

public class OmegaTopPanel extends GenericPanel {
	private static final long serialVersionUID = -2511349408103225400L;

	private final Map<Long, JButton> buttons;

	public OmegaTopPanel(final JFrame parent) {
		super(parent);

		this.buttons = new LinkedHashMap<Long, JButton>();
	}

	protected void initializePanel(final Map<Long, OmegaPlugin> registeredPlugin) {
		this.createAndAddWidgets(registeredPlugin);

		this.addListeners();
	}

	private void createAndAddWidgets(
	        final Map<Long, OmegaPlugin> registeredPlugin) {
		this.setLayout(new FlowLayout());

		for (final Long id : registeredPlugin.keySet()) {
			final OmegaPlugin plugin = registeredPlugin.get(id);

			final JButton butt = new JButton(plugin.getShortName());

			butt.setPreferredSize(new Dimension(120, 120));
			this.buttons.put(id, butt);
			this.add(butt);
		}
	}

	private void addListeners() {
		for (final Long id : this.buttons.keySet()) {
			final JButton butt = this.buttons.get(id);
			butt.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt) {
					if (OmegaTopPanel.this.getParentContainer() instanceof JFrame) {
						final JFrame frame = (JFrame) OmegaTopPanel.this
						        .getParentContainer();
						frame.firePropertyChange(OmegaGUIFrame.PROP_PLUGIN, -1, id);
					} else {
						final JInternalFrame intFrame = (JInternalFrame) OmegaTopPanel.this
						        .getParentContainer();
						intFrame.firePropertyChange(OmegaGUIFrame.PROP_PLUGIN, -1,
						        id);
					}
				}
			});
		}
	}
}
