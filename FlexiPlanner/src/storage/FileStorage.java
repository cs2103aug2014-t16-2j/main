package storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import commons.TaskData;

/**
 * This class implements functions of saving and loading
 * tasks from respective files.
 * The files must only contain task data in JSON format.
 * 
 * @author Moe Lwin Hein (A0117989H)
 *
 */

public class FileStorage implements Storage {
	
	private final String ERROR_IO = "IO Error!\n";
	private final String ERROR_PARSE = "Parse Error!\n";
	private final String ERROR_INVALID_FILE_NAME = "Invalid file name!\n";
	private final String ERROR_NULL_LIST = "List cannot be null!\n";
	private final String ERROR_NOT_SETUP_YET = "File record not found! Setup database first!\n";
	
	private final String INFO_FILE_ALD_EXISTS = "File exists!\n";
	private final String INFO_FILE_CREATED = "Database setup completed for : ";
	
	private final String NEXT_LINE = "\n";
	
	private List<String> path;
	
	private FileManager manager;
	private JsonConverter converter;
	
	private static FileStorage fStorageInstance;
	
	/** Singleton Constructor Method **/
	public static synchronized FileStorage getInstance() {
		if (fStorageInstance == null) {
			fStorageInstance = new FileStorage();
		}
		return fStorageInstance;
	}
	
	/** Constructor Method **/
	
	private FileStorage() {
		manager = new FileManager();
		converter = new JsonConverter();
		
		path = new ArrayList<String>();
	}
	
	@Override
	public boolean setupDatabase(final String filePath) {
		boolean isSetup = false;
		
		if (manager.isValidFileName(filePath)) {
			try {
				isSetup = manager.create(filePath);
				if (isSetup) {
					path.add(filePath);
					report(INFO_FILE_CREATED + filePath + NEXT_LINE);
				}
				else {
					report(INFO_FILE_ALD_EXISTS);
					path.add(filePath);
				}
			} catch (IOException e) {
				report(ERROR_IO);
				return false;
			}
		}
		else {
			isSetup = false;
			report(ERROR_INVALID_FILE_NAME);
		}
		
		return isSetup;
	}
	
	

	@Override
	public boolean saveTasks(final String filePath, ArrayList<TaskData> taskList) {
		boolean isSaveSuccess = false;
		
		if (path.isEmpty() || !path.contains(filePath)) {
			report(ERROR_NOT_SETUP_YET);
			return isSaveSuccess;
		}
		
		if (taskList == null) {
			report(ERROR_NULL_LIST);
			return isSaveSuccess;
		}
		
		try {
			JSONObject jObjToSave;
			JSONArray jArr;
			
			jArr = converter.tasksToJsonArr(taskList);
			jObjToSave = converter.encloseJsonArrInJsonObj(jArr);
			manager.writeInJsonFormat(filePath, jObjToSave, false);
			isSaveSuccess = true;	
		} catch (IOException e) {
			report(ERROR_IO);
			isSaveSuccess = false;
		}
		
		return isSaveSuccess;
	}

	@Override
	public ArrayList<TaskData> loadTasks(final String filePath) {
		ArrayList<TaskData> tasksToReturn = new ArrayList<TaskData>();
		
		if (path.isEmpty() || !path.contains(filePath)) {
			report(ERROR_NOT_SETUP_YET);
			return tasksToReturn;
		}
		
		try {
			if (manager.isEmptyFile(filePath)) {
				return tasksToReturn;
			}
			
			JSONObject jObj = manager.readInJsonFormat(filePath);
			JSONArray jArr = converter.retrieveJsonArrFromJsonObj(jObj);
			
			tasksToReturn = converter.jsonArrToTasks(jArr);
			
		} catch (IOException e) {
			report(ERROR_IO);
			tasksToReturn.clear();
		} catch (ParseException pe) {
			report(ERROR_PARSE);
			tasksToReturn.clear();
		}
		
		return tasksToReturn;
	}
	
	@Override
	public boolean saveFile(final String filePath, ArrayList<String> list) {
		boolean isSaveSuccess = false;
		
		if (path.isEmpty() || !path.contains(filePath)) {
			report(ERROR_NOT_SETUP_YET);
			return isSaveSuccess;
		}
		
		try {
			manager.write(filePath, "", false);
			
			for (String category : list) {
				manager.write(filePath, category, true);
				if (list.indexOf(category) != (list.size() - 1)) {
					manager.write(filePath, "\n", true);
				}
			}
			
			isSaveSuccess = true;
		} catch (IOException e) {
			report(ERROR_IO);
			isSaveSuccess = false;
		}
		
		return isSaveSuccess;
	}

	@Override
	public ArrayList<String> loadFile(final String filePath) {
		ArrayList<String> categories = new ArrayList<String>();
		
		if (path.isEmpty() || !path.contains(filePath)) {
			report(ERROR_NOT_SETUP_YET);
			return categories;
		}
		
		try {
			if (manager.isEmptyFile(filePath)) {
				return categories;
			}
			
			categories = manager.read(filePath);
		} catch (IOException e) {
			report(ERROR_IO);
			categories.clear();
		}
		
		return categories;
	}
	
	private void report(final String toReport) {
		System.out.print(toReport);
	}
}
