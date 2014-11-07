package parser;

//@author A0111887Y
//This class creates an Action object that contains a Task object and a String command for the Task object.

public class Action {

	private Command command;
	private Task task;

	//@author A0111887Y
	
	public Action(Command cmd, Task t) {

		command = cmd;
		task = t;

	}

	//@author A0111887Y
	
	public Command getCommand() {

		return command;

	}

	//@author A0111887Y
	
	public Task getTask() {

		return task;

	}

}