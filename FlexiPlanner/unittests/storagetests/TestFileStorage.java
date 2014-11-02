package storagetests;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import Storage.FileManager;
import Storage.FileStorage;
import Storage.Storage;
import Storage.TaskData;

/**
 * This unit test class is for testing FileStorage class under storage component. 
 * 
 * @author Moe Lwin Hein (A0117989H)
 *
 */
public class TestFileStorage {
	Storage database = FileStorage.getInstance();
	FileManager manager = new FileManager();
	
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	TaskData t1 = new TaskData("first", "personal", "normal", LocalDateTime.of(2014, 10, 23, 0, 0), LocalDateTime.of(2014, 10, 24, 0, 0));
	TaskData t2 = new TaskData("second", "work", "high", LocalDateTime.of(2014, 10, 25, 0, 0), LocalDateTime.of(2014, 10, 26, 0, 0));
	TaskData t3 = new TaskData("third", "work", "high", LocalDateTime.of(2014, 10, 27, 0, 0), LocalDateTime.of(2014, 10, 28, 0, 0));
	
	final String f1 = "testresources/notinjsonformat.json";
	final String f2 = "testresources/empty.json";
	final String f3 = "testresources/threetask.json";
	final String f4 = "testresources/testingsave.json";
	final String f5 = "testresources/save.txt";
	final String f6 = "testresources/onetask.json";
	
	final String c1 = "#personal";
	final String c2 = "#work";
	final String c3 = "#today";
	
	@Before
	public void setUp() throws Exception {
		database.setupDatabase(f1);
		database.setupDatabase(f2);
		database.setupDatabase(f3);
		database.setupDatabase(f4);
		database.setupDatabase(f5);
		database.setupDatabase(f6);
	}
	
	/** Test Loading Method **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	
	@Test
	public void testLoadFromNullFilePath() throws IOException, ParseException {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		System.setOut(new PrintStream(out));
		tasks = database.loadTasks(null);
		assertTrue(tasks.isEmpty());
		assertEquals("File record not found! Setup database first!\n", out.toString());
	}
	
	@Test
	public void testLoadFileNotInJSONFormat() {
		System.setOut(new PrintStream(out));
		database.loadTasks("testresources/notinjsonformat.json");
		assertEquals("Parse Error!\n", out.toString());
	}
	
	@Test 
	public void testLoadEmptyFile() {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		tasks = database.loadTasks("testresources/empty.json");
		assertTrue(tasks.isEmpty());
	}
	
	@Test 
	public void testLoadFileWithOneTask() {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		tasks = database.loadTasks("testresources/onetask.json");
		System.out.println(tasks.size());
		assertTrue(tasks.size() == 1);
		assertTrue(tasks.get(0).equals(t1));
	}
	
	@Test 
	public void testLoadFileWithThreeTask() {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		tasks = database.loadTasks("testresources/threetask.json");
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
		assertFalse(database.saveTasks(null, new ArrayList<TaskData>()));
		assertEquals("File record not found! Setup database first!\n", out.toString());
	}

	@Test 
	public void testSaveNullList() {
		System.setOut(new PrintStream(out));
		assertFalse(database.saveTasks("testresources/testingsave.json", null));
		assertEquals("List cannot be null!\n", out.toString());
	}
	
	@Test 
	public void testSaveEmptyList() throws FileNotFoundException, IOException {
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		manager.clear("testresources/testingsave.json");
		assertTrue(database.saveTasks("testresources/testingsave.json", new ArrayList<TaskData>()));
		tasks = database.loadTasks("testresources/testingsave.json");
		assertTrue(tasks.isEmpty());
	}
	
	@Test 
	public void testSaveATaskInEmptyFile() throws FileNotFoundException, IOException {
		ArrayList<TaskData> oneTask = new ArrayList<TaskData>();
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		oneTask.add(t1);
		manager.clear("testresources/testingsave.json");
		assertTrue(database.saveTasks("testresources/testingsave.json", oneTask));
		tasks = database.loadTasks("testresources/testingsave.json");
		assertTrue(tasks.size() == 1);
		assertTrue(tasks.get(0).equals(oneTask.get(0)));
	}
	
	@Test 
	public void testOverwrite() {
		ArrayList<TaskData> threeTasks = new ArrayList<TaskData>();
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		threeTasks.add(t1);
		threeTasks.add(t2);
		threeTasks.add(t2);
		assertTrue(database.saveTasks("testresources/testingsave.json", threeTasks));
		tasks = database.loadTasks("testresources/testingsave.json");
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
}
