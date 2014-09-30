package FlexiPlanner.Storage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author A0117989H
 *
 */

public class JsonFormatter {
	
	@SuppressWarnings("unchecked")
	public JSONArray concatJsonArrs(JSONArray...arrs) {
		JSONArray mergedArray = new JSONArray();
		for (JSONArray arr : arrs) {
			for (int i = 0; i < arrs.length; i++) {
				mergedArray.add(arr.get(i));
			}
		}
		return mergedArray;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject concatJsonObjs(JSONObject obj1, JSONObject obj2) {
		JSONObject mergedObj = new JSONObject();
		mergedObj.put("Tasks", (JSONArray) obj1.get("Tasks"));
		mergedObj.put("Tasks2", (JSONArray) obj2.get("Tasks"));
		return mergedObj;
	}
	
	public String toPrettyFormat(Object obj) {
		if (obj instanceof JSONObject) {
			obj = (JSONObject) obj;
		}
		else if (obj instanceof JSONArray) {
			obj = (JSONArray) obj;
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		String prettyJson = gson.toJson(obj);
		return prettyJson;
	}
}
