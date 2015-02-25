package windows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import objects.MeasurementEntry;
import objects.TemplatePlot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.swing.filechooser.FileNameExtensionFilter;

/** @see http://stackoverflow.com/questions/5522575 */
public class plotConfirmWindow {

    //private static final String title = "Return On Investment";
	private String title = "Return On Investment";
    private ChartPanel chartPanel = createChart();
    private String filePath = null;
    private ArrayList<MeasurementEntry> templateList;
    private TemplatePlot tPlot;
    JFrame f;
    
    private final String xLabel = "Time";
    private final String yLabel = "Value";

    public plotConfirmWindow(String t, TemplatePlot p) {
    	this.tPlot = p;
    	this.title = t;
        //JFrame f = new JFrame(title);
    	f = new JFrame(title);
        f.setTitle(title);
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
            	functions.Events.closePlotWindow();
            }
        });
    }
    
    

    private JComboBox createTrace() {
        final JComboBox trace = new JComboBox();
        final String[] traceCmds = {"Enable Trace", "Disable Trace"};
        trace.setModel(new DefaultComboBoxModel(traceCmds));
        trace.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (traceCmds[0].equals(trace.getSelectedItem())) {
                    chartPanel.setHorizontalAxisTrace(true);
                    chartPanel.setVerticalAxisTrace(true);
                    chartPanel.repaint();
                } else {
                    chartPanel.setHorizontalAxisTrace(false);
                    chartPanel.setVerticalAxisTrace(false);
                    chartPanel.repaint();
                }
            }
        });
        return trace;
    }

    private JComboBox createDate() {
        final JComboBox date = new JComboBox();
        final String[] dateCmds = {"Horizontal Dates", "Vertical Dates"};
        date.setModel(new DefaultComboBoxModel(dateCmds));
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
            	    System.out.println("Save as file: " + fileToOpen.getAbsolutePath());
            	    tPlot.readTemplateFromFile(fileToOpen.getAbsolutePath());
                	//chartPanel = createChart();
            	    refreshChart();
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
            public void actionPerformed(ActionEvent e) {
                //chartPanel.restoreAutoBounds();
            	System.out.println("close dialog");
            	functions.Events.closePlotWindow();
            	f.dispose();
            }
        });
        return auto;
    }
    
    private ChartPanel createChart() {
        XYDataset roiData = createDataset();
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title, xLabel, yLabel, roiData, true, true, false);
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer =
            (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);
        //NumberFormat currency = NumberFormat.getCurrencyInstance();
        //currency.setMaximumFractionDigits(0);
        //NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        //rangeAxis.setNumberFormatOverride(currency);
        chart.removeLegend();
        return new ChartPanel(chart);
    }

    private XYDataset createDataset() {
        TimeSeriesCollection tsc = new TimeSeriesCollection();
        //tsc.addSeries(createSeries("Projected", 200));
        tsc.addSeries(createSeries(xLabel, 100));
        return tsc;
    }

    private TimeSeries createSeries(String name, double scale) {
        TimeSeries series = new TimeSeries(name);
        /*
        for (int i = 0; i < 6; i++) {
        	//series.add(new Year(2005 + i), Math.pow(2, i) * scale);
            series.add(new Millisecond(), Math.pow(2, i) * scale);
        }
        */
        ArrayList<MeasurementEntry> templateList = sensorWindow.templatePlot.allPoints;
        for (int i= 0; i< templateList.size(); i++)
        {
        	Date d = new java.sql.Date((long) templateList.get(i).value1);
        	//String s = d.toString();
        	//series.add(new Second(d), list.get(i).value2);
        	series.addOrUpdate(new Second(d), templateList.get(i).value2);
        }
        return series;
    }

    
    private void refreshChart() 
    {
    	asd
    }
    /*
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                plotConfirmWindow cpd = new plotConfirmWindow("asdasd");
            }
        });
    }
    */
    
}