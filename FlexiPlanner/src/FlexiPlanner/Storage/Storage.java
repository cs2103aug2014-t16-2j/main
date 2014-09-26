package FlexiPlanner.Storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * @author A0117989H
 *
 */

public class Storage implements StorageInterface {
	
	private String filePath;
	
	/** Constructor Method **/
	
	public Storage() throws IOException, FileNotFoundException {
		this("data/tasks.json");
	}
	
	public Storage(String _filePath) throws IOException, FileNotFoundException {
		this.setFilePath(_filePath);
		FileManager.create(filePath);
	}
	
	/** Accessor Method **/
	
	public String getFilePath() {
		return filePath;
	}
	
	/** Mutator Method **/

	public void setFilePath(String _filePath) {
		this.filePath = _filePath;
	}

	@Override
	public boolean saveData(ArrayList<TaskData> taskList, boolean isAppendable) {
		boolean isSaveSuccess = false;
		try {
			JSONObject jObj, jObjToSave;
			JSONArray jArr1, jArr2, jArr;
			
			if (isAppendable && !FileManager.isEmptyFile(filePath)) {
				jObj = FileManager.readJson(filePath);
				jArr1 = JsonCodec.seperateJsonArrFromObj(jObj);
				jArr2 = JsonCodec.encodeJsonArr(taskList);
				jArr = JsonFormatter.concatJsonArrs(jArr1, jArr2);
				jObjToSave = JsonCodec.putToJsonObj(jArr);
				FileManager.writeJson(filePath, jObjToSave, false);
				isSaveSuccess = true;
			}
			else {
				jArr = JsonCodec.encodeJsonArr(taskList);
				jObjToSave = JsonCodec.putToJsonObj(jArr);
				FileManager.writeJson(filePath, jObjToSave, false);
				isSaveSuccess = true;
			}
		} catch (IOException e) {
			isSaveSuccess = false;
		} catch (ParseException pe) {
			isSaveSuccess = false;
		}
		return isSaveSuccess;
	}

	@Override
	public ArrayList<TaskData> loadData(Option loadOption) {
		ArrayList<TaskData> tasksToReturn = new ArrayList<TaskData>();
		try {
			JSONObject jObj = FileManager.readJson(filePath);
			JSONArray jArr = JsonCodec.seperateJsonArrFromObj(jObj);
			
			tasksToReturn = JsonCodec.decodeJsonArr(CustomSearch.search(loadOption, jArr));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException pe) {
			return null;
		}
		return tasksToReturn;
	}
	
	
	
}
