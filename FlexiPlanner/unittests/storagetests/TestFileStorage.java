package storagetests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import Storage.FileManager;
import Storage.FileStorage;
import Storage.Option;
import Storage.Storage;
import Storage.TaskData;

public class TestFileStorage {
	Storage database = new FileStorage();
	FileManager manager = new FileManager();
	
	TaskData t1 = new TaskData("first", "personal", "normal", LocalDateTime.of(2014, 10, 23, 0, 0), LocalDateTime.of(2014, 10, 24, 0, 0));
	TaskData t2 = new TaskData("second", "work", "high", LocalDateTime.of(2014, 10, 25, 0, 0), LocalDateTime.of(2014, 10, 26, 0, 0));
	TaskData t3 = new TaskData("third", "work", "high", LocalDateTime.of(2014, 10, 27, 0, 0), LocalDateTime.of(2014, 10, 28, 0, 0));
	
	/** Test Loading Method **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	
	@Test (expected = NullPointerException.class)
	public void testLoadFromNullFilePath() throws IOException, ParseException {
		database.loadTasks(null, new Option(true));
	}
	
	@Test (expected = NullPointerException.class)
	public void testLoadWithNullOption() throws IOException, ParseException {
		database.loadTasks("testresources/onetask.json", null);
	}
	
	@Test (expected = ParseException.class)
	public void testLoadFileNotInJSONFormat() throws IOException, ParseException {
		database.loadTasks("testresources/notinjsonformat.json", new Option(true));
	}
	
	@Test 
	public void testLoadEmptyFile() throws IOException, ParseException {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		tasks = database.loadTasks("testresources/empty.json", new Option(true));
		assertTrue(tasks.isEmpty());
	}
	
	@Test 
	public void testLoadFileWithOneTask() throws IOException, ParseException {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		tasks = database.loadTasks("testresources/onetask.json", new Option(true));
		assertTrue(tasks.size() == 1);
		assertTrue(tasks.get(0).equals(t1));
	}
	
	@Test 
	public void testLoadFileWithThreeTask() throws IOException, ParseException {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		tasks = database.loadTasks("testresources/threetask.json", new Option(true));
		assertTrue(tasks.size() == 3);
		assertTrue(tasks.get(0).equals(t1) && tasks.get(1).equals(t2) && tasks.get(2).equals(t3));
	}
	
	/** Test Save Method **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/

	@Test (expected = NullPointerException.class)
	public void testSaveInNullFilePath() {
		database.saveTasks(null, new ArrayList<TaskData>(), false);
	}

	@Test (expected = NullPointerException.class)
	public void testSaveNullList() {
		database.saveTasks("testresources/testingsave.json", null, true);
	}
	
	@Test 
	public void testSaveInNonJSONFileWithAppend() {
		assertFalse(database.saveTasks("testresources/notinjsonformat.json", new ArrayList<TaskData>(), true));
	}
	
	@Test 
	public void testSaveEmptyList() throws IOException, ParseException {
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		deleteFile("testresources/testingsave.json");
		assertTrue(database.saveTasks("testresources/testingsave.json", new ArrayList<TaskData>(), false));
		tasks = database.loadTasks("testresources/testingsave.json", new Option(true));
		assertTrue(tasks.isEmpty());
	}
	
	@Test 
	public void testSaveEmptyListAppend() throws IOException, ParseException {
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		deleteFile("testresources/testingsave.json");
		assertTrue(database.saveTasks("testresources/testingsave.json", new ArrayList<TaskData>(), true));
		tasks = database.loadTasks("testresources/testingsave.json", new Option(true));
		assertTrue(tasks.isEmpty());
	}
	
	@Test 
	public void testSaveATaskInEmptyFile() throws IOException, ParseException {
		ArrayList<TaskData> oneTask = new ArrayList<TaskData>();
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		oneTask.add(t1);
		deleteFile("testresources/testingsave.json");
		assertTrue(database.saveTasks("testresources/testingsave.json", oneTask, false));
		tasks = database.loadTasks("testresources/testingsave.json", new Option(true));
		assertTrue(tasks.size() == 1);
		assertTrue(tasks.get(0).equals(oneTask.get(0)));
	}
	
	@Test 
	public void testAppendATaskInEmptyFile() throws IOException, ParseException {
		ArrayList<TaskData> oneTask = new ArrayList<TaskData>();
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		oneTask.add(t1);
		deleteFile("testresources/testingsave.json");
		assertTrue(database.saveTasks("testresources/testingsave.json", oneTask, true));
		tasks = database.loadTasks("testresources/testingsave.json", new Option(true));
		assertTrue(tasks.size() == 1);
		assertTrue(tasks.get(0).equals(oneTask.get(0)));
	}
	
	@Test 
	public void testAppendATask() throws IOException, ParseException {
		ArrayList<TaskData> oneTask = new ArrayList<TaskData>();
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		oneTask.add(t2);
		manager.copy("testresources/onetask.json", "testresources/onetoappend.json");
		assertTrue(database.saveTasks("testresources/onetoappend.json", oneTask, true));
		tasks = database.loadTasks("testresources/onetoappend.json", new Option(true));
		assertTrue(tasks.size() == 2);
		assertTrue(tasks.get(0).equals(t1) && tasks.get(1).equals(oneTask.get(0)));
		deleteFile("testresources/onetoappend.json");
	}
	
	@Test 
	public void testAppendMultiTask() throws IOException, ParseException {
		ArrayList<TaskData> threeTask = new ArrayList<TaskData>();
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		threeTask.add(t1);
		threeTask.add(t2);
		threeTask.add(t3);
		manager.copy("testresources/threetask.json", "testresources/threetoappend.json");
		assertTrue(database.saveTasks("testresources/threetoappend.json", threeTask, true));
		tasks = database.loadTasks("testresources/threetoappend.json", new Option(true));
		assertTrue(tasks.size() == 6);
		assertTrue(tasks.get(0).equals(t1) && tasks.get(1).equals(t2) &&
				   tasks.get(2).equals(t3) && tasks.get(3).equals(t1) &&
				   tasks.get(4).equals(t2) && tasks.get(5).equals(t3));
		deleteFile("testresources/threetoappend.json");
	}
	
	@Test 
	public void testOverwrite() throws IOException, ParseException {
		ArrayList<TaskData> threeTasks = new ArrayList<TaskData>();
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		threeTasks.add(t1);
		threeTasks.add(t2);
		threeTasks.add(t2);
		assertTrue(database.saveTasks("testresources/testingsave.json", threeTasks, false));
		tasks = database.loadTasks("testresources/testingsave.json", new Option(true));
		assertTrue(tasks.size() == 3);
		assertTrue(tasks.get(0).equals(t1) && tasks.get(1).equals(t2) &&
				   tasks.get(2).equals(t2));
	}
	
	/** Assist Methods **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	
	private void deleteFile(String filePath) {
		try {
			manager.delete(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
