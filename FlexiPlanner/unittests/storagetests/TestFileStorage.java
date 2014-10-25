package storagetests;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import Storage.FileManager;
import Storage.FileStorage;
import Storage.Option;
import Storage.Storage;
import Storage.TaskData;

/**
 * This unit test class is for testing FileStorage class under storage component. 
 * 
 * @author Moe Lwin Hein (A0117989H)
 *
 */
public class TestFileStorage {
	Storage database = new FileStorage();
	FileManager manager = new FileManager();
	
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	TaskData t1 = new TaskData("first", "personal", "normal", LocalDateTime.of(2014, 10, 23, 0, 0), LocalDateTime.of(2014, 10, 24, 0, 0));
	TaskData t2 = new TaskData("second", "work", "high", LocalDateTime.of(2014, 10, 25, 0, 0), LocalDateTime.of(2014, 10, 26, 0, 0));
	TaskData t3 = new TaskData("third", "work", "high", LocalDateTime.of(2014, 10, 27, 0, 0), LocalDateTime.of(2014, 10, 28, 0, 0));
	
	final String c1 = "#personal";
	final String c2 = "#work";
	final String c3 = "#today";
	
	/** Test Loading Method **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	
	@Test
	public void testLoadFromNullFilePath() throws IOException, ParseException {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		System.setOut(new PrintStream(out));
		tasks = database.loadTasks(null, new Option(true));
		assertTrue(tasks.isEmpty());
		assertEquals("Invalid file name!\n", out.toString());
	}
	
	@Test (expected = NullPointerException.class)
	public void testLoadWithNullOption() {
		database.loadTasks("testresources/onetask.json", null);
	}
	
	@Test
	public void testLoadFileNotInJSONFormat() {
		System.setOut(new PrintStream(out));
		database.loadTasks("testresources/notinjsonformat.json", new Option(true));
		assertEquals("Parse Error!\n", out.toString());
	}
	
	@Test 
	public void testLoadEmptyFile() {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		tasks = database.loadTasks("testresources/empty.json", new Option(true));
		assertTrue(tasks.isEmpty());
	}
	
	@Test 
	public void testLoadFileWithOneTask() {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		tasks = database.loadTasks("testresources/onetask.json", new Option(true));
		assertTrue(tasks.size() == 1);
		assertTrue(tasks.get(0).equals(t1));
	}
	
	@Test 
	public void testLoadFileWithThreeTask() {
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

	@Test
	public void testSaveInNullFilePath() {
		System.setOut(new PrintStream(out));
		assertFalse(database.saveTasks(null, new ArrayList<TaskData>(), false));
		assertEquals("Invalid file name!\n", out.toString());
	}

	@Test 
	public void testSaveNullList() {
		System.setOut(new PrintStream(out));
		assertFalse(database.saveTasks("testresources/testingsave.json", null, true));
		assertEquals("List cannot be null!\n", out.toString());
	}
	
	@Test 
	public void testSaveInNonJSONFileWithAppend() {
		assertFalse(database.saveTasks("testresources/notinjsonformat.json", new ArrayList<TaskData>(), true));
	}
	
	@Test 
	public void testSaveEmptyList() {
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		deleteFile("testresources/testingsave.json");
		assertTrue(database.saveTasks("testresources/testingsave.json", new ArrayList<TaskData>(), false));
		tasks = database.loadTasks("testresources/testingsave.json", new Option(true));
		assertTrue(tasks.isEmpty());
	}
	
	@Test 
	public void testSaveEmptyListAppend() {
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		deleteFile("testresources/testingsave.json");
		assertTrue(database.saveTasks("testresources/testingsave.json", new ArrayList<TaskData>(), true));
		tasks = database.loadTasks("testresources/testingsave.json", new Option(true));
		assertTrue(tasks.isEmpty());
	}
	
	@Test 
	public void testSaveATaskInEmptyFile() {
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
	public void testAppendATaskInEmptyFile() {
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
	public void testAppendATask() throws FileNotFoundException, IOException {
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
	public void testAppendMultiTask() throws FileNotFoundException, IOException  {
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
	public void testOverwrite() {
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
	
	/** Test Save Category Method **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	@Test
	public void testSaveCategory() {
		ArrayList<String> c = new ArrayList<String>();
		ArrayList<String> load = new ArrayList<String>();
		c.add(c1);
		c.add(c2);
		c.add(c3);
		assertTrue(database.saveCategory("testresources/save.txt", c));
		load = database.loadCategory("testresources/save.txt");
		assertTrue(load.size() == 3);
		assertTrue(load.get(0).equals(c1) && load.get(1).equals(c2) && load.get(2).equals(c3));
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
