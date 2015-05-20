package edu.umassmed.omega.commons.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaConstantsMathSymbols;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.gui.dialogs.GenericDialog;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;

public class GenericElementDetailsDialog extends GenericDialog {

	private static final long serialVersionUID = 2643754451715371916L;
	private RootPaneContainer parent;

	public static final String WIDTH = "X:";
	public static final String PIXELWIDTH = "Pixel width:";

	public static final String HEIGHT = "Y:";
	public static final String PIXELHEIGHT = "Pixel height:";

	public static final String DEPTH = "Z:";
	public static final String SECTION = "z-section(s)";
	public static final String PIXELDEPTH = "Voxel depth:";

	public static final String TIME = "T:";
	public static final String TIMEPOINT = "timepoint";
	public static final String TIMEP = GenericElementDetailsDialog.TIMEPOINT
			+ "(s)";
	public static final String TIME_INTERVAL = "Time interval:";

	public static final String CHANNEL = "C:";

	public static final String PX = "pixel";
	public static final String UM = OmegaConstantsMathSymbols.MU + "m";
	public static final String SEC = "sec";
	public static final String SEC_T = GenericElementDetailsDialog.SEC + "/"
			+ GenericElementDetailsDialog.TIMEPOINT;
	public static final String CH = "channel(s)";

	private JLabel xPx_lbl, xUm_lbl, yPx_lbl, yUm_lbl, zSec_lbl, zUm_lbl,
	tP_lbl, t_lbl, c_lbl;
	private GenericTextFieldValidable xSize_lbl, ySize_lbl, zSize_lbl,
	        tSize_lbl;

	private JButton save_btt, close_btt;

	private OmegaImage image;

	public GenericElementDetailsDialog(final RootPaneContainer parent) {
		super(parent, "OMEGA - Edit image details", false);

		this.image = null;

		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

	@Override
	public void createAndAddWidgets() {

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(5, 1));

		final Dimension labelSize = new Dimension(80, 20);
		final Dimension valueSize = new Dimension(40, 20);
		final int align = SwingConstants.RIGHT;

		final JPanel widthPanel = new JPanel();
		widthPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JLabel width_lbl1 = new JLabel(GenericElementDetailsDialog.WIDTH);
		width_lbl1.setPreferredSize(labelSize);
		widthPanel.add(width_lbl1);

		this.xPx_lbl = new JLabel(" NA ");
		this.xPx_lbl.setHorizontalAlignment(align);
		this.xPx_lbl.setPreferredSize(valueSize);
		widthPanel.add(this.xPx_lbl);

		final JLabel width_lbl2 = new JLabel(GenericElementDetailsDialog.PX
		        + ", ");
		width_lbl2.setPreferredSize(labelSize);
		widthPanel.add(width_lbl2);

		this.xUm_lbl = new JLabel(" NA ");
		this.xUm_lbl.setHorizontalAlignment(align);
		this.xUm_lbl.setPreferredSize(valueSize);
		widthPanel.add(this.xUm_lbl);

		final JLabel width_lbl3 = new JLabel(GenericElementDetailsDialog.UM
		        + ". ");
		width_lbl3.setPreferredSize(labelSize);
		widthPanel.add(width_lbl3);

		final JLabel width_lbl4 = new JLabel(
		        GenericElementDetailsDialog.PIXELWIDTH);
		width_lbl4.setPreferredSize(labelSize);
		widthPanel.add(width_lbl4);

		this.xSize_lbl = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_DOUBLE);
		this.xSize_lbl.setText(" NA ");
		this.xSize_lbl.setHorizontalAlignment(align);
		this.xSize_lbl.setPreferredSize(valueSize);
		widthPanel.add(this.xSize_lbl);

		final JLabel width_lbl5 = new JLabel(GenericElementDetailsDialog.UM);
		width_lbl5.setPreferredSize(labelSize);
		widthPanel.add(width_lbl5);

		mainPanel.add(widthPanel);

		final JPanel heightPanel = new JPanel();
		heightPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		final JLabel height_lbl1 = new JLabel(GenericElementDetailsDialog.HEIGHT);
		height_lbl1.setPreferredSize(labelSize);
		heightPanel.add(height_lbl1);

		this.yPx_lbl = new JLabel(" NA ");
		this.yPx_lbl.setHorizontalAlignment(align);
		this.yPx_lbl.setPreferredSize(valueSize);
		heightPanel.add(this.yPx_lbl);

		final JLabel height_lbl2 = new JLabel(GenericElementDetailsDialog.PX
		        + ", ");
		height_lbl2.setPreferredSize(labelSize);
		heightPanel.add(height_lbl2);

		this.yUm_lbl = new JLabel(" NA ");
		this.yUm_lbl.setHorizontalAlignment(align);
		this.yUm_lbl.setPreferredSize(valueSize);
		heightPanel.add(this.yUm_lbl);

		final JLabel height_lbl3 = new JLabel(GenericElementDetailsDialog.UM
		        + ". ");
		height_lbl3.setPreferredSize(labelSize);
		heightPanel.add(height_lbl3);

		final JLabel height_lbl4 = new JLabel(
		        GenericElementDetailsDialog.PIXELHEIGHT);
		height_lbl4.setPreferredSize(labelSize);
		heightPanel.add(height_lbl4);

		this.ySize_lbl = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_DOUBLE);
		this.ySize_lbl.setText(" NA ");
		this.ySize_lbl.setHorizontalAlignment(align);
		this.ySize_lbl.setPreferredSize(valueSize);
		heightPanel.add(this.ySize_lbl);

		final JLabel height_lbl5 = new JLabel(GenericElementDetailsDialog.UM);
		height_lbl5.setPreferredSize(labelSize);
		heightPanel.add(height_lbl5);

		mainPanel.add(heightPanel);

		final JPanel depthPanel = new JPanel();
		depthPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		final JLabel depth_lbl1 = new JLabel(GenericElementDetailsDialog.DEPTH);
		depth_lbl1.setPreferredSize(labelSize);
		depthPanel.add(depth_lbl1);

		this.zSec_lbl = new JLabel(" NA ");
		this.zSec_lbl.setHorizontalAlignment(align);
		this.zSec_lbl.setPreferredSize(valueSize);
		depthPanel.add(this.zSec_lbl);

		final JLabel depth_lbl2 = new JLabel(GenericElementDetailsDialog.SECTION
		        + ", ");
		depth_lbl2.setPreferredSize(labelSize);
		depthPanel.add(depth_lbl2);

		this.zUm_lbl = new JLabel(" NA ");
		this.zUm_lbl.setHorizontalAlignment(align);
		this.zUm_lbl.setPreferredSize(valueSize);
		depthPanel.add(this.zUm_lbl);

		final JLabel depth_lbl3 = new JLabel(GenericElementDetailsDialog.UM
		        + ". ");
		depth_lbl3.setPreferredSize(labelSize);
		depthPanel.add(depth_lbl3);

		final JLabel depth_lbl4 = new JLabel(
		        GenericElementDetailsDialog.PIXELDEPTH);
		depth_lbl4.setPreferredSize(labelSize);
		depthPanel.add(depth_lbl4);

		this.zSize_lbl = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_DOUBLE);
		this.zSize_lbl.setText(" NA ");
		this.zSize_lbl.setHorizontalAlignment(align);
		this.zSize_lbl.setPreferredSize(valueSize);
		depthPanel.add(this.zSize_lbl);

		final JLabel depth_lbl5 = new JLabel(GenericElementDetailsDialog.UM);
		depth_lbl5.setPreferredSize(labelSize);
		depthPanel.add(depth_lbl5);

		mainPanel.add(depthPanel);

		final JPanel timePanel = new JPanel();
		timePanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		final JLabel time_lbl1 = new JLabel(GenericElementDetailsDialog.TIME);
		time_lbl1.setPreferredSize(labelSize);
		timePanel.add(time_lbl1);

		this.tP_lbl = new JLabel(" NA ");
		this.tP_lbl.setHorizontalAlignment(align);
		this.tP_lbl.setPreferredSize(valueSize);
		timePanel.add(this.tP_lbl);

		final JLabel time_lbl2 = new JLabel(GenericElementDetailsDialog.TIMEP
				+ ", ");
		time_lbl2.setPreferredSize(labelSize);
		timePanel.add(time_lbl2);

		this.t_lbl = new JLabel(" NA ");
		this.t_lbl.setHorizontalAlignment(align);
		this.t_lbl.setPreferredSize(valueSize);
		timePanel.add(this.t_lbl);

		final JLabel time_lbl3 = new JLabel(GenericElementDetailsDialog.SEC
				+ ". ");
		time_lbl3.setPreferredSize(labelSize);
		timePanel.add(time_lbl3);

		final JLabel time_lbl4 = new JLabel(
				GenericElementDetailsDialog.PIXELDEPTH);
		time_lbl4.setPreferredSize(labelSize);
		timePanel.add(time_lbl4);

		this.tSize_lbl = new GenericTextFieldValidable(
		        GenericTextFieldValidable.CONTENT_DOUBLE);
		this.tSize_lbl.setText(" NA ");
		this.tSize_lbl.setHorizontalAlignment(align);
		this.tSize_lbl.setPreferredSize(valueSize);
		timePanel.add(this.tSize_lbl);

		final JLabel time_lbl5 = new JLabel(GenericElementDetailsDialog.SEC_T);
		time_lbl5.setPreferredSize(labelSize);
		timePanel.add(time_lbl5);

		mainPanel.add(timePanel);

		final JPanel channelPanel = new JPanel();
		channelPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		final JLabel channel_lbl1 = new JLabel(
		        GenericElementDetailsDialog.CHANNEL);
		channel_lbl1.setPreferredSize(labelSize);
		channelPanel.add(channel_lbl1);

		this.c_lbl = new JLabel(" NA ");
		this.c_lbl.setHorizontalAlignment(align);
		this.c_lbl.setPreferredSize(valueSize);
		channelPanel.add(this.c_lbl);

		final JLabel channel_lbl2 = new JLabel(GenericElementDetailsDialog.CH
				+ ".");
		channel_lbl2.setPreferredSize(labelSize);
		channelPanel.add(channel_lbl2);

		mainPanel.add(channelPanel);

		this.add(mainPanel, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		this.save_btt = new JButton(OmegaGUIConstants.SAVE);
		this.save_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.save_btt);

		this.close_btt = new JButton(OmegaGUIConstants.MENU_FILE_CLOSE);
		this.close_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.close_btt);

		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void fillFields() {
		if (this.image == null)
			return;
		final OmegaImagePixels pixels = this.image.getDefaultPixels();
		this.xPx_lbl.setText(String.valueOf(pixels.getSizeX()));
		if (pixels.getPixelSizeX() != -1) {
			this.xUm_lbl.setText(String.valueOf(pixels.getSizeX()
					* pixels.getPixelSizeX()));
			this.xSize_lbl.setText(String.valueOf(pixels.getPixelSizeX()));
		} else {
			this.xUm_lbl.setText("");
			this.xSize_lbl.setText("");
		}
		this.yPx_lbl.setText(String.valueOf(pixels.getSizeY()));
		if (pixels.getPixelSizeY() != -1) {
			this.yUm_lbl.setText(String.valueOf(pixels.getSizeY()
					* pixels.getPixelSizeY()));
			this.ySize_lbl.setText(String.valueOf(pixels.getPixelSizeY()));
		} else {
			this.yUm_lbl.setText("");
			this.ySize_lbl.setText("");
		}
		this.zSec_lbl.setText(String.valueOf(pixels.getSizeZ()));
		if (pixels.getPixelSizeZ() != -1) {
			this.zUm_lbl.setText(String.valueOf(pixels.getSizeZ()
					* pixels.getPixelSizeZ()));
			this.zSize_lbl.setText(String.valueOf(pixels.getPixelSizeZ()));
		} else {
			this.zUm_lbl.setText("");
			this.zSize_lbl.setText("");
		}
		this.tP_lbl.setText(String.valueOf(pixels.getSizeT()));
		if (pixels.getPixelSizeZ() != -1) {
			// t_lbl.setText(String.valueOf(pixels.getSizeT() *
			// pixels.getPixelSizeT()));
			// tSize_lbl.setText(String.valueOf(pixels.getPixelSizeT()));
		} else {
			this.t_lbl.setText("");
			this.tSize_lbl.setText("");
		}
		this.c_lbl.setText(String.valueOf(pixels.getSizeC()));
	}

	@Override
	public void addListeners() {
		this.save_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GenericElementDetailsDialog.this.handleSave();
			}
		});
		this.close_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GenericElementDetailsDialog.this.handleClose();
			}
		});
	}

	private void handleSave() {
		if (!this.validateValues())
			return;
		final OmegaImagePixels pixel = this.image.getDefaultPixels();
		final String x = this.xSize_lbl.getText();
		final String y = this.ySize_lbl.getText();
		final String z = this.zSize_lbl.getText();
		final String t = this.tSize_lbl.getText();
		if (!x.isEmpty() && !y.isEmpty()) {
			final double sizeX = Double.valueOf(x);
			pixel.setPixelSizeX(sizeX);
			final double sizeY = Double.valueOf(y);
			pixel.setPixelSizeX(sizeY);
		}
		if (!z.isEmpty()) {
			final double sizeZ = Double.valueOf(z);
			pixel.setPixelSizeX(sizeZ);
		}
		if (!t.isEmpty()) {
			final double sizeT = Double.valueOf(t);
			pixel.setPixelSizeX(sizeT);
		}
	}

	@Override
	public void setVisible(final boolean b) {
		if (!b) {
			this.fillFields();
		}
		super.setVisible(b);
	}

	private void handleClose() {
		this.setVisible(false);
	}

	private boolean validateValues() {
		final String x = this.xSize_lbl.getText();
		final String y = this.ySize_lbl.getText();
		final String z = this.zSize_lbl.getText();
		final String t = this.zSize_lbl.getText();
		if (!x.isEmpty() && !y.isEmpty()) {
			if (!this.xSize_lbl.isContentValidated()) {
				final String error = this.xSize_lbl.getError();
				System.out.println(error);
				return false;
			}
			if (!this.ySize_lbl.isContentValidated()) {
				final String error = this.ySize_lbl.getError();
				System.out.println(error);
				return false;
			}
		} else {
			if (x.isEmpty() && !y.isEmpty()) {

			} else if (y.isEmpty() && !x.isEmpty()) {

			}
		}
		if (!z.isEmpty() && !this.zSize_lbl.isContentValidated()) {
			final String error = this.zSize_lbl.getError();
			System.out.println(error);
			return false;
		}
		if (!t.isEmpty() && !this.tSize_lbl.isContentValidated()) {
			final String error = this.tSize_lbl.getError();
			System.out.println(error);
			return false;
		}
		return true;
	}

	public void updateImage(final OmegaImage img) {
		this.image = img;
		this.fillFields();
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		this.parent = parent;
	}

	@Override
	protected RootPaneContainer getParentContainer() {
		return this.parent;
	}
}
