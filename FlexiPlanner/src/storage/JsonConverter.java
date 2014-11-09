package storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import commons.TaskData;

//@author A0117989H

/**
 * This class handles encoding and decoding operations
 * among JSONObjects, JSONArrays, tasks and JSONStrings.
 *
 */

@SuppressWarnings("unchecked")
public class JsonConverter {
	
	private final String TASK_ID = "taskId";
	private final String TASK_CONTENT = "content";
	private final String TASK_CATEGORY = "category";
	private final String TASK_PRIORITY = "priority";
	private final String TASK_STARTDATE = "startDateTime";
	private final String TASK_ENDDATE = "endDateTime";
	private final String TASK_REMINDDATE = "remindDateTime";
	
	private final String TITLE_JSONARRAY = "Tasks";
	
	public JSONObject taskToJsonObj(TaskData task) {
		JSONObject jsonObjToReturn = new JSONObject();
		
		String startDateTime = parseToStringIfNotNull(task.getStartDateTime());
		String endDateTime = parseToStringIfNotNull(task.getEndDateTime());
		String remindDateTime = parseToStringIfNotNull(task.getRemindDateTime());
		
		jsonObjToReturn.put(TASK_ID, task.getTaskId());
		jsonObjToReturn.put(TASK_CONTENT, task.getContent());
		jsonObjToReturn.put(TASK_CATEGORY, task.getCategory());
		jsonObjToReturn.put(TASK_PRIORITY, task.getPriority());
		jsonObjToReturn.put(TASK_STARTDATE, startDateTime);
		jsonObjToReturn.put(TASK_ENDDATE, endDateTime);
		jsonObjToReturn.put(TASK_REMINDDATE, remindDateTime);
		
		return jsonObjToReturn;
	}
	
	private String parseToStringIfNotNull(LocalDateTime ldt) {
		if(ldt != null) {
			return ldt.toString();
		}
		
		return null;
	}
	
	/** ******************** **/

	public JSONArray tasksToJsonArr(ArrayList<TaskData> tasks) {
		JSONArray jsonArrToReturn = new JSONArray();
		
		for (int i = 0; i < tasks.size(); i++) {
			jsonArrToReturn.add(tasks.get(i).convertToJsonObject());
		}
		
		return jsonArrToReturn;
	}
	
	/** ******************** **/
	
	public TaskData jsonObjToTask(JSONObject obj) {
		TaskData taskToReturn = new TaskData();
		
		taskToReturn.setTaskId((String)obj.get(TASK_ID));
		taskToReturn.setContent((String)obj.get(TASK_CONTENT));
		taskToReturn.setCategory((String)obj.get(TASK_CATEGORY));
		taskToReturn.setPriority((String)obj.get(TASK_PRIORITY));
		taskToReturn.setStartDateTime(parseToLocalDateTimeIfNotNull((String) obj.get(TASK_STARTDATE)));
		taskToReturn.setEndDateTime(parseToLocalDateTimeIfNotNull((String) obj.get(TASK_ENDDATE)));
		taskToReturn.setRemindDateTime(parseToLocalDateTimeIfNotNull((String) obj.get(TASK_REMINDDATE)));
		
		return taskToReturn;
	}
	
	private LocalDateTime parseToLocalDateTimeIfNotNull(String str) {
		if (str != null) {
			return LocalDateTime.parse(str);
		}
		
		return null;
	}
	
	/** ******************** **/
	
	public ArrayList<TaskData> jsonArrToTasks(JSONArray jsonArr) {
		ArrayList<TaskData> taskListToReturn = new ArrayList<TaskData> ();
		
		for (int i = 0; i < jsonArr.size(); i++) {
			taskListToReturn.add(jsonObjToTask((JSONObject) jsonArr.get(i)));
		}
		
		return taskListToReturn;
	}
	
	/** ******************** **/

	public JSONObject encloseJsonArrInJsonObj(JSONArray jarr) {
		JSONObject jo = new JSONObject();
		jo.put(TITLE_JSONARRAY, jarr);
		
		return jo;
	}
	
	/** ******************** **/
	
	public JSONArray retrieveJsonArrFromJsonObj(JSONObject jsonObj) {
		JSONArray jsonArrToReturn = (JSONArray) jsonObj.get(TITLE_JSONARRAY);
		
		if (jsonArrToReturn == null) {
			
			return new JSONArray();
		}
		
		return jsonArrToReturn;
	}
	
	/** ******************** **/
	
	public JSONObject getJsonObjFromFile(BufferedReader br) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(br);
		
		return jsonObj;
	}
	
	/**
	 * This method transforms JSONObject display
	 * into a more readable string format.
	 * 
	 * @param JSONObject : JSONObject
	 * @return formatted string : String
	 */
	public String toPrettyFormat(Object obj) {
		if (obj instanceof JSONObject) {
			obj = (JSONObject) obj;
		}
		
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		String prettyJson = gson.toJson(obj);
		
		return prettyJson;
	}
}
