package flexiplanner.storage;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

/**
 * @author A0117989H
 *
 */

public interface StorageInterface {
	
	public boolean saveData(ArrayList<TaskData> taskList, boolean isAppendable);
	
	public ArrayList<TaskData> loadData(Option loadOption) throws IOException, ParseException;
}
