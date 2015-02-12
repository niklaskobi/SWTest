package windows;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

import com.tinkerforge.NotConnectedException;

import functions.connection;

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     * @throws IOException 
     * @throws AlreadyConnectedException 
     * @throws UnknownHostException 
     * @throws NotConnectedException 
     * @throws TimeoutException 
     */
public class main{
		
    public static void main(final String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, NotConnectedException, IOException
    {	
    	// set style
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    	// init constants
    	data.constants.initConstants();       	

    	// open main window
    	final mainWindow mw1 = new mainWindow();
    	mw1.open();
    }  
}