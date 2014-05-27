package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sun.awt.image.IntegerInterleavedRaster;
import edu.umassmed.omega.commons.gui.GenericImageCanvas;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaFrame;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaImagePixels;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;

public class OmegaElementImagePanel extends GenericPanel {

	private static final long serialVersionUID = 7327403424923046398L;

	private OmegaImagePixels pixels;
	private OmegaGateway gateway;
	/** The image canvas. */
	private GenericImageCanvas canvas;

	/** The compression level. */
	private static final float COMPRESSION = 0.5f;
	/** The red mask. */
	private static final int RED_MASK = 0x00ff0000;
	/** The green mask. */
	private static final int GREEN_MASK = 0x0000ff00;
	/** The blue mask. */
	private static final int BLUE_MASK = 0x000000ff;
	/** The RGB masks. */
	private static final int[] RGB = { OmegaElementImagePanel.RED_MASK,
	        OmegaElementImagePanel.GREEN_MASK, OmegaElementImagePanel.BLUE_MASK };
	/** The slider to select the z-section and t-section. */
	private JSlider z_slider, t_slider;
	/** The label showing the Z and T values */
	private JLabel z_label, t_label;
	/** Box indicating to render the image as compressed or not. */
	private JCheckBox compressed;
	/** JPanel displaying all the available channels **/
	private GenericPanel channelsPanel;
	/** Number of channels of the image **/
	private int channelsNumber;
	/** Checkboxs rappresenting the channels **/
	private JCheckBox[] channels;
	/** The current maximum Z of the image. **/
	private int currentMaximumZValue;
	/**
	 * The current sizeX, sizeY, sizeZ (i.e.: micron between pixels/planes) of
	 * the image (if present).
	 */
	private Double sizeX = null;
	private Double sizeY = null;
	private Double sizeZ = null;
	/**
	 * The current maximum T of the image.
	 */
	private int currentMaximumTValue;
	/**
	 * The current sizeT (i.e.: seconds between frames) of the image (if
	 * present).
	 */
	private Double sizeT = null;

	public OmegaElementImagePanel(final RootPaneContainer parent) {
		super(parent);

		this.pixels = null;
		this.gateway = null;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.setBorder(new TitledBorder("Selected element"));

		this.setBackground(Color.white);

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		this.canvas = new GenericImageCanvas(this.getParentContainer(), this);
		this.render();
		final JScrollPane scrollPane = new JScrollPane(this.canvas);
		// scrollPane.setPreferredSize(this.canvas.getPreferredSize());

		final GenericPanel bottomPanel = new GenericPanel(
		        this.getParentContainer());
		bottomPanel.setBorder(new TitledBorder("Image controls"));
		bottomPanel.setLayout(new GridLayout(3, 1));

		// sliders panel
		final GenericPanel slidersPanel = new GenericPanel(
		        this.getParentContainer());
		slidersPanel.setLayout(new GridLayout(3, 1));

		final GenericPanel zSliderPanel = new GenericPanel(
		        this.getParentContainer());
		zSliderPanel.setLayout(new BorderLayout());

		this.z_slider = new JSlider();
		this.z_slider.setMinimum(0);
		this.z_slider.setMaximum(0);
		this.z_slider.setExtent(1);
		this.z_slider.setEnabled(false);
		zSliderPanel.add(new JLabel("Z"), BorderLayout.WEST);
		zSliderPanel.add(this.z_slider, BorderLayout.CENTER);
		this.z_label = new JLabel();
		zSliderPanel.add(this.z_label, BorderLayout.EAST);

		slidersPanel.add(Box.createHorizontalStrut(5));

		final GenericPanel tSliderPanel = new GenericPanel(
		        this.getParentContainer());
		tSliderPanel.setLayout(new BorderLayout());

		this.t_slider = new JSlider();
		this.t_slider.setMinimum(0);
		this.t_slider.setMaximum(0);
		this.t_slider.setExtent(1);
		this.t_slider.setEnabled(false);
		tSliderPanel.add(new JLabel("T"), BorderLayout.WEST);
		tSliderPanel.add(this.t_slider, BorderLayout.CENTER);
		this.t_label = new JLabel();
		tSliderPanel.add(this.t_label, BorderLayout.EAST);

		slidersPanel.add(zSliderPanel);
		slidersPanel.add(tSliderPanel);

		bottomPanel.add(slidersPanel);

		// compressed panel
		final GenericPanel compressionPanel = new GenericPanel(
		        this.getParentContainer());
		compressionPanel.setLayout(new GridLayout(1, 1));

		this.compressed = new JCheckBox("Compressed Image");
		this.compressed.setSelected(false);
		this.compressed.setEnabled(false);

		compressionPanel.add(this.compressed);
		bottomPanel.add(compressionPanel);

		// channels panel
		this.channelsPanel = new GenericPanel(this.getParentContainer());
		this.channelsPanel.setLayout(new GridLayout(1, 1));

		bottomPanel.add(this.channelsPanel);

		this.add(scrollPane, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void addListeners() {
		this.compressed.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (OmegaElementImagePanel.this.compressed.isEnabled()) {
					OmegaElementImagePanel.this.renderImage();
				}
			}
		});
		this.z_slider.addChangeListener(this.createChangeListener());
		this.t_slider.addChangeListener(this.createChangeListener());
	}

	private ChangeListener createChangeListener() {
		final ChangeListener cl = new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent evt) {
				final JSlider slider = (JSlider) evt.getSource();
				if (!slider.isEnabled())
					return;

				final String sizes[] = OmegaElementImagePanel.this
				        .createSizesStrings(
				                OmegaElementImagePanel.this.z_slider.getValue(),
				                OmegaElementImagePanel.this.t_slider.getValue());

				final Long id = OmegaElementImagePanel.this.pixels
				        .getElementID();
				final int z = OmegaElementImagePanel.this.z_slider.getValue();
				final int t = OmegaElementImagePanel.this.t_slider.getValue();

				if (t == 0) {
					System.out.println("ERROR");
				}

				if (slider == OmegaElementImagePanel.this.z_slider) {
					OmegaElementImagePanel.this.gateway.setDefaultZ(id, z - 1);
					OmegaElementImagePanel.this.pixels.setSelectedZ(z - 1);
				} else if (slider == OmegaElementImagePanel.this.t_slider) {
					OmegaElementImagePanel.this.gateway.setDefaultT(id, t - 1);
					OmegaElementImagePanel.this.canvas.setCurrentT(t - 1);
				}

				OmegaElementImagePanel.this.z_label.setText(String.format(
				        "%d %s / %d", z, sizes[0],
				        OmegaElementImagePanel.this.currentMaximumZValue));
				OmegaElementImagePanel.this.t_label.setText(String.format(
				        "%d %s / %d", t, sizes[1],
				        OmegaElementImagePanel.this.currentMaximumTValue));

				OmegaElementImagePanel.this.renderImage();
			}
		};
		return cl;
	}

	/**
	 * Builds the channel component.
	 */
	private void buildChannelsPane(final int n) {
		this.channelsPanel.removeAll();
		this.channelsPanel.setLayout(new GridLayout(n, 1));
		this.channels = new JCheckBox[n];

		for (int i = 0; i < n; i++) {
			this.channels[i] = new JCheckBox("Channel " + i);

			this.channels[i].setSelected(true);

			this.channels[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					OmegaElementImagePanel.this.setActiveChannels(e);
				}
			});

			this.channelsPanel.add(this.channels[i]);
		}

		this.channelsPanel.repaint();
	}

	// final ImageData image, final RenderingEnginePrx engine
	private void setRenderingControl() {
		// this.engine = engine;
		// this.image = image;
		if (this.pixels == null) {
			this.removeRenderingControl();
		} else {
			this.addRenderingControl();
		}

	}

	private void removeRenderingControl() {
		this.compressed.setEnabled(false);
		this.compressed.setSelected(false);

		this.z_slider.setEnabled(false);
		this.z_slider.setMaximum(0);
		this.z_slider.setMinimum(0);
		this.z_slider.setValue(0);

		this.currentMaximumTValue = -1;
		this.currentMaximumZValue = -1;

		this.t_slider.setEnabled(false);
		this.t_slider.setMaximum(0);
		this.t_slider.setMinimum(0);
		this.t_slider.setValue(0);

		this.sizeX = null;
		this.sizeY = null;
		this.sizeZ = null;
		this.sizeT = null;

		this.z_label.setText("");
		this.t_label.setText("");

		// number of channels in the image (RGB)
		this.channelsNumber = 0;

		this.buildChannelsPane(this.channelsNumber);
	}

	private void addRenderingControl() {
		this.compressed.setEnabled(true);
		this.compressed.setSelected(true);
		final Long id = this.pixels.getElementID();

		this.gateway
		        .setCompressionLevel(id, OmegaElementImagePanel.COMPRESSION);

		// final PixelsData pixels = image.getDefaultPixels();
		this.currentMaximumTValue = this.pixels.getSizeT();
		this.currentMaximumZValue = this.pixels.getSizeZ();

		this.z_slider.setMaximum(this.currentMaximumZValue);
		this.z_slider.setMinimum(1);

		this.t_slider.setMaximum(this.currentMaximumTValue);
		this.t_slider.setMinimum(1);

		// get the sizeZ and the sizeT of the image in order to display them in
		// the sliders
		// cache the double values, so when the user moves the slider only the
		// strings are re-calculated
		this.sizeX = this.pixels.getPixelSizeX();
		this.sizeY = this.pixels.getPixelSizeY();
		this.sizeZ = this.pixels.getPixelSizeZ();

		this.sizeT = this.gateway.computeSizeT(id, this.pixels.getSizeT(),
		        this.currentMaximumTValue);

		final int defaultZ = this.gateway.getDefaultZ(id);
		final int defaultT = this.gateway.getDefaultT(id);
		this.z_slider.setValue(defaultZ + 1);
		this.t_slider.setValue(defaultT + 1);

		final String sizes[] = this.createSizesStrings(defaultZ + 1,
		        defaultT + 1);

		this.z_label.setText(String.format("%d %s / %d", defaultZ + 1,
		        sizes[0], this.currentMaximumZValue));
		this.t_label.setText(String.format("%d %s / %d", defaultT + 1,
		        sizes[1], this.currentMaximumTValue));

		this.canvas.setCurrentT(defaultT + 1);

		// number of channels in the image (RGB)
		this.channelsNumber = this.pixels.getSizeC();

		this.buildChannelsPane(this.channelsNumber);

		// Enabled at the end to avoid triggering of listeners
		this.z_slider.setEnabled(this.pixels.getSizeZ() > 1);
		this.t_slider.setEnabled(this.pixels.getSizeT() > 1);
	}

	private String[] createSizesStrings(final int defaultZ, final int defaultT) {
		final String sizes[] = new String[] { "", "" };

		if ((this.sizeZ != null) && (this.sizeZ > 0.0)) {
			sizes[0] = String.format("(%.2f micron)", this.sizeZ * defaultZ);
		}

		if ((this.sizeT != null) && (this.sizeT > 0)) {
			sizes[1] = String.format("(%.2f sec)", this.sizeT * defaultT);
		}

		return sizes;
	}

	private void render() {
		if (this.pixels == null) {
			this.renderNoImage();
		} else {
			this.renderImage();
		}
	}

	private void renderNoImage() {
		final String fileName = "img" + File.separator + "noImage.jpg";
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(fileName));
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}
		this.canvas.setImage(img, true);
	}

	/** Renders a plane. */
	private void renderImage() {
		try {
			final Long id = this.pixels.getElementID();
			// now render the image, possible to render it compressed or not
			// compressed
			BufferedImage img = null;
			final int sizeX = this.pixels.getSizeX();
			final int sizeY = this.pixels.getSizeY();
			if (this.compressed.isSelected()) {
				final int[] buf = this.gateway.renderAsPackedInt(id);
				img = this.createImage(buf, 32, sizeX, sizeY);
			} else {
				final byte[] values = this.gateway.renderCompressed(id);
				final ByteArrayInputStream stream = new ByteArrayInputStream(
				        values);
				img = ImageIO.read(stream);
				img.setAccelerationPriority(1f);
			}
			this.canvas.setImage(img, false);
		} catch (final IOException e) {
			// TODO manage exception
			e.printStackTrace();
		}
	}

	/**
	 * Creates a buffer image from the specified <code>array</code> of integers.
	 * 
	 * @param buf
	 *            The array to handle.
	 * @param bits
	 *            The number of bits in the pixel values.
	 * @param sizeX
	 *            The width (in pixels) of the region of image data described.
	 * @param sizeY
	 *            The height (in pixels) of the region of image data described.
	 * @return See above.
	 */
	private BufferedImage createImage(final int[] buf, final int bits,
	        final int sizeX, final int sizeY) {
		if (buf == null)
			return null;

		final DataBuffer j2DBuf = new DataBufferInt(buf, sizeX * sizeY);

		final SinglePixelPackedSampleModel sampleModel = new SinglePixelPackedSampleModel(
		        DataBuffer.TYPE_INT, sizeX, sizeY, sizeX,
		        OmegaElementImagePanel.RGB);

		final WritableRaster raster = new IntegerInterleavedRaster(sampleModel,
		        j2DBuf, new Point(0, 0));

		final ColorModel colorModel = new DirectColorModel(bits,
		        OmegaElementImagePanel.RGB[0], OmegaElementImagePanel.RGB[1],
		        OmegaElementImagePanel.RGB[2]);
		final BufferedImage image = new BufferedImage(colorModel, raster,
		        false, null);
		image.setAccelerationPriority(1f);
		return image;
	}

	/**
	 * Set or unset a channel.
	 */
	private void setActiveChannels(final ActionEvent evt) {
		int c = 0;
		for (int i = 0; i < this.channelsNumber; i++) {
			final boolean active = this.channels[i].isSelected();
			this.gateway
			        .setActiveChannel(this.pixels.getElementID(), i, active);
			if (active) {
				c++;
			}
		}
		this.pixels.setSelectedC(c);
		this.renderImage();
	}

	public void update(final OmegaElement element, final OmegaGateway gateway) {
		this.gateway = gateway;
		if (element == null) {
			this.pixels = null;
		} else {
			if (element instanceof OmegaImage) {
				final OmegaImage image = (OmegaImage) element;
				this.pixels = image.getPixels().get(0);
			} else if (element instanceof OmegaImagePixels) {
				this.pixels = (OmegaImagePixels) element;
			} else if (element instanceof OmegaFrame) {
				final OmegaFrame frame = (OmegaFrame) element;
				frame.getIndex();
				this.pixels = null;
			} else {
				this.pixels = null;
			}
		}
		this.setRenderingControl();
		this.render();
	}

	public Double getSizeX() {
		return this.sizeX;
	}

	public Double getSizeY() {
		return this.sizeY;
	}
}
