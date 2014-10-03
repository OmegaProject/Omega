package edu.umassmed.omega.trajectoryManagerPlugin.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.constants.OmegaMathSymbolsConstants;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaROI;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class TMSegmentSingleTrajectoryPanel extends GenericPanel {

	private static final long serialVersionUID = -6552285830250313567L;

	private static final int LABELS_SPACE = 35;

	private final TMPluginPanel pluginPanel;

	private JPopupMenu segmentMenu;
	private JMenuItem zoomIn, zoomOut;

	private final OmegaTrajectory trajectory;

	private String segmName;
	private final List<OmegaSegment> segmentationResults;

	private double maxX, maxY, minX, minY, normalizedDiffX, normalizedDiffY;
	private int maxXP, maxYP, minXP, minYP;
	boolean autoscale;
	private int scale, radius;

	private final int offsetX, offsetY;

	private final List<Point> points;

	private OmegaROI startingROI;

	private final double pixelSizeX, pixelSizeY;

	private Dimension oldDim;

	private boolean isMouseIn;
	private Point mousePosition;

	public TMSegmentSingleTrajectoryPanel(final RootPaneContainer parent,
	        final TMPluginPanel pluginPanel, final double pixelSizeX,
	        final double pixelSizeY, final OmegaTrajectory traj,
	        final boolean autoscale) {
		super(parent);

		this.pluginPanel = pluginPanel;

		this.trajectory = traj;
		this.findTrajectorySizes();
		this.points = new ArrayList<Point>();

		this.segmentationResults = new ArrayList<OmegaSegment>();

		this.segmName = OmegaSegmentationTypes.NOT_ASSIGNED;

		this.scale = 1;
		this.autoscale = true;
		this.offsetX = 75;
		this.offsetY = 75;

		this.radius = 4;

		this.startingROI = null;

		this.pixelSizeX = pixelSizeX;
		this.pixelSizeY = pixelSizeY;

		this.oldDim = null;

		this.isMouseIn = false;
		this.mousePosition = null;

		this.autoscale = autoscale;

		this.createPopupMenu();

		this.addListeners();
	}

	private void createPopupMenu() {
		this.segmentMenu = new JPopupMenu();
		this.zoomIn = new JMenuItem("Zoom in");
		this.zoomOut = new JMenuItem("Zoom out");
		this.segmentMenu.add(this.zoomIn);
		this.segmentMenu.add(this.zoomOut);
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				TMSegmentSingleTrajectoryPanel.this.handleResize();
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				// TODO Auto-generated method stub
				TMSegmentSingleTrajectoryPanel.this.handleMouseClick(
				        evt.getPoint(), SwingUtilities.isRightMouseButton(evt));
			}

			@Override
			public void mouseEntered(final MouseEvent evt) {
				TMSegmentSingleTrajectoryPanel.this.isMouseIn = true;
				TMSegmentSingleTrajectoryPanel.this.mousePosition = evt
				        .getPoint();
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				TMSegmentSingleTrajectoryPanel.this.isMouseIn = false;
				TMSegmentSingleTrajectoryPanel.this.mousePosition = null;
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(final MouseEvent evt) {
				if (TMSegmentSingleTrajectoryPanel.this.startingROI == null)
					return;
				TMSegmentSingleTrajectoryPanel.this.mousePosition = evt
				        .getPoint();
				TMSegmentSingleTrajectoryPanel.this.repaint();
			}
		});
		this.zoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMSegmentSingleTrajectoryPanel.this.modifyZoom(2);
			}
		});
		this.zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMSegmentSingleTrajectoryPanel.this.modifyZoom(0.5);
			}
		});
	}

	private void handleResize() {
		final int width = this.getWidth();
		final int height = this.getHeight();
		final boolean bool1 = this.oldDim != null;
		if (bool1) {
			final boolean bool2 = this.oldDim.width == width;
			final boolean bool3 = this.oldDim.height == height;
			if (bool2 && bool3)
				return;
		}
		this.oldDim = this.getSize();
		this.rescale();
		this.repaint();
	}

	private void modifyZoom(final double modifier) {
		final double scaleTmp = this.scale * modifier;
		if (scaleTmp < 1) {
			this.scale = 1;
		} else if (scaleTmp > 5000) {
			this.scale = 5000;
		} else {
			this.scale = (int) scaleTmp;
		}
		this.rescale();
		this.repaint();
	}

	private void handleMouseClick(final Point clickP,
	        final boolean isRightButton) {
		if (isRightButton) {
			this.startingROI = null;
			this.segmentMenu.show(this, clickP.x, clickP.y);
		} else {
			this.manageLeftClick(clickP);
		}
		this.repaint();
	}

	private void manageLeftClick(final Point clickP) {
		final int squareWidth = this.radius / 2;
		for (final Point p : this.points) {
			if ((clickP.x > (p.x - squareWidth))
			        && (clickP.x < (p.x + squareWidth))
			        && (clickP.y > (p.y - squareWidth))
			        && (clickP.y < (p.y + squareWidth))) {
				final int index = this.points.indexOf(p);
				this.manageROISelection(this.trajectory.getROIs().get(index));
			}
		}
	}

	private void manageROISelection(final OmegaROI roi) {
		if (this.startingROI == null) {
			this.startingROI = roi;
			this.pluginPanel.selectSegmentStartingPoint(this.startingROI);
		} else {
			OmegaROI endingROI = roi;
			if (this.startingROI.equals(endingROI))
				return;
			if (endingROI.getFrameIndex() < this.startingROI.getFrameIndex()) {
				final OmegaROI tmpROI = this.startingROI;
				this.startingROI = endingROI;
				endingROI = tmpROI;
			}
			this.pluginPanel.selectSegmentStartingPoint(this.startingROI);
			this.pluginPanel.selectSegmentEndingPoint(endingROI);
			this.pluginPanel.segmentTrajectory(this.trajectory,
			        this.segmentationResults, this.startingROI, endingROI,
			        this.segmName);
			this.startingROI = null;
		}
		this.repaint();
	}

	public void rescale() {
		this.computeAndSetSize();
		this.computePoints();
	}

	private void findTrajectorySizes() {
		this.minX = Double.MAX_VALUE;
		this.maxX = 0;
		this.minY = Double.MAX_VALUE;
		this.maxY = 0;
		for (final OmegaROI roi : this.trajectory.getROIs()) {
			final double xD = roi.getX();
			final double yD = roi.getY();
			if (this.minX > xD) {
				this.minX = xD;
			}
			if (this.maxX < xD) {
				this.maxX = xD;
			}
			if (this.minY > yD) {
				this.minY = yD;
			}
			if (this.maxY < yD) {
				this.maxY = yD;
			}
		}
		this.normalizedDiffX = 1 - (this.minX / this.maxX);
		this.normalizedDiffY = 1 - (this.minY / this.maxY);
	}

	private void computePoints() {
		this.points.clear();
		final int width = this.getWidth() - (this.offsetX * 2);
		final int height = this.getHeight() - (this.offsetY * 2);

		final int realOffsetX = this.offsetX;
		final int realOffsetY = this.offsetY;

		double realScale = 1;
		if (this.autoscale) {
			double realScaleX = width / this.normalizedDiffX;
			double realScaleY = height / this.normalizedDiffY;
			if (realScaleX < 0) {
				realScaleX = 0;
			}
			if (realScaleY < 0) {
				realScaleY = 0;
			}
			if (realScaleX <= realScaleY) {
				realScale *= realScaleX;
			} else {
				realScale *= realScaleY;
				// final double centerOffsetX = (this.normalizedDiffX / 2)
				// * realScaleX;
				// final double centerOffsetY = (this.normalizedDiffY / 2)
				// * realScaleY;
				// realOffsetX += centerOffsetX;
				// realOffsetY += centerOffsetY;
			}
		}

		int pointX = 0;
		int pointY = 0;
		double oldX = 0;
		double oldY = 0;

		this.minXP = Integer.MAX_VALUE;
		this.maxXP = 0;
		this.minYP = Integer.MAX_VALUE;
		this.maxYP = 0;
		for (final OmegaROI roi : this.trajectory.getROIs()) {
			final double normalizedXD = roi.getX() / this.maxX;
			final double normalizedYD = roi.getY() / this.maxY;
			final double scaledXD = normalizedXD * realScale;
			final double scaledYD = normalizedYD * realScale;
			if (roi.getFrameIndex() != 0) {
				final double distXD = scaledXD - oldX;
				final double distYD = scaledYD - oldY;
				final int distX = new BigDecimal(String.valueOf(distXD))
				        .setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
				final int distY = new BigDecimal(String.valueOf(distYD))
				        .setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
				pointX += distX;
				pointY += distY;
			}
			oldX = scaledXD;
			oldY = scaledYD;
			this.points.add(new Point(pointX, pointY));
			if (this.minXP > pointX) {
				this.minXP = pointX;
			}
			if (this.minYP > pointY) {
				this.minYP = pointY;
			}
			if (this.maxXP < pointX) {
				this.maxXP = pointX;
			}
			if (this.maxYP < pointY) {
				this.maxYP = pointY;
			}
		}

		// System.out.println("Width " + width + " Height " + height);
		// System.out.println("X min: " + this.minXP + " max: " + this.maxXP);
		// System.out.println("Y min: " + this.minYP + " max: " + this.maxYP);
		for (final Point p : this.points) {
			final int tmpX = p.x + Math.abs(this.minXP) + realOffsetX;
			final int tmpY = p.y + Math.abs(this.minYP) + realOffsetY;
			p.x = tmpX;
			p.y = tmpY;
		}
	}

	private void computeAndSetSize() {
		final Dimension parentDim = this.getParent().getParent().getParent()
		        .getSize();
		int width = parentDim.width - this.offsetX;
		int height = parentDim.height - this.offsetY;
		double realScale = this.scale;
		if (this.autoscale) {
			final double realScaleX = (width / this.normalizedDiffX);
			final double realScaleY = (height / this.normalizedDiffY);
			if (realScaleX <= realScaleY) {
				realScale *= realScaleX;
			} else {
				realScale *= realScaleY;
			}
		}
		final int scaledMaxX = (int) (this.normalizedDiffX * realScale);
		final int scaledMaxY = (int) (this.normalizedDiffY * realScale);

		if (scaledMaxX > width) {
			width = scaledMaxX;
		}
		if (scaledMaxY > height) {
			height = scaledMaxY;
		}
		final Dimension dim = new Dimension(width, height);
		this.setPreferredSize(dim);
		this.setSize(dim);
	}

	private int getSegmentationType(final int startingIndex,
	        final int endingIndex) {
		for (final OmegaSegment edge : this.segmentationResults) {
			final int edgeStartingIndex = edge.getStartingROI().getFrameIndex();
			final int edgeEndingIndex = edge.getEndingROI().getFrameIndex();
			if ((edgeStartingIndex <= startingIndex)
			        && (edgeEndingIndex >= endingIndex))
				return edge.getSegmentationType();
		}
		return 0;
	}

	private void paintMetrics(final Graphics2D g2D) {
		final int trajLength = this.trajectory.getLength();
		final OmegaROI p0R = this.trajectory.getROIs().get(0);
		final Point p0 = this.points.get(0);
		final double x0D = p0.getX();
		final double y0D = p0.getY();
		final int x0 = new BigDecimal(String.valueOf(x0D)).setScale(0,
		        BigDecimal.ROUND_HALF_UP).intValue();
		final int y0 = new BigDecimal(String.valueOf(y0D)).setScale(0,
		        BigDecimal.ROUND_HALF_UP).intValue();

		// final int numOfPoints = this.points.size();
		// final int hSpaceP = this.maxXP - this.minXP;
		// final int vSpaceP = this.maxYP - this.minYP;
		// final int meanHDistP = hSpaceP / numOfPoints;
		// final int meanVDistP = vSpaceP / numOfPoints;
		// System.out.println(meanHDistP);
		// System.out.println(meanVDistP);

		if ((this.pixelSizeX == -1) && (this.pixelSizeY == -1)) {
			g2D.drawString("Pixels", 10, 40);
		} else {
			g2D.drawString(OmegaMathSymbolsConstants.MU + "m", 10, 40);
		}

		final int x = Math.abs(this.minXP) + Math.abs(this.maxXP)
		        + this.offsetX;
		final int y = Math.abs(this.minYP) + Math.abs(this.maxYP)
		        + this.offsetY;
		g2D.drawLine(10, 50, x, 50);
		g2D.drawLine(50, 10, 50, y);

		g2D.drawLine(50, y0, 45, y0);
		g2D.drawString("0", 15, y0 + 5);

		final Font font = g2D.getFont();
		final AffineTransform fontAT = new AffineTransform();
		fontAT.rotate(Math.toRadians(-90));
		final Font newFont = font.deriveFont(fontAT);
		g2D.setFont(newFont);

		g2D.drawLine(x0, 50, x0, 45);
		g2D.drawString("0", x0 + 5, 35);

		g2D.setFont(font);

		final List<Point> drawnStrings = new ArrayList<Point>();
		drawnStrings.add(p0);
		for (int i = 1; i < trajLength; i++) {
			final Point pt = this.points.get(i);
			final double xtD = pt.getX();
			final double ytD = pt.getY();
			final int xt = new BigDecimal(String.valueOf(xtD)).setScale(0,
			        BigDecimal.ROUND_HALF_UP).intValue();
			final int yt = new BigDecimal(String.valueOf(ytD)).setScale(0,
			        BigDecimal.ROUND_HALF_UP).intValue();

			final OmegaROI ptR = this.trajectory.getROIs().get(i);
			boolean canBeDrawnX = true;
			boolean canBeDrawnY = true;
			for (final Point p : drawnStrings) {
				final double xtPD = p.getX();
				final double ytPD = p.getY();
				final int xtP = new BigDecimal(String.valueOf(xtPD)).setScale(
				        0, BigDecimal.ROUND_HALF_UP).intValue();
				final int ytP = new BigDecimal(String.valueOf(ytPD)).setScale(
				        0, BigDecimal.ROUND_HALF_UP).intValue();
				final int diffXP = Math.abs(xt - xtP);
				final int diffYP = Math.abs(yt - ytP);

				if (diffXP < TMSegmentSingleTrajectoryPanel.LABELS_SPACE) {
					canBeDrawnX = false;
				}
				if (diffYP < TMSegmentSingleTrajectoryPanel.LABELS_SPACE) {
					canBeDrawnY = false;
				}
			}
			if (canBeDrawnX) {
				// System.out.println("X mark " + i);
				final double diffXR = ptR.getX() - p0R.getX();
				final double diffX = new BigDecimal(String.valueOf(diffXR))
				        .setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
				g2D.setFont(newFont);
				g2D.drawLine(xt, 50, xt, 45);
				g2D.drawString(String.valueOf(diffX), xt + 5, 35);
				g2D.setFont(font);
			}
			if (canBeDrawnY) {
				// System.out.println("Y mark " + i);
				final double diffYR = ptR.getY() - p0R.getY();
				final double diffY = new BigDecimal(String.valueOf(diffYR))
				        .setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
				g2D.drawLine(50, yt, 45, yt);
				g2D.drawString(String.valueOf(diffY), 15, yt + 5);

			}
			if (canBeDrawnX || canBeDrawnY) {
				drawnStrings.add(pt);
			}
		}
	}

	@Override
	public void paint(final Graphics g) {
		final int width = this.getWidth() + this.offsetX;
		final int height = this.getHeight() + this.offsetY;

		final Graphics2D g2D = (Graphics2D) g;

		g2D.setBackground(Color.white);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
		        RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.clearRect(0, 0, width, height);

		if (this.trajectory == null)
			return;

		final int trajLength = this.trajectory.getLength();

		g2D.setColor(Color.black);
		final int squareWidth = this.radius / 2;
		final int squareWidth2 = squareWidth * 2;

		this.paintMetrics(g2D);

		final Font font = g2D.getFont();
		final AffineTransform fontAT = new AffineTransform();
		fontAT.rotate(Math.toRadians(-90));
		final Font newFont = font.deriveFont(fontAT);
		g2D.setFont(newFont);

		// TODO how to put frame index on the drawn?
		Point selectedPoint = null;
		for (int index = 0; index < trajLength; index++) {
			final Point from = this.points.get(index);
			final double x1D = from.getX();
			final double y1D = from.getY();
			final int x1 = new BigDecimal(String.valueOf(x1D)).setScale(0,
			        BigDecimal.ROUND_HALF_UP).intValue();
			final int y1 = new BigDecimal(String.valueOf(y1D)).setScale(0,
			        BigDecimal.ROUND_HALF_UP).intValue();

			g2D.setColor(Color.black);
			if ((index == 0) || (index == (trajLength - 1))) {
				g2D.setColor(Color.red);
			}
			if ((this.startingROI != null)
			        && (this.startingROI.getFrameIndex() == index)) {
				selectedPoint = from;
				g2D.setColor(Color.red);
			}
			g2D.drawRect(x1 - squareWidth, y1 - squareWidth, squareWidth2,
			        squareWidth2);

			final int fIndex = index + 1;
			if ((fIndex == 1) || ((fIndex % 10) == 0) || (fIndex == trajLength)) {
				g2D.drawString(String.valueOf(fIndex), x1 + squareWidth,
				        this.maxYP + (this.offsetY * 2));
			}

			if (index == (trajLength - 1)) {
				continue;
			}

			final Point to = this.points.get(index + 1);
			final double x2D = to.getX();
			final double y2D = to.getY();
			final int x2 = new BigDecimal(String.valueOf(x2D)).setScale(0,
			        BigDecimal.ROUND_HALF_UP).intValue();
			final int y2 = new BigDecimal(String.valueOf(y2D)).setScale(0,
			        BigDecimal.ROUND_HALF_UP).intValue();

			final int segmType = this.getSegmentationType(index, index + 1);
			g2D.setColor(this.pluginPanel.getSegmentationColor(segmType));
			g2D.drawLine(x1 + squareWidth, y1, x2 - squareWidth, y2);
			g2D.setColor(Color.black);
		}

		if ((selectedPoint != null) && this.isMouseIn) {
			g2D.setColor(Color.red);
			final double x1D = selectedPoint.getX();
			final double y1D = selectedPoint.getY();
			final int x1 = new BigDecimal(String.valueOf(x1D)).setScale(0,
			        BigDecimal.ROUND_HALF_UP).intValue();
			final int y1 = new BigDecimal(String.valueOf(y1D)).setScale(0,
			        BigDecimal.ROUND_HALF_UP).intValue();
			g2D.drawLine(x1 - (squareWidth / 2), y1 - (squareWidth / 2),
			        this.mousePosition.x, this.mousePosition.y);
		}

		g2D.setFont(font);
	}

	public OmegaTrajectory getTrajectory() {
		return this.trajectory;
	}

	public void setRadius(final int radius) {
		this.radius = radius;
	}

	public void setSegmentationType(final String segmName) {
		this.segmName = segmName;
	}

	public void updateSegmentationResults(final List<OmegaSegment> segments) {
		// TODO maybe to refactor to avoid recompute
		this.segmentationResults.clear();
		if (segments != null) {
			this.segmentationResults.addAll(segments);
		}
		this.repaint();
	}

	public void setAutoscale(final boolean autoscale) {
		this.autoscale = autoscale;
		this.rescale();
		this.repaint();
	}

	public void selectTrajectoryStart() {
		this.manageROISelection(this.trajectory.getROIs().get(0));
	}

	public void selectTrajectoryEnd() {
		this.manageROISelection(this.trajectory.getROIs().get(
		        this.trajectory.getLength() - 1));
	}

	public void resetSegmentation() {
		this.startingROI = null;
		this.repaint();
	}
}
