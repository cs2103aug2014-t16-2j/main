package reminder;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import commons.TaskData;

//@author A0117989H

/**
 * This class is to test the reminder setup, start and stop methods.
 * It also tests the coverage of ReminderPopup class to display in order.
 *
 */

public class ReminderTest {
	
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	/** test reminder scheduled at a time which is over **/
	@Test
	public void tc1() throws InterruptedException {
		System.setOut(new PrintStream(out));
		TaskData task = new TaskData("reminder test", "project", "high",
				LocalDateTime.now(), LocalDateTime.now().plusHours(2));
		new Reminder(LocalDateTime.now(), task).start();
		assertEquals(out.toString(), "The schedule is over!\n");
		TimeUnit.SECONDS.sleep(5);
	}
	
	/** test reminder on a task which has only end time **/
	@Test
	public void tc2() throws InterruptedException {
		TaskData task = new TaskData("reminder test", "project", "high",
				null, LocalDateTime.now().plusHours(2));
		new Reminder(LocalDateTime.now(), task).start();
		TimeUnit.SECONDS.sleep(5);
	}
	
	/** test reminder on a task which has only start time **/
	@Test
	public void tc3() throws InterruptedException {
		TaskData task = new TaskData("reminder test", "project", "high",
				LocalDateTime.now().plusHours(2), null);
		new Reminder(LocalDateTime.now(), task).start();
		TimeUnit.SECONDS.sleep(5);
	}
	
	/** test reminder on a task which has only content to reminder display **/
	@Test
	public void tc4() throws InterruptedException {
		TaskData task = new TaskData("reminder test", "project", "high",
				null, null);
		new Reminder(LocalDateTime.now(), task).start();
		TimeUnit.SECONDS.sleep(5);
	}
	
	/** repeated test to check displaying pop-ups in order on screen **/
	@Test
	public void tc5() throws InterruptedException {
		TaskData task = new TaskData("reminder test", "project", "high",
				null, null);
		new Reminder(LocalDateTime.now(), task).start();
		TimeUnit.SECONDS.sleep(5);
	}
	
	/** repeated test to check displaying pop-ups in order on screen **/
	@Test
	public void tc6() throws InterruptedException {
		TaskData task = new TaskData("reminder test", "project", "high",
				null, null);
		new Reminder(LocalDateTime.now(), task).start();
		TimeUnit.SECONDS.sleep(5);
	}
	
	/** repeated test to check displaying pop-ups in order on screen **/
	@Test
	public void tc7() throws InterruptedException {
		TaskData task = new TaskData("reminder test", "project", "high",
				null, null);
		new Reminder(LocalDateTime.now(), task).start();
		TimeUnit.SECONDS.sleep(5);
	}

}
