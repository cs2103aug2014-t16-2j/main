package Storage;

import java.util.ArrayList;

/**
 * @author A0117989H
 *
 */

public interface Storage {
	/**
	 * This method saves a list of data in a specified file.
	 * @param filePath : path to the file to save into
	 * @param taskList : ArrayList<TaskData>
	 * @param isAppendable : boolean
	 * 
	 * @return successful or not : boolean
	 */
	public boolean saveTasks(String filePath, ArrayList<TaskData> taskList, boolean isAppendable);
	
	/**
	 * This method loads data from a specified file.
	 * @param filePath : path to the file to load from
	 * @param loadOption : Option
	 * 
	 * @return taskList : ArrayList<TaskData>
	 */
	public ArrayList<TaskData> loadTasks(String filePath, Option loadOption);
}
