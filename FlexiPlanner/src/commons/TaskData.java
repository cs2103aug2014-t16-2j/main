package commons;

import java.time.LocalDateTime;

import org.json.simple.JSONObject;

import reminder.Reminder;
import storage.JsonConverter;

//@author A0117989H

/**
 * This class creates a task object representing an actual task.
 */

public class TaskData implements Comparable<TaskData> {
	
	private static final int MIN_ID = 10;
	private static final int MAX_ID = 100;
	
	//to extract numbers only from LocalDateTime string
	private final String REGEX_LDT = "[-:.T]";
	
	//priorities
	private final String HIGH_PRI = "high";
	private final String VERY_HIGH_PRI = "very high";

	private String content;
	private String category;
	private String priority;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private String taskId;
	private static int id = MIN_ID;

	private Reminder reminder;
	private LocalDateTime remindDateTime;

	//** Constructor Method **/

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

	//** Accessor Methods **/

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

	//** Mutator Methods **/

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
		//check that when app exited, there is reminder still on
		if (remindDateTime != null && reminder == null) {
			//LocalDateTime.Min is an instruction to clear the reminder
			if (remindDateTime.equals(LocalDateTime.MIN)) {
				remindDateTime = null;
				return;
			}
			//continue the reminder clock
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

	//** Other Methods **/

	public void generateTaskId() {
		//to make sure each ID is unique
		if (id == MAX_ID) {
			id = MIN_ID;
		}

		taskId = LocalDateTime.now().toString() + (++id);
		taskId = taskId.replaceAll(REGEX_LDT, "");
	}

	public JSONObject convertToJsonObject() {
		JsonConverter converter = new JsonConverter();
		JSONObject obj = new JSONObject();

		obj = converter.taskToJsonObj(this);

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
		sb.append("Deadline   =   " + this.getEndDateTime() + "\n");
		sb.append("Remind Time=   " + this.getRemindDateTime());
		sb.append("\n******************************************");

		return sb.toString();
	}

	//@author A0112066U
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
			if (this.getPriority().equals(VERY_HIGH_PRI)) {
				return -1;
			}
			if (prior.equals(VERY_HIGH_PRI)) {
				return 1;
			}
			if (this.getPriority().equals(HIGH_PRI)) {
				return -1;
			}
			if (prior.equals(HIGH_PRI)) {
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
			if (this.getPriority().equals(VERY_HIGH_PRI)) {
				return -1;
			}
			if (prior.equals(VERY_HIGH_PRI)) {
				return 1;
			}
			if (this.getPriority().equals(HIGH_PRI)) {
				return -1;
			}
			if (prior.equals(HIGH_PRI)) {
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
