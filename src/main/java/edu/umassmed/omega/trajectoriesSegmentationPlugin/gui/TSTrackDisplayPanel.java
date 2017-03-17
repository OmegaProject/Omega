package edu.umassmed.omega.trajectoriesSegmentationPlugin.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.gui.GenericPanel;

public class TSTrackDisplayPanel extends GenericPanel {
	
	private static final long serialVersionUID = -786876453205164899L;
	
	private final TSTrackPanel trackPanel;
	
	private int borderX, borderY;
	private Map<OmegaROI, Point> points;
	private final Map<Point, Point> connections;
	private List<OmegaROI> rois;
	private OmegaROI startingROI, endingROI;
	
	private int radius;
	
	private boolean isMouseIn;
	private Point mousePosition;
	
	public TSTrackDisplayPanel(final RootPaneContainer parent,
			final TSTrackPanel tsTrackPanel, final int radius,
			final int borderX, final int borderY) {
		super(parent);
		
		this.trackPanel = tsTrackPanel;
		
		this.points = null;
		this.rois = null;
		this.connections = new LinkedHashMap<>();
		this.startingROI = null;
		this.endingROI = null;
		
		this.radius = radius;
		
		this.isMouseIn = false;
		this.mousePosition = null;
		
		this.borderX = borderX;
		this.borderY = borderY;
		
		this.addListeners();
	}
	
	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				// TSSingleTrajectoryPanel.this.handleResize(evt.getComponent());
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(final MouseEvent evt) {
				TSTrackDisplayPanel.this.handleMouseIn(evt.getPoint());
			}
			
			@Override
			public void mouseExited(final MouseEvent e) {
				TSTrackDisplayPanel.this.handleMouseOut();
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(final MouseEvent evt) {
				TSTrackDisplayPanel.this.handleMouseMotion(evt.getPoint());
			}
		});
	}
	
	private void handleMouseIn(final Point mouseP) {
		this.isMouseIn = true;
		this.mousePosition = mouseP;
	}
	
	private void handleMouseOut() {
		this.isMouseIn = false;
		this.mousePosition = null;
	}
	
	private void handleMouseMotion(final Point mouseP) {
		if (this.startingROI == null)
			return;
		this.mousePosition = mouseP;
		this.revalidate();
		this.repaint();
	}
	
	private boolean drawLabels(final Graphics2D g2D, final String label,
			final Point p, final List<Point> labeledPoints,
			final boolean toBeDrawn) {
		// final int exRadius = this.radius + 2;
		// final int exOvalWidth = exRadius * 2;
		final int scale = new BigDecimal(String.valueOf(this.trackPanel
				.getScale())).setScale(0, RoundingMode.DOWN).intValue();
		final int size = this.radius < scale ? this.radius : scale;
		final int ovalWidth = size * 2;
		final int dist = TSTrackPanel.SPACE_BETWEEN_LABELS / 3;
		final int dist2 = TSTrackPanel.SPACE_BETWEEN_LABELS;
		boolean hasPointCloseAbove = false, hasPointCloseBelow = false, hasPointCloseLeft = false;
		final boolean hasPointCloseRight = false;
		boolean spaceAround = true, spaceFromLast = true;
		// Build list of connectors and check their position to pick side for
		// label
		// How to pick the side based on connections?
		for (final Point p2 : this.points.values()) {
			if (p2 == p) {
				continue;
			}
			final int distX = p.x - p2.x;
			final int distY = p.y - p2.y;
			if (!hasPointCloseBelow && (StrictMath.abs(distX) < dist)
					&& (distY < dist) && (distY > 0)) {
				hasPointCloseBelow = true;
			}
			if (!hasPointCloseAbove && (StrictMath.abs(distX) < dist)
					&& (distY < -dist) && (distY < 0)) {
				hasPointCloseAbove = true;
			}
			if (!hasPointCloseLeft && (StrictMath.abs(distY) < dist)
					&& (distX < -dist) && (distX < 0)) {
				hasPointCloseLeft = true;
			}
			if (!hasPointCloseRight && (StrictMath.abs(distY) < dist)
					&& (distX < dist) && (distX > 0)) {
				hasPointCloseLeft = true;
			}
			if ((StrictMath.abs(distX) < dist)
					&& (StrictMath.abs(distY) < dist)) {
				spaceAround = false;
			}
		}
		for (final Point p2 : labeledPoints) {
			if (p2 == p) {
				continue;
			}
			final int distX = p.x - p2.x;
			final int distY = p.y - p2.y;
			if ((StrictMath.abs(distX) < dist2)
					&& (StrictMath.abs(distY) < dist2)) {
				spaceFromLast = false;
			}
		}
		int labelSide = 0;
		if (!hasPointCloseBelow) {
			labelSide = 0;
		} else if (!hasPointCloseAbove) {
			labelSide = 1;
		} else {
			labelSide = 2;
		}
		if (toBeDrawn || (spaceAround && spaceFromLast)) {
			int x, y;
			switch (labelSide) {
				case 1:
					// ABOVE
					x = p.x - size;
					y = p.y - (ovalWidth * 2);
					break;
				case 2:
					// LEFT
					x = p.x - (ovalWidth * 3);
					y = p.y + size;
					break;
				default:
					// BELOW
					x = p.x - size;
					y = p.y + (ovalWidth * 3);
					
			}
			g2D.drawString(label, x, y);
			return true;
		}
		return false;
	}
	
	@Override
	public void paint(final Graphics g) {
		final int width = this.getWidth();
		final int height = this.getHeight();
		
		final Graphics2D g2D = (Graphics2D) g;
		
		g2D.setBackground(Color.white);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.clearRect(0, 0, width, height);
		
		g2D.setColor(Color.black);
		g2D.drawRect(0, 0, this.borderX, this.borderY);
		
		if (this.points == null)
			return;
		
		final int length = this.points.size();
		
		// final Font font = g2D.getFont();
		// final AffineTransform fontAT = new AffineTransform();
		// fontAT.rotate(Math.toRadians(-90));
		// final Font newFont = font.deriveFont(fontAT);
		// g2D.setFont(newFont);
		
		final int scale = new BigDecimal(String.valueOf(this.trackPanel
				.getScale())).setScale(0, RoundingMode.DOWN).intValue();
		final int size = this.radius < scale ? this.radius : scale;
		final int exRadius = size;
		final int exOvalWidth = exRadius * 2;
		final int ovalWidth = size * 2;
		
		this.connections.clear();
		final List<Point> labeledPoints = new ArrayList<>();
		Point startingPoint = null, endingPoint = null;
		for (int i = 0; i < this.rois.size(); i++) {
			final OmegaROI roi1 = this.rois.get(i);
			final Point p1 = this.points.get(roi1);
			final int x1 = p1.x;
			final int y1 = p1.y;
			g2D.setColor(Color.black);
			
			if ((this.startingROI != null) && (this.startingROI == roi1)) {
				startingPoint = p1;
				final Color c = this.trackPanel.getCurrentSegmentationColor();
				g2D.setColor(c);
			} else if ((this.endingROI != null) && (this.endingROI == roi1)) {
				endingPoint = p1;
				final Color c = this.trackPanel.getCurrentSegmentationColor();
				g2D.setColor(c);
			}
			
			boolean toBeDraw = false;
			if ((i == 0) || (i == (length - 1))) {
				toBeDraw = true;
			}
			if (this.drawLabels(g2D, String.valueOf(roi1.getFrameIndex()), p1,
			        labeledPoints, toBeDraw)) {
				labeledPoints.add(p1);
			}
			
			if (i == 0) {
				continue;
			}
			
			final OmegaROI roi2 = this.rois.get(i - 1);
			final Point p2 = this.points.get(roi2);
			this.connections.put(p1, p2);
			final double x2D = p2.getX();
			final double y2D = p2.getY();
			final int x2 = new BigDecimal(String.valueOf(x2D)).setScale(0,
					BigDecimal.ROUND_HALF_UP).intValue();
			final int y2 = new BigDecimal(String.valueOf(y2D)).setScale(0,
					BigDecimal.ROUND_HALF_UP).intValue();
			
			int segmType = this.trackPanel.getSegmentationType(
					roi2.getFrameIndex(), roi1.getFrameIndex());
			Color segmColor = this.trackPanel.getSegmentationColor(segmType);
			g2D.setColor(segmColor);
			g2D.drawLine(x1, y1, x2, y2);
			
			if ((i - 1) == 0) {
				g2D.setColor(Color.green);
				g2D.fillRect(x2 - exRadius, y2 - exRadius, exOvalWidth,
						exOvalWidth);
			} else if (i == (length - 1)) {
				g2D.setColor(Color.red);
				g2D.fillRect(x1 - exRadius, y1 - exRadius, exOvalWidth,
						exOvalWidth);
				final OmegaROI roi3 = this.rois.get(i - 1);
				segmType = this.trackPanel.getSegmentationType(
						roi3.getFrameIndex(), roi2.getFrameIndex());
				segmColor = this.trackPanel.getSegmentationColor(segmType);
				g2D.setColor(segmColor);
				g2D.fillOval(x2 - size, y2 - size, ovalWidth, ovalWidth);
			} else {
				final OmegaROI roi3 = this.rois.get(i - 2);
				segmType = this.trackPanel.getSegmentationType(
						roi3.getFrameIndex(), roi2.getFrameIndex());
				segmColor = this.trackPanel.getSegmentationColor(segmType);
				g2D.setColor(segmColor);
				g2D.fillOval(x2 - size, y2 - size, ovalWidth, ovalWidth);
			}
			g2D.setColor(Color.black);
		}
		
		if (this.isMouseIn) {
			if ((startingPoint != null)) {
				final Color c = this.trackPanel.getCurrentSegmentationColor();
				g2D.setColor(c);
				final double x1D = startingPoint.getX();
				final double y1D = startingPoint.getY();
				final int x1 = new BigDecimal(String.valueOf(x1D)).setScale(0,
						BigDecimal.ROUND_HALF_UP).intValue();
				final int y1 = new BigDecimal(String.valueOf(y1D)).setScale(0,
						BigDecimal.ROUND_HALF_UP).intValue();
				if (endingPoint == null) {
					g2D.drawLine(x1, y1, this.mousePosition.x,
							this.mousePosition.y);
				} else {
					final double x2D = endingPoint.getX();
					final double y2D = endingPoint.getY();
					final int x2 = new BigDecimal(String.valueOf(x2D))
					.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
					final int y2 = new BigDecimal(String.valueOf(y2D))
					.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
					g2D.drawLine(x1, y1, x2, y2);
				}
			}
		}
		
		// g2D.setFont(this.font);
	}
	
	public void setRadius(final int radius) {
		this.radius = radius;
	}
	
	public void setPoints(final Map<OmegaROI, Point> points) {
		if (points == null) {
			this.points = null;
			this.rois = null;
		}
		this.points = points;
		this.rois = new ArrayList<>(points.keySet());
		Collections.sort(this.rois, new Comparator<OmegaROI>() {
			@Override
			public int compare(final OmegaROI o1, final OmegaROI o2) {
				if (o1.getFrameIndex() < o2.getFrameIndex())
					return -1;
				else if (o1.getFrameIndex() > o2.getFrameIndex())
					return 1;
				return 0;
			}
		});
		this.revalidate();
		this.repaint();
	}
	
	public void setStartingROI(final OmegaROI startingROI) {
		this.startingROI = startingROI;
	}
	
	public void setEndingROI(final OmegaROI endingROI) {
		this.endingROI = endingROI;
	}
	
	public void setSizes(int width, int height, final int borderX,
			final int borderY) {
		if (width == borderX) {
			width += 15;
		}
		if (height == borderY) {
			height += 15;
		}
		final Dimension dim = new Dimension(width, height);
		this.setPreferredSize(dim);
		this.setSize(dim);
		this.borderX = borderX;
		this.borderY = borderY;
		this.revalidate();
		this.repaint();
	}
	
	public void resetSegmentation() {
		this.startingROI = null;
		this.endingROI = null;
	}
}
