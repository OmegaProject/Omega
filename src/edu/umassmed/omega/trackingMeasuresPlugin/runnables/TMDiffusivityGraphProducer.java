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
import edu.umassmed.omega.trackingMeasuresPlugin.gui.TMDiffusivityPanel;

public class TMDiffusivityGraphProducer extends TMGraphProducer {

	private final TMDiffusivityPanel diffusivityPanel;

	private final int diffusivityOption;
	private final int maxT;
	final Map<OmegaTrajectory, Double[]> nyMap;
	private final Map<OmegaTrajectory, Double[][]> logMuMap;
	private final Map<OmegaTrajectory, Double[][]> muMap;
	private final Map<OmegaTrajectory, Double[][]> logDeltaTMap;
	private final Map<OmegaTrajectory, Double[][]> deltaTMap;
	private final Map<OmegaTrajectory, Double[][]> gammaDFromLogMap;
	private final Map<OmegaTrajectory, Double[][]> gammaDMap;
	private final Map<OmegaTrajectory, Double[]> gammaFromLogMap;
	private final Map<OmegaTrajectory, Double[]> gammaMap;
	private final Map<OmegaTrajectory, Double[]> smssFromLogMap;
	private final Map<OmegaTrajectory, Double[]> smssMap;

	private ChartPanel graphPanel;

	public TMDiffusivityGraphProducer(
	        final TMDiffusivityPanel diffusivityPanel, final int graphType,
	        final int diffusivityOption, final int tMax,
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
	        final Map<OmegaTrajectory, Double[]> nyMap,
	        final Map<OmegaTrajectory, Double[][]> muMap,
	        final Map<OmegaTrajectory, Double[][]> logMuMap,
	        final Map<OmegaTrajectory, Double[][]> deltaTMap,
	        final Map<OmegaTrajectory, Double[][]> logDeltaTMap,
	        final Map<OmegaTrajectory, Double[][]> gammaDMap,
	        final Map<OmegaTrajectory, Double[][]> gammaDLogMap,
	        final Map<OmegaTrajectory, Double[]> gammaMap,
	        final Map<OmegaTrajectory, Double[]> gammaLogMap,
	        final Map<OmegaTrajectory, Double[]> smssMap,
	        final Map<OmegaTrajectory, Double[]> smssLogMap) {
		super(graphType, segmentsMap);
		this.diffusivityPanel = diffusivityPanel;
		this.diffusivityOption = diffusivityOption;
		this.maxT = tMax;
		this.nyMap = nyMap;
		this.logMuMap = logMuMap;
		this.muMap = muMap;
		this.logDeltaTMap = logDeltaTMap;
		this.deltaTMap = deltaTMap;
		this.gammaDFromLogMap = gammaDLogMap;
		this.gammaDMap = gammaDMap;
		this.gammaFromLogMap = gammaLogMap;
		this.gammaMap = gammaMap;
		this.smssFromLogMap = smssLogMap;
		this.smssMap = smssMap;
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
		switch (this.diffusivityOption) {
		default:
			this.prepareTracksGraph();
		}
	}

	private String getTitle() {
		String title = "";
		switch (this.diffusivityOption) {
		case TMDiffusivityPanel.OPTION_TRACK_SLOPE_MSS:
			title = "Slope MSS";
			break;
		case TMDiffusivityPanel.OPTION_TRACK_D:
			title = "Diffusion coefficent (order 2)";
			break;
		case TMDiffusivityPanel.OPTION_TRACK_ERROR_D:
			title = "Error D";
			break;
		case TMDiffusivityPanel.OPTION_TRACK_ERROR_SMSS:
			title = "Error SMSS";
			break;
		default:
			title = "Slope log MSD (order 2)";
		}
		return title;
	}

	@Override
	protected Double getValue(final OmegaTrajectory track, final OmegaROI roi) {
		Double[][] array = null;
		Double[] values = null;
		Double value = null;
		switch (this.diffusivityOption) {
		case TMDiffusivityPanel.OPTION_TRACK_SLOPE_MSS:
			values = this.smssFromLogMap.get(track);
			value = values[0];
			break;
		case TMDiffusivityPanel.OPTION_TRACK_D:
			array = this.gammaDFromLogMap.get(track);
			values = array[2];
			value = values[3];
			break;
		// case TMDiffusivityPanel.OPTION_TRACK_ERROR_SMSS:
		// value = this.meanVelocityMap.get(track);
		// break;
		// case TMDiffusivityPanel.OPTION_TRACK_ERROR_D:
		// value = this.meanVelocityMap.get(track);
		// break;
		default:
			array = this.gammaDFromLogMap.get(track);
			values = array[2];
			value = values[0];
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
			if (!this.nyMap.containsKey(track)) {
				continue;
			}
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
		final NumberAxis yAxis = new NumberAxis(title);

		// renderer.setSeriesFillPaint(0, Color.black);
		Plot plot = null;
		JFreeChart chart = null;
		if (dataset instanceof HistogramDataset) {
			final HistogramDataset histDataset = (HistogramDataset) dataset;
			chart = ChartFactory.createHistogram(title, title, "Frequency",
			        histDataset, PlotOrientation.VERTICAL, true, true, true);
			plot = chart.getPlot();
		} else {
			final DefaultCategoryDataset catDataset = (DefaultCategoryDataset) dataset;
			plot = new CategoryPlot(catDataset, xAxis, yAxis, renderer);
			chart = new JFreeChart(title, plot);
		}
		plot.setBackgroundPaint(Color.WHITE);

		this.graphPanel = new ChartPanel(chart);

		// this.graphPanel = new GenericGraphPanel(dataset,
		// GenericGraphPanel.GRAPH_KIND_LINE, title, "Tracks", title,
		// GenericGraphPanel.PLOT_VERTICAL, true, true, false);
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
					// List<Double> values = null;
					final Double value = null;
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
		final NumberAxis yAxis = new NumberAxis(title);

		Plot plot = null;
		JFreeChart chart = null;
		if (dataset instanceof HistogramDataset) {
			final HistogramDataset histDataset = (HistogramDataset) dataset;
			chart = ChartFactory.createHistogram(title, title, "Frequency",
			        histDataset, PlotOrientation.VERTICAL, true, true, true);
			plot = chart.getPlot();
		} else {
			final DefaultCategoryDataset catDataset = (DefaultCategoryDataset) dataset;
			plot = new CategoryPlot(catDataset, xAxis, yAxis, renderer);
			chart = new JFreeChart(title, plot);
		}
		plot.setBackgroundPaint(Color.WHITE);

		this.graphPanel = new ChartPanel(chart);

		// this.graphPanel = new GenericGraphPanel(dataset,
		// GenericGraphPanel.GRAPH_KIND_LINE, title, "Timepoints", title,
		// GenericGraphPanel.PLOT_VERTICAL, true, true, false);
		// this.graphPanel.changeColors(drawTracks);
		this.updateStatus(true);
	}

	public void updateStatus(final boolean ended) {
		if (this.isTerminated())
			return;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					TMDiffusivityGraphProducer.this.diffusivityPanel
					        .updateStatus(TMDiffusivityGraphProducer.this
					                .getCompleted(), ended,
					                TMDiffusivityGraphProducer.this.graphPanel);
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
