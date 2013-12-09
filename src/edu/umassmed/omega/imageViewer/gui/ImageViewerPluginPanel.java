package edu.umassmed.omega.imageViewer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import omero.api.RenderingEnginePrx;
import omero.romio.PlaneDef;
import pojos.ImageData;
import pojos.PixelsData;
import sun.awt.image.IntegerInterleavedRaster;

/**
 * Displays the image and controls.
 */
public class ImageViewerPluginPanel extends JPanel implements ChangeListener {

	private static final long serialVersionUID = -2795661944564940446L;

	/** The compression level. */
	private static final float COMPRESSION = 0.5f;

	/** The red mask. */
	private static final int RED_MASK = 0x00ff0000;

	/** The green mask. */
	private static final int GREEN_MASK = 0x0000ff00;

	/** The blue mask. */
	private static final int BLUE_MASK = 0x000000ff;

	/** The RGB masks. */
	private static final int[] RGB = { ImageViewerPluginPanel.RED_MASK,
	        ImageViewerPluginPanel.GREEN_MASK, ImageViewerPluginPanel.BLUE_MASK };

	/** Reference to the rendering engine. */
	private RenderingEnginePrx engine;

	/** The slider to select the z-section. */
	private JSlider zSlider;

	/** The slider to select the z-section. */
	private JSlider tSlider;

	/** The label showing the T value */
	private JLabel jLabeltValue;

	/** The label showing the Z value */
	private JLabel jLabelZValue;

	/** Box indicating to render the image as compressed or not. */
	private JCheckBox compressed;

	/** The image canvas. */
	private ImageViewerCanvasPanel canvas;

	/** The image currently viewed. */
	private ImageData image;

	/** JPanel displaying all the controls **/
	private JPanel jPanelBottom;

	/** JPanel displaying all the available channels **/
	private JPanel jPanelChannels;

	private JScrollPane jScrollPaneImage;

	/** Number of channels of the image **/
	private int channelsNumber;

	/** Checkboxs rappresenting the channels **/
	private JCheckBox[] channels;

	/**
	 * The current maximum Z of the image.
	 */
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

	/**
	 * The current sizeT (i.e.: seconds between frames) of the image (if
	 * present).
	 */

	public RenderingEnginePrx getEngine() {
		return this.engine;
	}

	public void setEngine(final RenderingEnginePrx engine) {
		this.engine = engine;
	}

	public ImageData getImage() {
		return this.image;
	}

	public JSlider gettSlider() {
		return this.tSlider;
	}

	public JSlider getzSlider() {
		return this.zSlider;
	}

	public ImageViewerCanvasPanel getCanvas() {
		return this.canvas;
	}

	public JCheckBox[] getChannels() {
		return this.channels;
	}

	public Double getSizeX() {
		return this.sizeX;
	}

	public Double getSizeY() {
		return this.sizeY;
	}

	/**
	 * Creates a new instance of this Panel.
	 * 
	 * @param reviewFrame
	 *            a reviewFrame object
	 */
	public ImageViewerPluginPanel() {

		this.setBackground(Color.white);

		this.initComponents();
	}

	private void initComponents() {
		this.removeAll();

		this.compressed = new JCheckBox("Compressed Image");
		this.compressed.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				ImageViewerPluginPanel.this.render();
			}
		});

		this.canvas = new ImageViewerCanvasPanel(this);
		this.zSlider = new JSlider();
		this.zSlider.setMinimum(0);
		this.zSlider.setEnabled(false);
		this.zSlider.addChangeListener(this);
		this.tSlider = new JSlider();
		this.tSlider.setMinimum(0);
		this.tSlider.setEnabled(false);
		this.tSlider.addChangeListener(this);

		this.setLayout(new BorderLayout());

		this.jPanelBottom = new JPanel();
		this.jPanelBottom.setLayout(new GridLayout(3, 1));

		// sliders panel
		final JPanel jPanelSliders = new JPanel();

		jPanelSliders.setLayout(new FlowLayout(FlowLayout.LEFT));

		jPanelSliders.add(new JLabel("Z"));
		jPanelSliders.add(this.zSlider);
		this.jLabelZValue = new JLabel();
		jPanelSliders.add(this.jLabelZValue);

		jPanelSliders.add(Box.createHorizontalStrut(5));
		jPanelSliders.add(new JLabel("T"));
		jPanelSliders.add(this.tSlider);

		this.jLabeltValue = new JLabel();
		jPanelSliders.add(this.jLabeltValue);

		this.jPanelBottom.add(jPanelSliders);

		// compressed panel
		final JPanel jPanelCompressed = new JPanel();
		jPanelCompressed.setLayout(new FlowLayout(FlowLayout.LEFT));
		jPanelCompressed.add(this.compressed);
		this.jPanelBottom.add(jPanelCompressed);

		// channels panel
		this.jPanelChannels = new JPanel();
		this.jPanelChannels.setLayout(new FlowLayout(FlowLayout.LEFT));

		this.jScrollPaneImage = new JScrollPane(this.canvas);

		this.add(this.jScrollPaneImage, BorderLayout.CENTER);
		this.add(this.jPanelBottom, BorderLayout.SOUTH);

	}

	/**
	 * Builds the channel component.
	 */
	private void buildChannelsPane(final int n) {
		try {
			this.jPanelChannels.removeAll();
			this.jPanelBottom.remove(this.jPanelChannels);

			this.channels = new JCheckBox[n];

			for (int i = 0; i < n; i++) {
				this.channels[i] = new JCheckBox("Channel " + i);

				this.channels[i].setSelected(true);

				this.channels[i].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						ImageViewerPluginPanel.this.setActiveChannels(e);
					}
				});

				this.jPanelChannels.add(this.channels[i]);
			}

			this.jPanelBottom.add(this.jPanelChannels);
		} catch (final Exception e) {
			System.out.println(e.toString());
		}
	}

	/**
	 * Sets the rendering engine.
	 * 
	 * @param image
	 *            The image.
	 * @param engine
	 *            The engine.
	 */
	public void setRenderingControl(final ImageData image,
	        final RenderingEnginePrx engine) {
		this.engine = engine;
		this.image = image;

		try {
			this.engine.setCompressionLevel(ImageViewerPluginPanel.COMPRESSION);
		} catch (final Exception e) {
			// nothing to do here...
		}

		final PixelsData pixels = image.getDefaultPixels();

		final Dimension d = new Dimension(pixels.getSizeX(), pixels.getSizeY());
		this.canvas.setPreferredSize(d);
		this.canvas.setSize(d);
		this.zSlider.removeChangeListener(this);
		this.tSlider.removeChangeListener(this);
		this.zSlider.setMaximum(pixels.getSizeZ());
		this.zSlider.setEnabled(pixels.getSizeZ() > 1);

		this.currentMaximumTValue = pixels.getSizeT();
		this.currentMaximumZValue = pixels.getSizeZ();

		this.tSlider.setMaximum(this.currentMaximumTValue);
		this.tSlider.setMinimum(1);
		this.tSlider.setEnabled(pixels.getSizeT() > 1);

		// get the sizeZ and the sizeT of the image in order to display them in
		// the sliders
		// cache the double values, so when the user moves the slider only the
		// strings are re-calculated
		this.sizeX = null;
		this.sizeY = null;
		this.sizeZ = null;
		this.sizeT = null;

		try {
			this.sizeX = pixels.getPixelSizeX();
			this.sizeY = pixels.getPixelSizeY();
			this.sizeZ = pixels.getPixelSizeZ();
		} catch (final Exception e) {
		}

		try {
			// final List<IObject> planeInfoObjects = this.reviewFrame
			// .getGateway().loadPlaneInfo(pixels.getId(), 0,
			// this.currentMaximumTValue - 1, 0);
			// final PlaneInfoI pi = (PlaneInfoI) planeInfoObjects.get(0);

			// final RDouble tTemp = pi.getDeltaT();

			// if (tTemp != null) {
			// this.sizeT = tTemp.getValue() / pixels.getSizeT();
			// }
		} catch (final Exception e) {
		}

		try {
			this.zSlider.setValue(engine.getDefaultZ() + 1);
			this.tSlider.setValue(engine.getDefaultT() + 1);

			final String sizes[] = this.createSizesStrings(
			        engine.getDefaultZ() + 1, engine.getDefaultT() + 1);

			this.jLabelZValue.setText(String.format("%d %s / %d",
			        engine.getDefaultZ() + 1, sizes[0],
			        this.currentMaximumZValue));
			this.jLabeltValue.setText(String.format("%d %s / %d",
			        engine.getDefaultT() + 1, sizes[1],
			        this.currentMaximumTValue));

			this.canvas.setCurrentT(engine.getDefaultT() + 1);
		} catch (final Exception e) {
			// nothing to do here
		}
		this.zSlider.addChangeListener(this);
		this.tSlider.addChangeListener(this);

		// number of channels in the image (RGB)
		this.channelsNumber = image.getDefaultPixels().getSizeC();

		this.buildChannelsPane(this.channelsNumber);
		this.render();
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

	/** Renders a plane. */
	public void render() {
		try {
			final PlaneDef pDef = new PlaneDef();

			// time choice (sliding)
			pDef.t = this.engine.getDefaultT();

			// Z-plan choice
			pDef.z = this.engine.getDefaultZ();

			// display the XY plane
			pDef.slice = omero.romio.XY.value;

			// now render the image, possible to render it compressed or not
			// compressed
			BufferedImage img = null;

			final int sizeX = this.image.getDefaultPixels().getSizeX();
			final int sizeY = this.image.getDefaultPixels().getSizeY();

			if (this.compressed.isSelected()) {
				final int[] buf = this.engine.renderAsPackedInt(pDef);
				img = this.createImage(buf, 32, sizeX, sizeY);
			} else {
				final byte[] values = this.engine.renderCompressed(pDef);

				final ByteArrayInputStream stream = new ByteArrayInputStream(
				        values);
				img = ImageIO.read(stream);
				img.setAccelerationPriority(1f);
			}

			this.canvas.setImage(img);
		} catch (final Exception e) {
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
		        ImageViewerPluginPanel.RGB);

		final WritableRaster raster = new IntegerInterleavedRaster(sampleModel,
		        j2DBuf, new Point(0, 0));

		final ColorModel colorModel = new DirectColorModel(bits,
		        ImageViewerPluginPanel.RGB[0], ImageViewerPluginPanel.RGB[1],
		        ImageViewerPluginPanel.RGB[2]);
		final BufferedImage image = new BufferedImage(colorModel, raster,
		        false, null);
		image.setAccelerationPriority(1f);
		return image;
	}

	/**
	 * Set or unset a channel.
	 */
	private void setActiveChannels(final ActionEvent evt) {
		try {
			for (int i = 0; i < this.channelsNumber; i++) {
				if (this.channels[i].isSelected()) {
					this.engine.setActive(i, true);
				} else {
					this.engine.setActive(i, false);
				}
			}
			this.render();
		} catch (final Exception e) {
		}
	}

	/**
	 * Sets the z-section or time-point.
	 */
	@Override
	public void stateChanged(final ChangeEvent e) {
		final Object src = e.getSource();

		final String sizes[] = this.createSizesStrings(this.zSlider.getValue(),
		        this.tSlider.getValue());

		try {
			if (src == this.zSlider) {
				this.engine.setDefaultZ(this.zSlider.getValue() - 1);
			} else if (src == this.tSlider) {
				this.engine.setDefaultT(this.tSlider.getValue() - 1);
				this.canvas.setCurrentT(this.tSlider.getValue());
			}

			this.jLabelZValue.setText(String.format("%d %s / %d",
			        this.engine.getDefaultZ() + 1, sizes[0],
			        this.currentMaximumZValue));
			this.jLabeltValue.setText(String.format("%d %s / %d",
			        this.engine.getDefaultT() + 1, sizes[1],
			        this.currentMaximumTValue));

			this.render();
		} catch (final Exception e2) {

		}
	}
}
