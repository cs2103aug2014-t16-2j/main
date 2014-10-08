package flexiplanner.storage;

import java.util.ArrayList;

/**
 * @author A0117989H
 *
 */

public interface StorageInterface {
	public boolean saveData(ArrayList<TaskData> taskList, boolean isAppendable);
	
	public ArrayList<TaskData> loadData(Option loadOption);
}
