package Parser;

/**
 * This class creates an Action object that contains a Task object and a String command for the Task object.
 * 
 * @author Choo Xin Min (A0111887Y)
 */

public class Action {

	private String command;
	private Task task;

	public Action(String cmd, Task t) {

		command = cmd;
		task = t;

	}

	public String getCommand() {

		return command;

	}

	public Task getTask() {

		return task;

	}

}