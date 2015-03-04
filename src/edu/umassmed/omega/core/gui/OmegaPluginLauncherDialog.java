package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RootPaneContainer;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.gui.dialogs.GenericDialog;
import edu.umassmed.omega.commons.plugins.OmegaAlgorithmPlugin;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.commons.utilities.OmegaStringUtilities;
import edu.umassmed.omega.core.OmegaAlgorithmPluginDetailsDialogManager;

public class OmegaPluginLauncherDialog extends GenericDialog {

	private static final long serialVersionUID = 1271725669283360422L;

	private static int DIALOG_WIDTH = 600;
	private JPanel mainPanel;
	private final Map<Long, ? extends OmegaPlugin> plugins;
	private final List<JButton> buttons;
	private final Map<Long, JButton> pluginLauncherbuttons, detailsButtons;

	private JButton close_btt;

	public OmegaPluginLauncherDialog(final RootPaneContainer parent,
	        final Map<Long, ? extends OmegaPlugin> plugins) {
		super(parent, "Plugin launcher", false);

		this.plugins = plugins;
		this.buttons = new ArrayList<>();
		this.pluginLauncherbuttons = new LinkedHashMap<>();
		this.detailsButtons = new LinkedHashMap<>();

		final Dimension dialogDim = new Dimension(
		        OmegaPluginLauncherDialog.DIALOG_WIDTH + 20, 440);
		this.setPreferredSize(dialogDim);
		this.setSize(dialogDim);
		final int requiredHeight = plugins.size() * 140;
		final Dimension panelDim = new Dimension(
		        OmegaPluginLauncherDialog.DIALOG_WIDTH, requiredHeight);
		this.mainPanel.setPreferredSize(panelDim);
		this.mainPanel.setSize(panelDim);

		this.populateMainPanel();

		this.addLauncherListeners();

		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.revalidate();
		this.repaint();
	}

	@Override
	protected void createAndAddWidgets() {
		this.setLayout(new BorderLayout());
		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new FlowLayout());
		final JScrollPane sp = new JScrollPane(this.mainPanel);
		this.add(sp, BorderLayout.CENTER);
		this.close_btt = new JButton("Close");
		final JPanel buttPanel = new JPanel();
		buttPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttPanel.add(this.close_btt);
		this.add(buttPanel, BorderLayout.SOUTH);
	}

	private void populateMainPanel() {
		final Dimension pluginDim = new Dimension(
		        OmegaPluginLauncherDialog.DIALOG_WIDTH, 140);
		final Dimension buttDim = new Dimension(120, 120);
		for (final Long id : this.plugins.keySet()) {
			final JPanel pluginPanel = new JPanel();
			pluginPanel.setLayout(new BorderLayout());
			pluginPanel.setPreferredSize(pluginDim);
			pluginPanel.setSize(pluginDim);
			final OmegaPlugin plugin = this.plugins.get(id);

			final JPanel launcherButtPanel = new JPanel();
			final FlowLayout fl = new FlowLayout();
			final int vGap = (pluginDim.height - buttDim.height) / 2;
			fl.setVgap(vGap);
			launcherButtPanel.setLayout(fl);
			launcherButtPanel.setPreferredSize(buttDim);
			launcherButtPanel.setSize(buttDim);
			final Icon icon = plugin.getIcon();
			final JButton butt = new JButton();
			this.buttons.add(butt);
			butt.setPreferredSize(buttDim);
			butt.setSize(buttDim);
			if (icon == null) {
				String name = plugin.getShortName();
				if (name == null) {
					name = plugin.getName();
				}
				butt.setText(name);
			} else {
				butt.setIcon(icon);
			}
			this.pluginLauncherbuttons.put(id, butt);
			launcherButtPanel.add(butt, BorderLayout.CENTER);
			pluginPanel.add(launcherButtPanel, BorderLayout.WEST);

			final JPanel descPanel = new JPanel();
			descPanel.setBorder(new TitledBorder(plugin.getName()));
			descPanel.setLayout(new BorderLayout());
			final JLabel plugDesc_lbl = new JLabel(plugin.getDescription());
			descPanel.add(plugDesc_lbl, BorderLayout.CENTER);

			if (plugin instanceof OmegaAlgorithmPlugin) {
				final JPanel buttPanel = new JPanel();
				buttPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
				final JButton detail_btt = new JButton("Algorithm details");
				detail_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
				detail_btt.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
				buttPanel.add(detail_btt);
				this.detailsButtons.put(id, detail_btt);
				descPanel.add(buttPanel, BorderLayout.SOUTH);
			}
			pluginPanel.add(descPanel, BorderLayout.CENTER);
			this.mainPanel.add(pluginPanel);
		}
	}

	@Override
	protected void addListeners() {
		this.close_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaPluginLauncherDialog.this.setVisible(false);
			}
		});
	}

	private void addLauncherListeners() {
		for (final Long id : this.pluginLauncherbuttons.keySet()) {
			final OmegaPlugin plugin = this.plugins.get(id);
			if (plugin instanceof OmegaAlgorithmPlugin) {
				final JButton pluginDetails_btt = this.detailsButtons.get(id);
				pluginDetails_btt.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						OmegaAlgorithmPluginDetailsDialogManager.showDialog(id,
						        OmegaPluginLauncherDialog.this
						                .getParentContainer());
					}
				});
			}
			final JButton pluginLauncher_btt = this.pluginLauncherbuttons
			        .get(id);
			pluginLauncher_btt.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt) {
					final RootPaneContainer parentContainer = OmegaPluginLauncherDialog.this
					        .getParentContainer();
					if (parentContainer instanceof JFrame) {
						final JFrame frame = (JFrame) parentContainer;
						frame.firePropertyChange(OmegaGUIFrame.PROP_PLUGIN, -1,
						        id);
					} else {
						final JInternalFrame intFrame = (JInternalFrame) parentContainer;
						intFrame.firePropertyChange(OmegaGUIFrame.PROP_PLUGIN,
						        -1, id);
					}
					OmegaPluginLauncherDialog.this.setVisible(false);
				}
			});
		}
	}

	public void reinitializeStrings() {
		for (final JButton butt : this.buttons) {
			final String s = butt.getText();
			final String name = OmegaStringUtilities.getHtmlString(s, " ");
			butt.setText(name);
		}

	}
}
