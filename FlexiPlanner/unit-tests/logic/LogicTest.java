package logic;

/**
 * Logic Test cases
 * @author A0112066U
 */

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commons.TaskData;

public class LogicTest {

	CopyLogic logic;

	@Before
	public void initialise() {
		try {
			logic = new CopyLogic();
			logic.clear();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (ParseException e) {
		}
	}

	// normal case + undo + redo
	@Test
	public void testAddNomal() {
		try {
			String command = "add meet Jim from 11/11 2pm 4pm #funny";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);

			TaskData task = new TaskData();
			task.setContent("meet Jim");
			task.setCategory("funny");
			task.setStartDateTime(LocalDateTime.of(2014, 11, 11, 14, 00));
			task.setEndDateTime(LocalDateTime.of(2014, 11, 11, 16, 00));
			task.setPriority("normal");
			assertTrue(logic.taskList.contains(task));

			command = "undo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			assertTrue(!logic.taskList.contains(task));

			command = "redo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			assertTrue(logic.taskList.contains(task));

		} catch (IOException | ParseException e) {
		}
	}

	@Test
	public void testAddFloating() {
		try {
			String command = "meet Jim";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			TaskData task = new TaskData();
			task.setContent("meet Jim");
			task.setCategory("none");
			task.setPriority("normal");
			assertTrue(logic.taskList.contains(task));

		} catch (IOException | ParseException e) {
		}
	}

	// empty input case + undo
	@Test
	public void testAddEmpty() {
		try {
			String command = "";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", s, CopyLogic.MSG_ERROR
					+ CopyLogic.MSG_EMPTY_INPUT);

			command = "undo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_ERROR
					+ CopyLogic.MSG_CANNOT_UNDO, s);

		} catch (IOException | ParseException e) {
		}
	}

	// add an existing task
	@Test
	public void testAddExistingTask() {
		try {
			String command = "add meet Jim from 11/11 2pm 4pm #funny";
			logic.executeInputCommand(command);
			command = "meet Jim from 11/11 2pm 4pm";
			logic.executeInputCommand(command);
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_ERROR
					+ CopyLogic.MSG_EXISTING_TASK, s);

		} catch (IOException | ParseException e) {
		}
	}

	// modify normal case + undo + redo
	@Test
	public void testModifyNormal() {
		try {
			String command = "add meet Jim from 11/11 2pm 4pm #funny";
			logic.executeInputCommand(command);
			command = "modify meet Jim from 11/11 3pm 4pm #project important";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			TaskData task = new TaskData();
			task.setContent("meet Jim");
			task.setCategory("project");
			task.setPriority("high");
			task.setStartDateTime(LocalDateTime.of(2014, 11, 11, 15, 00));
			task.setEndDateTime(LocalDateTime.of(2014, 11, 11, 16, 00));
			assertTrue(logic.taskList.contains(task));

			command = "undo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			task.setCategory("funny");
			task.setPriority("normal");
			task.setStartDateTime(LocalDateTime.of(2014, 11, 11, 14, 00));
			task.setEndDateTime(LocalDateTime.of(2014, 11, 11, 16, 00));
			assertTrue(logic.taskList.contains(task));

			command = "redo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			task.setCategory("project");
			task.setPriority("high");
			task.setStartDateTime(LocalDateTime.of(2014, 11, 11, 15, 00));
			task.setEndDateTime(LocalDateTime.of(2014, 11, 11, 16, 00));
			assertTrue(logic.taskList.contains(task));
		} catch (IOException | ParseException e) {
		}
	}

	// modify one of two tasks of same content + undo + redo
	@Test
	public void testModifyTasksOfSameContent() {
		try {
			String command = "add meet Jim from 11/11 2pm 4pm #funny";
			logic.executeInputCommand(command);
			command = "add meet Jim from 12/11 3pm 4pm #funny";
			logic.executeInputCommand(command);
			command = "modify meet Jim from 13/11 3pm 4pm #project important";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_ASK_FOR_TIME, s);
			command = "11/11 2pm 4pm";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			TaskData task = new TaskData();
			task.setContent("meet Jim");
			task.setCategory("project");
			task.setPriority("high");
			task.setStartDateTime(LocalDateTime.of(2014, 11, 13, 15, 00));
			task.setEndDateTime(LocalDateTime.of(2014, 11, 13, 16, 00));
			assertTrue(logic.taskList.contains(task));

			command = "undo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			task.setCategory("funny");
			task.setPriority("normal");
			task.setStartDateTime(LocalDateTime.of(2014, 11, 11, 14, 00));
			task.setEndDateTime(LocalDateTime.of(2014, 11, 11, 16, 00));
			assertTrue(logic.taskList.contains(task));

			command = "redo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			task.setCategory("project");
			task.setPriority("high");
			task.setStartDateTime(LocalDateTime.of(2014, 11, 13, 15, 00));
			task.setEndDateTime(LocalDateTime.of(2014, 11, 13, 16, 00));
			assertTrue(logic.taskList.contains(task));
		} catch (IOException | ParseException e) {
		}
	}

	// modify non existen task
	@Test
	public void testModifyNonExistTask() {
		try {
			String command = "add meet Jim from 11/11 2pm 4pm #funny";
			logic.executeInputCommand(command);
			command = "modify meet Anna from 11/11 3pm 4pm #project important";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_ERROR
					+ CopyLogic.MSG_NO_TASK_FOUND, s);
		} catch (IOException | ParseException e) {
		}
	}

	// delete normal case + undo + redo
	@Test
	public void testDeleteNormal() {
		try {
			String command = "add meet Jim from 11/11 2pm 4pm #funny";
			logic.executeInputCommand(command);
			command = "delete meet Jim";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			assertTrue(logic.taskList.isEmpty());

			TaskData task = new TaskData();
			task.setContent("meet Jim");
			task.setCategory("funny");
			task.setPriority("normal");
			task.setStartDateTime(LocalDateTime.of(2014, 11, 11, 14, 00));
			task.setEndDateTime(LocalDateTime.of(2014, 11, 11, 16, 00));

			command = "undo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			assertTrue(logic.taskList.contains(task));

			command = "redo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			assertTrue(!logic.taskList.contains(task));
		} catch (IOException | ParseException e) {
		}
	}

	// delete non existen task
	@Test
	public void testDeleteNonExistTask() {
		try {
			String command = "add meet Jim from 11/11 2pm 4pm #funny";
			logic.executeInputCommand(command);
			command = "delete Jim";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_ERROR
					+ CopyLogic.MSG_NO_TASK_FOUND, s);
		} catch (IOException | ParseException e) {
		}
	}

	// delete one of two tasks of same content + undo + redo
	@Test
	public void testDeleteDuplicateTask() {
		try {
			String command = "add meet Jim from 11/11 2pm 4pm #funny";
			logic.executeInputCommand(command);
			command = "add meet Jim from 12/11 3pm 4pm #funny";
			logic.executeInputCommand(command);
			command = "delete meet Jim";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_ASK_FOR_TIME, s);
			command = "12/11 3pm 4pm";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);

			TaskData task = new TaskData();
			task.setContent("meet Jim");
			task.setCategory("funny");
			task.setPriority("normal");
			task.setStartDateTime(LocalDateTime.of(2014, 11, 12, 15, 00));
			task.setEndDateTime(LocalDateTime.of(2014, 11, 12, 16, 00));
			assertTrue(!logic.taskList.contains(task));
			assertTrue(logic.taskList.size() == 1);

			command = "undo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			assertTrue(logic.taskList.contains(task));

			command = "redo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			assertTrue(!logic.taskList.contains(task));
		} catch (IOException | ParseException e) {
		}
	}

	// mark normal
	@Test
	public void testMarkNormal() {
		try {
			String command = "add meet Jim from 11/11 2pm 4pm #funny";
			logic.executeInputCommand(command);
			command = "mark meet Jim";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);

			TaskData task = new TaskData();
			task.setContent("meet Jim");
			task.setCategory("funny");
			task.setPriority("normal");
			task.setStartDateTime(LocalDateTime.of(2014, 11, 11, 14, 00));
			task.setEndDateTime(LocalDateTime.of(2014, 11, 11, 16, 00));
			assertTrue(logic.taskList.isEmpty());
			assertTrue(logic.completedTask.contains(task));

			command = "undo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			assertTrue(logic.taskList.contains(task));
			assertTrue(!logic.completedTask.contains(task));

			command = "redo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			assertTrue(!logic.taskList.contains(task));
			assertTrue(logic.completedTask.contains(task));
		} catch (IOException | ParseException e) {
		}
	}

	// mark one of two task of same content as done
	@Test
	public void testMarkTaskOfSameContent() {
		try {
			String command = "add meet Jim from 11/11 2pm 4pm #funny";
			logic.executeInputCommand(command);
			command = "add meet Jim from 12/11 3pm 4pm #funny";
			logic.executeInputCommand(command);
			command = "mark meet Jim";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_ASK_FOR_TIME, s);
			command = "12/11 3pm 4pm";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);

			TaskData task = new TaskData();
			task.setContent("meet Jim");
			task.setCategory("funny");
			task.setPriority("normal");
			task.setStartDateTime(LocalDateTime.of(2014, 11, 12, 15, 00));
			task.setEndDateTime(LocalDateTime.of(2014, 11, 12, 16, 00));
			assertTrue(!logic.taskList.contains(task));
			assertTrue(logic.completedTask.contains(task));

			command = "undo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			assertTrue(logic.taskList.contains(task));
			assertTrue(!logic.completedTask.contains(task));

			command = "redo";
			s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			assertTrue(!logic.taskList.contains(task));
			assertTrue(logic.completedTask.contains(task));
		} catch (IOException | ParseException e) {
		}
	}

	@Test
	public void testBlockThenAdd() {
		try {
			String command = "block 11/11 1pm 5pm";
			logic.executeInputCommand(command);
			command = "add meet Jim from 11/11 3pm 4pm #funny";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_ERROR + CopyLogic.MSG_CLASHES, s);
		} catch (IOException | ParseException e) {
		}
	}

	public void testUnBlock() {
		try {
			String command = "block 11/11 1pm 5pm";
			logic.executeInputCommand(command);
			command = "unblock 11/11 2pm 3pm";
			String s = logic.executeInputCommand(command);
			assertEquals("fail", CopyLogic.MSG_SUCCESSFUL, s);
			
			TaskData block = new TaskData();
			block.setContent("Blocked slot");
			block.setStartDateTime(LocalDateTime.of(2014, 11, 11, 13, 00));
			block.setEndDateTime(LocalDateTime.of(2014, 11, 11, 14, 00));
			assertTrue(logic.blockedList.contains(block));

			TaskData block1 = new TaskData();
			block1.setContent("Blocked slot");
			block1.setStartDateTime(LocalDateTime.of(2014, 11, 11, 13, 00));
			block1.setEndDateTime(LocalDateTime.of(2014, 11, 11, 14, 00));
			assertTrue(logic.blockedList.contains(block1));
			
			TaskData block2 = new TaskData();
			block2.setContent("Blocked slot");
			block2.setStartDateTime(LocalDateTime.of(2014, 11, 11, 15, 00));
			block2.setEndDateTime(LocalDateTime.of(2014, 11, 11, 17, 00));
			assertTrue(logic.blockedList.contains(block2));

			// undo
			command = "undo";
			s = logic.executeInputCommand(command);
			assertTrue(logic.blockedList.contains(block));
			
			command = "undo";
			s = logic.executeInputCommand(command);
			assertTrue(logic.blockedList.contains(block1));
			assertTrue(logic.blockedList.contains(block2));
			
		} catch (IOException | ParseException e) {
		}
	}
	
	@Test
	public void testSearch() {
		try {
			logic.executeInputCommand("lunch with John 11/11 1pm");
			logic.executeInputCommand("do CS2101 assignment 11/11 8pm 10pm #study");
			logic.executeInputCommand("meet Jim #project very importent 12/11 10am");
			
			logic.executeInputCommand("search assignment"); //search existent
			TaskData t = new TaskData();
			t.setCategory("study");
			t.setContent("do CS2101 assignment");
			t.setPriority("normal");
			t.setStartDateTime(LocalDateTime.of(2014, 11, 11, 20, 00));
			t.setEndDateTime(LocalDateTime.of(2014, 11, 11, 22, 00));
			assertTrue(logic.searchResult.contains(t));
			
			assertTrue(logic.searchResult.contains(t));

		} catch (IOException | ParseException e) {
			
		}
		
	}

	@After
	public void clear() {
		try {
			logic.clear();
		} catch (IOException e) {

		}
	}
}
