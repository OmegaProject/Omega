package edu.umassmed.omega.trajectoryManagerPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.gui.dialogs.GenericDialog;
import edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import edu.umassmed.omega.commons.utilities.OmegaColorManagerUtilities;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationType;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationTypes;

public class TMSegmentPreferencesDialog extends GenericDialog {

	private static final long serialVersionUID = -915728874655718206L;
	private static final String CREATE_NEW = "Create new segmentation types";
	private static final String NEW_TYPE_NAME = "New segmentation type";

	private final TMPluginPanel pluginPanel;

	private JPanel mainPanel;
	private List<OmegaSegmentationTypes> segmTypesList;
	private OmegaSegmentationTypes actualSegmTypes, oldSegmTypes;

	private final List<OmegaSegmentationType> types;

	private final Map<String, JPanel> segmPanels;
	private final Map<String, JTextField> segmNames_txt, segmVal_txt;
	private final Map<Document, JTextField> namesDoc, valsDoc;
	private final Map<String, JButton> segmColors_btt;
	private final Map<String, JButton> segmRemove_btt;

	private final Map<JPanel, String> panelsToAdd, panelsToRemove;

	private final Map<String, String> newNames;
	private final Map<String, String> newValues;

	private JComboBox<String> segmTypesList_cmb;
	private final Map<String, OmegaSegmentationTypes> segmTypesComboMap;
	private JTextField segmTypesName_txt;
	private JButton newSegmType_btt, cancel_btt, apply_btt, close_btt;
	private ActionListener removeButt_al, colorButt_al;
	private DocumentListener changeName_dl;
	private DocumentListener changeVal_dl;

	private boolean isPopulating, isSegmTypesChanged;

	private int actualCounter;

	public TMSegmentPreferencesDialog(final TMPluginPanel pluginPanel,
	        final RootPaneContainer parent,
	        final List<OmegaSegmentationTypes> segmTypesList) {
		super(parent, "Trajectory segmentation preferences", true);
		this.pluginPanel = pluginPanel;
		this.segmTypesList = new ArrayList<OmegaSegmentationTypes>(
		        segmTypesList);

		this.actualCounter = -1;

		this.isSegmTypesChanged = false;
		this.oldSegmTypes = null;
		this.actualSegmTypes = null;

		this.removeButt_al = null;
		this.changeName_dl = null;
		this.changeVal_dl = null;

		this.types = new ArrayList<OmegaSegmentationType>();

		this.panelsToAdd = new LinkedHashMap<JPanel, String>();
		this.panelsToRemove = new LinkedHashMap<JPanel, String>();

		this.segmPanels = new LinkedHashMap<String, JPanel>();
		this.segmNames_txt = new LinkedHashMap<String, JTextField>();
		this.segmVal_txt = new LinkedHashMap<String, JTextField>();
		this.segmColors_btt = new LinkedHashMap<String, JButton>();
		this.segmRemove_btt = new LinkedHashMap<String, JButton>();

		this.newNames = new LinkedHashMap<String, String>();
		this.newValues = new LinkedHashMap<String, String>();

		this.namesDoc = new LinkedHashMap<Document, JTextField>();
		this.valsDoc = new LinkedHashMap<Document, JTextField>();

		this.segmTypesComboMap = new LinkedHashMap<String, OmegaSegmentationTypes>();

		final Dimension dim = new Dimension(400, 600);
		this.setPreferredSize(dim);
		this.setSize(dim);

		this.populateSegmTypesCombo();

		this.setModal(true);
	}

	private void selectSegmentationTypes(final OmegaSegmentationTypes segmTypes) {
		this.clearAllMaps();

		this.types.addAll(segmTypes.getTypes());

		this.mainPanel.removeAll();
		this.createAndAddSinglePanels();

		final String name = segmTypes.getName();
		this.segmTypesName_txt.setText(name);
		if (name.equals(OmegaSegmentationTypes.DEFAULT_NAME)) {
			this.segmTypesName_txt.setEnabled(false);
		} else {
			this.segmTypesName_txt.setEnabled(true);
		}

		this.resizePanels();
	}

	private void resetSegmentationTypes() {
		this.actualCounter = -1;
		this.clearAllMaps();

		this.types.add(OmegaSegmentationTypes.getDefaultNotAssigned());

		this.mainPanel.removeAll();
		this.createAndAddSinglePanels();

		this.segmTypesName_txt.setText("New segmentation types");
		this.segmTypesName_txt.setEnabled(true);

		this.resizePanels();
	}

	private void clearAllMaps() {
		this.types.clear();

		this.panelsToAdd.clear();
		this.panelsToRemove.clear();

		for (final JButton btt : this.segmColors_btt.values()) {
			btt.removeActionListener(this.colorButt_al);
		}
		this.segmColors_btt.clear();
		for (final JButton btt : this.segmRemove_btt.values()) {
			btt.removeActionListener(this.removeButt_al);
		}
		this.segmRemove_btt.clear();
		this.segmNames_txt.clear();
		for (final Document doc : this.namesDoc.keySet()) {
			doc.removeDocumentListener(this.changeName_dl);
		}
		this.namesDoc.clear();
		this.newNames.clear();
		this.segmVal_txt.clear();
		for (final Document doc : this.valsDoc.keySet()) {
			doc.removeDocumentListener(this.changeVal_dl);
		}
		this.valsDoc.clear();
		this.newValues.clear();

		for (final JPanel panel : this.segmPanels.values()) {
			panel.removeAll();
		}
		this.segmPanels.clear();
	}

	private void createAndAddSinglePanels() {
		this.actualCounter = -1;
		for (final OmegaSegmentationType segmType : this.types) {
			final String name = segmType.getName();
			final Integer val = segmType.getValue();
			final Color col = segmType.getColor();
			if (this.actualCounter < val) {
				this.actualCounter = val;
			}
			final boolean hasRemoveButton = val != 0;
			this.createAndAddSingleSegmTypePanel(name, col, val,
			        hasRemoveButton, false);
		}
		this.mainPanel.add(this.newSegmType_btt);
	}

	private void populateSegmTypesCombo() {
		this.isPopulating = true;
		for (final OmegaSegmentationTypes segmTypes : this.segmTypesList) {
			this.segmTypesList_cmb.addItem(segmTypes.getName());
			this.segmTypesComboMap.put(segmTypes.getName(), segmTypes);
		}
		this.segmTypesList_cmb.addItem(TMSegmentPreferencesDialog.CREATE_NEW);
		this.isPopulating = false;
		this.segmTypesList_cmb.setSelectedIndex(0);
	}

	@Override
	protected void createAndAddWidgets() {
		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());

		this.segmTypesList_cmb = new JComboBox<String>();
		topPanel.add(this.segmTypesList_cmb, BorderLayout.NORTH);

		this.segmTypesName_txt = new JTextField();
		topPanel.add(this.segmTypesName_txt, BorderLayout.SOUTH);

		this.add(topPanel, BorderLayout.NORTH);

		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		final Dimension butt_dim = new Dimension(50, 20);
		this.newSegmType_btt = new JButton("+");
		this.newSegmType_btt.setPreferredSize(butt_dim);
		this.newSegmType_btt.setSize(butt_dim);
		this.mainPanel.add(this.newSegmType_btt);

		// this.createAndAddSinglePanels();

		final JScrollPane mainScrollPane = new JScrollPane(this.mainPanel);
		this.add(mainScrollPane, BorderLayout.CENTER);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		this.cancel_btt = new JButton("Cancel");
		this.cancel_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.cancel_btt.setSize(OmegaConstants.BUTTON_SIZE);
		bottomPanel.add(this.cancel_btt);

		this.apply_btt = new JButton("Apply");
		this.apply_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.apply_btt.setSize(OmegaConstants.BUTTON_SIZE);
		bottomPanel.add(this.apply_btt);
		this.apply_btt.setEnabled(false);

		this.close_btt = new JButton("Close");
		this.close_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.close_btt.setSize(OmegaConstants.BUTTON_SIZE);
		bottomPanel.add(this.close_btt);

		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void createAndAddSingleSegmTypePanel(final String s, final Color c,
	        final Integer val, final boolean hasRemoveButton,
	        final boolean isPanelToAdd) {
		final Dimension text_dim = new Dimension(200, 20);
		final Dimension val_dim = new Dimension(40, 20);
		final Dimension c_butt_dim = new Dimension(20, 20);
		final Dimension rem_butt_dim = new Dimension(50, 20);

		final JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.segmPanels.put(s, panel);

		final JTextField name_txt = new JTextField(s);
		name_txt.setPreferredSize(text_dim);
		name_txt.setSize(text_dim);
		panel.add(name_txt);
		this.segmNames_txt.put(s, name_txt);
		name_txt.getDocument().addDocumentListener(
		        this.getChangeNameDocumentListener());
		this.namesDoc.put(name_txt.getDocument(), name_txt);

		final JTextField val_txt = new JTextField(String.valueOf(val));
		val_txt.setPreferredSize(val_dim);
		val_txt.setSize(val_dim);
		panel.add(val_txt);
		this.segmVal_txt.put(s, val_txt);
		if (hasRemoveButton) {
			val_txt.getDocument().addDocumentListener(
			        this.getChangeValDocumentListener());
		} else {
			val_txt.setEnabled(false);
		}
		this.valsDoc.put(val_txt.getDocument(), val_txt);
		val_txt.setEnabled(false);

		final JButton butt = new JButton();
		butt.setPreferredSize(c_butt_dim);
		butt.setSize(c_butt_dim);
		butt.setContentAreaFilled(false);
		this.setButtonColor(butt, c);
		panel.add(butt);
		this.segmColors_btt.put(s, butt);
		butt.addActionListener(this.getColorButtActionListener());

		// TODO find a way to check the string not hardcoded
		if (hasRemoveButton) {
			final JButton rem_btt = new JButton("-");
			rem_btt.setPreferredSize(rem_butt_dim);
			rem_btt.setSize(rem_butt_dim);
			panel.add(rem_btt);
			this.segmRemove_btt.put(s, rem_btt);
			if (isPanelToAdd) {
				this.panelsToAdd.put(panel, s);
			}
			rem_btt.addActionListener(this.getRemoveButtActionListener());
		}

		if (isPanelToAdd) {
			panel.setBackground(Color.green);
		}
		this.mainPanel.add(panel);
	}

	private void setButtonColor(final JButton butt, final Color c) {
		final Dimension size = butt.getPreferredSize();
		final BufferedImage image = new BufferedImage(size.width, size.height,
		        BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2d = image.createGraphics();
		g2d.setColor(c);
		g2d.fillRect(0, 0, size.width, size.height);
		g2d.dispose();
		final ImageIcon icon = new ImageIcon(image);
		butt.setIcon(icon);
	}

	@Override
	protected void addListeners() {
		this.segmTypesList_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMSegmentPreferencesDialog.this.handleSegmTypesSelection();
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				TMSegmentPreferencesDialog.this.resizePanels();
			}
		});
		this.newSegmType_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMSegmentPreferencesDialog.this.createNewSegmType();
			}
		});
		this.cancel_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMSegmentPreferencesDialog.this.cancel();
			}
		});
		this.apply_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMSegmentPreferencesDialog.this.apply();
			}
		});
		this.close_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMSegmentPreferencesDialog.this.close();
			}
		});
		this.segmTypesName_txt.getDocument().addDocumentListener(
		        new DocumentListener() {
			        @Override
			        public void removeUpdate(final DocumentEvent e) {
				        TMSegmentPreferencesDialog.this.changeSegmTypesName();
			        }

			        @Override
			        public void insertUpdate(final DocumentEvent e) {
				        TMSegmentPreferencesDialog.this.changeSegmTypesName();
			        }

			        @Override
			        public void changedUpdate(final DocumentEvent e) {
			        }
		        });
	}

	private void handleSegmTypesSelection() {
		if (this.isPopulating)
			return;
		final String s = (String) this.segmTypesList_cmb.getSelectedItem();
		if (s.equals(TMSegmentPreferencesDialog.CREATE_NEW)) {
			this.resetSegmentationTypes();
			this.oldSegmTypes = this.actualSegmTypes;
			this.actualSegmTypes = null;
		} else {
			final OmegaSegmentationTypes segmTypes = this.segmTypesComboMap
			        .get(s);
			this.oldSegmTypes = this.actualSegmTypes;
			this.actualSegmTypes = segmTypes;
			this.selectSegmentationTypes(segmTypes);
		}
		this.apply_btt.setEnabled(true);
	}

	private DocumentListener getChangeValDocumentListener() {
		if (this.changeVal_dl == null) {
			this.changeVal_dl = new DocumentListener() {
				@Override
				public void removeUpdate(final DocumentEvent evt) {
					final JTextField txt = TMSegmentPreferencesDialog.this.valsDoc
					        .get(evt.getDocument());
					TMSegmentPreferencesDialog.this.changeSegmVal(txt);
				}

				@Override
				public void insertUpdate(final DocumentEvent evt) {
					final JTextField txt = TMSegmentPreferencesDialog.this.valsDoc
					        .get(evt.getDocument());
					TMSegmentPreferencesDialog.this.changeSegmVal(txt);
				}

				@Override
				public void changedUpdate(final DocumentEvent evt) {
				}
			};
		}
		return this.changeVal_dl;
	}

	private DocumentListener getChangeNameDocumentListener() {
		if (this.changeName_dl == null) {
			this.changeName_dl = new DocumentListener() {
				@Override
				public void removeUpdate(final DocumentEvent evt) {
					final JTextField txt = TMSegmentPreferencesDialog.this.namesDoc
					        .get(evt.getDocument());
					TMSegmentPreferencesDialog.this.changeSegmName(txt);
				}

				@Override
				public void insertUpdate(final DocumentEvent evt) {
					final JTextField txt = TMSegmentPreferencesDialog.this.namesDoc
					        .get(evt.getDocument());
					TMSegmentPreferencesDialog.this.changeSegmName(txt);
				}

				@Override
				public void changedUpdate(final DocumentEvent evt) {
				}
			};
		}
		return this.changeName_dl;
	}

	private ActionListener getRemoveButtActionListener() {
		if (this.removeButt_al == null) {
			this.removeButt_al = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt) {
					TMSegmentPreferencesDialog.this
					        .removeSegmType((JButton) evt.getSource());
				}
			};
		}
		return this.removeButt_al;
	}

	private ActionListener getColorButtActionListener() {
		if (this.colorButt_al == null) {
			this.colorButt_al = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt) {
					TMSegmentPreferencesDialog.this
					        .changeSegmColor((JButton) evt.getSource());
				}
			};
		}
		return this.colorButt_al;
	}

	private void changeSegmColor(final JButton button) {
		String segmName = null;
		for (final String s : this.segmColors_btt.keySet()) {
			final JButton btt = this.segmColors_btt.get(s);
			if (btt.equals(button)) {
				segmName = s;
			}
		}

		if (segmName == null)
			// TODO THROW ERROR
			return;

		final OmegaSegmentationType actualSegmType = this
		        .getSegmentationType(segmName);
		if (actualSegmType == null)
			// TODO THROW ERROR
			return;

		final StringBuffer buf = new StringBuffer();
		buf.append("Choose color for ");
		buf.append(segmName);
		final Color originalColor = actualSegmType.getColor();

		Color c = OmegaColorManagerUtilities.openPaletteColor(this.mainPanel,
		        buf.toString(), originalColor);
		if ((c == null) || (c == originalColor)) {
			c = originalColor;
		} else {
			this.isSegmTypesChanged = true;
			this.apply_btt.setEnabled(true);
		}

		actualSegmType.setColor(c);
		this.setButtonColor(button, c);
		this.resizePanels();
	}

	private void createNewSegmType() {
		final Color c = Color.black;
		final Integer val = ++this.actualCounter;
		final String name = TMSegmentPreferencesDialog.NEW_TYPE_NAME + " "
		        + this.actualCounter;
		final OmegaSegmentationType newSegmType = new OmegaSegmentationType(
		        name, val, c);
		this.types.add(newSegmType);

		this.mainPanel.remove(this.newSegmType_btt);
		this.createAndAddSingleSegmTypePanel(name, c, val, true, true);
		this.mainPanel.add(this.newSegmType_btt);

		this.isSegmTypesChanged = true;
		this.apply_btt.setEnabled(true);
		this.resizePanels();
	}

	private void removeSegmType(final JButton button) {
		String segmName = null;
		for (final String s : this.segmRemove_btt.keySet()) {
			final JButton rem_btt = this.segmRemove_btt.get(s);
			if (rem_btt.equals(button)) {
				segmName = s;
			}
		}
		if (segmName == null)
			// TODO THROW ERROR
			return;

		final JPanel panelToRemove = this.segmPanels.get(segmName);

		panelToRemove.setEnabled(false);
		panelToRemove.setBackground(Color.red);

		this.panelsToRemove.put(panelToRemove, segmName);

		this.isSegmTypesChanged = true;
		this.apply_btt.setEnabled(true);
		this.resizePanels();
	}

	private void changeSegmTypesName() {
		this.isSegmTypesChanged = true;
		this.apply_btt.setEnabled(true);
		this.redraw();
	}

	private void changeSegmName(final JTextField textField) {
		// TODO trying to resolve multiple call!
		String segmOldName = null;
		for (final String s : this.segmNames_txt.keySet()) {
			final JTextField txt = this.segmNames_txt.get(s);
			if (txt.equals(textField)) {
				segmOldName = s;
			}
		}
		if (segmOldName == null)
			// TODO throw error
			return;

		final String segmNewName = textField.getText();
		this.newNames.put(segmOldName, segmNewName);

		this.isSegmTypesChanged = true;
		this.apply_btt.setEnabled(true);
		this.redraw();
	}

	private void changeSegmVal(final JTextField textField) {
		// TODO trying to resolve multiple call!
		String segmName = null;
		for (final String s : this.segmVal_txt.keySet()) {
			final JTextField txt = this.segmVal_txt.get(s);
			if (txt.equals(textField)) {
				segmName = s;
				break;
			}
		}
		if (segmName == null)
			// TODO throw error
			return;

		final String segmNewVals = textField.getText();
		this.newValues.put(segmName, segmNewVals);

		this.isSegmTypesChanged = true;
		this.apply_btt.setEnabled(true);
		this.redraw();
	}

	private void cancel() {
		final Color color = UIManager.getColor("Panel.background");
		if (this.actualSegmTypes != null) {
			this.segmTypesName_txt.setText(this.actualSegmTypes.getName());
		}

		for (final JPanel panel : this.panelsToRemove.keySet()) {
			panel.setBackground(color);
			panel.setEnabled(true);
		}
		this.panelsToRemove.clear();
		for (final JPanel panel : this.panelsToAdd.keySet()) {
			final String segmName = this.panelsToAdd.get(panel);
			this.removeSegmTypeEntries(segmName);
			this.mainPanel.remove(panel);
		}
		this.panelsToAdd.clear();

		for (final String name : this.newValues.keySet()) {
			final JTextField txt = this.segmVal_txt.get(name);
			final OmegaSegmentationType segmType = this
			        .getSegmentationType(name);
			final Integer val = segmType.getValue();
			txt.setText(String.valueOf(val));
		}
		this.newValues.clear();
		for (final String oldName : this.newNames.keySet()) {
			final JTextField txt = this.segmNames_txt.get(oldName);
			txt.setText(oldName);
		}
		this.newNames.clear();

		if (this.oldSegmTypes != null) {
			this.actualSegmTypes = this.oldSegmTypes;
		}

		this.isSegmTypesChanged = false;
		this.apply_btt.setEnabled(false);
		this.redraw();
	}

	private Integer getValueOrError(final String segmName,
	        final String segmNewVals) {
		Integer segmNewVal = null;
		try {
			segmNewVal = Integer.valueOf(segmNewVals);
		} catch (final NumberFormatException ex) {
			// TODO do nothing
		}
		String error = null;
		if (segmNewVal == null) {
			error = "<html>Error in " + segmName + ".<br>" + segmNewVals
			        + " is not a possible value.<html>";
		} else if (segmNewVal == 0) {
			error = "<html>Error in " + segmName + ".<br>"
			        + "0 is a reserved value.<html>";
		}

		if (error != null) {
			final String title = "Change segmentation types value error";
			final GenericMessageDialog dialog = new GenericMessageDialog(
			        this.getParentContainer(), title, error, true);
			dialog.enableClose();
			dialog.setVisible(true);
		}
		return segmNewVal;
	}

	private String getNameOrError() {
		final String name = this.segmTypesName_txt.getText();
		String error = null;

		if (this.actualSegmTypes == null) {
			if (name.isEmpty()) {
				error = "Cannot create a new segmentation types without a name";
			} else if (name.equals(OmegaSegmentationTypes.DEFAULT_NAME)) {
				error = "Cannot create a new segmentation with the default name";
			}
		} else {
			for (final OmegaSegmentationTypes segmTypes : this.segmTypesList) {
				if (!segmTypes.equals(this.actualSegmTypes)
				        && name.equals(segmTypes.getName())) {
					error = "The choosen name is already present for another segmentation";
					break;
				}
			}
		}

		if (error != null) {
			final String title = "Create new segmentation types error";
			final GenericMessageDialog dialog = new GenericMessageDialog(
			        this.getParentContainer(), title, error, true);
			dialog.enableClose();
			dialog.setVisible(true);
			return null;
		}
		return name;
	}

	private String getSegmNameOrError(final String oldName) {
		final String newName = this.newNames.get(oldName);
		for (final OmegaSegmentationType segmType : this.types) {
			if (!oldName.equals(segmType.getName())
			        && newName.equals(segmType.getName())) {
				final String title = "Add new segmentation type error";
				final String error = newName + " is already present.";
				final GenericMessageDialog dialog = new GenericMessageDialog(
				        this.getParentContainer(), title, error, true);
				dialog.enableClose();
				dialog.setVisible(true);
				return null;
			}
		}
		return newName;
	}

	private boolean areSegmentationColorsOrValuesChanged() {
		if (this.actualSegmTypes.getTypes().size() != this.types.size())
			return true;
		for (final OmegaSegmentationType segmType : this.actualSegmTypes
		        .getTypes()) {
			final String name = segmType.getName();
			final Integer val = segmType.getValue();
			final Color col = segmType.getColor();
			for (final OmegaSegmentationType segmType2 : this.types) {
				final String name2 = segmType2.getName();
				final Integer val2 = segmType2.getValue();
				final Color col2 = segmType2.getColor();
				if ((val == val2)
				        && (!name.equals(name2)
				                || (col.getRed() != col2.getRed())
				                || (col.getBlue() != col2.getBlue()) || (col
				                .getGreen() != col2.getGreen())))
					return true;
			}
		}
		return false;
	}

	private void apply() {
		final String segmTypesName = this.getNameOrError();
		if (segmTypesName == null)
			return;
		for (final String s : this.newValues.keySet()) {
			final String vals = this.newValues.get(s);
			final Integer val = this.getValueOrError(s, vals);
			if (val == null)
				return;
		}
		for (final String oldName : this.newNames.keySet()) {
			final String newName = this.getSegmNameOrError(oldName);
			if (newName == null)
				return;
		}

		final Color color = UIManager.getColor("Panel.background");
		for (final JPanel panel : this.panelsToRemove.keySet()) {
			final String segmName = this.panelsToRemove.get(panel);
			this.removeSegmTypeEntries(segmName);
			this.mainPanel.remove(panel);
		}
		this.panelsToRemove.clear();
		for (final JPanel panel : this.panelsToAdd.keySet()) {
			panel.setBackground(color);
		}
		this.panelsToAdd.clear();

		for (final String s : this.newValues.keySet()) {
			this.getSegmentationType(s);
			final String vals = this.newValues.get(s);
			final Integer val = Integer.valueOf(vals);
			System.out.println(val);
			// segmType.setValue(val);
		}
		this.newValues.clear();
		for (final String oldName : this.newNames.keySet()) {
			this.updateSegmTypeEntry(oldName);
		}
		this.newNames.clear();

		if (this.actualSegmTypes != null) {
			final String segmTypesOldName = this.actualSegmTypes.getName();
			if (!segmTypesOldName.equals(segmTypesName)) {
				this.actualSegmTypes.setName(segmTypesName);
				this.isPopulating = true;
				final int index = this.segmTypesList_cmb.getSelectedIndex();
				this.segmTypesList_cmb.removeItemAt(index);
				this.segmTypesComboMap.remove(this.oldSegmTypes);
				this.segmTypesList_cmb.insertItemAt(segmTypesName, index);
				this.segmTypesComboMap.put(segmTypesName, this.actualSegmTypes);
				this.segmTypesList_cmb.setSelectedIndex(index);
				this.isPopulating = false;
			}
			if (this.areSegmentationColorsOrValuesChanged()) {
				this.actualSegmTypes.setNewTypes(this.types);
			}
		} else {
			this.actualSegmTypes = new OmegaSegmentationTypes(segmTypesName,
			        this.types);
			this.segmTypesList.add(this.actualSegmTypes);
			this.isPopulating = true;
			final int index = this.segmTypesList_cmb.getItemCount() - 1;
			this.segmTypesList_cmb.insertItemAt(segmTypesName, index);
			this.segmTypesComboMap.put(segmTypesName, this.actualSegmTypes);
			this.segmTypesList_cmb.setSelectedIndex(index);
			this.isPopulating = false;
		}
		this.oldSegmTypes = null;

		this.isSegmTypesChanged = false;
		this.apply_btt.setEnabled(false);
		this.redraw();
	}

	private void close() {
		if (this.isSegmTypesChanged) {
			this.cancel();
		}
		this.pluginPanel.handleSegmTypesChanged();
	}

	private void updateSegmTypeEntry(final String oldName) {
		final String newName = this.newNames.get(oldName);
		final JButton color_btt = this.segmColors_btt.get(oldName);
		this.segmColors_btt.remove(oldName);
		this.segmColors_btt.put(newName, color_btt);
		final JTextField name_txt = this.segmNames_txt.get(oldName);
		this.segmNames_txt.remove(oldName);
		this.segmNames_txt.put(newName, name_txt);
		final JTextField val_txt = this.segmVal_txt.get(oldName);
		this.segmVal_txt.remove(oldName);
		this.segmVal_txt.put(newName, val_txt);
		final JButton rem_btt = this.segmRemove_btt.get(oldName);
		if (rem_btt != null) {
			this.segmRemove_btt.remove(oldName);
			this.segmRemove_btt.put(newName, rem_btt);
		}

		final OmegaSegmentationType segmType = this
		        .getSegmentationType(oldName);
		segmType.setName(newName);
	}

	private void removeSegmTypeEntries(final String segmName) {
		this.segmPanels.remove(segmName);
		final JButton col_btt = this.segmColors_btt.get(segmName);
		col_btt.removeActionListener(this.colorButt_al);
		this.segmColors_btt.remove(segmName);
		final JTextField name_txt = this.segmNames_txt.get(segmName);
		name_txt.getDocument().removeDocumentListener(this.changeName_dl);
		this.segmNames_txt.remove(segmName);
		final JTextField val_txt = this.segmVal_txt.get(segmName);
		val_txt.getDocument().removeDocumentListener(this.changeVal_dl);
		this.segmVal_txt.remove(segmName);
		final JButton rem_btt = this.segmRemove_btt.get(segmName);
		rem_btt.removeActionListener(this.removeButt_al);
		this.segmRemove_btt.remove(segmName);
		this.newNames.remove(segmName);
		this.newValues.remove(segmName);

		final OmegaSegmentationType segmType = this
		        .getSegmentationType(segmName);
		this.types.remove(segmType);
	}

	private void resizePanels() {
		final int width = this.mainPanel.getWidth();
		int height = this.mainPanel.getHeight();
		final int tmp = (this.types.size() + 2) * 40;
		if (height < tmp) {
			height = tmp;
		}
		final Dimension mainPanel_dim = new Dimension(width, height);
		this.mainPanel.setPreferredSize(mainPanel_dim);
		mainPanel_dim.setSize(mainPanel_dim);
		if (this.segmPanels == null)
			return;
		final Dimension panelSize = new Dimension(width, 40);
		for (final JPanel panel : this.segmPanels.values()) {
			panel.setPreferredSize(panelSize);
			panel.setSize(panelSize);
		}
		this.redraw();
	}

	private void redraw() {
		this.revalidate();
		this.repaint();
	}

	public List<OmegaSegmentationTypes> getSegmentationTypesList() {
		return this.segmTypesList;
	}

	public OmegaSegmentationTypes getActualSegmentationTypes() {
		return this.actualSegmTypes;
	}

	public void setSegmentationTypesList(
	        final List<OmegaSegmentationTypes> segmTypesList,
	        final OmegaSegmentationTypes actualSegmentationTypes) {
		this.segmTypesList = segmTypesList;
		this.populateSegmTypesCombo();
		if (actualSegmentationTypes != null) {
			this.segmTypesList_cmb.setSelectedItem(actualSegmentationTypes
			        .getName());
		}
	}

	public OmegaSegmentationType getSegmentationType(final String name) {
		for (final OmegaSegmentationType segmType : this.types) {
			if (segmType.getName().equals(name))
				return segmType;
		}
		return null;
	}
}
