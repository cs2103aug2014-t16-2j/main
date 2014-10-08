package Storage;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author A0117989H
 *
 */

public class JsonCodec {
	
	@SuppressWarnings("unchecked")
	public JSONObject encodeJsonObj(TaskData task) {
		JSONObject jsonObjToReturn = new JSONObject();
		String startDateTime = null;
		String endDateTime = null;
		if (task.getStartDateTime() != null) {
			startDateTime = task.getStartDateTime().toString();
		}
		if (task.getEndDateTime() != null) {
			endDateTime = task.getEndDateTime().toString();
		}
		jsonObjToReturn.put("taskId", task.getTaskId());
		jsonObjToReturn.put("content", task.getContent());
		jsonObjToReturn.put("category", task.getCategory());
		jsonObjToReturn.put("priority", task.getPriority());
		jsonObjToReturn.put("startDateTime", startDateTime);
		jsonObjToReturn.put("endDateTime", endDateTime);
		jsonObjToReturn.put("isDone", Boolean.toString(task.isDone()));
		return jsonObjToReturn;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray encodeJsonArr(ArrayList<TaskData> tasks) {
		JSONArray jsonArrToReturn = new JSONArray();
		for (int i = 0; i < tasks.size(); i++) {
			jsonArrToReturn.add(tasks.get(i).getJsonObject());
		}
		return jsonArrToReturn;
	}
	
	public ArrayList<TaskData> decodeJsonArr(JSONArray jsonArr) {
		ArrayList<TaskData> taskListToReturn = new ArrayList<TaskData> ();
		for (int i = 0; i < jsonArr.size(); i++) {
			taskListToReturn.add(decodeJsonObj((JSONObject) jsonArr.get(i)));
		}
		return taskListToReturn;
	}
	
	public TaskData decodeJsonObj(JSONObject obj) {
		TaskData taskToReturn = new TaskData();
		taskToReturn.setTaskId((String)obj.get("taskId"));
		taskToReturn.setContent((String)obj.get("content"));
		taskToReturn.setCategory((String)obj.get("category"));
		taskToReturn.setPriority((String)obj.get("priority"));
		if (obj.get("startDateTime") == null) {
			taskToReturn.setStartDateTime(null);
		}
		else {
			taskToReturn.setStartDateTime(LocalDateTime.parse((String)obj.get("startDateTime")));
		}
		if (obj.get("endDateTime") == null) {
			taskToReturn.setEndDateTime(null);
		}
		else {
			taskToReturn.setEndDateTime(LocalDateTime.parse((String)obj.get("endDateTime")));
		}
		taskToReturn.setDone(Boolean.parseBoolean((String)obj.get("isDone")));
		return taskToReturn;
	}
	
	public TaskData decodeJsonStr(String str) throws ParseException {
		JSONObject object = (JSONObject) JSONValue.parseWithException(str);
		return decodeJsonObj(object);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject putToJsonObj(JSONArray jarr) {
		JSONObject jo = new JSONObject();
		jo.put("Tasks", jarr);
		return jo;
	}
	
	public JSONArray seperateJsonArrFromStr(String strContainingObj) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject objContainingArr = (JSONObject) parser.parse(strContainingObj);
		return seperateJsonArrFromObj(objContainingArr);
	}
	
	public JSONArray seperateJsonArrFromObj(JSONObject jsonObj) {
		JSONArray jsonArrToReturn = (JSONArray) jsonObj.get("Tasks");
		return jsonArrToReturn == null ? new JSONArray() : jsonArrToReturn;
	}
}
