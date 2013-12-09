package edu.umassmed.omega.omero.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.jfree.ui.Align;

import edu.umassmed.omega.commons.OmegaConstants;
import edu.umassmed.omega.commons.OmegaDataEncryption;
import edu.umassmed.omega.commons.OmegaEvents;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.omero.OmeroGateway;
import edu.umassmed.omega.omero.OmeroLoginCredentials;
import edu.umassmed.omega.omero.OmeroServerInformation;

public class OmeroConnectionDialog extends JDialog {

	public String OPTION_SERVER_ADRESS = "Omero server adress";
	public String OPTION_SERVER_PORT = "Omero server port";
	public String OPTION_LOGIN_USERNAME = "Omero login username";
	public String OPTION_LOGIN_PASSWORD = "Omero login password";

	private Map<String, String> pluginOptions;

	private final JComponent parent;

	private static final long serialVersionUID = -1021787512167305062L;

	private JTextField usernameTxtFie, hostnameTxtFie, portTxtFie;
	private JPasswordField passwordPswFie;

	private JCheckBox saveServerInfo, saveLoginInfo;

	private JLabel connectionStatusLbl;

	private JButton connectButt;

	private final OmeroGateway gateway;

	public OmeroConnectionDialog(final JComponent parent,
	        final OmeroGateway gateway) {
		this.parent = parent;
		this.gateway = gateway;

		if (parent instanceof GenericPluginPanel) {
			this.pluginOptions = ((GenericPluginPanel) parent).getPlugin()
			        .getPluginOptions();
		} else {
			this.pluginOptions = new LinkedHashMap<String, String>();
		}

		this.createAndAddWidgets();

		this.addListeners();
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.pack();
	}

	private void createAndAddWidgets() {
		this.setLayout(new BorderLayout());

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 0));

		final JPanel serverPanel = this.createServerPanel();
		mainPanel.add(serverPanel);

		final JPanel loginPanel = this.createLoginPanel();
		mainPanel.add(loginPanel);

		this.add(mainPanel, BorderLayout.CENTER);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		this.connectButt = new JButton("Connect");
		this.connectButt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.connectButt);

		bottomPanel.add(buttonPanel, BorderLayout.CENTER);

		this.connectionStatusLbl = new JLabel("Status: not connected.");
		this.connectionStatusLbl.setHorizontalAlignment(Align.CENTER);
		bottomPanel.add(this.connectionStatusLbl, BorderLayout.SOUTH);

		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private JPanel createServerPanel() {
		final JPanel serverPanel = new JPanel();
		serverPanel.setLayout(new GridLayout(3, 0));

		serverPanel.setBorder(new TitledBorder("Server information"));

		final JLabel hostnameLbl = new JLabel("Insert server hostname:");
		serverPanel.add(hostnameLbl);

		this.hostnameTxtFie = new JTextField();
		this.hostnameTxtFie.setPreferredSize(OmegaConstants.TEXT_SIZE);
		serverPanel.add(this.hostnameTxtFie);
		if (this.pluginOptions.containsKey(this.OPTION_SERVER_ADRESS)) {
			this.hostnameTxtFie.setText(this.pluginOptions
			        .get(this.OPTION_SERVER_ADRESS));
		}

		final JLabel portLbl = new JLabel(
		        "Insert server port (empty for default "
		                + OmeroServerInformation.DEFAULT_PORT + "):");
		serverPanel.add(portLbl);

		this.portTxtFie = new JTextField();
		this.portTxtFie.setPreferredSize(OmegaConstants.TEXT_SIZE);
		serverPanel.add(this.portTxtFie);
		if (this.pluginOptions.containsKey(this.OPTION_SERVER_PORT)) {
			this.portTxtFie.setText(this.pluginOptions
			        .get(this.OPTION_SERVER_PORT));
		}

		this.saveServerInfo = new JCheckBox("Remember server information?");
		serverPanel.add(this.saveServerInfo);

		return serverPanel;
	}

	private JPanel createLoginPanel() {
		final JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new GridLayout(3, 0));

		loginPanel.setBorder(new TitledBorder("Login information"));

		final JLabel usernameLbl = new JLabel("Insert your username:");
		loginPanel.add(usernameLbl);

		this.usernameTxtFie = new JTextField();
		this.usernameTxtFie.setPreferredSize(OmegaConstants.TEXT_SIZE);
		loginPanel.add(this.usernameTxtFie);
		if (this.pluginOptions.containsKey(this.OPTION_LOGIN_USERNAME)) {
			this.usernameTxtFie.setText(this.pluginOptions
			        .get(this.OPTION_LOGIN_USERNAME));
		}

		final JLabel passwordLbl = new JLabel("Insert your password:");
		loginPanel.add(passwordLbl);

		this.passwordPswFie = new JPasswordField();
		loginPanel.add(this.passwordPswFie);
		this.passwordPswFie.setPreferredSize(OmegaConstants.TEXT_SIZE);
		if (this.pluginOptions.containsKey(this.OPTION_LOGIN_PASSWORD)) {
			final String psw = this.pluginOptions
			        .get(this.OPTION_LOGIN_PASSWORD);
			try {
				this.passwordPswFie.setText(OmegaDataEncryption.decrypt(psw));
			} catch (final GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.saveLoginInfo = new JCheckBox("Remember login information?");
		loginPanel.add(this.saveLoginInfo);

		return loginPanel;
	}

	private void addListeners() {
		this.connectButt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				if (!OmeroConnectionDialog.this.gateway.isConnected()) {
					final String hostname = OmeroConnectionDialog.this.hostnameTxtFie
					        .getText();
					final String portS = OmeroConnectionDialog.this.portTxtFie
					        .getText();

					OmeroServerInformation serverInfo = null;
					if (portS.length() == 0) {
						serverInfo = new OmeroServerInformation(hostname);
					} else {
						final Integer port = Integer.valueOf(portS);
						serverInfo = new OmeroServerInformation(hostname, port);
					}
					final String username = OmeroConnectionDialog.this.usernameTxtFie
					        .getText();
					final String password = String
					        .valueOf(OmeroConnectionDialog.this.passwordPswFie
					                .getPassword());
					final OmeroLoginCredentials loginCred = new OmeroLoginCredentials(
					        username, password);

					OmeroConnectionDialog.this.connectionStatusLbl
					        .setText("Status:  connecting...");

					boolean connected = false;
					try {
						connected = OmeroConnectionDialog.this.gateway.connect(
						        loginCred, serverInfo);
					} catch (final Exception ext) {
						OmeroConnectionDialog.this.connectionStatusLbl
						        .setText(ext.getMessage());
					}

					if (connected == false) {
						OmeroConnectionDialog.this.connectionStatusLbl
						        .setText("Status: not connected.");
						OmeroConnectionDialog.this.parent.firePropertyChange(
						        OmegaEvents.PROPERTY_CONNECTION, 0, 1);
					} else {
						OmeroConnectionDialog.this.usernameTxtFie
						        .setEditable(false);
						OmeroConnectionDialog.this.passwordPswFie
						        .setEditable(false);
						OmeroConnectionDialog.this.hostnameTxtFie
						        .setEditable(false);
						OmeroConnectionDialog.this.portTxtFie
						        .setEditable(false);
						OmeroConnectionDialog.this.connectButt
						        .setText("Disconnect");
						OmeroConnectionDialog.this.connectionStatusLbl
						        .setText("Status: connected.");
						OmeroConnectionDialog.this.parent.firePropertyChange(
						        OmegaEvents.PROPERTY_CONNECTION, 0, 1);
					}

					OmeroConnectionDialog.this.saveOptions(hostname, portS,
					        username, password);
				} else {
					OmeroConnectionDialog.this.gateway.disconnect();
					OmeroConnectionDialog.this.usernameTxtFie.setEditable(true);
					OmeroConnectionDialog.this.passwordPswFie.setEditable(true);
					OmeroConnectionDialog.this.hostnameTxtFie.setEditable(true);
					OmeroConnectionDialog.this.portTxtFie.setEditable(true);
					OmeroConnectionDialog.this.connectButt.setText("Connect");
					OmeroConnectionDialog.this.parent.firePropertyChange(
					        OmegaEvents.PROPERTY_CONNECTION, 0, 1);

				}
			}
		});
	}

	public void saveOptions(final String hostname, final String port,
	        final String username, final String password) {
		OmeroPluginPanel pluginPanel = null;
		if (OmeroConnectionDialog.this.parent instanceof OmeroPluginPanel) {
			pluginPanel = (OmeroPluginPanel) OmeroConnectionDialog.this.parent;
		}

		if (pluginPanel != null) {
			final Map<String, String> options = new LinkedHashMap<String, String>();
			if (OmeroConnectionDialog.this.saveServerInfo.isSelected()) {
				options.put(OmeroConnectionDialog.this.OPTION_SERVER_ADRESS,
				        hostname);
				if (port.isEmpty()) {
					options.put(OmeroConnectionDialog.this.OPTION_SERVER_PORT,
					        OmeroConnectionDialog.this.portTxtFie.getText());
				}
			}
			if (OmeroConnectionDialog.this.saveLoginInfo.isSelected()) {
				options.put(this.OPTION_LOGIN_USERNAME, username);
				String loginPsw = null;
				try {
					loginPsw = OmegaDataEncryption.encrypt(password);
				} catch (final UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (final GeneralSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (loginPsw != null) {
					options.put(this.OPTION_LOGIN_PASSWORD, loginPsw);
				}
			}
			pluginPanel.getPlugin().addPluginOptions(options);
		}
	}
}
