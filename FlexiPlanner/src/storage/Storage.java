package storage;

import java.util.ArrayList;

/**
 * @author Moe Lwin Hein (A0117989H)
 *
 */

public interface Storage {
	public boolean setupDatabase(final String filePath);
	
	/**
	 * This method saves a list of data in a specified file.
	 * @param filePath : path to the file to save into
	 * @param taskList : ArrayList<TaskData>
	 * @param isAppendable : boolean
	 * 
	 * @return successful or not : boolean
	 */
	public boolean saveTasks(final String filePath, ArrayList<TaskData> taskList);
	
	/**
	 * This method loads data from a specified file.
	 * @param filePath : path to the file to load from
	 * @param loadOption : Option
	 * 
	 * @return taskList : ArrayList<TaskData> 
	 */
	public ArrayList<TaskData> loadTasks(final String filePath);

	boolean saveFile(String filePath, ArrayList<String> list);

	ArrayList<String> loadFile(String filePath);
}
