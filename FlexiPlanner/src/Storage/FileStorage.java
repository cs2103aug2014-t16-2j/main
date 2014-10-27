package Storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * This class implements functions of saving and loading
 * tasks from respective files.
 * The files must only contain task data in JSON format.
 * 
 * @author Moe Lwin Hein (A0117989H)
 *
 */

public class FileStorage implements Storage {
	
	private final String ERROR_IO = "IO Error!";
	private final String ERROR_PARSE = "Parse Error!";
	private final String ERROR_INVALID_FILE_NAME = "Invalid file name!";
	private final String ERROR_NULL_LIST = "List cannot be null!";
	private final String ERROR_NOT_SETUP_YET = "File record not found! Setup database first!";
	
	private final String WARNING_FILE_ALD_EXISTS = "File exists!";
	
	private final String FILE_NAME_PATTERN = "^[\\w,\\s-]+$";
	private final String VALID_EXTENSION_TASKS_FILE = "json";
	private final String VALID_EXTENSION_NORMAL_FILE = "txt";
	
	private List<String> path;
	
	private FileManager manager;
	private JsonConverter converter;
	
	/** Constructor Method **/
	
	public FileStorage() {
		manager = new FileManager();
		converter = new JsonConverter();
		
		path = new ArrayList<String>();
	}
	
	@Override
	public boolean setupDatabase(final String filePath) {
		boolean isSetup = false;
		if (isValidFileName(filePath)) {
			try {
				isSetup = manager.create(filePath);
				if (isSetup) {
					path.add(filePath);
				}
				else {
					reportError(WARNING_FILE_ALD_EXISTS);
					path.add(filePath);
				}
			} catch (IOException e) {
				reportError(ERROR_IO);
				return false;
			}
		}
		else {
			isSetup = false;
			reportError(ERROR_INVALID_FILE_NAME);
		}
		
		return isSetup;
	}
	
	

	@Override
	public boolean saveTasks(final String filePath, ArrayList<TaskData> taskList) {
		boolean isSaveSuccess = false;
		
		if (path.isEmpty() || !path.contains(filePath)) {
			reportError(ERROR_NOT_SETUP_YET);
			return isSaveSuccess;
		}
		
		if (taskList == null) {
			reportError(ERROR_NULL_LIST);
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
			reportError(ERROR_IO);
			isSaveSuccess = false;
		}
		
		return isSaveSuccess;
	}

	@Override
	public ArrayList<TaskData> loadTasks(final String filePath) {
		ArrayList<TaskData> tasksToReturn = new ArrayList<TaskData>();
		
		if (path.isEmpty() || !path.contains(filePath)) {
			reportError(ERROR_NOT_SETUP_YET);
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
			reportError(ERROR_IO);
			tasksToReturn.clear();
		} catch (ParseException pe) {
			reportError(ERROR_PARSE);
			tasksToReturn.clear();
		}
		
		return tasksToReturn;
	}
	
	@Override
	public boolean saveCategory(final String filePath, ArrayList<String> categoryList) {
		boolean isSaveSuccess = false;
		
		if (path.isEmpty() || !path.contains(filePath)) {
			reportError(ERROR_NOT_SETUP_YET);
			return isSaveSuccess;
		}
		
		try {
			manager.write(filePath, "", false);
			
			for (String category : categoryList) {
				manager.write(filePath, category, true);
				if (categoryList.indexOf(category) != (categoryList.size() - 1)) {
					manager.write(filePath, "\n", true);
				}
			}
			
			isSaveSuccess = true;
		} catch (IOException e) {
			reportError(ERROR_IO);
			isSaveSuccess = false;
		}
		
		return isSaveSuccess;
	}

	@Override
	public ArrayList<String> loadCategory(final String filePath) {
		ArrayList<String> categories = new ArrayList<String>();
		
		if (path.isEmpty() || !path.contains(filePath)) {
			reportError(ERROR_NOT_SETUP_YET);
			return categories;
		}
		
		try {
			if (manager.isEmptyFile(filePath)) {
				return categories;
			}
			
			categories = manager.read(filePath);
		} catch (IOException e) {
			reportError(ERROR_IO);
			categories.clear();
		}
		
		return categories;
	}
	
	private boolean isValidFileName(final String filePath) {
		if (filePath == null) {
			return false;
		}
		
		if (!FilenameUtils.getExtension(filePath).equalsIgnoreCase(VALID_EXTENSION_TASKS_FILE) &&
			!FilenameUtils.getExtension(filePath).equalsIgnoreCase(VALID_EXTENSION_NORMAL_FILE)) {
			return false;
		}
		
		Pattern pattern = Pattern.compile(FILE_NAME_PATTERN);
		Matcher matcher = pattern.matcher(FilenameUtils.getBaseName(filePath));
		
		return matcher.matches();
	}
	
	private void reportError(final String err) {
		System.out.println(err);
	}
}
