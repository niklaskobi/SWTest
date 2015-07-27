package data;

import java.util.ArrayList;
import java.util.List;

import objects.Brick;

import com.tinkerforge.IPConnection;

/**
 * connectionData class - all relevant data for connection with the brick is stored here.
 * @author Kv1
 *
 */
public class connectionData {
	
	/**
	 * List for all of the brick objects
	 */
	public static List<Brick> BrickList = new ArrayList<Brick>();
	
	/**
	 * List only for Bricks that are selected and thus are presented on the sensor window
	 */
	public static List<Brick> presentedBrickList = new ArrayList<Brick>();
	
	
	public static IPConnection ipcon = new IPConnection();
	
	/**
	 * File path for sensor data storage
	 */
	public static String storageFilePath;
	
	/**
	 * true if user allowed storing data from the sensors
	 */
	public static boolean storeData = true;
	
	
	/**
	 * true if sensor window is open
	 */
	public static boolean showData = false;

	
	
	public static List<Brick> getBrickList() {
		return BrickList;
	}

	public static void setBrickList(List<Brick> BrickList) {
		connectionData.BrickList = BrickList;
	}

	public static void removeAllBricks()
	{
		BrickList.clear();
	}
	
	public static void addPresentedBrick(Brick br)
	{
		presentedBrickList.add(br);
	}
	
	public static void removePresentedBrick(Brick br)
	{
		presentedBrickList.remove(br);
	}
	
	public static void removeAllPresentedBricks()
	{
		presentedBrickList.clear();
	}
	
	public static void setStorageFilePath(String s)
	{
		storageFilePath = s;
	}
	
	public static String getStorageFilePath()
	{
		return storageFilePath;
	}
	
	public static void allowDataStorage(boolean allow)
	{
		storeData = allow;
		windows.mainWindow.allowDataStore = allow;
	}

	public static void forbidFileStorage() {
		storeData = false;		
	}
	
	public static void allowFileStorage() {
		storeData = true;		
	}
	
}
