package functions;

import objects.Brick;
import objects.TemplatePlot;

import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;



//import windows.DualAxisDemo2;
import windows.mainWindow;
import windows.plotConfirmWindow;
import windows.sensorWindow;

public class windowController {
	
	public static sensorWindow 			sensorWindow;
	public static plotConfirmWindow[]	plotWindow = new plotConfirmWindow[5];
    public static boolean				existsSensorWindow 		= false;
    public static boolean 				existsSettingWindow		= false;
    public static boolean[]				existsPlotWindow = new boolean[5];
	    
        
    public static void updateTresholdMin1(String UID, double threshold)
    {
    	windows.sensorWindow.updateTresholdMin1(UID, threshold);
    }
    
    public static void updateAvgCtrl(String UID, double value, int index, boolean high)
    {
    	windows.sensorWindow.updateAvrgCtrl(UID, value, index, high);
    }
            
    public static void updateTresholdMin2(String UID, double threshold)
    {
    	windows.sensorWindow.updateTresholdMin2(UID, threshold);
    }
    
    public static void updateTresholdMax1(String UID, double threshold)
    {
    	windows.sensorWindow.updateTresholdMax1(UID, threshold);
    }
    
    public static void updateTresholdMax2(String UID, double threshold)
    {
    	windows.sensorWindow.updateTresholdMax2(UID, threshold);
    }

    public static void removePlot(String UID)
    {
    	if (existsSensorWindow == true)windows.sensorWindow.removePlot(UID);
    }
    
    public static void addPlot(Brick br)
    {
    	// don't show settings for master brick since there are no relevant settings
    	if (br.getDeviceIdentifier() != 13)
    	{
    		if (existsSensorWindow == true)windows.sensorWindow.addPlot(br);
    	}
    }
    
    public static void hideUnhidePlot(Brick br, int index)
    {
    	if (existsSensorWindow == true) windows.sensorWindow.hideUnhidePlot(br, index);
    }
    
    public static void hideUnhideAvgControl(Brick br, int index)
    {
    	if (existsSensorWindow == true)
    	{
    		windows.sensorWindow.hideUnhideAvgCtrl(br,index);
    	}
    }
        
    
    public static void changeTmplCntrl(Brick br, int index, boolean on)
    {
    	if (existsSensorWindow == true)
    	{
    		windows.sensorWindow.changeTmplCntrl(br,index, on);
    	}    	
    }
    
    public static void openSettingsWindow()
    {
		if (existsSettingWindow == false)
		{
			//sWin = new DualAxisDemo2("Settings");
			//sWin.pack();
			//sWin.setVisible(true);
			existsSettingWindow = true;
		}		
    }
    
	public static void closeSettingsWindow()
	{
		/*
		sWin.setVisible(false);
		sWin.dispose();
		*/		
		existsSettingWindow = false;
	}
    
	public static void openSensorWindow()
	{

		if (existsSensorWindow == false)
		{
			sensorWindow = new sensorWindow("sensor data");
			sensorWindow.setDefaultCloseOperation(ApplicationFrame.HIDE_ON_CLOSE);
			sensorWindow.pack();
        	RefineryUtilities.centerFrameOnScreen(sensorWindow);
        	sensorWindow.setVisible(true);
        	existsSensorWindow = true;
		}		
		else 
		{
			sensorWindow.dispose();			
			sensorWindow = new sensorWindow("sensor data");			
			sensorWindow.pack();
        	RefineryUtilities.centerFrameOnScreen(sensorWindow);
        	sensorWindow.setVisible(true);
        	existsSensorWindow = true;
		}
    	mainWindow.startButtonChangeTo_stop();
	}
	
	public static void closeSensorWindow()
	{
		if (existsSensorWindow)
		{
			sensorWindow.setVisible(false);
			sensorWindow.dispose();
			existsSensorWindow = false;
			mainWindow.startButtonChangeTo_start();
		}
	}
	
	public static void openPlotWindow(TemplatePlot t, int index)
	{		
		if (existsPlotWindow[index] == false)
		{
			plotWindow[index] = new plotConfirmWindow(t, index);
        	existsPlotWindow[index] = true;
		}
	}
	
	/**
	 * creating new template window with a given plot path
	 * @param path
	 */
	public static void openPlotWindow(String path, int index)
	{
		plotWindow[index] = new plotConfirmWindow(path, index);
	}
	
	public static void closePlotWindow(int index)
	{
		if (existsPlotWindow[index])
		{
			existsPlotWindow[index] = false;
		}
	}
		
	public static boolean isVisibleSensorWindow()
	{
		if (existsSensorWindow == true) return true;
		else return false;
	}
	
	
	
	
}
