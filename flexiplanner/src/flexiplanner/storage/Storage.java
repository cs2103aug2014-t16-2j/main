package flexiplanner.storage;

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
	private FileManager manager;
	private JsonCodec coder;
	private JsonFormatter formatter;
	
	/** Constructor Method **/
	
	public Storage() throws IOException {
		this("data/tasks.json");
	}
	
	public Storage(String filePath) throws IOException {
		manager = new FileManager();
		coder = new JsonCodec();
		formatter = new JsonFormatter();
		this.setFilePath(filePath);
		manager.create(this.filePath);
	}
	
	/** Accessor Method **/
	
	public String getFilePath() {
		return filePath;
	}
	
	/** Mutator Method **/

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public boolean saveData(ArrayList<TaskData> taskList, boolean isAppendable) {
		boolean isSaveSuccess = false;
		try {
			JSONObject jObj, jObjToSave;
			JSONArray jArr1, jArr2, jArr;
			
			if (isAppendable && !manager.isEmptyFile(filePath)) {
				jObj = manager.readJson(filePath);
				jArr1 = coder.seperateJsonArrFromObj(jObj);
				jArr2 = coder.encodeJsonArr(taskList);
				jArr = formatter.concatJsonArrs(jArr1, jArr2);
				jObjToSave = coder.putToJsonObj(jArr);
				manager.writeJson(filePath, jObjToSave, false);
				isSaveSuccess = true;
			}
			else {
				jArr = coder.encodeJsonArr(taskList);
				jObjToSave = coder.putToJsonObj(jArr);
				manager.writeJson(filePath, jObjToSave, false);
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
			JSONObject jObj = manager.readJson(filePath);
			JSONArray jArr = coder.seperateJsonArrFromObj(jObj);
			Filter filter = new Filter(jArr, loadOption);
			
			tasksToReturn = coder.decodeJsonArr(filter.refine());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException pe) {
			pe.printStackTrace();;
		}
		return tasksToReturn;
	}
	
	
	
}
