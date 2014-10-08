package flexiplanner.storage;

import java.time.LocalDateTime;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author A0117989H
 *
 */
@SuppressWarnings("unchecked")
public class Filter {
	
	private JSONArray jsonArr;
	private Option filterOption;
	
	public Filter(JSONArray jsonArr, Option filterOption) {
		this.filterOption = filterOption;
		this.jsonArr = new JSONArray();
		clone(jsonArr);
	}
	
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
