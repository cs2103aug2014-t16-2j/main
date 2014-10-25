package Storage;

import java.io.IOException;
import java.util.ArrayList;
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
	
	private final String FILE_NAME_PATTERN = "^[\\w,\\s-]+$";
	private final String VALID_EXTENSION_TASKS_FILE = "json";
	private final String VALID_EXTENSION_NORMAL_FILE = "txt";
	
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
		
		if (!isValidFileName(filePath)) {
			reportError(ERROR_INVALID_FILE_NAME);
			return isSaveSuccess;
		}
		
		if (taskList == null) {
			reportError(ERROR_NULL_LIST);
			return isSaveSuccess;
		}
		
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
		
		if (!isValidFileName(filePath)) {
			reportError(ERROR_INVALID_FILE_NAME);
			return tasksToReturn;
		}
		
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
	
	@Override
	public boolean saveCategory(String filePath, ArrayList<String> categoryList) {
		boolean isSaveSuccess = false;
		
		if (!isValidFileName(filePath)) {
			reportError(ERROR_INVALID_FILE_NAME);
			return isSaveSuccess;
		}
		
		try {
			manager.create(filePath);
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
		}
		
		return isSaveSuccess;
	}

	@Override
	public ArrayList<String> loadCategory(String filePath) {
		ArrayList<String> categories = new ArrayList<String>();
		
		if (!isValidFileName(filePath)) {
			reportError(ERROR_INVALID_FILE_NAME);
			return categories;
		}
		
		try {
			manager.create(filePath);
			
			if (manager.isEmptyFile(filePath)) {
				return categories;
			}
			
			categories = manager.read(filePath);
		} catch (IOException e) {
			reportError(ERROR_IO);
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
