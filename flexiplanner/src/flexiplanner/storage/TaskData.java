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
	
	public TaskData(String _content, String _category, String _priority, 
			LocalDateTime _startDateTime, LocalDateTime _endDateTime, boolean _isDone) {
		this.setContent(_content);
		this.setCategory(_category);
		this.setPriority(_priority);
		this.setStartDateTime(_startDateTime);
		this.setEndDateTime(_endDateTime);
		this.setDone(_isDone);
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

	public void setContent(String _content) {
		this.content = _content;
	}

	public void setCategory(String _category) {
		this.category = _category;
	}

	public void setPriority(String _priority) {
		this.priority = _priority;
	}

	public void setStartDateTime(LocalDateTime _startDateTime) {
		this.startDateTime = _startDateTime;
	}

	public void setEndDateTime(LocalDateTime _endDateTime) {
		this.endDateTime = _endDateTime;
	}

	public void setDone(boolean _isDone) {
		this.isDone = _isDone;
	}
	
	public void setTaskId(String _taskId) {
		this.taskId = _taskId;
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
