package objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class TemplatePlot {

	public final static String fileExtention = "tmp";
	private final static String entriesDescriptionLine = "value1;value2";

	public String dateStr = "";
	public String fileName = "new plot";
	public ArrayList <MeasurementEntry> allPoints;
	
	private int currentIndex = 0;
	private long currentLap  = 0;
			
	
	/**
	 * constructor
	 */
	public TemplatePlot()
	{
		allPoints = new ArrayList<MeasurementEntry>();
	}
	
	
	/**
	 * constructor with a given array list
	 * @param list
	 */
	public TemplatePlot(ArrayList<MeasurementEntry> list)
	{
		this.allPoints = list;
		normalizeTimestamps();
	}
	
	
	/**
	 * 1st timetamp will become 0:00:00, 
	 * all following times will become: 0:00:01, 0:00:02.... 
	 * according to the time difference to the first timestamp in the given list
	 * @param oldList list of all measurements objects
	 * @return new list
	 */
	public void normalizeTimestamps()
	{		
		ArrayList<MeasurementEntry> newList = new ArrayList<MeasurementEntry>();
		//double offset = this.allPoints.get(0).value1;
		//if (this.allPoints!=null)
		//{		
			if (!this.allPoints.isEmpty())
			{
				long offset = this.allPoints.get(0).value1;
				for (int i=0; i<this.allPoints.size(); i++)
				{
					newList.add(new MeasurementEntry(this.allPoints.get(i).value1-offset, this.allPoints.get(i).value2));
				}
				this.allPoints = newList;
			}
		//}
	}
	
	
	public MeasurementEntry getEntry(int i)
	{
		return allPoints.get(i);
	}
	
	
	/**
	 * returns a y value corresponding to given x on the plot
	 * @param currentMs	current time in milliseconds
	 * @param startMs	start time of the template plot 
	 * @return			y value
	 */
	public double getYValue(long currentMs, long startMs)
	{
		long msTmp = currentMs - startMs;
		//msTmp = msTmp- lapCnt*this.getLapLength();		
		msTmp = msTmp % this.getLapLength();
		double y1 = 0;
		double y2 = 0;
		long x1 = 0;
		long x2 = 0;
		
		// find 2 surrounding values
		for (int i=1; i<this.allPoints.size(); i++)
		{
			if ( this.allPoints.get(i).value1>msTmp )
			{
				y1 = allPoints.get(i-1).value2;
				y2 = allPoints.get(i).value2;
				x1 = allPoints.get(i-1).value1;
				x2 = allPoints.get(i).value1;
				break;
			}
		}		
		// find the corresponding y value on the line between (x1,y1) and (x2,y2)
		double res = ((y2-y1)/(x2-x1))*msTmp + ((x2*y1-x1*y2)/(x2-x1));
		return res;
	}
	
	
	public int getEntriesNumber()
	{
		return this.allPoints.size();
	}
	
	
	/**
	 * returns all values of this template plot in sequence.
	 * returns a value corresponding the current index, and increments the current index.  
	 * @return double value
	 */
	public MeasurementEntry getNextValue()
	{
		if (this.currentIndex >= this.allPoints.size())
		{
			this.currentLap++;
			this.currentIndex = 0;
			return this.allPoints.get(currentIndex);
		}
		else
		{
			return this.allPoints.get(currentIndex++);
		}		
	}
	
	
	/**
	 * returns the number of times all values have been requested
	 * @return
	 */
	public Long getCurrentLap()
	{
		return this.currentLap;
	}
	
	/**
	 * returns the length of the measured plot in milliseconds 
	 * @return milliseconds
	 */
	public Long getLapLength()
	{
		return this.allPoints.get(allPoints.size()-1).value1;
	}
	
	
	/**
	 * adds a new point to the array
	 * @param entry new antry
	 */
	public void addPoint(MeasurementEntry entry)
	{
		this.allPoints.add(entry);
	}
	
	
	/**
	 * replaces the current list with the given one
	 * @param list
	 */
	public void replacePointList(ArrayList<MeasurementEntry> list)
	{
		this.allPoints = list;
	}

	
	/**
	 * writes template data to given file path
	 * @param filePath
	 * @return true if OK
	 */
	public boolean writeTemplateToFile(String fp)
	{
		String str = createTemplateString();
		String filePath = null;
		if (fp!=null) filePath = fp+"."+fileExtention; 
			
		if (filePath == null)
		{
			data.dialogs.fileNotDefined();
			return false;
		}
		else		
		try
		{
			File file = new File(filePath);
			if (!file.exists()) 
			{
				Path path = Paths.get(filePath);
		        Files.createDirectories(path.getParent());
		        try {
		            Files.createFile(path);
		        } catch (FileAlreadyExistsException e) {
		            System.err.println("already exists: " + e.getMessage());
		        }
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);		// true to append
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(str);
			bw.close();
			return true;
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			data.dialogs.fileFNFException(data.connectionData.storageFilePath);
			return false;			  
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			data.dialogs.fileIOException(data.connectionData.storageFilePath);
			return false;
		}		
	}
	
		
	/**
	 * structure of the future file:
	 * 
	 * 1st line: date of creation("yyyy-MM-dd HH:mm:ss"); 
	 * 2nd line: doubleValueX,doubleValueY;
	 * 3rd line: doubleValueX,doubleValueY;
	 * ...
	 * 
	 * @param uid
	 * @param value
	 * @param index	index of the value (1st value, 2nd value...)
	 */
	private String createTemplateString()
	{
		String entry = "";
		//Brick br = objects.Brick.getBrick(data.connectionData.BrickList, uid);
		Date date = new Date();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		this.dateStr = dateFormat.format(date);
		
		entry += dateFormat.format(date);												// date+time
		entry += "\n";
		entry += entriesDescriptionLine + "\n";
		
		for (int i=0; i<this.allPoints.size(); i++)
		{
			entry+= Long.toString(this.allPoints.get(i).value1)+",";					// 1st value
			entry+= Double.toString(this.allPoints.get(i).value2)+";";					// 2nd value
			entry+= "\n";
		}		
		return entry;
	}
	
	/**
	 * scan file on given path
	 * store values into allPoints - array
	 * @param path file path
	 * @param replace if false, no plot data will be replaced, can be used to proof if file is convienient
	 * if true, plot data of the current kobject will be replaced by the data from file 
	 * @return true if template file was OK
	 */
	public boolean readTemplateFromFile(String path, boolean replace)
	{
		Scanner sc = null;
		try {
			File f = new File(path);
			sc = new Scanner(f);
		} catch (FileNotFoundException e) {
			data.dialogs.fileFNFException(data.connectionData.storageFilePath);
			return false;
		}

		String line = sc.nextLine();
		String date = line;
		line = sc.nextLine();														// skip 1st line for now
		if (!entriesDescriptionLine.equals(line))									// check for the right data in file
		{
			data.dialogs.fileDataError(path);
			sc.close();
			return false;							
		}
		String str[] = new String[2];
		long value1;
		double value2; 
		
		if (replace)
		{
			allPoints.clear();														// delete old entries
			this.dateStr = date;
			String tmp = path.substring(path.lastIndexOf("\\")+1);
			this.fileName = tmp;
			System.out.println("start read data from "+path);		
			// scan file and add values to the array
			while(sc.hasNextLine())
			{
				line = sc.nextLine();
				line = line.substring(0, line.length()-1);
				str = line.split(",");
				if (str.length != 2)	
				{
					data.dialogs.fileDataError(path);
					sc.close();
					return false;
				}
				//value1 = Double.parseDouble(str[0]);
				try
				{
					value1 = Long.parseLong(str[0]);
					value2 = Double.parseDouble(str[1]);
				}
				catch (Exception e)
				{
					data.dialogs.fileDataError(path);
					sc.close();
					return false;
				}
				allPoints.add(new MeasurementEntry(value1, value2));
			}
			sc.close();
		}
		
		return true;
	}
	
	
	/**
	 * returns string containing all values of the template plot
	 */
	public String toString()
	{
		String str = "";

	    for(int j=0 ; j<this.allPoints.size();j++)
	    {
	    	double x=allPoints.get(j).value1;
	    	double y=allPoints.get(j).value2;
	    	str += "value["+j+"], x = "+functions.Common.doubleToTime(x)+", y = "+y+"\n";
	    }	    
	    return str;
	}
	
	
	/**
	 * verify if file on the given path is a valid template file
	 * @param path
	 * @return
	 */
	public static boolean isTemplateValid(String path)
	{
		Scanner sc = null;
		try {
			File f = new File(path);
			sc = new Scanner(f);
		} catch (FileNotFoundException e) {
			return false;
		}

		String line = sc.nextLine();
		String date = line;
		line = sc.nextLine();														// skip 1st line for now
		if (!entriesDescriptionLine.equals(line))									// check for the right data in file
		{
			sc.close();
			return false;							
		}
		String str[] = new String[2];
		long value1;
		double value2; 
		
		while(sc.hasNextLine())
		{
			line = sc.nextLine();
			line = line.substring(0, line.length()-1);
			str = line.split(",");
			if (str.length != 2)	
			{
				sc.close();
				return false;
			}
			//value1 = Double.parseDouble(str[0]);
			try
			{
				value1 = Long.parseLong(str[0]);
				value2 = Double.parseDouble(str[1]);
			}
			catch (Exception e)
			{
				sc.close();
				return false;
			}
		}
		
		sc.close();
		return true;
		/*
		Scanner sc = null;
		try {
			File f = new File(path);
			sc = new Scanner(f);
		} catch (FileNotFoundException e) {
			//data.dialogs.fileFNFException(data.connectionData.storageFilePath);
			return false;
		}

		String line = sc.nextLine();
		line = sc.nextLine();														// skip 1st line for now
		if (!entriesDescriptionLine.equals(line))									// check for the right data in file
		{
			data.dialogs.fileDataError(path);
			sc.close();
			return false;							
		}
		sc.close();
		return true;
		*/
	}

}
