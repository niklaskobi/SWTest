package data;

import java.util.HashMap;
import java.util.Map;

/**
 * in this class all of the constants (like default values, min/max values and brick device ID's)
 * are stored. If You want to add a new kind of a brick You have to define its defaults and 
 * describing information (like unit name and number of sensors). Defaults for every single case (simple and average) 
 * must be fulfilled with information for the new brick.
 * 
 * @author Kv1
 *
 */
public class constants {
	
	/**
	 * these are maps with some 
	 */
	public static Map<Integer, String> brickIdMap = new HashMap<Integer, String> ();
	public static Map<Integer, String> brickUnitMap = new HashMap<Integer, String> ();
	public static Map<Integer, String> brick2ndUnitMap = new HashMap<Integer, String> ();
	public static Map<Integer, String> enumTypeMap = new HashMap<Integer, String> ();
	public static Map<Integer, Double> brickMinValue = new HashMap<Integer, Double> ();
	public static Map<Integer, Double> brickMaxValue = new HashMap<Integer, Double> ();
	public static Map<Integer, Double> brickMinValue2nd = new HashMap<Integer, Double> ();
	public static Map<Integer, Double> brickMaxValue2nd = new HashMap<Integer, Double> ();
	public static Map<Integer, Double> brickAvgHigh1 = new HashMap<Integer, Double> ();
	public static Map<Integer, Double> brickAvgHigh2 = new HashMap<Integer, Double> ();
	public static Map<Integer, Double> brickAvgLow1 = new HashMap<Integer, Double> ();
	public static Map<Integer, Double> brickAvgLow2 = new HashMap<Integer, Double> ();
	
	public static Map<Integer, Double> brickTresholdDef = new HashMap<Integer, Double> ();
	public static Map<Integer, Integer> brickSensorNumber = new HashMap<Integer, Integer> ();
	
	/**
	 * update frequency of all sensors
	 * value is amount of ms = period between measurements,
	 * 100 = every 100th ms = 10 times per second
	 * 
	 */
	public static int updateFrequency = 100;
		
	public enum brickIds
	{
		// add new brick ID's here---------------------
		MASTER (13, "Brick Master", "",0,0),
		BRICK_AMLIGHT(21, "Bricklet Ambient Light", "Lum", 0.0, 100.0),
		BRICK_VOLTAGE(227, "Bricklet Voltage/Current", "Volt", 0.0, 10.0);
		//-----------------------------------------------
		
		// enum constructor -----------------------------
		private final int id;
		private final String name;
		private final String unit;
		private final double min;
		private final double max;
		brickIds(int id, String name, String unit, double min, double max)
		{
			this.id = id;
			this.name = name;
			this.unit = unit;
			this.min = min;
			this.max = max;
		}
		public int getId() {return id;}
		public String getName() {return name;}
		public String getUnit() {return unit;}
		public double getMin() {return min;}
		public double getMax() {return max;}
		//-----------------------------------------------						
	}
	
	
	/**
	 * all the init methods are wrapped in this method
	 */
	public static void initConstants()
	{
		fillBrickIdMap();
		fillEnumMap();
		fillUnitMap();
		fillMinMap();
		fillMaxMap();
		fillMinMap2nd();
		fillMaxMap2nd();
		fillTresholdMap();
		fill2ndUnitMap();
		fillSensorNumberMap();
		fillAvgLow2nd();
		fillAvgLow1st();
		fillAvgHigh2nd();
		fillAvgHigh1st();
	}
	
	
	/**
	 * Fill the map <deviceID:brick name>
	 * if You are about to add a new kind of a brick you must add it here.
	 */
	private static void fillBrickIdMap()
	{
		brickIdMap.put(13, "Brick Master");
		brickIdMap.put(21, "Ambient Light");
		brickIdMap.put(227, "Voltage/Current");
	}
	
	/**
	 * Fill the map for connection state names
	 */
	private static void fillEnumMap()
	{
		enumTypeMap.put(0, "available");
		enumTypeMap.put(1, "reconfig");
		enumTypeMap.put(2, "disconnected");
	}	
	
	/**
	 * Fill the map with units. Almost each brick will have its own measurement unit.  
	 */
	private static void fillUnitMap()
	{
		brickUnitMap.put(13, "");
		brickUnitMap.put(21, "Lum");
		brickUnitMap.put(227, "Volt");
	}	

	
	/**
	 * If there 2 sensors with separate measurements you can also define its second measurement unit name
	 */
	private static void fill2ndUnitMap()
	{
		brick2ndUnitMap.put(13, "");
		brick2ndUnitMap.put(21, "");
		brick2ndUnitMap.put(227, "Ampere");
	}		
	
	/**
	 * this values are default min values for the simple case. 
	 */
	private static void fillMinMap()
	{
		brickMinValue.put(13, 0.0);
		brickMinValue.put(21, 0.0);
		brickMinValue.put(227, 0.0);
	}	

	/**
	 * this values are default max values for the simple case. 
	 */
	private static void fillMaxMap()
	{
		brickMaxValue.put(13, 0.0);
		brickMaxValue.put(21, 100.0);
		brickMaxValue.put(227, 10.0);
	}	
	
	/**
	 * this values are default min values for the simple case for the 2nd sensor. 
	 */
	private static void fillMinMap2nd()
	{
		brickMinValue2nd.put(13, 0.0);
		brickMinValue2nd.put(21, 0.0);
		brickMinValue2nd.put(227, 0.0);
	}

	/**
	 * this values are default max values for the simple case for the 2nd sensor. 
	 */
	private static void fillMaxMap2nd()
	{
		brickMaxValue2nd.put(13, 0.0);
		brickMaxValue2nd.put(21, 100.0);
		brickMaxValue2nd.put(227, 10.0);
	}	
	
	/**
	 * this values are default min values for the average case. 
	 */
	private static void fillAvgLow1st()
	{
		brickAvgLow1.put(13, 0.0);
		brickAvgLow1.put(21, 1.0);
		brickAvgLow1.put(227, 1.0);
	}
	
	/**
	 * this values are default min values for the average case for 2nd sensor. 
	 */
	private static void fillAvgLow2nd()
	{
		brickAvgLow2.put(13, 0.0);
		brickAvgLow2.put(21, 1.0);
		brickAvgLow2.put(227, 1.0);
	}

	/**
	 * this values are default max values for the average case for 2nd sensor. 
	 */	
	private static void fillAvgHigh2nd()
	{
		brickAvgHigh2.put(13, 0.0);
		brickAvgHigh2.put(21, 1.0);
		brickAvgHigh2.put(227, 1.0);
	}

	/**
	 * this values are default max values for the average case for 1st sensor. 
	 */		
	private static void fillAvgHigh1st()
	{
		brickAvgHigh1.put(13, 0.0);
		brickAvgHigh1.put(21, 1.0);
		brickAvgHigh1.put(227, 1.0);
	}
		
	
	private static void fillTresholdMap()
	{
		brickTresholdDef.put(13, 0.0);
		brickTresholdDef.put(21, 10.0);
		brickTresholdDef.put(227, 0.5);
	}	
	
	/**
	 * in this maps the number of the sensors for each bricklet is stored. Master brick (the 1st one) has no sensors,
	 * Light sensor (2nd one, with device id 21) has only 1 sensor, and the current/voltage sensor has 2 sensors.
	 */
	private static void fillSensorNumberMap()
	{
		brickSensorNumber.put(13, 0);
		brickSensorNumber.put(21, 1);
		brickSensorNumber.put(227, 2);		
	}
}
