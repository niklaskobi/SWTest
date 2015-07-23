package windows;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import objects.TemplatePlot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import javax.swing.filechooser.FileNameExtensionFilter;

/** @see http://stackoverflow.com/questions/5522575 */
public class plotConfirmWindow {

    //private static final String title = "Return On Investment";
	private String title = "";
    private ChartPanel chartPanel;
    private TemplatePlot tPlot;
    private int myIndex;
    JFrame f;
    
    private final String windowTitle = "template chart";
    private final String xLabel = "Time";
    private final String yLabel = "Value";    


    public plotConfirmWindow(TemplatePlot p, int index) {
    	
    	// return if no measurements values are available
    	if (p.allPoints.isEmpty()) return;
    	
    	this.myIndex = index;
    	this.tPlot = new TemplatePlot(p);
    	this.tPlot.normalizeTimestamps();
    	this.title = tPlot.fileName;
    	if (!tPlot.dateStr.isEmpty()) this.title += " ("+tPlot.dateStr+")";
    	chartPanel = createChart();
        //JFrame f = new JFrame(title);
    	f = new JFrame(windowTitle+" ("+(index+1)+")");
        f.setTitle(windowTitle+" ("+(index+1)+")");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new BorderLayout(0, 5));
        f.add(chartPanel, BorderLayout.CENTER);
        chartPanel.setMouseWheelEnabled(true);
        //chartPanel.setHorizontalAxisTrace(true);
        //chartPanel.setVerticalAxisTrace(true);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(createOpenButton());
        panel.add(createSaveButton());
        panel.add(createCloseButton());
        panel.add(createTrace());
        panel.add(createDate());
        f.add(panel, BorderLayout.SOUTH);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
            	functions.Events.closePlotWindow(myIndex);
            }
        });
    }
    
    
    /**
     * constructor with a given path, instead of a plot object
     * @param t
     * @param p
     */
    public plotConfirmWindow(String path, int index) 
    {   	
    	this.myIndex = index;
    	this.tPlot = new TemplatePlot();
    	tPlot.readTemplateFromFile(path, true);
    	//this.title = windowTitle;
    	this.title = tPlot.fileName;
    	if (!tPlot.dateStr.isEmpty()) this.title += " ("+tPlot.dateStr+")";
    	chartPanel = createChart();
    	
    	f = new JFrame(windowTitle);
        f.setTitle(windowTitle);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new BorderLayout(0, 5));
        f.add(chartPanel, BorderLayout.CENTER);
        chartPanel.setMouseWheelEnabled(true);
        //chartPanel.setHorizontalAxisTrace(true);
        //chartPanel.setVerticalAxisTrace(true);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(createOpenButton());
        panel.add(createSaveButton());
        panel.add(createCloseButton());
        panel.add(createTrace());
        panel.add(createDate());
        f.add(panel, BorderLayout.SOUTH);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        
        f.addWindowListener(new java.awt.event.WindowAdapter() 
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
            	functions.Events.closePlotWindow(myIndex);
            }
        });
    }

    

    private JComboBox<String> createTrace() {
        final JComboBox<String> trace = new JComboBox<String>();
        final String[] traceCmds = {"Disable Trace", "Enable Trace"};
        trace.setModel(new DefaultComboBoxModel<String>(traceCmds));
        trace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (traceCmds[0].equals(trace.getSelectedItem())) {
                    chartPanel.setHorizontalAxisTrace(false);
                    chartPanel.setVerticalAxisTrace(false);
                    chartPanel.repaint();
                } else {
                    chartPanel.setHorizontalAxisTrace(true);
                    chartPanel.setVerticalAxisTrace(true);
                    chartPanel.repaint();
                }
            }
        });
        return trace;
    }

    private JComboBox<String> createDate() {
        final JComboBox<String> date = new JComboBox<String>();
        final String[] dateCmds = {"Horizontal Dates", "Vertical Dates"};
        date.setModel(new DefaultComboBoxModel<String>(dateCmds));
        date.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFreeChart chart = chartPanel.getChart();
                XYPlot plot = (XYPlot) chart.getPlot();
                DateAxis domain = (DateAxis) plot.getDomainAxis();
                if (dateCmds[0].equals(date.getSelectedItem())) {
                    domain.setVerticalTickLabels(false);
                } else {
                    domain.setVerticalTickLabels(true);
                }
            }
        });
        return date;
    }

    private JButton createOpenButton() {
        final JButton auto = new JButton(new AbstractAction("Open") {

            @Override
            public void actionPerformed(ActionEvent e) 
            {
                //chartPanel.restoreAutoBounds();
            	System.out.println("open dialog");
            	JFrame parentFrame = new JFrame();           	 
            	JFileChooser fileChooser = new JFileChooser();
            	FileNameExtensionFilter tmpfilter = new FileNameExtensionFilter("template files", objects.TemplatePlot.fileExtention);
            	fileChooser.setFileFilter(tmpfilter);
            	fileChooser.setDialogTitle("Specify a file to open");           	 
            	int userSelection = fileChooser.showOpenDialog(parentFrame);	 
            	if (userSelection == JFileChooser.APPROVE_OPTION) 
            	{
            	    File fileToOpen = fileChooser.getSelectedFile();
            	    System.out.println("Open file: " + fileToOpen.getAbsolutePath());
            	    if (tPlot.readTemplateFromFile(fileToOpen.getAbsolutePath(),false))
            	    {            	
            	    	functions.Events.reopenTmpPlot(fileToOpen.getAbsolutePath(), myIndex);
            	    	closeThisWindow();
            	    }
            	}
            }
        });
        return auto;
    }

    
    private JButton createSaveButton() {
        final JButton auto = new JButton(new AbstractAction("Save") {

            @Override
            public void actionPerformed(ActionEvent e) 
            {
                //chartPanel.restoreAutoBounds();
            	System.out.println("save dialog");
            	JFrame parentFrame = new JFrame();           	 
            	JFileChooser fileChooser = new JFileChooser();
            	FileNameExtensionFilter tmpfilter = new FileNameExtensionFilter("template files", objects.TemplatePlot.fileExtention);
            	fileChooser.setFileFilter(tmpfilter);
            	fileChooser.setDialogTitle("Specify a file to save");           	 
            	int userSelection = fileChooser.showSaveDialog(parentFrame);	 
            	if (userSelection == JFileChooser.APPROVE_OPTION) 
            	{
            	    File fileToSave = fileChooser.getSelectedFile();
            	    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            	    tPlot.writeTemplateToFile(fileToSave.getAbsolutePath());
            	}
            }
        });
        return auto;
    }
    
    
    private JButton createCloseButton() {
        final JButton auto = new JButton(new AbstractAction("Close") {

            @Override
            public void actionPerformed(ActionEvent e) 
            {
            	closeThisWindow();
            }
        });
        return auto;
    }
    
    
    private ChartPanel createChart() 
    {
        XYDataset roiData = createDataset();
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xLabel, yLabel, roiData, true, true, false);
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);
        //NumberFormat currency = NumberFormat.getCurrencyInstance();
        //currency.setMaximumFractionDigits(0);
        //NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        //rangeAxis.setNumberFormatOverride(currency);
        chart.removeLegend();
        return new ChartPanel(chart);
    }

    
    private XYDataset createDataset() 
    {
        TimeSeriesCollection tsc = new TimeSeriesCollection();
        //tsc.addSeries(createSeries("Projected", 200));
        tsc.addSeries(createSeries(xLabel, 100));
        return tsc;
    }

    
    private TimeSeries createSeries(String name, double scale) 
    {
        TimeSeries series = new TimeSeries(name);
        //ArrayList<MeasurementEntry> templateList = sensorWindow.templatePlot.allPoints;
        for (int i= 0; i< tPlot.allPoints.size(); i++)
        {
        	Date d = new java.sql.Date((long) tPlot.allPoints.get(i).value1);
        	series.addOrUpdate(new Second(d), tPlot.allPoints.get(i).value2);
        }
        return series;
    }

    
    private void closeThisWindow()
    {
    	System.out.println("close tmp window");
    	functions.Events.closePlotWindow(myIndex);
    	f.dispose();
    }
    
    
    
    
}