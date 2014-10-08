package flexiplanner.storage;

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
	public JSONArray concatJsonArrs(JSONArray arr1, JSONArray arr2) {
		JSONArray mergedArray = new JSONArray();
		for (int i = 0; i < arr1.size(); i++) {
			mergedArray.add(arr1.get(i));
		}
		
		for (int i = 0; i < arr2.size(); i++) {
			mergedArray.add(arr2.get(i));
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
