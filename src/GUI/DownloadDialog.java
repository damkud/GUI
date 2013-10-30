package GUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import notification.MailThread;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import api.DownloadManagerInterface;
import api.communication.events.DownloadEndedEvent;
import api.communication.events.DownloadNodeRelatedDataFinishedEvent;
import api.communication.events.LevelDownloadEndedEvent;
import api.communication.events.ProcessingNodeDataStartedEvent;
import api.communication.listeners.DownloadFinishedListener;
import api.communication.listeners.DownloadNodeRelatedDataFinishedListener;
import api.communication.listeners.DownloadNodeRelatedDataStartedListener;
import api.communication.listeners.LevelDownloadEndedListener;
import api.communication.listeners.LevelDownloadStartedListener;
import api.communication.listeners.ProcessingNodeDataStartedListener;
import api.settings.CrawlerSettings;

/**
 * Dialog that displays download progress and statistics
 * @author Mateusz Kaczor
 *
 */
public class DownloadDialog extends JDialog{

	protected static final String SEED_LABEL = "Downloading data for %s %s";
	protected static final String USER = "user";
	protected static final String VIDEO = "video";
	protected static final String DOWNLOAD_DIALOG_DOWNLOADER_THREAD = "DownloadDialogDownloaderThread";
	protected static final String LOG_AREA_SEPARATOR = "---------------------------------------------\n";
	
	protected JTextArea logArea = null;
	protected TimerPanel elapsedTimeTimer = null;
	protected TimerPanel timeLeftPanel = null;
	protected JButton stopDownloadButton = null;
	protected DownloadManagerInterface downloadManager = null;
	protected JButton saveLogButton = null;
	protected JFileChooser fileChooser = null;
	protected JButton clearLogButton = null;
	protected AtomicBoolean downloadStopped = new AtomicBoolean(false);
	
	/**
	 * 
	 */
	protected void initListeners() {
		this.downloadManager.addProcessingNodeDataStartedListener(new ProcessingNodeDataStartedListener() {
			
			public void businessNodeRelatedDataDownloadStarted(
					ProcessingNodeDataStartedEvent p_event) {
				doProcessingNodeDataStarted(p_event);		
			}
		});
		this.downloadManager.addLevelDownloadEndedListener(new LevelDownloadEndedListener() {
			
			public void levelDownloadEnded(LevelDownloadEndedEvent p_event) {
				doLevelDownloadEnded(p_event);
			}
		});
		this.downloadManager.addLevelDownloadStartedListener(new LevelDownloadStartedListener() {
			
			public void levelDownloadStarted(int p_level) {
				doLevelDownloadStarted(p_level);
				
			}
		});
		this.downloadManager.addDownloadFinishedListener(new DownloadFinishedListener() {
			
			public void downloadFinished(DownloadEndedEvent p_event) {
				doDownloadFinished(p_event);
			}
		});
		this.downloadManager.addDownloadRelatedNodeDataStartedListener(new DownloadNodeRelatedDataStartedListener() {
			
			public void downloadNodeRelatedDataStarted(String p_dataName) {
				doRelatedNodeDataDownloadStarted(p_dataName);
				
			}
		});
		this.downloadManager.addDownloadRelatedNodeDataFinishedListener(new DownloadNodeRelatedDataFinishedListener() {
			
			public void downloadNodeRelatedData(
					DownloadNodeRelatedDataFinishedEvent p_event) {
				doRelatedNodeDataDownloadFinished(p_event);
			}
		});
	}
	
	/**
	 * 
	 */
	protected void startDownload() {


		
		long startTime = System.currentTimeMillis();
		this.elapsedTimeTimer.startCount(startTime);
		if (this.timeLeftPanel != null) {
			this.timeLeftPanel.startCount(startTime);
		}
	}
	
	/**
	 * @param p_level
	 */
	protected void nextLevelDownload(int p_level) {
		this.logArea.append("Downloading level " + p_level);

	}
	
	
	/**
	 * @param p_seed
	 * @param p_className
	 * @return
	 */
	protected JLabel getDownloadLabel(String p_seed, String p_className) {
		
		String labelText = String.format(SEED_LABEL, p_className, p_seed);
		return new JLabel(labelText);
	}
	
	/**
	 * @param p_settings
	 * @return
	 */
	protected TimerPanel getTimeLeftTimerPanel(CrawlerSettings p_settings) {
		int maxTime = p_settings.getMaxTime();
		if (maxTime <= 0) {
			return null;
		}
		TimeUnit maxTimeUnit = p_settings.getMaxTimeUnit();
		switch (maxTimeUnit) {
		case HOURS:
			return new TimerPanel(maxTime, 0);
		case MINUTES:
			return new TimerPanel(0, maxTime);
		default:
			throw new IllegalStateException("Time unit " + maxTimeUnit + " is not supported");
		}
	}
	
	/**
	 * @param p_level
	 */
	protected void doLevelDownloadStarted(int p_level) {
		this.logArea.append("Downloading level: " + p_level + "\n");
	}
	
	/**
	 * @param p_event
	 */
	protected void doProcessingNodeDataStarted(ProcessingNodeDataStartedEvent p_event) {
		
		this.logArea.append("\t" + p_event.getInfo() + "\n");
	}
	
	/**
	 * @param p_event
	 */
	protected void doLevelDownloadEnded(LevelDownloadEndedEvent p_event) {
		
		this.logArea.append(LOG_AREA_SEPARATOR);
		this.logArea.append("Downloading level " + p_event.getLevel() + " finished: " + p_event.getReason() + ". Statistics:\n");
		this.logArea.append(p_event.getStatistics().toString());
		this.logArea.append(LOG_AREA_SEPARATOR);
	}
	
	/**
	 * @param p_event
	 */
	protected void doDownloadFinished(DownloadEndedEvent p_event) {
		
		this.elapsedTimeTimer.stopCount();
		if (this.timeLeftPanel != null) {
			this.timeLeftPanel.stopCount();
		}
		this.stopDownloadButton.setEnabled(false);
		this.saveLogButton.setEnabled(true);
		this.downloadStopped.set(true);
		this.logArea.append(LOG_AREA_SEPARATOR);
		this.logArea.append("Download finished: " + p_event.getReason() + ". Statisctics:\n");
		this.logArea.append(p_event.getStatistics().toString());
		this.logArea.append(LOG_AREA_SEPARATOR);
	}
	
	/**
	 * @param p_dataType
	 */
	protected void doRelatedNodeDataDownloadStarted(String p_dataType) {
		this.logArea.append("\t\tDownloading " + p_dataType + "..." );
	}
	
	/**
	 * @param p_event
	 */
	protected void doRelatedNodeDataDownloadFinished(DownloadNodeRelatedDataFinishedEvent p_event) {
		this.logArea.append(p_event.getDownloaded() + " downloaded (" + p_event.getTimeElapsed() + ")\n");
	}
	
	/**
	 * @throws IOException
	 */
	protected void doSaveLogAction() throws IOException {
		int retVal = this.fileChooser.showSaveDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File file  = this.fileChooser.getSelectedFile();
			FileWriter writer = new FileWriter(file);
			writer.write(this.logArea.getText());
			writer.write("\nElapsed time: " + this.elapsedTimeTimer.getTimeLabelText());
			writer.close();
		}
	}
	
	/**
	 * @return
	 */
	protected boolean stopDownload() {
		if (this.downloadStopped.get()) {
			return true;
		}
		int showConfirmDialog = JOptionPane.showConfirmDialog(this,
				"Are you sure?",
				"Stoping download", JOptionPane.YES_NO_OPTION);
		if (showConfirmDialog == JOptionPane.YES_OPTION) {
			this.downloadStopped.set(true);
			this.elapsedTimeTimer.stopCount();
			if (this.timeLeftPanel != null) {
				this.timeLeftPanel.stopCount();
			}
			this.downloadManager.stopDownload();
			MailThread.ok=false;
			return true;
		}
		return false;
	}
	
	/**
	 * @param p_downloadManager
	 */
	public DownloadDialog(DownloadManagerInterface p_downloadManager) {
		
		this.downloadManager = p_downloadManager;
		initListeners();
		setSize(600, 600);
		this.logArea = new JTextArea();
		this.logArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)this.logArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.stopDownloadButton = new JButton("Stop download");
		this.stopDownloadButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				stopDownload();
			}
		});
		this.fileChooser = new JFileChooser();
		this.saveLogButton = new JButton("Save log");
		this.saveLogButton.setEnabled(false);
		this.saveLogButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				try {
					doSaveLogAction();
				} catch (IOException e1) {
					ErrorInfo errorInfo = new ErrorInfo("Cannot save file", e1.getMessage(), null, null, e1, null, null);
					JXErrorPane.showDialog(DownloadDialog.this, errorInfo);
				}
			}
		});
		this.clearLogButton = new JButton("Clear");
		this.clearLogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	
				logArea.setText("");
			}
		});
		this.elapsedTimeTimer = new TimerPanel();
		this.timeLeftPanel = getTimeLeftTimerPanel(p_downloadManager.getCrawlerSettings());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (stopDownload()) {
					DownloadDialog.this.dispose();
				}
			}	
			
			
		});
		setLayout(new BorderLayout());
		add(getDownloadLabel(p_downloadManager.getCrawlerSettings().getSeed(), p_downloadManager.getCrawlerSettings().getSeedClassName()), BorderLayout.NORTH);
		add(new JScrollPane(this.logArea), BorderLayout.CENTER);
		JPanel controlPanel = new JPanel();
		controlPanel.add(this.elapsedTimeTimer);
		if (this.timeLeftPanel != null) {
			controlPanel.add(this.timeLeftPanel);
		}
		controlPanel.add(this.stopDownloadButton);
		controlPanel.add(this.saveLogButton);
		controlPanel.add(this.clearLogButton);
		add(controlPanel, BorderLayout.SOUTH);
		long startTime = System.currentTimeMillis();
		this.elapsedTimeTimer.startCount(startTime);
		if (this.timeLeftPanel != null) {
			this.timeLeftPanel.startCount(startTime);
		}
		new Thread(new Runnable() {
			
			public void run() {
				try {
					downloadManager.downloadData();
				} catch (MalformedURLException e) {
					ErrorInfo errorInfo = new ErrorInfo("Download error", e.getMessage(), null, null, e, null, null);
					JXErrorPane.showDialog(DownloadDialog.this, errorInfo);
				} catch (IOException e) {
					ErrorInfo errorInfo = new ErrorInfo("Download error", e.getMessage(), null, null, e, null, null);
					JXErrorPane.showDialog(DownloadDialog.this, errorInfo);
				} catch (ClassNotFoundException e) {
					ErrorInfo errorInfo = new ErrorInfo("Download error", e.getMessage(), null, null, e, null, null);
					JXErrorPane.showDialog(DownloadDialog.this, errorInfo);
				}
				
			}
		}, DOWNLOAD_DIALOG_DOWNLOADER_THREAD).start();
	}
	
}
