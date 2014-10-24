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
package edu.umassmed.omega.imageViewer.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.utilities.OmegaStringUtilities;

/**
 * Paints the image.
 */
public class ImageViewerCanvasPanel extends JPanel {
	private static final long serialVersionUID = -2321440745146284043L;

	private static int AXISSPACE = 25;
	/**
	 * The current instance
	 */
	private static ImageViewerCanvasPanel INSTANCE = null;

	/**
	 * The parent JPanel.
	 */
	private ImageViewerPluginPanel jPanelViewer = null;

	/**
	 * The image to be displayed.
	 */
	private BufferedImage image = null;

	/**
	 * The image to be displayed (scaled).
	 */
	private final BufferedImage imageScaled = null;

	/**
	 * Image zoom factor.
	 */
	private double scale = 1.0;

	// /**
	// * Trajectories to be drawed (data exploration).
	// */
	// private final List<Trajectory> trajectories = null;
	//
	// /**
	// * Trajectories to be drawed, scaled.
	// */
	// private final List<Trajectory> trajectoriesScaled = null;

	/**
	 * Color used to draw the trajectories.
	 */
	private Color trajectoryColor = null;

	/**
	 * List of (random) Colors to be used when the user does not specify any
	 * color.
	 */
	private final List<Color> trajectoryRandomColors = null;

	/**
	 * Set to true when the random colors are generated.
	 */
	private final boolean randomColorsGenerated = false;

	/**
	 * Graphics2D stroke used.
	 */
	private final int currentStroke = 1;

	/**
	 * Current frame.
	 */
	private int currentT = 0;

	/**
	 * The radius of the pixel
	 */
	// private int radius = OmegaConstants.DRAWING_POINTSIZE;

	/**
	 * The trajectory to draw (-1 draws everything)
	 */
	private int trajectoryToDraw = -1;

	// @formatter:off
	public static ImageViewerCanvasPanel getINSTANCE() {
		return ImageViewerCanvasPanel.INSTANCE;
	}

	public ImageViewerPluginPanel getjPanelViewer() {
		return this.jPanelViewer;
	}

	public void setImage(final BufferedImage image) {
		this.image = image;
		this.repaint();
	}

	public double getScale() {
		return this.scale;
	}

	public void setScale(final double scale) {
		this.scale = scale;
	}

	// public void setTrajectories(final List<Trajectory> trajectories) {
	// this.trajectories = trajectories;
	// this.randomColorsGenerated = false;
	// }
	//
	// public void setTrajectoriesScaled(final List<Trajectory>
	// trajectoriesScaled) {
	// this.trajectoriesScaled = trajectoriesScaled;
	// }

	public Color getTrajectoryColor() {
		return this.trajectoryColor;
	}

	public void setTrajectoryColor(final Color trajectoryColor) {
		this.trajectoryColor = trajectoryColor;
	}

	public int getCurrentT() {
		return this.currentT;
	}

	public void setCurrentT(final int currentT) {
		this.currentT = currentT;
	}

	// public void setRadius(final int radius) {
	// this.radius = radius;
	// }

	public void setTrajectoryToDraw(final int trajectoryToDraw) {
		this.trajectoryToDraw = trajectoryToDraw;
	}

	// @formatter:on

	/**
	 * Creates a new instance.
	 */
	public ImageViewerCanvasPanel(final ImageViewerPluginPanel jPanelViewer) {
		ImageViewerCanvasPanel.INSTANCE = this;

		this.jPanelViewer = jPanelViewer;

		this.setDoubleBuffered(true);
		this.addMouseListener(new ImageCanvasListener(this));
	}

	/**
	 * Set the size of the this JPanel accordingly to the image size and
	 * revalidates the panel.
	 */
	public void callRevalidate() {
		final Dimension newDimension = new Dimension(
		        (int) (this.image.getWidth() * this.scale)
		                + ImageViewerCanvasPanel.AXISSPACE + 5,
		        (int) (this.image.getHeight() * this.scale)
		                + ImageViewerCanvasPanel.AXISSPACE + 5);
		this.setPreferredSize(newDimension);
		this.revalidate();
	}

	/**
	 * Overridden to paint the image.
	 */
	// @Override
	// public void paint(final Graphics g) {
	// if (this.image == null)
	// return;
	//
	// // scale image
	// this.imageScaled = this.imageZoom(this.image);
	//
	// this.setDoubleBuffered(true);
	//
	// final Graphics2D g2D = (Graphics2D) g;
	//
	// g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	// RenderingHints.VALUE_ANTIALIAS_ON);
	// g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
	// RenderingHints.VALUE_RENDER_QUALITY);
	// g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	// RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	//
	// // can this be handled better?
	// g2D.clearRect(0, 0, this.getWidth(), this.getHeight());
	//
	// g2D.drawImage(this.imageScaled, null, 0, 0);
	//
	// // X
	// this.drawArrow(g2D, 0, (int) (this.image.getHeight() * this.scale)
	// + ImageViewerCanvasPanel.AXISSPACE,
	// (int) (this.image.getWidth() * this.scale),
	// (int) (this.image.getHeight() * this.scale)
	// + ImageViewerCanvasPanel.AXISSPACE);
	// // Y
	// g2D.drawLine((int) (this.image.getWidth() * this.scale)
	// + ImageViewerCanvasPanel.AXISSPACE, 0,
	// (int) (this.image.getWidth() * this.scale)
	// + ImageViewerCanvasPanel.AXISSPACE, 10);
	// this.drawArrow(g2D, (int) (this.image.getWidth() * this.scale)
	// + ImageViewerCanvasPanel.AXISSPACE, 30,
	// (int) (this.image.getWidth() * this.scale)
	// + ImageViewerCanvasPanel.AXISSPACE,
	// (int) (this.image.getHeight() * this.scale));
	//
	// // information on X pixels
	// String xInformation;
	// final int x = (int) (this.image.getWidth() * this.scale);
	// final int y = (int) (this.image.getHeight() * this.scale);
	//
	// if ((this.jPanelViewer.getSizeX() != null)
	// && (this.jPanelViewer.getSizeX() > 0.0)) {
	// xInformation = String.format(
	// "%s %sm",
	// OmegaStringUtilities.DoubleToString(this.jPanelViewer.getSizeX()
	// * this.image.getWidth(), 1), Greeks.MU);
	// g2D.drawString(xInformation, 0, y + 15);
	// } else {
	// g2D.drawString(String.valueOf(this.image.getWidth()) + " px", 0,
	// y + 15);
	// }
	//
	// // information on Y pixels
	// String yInformation;
	//
	// if ((this.jPanelViewer.getSizeY() != null)
	// && (this.jPanelViewer.getSizeY() > 0.0)) {
	// yInformation = String.format(
	// "%s %sm",
	// OmegaStringUtilities.DoubleToString(this.jPanelViewer.getSizeY()
	// * this.image.getHeight(), 1), Greeks.MU);
	// g2D.drawString(yInformation, x, 24);
	// } else {
	// g2D.drawString(String.valueOf(this.image.getHeight()) + " px", x,
	// 24);
	// }
	//
	// // start drawing trajectories...
	//
	// if (this.trajectoriesScaled != null) {
	// // set the point size
	// final int pointSize = (int) (this.radius * this.scale);
	//
	// // set the stroke
	// g2D.setStroke(new BasicStroke(this.currentStroke));
	//
	// // set the trajectory color (if we have one), or set random ones
	// boolean colorWasChoosen = false;
	//
	// if (this.trajectoryColor != null) {
	// colorWasChoosen = true;
	// } else if (!this.randomColorsGenerated) {
	// this.generateRandomColors();
	// }
	//
	// // draw trajectories
	// int currentTrajectoryIndex = 0;
	//
	// for (final Trajectory trajectory : this.trajectoriesScaled) {
	// if ((this.trajectoryToDraw > -1)
	// && (this.trajectoryToDraw != currentTrajectoryIndex)) {
	// currentTrajectoryIndex++;
	// continue;
	// }
	//
	// final List<TPoint> points = trajectory.getPoints();
	//
	// for (int i = 0; i < (points.size() - 1); i++) {
	// final TPoint one = points.get(i);
	// final TPoint two = points.get(i + 1);
	//
	// for (int frame = 1; frame <= this.currentT; frame++) {
	// // first point
	// if (one.getFrame() == 1) {
	// g2D.setColor(Color.YELLOW);
	// // g2D.fill(new
	// // Ellipse2D.Double(one.getX()-pointSize/2,
	// // one.getY()-pointSize/2, pointSize, pointSize));
	// g2D.drawOval((int) one.getX() - (pointSize / 2),
	// (int) one.getY() - (pointSize / 2),
	// pointSize, pointSize);
	// }
	//
	// // lines
	// else if (one.getFrame() == frame) {
	// // set the correct color, the choosen or the random
	// // one
	// if (colorWasChoosen) {
	// g2D.setColor(this.trajectoryColor);
	// } else {
	// g2D.setColor(this.trajectoryRandomColors
	// .get(currentTrajectoryIndex));
	// }
	//
	// g2D.draw(new Line2D.Double(one.getX(), one.getY(),
	// two.getX(), two.getY()));
	// }
	// }
	// }
	// currentTrajectoryIndex++;
	// }
	// }
	// }

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

	// private void generateRandomColors() {
	// final Random random = new Random();
	//
	// this.trajectoryRandomColors = new ArrayList<Color>(
	// this.trajectoriesScaled.size());
	//
	// for (int i = 0; i < this.trajectoriesScaled.size(); i++) {
	// final float fr = (random.nextFloat() / 2.0f) + 0.5f;
	// final float fg = (random.nextFloat() / 2.0f) + 0.5f;
	// final float fb = (random.nextFloat() / 2.0f) + 0.5f;
	// this.trajectoryRandomColors.add(new Color(fr, fg, fb));
	// }
	//
	// this.randomColorsGenerated = true;
	// }
	//
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

	private void saveSingleImage(final String imageDirectory) {
		try {
			final String imageName = OmegaStringUtilities
			        .removeFileExtension(OmegaStringUtilities
			                .getImageName(this.jPanelViewer.getImage()
			                        .getName()));
			final String fileName = String.format("%s%s%s_frame_%d.png",
			        imageDirectory, System.getProperty("file.separator"),
			        imageName, this.currentT);
			ImageIO.write(this.imageScaled, "PNG", new File(fileName));
		} catch (final IOException e) {
			JOptionPane.showMessageDialog(null,
			        OmegaConstants.ERROR_SAVE_IMAGE,
			        OmegaConstants.OMEGA_TITLE, JOptionPane.ERROR_MESSAGE);
		}
	}

	// public void scaleTrajectories() {
	// if (this.trajectories != null) {
	// this.trajectoriesScaled = new ArrayList<Trajectory>();
	//
	// for (final Trajectory trajectory : this.trajectories) {
	// try {
	// this.trajectoriesScaled
	// .add((Trajectory) trajectory.clone());
	// } catch (final CloneNotSupportedException e) {
	// // nothing we can do...
	// }
	// }
	//
	// for (final Trajectory trajectory : this.trajectoriesScaled) {
	// final List<TPoint> points = trajectory.getPoints();
	//
	// for (final TPoint point : points) {
	// point.setX(point.getX() * this.scale);
	// point.setY(point.getY() * this.scale);
	// }
	// }
	// }
	// }
}
