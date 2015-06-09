package windows;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import data.connectionData;
import data.constants;
import functions.connection;
import functions.windowController;

import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;

import objects.Brick;

import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.jface.viewers.ComboViewer;

public class mainWindow {
	protected static Shell shell;
	private final static FormToolkit formToolkit = new FormToolkit(Display.getDefault());

	// setting region values
	final static String USECASE_SIMPLE = "simple";
	final static String USECASE_AVERAGE = "average";
	final static String USECASE_TEMPLATE = "template";
	static String[] useCases = { USECASE_SIMPLE, USECASE_AVERAGE, USECASE_TEMPLATE};
	static int width_one_half;
	static int width_one_fourth;
	static int heightSum;
	
	static boolean updateSettingTabPls = false;
	
	// tree items
	CheckboxTreeViewer treeViewer;
	static Tree tree;
	static Group group;
	static TreeItem treeItem;
	
	// dynamic menu items
	static MenuItem mntmCreateFile;
	static MenuItem mntmSettings;
	static MenuItem mntmStoreToFile;
	static MenuItem mntmOpenTemplateWindow;
	static Button btnNewButton_1;
	static Button btnNewButton;
	static Text text;
	static Text txtLocalhost;

	// combobox for usecase choice
    static String[] ids = { "north", "west", "south", "east" };
    static Boolean[] values =
    {
        Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE
    };
    static CheckComboStore[] stores = new CheckComboStore[ids.length];
	
	// flags
	static boolean buttonConnect = true;	
	static boolean buttonStart = true;
	public static boolean allowDataStore = false;		// !!! must be equal with storeData in data.connectionData
	
	// events
	CheckStateChangedEvent event;
	
	// window coordinates statics
	static int shellHeight;
	static int shellStartHeight;
	static int shellStartWidth;
	static int settingRegionHeight; 
	static int settingRegionWidth;
	static final int settingRegionStartY 				= 5;
	static int settingRegionStartX;
	static int settingOffsetX 							= 10;
	static final int settingRegionSeparateWidth 		= 5;
	static final int settingRegionSeparateHeight 		= 10;
	static final int settingRegionLineHeight 			= 20;
	static final int settingRegionBorderLineHeight		= 20;
	static final int settingRegionBetweenLineHeight 	= 10;
	static final int settingRegionSeparateLineHeight 	= 1;
	static final int textFieldExtention 				= 3;
	static final int templateControlOffset				= -12;
	
	/**
	 * Launch the application.
	 * 
	 * @param args
	 * @throws NotConnectedException
	 * @throws TimeoutException
	 */
	public static void main(String[] args) throws TimeoutException,
			NotConnectedException 
	{
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					mainWindow window = new mainWindow();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}		
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		
		//close event
		shell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  System.out.println("closing");
					windowController.closeSensorWindow();
					connection.stopAllSensors();
					try {
						connection.disconnect();
					} catch (AlreadyConnectedException | NotConnectedException
							| IOException e) {						
						//e.printStackTrace();
						System.out.println("connections cant be closed");
					}
		      }
		    });

		while (!shell.isDisposed()) {
			if (updateSettingTabPls == true)
			{
				updateSettingTabs();
				updateSettingTabPls = false;
			}
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}		
	}

	/**
	 * Create contents of the window.
	 */
	public void createContents() {		
		
		shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shell.setSize(481, 442);
		shellStartWidth = shell.getSize().x;
		shellHeight = shell.getSize().y;
		shell.setText("DemoTable Application");		

		Label lblPort = new Label(shell, SWT.NONE);
		lblPort.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblPort.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		lblPort.setBounds(25, 13, 39, 24);
		lblPort.setText("port:");
		
		settingRegionStartX = lblPort.getBounds().x;

		text = new Text(shell, SWT.BORDER);
		text.setText("4223");
		text.setBounds(79, 10, 76, 27);
		
		Label lblHost = new Label(shell, SWT.NONE);
		lblHost.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblHost.setBounds(25, 43, 39, 27);
		lblHost.setText("host:");
		
		txtLocalhost = new Text(shell, SWT.BORDER);
		txtLocalhost.setText("localhost");
		txtLocalhost.setBounds(79, 43, 76, 27);			
		

		group = new Group(shell, SWT.NONE);
		group.setVisible(false);
		/*
		group.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		group.setBounds(10, 230, 414, 15);
		*/
		
		//START BUTTON-------------------------------------------
		btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {				
				try {
					if (buttonStart == true)
					{
						functions.Events.startButtonClicked();
					}
					else
					{
						functions.Events.stopButtonClicked();
					}
				} catch (TimeoutException | NotConnectedException e1) {
					e1.printStackTrace();
				}				
				//btnNewButton_1.setEnabled(false);
			}
		});
		btnNewButton_1.setEnabled(false);
		btnNewButton_1.setBounds(320, 13, 119, 56);
		if (buttonStart == true) btnNewButton_1.setText("Start");
		else btnNewButton_1.setText("Stop");
		//------------------------------------------------------
		
		//CONNECT BUTTON------------------------------------------------
		btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (buttonConnect == true) 
				{					
					//connect				
					System.out.println("trying to connect to:");
					System.out.println("host : " + text.getText());
					System.out.println("port : " + txtLocalhost.getText());
					try 
					{
				    	// init connections
				    	connection.initConnections();      							
						connection.connect(txtLocalhost.getText(), Integer.parseInt(text.getText()));
						superviseConnection();
					} 
					catch (NumberFormatException | AlreadyConnectedException | NotConnectedException | IOException e1) 
					{						
						e1.printStackTrace();
						System.out.println("cant connect!");
						return;
					}
				}								    
				else
				{
					//disconnect
					deviceDisconnected();
				}}});
		btnNewButton.setBounds(178, 13, 121, 56);
		btnNewButton.setText("Connect");		
		// ---------------------------------------------------------------------------------

		// TREE ----------------------------------------------------------------------------		
		treeViewer = new CheckboxTreeViewer(shell, SWT.BORDER);
		//treeViewer = new CheckboxTreeViewer(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		//treeViewer.setContentProvider(new FeaturePropertyDialogContentProvider());
		treeViewer.setAutoExpandLevel(3);
		tree = treeViewer.getTree();
		tree.addListener(SWT.Selection, new Listener () {
			public void handleEvent (Event event){
			    if (event.detail == SWT.CHECK){
			    	System.out.println(((TreeItem) event.item).getText(1) +" checked");
			    	itemChecked(((TreeItem) event.item).getText(1));
		}}});
		tree.setBounds(25, 88, 414, 266);									
		formToolkit.paintBordersFor(tree);
		tree.setHeaderVisible(true);
		TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
		column1.setText("device");
		column1.setWidth(200);
		TreeColumn column2 = new TreeColumn(tree, SWT.CENTER);
		column2.setText("UID");
		column2.setWidth(100);
		TreeColumn column3 = new TreeColumn(tree, SWT.RIGHT);
		column3.setText("status");
		column3.setWidth(110);
		TreeColumn column4 = new TreeColumn(tree, SWT.RIGHT);
		column4.setText("firmware vers.");
		column4.setWidth(70);
		TreeColumn column5 = new TreeColumn(tree, SWT.RIGHT);
		column5.setText("hardware vers.");
		column5.setWidth(70);	
		
		shellStartHeight = tree.getBounds().height+tree.getBounds().y;
		settingRegionWidth = tree.getBounds().width;
				
	    tree.addListener(SWT.Expand, new Listener() {
	        public void handleEvent(Event e) {
	          System.out.println("Expand={" + e.item + "}");
	          treeItem.setExpanded(true);
	        }
	      });	    
	    tree.addListener(SWT.Collapse, new Listener() {
	        public void handleEvent(Event e) {
	          System.out.println("Collapse={" + e.item + "}");
	          treeItem.setExpanded(false);
	        }
	      });
		// ---------------------------------------------------------------------------------		
								
		
		// MENU -----------------------------------------------------------------------------		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		// ---------------------------------------------------------------------------------

		// ALLOW STORAGE RADIO -------------------------------------------------------------
		mntmOpenTemplateWindow = new MenuItem(menu_1, SWT.NONE);
		mntmOpenTemplateWindow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				// open file dialog
	        	JFrame parentFrame = new JFrame();           	 
	        	JFileChooser fileChooser = new JFileChooser();
	        	FileNameExtensionFilter tmpfilter = new FileNameExtensionFilter("template files", objects.TemplatePlot.fileExtention);
	        	fileChooser.setFileFilter(tmpfilter);
	        	fileChooser.setDialogTitle("Specify a template file");
	        	int userSelection = fileChooser.showOpenDialog(parentFrame);
	        	if (userSelection == JFileChooser.APPROVE_OPTION)
	        	{
	        	    File fileToOpen = fileChooser.getSelectedFile();
	        	    System.out.println("Open file: " + fileToOpen.getAbsolutePath());
	        	    if (objects.TemplatePlot.isTemplateValid(fileToOpen.getAbsolutePath()))        	    
	        	    {
	        	    	functions.Events.openTemplatePlotWindow(fileToOpen.getAbsolutePath(),0);       	    	
	        	    }
	        	    else 
	        	    {
	        	    	data.dialogs.fileDataError(fileToOpen.getAbsolutePath());	        	    	        	    
	        	    }
	        	}
	        	// end file dialog
	
			}
		});	
		mntmOpenTemplateWindow.setSelection(allowDataStore);
		mntmOpenTemplateWindow.setText("open a template plot");				
		// ---------------------------------------------------------------------------------
		
		// SEPARATOR -----------------------------------------------------------------------		
		new MenuItem(menu_1, SWT.SEPARATOR);
		// ---------------------------------------------------------------------------------		
		
		// ALLOW STORAGE RADIO -------------------------------------------------------------
		mntmStoreToFile = new MenuItem(menu_1, SWT.RADIO);
		mntmStoreToFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (allowDataStore == false)
				{
					functions.Events.allowFileStorage();
				}
				else 
				{
					functions.Events.forbidFileStorage();
				}					
			}
		});	
		mntmStoreToFile.setSelection(allowDataStore);
		mntmStoreToFile.setText("enable data storage");				
		
		// CREATE FILE----------------------------------------------------------------------		
		//final MenuItem mntmCreateFile = new MenuItem(menu_1, SWT.NONE);
		mntmCreateFile = new MenuItem(menu_1, SWT.NONE);
		mntmCreateFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				//dialog.setFilterPath("");
				dialog.setFilterExtensions(new String[] { "*.csv"});
				data.connectionData.setStorageFilePath(dialog.open());				
				System.out.println("save under file : "+data.connectionData.getStorageFilePath());
				functions.file.createNewFile(data.connectionData.getStorageFilePath());
			}
		});
		
		mntmCreateFile.setText("create file");
		mntmCreateFile.setEnabled(allowDataStore);
		// ---------------------------------------------------------------------------------
		
		// CHOOSE FILE----------------------------------------------------------------------
		mntmSettings = new MenuItem(menu_1, SWT.OPEN);
		mntmSettings.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.NONE);
				//dialog.setFilterPath("");
				dialog.setFilterExtensions(new String[] { "*.csv"});
				data.connectionData.setStorageFilePath(dialog.open());				
				System.out.println("file : "+data.connectionData.getStorageFilePath());
			}
		});
		mntmSettings.setText("choose file");
		mntmSettings.setEnabled(allowDataStore);
		// ---------------------------------------------------------------------------------		

		// SEPARATOR -----------------------------------------------------------------------		
		new MenuItem(menu_1, SWT.SEPARATOR);
		// ---------------------------------------------------------------------------------		
				
		// CLOSE ---------------------------------------------------------------------------		
		MenuItem mntmClose = new MenuItem(menu_1, SWT.NONE);
		mntmClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				windowController.closeSensorWindow();
				connection.stopAllSensors();
				shell.close();
			}
		});
		mntmClose.setText("close");
		// ---------------------------------------------------------------------------------			
				
		/*
		Button button = new Button(shell, SWT.NONE);
		button.setBounds(-11, -27, 75, 25);
		formToolkit.adapt(button, true, true);
		button.setText("New Button");
		*/

	}
	
	/**
	 * supervise and handle the connection setup
	 */
	private static void superviseConnection() 
	{
		new Thread(new Runnable() {
			public void run() 
			{
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						text.setEnabled(false);
						txtLocalhost.setEnabled(false);
						btnNewButton.setEnabled(false);
						int cnt = 0;
						while (cnt < 10) {
							try 
							{
								Thread.sleep(100);
							} 
							catch (Exception e) 
							{
							}
							btnNewButton.setText("Connecting.. (" + cnt + ")");
							if (connection.connectionAvalaible == true) break;
							System.out.println("superviser: " + cnt);
							cnt++;
						}
						text.setEnabled(true);
						txtLocalhost.setEnabled(true);
						btnNewButton.setEnabled(true);
						
						if (connection.connectionAvalaible == false) {
							btnNewButton.setText("Connect");
							try {
								connection.disconnect();
							} catch (AlreadyConnectedException | NotConnectedException
									| IOException e) {
								e.printStackTrace();
							}
						}						
					}
				});
		 }}).start();
	}
			
	public static void forbidFileStorage() 
	{
		new Thread(new Runnable() {
		      public void run() {
		         //while (true) {
		            try { Thread.sleep(200); } catch (Exception e) { }
		            Display.getDefault().asyncExec(new Runnable() {
		               public void run() 
		               {
		            	   mntmSettings.setEnabled(false);
		            	   mntmStoreToFile.setSelection(false);
		            	   mntmCreateFile.setEnabled(false);
		            	   allowDataStore = false;
		               }
		});}}).start();
	}
		
	/**
	 * set the boolean corresponding to availability of any connection
	 */
	public static void deviceConnected()
	{
		new Thread(new Runnable() {
		      public void run() {
		         //while (true) {
		            try { Thread.sleep(200); } catch (Exception e) { }
		            Display.getDefault().asyncExec(new Runnable() {
		               public void run() 
		               {
		            	//connection.connectionAvalaible = true;
		        		System.out.println("success");
		        		// lblNewLabel.setText("connected to : ");
		        		btnNewButton.setText("Disconnect");
		        		text.setEnabled(false);
		        		txtLocalhost.setEnabled(false);
		        		buttonConnect = false;
		        		//btnNewButton_1.setEnabled(true);
		               }
		});}}).start();
	}
		
	/**
	 * handle a disconnect event
	 * set the boolean corresponding to availability of any connection
	 */
	public static void deviceDisconnected()
	{		
		new Thread(new Runnable() {
		      public void run() {
		         //while (true) {
		            try { Thread.sleep(200); } catch (Exception e) { }
		            Display.getDefault().asyncExec(new Runnable() {
		               public void run() 
		               {
		            	   	//connection.connectionAvalaible = false;
		           			buttonConnect = true;
		           			btnNewButton.setText("Connect");
		           			text.setEnabled(true);
		           			txtLocalhost.setEnabled(true);
		           			tree.removeAll();					
		           			updateSettingTabs();
		           			functions.Events.disconnectDevice();
		           			buttonStart = true;
		           			btnNewButton_1.setEnabled(false);
		           			btnNewButton_1.setText("Start");
		        		}
		});}}).start();		
	}
			
	public static void allowFileStorage() 
	{
		new Thread(new Runnable() {
		      public void run() {
		      //   while (true) {
		            try { Thread.sleep(200); } catch (Exception e) { }
		            Display.getDefault().asyncExec(new Runnable() {
		               public void run() 
		               {
		            	   mntmSettings.setEnabled(true);
		            	   mntmStoreToFile.setSelection(true);
		            	   mntmCreateFile.setEnabled(true);
		            	   allowDataStore = true;	
		               }});}
		   }).start();
	}		
		
	
	/**
	 * sensor name + uid
	 * @param i
	 * @param lineNumber
	 */
	public static void settingsMenu_addName(int i, int lineNumber)
	{
		Label lblBricketuid = new Label(group, SWT.NONE);
		lblBricketuid.setBounds(settingOffsetX, 
								settingRegionBorderLineHeight+heightSum+settingRegionLineHeight*lineNumber, 
								width_one_half, 
								settingRegionLineHeight);
		lblBricketuid.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		//formToolkit.adapt(lblBricketuid, true, true);
		lblBricketuid.setText(String.valueOf(constants.brickIdMap.get( connectionData.presentedBrickList.get(i).getDeviceIdentifier()))
								+ " (" + connectionData.presentedBrickList.get(i).uid +")");
	}
	
	
	public static void settingMenu_addSimpleCheckbox(Brick tmpBr, int lineNumber, int quant, int index)
	{
		final String tmpStr = tmpBr.uid;
		Button btnCheckButton = new Button(group, SWT.CHECK);
		btnCheckButton.setBounds(settingOffsetX, 
								 settingRegionBorderLineHeight+settingRegionBetweenLineHeight*lineNumber
								 +settingRegionLineHeight*lineNumber+heightSum, 
								 width_one_fourth, 
								 settingRegionLineHeight);
		btnCheckButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		//btnCheckButton.setText(USECASE_SIMPLE+String.valueOf(constants.brickUnitMap.get( connectionData.presentedBrickList.get(i).getDeviceIdentifier())));
		btnCheckButton.setText(USECASE_SIMPLE);
		btnCheckButton.setSelection(tmpBr.ctrlSimple[0]);
		btnCheckButton.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetSelected(SelectionEvent e) {						
				simpleControlChecked(tmpStr, 0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {				
				
			}			
		});
		
		if (quant == 2)
		{
			Button btnCheckButton2 = new Button(group, SWT.CHECK);
			btnCheckButton2.setBounds(settingOffsetX+settingRegionWidth/2, 
								  settingRegionBorderLineHeight+settingRegionLineHeight*lineNumber
								  +settingRegionBetweenLineHeight*lineNumber+heightSum,  
								  width_one_fourth, 
								  settingRegionLineHeight);
			btnCheckButton2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			//btnCheckButton2.setText(USECASE_SIMPLE+String.valueOf(constants.brickUnitMap.get( connectionData.presentedBrickList.get(i).getDeviceIdentifier())));
			btnCheckButton2.setText(USECASE_SIMPLE);
			btnCheckButton2.setSelection(tmpBr.ctrlSimple[1]);
			btnCheckButton2.addSelectionListener(new SelectionListener(){
				@Override
				public void widgetSelected(SelectionEvent e) {						
					simpleControlChecked(tmpStr, 1);
				}
	
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {					
					
				}
			});
		}		
	}
			
	/**
	 * settings region for simple use case: simple
	 * @param tmpBr			brick object
	 * @param lineNumber	
	 * @param quant 		number of sensors
	 */
	public static void settingMenu_addSimpleControls(Brick tmpBr, int lineNumber, int quant, int index)
	{
		final Brick updBr = tmpBr;
		

		if (tmpBr.ctrlSimple[0] == true)
		{
		// --------------------------------------------------------------------------
		// min label 1
		Label lblMin1 = new Label(group, SWT.NONE);
		lblMin1.setBounds(settingOffsetX, 
							  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
							  + settingRegionBetweenLineHeight*(lineNumber+1)+heightSum, 
							  width_one_fourth/3, 
							  settingRegionLineHeight);
		lblMin1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMin1.setText("min ");
		
		// text field
		final Text txtL1min = new Text(group, SWT.BORDER);
		int min = (int)((double)constants.brickMinValue.get(connectionData.presentedBrickList.get(index).getDeviceIdentifier())) ;						
		txtL1min.setText(""+min);
		txtL1min.setBounds(lblMin1.getBounds().x+lblMin1.getBounds().width+settingRegionSeparateWidth,
							settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
							+ settingRegionBetweenLineHeight*(lineNumber+1)+heightSum-textFieldExtention, 
						   width_one_fourth/2,
						   settingRegionLineHeight+textFieldExtention);
		
		txtL1min.addListener(SWT.FocusOut , new Listener() {
		      public void handleEvent(Event event) {
	    		  double perspectiveValue = 0;
	    		  boolean update = true;
		    	  try 
		    	  {				 
		    		  perspectiveValue = Double.parseDouble(txtL1min.getText());
		    	  }
		    	  catch (NumberFormatException e)
		    	  {
		    		  txtL1min.setText(Double.toString( Brick.getThresholdMin1(connectionData.BrickList, updBr.uid))); 
		    		  update = false;
		    	  }
		    	  if (update == true)
		    	  {
		    		  updateTresholdMin1(updBr, perspectiveValue);
		    		  System.out.println("new min value : "+perspectiveValue);
		    	  }
		        ;}});
		txtL1min.addListener(SWT.Traverse , new Listener() {
		      public void handleEvent(Event event) {
		    	  if (event.detail == SWT.TRAVERSE_RETURN)
		    	  {
		    		  double perspectiveValue = 0;
		    		  boolean update = true;
			    	  try 
			    	  {				 
			    		  perspectiveValue = Double.parseDouble(txtL1min.getText());
			    	  }
			    	  catch (NumberFormatException e)
			    	  {
			    		  txtL1min.setText(Double.toString( Brick.getThresholdMin1(connectionData.BrickList, updBr.uid))); 
			    		  update = false;
			    	  }
			    	  if (update == true)
			    	  {
			    		  updateTresholdMin1(updBr, perspectiveValue);
			    		  System.out.println("new min value : "+perspectiveValue);
			    	  }
		    	  }
		        ;}});
		
		
		//txtL1min.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));	
		
		// label for unit
		Label lblUnit1 = new Label(group, SWT.NONE);
		lblUnit1.setBounds(txtL1min.getBounds().x+txtL1min.getBounds().width+settingRegionSeparateWidth, 
							  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
							  + settingRegionBetweenLineHeight*(lineNumber+1)+heightSum, 
							  width_one_fourth/2, 
							  settingRegionLineHeight);
		lblUnit1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		//formToolkit.adapt(lblTreshold, true, true);
		lblUnit1.setText(String.valueOf(constants.brickUnitMap.get( connectionData.presentedBrickList.get(index).getDeviceIdentifier())));				
		//----------------------------------------------------------------------------------------
		
		// label 1 max -----------------------------------------------------------------------------------
		Label lblMax1 = new Label(group, SWT.NONE);
		lblMax1.setBounds(settingOffsetX, 
							  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+2)
							  + settingRegionBetweenLineHeight*(lineNumber+2)+heightSum, 
							  width_one_fourth/3, 
							  settingRegionLineHeight);
		//lblTreshold.setBounds(settingRegionStartX, settingRegionStartY+44+i*settingRegionHeight, 55, 15);
		lblMax1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		//formToolkit.adapt(lblTreshold, true, true);
		lblMax1.setText("max ");
		
		// text field
		final Text txtL1max = new Text(group, SWT.BORDER);
		int max = (int)((double)constants.brickMaxValue.get(connectionData.presentedBrickList.get(index).getDeviceIdentifier())) ;				
		txtL1max.setText(""+max);
		txtL1max.setBounds(lblMax1.getBounds().x+lblMax1.getBounds().width+settingRegionSeparateWidth,
							settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+2)
							+ settingRegionBetweenLineHeight*(lineNumber+2)+heightSum-textFieldExtention, 
						   width_one_fourth/2,
						   settingRegionLineHeight+textFieldExtention);
		//txtL1min.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtL1max.addListener(SWT.FocusOut , new Listener() {
		      public void handleEvent(Event event) {
	    		  double perspectiveValue = 0;
	    		  boolean update = true;
		    	  try 
		    	  {				 
		    		  perspectiveValue = Double.parseDouble(txtL1max.getText());
		    	  }
		    	  catch (NumberFormatException e)
		    	  {
		    		  txtL1max.setText(Double.toString( Brick.getThresholdMax1(connectionData.BrickList, updBr.uid))); 
		    		  update = false;
		    	  }
		    	  if (update == true)
		    	  {
		    		  updateTresholdMax1(updBr, perspectiveValue);
		    		  System.out.println("new min value : "+perspectiveValue);
		    	  }
		        ;}});
		txtL1max.addListener(SWT.Traverse , new Listener() {
		      public void handleEvent(Event event) {
		    	  if (event.detail == SWT.TRAVERSE_RETURN)
		    	  {
		    		  double perspectiveValue = 0;
		    		  boolean update = true;
			    	  try 
			    	  {				 
			    		  perspectiveValue = Double.parseDouble(txtL1max.getText());
			    	  }
			    	  catch (NumberFormatException e)
			    	  {
			    		  txtL1max.setText(Double.toString( Brick.getThresholdMax1(connectionData.BrickList, updBr.uid))); 
			    		  update = false;
			    	  }
			    	  if (update == true)
			    	  {
			    		  updateTresholdMax1(updBr, perspectiveValue);
			    		  System.out.println("new min value : "+perspectiveValue);
			    	  }
		    	  }
		        ;}});
						
		// label for unit
		Label lblUnit2 = new Label(group, SWT.NONE);
		lblUnit2.setBounds(txtL1max.getBounds().x+txtL1max.getBounds().width+settingRegionSeparateWidth, 
							  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+2)
							  + settingRegionBetweenLineHeight*(lineNumber+2)+heightSum, 
							  width_one_fourth/2, 
							  settingRegionLineHeight);
		lblUnit2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		//formToolkit.adapt(lblTreshold, true, true);
		lblUnit2.setText(String.valueOf(constants.brickUnitMap.get( connectionData.presentedBrickList.get(index).getDeviceIdentifier())));								
		// -------------------------------------------------------------------------------------------------
		}
		
			
		//if (quant == 2)
		//{
			if (tmpBr.ctrlSimple[1] == true)
			{				
			// -------------------------------------------------------------------------------------
			// min label 2
			Label lblMin2 = new Label(group, SWT.NONE);
			lblMin2.setBounds(settingOffsetX+settingRegionWidth/2, 
							  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
							  + settingRegionBetweenLineHeight*(lineNumber+1)+heightSum, 
							  width_one_fourth/3, 
							  settingRegionLineHeight);
			//lblTreshold.setBounds(settingRegionStartX, settingRegionStartY+44+i*settingRegionHeight, 55, 15);
			lblMin2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			//formToolkit.adapt(lblTreshold, true, true);
			lblMin2.setText("min ");	
			
			// text field
			final Text txtL2min = new Text(group, SWT.BORDER);
			int min = (int)((double)constants.brickMinValue2nd.get(connectionData.presentedBrickList.get(index).getDeviceIdentifier())) ;						
			txtL2min.setText(""+min);
			txtL2min.setBounds(lblMin2.getBounds().x+lblMin2.getBounds().width+settingRegionSeparateWidth,
								settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
								+ settingRegionBetweenLineHeight*(lineNumber+1)+heightSum-textFieldExtention, 
							   width_one_fourth/2,
							   settingRegionLineHeight+textFieldExtention);
			//txtL1min.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));	
			txtL2min.addListener(SWT.FocusOut , new Listener() {
			      public void handleEvent(Event event) {
		    		  double perspectiveValue = 0;
		    		  boolean update = true;
			    	  try 
			    	  {				 
			    		  perspectiveValue = Double.parseDouble(txtL2min.getText());
			    	  }
			    	  catch (NumberFormatException e)
			    	  {
			    		  txtL2min.setText(Double.toString( Brick.getThresholdMin2(connectionData.BrickList, updBr.uid))); 
			    		  update = false;
			    	  }
			    	  if (update == true)
			    	  {
			    		  updateTresholdMin2(updBr, perspectiveValue);
			    		  System.out.println("new min value : "+perspectiveValue);
			    	  }
			        ;}});
			txtL2min.addListener(SWT.Traverse , new Listener() {
			      public void handleEvent(Event event) {
			    	  if (event.detail == SWT.TRAVERSE_RETURN)
			    	  {
			    		  double perspectiveValue = 0;
			    		  boolean update = true;
				    	  try 
				    	  {				 
				    		  perspectiveValue = Double.parseDouble(txtL2min.getText());
				    	  }
				    	  catch (NumberFormatException e)
				    	  {
				    		  txtL2min.setText(Double.toString( Brick.getThresholdMin2(connectionData.BrickList, updBr.uid))); 
				    		  update = false;
				    	  }
				    	  if (update == true)
				    	  {
				    		  updateTresholdMin2(updBr, perspectiveValue);
				    		  System.out.println("new min value : "+perspectiveValue);
				    	  }
			    	  }
			        ;}});

			// label for unit
			Label lblUnit3 = new Label(group, SWT.NONE);
			lblUnit3.setBounds(txtL2min.getBounds().x+txtL2min.getBounds().width+settingRegionSeparateWidth, 
								  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
								  + settingRegionBetweenLineHeight*(lineNumber+1)+heightSum, 
								  width_one_fourth, 
								  settingRegionLineHeight);
			lblUnit3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			//formToolkit.adapt(lblTreshold, true, true);
			lblUnit3.setText(String.valueOf(constants.brick2ndUnitMap.get( connectionData.presentedBrickList.get(index).getDeviceIdentifier())));					

			// --------------------------------------------------------------------------------------------------
			
			
			// --------------------------------------------------------------------------------------------------
			// max label 2
			Label lblMax2 = new Label(group, SWT.NONE);
			lblMax2.setBounds(settingOffsetX+settingRegionWidth/2, 
							  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+2)
							  + settingRegionBetweenLineHeight*(lineNumber+2)+heightSum, 
							  width_one_fourth/3, 
							  settingRegionLineHeight);
			//lblTreshold.setBounds(settingRegionStartX, settingRegionStartY+44+i*settingRegionHeight, 55, 15);
			lblMax2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			//formToolkit.adapt(lblTreshold, true, true);
			lblMax2.setText("max ");					
		
			// text field
			final Text txtL2max = new Text(group, SWT.BORDER);
			int max = (int)((double)constants.brickMaxValue2nd.get(connectionData.presentedBrickList.get(index).getDeviceIdentifier())) ;						
			txtL2max.setText(""+max);
			txtL2max.setBounds(lblMax2.getBounds().x+lblMax2.getBounds().width+settingRegionSeparateWidth,
							settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+2)
							+ settingRegionBetweenLineHeight*(lineNumber+2)+heightSum-textFieldExtention, 
						   width_one_fourth/2,
						   settingRegionLineHeight+textFieldExtention);
			//txtL1min.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			txtL2max.addListener(SWT.FocusOut , new Listener() {
			      public void handleEvent(Event event) {
		    		  double perspectiveValue = 0;
		    		  boolean update = true;
			    	  try 
			    	  {				 
			    		  perspectiveValue = Double.parseDouble(txtL2max.getText());
			    	  }
			    	  catch (NumberFormatException e)
			    	  {
			    		  txtL2max.setText(Double.toString( Brick.getThresholdMax2(connectionData.BrickList, updBr.uid))); 
			    		  update = false;
			    	  }
			    	  if (update == true)
			    	  {
			    		  updateTresholdMax2(updBr, perspectiveValue);
			    		  System.out.println("new min value : "+perspectiveValue);
			    	  }
			        ;}});
			txtL2max.addListener(SWT.Traverse , new Listener() {
			      public void handleEvent(Event event) {
			    	  if (event.detail == SWT.TRAVERSE_RETURN)
			    	  {
			    		  double perspectiveValue = 0;
			    		  boolean update = true;
				    	  try 
				    	  {				 
				    		  perspectiveValue = Double.parseDouble(txtL2max.getText());
				    	  }
				    	  catch (NumberFormatException e)
				    	  {
				    		  txtL2max.setText(Double.toString( Brick.getThresholdMax2(connectionData.BrickList, updBr.uid))); 
				    		  update = false;
				    	  }
				    	  if (update == true)
				    	  {
				    		  updateTresholdMax2(updBr, perspectiveValue);
				    		  System.out.println("new min value : "+perspectiveValue);
				    	  }
			    	  }
			        ;}});
		
			// label for unit
			Label lblUnit4 = new Label(group, SWT.NONE);
			lblUnit4.setBounds(txtL2max.getBounds().x+txtL2max.getBounds().width+settingRegionSeparateWidth, 
							  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+2)
							  + settingRegionBetweenLineHeight*(lineNumber+2)+heightSum, 
							  width_one_fourth, 
							  settingRegionLineHeight);
			lblUnit4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			//formToolkit.adapt(lblTreshold, true, true);
			lblUnit4.setText(String.valueOf(constants.brick2ndUnitMap.get( connectionData.presentedBrickList.get(index).getDeviceIdentifier())));

			// -----------------------------------------------------------------------------------------
			}
		//}
	}
	
	
	public static void settingMenu_addAverageCheckBox(Brick tmpBr, int lineNumber, int quant, int index, int caseNumber)
	{
		final String tmpStr = tmpBr.uid;
		Button btnCheckButton = new Button(group, SWT.CHECK);
		btnCheckButton.setBounds(settingOffsetX, 
								 settingRegionBorderLineHeight+settingRegionBetweenLineHeight*lineNumber+settingRegionSeparateHeight*caseNumber
								 +settingRegionLineHeight*lineNumber+heightSum, 
								 width_one_half, 
								 settingRegionLineHeight);
		btnCheckButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnCheckButton.setText("control average");
		btnCheckButton.setSelection(tmpBr.controlAverage);
		btnCheckButton.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {						
				averageControlChecked(tmpStr);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {				
				
			}
		});
		
		
		// --------
		if (quant == 2)
		{	
			final String tmpStr2 = tmpBr.uid;
								
			Button btnCheckButton3 = new Button(group, SWT.CHECK);
			btnCheckButton3.setBounds(settingOffsetX+settingRegionWidth/2, 
									  settingRegionBorderLineHeight+settingRegionLineHeight*lineNumber+settingRegionSeparateHeight*caseNumber
									  +settingRegionBetweenLineHeight*lineNumber+heightSum,  
									  width_one_half, 
									  settingRegionLineHeight);
			btnCheckButton3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			btnCheckButton3.setText("control average");
			btnCheckButton3.setSelection(tmpBr.controlAverage2);
			btnCheckButton3.addSelectionListener(new SelectionListener(){
				@Override
				public void widgetSelected(SelectionEvent e) {						
					averageControlChecked2(tmpStr2);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {					
					
				}
			});		
		}
	}
	
	
	public static void settingMenu_addAverageControls(final Brick tmpBr, int lineNumber, int quant, int index, int caseNumber)
	{
		if (tmpBr.controlAverage == true)
		{
			// label control average bottom high ----------
			Label lblMin3 = new Label(group, SWT.NONE);
			lblMin3.setBounds(settingOffsetX, 
								  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)+settingRegionSeparateHeight*caseNumber
								  + settingRegionBetweenLineHeight*(lineNumber+1)+heightSum, 
								  width_one_fourth/3, 
								  settingRegionLineHeight);
			lblMin3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblMin3.setText("high");
			
			// text field
			final Text txtL1min2 = new Text(group, SWT.BORDER);
			int min = (int)((double)constants.brickAvgHigh1.get(connectionData.presentedBrickList.get(index).getDeviceIdentifier())) ;						
			txtL1min2.setText(""+min);
			txtL1min2.setBounds(lblMin3.getBounds().x+lblMin3.getBounds().width+settingRegionSeparateWidth,
								settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)+settingRegionSeparateHeight*caseNumber
								+ settingRegionBetweenLineHeight*(lineNumber+1)+heightSum-textFieldExtention, 
							    width_one_fourth/2,
							    settingRegionLineHeight+textFieldExtention);
			
			txtL1min2.addListener(SWT.FocusOut , new Listener() {
			      public void handleEvent(Event event) {
		    		  double perspectiveValue = 0;
		    		  boolean update = true;
			    	  try 
			    	  {				 
			    		  perspectiveValue = Double.parseDouble(txtL1min2.getText());
			    	  }
			    	  catch (NumberFormatException e)
			    	  {
			    		  txtL1min2.setText(Double.toString( Brick.getAvgHigh(connectionData.BrickList, tmpBr.uid,1))); 
			    		  update = false;
			    	  }
			    	  if (update == true)
			    	  {
			    		  updateAvg(tmpBr, perspectiveValue, 1, true);
			    		  System.out.println("new avg high : "+perspectiveValue);
			    	  }
			        ;}});
			txtL1min2.addListener(SWT.Traverse , new Listener() {
			      public void handleEvent(Event event) {
			    	  if (event.detail == SWT.TRAVERSE_RETURN)
			    	  {
			    		  double perspectiveValue = 0;
			    		  boolean update = true;
				    	  try 
				    	  {				 
				    		  perspectiveValue = Double.parseDouble(txtL1min2.getText());
				    	  }
				    	  catch (NumberFormatException e)
				    	  {
				    		  txtL1min2.setText(Double.toString( Brick.getAvgHigh(connectionData.BrickList, tmpBr.uid,1)));
				    		  update = false;
				    	  }
				    	  if (update == true)
				    	  {
				    		  updateAvg(tmpBr, perspectiveValue, 1, true);
				    		  System.out.println("new avg high : "+perspectiveValue);
				    	  }
			    	  }
			        ;}});
							
			//txtL1min.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));	
			
			// label for unit
			Label lblUnit3 = new Label(group, SWT.NONE);
			lblUnit3.setBounds(txtL1min2.getBounds().x+txtL1min2.getBounds().width+settingRegionSeparateWidth, 
								  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)+settingRegionSeparateHeight*caseNumber
								  + settingRegionBetweenLineHeight*(lineNumber+1)+heightSum, 
								  width_one_fourth/2, 
								  settingRegionLineHeight);
			lblUnit3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			//formToolkit.adapt(lblTreshold, true, true);
			lblUnit3.setText(String.valueOf(constants.brickUnitMap.get( connectionData.presentedBrickList.get(index).getDeviceIdentifier())));
			// -------------------------------------------------------------------------------------------------
			
			
			// label control average bottom  max -----------------------------------------------------------------------------------
			Label lblMax4 = new Label(group, SWT.NONE);
			lblMax4.setBounds(settingOffsetX, 
								  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+2)+settingRegionSeparateHeight*caseNumber
								  + settingRegionBetweenLineHeight*(lineNumber+2)+heightSum, 
								  width_one_fourth/3, 
								  settingRegionLineHeight);
			//lblTreshold.setBounds(settingRegionStartX, settingRegionStartY+44+i*settingRegionHeight, 55, 15);
			lblMax4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			//formToolkit.adapt(lblTreshold, true, true);
			lblMax4.setText("low");
			
			// text field
			final Text txtL1max4 = new Text(group, SWT.BORDER);
			int max = (int)((double)constants.brickAvgLow1.get(connectionData.presentedBrickList.get(index).getDeviceIdentifier())) ;				
			txtL1max4.setText(""+max);
			txtL1max4.setBounds(lblMax4.getBounds().x+lblMax4.getBounds().width+settingRegionSeparateWidth,
								settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+2)+settingRegionSeparateHeight*caseNumber
								+ settingRegionBetweenLineHeight*(lineNumber+2)+heightSum-textFieldExtention, 
							   width_one_fourth/2,
							   settingRegionLineHeight+textFieldExtention);
			//txtL1min.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			txtL1max4.addListener(SWT.FocusOut , new Listener() {
			      public void handleEvent(Event event) {
		    		  double perspectiveValue = 0;
		    		  boolean update = true;
			    	  try 
			    	  {				 
			    		  perspectiveValue = Double.parseDouble(txtL1max4.getText());
			    	  }
			    	  catch (NumberFormatException e)
			    	  {
			    		  txtL1max4.setText(Double.toString( Brick.getAvgLow(connectionData.BrickList, tmpBr.uid, 1))); 
			    		  update = false;
			    	  }
			    	  if (update == true)
			    	  {
			    		  updateAvg(tmpBr, perspectiveValue, 1, false);
			    		  System.out.println("new avg1 low : "+perspectiveValue);
			    	  }
			        ;}});
			txtL1max4.addListener(SWT.Traverse , new Listener() {
			      public void handleEvent(Event event) {
			    	  if (event.detail == SWT.TRAVERSE_RETURN)
			    	  {
			    		  double perspectiveValue = 0;
			    		  boolean update = true;
				    	  try 
				    	  {				 
				    		  perspectiveValue = Double.parseDouble(txtL1max4.getText());
				    	  }
				    	  catch (NumberFormatException e)
				    	  {
				    		  txtL1max4.setText(Double.toString( Brick.getAvgLow(connectionData.BrickList, tmpBr.uid, 1))); 
				    		  update = false;
				    	  }
				    	  if (update == true)
				    	  {
				    		  updateAvg(tmpBr, perspectiveValue, 1, false);
				    		  System.out.println("new avg1 low : "+perspectiveValue);
				    	  }
			    	  }
			        ;}});
							
			// label for unit
			Label lblUnit4 = new Label(group, SWT.NONE);
			lblUnit4.setBounds(txtL1max4.getBounds().x+txtL1max4.getBounds().width+settingRegionSeparateWidth, 
								  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+2)+settingRegionSeparateHeight*caseNumber
								  + settingRegionBetweenLineHeight*(lineNumber+2)+heightSum, 
								  width_one_fourth/2, 
								  settingRegionLineHeight);
			lblUnit4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			//formToolkit.adapt(lblTreshold, true, true);
			lblUnit4.setText(String.valueOf(constants.brickUnitMap.get( connectionData.presentedBrickList.get(index).getDeviceIdentifier())));
			// -------------------------------------------------------------------------------------------------
		}

		
		if (tmpBr.controlAverage2 == true)		
		{
			// average in case of double chart (e.g. for current / voltage)-------------------------------------
				// label control average bottom min -----------------------------------------------------------------------------------
				Label lblMin5 = new Label(group, SWT.NONE);
				lblMin5.setBounds(settingOffsetX+settingRegionWidth/2, 
									  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)+settingRegionSeparateHeight*caseNumber
									  + settingRegionBetweenLineHeight*(lineNumber+1)+heightSum, 
									  width_one_fourth/3, 
									  settingRegionLineHeight);
				lblMin5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				lblMin5.setText("high");
				
				// text field
				final Text txtL1min5 = new Text(group, SWT.BORDER);
				int min = (int)((double)constants.brickAvgHigh2.get(connectionData.presentedBrickList.get(index).getDeviceIdentifier())) ;						
				txtL1min5.setText(""+min);
				txtL1min5.setBounds(lblMin5.getBounds().x+lblMin5.getBounds().width+settingRegionSeparateWidth,
									settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)+settingRegionSeparateHeight*caseNumber
									+ settingRegionBetweenLineHeight*(lineNumber+1)+heightSum-textFieldExtention, 
								   width_one_fourth/2,
								   settingRegionLineHeight+textFieldExtention);
				
				txtL1min5.addListener(SWT.FocusOut , new Listener() {
				      public void handleEvent(Event event) {
			    		  double perspectiveValue = 0;
			    		  boolean update = true;
				    	  try 
				    	  {				 
				    		  perspectiveValue = Double.parseDouble(txtL1min5.getText());
				    	  }
				    	  catch (NumberFormatException e)
				    	  {
				    		  txtL1min5.setText(Double.toString( Brick.getAvgHigh(connectionData.BrickList, tmpBr.uid, 2))); 
				    		  update = false;
				    	  }
				    	  if (update == true)
				    	  {
				    		  updateAvg(tmpBr, perspectiveValue, 2, true);
				    		  System.out.println("new avg2 high : "+perspectiveValue);
				    	  }
				        ;}});
				txtL1min5.addListener(SWT.Traverse , new Listener() {
				      public void handleEvent(Event event) {
				    	  if (event.detail == SWT.TRAVERSE_RETURN)
				    	  {
				    		  double perspectiveValue = 0;
				    		  boolean update = true;
					    	  try 
					    	  {				 
					    		  perspectiveValue = Double.parseDouble(txtL1min5.getText());
					    	  }
					    	  catch (NumberFormatException e)
					    	  {
					    		  txtL1min5.setText(Double.toString( Brick.getAvgHigh(connectionData.BrickList, tmpBr.uid, 2))); 
					    		  update = false;
					    	  }
					    	  if (update == true)
					    	  {
					    		  updateAvg(tmpBr, perspectiveValue, 2, true);
					    		  System.out.println("new avg2 high : "+perspectiveValue);
					    	  }
				    	  }
				        ;}});
				
				
				//txtL1min.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));	
				
				// label for unit
				Label lblUnit5 = new Label(group, SWT.NONE);
				lblUnit5.setBounds(txtL1min5.getBounds().x+txtL1min5.getBounds().width+settingRegionSeparateWidth, 
									  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)+settingRegionSeparateHeight*caseNumber
									  + settingRegionBetweenLineHeight*(lineNumber+1)+heightSum, 
									  width_one_fourth, 
									  settingRegionLineHeight);
				lblUnit5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				//formToolkit.adapt(lblTreshold, true, true);
				lblUnit5.setText(String.valueOf(constants.brick2ndUnitMap.get( connectionData.presentedBrickList.get(index).getDeviceIdentifier())));					
				// -------------------------------------------------------------------------------------------------					


			// -------------------------------------------------------------------------------------------------
			
							
			// average in case of double chart (e.g. for current / voltage)-------------------------------------

				Label lblMax7 = new Label(group, SWT.NONE);
				lblMax7.setBounds(settingOffsetX+settingRegionWidth/2, 
									  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+2)+settingRegionSeparateHeight*caseNumber
									  + settingRegionBetweenLineHeight*(lineNumber+2)+heightSum, 
									  width_one_fourth/3, 
									  settingRegionLineHeight);
				//lblTreshold.setBounds(settingRegionStartX, settingRegionStartY+44+i*settingRegionHeight, 55, 15);
				lblMax7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				//formToolkit.adapt(lblTreshold, true, true);
				lblMax7.setText("low");
				
				// text field
				final Text txtL1max5 = new Text(group, SWT.BORDER);
				int max = (int)((double)constants.brickAvgLow2.get(connectionData.presentedBrickList.get(index).getDeviceIdentifier())) ;				
				txtL1max5.setText(""+max);
				txtL1max5.setBounds(lblMax7.getBounds().x+lblMax7.getBounds().width+settingRegionSeparateWidth,
									settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+2)+settingRegionSeparateHeight*caseNumber
									+ settingRegionBetweenLineHeight*(lineNumber+2)+heightSum-textFieldExtention, 
								   width_one_fourth/2,
								   settingRegionLineHeight+textFieldExtention);
				//txtL1min.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				txtL1max5.addListener(SWT.FocusOut , new Listener() {
				      public void handleEvent(Event event) {
			    		  double perspectiveValue = 0;
			    		  boolean update = true;
				    	  try 
				    	  {				 
				    		  perspectiveValue = Double.parseDouble(txtL1max5.getText());
				    	  }
				    	  catch (NumberFormatException e)
				    	  {
				    		  txtL1max5.setText(Double.toString( Brick.getAvgLow(connectionData.BrickList, tmpBr.uid, 2))); 
				    		  update = false;
				    	  }
				    	  if (update == true)
				    	  {
				    		  updateAvg(tmpBr, perspectiveValue, 2, false);
				    		  System.out.println("new avg2 low : "+perspectiveValue);
				    	  }
				        ;}});
				txtL1max5.addListener(SWT.Traverse , new Listener() {
				      public void handleEvent(Event event) {
				    	  if (event.detail == SWT.TRAVERSE_RETURN)
				    	  {
				    		  double perspectiveValue = 0;
				    		  boolean update = true;
					    	  try 
					    	  {				 
					    		  perspectiveValue = Double.parseDouble(txtL1max5.getText());
					    	  }
					    	  catch (NumberFormatException e)
					    	  {
					    		  txtL1max5.setText(Double.toString( Brick.getAvgLow(connectionData.BrickList, tmpBr.uid, 2))); 
					    		  update = false;
					    	  }
					    	  if (update == true)
					    	  {
					    		  updateAvg(tmpBr, perspectiveValue, 2, false);
					    		  System.out.println("new avg2 low : "+perspectiveValue);
					    	  }
				    	  }
				        ;}});
								
				// label for unit
				Label lblUnit6 = new Label(group, SWT.NONE);
				lblUnit6.setBounds(txtL1max5.getBounds().x+txtL1max5.getBounds().width+settingRegionSeparateWidth, 
									  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+2)+settingRegionSeparateHeight*caseNumber
									  + settingRegionBetweenLineHeight*(lineNumber+2)+heightSum, 
									  width_one_fourth/2, 
									  settingRegionLineHeight);
				lblUnit6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				//formToolkit.adapt(lblTreshold, true, true);
				lblUnit6.setText(String.valueOf(constants.brick2ndUnitMap.get( connectionData.presentedBrickList.get(index).getDeviceIdentifier())));
			// ------------------------------------------------------------------------------------------------
		}
	}
	
	
	public static void settingMenu_addTemplateCheckBox(final Brick tmpBr, int lineNumber, int quant, int index, int caseNumber)
	{
		final String tmpStr = tmpBr.uid;
		final Label tmplPath1txt = new Label(group, SWT.NONE);						
		tmplPath1txt.setText(Brick.getBrick(connectionData.BrickList,tmpBr.uid).ctrlTmplPath[0]);
		tmplPath1txt.setBounds(settingOffsetX,
							settingRegionBorderLineHeight+settingRegionBetweenLineHeight*(lineNumber+2)+settingRegionSeparateHeight*caseNumber
							+settingRegionLineHeight*lineNumber+heightSum, 									
							width_one_half,
						    settingRegionLineHeight);
		tmplPath1txt.setEnabled(tmpBr.ctrlTmpl[0]);
		tmplPath1txt.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		// template control 1 button
		final Button btnCheckButtonTmpl = new Button(group, SWT.CHECK);
		btnCheckButtonTmpl.setBounds(settingOffsetX, 
								 settingRegionBorderLineHeight+settingRegionBetweenLineHeight*lineNumber+settingRegionSeparateHeight*caseNumber
								 +settingRegionLineHeight*lineNumber+heightSum, 
								 width_one_half, 
								 settingRegionLineHeight);
		btnCheckButtonTmpl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnCheckButtonTmpl.setText("template control");
		btnCheckButtonTmpl.setSelection(tmpBr.ctrlTmpl[0]);
		btnCheckButtonTmpl.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				// if template control is disabled (we want to enable it now)
				if (tmpBr.ctrlTmpl[0]==false)
				{
					System.out.println("enable TEMPLATE");
					if (tmpltCtrlCheck(tmpStr, 0) == true)
					{
						if (Brick.getBrick(connectionData.BrickList,tmpBr.uid).ctrlTmplPath[0].length() > 30)
						{
							// adjust text length
							String tmp = Brick.getBrick(connectionData.BrickList,tmpBr.uid).ctrlTmplPath[0];
							tmp = ".." + tmp.substring(tmp.length()-20, tmp.length());
							tmplPath1txt.setText(tmp);
						}
						else
						{
							tmplPath1txt.setText(Brick.getBrick(connectionData.BrickList,tmpBr.uid).ctrlTmplPath[0]);
						}
					}
				}
				// if template control is enabled (we are going to disable it)
				else
				{
					updateSettingTabPls = true;
					System.out.println("disable TEMPLATE");
					functions.Events.disableTmpltCntrl(tmpBr.uid, 0, Brick.getBrick(connectionData.BrickList,tmpBr.uid).ctrlTmplPath[0]);
				}
				tmplPath1txt.setEnabled(tmpBr.ctrlTmpl[0]);
				btnCheckButtonTmpl.setSelection(tmpBr.ctrlTmpl[0]);						
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		
		if (quant == 2)
		{
			final Label tmplPath2txt = new Label(group, SWT.NONE);						
			tmplPath2txt.setText(Brick.getBrick(connectionData.BrickList,tmpBr.uid).ctrlTmplPath[1]);
			tmplPath2txt.setBounds(settingOffsetX+settingRegionWidth/2,
								settingRegionBorderLineHeight+settingRegionBetweenLineHeight*(lineNumber+2)+settingRegionSeparateHeight*caseNumber
								+settingRegionLineHeight*lineNumber+heightSum, 									
								width_one_half,
								settingRegionLineHeight);
			tmplPath2txt.setEnabled(tmpBr.ctrlTmpl[1]);
			tmplPath2txt.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
			// template control 1 button
			final Button btnCheckButtonTmpl2 = new Button(group, SWT.CHECK);
			btnCheckButtonTmpl2.setBounds(settingOffsetX+settingRegionWidth/2, 
								settingRegionBorderLineHeight+settingRegionBetweenLineHeight*lineNumber+settingRegionSeparateHeight*caseNumber
								+settingRegionLineHeight*lineNumber+heightSum, 
								width_one_half, 
								settingRegionLineHeight);
			btnCheckButtonTmpl2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			btnCheckButtonTmpl2.setText("template control");
			btnCheckButtonTmpl2.setSelection(tmpBr.ctrlTmpl[1]);
			btnCheckButtonTmpl2.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetSelected(SelectionEvent e) 
				{
					// if template control is disabled (we want to enable it now)
					if (tmpBr.ctrlTmpl[1]==false)
					{
						System.out.println("enable TEMPLATE");
						if (tmpltCtrlCheck(tmpStr, 1) == true)
						{
							if (Brick.getBrick(connectionData.BrickList,tmpBr.uid).ctrlTmplPath[1].length() > 30)
							{
							// 	adjust text length
								String tmp = Brick.getBrick(connectionData.BrickList,tmpBr.uid).ctrlTmplPath[1];
								tmp = ".." + tmp.substring(tmp.length()-20, tmp.length());
								tmplPath2txt.setText(tmp);
							}
							else
							{
								tmplPath2txt.setText(Brick.getBrick(connectionData.BrickList,tmpBr.uid).ctrlTmplPath[1]);
							}
						}
					}
				// 	if template control is enabled (we are going to disable it)
					else
					{
						updateSettingTabPls = true;
						System.out.println("disable TEMPLATE");
						functions.Events.disableTmpltCntrl(tmpBr.uid, 1, Brick.getBrick(connectionData.BrickList,tmpBr.uid).ctrlTmplPath[1]);
					}
					tmplPath2txt.setEnabled(tmpBr.ctrlTmpl[1]);
					btnCheckButtonTmpl2.setSelection(tmpBr.ctrlTmpl[1]);						
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {}
			});

		}
	}
	
	
	public static void settingMenu_addTemplateControls(final Brick tmpBr, int lineNumber, int quant, int index, int caseNumber)
	{
		if (tmpBr.ctrlTmpl[0] == true)
		{
		// template control 1 label + textfield
		Label lblMax10 = new Label(group, SWT.NONE);
		lblMax10.setBounds(settingOffsetX, 
							  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
							  +settingRegionSeparateHeight*caseNumber
							  +templateControlOffset
							  + settingRegionBetweenLineHeight*(lineNumber+3)+heightSum+5, 
							  width_one_fourth/3, 
							  settingRegionLineHeight);
		//lblTreshold.setBounds(settingRegionStartX, settingRegionStartY+44+i*settingRegionHeight, 55, 15);
		lblMax10.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		//formToolkit.adapt(lblTreshold, true, true);
		lblMax10.setText("+/-");
		
		// text field
		final Text txtL10max = new Text(group, SWT.BORDER);
		int max = (int)(tmpBr.tmpl1Width);				
		txtL10max.setText(""+max);
		txtL10max.setBounds(settingOffsetX+lblMax10.getBounds().width+settingRegionSeparateWidth,
							settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
							+templateControlOffset
							+settingRegionSeparateHeight*caseNumber
							+ settingRegionBetweenLineHeight*(lineNumber+3)+heightSum-textFieldExtention+5, 
						    width_one_fourth/2,
						    settingRegionLineHeight+textFieldExtention);
		//txtL1min.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtL10max.addListener(SWT.FocusOut , new Listener() {
		      public void handleEvent(Event event) {
	    		  double perspectiveValue = 0;
	    		  boolean update = true;
		    	  try 
		    	  {				 
		    		  perspectiveValue = Double.parseDouble(txtL10max.getText());
		    	  }
		    	  catch (NumberFormatException e)
		    	  {
		    		  txtL10max.setText(Double.toString( Brick.getThresholdMax1(connectionData.BrickList, tmpBr.uid))); 
		    		  update = false;
		    	  }
		    	  if (update == true)
		    	  {
		    	  	  if (perspectiveValue>=0)
		    	  	  {
		    	  		  tmpBr.setTmplWidth(perspectiveValue,0);
		    	  		  System.out.println("new tmpl value : "+perspectiveValue);
		    	  		  functions.Events.updateTmplPlotWidth(tmpBr.uid, 0);
		    	  	  }
		    	  }
		        ;}});
		txtL10max.addListener(SWT.Traverse , new Listener() {
		      public void handleEvent(Event event) {
		    	  if (event.detail == SWT.TRAVERSE_RETURN)
		    	  {
		    		  double perspectiveValue = 0;
		    		  boolean update = true;
			    	  try
			    	  {
			    		  perspectiveValue = Double.parseDouble(txtL10max.getText());
			    	  }
			    	  catch (NumberFormatException e)
			    	  {
			    		  txtL10max.setText(Double.toString( Brick.getThresholdMax1(connectionData.BrickList, tmpBr.uid)));
			    		  update = false;
			    	  }
			    	  if (update == true)
			    	  {
			    	  	  if (perspectiveValue>=0)
			    	  	  {
			    	  		tmpBr.setTmplWidth(perspectiveValue,0);
			    		  	  System.out.println("new tmpl value : "+perspectiveValue);
			    		  	  functions.Events.updateTmplPlotWidth(tmpBr.uid, 0);
			    	  	  }
			    	  }
		    	  }
		        ;}});
						
		// label for unit
		Label lblUnit8 = new Label(group, SWT.NONE);
		/*
		lblUnit8.setBounds(txtL1max.getBounds().x+txtL1max.getBounds().width+settingRegionSeparateWidth, 
							  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
							  + settingRegionBetweenLineHeight*(lineNumber+3)+heightSum+5, 
							  width_one_fourth/2, 
							  settingRegionLineHeight);									  
		*/
		lblUnit8.setBounds(settingOffsetX+width_one_fourth/3+settingRegionSeparateWidth+width_one_fourth/2+settingRegionSeparateWidth, 
				  settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
				  +templateControlOffset
				  +settingRegionSeparateHeight*caseNumber
				  + settingRegionBetweenLineHeight*(lineNumber+3)+heightSum+5, 
				  width_one_fourth/2, 
				  settingRegionLineHeight);
		
		lblUnit8.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		//formToolkit.adapt(lblTreshold, true, true);
		lblUnit8.setText(String.valueOf(constants.brickUnitMap.get( connectionData.presentedBrickList.get(index).getDeviceIdentifier())));								
		// -------------------------------------------------------------------------------------------------				
		// template control 1 end	
		// -------------------------------------------------------------------------------------------------------------------
		}
		

		if (tmpBr.ctrlTmpl[1] == true)
		{
			// -------------------------------------------------------------------------------------------------------------------
			// template control 2
			// -------------------------------------------------------------------------------------------------------------------									
			// 	template control 1 label + textfield
				Label lblMax11 = new Label(group, SWT.NONE);
				lblMax11.setBounds(settingOffsetX+settingRegionWidth/2, 
								  	settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
								  	+settingRegionSeparateHeight*caseNumber
								  	+templateControlOffset
								  	+ settingRegionBetweenLineHeight*(lineNumber+3)+heightSum+5, 
								  	width_one_fourth/3, 
								  	settingRegionLineHeight);
			//	lblTreshold.setBounds(settingRegionStartX, settingRegionStartY+44+i*settingRegionHeight, 55, 15);
				lblMax11.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			//	formToolkit.adapt(lblTreshold, true, true);
				lblMax11.setText("+/-");
			
				// text field
				final Text txtL11max = new Text(group, SWT.BORDER);
				int max = (int)(tmpBr.tmpl1Width);				
				txtL11max.setText(""+max);
				/*
				txtL11max.setBounds(lblMax1.getBounds().x+lblMax11.getBounds().width+settingRegionSeparateWidth+settingRegionWidth/2,
									settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
									+ settingRegionBetweenLineHeight*(lineNumber+3)+heightSum-textFieldExtention+5, 
									width_one_fourth/2,
									settingRegionLineHeight+textFieldExtention);
				 */
				txtL11max.setBounds(settingOffsetX+width_one_fourth/3+settingRegionSeparateWidth+settingRegionWidth/2,
						settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
						+templateControlOffset
						+settingRegionSeparateHeight*caseNumber
						+ settingRegionBetweenLineHeight*(lineNumber+3)+heightSum-textFieldExtention+5, 
						width_one_fourth/2,
						settingRegionLineHeight+textFieldExtention);					
				//txtL1min.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				txtL11max.addListener(SWT.FocusOut , new Listener() 
				{
					public void handleEvent(Event event) 
					{
		    		  double perspectiveValue = 0;
		    		  boolean update = true;
			    	  try 
			    	  {				 
			    		  perspectiveValue = Double.parseDouble(txtL11max.getText());
			    	  }
			    	  catch (NumberFormatException e)
			    	  {
			    		  txtL11max.setText(Double.toString( Brick.getThresholdMax1(connectionData.BrickList, tmpBr.uid))); 
			    		  update = false;
			    	  }
			    	  if (update == true)
			    	  {
			    	  	  if (perspectiveValue>=0)
			    	  	  {				    		  
			    	  		tmpBr.setTmplWidth(perspectiveValue,1);
			    	  		  System.out.println("new tmpl value : "+perspectiveValue);
			    	  		  functions.Events.updateTmplPlotWidth(tmpBr.uid, 1);
			    	  	  }
			    	  }
			        ;}});
					txtL11max.addListener(SWT.Traverse , new Listener() 
					{
						public void handleEvent(Event event) 
						{
							if (event.detail == SWT.TRAVERSE_RETURN)
							{
								double perspectiveValue = 0;
								boolean update = true;
								try 
								{				 
									perspectiveValue = Double.parseDouble(txtL11max.getText());
								}
								catch (NumberFormatException e)
								{
									txtL11max.setText(Double.toString( Brick.getThresholdMax1(connectionData.BrickList, tmpBr.uid))); 
									update = false;
								}
								if (update == true)
								{
									if (perspectiveValue>=0)
									{
										tmpBr.setTmplWidth(perspectiveValue,1);
										System.out.println("new tmpl value : "+perspectiveValue);
										functions.Events.updateTmplPlotWidth(tmpBr.uid, 1);
									}
								}
							}
			        ;}});
							
					// label for unit
					Label lblUnit9 = new Label(group, SWT.NONE);
					lblUnit9.setBounds(settingOffsetX+width_one_fourth/3+settingRegionSeparateWidth+width_one_fourth/2+settingRegionSeparateWidth+settingRegionWidth/2, 
									settingRegionBorderLineHeight+settingRegionLineHeight*(lineNumber+1)
									+settingRegionSeparateHeight*caseNumber
									+templateControlOffset
									+ settingRegionBetweenLineHeight*(lineNumber+3)+heightSum+5, 
								  	width_one_fourth/2, 
								  	settingRegionLineHeight);
					lblUnit9.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					//formToolkit.adapt(lblTreshold, true, true);
					lblUnit9.setText(String.valueOf(constants.brickUnitMap.get( connectionData.presentedBrickList.get(index).getDeviceIdentifier())));														
			}		
			// -------------------------------------------------------------------------------------------------				
			// template control 2 end	
			// -------------------------------------------------------------------------------------------------------------------
	}
	
	
	public static void updateSettingTabs()
	{						
		group.dispose();
		group = new Group(shell, SWT.NONE);
		group.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));		
		heightSum = 0;
				
		// width of the label (1/2 of max width)
		width_one_half = settingRegionWidth/2 - 2*settingOffsetX;

		// width of the label (1/4 of max width)
		width_one_fourth = settingRegionWidth/4 - 2*settingOffsetX;
	
		//list settings for each sensor-----------------------------------------
		for (int i = 0; i<connectionData.presentedBrickList.size(); i++)
		{			
			final Brick tmpBr = connectionData.presentedBrickList.get(i);

			if (tmpBr.checked1 == true)			
			{
				int lineNumber = 0;	
				int caseNumber = 0;
				
				// -----------------------------------------
				// sensor name + uid
				// -----------------------------------------
				settingsMenu_addName(i, lineNumber);
				lineNumber ++;		
				// -----------------------------------------

				
				// -----------------------------------------		
				// use case: SIMPLE for bricks with 2 sensor
				// -----------------------------------------
				if (tmpBr.deviceIdentifier == 227)
				{	
					settingMenu_addSimpleCheckbox(tmpBr, lineNumber, 2, i);
					if ((tmpBr.ctrlSimple[0] == true) || (tmpBr.ctrlSimple[1] == true))
					{
						settingMenu_addSimpleControls(tmpBr, lineNumber, 2, i);
						lineNumber += 2;
						//lineNumber += 1;
						caseNumber ++;
					}
				}
				// use case simple for bricks with 1 sensor
				else
				{
					settingMenu_addSimpleCheckbox(tmpBr, lineNumber, 1, i);
					if (tmpBr.ctrlSimple[0] == true)
					{
						settingMenu_addSimpleControls(tmpBr, lineNumber, 1, i);
						lineNumber += 2;
						//lineNumber += 1;
						caseNumber ++;
					}
				}
				lineNumber += 1;
				//caseNumber ++;
				// -----------------------------------------


				// --------------------------------------------				
				// use case: AVERAGE for bricks with 2 sensors
				// --------------------------------------------
				if (tmpBr.deviceIdentifier == 227)
				{
					settingMenu_addAverageCheckBox(tmpBr, lineNumber, 2, i, caseNumber);
					if ((tmpBr.controlAverage==true)||(tmpBr.controlAverage2==true))
					{
						settingMenu_addAverageControls(tmpBr, lineNumber, 2, i, caseNumber);
						lineNumber += 2;
						caseNumber++;
					}
				}
				// use case: average for bricks with 1 sensor 
				else
				{	
					settingMenu_addAverageCheckBox(tmpBr, lineNumber, 1, i, caseNumber);
					if (tmpBr.controlAverage==true)
					{
						settingMenu_addAverageControls(tmpBr, lineNumber, 1, i, caseNumber);
						lineNumber += 2;
						caseNumber++;
					}
				}
				lineNumber +=1;
				//caseNumber++;
				// ---------------------------------------------
																							
				
				// ---------------------------------------------
				// use case: TEMPLATE for bricks with 2 sensors	
				// ---------------------------------------------
				if (tmpBr.deviceIdentifier == 227)
				{
					settingMenu_addTemplateCheckBox(tmpBr, lineNumber, 2, i, caseNumber);
					if ((tmpBr.ctrlTmpl[0]==true)||(tmpBr.ctrlTmpl[1]==true))
					{
						settingMenu_addTemplateControls(tmpBr, lineNumber, 2, i, caseNumber);
						lineNumber += 1;
						caseNumber++;
					}
				}
				// use case: average for bricks with 1 sensor 
				else
				{	
					settingMenu_addTemplateCheckBox(tmpBr, lineNumber, 1, i, caseNumber);
					if (tmpBr.ctrlTmpl[0]==true)
					{
						settingMenu_addTemplateControls(tmpBr, lineNumber, 1, i, caseNumber);
						lineNumber += 1;
						caseNumber++;
					}
				}				
				lineNumber += 1;
				//caseNumber++;				
				// ---------------------------------------------

				
				heightSum += lineNumber*settingRegionLineHeight
							+ (lineNumber-1)*settingRegionBetweenLineHeight
							+ 2*settingRegionBorderLineHeight
							+ settingRegionSeparateLineHeight+settingRegionSeparateHeight
							+ caseNumber*settingRegionBetweenLineHeight;				
								
				// ---------------------------------------------
				// separation line
				// ---------------------------------------------
				if (i!=connectionData.presentedBrickList.size()-1)
				{
					Label label_1 = new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL);
					label_1.setBounds(settingOffsetX, 
										heightSum-settingRegionSeparateLineHeight,
										settingRegionWidth-settingOffsetX*2, 
										settingRegionSeparateLineHeight);
					label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				// ---------------------------------------------
									
				group.pack();
			}
		}
        // update window size
		shell.setSize(shellStartWidth, shellHeight +heightSum);
		group.setBounds(settingRegionStartX, shellStartHeight + settingRegionSeparateHeight, settingRegionWidth, heightSum);		
		//shell.setSize(shellStartWidth, shellHeight + cnt*settingRegionHeight);
		//group.setBounds(settingRegionStartX, shellStartHeight + settingRegionSeparateHeight, settingRegionWidth, cnt*settingRegionHeight);
		//------------------------------------------------------------------------		
	}
				
	/**
	 * updates both Brick-lists and calls the corresponding function in windowController to update the treshold  
	 * @param tmpBr	Brick object
	 * @param perspectiveValue	new threshold value
	 */
	public static void updateTresholdMin1(Brick tmpBr, double perspectiveValue)
	{
		Brick.setThresholdMin1(connectionData.BrickList, tmpBr.uid, perspectiveValue);
		Brick.setThresholdMin1(connectionData.presentedBrickList, tmpBr.uid, perspectiveValue);
		windowController.updateTresholdMin1(tmpBr.uid, perspectiveValue);
	}		
	
	/**
	 * updates both Brick-lists and calls the corresponding function in windowController to update the upper deviation of the average  
	 * @param tmpBr	Brick object
	 * @param perspectiveValue	new threshold value
	 */
	public static void updateAvg(Brick tmpBr, double perspectiveValue, int index, boolean high)
	{
		functions.Events.updateAvgCntrlValues(tmpBr.uid, perspectiveValue, index, high);
	}
			
	/**
	 * updates both Brick-lists and calls the corresponding function in windowController to update the treshold  
	 * @param tmpBr	Brick object
	 * @param perspectiveValue	new threshold value
	 */
	public static void updateTresholdMin2(Brick tmpBr, double perspectiveValue)
	{
		Brick.setThresholdMin2(connectionData.BrickList, tmpBr.uid, perspectiveValue);
		Brick.setThresholdMin2(connectionData.presentedBrickList, tmpBr.uid, perspectiveValue);
		windowController.updateTresholdMin2(tmpBr.uid, perspectiveValue);
	}

	/**
	 * updates both Brick-lists and calls the corresponding function in windowController to update the treshold  
	 * @param tmpBr	Brick object
	 * @param perspectiveValue	new threshold value
	 */
	public static void updateTresholdMax1(Brick tmpBr, double perspectiveValue)
	{
		Brick.setThresholdMax1(connectionData.BrickList, tmpBr.uid, perspectiveValue);
		Brick.setThresholdMax1(connectionData.presentedBrickList, tmpBr.uid, perspectiveValue);
		windowController.updateTresholdMax1(tmpBr.uid, perspectiveValue);
	}

	/**
	 * updates both Brick-lists and calls the corresponding function in windowController to update the treshold  
	 * @param tmpBr	Brick object
	 * @param perspectiveValue	new threshold value
	 */
	public static void updateTresholdMax2(Brick tmpBr, double perspectiveValue)
	{
		Brick.setThresholdMax2(connectionData.BrickList, tmpBr.uid, perspectiveValue);
		Brick.setThresholdMax2(connectionData.presentedBrickList, tmpBr.uid, perspectiveValue);
		windowController.updateTresholdMax2(tmpBr.uid, perspectiveValue);
	}

	/**
	 * if an item in the item-tree was checked or unchecked we need to update 
	 * the list of presented items and show/hide it in the settings list.
	 */
	public static void itemChecked(String UID)
	{
		//addSettingTab(Brick.getBrick(UID));		
		if (Brick.getBrick(connectionData.BrickList,UID).checked1 == false)
		{
			functions.Events.sensorBoxChecked(UID);
			updateSettingTabs();					
		}
		else
		{
			functions.Events.sensorBoxUnchecked(UID);
			updateSettingTabs();
		}
		// switch on or off the "start button"
		showOrHideStartButton();
	}
		
	/**
	 * switch on/off "start button" due to the existence of any presented Bricks
	 */
	private static void showOrHideStartButton()
	{
		if (connectionData.presentedBrickList.isEmpty())
		{
			btnNewButton_1.setEnabled(false);
		}
		else
		{
			btnNewButton_1.setEnabled(true);
		}
	}
		
	/**
	 * set start button text to "start"
	 */
	public static void startButtonChangeTo_start()
	{
		btnNewButton_1.setText("Start");
		buttonStart = true;
	}
		
	/**
	 * set start button to "stop"
	 */
	public static void startButtonChangeTo_stop()
	{
		btnNewButton_1.setText("Stop");
		buttonStart = false;		
	}
		
	/**
	 * update Brick item and view in sensor window
	 * @param UID UID of the Brick
	 */
	/*
	public static void voltageChecked(String UID)
	{		
		if (Brick.getBrick(connectionData.BrickList,UID).checked2 == false)
		{
			// check the item
			Brick.check2Brick(Brick.getBrick(connectionData.BrickList,UID));		
			windowController.hideUnhidePlot(Brick.getBrick(connectionData.BrickList, UID),1);			
		}
		else
		{
			// uncheck the item
			Brick.uncheck2Brick(Brick.getBrick(connectionData.BrickList,UID));
			windowController.hideUnhidePlot(Brick.getBrick(connectionData.BrickList, UID),1);
		}
	}
	*/
	
	
	public static void simpleControlChecked(String UID, int index)
	{
		if (Brick.getBrick(connectionData.BrickList,UID).ctrlSimple[index] == false)
		{
			functions.Events.enableSimpleControl(UID, index);
		}
		else
		{
			functions.Events.disableSimpleControl(UID, index);
		}
		updateSettingTabPls = true;
	}


	/**
	 * update Brick item and view in sensor window
	 * @param UID UID of the Brick
	 */
	public static void averageControlChecked(String UID)
	{
		if (Brick.getBrick(connectionData.BrickList,UID).controlAverage == false)
		{
			functions.Events.enableAverageControl(UID, 1);
		}
		else
		{
			functions.Events.disableAverageControl(UID, 1);
		}
		updateSettingTabPls = true;
	}

	/**
	 * update Brick item and view in sensor window
	 * @param UID UID of the Brick
	 */
	public static void averageControlChecked2(String UID)
	{		
		if (Brick.getBrick(connectionData.BrickList,UID).controlAverage2 == false)
		{
			functions.Events.enableAverageControl(UID, 2);
		}
		else
		{
			functions.Events.disableAverageControl(UID, 2);
		}
		updateSettingTabPls = true;
	}	
			
	/**
	 * handle change in checkbox for template control
	 * @param UID
	 * @param index
	 * @return on
	 */
	public static boolean tmpltCtrlCheck(String UID, int index)
	{		
		if (Brick.getBrick(connectionData.BrickList,UID).ctrlTmpl[index] == false)
		{			
			// open file dialog
        	JFrame parentFrame = new JFrame();           	 
        	JFileChooser fileChooser = new JFileChooser();
        	FileNameExtensionFilter tmpfilter = new FileNameExtensionFilter("template files", objects.TemplatePlot.fileExtention);
        	fileChooser.setFileFilter(tmpfilter);
        	fileChooser.setDialogTitle("Specify a template file");
        	int userSelection = fileChooser.showOpenDialog(parentFrame);
        	if (userSelection == JFileChooser.APPROVE_OPTION)
        	{
        	    File fileToOpen = fileChooser.getSelectedFile();
        	    System.out.println("Open file: " + fileToOpen.getAbsolutePath());
        	    if (objects.TemplatePlot.isTemplateValid(fileToOpen.getAbsolutePath()))        	    
        	    {
        			functions.Events.enableTmpltCntrl(UID, index, fileToOpen.getAbsolutePath());
        			updateSettingTabPls = true;
        			return true;        	    	
        	    }
        	    else 
        	    {
        	    	data.dialogs.fileDataError(fileToOpen.getAbsolutePath());
        	    	return false;        	    	
        	    }
        	}
        	// end file dialog
		}
		else
		{
			functions.Events.disableTmpltCntrl(UID, index, "undefined");
			updateSettingTabPls = true;
			return false;
		}
		return true;
	}
					
	/**
	 * update Brick item and the view in sensor window
	 * @param UID UID of the Brick
	 */
	public static void ampereChecked(String UID)
	{		
	if (Brick.getBrick(connectionData.BrickList,UID).checked3 == false)
	{
		// check the item
		Brick.check3Brick(Brick.getBrick(connectionData.BrickList,UID));		
		windowController.hideUnhidePlot(Brick.getBrick(connectionData.BrickList, UID),2);			
	}
	else
	{
		// uncheck the item
		Brick.uncheck3Brick(Brick.getBrick(connectionData.BrickList,UID));
		windowController.hideUnhidePlot(Brick.getBrick(connectionData.BrickList, UID),2);
	}
}	
		
	/**
	 * if a new Brick was added into the configuration we have to 
	 * add it to the Brick-list and update the presented tree
	 * @param BrickList
	 */
	public static void newBrickAdded(final List<Brick> BrickList) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {			
				boolean parentChecked;
				tree.removeAll();				
				// refresh the whole tree
				for (Iterator<Brick> i = BrickList.iterator(); i.hasNext();) 
				{
					parentChecked = false;
					Brick tmpBrick = i.next();	
					//System.out.println(constants.enumTypeMap.get(tmpBrick.getEnumerationType()));
					TreeItem treeItemNew = new TreeItem(tree, SWT.NONE);
					//treeItemNew = new TreeItem(tree, 0);
					if (tmpBrick.checked1 == true)
					{
						treeItemNew.setChecked(true);
						parentChecked = true;
					}
					
					treeItemNew.setText(new String[] 
							{
								String.valueOf(constants.brickIdMap.get(tmpBrick.getDeviceIdentifier())),							
								tmpBrick.getUid(),								
								String.valueOf(constants.enumTypeMap.get((int)tmpBrick.getEnumerationType())),
								(String.valueOf(tmpBrick.getFirmwareVersion()[0])
									+ "."+ String.valueOf(tmpBrick.getFirmwareVersion()[1]) + "." + String.valueOf(tmpBrick.getFirmwareVersion()[2])),
									(String.valueOf(tmpBrick.getHardwareVersion()[0])
									+ "."+ String.valueOf(tmpBrick.getHardwareVersion()[1]) + "." + String.valueOf(tmpBrick.getHardwareVersion()[2])), });
					int subItemCnt = 0;
					// add subItems
					for (Iterator<Brick> i2 = tmpBrick.getSubBricksList(
							tmpBrick).iterator(); i2.hasNext();)							
					{	
						subItemCnt++;
						Brick tmpBrick2 = i2.next();
						TreeItem subItem = new TreeItem(treeItemNew, SWT.NONE);
						if ((tmpBrick2.checked1 == true) || (parentChecked == true))
						{
							subItem.setChecked(true);
						}						
						subItem.setText(new String[] 
								{
									String.valueOf(constants.brickIdMap.get(tmpBrick2.getDeviceIdentifier())),								
									tmpBrick2.getUid(),
									String.valueOf(constants.enumTypeMap.get((int)tmpBrick.getEnumerationType())),
									(String.valueOf(tmpBrick2.getFirmwareVersion()[0])
										+ "."
										+ String.valueOf(tmpBrick2.getFirmwareVersion()[1]) + "." + String.valueOf(tmpBrick2.getFirmwareVersion()[2])),
									(String.valueOf(tmpBrick2.getHardwareVersion()[0])+ "."+ String.valueOf(tmpBrick2.getHardwareVersion()[1])
										+ "." + String.valueOf(tmpBrick2.getHardwareVersion()[2])), 
								});}
					treeItemNew.setItemCount(subItemCnt);
					treeItemNew.setExpanded(true);					
					//treeItem = treeItemNew; 
					
					}}});
		}
	
	
	/*
	 * copied from http://www.coderanch.com/t/343024/GUI/java/Components-JComboBox
	 */
	/** adapted from comment section of ListCellRenderer api */
	static class CheckComboRenderer implements ListCellRenderer
	{
	    JCheckBox checkBox;
	  
	    public CheckComboRenderer()
	    {
	        checkBox = new JCheckBox();
	    }
	    public Component getListCellRendererComponent(JList list,
	                                                  Object value,
	                                                  int index,
	                                                  boolean isSelected,
	                                                  boolean cellHasFocus)
	    {
	        CheckComboStore store = (CheckComboStore)value;
	        checkBox.setText(store.id);
	        checkBox.setSelected(((Boolean)store.state).booleanValue());
	        checkBox.setBackground(isSelected ? Color.blue : Color.white);
	        checkBox.setForeground(isSelected ? Color.white : Color.black);
	        return checkBox;
	    }
	}

	/*
	 * copied from http://www.coderanch.com/t/343024/GUI/java/Components-JComboBox
	 */
	static class CheckComboStore
	{
	    String id;
	    Boolean state;
	  
	    public CheckComboStore(String id, Boolean state)
	    {
	        this.id = id;
	        this.state = state;
	    }
	}
}
			
/*
//--------------------------------------------------------------------------------------------------------------				
// scale
//--------------------------------------------------------------------------------------------------------------
final Scale scale = new Scale(group, SWT.NONE);
int min = (int)((double)constants.BrickMinValue.get(connectionData.presentedBrickList.get(i).getDeviceIdentifier())) ;
int max = (int)((double)constants.BrickMaxValue.get(connectionData.presentedBrickList.get(i).getDeviceIdentifier())) ;
final int pos = i;
final Brick updBr = tmpBr;
scale.setOrientation(SWT.RIGHT_TO_LEFT);
scale.setSelection((int)(max - connectionData.presentedBrickList.get(i).treshold));
scale.setMinimum(min);
scale.setMaximum(max);
scale.setBounds(settingOffsetX+settingRegionWidth/2, 
				settingRegionBorderLineHeight+i*settingRegionHeight, 
				width_one_half, 
				settingRegionLineHeight*2+settingRegionBetweenLineHeight);
scale.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
//formToolkit.adapt(scale, true, true);
scale.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        int perspectiveValue = scale.getMaximum() - scale.getSelection() + scale.getMinimum();
        System.out.println("Vol: " + perspectiveValue);
        updateTreshold(updBr, perspectiveValue);
        lblLum.setText(""+perspectiveValue+" "
        		+ String.valueOf(constants.BrickUnitMap.get( connectionData.presentedBrickList.get(pos).getDeviceIdentifier()))	);
        ;}});
//label.pack();
//--------------------------------------------------------------------------------------------------------------				
// scale end
//--------------------------------------------------------------------------------------------------------------
*/
