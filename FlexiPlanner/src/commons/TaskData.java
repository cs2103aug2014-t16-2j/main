package commons;

import java.time.LocalDateTime;

import org.json.simple.JSONObject;

import reminder.Reminder;
import storage.JsonConverter;

/**
 * This class creates a task object representing an actual task.
 * 
 * @author Moe Lwin Hein (A0117989H)
 *
 */

public class TaskData implements Comparable<TaskData> {

	private String content;
	private String category;
	private String priority;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private String taskId;
	private static int id = 10;

	private Reminder reminder;
	private LocalDateTime remindDateTime;

	/** Constructor Method **/

	public TaskData() {
		this(null, null, null, null, null);
	}

	public TaskData(String content) {
		this(content, null, null, null, null);
	}

	public TaskData(TaskData t) {
		this(t.getContent(), t.getCategory(), t.getPriority(), t
				.getStartDateTime(), t.getEndDateTime());
		setTaskId(t.getTaskId());
	}

	public TaskData(String content, String category, String priority,
			LocalDateTime startDateTime, LocalDateTime endDateTime) {
		this.setContent(content);
		this.setCategory(category);
		this.setPriority(priority);
		this.setStartDateTime(startDateTime);
		this.setEndDateTime(endDateTime);
		if (this.getTaskId() == null) {
			this.generateTaskId();
		}
	}

	/** Accessor Methods **/

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

	public String getTaskId() {
		return taskId;
	}

	public LocalDateTime getRemindDateTime() {
		return remindDateTime;
	}

	public Reminder getReminder() {
		return reminder;
	}

	/** Mutator Methods **/

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

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setRemindDateTime(LocalDateTime remindDateTime) {
		this.remindDateTime = remindDateTime;

		if (this.remindDateTime != null) {
			setReminder();
		}
	}

	public void setReminder() {
		if (remindDateTime != null && reminder == null) {
			if (remindDateTime.equals(LocalDateTime.MIN)) {
				remindDateTime = null;
				return;
			}
			reminder = new Reminder(remindDateTime, this);
			reminder.start();
		}
	}

	public void clearReminder() {
		if (reminder != null) {
			reminder.stop();
			setRemindDateTime(null);
			reminder = null;
		}
	}

	/** Other Methods **/

	public void generateTaskId() {
		if (id == 100) {
			id = 10;
		}

		taskId = LocalDateTime.now().toString() + (++id);
		taskId = taskId.replaceAll("[-:.T]", "");
	}

	public JSONObject convertToJsonObject() {
		JsonConverter coder = new JsonConverter();
		JSONObject obj = new JSONObject();

		obj = coder.taskToJsonObj(this);

		return obj;
	}

	public boolean hasReminder() {
		return getReminder() != null;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}

		final TaskData other = (TaskData) object;

		if (this.content == null && other.content != null) {
			return false;
		}
		if (this.content != null && !this.content.equals(other.content)) {
			return false;
		}
		if (this.category == null && other.category != null) {
			return false;
		}
		if (this.category != null && !this.category.equals(other.category)) {
			return false;
		}
		if (this.priority == null && other.priority != null) {
			return false;
		}
		if (this.priority != null && !this.priority.equals(other.priority)) {
			return false;
		}
		if (this.startDateTime == null && other.startDateTime != null) {
			return false;
		}
		if (this.startDateTime != null
				&& !this.startDateTime.equals(other.startDateTime)) {
			return false;
		}
		if (this.endDateTime == null && other.endDateTime != null) {
			return false;
		}
		if (this.endDateTime != null
				&& !this.endDateTime.equals(other.endDateTime)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("************** Task Details **************\n");
		sb.append("Task ID    =   " + this.getTaskId() + "\n");
		sb.append("Content    =   " + this.getContent() + "\n");
		sb.append("Category   =   " + this.getCategory() + "\n");
		sb.append("Priority   =   " + this.getPriority() + "\n");
		sb.append("Start Date =   " + this.getStartDateTime() + "\n");
		sb.append("Deadline   =   " + this.getEndDateTime());
		sb.append("\n******************************************");

		return sb.toString();
	}

	@Override
	public int compareTo(TaskData task) {
		String prior = task.getPriority();
		LocalDateTime start = task.getStartDateTime();
		LocalDateTime end = task.getEndDateTime();

		if (this.startDateTime == null && start != null) {
			if (this.endDateTime == null) {
				return 1;
			}
			if (this.endDateTime.isBefore(end)) {
				return -1;
			}
			return 1;
		}
		if (this.startDateTime != null && start == null) {
			if (end == null) {
				return -1;
			}
			if (end.isBefore(this.startDateTime)) {
				return 1;
			}
			return -1;
		}
		if (this.startDateTime != null && start != null) {
			if (this.startDateTime.isAfter(start)) {
				return 1;
			}
			if (this.startDateTime.isBefore(start)) {
				return -1;
			}
		}
		if (this.endDateTime == null && end == null) {
			if (this.getPriority().equals("very high")) {
				return -1;
			}
			if (prior.equals("very high")) {
				return 1;
			}
			if (this.getPriority().equals("high")) {
				return -1;
			}
			if (prior.equals("high")) {
				return 1;
			}
			return 0;
 		}
		if (this.endDateTime == null) {
			return 1;
		}
		if (end == null) {
			return -1;
		}
		if (this.endDateTime.equals(end)) {
			if (this.getPriority().equals("very high")) {
				return -1;
			}
			if (prior.equals("very high")) {
				return 1;
			}
			if (this.getPriority().equals("high")) {
				return -1;
			}
			if (prior.equals("high")) {
				return 1;
			}
			return 0;
		}
		if (this.endDateTime.isAfter(end)) {
			return 1;
		}
		return -1;

	}
}
