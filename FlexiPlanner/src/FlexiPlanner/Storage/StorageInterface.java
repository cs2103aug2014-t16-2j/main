package FlexiPlanner.Storage;

import java.util.ArrayList;

public interface StorageInterface {
	public boolean saveData(ArrayList<TaskData> taskList, String filePath);
	
	public ArrayList<TaskData> loadData(Option loadOption, String filePath);
}
