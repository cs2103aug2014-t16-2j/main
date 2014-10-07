package Parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
	
	public void setDate(LocalDate ld) {
		
		if (startDateTime != null){
			if (startDateTime.getYear() == 0) {
				startDateTime = startDateTime.withDayOfMonth(ld.getDayOfMonth());
				startDateTime = startDateTime.withMonth(ld.getMonth().getValue());
				startDateTime = startDateTime.withYear(ld.getYear());
			} else if (endDateTime != null){
				if (endDateTime.getYear() == 0) {
					endDateTime = endDateTime.withDayOfMonth(ld.getDayOfMonth());
					endDateTime = endDateTime.withMonth(ld.getMonth().getValue());
					endDateTime = endDateTime.withYear(ld.getYear());
				}
			} else {
				endDateTime = LocalDateTime.of(ld, LocalTime.of(0, 0, 0));
			}
		} else {
			startDateTime = LocalDateTime.of(ld, LocalTime.of(0, 0, 0));
		}
		
	}
	
	public void setTime(LocalTime lt) {
		
		if (startDateTime != null){
			if (startDateTime.getHour() == 0 && startDateTime.getMinute() == 0 && startDateTime.getSecond() == 0) {
				startDateTime = startDateTime.withHour(lt.getHour());
				startDateTime = startDateTime.withMinute(lt.getMinute());
				startDateTime = startDateTime.withSecond(lt.getSecond());
			} else if (endDateTime != null){
				if (endDateTime.getHour() == 0 && endDateTime.getMinute() == 0 && endDateTime.getSecond() == 0) {
					endDateTime = endDateTime.withHour(lt.getHour());
					endDateTime = endDateTime.withMinute(lt.getMinute());
					endDateTime = endDateTime.withSecond(lt.getSecond());
				}
			} else {
				endDateTime = LocalDateTime.of(LocalDate.of(0, 1, 1), lt);
			}
		} else {
			startDateTime = LocalDateTime.of(LocalDate.of(0, 1, 1), lt);
		}
		
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