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

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.data.trajectoryElements.OmegaParticle;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.trackingMeasuresPlugin.gui.TMConstants;
import edu.umassmed.omega.trackingMeasuresPlugin.gui.TMIntensityPanel;

public class TMIntesityGraphProducer extends TMGraphProducer {

	private final TMIntensityPanel intensityPanel;

	private final int peakMeanBgSnrOption, minMeanMaxOption;
	private final boolean isTimepointsGraph;
	private final int maxT;

	private final Map<OmegaTrajectory, Double[]> peakSignalmaMap;
	private final Map<OmegaTrajectory, Double[]> meanSignalMap;
	private final Map<OmegaTrajectory, Double[]> localBackgroundMap;
	private final Map<OmegaTrajectory, Double[]> localSNRMap;

	private ChartPanel graphPanel;

	public TMIntesityGraphProducer(final TMIntensityPanel intensityPanel,
			final int graphType, final int peakMeanBgSnrOption,
			final int minMeanMaxOption, final int maxT,
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final Map<OmegaTrajectory, Double[]> peakSignalMap,
			final Map<OmegaTrajectory, Double[]> meanSignalMap,
			final Map<OmegaTrajectory, Double[]> localBackgroundMap,
			final Map<OmegaTrajectory, Double[]> localSNRMap) {
		super(graphType, segmentsMap);
		this.intensityPanel = intensityPanel;
		this.peakMeanBgSnrOption = peakMeanBgSnrOption;
		this.minMeanMaxOption = minMeanMaxOption;
		this.isTimepointsGraph = true;
		this.maxT = maxT;
		this.peakSignalmaMap = peakSignalMap;
		this.meanSignalMap = meanSignalMap;
		this.localBackgroundMap = localBackgroundMap;
		this.localSNRMap = localSNRMap;

		this.graphPanel = null;
	}

	public TMIntesityGraphProducer(final TMIntensityPanel intensityPanel,
			final int graphType, final int peakMeanBgSnrOption, final int maxT,
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		super(graphType, segmentsMap);
		this.intensityPanel = intensityPanel;
		this.peakMeanBgSnrOption = peakMeanBgSnrOption;
		this.minMeanMaxOption = -1;
		this.isTimepointsGraph = true;
		this.maxT = maxT;
		this.peakSignalmaMap = null;
		this.meanSignalMap = null;
		this.localBackgroundMap = null;
		this.localSNRMap = null;

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
		String title = "";
		switch (this.minMeanMaxOption) {
		case TMIntensityPanel.OPTION_MIN:
			title += "Min ";
			break;
		case TMIntensityPanel.OPTION_MEAN:
			title += "Mean ";
			break;
		case TMIntensityPanel.OPTION_MAX:
			title += "Max ";
			break;
		default:
			break;
		}
		switch (this.peakMeanBgSnrOption) {
		case TMIntensityPanel.OPTION_MEAN_SIGNAL:
			title += "Mean Intensity";
			break;
		case TMIntensityPanel.OPTION_LOCAL_BACKGROUND:
			title += "Background";
			break;
		case TMIntensityPanel.OPTION_LOCAL_SNR:
			title += "SNR";
			break;
		default:
			title += TMConstants.GRAPH_NAME_INT_PEAK;
		}
		return title;
	}

	@Override
	protected Double getValue(final OmegaTrajectory track, final OmegaROI roi) {
		Double[] values = null;
		Double value = null;
		if (roi != null) {
			final OmegaParticle particle = (OmegaParticle) roi;
			switch (this.peakMeanBgSnrOption) {
			case TMIntensityPanel.OPTION_MEAN_SIGNAL:
				value = particle.getMeanSignal();
			case TMIntensityPanel.OPTION_LOCAL_BACKGROUND:
				value = particle.getMeanBackground();
			case TMIntensityPanel.OPTION_LOCAL_SNR:
				value = particle.getSNR();
			default:
				value = particle.getPeakSignal();
			}
		} else {
			switch (this.peakMeanBgSnrOption) {
			case TMIntensityPanel.OPTION_MEAN_SIGNAL:
				values = this.meanSignalMap.get(track);
				break;
			case TMIntensityPanel.OPTION_LOCAL_BACKGROUND:
				values = this.localBackgroundMap.get(track);
				break;
			case TMIntensityPanel.OPTION_LOCAL_SNR:
				values = this.localSNRMap.get(track);
				break;
			default:
				values = this.peakSignalmaMap.get(track);
			}
			value = values[this.minMeanMaxOption];
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
			// dataset.addValue(values[this.minMeanMaxOption], title, name);
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

		Plot plot = null;
		JFreeChart chart = null;
		if (dataset instanceof HistogramDataset) {
			final HistogramDataset histDataset = (HistogramDataset) dataset;
			chart = ChartFactory.createHistogram(title,
			        TMConstants.GRAPH_LAB_Y_INT, TMConstants.GRAPH_LAB_Y_FREQ,
			        histDataset, PlotOrientation.VERTICAL, true, true, true);
			plot = chart.getPlot();
		} else {
			final CategoryAxis xAxis = new CategoryAxis(
			        TMConstants.GRAPH_LAB_X_TRACK);
			final NumberAxis yAxis = new NumberAxis(TMConstants.GRAPH_LAB_Y_INT);
			final DefaultCategoryDataset catDataset = (DefaultCategoryDataset) dataset;
			plot = new CategoryPlot(catDataset, xAxis, yAxis, renderer);
			chart = new JFreeChart(title, plot);
		}
		plot.setBackgroundPaint(Color.WHITE);

		this.graphPanel = new ChartPanel(chart);

		// this.graphPanel = new GenericGraphPanel(dataset,
		// GenericGraphPanel.GRAPH_KIND_LINE, title, "Tracks",
		// "Image values", GenericGraphPanel.PLOT_VERTICAL, true, true,
		// false);
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
					final OmegaParticle particle = (OmegaParticle) roi;
					final Double newVal = this.getValue(track, particle);
					if (dataset instanceof HistogramDataset) {
						histValues.add(newVal);
					} else {
						final DefaultCategoryDataset catDataset = (DefaultCategoryDataset) dataset;
						catDataset.addValue(newVal, name, timepoint);
						found = true;
					}
					// dataset.addValue(newVal, name, timepoint);
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

		Plot plot = null;
		JFreeChart chart = null;
		if (dataset instanceof HistogramDataset) {
			final HistogramDataset histDataset = (HistogramDataset) dataset;
			chart = ChartFactory.createHistogram(title,
					TMConstants.GRAPH_LAB_Y_INT, TMConstants.GRAPH_LAB_Y_FREQ,
					histDataset, PlotOrientation.VERTICAL, true, true, true);
			plot = chart.getPlot();
		} else {
			final CategoryAxis xAxis = new CategoryAxis(
					TMConstants.GRAPH_LAB_X_TIME);
			// xAxis.setTickUnit(new NumberTickUnit(1.0));
			final NumberAxis yAxis = new NumberAxis(TMConstants.GRAPH_LAB_Y_INT);
			final DefaultCategoryDataset catDataset = (DefaultCategoryDataset) dataset;
			plot = new CategoryPlot(catDataset, xAxis, yAxis, renderer);
			chart = new JFreeChart(title, plot);
		}
		plot.setBackgroundPaint(Color.WHITE);

		this.graphPanel = new ChartPanel(chart);

		// this.graphPanel = new GenericGraphPanel(dataset,
		// GenericGraphPanel.GRAPH_KIND_LINE, title, "Timepoints",
		// "Image values", GenericGraphPanel.PLOT_VERTICAL, true, true,
		// false);
		// this.graphPanel.changeColors(drawTracks);
		this.updateStatus(true);
	}

	public void updateStatus(final boolean ended) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					TMIntesityGraphProducer.this.intensityPanel.updateStatus(
							TMIntesityGraphProducer.this.getCompleted(), ended,
							TMIntesityGraphProducer.this.graphPanel);
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
