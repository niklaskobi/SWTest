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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import objects.MeasurementEntry;
import objects.Slider;
import objects.Brick;
import objects.Measurement;
import objects.TemplatePlot;

import org.eclipse.swt.widgets.Display;
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
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.NotConnectedException;

import data.connectionData;
import data.constants;
import functions.connection;

@SuppressWarnings("serial")
public class sensorWindow extends ApplicationFrame implements ActionListener {

	final static double warningPercentage = 100; 	//
	final static int sliderValuesNumber = 120; 		// how many x values are visible
													// at the scroll bar
	final static int chartRangeSec = 120;

	final static int maxValues = 1000; 				// max number of values which will be
													// stored in object - measurement
	final static int maxCycles = 10; 				// max number of cycles which will ne taken
													// into account computing extrema and
													// avergae values

	public static boolean sliderUpdate = false;		// true means we have moved the
													// slider at least once, but now
													// it is on its start position
													// which means we want to
													// auto-update the charts

	final static String buttonComAddBtn = "ADD_DATA_";

	Slider sliderData = new Slider(sliderValuesNumber);
	static JSlider slider;

	static boolean chartAutoUpdate = false;
	
	static JPanel content;
	static JPanel buttonPanel;

	public static TemplatePlot templatePlot = new TemplatePlot();

	public static Map<String, TimeSeriesCollection> seriesCollectionMap = new HashMap<String, TimeSeriesCollection>();
	public static Map<String, TimeSeriesCollection> seriesCollectionMap2 = new HashMap<String, TimeSeriesCollection>();

	public static Map<String, TimeSeries> seriesMap = new HashMap<String, TimeSeries>();
	public static Map<String, TimeSeries> seriesMap2 = new HashMap<String, TimeSeries>();
	public static Map<String, TimeSeries> seriesMap3 = new HashMap<String, TimeSeries>();
	public static Map<String, TimeSeries> seriesMap4 = new HashMap<String, TimeSeries>();
	public static Map<String, TimeSeries> seriesMap5 = new HashMap<String, TimeSeries>();
	public static Map<String, TimeSeries> seriesMap6 = new HashMap<String, TimeSeries>();

	public static Map<String, NumberAxis> axisMap = new HashMap<String, NumberAxis>();
	public static Map<String, NumberAxis> axisMap2 = new HashMap<String, NumberAxis>();

	public static Map<String, Font> customFonts = new HashMap<String, Font>();

	public static Map<String, XYItemRenderer> rendererMap = new HashMap<String, XYItemRenderer>();
	public static Map<String, XYItemRenderer> rendererMap2 = new HashMap<String, XYItemRenderer>();
	public static Map<String, XYItemRenderer> rendererMap3 = new HashMap<String, XYItemRenderer>();
	public static Map<String, XYItemRenderer> rendererMap4 = new HashMap<String, XYItemRenderer>();

	public static Map<String, ValueMarker> markerMapMin1Critical = new HashMap<String, ValueMarker>();
	public static Map<String, ValueMarker> markerMapMin1Warning = new HashMap<String, ValueMarker>();

	public static Map<String, ValueMarker> markerMapMax1Critical = new HashMap<String, ValueMarker>();
	public static Map<String, ValueMarker> markerMapMax1Warning = new HashMap<String, ValueMarker>();

	public static Map<String, ValueMarker> markerMapMin2Critical = new HashMap<String, ValueMarker>();
	public static Map<String, ValueMarker> markerMapMin2Warning = new HashMap<String, ValueMarker>();

	public static Map<String, ValueMarker> markerMapMax2Critical = new HashMap<String, ValueMarker>();
	public static Map<String, ValueMarker> markerMapMax2Warning = new HashMap<String, ValueMarker>();

	public static Map<String, ValueMarker> markerMaxima = new HashMap<String, ValueMarker>();
	public static Map<String, ValueMarker> markerMinima = new HashMap<String, ValueMarker>();
	public static Map<String, ValueMarker> markerAverage = new HashMap<String, ValueMarker>();

	public static Map<String, ValueMarker> marker2Maxima = new HashMap<String, ValueMarker>();
	public static Map<String, ValueMarker> marker2Minima = new HashMap<String, ValueMarker>();
	public static Map<String, ValueMarker> marker2Average = new HashMap<String, ValueMarker>();

	public static Map<String, XYPlot> plotMap = new HashMap<String, XYPlot>();

	public static Map<String, Measurement> valuesMap = new HashMap<String, Measurement>();
	public static Map<String, Measurement> values2Map = new HashMap<String, Measurement>();

	// flag for enabling/disabling average control elements, 1st sensor
	public static Map<String, Boolean> avrgCtrl1Enabled = new HashMap<String, Boolean>();
	// flag for enabling/disabling average control elements, 2nd sensor	
	public static Map<String, Boolean> avrgCtrl2Enabled = new HashMap<String, Boolean>();

	public static Map<String, ValueMarker> avrg1High = new HashMap<String, ValueMarker>();
	public static Map<String, ValueMarker> avrg1Low = new HashMap<String, ValueMarker>();
	public static Map<String, ValueMarker> avrg2High = new HashMap<String, ValueMarker>();
	public static Map<String, ValueMarker> avrg2Low = new HashMap<String, ValueMarker>();

	public static Map<String, TimeSeriesCollection> tmplCollection1_1 = new HashMap<String, TimeSeriesCollection>();
	public static Map<String, TimeSeriesCollection> tmplCollection1_2 = new HashMap<String, TimeSeriesCollection>();
	public static Map<String, TimeSeriesCollection> tmplCollection2_1 = new HashMap<String, TimeSeriesCollection>();
	public static Map<String, TimeSeriesCollection> tmplCollection2_2 = new HashMap<String, TimeSeriesCollection>();

	public static Map<String, Long> 	tmplStartMs 		= new HashMap<String, Long>();
	public static Map<String, Integer> 	tmplindex 			= new HashMap<String, Integer>();
	public static Map<String, Integer> 	tmplLapCnt 			= new HashMap<String, Integer>();
	public static Map<String, Integer> 	tmplLapCnt2 		= new HashMap<String, Integer>();

	public static Map<String, JButton> tmplButtons 			= new HashMap<String, JButton>();
	public static Map<String, JButton> tmplButtonsVisible 	= new HashMap<String, JButton>();

	/**
	 * map with int states of the plot 
	 * 0 = OK  
	 * 1 = warning level trespassed 
	 * 2 = critical value trespassed
	 */
	public static Map<String, Integer> plot1StateMap = new HashMap<String, Integer>();
	public static Map<String, Integer> plot2StateMap = new HashMap<String, Integer>();
	
	
	/**
	 * arrayList[0] : simple threshold control state
	 * arrayList[1] : average threshold control state
	 * arrayList[2] : template control state
	 * 
	 * values = states of the plot 
	 * 0 = OK  
	 * 1 = warning level trespassed 
	 * 2 = critical value trespassed
	 */
	static final int plotControlMapNum = 3;			// 3 states -> array of 3 integers, increase this number for more control cases
	public static Map<String, Integer[]> plot1ControlMap = new HashMap<String, Integer[]>();
	public static Map<String, Integer[]> plot2ControlMap = new HashMap<String, Integer[]>();


	XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer();

	static XYDifferenceRenderer renderer2 = new XYDifferenceRenderer(
			Color.green, Color.red, true);

	static CombinedDomainXYPlot plot;

	/**
	 * creates all relevant data and adds it into the corresponding maps
	 * 
	 * @param UID
	 *            UID of the plotting sensor
	 */
	@SuppressWarnings("deprecation")
	public static void addPlot(Brick newBrick) {
		// create series
		TimeSeries newSeries = new TimeSeries("" + 0, Millisecond.class);
		TimeSeries newSeries2 = new TimeSeries("" + 0, Millisecond.class);
		TimeSeries newSeries3 = new TimeSeries("" + 0, Millisecond.class);
		TimeSeries newSeries4 = new TimeSeries("" + 0, Millisecond.class);
		TimeSeries newSeries5 = new TimeSeries("" + 0, Millisecond.class);
		TimeSeries newSeries6 = new TimeSeries("" + 0, Millisecond.class);

		Measurement m1 = new Measurement(maxValues, maxCycles, newBrick.uid, 0);
		valuesMap.put(newBrick.uid, m1);
		if (newBrick.checked3 == true) {
			Measurement m2 = new Measurement(maxValues, maxCycles,
					newBrick.uid, 1);
			values2Map.put(newBrick.uid, m2);
		}

		// create entry in state map
		plot1StateMap.put(newBrick.uid, 0);
		plot2StateMap.put(newBrick.uid, 0);

		// create index map entry
		tmplindex.put(newBrick.uid, 0);

		// create avrgCtrlEnabled maps
		if (newBrick.controlAverage == true)
			avrgCtrl1Enabled.put(newBrick.uid, true);
		else
			avrgCtrl1Enabled.put(newBrick.uid, false);
		if (newBrick.controlAverage2 == true)
			avrgCtrl2Enabled.put(newBrick.uid, true);
		else
			avrgCtrl2Enabled.put(newBrick.uid, false);

		// create series map entry
		seriesMap.put(newBrick.uid, newSeries);
		seriesMap2.put(newBrick.uid, newSeries2);
		seriesMap3.put(newBrick.uid, newSeries3);
		seriesMap4.put(newBrick.uid, newSeries4);
		seriesMap5.put(newBrick.uid, newSeries3);
		seriesMap6.put(newBrick.uid, newSeries4);

		// create collection map entry
		seriesCollectionMap.put(newBrick.uid, new TimeSeriesCollection(newSeries));
		seriesCollectionMap2.put(newBrick.uid, new TimeSeriesCollection(newSeries2));
		tmplCollection1_1.put(newBrick.uid, new TimeSeriesCollection(newSeries3));
		tmplCollection1_2.put(newBrick.uid, new TimeSeriesCollection(newSeries4));
		tmplCollection2_1.put(newBrick.uid, new TimeSeriesCollection(newSeries5));
		tmplCollection2_2.put(newBrick.uid, new TimeSeriesCollection(newSeries6));
		
		// create plot1ControlMap
		Integer tmpArray1[] = new Integer[plotControlMapNum];
		Integer tmpArray2[] = new Integer[plotControlMapNum];
		for (int i=0; i<plotControlMapNum; i++)
		{
			tmpArray1[i] = new Integer(0);
			tmpArray2[i] = new Integer(0);
		}
		plot1ControlMap.put(newBrick.uid, tmpArray1);
		plot2ControlMap.put(newBrick.uid, tmpArray2);
		
		// create plot map entry, special case for current/voltage brick, since
		// it has 2 parallel measurements and therefore 2 graphs must be treated
		XYPlot tmpSubPlot;
		tmpSubPlot = new XYPlot(seriesCollectionMap.get(newBrick.uid), null,
				null, new StandardXYItemRenderer());

		// create the 1st graph
		if (newBrick.checked2 == true) {
			// create plot map entry
			NumberAxis rangeAxis = new NumberAxis(
					String.valueOf(constants.brickUnitMap
							.get(newBrick.deviceIdentifier)));
			rangeAxis.setAutoRangeIncludesZero(true);
			tmpSubPlot.setRangeAxis(0, rangeAxis);
			rangeAxis.setLabelPaint(Color.BLUE);
			rangeAxis.setVisible(newBrick.checked2);
			tmpSubPlot.setDataset(0, seriesCollectionMap.get(newBrick.uid));

			// set dot - shape
			// Shape cross = ShapeUtilities.createDiagonalCross(3, 1);

			// create and store renderer
			XYItemRenderer renderer1 = new XYLineAndShapeRenderer();
			renderer1 = tmpSubPlot.getRenderer();
			renderer1.setSeriesPaint(0, Color.BLUE);
			renderer1.setSeriesStroke(0, new BasicStroke(3));
			// line = dashes:
			// float dash[] = {5.0f};
			// renderer1.setSeriesStroke( 0, new
			// BasicStroke(3,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
			// 10.0f, dash, 0.0f));
			// renderer1.setSeriesShape(0, cross);
			tmpSubPlot.setRenderer(0, renderer1);

			// set colors
			tmpSubPlot.setBackgroundPaint(Color.white);
			tmpSubPlot.setDomainGridlinePaint(Color.lightGray);
			tmpSubPlot.setRangeGridlinePaint(Color.lightGray);
			// tmpSubPlot.setRenderer(renderer2);

			// set font
			rangeAxis.setLabelFont(customFonts.get("axisLabelFont"));
			rangeAxis.setTickLabelFont(customFonts.get("axisValueFont"));

			// create template graph
			// if (newBrick.ctrlTmpl[0] == true)
			// {
			tmpSubPlot.setDataset(2, tmplCollection1_1.get(newBrick.uid));

			XYItemRenderer renderer3 = new XYLineAndShapeRenderer();
			int width = computeTmplPlotWidth(newBrick.tmpl1Width);
			BasicStroke stroke = new BasicStroke(width, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_ROUND);// , 10.0f, dash, 0.0f);
			renderer3.setSeriesPaint(0, Color.GREEN);
			// renderer3.setSeriesStroke( 0, new BasicStroke( 1 ) );
			renderer3.setSeriesStroke(0, stroke);
			renderer3.setSeriesVisible(0, newBrick.ctrlTmpl[0]);
			rendererMap3.put(newBrick.uid, renderer3);
			tmpSubPlot.setRenderer(2, rendererMap3.get(newBrick.uid));
			// }

			// put everything to the maps
			rendererMap.put(newBrick.uid, renderer1);
			plotMap.put(newBrick.uid, tmpSubPlot);
			axisMap.put(newBrick.uid, rangeAxis);
		}

		// create the 2nd graph
		if (newBrick.checked3 == true) {
			// set second axis for voltage/ampere brick
			NumberAxis secondaryAxis = new NumberAxis(
					String.valueOf(constants.brick2ndUnitMap
							.get(newBrick.deviceIdentifier)));
			secondaryAxis.setAutoRangeIncludesZero(true);
			tmpSubPlot.setRangeAxis(1, secondaryAxis);
			secondaryAxis.setLabelPaint(Color.RED);
			secondaryAxis.setVisible(newBrick.checked3);
			tmpSubPlot.setDataset(1, seriesCollectionMap2.get(newBrick.uid));
			tmpSubPlot.mapDatasetToRangeAxis(1, 1);

			// set font
			secondaryAxis.setLabelFont(customFonts.get("axisLabelFont"));
			secondaryAxis.setTickLabelFont(customFonts.get("axisValueFont"));

			// create and store renderer
			XYItemRenderer renderer2 = new StandardXYItemRenderer();
			// renderer2 = tmpSubPlot.getRenderer();
			renderer2.setSeriesPaint(1, Color.RED);
			renderer2.setSeriesStroke(0, new BasicStroke(3));
			tmpSubPlot.setRenderer(1, renderer2);

			// set colors
			tmpSubPlot.setBackgroundPaint(Color.white);
			tmpSubPlot.setDomainGridlinePaint(Color.lightGray);
			tmpSubPlot.setRangeGridlinePaint(Color.lightGray);

			// ----------------------------------------------------------------------------------
			// create min1 critical map value
			ValueMarker vm5 = new ValueMarker(newBrick.tresholdMin2);
			markerMapMin2Critical.put(newBrick.uid, vm5);
			// set critical line
			markerMapMin2Critical.get(newBrick.uid).setPaint(Color.red);
			markerMapMin2Critical.get(newBrick.uid).setLabel(
					String.valueOf(constants.brick2ndUnitMap
							.get(newBrick.deviceIdentifier)) + " critical min");
			markerMapMin2Critical.get(newBrick.uid).setLabelAnchor(
					RectangleAnchor.TOP_RIGHT);
			markerMapMin2Critical.get(newBrick.uid).setLabelTextAnchor(
					TextAnchor.TOP_RIGHT);
			tmpSubPlot.addRangeMarker(1,
					markerMapMin2Critical.get(newBrick.uid), Layer.BACKGROUND);

			// create min1 warning map value
			ValueMarker vm6 = new ValueMarker(newBrick.tresholdMin2
					+ newBrick.tresholdMin2 * warningPercentage / 100);
			markerMapMin2Warning.put(newBrick.uid, vm6);
			// set warning line
			markerMapMin2Warning.get(newBrick.uid).setPaint(Color.orange);
			markerMapMin2Warning.get(newBrick.uid).setLabel(
					String.valueOf(constants.brick2ndUnitMap
							.get(newBrick.deviceIdentifier)) + " warning min");
			markerMapMin2Warning.get(newBrick.uid).setLabelAnchor(
					RectangleAnchor.TOP_RIGHT);
			markerMapMin2Warning.get(newBrick.uid).setLabelTextAnchor(
					TextAnchor.TOP_RIGHT);
			// tmpSubPlot.addRangeMarker(markerMapMin2Warning.get(newBrick.uid));
			tmpSubPlot.addRangeMarker(1,
					markerMapMin2Warning.get(newBrick.uid), Layer.BACKGROUND);

			// create max1 critical map value
			ValueMarker vm7 = new ValueMarker(newBrick.tresholdMax2);
			markerMapMax2Critical.put(newBrick.uid, vm7);
			// set critical line
			markerMapMax2Critical.get(newBrick.uid).setPaint(Color.red);
			markerMapMax2Critical.get(newBrick.uid).setLabel(
					String.valueOf(constants.brick2ndUnitMap
							.get(newBrick.deviceIdentifier)) + " critical max");
			markerMapMax2Critical.get(newBrick.uid).setLabelAnchor(
					RectangleAnchor.TOP_RIGHT);
			markerMapMax2Critical.get(newBrick.uid).setLabelTextAnchor(
					TextAnchor.TOP_RIGHT);
			tmpSubPlot.addRangeMarker(1,
					markerMapMax2Critical.get(newBrick.uid), Layer.BACKGROUND);

			// create max1 warning map value
			ValueMarker vm8 = new ValueMarker(newBrick.tresholdMax2
					+ newBrick.tresholdMax2 * warningPercentage / 100);
			markerMapMax2Warning.put(newBrick.uid, vm8);
			// set warning line
			markerMapMax2Warning.get(newBrick.uid).setPaint(Color.orange);
			markerMapMax2Warning.get(newBrick.uid).setLabel(
					String.valueOf(constants.brick2ndUnitMap
							.get(newBrick.deviceIdentifier)) + " warning max");
			markerMapMax2Warning.get(newBrick.uid).setLabelAnchor(
					RectangleAnchor.TOP_RIGHT);
			markerMapMax2Warning.get(newBrick.uid).setLabelTextAnchor(
					TextAnchor.TOP_RIGHT);
			tmpSubPlot.addRangeMarker(1,
					markerMapMax2Warning.get(newBrick.uid), Layer.BACKGROUND);

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
			if (newBrick.controlAverage2) 
			{
				tmpSubPlot.addRangeMarker(1, vmMax, Layer.BACKGROUND);
				tmpSubPlot.addRangeMarker(1, vmMin, Layer.BACKGROUND);
				tmpSubPlot.addRangeMarker(1, vmAvg, Layer.BACKGROUND);
			}

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
			// ----------------------------------------------------------------------------------

			// put everything to the map
			rendererMap2.put(newBrick.uid, renderer2);
			plotMap.put(newBrick.uid, tmpSubPlot);
			axisMap2.put(newBrick.uid, secondaryAxis);
		}

		// 1st graph
		// markers--------------------------------------------------------------------------------------------------
		// create min1 critical map value
		ValueMarker vm1 = new ValueMarker(newBrick.tresholdMin1);
		markerMapMin1Critical.put(newBrick.uid, vm1);
		// set critical line
		markerMapMin1Critical.get(newBrick.uid).setPaint(Color.red);
		// / .setLabel("critical");
		// markerMapMin1Critical.get(newBrick.uid).setLabelAnchor(RectangleAnchor.BOTTOM);
		markerMapMin1Critical.get(newBrick.uid).setLabel(
				String.valueOf(constants.brickUnitMap
						.get(newBrick.deviceIdentifier)) + " critical min");
		markerMapMin1Critical.get(newBrick.uid).setLabelAnchor(
				RectangleAnchor.TOP_LEFT);
		markerMapMin1Critical.get(newBrick.uid).setLabelTextAnchor(
				TextAnchor.TOP_LEFT);
		plotMap.get(newBrick.uid).addRangeMarker(
				markerMapMin1Critical.get(newBrick.uid));

		// create min1 warning map value
		ValueMarker vm2 = new ValueMarker(newBrick.tresholdMin1
				+ newBrick.tresholdMin1 * warningPercentage / 100);
		markerMapMin1Warning.put(newBrick.uid, vm2);
		// set warning line
		markerMapMin1Warning.get(newBrick.uid).setPaint(Color.orange);
		// marker2Map.get(newBrick.uid).setPaint(Color.);
		// / marker2Map.get(newBrick.uid).setLabel("warning");
		markerMapMin1Warning.get(newBrick.uid).setLabel(
				String.valueOf(constants.brickUnitMap
						.get(newBrick.deviceIdentifier)) + " warning min");
		markerMapMin1Warning.get(newBrick.uid).setLabelAnchor(
				RectangleAnchor.TOP_LEFT);
		markerMapMin1Warning.get(newBrick.uid).setLabelTextAnchor(
				TextAnchor.TOP_LEFT);
		plotMap.get(newBrick.uid).addRangeMarker(
				markerMapMin1Warning.get(newBrick.uid));

		// create max1 critical map value
		ValueMarker vm3 = new ValueMarker(newBrick.tresholdMax1);
		markerMapMax1Critical.put(newBrick.uid, vm3);
		// set critical line
		markerMapMax1Critical.get(newBrick.uid).setPaint(Color.red);
		// / .setLabel("critical");
		// markerMapMax1Critical.get(newBrick.uid).setLabelAnchor(RectangleAnchor.BOTTOM);
		markerMapMax1Critical.get(newBrick.uid).setLabel(
				String.valueOf(constants.brickUnitMap
						.get(newBrick.deviceIdentifier)) + " critical max");
		markerMapMax1Critical.get(newBrick.uid).setLabelAnchor(
				RectangleAnchor.TOP_LEFT);
		markerMapMax1Critical.get(newBrick.uid).setLabelTextAnchor(
				TextAnchor.TOP_LEFT);
		plotMap.get(newBrick.uid).addRangeMarker(
				markerMapMax1Critical.get(newBrick.uid));

		// create max1 warning map value
		ValueMarker vm4 = new ValueMarker(newBrick.tresholdMax1
				+ newBrick.tresholdMax1 * warningPercentage / 100);
		markerMapMax1Warning.put(newBrick.uid, vm4);
		// set warning line
		markerMapMax1Warning.get(newBrick.uid).setPaint(Color.orange);
		markerMapMax1Warning.get(newBrick.uid).setLabel(
				String.valueOf(constants.brickUnitMap
						.get(newBrick.deviceIdentifier)) + " warning max");
		markerMapMax1Warning.get(newBrick.uid).setLabelAnchor(
				RectangleAnchor.TOP_LEFT);
		markerMapMax1Warning.get(newBrick.uid).setLabelTextAnchor(
				TextAnchor.TOP_LEFT);
		plotMap.get(newBrick.uid).addRangeMarker(
				markerMapMax1Warning.get(newBrick.uid));

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
		if (newBrick.controlAverage) 
		{
			plotMap.get(newBrick.uid).addRangeMarker(vmMax);
			plotMap.get(newBrick.uid).addRangeMarker(vmMin);
			plotMap.get(newBrick.uid).addRangeMarker(vmAvg);
		}

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
		if (newBrick.controlAverage) {
			plotMap.get(newBrick.uid).addRangeMarker(avrgCtrl1high);
			plotMap.get(newBrick.uid).addRangeMarker(avrgCtrl1low);
		}
		// -----------------------------------------------------------------------------------------------------

		// set title
		NumberAxis axisForTitleOnly = new NumberAxis(
				data.constants.brickIdMap.get(newBrick.deviceIdentifier) + " ("
						+ newBrick.uid + ")");
		axisForTitleOnly.setLabelFont(customFonts.get("titleFont"));
		axisForTitleOnly.setTickLabelsVisible(false);
		axisForTitleOnly.setTickMarksVisible(false);
		axisForTitleOnly.setMinorTickMarksVisible(false);
		axisForTitleOnly.setAxisLineVisible(false);
		plotMap.get(newBrick.uid).setDomainAxis(1, axisForTitleOnly);

		// add subplot to the main plot
		plot.add(plotMap.get(newBrick.uid));
	}

	/**
	 * updates the threshold marker on the graph of the given sensor
	 * 
	 * @param UID
	 *            UID of the sensor
	 * @param treshold
	 *            new threshold
	 */
	public static void updateTresholdMin1(String UID, double treshold) {
		if (markerMapMin1Critical.containsKey(UID)) {
			ValueMarker vm = markerMapMin1Critical.get(UID);
			vm.setValue(treshold);
			markerMapMin1Critical.put(UID, vm);

			ValueMarker vm2 = markerMapMin1Warning.get(UID);
			// vm2.setValue(treshold+treshold*warningPercentage/100);
			vm2.setValue(treshold + treshold);
			markerMapMin1Warning.put(UID, vm2);

			ValueMarker vm3 = markerMapMax1Warning.get(UID);
			double diff = markerMapMin1Warning.get(UID).getValue()
					- markerMapMin1Critical.get(UID).getValue();
			vm3.setValue(markerMapMax1Critical.get(UID).getValue() - diff);
			markerMapMax1Warning.put(UID, vm3);
		}
	}

	/**
	 * updates the width of the template plot
	 * 
	 * @param UID
	 */
	public static void updateTmplPlotWidth(String UID, int index) {
		Brick tmpBrick = Brick.getBrick(connectionData.BrickList, UID);
		if (index == 0) {
			if (rendererMap3.containsKey(UID)) {
				XYItemRenderer rendererTmp = rendererMap3.get(UID);
				int width = computeTmplPlotWidth(tmpBrick.tmpl1Width);
				BasicStroke stroke = new BasicStroke(width,
						BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);// ,
																		// 10.0f,
																		// dash,
																		// 0.0f);
				rendererTmp.setSeriesPaint(0, Color.GREEN);
				// renderer3.setSeriesStroke( 0, new BasicStroke( 1 ) );
				rendererTmp.setSeriesStroke(0, stroke);
				rendererMap3.put(UID, rendererTmp);
				// tmplPlot1Width.put(UID,
				// computeTmplPlotWidth(tmpBrick.tmpl1Width));
			}
		}
		if (index == 1) {
			if (rendererMap4.containsKey(UID)) {
				// TODO
			}
		}
	}

	/**
	 * update average control values
	 * 
	 * @param UID
	 *            uid of the brick
	 * @param value
	 *            new value
	 * @param index
	 *            1=sensor1, 2=sensor2
	 * @param high
	 *            true = upper line, false = lower line
	 */
	public static void updateAvrgCtrl(String UID, double value, int index,
			boolean high) {
		switch (index) {
		case 1:
			if (high == true) {
				ValueMarker vm = avrg1High.get(UID);
				vm.setValue(value);
				avrg1High.put(UID, vm);
			}
			if (high == false) {
				ValueMarker vm = avrg1Low.get(UID);
				vm.setValue(value);
				avrg1Low.put(UID, vm);
			}
			break;

		case 2:
			if (high == true) {
				ValueMarker vm = avrg2High.get(UID);
				vm.setValue(value);
				avrg2High.put(UID, vm);
			}
			if (high == false) {
				ValueMarker vm = avrg2Low.get(UID);
				vm.setValue(value);
				avrg2Low.put(UID, vm);
			}
			break;
		}
	}

	/**
	 * updates the threshold marker on the graph of the given sensor
	 * 
	 * @param UID
	 *            UID of the sensor
	 * @param treshold
	 *            new threshold
	 */
	public static void updateTresholdMin2(String UID, double treshold) {
		if (markerMapMin2Critical.containsKey(UID)) {
			ValueMarker vm = markerMapMin2Critical.get(UID);
			vm.setValue(treshold);
			markerMapMin2Critical.put(UID, vm);

			ValueMarker vm2 = markerMapMin2Warning.get(UID);
			vm2.setValue(treshold + treshold);
			markerMapMin2Warning.put(UID, vm2);

			ValueMarker vm3 = markerMapMax2Warning.get(UID);
			double diff = markerMapMin2Warning.get(UID).getValue()
					- markerMapMin2Critical.get(UID).getValue();
			vm3.setValue(markerMapMax2Critical.get(UID).getValue() - diff);
			markerMapMax2Warning.put(UID, vm3);
		}
	}

	/**
	 * updates the threshold marker on the graph of the given sensor
	 * 
	 * @param UID
	 *            UID of the sensor
	 * @param treshold
	 *            new threshold
	 */

	public static void updateTresholdMax1(String UID, double treshold) {
		if (markerMapMax1Critical.containsKey(UID)) {
			ValueMarker vm = markerMapMax1Critical.get(UID);
			vm.setValue(treshold);
			markerMapMax1Critical.put(UID, vm);

			ValueMarker vm2 = markerMapMax1Warning.get(UID);
			double diff = markerMapMin1Warning.get(UID).getValue()
					- markerMapMin1Critical.get(UID).getValue();
			vm2.setValue(treshold - diff);
			markerMapMax1Warning.put(UID, vm2);
		}
	}

	/**
	 * updates the threshold marker on the graph of the given sensor
	 * 
	 * @param UID
	 *            UID of the sensor
	 * @param treshold
	 *            new threshold
	 */
	public static void updateTresholdMax2(String UID, double treshold) {
		if (markerMapMax2Critical.containsKey(UID)) {
			ValueMarker vm = markerMapMax2Critical.get(UID);
			vm.setValue(treshold);
			markerMapMax2Critical.put(UID, vm);

			ValueMarker vm2 = markerMapMax2Warning.get(UID);
			double diff = markerMapMin2Warning.get(UID).getValue()
					- markerMapMin2Critical.get(UID).getValue();
			vm2.setValue(treshold - diff);
			markerMapMax2Warning.put(UID, vm2);
		}
	}

	/**
	 * update maxima marker
	 * 
	 * @param UID
	 *            uid of the sensor
	 * @param value
	 *            new maximum vallue
	 * @param index
	 *            sensor index, 0 = primary sensor, 1 = secondary
	 */
	public static void updateMaxima(String UID, double value, int index) {
		if (index == 0) {
			if (markerMaxima.containsKey(UID)) {
				ValueMarker vm = markerMaxima.get(UID);
				vm.setValue(value);
				markerMaxima.put(UID, vm);
			}
		}
		if (index == 1) {
			if (marker2Maxima.containsKey(UID)) {
				ValueMarker vm = marker2Maxima.get(UID);
				vm.setValue(value);
				marker2Maxima.put(UID, vm);
			}
		}
	}

	/**
	 * update minima marker
	 * 
	 * @param UID
	 *            uid of the sensor
	 * @param value
	 *            new maximum vallue
	 * @param index
	 *            sensor index, 0 = primary sensor, 1 = secondary
	 */
	public static void updateMinima(String UID, double value, int index) {
		if (index == 0) {
			if (markerMinima.containsKey(UID)) {
				ValueMarker vm = markerMinima.get(UID);
				vm.setValue(value);
				markerMinima.put(UID, vm);
			}
		}
		if (index == 1) {
			if (marker2Minima.containsKey(UID)) {
				ValueMarker vm = marker2Minima.get(UID);
				vm.setValue(value);
				marker2Minima.put(UID, vm);
			}
		}
	}

	/**
	 * update maxima marker
	 * @param UID 	uid of the sensor
	 * @param value new maximum vallue
	 * @param index sensor index, 0 = primary sensor, 1 = secondary
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

				if (avrgCtrl1Enabled.get(UID) == true) 
				{
					// verify whether we are between admissible average control lines
					if ((value > Brick.getBrick(connectionData.BrickList, UID).getAvg1high())
						|| (value < Brick.getBrick(connectionData.BrickList, UID).getAvg1low())) 
					{
						// set alarm state
						Integer[] tmpArray = plot1ControlMap.get(UID);
						tmpArray[1] = 2;
						plot1ControlMap.put(UID, tmpArray);
					} 
					else
					{
						// set idle state
						Integer[] tmpArray = plot1ControlMap.get(UID);
						tmpArray[1] = 0;
						plot1ControlMap.put(UID, tmpArray);
					}
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

				if (avrgCtrl1Enabled.get(UID) == true) 
				{
					// verify whether we are between admissible average control lines
					if ((value > Brick.getBrick(connectionData.BrickList, UID).getAvg2high())
						|| (value < Brick.getBrick(connectionData.BrickList, UID).getAvg2low())) 
					{
						// set alarm state
						Integer[] tmpArray = plot2ControlMap.get(UID);
						tmpArray[1] = 2;
						plot2ControlMap.put(UID, tmpArray);
					} 
					else
					{
						// set idle state
						Integer[] tmpArray = plot2ControlMap.get(UID);
						tmpArray[1] = 0;
						plot2ControlMap.put(UID, tmpArray);
					}
				}
			}
		}		
	}

	/**
	 * remove subplot from the main plot, remove all data of the subblot
	 * 
	 * @param UID
	 *            UID of the brick, which is presented by the plot
	 */
	public static void removePlot(String UID) {
		// remove markers
		// plot.clearRangeMarkers();
		// plotMap.get(UID).clearRangeMarkers();

		// remove from the main plot
		plot.remove(plotMap.get(UID));

		// remove from series map entry
		seriesMap.remove(UID);

		// remove from collection map entry
		seriesCollectionMap.remove(UID);

		// remove from plot map entry
		plotMap.remove(UID);

		// remove from marker maps
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
	 * 
	 * @param title
	 *            title for the new window
	 */
	public sensorWindow(final String title) {

		super(title);
		
		System.out.println("create sensorWindow");

		// font customizing
		// -------------------------------------------------------
		/*
		 * Font font1 = null; try { font1 = Font.createFont(Font.TRUETYPE_FONT,
		 * new File("U:/workspace/SWTtest/fonts/roboto/Roboto-Black.ttf")); }
		 * catch (FontFormatException | IOException e1) { e1.printStackTrace();
		 * } StandardChartTheme chartTheme = new
		 * StandardChartTheme("robotTheme");
		 * chartTheme.setExtraLargeFont(font1.deriveFont(24f));
		 * chartTheme.setLargeFont(font1.deriveFont(16f));
		 * chartTheme.setRegularFont(font1.deriveFont(12f));
		 * chartTheme.setSmallFont(font1.deriveFont(10f));
		 * ChartFactory.setChartTheme(chartTheme);
		 */
		Font font1 = new Font("Tahoma", Font.BOLD, 16);
		Font font2 = new Font("Tahoma", Font.PLAIN, 12);
		Font font3 = new Font("Tahoma", Font.BOLD, 16);
		customFonts.put("axisLabelFont", font1);
		customFonts.put("axisValueFont", font2);
		customFonts.put("titleFont", font3);
		// -------------------------------------------------------------------------

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		plot = new CombinedDomainXYPlot(new DateAxis("Time"));

		for (int i = 0; i < connectionData.presentedBrickList.size(); i++) {
			Brick tmpBrick = connectionData.presentedBrickList.get(i);
			addPlot(tmpBrick);
		}

		final JFreeChart chart = new JFreeChart("", plot);
		// chart.setBorderPaint(Color.black);
		// chart.setBorderVisible(true);
		// chart.setBackgroundPaint(Color.white);
		chart.removeLegend();

		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		// plot.setRenderer(renderer2);
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 4, 4, 4, 4));
		final ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(chartRangeSec * 1000); // chart range seconds
		axis.setLabelFont(customFonts.get("axisLabelFont"));
		axis.setTickLabelFont(customFonts.get("axisValueFont"));

		//final JPanel content = new JPanel(new BorderLayout());
		content = new JPanel(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane();
		JViewport viewport = scrollPane.getViewport();
		viewport.setView(content);

		final ChartPanel chartPanel = new ChartPanel(chart);
		content.add(chartPanel, BorderLayout.NORTH);
		// content.add(getScrollBar(xAxe), BorderLayout.SOUTH);

		// disable zoom
		chartPanel.setRangeZoomable(false);
		chartPanel.setDomainZoomable(false);

		// mouse selection
		chartPanel.addMouseListener(new MouseMarker(chartPanel));

		chartPanel.setPreferredSize(new java.awt.Dimension(500, 470));
		// chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		// setContentPane(content);

		// ===================================================
		// scroll bar
		final JPanel sliderPanel = new JPanel(new FlowLayout());

		slider = new JSlider(1, sliderValuesNumber);
		slider.setValue(sliderValuesNumber);
		slider.setEnabled(false);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (sliderData.sliderActive == true) {
					int sliderValue = slider.getValue();
					if (sliderValue == sliderValuesNumber)
						sliderUpdate = true;
					else
						sliderUpdate = false;
					/*
					System.out.println("slider : " + sliderValue);
					System.out.println("Millis first: "
							+ sliderData.getMilliseconds(
									sliderValue - sliderValuesNumber)
									.toString());
					System.out.println("Millis last : "
							+ sliderData.getMilliseconds(sliderValue)
									.toString());
					*/
					DateRange range = new DateRange(sliderData.getMilliseconds(
							sliderValue - sliderValuesNumber)
							.getFirstMillisecond(), sliderData.getMilliseconds(
							sliderValue).getFirstMillisecond());
					plot.getDomainAxis().setRange(range);
				}
			}
		});
		sliderPanel.add(slider);
		// chartPanel.add(slider);
		/*
		 * final Panel chartPanel2 = new Panel(); chartPanel2.add(slider);
		 * content.add(chartPanel2, BorderLayout.SOUTH);
		 */
		//content.add(sliderPanel, BorderLayout.CENTER);
		content.add(sliderPanel, BorderLayout.CENTER);
		// ===================================================
		
		
		// ===================================================
		// buttons
		buttonPanel = new JPanel(new FlowLayout());

		for (int i = 0; i < connectionData.presentedBrickList.size(); i++) {		
			for (int i2 = 0; i2 < 2; i2++) 
			{
				Brick tmpBrick = connectionData.presentedBrickList.get(i);
				if (tmpBrick.ctrlTmpl[i2]) 
				{				
					JButton button = new JButton(tmpBrick.uid + " start");				
					//button.setActionCommand(buttonComAddBtn + tmpBrick.uid + i+ "(" + i2 + ")");
					button.setActionCommand(buttonComAddBtn + tmpBrick.uid + i2);
					button.addActionListener(this);
					tmplButtons.put(tmpBrick.uid, button);
					/*
					if (tmpBrick.ctrlTmpl[i2]) 
					{
						enableTmplCtrl(tmpBrick, i2);
					}
					*/
					enableTmplCtrl(tmpBrick, i2);
				}
			}
		}
		content.add(buttonPanel, BorderLayout.SOUTH);
		// ===================================================		
		

		// ===================================================
		// scrolling
		/*
		 * String[] data = {"one", "two", "three", "four", "five", "six",
		 * "seven", "eight", "nine", "ten"}; JList list = new JList(data);
		 * 
		 * // give the list some scrollbars. // the horizontal (bottom)
		 * scrollbar will only appear // when the screen is too wide. The
		 * vertical // scrollbar is always present, but disabled if the // list
		 * is small. JScrollPane jsp = new JScrollPane(list,
		 * JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		 * JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 * 
		 * // add the JScrollPane (not the list) to the window.
		 * getContentPane().add(jsp, BorderLayout.CENTER);
		 */
		// ==================================================

		setContentPane(content);

		
		 this.addComponentListener(new java.awt.event.ComponentAdapter() 
		 {
			 public void componentResized(java.awt.event.ComponentEvent e) 
			 {
				 chartPanel.setMaximumDrawWidth((int)e.getComponent().getSize().getWidth());
				 chartPanel.setMaximumDrawHeight((int)e.getComponent().getSize().getHeight());
				 //chartPanel.setPreferredSize(e.getComponent().getPreferredSize());
				 //chartPanel.setSize(e.getComponent().getSize());
				 //chartPanel.setLocation(0,0); 
			 } 
		 });
		 

		// start auto update plot
		autoUpdatePlot();

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("close sensor window");
				functions.windowController.closeSensorWindow();
			}

		});
	}

	/**
	 * mouse select
	 * 
	 * @author kv1
	 *
	 */
	private final static class MouseMarker extends MouseAdapter {
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
			// this.mouse_plot = plot.findSubplot(plot, source)
			// this.mouse_plot =
			// plotMap.get(connectionData.presentedBrickList.get(0).uid);
			/*
			 * if (tmp_plot != null ) {
			 * System.out.println("plot clicked: "+tmp_plot.toString());
			 * this.mouse_plot = tmp_plot; } else System.out.println("no plot");
			 */
			// plot.findSubplot(panel.getChartRenderingInfo().getPlotInfo(),
			// panel.getMousePosition());
			this.mouse_plot = null;
		}

		/*
		 * private void updateMarker(){ if (marker != null){ if (mouse_plot !=
		 * null) mouse_plot.removeDomainMarker(marker,Layer.BACKGROUND); } if
		 * (!( markerStart.isNaN() && markerEnd.isNaN())){ if ( markerEnd >
		 * markerStart){ marker = new IntervalMarker(markerStart, markerEnd);
		 * marker.setPaint(new Color(0xDD, 0xFF, 0xDD, 0x80));
		 * marker.setAlpha(0.5f); if (mouse_plot != null)
		 * mouse_plot.addDomainMarker(marker,Layer.BACKGROUND); } } }
		 */
		private void updateMarker() {
			if (marker != null) {
				if (mouse_plot != null) {
					// mouse_plot.removeDomainMarker(marker,Layer.BACKGROUND);
					mouse_plot.removeDomainMarker(markerMap.get(mouse_plot),
							Layer.BACKGROUND);
					areaMarked.put(mouse_plot, false);
					// System.out.println("delete area");
				}
			}
			if (!(markerStart[0].isNaN() && markerEnd[0].isNaN())) {
				if (markerEnd[0] > markerStart[0]) {
					marker = new IntervalMarker(markerStart[0], markerEnd[0]);
					markerMap.put(mouse_plot, marker);
					marker.setPaint(new Color(0xDD, 0xFF, 0xDD, 0x80));
					marker.setAlpha(0.5f);
					if (mouse_plot != null) {
						mouse_plot.addDomainMarker(marker, Layer.BACKGROUND);
						areaMarked.put(mouse_plot, true);
						// System.out.println("add area");
						for (int i = 0; i < mouse_plot.getDatasetCount(); i++) {
							// collectPlotValues(markerStart[0], markerEnd[0],
							// mouse_plot.getDataset());
							if (mouse_plot == null)
								continue;
							if (mouse_plot.getDataset(i) == null)
								continue;

							Comparable comparable = mouse_plot.getDataset()
									.getSeriesKey(i);
							int indexOf = mouse_plot.getDataset().indexOf(
									comparable);
							if (mouse_plot.getDataset().getItemCount(indexOf) < 3)
								continue;

							// if ( mouse_plot.getDataset(i).get < 2) continue;
							collectPlotValues(markerStart[0], markerEnd[0],
									mouse_plot.getDataset(i));

							functions.Events.handleMouseSelection(templatePlot,
									i);
						}
					}
				}
			}
		}

		/**
		 * returns x and y value of the given mouse event
		 * 
		 * @param e
		 *            event
		 * @return double array, 1st value = X, 2nd = Y
		 */
		private Double[] getPosition(MouseEvent e) {
			Double[] r = new Double[2];
			// Point2D p = panel.translateScreenToJava2D( e.getPoint());
			Point2D p = e.getPoint();
			Rectangle2D plotArea = panel.getScreenDataArea();
			XYPlot plotLocal = (XYPlot) chart.getPlot();
			r[0] = plotLocal.getDomainAxis().java2DToValue(p.getX(), plotArea,
					plotLocal.getDomainAxisEdge());

			XYPlot plotTest = plot.findSubplot(panel.getChartRenderingInfo()
					.getPlotInfo(), panel.getMousePosition());
			
			try
			{
				r[1] = plotTest.getRangeAxis().java2DToValue(p.getY(), plotArea,
					plotLocal.getRangeAxisEdge());
			}catch(Exception exception)
			{
				System.out.println("cant read coordinate!");
				r[1] = 0.0;
			}
			
			// end 20.02
			// r[1] = plotLocal.getRangeAxis().java2DToValue(p.getY(), plotArea,
			// plotLocal.getRangeAxisEdge());
			return r;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			markerEnd = getPosition(e);
			updateMarker();
			// System.out.println("marker end pos: "+markerEnd);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			markerStart = getPosition(e);
			System.out.print("marker = "
					+ functions.Common.doubleToTime(markerStart[0]));
			System.out.println("value = " + markerStart[1]);
			
			// ignore event if click was outside the chart 
			if ((markerStart[0]!=0.0) && (markerStart[1]!=0.0))
			{
				this.mouse_plot = plot.findSubplot(panel.getChartRenderingInfo()
					.getPlotInfo(), panel.getMousePosition());

				double d = mouse_plot.getDomainCrosshairValue(); // get crosshair X
																// value
				double r = mouse_plot.getRangeCrosshairValue(); // get crosshair y
															// value
				System.out.println("" + functions.Common.doubleToTime(d) + ",r = "
					+ r);
			}
		}

		public void mouseMoved(MouseEvent e) {
			if ((!markerStart[0].isNaN()) && (markerEnd[0].isNaN())) {
				marker = new IntervalMarker(markerStart[0], markerEnd[0]);
				markerMap.put(mouse_plot, marker);
				marker.setPaint(new Color(0xDD, 0xFF, 0xDD, 0x80));
				marker.setAlpha(0.5f);
				if (mouse_plot != null) {
					mouse_plot.addDomainMarker(marker, Layer.BACKGROUND);
				}
			}
		}
	}

	/**
	 * Handles a click on the button by adding new (random) data.
	 *
	 * @param e
	 *            the action event.
	 */
	/*
	 * public void actionPerformed(final ActionEvent e) {
	 * 
	 * for (int i = 0; i < SUBPLOT_COUNT; i++) { if
	 * (e.getActionCommand().endsWith(String.valueOf(i))) { final Millisecond
	 * now = new Millisecond(); System.out.println("Now = " + now.toString());
	 * this.lastValue[i] = this.lastValue[i] * (0.90 + 0.2 * Math.random());
	 * this.datasets[i].getSeries(0).add(new Millisecond(), this.lastValue[i]);
	 * } }
	 * 
	 * if (e.getActionCommand().equals("ADD_ALL")) { final Millisecond now = new
	 * Millisecond(); System.out.println("Now = " + now.toString()); for (int i
	 * = 0; i < SUBPLOT_COUNT; i++) { this.lastValue[i] = this.lastValue[i] *
	 * (0.90 + 0.2 * Math.random()); this.datasets[i].getSeries(0).add(new
	 * Millisecond(), this.lastValue[i]); } } }
	 */

	/**
	 * add a value to the second field of a sensor (used for sensors with 2
	 * measurements)
	 * 
	 * @param sensorUID
	 * @param value
	 */
	public void add2ndValue(String sensorUID, double value) {

		if (seriesCollectionMap2.containsKey(sensorUID))
		{
			seriesCollectionMap2.get(sensorUID).getSeries(0).addOrUpdate(new Millisecond(), value);
		}

		// add measurement value
		if (values2Map.containsKey(sensorUID))
		{
			values2Map.get(sensorUID).addValue(value);
		}

		// execute simple threshold validation
		simpleTresholdCtrlUpdate(sensorUID, value, 2);		

		// color the plot's background according to it's current state,
		colorChart(sensorUID);	
	}

	
	
	public double addTmplValue(String uid, Millisecond ms, int index) 
	{
		double value1 = 0;
		// add next value to template plot
		if ((Brick.getBrick(connectionData.BrickList, uid).ctrlTmpl[index] == true)
				&& (tmplCollection1_1.containsKey(uid))
				&& (tmplStartMs.containsKey(uid))) 
		{
			Brick tmpBrick = Brick.getBrick(connectionData.BrickList, uid);

			long timeNow = System.currentTimeMillis();

			value1 = tmpBrick.tmplPlot[index].getYValue(timeNow,
					tmplStartMs.get(uid));

			// add value to the plot
			if (tmplCollection1_1 != null) 
			{
				if (tmplCollection1_1.get(uid) != null) 
				{
					if (tmplCollection1_1.get(uid).getSeries(0) != null) 
					{
						//System.out.println("ms= "+ms+", val = "+value1);
						tmplCollection1_1.get(uid).getSeries(0).addOrUpdate(ms, value1);
					}
				}
			}
		}
		return value1;
	}
	
	
	/**
	 * function for auto updating the plot,
	 */
	public void autoUpdatePlot() {
		new Thread(new Runnable() {			
			public void run() {
				System.out.println("THREAD STARTED");
				int cnt = 0;
				// TODO: stop functionality
				while (true) {
					try 
					{
						Thread.sleep(100);
					} catch (Exception e) {}
					cnt++;
					Millisecond ms = new Millisecond();
					for (int i = 0; i < connectionData.presentedBrickList.size(); i++) 
					{						
						Brick tmpBrick = connectionData.presentedBrickList.get(i);
						for (int i2=0;i2<tmpBrick.ctrlTmplruns.length;i2++)
						{
							//if (tmpBrick.ctrlTmplruns[i2] == true)
							if (tmpBrick.ctrlTmpl[i2] == true)
							{
								addTmplValue(tmpBrick.uid, ms, i2);
							}
						}
					}
				}
			}
		}).start();
	}

	/**
	 * add a value to the main field of the sensor
	 * 
	 * @param sensorUID
	 * @param value
	 */
	public void addValue(String sensorUID, double value, int index) 
	{	
		Millisecond ms = new Millisecond();
		
		if (seriesCollectionMap.containsKey(sensorUID)) 
		{
			if (seriesCollectionMap.get(sensorUID).getSeries(0) != null)
			{
				seriesCollectionMap.get(sensorUID).getSeries(0).addOrUpdate(ms, value);
			}
		}

		// add timestamp to slider
		sliderData.addMS(ms);

		// if we have moved slider at least once, but now it is on it's
		// start position which means we want let the charts auto update again
		if (sliderUpdate == true)
		{
			DateRange range = new DateRange(sliderData.getMilliseconds(0).getFirstMillisecond(), 
											sliderData.getMilliseconds(sliderValuesNumber).getFirstMillisecond());
			plot.getDomainAxis().setRange(range);
		}

		// add measurement value
		if (valuesMap.containsKey(sensorUID))
		{
			valuesMap.get(sensorUID).addValue(value);
		}

		// if template control is turned on and template control variables exist
		templateCtrlUpdate(sensorUID, value, index);

		// execute simple threshold validation
		simpleTresholdCtrlUpdate(sensorUID, value, index);

		// color the plot's background according to it's current state,
		colorChart(sensorUID);		
	}
	

	/**
	 * add value to the main map
	 * 
	 * @param uid
	 *            uid of the sensor
	 * @param value
	 *            value
	 */
	@SuppressWarnings("unchecked")
	private void addValueToMap(String uid, double value) {
		if (valuesMap.containsKey(uid)) {
			valuesMap.get(uid).addValue(value);
		}
	}

	/**
	 * activate slider
	 */
	public static void activateSlider() {
		slider.setEnabled(true);
	}

	/**
	 * hide or unhide (depending on previous state) a graph
	 * 
	 * @param br
	 */
	public static void hideUnhidePlot(Brick br, int index) {
		if ((index == 1) && (rendererMap.get(br.uid) != null)) {
			rendererMap.get(br.uid).setSeriesVisible(0, br.checked2);
			axisMap.get(br.uid).setVisible(br.checked2);
			// hide/show average control elements
			//hideUnhideAvgCtrl(br, 1);
		}
		if ((index == 2) && (rendererMap2.get(br.uid) != null)) {
			rendererMap2.get(br.uid).setSeriesVisible(0, br.checked3);
			axisMap2.get(br.uid).setVisible(br.checked3);
			// hide/show average control elements
			//hideUnhideAvgCtrl(br, 2);
		}
	}

	/**
	 * hide or show average control elements (depending on previous state)
	 * 
	 * @param br
	 *            brick object
	 * @param index
	 *            sensor index
	 */
	public static void hideUnhideAvgCtrl(Brick br, int index) 
	{
		if (index == 1) 
		{
			if (avrgCtrl1Enabled.get(br.uid) == false) 
			{
				// unhide average control elements
				avrgCtrl1Enabled.put(br.uid, true);
				// add range markers to the plot again
				plotMap.get(br.uid).addRangeMarker(avrg1High.get(br.uid));
				plotMap.get(br.uid).addRangeMarker(avrg1Low.get(br.uid));
				plotMap.get(br.uid).addRangeMarker(markerMaxima.get(br.uid));
				plotMap.get(br.uid).addRangeMarker(markerMinima.get(br.uid));
				plotMap.get(br.uid).addRangeMarker(markerAverage.get(br.uid));
			} 
			else if (avrgCtrl1Enabled.get(br.uid) == true) 
			{
				// hide average control elements				
				avrgCtrl1Enabled.put(br.uid, false);
				// remove range markers
				plotMap.get(br.uid).removeRangeMarker(avrg1High.get(br.uid));
				plotMap.get(br.uid).removeRangeMarker(avrg1Low.get(br.uid));
				plotMap.get(br.uid).removeRangeMarker(markerMaxima.get(br.uid));
				plotMap.get(br.uid).removeRangeMarker(markerMinima.get(br.uid));
				plotMap.get(br.uid).removeRangeMarker(markerAverage.get(br.uid));
				/*
				// set background color
				if (plot1StateMap.get(br.uid) == 1) 
				{
					plotMap.get(br.uid).setBackgroundPaint(Color.GRAY);
					plot1StateMap.put(br.uid, 0);
				}
				*/
				Integer[] tmpArray = plot1ControlMap.get(br.uid);
				tmpArray[1] = 0;
				plot1ControlMap.put(br.uid, tmpArray);
			}
		}

		if (index == 2) 
		{
			if (avrgCtrl2Enabled.get(br.uid) == false) 
			{
				// unhide average control elements				
				avrgCtrl2Enabled.put(br.uid, true);
				// add both markers
				plotMap.get(br.uid).addRangeMarker(1, avrg2High.get(br.uid), Layer.BACKGROUND);
				plotMap.get(br.uid).addRangeMarker(1, avrg2Low.get(br.uid), Layer.BACKGROUND);
				plotMap.get(br.uid).addRangeMarker(1, marker2Maxima.get(br.uid), Layer.BACKGROUND);
				plotMap.get(br.uid).addRangeMarker(1, marker2Minima.get(br.uid), Layer.BACKGROUND);
				plotMap.get(br.uid).addRangeMarker(1, marker2Average.get(br.uid), Layer.BACKGROUND);
			} 
			else if (avrgCtrl2Enabled.get(br.uid) == true) 
			{
				// hide average control elements				
				avrgCtrl2Enabled.put(br.uid, false);
				// remove range markers
				plotMap.get(br.uid).removeRangeMarker(1, avrg2High.get(br.uid), Layer.BACKGROUND);
				plotMap.get(br.uid).removeRangeMarker(1, avrg2Low.get(br.uid), Layer.BACKGROUND);
				plotMap.get(br.uid).removeRangeMarker(1, marker2Maxima.get(br.uid), Layer.BACKGROUND);
				plotMap.get(br.uid).removeRangeMarker(1, marker2Minima.get(br.uid), Layer.BACKGROUND);
				plotMap.get(br.uid).removeRangeMarker(1, marker2Average.get(br.uid), Layer.BACKGROUND);
				/*
				// set background color
				if (plot2StateMap.get(br.uid) == 1) 
				{
					plotMap.get(br.uid).setBackgroundPaint(Color.GRAY);
					plot2StateMap.put(br.uid, 0);
				}
				*/
				Integer[] tmpArray = plot2ControlMap.get(br.uid);
				tmpArray[1] = 0;
				plot2ControlMap.put(br.uid, tmpArray);
			}
		}
	}

	
	private void addButtonToContent(Brick br, int index )
	{
		if (!tmplButtons.containsKey(br.uid))
		{
			JButton button = new JButton(br.uid + " start");				
			//button.setActionCommand(buttonComAddBtn + tmpBrick.uid + i+ "(" + i2 + ")");
			button.setActionCommand(buttonComAddBtn + br.uid + index);
			button.addActionListener(this);
			tmplButtons.put(br.uid, button);
			//changeTmplCntrl(br, index);
		}
	}
	
	
	/*
	public void changeTmplCntrl(Brick br, int index) 
	{
		rendererMap3.get(br.uid).setSeriesVisible(0, br.ctrlTmplruns[index]);		
		
		if (br.ctrlTmpl[index]) {
			// make button visible
			addButtonToContent(br, index); 
			tmplButtonsVisible.put(br.uid, tmplButtons.get(br.uid));
			//buttonPanel.add(tmplButtons.get(br.uid));
		} else {
			// hide button
			tmplButtonsVisible.remove(br.uid, tmplButtons.get(br.uid));
			//buttonPanel.remove(tmplButtons.get(br.uid));
			stopTmplControl(br, index);
		}

		updateButtonPanel();
	}
	*/
	
	
	public void disableTmplCtrl(Brick br, int index)
	{
		//rendererMap3.get(br.uid).setSeriesVisible(0, br.ctrlTmplruns[index]);
		//br.ctrlTmplruns[index] = false;
		
		// hide button
		tmplButtonsVisible.remove(br.uid, tmplButtons.get(br.uid));
		//buttonPanel.remove(tmplButtons.get(br.uid));
		stopTmplControl(br, index);
		updateButtonPanel();
	}
	
		
	public void enableTmplCtrl(Brick br, int index)
	{
		// set template plot visible
		//rendererMap3.get(br.uid).setSeriesVisible(0, br.ctrlTmplruns[index]);		
		
		// make button visible
		addButtonToContent(br, index); 
		tmplButtonsVisible.put(br.uid, tmplButtons.get(br.uid));

		updateButtonPanel();		
	}
	
	
	/*
	public static void startStopTmplCntrl(Brick br, int index) {
		
		if (br.ctrlTmplruns[index] == true) 
		{
			// stop template control
			tmplButtons.get(br.uid).setText(br.uid + " template start");
			tmplButtonsVisible.get(br.uid).setText(br.uid + " template start");
			br.ctrlTmplruns[index] = false;
			
			// remove dataset from plot
			//plotMap.get(br.uid).getDataset(2).
			//tmplCollection1_1.get(br.uid).removeSeries(0);
			if (index == 0)
			{
				Integer[] tmpArray = plot1ControlMap.get(br.uid);
				tmpArray[2] = 0;
				plot1ControlMap.put(br.uid, tmpArray);
			}
			if (index == 1)
			{
				Integer[] tmpArray = plot2ControlMap.get(br.uid);
				tmpArray[2] = 0;
				plot2ControlMap.put(br.uid, tmpArray);
			}
		} 
		else 
		{
			// start template control
			tmplButtons.get(br.uid).setText(br.uid + " template stop");
			tmplButtonsVisible.get(br.uid).setText(br.uid + " template stop");
			br.ctrlTmplruns[index] = true;
			
			// add data set to plot
			TimeSeries newSeries3 = new TimeSeries("" + 0, Millisecond.class);	
			seriesMap3.put(br.uid, newSeries3);
			tmplCollection1_1.put(br.uid, new TimeSeriesCollection(newSeries3));
			plotMap.get(br.uid).setDataset(2, tmplCollection1_1.get(br.uid));
		}
		
		rendererMap3.get(br.uid).setSeriesVisible(0, br.ctrlTmplruns[index]);
		updateButtonPanel();
	}
	*/
	
		
	public static void startTmplControl(Brick br, int index)
	{
		// start template control
		tmplButtons.get(br.uid).setText(br.uid + " template stop");
		tmplButtonsVisible.get(br.uid).setText(br.uid + " template stop");
		br.ctrlTmplruns[index] = true;
		
		// add data set to plot
		TimeSeries newSeries3 = new TimeSeries("" + 0, Millisecond.class);	
		seriesMap3.put(br.uid, newSeries3);
		tmplCollection1_1.put(br.uid, new TimeSeriesCollection(newSeries3));
		plotMap.get(br.uid).setDataset(2, tmplCollection1_1.get(br.uid));
		
		rendererMap3.get(br.uid).setSeriesVisible(0, br.ctrlTmplruns[index]);
		updateButtonPanel();
	}
	
	
	public static void stopTmplControl(Brick br, int index)
	{
		// stop template control
		if (tmplButtons.containsKey(br.uid)) tmplButtons.get(br.uid).setText(br.uid + " template start");
		if (tmplButtonsVisible.containsKey(br.uid)) tmplButtonsVisible.get(br.uid).setText(br.uid + " template start");
		br.ctrlTmplruns[index] = false;
		
		// remove dataset from plot
		//plotMap.get(br.uid).getDataset(2).
		//tmplCollection1_1.get(br.uid).removeSeries(0);
		if (index == 0)
		{
			Integer[] tmpArray = plot1ControlMap.get(br.uid);
			tmpArray[2] = 0;
			plot1ControlMap.put(br.uid, tmpArray);
		}
		if (index == 1)
		{
			Integer[] tmpArray = plot2ControlMap.get(br.uid);
			tmpArray[2] = 0;
			plot2ControlMap.put(br.uid, tmpArray);
		}
		
		rendererMap3.get(br.uid).setSeriesVisible(0, br.ctrlTmplruns[index]);
		updateButtonPanel();

	}
	

	public static void updateButtonPanel() {
		
		buttonPanel.removeAll();

		for (Map.Entry<String, JButton> entry : tmplButtonsVisible.entrySet()) 
		{
			// String uid = entry.getKey();
			JButton button = entry.getValue();
			if (button != null)
			{
				buttonPanel.add(button);
			}
		}
		
		content.updateUI();
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < connectionData.presentedBrickList.size(); i++) 
		{
			Brick tmpBrick = connectionData.presentedBrickList.get(i);

			String command = e.getActionCommand().substring(0,e.getActionCommand().length() - 1);
			String indexStr = e.getActionCommand().substring(e.getActionCommand().length() - 1,	e.getActionCommand().length());
			
			int index = Integer.valueOf(indexStr);

			// start template control
			if ((command.equals(buttonComAddBtn + tmpBrick.uid))
					&& (!tmpBrick.ctrlTmplruns[index])) 
			{
				System.out.println("set new template start time");
				long timeNow = System.currentTimeMillis();
				tmplStartMs.put(tmpBrick.uid, timeNow);
				tmplLapCnt.put(tmpBrick.uid, 0);
				
				// start template comtrol
				//startStopTmplCntrl(tmpBrick, index);
				startTmplControl(tmpBrick, index);
			}

			// stop template control
			else if ((command.equals(buttonComAddBtn + tmpBrick.uid))
					&& (tmpBrick.ctrlTmplruns[index])) 
			{
				//startStopTmplCntrl(tmpBrick, index);
				// stop template comtrol
				stopTmplControl(tmpBrick, index);
			}
		}

	}

	
	/**
	 * 
	 * @param domainVal
	 * @param rangeVal
	 * @param xydataset
	 * @return
	 */
	public static SeriesAndItemIndex getItemIndex(double domainVal,
			double rangeVal, XYDataset xydataset) {
		Comparable comparable;
		int indexOf;

		for (int i = 0; i < xydataset.getSeriesCount(); i++) {
			comparable = xydataset.getSeriesKey(i);
			indexOf = xydataset.indexOf(comparable);
			for (int j = 0; j < xydataset.getItemCount(indexOf); j++) {
				double x = xydataset.getXValue(indexOf, j);
				double y = xydataset.getYValue(indexOf, j);
				if (x == domainVal && y == rangeVal) {
					return new SeriesAndItemIndex(j, indexOf);// return item
																// index and
																// series index
				}
			}
		}
		return null;
	}

	
	/**
	 * collects values from the selection and copies them into the templatePlot
	 * object
	 * 
	 * @param x1
	 * @param x2
	 * @param dataSet
	 */
	public static void collectPlotValues(double x1, double x2, XYDataset dataSet) {
		Comparable comparable;
		int indexOf;
		ArrayList<MeasurementEntry> entries = new ArrayList<MeasurementEntry>();
		if (dataSet != null) {
			int k = dataSet.getSeriesCount();

			for (int i = 0; i < dataSet.getSeriesCount(); i++) {
				comparable = dataSet.getSeriesKey(i);
				indexOf = dataSet.indexOf(comparable);
				for (int j = 0; j < dataSet.getItemCount(indexOf); j++) {
					long x = (long) dataSet.getXValue(indexOf, j);
					if (x >= x1) {
						double y = dataSet.getYValue(indexOf, j);
						// System.out.println("valueX["+j+"] = "+functions.Common.doubleToTime(x)+", y = "+y);
						entries.add(new MeasurementEntry(x, y));
					}
					if (x >= x2) {
						break;
					}
				}
			}
		}
		templatePlot.replacePointList(entries);
	}

	
	private static class SeriesAndItemIndex {
		// /inner CLASS to group series and item clicked index
		public int itemIndex;
		public int seriesIndex;

		public SeriesAndItemIndex(int i, int s) {
			itemIndex = i;
			seriesIndex = s;
		}

		@Override
		public String toString() {
			return "itemIndex=" + itemIndex + ",seriesIndex=" + seriesIndex;
		}
	}

	
	/**
	 * computes width in pixels of the template plot according to given real
	 * width
	 * 
	 * @param width
	 * @return
	 */
	private static int computeTmplPlotWidth(double width) {
		return (int) (width * 9);
	}

	
	/**
	 * template control mechanism
	 * 
	 * if template control is turned on and template control variables exist
	 * proof whether current value is in template's area
	 * @param sensorUID
	 * @param value
	 */
	private void templateCtrlUpdate(String sensorUID, double value, int index)
	{		
		Brick tmpBrick = Brick.getBrick(connectionData.BrickList, sensorUID);
		/*
		if ((Brick.getBrick(connectionData.BrickList, sensorUID).ctrlTmpl[0] == true)
				&& (tmplCollection1_1.containsKey(sensorUID))
				&& (tmplStartMs.containsKey(sensorUID))
				&& ((tmpBrick.ctrlTmplruns[0] == true) || (tmpBrick.ctrlTmplruns[0] == true)))
		*/
		if (Brick.getBrick(connectionData.BrickList, sensorUID).ctrlTmpl[index] == true)
			if (tmplCollection1_1.containsKey(sensorUID))
				if (tmplStartMs.containsKey(sensorUID))
					if ((tmpBrick.ctrlTmplruns[index] == true))
				//if ((tmpBrick.ctrlTmplruns[0] == true) || (tmpBrick.ctrlTmplruns[0] == true))		
		{
			long timeNow 		= System.currentTimeMillis();
			double tmplValue 	= tmpBrick.tmplPlot[index].getYValue(timeNow, tmplStartMs.get(sensorUID));
		
			if (((tmplValue + tmpBrick.tmpl1Width) < value)|| ((tmplValue - tmpBrick.tmpl1Width) > value)) 
			{
				// set alarm state
				//plot1StateMap.put(sensorUID, 3);
				Integer[] tmpArray = plot1ControlMap.get(sensorUID);
				tmpArray[2] = 2;
				plot1ControlMap.put(sensorUID, tmpArray);
			}
			else
			{
				Integer[] tmpArray 	= plot1ControlMap.get(sensorUID);
				tmpArray[2] 		= 0;
				plot1ControlMap.put(sensorUID, tmpArray);
			}
		}
	}
		
	
	/**
	 * set the states of each plot by finding the highest state over all control cases,
	 * update the color of the plot according to the state
	 * @param sensorUID		UID of the sensor
	 */
	private void colorChart(String sensorUID)
	{
		// find the state for each plot by finding the highest state over all control cases
		int state1 = 0;
		int state2 = 0;
		
		Integer tmpArray[] = plot1ControlMap.get(sensorUID);
		for (int i=0; i<tmpArray.length; i++)
		{
			if (state1<tmpArray[i]) state1 = tmpArray[i];
		}

		tmpArray = plot2ControlMap.get(sensorUID);
		for (int i=0; i<tmpArray.length; i++)
		{
			if (state2<tmpArray[i]) state2 = tmpArray[i];
		}
		
		// set the states
		plot1StateMap.put(sensorUID, state1);
		plot2StateMap.put(sensorUID, state2);
		
		// set color of the plot whose state is the highest
		if (plot1StateMap.get(sensorUID) <= plot2StateMap.get(sensorUID)) 
		{
			switch (plot2StateMap.get(sensorUID)) 
			{
				case 0:
					plotMap.get(sensorUID).setBackgroundPaint(Color.white);
					break;
				case 1:
					plotMap.get(sensorUID).setBackgroundPaint(Color.yellow);
					break;
				case 2:
					plotMap.get(sensorUID).setBackgroundPaint(Color.red);
					break;
			}
		}
		else
		{
			switch (plot1StateMap.get(sensorUID)) 
			{
				case 0:
					plotMap.get(sensorUID).setBackgroundPaint(Color.white);
					break;
				case 1:
					plotMap.get(sensorUID).setBackgroundPaint(Color.yellow);
					break;
				case 2:
					plotMap.get(sensorUID).setBackgroundPaint(Color.red);
					break;
			}

		}
	}
	
	
	/**
	 * average control mechanism
	 */
	private void simpleTresholdCtrlUpdate(String sensorUID, double value, int index)
	{
		if (index == 0)
		{
			// check whether the value is below the threshold
			if (markerMapMin1Critical.containsKey(sensorUID)) 
			{
				// critical
				if ((value < markerMapMin1Critical.get(sensorUID).getValue())
					|| (value > markerMapMax1Critical.get(sensorUID).getValue())) 
				{
					// set alarm state
					//plot1StateMap.put(sensorUID, 3);
					Integer[] tmpArray = plot1ControlMap.get(sensorUID);
					tmpArray[0] = 2;
					plot1ControlMap.put(sensorUID, tmpArray);
				}

				// warning
				else if ((value < markerMapMin1Warning.get(sensorUID).getValue())
					|| (value > markerMapMax1Warning.get(sensorUID).getValue())) 
				{
					Integer[] tmpArray = plot1ControlMap.get(sensorUID);
					tmpArray[0] = 1;
					plot1ControlMap.put(sensorUID, tmpArray);
				}

				// normal level
				else 
				{
					Integer[] tmpArray = plot1ControlMap.get(sensorUID);
					tmpArray[0] = 0;
					plot1ControlMap.put(sensorUID, tmpArray);
				}
			}
		}
		else if (index == 1)
		{
			// check whether the value is below the thresholds
			if (markerMapMin2Critical.containsKey(sensorUID)) 
			{
				// 	critical
				if ((value < markerMapMin2Critical.get(sensorUID).getValue())
						|| (value > markerMapMax2Critical.get(sensorUID).getValue())) 
				{
					Integer[] tmpArray = plot2ControlMap.get(sensorUID);
					tmpArray[0] = 2;
					plot2ControlMap.put(sensorUID, tmpArray);
				}

				// warning
				else if ((value < markerMapMin2Warning.get(sensorUID).getValue())
						|| (value > markerMapMax2Warning.get(sensorUID).getValue())) 
				{
					Integer[] tmpArray = plot2ControlMap.get(sensorUID);
					tmpArray[0] = 1;
					plot2ControlMap.put(sensorUID, tmpArray);
				}

				// normal level
				else 
				{
					Integer[] tmpArray = plot2ControlMap.get(sensorUID);
					tmpArray[0] = 0;
					plot2ControlMap.put(sensorUID, tmpArray);
				}
			}
		}
	}
	
	
	/*
	 * static void addFill(Plot plot) {
	 * 
	 * XYSeries lowerLimitSeries = ((XYSeriesCollection)
	 * (plot.getDataset())).getSeries(1); XYSeriesCollection fillSet = new
	 * XYSeriesCollection(); double lowerBound =
	 * plot.getRangeAxis().getLowerBound(); fillSet.addSeries(lowerLimitSeries);
	 * fillSet.addSeries(createLowerFillSeries(lowerLimitSeries, lowerBound));
	 * plot.setDataset(1, fillSet); Paint fillPaint = Color.GREEN;
	 * XYDifferenceRenderer fillRenderer = new XYDifferenceRenderer(fillPaint,
	 * fillPaint, false); fillRenderer.setSeriesStroke(0, new BasicStroke(0));
	 * //do not show fillRenderer.setSeriesStroke(1, new BasicStroke(0)); //do
	 * not show plot.setRenderer(1, fillRenderer); ... }
	 * 
	 * static XYSeries createLowerFillSeries(XYSeries lowerLimitSeries, double
	 * lowerLimit) { int size = lowerLimitSeries.getItems().size(); XYSeries res
	 * = new XYSeries("lowerFillSeries"); for (int i = 0; i < size; i++)
	 * res.add(new XYDataItem(lowerLimitSeries.getX(i), lowerLimit)); return
	 * res; }
	 */

}
