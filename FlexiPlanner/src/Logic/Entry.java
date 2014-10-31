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

	LocalDateTime getStartDateTime()  {
		return start;
	}

	LocalDateTime getEndDateTime()  {
		return end;
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

