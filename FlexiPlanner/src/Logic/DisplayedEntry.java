package Logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import Storage.TaskData;

public class DisplayedEntry {
	private String content;
	private String category;
	private String priority;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;

	public DisplayedEntry(TaskData t) {
		this.setContent(t.getContent());
		this.setCategory(t.getCategory());
		this.setPriority(t.getPriority());
		this.setStartDateTime(t.getStartDateTime());
		this.setEndDateTime(t.getEndDateTime());
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
		SimpleDateFormat f = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

		Date d = formater.parse(startDateTime + "");
		return f.format(d);
	}

	public String getEndDateTime() throws ParseException {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		SimpleDateFormat f = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

		Date d = formater.parse(endDateTime + "");
		return f.format(d);
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
