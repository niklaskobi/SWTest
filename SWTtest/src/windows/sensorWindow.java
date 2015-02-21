package windows;
 /* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform

 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------------
 * DynamicDataDemo3.java
 * ---------------------
 * (C) Copyright 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited).
 * Contributor(s):   -;
 *
 * $Id: DynamicDataDemo3.java,v 1.5 2004/04/26 19:11:54 taqua Exp $
 *
 * Changes
 * -------
 * 02-Mar-2004 : Version 1 (DG);
 *
 */

//package org.jfree.chart.demo;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import objects.MeasurementEntry;
import objects.Slider;
import objects.Brick;
import objects.Measurement;
import objects.TemplatePlot;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import data.connectionData;
import data.constants;


@SuppressWarnings("serial")
public class sensorWindow extends ApplicationFrame implements ActionListener {
	
	final static double warningPercentage 			= 100;	//
	final static int 	sliderValuesNumber		 	= 20;	// how many x values are visible at the scroll bar
	final static int	chartRangeSec				= 20;

	final static int 	maxValues		  			= 1000;	// max number of values which will be stored in object - measurement 
	final static int 	maxCycles		  			= 10;	// max number of cycles which will ne taken into account computing  extrema and avergae values
	
	public static boolean sliderUpdate 				= false;// true means we have moved the slider at least once, but now it is on its start position
															// which means we want to auto-update the charts
	

	Slider sliderData = new Slider(sliderValuesNumber);
	static JSlider slider;
	
	public static TemplatePlot templatePlot = new TemplatePlot();
	
    public static Map<String, TimeSeriesCollection> seriesCollectionMap = new HashMap<String, TimeSeriesCollection>();
    public static Map<String, TimeSeriesCollection> seriesCollectionMap2 = new HashMap<String, TimeSeriesCollection>();       
    
    public static Map<String, TimeSeries> seriesMap = new HashMap<String, TimeSeries>();
    public static Map<String, TimeSeries> seriesMap2 = new HashMap<String, TimeSeries>();
    
    public static Map<String, NumberAxis> axisMap = new HashMap<String, NumberAxis>();
    public static Map<String, NumberAxis> axisMap2 = new HashMap<String, NumberAxis>();
    
    public static Map<String, Font> customFonts = new HashMap<String, Font>();
    
    public static Map<String, XYItemRenderer> rendererMap = new HashMap<String, XYItemRenderer>();
    public static Map<String, XYItemRenderer> rendererMap2 = new HashMap<String, XYItemRenderer>();
    
    public static Map<String, ValueMarker> markerMapMin1Critical = new HashMap<String, ValueMarker> ();
    public static Map<String, ValueMarker> markerMapMin1Warning = new HashMap<String, ValueMarker> ();

    public static Map<String, ValueMarker> markerMapMax1Critical = new HashMap<String, ValueMarker> ();
    public static Map<String, ValueMarker> markerMapMax1Warning = new HashMap<String, ValueMarker> ();

    public static Map<String, ValueMarker> markerMapMin2Critical = new HashMap<String, ValueMarker> ();
    public static Map<String, ValueMarker> markerMapMin2Warning = new HashMap<String, ValueMarker> ();

    public static Map<String, ValueMarker> markerMapMax2Critical = new HashMap<String, ValueMarker> ();
    public static Map<String, ValueMarker> markerMapMax2Warning = new HashMap<String, ValueMarker> ();

    public static Map<String, ValueMarker> markerMaxima = new HashMap<String, ValueMarker> ();
    public static Map<String, ValueMarker> markerMinima = new HashMap<String, ValueMarker> ();
    public static Map<String, ValueMarker> markerAverage = new HashMap<String, ValueMarker> ();

    public static Map<String, ValueMarker> marker2Maxima = new HashMap<String, ValueMarker> ();
    public static Map<String, ValueMarker> marker2Minima = new HashMap<String, ValueMarker> ();
    public static Map<String, ValueMarker> marker2Average = new HashMap<String, ValueMarker> ();

    public static Map<String, XYPlot> plotMap = new HashMap<String, XYPlot> ();
    	
    public static Map<String, Measurement> valuesMap = new HashMap<String, Measurement>();
    public static Map<String, Measurement> values2Map = new HashMap<String, Measurement>();
    
    public static Map<String, Boolean> avrgCtrl1Enabled = new HashMap<String, Boolean>(); // flag for enabling/disabling average control elements, 1st sensor
    public static Map<String, Boolean> avrgCtrl2Enabled = new HashMap<String, Boolean>(); // flag for enabling/disabling average control elements, 2nd sensor
    
    public static Map<String, ValueMarker> avrg1High = new HashMap<String, ValueMarker> ();
    public static Map<String, ValueMarker> avrg1Low  = new HashMap<String, ValueMarker> ();
    public static Map<String, ValueMarker> avrg2High = new HashMap<String, ValueMarker> ();
    public static Map<String, ValueMarker> avrg2Low  = new HashMap<String, ValueMarker> ();
    
    /**
     * map with int states of the plot
     * 0 = OK
     * 1 = average control line trespassed
     * 2 = warning level trespassed
     * 3 = critical value trespassed
     */
    public static Map<String, Integer> plotStateMap = new HashMap<String, Integer> ();
   

    XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer(); 
    
    static XYDifferenceRenderer renderer2 = new XYDifferenceRenderer(Color.green,Color.red, true);

	static CombinedDomainXYPlot plot;	
	
	/**
	 * creates all relevant data and adds it into the corresponding maps
	 * @param UID	UID of the plotting sensor
	 */
	@SuppressWarnings("deprecation")
	public static void addPlot(Brick newBrick)
	{
		//create series
		TimeSeries newSeries =  new TimeSeries("" + 0,Millisecond.class);
		TimeSeries newSeries2 =  new TimeSeries("" + 0,Millisecond.class);
		Measurement m1 = new Measurement(maxValues, maxCycles, newBrick.uid, 0);
		valuesMap.put(newBrick.uid, m1);
		if (newBrick.checked3 == true)
		{
			Measurement m2 = new Measurement(maxValues, maxCycles, newBrick.uid, 1);
			values2Map.put(newBrick.uid, m2);
		}
		
		//create entry in state map
		plotStateMap.put(newBrick.uid, 0);
		
		//create avrgCtrlEnabled maps
		if (newBrick.controlAverage == true) avrgCtrl1Enabled.put(newBrick.uid, true);
		else avrgCtrl1Enabled.put(newBrick.uid, false);
		if (newBrick.controlAverage2 == true) avrgCtrl2Enabled.put(newBrick.uid, true);
		else avrgCtrl2Enabled.put(newBrick.uid, false);

		//create series map entry
		seriesMap.put(newBrick.uid, newSeries);
		seriesMap2.put(newBrick.uid, newSeries2);
		
		//create collection map entry
		seriesCollectionMap.put(newBrick.uid, new TimeSeriesCollection(newSeries));
		seriesCollectionMap2.put(newBrick.uid, new TimeSeriesCollection(newSeries2));
				
		// create plot map entry, special case for current/voltage brick, since 
		// it has 2 parallel measurements and therefore 2 graphs must be treated
		XYPlot tmpSubPlot;	
		tmpSubPlot = new XYPlot(seriesCollectionMap.get(newBrick.uid), null,null, new StandardXYItemRenderer());

		// create the 1st graph 
		if (newBrick.checked2 == true)
		{
			//create plot map entry
			NumberAxis rangeAxis = new NumberAxis(String.valueOf(constants.brickUnitMap.get(newBrick.deviceIdentifier)));
			rangeAxis.setAutoRangeIncludesZero(true);	
			tmpSubPlot.setRangeAxis(0, rangeAxis);
			rangeAxis.setLabelPaint(Color.BLUE);
			rangeAxis.setVisible(newBrick.checked2);			
			tmpSubPlot.setDataset(0, seriesCollectionMap.get(newBrick.uid));
			
			//set dot - shape
			//Shape cross = ShapeUtilities.createDiagonalCross(3, 1);						
				
			// create and store renderer
			XYItemRenderer renderer1 = new XYLineAndShapeRenderer();
			renderer1 = tmpSubPlot.getRenderer();
			renderer1.setSeriesPaint(0, Color.BLUE);
			renderer1.setSeriesStroke( 0, new BasicStroke( 3 ) );
			// line = dashes:
			//float dash[] = {5.0f};
			//renderer1.setSeriesStroke( 0, new BasicStroke(3,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			//renderer1.setSeriesShape(0, cross);
			tmpSubPlot.setRenderer(0, renderer1);
				
			// set colors
			tmpSubPlot.setBackgroundPaint(Color.white);
			tmpSubPlot.setDomainGridlinePaint(Color.lightGray);
			tmpSubPlot.setRangeGridlinePaint(Color.lightGray);
			//tmpSubPlot.setRenderer(renderer2);
				
			//set font
			rangeAxis.setLabelFont(customFonts.get("axisLabelFont"));
			rangeAxis.setTickLabelFont(customFonts.get("axisValueFont"));
						
			// put everything to the maps
			rendererMap.put(newBrick.uid, renderer1);
			plotMap.put(newBrick.uid, tmpSubPlot);					
			axisMap.put(newBrick.uid, rangeAxis);			
		}
		
		// create the 2nd graph		
		if (newBrick.checked3 == true)
		{			
			//set second axis for voltage/ampere brick
			NumberAxis secondaryAxis = new NumberAxis(String.valueOf(constants.brick2ndUnitMap.get(newBrick.deviceIdentifier)));
			secondaryAxis.setAutoRangeIncludesZero(true);
			tmpSubPlot.setRangeAxis(1, secondaryAxis);		
			secondaryAxis.setLabelPaint(Color.RED);
			secondaryAxis.setVisible(newBrick.checked3);
			tmpSubPlot.setDataset(1, seriesCollectionMap2.get(newBrick.uid));
			tmpSubPlot.mapDatasetToRangeAxis(1, 1);
				
			//set font
			secondaryAxis.setLabelFont(customFonts.get("axisLabelFont"));
			secondaryAxis.setTickLabelFont(customFonts.get("axisValueFont"));						
			
			// create and store renderer
			XYItemRenderer renderer2 = new StandardXYItemRenderer();
			//renderer2 = tmpSubPlot.getRenderer();
			renderer2.setSeriesPaint(1, Color.RED);
			renderer2.setSeriesStroke( 0, new BasicStroke( 3 ) );
			tmpSubPlot.setRenderer(1, renderer2);				

			// set colors
			tmpSubPlot.setBackgroundPaint(Color.white);
			tmpSubPlot.setDomainGridlinePaint(Color.lightGray);
			tmpSubPlot.setRangeGridlinePaint(Color.lightGray);
			
			//----------------------------------------------------------------------------------
			//create min1 critical map value
			ValueMarker vm5 = new ValueMarker(newBrick.tresholdMin2);
			markerMapMin2Critical.put(newBrick.uid, vm5);		
			// set critical line				
			markerMapMin2Critical.get(newBrick.uid).setPaint(Color.red);
			markerMapMin2Critical.get(newBrick.uid).setLabel(String.valueOf(constants.brick2ndUnitMap.get(newBrick.deviceIdentifier))+" critical min");
			markerMapMin2Critical.get(newBrick.uid).setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			markerMapMin2Critical.get(newBrick.uid).setLabelTextAnchor(TextAnchor.TOP_RIGHT);
			tmpSubPlot.addRangeMarker(1, markerMapMin2Critical.get(newBrick.uid), Layer.BACKGROUND);
			
			//create min1 warning map value
			ValueMarker vm6 = new ValueMarker(newBrick.tresholdMin2+newBrick.tresholdMin2*warningPercentage/100);
			markerMapMin2Warning.put(newBrick.uid, vm6);		
			// set warning line				
			markerMapMin2Warning.get(newBrick.uid).setPaint(Color.orange);
			markerMapMin2Warning.get(newBrick.uid).setLabel(String.valueOf(constants.brick2ndUnitMap.get(newBrick.deviceIdentifier))+" warning min");
			markerMapMin2Warning.get(newBrick.uid).setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			markerMapMin2Warning.get(newBrick.uid).setLabelTextAnchor(TextAnchor.TOP_RIGHT);
			//tmpSubPlot.addRangeMarker(markerMapMin2Warning.get(newBrick.uid));
			tmpSubPlot.addRangeMarker(1, markerMapMin2Warning.get(newBrick.uid), Layer.BACKGROUND);		
			
			//create max1 critical map value
			ValueMarker vm7 = new ValueMarker(newBrick.tresholdMax2);
			markerMapMax2Critical.put(newBrick.uid, vm7);		
			// set critical line				
			markerMapMax2Critical.get(newBrick.uid).setPaint(Color.red);
			markerMapMax2Critical.get(newBrick.uid).setLabel(String.valueOf(constants.brick2ndUnitMap.get(newBrick.deviceIdentifier))+" critical max");
			markerMapMax2Critical.get(newBrick.uid).setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			markerMapMax2Critical.get(newBrick.uid).setLabelTextAnchor(TextAnchor.TOP_RIGHT);
			tmpSubPlot.addRangeMarker(1, markerMapMax2Critical.get(newBrick.uid), Layer.BACKGROUND);
			
			//create max1 warning map value
			ValueMarker vm8 = new ValueMarker(newBrick.tresholdMax2+newBrick.tresholdMax2*warningPercentage/100);
			markerMapMax2Warning.put(newBrick.uid, vm8);		
			// set warning line				
			markerMapMax2Warning.get(newBrick.uid).setPaint(Color.orange);
			markerMapMax2Warning.get(newBrick.uid).setLabel(String.valueOf(constants.brick2ndUnitMap.get(newBrick.deviceIdentifier))+" warning max");
			markerMapMax2Warning.get(newBrick.uid).setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			markerMapMax2Warning.get(newBrick.uid).setLabelTextAnchor(TextAnchor.TOP_RIGHT);
			tmpSubPlot.addRangeMarker(1, markerMapMax2Warning.get(newBrick.uid), Layer.BACKGROUND);
			
			// create and add min, max and average markers
			// create maxima marker
			ValueMarker vmMax = new ValueMarker(0);
			vmMax.setPaint(Color.orange);
			vmMax.setLabel("max");
			vmMax.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			vmMax.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
			// create minima marker
			ValueMarker vmMin = new ValueMarker(0);
			vmMin.setPaint(Color.orange);
			vmMin.setLabel("min");
			vmMin.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			vmMin.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
			// create average marker
			ValueMarker vmAvg = new ValueMarker(0);
			vmAvg.setPaint(Color.red);
			vmAvg.setLabel("average");
			vmAvg.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			vmAvg.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
			// add to maps
			marker2Maxima.put(newBrick.uid, vmMax);
			marker2Minima.put(newBrick.uid, vmMin);
			marker2Average.put(newBrick.uid, vmAvg);
			// add to plot
			tmpSubPlot.addRangeMarker(1, vmMax, Layer.BACKGROUND);
			tmpSubPlot.addRangeMarker(1, vmMin, Layer.BACKGROUND);
			tmpSubPlot.addRangeMarker(1, vmAvg, Layer.BACKGROUND);
			
			// create and add avrgCntrMarkers
			// create upper marker
			ValueMarker avrgCtrl2high = new ValueMarker(newBrick.getAvg2high());
			avrgCtrl2high.setPaint(Color.orange);
			avrgCtrl2high.setLabel("avrg high");
			avrgCtrl2high.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			avrgCtrl2high.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
			// create lower marker
			ValueMarker avrgCtrl2low = new ValueMarker(newBrick.getAvg2low());
			avrgCtrl2low.setPaint(Color.orange);
			avrgCtrl2low.setLabel("avrg low");
			avrgCtrl2low.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			avrgCtrl2low.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
			// add both markers
			avrg2High.put(newBrick.uid, avrgCtrl2high);
			avrg2Low.put(newBrick.uid, avrgCtrl2low);
			// add both to plot
			if (newBrick.controlAverage2)
			{
				tmpSubPlot.addRangeMarker(1, avrgCtrl2high, Layer.BACKGROUND);
				tmpSubPlot.addRangeMarker(1, avrgCtrl2low, Layer.BACKGROUND);
			}					
			//----------------------------------------------------------------------------------
						
			// put everything to the map
			rendererMap2.put(newBrick.uid, renderer2);
			plotMap.put(newBrick.uid, tmpSubPlot);	
			axisMap2.put(newBrick.uid, secondaryAxis);				
		}
		
		// 1st graph markers--------------------------------------------------------------------------------------------------
		//create min1 critical map value
		ValueMarker vm1 = new ValueMarker(newBrick.tresholdMin1);
		markerMapMin1Critical.put(newBrick.uid, vm1);		
		// set critical line				
		markerMapMin1Critical.get(newBrick.uid).setPaint(Color.red);
		/// .setLabel("critical");
		//markerMapMin1Critical.get(newBrick.uid).setLabelAnchor(RectangleAnchor.BOTTOM);
		markerMapMin1Critical.get(newBrick.uid).setLabel(String.valueOf(constants.brickUnitMap.get(newBrick.deviceIdentifier))+" critical min");
		markerMapMin1Critical.get(newBrick.uid).setLabelAnchor(RectangleAnchor.TOP_LEFT);
		markerMapMin1Critical.get(newBrick.uid).setLabelTextAnchor(TextAnchor.TOP_LEFT);
		plotMap.get(newBrick.uid).addRangeMarker(markerMapMin1Critical.get(newBrick.uid));
		
		//create min1 warning map value
		ValueMarker vm2 = new ValueMarker(newBrick.tresholdMin1+newBrick.tresholdMin1*warningPercentage/100);
		markerMapMin1Warning.put(newBrick.uid, vm2);		
		// set warning line				
		markerMapMin1Warning.get(newBrick.uid).setPaint(Color.orange);
		//marker2Map.get(newBrick.uid).setPaint(Color.);
		/// marker2Map.get(newBrick.uid).setLabel("warning");		
		markerMapMin1Warning.get(newBrick.uid).setLabel(String.valueOf(constants.brickUnitMap.get(newBrick.deviceIdentifier))+" warning min");
		markerMapMin1Warning.get(newBrick.uid).setLabelAnchor(RectangleAnchor.TOP_LEFT);
		markerMapMin1Warning.get(newBrick.uid).setLabelTextAnchor(TextAnchor.TOP_LEFT);
		plotMap.get(newBrick.uid).addRangeMarker(markerMapMin1Warning.get(newBrick.uid));
		
		//create max1 critical map value
		ValueMarker vm3 = new ValueMarker(newBrick.tresholdMax1);
		markerMapMax1Critical.put(newBrick.uid, vm3);		
		// set critical line				
		markerMapMax1Critical.get(newBrick.uid).setPaint(Color.red);
		/// .setLabel("critical");
		//markerMapMax1Critical.get(newBrick.uid).setLabelAnchor(RectangleAnchor.BOTTOM);
		markerMapMax1Critical.get(newBrick.uid).setLabel(String.valueOf(constants.brickUnitMap.get(newBrick.deviceIdentifier))+" critical max");		
		markerMapMax1Critical.get(newBrick.uid).setLabelAnchor(RectangleAnchor.TOP_LEFT);
		markerMapMax1Critical.get(newBrick.uid).setLabelTextAnchor(TextAnchor.TOP_LEFT);		
		plotMap.get(newBrick.uid).addRangeMarker(markerMapMax1Critical.get(newBrick.uid));
		
		//create max1 warning map value
		ValueMarker vm4 = new ValueMarker(newBrick.tresholdMax1+newBrick.tresholdMax1*warningPercentage/100);
		markerMapMax1Warning.put(newBrick.uid, vm4);		
		// set warning line				
		markerMapMax1Warning.get(newBrick.uid).setPaint(Color.orange);
		markerMapMax1Warning.get(newBrick.uid).setLabel(String.valueOf(constants.brickUnitMap.get(newBrick.deviceIdentifier))+" warning max");
		markerMapMax1Warning.get(newBrick.uid).setLabelAnchor(RectangleAnchor.TOP_LEFT);
		markerMapMax1Warning.get(newBrick.uid).setLabelTextAnchor(TextAnchor.TOP_LEFT);
		plotMap.get(newBrick.uid).addRangeMarker(markerMapMax1Warning.get(newBrick.uid));
		
		// create and add min, max and average markers
		// create maxima marker
		ValueMarker vmMax = new ValueMarker(0);
		vmMax.setPaint(Color.cyan);
		vmMax.setLabel("max");
		vmMax.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		vmMax.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		// create minima marker
		ValueMarker vmMin = new ValueMarker(0);
		vmMin.setPaint(Color.cyan);
		vmMin.setLabel("min");
		vmMin.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		vmMin.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		// create average marker
		ValueMarker vmAvg = new ValueMarker(0);
		vmAvg.setPaint(Color.blue);
		vmAvg.setLabel("average");
		vmAvg.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		vmAvg.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		// add to maps
		markerMaxima.put(newBrick.uid, vmMax);
		markerMinima.put(newBrick.uid, vmMin);
		markerAverage.put(newBrick.uid, vmAvg);
		// add to plot
		plotMap.get(newBrick.uid).addRangeMarker(vmMax);
		plotMap.get(newBrick.uid).addRangeMarker(vmMin);
		plotMap.get(newBrick.uid).addRangeMarker(vmAvg);

		// create and add avrgCntrMarkers
		// create upper marker
		ValueMarker avrgCtrl1high = new ValueMarker(newBrick.getAvg1high());
		avrgCtrl1high.setPaint(Color.orange);
		avrgCtrl1high.setLabel("avrg high");
		avrgCtrl1high.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		avrgCtrl1high.setLabelTextAnchor(TextAnchor.TOP_LEFT);		
		// create lower marker
		ValueMarker avrgCtrl1low = new ValueMarker(newBrick.getAvg1low());
		avrgCtrl1low.setPaint(Color.orange);
		avrgCtrl1low.setLabel("avrg low");
		avrgCtrl1low.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		avrgCtrl1low.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		// add both markers
		avrg1High.put(newBrick.uid, avrgCtrl1high);
		avrg1Low.put(newBrick.uid, avrgCtrl1low);
		// add both to plot
		if (newBrick.controlAverage)
		{
			plotMap.get(newBrick.uid).addRangeMarker(avrgCtrl1high);
			plotMap.get(newBrick.uid).addRangeMarker(avrgCtrl1low);
		}
		// -----------------------------------------------------------------------------------------------------
				
								
		// set title
		NumberAxis axisForTitleOnly = new NumberAxis(data.constants.brickIdMap.get(newBrick.deviceIdentifier) + " (" +newBrick.uid+")");
		axisForTitleOnly.setLabelFont(customFonts.get("titleFont"));
		axisForTitleOnly.setTickLabelsVisible(false);
		axisForTitleOnly.setTickMarksVisible(false);
		axisForTitleOnly.setMinorTickMarksVisible(false);
		axisForTitleOnly.setAxisLineVisible(false);
		plotMap.get(newBrick.uid).setDomainAxis(1, axisForTitleOnly);
		
		//add subblot to the main plot		
		plot.add(plotMap.get(newBrick.uid));
	}

		
	/**
	 * updates the threshold marker on the graph of the given sensor 
	 * @param UID UID of the sensor
	 * @param treshold new threshold
	 */
	public static void updateTresholdMin1(String UID, double treshold)
	{
		if (markerMapMin1Critical.containsKey(UID))
		{
			ValueMarker vm = markerMapMin1Critical.get(UID);
			vm.setValue(treshold);
			markerMapMin1Critical.put(UID, vm);
			
			ValueMarker vm2 = markerMapMin1Warning.get(UID);
			//vm2.setValue(treshold+treshold*warningPercentage/100);
			vm2.setValue(treshold+treshold);
			markerMapMin1Warning.put(UID, vm2);
			
			ValueMarker vm3 = markerMapMax1Warning.get(UID);
			double diff = markerMapMin1Warning.get(UID).getValue()-markerMapMin1Critical.get(UID).getValue();
			vm3.setValue(markerMapMax1Critical.get(UID).getValue()-diff);
			markerMapMax1Warning.put(UID, vm3);			
		}		
	}
	
	
	/**
	 * update average control values
	 * @param UID	uid of the brick
	 * @param value	new value
	 * @param index	1=sensor1, 2=sensor2
	 * @param high	true = upper line, false = lower line 
	 */
	public static void updateAvrgCtrl(String UID, double value,  int index, boolean high)
	{
		switch (index)
		{
			case 1:
					if (high==true)
					{
						ValueMarker vm = avrg1High.get(UID);
						vm.setValue(value);
						avrg1High.put(UID, vm);
					}
					if (high==false)
					{
						ValueMarker vm = avrg1Low.get(UID);
						vm.setValue(value);
						avrg1Low.put(UID, vm);
					}
					break;
					
			case 2:				
					if (high==true)
					{
						ValueMarker vm = avrg2High.get(UID);
						vm.setValue(value);
						avrg2High.put(UID, vm);
					}
					if (high==false)
					{
						ValueMarker vm = avrg2Low.get(UID);
						vm.setValue(value);
						avrg2Low.put(UID, vm);
					}
					break;			
		}		
	}
	
	
	/**
	 * updates the threshold marker on the graph of the given sensor 
	 * @param UID UID of the sensor
	 * @param treshold new threshold
	 */
	public static void updateTresholdMin2(String UID, double treshold)
	{
		if (markerMapMin2Critical.containsKey(UID))
		{
			ValueMarker vm = markerMapMin2Critical.get(UID);
			vm.setValue(treshold);
			markerMapMin2Critical.put(UID, vm);
			
			ValueMarker vm2 = markerMapMin2Warning.get(UID);
			vm2.setValue(treshold+treshold);
			markerMapMin2Warning.put(UID, vm2);
			
			ValueMarker vm3 = markerMapMax2Warning.get(UID);
			double diff = markerMapMin2Warning.get(UID).getValue()-markerMapMin2Critical.get(UID).getValue();
			vm3.setValue(markerMapMax2Critical.get(UID).getValue()-diff);
			markerMapMax2Warning.put(UID, vm3);			
			
		}		
	}

	
	/**
	 * updates the threshold marker on the graph of the given sensor 
	 * @param UID UID of the sensor
	 * @param treshold new threshold
	 */
	
	public static void updateTresholdMax1(String UID, double treshold)
	{
		if (markerMapMax1Critical.containsKey(UID))
		{
			ValueMarker vm = markerMapMax1Critical.get(UID);
			vm.setValue(treshold);
			markerMapMax1Critical.put(UID, vm);
			
			ValueMarker vm2 = markerMapMax1Warning.get(UID);
			double diff = markerMapMin1Warning.get(UID).getValue()-markerMapMin1Critical.get(UID).getValue();
			vm2.setValue(treshold-diff);
			markerMapMax1Warning.put(UID, vm2);
		}		
	}

		
	/**
	 * updates the threshold marker on the graph of the given sensor 
	 * @param UID UID of the sensor
	 * @param treshold new threshold
	 */	
	public static void updateTresholdMax2(String UID, double treshold)
	{
		if (markerMapMax2Critical.containsKey(UID))
		{
			ValueMarker vm = markerMapMax2Critical.get(UID);
			vm.setValue(treshold);
			markerMapMax2Critical.put(UID, vm);
			
			ValueMarker vm2 = markerMapMax2Warning.get(UID);
			double diff = markerMapMin2Warning.get(UID).getValue()-markerMapMin2Critical.get(UID).getValue();
			vm2.setValue(treshold-diff);
			markerMapMax2Warning.put(UID, vm2);
		}		
	}
	
	
	/**
	 * update maxima marker
	 * @param UID		uid of the sensor	
	 * @param value		new maximum vallue
	 * @param index		sensor index, 0 = primary sensor, 1 = secondary
	 */
	public static void updateMaxima(String UID, double value, int index)
	{
		if (index == 0)
		{
			if (markerMaxima.containsKey(UID))
			{
				ValueMarker vm = markerMaxima.get(UID);
				vm.setValue(value);
				markerMaxima.put(UID, vm);
			}
		}
		if (index == 1)
		{
			if (marker2Maxima.containsKey(UID))
			{
				ValueMarker vm = marker2Maxima.get(UID);
				vm.setValue(value);
				marker2Maxima.put(UID, vm);
			}
		}
	}

	
	/**
	 * update minima marker
	 * @param UID		uid of the sensor	
	 * @param value		new maximum vallue
	 * @param index		sensor index, 0 = primary sensor, 1 = secondary
	 */
	public static void updateMinima(String UID, double value, int index)
	{
		if (index == 0)
		{
			if (markerMinima.containsKey(UID))
			{
				ValueMarker vm = markerMinima.get(UID);
				vm.setValue(value);
				markerMinima.put(UID, vm);
			}
		}
		if (index == 1)
		{
			if (marker2Minima.containsKey(UID))
			{
				ValueMarker vm = marker2Minima.get(UID);
				vm.setValue(value);
				marker2Minima.put(UID, vm);
			}
		}
	}

	
	/**
	 * update maxima marker
	 * @param UID		uid of the sensor	
	 * @param value		new maximum vallue
	 * @param index		sensor index, 0 = primary sensor, 1 = secondary
	 */
	public static void updateAverage(String UID, double value, int index)
	{
		if (index == 0)
		{
			if (markerAverage.containsKey(UID))
			{
				ValueMarker vm = markerAverage.get(UID);
				vm.setValue(value);
				markerAverage.put(UID, vm);
				
				// verify whether we are between admissible average control lines
	    		if ( (value > Brick.getBrick(connectionData.BrickList, UID).getAvg1high()) ||
		    				(value < Brick.getBrick(connectionData.BrickList, UID).getAvg1low()) )
	    		{
	    			if (avrgCtrl1Enabled.get(UID) == true)
	    			{
	    				if (plotStateMap.get(UID)==0) plotMap.get(UID).setBackgroundPaint(Color.GRAY);
	    				plotStateMap.put(UID,1);
	    			}
	    		}
	    		else if (plotStateMap.get(UID)==1)
	    		{
	    			plotMap.get(UID).setBackgroundPaint(Color.white);
	    			plotStateMap.put(UID,0);
	    		}
			}
		}
		if (index == 1)
		{
			if (marker2Average.containsKey(UID))
			{
				ValueMarker vm = marker2Average.get(UID);
				vm.setValue(value);
				marker2Average.put(UID, vm);
				
				// verify whether we are between admissible average control lines
	    		if ( (value > Brick.getBrick(connectionData.BrickList, UID).getAvg2high()) ||
		    				(value < Brick.getBrick(connectionData.BrickList, UID).getAvg2low()) )
	    		{
	    			if (avrgCtrl2Enabled.get(UID) == true)
	    			{	    			
	    				if (plotStateMap.get(UID)==0) plotMap.get(UID).setBackgroundPaint(Color.GRAY);
	    				plotStateMap.put(UID,1);
	    			}
	    		}
	    		else if (plotStateMap.get(UID)==1)
	    		{
	    			plotMap.get(UID).setBackgroundPaint(Color.white);
	    			plotStateMap.put(UID,0);
	    		}	    		
			}
		}
	}

	
	/**
	 * remove subplot from the main plot, remove all data of the subblot
	 * @param UID UID of the brick, which is presented by the plot
	 */
	public static void removePlot(String UID)
	{
		//remove markers		
		//plot.clearRangeMarkers();
		//plotMap.get(UID).clearRangeMarkers();
		
		//remove from the main plot
		plot.remove(plotMap.get(UID));
		
		//remove from series map entry
		seriesMap.remove(UID);
		
		//remove from collection map entry
		seriesCollectionMap.remove(UID);

		//remove from plot map entry
		plotMap.remove(UID);		
		
		//remove from marker maps
		markerMapMin1Critical.remove(UID);			
		markerMapMin1Warning.remove(UID);
		markerMapMax1Critical.remove(UID);
		markerMapMax1Warning.remove(UID);
		markerMapMin2Critical.remove(UID);			
		markerMapMin2Warning.remove(UID);
		markerMapMax2Critical.remove(UID);
		markerMapMax2Warning.remove(UID);			
	}
	
	
	/**
	 * window constructor for chart window
	 * @param title	title for the new window
	 */
	public sensorWindow(final String title) { 	
    	
        super(title);
        
        // font customizing -------------------------------------------------------
        /*
        Font font1 = null;
		try {
			font1 = Font.createFont(Font.TRUETYPE_FONT, new File("U:/workspace/SWTtest/fonts/roboto/Roboto-Black.ttf"));
		} catch (FontFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        StandardChartTheme chartTheme = new StandardChartTheme("robotTheme");
        chartTheme.setExtraLargeFont(font1.deriveFont(24f));
        chartTheme.setLargeFont(font1.deriveFont(16f));
        chartTheme.setRegularFont(font1.deriveFont(12f));
        chartTheme.setSmallFont(font1.deriveFont(10f));
        ChartFactory.setChartTheme(chartTheme);
        */
        Font font1 = new Font("Tahoma", Font.BOLD,	16);
        Font font2 = new Font("Tahoma", Font.PLAIN,	12);
        Font font3 = new Font("Tahoma", Font.BOLD,	16);
        customFonts.put("axisLabelFont", font1);
        customFonts.put("axisValueFont", font2);
        customFonts.put("titleFont", font3);
        // -------------------------------------------------------------------------
        
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        plot = new CombinedDomainXYPlot(new DateAxis("Time"));
                                     
       	for (int i=0; i<connectionData.presentedBrickList.size();i++)
       	{
			Brick tmpBrick = connectionData.presentedBrickList.get(i);
			addPlot(tmpBrick);
		}
	       
        final JFreeChart chart = new JFreeChart("", plot);
        chart.setBorderPaint(Color.black);
        chart.setBorderVisible(true);
        chart.setBackgroundPaint(Color.white);   
        chart.removeLegend();
        
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        //plot.setRenderer(renderer2);
        //plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 4, 4, 4, 4));
        final ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(chartRangeSec*1000);  			// chart range seconds
        axis.setLabelFont(customFonts.get("axisLabelFont"));
        axis.setTickLabelFont(customFonts.get("axisValueFont"));
                       
        final JPanel content = new JPanel(new BorderLayout());

        final ChartPanel chartPanel = new ChartPanel(chart);
        content.add(chartPanel);        
        //content.add(getScrollBar(xAxe), BorderLayout.SOUTH);                     
                
        // disable zoom
        chartPanel.setRangeZoomable(false);
        chartPanel.setDomainZoomable(false);
        
        // mouse selection
        chartPanel.addMouseListener(new MouseMarker(chartPanel));        
        
        
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 470));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(content); 
        
        
        //scroll bar
        slider = new JSlider(1, sliderValuesNumber);
        slider.setValue(sliderValuesNumber);
        slider.setEnabled(false);
        slider.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            	if (sliderData.sliderActive == true)
            	{
            		int sliderValue = slider.getValue();
            		if (sliderValue == sliderValuesNumber) sliderUpdate = true;
            		else sliderUpdate = false;
            		System.out.println("slider : "+sliderValue);
            		System.out.println("Millis first: "+sliderData.getMilliseconds(sliderValue-sliderValuesNumber).toString());
            		System.out.println("Millis last : "+sliderData.getMilliseconds(sliderValue).toString());
        			DateRange range = new DateRange(sliderData.getMilliseconds(sliderValue-sliderValuesNumber).getFirstMillisecond(),
							sliderData.getMilliseconds(sliderValue).getFirstMillisecond());
            		plot.getDomainAxis().setRange(range);
            	}               
            }
        });
        //chartPanel.add(slider);
        /*
        final Panel chartPanel2 = new Panel();
        chartPanel2.add(slider);
        content.add(chartPanel2, BorderLayout.SOUTH);
        */
        content.add(slider, BorderLayout.SOUTH);
        
        
        this.addComponentListener(new java.awt.event.ComponentAdapter()
        {
        	public void componentResized(java.awt.event.ComponentEvent e)
        	{
        		chartPanel.setMaximumDrawWidth((int)e.getComponent().getSize().getWidth());
        		chartPanel.setMaximumDrawHeight((int)e.getComponent().getSize().getHeight());
        		/*
        		chartPanel.setPreferredSize(e.getComponent().getPreferredSize());
        		chartPanel.setSize(e.getComponent().getSize());
        		chartPanel.setLocation(0,0);
        		*/        
        	}
        });
        
                    
        
    	this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	System.out.println("closing!!!!!!!!!!!!!!");
                functions.windowController.closeSensorWindow();
            }

        });                 
    	    	
    }
    
		
	/**
	 * mouse select
	 * @author kv1
	 *
	 */
	private final static class  MouseMarker extends MouseAdapter{
        private Marker marker;
        private Double markerStart[] = new Double[2];
        private Double markerEnd[] = new Double[2];
        private XYPlot mouse_plot;
        private final JFreeChart chart;
        private final ChartPanel panel;    
        private HashMap<XYPlot, Marker> markerMap = new HashMap<XYPlot, Marker>();
        private HashMap<XYPlot, Boolean> areaMarked = new HashMap<XYPlot, Boolean>();


        public MouseMarker(ChartPanel panel) {
            this.panel = panel;
            this.chart = panel.getChart();
            this.mouse_plot = (XYPlot) chart.getPlot();
            //this.mouse_plot = plot.findSubplot(plot, source)
            //this.mouse_plot = plotMap.get(connectionData.presentedBrickList.get(0).uid);
            /*
            if (tmp_plot != null )
            {
            	System.out.println("plot clicked: "+tmp_plot.toString());
            	this.mouse_plot = tmp_plot; 
            }
            else System.out.println("no plot");
            */
            // plot.findSubplot(panel.getChartRenderingInfo().getPlotInfo(), panel.getMousePosition());
            this.mouse_plot = null;
        }

        /*
        private void updateMarker(){
            if (marker != null){
            	if (mouse_plot != null) mouse_plot.removeDomainMarker(marker,Layer.BACKGROUND);
            }
            if (!( markerStart.isNaN() && markerEnd.isNaN())){
                if ( markerEnd > markerStart){                	
                    marker = new IntervalMarker(markerStart, markerEnd);
                    marker.setPaint(new Color(0xDD, 0xFF, 0xDD, 0x80));
                    marker.setAlpha(0.5f);
                    if (mouse_plot != null) mouse_plot.addDomainMarker(marker,Layer.BACKGROUND);
                }
            }
        }
        */
        private void updateMarker()
        {
            if (marker != null)
            {
            	if (mouse_plot != null)
            	{
            		//mouse_plot.removeDomainMarker(marker,Layer.BACKGROUND);
            		mouse_plot.removeDomainMarker(markerMap.get(mouse_plot),Layer.BACKGROUND);
            		areaMarked.put(mouse_plot, false);
            		//System.out.println("delete area");
            	}
            }
            if (!( markerStart[0].isNaN() && markerEnd[0].isNaN()))
            {
                if ( markerEnd[0] > markerStart[0])
                {                	
                    marker = new IntervalMarker(markerStart[0], markerEnd[0]);
                    markerMap.put(mouse_plot, marker);
                    marker.setPaint(new Color(0xDD, 0xFF, 0xDD, 0x80));
                    marker.setAlpha(0.5f);
                    if (mouse_plot != null)
                    {
                    	mouse_plot.addDomainMarker(marker,Layer.BACKGROUND);
                    	areaMarked.put(mouse_plot, true);
                    	//System.out.println("add area");
                    	collectPlotValues(markerStart[0], markerEnd[0], mouse_plot.getDataset());
                    	functions.Events.handleMouseSelection();
                    }
                }
            }
        }

        
        /**
         * returns x and y value of the given mouse event 
         * @param e event
         * @return	double array, 1st value = X, 2nd = Y 
         */
        private Double[] getPosition(MouseEvent e)
        {
        	Double[]r = new Double[2];
            //Point2D p = panel.translateScreenToJava2D( e.getPoint());
        	Point2D p = e.getPoint();
            Rectangle2D plotArea = panel.getScreenDataArea();
            XYPlot plotLocal = (XYPlot) chart.getPlot();
            r[0] = plotLocal.getDomainAxis().java2DToValue(p.getX(), plotArea, plotLocal.getDomainAxisEdge());
            // -------- 20.02
            XYPlot plotTest = plot.findSubplot(panel.getChartRenderingInfo().getPlotInfo(), panel.getMousePosition());
            r[1] = plotTest.getRangeAxis().java2DToValue(p.getY(), plotArea, plotLocal.getRangeAxisEdge());
            // end 20.02
            //r[1] = plotLocal.getRangeAxis().java2DToValue(p.getY(), plotArea, plotLocal.getRangeAxisEdge());
            return r;
        }

        
        @Override
        public void mouseReleased(MouseEvent e) 
        {
            markerEnd = getPosition(e);
            updateMarker();
            //System.out.println("marker end pos: "+markerEnd);
        }

        
        @Override
        public void mousePressed(MouseEvent e) 
        {
            markerStart = getPosition(e);
            System.out.print("marker = "+doubleToTime(markerStart[0]));
            System.out.println("value = "+markerStart[1]);
            
            //tmp_plot = plot.findSubplot(panel.getChartRenderingInfo().getPlotInfo(), panel.getMousePosition());
            this.mouse_plot = plot.findSubplot(panel.getChartRenderingInfo().getPlotInfo(), panel.getMousePosition());
            
            // begin 19.02.2015
            XYDataset xydataset= mouse_plot.getDataset();
            double d = mouse_plot.getDomainCrosshairValue(); //get crosshair X value
            double r = mouse_plot.getRangeCrosshairValue();  //get crosshair y value
            System.out.println(""+doubleToTime(d)+",r = "+r);
            //System.out.println(""+ reportDate+", r = "+r);
            /*
            SeriesAndItemIndex index=getItemIndex(d,r,xydataset);
            if(index != null){
                System.out.println(index.toString());
            }
            */
            // end 19.02.2015
            /*
            Thread thread = new Thread(){
                public void run(){
                  System.out.println("Thread Running");
                  marker = new IntervalMarker(markerStart, markerEnd);
//                  markerMap.put(mouse_plot, marker);
                  marker.setPaint(new Color(0xDD, 0xFF, 0xDD, 0x80));
                  marker.setAlpha(0.5f);
                  if (mouse_plot != null) mouse_plot.addDomainMarker(marker,Layer.BACKGROUND);
                }
            };            
            thread.start();
            */              
        }
        
        
        public void mouseMoved(MouseEvent e)
        {
            if (( !markerStart[0].isNaN()) && (markerEnd[0].isNaN()))
            {
                marker = new IntervalMarker(markerStart[0], markerEnd[0]);
                markerMap.put(mouse_plot, marker);
                marker.setPaint(new Color(0xDD, 0xFF, 0xDD, 0x80));
                marker.setAlpha(0.5f);
                if (mouse_plot != null)
                {
                	mouse_plot.addDomainMarker(marker,Layer.BACKGROUND);
                	System.out.println("6");
                }
                System.out.println("7");
                
            }
        }
    }

	    
    /**
     * Handles a click on the button by adding new (random) data.
     *
     * @param e  the action event.
     */
    /*
    public void actionPerformed(final ActionEvent e) {
 
        for (int i = 0; i < SUBPLOT_COUNT; i++) {
            if (e.getActionCommand().endsWith(String.valueOf(i))) {
                final Millisecond now = new Millisecond();
                System.out.println("Now = " + now.toString());
                this.lastValue[i] = this.lastValue[i] * (0.90 + 0.2 * Math.random());
                this.datasets[i].getSeries(0).add(new Millisecond(), this.lastValue[i]);       
            }
        }

        if (e.getActionCommand().equals("ADD_ALL")) {
            final Millisecond now = new Millisecond();
            System.out.println("Now = " + now.toString());
            for (int i = 0; i < SUBPLOT_COUNT; i++) {
                this.lastValue[i] = this.lastValue[i] * (0.90 + 0.2 * Math.random());
                this.datasets[i].getSeries(0).add(new Millisecond(), this.lastValue[i]);       
            }
        }
    }
    */

        
	/**
	 * add a value to the second field of a sensor (used for sensors with 2 measurements)
	 * @param sensorUID
	 * @param value
	 */
    public void add2ndValue(String sensorUID, double value)
    {
    	//TODO: make this method more elegant by evolving plotStateMap.get-things
    	
		if (seriesCollectionMap2.containsKey(sensorUID)) seriesCollectionMap2.get(sensorUID).getSeries(0).addOrUpdate(new Millisecond(), value);
		
    	// add measurement value
    	if (values2Map.containsKey(sensorUID)) values2Map.get(sensorUID).addValue(value);

		
    	// check whether the value is below the threshold
    	if (markerMapMin2Critical.containsKey(sensorUID))
    	{
    		if ( (value > markerMapMin2Critical.get(sensorUID).getValue()) && 
    				(value <= (markerMapMin2Warning.get(sensorUID).getValue())) )
    		{
    			//under warning level
    			plotStateMap.put(sensorUID,2);
    			plotMap.get(sensorUID).setBackgroundPaint(Color.yellow);
    		}
    		else if (value <= markerMapMin2Critical.get(sensorUID).getValue())
    		{
    			//under threshold level
    			plotStateMap.put(sensorUID,3);
    			plotMap.get(sensorUID).setBackgroundPaint(Color.pink);
    		}
    		else 
    		{
    			//normal level
    			//plotMap.get(sensorUID).setBackgroundPaint(Color.white);
    			
    	    	// check whether the value is above the threshold
    	    	if (markerMapMax2Critical.containsKey(sensorUID))
    	    	{
    	    		if ( (value < markerMapMax2Critical.get(sensorUID).getValue()) && 
    	    				(value >= (markerMapMax2Warning.get(sensorUID).getValue())) )
    	    		{
    	    			//between the warning and critical
    	    			plotStateMap.put(sensorUID,2);
    	    			plotMap.get(sensorUID).setBackgroundPaint(Color.yellow);
    	    		}
    	    		else if (value >= markerMapMax2Critical.get(sensorUID).getValue())
    	    		{
    	    			//above the max threshold level
    	    			plotStateMap.put(sensorUID,3);
    	    			plotMap.get(sensorUID).setBackgroundPaint(Color.pink);
    	    		}
    	    		else 
    	    		{
    	    			//normal level
    	    			/*
    	    			if (!(plotMap.get(sensorUID).getBackgroundPaint() == Color.pink) &&
    	    			    !(plotMap.get(sensorUID).getBackgroundPaint() == Color.yellow))
    	    			    */
    	    			if (plotStateMap.get(sensorUID) != 1)
    	    			{
    	    				plotStateMap.put(sensorUID,0);
    	    				plotMap.get(sensorUID).setBackgroundPaint(Color.white);
    	    			}
    	    		}
    	    	}

    		}
    	}    	    

    }
    
    
    /**
     * add a value to the main field of the sensor 
     * @param sensorUID
     * @param value
     */
    public void addValue(String sensorUID, double value)
    {        
    	//TODO: make this method more elegant by evolving plotStateMap.get-things
    	
		//if (seriesCollectionMap.containsKey(sensorUID)) seriesCollectionMap.get(sensorUID).getSeries(0).add(new Millisecond(), value);
    	Millisecond ms = new Millisecond();
    	if (seriesCollectionMap.containsKey(sensorUID)) seriesCollectionMap.get(sensorUID).getSeries(0).addOrUpdate(ms, value);    	
    	
    	
    	// add timestamp to slider 
    	sliderData.addMS(ms);
    	
    	// if we have moved the slider at least once, but now it is on its start position
		// which means we want to auto-update the charts
    	if (sliderUpdate == true) 
    	{
    		DateRange range = new DateRange(sliderData.getMilliseconds(0).getFirstMillisecond(),
    										sliderData.getMilliseconds(sliderValuesNumber).getFirstMillisecond());
    		plot.getDomainAxis().setRange(range);    				
   		}    	
    	
    	// add measurement value
    	if (valuesMap.containsKey(sensorUID)) valuesMap.get(sensorUID).addValue(value);
    	
    	// check whether the value is below the threshold
    	if (markerMapMin1Critical.containsKey(sensorUID))
    	{
    		if ( (value > markerMapMin1Critical.get(sensorUID).getValue()) && 
    				(value <= (markerMapMin1Warning.get(sensorUID).getValue())) )
    		{
    			//under warning level
    			plotStateMap.put(sensorUID,2); 
    			plotMap.get(sensorUID).setBackgroundPaint(Color.yellow);
    		}
    		else if (value <= markerMapMin1Critical.get(sensorUID).getValue())
    		{
    			//under threshold level
    			plotStateMap.put(sensorUID,3);
    			plotMap.get(sensorUID).setBackgroundPaint(Color.pink);
    		}
    		else 
    		{
    			//normal level
    			//plotMap.get(sensorUID).setBackgroundPaint(Color.white);
    			
    	    	// check whether the value is above the threshold
    	    	if (markerMapMax1Critical.containsKey(sensorUID))
    	    	{
    	    		if ( (value < markerMapMax1Critical.get(sensorUID).getValue()) && 
    	    				(value >= (markerMapMax1Warning.get(sensorUID).getValue())) )
    	    		{
    	    			//between the warning and critical
    	    			plotStateMap.put(sensorUID,2);
    	    			plotMap.get(sensorUID).setBackgroundPaint(Color.yellow);
    	    		}
    	    		else if (value >= markerMapMax1Critical.get(sensorUID).getValue())
    	    		{
    	    			//above the max threshold level
    	    			plotStateMap.put(sensorUID,3);
    	    			plotMap.get(sensorUID).setBackgroundPaint(Color.pink);
    	    		}
    	    		else 
    	    		{
    	    			//normal level
    	    			/*
    	    			if (!(plotMap.get(sensorUID).getBackgroundPaint() == Color.pink) &&
        	    			    !(plotMap.get(sensorUID).getBackgroundPaint() == Color.yellow))
        	    			    */
    	    			if (plotStateMap.get(sensorUID) != 1)
    	    			{
    	    				plotStateMap.put(sensorUID,0);
    	    				plotMap.get(sensorUID).setBackgroundPaint(Color.white);
    	    			}
    	    			if (plotStateMap.get(sensorUID) == 1)
    	    			{
    	    				plotMap.get(sensorUID).setBackgroundPaint(Color.gray);
    	    			}
    	    		}
    	    	}
    		}
    	}    	    
    }
    
    
    /**
     * add value to the main map
     * @param uid	uid of the sensor
     * @param value	value
     */
    @SuppressWarnings("unchecked")
	private void addValueToMap(String uid, double value)
    {
    	if (valuesMap.containsKey(uid))
    	{
    		valuesMap.get(uid).addValue(value);
    	}
    }
    
    
    /**
     * activate slider
     */
    public static void activateSlider()
    {
    	slider.setEnabled(true);
    }

        
    /**
     * hide or unhide (depending on previous state) a graph
     * @param br
     */
    public static void hideUnhidePlot(Brick br, int index)
    {
    	if ((index == 1) && (rendererMap.get(br.uid) != null))
    	{
    		rendererMap.get(br.uid).setSeriesVisible(0, br.checked2);
    		axisMap.get(br.uid).setVisible(br.checked2);
    		// hide/show average control elements    		
    		hideUnhideAvgCtrl(br, 1);
    	}
    	if ((index == 2) && (rendererMap2.get(br.uid) != null))
    	{
    		rendererMap2.get(br.uid).setSeriesVisible(0, br.checked3);
    		axisMap2.get(br.uid).setVisible(br.checked3);
    		// hide/show average control elements
    		hideUnhideAvgCtrl(br, 2);
    	}
    }
    
                   
    /**
     * hide or show average control elements (depending on previous state)
     * @param br brick object
     */
    public static void hideUnhideAvgCtrl(Brick br, int index)
    {
    	if (index == 1)
    	{
    		if (avrgCtrl1Enabled.get(br.uid) == false)
    		{
    			avrgCtrl1Enabled.put(br.uid, true);
    			// add range markers to the plot again
    			plotMap.get(br.uid).addRangeMarker(avrg1High.get(br.uid));
    			plotMap.get(br.uid).addRangeMarker(avrg1Low.get(br.uid));    			
    		}
    		else if (avrgCtrl1Enabled.get(br.uid) == true)
    		{
    			avrgCtrl1Enabled.put(br.uid, false);    			
    			//remove range markers
    			plotMap.get(br.uid).removeRangeMarker(avrg1High.get(br.uid));
    			plotMap.get(br.uid).removeRangeMarker(avrg1Low.get(br.uid));
    			// set background color
	    		if (plotStateMap.get(br.uid)==1)
	    		{
	    			plotMap.get(br.uid).setBackgroundPaint(Color.GRAY);
	    			plotStateMap.put(br.uid,0);
	    		}
    		}
    	}
    	if (index == 2)
    	{
    		if (avrgCtrl2Enabled.get(br.uid) == false)
    		{
    			avrgCtrl2Enabled.put(br.uid, true);    			
    			// add both markers
    			plotMap.get(br.uid).addRangeMarker(1, avrg2High.get(br.uid), Layer.BACKGROUND);
    			plotMap.get(br.uid).addRangeMarker(1, avrg2Low.get(br.uid), Layer.BACKGROUND);
    		}
    		else if (avrgCtrl2Enabled.get(br.uid) == true)
    		{
    			avrgCtrl2Enabled.put(br.uid, false);    			
    			//remove range markers
    			plotMap.get(br.uid).removeRangeMarker(1, avrg2High.get(br.uid), Layer.BACKGROUND);
    			plotMap.get(br.uid).removeRangeMarker(1, avrg2Low.get(br.uid), Layer.BACKGROUND);
    			// set background color
	    		if (plotStateMap.get(br.uid)==1)
	    		{
	    			plotMap.get(br.uid).setBackgroundPaint(Color.GRAY);
	    			plotStateMap.put(br.uid,0);
	    		}    			
    		}
    	}    	    	
    }

    
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("ACTION PERFORMED!!!!");
		// TODO Auto-generated method stub		
	}       


	/**
	 * 
	 * @param domainVal
	 * @param rangeVal
	 * @param xydataset
	 * @return
	 */
	public static SeriesAndItemIndex getItemIndex(double domainVal, double rangeVal, XYDataset xydataset)
	{
		Comparable comparable;
		int indexOf;
		
		for(int i=0;i<xydataset.getSeriesCount();i++)
		{
			comparable =  xydataset.getSeriesKey(i);
		    indexOf=xydataset.indexOf(comparable);
		    for(int j=0 ; j<xydataset.getItemCount(indexOf);j++)
		    {
		    	double x=xydataset.getXValue(indexOf, j);
		    	double y=xydataset.getYValue(indexOf, j);
		    	if(x == domainVal && y==rangeVal)
		    	{
		    		return  new SeriesAndItemIndex(j,indexOf);//return item index and series index
		    	}
            }
        }
        return null;
    }

	
	/**
	 * collects values from the selection and copies them into the templatePlot object
	 * @param x1
	 * @param x2
	 * @param dataSet
	 */
	public static void collectPlotValues(double x1, double x2, XYDataset dataSet)
	{
		Comparable comparable;
		int indexOf;
		ArrayList<MeasurementEntry> entries = new ArrayList<MeasurementEntry>();
		
		for(int i=0;i<dataSet.getSeriesCount();i++)
		{
			comparable =  dataSet.getSeriesKey(i);
		    indexOf=dataSet.indexOf(comparable);
		    for(int j=0 ; j<dataSet.getItemCount(indexOf);j++)
		    {
		    	double x=dataSet.getXValue(indexOf, j);
		    	if (x>=x1)
		    	{
		    		double y=dataSet.getYValue(indexOf, j);
		    		System.out.println("valueX["+j+"] = "+doubleToTime(x)+", y = "+y);
		    		entries.add(new MeasurementEntry(x,y));
		    	}
		    	if (x>=x2)
		    	{
		    		break;
		    	}
            }
        }
		templatePlot.replacePointList(entries);
	}
	
	
	private static class SeriesAndItemIndex
	{ 
		///inner CLASS to group series and item clicked index
		public int itemIndex;
		public int seriesIndex;
		
		public SeriesAndItemIndex(int i,int s)
		{
			itemIndex=i;
		    seriesIndex=s;
		}

		@Override
		public String toString()
		{
			return "itemIndex="+itemIndex+",seriesIndex="+seriesIndex;
		}
	}
	
	
	/**
	 * converts a double value into a string
	 * @param value
	 * @return
	 */
	public static String doubleToTime(Double v)
	{
		double value = v;
        Date m = new java.sql.Date((long) value);
        //DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(m);
	}
}

           
       