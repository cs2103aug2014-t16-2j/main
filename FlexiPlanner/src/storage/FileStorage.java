package storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
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
	private final String BACKUP = "-backup.";
	private final String NOTHING = "";
	
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
		boolean isValid = manager.isValidFileName(fileName);
		
		if (!isValid) {
			report(ERROR_INVALID_FILE_NAME);
			
			return isSetup;
		}
		
		isSetup = implementingSetupAndBackup(fileName);
		
		return isSetup;
	}
	
	//** ******************** **/

	@Override
	public boolean saveTasks(final String fileName, ArrayList<TaskData> taskList) {
		boolean isSaveSuccess = false;
		
		String filePath = manager.createFilePath(folderName, fileName);
		String backupFilePath = getBackupFilePath(fileName);
		
		if (isDataBaseNotReadyFor(filePath)) {
			report(ERROR_NOT_SETUP_YET);
			
			return isSaveSuccess;
		}
		
		if (isNullList(taskList)) {
			report(ERROR_NULL_LIST);
			
			return isSaveSuccess;
		}
		
		isSaveSuccess = convertTasksToJsonObjAndSave(filePath, backupFilePath, taskList);
		
		return isSaveSuccess;
	}
	
	//** ******************** **/

	@Override
	public ArrayList<TaskData> loadTasks(final String fileName) {
		ArrayList<TaskData> tasksToReturn = new ArrayList<TaskData>();
		
		String filePath = manager.createFilePath(folderName, fileName);
		String backupFilePath = getBackupFilePath(fileName);
		
		if (isDataBaseNotReadyFor(filePath)) {
			report(ERROR_NOT_SETUP_YET);
			
			return tasksToReturn;
		}
		
		tasksToReturn = loadingFrom(filePath, backupFilePath);
		
		return tasksToReturn;
	}
 	
	//** ******************** **/
	
	//@author A0117989H-unused
	//storage is used to save only tasks
	@Override
	public boolean saveFile(final String fileName, ArrayList<String> list) {
		boolean isSaveSuccess = false;
		
		String filePath = manager.createFilePath(folderName, fileName);
		
		if (isDataBaseNotReadyFor(filePath)) {
			report(ERROR_NOT_SETUP_YET);
			
			return isSaveSuccess;
		}
		
		isSaveSuccess = savingTo(filePath, list);
		
		return isSaveSuccess;
	}
	
	//** ******************** **/
	
	//@author A0117989H-unused
	//storage is used to load tasks only
	@Override
	public ArrayList<String> loadFile(final String fileName) {
		ArrayList<String> list = new ArrayList<String>();
		
		String filePath = manager.createFilePath(folderName, fileName);
		
		if (isDataBaseNotReadyFor(filePath)) {
			report(ERROR_NOT_SETUP_YET);
			return list;
		}
		
		list = loadingFrom(filePath);
		
		return list;
	}
	
	//** ******************** **/
	
	//@author A0117989H
	
	/**
	 * This method setup files and backup files
	 * 
	 * @param fileName
	 * @return successful or not
	 */
	private boolean implementingSetupAndBackup(String fileName) {
		boolean isSetup = false;
		
		try {
			//redirect file into the base folder
			String filePath = manager.createFilePath(folderName, fileName);
			String backupFileName = FilenameUtils.getBaseName(fileName) + BACKUP + 
									FilenameUtils.getExtension(fileName);

			isSetup = manager.createFile(filePath);

			//data automatic backup
			manager.createBackupFile(folderName, backupFileName);

			if (isSetup) {
				path.add(filePath);
				report(INFO_FILE_CREATED + filePath + NEXT_LINE);
			}
			else {
				path.add(filePath);
				report(INFO_FILE_ALD_EXISTS);
				isSetup = true;
			}
		} catch (IOException e) {
			report(ERROR_IO);
			isSetup = false;
		}
		
		return isSetup;
	}
	
	//** ******************** **/
	
	private boolean convertTasksToJsonObjAndSave(String filePath, String backupFilePath,
			ArrayList<TaskData> taskList) {

		try {
			JSONArray jArr = converter.tasksToJsonArr(taskList);
			JSONObject jObjToSave = converter.encloseJsonArrInJsonObj(jArr);

			manager.writeInJsonFormat(filePath, jObjToSave, false);

			//data auto backup
			if (!backupFilePath.equals(NOTHING)) {
				manager.writeInJsonFormat(backupFilePath, jObjToSave, false);
			}

			return true;	

		} catch (IOException e) {
			report(ERROR_IO);
			
			return recreateFolderIfFolderNotFound();
		}
	}
	
	//** ******************** **/

	private boolean isDataBaseNotReadyFor(String filePath) {
		return path.isEmpty() || !path.contains(filePath);
	}
	
	//** ******************** **/
	
	private boolean isNullList(ArrayList<TaskData> taskList) {
		return taskList == null;
	}
	
	//** ******************** **/
	
	private ArrayList<TaskData> loadingFrom(String filePath, String backupFilePath) {
		ArrayList<TaskData> tasksToReturn = new ArrayList<TaskData>();
		
		try {
			if (manager.isEmptyFile(filePath)) {
				
				//check backup file
				if (isBackupFileAlsoEmpty(backupFilePath)) {
					
					return tasksToReturn;
				}
				else {
					filePath = backupFilePath;
				}
			}
			
			JSONObject jObj = manager.readInJsonFormat(filePath);
			JSONArray jArr = converter.retrieveJsonArrFromJsonObj(jObj);
			
			tasksToReturn = converter.jsonArrToTasks(jArr);
			
		} catch (IOException e) {
			report(ERROR_IO);
			tasksToReturn.clear();
			recreateFolderIfFolderNotFound();
			
		} catch (ParseException pe) {
			report(ERROR_PARSE);
			tasksToReturn.clear();
			//get tasks from backup file
			if (isValidBackupPath(backupFilePath)) {
				
				return loadBackupTasks(backupFilePath);
			}
		}
		
		return tasksToReturn;
	}
	
	//** ******************** **/
	
	private boolean isBackupFileAlsoEmpty(String backupFilePath) throws IOException {
		return backupFilePath.equals(NOTHING) || manager.isEmptyFile(backupFilePath);
	}
	
	//** ******************** **/
	
	private boolean isValidBackupPath(String backupFilePath) {
		return !backupFilePath.equals(NOTHING);
	}
	
	//** ******************** **/
	
	private boolean savingTo(String filePath, ArrayList<String> list) {
		boolean isSaveSuccess = false;
		
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
			isSaveSuccess = recreateFolderIfFolderNotFound();
		}
		
		return isSaveSuccess;
	}
	
	//** ******************** **/
	
	private ArrayList<String> loadingFrom(String filePath) {
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			if (manager.isEmptyFile(filePath)) {
				return list;
			}
			
			list = manager.read(filePath);
			
		} catch (IOException e) {
			report(ERROR_IO);
			list.clear();
			recreateFolderIfFolderNotFound();
		}
		
		return list;
	}
	
	//** ******************** **/
	
	private ArrayList<TaskData> loadBackupTasks(String backupFilePath) {
		ArrayList<TaskData> tasksToReturn = new ArrayList<TaskData>();
		
		try {
			if (manager.isEmptyFile(backupFilePath)) {
				
				return tasksToReturn;
			}
			
			JSONObject jObj = manager.readInJsonFormat(backupFilePath);
			JSONArray jArr = converter.retrieveJsonArrFromJsonObj(jObj);
			
			tasksToReturn = converter.jsonArrToTasks(jArr);
			
			return tasksToReturn;
		} catch (IOException e){
			report(ERROR_IO);
			tasksToReturn.clear();
			
			return tasksToReturn;
		} catch (ParseException e) {
			report(ERROR_PARSE);
			tasksToReturn.clear();
			
			return tasksToReturn;
		}
	}
	
	//** ******************** **/
	
	/**
	 * This method returns the path to the backup file.
	 * 
	 * @param fileName
	 * @return backup file path
	 */
	
	private String getBackupFilePath(String fileName) {
		String backupFilePath = "";
		backupFilePath = manager.createBackupFilePath(folderName, 
				FilenameUtils.getBaseName(fileName) + BACKUP + 
						FilenameUtils.getExtension(fileName));
		
		return backupFilePath;
	}
	
	//** ******************** **/
	/*
	 * This method creates the application folder
	 * namely FlexiPlanner Database for better organization
	 * of files
	 */
	private void createFolder() {
		boolean isCreated = false;
		
		folderName = BASE_FOLDER_NAME;
		
		//check if BASE_FOLDER_NAME exists
		if (manager.hasFolder(folderName)) {
			return;
		}
		
		//check if BASE_FOLDER1,2,etc exists
		for (int i = 1; i < MAX_ITERATION; i++) {
			if (manager.hasFolder(folderName + i)) {
				folderName = folderName + i; //set folder if found
				return;
			}
		}
		
		try {
			isCreated = manager.createFolder(folderName);
			
			if (!isCreated) {
				//if creating fails, try create folder name followed by integers
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
	
	//** ******************** **/
	
	private boolean recreateFolderIfFolderNotFound() {
		boolean isCreated = false;
		
		if (!manager.hasFolder(folderName)) {
			createFolder();
			List<String> clonedPath = new ArrayList<String>(path);
			path.clear();
			for (int i = 0; i < clonedPath.size(); i++) {
				setupDatabase(manager.extractFileName(clonedPath.get(i)));
			}
			isCreated = true;
		}
		
		return isCreated;
	}
	
	//** ******************** **/
	
	private void report(final String toReport) {
		System.out.print(toReport);
	}
}
