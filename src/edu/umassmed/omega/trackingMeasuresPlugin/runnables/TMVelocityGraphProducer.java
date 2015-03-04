package edu.umassmed.omega.trackingMeasuresPlugin.runnables;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import edu.umassmed.omega.core.OmegaLogFileManager;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.trackingMeasuresPlugin.gui.TMVelocityPanel;

public class TMVelocityGraphProducer extends TMGraphProducer {

	private final TMVelocityPanel velocityPanel;

	private final int velocityOption;
	private final int maxT;
	private final Map<OmegaTrajectory, List<Double>> localSpeedMap;
	private final Map<OmegaTrajectory, List<Double>> localVelocityMap;
	private final Map<OmegaTrajectory, Double> meanSpeedMap;
	private final Map<OmegaTrajectory, Double> meanVelocityMap;

	private ChartPanel graphPanel;

	public TMVelocityGraphProducer(final TMVelocityPanel velocityPanel,
	        final int graphType, final int velocityOption, final int tMax,
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
	        final Map<OmegaTrajectory, List<Double>> localSpeedMap,
	        final Map<OmegaTrajectory, List<Double>> localVelocityMap,
	        final Map<OmegaTrajectory, Double> meanSpeedMap,
	        final Map<OmegaTrajectory, Double> meanVelocityMap) {
		super(graphType, segmentsMap);
		this.velocityPanel = velocityPanel;
		this.velocityOption = velocityOption;
		this.maxT = tMax;
		this.localSpeedMap = localSpeedMap;
		this.localVelocityMap = localVelocityMap;
		this.meanSpeedMap = meanSpeedMap;
		this.meanVelocityMap = meanVelocityMap;
		this.graphPanel = null;
	}

	// public TMMobilityGraphProducer(final int distDispOption,final int tMax,
	// final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
	// final Map<OmegaTrajectory, List<Double[]>> motilityMap) {
	// this.completed = 0.0;
	// this.distDispOption = distDispOption;
	// this.isTimepointsGraph = false;
	// this.maxT = tMax;
	// this.segmentsMap = segmentsMap;
	// this.motilityMap = motilityMap;
	//
	// this.graphPanel = null;
	// }

	@Override
	public void run() {
		super.run();
		switch (this.velocityOption) {
		case TMVelocityPanel.OPTION_LOCAL_VELOCITY:
		case TMVelocityPanel.OPTION_LOCAL_SPEED:
			this.prepareTimepointsGraph();
			break;
		default:
			this.prepareTracksGraph();
		}
	}

	private String getTitle() {
		String title;
		switch (this.velocityOption) {
		case TMVelocityPanel.OPTION_LOCAL_VELOCITY:
			title = "Local velocity";
			break;
		case TMVelocityPanel.OPTION_MEAN_SPEED:
			title = "Mean speed";
			break;
		case TMVelocityPanel.OPTION_MEAN_VELOCITY:
			title = "Mean velocity";
			break;
		default:
			title = "Local speed";
		}
		return title;
	}

	private String getYAxisTitle() {
		String yAxisTitle;
		switch (this.velocityOption) {
		case TMVelocityPanel.OPTION_LOCAL_VELOCITY:
		case TMVelocityPanel.OPTION_MEAN_VELOCITY:
			yAxisTitle = "Velocity";
			break;
		default:
			yAxisTitle = "Speed";
		}
		return yAxisTitle;
	}

	@Override
	protected Double getValue(final OmegaTrajectory track, final OmegaROI roi) {
		List<Double> values = null;
		Double value = null;
		switch (this.velocityOption) {
		case TMVelocityPanel.OPTION_MEAN_VELOCITY:
			value = this.meanVelocityMap.get(track);
			break;
		case TMVelocityPanel.OPTION_MEAN_SPEED:
			value = this.meanSpeedMap.get(track);
			break;
		case TMVelocityPanel.OPTION_LOCAL_VELOCITY:
			values = this.localVelocityMap.get(track);
			value = values.get(roi.getFrameIndex());
			break;
		default:
			values = this.localSpeedMap.get(track);
			value = values.get(roi.getFrameIndex());
		}
		return value;
	}

	private void prepareTracksGraph() {
		final String title = this.getTitle();
		Dataset dataset = null;
		if (this.getGraphType() == TMGraphProducer.HISTOGRAM_GRAPH) {
			dataset = new HistogramDataset();
			((HistogramDataset) dataset).setType(HistogramType.FREQUENCY);
		} else {
			dataset = new DefaultCategoryDataset();
		}
		final double partial = 100.0 / this.getSegmentsMap().keySet().size();
		final double increase = new BigDecimal(partial).setScale(2,
		        RoundingMode.HALF_UP).doubleValue();
		final CategoryItemRenderer renderer = this.getTracksRenderer();
		final List<Double> histValues = new ArrayList<>();
		for (final OmegaTrajectory track : this.getSegmentsMap().keySet()) {
			if (this.isTerminated())
				return;
			final String name = track.getName();
			final Double value = this.getValue(track, null);
			if (dataset instanceof HistogramDataset) {
				histValues.add(value);
			} else {
				final DefaultCategoryDataset catDataset = (DefaultCategoryDataset) dataset;
				catDataset.addValue(value, title, name);
			}
			// dataset.addValue(value, title, name);
			this.updateStatus(false);
			this.increaseCompletion(increase);
		}

		if (dataset instanceof HistogramDataset) {
			final double[] data = new double[histValues.size()];
			for (int i = 0; i < histValues.size(); i++) {
				data[i] = histValues.get(i);
			}
			((HistogramDataset) dataset).addSeries(title, data, data.length);
		}

		final CategoryAxis xAxis = new CategoryAxis("Tracks");
		final NumberAxis yAxis = new NumberAxis(this.getYAxisTitle());

		// renderer.setSeriesFillPaint(0, Color.black);
		Plot plot = null;
		JFreeChart chart = null;
		if (dataset instanceof HistogramDataset) {
			final HistogramDataset histDataset = (HistogramDataset) dataset;
			chart = ChartFactory.createHistogram(title, this.getYAxisTitle(),
			        "Frequency", histDataset, PlotOrientation.VERTICAL, true,
			        true, true);
			plot = chart.getPlot();
		} else {
			final DefaultCategoryDataset catDataset = (DefaultCategoryDataset) dataset;
			plot = new CategoryPlot(catDataset, xAxis, yAxis, renderer);
			chart = new JFreeChart(title, plot);
		}
		plot.setBackgroundPaint(Color.WHITE);

		this.graphPanel = new ChartPanel(chart);

		// this.graphPanel = new GenericGraphPanel(dataset,
		// GenericGraphPanel.GRAPH_KIND_LINE, title, "Tracks",
		// this.getYAxisTitle(), GenericGraphPanel.PLOT_VERTICAL, true,
		// true, false);
		this.updateStatus(true);
	}

	private void prepareTimepointsGraph() {
		final List<OmegaTrajectory> drawTracks = new ArrayList<>();
		final String title = this.getTitle();
		Dataset dataset = null;
		if (this.getGraphType() == TMGraphProducer.HISTOGRAM_GRAPH) {
			dataset = new HistogramDataset();
			((HistogramDataset) dataset).setType(HistogramType.FREQUENCY);
		} else {
			dataset = new DefaultCategoryDataset();
		}
		final double partial = 100.0 / (this.maxT * this.getSegmentsMap()
		        .keySet().size());
		final double increase = new BigDecimal(partial).setScale(2,
		        RoundingMode.HALF_UP).doubleValue();
		for (Integer t = 0; t < this.maxT; t++) {
			final List<Double> histValues = new ArrayList<>();
			for (final OmegaTrajectory track : this.getSegmentsMap().keySet()) {
				if (!drawTracks.contains(track)) {
					drawTracks.add(track);
				}
				final String name = track.getName();
				final List<OmegaROI> rois = track.getROIs();
				boolean found = false;
				for (final OmegaROI roi : rois) {
					if (this.isTerminated())
						return;
					// TODO FIND A SMARTER WAY TO DO IT HERE
					final Integer timepoint = roi.getFrameIndex();
					if (timepoint != t) {
						continue;
					}
					final Double value = this.getValue(track, roi);
					if (dataset instanceof HistogramDataset) {
						histValues.add(value);
					} else {
						final DefaultCategoryDataset catDataset = (DefaultCategoryDataset) dataset;
						catDataset.addValue(value, name, timepoint);
						found = true;
					}
					// dataset.addValue(value, name, timepoint);
				}
				if (!found) {
					if (dataset instanceof CategoryDataset) {
						final DefaultCategoryDataset catDataset = (DefaultCategoryDataset) dataset;
						catDataset.addValue(null, name, t);
					}
				}
			}
			if (dataset instanceof HistogramDataset) {
				final double[] data = new double[histValues.size()];
				for (int i = 0; i < histValues.size(); i++) {
					data[i] = histValues.get(i) == null ? 0 : histValues.get(i);
				}
				((HistogramDataset) dataset).addSeries(t, data, data.length);
			}
			this.updateStatus(false);
			this.increaseCompletion(increase);
		}

		Map<String, Map<Integer, Boolean>> renderingMap = null;
		if ((dataset instanceof CategoryDataset)
		        && (this.getGraphType() == TMGraphProducer.LINE_GRAPH)) {
			renderingMap = this
			        .createRenderingMap((DefaultCategoryDataset) dataset);
		}
		final CategoryItemRenderer renderer = this
		        .getTimepointsRenderer(renderingMap);

		final CategoryAxis xAxis = new CategoryAxis("Timepoints");
		// xAxis.setTickUnit(new NumberTickUnit(1.0));
		final NumberAxis yAxis = new NumberAxis(this.getYAxisTitle());

		Plot plot = null;
		JFreeChart chart = null;
		if (dataset instanceof HistogramDataset) {
			final HistogramDataset histDataset = (HistogramDataset) dataset;
			chart = ChartFactory.createHistogram(title, this.getYAxisTitle(),
			        "Frequency", histDataset, PlotOrientation.VERTICAL, true,
			        true, true);
			plot = chart.getPlot();
		} else {
			final DefaultCategoryDataset catDataset = (DefaultCategoryDataset) dataset;
			plot = new CategoryPlot(catDataset, xAxis, yAxis, renderer);
			chart = new JFreeChart(title, plot);
		}
		plot.setBackgroundPaint(Color.WHITE);

		this.graphPanel = new ChartPanel(chart);

		// this.graphPanel = new GenericGraphPanel(dataset,
		// GenericGraphPanel.GRAPH_KIND_LINE, title, "Timepoints",
		// this.getYAxisTitle(), GenericGraphPanel.PLOT_VERTICAL, true,
		// true, false);
		// this.graphPanel.changeColors(drawTracks);
		this.updateStatus(true);
	}

	public void updateStatus(final boolean ended) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					TMVelocityGraphProducer.this.velocityPanel.updateStatus(
					        TMVelocityGraphProducer.this.getCompleted(), ended,
					        TMVelocityGraphProducer.this.graphPanel);
				}
			});
		} catch (final InvocationTargetException | InterruptedException ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}

	public ChartPanel getGraph() {
		return this.graphPanel;
	}
}
