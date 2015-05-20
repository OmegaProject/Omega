package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.gui.dialogs.GenericDialog;
import edu.umassmed.omega.commons.gui.dialogs.GenericInsertDialog;
import edu.umassmed.omega.data.utilities.OmegaTracksImporter;

public class OmegaTracksImporterDialog extends GenericDialog {

	private static final long serialVersionUID = -4689339679604912836L;

	private JButton chooseFile_btt, import_btt, close_btt;
	private JButton addData_btt, moveDataUp_btt, moveDataDown_btt,
	        removeData_btt;
	private JTextField file_txt, fileIdentifier_txt, trackIdentifier_txt,
	        particleIdentifier_txt, nonParticleIdentifier_txt,
	        particleSeparatordentifier_txt;
	private JList<String> particleData_lst;
	private DefaultListModel<String> particleData_mdl;
	private JFileChooser fileChooser;
	private JCheckBox multipleFiles_ckb, startAtOne_ckb;
	private GenericInsertDialog insertDialog;

	private String selectedVal;

	private final OmegaTracksImporter oti;

	public OmegaTracksImporterDialog(final RootPaneContainer parentContainer,
	        final OmegaTracksImporter oti) {
		super(parentContainer, "Omega Tracks Importer", false);
		this.oti = oti;
		this.selectedVal = null;
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.revalidate();
		this.repaint();
		this.pack();
	}

	@Override
	protected void createAndAddWidgets() {
		this.insertDialog = new GenericInsertDialog(this.getParentContainer(),
		        "Insert particle data", true);
		this.fileChooser = new JFileChooser(System.getProperty("user.dir"));
		this.fileChooser
		        .setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		final JPanel fieldsPanelMain = new JPanel();
		fieldsPanelMain.setLayout(new BorderLayout());
		final JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(new GridLayout(8, 1));

		final JPanel filePanel = new JPanel();
		filePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		final JLabel folder_lbl = new JLabel("Select folder: ");
		folder_lbl.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		filePanel.add(folder_lbl);
		this.file_txt = new JTextField();
		this.file_txt.setPreferredSize(OmegaConstants.LARGE_TEXT_SIZE);
		filePanel.add(this.file_txt);
		this.chooseFile_btt = new JButton("Choose file");
		this.chooseFile_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		filePanel.add(this.chooseFile_btt);
		fieldsPanel.add(filePanel);

		final JPanel multipleFilesPanel = new JPanel();
		multipleFilesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		final JLabel multipleFiles_lbl = new JLabel("Is multiple file: ");
		multipleFiles_lbl.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		multipleFilesPanel.add(multipleFiles_lbl);
		this.multipleFiles_ckb = new JCheckBox();
		multipleFilesPanel.add(this.multipleFiles_ckb);
		fieldsPanel.add(multipleFilesPanel);

		final JPanel fileIdentifierPanel = new JPanel();
		fileIdentifierPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		final JLabel fileIdentifier_lbl = new JLabel(
		        "Insert file indentifier: ");
		fileIdentifier_lbl.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		fileIdentifierPanel.add(fileIdentifier_lbl);
		this.fileIdentifier_txt = new JTextField();
		this.fileIdentifier_txt
		        .setPreferredSize(OmegaConstants.LARGE_TEXT_SIZE);
		fileIdentifierPanel.add(this.fileIdentifier_txt);
		fieldsPanel.add(fileIdentifierPanel);

		final JPanel trackIdentifierPanel = new JPanel();
		trackIdentifierPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		final JLabel trackIdentifier_lbl = new JLabel("Track indentifier: ");
		trackIdentifier_lbl.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		trackIdentifierPanel.add(trackIdentifier_lbl);
		this.trackIdentifier_txt = new JTextField();
		this.trackIdentifier_txt
		        .setPreferredSize(OmegaConstants.LARGE_TEXT_SIZE);
		trackIdentifierPanel.add(this.trackIdentifier_txt);
		fieldsPanel.add(trackIdentifierPanel);

		final JPanel particleIdentifierPanel = new JPanel();
		particleIdentifierPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		final JLabel particleIdentifier_lbl = new JLabel(
		        "Particle indentifier: ");
		particleIdentifier_lbl
		        .setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		particleIdentifierPanel.add(particleIdentifier_lbl);
		this.particleIdentifier_txt = new JTextField();
		this.particleIdentifier_txt
		        .setPreferredSize(OmegaConstants.LARGE_TEXT_SIZE);
		particleIdentifierPanel.add(this.particleIdentifier_txt);
		fieldsPanel.add(particleIdentifierPanel);

		final JPanel particleStartPanel = new JPanel();
		particleStartPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		final JLabel particleStart_lbl = new JLabel(
		        "Particle index starts at 1: ");
		particleStart_lbl.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		particleStartPanel.add(particleStart_lbl);
		this.startAtOne_ckb = new JCheckBox();
		particleStartPanel.add(this.startAtOne_ckb);
		fieldsPanel.add(particleStartPanel);

		final JPanel nonParticleIdentifierPanel = new JPanel();
		nonParticleIdentifierPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		final JLabel nonParticleIdentifier_lbl = new JLabel(
		        "Non-particle indentifier: ");
		nonParticleIdentifier_lbl
		        .setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		nonParticleIdentifierPanel.add(nonParticleIdentifier_lbl);
		this.nonParticleIdentifier_txt = new JTextField();
		this.nonParticleIdentifier_txt
		        .setPreferredSize(OmegaConstants.LARGE_TEXT_SIZE);
		nonParticleIdentifierPanel.add(this.nonParticleIdentifier_txt);
		fieldsPanel.add(nonParticleIdentifierPanel);

		final JPanel particleSeparatorIdentifierPanel = new JPanel();
		particleSeparatorIdentifierPanel.setLayout(new FlowLayout(
		        FlowLayout.LEFT));
		final JLabel particleSeparatorIdentifier_lbl = new JLabel(
		        "Particle separator: ");
		particleSeparatorIdentifier_lbl
		        .setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		particleSeparatorIdentifierPanel.add(particleSeparatorIdentifier_lbl);
		this.particleSeparatordentifier_txt = new JTextField();
		this.particleSeparatordentifier_txt
		        .setPreferredSize(OmegaConstants.LARGE_TEXT_SIZE);
		particleSeparatorIdentifierPanel
		        .add(this.particleSeparatordentifier_txt);
		fieldsPanel.add(particleSeparatorIdentifierPanel);

		final JPanel particleDataPanel = new JPanel();
		particleDataPanel.setLayout(new BorderLayout(5, 5));
		particleDataPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JLabel particleDataPanel_lbl = new JLabel(
		        "Particle data and order: ");
		particleDataPanel_lbl
		        .setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		particleDataPanel_lbl.setHorizontalAlignment(SwingConstants.LEFT);
		particleDataPanel_lbl.setVerticalAlignment(SwingConstants.TOP);
		particleDataPanel.add(particleDataPanel_lbl, BorderLayout.WEST);
		this.particleData_mdl = new DefaultListModel<String>();
		this.particleData_lst = new JList<String>(this.particleData_mdl);
		this.particleData_mdl
		        .addElement(OmegaTracksImporter.PARTICLE_FRAMEINDEX);
		this.particleData_mdl.addElement(OmegaTracksImporter.PARTICLE_XCOORD);
		this.particleData_mdl.addElement(OmegaTracksImporter.PARTICLE_YCOORD);
		// this.particleData_mdl.addElement("intensity");
		this.particleData_lst
		        .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final JScrollPane sp = new JScrollPane(this.particleData_lst);
		sp.setPreferredSize(new Dimension(OmegaConstants.LARGE_TEXT_SIZE.width,
		        200));
		particleDataPanel.add(sp, BorderLayout.CENTER);
		final JPanel particleDataButtonPanelMain = new JPanel();
		particleDataButtonPanelMain.setLayout(new BorderLayout());
		final JPanel particleDataButtonPanel = new JPanel();
		particleDataButtonPanel.setLayout(new GridLayout(4, 1));
		this.addData_btt = new JButton("Add");
		this.addData_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		particleDataButtonPanel.add(this.addData_btt);
		this.moveDataUp_btt = new JButton("Move Up");
		this.moveDataUp_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.moveDataUp_btt.setEnabled(false);
		particleDataButtonPanel.add(this.moveDataUp_btt);
		this.moveDataDown_btt = new JButton("Move Down");
		this.moveDataDown_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.moveDataDown_btt.setEnabled(false);
		particleDataButtonPanel.add(this.moveDataDown_btt);
		this.removeData_btt = new JButton("Remove");
		this.removeData_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.removeData_btt.setEnabled(false);
		particleDataButtonPanel.add(this.removeData_btt);
		particleDataButtonPanelMain.add(particleDataButtonPanel,
		        BorderLayout.NORTH);
		particleDataButtonPanelMain.add(new JLabel(), BorderLayout.CENTER);
		particleDataPanel.add(particleDataButtonPanelMain, BorderLayout.EAST);

		fieldsPanelMain.add(fieldsPanel, BorderLayout.NORTH);
		fieldsPanelMain.add(particleDataPanel, BorderLayout.CENTER);

		mainPanel.add(fieldsPanelMain, BorderLayout.NORTH);
		mainPanel.add(new JLabel(), BorderLayout.CENTER);
		this.add(mainPanel, BorderLayout.CENTER);

		final JPanel buttPanel = new JPanel();
		buttPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		this.import_btt = new JButton("Import");
		this.import_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttPanel.add(this.import_btt);

		this.close_btt = new JButton(OmegaGUIConstants.MENU_FILE_CLOSE);
		this.close_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttPanel.add(this.close_btt);

		this.add(buttPanel, BorderLayout.SOUTH);
	}

	@Override
	protected void addListeners() {
		this.chooseFile_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTracksImporterDialog.this.handleChooseFile();
			}
		});
		this.multipleFiles_ckb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTracksImporterDialog.this.handleMultipleFilesSelection();
			}
		});
		this.particleData_lst
		        .addListSelectionListener(new ListSelectionListener() {
			        @Override
			        public void valueChanged(final ListSelectionEvent e) {
				        OmegaTracksImporterDialog.this.handleListSelection();
			        }
		        });
		this.addData_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTracksImporterDialog.this.handleAddData();
			}
		});
		this.moveDataUp_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTracksImporterDialog.this.handleMoveDataUp();
			}
		});
		this.moveDataDown_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTracksImporterDialog.this.handleMoveDataDown();
			}
		});
		this.removeData_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTracksImporterDialog.this.handleRemoveData();
			}
		});
		this.import_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTracksImporterDialog.this.handleImport();
			}
		});
		this.close_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTracksImporterDialog.this.handleClose();
			};
		});
	}

	private void handleAddData() {
		this.insertDialog.reset();
		this.insertDialog.setVisible(true);
		if (!this.insertDialog.getConfirmation())
			return;
		final String toInsert = this.insertDialog.getContent().toLowerCase();
		if (this.particleData_mdl.contains(toInsert)) {
			// TODO ERROR
		} else if (toInsert.isEmpty()) {
			// TODO ERROR
		} else {
			this.particleData_mdl.addElement(toInsert);
		}
	}

	private void handleMoveDataUp() {
		final int index = this.particleData_mdl.indexOf(this.selectedVal);
		this.particleData_mdl.insertElementAt(this.selectedVal, index - 1);
		this.particleData_mdl.remove(index + 1);
	}

	private void handleMoveDataDown() {
		final int index = this.particleData_mdl.indexOf(this.selectedVal);
		int newIndex = index + 2;
		if (newIndex > this.particleData_mdl.getSize()) {
			newIndex = this.particleData_mdl.getSize();
		}
		this.particleData_mdl.insertElementAt(this.selectedVal, newIndex);
		this.particleData_mdl.remove(index);
	}

	private void handleRemoveData() {
		this.particleData_mdl.removeElement(this.selectedVal);
	}

	private void handleListSelection() {
		this.moveDataUp_btt.setEnabled(false);
		this.moveDataDown_btt.setEnabled(false);
		this.removeData_btt.setEnabled(false);
		this.selectedVal = this.particleData_lst.getSelectedValue();
		if (this.selectedVal == null)
			return;
		final int index = this.particleData_mdl.indexOf(this.selectedVal);
		if (!this.selectedVal.equals("identifier")
		        && !this.selectedVal.equals("x")
		        && !this.selectedVal.equals("y")
		        && !this.selectedVal.equals("intensity")) {
			if (index == 3) {
				this.moveDataDown_btt.setEnabled(true);
				this.removeData_btt.setEnabled(true);
			} else {
				this.moveDataUp_btt.setEnabled(true);
				this.moveDataDown_btt.setEnabled(true);
				this.removeData_btt.setEnabled(true);
			}
		}
	}

	private void handleMultipleFilesSelection() {
		if (this.multipleFiles_ckb.isSelected()) {
			this.fileIdentifier_txt.setEnabled(true);
		} else {
			this.fileIdentifier_txt.setEnabled(false);
		}
	}

	private void handleChooseFile() {
		final int result = this.fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			final File dir = this.fileChooser.getSelectedFile();
			this.file_txt.setText(dir.getAbsolutePath());
		} else {
			this.file_txt.setText("");
		}
	}

	private void handleImport() {
		this.oti.reset();
		String fileIdentifier = null;
		File sourceFolder = null;
		String trackIdentifier = null;
		if (!this.trackIdentifier_txt.getText().isEmpty()) {
			trackIdentifier = this.trackIdentifier_txt.getText();
		}
		String particleIdentifier = null;
		if (!this.particleIdentifier_txt.getText().isEmpty()) {
			particleIdentifier = this.particleIdentifier_txt.getText();
		}
		String nonParticleIdentifier = null;
		if (!this.nonParticleIdentifier_txt.getText().isEmpty()) {
			nonParticleIdentifier = this.nonParticleIdentifier_txt.getText();
		}
		String particleSeparator = null;
		if (!this.particleSeparatordentifier_txt.getText().isEmpty()) {
			particleSeparator = this.particleSeparatordentifier_txt.getText();
		}
		final List<String> dataOrder = new ArrayList<String>();
		for (int i = 0; i < this.particleData_mdl.getSize(); i++) {
			dataOrder.add(this.particleData_mdl.get(i));
		}
		String fName = null;
		if (!this.file_txt.getText().isEmpty()) {
			fName = this.file_txt.getText();
		}
		if (fName == null)
			// TODO ERROR
			return;
		final File f = new File(fName);
		if (this.multipleFiles_ckb.isSelected()) {
			if (f.isFile())
				// TODO ERROR
				return;
			if (!this.fileIdentifier_txt.getText().isEmpty()) {
				fileIdentifier = this.fileIdentifier_txt.getText();
			}
			if (fileIdentifier == null)
				// TODO ERROR
				return;
			sourceFolder = f;
		} else {
			if (f.isDirectory())
				// TODO ERROR
				return;
			sourceFolder = f.getParentFile();
			fileIdentifier = f.getName();
		}
		final boolean startAtOne = this.startAtOne_ckb.isSelected();
		try {
			this.oti.importTrajectories(fileIdentifier, trackIdentifier,
			        particleIdentifier, startAtOne, nonParticleIdentifier,
			        particleSeparator, dataOrder, sourceFolder);
		} catch (final IllegalArgumentException e) {
			// TODO MANAGE ERROR
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO MANAGE ERROR
			e.printStackTrace();
		}
		this.setVisible(false);
	}

	private void handleClose() {
		this.setVisible(false);
	}
}
