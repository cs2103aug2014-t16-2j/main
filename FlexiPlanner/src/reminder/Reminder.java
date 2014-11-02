package reminder;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Reminder {
	private final String INFO_TIME_IS_OVER = "The schedule is over!\n";
	
	private final String WARNING_INTERRUPTED = "Thread is interrupted!\n";
	
	private final int ZERO = 0;
	
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	
	private final LocalDateTime dateTime;
	private final String reminderText;
	
	public Reminder(final LocalDateTime dateTime, final String reminderText) {
		this.dateTime = dateTime;
		this.reminderText = reminderText;
	}

    public void start() {
    	long ms = getDifferenceInMilliseconds();
    	scheduleReminder(ms, reminderText);
    	
    	try {
			TimeUnit.MILLISECONDS.sleep(5);
		} catch (InterruptedException e) {
			report(WARNING_INTERRUPTED);
		}
    }
    
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

    private void scheduleReminder(long ms, final String reminderText) {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                ReminderPopup popup = new ReminderPopup();
                popup.reminderPopup(reminderText);
                stop();
            }
        }, ms, MILLISECONDS);
    }
    
    private void report(final String toReport) {
    	System.out.print(toReport);
    }
}
