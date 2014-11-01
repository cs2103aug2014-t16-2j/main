package parser;

/**
 * This class creates an Action object that contains a Task object and a String command for the Task object.
 * 
 * @author Choo Xin Min (A0111887Y)
 */

public class Action {

	private Command command;
	private Task task;

	public Action(Command cmd, Task t) {

		command = cmd;
		task = t;

	}

	public Command getCommand() {

		return command;

	}

	public Task getTask() {

		return task;

	}

}