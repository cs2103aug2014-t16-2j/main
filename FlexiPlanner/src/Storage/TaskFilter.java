package Storage;

import java.time.LocalDateTime;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * This class filters the tasks according to :
 * Category, Start Time and End Time.
 * 
 * @author Moe Lwin Hein (A0117989H)
 *
 */
@SuppressWarnings("unchecked")
public class TaskFilter {
	
	private JSONArray jsonArr;
	private Option filterOption;
	
	/** Constructor Method **/
	
	public TaskFilter(JSONArray jsonArr, Option filterOption) {
		this.filterOption = filterOption;
		this.jsonArr = new JSONArray();
		clone(jsonArr);
	}
	
	/**
	 * This method filters the tasks by given filter option.
	 * Return the filtered task list.
	 * 
	 * @return filtered task list : JSONArray
	 */
	public JSONArray refine() {
		if (filterOption.getSelectAll()) {
			return jsonArr;
		}
		if (filterOption.getCategory() != null) {
			byCategory(filterOption.getCategory());
		}
		if (filterOption.getStartDateTime() != null && filterOption.getEndDateTime() == null) {
			byStartDate(filterOption.getStartDateTime().toString());
		}
		if (filterOption.getStartDateTime() == null && filterOption.getEndDateTime() != null) {
			byEndDate(filterOption.getEndDateTime().toString());
		}
		if (filterOption.getStartDateTime() != null && filterOption.getEndDateTime() != null) {
			byStartDate(filterOption.getStartDateTime().toString());
			byEndDate(filterOption.getEndDateTime().toString());
		}
		return jsonArr;
	}
	
	private void byCategory(String category) {
		int startIndex = jsonArr.size() - 1;
		for (int i = startIndex; i >= 0; i--) {
			JSONObject obj = (JSONObject) jsonArr.get(i);
			if ((obj.get("category") == null) || (!obj.get("category").equals(category))) { 
				jsonArr.remove(obj);
			}
		}
	}
	
	private void byStartDate(String startDate) {
		byDate(startDate, "startDateTime");
	}
	
	private void byEndDate(String endDate) {
		byDate(endDate, "endDateTime");
	}
	
	private void byDate(String key, String date) {
		int startIndex = jsonArr.size() - 1;
		for (int i = startIndex; i >= 0; i--) {
			JSONObject obj = (JSONObject) jsonArr.get(i);
			if (obj.get(date) == null) {
				jsonArr.remove(obj);
			}
			else {
				LocalDateTime DateTime = LocalDateTime.parse((String) obj.get(date));
				if (date.equals("startDateTime") && DateTime.isBefore(LocalDateTime.parse(key))) {
					jsonArr.remove(i);
				}
				else if (date.equals("endDateTime") && DateTime.isAfter(LocalDateTime.parse(key))) {
					jsonArr.remove(i);
				}
			}
		}
	}
	
	private void clone(JSONArray jsonArr) {
		for (int i = 0; i < jsonArr.size(); i++) {
			this.jsonArr.add(jsonArr.get(i));
		}
	}
}
