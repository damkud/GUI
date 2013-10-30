package GUI;

import java.io.File;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


public class General {
	private GridLayout mainLayout= new GridLayout(2, true);
	private Label topicLabel, depthLabel, timeLabel;
	private static Label relationLabel;
	private Font fontBorder;
	private static Spinner depthSpinner, timeSpinner;
	private static Button[] databaseButtons = new Button[2];
	private static Label locationLabel;
	private static FileChooser chooser;
	private static Combo combo;	
	private static String dbLocation="";
	 
	@PostConstruct
	public void createControls(Composite parent) {
		initComponents(parent);
		initLayouts(parent);
		initListeners(parent);
	}

	private void initListeners(Composite parent) {

		databaseButtons[0].addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
					
				locationLabel.setEnabled(false);
				chooser.setEnabled(false);
				
				relationLabel.setEnabled(true);
				combo.setEnabled(true);
				
			}
			

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		
		databaseButtons[1].addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
					
				locationLabel.setEnabled(true);
				chooser.setEnabled(true);
					
				relationLabel.setEnabled(false);
				combo.setEnabled(false);
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
	}

	private void initLayouts(Composite parent) {
		
		locationLabel.setEnabled(false);
		chooser.setEnabled(false);
		
		relationLabel.setEnabled(true);
		combo.setEnabled(true);
		
	    parent.setLayout(mainLayout);

	}

	private void initComponents(Composite parent) {
		
		mainLayout.horizontalSpacing = 5;
		mainLayout.verticalSpacing = 20;
		mainLayout.marginBottom = 7;
		mainLayout.marginTop = 7;
        
		fontBorder= new Font(parent.getDisplay(), new FontData("Arial", 13, SWT.ABORT));

		topicLabel= new Label(parent, SWT.CENTER);
		topicLabel.setText("Download Setting");
		topicLabel.setFont(fontBorder);
		
		GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        gridData.horizontalAlignment = GridData.FILL;
		topicLabel.setLayoutData(gridData);
        
		depthLabel= new Label(parent, SWT.LEFT);
	    depthLabel.setText("Depth");
	    
	    GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
	    depthSpinner= new Spinner(parent, SWT.SINGLE | SWT.ABORT);
	    depthSpinner.setLayoutData(gd);
	    
	    timeLabel= new Label(parent, SWT.NULL);
	    timeLabel.setText("Max Time");
	    
	    timeSpinner= new Spinner(parent, SWT.SINGLE | SWT.ABORT);
	    timeSpinner.setLayoutData(gd);

		databaseButtons[0] = new Button(parent, SWT.RADIO);
		databaseButtons[0].setSelection(true);
		databaseButtons[0].setText("Relation Database");
		databaseButtons[0].pack();
	 
		databaseButtons[1] = new Button(parent, SWT.RADIO);
		databaseButtons[1].setText("Graph Database");
		databaseButtons[1].pack();
		
		locationLabel= new Label(parent,SWT.CENTER);
		locationLabel.setText("Database Location");
		chooser= new FileChooser(parent);

		relationLabel= new Label(parent, SWT.LEFT);
		relationLabel.setText("Database:");
		relationLabel.setLayoutData(gridData);
		
		combo= new Combo(parent, SWT.CENTER);		
		combo.setText("Choose a relation database..");
		combo.add("MySQL");
		combo.add("Postgres");
		combo.setLayoutData(gridData);
		
	}

	@Focus
	  public void onFocus() {
	 //   text.setFocus();
	  }
	
	public class FileChooser extends Composite {
		 
		Text mText;
		Button mButton;
		String title = null;
	 
		public FileChooser(Composite parent) {
			super(parent, SWT.NULL);
			createContent();
		}
	 
		public void createContent() {
			GridLayout layout = new GridLayout(2, false);
			setLayout(layout);
	 
			mText = new Text(this, SWT.SINGLE | SWT.BORDER);
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = GridData.FILL;
			mText.setLayoutData(gd);
	 
	 
			mButton = new Button(this, SWT.NONE);
			mButton.setText("BROWSE");
			mButton.addSelectionListener(new SelectionListener() {
	 
				public void widgetDefaultSelected(SelectionEvent e) {
				}
	 
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dlg = new DirectoryDialog(mButton.getShell(),  SWT.OPEN  );
					dlg.setText("Open");
					String path = dlg.open();
					if (path == null) return;
					mText.setText(path);
					dbLocation= path;
				}
			});
		}
		
		public void setText(){
			
			mText.setText(dbLocation);
		}
	 
		public String getText() {
			return mText.getText();
	 
		}
	 
		public Text getTextControl() {
			return mText;		
		}
	 
		public File getFile() {
			String text = mText.getText();
			if (text.length() == 0) return null;
			return new File(text);
		}
	 
		public String getTitle() {
			return title;
		}
	 
		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	public static void setDbLocation(String loc){
		
		dbLocation= loc;
		chooser.setText();
	}
	
	public static String getDbLocation(){
		
		return dbLocation;
	}
	
	public static void setMaxTime(int val){
		
		timeSpinner.setSelection(val);
	}
	
	public static int getMaxTime(){
		
		return timeSpinner.getSelection();
	}
	
	public static void setMaxDepth(int val){
		
		depthSpinner.setSelection(val);
	}
	
	public static int getMaxDepth(){
		
		return depthSpinner.getSelection();
	}
	
	public static boolean isGraphDatabase(){
		
		return ((Button)databaseButtons[1]).getSelection();
	}
	
	public static String getRelationDatabase(){
		
		return combo.getText();
	}
	
	public static void setRelationButton(){
		
		((Button)databaseButtons[1]).setSelection(false);
		((Button)databaseButtons[0]).setSelection(true);
		
		locationLabel.setEnabled(false);
		chooser.setEnabled(false);
		
		relationLabel.setEnabled(true);
		combo.setEnabled(true);
	}
	
	public static void setGraphButton(){
		
		((Button)databaseButtons[0]).setSelection(false);
		((Button)databaseButtons[1]).setSelection(true);
		
		locationLabel.setEnabled(true);
		chooser.setEnabled(true);
			
		relationLabel.setEnabled(false);
		combo.setEnabled(false);
	}
}
