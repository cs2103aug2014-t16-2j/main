package storage;

import java.util.ArrayList;

import commons.TaskData;

//@author A0117989H

/**
 * Interface
 *
 */

public interface Storage {
	
	/**
	 * This method create folders and files necessary
	 * for storing data.
	 * 
	 * @param filePath - the path to the file to set up
	 * @return setup successful or not : boolean
	 */
	public boolean setupDatabase(final String filePath);
	
	/**
	 * This method saves a list of tasks in a specified file.
	 * 
	 * @param fileName : name of the file to save into
	 * @param taskList : ArrayList<TaskData>
	 * 
	 * @return successful or not : boolean
	 */
	public boolean saveTasks(final String fileName, ArrayList<TaskData> taskList);
	
	/**
	 * This method loads task data from a specified file.
	 * 
	 * @param fileName : name of the file to load from
	 * 
	 * @return taskList : ArrayList<TaskData> 
	 */
	public ArrayList<TaskData> loadTasks(final String fileName);
	
	/**
	 * This method saves a list of text data in a specified file.
	 * 
	 * @param fileName : name of the text file to save into
	 * @param list : ArrayList<String>
	 * 
	 * @return successful or not : boolean
	 */
	boolean saveFile(String fileName, ArrayList<String> list);
	
	
	/**
	 * This method loads data from a specified file.
	 * 
	 * @param fileName : name of the file to load from
	 * 
	 * @return list : ArrayList<String> 
	 */
	ArrayList<String> loadFile(String fileName);
}
