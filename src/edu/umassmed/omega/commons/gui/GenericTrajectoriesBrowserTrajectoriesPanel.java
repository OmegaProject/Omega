package edu.umassmed.omega.commons.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.runnable.TBROIThumbnailLoader;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class GenericTrajectoriesBrowserTrajectoriesPanel extends GenericPanel {

	private static final long serialVersionUID = 2744718217539154609L;

	private final GenericTrajectoriesBrowserPanel tbPanel;

	private final Map<Point, OmegaROI> particlesMap;
	private final Map<Integer, OmegaTrajectory> trajectoriesMap;

	private OmegaGateway gateway;
	private OmegaImage img;
	private final List<BufferedImage> buffImages;

	private Thread frameLoaderThread;
	private TBROIThumbnailLoader frameLoader;

	private Point clickPosition;
	private final boolean isSelectionEnabled;

	public GenericTrajectoriesBrowserTrajectoriesPanel(
	        final RootPaneContainer parent,
	        final GenericTrajectoriesBrowserPanel tbPanel,
	        final OmegaGateway gateway, final boolean isSelectionEnabled) {
		super(parent);
		this.tbPanel = tbPanel;

		this.particlesMap = new LinkedHashMap<Point, OmegaROI>();
		this.trajectoriesMap = new LinkedHashMap<Integer, OmegaTrajectory>();

		this.gateway = gateway;
		this.img = null;
		this.buffImages = new ArrayList<BufferedImage>();

		this.frameLoaderThread = null;
		this.frameLoader = null;

		this.clickPosition = null;

		this.isSelectionEnabled = isSelectionEnabled;
	}

	protected Point findTrajectoryLocation(final OmegaTrajectory trajectory) {
		final OmegaROI startingROI = trajectory.getROIs().get(0);
		for (final Point p : this.particlesMap.keySet()) {
			final OmegaROI roi = this.particlesMap.get(p);
			if (roi.equals(startingROI))
				return p;
		}
		return null;
	}

	protected void findSelectedTrajectory(final Point clickP) {
		final int adj1 = GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT / 4;
		final int adj2 = adj1 * 3;
		for (final Integer y : this.trajectoriesMap.keySet()) {
			if ((clickP.y > (y - adj1)) && (clickP.y < (y + adj2))) {
				this.tbPanel.setSelectedTrajectory(this.trajectoriesMap.get(y));
				return;
			}
		}
	}

	protected void findSelectedParticle(final Point clickP) {
		final int size = GenericTrajectoriesBrowserPanel.SPOT_SIZE_DEFAULT;
		final int border = GenericTrajectoriesBrowserPanel.TRAJECTORY_SQUARE_BORDER;
		for (final Point p : this.particlesMap.keySet()) {
			final int px = p.x - border;
			final int py = p.y - border;
			final int pX = p.x + size + border;
			final int pY = p.y + size + border;
			if ((clickP.x > px) && (clickP.x < pX)) {
				if ((clickP.y > py) && (clickP.y < pY)) {
					this.clickPosition = clickP;
					this.tbPanel.setSelectedParticle(this.particlesMap.get(p));
					return;
				}
			}
		}
	}

	private void drawAlternateTimeframeBackground(final Graphics2D g2D) {
		final int space = GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
		final int border = GenericTrajectoriesBrowserPanel.TRAJECTORY_SQUARE_BORDER;
		for (Integer x = 0; x < this.tbPanel.getSizeT(); x++) {
			Color bg;
			if ((x % 2) == 0) {
				bg = Color.gray;
			} else {
				bg = Color.white;
			}
			g2D.setBackground(bg);
			final int xPos = ((space * (x)) - border) + (space / 2);
			final int xAdj = xPos - (space / 2);
			final int yAdj = 0;
			final int width = space;
			final int height = this.getHeight();
			g2D.clearRect(xAdj, yAdj, width, height);
		}
		g2D.setBackground(Color.white);
	}

	private void drawSelectedTrajectoryBackground(final Graphics2D g2D) {
		for (int y = 0; y < this.tbPanel.getNumberOfTrajectories(); y++) {
			final OmegaTrajectory traj = this.tbPanel.getTrajectories().get(y);
			final int yPos = GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT
			        * y;
			final int adjY = yPos;
			if (this.tbPanel.getSelectedTrajectories().contains(traj)) {
				g2D.setBackground(OmegaConstants
				        .getDefaultSelectionBackgroundColor());
				g2D.clearRect(0, adjY, this.getWidth(),
				        GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT);
				g2D.setBackground(Color.white);
			}
		}
	}

	private void drawTrajectories(final Graphics2D g2D) {
		final int space = GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
		final int size = GenericTrajectoriesBrowserPanel.SPOT_SIZE_DEFAULT;
		final int border = GenericTrajectoriesBrowserPanel.TRAJECTORY_SQUARE_BORDER;
		this.trajectoriesMap.clear();
		this.particlesMap.clear();
		for (int y = 0; y < this.tbPanel.getNumberOfTrajectories(); y++) {
			final OmegaTrajectory traj = this.tbPanel.getTrajectories().get(y);
			final List<OmegaROI> rois = traj.getROIs();
			final int yPos = ((space * y) + (space / 4)) - border;
			if (!this.trajectoriesMap.containsKey(yPos)) {
				this.trajectoriesMap.put(yPos, traj);
			}
			for (int x = 0; x < rois.size(); x++) {
				final OmegaROI roi = rois.get(x);
				final int roiIndex = roi.getFrameIndex();
				BufferedImage bufferedImage = null;
				if (this.buffImages.size() > roiIndex) {
					bufferedImage = this.buffImages.get(roiIndex);
				}
				final int xPos = ((space * roiIndex) - (size / 2) - border)
				        + (space / 2);
				final Point p = new Point(xPos, yPos);
				this.particlesMap.put(p, roi);
				if (traj.getColor() == null) {
					// generate random colors
				}
				if (roiIndex < (rois.get(rois.size() - 1).getFrameIndex())) {
					final OmegaROI nextROI = rois.get(x + 1);
					final int nextROIIndex = nextROI.getFrameIndex();
					final int xNextPos = ((space * nextROIIndex) - (size / 2) - border)
					        + (space / 2);
					final Point p2 = new Point(xNextPos, yPos);
					this.drawConnection(g2D, p, p2, traj.getColor());
				}
				if ((bufferedImage == null) || !this.tbPanel.isShowParticles()) {
					this.drawParticleSquare(g2D, p, traj.getColor());
				} else {
					this.drawParticleImage(g2D, p, traj.getColor(),
					        bufferedImage, roi);
				}
			}

			// for (int x = 0; x < this.tbPanel.getSizeT(); x++) {
			// BufferedImage bufferedImage = null;
			// if (this.buffImages.size() > x) {
			// bufferedImage = this.buffImages.get(x);
			// }
			// for (int y = 0; y < this.tbPanel.getNumberOfTrajectories(); y++)
			// {
			// final OmegaTrajectory traj = this.tbPanel.getTrajectories()
			// .get(y);
			// final List<OmegaROI> rois = traj.getROIs();
			// final int yPos = ((space * y) + (space / 4)) - border;
			// if (!this.trajectoriesMap.containsKey(yPos)) {
			// this.trajectoriesMap.put(yPos, traj);
			// }
			// if (rois.size() <= x) {
			// continue;
			// }
			// final OmegaROI roi = rois.get(x);
			// final int roiIndex = roi.getFrameIndex();
			// if (roiIndex != x) {
			// continue;
			// }
			// System.out.println("I: " + x + " VS " + "PI: " + roiIndex);
			// final int xPos = ((space * roiIndex) - (size / 2) - border)
			// + (space / 2);
			// final Point p = new Point(xPos, yPos);
			// this.particlesMap.put(p, roi);
			// if (traj.getColor() == null) {
			// // generate random colors
			// }
			// // TODO add image from buffImage
			// if (roiIndex < (rois.get(rois.size() - 1).getFrameIndex())) {
			// final OmegaROI nextROI = rois.get(x + 1);
			// final int nextROIIndex = nextROI.getFrameIndex();
			// final int xNextPos = ((space * nextROIIndex) - (size / 2) -
			// border)
			// + (space / 2);
			// final Point p2 = new Point(xNextPos, yPos);
			// this.drawConnection(g2D, p, p2, traj.getColor());
			// }
			// if ((bufferedImage == null) || !this.tbPanel.isShowParticles()) {
			// this.drawParticleSquare(g2D, p, traj.getColor());
			// } else {
			// this.drawParticleImage(g2D, p, traj.getColor(),
			// bufferedImage, roi);
			// }
			// }
		}
	}

	private void drawConnection(final Graphics2D g2D, final Point p,
	        final Point p2, final Color c) {
		final int size = GenericTrajectoriesBrowserPanel.SPOT_SIZE_DEFAULT;
		g2D.setColor(c);
		final int width = p2.x - p.x;
		final int height = 3;
		g2D.fillRect(p.x + size, (p.y + (size / 2)) - 3, width, height);
		g2D.setColor(Color.white);
	}

	private void drawParticleSquare(final Graphics2D g2D, final Point p,
	        final Color color) {
		final int border = GenericTrajectoriesBrowserPanel.TRAJECTORY_SQUARE_BORDER;
		final int spotSize = GenericTrajectoriesBrowserPanel.SPOT_SIZE_DEFAULT
		        + (GenericTrajectoriesBrowserPanel.TRAJECTORY_SQUARE_BORDER * 2);
		g2D.setColor(color);
		g2D.fillRect(p.x - border, p.y - border, spotSize, spotSize);
		g2D.setColor(Color.black);
	}

	private void drawParticleImage(final Graphics2D g2D, final Point p,
	        final Color color, final BufferedImage bufferedImage,
	        final OmegaROI roi) {
		final int radius = this.tbPanel.getRadius();
		final int border = GenericTrajectoriesBrowserPanel.TRAJECTORY_SQUARE_BORDER;
		final int spotSize = GenericTrajectoriesBrowserPanel.SPOT_SIZE_DEFAULT
		        + (GenericTrajectoriesBrowserPanel.TRAJECTORY_SQUARE_BORDER * 2);
		final double xD = roi.getX();
		final double yD = roi.getY();
		final int x1 = new BigDecimal(String.valueOf(xD)).setScale(0,
		        BigDecimal.ROUND_HALF_UP).intValue();
		final int y1 = new BigDecimal(String.valueOf(yD)).setScale(0,
		        BigDecimal.ROUND_HALF_UP).intValue();
		g2D.setColor(color);
		g2D.fillRect(p.x - border, p.y - border, spotSize, spotSize);
		g2D.setColor(Color.black);
		final int xS1 = x1 - radius;
		final int yS1 = y1 - radius;
		final int xS2 = x1 + radius;
		final int yS2 = y1 + radius;
		g2D.drawImage(bufferedImage, p.x, p.y, p.x
		        + (GenericTrajectoriesBrowserPanel.SPOT_SIZE_DEFAULT), p.y
		        + (GenericTrajectoriesBrowserPanel.SPOT_SIZE_DEFAULT), xS1,
		        yS1, xS2, yS2, this);
	}

	@Override
	public void paint(final Graphics g) {
		if (this.tbPanel.getTrajectories() == null)
			return;
		// this.setPanelSize();

		final Graphics2D g2D = (Graphics2D) g;
		g2D.setBackground(Color.white);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
		        RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.clearRect(0, 0, this.getWidth(), this.getHeight());

		this.drawAlternateTimeframeBackground(g2D);
		if (this.isSelectionEnabled) {
			this.drawSelectedTrajectoryBackground(g2D);
		}

		this.drawTrajectories(g2D);

		this.tbPanel.addToPaint(g2D);
	}

	protected void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
	}

	protected void setImage(final OmegaImage img) {
		if ((this.img == img))
			return;
		this.img = img;
		this.buffImages.clear();
		if ((img == null)
		        || ((this.frameLoaderThread != null) && this.frameLoaderThread
		                .isAlive())) {
			this.frameLoader.kill();
			// try {
			// this.frameLoaderThread.join();
			// } catch (final InterruptedException ex) {
			// OmegaLogFileManager.handleCoreException(ex);
			// }
		}
		this.frameLoader = new TBROIThumbnailLoader(this.tbPanel, this.gateway,
		        this.img);
		this.frameLoaderThread = new Thread(this.frameLoader);
		this.frameLoaderThread.setName(this.frameLoader.getClass()
		        .getSimpleName());
		OmegaLogFileManager
		        .registerAsExceptionHandlerOnThread(this.frameLoaderThread);
		this.frameLoaderThread.start();
	}

	public void loadBufferedImages() {
		this.buffImages.clear();
		this.buffImages.addAll(this.frameLoader.getImages());
		this.repaint();
	}

	public void setPanelSize() {
		final int sizeT = this.tbPanel.getSizeT();
		final int numOfTraj = this.tbPanel.getNumberOfTrajectories();
		if ((sizeT < 1) || (numOfTraj < 1))
			return;
		int width = sizeT * GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
		int height = numOfTraj
		        * GenericTrajectoriesBrowserPanel.SPOT_SPACE_DEFAULT;
		final Dimension parentDim = this.tbPanel.getTrajectoriesPanelSize();
		if (parentDim.width > width) {
			width = parentDim.width;
		}
		if (parentDim.height > height) {
			height = parentDim.height;
		}
		final Dimension dim = new Dimension(width, height);
		this.setPreferredSize(dim);
		this.setSize(dim);
	}

	protected Point getClickPosition() {
		return this.clickPosition;
	}
}
