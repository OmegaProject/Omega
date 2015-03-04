package edu.umassmed.omega.commons.gui.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.RootPaneContainer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;

import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class GenericGraphPanel extends GenericPanel {
	private static final long serialVersionUID = 86454989469526860L;

	public static final int GRAPH_KIND_BAR = 0;
	public static final int GRAPH_KIND_LINE = 1;
	public static final int GRAPH_KIND_AREA = 2;
	public static final int GRAPH_KIND_PIE = 3;
	public static final int GRAPH_KIND_SCATTER = 4;
	public static final int GRAPH_KIND_XYBAR = 5;
	public static final int GRAPH_KIND_XYLINE = 6;
	public static final int GRAPH_KIND_XYAREA = 7;

	public static final int PLOT_HORIZONTAL = 0;
	public static final int PLOT_VERTICAL = 1;

	private final int graphKind;
	private final String title, xAxis, yAxis;
	private final boolean hasLegend, hasTooltips, hasUrls;
	private PlotOrientation plotOrientation;
	private final Dataset dataset;

	private ChartPanel chartPanel;
	private JFreeChart chart;

	public GenericGraphPanel(final Dataset dataset, final int graphKind,
	        final String title, final String xAxis, final String yAxis,
	        final int isPlotHorizontal, final boolean hasLegend,
	        final boolean hasTooltips, final boolean hasUrls) {
		this(null, dataset, graphKind, title, xAxis, yAxis, isPlotHorizontal,
		        hasLegend, hasTooltips, hasUrls);
	}

	public GenericGraphPanel(final RootPaneContainer parent,
	        final Dataset dataset, final int graphKind, final String title,
	        final String xAxis, final String yAxis, final int isPlotHorizontal,
	        final boolean hasLegend, final boolean hasTooltips,
	        final boolean hasUrls) {
		super(parent);

		this.graphKind = graphKind;

		this.dataset = dataset;

		this.title = title;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		if (isPlotHorizontal == 1) {
			this.plotOrientation = PlotOrientation.VERTICAL;
		} else {
			this.plotOrientation = PlotOrientation.HORIZONTAL;
		}
		this.hasLegend = hasLegend;
		this.hasTooltips = hasTooltips;
		this.hasUrls = hasUrls;

		this.setLayout(new BorderLayout());
		this.createChart();
		this.createAndAddWigets();

		this.addListeners();
	}

	private void createAndAddWigets() {
		final Plot plot = this.chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		if (plot instanceof CategoryPlot) {
			final CategoryPlot catPlot = (CategoryPlot) plot;
			final CategoryAxis xAxis = catPlot.getDomainAxis();
			// final ValueAxis yAxis = plot.getRangeAxis();
			xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
			// yAxis.setTick

			catPlot.setDomainGridlinesVisible(true);
			catPlot.setRangeGridlinesVisible(true);
			catPlot.setRangeGridlinePaint(Color.black);
			catPlot.setDomainGridlinePaint(Color.black);
		} else if (plot instanceof XYPlot) {
			final XYPlot xyPlot = (XYPlot) plot;
			// final GenericNumberAxis axis = new GenericNumberAxis(-45);
			// xyPlot.setDomainAxis(axis);
			xyPlot.setDomainGridlinesVisible(true);
			xyPlot.setRangeGridlinesVisible(true);
			xyPlot.setRangeGridlinePaint(Color.black);
			xyPlot.setDomainGridlinePaint(Color.black);
		}

		this.chartPanel = new ChartPanel(this.chart);
		this.add(this.chartPanel, BorderLayout.CENTER);
	}

	public void changeColors(final List<OmegaTrajectory> tracks) {
		final CategoryPlot plot = (CategoryPlot) this.chart.getPlot();
		final CategoryItemRenderer renderer = plot.getRenderer();
		for (final OmegaTrajectory track : tracks) {
			final int index = plot.getDataset().getRowIndex(track.getName());
			renderer.setSeriesPaint(index, track.getColor());
		}
	}

	private void createChart() {
		switch (this.graphKind) {
		case GRAPH_KIND_BAR:
			this.chart = ChartFactory.createBarChart(this.title, this.xAxis,
			        this.yAxis, (CategoryDataset) this.dataset,
			        this.plotOrientation, this.hasLegend, this.hasTooltips,
			        this.hasUrls);
			break;
		case GRAPH_KIND_AREA:
			this.chart = ChartFactory.createAreaChart(this.title, this.xAxis,
			        this.yAxis, (CategoryDataset) this.dataset,
			        this.plotOrientation, this.hasLegend, this.hasTooltips,
			        this.hasUrls);
			break;
		case GRAPH_KIND_PIE:
			this.chart = ChartFactory.createPieChart(this.title,
			        (PieDataset) this.dataset, this.hasLegend,
			        this.hasTooltips, this.hasUrls);
			break;
		case GRAPH_KIND_SCATTER:
			this.chart = ChartFactory.createScatterPlot(this.title, this.xAxis,
			        this.yAxis, (XYDataset) this.dataset, this.plotOrientation,
			        this.hasLegend, this.hasTooltips, this.hasUrls);
			break;
		case GRAPH_KIND_XYBAR:
			this.chart = ChartFactory.createXYBarChart(this.title, this.xAxis,
			        false, this.yAxis, (IntervalXYDataset) this.dataset,
			        this.plotOrientation, this.hasLegend, this.hasTooltips,
			        this.hasUrls);
			break;
		case GRAPH_KIND_XYLINE:
			this.chart = ChartFactory.createXYLineChart(this.title, this.xAxis,
			        this.yAxis, (XYDataset) this.dataset, this.plotOrientation,
			        this.hasLegend, this.hasTooltips, this.hasUrls);
			break;
		case GRAPH_KIND_XYAREA:
			this.chart = ChartFactory.createXYAreaChart(this.title, this.xAxis,
			        this.yAxis, (XYDataset) this.dataset, this.plotOrientation,
			        this.hasLegend, this.hasTooltips, this.hasUrls);
			break;
		default:
			this.chart = ChartFactory.createLineChart(this.title, this.xAxis,
			        this.yAxis, (CategoryDataset) this.dataset,
			        this.plotOrientation, this.hasLegend, this.hasTooltips,
			        this.hasUrls);
			break;
		}

		this.chartPanel = new ChartPanel(this.chart);
	}

	private void addListeners() {

	}
}
