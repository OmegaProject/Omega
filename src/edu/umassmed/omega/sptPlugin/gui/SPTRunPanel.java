package edu.umassmed.omega.sptPlugin.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.GenericTextFieldValidable;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaImagePixels;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.sptPlugin.SPTConstants;

public class SPTRunPanel extends GenericPanel {

	private static final long serialVersionUID = -2109646064541873817L;

	private static final Dimension VALUE_FIELDS_DIM = new Dimension(45, 20);
	private static final Dimension LBL_FIELDS_DIM = new Dimension(120, 20);

	private GenericTextFieldValidable radius_txtField, cutoff_txtField,
	        percentile_txtField, displacement_txtField, linkrange_txtField,
	        minPoints_txtField;

	private JTextField imgWidthPixels_txtField, imgWidthMicron_txtField,
	        imgWidthPixelSize_txtField;
	private JTextField imgHeightPixels_txtField, imgHeightMicron_txtField,
	        imgHeightPixelSize_txtField;
	private JTextField imgFrames_txtField, imgTime_txtField, imgAvgInterval;
	private JTextField zPlane_txtField, channel_txtField;
	private JTextField minValue_txtField, maxValue_txtField;

	private OmegaGateway gateway;

	public SPTRunPanel(final RootPaneContainer parent,
	        final OmegaGateway gateway) {
		super(parent);

		this.gateway = gateway;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.createAndAddWidgets();
	}

	private void createAndAddWidgets() {
		this.createAndAddImageDetailsPanel();

		final JPanel parametersPanel = new JPanel();
		parametersPanel.setLayout(new GridLayout(1, 2));
		parametersPanel.setBorder(new TitledBorder("Parameters"));

		final JPanel detParamPanel = this.createDetectionParamPanel();
		final JPanel linkParamPanel = this.createLinkingParamPanel();
		parametersPanel.add(detParamPanel);
		parametersPanel.add(linkParamPanel);

		this.add(parametersPanel);

		final JPanel additionalParamPanel = this.createAdditionalParamPanel();

		this.add(additionalParamPanel);
	}

	private void createAndAddImageDetailsPanel() {
		// DetectionPanel
		final JPanel imgDetailsPanel = new JPanel();
		imgDetailsPanel.setLayout(new BoxLayout(imgDetailsPanel,
		        BoxLayout.Y_AXIS));
		imgDetailsPanel.setBorder(new TitledBorder("Image details"));
		// Width
		final JPanel widthPanel = new JPanel();
		widthPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		final JLabel width_lbl1 = new JLabel("Image width (X):");
		width_lbl1.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		widthPanel.add(width_lbl1);
		this.imgWidthPixels_txtField = new JTextField();
		this.imgWidthPixels_txtField.setEditable(false);
		this.imgWidthPixels_txtField
		        .setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		widthPanel.add(this.imgWidthPixels_txtField);

		final JLabel width_lbl2 = new JLabel("pixels,");
		width_lbl2.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		widthPanel.add(width_lbl2);
		this.imgWidthMicron_txtField = new JTextField();
		this.imgWidthMicron_txtField.setEditable(false);
		this.imgWidthMicron_txtField
		        .setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		widthPanel.add(this.imgWidthMicron_txtField);

		final JLabel width_lbl3 = new JLabel("µm, pixel size:");
		width_lbl3.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		widthPanel.add(width_lbl3);
		this.imgWidthPixelSize_txtField = new JTextField();
		this.imgWidthPixelSize_txtField.setEditable(false);
		this.imgWidthPixelSize_txtField
		        .setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		widthPanel.add(this.imgWidthPixelSize_txtField);

		final JLabel width_lbl4 = new JLabel("µm/pixel");
		widthPanel.add(width_lbl4);

		imgDetailsPanel.add(widthPanel);

		final JPanel heightPanel = new JPanel();
		heightPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		final JLabel height_lbl1 = new JLabel("Image width (X):");
		height_lbl1.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		heightPanel.add(height_lbl1);
		this.imgHeightPixels_txtField = new JTextField();
		this.imgHeightPixels_txtField.setEditable(false);
		this.imgHeightPixels_txtField
		        .setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		heightPanel.add(this.imgHeightPixels_txtField);

		final JLabel height_lbl2 = new JLabel("pixels,");
		height_lbl2.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		heightPanel.add(height_lbl2);
		this.imgHeightMicron_txtField = new JTextField();
		this.imgHeightMicron_txtField.setEditable(false);
		this.imgHeightMicron_txtField
		        .setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		heightPanel.add(this.imgHeightMicron_txtField);

		final JLabel height_lbl3 = new JLabel("µm, pixel size:");
		height_lbl3.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		heightPanel.add(height_lbl3);
		this.imgHeightPixelSize_txtField = new JTextField();
		this.imgHeightPixelSize_txtField.setEditable(false);
		this.imgHeightPixelSize_txtField
		        .setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		heightPanel.add(this.imgHeightPixelSize_txtField);

		final JLabel height_lbl4 = new JLabel("µm/pixel");
		heightPanel.add(height_lbl4);

		imgDetailsPanel.add(heightPanel);

		final JPanel timePanel = new JPanel();
		timePanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		final JLabel time_lbl1 = new JLabel("Image frames (T):");
		time_lbl1.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		timePanel.add(time_lbl1);
		this.imgFrames_txtField = new JTextField();
		this.imgFrames_txtField.setEditable(false);
		this.imgFrames_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		timePanel.add(this.imgFrames_txtField);

		final JLabel time_lbl2 = new JLabel("frames,");
		time_lbl2.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		timePanel.add(time_lbl2);
		this.imgTime_txtField = new JTextField();
		this.imgTime_txtField.setEditable(false);
		this.imgTime_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		timePanel.add(this.imgTime_txtField);

		final JLabel time_lbl3 = new JLabel("s, average interval:");
		time_lbl3.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		timePanel.add(time_lbl3);
		this.imgAvgInterval = new JTextField();
		this.imgAvgInterval.setEditable(false);
		this.imgAvgInterval.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		timePanel.add(this.imgAvgInterval);

		final JLabel time_lbl4 = new JLabel("s/frame");
		timePanel.add(time_lbl4);

		imgDetailsPanel.add(timePanel);

		final JPanel imgDetailsSubPanel = new JPanel();
		imgDetailsSubPanel.setLayout(new GridLayout(1, 2));

		final JPanel planesPanel = new JPanel();
		planesPanel.setLayout(new BoxLayout(planesPanel, BoxLayout.Y_AXIS));

		final JPanel planePanel = new JPanel();
		planePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel plane_lbl = new JLabel("Plane(s) (z):");
		plane_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		planePanel.add(plane_lbl);
		this.zPlane_txtField = new JTextField();
		this.zPlane_txtField.setEditable(false);
		this.zPlane_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		planePanel.add(this.zPlane_txtField);

		planesPanel.add(planePanel);

		final JPanel channelPanel = new JPanel();
		channelPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel channel_lbl = new JLabel("Channel(s) (c):");
		channel_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		channelPanel.add(channel_lbl);
		this.channel_txtField = new JTextField();
		this.channel_txtField.setEditable(false);
		this.channel_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		channelPanel.add(this.channel_txtField);

		planesPanel.add(channelPanel);

		imgDetailsSubPanel.add(planesPanel);

		final JPanel valuesPanel = new JPanel();
		valuesPanel.setLayout(new BoxLayout(valuesPanel, BoxLayout.Y_AXIS));

		final JPanel minValPanel = new JPanel();
		minValPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel minValue_lbl = new JLabel("Min value:");
		minValue_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		minValPanel.add(minValue_lbl);
		this.minValue_txtField = new JTextField();
		this.minValue_txtField.setEditable(false);
		this.minValue_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		minValPanel.add(this.minValue_txtField);

		valuesPanel.add(minValPanel);

		final JPanel maxValPanel = new JPanel();
		maxValPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel maxValue_lbl = new JLabel("Max value:");
		maxValue_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		maxValPanel.add(maxValue_lbl);
		this.maxValue_txtField = new JTextField();
		this.maxValue_txtField.setEditable(false);
		this.maxValue_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		maxValPanel.add(this.maxValue_txtField);

		valuesPanel.add(maxValPanel);

		imgDetailsSubPanel.add(valuesPanel);

		imgDetailsPanel.add(imgDetailsSubPanel);

		this.add(imgDetailsPanel);
	}

	private JPanel createDetectionParamPanel() {
		// DetectionPanel
		final JPanel paramDetectionPanel = new JPanel();
		paramDetectionPanel.setLayout(new BoxLayout(paramDetectionPanel,
		        BoxLayout.Y_AXIS));
		paramDetectionPanel.setBorder(new TitledBorder("Detection"));

		// Radius
		final JPanel radiusPanel = new JPanel();
		radiusPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel radius_lbl = new JLabel(SPTConstants.PARAM_RADIUS + ":");
		radius_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		radiusPanel.add(radius_lbl);
		this.radius_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_INT);
		this.radius_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		radiusPanel.add(this.radius_txtField);
		paramDetectionPanel.add(radiusPanel);

		// Cutoff
		final JPanel cutoffPanel = new JPanel();
		cutoffPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel cutoff_lbl = new JLabel(SPTConstants.PARAM_CUTOFF + ":");
		cutoff_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		cutoffPanel.add(cutoff_lbl);
		this.cutoff_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_DOUBLE);
		this.cutoff_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		cutoffPanel.add(this.cutoff_txtField);
		paramDetectionPanel.add(cutoffPanel);

		// Percentile
		final JPanel percentilePanel = new JPanel();
		percentilePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel percentile_lbl = new JLabel(SPTConstants.PARAM_PERCENTILE
		        + ":");
		percentile_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		percentilePanel.add(percentile_lbl);
		this.percentile_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_DOUBLE);
		this.percentile_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		percentilePanel.add(this.percentile_txtField);
		paramDetectionPanel.add(percentilePanel);

		return paramDetectionPanel;
	}

	private JPanel createLinkingParamPanel() {
		// Linking panel
		final JPanel paramLinkingPanel = new JPanel();
		paramLinkingPanel.setLayout(new BoxLayout(paramLinkingPanel,
		        BoxLayout.Y_AXIS));
		paramLinkingPanel.setBorder(new TitledBorder("Linking"));

		// Displacement
		final JPanel displacementPanel = new JPanel();
		displacementPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel displacement_lbl = new JLabel(
		        SPTConstants.PARAM_DISPLACEMENT + ":");
		displacement_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		displacementPanel.add(displacement_lbl);
		this.displacement_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_INT);
		this.displacement_txtField
		        .setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		displacementPanel.add(this.displacement_txtField);
		paramLinkingPanel.add(displacementPanel);

		// Linkrange
		final JPanel linkrangePanel = new JPanel();
		linkrangePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel linkrange_lbl = new JLabel(SPTConstants.PARAM_LINKRANGE
		        + ":");
		linkrange_lbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		linkrangePanel.add(linkrange_lbl);
		this.linkrange_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_INT);
		this.linkrange_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		linkrangePanel.add(this.linkrange_txtField);
		paramLinkingPanel.add(linkrangePanel);

		return paramLinkingPanel;
	}

	private JPanel createAdditionalParamPanel() {
		final JPanel additionaParamPanel = new JPanel();
		additionaParamPanel.setLayout(new BoxLayout(additionaParamPanel,
		        BoxLayout.Y_AXIS));
		additionaParamPanel
		        .setBorder(new TitledBorder("Additional parameters"));

		// Displacement
		final JPanel minPointsPanel = new JPanel();
		minPointsPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel minPointsLbl = new JLabel(SPTConstants.PARAM_MINPOINTS
		        + ":");
		minPointsLbl.setPreferredSize(SPTRunPanel.LBL_FIELDS_DIM);
		minPointsPanel.add(minPointsLbl);
		this.minPoints_txtField = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_INT);
		this.minPoints_txtField.setPreferredSize(SPTRunPanel.VALUE_FIELDS_DIM);
		minPointsPanel.add(this.minPoints_txtField);
		additionaParamPanel.add(minPointsPanel);

		return additionaParamPanel;
	}

	public boolean areParametersValidated() {
		return this.radius_txtField.isContentValidated()
		        && this.cutoff_txtField.isContentValidated()
		        && this.percentile_txtField.isContentValidated()
		        && this.displacement_txtField.isContentValidated()
		        && this.linkrange_txtField.isContentValidated()
		        && this.minPoints_txtField.isContentValidated();
	}

	public String[] getParametersError() {
		final String[] errors = new String[6];
		for (int i = 0; i < 6; i++) {
			errors[i] = null;
		}
		if (!this.radius_txtField.isContentValidated()) {
			errors[0] = SPTConstants.PARAM_RADIUS + ": "
			        + this.radius_txtField.getError();
		}
		if (!this.cutoff_txtField.isContentValidated()) {
			errors[1] = SPTConstants.PARAM_CUTOFF + ": "
			        + this.cutoff_txtField.getError();
		}
		if (!this.percentile_txtField.isContentValidated()) {
			errors[2] = SPTConstants.PARAM_PERCENTILE + ": "
			        + this.percentile_txtField.getError();
		}
		if (!this.displacement_txtField.isContentValidated()) {
			errors[3] = SPTConstants.PARAM_DISPLACEMENT + ": "
			        + this.displacement_txtField.getError();
		}
		if (!this.linkrange_txtField.isContentValidated()) {
			errors[4] = SPTConstants.PARAM_LINKRANGE + ": "
			        + this.linkrange_txtField.getError();
		}

		if (!this.minPoints_txtField.isContentValidated()) {
			errors[5] = SPTConstants.PARAM_MINPOINTS + ": "
			        + this.minPoints_txtField.getError();
		}
		return errors;
	}

	public void updateImageFields(final OmegaImage image) {
		final OmegaImagePixels pixels = image.getDefaultPixels();

		final int sizeX = pixels.getSizeX();
		final double pixelsSizeX = pixels.getPixelSizeX();
		this.imgWidthPixels_txtField.setText(String.valueOf(sizeX));
		this.imgWidthMicron_txtField.setText(String
		        .valueOf(pixelsSizeX * sizeX));
		this.imgWidthPixelSize_txtField.setText(String.valueOf(pixelsSizeX));

		final int sizeY = pixels.getSizeY();
		final double pixelsSizeY = pixels.getPixelSizeY();
		this.imgHeightPixels_txtField.setText(String.valueOf(sizeY));
		this.imgHeightMicron_txtField.setText(String.valueOf(pixelsSizeY
		        * sizeY));
		this.imgHeightPixelSize_txtField.setText(String.valueOf(pixelsSizeY));

		final int sizeT = pixels.getSizeT();
		final int c = pixels.getSelectedC();
		final int z = pixels.getSelectedZ();

		final long id = pixels.getElementID();

		final double pixelsSizeT = this.gateway.getTotalT(id, z, sizeT, c);

		this.imgFrames_txtField.setText(String.valueOf(sizeT));
		this.imgTime_txtField.setText(String.valueOf(pixelsSizeT * sizeT));
		this.imgAvgInterval.setText(String.valueOf(pixelsSizeT));

		this.zPlane_txtField.setText(String.valueOf(z));
		this.channel_txtField.setText(String.valueOf(c));

		final int pixelsBits = this.gateway.getByteWidth(id) * 8;
		final int minVal = 0;
		final int maxVal = (int) Math.pow(2, pixelsBits);

		this.minValue_txtField.setText(String.valueOf(minVal));
		this.maxValue_txtField.setText(String.valueOf(maxVal));
	}

	public void updateRunFields(final List<OmegaParameter> parameters) {
		for (final OmegaParameter param : parameters) {
			if (param.getName().equals(SPTConstants.PARAM_RADIUS)) {
				this.radius_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SPTConstants.PARAM_CUTOFF)) {
				this.cutoff_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SPTConstants.PARAM_PERCENTILE)) {
				this.percentile_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SPTConstants.PARAM_DISPLACEMENT)) {
				this.displacement_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SPTConstants.PARAM_LINKRANGE)) {
				this.linkrange_txtField.setText(param.getStringValue());
			} else if (param.getName().equals(SPTConstants.PARAM_MINPOINTS)) {
				this.minPoints_txtField.setText(param.getStringValue());
			} else {
				// TODO gestire errore
			}
		}
	}

	public void updateRunFieldsDefault() {
		this.radius_txtField.setText("3");
		this.cutoff_txtField.setText("3.0");
		this.percentile_txtField.setText("0.1");
		this.displacement_txtField.setText("5");
		this.linkrange_txtField.setText("5");
		this.minPoints_txtField.setText("25");
	}

	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
	}

	public List<OmegaParameter> getSPTParameters() {
		if (!this.areParametersValidated())
			return null;
		final List<OmegaParameter> params = new ArrayList<OmegaParameter>();
		final int radius = Integer.valueOf(this.radius_txtField.getText());
		params.add(new OmegaParameter(UUID.randomUUID()
		        .getMostSignificantBits(), SPTConstants.PARAM_RADIUS, radius));
		final double cutoff = Double.valueOf(this.cutoff_txtField.getText());
		params.add(new OmegaParameter(UUID.randomUUID()
		        .getMostSignificantBits(), SPTConstants.PARAM_CUTOFF, cutoff));
		final double percentile = Double.valueOf(this.percentile_txtField
		        .getText());
		params.add(new OmegaParameter(UUID.randomUUID()
		        .getMostSignificantBits(), SPTConstants.PARAM_PERCENTILE,
		        percentile));
		final int linkrange = Integer
		        .valueOf(this.linkrange_txtField.getText());
		params.add(new OmegaParameter(UUID.randomUUID()
		        .getMostSignificantBits(), SPTConstants.PARAM_LINKRANGE,
		        linkrange));
		final double displacement = Double.valueOf(this.displacement_txtField
		        .getText());
		params.add(new OmegaParameter(UUID.randomUUID()
		        .getMostSignificantBits(), SPTConstants.PARAM_DISPLACEMENT,
		        displacement));
		final int minPoints = Integer
		        .valueOf(this.minPoints_txtField.getText());
		params.add(new OmegaParameter(UUID.randomUUID()
		        .getMostSignificantBits(), SPTConstants.PARAM_MINPOINTS,
		        minPoints));
		return params;
	}
}
