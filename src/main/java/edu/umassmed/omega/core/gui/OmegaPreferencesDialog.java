package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.constants.OmegaGenericConstants;
import edu.umassmed.omega.commons.exceptions.OmegaPluginExceptionStatusPanel;
import edu.umassmed.omega.commons.gui.GenericStatusPanel;
import edu.umassmed.omega.commons.gui.GenericTextFieldValidable;
import edu.umassmed.omega.commons.gui.dialogs.GenericDialog;

public class OmegaPreferencesDialog extends GenericDialog {

	public static String CATEGORY = "CATEGORY GENERAL PREFERENCES";

	private static final long serialVersionUID = 7716993379359524083L;

	private GenericTextFieldValidable graphLineWidth_txt, graphShapeWidth_txt,
			trackLineWidth_txt;
	private GenericStatusPanel statusPanel;

	private final OmegaGUIFrame parent;

	private JButton confirm, cancel;
	private boolean confirmation;
	
	private final Map<String, String> options;
	
	public OmegaPreferencesDialog(final OmegaGUIFrame parent) {
		super(parent, "Omega Preferences", false);

		this.parent = parent;

		this.options = parent
				.getGeneralOptions(OmegaDBPreferencesDialog.CATEGORY);
		this.populateFields();

		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

	@Override
	protected void createAndAddWidgets() {
		final JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		final JPanel trackLineWidthPanel = new JPanel();
		trackLineWidthPanel.setLayout(new BoxLayout(trackLineWidthPanel,
				BoxLayout.X_AXIS));
		final JLabel trackLineWidth_lbl = new JLabel(
				OmegaGenericConstants.PREF_TRACK_LINE_SIZE);
		trackLineWidth_lbl.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		trackLineWidthPanel.add(trackLineWidth_lbl);

		this.trackLineWidth_txt = new GenericTextFieldValidable(
				GenericTextFieldValidable.CONTENT_INT);
		this.trackLineWidth_txt.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		trackLineWidthPanel.add(this.trackLineWidth_txt);
		
		centerPanel.add(trackLineWidthPanel);

		final JPanel graphLineWidthPanel = new JPanel();
		graphLineWidthPanel.setLayout(new BoxLayout(graphLineWidthPanel,
				BoxLayout.X_AXIS));
		final JLabel graphLineWidth_lbl = new JLabel(
				OmegaGenericConstants.PREF_GRAPH_LINE_SIZE);
		graphLineWidth_lbl.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		graphLineWidthPanel.add(graphLineWidth_lbl);

		// this.lineWidth_txt = new JTextField();
		this.graphLineWidth_txt = new GenericTextFieldValidable(
				GenericTextFieldValidable.CONTENT_INT);
		this.graphLineWidth_txt.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		graphLineWidthPanel.add(this.graphLineWidth_txt);
		
		centerPanel.add(graphLineWidthPanel);

		final JPanel graphShapeWidthPanel = new JPanel();
		graphShapeWidthPanel.setLayout(new BoxLayout(graphShapeWidthPanel,
				BoxLayout.X_AXIS));
		final JLabel trackShapeWidth_lbl = new JLabel(
				OmegaGenericConstants.PREF_GRAPH_SHAPE_SIZE);
		trackShapeWidth_lbl.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		graphShapeWidthPanel.add(trackShapeWidth_lbl);

		this.graphShapeWidth_txt = new GenericTextFieldValidable(
				GenericTextFieldValidable.CONTENT_INT);
		this.graphShapeWidth_txt.setPreferredSize(OmegaGUIConstants.TEXT_SIZE);
		graphShapeWidthPanel.add(this.graphShapeWidth_txt);
		
		centerPanel.add(graphShapeWidthPanel);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		this.confirm = new JButton("Confirm");
		this.confirm.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE);
		this.confirm.setSize(OmegaGUIConstants.BUTTON_SIZE);
		buttonPanel.add(this.confirm);

		this.cancel = new JButton("Cancel");
		this.cancel.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE);
		this.cancel.setSize(OmegaGUIConstants.BUTTON_SIZE);
		buttonPanel.add(this.cancel);

		bottomPanel.add(buttonPanel, BorderLayout.NORTH);

		this.statusPanel = new GenericStatusPanel(1);
		try {
			this.statusPanel.updateStatus(0, " ");
		} catch (final OmegaPluginExceptionStatusPanel e) {
			OmegaLogFileManager.handle(e, false);
		}
		bottomPanel.add(this.statusPanel, BorderLayout.SOUTH);

		this.add(centerPanel, BorderLayout.CENTER);

		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	@Override
	protected void addListeners() {
		this.confirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				if (OmegaPreferencesDialog.this.validateFields()) {
					OmegaPreferencesDialog.this.confirmation = true;
					OmegaPreferencesDialog.this.setVisible(false);
					OmegaPreferencesDialog.this.saveOptions();
				}
			}
		});
		this.cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaPreferencesDialog.this.confirmation = false;
				OmegaPreferencesDialog.this.setVisible(false);
			}
		});
	}
	
	private boolean validateFields() {
		if (!this.trackLineWidth_txt.isContentValidated()) {
			try {
				this.statusPanel.updateStatus(0,
						this.trackLineWidth_txt.getError());
			} catch (final OmegaPluginExceptionStatusPanel e) {
				OmegaLogFileManager.handle(e, false);
			}
			return false;
		} else if (!this.graphLineWidth_txt.isContentValidated()) {
			try {
				this.statusPanel.updateStatus(0,
						this.graphLineWidth_txt.getError());
			} catch (final OmegaPluginExceptionStatusPanel e) {
				OmegaLogFileManager.handle(e, false);
			}
			return false;
		} else if (!this.graphShapeWidth_txt.isContentValidated()) {
			try {
				this.statusPanel.updateStatus(0,
						this.graphShapeWidth_txt.getError());
			} catch (final OmegaPluginExceptionStatusPanel e) {
				OmegaLogFileManager.handle(e, false);
			}
			return false;
		}
		try {
			this.statusPanel.updateStatus(0, " ");
		} catch (final OmegaPluginExceptionStatusPanel e) {
			OmegaLogFileManager.handle(e, false);
		}
		return true;
	}
	
	public boolean getConfirmation() {
		return this.confirmation;
	}

	private void populateFields() {
		if (this.options
				.containsKey(OmegaGenericConstants.PREF_TRACK_LINE_SIZE)) {
			this.trackLineWidth_txt.setText(this.options
					.get(OmegaGenericConstants.PREF_TRACK_LINE_SIZE));
		} else {
			this.trackLineWidth_txt.setText(String
					.valueOf(OmegaGenericConstants.PREF_TRACK_LINE_SIZE_VAL));
		}
		if (this.options
				.containsKey(OmegaGenericConstants.PREF_GRAPH_LINE_SIZE)) {
			this.graphLineWidth_txt.setText(this.options
					.get(OmegaGenericConstants.PREF_GRAPH_LINE_SIZE));
		} else {
			this.graphLineWidth_txt.setText(String
					.valueOf(OmegaGenericConstants.PREF_GRAPH_LINE_SIZE_VAL));
		}
		if (this.options
				.containsKey(OmegaGenericConstants.PREF_GRAPH_SHAPE_SIZE)) {
			this.graphShapeWidth_txt.setText(this.options
					.get(OmegaGenericConstants.PREF_GRAPH_SHAPE_SIZE));
		} else {
			this.graphShapeWidth_txt.setText(String
					.valueOf(OmegaGenericConstants.PREF_GRAPH_SHAPE_SIZE_VAL));
		}
	}
	
	public void saveOptions() {
		final Map<String, String> options = new LinkedHashMap<String, String>();
		final String val = this.trackLineWidth_txt.getText();
		final int trackLineSize = Integer.valueOf(val);
		options.put(OmegaGenericConstants.PREF_TRACK_LINE_SIZE, val);
		
		final String val2 = this.graphLineWidth_txt.getText();
		final int graphLineSize = Integer.valueOf(val2);
		options.put(OmegaGenericConstants.PREF_GRAPH_LINE_SIZE, val2);
		
		final String val3 = this.graphShapeWidth_txt.getText();
		final int graphShapeSize = Integer.valueOf(val3);
		options.put(OmegaGenericConstants.PREF_GRAPH_SHAPE_SIZE, val3);
		
		this.parent.addGeneralOptions(OmegaPreferencesDialog.CATEGORY, options);
		this.parent.setTrackLineSize(trackLineSize);
		this.parent.setGraphLineSize(graphLineSize);
		this.parent.setGraphShapeSize(graphShapeSize);
	}
}
