package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import internal.DataPack;
import internal.NewDataListener;
import internal.RouterInterface;


public class InfoPanel extends JPanel implements NewDataListener {
	private static final Color TRANSPARENT = new Color(0,0,0,0);
	private List<Long> times;
	private List<Long> inPackets;
	private List<Long> outPackets;
	private List<Double> inBandwidth;
	private List<Double> outBandwidth;
	private boolean hasData=false;
	
	private RouterInterface routerInterface = null;
	
	ChartPanel chartPanel1 = new ChartPanel(null), chartPanel2 = new ChartPanel(null);
	
	public InfoPanel() {
		this.setLayout(new GridLayout(2,1));
		this.add(chartPanel1);
		this.add(chartPanel2);
	}
	
	private JFreeChart createPacketsChart() {
		XYSeries series1 = new XYSeries("In");
		XYSeries series2 = new XYSeries("Out");
		
		long minX = times.get(0), maxX = times.get(times.size()-1);
		if (minX == maxX) maxX++;
		
		long minY = inPackets.get(0), maxY = inPackets.get(0);
		for (int i=0;i<times.size();i++) {
			series1.add(times.get(i), inPackets.get(i));
			series2.add(times.get(i), outPackets.get(i));
			minY = Math.min(minY, Math.min(inPackets.get(i), outPackets.get(i)));
			maxY = Math.max(maxY, Math.max(inPackets.get(i), outPackets.get(i)));
		}
		if (minY == maxY) maxY++;
				
		XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "Time",
                "Bytes",
                dataset
        );
        
        XYPlot plot = chart.getXYPlot();
        NumberAxis domain = (NumberAxis)plot.getDomainAxis();
        NumberAxis range = (NumberAxis)plot.getRangeAxis();
        domain.setRange(minX, maxX);
        range.setRange(0.95*minY, 1.05*maxY);
		
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(TRANSPARENT);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);

        chart.setBackgroundPaint(TRANSPARENT);
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setBackgroundPaint(TRANSPARENT);
        
        return chart;
	}
	
	private JFreeChart createBandwidthChart() {
		XYSeries series1 = new XYSeries("In");
		XYSeries series2 = new XYSeries("Out");
		
		long minX = times.get(0), maxX = times.get(times.size()-1);
		if (minX == maxX) maxX++;
		
		double minY = inBandwidth.get(0), maxY = inBandwidth.get(0);
		for (int i=0;i<times.size();i++) {
			series1.add(times.get(i), inBandwidth.get(i));
			series2.add(times.get(i), outBandwidth.get(i));
			minY = Math.min(minY, Math.min(inBandwidth.get(i), outBandwidth.get(i)));
			maxY = Math.max(maxY, Math.max(inBandwidth.get(i), outBandwidth.get(i)));
		}
		if (minY == maxY) maxY++;
		
		XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "Time",
                "Bandwidth [bps]",
                dataset
        );
        
        XYPlot plot = chart.getXYPlot();
        NumberAxis domain = (NumberAxis)plot.getDomainAxis();
        NumberAxis range = (NumberAxis)plot.getRangeAxis();
        domain.setRange(minX, maxX);
        range.setRange(0.95*minY, 1.05*maxY);
		
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(TRANSPARENT);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);

        chart.setBackgroundPaint(TRANSPARENT);
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setBackgroundPaint(TRANSPARENT);
        
        return chart;
	}
	
	private void plot() {
		JFreeChart chart1 = null, chart2 = null;
	    if (hasData) {
	    	chart1 = createPacketsChart();
		    chart2 = createBandwidthChart();
	    }
	    
	    chartPanel1.setChart(chart1);
	    chartPanel2.setChart(chart2);
	}

	@Override
	public void newData() {
		getData();
		plot();
	}
	
	private void getData() {
		if (routerInterface == null) return;
		
		DataPack data = this.routerInterface.getLatestData();
		this.times = data.getTimes();
		this.inPackets = data.getInPackets();
		this.outPackets = data.getOutPackets();
		this.inBandwidth = data.getInBandwidth();
		this.outBandwidth = data.getOutBandwidth();
		this.hasData = true;
	}

	public void setInterface(RouterInterface routerInterface) {
		if (this.routerInterface != null) 
			this.routerInterface.removeListener();
		
		this.routerInterface = routerInterface;
		this.routerInterface.setNewDataListener(this);
		
		getData();
		plot();
	}

	public void clearInterface() {
		if (this.routerInterface != null) 
			this.routerInterface.removeListener();
		this.routerInterface = null;
		this.hasData = false;
		plot();
	}

	@Override
	public void removed() {
		this.routerInterface = null;
		this.hasData = false;
		plot();
	}
}
