package flexiplanner.storage;

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
	
	public Option(boolean _selectAll) {
		this(_selectAll, null, null, null);
	}
	
	public Option(String _category) {
		this(false, _category, null, null);
	}
	
	public Option(LocalDateTime _startDateTime, LocalDateTime _endDateTime) {
		this(false, null, _startDateTime, _endDateTime);
	}
	
	public Option(boolean _selectAll, String _category, LocalDateTime _startDateTime, LocalDateTime _endDateTime) {
		setSelectAll(_selectAll);
		setCategory(_category);
		setStartDateTime(_startDateTime);
		setEndDateTime(_endDateTime);
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

	public void setSelectAll(boolean _selectAll) {
		this.selectAll = _selectAll;
	}

	public void setStartDateTime(LocalDateTime _startDateTime) {
		this.startDateTime = _startDateTime;
	}

	public void setEndDateTime(LocalDateTime _endDateTime) {
		this.endDateTime = _endDateTime;
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
