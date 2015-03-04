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
import edu.umassmed.omega.trackingMeasuresPlugin.gui.TMMobilityPanel;

public class TMMobilityGraphProducer extends TMGraphProducer {

	private final TMMobilityPanel mobilityPanel;

	private final int mobilityOption;
	private final boolean isTimepointsGraph;
	private final int maxT;
	private final Map<OmegaTrajectory, List<Double>> distanceMap;
	private final Map<OmegaTrajectory, List<Double>> displacementMap;
	private final Map<OmegaTrajectory, Double> maxDisplacementMap;
	private final Map<OmegaTrajectory, Integer> totalTimeTraveledMap;
	private final Map<OmegaTrajectory, List<Double>> confinementRatioMap;
	final Map<OmegaTrajectory, List<Double[]>> anglesAndDirectionalChangesMap;

	private ChartPanel graphPanel;

	public TMMobilityGraphProducer(
	        final TMMobilityPanel mobilityPanel,
	        final int graphType,
	        final int mobilityOption,
	        final boolean isTimepointsGraph,
	        final int tMax,
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
	        final Map<OmegaTrajectory, List<Double>> distanceMap,
	        final Map<OmegaTrajectory, List<Double>> displacementMap,
	        final Map<OmegaTrajectory, Double> maxDisplacementMap,
	        final Map<OmegaTrajectory, Integer> totalTimeTraveledMap,
	        final Map<OmegaTrajectory, List<Double>> confinementRatioMap,
	        final Map<OmegaTrajectory, List<Double[]>> anglesAndDirectionalChangesMap) {
		super(graphType, segmentsMap);
		this.mobilityPanel = mobilityPanel;
		this.mobilityOption = mobilityOption;
		this.isTimepointsGraph = isTimepointsGraph;
		this.maxT = tMax;
		this.distanceMap = distanceMap;
		this.displacementMap = displacementMap;
		this.maxDisplacementMap = maxDisplacementMap;
		this.totalTimeTraveledMap = totalTimeTraveledMap;
		this.confinementRatioMap = confinementRatioMap;
		this.anglesAndDirectionalChangesMap = anglesAndDirectionalChangesMap;
		this.graphPanel = null;
	}

	@Override
	public void run() {
		super.run();
		if (this.isTimepointsGraph) {
			this.prepareTimepointsGraph();
		} else {
			this.prepareTracksGraph();
		}
	}

	private String getTitle() {
		String title;
		switch (this.mobilityOption) {
		case TMMobilityPanel.OPTION_DISPLACEMENT:
			title = "Total net displacement";
			break;
		case TMMobilityPanel.OPTION_MAX_DISPLACEMENT:
			title = "Max displacement";
			break;
		case TMMobilityPanel.OPTION_TOTAL_TIME_TRAVELED:
			title = "Total time traveled";
			break;
		case TMMobilityPanel.OPTION_CONFINEMENT_RATIO:
			title = "Confinement ratio";
			break;
		case TMMobilityPanel.OPTION_LOCAL_ANGLES:
			title = "Local angles";
			break;
		case TMMobilityPanel.OPTION_LOCAL_DIRECTIONAL_CHANGES:
			title = "Local directional changes";
			break;
		default:
			title = "Total distance traveled";
		}
		return title;
	}

	private String getYAxisTitle() {
		String yAxisTitle;
		switch (this.mobilityOption) {
		case TMMobilityPanel.OPTION_DISPLACEMENT:
		case TMMobilityPanel.OPTION_MAX_DISPLACEMENT:
			yAxisTitle = "Displacement";
			break;
		case TMMobilityPanel.OPTION_TOTAL_TIME_TRAVELED:
			yAxisTitle = "Time";
			break;
		case TMMobilityPanel.OPTION_CONFINEMENT_RATIO:
			yAxisTitle = "Ratio";
			break;
		case TMMobilityPanel.OPTION_LOCAL_ANGLES:
			yAxisTitle = "Angle";
			break;
		case TMMobilityPanel.OPTION_LOCAL_DIRECTIONAL_CHANGES:
			yAxisTitle = "Directional change";
			break;
		default:
			yAxisTitle = "Distance";
		}
		return yAxisTitle;
	}

	@Override
	protected Double getValue(final OmegaTrajectory track, final OmegaROI roi) {
		List<Double[]> valuesList = null;
		Double[] localValues = null;
		List<Double> values = null;
		Double value = null;
		switch (this.mobilityOption) {
		case TMMobilityPanel.OPTION_DISPLACEMENT:
			values = this.displacementMap.get(track);
			value = values.get(roi.getFrameIndex());
			break;
		case TMMobilityPanel.OPTION_MAX_DISPLACEMENT:
			value = this.maxDisplacementMap.get(track);
			break;
		case TMMobilityPanel.OPTION_TOTAL_TIME_TRAVELED:
			value = Double.valueOf(this.totalTimeTraveledMap.get(track));
			break;
		case TMMobilityPanel.OPTION_CONFINEMENT_RATIO:
			values = this.confinementRatioMap.get(track);
			value = values.get(roi.getFrameIndex());
			break;
		case TMMobilityPanel.OPTION_LOCAL_ANGLES:
			valuesList = this.anglesAndDirectionalChangesMap.get(track);
			localValues = null;
			localValues = valuesList.get(roi.getFrameIndex());
			value = localValues[0];
			break;
		case TMMobilityPanel.OPTION_LOCAL_DIRECTIONAL_CHANGES:
			valuesList = this.anglesAndDirectionalChangesMap.get(track);
			localValues = valuesList.get(roi.getFrameIndex());
			value = localValues[1];
			break;
		default:
			values = this.distanceMap.get(track);
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
			final List<OmegaROI> rois = track.getROIs();
			final OmegaROI roi = rois.get(rois.size() - 1);
			final Double value = this.getValue(track, roi);
			if (dataset instanceof HistogramDataset) {
				histValues.add(value);
			} else {
				final DefaultCategoryDataset catDataset = (DefaultCategoryDataset) dataset;
				catDataset.addValue(value, title, name);
			}
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

		// this.graphPanel = new GenericGraphPanel(xySeriesCollection,
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
					TMMobilityGraphProducer.this.mobilityPanel.updateStatus(
					        TMMobilityGraphProducer.this.getCompleted(), ended,
					        TMMobilityGraphProducer.this.graphPanel);
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
