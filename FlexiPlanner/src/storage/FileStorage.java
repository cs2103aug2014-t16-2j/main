package storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import commons.TaskData;

//@author A0117989H

/**
 * This class implements functions of saving and loading
 * data from respective files.
 * 
 * For saving and loading task files, they must only contain task data in JSON format.
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
	
	private final String BASE_FOLDER_NAME = "FlexiPlanner Database";
	private final String NEXT_LINE = "\n";
	private final String SEPERATOR = "//";
	
	private final int MAX_ITERATION = 10000;
	
	private String folderName;
	
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
		createFolder();
	}
	
	@Override
	public boolean setupDatabase(final String fileName) {
		boolean isSetup = false;
		
		if (manager.isValidFileName(fileName)) {
			try {
				//redirect file to be created into the folder
				final String filePath = createFilePath(fileName);
				
				isSetup = manager.createFile(filePath);
				
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
	
	/** ******************** **/

	@Override
	public boolean saveTasks(final String fileName, ArrayList<TaskData> taskList) {
		boolean isSaveSuccess = false;
		
		final String filePath = createFilePath(fileName);
		
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
	
	/** ******************** **/

	@Override
	public ArrayList<TaskData> loadTasks(final String fileName) {
		ArrayList<TaskData> tasksToReturn = new ArrayList<TaskData>();
		
		final String filePath = createFilePath(fileName);
		
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
	
	/** ******************** **/
	
	@Override
	public boolean saveFile(final String fileName, ArrayList<String> list) {
		boolean isSaveSuccess = false;
		
		final String filePath = createFilePath(fileName);
		
		if (path.isEmpty() || !path.contains(filePath)) {
			report(ERROR_NOT_SETUP_YET);
			return isSaveSuccess;
		}
		
		try {
			manager.clearFile(filePath);
			
			for (String s : list) {
				manager.write(filePath, s, true);
				if (list.indexOf(s) != (list.size() - 1)) {
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
	
	/** ******************** **/

	@Override
	public ArrayList<String> loadFile(final String fileName) {
		ArrayList<String> categories = new ArrayList<String>();
		
		final String filePath = createFilePath(fileName);
		
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
	
	/** ******************** **/
	
	private void createFolder() {
		boolean isCreated = false;
		
		folderName = BASE_FOLDER_NAME;
		
		//check if BASE_FOLDER_NAME exists
		if (manager.hasFolder(folderName)) {
			return;
		}
		//check if the children derived from BASE_FOLDER exists
		for (int i = 1; i < MAX_ITERATION; i++) {
			if (manager.hasFolder(folderName + i)) {
				folderName = folderName + i; //set folder if found
				return;
			}
		}
		
		try {
			isCreated = manager.createFolder(folderName);
			
			if (!isCreated) {
				//if creating fails, create using another names
				for (int i = 1; i < MAX_ITERATION; i++) {
					if (manager.createFolder(folderName + i)) {
						folderName = folderName + i;
						break;
					}
				}
			}
		} catch (IOException e) {
			report(ERROR_IO);
		}
	}
	
	/** ******************** **/
	
	private String createFilePath(final String fileName) {
		return folderName + SEPERATOR + fileName;
	}
	
	/** ******************** **/
	
	private void report(final String toReport) {
		System.out.print(toReport);
	}
}
