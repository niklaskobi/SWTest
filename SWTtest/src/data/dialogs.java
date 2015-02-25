package data;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class dialogs {
	
	public static void fileIOException(String path)
	{
		String message = "unable to write to the file: "
		        + path +"\n"
		        + "file not found or in use, writing to file will be disabled ";
		    JOptionPane.showMessageDialog(new JFrame(), message, "error",
		        JOptionPane.ERROR_MESSAGE);
	}
	
	public static void fileDataError(String str)
	{
		String message = "unable to read file: "
		        + str +"\n"
		        + "corrupt data in file";
		    JOptionPane.showMessageDialog(new JFrame(), message, "error",
		        JOptionPane.ERROR_MESSAGE);
	}
	
	public static void fileFNFException(String path)
	{
		String message = "unable to write to the file: "
		        + path +"\n"
		        + "file not found or in use, writing to file will be disabled ";
		    JOptionPane.showMessageDialog(new JFrame(), message, "error",
		        JOptionPane.ERROR_MESSAGE);
	}	
	
	public static void fileNotDefined()
	{
		String message = "no starage file defined, writing to file will be disabled\n";
		    JOptionPane.showMessageDialog(new JFrame(), message, "error",
		        JOptionPane.ERROR_MESSAGE);
	}	
	
	public static void unableToCreateDirectory(String dir)
	{
		String message = "unable to make directory: "+dir+"\n"
						 + "writing to file will be disabled";
		    JOptionPane.showMessageDialog(new JFrame(), message, "error",
		        JOptionPane.ERROR_MESSAGE);
	}	
	
	public static void unableToCreateFile(String path)
	{
		String message = "unable to create file: "+path
				 		+ "writing to file will be disabled";
		    JOptionPane.showMessageDialog(new JFrame(), message, "error",
		        JOptionPane.ERROR_MESSAGE);
	}
	
	public static void ipBrickDisconnected(String path)
	{
		String message = "disconnected from brick\n"+path;
		    JOptionPane.showMessageDialog(new JFrame(), message, "warning",
		        JOptionPane.ERROR_MESSAGE);
	}	

	public static void ipBrickConnected(String path)
	{
		String message = "reconnected to brick\n"+path;
		    JOptionPane.showMessageDialog(new JFrame(), message, "warning",
		        JOptionPane.ERROR_MESSAGE);
	}	

	
}
