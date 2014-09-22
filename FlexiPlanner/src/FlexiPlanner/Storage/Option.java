package FlexiPlanner.Storage;

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
		new Option(false, null, null, null);
	}
	
	public Option(boolean _selectAll) {
		new Option(_selectAll, null, null, null);
	}
	
	public Option(String _category) {
		new Option(false, _category, null, null);
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
}
