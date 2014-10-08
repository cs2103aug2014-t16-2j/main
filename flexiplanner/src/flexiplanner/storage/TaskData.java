package flexiplanner.storage;

import java.time.LocalDateTime;

import org.json.simple.JSONObject;

/**
 * @author A0117989H
 *
 */

public class TaskData {
	
	private String content; 
	private String category;
	private String priority;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private boolean isDone;
	private String taskId;
	private static int id = 10;
	
	/** Constructor Method **/
	
	public TaskData() {
		this(null, null, null, null, null, false);
	}
	
	public TaskData(TaskData t) {
		this(t.getContent(), t.getCategory(), t.getPriority(), t.getStartDateTime(), 
				t.getEndDateTime(), t.isDone());
		setTaskId(t.getTaskId());
	}
	
	public TaskData(String content, String category, String priority, 
			LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isDone) {
		this.setContent(content);
		this.setCategory(category);
		this.setPriority(priority);
		this.setStartDateTime(startDateTime);
		this.setEndDateTime(endDateTime);
		this.setDone(isDone);
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
	
	public boolean isDone() {
		return isDone;
	}
	
	public String getTaskId() {
		return taskId;
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

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	/** Other Methods **/
	
	public void generateTaskId() {
		if (id == 100) {
			id = 10;
		}
		
		taskId = LocalDateTime.now().toString() + (++id);
		taskId = taskId.replaceAll("[-:.T]", "");
	}
	
	public JSONObject getJsonObject() {
		JsonCodec coder = new JsonCodec();
		JSONObject obj = new JSONObject();
		
		obj = coder.encodeJsonObj(this);
		
		return obj;
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
		sb.append("Deadline   =   "+ this.getEndDateTime() + "\n");
		sb.append("Done       =   "+ this.isDone());
		sb.append("\n******************************************");
		
		return sb.toString();
	}
}
