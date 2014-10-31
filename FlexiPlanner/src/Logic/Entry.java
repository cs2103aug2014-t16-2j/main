package Logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import Parser.Action;
import Parser.Task;
import Storage.TaskData;

class DateInfo {
	LocalDateTime start, end;

	DateInfo(LocalDateTime _start, LocalDateTime _end) {
		if (_start == null) {
			this.start = LocalDateTime.MIN;
		} else {
			this.start = _start;
		}
		if (_end == null) {
			this.end = LocalDateTime.MAX;
		} else {
			this.end = _end;
		}
	}

	String getStartDateTime() throws ParseException {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		SimpleDateFormat f = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

		Date d = formater.parse(start + "");
		return f.format(d);
	}

	String getEndDateTime() throws ParseException {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		SimpleDateFormat f = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

		Date d = formater.parse(end + "");
		return f.format(d);
	}

	@Override
	public boolean equals(Object t) {
		if (t instanceof DateInfo) {
			DateInfo time = (DateInfo) t;
			return this.start.equals(time.start) && this.end.equals(time.end);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		String s = start + "" + end;
		return s.hashCode();

	}
}

class ActionEntry {
	private Action action;
	private Task task;

	ActionEntry(Action action, Task task) {
		this.action = action;
		this.task = task;
	}

	public Action getAction() {
		return action;
	}

	public Task getTask() {
		return task;
	}
}

class DisplayedEntry {
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

	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
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