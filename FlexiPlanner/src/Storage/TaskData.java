package Storage;

import java.time.LocalDateTime;

import org.json.simple.JSONObject;

/**
 * This class creates a task object representing an actual task.
 * 
 * @author Moe Lwin Hein (A0117989H)
 *
 */

public class TaskData {
	
	private String content; 
	private String category;
	private String priority;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private String taskId;
	private static int id = 10;
	
	private Reminder reminder;
	
	/** Constructor Method **/
	
	public TaskData() {
		this(null, null, null, null, null);
	}
	
	public TaskData(String content) {
		this(content, null, null, null, null);
	}
	
	public TaskData(TaskData t) {
		this(t.getContent(), t.getCategory(), t.getPriority(), t.getStartDateTime(), 
				t.getEndDateTime());
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
	
	public void setReminder(final LocalDateTime dateTime) {
		this.reminder = new Reminder(dateTime, this.content);
		reminder.start();
	}
	
	public void clearReminder() {
		reminder.stop();
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
		if (this.startDateTime != null && !this.startDateTime.equals(other.startDateTime)) {
			return false;
		}
		if (this.endDateTime == null && other.endDateTime != null) {
			return false;
		}
		if (this.endDateTime != null && !this.endDateTime.equals(other.endDateTime)) {
			return false;
		}

		return true;
 	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("************** Task Details **************\n");
		sb.append("Task ID    =   "+ this.getTaskId() + "\n");
		sb.append("Content    =   "+ this.getContent() + "\n");
		sb.append("Category   =   "+ this.getCategory() + "\n");
		sb.append("Priority   =   "+ this.getPriority() + "\n");
		sb.append("Start Date =   "+ this.getStartDateTime() + "\n");
		sb.append("Deadline   =   "+ this.getEndDateTime());
		sb.append("\n******************************************");
		
		return sb.toString();
	}
}
