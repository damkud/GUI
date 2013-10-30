package GUI;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.swing.SwingUtilities;

import logic.YtDownloadManager;
import logic.neo4j.Neo4jDbProxy;

import notification.MailThread;

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
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import api.DownloadManagerInterface;
import api.settings.CrawlerSettings;
import api.settings.io.SettingFileReaderInterface;
import api.settings.io.SettingsFileWriterInterface;
import api.settings.io.SettingsReaderAndWriterFactory;
import api.settings.io.UnsupportedFileExtensionException;

public class UpdateSetting {

	private static final String VIDEO_CLASS = "logic.neo4j.nodes.businessnodes.YtVideo";
	private static final String USER_CLASS = "logic.neo4j.nodes.businessnodes.YtUser";

	@SuppressWarnings("serial")
	private static final HashMap<String, TimeUnit> unitMap = new HashMap<String, TimeUnit>() {
		{
			put("HOURS", TimeUnit.HOURS);
			put("DAYS", TimeUnit.DAYS);
		}
	};

	private Composite parent;
	private GridLayout mainLayout = new GridLayout(3, false);
	private Label topicLabel, timeLabel, minDateLabel, maxDateLabel;
	private Font fontBorder, fontButton;
	private Spinner timeSpinner;
	private Combo combo;
	private Button minBox, maxBox;
	private DateTime minDate, maxDate;
	private Button start, save, load;
	private CrawlerSettings downloadSettings = new CrawlerSettings();
	private SettingsReaderAndWriterFactory settingsReaderAndWriterFactory = new SettingsReaderAndWriterFactory();

	@PostConstruct
	public void createControls(Composite parent) {

		initComponents(parent);
		initLayouts(parent);
		initListeners(parent);
	}

	private void initComponents(Composite parent) {

		this.parent = parent;

		mainLayout.horizontalSpacing = 5;
		mainLayout.verticalSpacing = 20;
		mainLayout.marginBottom = 7;
		mainLayout.marginTop = 7;

		fontBorder = new Font(parent.getDisplay(), new FontData("Arial", 13,
				SWT.ABORT));
		fontButton = new Font(parent.getDisplay(), new FontData("Arial", 9,
				SWT.BOLD));

		topicLabel = new Label(parent, SWT.CENTER);
		topicLabel.setText("Update Setting");
		topicLabel.setFont(fontBorder);

		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		topicLabel.setLayoutData(gridData);

		timeLabel = new Label(parent, SWT.LEFT);
		timeLabel.setText("Update data updated earlier than");

		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		timeSpinner = new Spinner(parent, SWT.SINGLE | SWT.ABORT);
		timeSpinner.setLayoutData(gd);

		combo = new Combo(parent, SWT.CENTER);
		combo.add("HOURS");
		combo.add("DAYS");
		combo.setText("HOURS");
		combo.setLayoutData(gd);

		minDateLabel = new Label(parent, SWT.LEFT);
		minDateLabel.setText("Min Date");

		minDate = new DateTime(parent, SWT.CENTER);

		minBox = new Button(parent, SWT.CHECK);

		maxDateLabel = new Label(parent, SWT.LEFT);
		maxDateLabel.setText("Max Date");

		maxDate = new DateTime(parent, SWT.CENTER);

		maxBox = new Button(parent, SWT.CHECK);

		initEndButtons(parent);
	}

	private void initEndButtons(Composite parent) {
		GridData gridD = new GridData(SWT.LEFT, SWT.LEFT, false, false);
		gridD.horizontalSpan = 1;
		gridD.verticalIndent = 80;
		gridD.verticalSpan = 1;

		start = new Button(parent, SWT.PUSH);
		start.setFont(fontButton);
		start.setText("Start download");
		start.setLayoutData(gridD);

		save = new Button(parent, SWT.PUSH);
		save.setFont(fontButton);
		save.setText("Save settings");
		save.setLayoutData(gridD);

		load = new Button(parent, SWT.PUSH);
		load.setFont(fontButton);
		load.setText("Load settings from file");
		load.setLayoutData(gridD);
	}

	private void initLayouts(Composite parent) {

		minDate.setEnabled(false);

		maxDate.setEnabled(false);

		parent.setLayout(mainLayout);

	}

	private void initListeners(Composite parent) {

		minBox.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {

				if (minBox.getSelection())
					minDate.setEnabled(true);
				else
					minDate.setEnabled(false);

			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		maxBox.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {

				if (maxBox.getSelection())
					maxDate.setEnabled(true);
				else
					maxDate.setEnabled(false);
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		start.addSelectionListener(new SelectionListener() {

		//	@Override
			public void widgetSelected(SelectionEvent e) {
				doStartButton();

			}

		//	@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		save.addSelectionListener(new SelectionListener() {

		//	@Override
			public void widgetSelected(SelectionEvent e) {

				doSaveAsFileButton();
			}

		//	@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		load.addSelectionListener(new SelectionListener() {

		//	@Override
			public void widgetSelected(SelectionEvent e) {

				doLoadFromFileButton();
			}

		//	@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

	}

	/**
	 * Saves setting to file
	 */
	private void doSaveAsFileButton() {

		setVideoYt(YoutubeGUI.getvButton());
		setId(YoutubeGUI.getIdText());

		Display display = parent.getDisplay();
		final Shell shell = new Shell(display);
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.open();
		dlg.setFilterExtensions(new String[] { "*.xml" });

		try {
			SettingsFileWriterInterface settingsFileWriter = this.settingsReaderAndWriterFactory
					.getSettingsFileWriter("xml");

			settingsFileWriter.writeSettingsToFile(
					getCrawlerSettings(),
					dlg.getFilterPath()
							+ "\\"
							+ dlg.getFileName().substring(0,
									dlg.getFileName().length() - 4));
		} catch (UnsupportedFileExtensionException e) {
			System.err
					.println("UnsupportedFileExtensionException: Cannot load settings file");
		} catch (IOException e) {
			System.err.println("IOException: Cannot load settings file");
		}

		System.out.println("File saved..");
	}

	/**
	 * Loads setting from file
	 */
	protected void doLoadFromFileButton() {

		Display display = parent.getDisplay();
		final Shell shell = new Shell(display);
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.open();
		dlg.setFilterExtensions(new String[] { "*.xml" });

		try {
			SettingFileReaderInterface settingsFileReader = this.settingsReaderAndWriterFactory
					.getSettingsFileReader("xml");
			CrawlerSettings crawlerSettings = settingsFileReader
					.readSettingFromFile(dlg.getFilterPath() + "\\"
							+ dlg.getFileName());
			setCrawlerSettings(crawlerSettings);
		} catch (UnsupportedFileExtensionException e) {

			System.err
					.println("UnsupportedFileExtensionException: Cannot load settings file");
		} catch (IOException e) {

			System.err.println("IOException: Cannot load settings file");
		} catch (ParseException e) {

			System.err.println("ParseException: Cannot load settings file");
		}

		System.out.println("File loaded..");
	}

	public void setDbLocation(String location) {

		downloadSettings.setDbLocation(location);
	}

	public void setDepth(int depth) {

		downloadSettings.setDepth(depth);
	}

	public void setVideoYt(boolean video) {

		if (video) {
			downloadSettings.setSeedClassName(VIDEO_CLASS);
		} else {
			downloadSettings.setSeedClassName(USER_CLASS);
		}
	}

	public void setId(String id) {

		downloadSettings.setSeed(id);
	}

	/**
	 * @param p_downloadSettings
	 */

	public void setCrawlerSettings(CrawlerSettings p_downloadSettings) {

		YoutubeGUI.setIdText(p_downloadSettings.getSeed());
		String seedClassName = p_downloadSettings.getSeedClassName();
		if (VIDEO_CLASS.equals(seedClassName)) {
			YoutubeGUI.setuButton(false);
			YoutubeGUI.setvButton(true);
		} else {
			YoutubeGUI.setuButton(true);
			YoutubeGUI.setvButton(false);
		}

		if (!p_downloadSettings.getDbLocation().equals("")) {

			General.setGraphButton();
		} else {
			General.setRelationButton();
		}
		General.setDbLocation(p_downloadSettings.getDbLocation());
		General.setMaxTime(p_downloadSettings.getMaxTime());
		General.setMaxDepth(p_downloadSettings.getDepth());

		timeSpinner.setSelection(p_downloadSettings.getUpdatedEarlierThan());
		if (p_downloadSettings.getUpdatedEarilerThanTimeUnit() == TimeUnit.HOURS) {
			combo.setText("HOURS");
		} else {
			combo.setText("DAYS");
		}

		Calendar cal = Calendar.getInstance();

		cal.setTime(p_downloadSettings.getMinUpdateDate());
		minDate.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) - 1,
				cal.get(Calendar.DAY_OF_MONTH));

		cal.setTime(p_downloadSettings.getMaxUpdateDate());
		maxDate.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) - 1,
				cal.get(Calendar.DAY_OF_MONTH));

	}

	/**
	 * @return crawler settings
	 */
	public CrawlerSettings getCrawlerSettings() {

		downloadSettings.setDbLocation(General.getDbLocation());
		downloadSettings.setMaxTime(General.getMaxTime());
		downloadSettings.setDepth(General.getMaxDepth());
		downloadSettings.setMaxTimeUnit(TimeUnit.MINUTES);
		downloadSettings.setUpdatedEarilerThanTimeUnit(unitMap.get(combo
				.getText()));
		String helpSpinner = timeSpinner.getText();
		int timeSpin = Integer.parseInt(helpSpinner);
		downloadSettings.setUpdatedEarlierThan(timeSpin);

		Date dateMin = getMinDateValue();
		Date dateMax = getMaxDateValue();

		downloadSettings.setMinUpdateDate(dateMin);
		downloadSettings.setMaxUpdateDate(dateMax);

		return downloadSettings;
	}

	private Date getMinDateValue() {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, minDate.getYear());
		cal.set(Calendar.MONTH, minDate.getMonth());
		cal.set(Calendar.DAY_OF_MONTH, minDate.getDay());

		Date dateMin = cal.getTime();
		return dateMin;
	}

	private Date getMaxDateValue() {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, maxDate.getYear());
		cal.set(Calendar.MONTH, maxDate.getMonth());
		cal.set(Calendar.DAY_OF_MONTH, maxDate.getDay());

		Date dateMax = cal.getTime();
		return dateMax;
	}

	protected void doStartButton() {

		final DownloadManagerInterface manager = new YtDownloadManager(getCrawlerSettings());
		manager.setDbProxy(Neo4jDbProxy.getNewInstance(getCrawlerSettings()
				.getDbLocation()));


	/*	if(MailChanger.isNotifi){
			
			Thread mailThread= new Thread(new MailThread());
			mailThread.start();
		}	
		*/
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		
				DownloadDialog dialog = new DownloadDialog(manager);
				dialog.setModal(true);
				dialog.setVisible(true);;
			}
		});
		

	}

}
