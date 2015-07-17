package functions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import objects.Brick;

/**
 * this class describes methods for writing the measured data into a predefined csv file. 
 * @author Kv1
 *
 */
public class file {
	
	/**
	 * writes a given string to the file
	 * @param str
	 * @param filePath
	 * @return
	 */
	private static boolean writeStrToFile(String str, String filePath)
	{
		if (filePath == null)
		{
			data.dialogs.fileNotDefined();
			functions.Events.forbidFileStorage();
			return false;
		}
		else		
		try
		{
			File file = new File(filePath);
		 
			if (!file.exists()) 
			{
				//file.createNewFile();
				data.dialogs.fileIOException(filePath);
				functions.Events.forbidFileStorage();
				return false;
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(str);
			bw.close();
			return true;
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			data.dialogs.fileFNFException(data.connectionData.storageFilePath);
			functions.Events.forbidFileStorage();
			return false;			  
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			data.dialogs.fileIOException(data.connectionData.storageFilePath);
			functions.Events.forbidFileStorage();
			return false;
		}		
	}
	
	
	/**
	 * creates a new measurement entry
	 * @param uid
	 * @param value
	 * @param index	index of the value (1st value, 2nd value...)
	 */
	public static String createNewEntry(String uid, double value, int index)
	{
		String entry;
		Brick br = objects.Brick.getBrick(data.connectionData.BrickList, uid);
		Date date = new Date();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//Timestamp tstamp = new Timestamp(System.currentTimeMillis()); 

		entry = br.uid+";";															// UID
		entry += data.constants.brickIdMap.get(br.deviceIdentifier)+";";			// device type
		//entry += br.connectedUid+";";												// father UID
		entry += dateFormat.format(date)+";";										// date+time
		if (index == 0)
		{
			entry += Double.toString(value)+";";									// 1st value
			entry += data.constants.brickUnitMap.get(br.deviceIdentifier)+";";		// unit
			entry += ";"+";";														// 2nd value and unit are empty							
		}
		if (index == 1)
		{
			entry += ";"+";" + Double.toString(value)+";";							// empty fields for 1st value + 2nd value
			entry += data.constants.brick2ndUnitMap.get(br.deviceIdentifier)+";";	// unit			
		}
		entry += date.getTime();													// milliseconds		
		entry += "\n";
		return entry;	
	}
	
	
	/**
	 * creates a new csv file with 1st string 
	 * @param filePath path of the new file
	 */
	public static void createInitEntry(String filePath)
	{
		String entry;
		entry = "UID"+";";					// UID
		entry += "device type"+";";			// device type
		//entry += "father uid"+";";		// father UID
		entry += "date and time"+";";		// date+time
		entry += "1st value"+";";			// 1st value
		entry += "unit"+";";				// unit
		entry += "2nd value"+";"; 			// empty fields for 1st value + 2nd value
		entry += "unit"+";";				// unit
		entry += "timestamp";				// milliseconds		
		entry += "\n";

		try
		{
			File file = new File(filePath);
		 
			if (!file.exists()) 
			{
				data.dialogs.fileFNFException(filePath);
				functions.Events.forbidFileStorage();
				return;
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(entry);
			bw.close();
			return;
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			data.dialogs.fileFNFException(data.connectionData.storageFilePath);			  
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			data.dialogs.fileIOException(data.connectionData.storageFilePath);
		}		
	}
	
	
	/**
	 * adds entry to an exitsting csv file
	 * @param uid
	 * @param value
	 * @param index
	 */
	public static void addEntryToFile(String uid, double value, int index)
	{
		String entry = createNewEntry(uid, value, index);
		writeStrToFile(entry, data.connectionData.storageFilePath);
	}
	
	/**
	 * creates a new csv file
	 * @param str	file path
	 */
	public static void createNewFile(String str)
	{
		Path path;
		if (str != null) path = Paths.get(str);
		else return;

        try {
			Files.createDirectories(path.getParent());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			data.dialogs.unableToCreateDirectory(str);
			functions.Events.forbidFileStorage();
			return;
		}

        try {
            Files.createFile(path);
        } catch (IOException e) {
            System.err.println("already exists: " + e.getMessage());
            data.dialogs.unableToCreateFile(str);
			functions.Events.forbidFileStorage();
            return;
        }
        
        createInitEntry(str);
	}
}
