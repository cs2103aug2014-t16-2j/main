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
	 * 
	 * @param list : ArrayList<Object>
	 * @param isAppendable : boolean
	 * @return successful or not : boolean
	 */
	public boolean saveData(ArrayList<TaskData> taskList, boolean isAppendable);
	
	/**
	 * This method loads data from a specified file.
	 * 
	 * @param loadOption : Option
	 * @return list : ArrayList<Object>
	 * @throws IOException
	 * @throws ParseException
	 */
	public ArrayList<TaskData> loadData(Option loadOption) throws IOException, ParseException;
}
