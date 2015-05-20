package edu.umassmed.omega.commons.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.gui.dialogs.GenericDialog;
import edu.umassmed.omega.commons.utilities.OmegaStringUtilities;
import edu.umassmed.omega.data.analysisRunElements.OmegaAlgorithmInformation;

public class GenericAlgorithmDetailsDialog extends GenericDialog {

	private static final long serialVersionUID = -678221971763691869L;
	private JPanel topIconPanel, topInfoPanel, mainPanel;
	private JButton close_btt;

	private JLabel name_lbl, auth_lbl, date_lbl, version_lbl, desc_lbl;

	public GenericAlgorithmDetailsDialog(final RootPaneContainer parentContainer) {
		super(parentContainer, OmegaGUIConstants.ALGORITHM_INFORMATION, false);

		final Dimension dialogDim = new Dimension(400, 220);
		this.setPreferredSize(dialogDim);
		this.setSize(dialogDim);

		this.createPluginInformation();

		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.revalidate();
		this.repaint();
		// this.pack();
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

		this.close_btt = new JButton(OmegaGUIConstants.MENU_FILE_CLOSE);
		this.close_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttPanel.add(this.close_btt);

		this.add(buttPanel, BorderLayout.SOUTH);
	}

	private void createPluginInformation() {
		// if (this.plugin.getIcon() != null) {
		// final JLabel icon_lbl = new JLabel(this.plugin.getIcon());
		// this.topIconPanel.add(icon_lbl);
		// }

		this.name_lbl = new JLabel(OmegaGUIConstants.SIDEPANEL_INFO_NAME);
		this.auth_lbl = new JLabel(OmegaGUIConstants.AUTHOR);
		this.date_lbl = new JLabel(OmegaGUIConstants.RELEASED);
		this.version_lbl = new JLabel();
		// final JLabel reference_lbl = new JLabel(OmegaGUIConstants.REFERENCE
		// + this.plugin.getAlgorithmReference());
		this.topInfoPanel.add(this.name_lbl);
		this.topInfoPanel.add(this.auth_lbl);
		this.topInfoPanel.add(this.date_lbl);
		this.topInfoPanel.add(this.version_lbl);
		this.desc_lbl = new JLabel();
		final Dimension dim = new Dimension(this.getWidth() - 10,
				(this.getHeight() - 30) / 2);
		this.desc_lbl.setPreferredSize(dim);
		this.desc_lbl.setSize(dim);
		this.mainPanel.add(this.desc_lbl);
	}

	@Override
	protected void addListeners() {
		this.close_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GenericAlgorithmDetailsDialog.this.setVisible(false);
			}
		});
	}

	public void updateAlgorithmInformation(
	        final OmegaAlgorithmInformation algoInfo) {
		if (algoInfo != null) {
			this.name_lbl.setText(OmegaGUIConstants.SIDEPANEL_INFO_NAME
					+ algoInfo.getName());
			this.auth_lbl.setText(OmegaGUIConstants.AUTHOR
					+ algoInfo.getAuthor().getFirstName() + " "
					+ algoInfo.getAuthor().getLastName());
			final DateFormat format = new SimpleDateFormat(
					OmegaConstants.OMEGA_DATE_FORMAT_LBL);
			this.date_lbl.setText(OmegaGUIConstants.RELEASED
					+ format.format(algoInfo.getPublicationData()));
			this.version_lbl.setText(OmegaGUIConstants.VERSION + " "
					+ algoInfo.getVersion());
			final String s = algoInfo.getDescription();
			final String desc = OmegaStringUtilities.getHtmlString(s, " ",
					SwingConstants.LEADING);
			this.desc_lbl.setText(desc);
		} else {
			this.name_lbl.setText(OmegaGUIConstants.SIDEPANEL_INFO_NAME);
			this.auth_lbl.setText(OmegaGUIConstants.AUTHOR);
			this.date_lbl.setText(OmegaGUIConstants.RELEASED);
			this.version_lbl.setText(OmegaGUIConstants.VERSION);
			this.desc_lbl.setText("");
		}
	}
}
