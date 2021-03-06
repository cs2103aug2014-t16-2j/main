package logic;

/**
 * other necessary classes
 * @author A0112066U
 */

import java.time.LocalDateTime;

import parser.Action;
import parser.Task;

class DateInfo {
	private LocalDateTime start, end;

	public DateInfo(LocalDateTime _start, LocalDateTime _end) {
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

	public LocalDateTime getStartDateTime() {
		return start;
	}

	public LocalDateTime getEndDateTime() {
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

	public ActionEntry(Action action, Task task) {
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
