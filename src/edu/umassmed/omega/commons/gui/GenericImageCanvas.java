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
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaMathSymbolsConstants;
import edu.umassmed.omega.commons.utilities.OmegaTrajectoryColorManagerUtility;
import edu.umassmed.omega.commons.utilities.StringUtility;
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

	private final List<OmegaTrajectory> selectedTrajectories;
	/**
	 * List of (random) Colors to be used when the user does not specify any
	 * color.
	 */
	private final int trajectoryToDraw;

	private int selectedTrajectoryIndex;

	/** Graphics2D stroke used. **/
	private final int currentStroke;
	/** Current frame. **/
	private int currentT;
	/** The radius of the pixel **/
	private int radius;

	private boolean isPlaceholderImage;

	private JPopupMenu canvasMenu;
	private JMenuItem canvasZoomIn, canvasZoomOut;
	private JMenuItem generateRandomColors, chooseColor;

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
		this.selectedTrajectories = new ArrayList<OmegaTrajectory>();
		this.trajectoryToDraw = -1;
		this.selectedTrajectoryIndex = -1;

		this.setDoubleBuffered(true);

		this.createPopupMenu();

		this.addListeners();
	}

	private void createPopupMenu() {
		this.canvasMenu = new JPopupMenu();

		this.generateRandomColors = new JMenuItem(
		        "Generate random trajectories colors");
		this.chooseColor = new JMenuItem("Choose trajectory color");

		this.canvasZoomIn = new JMenuItem("Zoom in");
		this.canvasZoomOut = new JMenuItem("Zoom out");

		this.canvasMenu.add(this.generateRandomColors);
		this.canvasMenu.add(new JSeparator());
		this.canvasMenu.add(this.canvasZoomIn);
		this.canvasMenu.add(this.canvasZoomOut);
	}

	private void addListeners() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				final Point clickP = evt.getPoint();
				GenericImageCanvas.this.findTrajectoryIndex(clickP);
				GenericImageCanvas.this.canvasMenu
				        .remove(GenericImageCanvas.this.chooseColor);
				final int index = GenericImageCanvas.this.selectedTrajectoryIndex;
				OmegaTrajectory traj = null;
				if (index > -1) {
					traj = GenericImageCanvas.this.trajectories.get(index);
				}
				if (SwingUtilities.isRightMouseButton(evt)) {
					if (traj != null) {
						GenericImageCanvas.this.canvasMenu.add(
						        GenericImageCanvas.this.chooseColor, 1);
					}
					GenericImageCanvas.this.canvasMenu.show(
					        GenericImageCanvas.this, evt.getPoint().x,
					        evt.getPoint().y);
				} else {
					if (!evt.isControlDown()) {
						GenericImageCanvas.this.selectedTrajectories.clear();
					}
					GenericImageCanvas.this.canvasMenu.setVisible(false);
				}

				if (traj != null) {
					GenericImageCanvas.this.selectedTrajectories.add(traj);
					GenericImageCanvas.this.sendApplicationTrajectoriesEvent(
					        GenericImageCanvas.this.selectedTrajectories, true);
					GenericImageCanvas.this.repaint();
				}
			}
		});
		this.canvasZoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GenericImageCanvas.this.setScale(GenericImageCanvas.this
				        .getScale() / 2.0);
				GenericImageCanvas.this.callRevalidate();
				GenericImageCanvas.this.repaint();
			}
		});
		this.canvasZoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GenericImageCanvas.this.setScale(GenericImageCanvas.this
				        .getScale() * 2.0);
				GenericImageCanvas.this.callRevalidate();
				GenericImageCanvas.this.repaint();
			}
		});
		this.generateRandomColors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
				        GenericImageCanvas.this.getParentContainer(),
				        "Random colors generation confirmation",
				        "Do you want do generate new random colors for the trajectories?",
				        true);
				dialog.setVisible(true);
				if (!dialog.getConfirmation())
					return;
				OmegaTrajectoryColorManagerUtility
				        .generateRandomColors(GenericImageCanvas.this.trajectories);
				GenericImageCanvas.this.repaint();
				GenericImageCanvas.this.sendApplicationTrajectoriesEvent(
				        GenericImageCanvas.this.trajectories, false);

			}
		});
		this.chooseColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				final Color c = OmegaTrajectoryColorManagerUtility
				        .openPaletteColor(GenericImageCanvas.this.trajectories,
				                GenericImageCanvas.this,
				                GenericImageCanvas.this.selectedTrajectoryIndex);

				final StringBuffer buf = new StringBuffer();
				buf.append("Do you want to color trajectory ");
				buf.append(GenericImageCanvas.this.selectedTrajectoryIndex + 1);
				buf.append("?");

				final GenericConfirmationDialog dialog = new GenericConfirmationDialog(
				        GenericImageCanvas.this.getParentContainer(),
				        "Choose single color confirmation", buf.toString(),
				        true);
				dialog.setVisible(true);
				if (!dialog.getConfirmation())
					return;

				final OmegaTrajectory traj = GenericImageCanvas.this.trajectories
				        .get(GenericImageCanvas.this.selectedTrajectoryIndex);
				traj.setColor(c);
				GenericImageCanvas.this.repaint();
				final List<OmegaTrajectory> trajectories = new ArrayList<OmegaTrajectory>();
				trajectories.add(traj);
				GenericImageCanvas.this.sendApplicationTrajectoriesEvent(
				        trajectories, false);
			}
		});
	}

	private void sendApplicationTrajectoriesEvent(
	        final List<OmegaTrajectory> trajectories, final boolean selection) {
		this.parentPanel.sendApplicationTrajectoriesEvent(trajectories,
		        selection);
	}

	private void findTrajectoryIndex(final Point clickP) {
		final int x = (int) (clickP.x / this.scale);
		final int y = (int) (clickP.y / this.scale);
		int trajIndex = -1;
		for (final OmegaTrajectory traj : GenericImageCanvas.this.trajectories) {
			final List<OmegaROI> rois = traj.getROIs();
			for (final OmegaROI roi : rois) {
				final int roiX = (int) roi.getX();
				final int roiY = (int) roi.getY();
				if ((x < (roiX + 2)) && (x > (roiX - 2)) && (y < (roiY + 2))
				        && (y > (roiY - 2))) {
					trajIndex = GenericImageCanvas.this.trajectories
					        .indexOf(traj);
					break;
				}
			}
			if (trajIndex != -1) {
				break;
			}
		}

		this.selectedTrajectoryIndex = trajIndex;
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
				        StringUtility.doubleToString(
				                this.parentPanel.getSizeX() * width, 1),
				        OmegaMathSymbolsConstants.MU);
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
				        StringUtility.doubleToString(
				                this.parentPanel.getSizeY() * height, 1),
				        OmegaMathSymbolsConstants.MU);
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
			// draw trajectories
			int currentTrajectoryIndex = 0;
			for (final OmegaTrajectory trajectory : this.trajectories) {
				if (!trajectory.isVisible()) {
					continue;
				}
				if ((this.trajectoryToDraw > -1)
				        && (this.trajectoryToDraw != currentTrajectoryIndex)) {
					currentTrajectoryIndex++;
					continue;
				}
				int minX = scaledWidth;
				int maxX = 0;
				int minY = scaledHeight;
				int maxY = 0;
				final List<OmegaROI> points = trajectory.getROIs();
				for (int i = 0; i < (points.size() - 1); i++) {
					final OmegaROI one = points.get(i);
					final OmegaROI two = points.get(i + 1);
					// for (int frame = 1; frame <= this.currentT; frame++) {
					// // first point
					// if (one.getFrameIndex() == frame) {
					// set the correct color, the choosen or the random
					// one

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

					g2D.setColor(trajectory.getColor());
					g2D.draw(new Line2D.Double(x1, y1, x2, y2));
					if (minX > x1) {
						minX = x1;
					}
					if (maxX < x1) {
						maxX = x1;
					}
					if (minY > y1) {
						minY = y1;
					}
					if (maxY < y1) {
						maxY = y1;
					}
				}
				if (this.selectedTrajectories.contains(trajectory)) {
					g2D.setColor(OmegaConstants
					        .getDefaultSelectionBackgroundColor());
					minX -= 5;
					maxX += 5;
					minY -= 5;
					maxY += 5;
					final int ovalWidth = (maxX - minX);
					final int ovalHeight = (maxY - minY);
					g2D.drawRect(minX, minY, ovalWidth, ovalHeight);
					// g2D.draw(new Line2D.Double(x1 + 1, y1 + 1, x2 + 1,
					// y2 + 1));
					// g2D.draw(new Line2D.Double(x1 + 1, y1 + 1, x2 + 1,
					// y2 + 1));
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
	// final String imageName = StringUtility
	// .removeFileExtension(StringUtility
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

	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
	        final boolean selection) {
		// TODO check if this can be done smarter, maybe just repainting
		// trajectories instead of everything
		this.selectedTrajectories.clear();
		this.selectedTrajectories.addAll(trajectories);
		this.repaint();
	}
}
