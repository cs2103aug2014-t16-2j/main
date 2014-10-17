package Parser;

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