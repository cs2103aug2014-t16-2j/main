package storage;

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

import storage.FileManager;
import storage.FileStorage;
import storage.Storage;

import commons.TaskData;

//@author A0117989H

/**
 * This unit test class is for testing FileStorage class under storage component. 
 * This also check the associated classes with FileStorage class:
 * FileManager() and JsonConverter()
 *
 */
public class FileStorageTest {
	Storage database = FileStorage.getInstance();
	FileManager manager = new FileManager();
	
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	TaskData t1 = new TaskData("first", "personal", "normal", LocalDateTime.of(2014, 10, 23, 0, 0), LocalDateTime.of(2014, 10, 24, 0, 0));
	TaskData t2 = new TaskData("second", "work", "high", LocalDateTime.of(2014, 10, 25, 0, 0), LocalDateTime.of(2014, 10, 26, 0, 0));
	TaskData t3 = new TaskData("third", "work", "high", LocalDateTime.of(2014, 10, 27, 0, 0), LocalDateTime.of(2014, 10, 28, 0, 0));
	
	final String FOLDER = "FlexiPlanner Database//";
	final String NOT_IN_JSON_FORMAT = "test-resources//notinjsonformat.json";
	final String EMPTY = "test-resources//empty.json";
	final String THREE_TASKS = "test-resources//threetask.json";
	final String TESTING_SAVE = "test-resources//testingsave.json";
	final String SAVE = "test-resources//save.txt";
	final String ONE_TASK = "test-resources//onetask.json";
	final String MULTI_SAVE = "test-resources//multisave.json";
	final String SIMULATE_PE = "test-resources//simulatepe.json";
	
	final String ERROR_NO_RECORD = "File record not found! Setup database first!\n";
	final String ERROR_PARSE_ERROR = "Parse Error!\nParse Error!\n";
	final String ERROR_NULL_LIST = "List cannot be null!\n";
	
	final String PERSONAL = "#personal";
	final String WORK = "#work";
	final String TODAY = "#today";
	
	@Before
	public void setUp() throws Exception {
		database.setupDatabase(NOT_IN_JSON_FORMAT);
		database.setupDatabase(EMPTY);
		database.setupDatabase(THREE_TASKS);
		database.setupDatabase(TESTING_SAVE);
		database.setupDatabase(SAVE);
		database.setupDatabase(ONE_TASK);
		database.setupDatabase(MULTI_SAVE);
		database.setupDatabase(SIMULATE_PE);
	}
	
	/** Test Invalid File Name **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	
	/** test output for invalid file name **/
	@Test
	public void testInvalidFileName() {
		boolean isSetup = false;
		isSetup = database.setupDatabase("..text");
		assertFalse(isSetup);
		isSetup = database.setupDatabase("text.txt.txt");
		assertFalse(isSetup);
		isSetup = database.setupDatabase(".txt");
		assertFalse(isSetup);
	}
	
	/** test output for invalid file extension **/
	@Test
	public void testInvalidFileExtension() {
		boolean isSetup = false;
		isSetup = database.setupDatabase("text.doc");
		assertFalse(isSetup);
		isSetup = database.setupDatabase("text.xls");
		assertFalse(isSetup);
		isSetup = database.setupDatabase("text.ppt");
		assertFalse(isSetup);
		isSetup = database.setupDatabase("text.something");
		assertFalse(isSetup);
		isSetup = database.setupDatabase("text.pdf");
		assertFalse(isSetup);
	}
	
	/** test output for valid files **/
	@Test
	public void testValidFile() throws FileNotFoundException {
		boolean isSetup = false;
		isSetup = database.setupDatabase("tosave-tasks.json");
		assertTrue(isSetup);
		manager.deleteFile("FlexiPlanner Database//tosave-tasks.json");
		isSetup = database.setupDatabase("tOsaVeTaSkS.TXT");
		assertTrue(isSetup);
		manager.deleteFile("FlexiPlanner Database//tosavetasks.txt");
	}
	
	/** Test Loading Method **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	
	/** test output when file path is not provided or it is null **/
	@Test
	public void testLoadFromNullFilePath() throws IOException, ParseException {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		System.setOut(new PrintStream(out));
		tasks = database.loadTasks(null);
		assertTrue(tasks.isEmpty());
		assertEquals(ERROR_NO_RECORD, out.toString());
	}
	
	/** test output when the task file is not in JSON format **/
	@Test
	public void testLoadFileNotInJSONFormat() {
		System.setOut(new PrintStream(out));
		database.loadTasks(NOT_IN_JSON_FORMAT);
		assertEquals(ERROR_PARSE_ERROR, out.toString());
	}
	
	/** test boundary case of loading from an empty task file **/
	@Test 
	public void testLoadEmptyFile() {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		tasks = database.loadTasks(EMPTY);
		assertTrue(tasks.isEmpty());
	}
	
	/** test boundary case of loading task file with only one task in it **/
	@Test 
	public void testLoadFileWithOneTask() {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		tasks = database.loadTasks(ONE_TASK);
		System.out.println(tasks.size());
		assertTrue(tasks.size() == 1);
		assertTrue(tasks.get(0).equals(t1));
	}
	
	/** test loading task file which has more than one tasks **/
	@Test 
	public void testLoadFileWithThreeTask() {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		tasks = database.loadTasks(THREE_TASKS);
		assertTrue(tasks.size() == 3);
		assertTrue(tasks.get(0).equals(t1) && tasks.get(1).equals(t2) && tasks.get(2).equals(t3));
	}
	
	/** test loading back up data retrieval when task file is accidentally cleared **/
	@Test
	public void testRetrieveBackupClearedFile() throws FileNotFoundException, IOException {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		ArrayList<TaskData> bkupTasks = new ArrayList<TaskData>();
		tasks = database.loadTasks(MULTI_SAVE);
		assertTrue(tasks.size() == 5);
		manager.clearFile(FOLDER + MULTI_SAVE);
		bkupTasks = database.loadTasks(MULTI_SAVE);
		assertTrue(bkupTasks.size() == 5);
		database.saveTasks(MULTI_SAVE, bkupTasks);
	}
	
	/** test loading back up data retrieval when parse exception occurs in parsing JSON format **/
	@Test
	public void testRetrieveBackupWhenParseExOccurs() throws FileNotFoundException, IOException {
		ArrayList<TaskData> tasks = new ArrayList<TaskData>();
		tasks = database.loadTasks(SIMULATE_PE);
		assertTrue(tasks.size() == 5);
	}
	
	
	
	/** Test Save Method **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	
	/** test output when file path is not provided **/
	@Test
	public void testSaveInNullFilePath() {
		System.setOut(new PrintStream(out));
		assertFalse(database.saveTasks(null, new ArrayList<TaskData>()));
		assertEquals(ERROR_NO_RECORD, out.toString());
	}
	
	/** test output when the task list to be saved is null **/
	@Test 
	public void testSaveNullList() {
		System.setOut(new PrintStream(out));
		assertFalse(database.saveTasks(TESTING_SAVE, null));
		assertEquals(ERROR_NULL_LIST, out.toString());
	}
	
	/** test boundary case of saving an empty task list to the task file **/
	@Test 
	public void testSaveEmptyList() throws FileNotFoundException, IOException {
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		manager.clearFile(FOLDER + TESTING_SAVE);
		assertTrue(database.saveTasks(TESTING_SAVE, new ArrayList<TaskData>()));
		tasks = database.loadTasks(TESTING_SAVE);
		assertTrue(tasks.isEmpty());
	}
	
	/** test boundary case of saving a task to an empty task file **/
	@Test 
	public void testSaveATaskInEmptyFile() throws FileNotFoundException, IOException {
		ArrayList<TaskData> oneTask = new ArrayList<TaskData>();
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		oneTask.add(t1);
		manager.clearFile(FOLDER + TESTING_SAVE);
		assertTrue(database.saveTasks(TESTING_SAVE, oneTask));
		tasks = database.loadTasks(TESTING_SAVE);
		assertTrue(tasks.size() == 1);
		assertTrue(tasks.get(0).equals(oneTask.get(0)));
	}
	
	/** test saving more than one task in a task file **/
	@Test 
	public void testSaveMoreThanOneTask() {
		ArrayList<TaskData> threeTasks = new ArrayList<TaskData>();
		ArrayList<TaskData> tasks = new ArrayList<TaskData> ();
		threeTasks.add(t1);
		threeTasks.add(t2);
		threeTasks.add(t2);
		assertTrue(database.saveTasks(TESTING_SAVE, threeTasks));
		tasks = database.loadTasks(TESTING_SAVE);
		assertTrue(tasks.size() == 3);
		assertTrue(tasks.get(0).equals(t1) && tasks.get(1).equals(t2) &&
				   tasks.get(2).equals(t2));
	}
	
	/** Test Save and Load Normal String Texts Method **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	/** ******************* **/
	@Test
	public void testSaveCategory() {
		ArrayList<String> c = new ArrayList<String>();
		ArrayList<String> load = new ArrayList<String>();
		c.add(PERSONAL);
		c.add(WORK);
		c.add(TODAY);
		assertTrue(database.saveFile(SAVE, c));
		load = database.loadFile(SAVE);
		assertTrue(load.size() == 3);
		assertTrue(load.get(0).equals(PERSONAL) && load.get(1).equals(WORK) && load.get(2).equals(TODAY));
	}
}
