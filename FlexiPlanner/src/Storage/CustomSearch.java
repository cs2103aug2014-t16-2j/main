package Storage;

import java.time.LocalDateTime;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author A0117989H
 *
 */

public class CustomSearch {
	@SuppressWarnings("unchecked")
	public static JSONArray search(Option searchOption, JSONArray jsonArr) {
		JSONArray arrToReturn = new JSONArray();
		String searchKeyCat = searchOption.getCategory();
		LocalDateTime searchKeyStart = searchOption.getStartDateTime();
		LocalDateTime searchKeyEnd = searchOption.getEndDateTime();
		
		if (jsonArr.isEmpty() || searchOption.getSelectAll()) {
			return jsonArr;
		}
		else if (searchKeyCat != null) {
			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject obj = (JSONObject) jsonArr.get(i);
				if (obj.get("category").equals(searchKeyCat)) { 
					arrToReturn.add(obj);
				}
			}
			if (searchKeyStart != null && !arrToReturn.isEmpty()) {
				for (int i = 0; i < arrToReturn.size(); i++) {
					JSONObject obj = (JSONObject) arrToReturn.get(i);
					if (obj.get("startDateTime") != null) {
						LocalDateTime startDate = LocalDateTime.parse((String) obj.get("startDateTime"));
						if (startDate.isBefore(searchKeyStart)) {
							arrToReturn.remove(i);
						}
					}
				}
			}
			if (searchKeyEnd != null && !arrToReturn.isEmpty()) {
				for (int i = 0; i < arrToReturn.size(); i++) {
					JSONObject obj = (JSONObject) arrToReturn.get(i);
					if (obj.get("endDateTime") != null) {
						LocalDateTime endDate = LocalDateTime.parse((String) obj.get("endDateTime"));
						if (endDate.isAfter(searchKeyEnd)) {
							arrToReturn.remove(i);
						}
					}
				}
			}
		}
		else if (searchKeyStart != null) {
			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject obj = (JSONObject) jsonArr.get(i);
				if (obj.get("startDateTime") != null) { 
					LocalDateTime startDate = LocalDateTime.parse((String) obj.get("startDateTime"));
					if (startDate.isEqual(searchKeyStart) || startDate.isAfter(searchKeyStart)) {
						arrToReturn.add(obj);
					}
				}
			}
			if (searchKeyEnd != null && !arrToReturn.isEmpty()) {
				for (int i = 0; i < arrToReturn.size(); i++) {
					JSONObject obj = (JSONObject) arrToReturn.get(i);
					if (obj.get("endDateTime") != null) {
						LocalDateTime endDate = LocalDateTime.parse((String) obj.get("endDateTime"));
						if (endDate.isAfter(searchKeyEnd)) {
							arrToReturn.remove(i);
						}
					}
				}
			}
		}
		return arrToReturn;
	}
}
