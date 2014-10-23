package Storage;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

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
	 * @throws IOException 
	 */
	public boolean saveTasks(String filePath, ArrayList<TaskData> taskList, boolean isAppendable);
	
	/**
	 * This method loads data from a specified file.
	 * @param filePath : path to the file to load from
	 * @param loadOption : Option
	 * 
	 * @return taskList : ArrayList<TaskData>
	 * @throws IOException
	 * @throws ParseException
	 */
	public ArrayList<TaskData> loadTasks(String filePath, Option loadOption) throws IOException, ParseException;
}
