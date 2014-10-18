package Storage;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

/**
 * @author A0117989H
 *
 */

public interface Storage {
	
	public boolean saveData(ArrayList<TaskData> taskList, boolean isAppendable);
	
	public ArrayList<TaskData> loadData(Option loadOption) throws IOException, ParseException;
}
