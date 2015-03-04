package edu.umassmed.omega.trackingMeasuresPlugin.runnables;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import edu.umassmed.omega.core.OmegaLogFileManager;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.trackingMeasuresPlugin.gui.TMMotionTypeClassificationPanel;

public class TMMotionTypeClassificationGraphProducer implements Runnable {

	private final TMMotionTypeClassificationPanel motionTypeClassificationPanel;
	private double completed;
	private volatile boolean isTerminated;

	private final int motionTypeOption;
	private final int maxT;
	private final int width, height;
	private final OmegaTrajectory track;
	private final List<OmegaSegment> segments;
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

	private final ChartPanel[] chartPanels;

	public TMMotionTypeClassificationGraphProducer(
	        final TMMotionTypeClassificationPanel motionTypeClassificationPanel,
	        final int motionTypeOption, final int tMax, final int imgWidth,
	        final int imgHeight, final OmegaTrajectory track,
	        final List<OmegaSegment> segments,
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
		this.motionTypeClassificationPanel = motionTypeClassificationPanel;
		this.completed = 0.0;
		this.isTerminated = false;
		this.motionTypeOption = motionTypeOption;
		this.maxT = tMax;
		this.width = imgWidth;
		this.height = imgHeight;
		this.track = track;
		this.segments = segments;
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
		this.chartPanels = new ChartPanel[4];
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
		switch (this.motionTypeOption) {
		case TMMotionTypeClassificationPanel.OPTION_LOG:
			this.prepareTrackGraph();
			this.prepareLogMSDGraph();
			this.prepareMSSFromLogGraph();
			this.prepareSMSSvsDFromLogGraph();
			break;
		default:
			this.prepareTrackGraph();
			this.prepareLinearMSDGraph();
			this.prepareMSSGraph();
		}
		this.updateStatus(-1);
	}

	private void prepareLinearMSDGraph() {
		final String title = "Linear MSD plot";
		final XYSeries serie = new XYSeries(title, false);
		if (this.muMap.containsKey(this.track)) {
			final Double[] msd = this.muMap.get(this.track)[2];
			final Double[] deltaT = this.deltaTMap.get(this.track)[2];
			final double partial = 100.0 / msd.length;
			final double increase = new BigDecimal(partial).setScale(2,
			        RoundingMode.HALF_UP).doubleValue();
			for (int i = 0; i < msd.length; i++) {
				if (this.isTerminated)
					return;
				final Double msdVal = msd[i];
				final Double deltaTVal = deltaT[i];
				if ((msdVal != null) && (deltaTVal != null)) {
					serie.add(deltaTVal, msdVal);
				}
				this.updateStatus(1);
				this.completed += increase;
				if (this.completed > 100.0) {
					this.completed = 100.0;
				}
			}
		}

		final NumberAxis xAxis = new NumberAxis("Delta T");
		final NumberAxis yAxis = new NumberAxis("Linear MSD");

		final XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		xySeriesCollection.addSeries(serie);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		final XYPlot plot = new XYPlot(xySeriesCollection, xAxis, yAxis,
		        renderer);
		plot.setBackgroundPaint(Color.WHITE);

		renderer.setSeriesPaint(0, this.track.getColor());
		plot.setRenderer(renderer);

		final JFreeChart chart = new JFreeChart(title, plot);

		this.chartPanels[1] = new ChartPanel(chart);

		// this.chartPanels[1] = new GenericGraphPanel(dataset,
		// GenericGraphPanel.GRAPH_KIND_LINE, title, "Delta T",
		// "Linear MSD", GenericGraphPanel.PLOT_VERTICAL, true, true,
		// false);
	}

	private void prepareLogMSDGraph() {
		final String title = "Log MSD plot";
		final XYSeries serie = new XYSeries(title, false);
		if (this.logMuMap.containsKey(this.track)) {
			final Double[] msd = this.logMuMap.get(this.track)[2];
			final Double[] deltaT = this.logDeltaTMap.get(this.track)[2];
			final double partial = 100.0 / msd.length;
			final double increase = new BigDecimal(partial).setScale(2,
			        RoundingMode.HALF_UP).doubleValue();
			for (int i = 0; i < msd.length; i++) {
				if (this.isTerminated)
					return;
				final Double msdVal = msd[i];
				final Double deltaTVal = deltaT[i];
				if ((msdVal != null) && (deltaTVal != null)) {
					serie.add(deltaTVal, msdVal);
				}
				this.updateStatus(1);
				this.completed += increase;
				if (this.completed > 100.0) {
					this.completed = 100.0;
				}
			}
		}

		final NumberAxis xAxis = new NumberAxis("Log delta T");
		final NumberAxis yAxis = new NumberAxis("Log MSD");

		final XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		xySeriesCollection.addSeries(serie);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		final XYPlot plot = new XYPlot(xySeriesCollection, xAxis, yAxis,
		        renderer);
		plot.setBackgroundPaint(Color.WHITE);

		renderer.setSeriesPaint(0, this.track.getColor());
		plot.setRenderer(renderer);

		final JFreeChart chart = new JFreeChart(title, plot);

		this.chartPanels[1] = new ChartPanel(chart);

		// this.chartPanels[1] = new GenericGraphPanel(dataset,
		// GenericGraphPanel.GRAPH_KIND_LINE, title, "Log delta T",
		// "Log linear MSD", GenericGraphPanel.PLOT_VERTICAL, true, true,
		// false);
	}

	private void prepareMSSFromLogGraph() {
		final String title = "Log MSS plot";
		final XYSeries serie = new XYSeries(title, false);
		final XYSeries upperbound = new XYSeries("Upperbound");
		final XYSeries lowerbound = new XYSeries("Lowerbound");
		if (this.gammaFromLogMap.containsKey(this.track)) {
			final Double[] gamma = this.gammaFromLogMap.get(this.track);
			final Double[] ny = this.nyMap.get(this.track);
			final double partial = 100.0 / gamma.length;
			final double increase = new BigDecimal(partial).setScale(2,
			        RoundingMode.HALF_UP).doubleValue();
			for (int i = 0; i < gamma.length; i++) {
				if (this.isTerminated)
					return;
				final Double gammaVal = gamma[i];
				final Double nyVal = ny[i];
				serie.add(nyVal, gammaVal);
				this.updateStatus(2);
				this.completed += increase;
				if (this.completed > 100.0) {
					this.completed = 100.0;
				}
			}

			for (int i = 0; i < gamma.length; i++) {
				final Double nyVal = ny[i];
				final Double ub = 1 * nyVal;
				final Double lb = 0.5 * nyVal;
				upperbound.add(nyVal, ub);
				lowerbound.add(nyVal, lb);
			}
		}

		final NumberAxis xAxis = new NumberAxis("Order moment");
		final NumberAxis yAxis = new NumberAxis("Gamma");

		final XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		xySeriesCollection.addSeries(serie);

		final XYPlot plot = new XYPlot(xySeriesCollection, xAxis, yAxis, null);
		plot.setBackgroundPaint(Color.WHITE);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, this.track.getColor());
		plot.setRenderer(renderer);

		final XYSeriesCollection boundsCollection = new XYSeriesCollection();
		boundsCollection.addSeries(upperbound);
		boundsCollection.addSeries(lowerbound);

		final XYDifferenceRenderer diffRenderer = new XYDifferenceRenderer(
		        Color.lightGray, Color.white, false);

		diffRenderer.setSeriesPaint(0, Color.black);
		diffRenderer.setSeriesStroke(0, new BasicStroke(1.0f,
		        BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
		        new float[] { 5.0f, 5.0f }, 0.0f));
		diffRenderer.setSeriesPaint(1, Color.black);
		diffRenderer.setSeriesStroke(1, new BasicStroke(1.0f,
		        BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
		        new float[] { 5.0f, 5.0f }, 0.0f));

		plot.setDataset(1, boundsCollection);
		plot.setRenderer(1, diffRenderer);

		final JFreeChart chart = new JFreeChart(title, plot);

		this.chartPanels[2] = new ChartPanel(chart);

		// this.chartPanels[2] = new GenericGraphPanel(dataset,
		// GenericGraphPanel.GRAPH_KIND_LINE, title, "Order moment",
		// "Gamma", GenericGraphPanel.PLOT_VERTICAL, true, true, false);
	}

	private void prepareMSSGraph() {
		final String title = "Linear MSS plot";
		final XYSeries serie = new XYSeries(title, false);
		final XYSeries upperbound = new XYSeries("Upperbound");
		final XYSeries lowerbound = new XYSeries("Lowerbound");
		if (this.gammaMap.containsKey(this.track)) {
			final Double[] gamma = this.gammaMap.get(this.track);
			final Double[] ny = this.nyMap.get(this.track);
			final double partial = 100.0 / gamma.length;
			final double increase = new BigDecimal(partial).setScale(2,
			        RoundingMode.HALF_UP).doubleValue();
			for (int i = 0; i < gamma.length; i++) {
				if (this.isTerminated)
					return;
				final Double gammaVal = gamma[i];
				final Double nyVal = ny[i];
				serie.add(nyVal, gammaVal);
				this.updateStatus(2);
				this.completed += increase;
				if (this.completed > 100.0) {
					this.completed = 100.0;
				}
			}

			for (int i = 0; i < gamma.length; i++) {
				final Double nyVal = ny[i];
				final Double ub = 0.5 * nyVal;
				final Double lb = 0.25 * nyVal;
				upperbound.add(nyVal, ub);
				lowerbound.add(nyVal, lb);
			}
		}

		final NumberAxis xAxis = new NumberAxis("Order moment");
		final NumberAxis yAxis = new NumberAxis("Gamma");

		final XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		xySeriesCollection.addSeries(serie);

		final XYPlot plot = new XYPlot(xySeriesCollection, xAxis, yAxis, null);
		plot.setBackgroundPaint(Color.WHITE);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, this.track.getColor());
		plot.setRenderer(0, renderer);

		final XYSeriesCollection boundsCollection = new XYSeriesCollection();
		boundsCollection.addSeries(upperbound);
		boundsCollection.addSeries(lowerbound);

		final XYDifferenceRenderer diffRenderer = new XYDifferenceRenderer(
		        Color.lightGray, Color.white, false);

		diffRenderer.setSeriesStroke(0, new BasicStroke(1.0f,
		        BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
		        new float[] { 5.0f, 5.0f }, 0.0f));
		diffRenderer.setSeriesStroke(1, new BasicStroke(1.0f,
		        BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
		        new float[] { 5.0f, 5.0f }, 0.0f));

		plot.setDataset(1, boundsCollection);
		plot.setRenderer(1, diffRenderer);

		final JFreeChart chart = new JFreeChart(title, plot);

		this.chartPanels[2] = new ChartPanel(chart);

		// this.chartPanels[2] = new GenericGraphPanel(dataset,
		// GenericGraphPanel.GRAPH_KIND_LINE, title, "Order moment",
		// "Gamma", GenericGraphPanel.PLOT_VERTICAL, true, true, false);
	}

	private void prepareSMSSvsDFromLogGraph() {
		final String title = "Log SMSS vs D plot";
		// final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		final XYIntervalSeries series = new XYIntervalSeries(
		        this.track.getName());
		if (this.gammaDFromLogMap.containsKey(this.track)
		        && this.smssFromLogMap.containsKey(this.track)) {
			final Double d = this.gammaDFromLogMap.get(this.track)[2][3];
			final Double smss = this.smssFromLogMap.get(this.track)[0];
			series.add(d, d - 0.1, d + 0.1, smss, smss - 0.1, smss + 0.1);
		}

		final double partial = 100.0;
		final double increase = new BigDecimal(partial).setScale(2,
		        RoundingMode.HALF_UP).doubleValue();

		if (this.isTerminated)
			return;

		final XYIntervalSeriesCollection xySeriesCollection = new XYIntervalSeriesCollection();
		xySeriesCollection.addSeries(series);

		// dataset2.addSeries(series);
		// dataset.addValue(dVal, title, smssVal);
		this.updateStatus(3);
		this.completed += increase;
		if (this.completed > 100.0) {
			this.completed = 100.0;
		}

		final NumberAxis numberaxisX = new LogarithmicAxis("D");
		numberaxisX.setTickUnit(new NumberTickUnit(1));
		final NumberAxis numberaxisY = new NumberAxis("SMSS");
		numberaxisY.setRange(0.0, 1.0);
		numberaxisY.setTickUnit(new NumberTickUnit(0.1));

		// error bars customization
		final XYErrorRenderer renderer = new XYErrorRenderer();
		// renderer.setErrorPaint(Color.black);
		renderer.setErrorStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
		        BasicStroke.JOIN_MITER, 1.0f, new float[] { 5.0f, 5.0f }, 0.0f));
		// }
		final XYPlot plot = new XYPlot(xySeriesCollection, numberaxisX,
		        numberaxisY, renderer);
		plot.setBackgroundPaint(Color.WHITE);

		// series
		final Shape cross = ShapeUtilities.createDiagonalCross(1, 1);

		// series shape
		// for (int i = 0; i < series.le; i++) {
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesShape(0, cross);
		// }

		// for (int i = 0; i < this.series.length; i++) {
		renderer.setSeriesPaint(0, this.track.getColor());
		plot.setRenderer(renderer);

		final Marker marker = new ValueMarker(0.5);
		marker.setPaint(Color.BLACK);
		marker.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
		        BasicStroke.JOIN_MITER, 1.0f, new float[] { 5.0f, 5.0f }, 0.0f));
		plot.addRangeMarker(marker);

		final JFreeChart chart = new JFreeChart(title, plot);

		this.chartPanels[3] = new ChartPanel(chart);

		// this.chartPanels[3] = new GenericGraphPanel(xySeriesCollection,
		// GenericGraphPanel.GRAPH_KIND_SCATTER, title, "D", "SMSS",
		// GenericGraphPanel.PLOT_VERTICAL, true, true, false);
	}

	private void prepareTrackGraph() {
		final String title = "Track";
		final double partial = 100.0 / this.track.getLength();
		final double increase = new BigDecimal(partial).setScale(2,
		        RoundingMode.HALF_UP).doubleValue();
		final List<OmegaROI> rois = this.track.getROIs();
		Double prevX = null, prevY = null;
		Double x = 0.0, y = 0.0;
		final XYSeries dataset = new XYSeries(this.track.getName(), false);
		for (int i = 0; i < this.track.getLength(); i++) {
			if (this.isTerminated)
				return;
			final OmegaROI roi = rois.get(i);
			final Double roiX = roi.getX();
			final Double roiY = roi.getY();
			if (prevX != null) {
				x += roiX - prevX;
			}
			if (prevY != null) {
				y -= roiY - prevY;
			}
			prevX = roiX;
			prevY = roiY;
			dataset.add(x, y);
			this.updateStatus(0);
			this.completed += increase;
			if (this.completed > 100.0) {
				this.completed = 100.0;
			}
		}

		Double max = 0.0;
		x = StrictMath.abs(x);
		y = StrictMath.abs(y);
		if (x >= y) {
			max = x;
		} else {
			max = y;
		}
		max += 2.5;

		final NumberAxis xAxis = new NumberAxis("X");
		xAxis.setRange(-max, max);
		final NumberAxis yAxis = new NumberAxis("Y");
		yAxis.setRange(-max, max);

		final XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		xySeriesCollection.addSeries(dataset);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		final XYPlot plot = new XYPlot(xySeriesCollection, xAxis, yAxis,
		        renderer);
		plot.setBackgroundPaint(Color.WHITE);

		renderer.setSeriesPaint(0, this.track.getColor());
		plot.setRenderer(renderer);

		final JFreeChart chart = new JFreeChart(title, plot);

		this.chartPanels[0] = new ChartPanel(chart);
	}

	public void updateStatus(final int graph) {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					TMMotionTypeClassificationGraphProducer.this.motionTypeClassificationPanel
					        .updateStatus(
					                TMMotionTypeClassificationGraphProducer.this.completed,
					                graph,
					                TMMotionTypeClassificationGraphProducer.this.chartPanels);
				}
			});
		} catch (final InvocationTargetException | InterruptedException ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}

	public double getCompleted() {
		return this.completed;
	}

	public ChartPanel[] getGraphs() {
		return this.chartPanels;
	}

	public void terminate() {
		this.isTerminated = true;
	}
}
