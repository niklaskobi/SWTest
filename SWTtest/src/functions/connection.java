package functions;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;

import objects.Brick;

import org.eclipse.swt.widgets.Display;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.BrickletVoltageCurrent;
import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.ConnectedListener;
import com.tinkerforge.IPConnection.DisconnectedListener;
import com.tinkerforge.IPConnection.EnumerateListener;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import data.connectionData;
import windows.mainWindow;

/**
 * this class describes methods and listeners for connection with the tinkerForge bricklet and its configuration.
 * @author Kv1
 *
 */
public class connection {
	
	/**
	 * is set to true as a tinkerForge device is connected
	 */
	public static boolean connectionAvalaible = false;
	
	/**
	 * create a new ip connection (IPConnection class is a part of the TinkerForge library)
	 */
	public static IPConnection ipcon = new IPConnection(); 		// Create IP connection	
	
	
	//create a connection listener
	public static ConnectedListener connectedListener = new ConnectedListener()
	{
		@Override
		public void connected(short connectReason) 
		{
			if (connectReason == IPConnection.CONNECT_REASON_AUTO_RECONNECT)
			{
				data.dialogs.ipBrickConnected(ipcon.toString());
			}
			
			if (connectReason == IPConnection.CALLBACK_CONNECTED)
			{
				System.out.println("Brick connected !!!");
			}
		}
	};
	
	// create a disconnection listener
	public static DisconnectedListener disconnectedListener = new DisconnectedListener()
	{
		@Override
		public void disconnected(short disconnectReason) 
		{
			System.out.println("DISCONNECT");
			if (disconnectReason != IPConnection.DISCONNECT_REASON_REQUEST)
			data.dialogs.ipBrickDisconnected(ipcon.toString());
			
			if (disconnectReason == IPConnection.DISCONNECT_REASON_REQUEST)
				System.out.println("Brick desconnected on request");
		}		
	};
	
	// create a enumerate listener
	public static EnumerateListener enumListener = new EnumerateListener() 
	{
		public void enumerate(String uid, String connectedUid, char position,
                  short[] hardwareVersion, short[] firmwareVersion,
                  int deviceIdentifier, short enumerationType) 
		{
			functions.Events.deviceConnected();
			Brick b = new Brick();
			b.setUid(uid);
			b.setEnumerationType(enumerationType);
			b.setPosition(position);					
		
			if(enumerationType == IPConnection.ENUMERATION_TYPE_DISCONNECTED)
			{
				Brick.addBrick(connectionData.getBrickList(),b);
				mainWindow.newBrickAdded(connectionData.getBrickList());
				System.out.println("unrequested disconnect !");
				functions.Events.unrequestedDisconnect(uid);
				return;
			}
			b.setConnectedUid(connectedUid);
			b.setHardwareVersion(hardwareVersion);
			b.setFirmwareVersion(firmwareVersion);
			b.setDeviceIdentifier(deviceIdentifier);
			b.setDefaultTreshold(deviceIdentifier);

			//	Brick.addBrick(BrickList,b);
			Brick.addBrick(connectionData.getBrickList(),b);
			mainWindow.newBrickAdded(connectionData.getBrickList());				
		}
	};
	
	
	/**
	 * trying to connect to given port and host 
	 * @param host
	 * @param port
	 * @return	0:	state disconnected
	 * 			1:	state connected
	 * 			2:	state pending 
	 * @throws UnknownHostException
	 * @throws AlreadyConnectedException
	 * @throws IOException
	 * @throws NotConnectedException
	 */
	public static int connect(String host, int port) throws UnknownHostException, AlreadyConnectedException, IOException, NotConnectedException
	{
		System.out.println("connection state: "+getConnectionState());
		System.out.println("_________________________connecting");			
		ipcon.connect(host, port);
		ipcon.enumerate();
		System.out.println("connection state: "+getConnectionState());
		return getConnectionState();
	}

	
	public static void stopAllSensors()
	{
		// iterate through the subBrick list
		for (Iterator<Brick> i2 = connectionData.presentedBrickList.iterator(); i2.hasNext();) 
		{				
			Brick tmpSubBrick = i2.next();
			
			// ambient light listener
			if (tmpSubBrick.deviceIdentifier == 21 && tmpSubBrick.checked1)
			{
				stopIlluminancelistener(tmpSubBrick.uid);
			}
			// voltage listener
			if (tmpSubBrick.deviceIdentifier == 227 && tmpSubBrick.checked2)
			{
				stopVoltagelistener(tmpSubBrick.uid);
			}
			// ampere listener 
			if (tmpSubBrick.deviceIdentifier == 227 && tmpSubBrick.checked3)
			{					
				stopAmperelistener(tmpSubBrick.uid);
			}			
		}
	}
	

	public static void startAllSensors() throws TimeoutException, NotConnectedException
	{
		// iterate through the subBrick list
		for (Iterator<Brick> i2 = connectionData.presentedBrickList.iterator(); i2.hasNext();) 
		{				
			Brick tmpSubBrick = i2.next();
			
			// ambient light listener
			if (tmpSubBrick.deviceIdentifier == 21 && tmpSubBrick.checked1)
			{
				startIlluminanceListener(tmpSubBrick.uid);
			}
				// voltage listener
			if (tmpSubBrick.deviceIdentifier == 227 && tmpSubBrick.checked2)
			{
				startCurrentListener(tmpSubBrick.uid,0);
			}
				// ampere listener 
			if (tmpSubBrick.deviceIdentifier == 227 && tmpSubBrick.checked3)
			{					
				startCurrentListener(tmpSubBrick.uid,1);
			}
			
			// open sensor window if its not already opened				
			//windowController.openSensorWindow();
		}
		// open sensor window if its not already opened				
		//windowController.openSensorWindow();
		
	}		
	
	
	
	/**
	 * disconnect form the Brick
	 * @throws UnknownHostException
	 * @throws AlreadyConnectedException
	 * @throws IOException
	 * @throws NotConnectedException
	 */
	public static void disconnect() throws UnknownHostException, AlreadyConnectedException, IOException, NotConnectedException
	{
		System.out.println("_________________________disconnecting");
		ipcon.disconnect();
		
		//remove connect and disconnect listeners
		ipcon.removeConnectedListener(connectedListener);
		ipcon.removeDisconnectedListener(disconnectedListener);
		ipcon.removeEnumerateListener(enumListener);
	}	
	
	
	
	/**
	 * returns the state of the connection
	 * @return	2: 	disconnected
	 * 			1:	pending (trying to reconnect)
	 * 			0:	connected
	 */
	public static int getConnectionState()
	{
		if (ipcon.getConnectionState() == IPConnection.CONNECTION_STATE_DISCONNECTED) 	return 2;
		if (ipcon.getConnectionState() == IPConnection.CONNECTION_STATE_PENDING) 		return 1;
		if (ipcon.getConnectionState() == IPConnection.CONNECTION_STATE_CONNECTED) 		return 0;
		return -1;
	}

	
			
	/**
	 * Registers enumerate listener and adds new Brick informations
	 */
	private static void registerEnumListener()
	{ 
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{    
				// add enumerate listener
				ipcon.addEnumerateListener(enumListener);				
				//add disconnect listener
				ipcon.addDisconnectedListener(disconnectedListener);
				//add connect listener
				ipcon.addConnectedListener(connectedListener);				
		 }});	     	    		
	}
	
		
	/**
	 * stop listener with the given UID
	 * @param UID	uid of the sensor
	 * @param index only for voltage/ampere (0 = voltage, 1 = ampere)
	 */
	public static void stopListener(String UID)
	{
		// find out the type of the sensor
		int sensorType = Brick.getBrick(connectionData.BrickList, UID).deviceIdentifier;
		Brick br = Brick.getBrick(connectionData.BrickList, UID);
		// if its ambient light sensor
		if (sensorType == data.constants.brickIds.BRICK_AMLIGHT.getId())
		{
			stopIlluminancelistener(UID);
		}
		// if it is voltage/ampere sensor
		if (sensorType == data.constants.brickIds.BRICK_VOLTAGE.getId())
		{
			if (! br.checked2) stopVoltagelistener(UID);
			if (! br.checked3) stopAmperelistener(UID);
		}		
	}
	
		
	/**
	 * stop listener with the given UID
	 * @param UID	uid of the sensor
	 * @param index only for voltage/ampere (0 = voltage, 1 = ampere)
	 * @throws NotConnectedException 
	 * @throws TimeoutException 
	 */
	public static void startListener(String UID) throws TimeoutException, NotConnectedException
	{
		// find out the type of the sensor
		int sensorType = Brick.getBrick(connectionData.BrickList, UID).deviceIdentifier;
		Brick br = Brick.getBrick(connectionData.BrickList, UID);
		// if its ambient light sensor
		if (sensorType == data.constants.brickIds.BRICK_AMLIGHT.getId())
		{
			startIlluminanceListener(UID);
		}		
		// if it is voltage/ampere sensor
		if (sensorType == data.constants.brickIds.BRICK_VOLTAGE.getId())
		{
			if (br.checked2) startCurrentListener(UID, 0);			
			if (br.checked3) startCurrentListener(UID, 1);
		}		
	}	
	
	
	
	public static void stopIlluminancelistener(final String UID)
	{
		BrickletAmbientLight al = new BrickletAmbientLight(UID, ipcon); // Create device object
		
		try {
			al.setIlluminanceCallbackPeriod(0);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}
	

	
	public static void stopVoltagelistener(final String UID)
	{
		BrickletVoltageCurrent vc = new BrickletVoltageCurrent(UID, ipcon); // Create device object
		
		try {
			vc.setVoltageCallbackPeriod(0);
		} catch (TimeoutException | NotConnectedException e) {
			System.out.println("nothing to stop");
		}
	}	
	
	
	
	public static void stopAmperelistener(final String UID)
	{
		BrickletVoltageCurrent vc = new BrickletVoltageCurrent(UID, ipcon); // Create device object
		
		try {
			vc.setCurrentCallbackPeriod(0);
		} catch (TimeoutException | NotConnectedException e) {
			System.out.println("nothing to stop");
		}
	}
	
	
	
	/**
	 * Add and implement illuminance listener (called if illuminance changes)
	 * @param UID
	 * @throws TimeoutException
	 * @throws NotConnectedException
	 */
	public static void startIlluminanceListener(final String UID) throws TimeoutException, NotConnectedException
	{
		// 	Don't use device before ipcon is connected
		BrickletAmbientLight al = new BrickletAmbientLight(UID, ipcon); // Create device object
		
		// 	Set Period for illuminance callback to 1s (1000ms)
		// 	Note: The illuminance callback is only called every second if the 
		//  illuminance has changed since the last call!
		
		al.setIlluminanceCallbackPeriod(data.constants.updateFrequency);
		
		// Add and implement illuminance listener (called if illuminance changes)
		al.addIlluminanceListener(new BrickletAmbientLight.IlluminanceListener() 
		{
			public void illuminance(int illuminance) 
			{
				double il = illuminance / 10.0; 
				//System.out.println("Illuminance: " + il + " Lux");
				//windowController.demo.addValue(0, il);
				if (functions.windowController.existsSensorWindow) windowController.sensorWindow.addValue(UID, il,0);
				Brick.setLastValue(connectionData.BrickList, UID, il);
				Brick.setLastValue(connectionData.presentedBrickList, UID, il);
				
				// add entry to the file
				if (data.connectionData.storeData == true)
				{
					functions.file.addEntryToFile(UID, il,0);
				}
			}
		});
	}
			
	
	/**
	 * starts voltage and ampere listener
	 * 
	 * Don't use device before ipcon is connected 
	 * Set Period for current callback to 1s (1000ms)
	 * Note: The current callback is only called every second if the 
	 * current has changed since the last call!
	 * @param UID
	 * @param type 0 = voltage, 1 = ampere
	 * @throws TimeoutException
	 * @throws NotConnectedException
	 * 
	 */
	public static void startCurrentListener(final String UID, int type) throws TimeoutException, NotConnectedException
	{
		BrickletVoltageCurrent vc = new BrickletVoltageCurrent(UID, ipcon); // Create device object
        // Don't use device before ipcon is connected
        // Set Period for current callback to 1s (1000ms)
        // Note: The current callback is only called every second if the 
        // current has changed since the last call!
		
		//--- voltage listener ---------------------------------------		
		vc.setVoltageCallbackPeriod(data.constants.updateFrequency);
		
        // Add and implement voltage listener (called if voltage changes)
        vc.addVoltageListener(new BrickletVoltageCurrent.VoltageListener() {
            public void voltage(int voltage) {
            	double volt = voltage/1000.0;
                //System.out.println("Voltage: " + volt + " V");                
                if (functions.windowController.existsSensorWindow) windowController.sensorWindow.addValue(UID, volt,0);
				Brick.setLastValue(connectionData.BrickList, UID, volt);
				Brick.setLastValue(connectionData.presentedBrickList, UID, volt);

				// add entry to the file
				if (data.connectionData.storeData == true)
				{
					functions.file.addEntryToFile(UID, volt,0);
				}				
            }
        });        		
		//--- ampere listener ---------------------------------------
        vc.setCurrentCallbackPeriod(data.constants.updateFrequency);
        
        // Add and implement voltage listener (called if voltage changes)
        vc.addCurrentListener(new BrickletVoltageCurrent.CurrentListener() {
            public void current(int current) {
            	double cur = current/1000.0;
                //System.out.println("Current: " + cur + " A");
                if (functions.windowController.existsSensorWindow) windowController.sensorWindow.add2ndValue(UID, cur);
				Brick.setLastValue(connectionData.BrickList, UID, cur);
				Brick.setLastValue(connectionData.presentedBrickList, UID, cur);
                
				// add entry to the file
				if (data.connectionData.storeData == true)
				{
					functions.file.addEntryToFile(UID, cur,1);
				}				
            }
        });                       
	}
	
	
	/**
	 * initialize connection
	 */
	public static void initConnections()
	{
		registerEnumListener();
	}

}


