package parser;

import java.time.LocalDateTime;

//@author A0111887Y
//This class creates a Task object that contains the Task data translated by the Parser class.

public class Task{
	
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private String content;
	private String category;
	private String priority;
	private int index;
	private boolean isDone;

	//@author A0111887Y
	
	public Task(){
		
		startDateTime = null;
		endDateTime = null;
		content = null;
		category = null;
		priority = null;
		index = 0;
		isDone = false;
	
	}
	
	//@author A0117989H
	/**
	 * Added this constructor class for convenience in unit testing. 
	 * 
	 * @param content
	 * @param category
	 * @param priority
	 * @param startDateTime
	 * @param endDateTime
	 * 
	 */
	public Task(String content, String category, String priority, 
			LocalDateTime startDateTime, LocalDateTime endDateTime) {
		this.setContent(content);
		this.setCategory(category);
		this.setPriority(priority);
		this.setStartDateTime(startDateTime);
		this.setEndDateTime(endDateTime);
		this.setDone(false);
	}
	
	/** end **/
	//@author A0111887Y
	
	public void setContent(String c){
	
		content = c;
	
	}

	//@author A0111887Y
	
	public String getContent(){
		
		return content;
	
	}

	//@author A0111887Y
	
	public void setStartDateTime(LocalDateTime ldt) {
		
		startDateTime = ldt;
		
	}

	//@author A0111887Y
	
	public void setEndDateTime(LocalDateTime ldt) {
		
		endDateTime = ldt;
		
	}

	//@author A0111887Y
	
	public LocalDateTime getStartDateTime() {
		
		return startDateTime;
	
	}

	//@author A0111887Y
	
	public LocalDateTime getEndDateTime() {
		
		return endDateTime;
	
	}

	//@author A0111887Y
	
	public void setPriority(String p) {
	
		priority = p;
	
	}

	//@author A0111887Y
	
	public String getPriority() {
	
		return priority;
	
	}

	//@author A0111887Y
	
	public void setIndex(int i) {
		
		index = i;
		
	}

	//@author A0111887Y
	
	public int getIndex() {
		
		return index;

	}

	//@author A0111887Y
	
	public void setDone(boolean d) {
	
		isDone = d;
	
	}

	//@author A0111887Y
	
	public Boolean isDone() {
	
		return isDone;
	
	}

	//@author A0111887Y
	
	public void setCategory(String c) {
	
		category = c;
	
	}

	//@author A0111887Y
	
	public String getCategory() {
	
		return category;
	
	}
	
	//@author A0117989H
	//Added this override method for convenience in testing.
	
	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		
		final Task other = (Task) object;
		
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
}
