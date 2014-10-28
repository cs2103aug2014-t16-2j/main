package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Logic.Logic;
import Storage.FileStorage;
import Storage.Storage;
import Storage.TaskData;

public class flexiPlannerIT {
	static Logic logic;
	static Storage storage;
	static ArrayList<TaskData> tasks;
	
	private final String ERROR = "Error. ";
	
	private final String INFO_SUCCESSFUL = "Successful";
	
	private static final String TASK_FILE = "text.json";
	private static final String COMPLETED_TASK_FILE = "completed.json";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logic = new Logic();
		storage = new FileStorage();
		storage.setupDatabase(TASK_FILE);
		storage.setupDatabase(COMPLETED_TASK_FILE);
		tasks = new ArrayList<TaskData>();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		FileUtils.write(new File(TASK_FILE), "", false);
		FileUtils.write(new File(COMPLETED_TASK_FILE), "", false);
	}
	
	public boolean equals(ArrayList<TaskData> list1, ArrayList<TaskData> list2) {
		if (list1.size() != list2.size()) {
			return false;
		}
		for (int i = 0; i < list1.size(); i++) {
			if (!list1.get(i).equals(list2.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Before
	public void setUp() throws Exception {
	}
	/**
	 * test empty input
	 */
	@Test
	public void tc1() throws IOException, ParseException {
		String input = "";
		assertTrue(logic.executeInputCommand(input).equals(ERROR));
	}
	
	/**
	 * test space input
	 */
	@Test
	public void tc2() throws IOException, ParseException {
		String input = " ";
		assertTrue(logic.executeInputCommand(input).equals(ERROR));
	}
	
	/**
	 * test add with no content
	 */
	@Test
	public void tc3() throws IOException, ParseException {
		String input = "add";
		assertTrue(logic.executeInputCommand(input).equals(ERROR));
	}
	
	/**
	 * test add a space
	 */
	@Test
	public void tc4() throws IOException, ParseException {
		String input = "add ";
		assertTrue(logic.executeInputCommand(input).equals(ERROR));
	}
	
	/**
	 * test add a floating task
	 */
	@Test
	public void tc5() throws IOException, ParseException {
		String input = "add a task";
		tasks.add(new TaskData("a task", null, "normal", null, null));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task today
	 */
	@Test
	public void tc6() throws IOException, ParseException {
		String input = "add watch webcast today";
		tasks.add(new TaskData("watch webcast", null, "normal", LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task tomorrow 
	 */
	@Test
	public void tc7() throws IOException, ParseException {
		String input = "add watch webcast tomorrow";
		tasks.add(new TaskData("watch webcast", null, "normal", LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task with date and time
	 */
	@Test
	public void tc8() throws IOException, ParseException {
		String input = "add project version 3 demo on 30/10 4pm to 5pm";
		tasks.add(new TaskData("project version 3 demo", null, "normal", LocalDateTime.of(2014, 10, 30, 16, 00), LocalDateTime.of(2014, 10, 30, 17, 0)));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task with date
	 */
	@Test
	public void tc9() throws IOException, ParseException {
		String input = "add project version 3 demo on 30 oct";
		tasks.add(new TaskData("project version 3 demo", null, "normal", LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 10, 30, 23, 59, 59)));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test undo add
	 */
	@Test
	public void tc10() throws IOException, ParseException {
		String input = "undo";
		tasks.remove(tasks.size() - 1);
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test redo the undo
	 */
	@Test
	public void tc11() throws IOException, ParseException {
		String input = "redo";
		tasks.add(new TaskData("project version 3 demo", null, "normal", LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 10, 30, 23, 59, 59)));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task with priority
	 */
	@Test
	public void tc12() throws IOException, ParseException {
		String input = "add please prioritize this !";
		tasks.add(new TaskData("please prioritize this", null, "high", null, null));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task with higher priority
	 */
	@Test
	public void tc13() throws IOException, ParseException {
		String input = "add please prioritize the document today !!";
		tasks.add(new TaskData("please prioritize the document", null, "very high", LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task without "add" at the beginning
	 */
	@Test
	public void tc14() throws IOException, ParseException {
		String input = "watch harry potter";
		tasks.add(new TaskData("watch harry potter", null, "normal", null, null));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
}
