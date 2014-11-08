package reminder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import commons.TaskData;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

//@author A0117989H

/**
 * This class is to setup, start and stop the reminder.
 *
 */

public class Reminder {
	
	private final String INFO_TIME_IS_OVER = "The schedule is over!\n";
	
	private final String WARNING_INTERRUPTED = "Thread is interrupted!\n";
	
	private final String NEXT_LINE = "\n";
	private final String FROM = "From: ";
	private final String TO   = "To  : ";
	private final String ON = "On: ";
	private final String BY = "By: ";
	
	private final int ZERO = 0;
	private final int FIVE = 5;
	
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	
	private LocalDateTime dateTime;
	private TaskData task;
	private String reminderText;
	
	public Reminder(LocalDateTime dateTime, TaskData task) {
		this.dateTime = dateTime;
		this.task = task;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);
		
		StringBuilder sb = new StringBuilder();
		sb.append(task.getContent());
		
		if (task.getStartDateTime() != null && task.getEndDateTime() != null) {
			sb.append(NEXT_LINE);
			sb.append(FROM + task.getStartDateTime().format(formatter));
			sb.append(NEXT_LINE);
			sb.append(TO + task.getEndDateTime().format(formatter));
		}
		
		else if (task.getStartDateTime() != null) {
			sb.append(NEXT_LINE);
			sb.append(ON + task.getStartDateTime().format(formatter));
		}
		
		else if (task.getEndDateTime() != null) {
			sb.append(NEXT_LINE);
			sb.append(BY + task.getEndDateTime().format(formatter));
		}
		
		this.reminderText = sb.toString();
	}
	
	/**
	 * start the reminder
	 */
    public void start() {
    	long ms = getDifferenceInMilliseconds();
    	scheduleReminder(ms, reminderText);
    	
    	try {
			TimeUnit.MILLISECONDS.sleep(FIVE);
		} catch (InterruptedException e) {
			report(WARNING_INTERRUPTED);
		}
    }
    
    /**
	 * stop the reminder
	 */
    public void stop() {
    	scheduler.shutdownNow();
    }

    private long getDifferenceInMilliseconds() {
        Calendar then = Calendar.getInstance();
        then.set(Calendar.YEAR, this.dateTime.getYear());
        then.set(Calendar.MONTH, this.dateTime.getMonthValue()-1);
        then.set(Calendar.DAY_OF_MONTH, this.dateTime.getDayOfMonth());
        then.set(Calendar.HOUR_OF_DAY, this.dateTime.getHour());
        then.set(Calendar.MINUTE, this.dateTime.getMinute());
        then.set(Calendar.SECOND, ZERO);
        
        Calendar now = Calendar.getInstance();
        
        if (now.getTimeInMillis() > then.getTimeInMillis()) {
            report(INFO_TIME_IS_OVER);
        }

        return then.getTimeInMillis()-now.getTimeInMillis();
    }
    
    /**
     * schedule the reminder and execute below when time's up
     * - remove reminder from task
     * - display pop-up
     * - stop the scheduler service
     */
    private void scheduleReminder(long ms, String reminderText) {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
            	task.clearReminder();
                ReminderPopup popup = new ReminderPopup();
                popup.displayPopupWSound(reminderText);
                stop();
            }
        }, ms, MILLISECONDS);
    }
    
    private void report(final String toReport) {
    	System.out.print(toReport);
    }
}
