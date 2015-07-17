package data;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * in this class all of the dialogs and warning are described.
 * @author Kv1
 *
 */
public class dialogs {
	
	/**
	 * File IO exception dialog
	 * @param path file
	 */
	public static void fileIOException(String path)
	{
		String message = "unable to write to the file: "
		        + path +"\n"
		        + "file not found or in use, data storage will be disabled";
		    JOptionPane.showMessageDialog(new JFrame(), message, "error",
		        JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Unable to read file dialog
	 * @param str file
	 */
	public static void fileDataError(String str)
	{
		String message = "unable to read file: "
		        + str +"\n"
		        + "corrupt data in file";
		    JOptionPane.showMessageDialog(new JFrame(), message, "error",
		        JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * File not found or in use dialog
	 * @param path file
	 */	
	public static void fileFNFException(String path)
	{
		String message = "unable to write to the file: "
		        + path +"\n"
		        + "file not found or in use, writing to file will be disabled ";
		    JOptionPane.showMessageDialog(new JFrame(), message, "error",
		        JOptionPane.ERROR_MESSAGE);
	}	
	
	
	/**
	 * No file definition dialog
	 */
	public static void fileNotDefined()
	{
		String message = "no starage file defined, writing to file will be disabled\n";
		    JOptionPane.showMessageDialog(new JFrame(), message, "error",
		        JOptionPane.ERROR_MESSAGE);
	}	
	
	
	/**
	 * Unable to create directory dialog
	 * @param dir path
	 */
	public static void unableToCreateDirectory(String dir)
	{
		String message = "unable to make directory: "+dir+"\n"
						 + "writing to file will be disabled";
		    JOptionPane.showMessageDialog(new JFrame(), message, "error",
		        JOptionPane.ERROR_MESSAGE);
	}	
	
	/**
	 * unable to creat efile dialog
	 * @param path
	 */
	public static void unableToCreateFile(String path)
	{
		String message = "unable to create file: "+path
				 		+ "writing to file will be disabled";
		    JOptionPane.showMessageDialog(new JFrame(), message, "error",
		        JOptionPane.ERROR_MESSAGE);
	}
	
	
	/**
	 * brick disconnetion warning
	 * @param path brick name
	 */
	public static void ipBrickDisconnected(String path)
	{
		String message = "disconnected from brick\n"+path;
		    JOptionPane.showMessageDialog(new JFrame(), message, "warning",
		        JOptionPane.ERROR_MESSAGE);
	}	

	/**
	 * reconned to brick warning
	 * @param path
	 */
	public static void ipBrickConnected(String path)
	{
		String message = "reconnected to brick\n"+path;
		    JOptionPane.showMessageDialog(new JFrame(), message, "warning",
		        JOptionPane.ERROR_MESSAGE);
	}	

	
}
