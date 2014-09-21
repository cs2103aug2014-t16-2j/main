package FlexiPlanner.Storage;

import java.time.LocalDateTime;

/**
 * @author A0117989H
 *
 */

public class Option {
	
	private boolean selectAll;
	private String category;
	private int taskIndex;
	private LocalDateTime startDateTime = LocalDateTime.of(1,1,1,0,0,0); // default
	private LocalDateTime endDateTime = LocalDateTime.of(1,1,1,0,0,0); // default
	
	/** Constructor Methods **/

	public Option() {
		new Option(false, null, 0, startDateTime, endDateTime);
	}
	
	public Option(boolean _selectAll) {
		new Option(_selectAll, null, 0, startDateTime, endDateTime);
	}
	
	public Option(String _category) {
		new Option(false, _category, 0, startDateTime, endDateTime);
	}
	
	public Option(int _taskIndex) {
		new Option(false, null, _taskIndex, startDateTime, endDateTime);
	}
	
	public Option(boolean _selectAll, String _category, int _taskIndex, LocalDateTime _startDateTime, LocalDateTime _endDateTime) {
		setSelectAll(_selectAll);
		setCategory(_category);
		setTaskIndex(_taskIndex);
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
	
	public int getTaskIndex() {
		return taskIndex;
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

	public void setTaskIndex(int _taskIndex) {
		this.taskIndex = _taskIndex;
	}

	public void setStartDateTime(LocalDateTime _startDateTime) {
		this.startDateTime = _startDateTime;
	}

	public void setEndDateTime(LocalDateTime _endDateTime) {
		this.endDateTime = _endDateTime;
	}
}
