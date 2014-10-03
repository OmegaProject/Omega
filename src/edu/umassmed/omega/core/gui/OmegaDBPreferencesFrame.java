/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team: 
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli, 
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban, 
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.utilities.OmegaDataEncryptionUtilities;
import edu.umassmed.omega.core.OmegaMySqlGateway;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaDBServerInformation;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaLoginCredentials;

public class OmegaDBPreferencesFrame extends JDialog {

	public static String CATEGORY = "CATEGORY GENERAL OMEGA_DB_PREFERENCES";
	public static String OPTION_SERVER_ADRESS = "Omega server adress";
	public static String OPTION_SERVER_PORT = "Omega server port";
	public static String OPTION_SERVER_DB_NAME = "Omega server db name";
	public static String OPTION_LOGIN_USERNAME = "Omega login username";
	public static String OPTION_LOGIN_PASSWORD = "Omega login password";

	private final Map<String, String> options;

	private final OmegaGUIFrame parent;

	private static final long serialVersionUID = -1021787512167305062L;

	private JTextField usernameTxtFie, hostnameTxtFie, portTxtFie, dbNameFie;
	private JPasswordField passwordPswFie;

	private JCheckBox saveServerInfo, saveLoginInfo;

	private JLabel connectionStatusLbl;

	private JButton connectButt;

	// private final OmegaMySqlGateway gateway;

	// public OmegaDBPreferencesFrame(final OmegaGUIFrame parent,
	// final OmegaMySqlGateway gateway) {
	public OmegaDBPreferencesFrame(final OmegaGUIFrame parent) {
		this.parent = parent;
		// this.gateway = gateway;

		this.options = parent
		        .getGeneralOptions(OmegaDBPreferencesFrame.CATEGORY);
		System.out.println(OmegaDBPreferencesFrame.CATEGORY);
		for (final String s : this.options.keySet()) {
			System.out.println(s + "\t" + this.options.get(s));
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

		// final JPanel bottomPanel = new JPanel();
		// bottomPanel.setLayout(new BorderLayout());

		// final JPanel buttonPanel = new JPanel();
		// buttonPanel.setLayout(new FlowLayout());

		// this.connectButt = new JButton("Connect");
		// this.connectButt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		// buttonPanel.add(this.connectButt);

		// bottomPanel.add(buttonPanel, BorderLayout.CENTER);

		// this.connectionStatusLbl = new JLabel("Status: not connected.");
		// this.connectionStatusLbl.setHorizontalAlignment(Align.CENTER);
		// bottomPanel.add(this.connectionStatusLbl, BorderLayout.SOUTH);

		// this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private JPanel createServerPanel() {
		final JPanel serverPanel = new JPanel();
		serverPanel.setLayout(new GridLayout(4, 0));

		serverPanel.setBorder(new TitledBorder("Server information"));

		final JLabel hostnameLbl = new JLabel("Insert server hostname:");
		serverPanel.add(hostnameLbl);

		this.hostnameTxtFie = new JTextField();
		this.hostnameTxtFie.setPreferredSize(OmegaConstants.TEXT_SIZE);
		serverPanel.add(this.hostnameTxtFie);
		if (this.options
		        .containsKey(OmegaDBPreferencesFrame.OPTION_SERVER_ADRESS)) {
			this.hostnameTxtFie.setText(this.options
			        .get(OmegaDBPreferencesFrame.OPTION_SERVER_ADRESS));
		} else {
			this.hostnameTxtFie.setText(OmegaMySqlGateway.HOSTNAME);
		}

		final JLabel portLbl = new JLabel("Insert server port:");
		serverPanel.add(portLbl);

		this.portTxtFie = new JTextField();
		this.portTxtFie.setPreferredSize(OmegaConstants.TEXT_SIZE);
		serverPanel.add(this.portTxtFie);
		if (this.options
		        .containsKey(OmegaDBPreferencesFrame.OPTION_SERVER_PORT)) {
			this.portTxtFie.setText(this.options
			        .get(OmegaDBPreferencesFrame.OPTION_SERVER_PORT));
		} else {
			this.portTxtFie.setText(OmegaMySqlGateway.PORT);
		}

		final JLabel dbNameLbl = new JLabel("Insert db name:");
		serverPanel.add(dbNameLbl);

		this.dbNameFie = new JTextField();
		this.dbNameFie.setPreferredSize(OmegaConstants.TEXT_SIZE);
		serverPanel.add(this.dbNameFie);
		if (this.options
		        .containsKey(OmegaDBPreferencesFrame.OPTION_SERVER_DB_NAME)) {
			this.dbNameFie.setText(this.options
			        .get(OmegaDBPreferencesFrame.OPTION_SERVER_DB_NAME));
		} else {
			this.dbNameFie.setText(OmegaMySqlGateway.DB_NAME);
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
		if (this.options
		        .containsKey(OmegaDBPreferencesFrame.OPTION_LOGIN_USERNAME)) {
			this.usernameTxtFie.setText(this.options
			        .get(OmegaDBPreferencesFrame.OPTION_LOGIN_USERNAME));
		} else {
			this.usernameTxtFie.setText(OmegaMySqlGateway.USER);
		}

		final JLabel passwordLbl = new JLabel("Insert your password:");
		loginPanel.add(passwordLbl);

		this.passwordPswFie = new JPasswordField();
		loginPanel.add(this.passwordPswFie);
		this.passwordPswFie.setPreferredSize(OmegaConstants.TEXT_SIZE);
		if (this.options
		        .containsKey(OmegaDBPreferencesFrame.OPTION_LOGIN_PASSWORD)) {
			final String psw = this.options
			        .get(OmegaDBPreferencesFrame.OPTION_LOGIN_PASSWORD);
			try {
				this.passwordPswFie.setText(OmegaDataEncryptionUtilities.decrypt(psw));
			} catch (final GeneralSecurityException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}

		} else {
			this.passwordPswFie.setText(OmegaMySqlGateway.PSW);
		}

		this.saveLoginInfo = new JCheckBox("Remember login information?");
		loginPanel.add(this.saveLoginInfo);

		return loginPanel;
	}

	public OmegaDBServerInformation getOmegaDBServerInformation() {
		final String hostname = OmegaDBPreferencesFrame.this.hostnameTxtFie
		        .getText();
		final String portS = OmegaDBPreferencesFrame.this.portTxtFie.getText();
		final String dbName = OmegaDBPreferencesFrame.this.dbNameFie.getText();

		OmegaDBServerInformation serverInfo = null;
		if (portS.length() == 0) {
			serverInfo = new OmegaDBServerInformation(hostname, dbName);
		} else {
			final Integer port = Integer.valueOf(portS);
			serverInfo = new OmegaDBServerInformation(hostname, port, dbName);
		}
		this.saveServerInfoOptions(hostname, portS, dbName);
		return serverInfo;
	}

	public OmegaLoginCredentials getOmegaDBLoginCredentials() {
		final String username = OmegaDBPreferencesFrame.this.usernameTxtFie
		        .getText();
		final String password = String
		        .valueOf(OmegaDBPreferencesFrame.this.passwordPswFie
		                .getPassword());
		final OmegaLoginCredentials loginCred = new OmegaLoginCredentials(
		        username, password);
		this.saveLoginCredsOptions(username, password);
		return loginCred;
	}

	private void addListeners() {

	}

	public void saveServerInfoOptions(final String hostname, final String port,
	        final String dbName) {
		final Map<String, String> options = new LinkedHashMap<String, String>();
		if (OmegaDBPreferencesFrame.this.saveServerInfo.isSelected()) {
			options.put(OmegaDBPreferencesFrame.OPTION_SERVER_ADRESS, hostname);
			if (!port.isEmpty()) {
				options.put(OmegaDBPreferencesFrame.OPTION_SERVER_PORT,
				        OmegaDBPreferencesFrame.this.portTxtFie.getText());
			}
			options.put(OmegaDBPreferencesFrame.OPTION_SERVER_DB_NAME, dbName);
		}
		this.parent
		        .addGeneralOptions(OmegaDBPreferencesFrame.CATEGORY, options);
	}

	public void saveLoginCredsOptions(final String username,
	        final String password) {
		final Map<String, String> options = new LinkedHashMap<String, String>();
		if (OmegaDBPreferencesFrame.this.saveLoginInfo.isSelected()) {
			options.put(OmegaDBPreferencesFrame.OPTION_LOGIN_USERNAME, username);
			String loginPsw = null;
			try {
				loginPsw = OmegaDataEncryptionUtilities.encrypt(password);
			} catch (final UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (final GeneralSecurityException e) {
				e.printStackTrace();
			}
			if (loginPsw != null) {
				options.put(OmegaDBPreferencesFrame.OPTION_LOGIN_PASSWORD,
				        loginPsw);
			}
		}
		this.parent
		        .addGeneralOptions(OmegaDBPreferencesFrame.CATEGORY, options);
	}
}
