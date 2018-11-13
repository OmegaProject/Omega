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
package edu.umassmed.omega.omeroImageBrowser.gui;

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

import omero.ServerError;
import Glacier2.CannotCreateSessionException;
import Glacier2.PermissionDeniedException;
import Ice.ConnectionRefusedException;
import Ice.DNSException;
import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaEventConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaLoginCredentials;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaServerInformation;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.gui.dialogs.GenericDialog;
import edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import edu.umassmed.omega.commons.utilities.OmegaDataEncryptionUtilities;
import edu.umassmed.omega.omero.commons.OmeroGateway;
import edu.umassmed.omega.omero.commons.data.OmeroServerInformation;

public class OmeroImageBrowserConnectionDialog extends GenericDialog {
	
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
	
	public OmeroImageBrowserConnectionDialog(final RootPaneContainer parentContainer,
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
		this.connectButt.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE);
		this.connectButt.setSize(OmegaGUIConstants.BUTTON_SIZE);
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
		this.hostnameTxtFie.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		serverPanel.add(this.hostnameTxtFie);
		final JLabel portLbl = new JLabel(
				"Insert server port (empty for default "
						+ OmeroServerInformation.DEFAULT_PORT + "):");
		serverPanel.add(portLbl);
		this.portTxtFie = new JTextField();
		this.portTxtFie.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
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
		this.usernameTxtFie.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		loginPanel.add(this.usernameTxtFie);
		final JLabel passwordLbl = new JLabel("Insert your password:");
		loginPanel.add(passwordLbl);
		this.passwordPswFie = new JPasswordField();
		loginPanel.add(this.passwordPswFie);
		this.passwordPswFie.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
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
				OmeroImageBrowserConnectionDialog.this.handleConnect();
			}
		});
	}

	private void handleConnect() {
		if (!OmeroImageBrowserConnectionDialog.this.gateway.isConnected()) {
			final String hostname = OmeroImageBrowserConnectionDialog.this.hostnameTxtFie
					.getText();
			final String portS = OmeroImageBrowserConnectionDialog.this.portTxtFie
					.getText();
			
			OmegaServerInformation serverInfo = null;
			if (portS.length() == 0) {
				serverInfo = new OmeroServerInformation(hostname);
			} else {
				final Integer port = Integer.valueOf(portS);
				serverInfo = new OmegaServerInformation(hostname, port);
			}
			final String username = OmeroImageBrowserConnectionDialog.this.usernameTxtFie
					.getText();
			final String password = String
					.valueOf(OmeroImageBrowserConnectionDialog.this.passwordPswFie
							.getPassword());
			final OmegaLoginCredentials loginCred = new OmegaLoginCredentials(
					username, password);
			
			OmeroImageBrowserConnectionDialog.this.connectionStatusLbl
					.setText("Status:  connecting...");
			
			boolean connected = false;
			String errorMsg = null;
			try {
				this.gateway.connect(loginCred, serverInfo);
			} catch (final CannotCreateSessionException ex) {
				errorMsg = "Unable to create a session.";
			} catch (final PermissionDeniedException ex) {
				errorMsg = "<html>Access denied.<br>Verify username and/or password.</html>";
			} catch (final ServerError ex) {
				errorMsg = "Server error.";
			} catch (final DNSException ex) {
				errorMsg = "<html>Unable to find the server<br>Verify server address.</html>";
			} catch (final ConnectionRefusedException ex) {
				errorMsg = "<html>Server refused the connection.<br>Verify port.</html>";
			} catch (final Exception ex) {
				errorMsg = "Unknown error.";
				// OmegaLogFileManager.handleUncaughtException(ex, true);
			}
			if (errorMsg != null) {
				final GenericMessageDialog errorDialog = new GenericMessageDialog(
						this.getParentContainer(),
						"Omero server connection error", errorMsg, true);
				errorDialog.enableClose();
				errorDialog.setVisible(true);
			}
			
			connected = OmeroImageBrowserConnectionDialog.this.gateway.isConnected();
			// OmeroImporterUtilities.handleConnectionError(
			// OmeroImageBrowserConnectionDialog.this.getParentContainer(), error);
			
			if (connected == false) {
				OmeroImageBrowserConnectionDialog.this.connectionStatusLbl
						.setText("Status: not connected.");
				OmeroImageBrowserConnectionDialog.this.parent.firePropertyChange(
						OmegaEventConstants.PROPERTY_CONNECTION, 0, 1);
			} else {
				OmeroImageBrowserConnectionDialog.this.usernameTxtFie.setEditable(false);
				OmeroImageBrowserConnectionDialog.this.passwordPswFie.setEditable(false);
				OmeroImageBrowserConnectionDialog.this.hostnameTxtFie.setEditable(false);
				OmeroImageBrowserConnectionDialog.this.portTxtFie.setEditable(false);
				OmeroImageBrowserConnectionDialog.this.connectButt.setText("Disconnect");
				OmeroImageBrowserConnectionDialog.this.connectionStatusLbl
						.setText("Status: connected.");
				OmeroImageBrowserConnectionDialog.this.parent.firePropertyChange(
						OmegaEventConstants.PROPERTY_CONNECTION, 0, 1);
				OmeroImageBrowserConnectionDialog.this.setVisible(false);
			}
			
			OmeroImageBrowserConnectionDialog.this.saveOptions(hostname, portS, username,
					password);
		} else {
			try {
				OmeroImageBrowserConnectionDialog.this.gateway.disconnect();
			} catch (final Exception ex) {
				final OmeroImageBrowserPluginPanel opp = (OmeroImageBrowserPluginPanel) this.parent;
				OmegaLogFileManager.handlePluginException(opp.getPlugin(), ex,
						false);
			}
			OmeroImageBrowserConnectionDialog.this.usernameTxtFie.setEditable(true);
			OmeroImageBrowserConnectionDialog.this.passwordPswFie.setEditable(true);
			OmeroImageBrowserConnectionDialog.this.hostnameTxtFie.setEditable(true);
			OmeroImageBrowserConnectionDialog.this.portTxtFie.setEditable(true);
			OmeroImageBrowserConnectionDialog.this.connectButt.setText("Connect");
			OmeroImageBrowserConnectionDialog.this.parent.firePropertyChange(
					OmegaEventConstants.PROPERTY_CONNECTION, 0, 1);
			
		}
	}
	
	public void saveOptions(final String hostname, final String port,
			final String username, final String password) {
		OmeroImageBrowserPluginPanel pluginPanel = null;
		if (OmeroImageBrowserConnectionDialog.this.parent instanceof OmeroImageBrowserPluginPanel) {
			pluginPanel = (OmeroImageBrowserPluginPanel) OmeroImageBrowserConnectionDialog.this.parent;
		}
		
		if (pluginPanel != null) {
			final Map<String, String> options = new LinkedHashMap<String, String>();
			if (OmeroImageBrowserConnectionDialog.this.saveServerInfo.isSelected()) {
				options.put(OmeroImageBrowserConnectionDialog.this.OPTION_SERVER_ADRESS,
						hostname);
				if (port.isEmpty()) {
					options.put(OmeroImageBrowserConnectionDialog.this.OPTION_SERVER_PORT,
							OmeroImageBrowserConnectionDialog.this.portTxtFie.getText());
				}
			}
			if (OmeroImageBrowserConnectionDialog.this.saveLoginInfo.isSelected()) {
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
