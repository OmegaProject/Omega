package edu.umassmed.omega.trajectoriesSegmentationPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.RootPaneContainer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.constants.OmegaConstantsMathSymbols;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.utilities.OmegaMathsUtilities;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class TSTrackPanel extends GenericPanel {
	private static final long serialVersionUID = -6552285830250313567L;
	public static final int LABELS_PANEL_SIZE = 60;
	public static final int SPACE_BETWEEN_LABELS = 50;

	private final TSPluginPanel pluginPanel;
	private TSTrackDisplayPanel trackDisplayPanel;
	private TSTrackLabelsPanel hLabelsPanel, vLabelsPanel;

	private JScrollPane trackDisplayScrollPane, hLabelsScrollPane,
	        vLabelsScrollPane;

	private JPopupMenu segmentMenu;
	private JMenuItem zoomIn, zoomOut;

	private final OmegaTrajectory trajectory;

	private OmegaROI startingROI, endingROI;

	private int segmType;
	private final List<OmegaSegment> segmentationResults;

	private final Map<OmegaROI, Point> points;

	boolean scaleToFit;
	private double scale;

	private int radius;
	private final int imgWidth, imgHeight;
	private final double pixelSizeX, pixelSizeY;

	private double maxX, maxY, minX, minY, normalizedDiffX, normalizedDiffY;
	private int maxXP, maxYP, minXP, minYP;
	private Point mousePosition;

	private boolean segmOnROISelection;

	public TSTrackPanel(final RootPaneContainer parent,
	        final TSPluginPanel pluginPanel, final int sizeX, final int sizeY,
	        final double pixelSizeX, final double pixelSizeY,
	        final OmegaTrajectory traj,
	        final List<OmegaSegment> segmentationsResults) {
		super(parent);
		this.pluginPanel = pluginPanel;

		this.trajectory = traj;
		this.startingROI = null;

		this.segmentationResults = new ArrayList<>(segmentationsResults);
		this.segmType = OmegaSegmentationTypes.NOT_ASSIGNED_VAL;

		this.points = new LinkedHashMap<>();

		this.scale = 1;
		this.scaleToFit = false;

		this.radius = 4;
		this.imgWidth = sizeX;
		this.imgHeight = sizeY;
		this.pixelSizeX = pixelSizeX;
		this.pixelSizeY = pixelSizeY;

		this.segmOnROISelection = true;

		this.setLayout(new BorderLayout());

		this.createPopupMenu();

		this.createAndAddWidgets();

		this.addListeners();

		this.findTrajectorySizes();
	}

	private void createPopupMenu() {
		this.segmentMenu = new JPopupMenu();
		this.zoomIn = new JMenuItem("Zoom in");
		this.zoomOut = new JMenuItem("Zoom out");
		this.segmentMenu.add(this.zoomIn);
		this.segmentMenu.add(this.zoomOut);
	}

	private void createAndAddWidgets() {
		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());

		final JPanel cornerPanel = new JPanel();
		cornerPanel.setLayout(new BorderLayout());
		final Dimension dim = new Dimension(TSTrackPanel.LABELS_PANEL_SIZE + 3,
		        TSTrackPanel.LABELS_PANEL_SIZE);
		cornerPanel.setPreferredSize(dim);
		cornerPanel.setSize(dim);

		final JLabel lbl = new JLabel(
		        "<html><div align=\"center\">Pixels<br/>("
		                + OmegaConstantsMathSymbols.MU + "m)</div></html>");
		cornerPanel.add(lbl, BorderLayout.CENTER);

		this.hLabelsPanel = new TSTrackLabelsPanel(this.getParentContainer(),
		        0, this.imgWidth, this.pixelSizeX, this.getWidth(), 0);
		this.hLabelsScrollPane = new JScrollPane(this.hLabelsPanel);
		this.hLabelsScrollPane
		        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.hLabelsScrollPane
		        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		topPanel.add(cornerPanel, BorderLayout.WEST);
		topPanel.add(this.hLabelsScrollPane, BorderLayout.CENTER);

		this.add(topPanel, BorderLayout.NORTH);

		this.vLabelsPanel = new TSTrackLabelsPanel(this.getParentContainer(),
		        1, this.imgHeight, this.pixelSizeY, this.getHeight(), 0);
		this.vLabelsScrollPane = new JScrollPane(this.vLabelsPanel);
		this.vLabelsScrollPane
		        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.vLabelsScrollPane
		        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		this.add(this.vLabelsScrollPane, BorderLayout.WEST);

		this.trackDisplayPanel = new TSTrackDisplayPanel(
		        this.getParentContainer(), this, this.radius, this.imgWidth,
		        this.imgHeight);
		this.trackDisplayScrollPane = new JScrollPane(this.trackDisplayPanel);

		this.add(this.trackDisplayScrollPane, BorderLayout.CENTER);

	}

	private void addListeners() {
		this.trackDisplayScrollPane.getHorizontalScrollBar()
		        .addAdjustmentListener(new AdjustmentListener() {
			        @Override
			        public void adjustmentValueChanged(final AdjustmentEvent evt) {
				        TSTrackPanel.this.handleHorizontalScrollBarChanged();
			        }
		        });
		this.trackDisplayScrollPane.getVerticalScrollBar()
		        .addAdjustmentListener(new AdjustmentListener() {
			        @Override
			        public void adjustmentValueChanged(final AdjustmentEvent evt) {
				        TSTrackPanel.this.handleVerticalScrollBarChanged();
			        }
		        });
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				TSTrackPanel.this.handleResize();
			}
		});
		this.trackDisplayPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				TSTrackPanel.this.handleMouseClick(evt.getPoint(),
				        SwingUtilities.isRightMouseButton(evt));
			}

			@Override
			public void mousePressed(final MouseEvent evt) {
				TSTrackPanel.this.handleMousePressed(evt.getPoint());
			}

			@Override
			public void mouseReleased(final MouseEvent evt) {
				TSTrackPanel.this.handleMouseReleased(evt.getPoint(),
				        SwingUtilities.isRightMouseButton(evt));
			}
		});
		this.trackDisplayPanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(final MouseEvent evt) {
				TSTrackPanel.this.handleMouseDragged(evt.getPoint(),
				        SwingUtilities.isRightMouseButton(evt));
			}
		});
		this.zoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TSTrackPanel.this.handleZoom(2);
			}
		});
		this.zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TSTrackPanel.this.handleZoom(0.5);
			}
		});
	}

	private void rescale() {
		int width = this.getWidth() - (TSTrackPanel.LABELS_PANEL_SIZE + 3) - 20;
		int height = this.getHeight() - TSTrackPanel.LABELS_PANEL_SIZE - 20;
		final int neededWidth = (int) (this.imgWidth * this.scale);
		final int neededHeight = (int) (this.imgHeight * this.scale);
		if (neededWidth > width) {
			width = neededWidth;
		}
		if (neededHeight > height) {
			height = neededHeight;
		}
		this.hLabelsPanel.setPanelSize(neededWidth);
		final Dimension hDim = new Dimension(neededWidth,
		        TSTrackPanel.LABELS_PANEL_SIZE);
		this.hLabelsScrollPane.getViewport().setPreferredSize(hDim);
		this.hLabelsScrollPane.getViewport().setSize(hDim);
		this.vLabelsPanel.setPanelSize(neededHeight);
		final Dimension vDim = new Dimension(TSTrackPanel.LABELS_PANEL_SIZE,
		        neededHeight);
		this.vLabelsScrollPane.getViewport().setPreferredSize(vDim);
		this.vLabelsScrollPane.getViewport().setSize(vDim);
		this.trackDisplayPanel.setSizes(width, height, neededWidth,
		        neededHeight);
		final Dimension dim = new Dimension(width, height);
		this.trackDisplayScrollPane.getViewport().setPreferredSize(dim);
		this.trackDisplayScrollPane.getViewport().setSize(dim);
		this.validate();
		this.repaint();
	}

	private void handleResize() {
		this.rescale();
		this.computePoints();
	}

	private void computePoints() {
		this.minXP = Integer.MAX_VALUE;
		this.maxXP = 0;
		this.minYP = Integer.MAX_VALUE;
		this.maxYP = 0;
		for (final OmegaROI roi : this.trajectory.getROIs()) {
			final double xD = roi.getX();
			final double yD = roi.getY();
			final double scaledXD = xD * this.scale;
			final double scaledYD = yD * this.scale;
			final int pointX = new BigDecimal(String.valueOf(scaledXD))
			        .setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			final int pointY = new BigDecimal(String.valueOf(scaledYD))
			        .setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			this.points.put(roi, new Point(pointX, pointY));
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

		// for (final Point p : this.points) {
		// final int tmpX = p.x + Math.abs(this.minXP) + this.offsetX;
		// final int tmpY = p.y + Math.abs(this.minYP) + this.offsetY;
		// p.x = tmpX;
		// p.y = tmpY;
		// }

		this.trackDisplayPanel.setPoints(this.points);
	}

	private void computeAndSetScaleToFit() {
		final int width = this.getWidth()
		        - (TSTrackPanel.LABELS_PANEL_SIZE + 3) - 60;
		final int height = this.getHeight() - TSTrackPanel.LABELS_PANEL_SIZE
		        - 60;
		final double neededWidth = (this.maxX - this.minX);
		final double neededHeight = (this.maxY - this.minY);
		final double maxScaleX = width / neededWidth;
		final double maxScaleY = height / neededHeight;
		if (maxScaleX <= maxScaleY) {
			this.scale = maxScaleX;
		} else {
			this.scale = maxScaleY;
		}
	}

	private void setScaleToFitScrollBarPosition() {
		final int meanX = (this.maxXP - this.minXP) / 2;
		final int meanY = (this.maxYP - this.minYP) / 2;
		final int x = this.minXP + meanX;
		final int y = this.minYP + meanY;
		final int width = this.trackDisplayScrollPane.getWidth() / 2;
		final int height = this.trackDisplayScrollPane.getHeight() / 2;
		this.setScrollBarPosition(x - width, y - height);
	}

	private void computeScrollBarPositionOnDrag(final Point startP,
	        final Point endP) {
		final int diffX = startP.x - endP.x;
		final int diffY = startP.y - endP.y;
		final int hSBPos = this.trackDisplayScrollPane.getHorizontalScrollBar()
		        .getValue();
		final int vSBPos = this.trackDisplayScrollPane.getVerticalScrollBar()
		        .getValue();
		this.setScrollBarPosition(hSBPos + diffX, vSBPos + diffY);
	}

	private void setScrollBarPosition(final int x, final int y) {
		this.trackDisplayScrollPane.getHorizontalScrollBar().setValue(x);
		this.trackDisplayScrollPane.getVerticalScrollBar().setValue(y);
	}

	private void handleHorizontalScrollBarChanged() {
		final int value = this.trackDisplayScrollPane.getHorizontalScrollBar()
		        .getValue();
		this.hLabelsScrollPane.getHorizontalScrollBar().setValue(value);
	}

	private void handleVerticalScrollBarChanged() {
		final int value = this.trackDisplayScrollPane.getVerticalScrollBar()
		        .getValue();
		this.vLabelsScrollPane.getVerticalScrollBar().setValue(value);
	}

	private void handleZoom(final double modifier) {
		this.scale *= modifier;
		// if (this.scale == 0) {
		// this.scale = 0.01;
		// }
		this.rescale();
		this.computePoints();
		int newPosX = (int) (this.mousePosition.x * modifier);
		int newPosY = (int) (this.mousePosition.y * modifier);
		final int width = this.trackDisplayScrollPane.getWidth() / 2;
		final int height = this.trackDisplayScrollPane.getHeight() / 2;
		newPosX -= width;
		newPosY -= height;
		this.setScrollBarPosition(newPosX, newPosY);
	}

	private void handleMousePressed(final Point pos) {
		this.mousePosition = pos;
	}

	private void handleMouseDragged(final Point pos, final boolean isRightButton) {
		if (isRightButton)
			return;
		this.computeScrollBarPositionOnDrag(this.mousePosition, pos);
	}

	private void handleMouseReleased(final Point pos,
	        final boolean isRightButton) {
		if (isRightButton)
			return;
		this.computeScrollBarPositionOnDrag(this.mousePosition, pos);
	}

	private void handleMouseClick(final Point clickP,
	        final boolean isRightButton) {
		if (isRightButton) {
			this.startingROI = null;
			this.endingROI = null;
			this.segmentMenu.show(this.trackDisplayPanel, clickP.x, clickP.y);
		} else {
			this.handleLeftClick(clickP);
		}
		this.revalidate();
		this.repaint();
	}

	private void handleLeftClick(final Point clickP) {
		final List<OmegaROI> rois = new ArrayList<>();
		for (final OmegaROI roi : this.points.keySet()) {
			final Point p = this.points.get(roi);
			if ((clickP.x > (p.x - this.radius))
			        && (clickP.x < (p.x + this.radius))
			        && (clickP.y > (p.y - this.radius))
			        && (clickP.y < (p.y + this.radius))) {
				// System.out.println("FI: " + roi.getFrameIndex());
				rois.add(roi);
			}
		}
		if (rois.isEmpty())
			return;
		this.handleROISelection(rois);
	}

	private void findTrajectorySizes() {
		double minX = Double.MAX_VALUE;
		double maxX = 0;
		double minY = Double.MAX_VALUE;
		double maxY = 0;
		for (final OmegaROI roi : this.trajectory.getROIs()) {
			final double xD = roi.getX();
			final double yD = roi.getY();
			if (minX > xD) {
				minX = xD;
			}
			if (maxX < xD) {
				maxX = xD;
			}
			if (minY > yD) {
				minY = yD;
			}
			if (maxY < yD) {
				maxY = yD;
			}
		}
		// this.normalizedDiffX = 1 - (this.minX / this.maxX);
		// this.normalizedDiffY = 1 - (this.minY / this.maxY);

		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.normalizedDiffX = OmegaMathsUtilities.normalize(maxX - minX, minX,
		        maxX);
		this.normalizedDiffY = OmegaMathsUtilities.normalize(maxY - minY, minY,
		        maxY);
	}

	private void handleROISelection(final List<OmegaROI> rois) {
		OmegaROI roi = null;
		if (rois.size() > 1) {
			final TSTrackROISelectionDialog dialog = new TSTrackROISelectionDialog(
			        this.getParentContainer(), rois);
			dialog.setVisible(true);
			roi = dialog.getSelectedROI();
		} else {
			roi = rois.get(0);
		}
		if (roi == null)
			return;
		if (this.startingROI == null) {
			this.startingROI = roi;
			this.pluginPanel.selectSegmentStartingPoint(this.startingROI);
			this.trackDisplayPanel.setStartingROI(this.startingROI);
		} else {
			final OmegaROI endingROI = roi;
			if (this.startingROI.equals(endingROI))
				return;
			if (endingROI.getFrameIndex() < this.startingROI.getFrameIndex()) {
				this.endingROI = this.startingROI;
				this.startingROI = endingROI;
			} else {
				this.endingROI = endingROI;
			}
			this.trackDisplayPanel.setStartingROI(this.startingROI);
			this.trackDisplayPanel.setEndingROI(this.endingROI);
			this.pluginPanel.selectSegmentStartingPoint(this.startingROI);
			this.pluginPanel.selectSegmentEndingPoint(this.endingROI);
			if (this.segmOnROISelection) {
				this.segmentTrajectory();
				// this.trackDisplayPanel.resetSegmentation();
			}
		}
		this.repaint();
	}

	public void segmentTrajectory() {
		if ((this.startingROI == null) || (this.endingROI == null))
			return;
		this.pluginPanel.segmentTrajectory(this.trajectory,
		        this.segmentationResults, this.startingROI, this.endingROI,
		        this.segmType);
		this.resetSegmentation();
	}

	public void setScaleToFit() {
		this.scaleToFit = true;
		// this.restoreSizes();
		this.computeAndSetScaleToFit();
		this.rescale();
		this.computePoints();
		this.setScaleToFitScrollBarPosition();
	}

	public void setScaleOneOne() {
		this.scaleToFit = false;
		this.scale = 1;
		// this.restoreSizes();
		this.rescale();
		this.computePoints();
	}

	public void selectTrajectoryStart() {
		final List<OmegaROI> rois = new ArrayList<>();
		rois.add(this.trajectory.getROIs().get(0));
		this.handleROISelection(rois);
	}

	public void selectTrajectoryEnd() {
		final List<OmegaROI> rois = new ArrayList<>();
		rois.add(this.trajectory.getROIs().get(this.trajectory.getLength() - 1));
		this.handleROISelection(rois);
	}

	public void resetSegmentation() {
		this.startingROI = null;
		this.endingROI = null;
		this.trackDisplayPanel.resetSegmentation();
		this.repaint();
	}

	public OmegaTrajectory getTrajectory() {
		return this.trajectory;
	}

	public void setRadius(final int radius) {
		this.radius = radius;
		this.trackDisplayPanel.setRadius(radius);
	}

	public Color getSegmentationColor(final int value) {
		return this.pluginPanel.getSegmentationColor(value);
	}

	public Color getCurrentSegmentationColor() {
		return this.pluginPanel.getCurrentSegmentationColor(this.segmType);
	}

	public int getSegmentationType(final int startingIndex,
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

	public void setSegmentationType(final int segmType) {
		this.segmType = segmType;
	}

	public void updateSegmentationResults(final List<OmegaSegment> segments) {
		// TODO maybe to refactor to avoid recompute
		this.segmentationResults.clear();
		if (segments != null) {
			this.segmentationResults.addAll(segments);
		}
		this.repaint();
	}

	public void segmentOnROISelection() {
		this.segmOnROISelection = true;
	}

	public void segmentOnMotionSelection() {
		this.segmOnROISelection = false;
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.trackDisplayPanel.updateParentContainer(parent);
		this.hLabelsPanel.updateParentContainer(parent);
		this.vLabelsPanel.updateParentContainer(parent);
	}

	public Double getScale() {
		return this.scale;
	}
}
