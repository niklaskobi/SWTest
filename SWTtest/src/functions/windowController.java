package functions;

import objects.Brick;

import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

//import windows.DualAxisDemo2;
import windows.mainWindow;
import windows.sensorWindow;

public class windowController {
	
    //public final static sensorWindow demo = new sensorWindow("sensor data", );
	public static sensorWindow demo;
	//static DualAxisDemo2 sWin;
	
    public static boolean existsSensorWindow = false;
    public static boolean existsSettingWindow = false;
	
    
    public static void updateTresholdMin1(String UID, double threshold)
    {
    	sensorWindow.updateTresholdMin1(UID, threshold);
    }
    
    public static void updateAvgHigh(String UID, double threshold, int index)
    {
    	// TODO::
    }
    
    public static void updateAvgLow(String UID, double threshold, int index)
    {
    	// TODO::
    }    
    
    
    public static void updateTresholdMin2(String UID, double threshold)
    {
    	sensorWindow.updateTresholdMin2(UID, threshold);
    }
    
    public static void updateTresholdMax1(String UID, double threshold)
    {
    	sensorWindow.updateTresholdMax1(UID, threshold);
    }
    
    public static void updateTresholdMax2(String UID, double threshold)
    {
    	sensorWindow.updateTresholdMax2(UID, threshold);
    }

    
    public static void removePlot(String UID)
    {
    	if (existsSensorWindow == true)sensorWindow.removePlot(UID);
    }
    
    
    public static void addPlot(Brick br)
    {
    	// don't show settings for master brick since there are no relevant settings
    	if (br.getDeviceIdentifier() != 13)
    	{
    		if (existsSensorWindow == true)sensorWindow.addPlot(br);
    	}
    }
    
    
    public static void hideUnhidePlot(Brick br)
    {
    	if (existsSensorWindow == true) sensorWindow.hideUnhide(br);
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
			demo = new sensorWindow("sensor data");
			demo.setDefaultCloseOperation(ApplicationFrame.HIDE_ON_CLOSE);
			demo.pack();
        	RefineryUtilities.centerFrameOnScreen(demo);
        	demo.setVisible(true);
        	existsSensorWindow = true;
		}		
		else 
		{
			demo.dispose();			
			demo = new sensorWindow("sensor data");			
			demo.pack();
        	RefineryUtilities.centerFrameOnScreen(demo);
        	demo.setVisible(true);
        	existsSensorWindow = true;
		}
    	mainWindow.startButtonChangeTo_stop();
	}
	
	public static void closeSensorWindow()
	{
		if (existsSensorWindow)
		{
			demo.setVisible(false);
			demo.dispose();
			existsSensorWindow = false;
			mainWindow.startButtonChangeTo_start();
		}
	}

	/*
	public static void displaySensorWindow()
	{
		if (existsSensorWindow == true)
		{
			demo.setVisible(true);
		}
		else openSensorWindow();
	}
	*/
	
	public static boolean isVisibleSensorWindow()
	{
		if (existsSensorWindow == true) return true;
		else return false;
	}
	
	
}
