package Parser;

import java.time.LocalDateTime;

public class Task{
	
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private String content;
	private String category;
	private String priority;
	private boolean isDone;
	
	public Task(){
		
		startDateTime = null;
		endDateTime = null;
		content = null;
		category = null;
		priority = null;
		isDone = false;
	
	}
	
	public void setContent(String c){
	
		content = c;
	
	}
	
	public String getContent(){
		
		return content;
	
	}
	
	public void setStartDateTime(LocalDateTime ldt) {
		
		startDateTime = ldt;
		
	}
	
	public void setEndDateTime(LocalDateTime ldt) {
		
		endDateTime = ldt;
		
	}
	
	public LocalDateTime getStartDateTime() {
		
		return startDateTime;
	
	}
	
	public LocalDateTime getEndDateTime() {
		
		return endDateTime;
	
	}

	public void setPriority(String p) {
	
		priority = p;
	
	}
	
	public String getPriority() {
	
		return priority;
	
	}

	public void setDone(boolean d) {
	
		isDone = d;
	
	}
	
	public boolean isDone() {
	
		return isDone;
	
	}

	public void setCategory(String c) {
	
		category = c;
	
	}
	
	public String getCategory() {
	
		return category;
	
	}

}