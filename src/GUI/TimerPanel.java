package GUI;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel displaying elapsed/left time
 * @author Mateusz Kaczor
 *
 */
public class TimerPanel extends JPanel {

	protected static final String THREAD_NAME = "TimerPanelCountThread";
	protected static final String ZERO_TIME = "00:00:00";
	
	protected JLabel timerLabel = new JLabel();
	protected volatile AtomicBoolean count = new AtomicBoolean(true);
	protected int hours = -1;
	protected int minutes = -1;
	protected long startTime = 0;
	protected long maxTime = 0;
	
	/**
	 * @param p_text
	 */
	protected synchronized void setTimerLabelText(String p_text) {
		
		this.timerLabel.setText(p_text);
	}

	/**
	 * @param p_startTime time from which elapsed time is count
	 */
	protected void startElapsedTimeCount(long p_startTime) {
		this.startTime = p_startTime;
		new Thread(new Runnable() {
			
			public void run() {
				while (count.get()) {
					setTimerLabelText(getTimeElapsed());
				}
				
			}
		}, THREAD_NAME).start();
	}
	
	/**
	 * @return
	 */
	protected String getTimeLeft() {
		long timeLeft = this.maxTime - System.currentTimeMillis() + 1000;
		if (timeLeft <= 0) {
			return ZERO_TIME;
		}
		return getTime(timeLeft);
	}
	
	/**
	 * @return
	 */
	protected String getTimeElapsed() {
		long elapsedTime = System.currentTimeMillis() - this.startTime;
		return getTime(elapsedTime);
	}
	
	/**
	 * @param p_time time in millis
	 * @return time in format hh:mm:ss
	 */
	protected String getTime(long p_time) {
		p_time = p_time / 1000;
		String seconds = Integer.toString((int) (p_time % 60));
		String minutes = Integer.toString((int) ((p_time % 3600) / 60));
		String hours = Integer.toString((int) (p_time / 3600));
		if (seconds.length() < 2)
			seconds = "0" + seconds;
		if (minutes.length() < 2)
			minutes = "0" + minutes;
		if (hours.length() < 2)
			hours = "0" + hours;
		return hours + ":" + minutes + ":" + seconds;
	}
	
	/**
	 * @param p_startTime
	 */
	protected void startTimeLeftCount(long p_startTime) {
		Calendar current = Calendar.getInstance();
		current.setTimeInMillis(p_startTime);
		current.add(Calendar.HOUR, this.hours);
		current.add(Calendar.MINUTE, minutes);
		this.maxTime = current.getTimeInMillis();
		new Thread(new Runnable() {

			public void run() {
				while (count.get()) {
					String timeLeft = getTimeLeft();
					setTimerLabelText(timeLeft);
					if (timeLeft.equals(ZERO_TIME)) {
						break;
					}
				}

			}
		}, THREAD_NAME).start();
		
	}

	/**
	 * Creates TimerPanel for counting elapsed time
	 */
	public TimerPanel() {
		add(new JLabel("Elapsed time: "));
		add(this.timerLabel);
	}
	
	/**
	 * Creates TimerPanel for counting time left
	 * @param p_hours max hours
	 * @param p_minutes max minutes
	 */
	public TimerPanel(int p_hours, int p_minutes) {
		add(new JLabel("Time left: "));
		this.hours = p_hours;
		this.minutes = p_minutes;
		add(this.timerLabel);
	}
	
	
	/**
	 * @param p_startTime time in millis from which elapse/left time is counted
	 */
	public void startCount(long p_startTime) {
		if (this.hours != -1 && this.minutes != -1) {			
			startTimeLeftCount(p_startTime);
		} else {
			startElapsedTimeCount(p_startTime);
		}
	}
	
	/**
	 * stops time count. This method should be always called in order to prevent resources leak.
	 */
	public void stopCount() {
		this.count.set(false);
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		stopCount();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		stopCount();
	}
	
	/**
	 * @return
	 */
	public synchronized String getTimeLabelText() {
		
		return this.timerLabel.getText();
	}
}
