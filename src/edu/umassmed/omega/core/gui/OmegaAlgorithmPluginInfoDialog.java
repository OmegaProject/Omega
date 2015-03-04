package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.WindowConstants;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.gui.dialogs.GenericDialog;
import edu.umassmed.omega.commons.plugins.OmegaAlgorithmPlugin;

public class OmegaAlgorithmPluginInfoDialog extends GenericDialog {

	private static final long serialVersionUID = -678221971763691869L;
	private final OmegaAlgorithmPlugin plugin;
	private JPanel topIconPanel, topInfoPanel, mainPanel;
	private JButton close_btt;

	public OmegaAlgorithmPluginInfoDialog(
	        final RootPaneContainer parentContainer,
	        final OmegaAlgorithmPlugin plugin) {
		super(parentContainer, "Algorithm Information", false);

		this.plugin = plugin;

		this.createPluginInformation();

		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.revalidate();
		this.repaint();
		this.pack();
	}

	@Override
	protected void createAndAddWidgets() {
		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());

		this.topIconPanel = new JPanel();
		this.topIconPanel.setLayout(new FlowLayout());
		topPanel.add(this.topIconPanel, BorderLayout.WEST);

		this.topInfoPanel = new JPanel();
		this.topInfoPanel.setLayout(new GridLayout(4, 1));
		topPanel.add(this.topInfoPanel, BorderLayout.CENTER);

		this.add(topPanel, BorderLayout.NORTH);

		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(this.mainPanel, BorderLayout.CENTER);

		final JPanel buttPanel = new JPanel();
		buttPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		this.close_btt = new JButton("Close");
		buttPanel.add(this.close_btt);

		this.add(buttPanel, BorderLayout.SOUTH);
	}

	private void createPluginInformation() {
		if (this.plugin.getIcon() != null) {
			final JLabel icon_lbl = new JLabel(this.plugin.getIcon());
			this.topIconPanel.add(icon_lbl);
		}

		final JLabel name_lbl = new JLabel("Name: "
		        + this.plugin.getAlgorithmName());
		final JLabel auth_lbl = new JLabel("Author: "
		        + this.plugin.getAlgorithmAuthor().getFirstName() + " "
		        + this.plugin.getAlgorithmAuthor().getLastName());
		final DateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT_LBL);
		final JLabel date_lbl = new JLabel("Published: "
		        + format.format(this.plugin.getAlgorithmPublicationDate()));
		final JLabel version_lbl = new JLabel("Version: "
		        + this.plugin.getAlgorithmVersion().toString());
		this.plugin.getAlgorithmDescription();
		this.topInfoPanel.add(name_lbl);
		this.topInfoPanel.add(auth_lbl);
		this.topInfoPanel.add(date_lbl);
		this.topInfoPanel.add(version_lbl);
		final JLabel desc_lbl = new JLabel(
		        this.plugin.getAlgorithmDescription());
		this.mainPanel.add(desc_lbl);
	}

	@Override
	protected void addListeners() {
		this.close_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaAlgorithmPluginInfoDialog.this.setVisible(false);
			}
		});
	}
}
