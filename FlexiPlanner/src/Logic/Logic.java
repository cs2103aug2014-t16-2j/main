package Logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import FlexiPlanner.Storage.*;
import Parser.*;

public class Logic {

	String command;
	Task task;
	ArrayList<TaskData> taskList;
	ArrayList<TaskData> currentTask;
	Map<String, TaskData> mapContentToTask;
	Stack<Action> actionList; // for undo and redo
	Stack<Action> redoList;
	Storage store;
	Parser parser;
	static Scanner scanner;

	public static void main(String args[]) {
		scanner = new Scanner(System.in);
		try {
			Logic controller = new Logic();
			while (true) {
				String command = readInputCommand();
				controller.execute(command);

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String readInputCommand() {
		String s = scanner.nextLine();
		return s;
	}

	public Logic() throws FileNotFoundException, IOException {
		command = null;
		task = null;
		taskList = new ArrayList<TaskData>();
		currentTask = new ArrayList<TaskData>();
		mapContentToTask = new HashMap<String, TaskData>();
		actionList = new Stack<Action>();
		redoList = new Stack<Action>();
		store = new Storage("text"); // At the present, I load and save for file text
		parser = new Parser();
		loadData();
	}

	private void determineCommandandTask(String _command) {
		Action commandAndTask = parser.getAction(_command);
		command = commandAndTask.getCommand();
		if (!command.equalsIgnoreCase("undo")
				&& !command.equalsIgnoreCase("redo") && command.equalsIgnoreCase("edit"))
			actionList.push(commandAndTask);

		// System.out.print(command);
		task = commandAndTask.getTask();
	}

	public void loadData() {
		taskList = new ArrayList<TaskData>(store.loadData(new Option(true)));
		// select all task from the day before onwards

		for (TaskData t : taskList) {
				mapContentToTask.put(t.getContent(), t);
		}
	}

	public void execute(String _command) {
		determineCommandandTask(_command);

		executeCommand(command, task);
	}

	public void executeCommand(String command, Task task) {
		switch (command) {
		case "add":
			addTask(task);
			break;
		case "delete":
			deleteTask(task);
			break;
		case "edit":
			editTask(task);
			break;
		case "undo":
			undo();
			break;
		case "redo":
			redo();
			break;
		case "exit":
			exit();
		default:
			return;
		}
	}

	private void redo() {
		if (redoList.isEmpty())
			return;
		Action done = redoList.pop();
		actionList.push(done);
		String command = done.getCommand();
		Task task = done.getTask();
		executeCommand(command, task);
	}

	private void undo() {
		if (actionList.isEmpty())
			return;
		Action done = actionList.pop();
		redoList.push(done);
		String command = done.getCommand();
		switch (command) {
		case "add":
			undoAdd(done);
			break;
		case "delete":
			undoDelete(done);
			break;
		case "edit":
		}
		
	}

	private void undoDelete(Action done) {
		addTask(done.getTask());

	}

	private void undoAdd(Action done) {
		deleteTask(done.getTask());

	}

	public void addTask(Task task) {
		TaskData t = toTaskDaTa(task);
		mapContentToTask.put(t.getContent(), t);
		taskList.add(t);
		currentTask.add(t);
		store.saveData(currentTask, true);
		currentTask.clear();
	}

	public void deleteTask(Task task) {
		TaskData t = toTaskDaTa(task);
		TaskData toDelete = mapContentToTask.get(t.getContent());
		taskList.remove(toDelete);
		mapContentToTask.remove(toDelete.getContent());
		saveData();
	}

	public void editTask(Task task) {
		String newContent = task.getContent();
		LocalDateTime newStartTime = task.getStartDateTime();
		LocalDateTime newEndTime = task.getEndDateTime();
		String newCategory = task.getCategory();
		String newPriority = task.getPriority();
		boolean isDone_new = task.isDone();
		
		TaskData savedTask = mapContentToTask.get(newContent);
		
		if (newStartTime != null) savedTask.setStartDateTime(newStartTime);
		if (newEndTime != null) savedTask.setEndDateTime(newEndTime);
		if (newCategory != null) savedTask.setCategory(newCategory);
		if (newPriority != null) savedTask.setPriority(newPriority);
		savedTask.setDone(isDone_new);
		
		saveData();
	}

	public void exit() {
		// store when exit
		saveData();
		System.exit(0);
	}

	private void saveData() {
		store.saveData(taskList, false);
	}

	// Translate a Task to TaskData for storage

	private TaskData toTaskDaTa(Task task) {
		String content = task.getContent();
		LocalDateTime startTime = task.getStartDateTime();
		LocalDateTime endTime = task.getEndDateTime();
		String category = task.getCategory();
		String priority = task.getPriority();
		boolean isDone = task.isDone();

		return new TaskData(content, category, priority, startTime, endTime, isDone);

	}
}
