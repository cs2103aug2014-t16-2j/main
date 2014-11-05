package logic;

/**
 * This class represents an entry that is to be displayed in UI
 * It is constructed from corresponding TaskData
 * @author A0112066
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import reminder.Reminder;
import commons.TaskData;

public class DisplayedEntry {
	private String content;
	private String category;
	private String priority;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private Reminder reminder;
	private LocalDateTime remindDateTime;

	public DisplayedEntry(TaskData t) {
		this.setContent(t.getContent());
		this.setCategory(t.getCategory());
		this.setPriority(t.getPriority());
		this.setStartDateTime(t.getStartDateTime());
		this.setEndDateTime(t.getEndDateTime());
		this.remindDateTime = t.getRemindDateTime();
		this.reminder = t.getReminder();
	}

	public String getContent() {
		return content;
	}

	public String getCategory() {
		return category;
	}

	public String getPriority() {
		return priority;
	}

	public String getStartDateTime() throws ParseException {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		SimpleDateFormat f = new SimpleDateFormat("d MMM yyyy HH:mm");

		Date d = formater.parse(startDateTime + "");
		return f.format(d);
	}

	public String getEndDateTime() throws ParseException {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		SimpleDateFormat f = new SimpleDateFormat("d MMM yyyy HH:mm");

		Date d = formater.parse(endDateTime + "");
		return f.format(d);
	}
	
	public LocalDateTime getRemindDateTime() {
		return remindDateTime;
	}
	
	public Reminder getReminder() {
		return reminder;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}
}
