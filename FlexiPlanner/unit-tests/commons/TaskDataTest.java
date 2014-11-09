package commons;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.json.simple.JSONObject;
import org.junit.Test;

//@author A0117989H	

/**
 * This unit test class is to test the important functions
 * of the TaskData class and its methods.
 *
 */

public class TaskDataTest {
	
	/** test the null constructor **/
	@Test
	public void tc1() {
		TaskData task = new TaskData();
		
		assertNull(task.getContent());
		assertNull(task.getCategory());
		assertNull(task.getPriority());
		assertNull(task.getStartDateTime());
		assertNull(task.getEndDateTime());
	}
	
	/** test equal method **/
	@Test
	public void tc2() {
		TaskData t1 = new TaskData();
		t1.setContent("holiday");
		t1.setCategory("perosnal");
		t1.setPriority("high");
		t1.setStartDateTime(LocalDateTime.now());
		t1.setEndDateTime(LocalDateTime.now().plusDays(30));
		TaskData t2 = new TaskData(t1);
		assertTrue(t1.equals(t2));
		
		assertFalse(t1.equals(new TaskData()));
		
		t1 = new TaskData("December holiday");
		t2 = new TaskData("December holiday");
		assertTrue(t1.equals(t2));
	}
	
	/** test equal method w all properties null **/
	@Test
	public void tc3() {
		TaskData t1 = new TaskData();
		TaskData t2 = new TaskData();
		assertTrue(t1.equals(t2));
	}
	
	/** test reminder auto set upon remind date time is not null **/
	@Test
	public void tc4() {
		TaskData t1 = new TaskData("reminder");
		t1.setRemindDateTime(LocalDateTime.now().plusYears(1));
		assertTrue(t1.hasReminder());
		t1.clearReminder();
		assertFalse(t1.hasReminder());
	}
	
	/** test clear reminder when remind date time is LDT.MIN (clear command) **/
	@Test
	public void tc5() {
		TaskData t1 = new TaskData("reminder");
		t1.setRemindDateTime(LocalDateTime.MIN);
		assertNull(t1.getRemindDateTime());
		assertFalse(t1.hasReminder());
	}
	
	/** test convert to JSON object **/
	@Test 
	public void tc6() {
		TaskData t1 = new TaskData("json object test");
		Object obj = t1.convertToJsonObject();
		assertTrue(obj instanceof JSONObject);
	}
	
	/** test toString method **/
	@Test
	public void tc7() {
		TaskData t1 = new TaskData("to String");
		TaskData t2 = new TaskData(t1);
		assertTrue(t1.toString().equals(t2.toString()));
	}
	
	/** test compare chronologically and w priority **/
	@Test
	public void tc8() {
		TaskData t1 = new TaskData();
		TaskData t2 = new TaskData();
		t1.setStartDateTime(LocalDateTime.now().plusDays(1));
		t1.setPriority("normal");
		t2.setStartDateTime(LocalDateTime.now().plusDays(2));
		t1.setPriority("normal");
		//t1 is before t2
		assertEquals(t1.compareTo(t2), -1);
		//t2 is after t1
		assertEquals(t2.compareTo(t1), 1);
		
		TaskData t3 = new TaskData(t1);
		//t1 = t3
		assertEquals(t1.compareTo(t3), 0);
	}

}
