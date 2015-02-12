package functions;

import java.io.IOException;

import windows.mainWindow;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import data.connectionData;
import objects.Brick;

public class Events {
	
	/**
	 * event which should be called if the user wants to forbid
	 * storing data to file 
	 */
	public static void forbidFileStorage()
	{
		System.out.println("forbid file storage");
		data.connectionData.forbidFileStorage();
		windows.mainWindow.forbidFileStorage();
	}
	
	
	/**
	 * event which is called after we detect that some device was connected
	 */
	public static void deviceConnected()
	{		
		System.out.println("device connected EVENT");
		connection.connectionAvalaible = true;
		windows.mainWindow.deviceConnected();
	}
	
	
	
	/**
	 * event which is called after we detect that some device was disconnected
	 */
	public static void unrequestedDisconnect(String uid)
	{
		System.out.println("device disconnected event");
		if (connection.connectionAvalaible == true)
		{
			connection.connectionAvalaible = false;
			windows.mainWindow.deviceDisconnected();
			data.dialogs.ipBrickDisconnected(uid);
		}
	}
	
	/**
	 * event which should be called if the user wants to allow
	 * storing data to file 
	 */
	public static void allowFileStorage()
	{
		System.out.println("allow file storage");
		data.connectionData.allowFileStorage();
		windows.mainWindow.allowFileStorage();
	}

	
	/**
	 * event which should be called after the "start" button was clicked 
	 * @throws NotConnectedException 
	 * @throws TimeoutException 
	 */
	public static void startButtonClicked() throws TimeoutException, NotConnectedException
	{
		connectionData.showData = true;
		connection.startAllSensors();
		windowController.openSensorWindow();
		mainWindow.startButtonChangeTo_stop();		
	}
	
	/**
	 * event which should be called after the "stop" button was clicked 
	 */
	public static void stopButtonClicked()
	{
		connectionData.showData = false;
		windowController.closeSensorWindow();
		mainWindow.startButtonChangeTo_start();
		connection.stopAllSensors();
	}
	
	/**
	 * called when one of the sensor boxes was unchecked
	 * @param UID
	 */
	public static void sensorBoxUnchecked(String UID)
	{
		// uncheck the item
		Brick.uncheckBrick(Brick.getBrick(connectionData.BrickList,UID));
		// uncheck also all sensors
		if (data.constants.brickSensorNumber.get(Brick.getBrick(connectionData.BrickList,UID).deviceIdentifier) >=1)
		{
			Brick.uncheck2Brick(Brick.getBrick(connectionData.BrickList,UID));
		}
		if (data.constants.brickSensorNumber.get(Brick.getBrick(connectionData.BrickList,UID).deviceIdentifier) >=2)
		{
			Brick.uncheck3Brick(Brick.getBrick(connectionData.BrickList,UID));
		}		
		connectionData.removePresentedBrick(Brick.getBrick(connectionData.BrickList,UID));
		windowController.removePlot(UID);
		
		if (connectionData.showData == true) connection.stopListener(UID);
	}

	
	/**
	 * called when one of the sensor boxes was checked
	 * @param UID
	 */
	
	public static void sensorBoxChecked(String UID)
	{
		// check the item
		Brick.checkBrick(Brick.getBrick(connectionData.BrickList,UID));
		//check also all sensors
		if (data.constants.brickSensorNumber.get(Brick.getBrick(connectionData.BrickList,UID).deviceIdentifier) >=1)
		{
			Brick.check2Brick(Brick.getBrick(connectionData.BrickList,UID));
		}
		if (data.constants.brickSensorNumber.get(Brick.getBrick(connectionData.BrickList,UID).deviceIdentifier) >=2)
		{
			Brick.check3Brick(Brick.getBrick(connectionData.BrickList,UID));
		}
		
		// add new presented Brick and a new graph to the sensor window, 
		// avoid master Brick, since there are no adjustable values and no graph for it
		if (Brick.getBrick(connectionData.BrickList,UID).deviceIdentifier != 13)
		{
			connectionData.addPresentedBrick(Brick.getBrick(connectionData.BrickList,UID));
			windowController.addPlot(Brick.getBrick(connectionData.BrickList, UID));
		}
		
		if (connectionData.showData == true)
			try {
				connection.startListener(UID);
			} catch (TimeoutException | NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}
	
	
	/**
	 * called when user enables average control
	 * @param UID
	 */
	public static void enableAverageControl(String UID)
	{
		
	}

	
	/**
	 * called when user disables average control
	 * @param UID
	 */
	public static void disableAverageControl(String UID)
	{
		
	}
	
	
	/**
	 * event which should be called after a disconnect has occurred
	 */	
	public static void disconnectDevice()
	{
		try {
			connection.disconnect();
			connection.stopAllSensors();
		} catch (AlreadyConnectedException | NotConnectedException
				| IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("catched exception");
		}
		connectionData.removeAllBricks();
		windowController.closeSensorWindow();
		connectionData.removeAllPresentedBricks();
	}
	
	/**
	 * event called when the maxima of a graph has been changed
	 * @param uid	sensor uid
	 * @param value	max value
	 */
	public static void updateMaxima(String uid, double value, int index)
	{
		windows.sensorWindow.updateMaxima(uid, value, index);
	}
	
	/**
	 * event called when the minima of a graph has been changed
	 * @param uid	sensor uid	
	 * @param value	new min value
	 */
	public static void updateMinima(String uid, double value, int index)
	{
		windows.sensorWindow.updateMinima(uid, value, index);
	}
	
	/**
	 * event called when the minima of a graph has been changed
	 * @param uid	sensor uid	
	 * @param value	new min value
	 */
	public static void updateAverage(String uid, double value, int index)
	{
		windows.sensorWindow.updateAverage(uid, value, index);
	}

	/**
	 * event will be triggered after a minimal amount of value have been stored
	 */
	public static void activateSliderSensorWindow()
	{
		windows.sensorWindow.activateSlider();
	}

	
}
