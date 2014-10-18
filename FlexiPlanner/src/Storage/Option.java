package Storage;

import java.time.LocalDateTime;

/**
 * @author A0117989H
 *
 */

public class Option {
	
	private boolean selectAll;
	private String category;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	
	/** Constructor Methods **/

	public Option() {
		this(false, null, null, null);
	}
	
	public Option(boolean selectAll) {
		this(selectAll, null, null, null);
	}
	
	public Option(String category) {
		this(false, category, null, null);
	}
	
	public Option(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		this(false, null, startDateTime, endDateTime);
	}
	
	public Option(boolean selectAll, String category, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		setSelectAll(selectAll);
		setCategory(category);
		setStartDateTime(startDateTime);
		setEndDateTime(endDateTime);
	}
	
	/** Accessor Methods **/

	public boolean getSelectAll() {
		return selectAll;
	}
	
	public String getCategory() {
		return category;
	}
	
	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}
	
	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}
	
	/** Mutator Methods **/
	
	public void setCategory(String category) {
		this.category = category;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}
	
	@Override
	public String toString(){
		String startDate = null;
		String endDate = null;
		
		if (this.getStartDateTime() != null) {
			startDate = this.getStartDateTime().toString();
		}
		if (this.getEndDateTime() != null) {
			endDate = this.getEndDateTime().toString();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("************** Load Options **************\n");
		sb.append("Select All  =   "+ this.getSelectAll() + "\n");
		sb.append("Category    =   "+ this.getCategory() + "\n");
		sb.append("Start Date  =   "+ startDate + "\n");
		sb.append("End Date    =   "+ endDate);
		sb.append("\n******************************************");
		
		return sb.toString();
	}
}
