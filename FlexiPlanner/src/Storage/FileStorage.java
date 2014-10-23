package Storage;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * This class implements functions of saving and loading
 * tasks from respective files.
 * The files must only contain task data in JSON format.
 * 
 * @author A0117989H
 *
 */

public class FileStorage implements Storage {
	
	private final String ERROR_IO = "IO Error!";
	private final String ERROR_PARSE = "Parse Error!";
	
	private FileManager manager;
	private JsonConverter coder;
	private JsonFormatter formatter;
	
	/** Constructor Method **/
	
	public FileStorage() {
		manager = new FileManager();
		coder = new JsonConverter();
		formatter = new JsonFormatter();
	}

	@Override
	public boolean saveTasks(String filePath, ArrayList<TaskData> taskList, boolean isAppendable) {
		boolean isSaveSuccess = false;
		
		try {
			JSONObject jObj, jObjToSave;
			JSONArray jArr1, jArr2, jArr;
			
			manager.create(filePath);
			
			if (isAppendable && !manager.isEmptyFile(filePath)) {
				jObj = manager.readInJsonFormat(filePath);
				jArr1 = coder.retrieveJsonArrFromJsonObj(jObj);
				jArr2 = coder.tasksToJsonArr(taskList);
				jArr = formatter.mergeJsonArrs(jArr1, jArr2);
				jObjToSave = coder.encloseJsonArrInJsonObj(jArr);
				manager.writeInJsonFormat(filePath, jObjToSave, false);
				isSaveSuccess = true;
			}
			else {
				jArr = coder.tasksToJsonArr(taskList);
				jObjToSave = coder.encloseJsonArrInJsonObj(jArr);
				manager.writeInJsonFormat(filePath, jObjToSave, false);
				isSaveSuccess = true;
			}
		} catch (IOException e) {
			reportError(ERROR_IO);
		} catch (ParseException pe) {
			reportError(ERROR_PARSE);
		}
		
		return isSaveSuccess;
	}

	@Override
	public ArrayList<TaskData> loadTasks(String filePath, Option loadOption) {
		ArrayList<TaskData> tasksToReturn = new ArrayList<TaskData>();
		
		try {
			manager.create(filePath);
			
			if (manager.isEmptyFile(filePath)) {
				return tasksToReturn;
			}
			
			JSONObject jObj = manager.readInJsonFormat(filePath);
			JSONArray jArr = coder.retrieveJsonArrFromJsonObj(jObj);
			
			tasksToReturn = coder.jsonArrToTasks(new TaskFilter(jArr, loadOption).refine());
			
		} catch (IOException e) {
			reportError(ERROR_IO);
		} catch (ParseException pe) {
			reportError(ERROR_PARSE);
		}
		
		return tasksToReturn;
	}
	
	private void reportError(final String err) {
		System.out.println(err);
	}
}
