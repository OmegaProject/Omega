/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School Alessandro
 * Rigano (Program in Molecular Medicine) Caterina Strambio De Castillia
 * (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team:
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli,
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban,
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts: Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.omeroPlugin.gui;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaConstantsEvent;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaLoginCredentials;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaServerInformation;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.gui.dialogs.GenericDialog;
import edu.umassmed.omega.commons.utilities.OmegaDataEncryptionUtilities;
import edu.umassmed.omega.omero.commons.OmeroGateway;
import edu.umassmed.omega.omero.commons.OmeroImporterUtilities;
import edu.umassmed.omega.omero.commons.data.OmeroServerInformation;

public class OmeroConnectionDialog extends GenericDialog {

	public String OPTION_SERVER_ADRESS = "Omero server adress";
	public String OPTION_SERVER_PORT = "Omero server port";
	public String OPTION_LOGIN_USERNAME = "Omero login username";
	public String OPTION_LOGIN_PASSWORD = "Omero login password";

	private final GenericPluginPanel parent;
	private final Map<String, String> pluginOptions;

	private static final long serialVersionUID = -1021787512167305062L;

	private JPanel mainPanel;
	private JTextField usernameTxtFie, hostnameTxtFie, portTxtFie;
	private JPasswordField passwordPswFie;
	private JCheckBox saveServerInfo, saveLoginInfo;
	private JLabel connectionStatusLbl;
	private JButton connectButt;

	private final OmeroGateway gateway;

	public OmeroConnectionDialog(final RootPaneContainer parentContainer,
			final GenericPluginPanel parent, final OmeroGateway gateway) {
		super(parentContainer, "Omega server connection manager", false);
		this.gateway = gateway;

		this.parent = parent;
		this.pluginOptions = parent.getPlugin().getPluginOptions();

		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.fillFields();
	}

	@Override
	protected void createAndAddWidgets() {
		this.setLayout(new BorderLayout());
		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new GridLayout(2, 0));
		this.createAndAddServerPanel();
		this.createAndAddLoginPanel();
		this.add(this.mainPanel, BorderLayout.CENTER);
		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		this.connectButt = new JButton("Connect");
		this.connectButt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.connectButt.setSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.connectButt);
		bottomPanel.add(buttonPanel, BorderLayout.CENTER);
		this.connectionStatusLbl = new JLabel("Status: not connected.");
		this.connectionStatusLbl.setHorizontalAlignment(SwingConstants.CENTER);
		bottomPanel.add(this.connectionStatusLbl, BorderLayout.SOUTH);
		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void createAndAddServerPanel() {
		final JPanel serverPanel = new JPanel();
		serverPanel.setLayout(new GridLayout(3, 0));
		serverPanel.setBorder(new TitledBorder("Server information"));
		final JLabel hostnameLbl = new JLabel("Insert server hostname:");
		serverPanel.add(hostnameLbl);
		this.hostnameTxtFie = new JTextField();
		this.hostnameTxtFie.setPreferredSize(OmegaConstants.TEXT_SIZE);
		serverPanel.add(this.hostnameTxtFie);
		final JLabel portLbl = new JLabel(
				"Insert server port (empty for default "
						+ OmeroServerInformation.DEFAULT_PORT + "):");
		serverPanel.add(portLbl);
		this.portTxtFie = new JTextField();
		this.portTxtFie.setPreferredSize(OmegaConstants.TEXT_SIZE);
		serverPanel.add(this.portTxtFie);
		this.saveServerInfo = new JCheckBox("Remember server information?");
		serverPanel.add(this.saveServerInfo);
		this.mainPanel.add(serverPanel);
	}

	private void createAndAddLoginPanel() {
		final JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new GridLayout(3, 0));
		loginPanel.setBorder(new TitledBorder("Login information"));
		final JLabel usernameLbl = new JLabel("Insert your username:");
		loginPanel.add(usernameLbl);
		this.usernameTxtFie = new JTextField();
		this.usernameTxtFie.setPreferredSize(OmegaConstants.TEXT_SIZE);
		loginPanel.add(this.usernameTxtFie);
		final JLabel passwordLbl = new JLabel("Insert your password:");
		loginPanel.add(passwordLbl);
		this.passwordPswFie = new JPasswordField();
		loginPanel.add(this.passwordPswFie);
		this.passwordPswFie.setPreferredSize(OmegaConstants.TEXT_SIZE);
		this.saveLoginInfo = new JCheckBox("Remember login information?");
		loginPanel.add(this.saveLoginInfo);
		this.mainPanel.add(loginPanel);
	}

	private void fillFields() {
		if (this.pluginOptions.containsKey(this.OPTION_SERVER_ADRESS)) {
			this.hostnameTxtFie.setText(this.pluginOptions
					.get(this.OPTION_SERVER_ADRESS));
		}
		if (this.pluginOptions.containsKey(this.OPTION_SERVER_PORT)) {
			this.portTxtFie.setText(this.pluginOptions
					.get(this.OPTION_SERVER_PORT));
		}
		if (this.pluginOptions.containsKey(this.OPTION_LOGIN_USERNAME)) {
			this.usernameTxtFie.setText(this.pluginOptions
					.get(this.OPTION_LOGIN_USERNAME));
		}
		if (this.pluginOptions.containsKey(this.OPTION_LOGIN_PASSWORD)) {
			final String psw = this.pluginOptions
					.get(this.OPTION_LOGIN_PASSWORD);
			try {
				this.passwordPswFie.setText(OmegaDataEncryptionUtilities
						.decrypt(psw));
			} catch (final GeneralSecurityException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void addListeners() {
		this.connectButt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmeroConnectionDialog.this.handleConnect();
			}
		});
	}
	
	private void handleConnect() {
		if (!OmeroConnectionDialog.this.gateway.isConnected()) {
			final String hostname = OmeroConnectionDialog.this.hostnameTxtFie
					.getText();
			final String portS = OmeroConnectionDialog.this.portTxtFie
					.getText();

			OmegaServerInformation serverInfo = null;
			if (portS.length() == 0) {
				serverInfo = new OmeroServerInformation(hostname);
			} else {
				final Integer port = Integer.valueOf(portS);
				serverInfo = new OmegaServerInformation(hostname, port);
			}
			final String username = OmeroConnectionDialog.this.usernameTxtFie
					.getText();
			final String password = String
					.valueOf(OmeroConnectionDialog.this.passwordPswFie
							.getPassword());
			final OmegaLoginCredentials loginCred = new OmegaLoginCredentials(
					username, password);

			OmeroConnectionDialog.this.connectionStatusLbl
			.setText("Status:  connecting...");

			boolean connected = false;
			int error = -1;
			try {
				error = this.gateway.connect(loginCred, serverInfo);
			} catch (final Exception ex) {
				final OmeroPluginPanel opp = (OmeroPluginPanel) this.parent;
				OmegaLogFileManager.handlePluginException(opp.getPlugin(), ex,
						false);
			}

			connected = OmeroConnectionDialog.this.gateway.isConnected();
			OmeroImporterUtilities.handleConnectionError(
					OmeroConnectionDialog.this.getParentContainer(), error);

			if (connected == false) {
				OmeroConnectionDialog.this.connectionStatusLbl
				.setText("Status: not connected.");
				OmeroConnectionDialog.this.parent.firePropertyChange(
						OmegaConstantsEvent.PROPERTY_CONNECTION, 0, 1);
			} else {
				OmeroConnectionDialog.this.usernameTxtFie.setEditable(false);
				OmeroConnectionDialog.this.passwordPswFie.setEditable(false);
				OmeroConnectionDialog.this.hostnameTxtFie.setEditable(false);
				OmeroConnectionDialog.this.portTxtFie.setEditable(false);
				OmeroConnectionDialog.this.connectButt.setText("Disconnect");
				OmeroConnectionDialog.this.connectionStatusLbl
				.setText("Status: connected.");
				OmeroConnectionDialog.this.parent.firePropertyChange(
						OmegaConstantsEvent.PROPERTY_CONNECTION, 0, 1);
				OmeroConnectionDialog.this.setVisible(false);
			}

			OmeroConnectionDialog.this.saveOptions(hostname, portS, username,
					password);
		} else {
			try {
				OmeroConnectionDialog.this.gateway.disconnect();
			} catch (final Exception ex) {
				final OmeroPluginPanel opp = (OmeroPluginPanel) this.parent;
				OmegaLogFileManager.handlePluginException(opp.getPlugin(), ex,
						false);
			}
			OmeroConnectionDialog.this.usernameTxtFie.setEditable(true);
			OmeroConnectionDialog.this.passwordPswFie.setEditable(true);
			OmeroConnectionDialog.this.hostnameTxtFie.setEditable(true);
			OmeroConnectionDialog.this.portTxtFie.setEditable(true);
			OmeroConnectionDialog.this.connectButt.setText("Connect");
			OmeroConnectionDialog.this.parent.firePropertyChange(
					OmegaConstantsEvent.PROPERTY_CONNECTION, 0, 1);

		}
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
					loginPsw = OmegaDataEncryptionUtilities.encrypt(password);
				} catch (final UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (final GeneralSecurityException e) {
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
