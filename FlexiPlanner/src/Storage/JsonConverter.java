package Storage;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class handles encoding and decoding operations
 * among JSONObjects, JSONArrays, tasks and JSONStrings.
 * 
 * @author Moe Lwin Hein (A0117989H)
 *
 */
@SuppressWarnings("unchecked")
public class JsonConverter {
	
	public JSONObject taskToJsonObj(TaskData task) {
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
		
		return jsonObjToReturn;
	}

	public JSONArray tasksToJsonArr(ArrayList<TaskData> tasks) {
		JSONArray jsonArrToReturn = new JSONArray();
		
		for (int i = 0; i < tasks.size(); i++) {
			jsonArrToReturn.add(tasks.get(i).convertToJsonObject());
		}
		
		return jsonArrToReturn;
	}
	
	public ArrayList<TaskData> jsonArrToTasks(JSONArray jsonArr) {
		ArrayList<TaskData> taskListToReturn = new ArrayList<TaskData> ();
		
		for (int i = 0; i < jsonArr.size(); i++) {
			taskListToReturn.add(jsonObjToTask((JSONObject) jsonArr.get(i)));
		}
		
		return taskListToReturn;
	}
	
	public TaskData jsonObjToTask(JSONObject obj) {
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
		
		return taskToReturn;
	}
	
	public TaskData jsonStrToTask(String str) throws ParseException {
		JSONObject object = (JSONObject) JSONValue.parseWithException(str);
		
		return jsonObjToTask(object);
	}

	public JSONObject encloseJsonArrInJsonObj(JSONArray jarr) {
		JSONObject jo = new JSONObject();
		jo.put("Tasks", jarr);
		
		return jo;
	}
	
	public JSONArray retrieveJsonArrFromJsonStr(String strContainingObj) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject objContainingArr = (JSONObject) parser.parse(strContainingObj);
		
		return retrieveJsonArrFromJsonObj(objContainingArr);
	}
	
	public JSONArray retrieveJsonArrFromJsonObj(JSONObject jsonObj) {
		JSONArray jsonArrToReturn = (JSONArray) jsonObj.get("Tasks");
		
		return jsonArrToReturn == null ? new JSONArray() : jsonArrToReturn;
	}
}
