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
package edu.umassmed.omega.commons.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.OmegaConstants;
import edu.umassmed.omega.commons.OmegaMathSymbols;
import edu.umassmed.omega.commons.StringHelper;
import edu.umassmed.omega.core.gui.OmegaElementImagePanel;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaROI;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

/**
 * Paints the image.
 */
public class GenericImageCanvas extends GenericPanel {
	private static final long serialVersionUID = -2321440745146284043L;

	private static int AXISSPACE = 25;
	/** The parent JPanel. **/
	// private JPanelViewer jPanelViewer = null;
	private final OmegaElementImagePanel parentPanel;
	/** The image to be displayed. **/
	private BufferedImage image;
	/** The image to be displayed (scaled). **/
	private BufferedImage scaledImage;
	/** Image zoom factor. **/
	private double scale;

	/** Trajectories to be drawed (data exploration). **/
	private List<OmegaROI> particles;
	/** Trajectories to be drawed, scaled. **/
	private List<OmegaTrajectory> trajectories;
	/** Color used to draw the trajectories. **/
	private final Color trajectoryColor;
	/**
	 * List of (random) Colors to be used when the user does not specify any
	 * color.
	 */
	private List<Color> trajectoryRandomColors;
	/** Set to true when the random colors are generated. **/
	private boolean randomColorsGenerated;
	private final int trajectoryToDraw;

	/** Graphics2D stroke used. **/
	private final int currentStroke;
	/** Current frame. **/
	private int currentT;
	/** The radius of the pixel **/
	private int radius;

	private boolean isPlaceholderImage;

	/**
	 * Creates a new instance.
	 */
	// final JPanelViewer jPanelViewer
	public GenericImageCanvas(final RootPaneContainer parent,
	        final OmegaElementImagePanel parentPanel) {
		super(parent);

		this.image = null;
		this.scaledImage = null;

		this.radius = OmegaConstants.DRAWING_POINTSIZE;
		this.currentT = 0;
		this.scale = 1.0;
		this.currentStroke = 1;
		this.isPlaceholderImage = true;

		// this.jPanelViewer = jPanelViewer;
		this.parentPanel = parentPanel;

		this.particles = null;
		this.trajectories = null;
		this.trajectoryColor = null;
		this.trajectoryRandomColors = null;
		this.randomColorsGenerated = false;
		this.trajectoryToDraw = -1;

		this.setDoubleBuffered(true);
		this.addMouseListener(new GenericImageCanvasListener(this));
	}

	/** The trajectory to draw (-1 draws everything) **/
	// private int trajectoryToDraw = -1;

	// public JPanelViewer getjPanelViewer() {
	// return this.jPanelViewer;
	// }

	/**
	 * Set the bufferedImage to render
	 * 
	 * @param image
	 */
	public void setImage(final BufferedImage image,
	        final boolean isPlaceholderImage) {
		this.isPlaceholderImage = isPlaceholderImage;
		this.image = image;
		Dimension dim;
		if (this.isPlaceholderImage) {
			dim = new Dimension(this.image.getWidth(), this.image.getHeight());
		} else {
			final int scaledWidth = (int) (image.getWidth() * this.scale);
			final int scaledHeight = (int) (image.getHeight() * this.scale);
			dim = new Dimension(scaledWidth + GenericImageCanvas.AXISSPACE + 5,
			        scaledHeight + GenericImageCanvas.AXISSPACE + 5);
		}
		this.setPreferredSize(dim);
		this.setSize(dim);
		this.repaint();
	}

	// public void setTrajectoryToDraw(final int trajectoryToDraw) {
	// this.trajectoryToDraw = trajectoryToDraw;
	// }

	// @formatter:on

	/**
	 * Set the size of the this JPanel accordingly to the image size and
	 * revalidates the panel.
	 */
	public void callRevalidate() {
		final Dimension newDimension = new Dimension(
		        (int) (this.image.getWidth() * this.scale)
		                + GenericImageCanvas.AXISSPACE + 5,
		        (int) (this.image.getHeight() * this.scale)
		                + GenericImageCanvas.AXISSPACE + 5);
		this.setPreferredSize(newDimension);
		this.revalidate();
	}

	/**
	 * Overridden to paint the image.
	 */
	@Override
	public void paint(final Graphics g) {
		if (this.image == null)
			return;

		// scale image
		if (this.scale != 1.0) {
			this.scaledImage = this.imageZoom(this.image);
		} else {
			this.scaledImage = this.image;
		}

		// this.setDoubleBuffered(true);

		final Graphics2D g2D = (Graphics2D) g;

		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
		        RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		        RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		final int width = this.scaledImage.getWidth();
		final int height = this.scaledImage.getHeight();
		final int scaledWidth = width;// (int) (width * this.scale);
		final int scaledHeight = height;// (int) (height * this.scale);

		g2D.clearRect(0, 0, this.getWidth(), this.getHeight());

		int startX = 0;
		int startY = 0;
		if (this.isPlaceholderImage) {
			startX = (this.getWidth() - width) / 2;
			startY = (this.getHeight() - height) / 2;
		}
		g2D.drawImage(this.scaledImage, null, startX, startY);

		if (!this.isPlaceholderImage) {
			g2D.setColor(Color.BLACK);
			// X
			this.drawArrow(g2D, 0, scaledHeight + GenericImageCanvas.AXISSPACE,
			        scaledWidth, scaledHeight + GenericImageCanvas.AXISSPACE);
			// Y
			g2D.drawLine(scaledWidth + GenericImageCanvas.AXISSPACE, 0,
			        scaledWidth + GenericImageCanvas.AXISSPACE, 10);
			this.drawArrow(g2D, scaledWidth + GenericImageCanvas.AXISSPACE, 30,
			        scaledWidth + GenericImageCanvas.AXISSPACE, scaledHeight);

			// information on X pixels
			if ((this.parentPanel.getSizeX() != null)
			        && (this.parentPanel.getSizeX() > 0.0)) {
				final String xInformation = String.format(
				        "%s %sm",
				        StringHelper.doubleToString(this.parentPanel.getSizeX()
				                * width, 1), OmegaMathSymbols.MU);
				g2D.drawString(xInformation, 0, scaledHeight + 15);
			} else {
				g2D.drawString(String.valueOf(this.image.getWidth()) + " px",
				        0, scaledHeight + 15);
			}

			// information on Y pixels
			if ((this.parentPanel.getSizeY() != null)
			        && (this.parentPanel.getSizeY() > 0.0)) {
				final String yInformation = String.format(
				        "%s %sm",
				        StringHelper.doubleToString(this.parentPanel.getSizeY()
				                * height, 1), OmegaMathSymbols.MU);
				g2D.drawString(yInformation, scaledWidth, 24);
			} else {
				g2D.drawString(String.valueOf(this.image.getHeight()) + " px",
				        scaledWidth, 24);
			}
		}

		final MathContext mc = new MathContext(4, RoundingMode.HALF_UP);

		final double adjusterD = this.scale / 2;
		final int adjuster = new BigDecimal(adjusterD, mc).intValue();

		if (this.particles != null) {
			// set the point size
			// final BigDecimal pointSizeD = new BigDecimal(this.radius
			// * this.scale, mc);
			final double pointSizeD = this.radius * this.scale;
			final int pointSize = new BigDecimal(pointSizeD, mc).intValue();
			final double offsetD = pointSizeD / 2;
			final int offset = new BigDecimal(offsetD, mc).intValue();
			// set the stroke
			g2D.setStroke(new BasicStroke(this.currentStroke));
			for (final OmegaROI roi : this.particles) {
				g2D.setColor(Color.RED);
				// g2D.fill(new
				// Ellipse2D.Double(one.getX()-pointSize/2,
				// one.getY()-pointSize/2, pointSize, pointSize));

				// final BigDecimal xD = new BigDecimal(roi.getX() * this.scale,
				// mc);
				// final BigDecimal yD = new BigDecimal(roi.getY() * this.scale,
				// mc);
				// final int pointSize = pointSizeD.divide(new BigDecimal(2),
				// mc)
				// .intValue();
				// final int x = xD.intValue() - pointSize;
				final double xD = roi.getX() * this.scale;
				final double yD = roi.getY() * this.scale;
				final int roiX = new BigDecimal(xD, mc).intValue();
				final int roiY = new BigDecimal(yD, mc).intValue();
				final int x = (roiX - offset) + adjuster;
				final int y = (roiY - offset) + adjuster;
				// final int y = yD.intValue() (- pointSize;
				// final int x = (int) ((roi.getX() * this.scale) - offsetD);
				// final int y = (int) ((roi.getY() * this.scale) - offsetD);
				g2D.drawOval(x, y, pointSize, pointSize);
			}
		}

		// start drawing trajectories...

		if (this.trajectories != null) {
			// set the stroke
			g2D.setStroke(new BasicStroke(this.currentStroke));
			// set the trajectory color (if we have one), or set random ones
			boolean colorWasChoosen = false;
			if (this.trajectoryColor != null) {
				colorWasChoosen = true;
			} else if (!this.randomColorsGenerated) {
				this.generateRandomColors();
			}
			// draw trajectories
			int currentTrajectoryIndex = 0;
			for (final OmegaTrajectory trajectory : this.trajectories) {
				if ((this.trajectoryToDraw > -1)
				        && (this.trajectoryToDraw != currentTrajectoryIndex)) {
					currentTrajectoryIndex++;
					continue;
				}
				final List<OmegaROI> points = trajectory.getROIs();
				for (int i = 0; i < (points.size() - 1); i++) {
					final OmegaROI one = points.get(i);
					final OmegaROI two = points.get(i + 1);
					// for (int frame = 1; frame <= this.currentT; frame++) {
					// // first point
					// if (one.getFrameIndex() == frame) {
					// set the correct color, the choosen or the random
					// one
					if (colorWasChoosen) {
						g2D.setColor(this.trajectoryColor);
					} else {
						g2D.setColor(this.trajectoryRandomColors
						        .get(currentTrajectoryIndex));
					}

					final double x1D = one.getX() * this.scale;
					final double y1D = one.getY() * this.scale;
					final double x2D = two.getX() * this.scale;
					final double y2D = two.getY() * this.scale;
					final int x1 = new BigDecimal(x1D, mc).intValue()
					        + adjuster;
					final int y1 = new BigDecimal(y1D, mc).intValue()
					        + adjuster;
					final int x2 = new BigDecimal(x2D, mc).intValue()
					        + adjuster;
					final int y2 = new BigDecimal(y2D, mc).intValue()
					        + adjuster;

					g2D.draw(new Line2D.Double(x1, y1, x2, y2));
					// }
					// }
				}
				currentTrajectoryIndex++;
			}
		}
	}

	private void drawArrow(final Graphics2D g, final int x1, final int y1,
	        final int x2, final int y2) {
		final float arrowWidth = 6.0f;
		final float theta = 0.423f;
		final int[] xPoints = new int[3];
		final int[] yPoints = new int[3];
		final float[] vecLine = new float[2];
		final float[] vecLeft = new float[2];
		float fLength;
		float th;
		float ta;
		float baseX, baseY;

		xPoints[0] = x2;
		yPoints[0] = y2;

		// build the line vector
		vecLine[0] = (float) xPoints[0] - x1;
		vecLine[1] = (float) yPoints[0] - y1;

		// build the arrow base vector - normal to the line
		vecLeft[0] = -vecLine[1];
		vecLeft[1] = vecLine[0];

		// setup length parameters
		fLength = (float) Math.sqrt((vecLine[0] * vecLine[0])
		        + (vecLine[1] * vecLine[1]));
		th = arrowWidth / (2.0f * fLength);
		ta = arrowWidth / (2.0f * ((float) Math.tan(theta) / 2.0f) * fLength);

		// find the base of the arrow
		baseX = (xPoints[0] - (ta * vecLine[0]));
		baseY = (yPoints[0] - (ta * vecLine[1]));

		// build the points on the sides of the arrow
		xPoints[1] = (int) (baseX + (th * vecLeft[0]));
		yPoints[1] = (int) (baseY + (th * vecLeft[1]));
		xPoints[2] = (int) (baseX - (th * vecLeft[0]));
		yPoints[2] = (int) (baseY - (th * vecLeft[1]));

		g.drawLine(x1, y1, (int) baseX, (int) baseY);
		g.fillPolygon(xPoints, yPoints, 3);
	}

	private BufferedImage imageZoom(final BufferedImage image) {
		final AffineTransform tx = new AffineTransform();
		tx.scale(this.scale, this.scale);
		final AffineTransformOp op = new AffineTransformOp(tx,
		        AffineTransformOp.TYPE_BILINEAR);
		return op.filter(image, null);
	}

	private void generateRandomColors() {
		final Random random = new Random();

		this.trajectoryRandomColors = new ArrayList<Color>(
		        this.trajectories.size());

		for (int i = 0; i < this.trajectories.size(); i++) {
			final float fr = (random.nextFloat() / 2.0f) + 0.5f;
			final float fg = (random.nextFloat() / 2.0f) + 0.5f;
			final float fb = (random.nextFloat() / 2.0f) + 0.5f;
			this.trajectoryRandomColors.add(new Color(fr, fg, fb));
		}

		this.randomColorsGenerated = true;
	}

	// public void saveImage() {
	// final FileHelper fileChooserHelper = new FileHelper();
	//
	// final String imageDirectory = fileChooserHelper.selectFile(
	// "explorationDir", OmegaConstants.INFO_SELECT_IMAGE_DIRECTOTY,
	// JFileChooser.DIRECTORIES_ONLY);
	//
	// if (imageDirectory != null) {
	// this.saveSingleImage(imageDirectory);
	// }
	// }

	// public synchronized void saveMovie() {
	// final FileHelper fileChooserHelper = new FileHelper();
	//
	// final String imageDirectory = fileChooserHelper.selectFile(
	// "explorationDir", OmegaConstants.INFO_SELECT_IMAGE_DIRECTOTY,
	// JFileChooser.DIRECTORIES_ONLY);
	//
	// if (imageDirectory != null) {
	// synchronized (this) {
	// for (int frame = 1; frame <= this.jPanelViewer.getImage()
	// .getDefaultPixels().getSizeT(); frame++) {
	// this.jPanelViewer.gettSlider().setValue(frame);
	// this.saveSingleImage(imageDirectory);
	// }
	// }
	// }
	// }

	// private void saveSingleImage(final String imageDirectory) {
	// try {
	// final String imageName = StringHelper
	// .removeFileExtension(StringHelper
	// .getImageName(this.jPanelViewer.getImage()
	// .getName()));
	// final String fileName = String.format("%s%s%s_frame_%d.png",
	// imageDirectory, System.getProperty("file.separator"),
	// imageName, this.currentT);
	// ImageIO.write(this.imageScaled, "PNG", new File(fileName));
	// } catch (final IOException e) {
	// JOptionPane.showMessageDialog(null,
	// OmegaConstants.ERROR_SAVE_IMAGE,
	// OmegaConstants.OMEGA_TITLE, JOptionPane.ERROR_MESSAGE);
	// }
	// }

	public double getScale() {
		return this.scale;
	}

	public void setScale(final double scale) {
		this.scale = scale;
	}

	public void setTrajectories(final List<OmegaTrajectory> trajectories) {
		this.trajectories = trajectories;
		this.repaint();
	}

	public void setParticles(final List<OmegaROI> particles) {
		this.particles = particles;
		this.repaint();
	}

	public int getCurrentT() {
		return this.currentT;
	}

	public void setCurrentT(final int currentT) {
		this.currentT = currentT;
	}

	public void setRadius(final int radius) {
		this.radius = radius;
	}
}
