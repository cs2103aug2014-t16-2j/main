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

public class TaskFileStorage implements Storage {
	
	private String filePath;
	private FileManager manager;
	private JsonCodec coder;
	private JsonFormatter formatter;
	
	/** Constructor Method **/
	
	public TaskFileStorage() throws IOException {
		this("tasks.json");
	}
	
	public TaskFileStorage(String filePath) throws IOException {
		assert filePath != null;
		
		this.setFilePath(filePath);
		
		manager = new FileManager();
		coder = new JsonCodec();
		formatter = new JsonFormatter();
		manager.create(this.filePath);
	}
	
	/** Accessor Method **/
	
	public String getFilePath() {
		return filePath;
	}
	
	/** Mutator Method **/

	public void setFilePath(String filePath) {
		assert filePath != null;
		
		this.filePath = filePath;
	}

	@Override
	public boolean saveData(ArrayList<TaskData> taskList, boolean isAppendable) {
		assert taskList != null;
		
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
	public ArrayList<TaskData> loadData(Option loadOption) throws IOException, ParseException{
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
