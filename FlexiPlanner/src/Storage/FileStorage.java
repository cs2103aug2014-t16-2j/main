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
	
	private FileManager manager;
	private JsonCodec coder;
	private JsonFormatter formatter;
	
	/** Constructor Method **/
	
	public FileStorage() throws IOException {
		manager = new FileManager();
		coder = new JsonCodec();
		formatter = new JsonFormatter();
	}

	@Override
	public boolean saveTasks(String filePath, ArrayList<TaskData> taskList, boolean isAppendable) throws IOException {
		assert filePath != null;
		assert taskList != null;
		
		manager.create(filePath);
		
		boolean isSaveSuccess = false;
		try {
			JSONObject jObj, jObjToSave;
			JSONArray jArr1, jArr2, jArr;
			
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
			isSaveSuccess = false;
		} catch (ParseException pe) {
			isSaveSuccess = false;
		}
		return isSaveSuccess;
	}

	@Override
	public ArrayList<TaskData> loadTasks(String filePath, Option loadOption) throws IOException, ParseException{
		assert loadOption != null;
		
		ArrayList<TaskData> tasksToReturn = new ArrayList<TaskData>();
		try {
			JSONObject jObj = manager.readInJsonFormat(filePath);
			JSONArray jArr = coder.retrieveJsonArrFromJsonObj(jObj);
			
			tasksToReturn = coder.jsonArrToTasks(new TaskFilter(jArr, loadOption).refine());
			
		} catch (IOException e) {
			throw e;
		} catch (ParseException pe) {
			throw pe;
		}
		
		return tasksToReturn;
	}
}
