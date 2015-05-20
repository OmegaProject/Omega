package edu.umassmed.omega.trackingMeasuresPlugin.runnables;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.DefaultCategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import edu.umassmed.omega.data.trajectoryElements.OmegaParticle;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public abstract class TMGraphProducer implements Runnable {

	public final static int LINE_GRAPH = 0;
	public final static int BAR_GRAPH = 1;
	public final static int HISTOGRAM_GRAPH = 2;

	private CategoryItemRenderer categoryItemRenderer;
	private CategoryItemRenderer xyLineAndShapeRenderer;

	private volatile boolean isTerminated;
	private double completed;

	private final int graphType;

	private final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap;

	public TMGraphProducer(final int graphType,
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		this.segmentsMap = segmentsMap;

		this.isTerminated = false;
		this.completed = 0.0;

		this.graphType = graphType;

		this.categoryItemRenderer = null;
		this.xyLineAndShapeRenderer = null;
	}

	/**
	 * Category Item renderer that is drawing item based on track color
	 *
	 * @return
	 */
	public CategoryItemRenderer getTracksRenderer() {
		if (this.categoryItemRenderer == null) {
			switch (this.graphType) {
			case BAR_GRAPH:
				this.createTracksBarRenderer();
				break;
			case HISTOGRAM_GRAPH:
				this.createTracksHistogramBarRenderer();
				break;
			default:
				this.createTracksLineRenderer();
			}

		}
		return this.categoryItemRenderer;
	}

	private void createTracksBarRenderer() {
		this.categoryItemRenderer = new BarRenderer() {
			private static final long serialVersionUID = 3343456141507762482L;

			@Override
			public Paint getItemPaint(final int row, final int column) {
				final String name = (String) this.getPlot().getDataset()
						.getColumnKey(column);
				for (final OmegaTrajectory track : TMGraphProducer.this.segmentsMap
						.keySet()) {
					if (track.getName().equals(name))
						return track.getColor();
				}
				return Color.black;
			}
		};
	}

	private void createTracksHistogramBarRenderer() {
		this.categoryItemRenderer = new BarRenderer() {
			private static final long serialVersionUID = -1976632258511076322L;

			// @Override
			// public Paint getItemPaint(final int row, final int column) {
			// final String name = (String) this.getPlot().getDataset()
			// .getColumnKey(column);
			// for (final OmegaTrajectory track :
			// TMGraphProducer.this.segmentsMap
			// .keySet()) {
			// if (track.getName().equals(name))
			// return track.getColor();
			// }
			// return Color.black;
			// }

		};
	}

	private void createTracksLineRenderer() {
		this.categoryItemRenderer = new DefaultCategoryItemRenderer() {
			private static final long serialVersionUID = 3343456141507762482L;

			// @Override
			// public Paint getItemPaint(final int row, final int column) {
			// final String name = (String) this.getPlot().getDataset()
			// .getColumnKey(column);
			// for (final OmegaTrajectory track :
			// TMGraphProducer.this.segmentsMap
			// .keySet()) {
			// if (track.getName().equals(name))
			// return track.getColor();
			// }
			// return Color.black;
			// }
		};
	}

	/**
	 * Line and Shape renderer that is drawing dashed line between between
	 * missing timepoints and solid line in other cases
	 *
	 * @param renderingMap
	 *
	 * @return
	 */
	public CategoryItemRenderer getTimepointsRenderer(
			final Map<String, Map<Integer, Boolean>> renderingMap) {
		if (this.xyLineAndShapeRenderer == null) {
			switch (this.graphType) {
			case BAR_GRAPH:
				this.createTimepointsBarRenderer(renderingMap);
				break;
			case HISTOGRAM_GRAPH:
				this.createTimepointsHistogramBarRenderer();
				break;
			default:
				this.createTimepointsLineRenderer(renderingMap);
			}

		}
		return this.xyLineAndShapeRenderer;
	}

	private void createTimepointsBarRenderer(
			final Map<String, Map<Integer, Boolean>> renderingMap) {
		this.xyLineAndShapeRenderer = new BarRenderer() {
			private static final long serialVersionUID = -4868788326156259962L;

			@Override
			public boolean getItemVisible(final int series, final int item) {
				final DefaultCategoryDataset dataset = (DefaultCategoryDataset) this
						.getPlot().getDataset();
				final String name = (String) dataset.getRowKey(series);
				final Map<Integer, Boolean> renderingList = renderingMap
						.get(name);
				if (!renderingList.containsKey(item))
					return false;
				final boolean bool = renderingList.get(item);
				return bool;
			}

			@Override
			public Paint getSeriesPaint(final int series) {
				final String trackName = (String) this.getPlot().getDataset()
						.getRowKey(series);
				for (final OmegaTrajectory track : TMGraphProducer.this.segmentsMap
						.keySet()) {
					if (track.getName().equals(trackName))
						return track.getColor();
				}
				return Color.black;
			}
		};
	}

	private void createTimepointsHistogramBarRenderer() {
		this.xyLineAndShapeRenderer = new BarRenderer() {
			private static final long serialVersionUID = -4868788326156259962L;

		};
	}

	private void createTimepointsLineRenderer(
			final Map<String, Map<Integer, Boolean>> renderingMap) {
		this.xyLineAndShapeRenderer = new DefaultCategoryItemRenderer() {
			private static final long serialVersionUID = 1071820316920620277L;

			// @Override
			// public boolean getItemLineVisible(final int series, final int
			// item) {
			// System.out.println("IL " + series + " - " + item);
			// final DefaultCategoryDataset dataset = (DefaultCategoryDataset)
			// this
			// .getPlot().getDataset();
			// final String name = (String) dataset.getRowKey(series);
			// final Map<Integer, Boolean> renderingList = renderingMap
			// .get(name);
			// if (!renderingList.containsKey(item))
			// return false;
			// return true;
			// }
			//
			// @Override
			// public boolean getItemVisible(final int series, final int item) {
			// System.out.println("I " + series + " - " + item);
			// final DefaultCategoryDataset dataset = (DefaultCategoryDataset)
			// this
			// .getPlot().getDataset();
			// final String name = (String) dataset.getRowKey(series);
			// final Map<Integer, Boolean> renderingList = renderingMap
			// .get(name);
			// if (!renderingList.containsKey(item))
			// return false;
			// return renderingList.get(item);
			// }

			@Override
			public boolean getItemShapeVisible(final int series, final int item) {
				final DefaultCategoryDataset dataset = (DefaultCategoryDataset) this
						.getPlot().getDataset();
				final String name = (String) dataset.getRowKey(series);
				final Map<Integer, Boolean> renderingList = renderingMap
						.get(name);
				if (!renderingList.containsKey(item))
					return false;
				final boolean bool = renderingList.get(item);
				return bool;
			}

			@Override
			public Stroke getItemStroke(final int row, final int column) {
				if ((column - 1) < 0)
					return new BasicStroke(1.0f);

				final DefaultCategoryDataset dataset = (DefaultCategoryDataset) this
						.getPlot().getDataset();
				final String name = (String) dataset.getRowKey(row);
				final Map<Integer, Boolean> renderingList = renderingMap
						.get(name);

				if (!renderingList.containsKey(column)
						|| !renderingList.containsKey(column - 1))
					return new BasicStroke(1.0f);

				final Boolean bool1 = renderingList.get(column);
				final Boolean bool2 = renderingList.get(column - 1);

				if (!bool1 || !bool2)
					return new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
							BasicStroke.JOIN_MITER, 1.0f, new float[] { 5.0f,
							5.0f }, 0.0f);
				else
					return new BasicStroke(1.0f);
			}

			@Override
			public Paint getSeriesPaint(final int series) {
				final String trackName = (String) this.getPlot().getDataset()
						.getRowKey(series);
				for (final OmegaTrajectory track : TMGraphProducer.this.segmentsMap
						.keySet()) {
					if (track.getName().equals(trackName))
						return track.getColor();
				}
				return Color.black;
			}
		};
	}

	@Override
	public void run() {
		this.isTerminated = false;
		this.completed = 0.0;
	}

	protected abstract Double getValue(OmegaTrajectory track, OmegaROI roi);

	protected Map<String, Map<Integer, Boolean>> createRenderingMap(
			final DefaultCategoryDataset catDataset) {
		final Map<String, Map<Integer, Boolean>> renderingMap = new LinkedHashMap<>();
		for (final OmegaTrajectory track : this.getSegmentsMap().keySet()) {
			final String name = track.getName();
			final Map<Integer, Boolean> renderingList = new LinkedHashMap<>();
			Integer oldIndex = null;
			Double oldVal = null;
			for (final OmegaROI roi : track.getROIs()) {
				final OmegaParticle particle = (OmegaParticle) roi;
				final int newIndex = particle.getFrameIndex();
				final Double newVal = this.getValue(track, particle);
				if (newVal == null) {
					continue;
				}

				if ((oldIndex != null) && (oldVal != null)) {
					final int delta = newIndex - oldIndex;
					final Double deltaVal = (newVal - oldVal) / delta;
					for (int i = 1; i < delta; i++) {
						final Integer t = oldIndex + i;
						final Double val = oldVal + (deltaVal * i);
						catDataset.setValue(val, name, t);
						renderingList.put(t, false);
					}
				}
				renderingList.put(newIndex, true);
				oldIndex = newIndex;
				oldVal = newVal;
			}
			renderingMap.put(name, renderingList);
		}
		return renderingMap;
	}

	public void increaseCompletion(final double increase) {
		this.completed += increase;
		if (this.completed > 100.0) {
			this.completed = 100.0;
		}
	}

	public Map<OmegaTrajectory, List<OmegaSegment>> getSegmentsMap() {
		return this.segmentsMap;
	}

	protected void setCompleted(final double completed) {
		this.completed = completed;
	}

	public double getCompleted() {
		return this.completed;
	}

	public boolean isTerminated() {
		return this.isTerminated;
	}

	public void terminate() {
		this.isTerminated = true;
	}

	public int getGraphType() {
		return this.graphType;
	}
}
