package FlexiPlanner.Storage;

import java.time.LocalDateTime;

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
	
	/** Constructor Method **/
	
	public TaskData() {
		new TaskData(null, null, null, null, null, false);
	}
	
	public TaskData(String _content, String _category, String _priority, 
			LocalDateTime _startDateTime, LocalDateTime _endDateTime, boolean _isDone) {
		this.setContent(_content);
		this.setCategory(_category);
		this.setPriority(_priority);
		this.setStartDateTime(_startDateTime);
		this.setEndDateTime(_endDateTime);
		this.setDone(_isDone);
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
}
