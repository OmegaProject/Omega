package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstantsMathSymbols;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.gui.GenericScrollPane;
import edu.umassmed.omega.commons.gui.GenericSpinner;
import edu.umassmed.omega.commons.utilities.OmegaStringUtilities;

public class OmegaElementRenderingPanel extends GenericScrollPane {
	
	private static final long serialVersionUID = 9110497839969472234L;
	
	private final OmegaSidePanel sidePanel;
	
	private static final float COMPRESSION = 0.5f;
	
	private JPanel sizesPanel, optionsPanel, channelsPanel, tLabelsPanel,
	zLabelsPanel, renderingPanel;
	private JSpinner zControl_spi, tControl_spi;
	private JSlider zControl_sli, tControl_sli;
	private JLabel zControlSize_lbl, tControlSize_lbl,
	zControlPhysicalSize_lbl, tControlPhysicalSize_lbl;
	private JCheckBox compressed;
	private int channelsNumber;
	private JCheckBox[] channels;
	private int currentMaximumZValue, currentMaximumTValue;
	
	private ChangeListener slider_cl, spinner_cl;
	private Double physicalSizeX, physicalSizeY, physicalSizeZ, physicalSizeT;
	
	private boolean isHandlingEvent;
	
	public OmegaElementRenderingPanel(final RootPaneContainer parent,
			final OmegaSidePanel sidePanel) {
		super(parent);
		
		this.sidePanel = sidePanel;
		
		this.physicalSizeX = null;
		this.physicalSizeY = null;
		this.physicalSizeZ = null;
		this.physicalSizeT = null;
		this.slider_cl = null;
		this.spinner_cl = null;
		
		this.isHandlingEvent = false;
		
		this.createAndAddWidgets();
		
		this.addListeners();
	}
	
	private void createAndAddWidgets() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		this.renderingPanel = new JPanel();
		this.renderingPanel.setLayout(new BoxLayout(this.renderingPanel,
				BoxLayout.Y_AXIS));
		
		// sliders panel
		this.sizesPanel = new JPanel();
		this.sizesPanel.setLayout(new GridLayout(2, 1));
		
		final JPanel zPanel = new JPanel();
		zPanel.setLayout(new GridLayout(2, 1));
		zPanel.setBorder(new TitledBorder(
				OmegaGUIConstants.SIDEPANEL_RENDERING_Z));
		
		this.zControl_sli = new JSlider();
		this.zControl_sli.setMinimum(0);
		this.zControl_sli.setMaximum(0);
		this.zControl_sli.setExtent(1);
		this.zControl_sli.setEnabled(false);
		zPanel.add(this.zControl_sli);
		
		final JPanel zSpinnerPanel = new JPanel();
		zSpinnerPanel.setLayout(new GridLayout(1, 2));
		this.zControl_spi = new GenericSpinner();
		this.zControl_spi.setValue(0);
		this.zControl_spi.setEnabled(false);
		this.zControlSize_lbl = new JLabel();
		zSpinnerPanel.add(this.zControl_spi);
		
		this.zLabelsPanel = new JPanel();
		this.zLabelsPanel.setLayout(new BorderLayout());
		this.zControlSize_lbl = new JLabel();
		this.zLabelsPanel.add(this.zControlSize_lbl, BorderLayout.WEST);
		this.zControlPhysicalSize_lbl = new JLabel();
		this.zLabelsPanel.add(this.zControlPhysicalSize_lbl, BorderLayout.EAST);
		
		zSpinnerPanel.add(this.zLabelsPanel);
		zPanel.add(zSpinnerPanel);
		
		final JPanel tPanel = new JPanel();
		tPanel.setLayout(new GridLayout(2, 1));
		tPanel.setBorder(new TitledBorder(
				OmegaGUIConstants.SIDEPANEL_RENDERING_T));
		
		this.tControl_sli = new JSlider();
		this.tControl_sli.setMinimum(0);
		this.tControl_sli.setMaximum(0);
		this.tControl_sli.setExtent(1);
		this.tControl_sli.setEnabled(false);
		tPanel.add(this.tControl_sli);
		
		final JPanel tSpinnerPanel = new JPanel();
		tSpinnerPanel.setLayout(new GridLayout(1, 2));
		this.tControl_spi = new GenericSpinner();
		this.tControl_spi.setValue(0);
		this.tControl_spi.setEnabled(false);
		tSpinnerPanel.add(this.tControl_spi);
		
		this.tLabelsPanel = new JPanel();
		this.tLabelsPanel.setLayout(new BorderLayout());
		this.tControlSize_lbl = new JLabel();
		this.tLabelsPanel.add(this.tControlSize_lbl, BorderLayout.WEST);
		this.tControlPhysicalSize_lbl = new JLabel();
		this.tLabelsPanel.add(this.tControlPhysicalSize_lbl, BorderLayout.EAST);
		
		tSpinnerPanel.add(this.tLabelsPanel);
		tPanel.add(tSpinnerPanel);
		
		this.sizesPanel.add(zPanel);
		this.sizesPanel.add(tPanel);
		
		this.renderingPanel.add(this.sizesPanel);
		
		// compressed panel
		this.optionsPanel = new JPanel();
		this.optionsPanel.setBorder(new TitledBorder(
				OmegaGUIConstants.SIDEPANEL_RENDERING_OPTIONS));
		this.optionsPanel.setLayout(new GridLayout(1, 1));
		
		this.compressed = new JCheckBox(
				OmegaGUIConstants.SIDEPANEL_RENDERING_IMAGE_COMPRESSION);
		this.compressed.setSelected(false);
		this.compressed.setEnabled(false);
		
		this.optionsPanel.add(this.compressed);
		this.renderingPanel.add(this.optionsPanel);
		
		// channels panel
		this.channelsPanel = new JPanel();
		this.channelsPanel.setLayout(new GridLayout(1, 1));
		
		this.renderingPanel.add(this.channelsPanel);
		
		mainPanel.add(this.renderingPanel, BorderLayout.NORTH);
		mainPanel.add(new JLabel(), BorderLayout.CENTER);
		
		this.setViewportView(mainPanel);
	}
	
	private void addListeners() {
		this.compressed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaElementRenderingPanel.this.handleCompressed();
			}
		});
		this.zControl_sli.addChangeListener(this.getSliderListener());
		this.tControl_sli.addChangeListener(this.getSliderListener());
		this.zControl_spi.addChangeListener(this.getSpinnerListener());
		this.tControl_spi.addChangeListener(this.getSpinnerListener());
	}
	
	private void handleCompressed() {
		final boolean oldStatus = this.compressed.isEnabled();
		this.sidePanel.setCompressed(this.compressed.isEnabled());
		try {
			final OmegaGateway gateway = this.sidePanel.getGateway();
			final OmegaImagePixels pixels = this.sidePanel.getImagePixels();
			final Long id = pixels.getOmeroId();
			if (this.compressed.isEnabled()) {
				gateway.setCompressionLevel(id,
						OmegaElementRenderingPanel.COMPRESSION);
			} else {
				gateway.setCompressionLevel(id, 0);
			}
			
		} catch (final Exception ex) {
			this.sidePanel.setCompressed(oldStatus);
			OmegaLogFileManager.appendToCoreLog(ex.getMessage());
		}
	}
	
	/**
	 * Builds the channel component.
	 */
	private void createChannelsPane(final int n) {
		this.renderingPanel.remove(this.channelsPanel);
		this.channelsPanel.removeAll();
		// this.channelsPanel.revalidate();
		// this.channelsPanel.repaint();
		this.channelsPanel.setBorder(null);
		this.channelsPanel.setLayout(new GridLayout(n, 1));
		this.channels = new JCheckBox[n];
		
		for (int i = 0; i < n; i++) {
			final String chanName = this.sidePanel.getImagePixels()
					.getChannelNames().get(i);
			String chan = String.valueOf(i);
			if (chanName != null) {
				chan += ": " + chanName;
			}
			this.channels[i] = new JCheckBox(chan);
			
			this.channels[i].setSelected(true);
			
			this.channels[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt) {
					OmegaElementRenderingPanel.this.setActiveChannels();
				}
			});
			
			this.channelsPanel.add(this.channels[i]);
		}
		if (this.channelsPanel.getComponentCount() > 0) {
			this.channelsPanel.setBorder(new TitledBorder(
					OmegaGUIConstants.SIDEPANEL_RENDERING_C));
		}
		
		final Dimension channelsDim = new Dimension(
				this.channelsPanel.getWidth(), 50 * n);
		this.channelsPanel.setPreferredSize(channelsDim);
		this.channelsPanel.setSize(channelsDim);
		
		this.renderingPanel.add(this.channelsPanel);
	}
	
	private ChangeListener getSliderListener() {
		if (this.slider_cl != null)
			return this.slider_cl;
		this.slider_cl = new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent evt) {
				final JSlider slider = (JSlider) evt.getSource();
				OmegaElementRenderingPanel.this.handleSlider(slider);
			}
		};
		return this.slider_cl;
	}
	
	private ChangeListener getSpinnerListener() {
		if (this.spinner_cl != null)
			return this.spinner_cl;
		this.spinner_cl = new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent evt) {
				final JSpinner spinner = (JSpinner) evt.getSource();
				OmegaElementRenderingPanel.this.handleSpinner(spinner);
			}
		};
		return this.spinner_cl;
	}
	
	private boolean validateSpinner(final JSpinner spinner) {
		int maxVal = -1;
		if (spinner == this.zControl_spi) {
			maxVal = this.currentMaximumZValue;
		} else if (spinner == this.tControl_spi) {
			maxVal = this.currentMaximumTValue;
		}
		final int val = (int) spinner.getValue();
		if (val < 1) {
			spinner.setValue(1);
			return false;
		} else if (val > maxVal) {
			spinner.setValue(maxVal);
			return false;
		}
		return true;
	}
	
	private void handleSpinner(final JSpinner spinner) {
		if (!spinner.isEnabled() || !this.validateSpinner(spinner)
				|| this.isHandlingEvent)
			return;
		final int z = (int) this.zControl_spi.getValue();
		final int t = (int) this.tControl_spi.getValue();
		final String[] sizesLbls = this.createSizesStrings(z, t);
		final String[] physicalSizesLbls = this
				.createPhysicalSizesStrings(z, t);
		if (t == 0) {
			// TODO handle error if needed
			// System.out.println("ERROR");
		}
		if (spinner == this.zControl_spi) {
			final int realZ = z - 1;
			this.sidePanel.setZValues(realZ, false);
			this.isHandlingEvent = true;
			this.zControl_sli.setValue(z);
			this.zControl_sli.revalidate();
			this.zControl_sli.repaint();
			this.isHandlingEvent = false;
			this.zControlSize_lbl.setText(sizesLbls[0]);
			this.zControlPhysicalSize_lbl.setText(physicalSizesLbls[0]);
		} else if (spinner == this.tControl_spi) {
			final int realT = t - 1;
			this.sidePanel.setTValues(realT, false);
			this.isHandlingEvent = true;
			this.tControl_sli.setValue(t);
			this.tControl_sli.revalidate();
			this.tControl_sli.repaint();
			this.isHandlingEvent = false;
			this.tControlSize_lbl.setText(sizesLbls[1]);
			this.tControlPhysicalSize_lbl.setText(physicalSizesLbls[1]);
		}
		this.sidePanel.render();
	}
	
	private void handleSlider(final JSlider slider) {
		if (!slider.isEnabled() || this.isHandlingEvent)
			return;
		final int z = this.zControl_sli.getValue();
		final int t = this.tControl_sli.getValue();
		final String[] sizesLbls = this.createSizesStrings(z, t);
		final String[] physicalSizesLbls = this
				.createPhysicalSizesStrings(z, t);
		if (t == 0) {
			// TODO handle error if needed
			// System.out.println("ERROR");
		}
		if (slider == this.zControl_sli) {
			final int realZ = z - 1;
			this.sidePanel.setZValues(realZ, false);
			this.isHandlingEvent = true;
			this.zControl_spi.setValue(z);
			this.isHandlingEvent = false;
			this.zControlSize_lbl.setText(sizesLbls[0]);
			this.zControlPhysicalSize_lbl.setText(physicalSizesLbls[0]);
		} else if (slider == this.tControl_sli) {
			final int realT = t - 1;
			this.sidePanel.setTValues(realT, false);
			this.isHandlingEvent = true;
			this.tControl_spi.setValue(t);
			this.isHandlingEvent = false;
			this.tControlSize_lbl.setText(sizesLbls[1]);
			this.tControlPhysicalSize_lbl.setText(physicalSizesLbls[1]);
		}
		this.sidePanel.render();
	}
	
	private void addRenderingControl() {
		final OmegaGateway gateway = this.sidePanel.getGateway();
		final OmegaImagePixels pixels = this.sidePanel.getImagePixels();
		final Long id = pixels.getOmeroId();
		this.compressed.setEnabled(true);
		this.compressed.setSelected(true);
		try {
			gateway.setCompressionLevel(id,
					OmegaElementRenderingPanel.COMPRESSION);
		} catch (final Exception ex) {
			OmegaLogFileManager.handleCoreException(ex, false);
			this.compressed.setSelected(false);
		}
		// final PixelsData pixels = image.getDefaultPixels();
		this.currentMaximumTValue = pixels.getSizeT();
		this.currentMaximumZValue = pixels.getSizeZ();
		this.zControl_sli.setMaximum(this.currentMaximumZValue);
		this.zControl_sli.setMinimum(1);
		this.tControl_sli.setMaximum(this.currentMaximumTValue);
		this.tControl_sli.setMinimum(1);
		
		// get the physicalSizeZ and the physicalSizeT of the image in order to
		// display them in the sliders cache the double values, so when the user
		// moves the slider only the strings are re-calculated
		this.physicalSizeX = pixels.getPhysicalSizeX();
		this.physicalSizeY = pixels.getPhysicalSizeY();
		this.physicalSizeZ = pixels.getPhysicalSizeZ();
		try {
			this.physicalSizeT = gateway.computeSizeT(id, pixels.getSizeT(),
					this.currentMaximumTValue);
		} catch (final Exception ex) {
			this.physicalSizeT = null;
			OmegaLogFileManager.handleCoreException(ex, false);
		}
		
		int defaultZ = 0;
		try {
			defaultZ = gateway.getDefaultZ(id);
		} catch (final Exception ex) {
			OmegaLogFileManager.handleCoreException(ex, false);
		}
		int defaultT = 0;
		try {
			defaultT = gateway.getDefaultT(id);
		} catch (final Exception ex) {
			OmegaLogFileManager.handleCoreException(ex, false);
		}
		this.zControl_sli.setValue(defaultZ + 1);
		this.tControl_sli.setValue(defaultT + 1);
		
		this.zControl_spi.setValue(defaultZ + 1);
		this.tControl_spi.setValue(defaultT + 1);
		
		String[] sizesLbl = this.createSizesStrings(this.currentMaximumZValue,
				this.currentMaximumTValue);
		String[] physicalSizesLbl = this.createSizesStrings(
				this.currentMaximumZValue, this.currentMaximumTValue);
		
		Dimension lblDim = this.computeAndSetNeededDimension(
				this.zControlSize_lbl, this.tControlSize_lbl, sizesLbl[0],
				sizesLbl[1]);
		this.zControlSize_lbl.setMinimumSize(lblDim);
		this.tControlSize_lbl.setMinimumSize(lblDim);
		lblDim = this.computeAndSetNeededDimension(
				this.zControlPhysicalSize_lbl, this.tControlPhysicalSize_lbl,
				physicalSizesLbl[0], physicalSizesLbl[1]);
		this.zControlPhysicalSize_lbl.setMinimumSize(lblDim);
		this.tControlPhysicalSize_lbl.setMinimumSize(lblDim);
		
		sizesLbl = this.createSizesStrings(defaultZ + 1, defaultT + 1);
		physicalSizesLbl = this.createPhysicalSizesStrings(defaultZ + 1,
				defaultT + 1);
		this.zControlSize_lbl.setText(sizesLbl[0]);
		this.tControlSize_lbl.setText(sizesLbl[1]);
		this.zControlPhysicalSize_lbl.setText(physicalSizesLbl[0]);
		this.tControlPhysicalSize_lbl.setText(physicalSizesLbl[1]);
		
		this.sidePanel.setZValues(defaultZ, false);
		this.sidePanel.setTValues(defaultT, false);
		// number of channels in the image (RGB)
		this.channelsNumber = pixels.getSizeC();
		this.createChannelsPane(this.channelsNumber);
		// Enabled at the end to avoid triggering of listeners
		final boolean zEnabled = pixels.getSizeZ() > 1;
		final boolean tEnabled = pixels.getSizeT() > 1;
		this.zControl_sli.setEnabled(zEnabled);
		this.tControl_sli.setEnabled(tEnabled);
		this.zControl_spi.setEnabled(zEnabled);
		this.tControl_spi.setEnabled(tEnabled);
	}
	
	private Dimension computeAndSetNeededDimension(final JLabel lbl1,
			final JLabel lbl2, final String s1, final String s2) {
		final Dimension lbl1Dim = OmegaStringUtilities.getStringSize(
				lbl1.getGraphics(), lbl2.getFont(), s1);
		final Dimension lbl2Dim = OmegaStringUtilities.getStringSize(
				lbl2.getGraphics(), lbl2.getFont(), s2);
		Dimension lblDim = null;
		if (lbl1Dim.width > lbl2Dim.width) {
			lblDim = lbl1Dim;
		} else {
			lblDim = lbl2Dim;
		}
		return lblDim;
	}
	
	private void removeRenderingControl() {
		this.compressed.setEnabled(false);
		this.compressed.setSelected(false);
		
		this.zControl_sli.setEnabled(false);
		this.zControl_sli.setMaximum(0);
		this.zControl_sli.setMinimum(0);
		this.zControl_sli.setValue(0);
		
		this.currentMaximumTValue = -1;
		this.currentMaximumZValue = -1;
		
		this.tControl_sli.setEnabled(false);
		this.tControl_sli.setMaximum(0);
		this.tControl_sli.setMinimum(0);
		this.tControl_sli.setValue(0);
		
		this.zControlSize_lbl.setText("");
		this.tControlSize_lbl.setText("");
		
		this.physicalSizeX = null;
		this.physicalSizeY = null;
		this.physicalSizeZ = null;
		this.physicalSizeT = null;
		
		// number of channels in the image (RGB)
		this.channelsNumber = 0;
		this.createChannelsPane(this.channelsNumber);
	}
	
	public void setRenderingControl(final boolean enabled) {
		// this.engine = engine;
		// this.image = image;
		if (!enabled) {
			this.removeRenderingControl();
			// this.removeOverlayControl();
		} else {
			this.addRenderingControl();
			// this.addOverlayControl();
		}
		this.resizePanel(this.getWidth(), this.getHeight());
	}
	
	private String[] createSizesStrings(final int z, final int t) {
		final String sizes[] = new String[] { "", "" };
		StringBuffer buf = new StringBuffer();
		buf.append("/");
		buf.append(this.currentMaximumZValue);
		sizes[0] = buf.toString();
		buf = new StringBuffer();
		buf.append("/");
		buf.append(this.currentMaximumTValue);
		sizes[1] = buf.toString();
		return sizes;
	}
	
	private String[] createPhysicalSizesStrings(final int z, final int t) {
		final String sizes[] = new String[] { "", "" };
		StringBuffer buf = new StringBuffer();
		if ((this.physicalSizeZ != null) && (this.physicalSizeZ > 0.0)) {
			buf.append("(");
			buf.append(String.format("%.2f", this.physicalSizeZ * z));
			buf.append("/");
			buf.append(String.format("%.2f", this.physicalSizeZ
					* this.currentMaximumZValue));
			buf.append(" ");
			buf.append(OmegaConstantsMathSymbols.MU);
			buf.append("m)");
		}
		sizes[0] = buf.toString();
		buf = new StringBuffer();
		if ((this.physicalSizeT != null) && (this.physicalSizeT > 0.0)) {
			buf.append("(");
			buf.append(String.format("%.2f", this.physicalSizeT * t));
			buf.append("/");
			buf.append(String.format("%.2f", this.physicalSizeT
					* this.currentMaximumTValue));
			buf.append(" s)");
		}
		sizes[1] = buf.toString();
		return sizes;
	}
	
	/**
	 * Set or unset a channel.
	 */
	private void setActiveChannels() {
		for (int i = 0; i < this.channelsNumber; i++) {
			final boolean isActive = this.channels[i].isSelected();
			this.sidePanel.setActiveChannel(i, isActive);
		}
		this.sidePanel.render();
	}
	
	public void resizePanel(final int width, final int height) {
		final Dimension sizesDim = new Dimension(width,
				this.sizesPanel.getHeight());
		this.sizesPanel.setPreferredSize(sizesDim);
		this.sizesPanel.setSize(sizesDim);
		final Dimension optionsDim = new Dimension(width,
				this.optionsPanel.getHeight());
		this.optionsPanel.setPreferredSize(optionsDim);
		this.optionsPanel.setSize(optionsDim);
		final Dimension channelsDim = new Dimension(width,
				this.channelsPanel.getHeight());
		this.channelsPanel.setPreferredSize(channelsDim);
		this.channelsPanel.setSize(channelsDim);
		this.revalidate();
		this.repaint();
	}
}
