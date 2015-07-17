package objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * this class describes the object brick and the requered methods. 
 * Brick contains all of the information about one Bricklet, regardless if it is a 
 * master-brick or a common sensor-brick. 
 * 
 * @author Kv1
 *
 */
public class Brick {
	public String 			uid; 							// Brick Universal ID
	public String 			connectedUid;					// UID of a brick this brick is connected to  
	public char 			position;						// position of the brick
	public short[] 			hardwareVersion;				// hardware version
	public short[] 			firmwareVersion;				// firmware version
	public int 				deviceIdentifier;				// TinkerForge's device identifier number
	public short 			enumerationType;				// status og the brick
	public List<Brick> 		subBricks;						// list of the connected Bricks
	public Brick 			fatherBrick;					// brick object this brick is connected to
	
	public double 			tresholdMin1;					// simple control case min for 1st sensor					
	public double 			tresholdMin2;					// simple control case min for 2nd sensor
	public double 			tresholdMax1;					// simple control case max for 1st sensor
	public double 			tresholdMax2;					// simple control case max for 2nd sensor
	
	private double 			avg1high;						// average control max for 1st sensor 
	private double 			avg2high;						// average control max for 2nd sensor
	private double 			avg1low;						// average control min for 1st sensor
	private double 			avg2low;						// average control min for 2nd sensor
	
	public double 			tmpl1Width;						// template control, width of the template plot for 1st sensor
	public double 			tmpl2Width;						// template control, width of the template plot for 2nd sensor
		
	//public int 				updateRate;						// update rate of current brick
	public boolean 			checked1;						// brick is enabled/disabled
	public boolean 			checked2;						// 1st sensor is enabled/disabled
	public boolean 			checked3;						// 2nd sensor is enabled/disabled
	public double 			lastValue;						// last measured value of the brick
	public boolean 			controlAverage;					// control average is enabled/disabled for the 1st sensor
	public boolean 			controlAverage2;				// control average is enabled/disabled for the 2nd sensor
	public boolean[] 		ctrlSimple = new boolean[2];	// control simple is enabled/disabled for the 1st/2nd sensor
	public boolean[] 		ctrlTmpl = new boolean[2];		// control template is enabled/disabled for the 1st/2nd sensor
	public boolean[] 		ctrlTmplruns = new boolean[2];	// control template is running for the 1st/2nd sensor
	public String[] 		ctrlTmplPath = new String[2];	// paths for control template for the 1st/2nd sensor
	public TemplatePlot[] 	tmplPlot = new TemplatePlot[2];	// objects containing the template plots for the 1st/2nd sensor
	
	
	/**
	 * constructor, sets all the values to defaults
	 */
	public Brick()
	{
		this.uid = 				"";
		this.connectedUid = 	"";
		this.position = 		0;
		this.deviceIdentifier = 0;
		this.uid = 				"";
		this.enumerationType = 	2; 							//2 = disconnected
		firmwareVersion = 		new short[3];
		hardwareVersion = 		new short[3];
		for (int i=0;i<3;i++)
		{
			this.hardwareVersion[i] = 0;
			this.firmwareVersion[i] = 0;
		}
		subBricks = 			new ArrayList<Brick>();
		fatherBrick = 			null;
		this.tresholdMin1 = 	0;
		this.tresholdMin2 = 	0;
		this.tresholdMax1 = 	0;
		this.tresholdMax2 = 	0;
		//this.updateRate = 	0;
		this.checked1 = 		false;
		this.checked2 = 		false;
		this.checked3 = 		false;
		this.lastValue = 		0;
		this.controlAverage = 	false;
		this.controlAverage2 = 	false;
		
		for (int i=0; i<2;i++)
		{
			this.tmplPlot[i] = 		new TemplatePlot();
			this.ctrlTmpl[i] = 		false;	
			this.ctrlSimple[i] = 	false;
			this.ctrlTmplruns[i] = 	false;
			this.ctrlTmplPath[i] = "undefined";
		}
		
		this.setAvg1high(1);
		this.setAvg2high(1);
		this.setAvg1low(1);
		this.setAvg2low(1);
		this.tmpl1Width = 1;
		this.tmpl2Width = 1;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getConnectedUid() {
		return connectedUid;
	}
	public void setConnectedUid(String connectedUid) {
		this.connectedUid = connectedUid;
	}
	public char getPosition() {
		return position;
	}
	public void setPosition(char position) {
		this.position = position;
	}
	public short[] getHardwareVersion() {
		return hardwareVersion;
	}
	public void setHardwareVersion(short[] hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}
	public short[] getFirmwareVersion() {
		return firmwareVersion;
	}
	public void setFirmwareVersion(short[] firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}
	public int getDeviceIdentifier() {
		return deviceIdentifier;
	}
	public void setDeviceIdentifier(int deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}
	public short getEnumerationType() {
		return enumerationType;
	}
	public void setEnumerationType(short enumerationType) {
		this.enumerationType = enumerationType;
	}	
	public List<Brick> getSubBricksList(Brick b)
	{
		return b.subBricks;
	}		
	public void addSubBrick(Brick b)
	{
		if (!this.subBricks.contains(b))
		{
			this.subBricks.add(b);
		}
	}
	public void addFatherBrick(Brick b)
	{
		this.fatherBrick = b;
	}

	public static boolean addBrick(Brick b)
	{
		return true;
	}
	
	public void setDefaultTreshold(int id)
	{
		this.tresholdMin1 = data.constants.brickMinValue.get(id);
		this.tresholdMin2 = data.constants.brickMinValue2nd.get(id);
		this.tresholdMax1 = data.constants.brickMaxValue.get(id);
		this.tresholdMax2 = data.constants.brickMaxValue2nd.get(id);
	}
		
	
	public static void setLastValue(List<Brick>BrickList, String uid, double value)
	{		
		// iterate through all existing Bricks
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid))
				{
					tmpBrick.lastValue = value;
					return;
				}
			
			// iterate through the subBrick list
			for (Iterator<Brick> i2 = tmpBrick.subBricks.iterator(); i2.hasNext();) 
			{
				Brick tmpBrick2 = i2.next();							
				if (tmpBrick2.uid.equals(uid))
					{
						tmpBrick2.lastValue = value;
						return;
					}
			}
		}		
	}
	

	
	/**
	 * this function counts and returns the number of existing subBricks in given list
	 * @param b	Brick
	 * @return number of all stored subBricks
	 */
	public static int getSubBricksNumber(List<Brick>BrickList)
	{
		int cnt = 0;
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();
			cnt += tmpBrick.subBricks.size();
		}
		return cnt;		
	}
	
	
	
	public static boolean addBrick(List<Brick>BrickList, Brick b)
	{
		// check whether the same item is already available in the father list		
		if (!BrickExists(BrickList,b.uid))
		{
			// if the current item is a subItem		
			if (!b.connectedUid.equals("0"))
			{				
				// check whether the same item is already available by corresponding fathers subItems list
				int index = getIndexByUid(BrickList, b.connectedUid);
				if (index == -1) return false;
				Brick father = BrickList.get(index); 
				if (!BrickExists(father.subBricks, b.uid))
				{
					//add item to fathers subItems list
					father.subBricks.add(b);
					return true;
				}
			}
			else
			{	
				// add new father item
				BrickList.add(b);
				return true;
			}
		}
		return false;			
	}
		
	
	
	public static int getIndexByUid(List<Brick>BrickList, String uid)
	{				
		int cnt = 0;
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid)) return cnt;
			else cnt++;
		}
		// not found
		return -1;
	}
	
	
	/**
	 * returns the trshold of the Brick
	 * @param BrickList	List of the corresponding Bricks 
	 * @param uid	UID of the Brick
	 * @return
	 */
	public static double getThresholdMin1(List<Brick>BrickList, String uid)
	{
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid)) return tmpBrick.tresholdMin1;
		}
		// not found
		return -1;		
	}
	
	
	/**
	 * returns the average high of the Brick
	 * @param BrickList	List of the corresponding Bricks 
	 * @param uid	UID of the Brick
	 * @return
	 */
	public static double getAvgHigh(List<Brick>BrickList, String uid, int index)
	{
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid)) 
			{
				if (index == 1) return tmpBrick.avg1high;
				if (index == 2) return tmpBrick.avg2high;
			}
		}
		// not found
		return -1;		
	}

	
	/**
	 * returns the average low of the Brick
	 * @param BrickList	List of the corresponding Bricks 
	 * @param uid	UID of the Brick
	 * @return
	 */
	public static double getAvgLow(List<Brick>BrickList, String uid, int index)
	{
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid)) 
			{
				if (index == 1) return tmpBrick.avg1low;
				if (index == 2) return tmpBrick.avg2low;
			}
		}
		// not found
		return -1;		
	}	
	
	/**
	 * returns the trshold of the Brick
	 * @param BrickList	List of the corresponding Bricks 
	 * @param uid	UID of the Brick
	 * @return
	 */
	public static double getThresholdMin2(List<Brick>BrickList, String uid)
	{
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid)) return tmpBrick.tresholdMin2;
		}
		// not found
		return -1;		
	}
	
	/**
	 * returns the trshold of the Brick
	 * @param BrickList	List of the corresponding Bricks 
	 * @param uid	UID of the Brick
	 * @return
	 */
	public static double getThresholdMax1(List<Brick>BrickList, String uid)
	{
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid)) return tmpBrick.tresholdMax1;
		}
		// not found
		return -1;		
	}
	
	/**
	 * returns the trshold of the Brick
	 * @param BrickList	List of the corresponding Bricks 
	 * @param uid	UID of the Brick
	 * @return
	 */
	public static double getThresholdMax2(List<Brick>BrickList, String uid)
	{
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid)) return tmpBrick.tresholdMax2;
		}
		// not found
		return -1;		
	}
	
	
	/**
	 * set the possible upper deviation of average of the Brick, identified through given UID and Brick list
	 * @param BrickList
	 * @param uid
	 * @param threshold
	 */
	public static void setAvgHigh(List<Brick>BrickList, String uid, double threshold, int index)
	{		
		// iterate through all existing Bricks
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid))
				{
					if (index == 1) tmpBrick.avg1high = threshold;
					if (index == 2) tmpBrick.avg2high = threshold;
					return;
				}
			
			// iterate through the subBrick list
			for (Iterator<Brick> i2 = tmpBrick.subBricks.iterator(); i2.hasNext();) 
			{
				Brick tmpBrick2 = i2.next();							
				if (tmpBrick2.uid.equals(uid))
				{
					if (index == 1) tmpBrick2.avg1high = threshold;
					if (index == 2) tmpBrick2.avg2high = threshold;
					return;
				}
			}
		}		
	}
	
	
	/**
	 * set the possible lower deviation of average of the Brick, identified through given UID and Brick list
	 * @param BrickList
	 * @param uid
	 * @param threshold
	 */
	public static void setAvgLow(List<Brick>BrickList, String uid, double threshold, int index)
	{		
		// iterate through all existing Bricks
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid))
				{
					if (index == 1) tmpBrick.avg1low = threshold;
					if (index == 2) tmpBrick.avg2low = threshold;
					return;
				}
			
			// iterate through the subBrick list
			for (Iterator<Brick> i2 = tmpBrick.subBricks.iterator(); i2.hasNext();) 
			{
				Brick tmpBrick2 = i2.next();							
				if (tmpBrick2.uid.equals(uid))
				{
					if (index == 1) tmpBrick2.avg1low = threshold;
					if (index == 2) tmpBrick2.avg2low = threshold;
					return;
				}
			}
		}		
	}	
	
		
	/**
	 * set the threoshold of the Brick, identified through given UID and Brick list
	 * @param BrickList
	 * @param uid
	 * @param threshold
	 */
	public static void setThresholdMin1(List<Brick>BrickList, String uid, double threshold)
	{		
		// iterate through all existing Bricks
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid))
				{
					tmpBrick.tresholdMin1 = threshold;
					return;
				}
			
			// iterate through the subBrick list
			for (Iterator<Brick> i2 = tmpBrick.subBricks.iterator(); i2.hasNext();) 
			{
				Brick tmpBrick2 = i2.next();							
				if (tmpBrick2.uid.equals(uid))
					{
						tmpBrick2.tresholdMin1 = threshold;
						return;
					}
			}
		}		
	}
	
	/**
	 * set the threoshold of the Brick, identified through given UID and Brick list
	 * @param BrickList
	 * @param uid
	 * @param threshold
	 */
	public static void setThresholdMin2(List<Brick>BrickList, String uid, double threshold)
	{		
		// iterate through all existing Bricks
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid))
				{
					tmpBrick.tresholdMin2 = threshold;
					return;
				}
			
			// iterate through the subBrick list
			for (Iterator<Brick> i2 = tmpBrick.subBricks.iterator(); i2.hasNext();) 
			{
				Brick tmpBrick2 = i2.next();							
				if (tmpBrick2.uid.equals(uid))
					{
						tmpBrick2.tresholdMin2 = threshold;
						return;
					}
			}
		}		
	}

	/**
	 * set the threoshold of the Brick, identified through given UID and Brick list
	 * @param BrickList
	 * @param uid
	 * @param threshold
	 */
	public static void setThresholdMax1(List<Brick>BrickList, String uid, double threshold)
	{		
		// iterate through all existing Bricks
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid))
				{
					tmpBrick.tresholdMax1 = threshold;
					return;
				}
			
			// iterate through the subBrick list
			for (Iterator<Brick> i2 = tmpBrick.subBricks.iterator(); i2.hasNext();) 
			{
				Brick tmpBrick2 = i2.next();							
				if (tmpBrick2.uid.equals(uid))
					{
						tmpBrick2.tresholdMax1 = threshold;
						return;
					}
			}
		}		
	}

	/**
	 * set the threoshold of the Brick, identified through given UID and Brick list
	 * @param BrickList
	 * @param uid
	 * @param threshold
	 */
	public static void setThresholdMax2(List<Brick>BrickList, String uid, double threshold)
	{		
		// iterate through all existing Bricks
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid))
				{
					tmpBrick.tresholdMax2 = threshold;
					return;
				}
			
			// iterate through the subBrick list
			for (Iterator<Brick> i2 = tmpBrick.subBricks.iterator(); i2.hasNext();) 
			{
				Brick tmpBrick2 = i2.next();							
				if (tmpBrick2.uid.equals(uid))
					{
						tmpBrick2.tresholdMax2 = threshold;
						return;
					}
			}
		}		
	}

	
	/**
	 * searching in a given list of Bricks and in its subBrick-list for the given Brick
	 * @param BrickList
	 * @param uid
	 * @return	true if the item is already available in the list, false if not
	 */
	public static boolean BrickExists(List<Brick>BrickList, String uid)
	{
		// iterate through all existing Bricks
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid)) return true;
			
			// iterate through the subBrick list
			for (Iterator<Brick> i2 = tmpBrick.subBricks.iterator(); i2.hasNext();) 
			{
				Brick tmpBrick2 = i2.next();							
				if (tmpBrick2.uid.equals(uid)) return true;
			}
		}
		return false;
	}
			
	
	/**
	 * 
	 * @param uid UID of the Brick
	 * @return Brick item
	 */
	/*
	public static Brick getBrick(String uid)
	{
		// iterate through all existing Bricks
		for (Iterator<Brick> i = connectionData.BrickList.iterator(); i.hasNext();) 
		{			
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid)) return tmpBrick;
			
			// iterate through the subBrick list
			for (Iterator<Brick> i2 = tmpBrick.subBricks.iterator(); i2.hasNext();) 
			{
				Brick tmpBrick2 = i2.next();							
				if (tmpBrick2.uid.equals(uid)) return tmpBrick2;
			}
		}
		return null;	
	}
	*/
	
	/**
	 * returns Brick object by the given uid and Brick-list 
	 * @param BrickList	Brick-list containing the Brick
	 * @param uid uid of the Brick
	 * @return Brick object
	 */
	public static Brick getBrick(List<Brick>BrickList, String uid)
	{
		// iterate through all existing Bricks
		for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
		{			
			Brick tmpBrick = i.next();							
			if (tmpBrick.uid.equals(uid)) return tmpBrick;
			
			// iterate through the subBrick list
			for (Iterator<Brick> i2 = tmpBrick.subBricks.iterator(); i2.hasNext();) 
			{
				Brick tmpBrick2 = i2.next();							
				if (tmpBrick2.uid.equals(uid)) return tmpBrick2;
			}
		}
		return null;	
	}
	
			
	/**
	 * check when the given item was unchecked and vice-versa
	 * @param b Brick item
	 */
	public static void checkBrick(Brick b)
	{
		if (b != null)
		{
			b.checked1 = true;
		}
	}
			
	
	/**
	 * check when the given item was unchecked and vice-versa
	 * @param b Brick item
	 */
	public static void uncheckBrick(Brick b)
	{
		if (b != null)
		{
			b.checked1 = false;
		}
	}
	
	
	/**
	 * check when the given item was unchecked and vice-versa
	 * @param b Brick item
	 */
	public static void check2Brick(Brick b)
	{
		if (b != null)
		{
			b.checked2 = true;
		}
	}
	
	/**
	 * check or uncheck control by a template for a given bricks sensor 
	 * @param b	brick object
	 * @param index	index of the sensor
	 * @param on true if control is allowd, false otherwise
	 */
	public static void checkCtrlTemplate(Brick b, int index, boolean on)
	{
		if (b != null)
		{
			b.ctrlTmpl[index] = on;
		}
	}
	
	
	/**
	 * set simple control use case 
	 * @param b	brick object
	 * @param index	index of the sensor
	 * @param on true if control is allowd, false otherwise
	 */
	public static void setCtrlSimple(Brick b, int index, boolean on)
	{
		if (b != null)
		{
			b.ctrlSimple[index] = on;
		}
	}
	
	
	/**
	 * set path of the template for current brick
	 * @param b	brick object
	 * @param index	sensor index
	 * @param on	true = allow control
	 * @param path	path of the file
	 */
	public static void setCtrlTmplPath(Brick b, int index, boolean on, String path)
	{
		if (b != null)
		{
			b.ctrlTmpl[index] = on;
			b.ctrlTmplPath[index] = path;
			b.tmplPlot[index].readTemplateFromFile(path, true);
		}
	}
	
	
	
	/**
	 * check when the given item was unchecked and vice-versa
	 * @param b Brick item
	 */
	public static void checkAvrgCntrl(Brick b)
	{
		if (b != null)
		{			
			b.controlAverage = true;
		}
	}
	
	
	/**
	 * check when the given item was unchecked and vice-versa
	 * @param b Brick item
	 */
	public static void uncheckAvrgCntrl(Brick b)
	{
		if (b != null)
		{			
			b.controlAverage = false;
		}
	}
	
	/**
	 * check when the given item was unchecked and vice-versa
	 * @param b Brick item
	 */
	public static void checkAvrgCntrl2(Brick b)
	{
		if (b != null)
		{			
			b.controlAverage2 = true;
		}
	}
	
	
	/**
	 * check when the given item was unchecked and vice-versa
	 * @param b Brick item
	 */
	public static void uncheckAvrgCntrl2(Brick b)
	{
		if (b != null)
		{			
			b.controlAverage2 = false;
		}
	}
		
		
	/**
	 * check when the given item was unchecked and vice-versa
	 * @param b Brick item
	 */
	public static void uncheck2Brick(Brick b)
	{
		if (b != null)
		{
			b.checked2 = false;
		}
	}

	/**
	 * check when the given item was unchecked and vice-versa
	 * @param b Brick item
	 */
	public static void check3Brick(Brick b)
	{
		if (b != null)
		{
			b.checked3 = true;
		}
	}
			
	
	/**
	 * check when the given item was unchecked and vice-versa
	 * @param b Brick item
	 */
	public static void uncheck3Brick(Brick b)
	{
		if (b != null)
		{
			b.checked3 = false;
		}
	}

	public double getAvg1high() {
		return avg1high;
	}

	public void setAvg1high(double avg1high) {
		this.avg1high = avg1high;
	}

	public double getAvg2high() {
		return avg2high;
	}

	public void setAvg2high(double avg2high) {
		this.avg2high = avg2high;
	}

	public double getAvg1low() {
		return avg1low;
	}

	public void setAvg1low(double avg1low) {
		this.avg1low = avg1low;
	}

	public double getAvg2low() {
		return avg2low;
	}

	public void setAvg2low(double avg2low) {
		this.avg2low = avg2low;
	}
	
	public void setTmplWidth(double value, int index)
	{
		if (index == 0)
		{
			this.tmpl1Width = value;
		}
		if (index == 1)
		{
			this.tmpl2Width = value;
		}		
	}
			
}


