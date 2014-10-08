package Logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import FlexiPlanner.Storage.*;
import Parser.*;

public class Logic {

	private String command;
	private Task task;
	private ArrayList<TaskData> taskList;
	private ArrayList<TaskData> currentTask;
	private Map<String, TaskData> mapContentToTask;
	private Stack<Action> actionList; // for undo and redo
	private Stack<Action> redoList;
	private Storage store;
	private Parser parser;

	// ----------Constructor----------//

	public Logic() throws FileNotFoundException, IOException {
		command = null;
		task = null;
		taskList = new ArrayList<TaskData>();
		currentTask = new ArrayList<TaskData>();
		mapContentToTask = new HashMap<String, TaskData>();
		actionList = new Stack<Action>();
		redoList = new Stack<Action>();
		store = new Storage("text"); // At the present, I load and save for file
										// text
		parser = new Parser();
		loadData();
	}

	// ----------Method-------------//

	// this method is to extract command and task from input command
	// using parser
	private void extractCommandandTask(String _command) {
		Action commandAndTask = parser.getAction(_command);
		command = commandAndTask.getCommand();
		task = commandAndTask.getTask();

		// push command into undo stack, except undo and redo
		// currently cannot handle edit
		if (!command.equalsIgnoreCase("undo")
				&& !command.equalsIgnoreCase("redo")
				&& !command.equalsIgnoreCase("modify")
				&& !command.equalsIgnoreCase("search"))
			actionList.push(commandAndTask);

	}

	// load all data saved in the file
	private void loadData() {
		taskList = new ArrayList<TaskData>(store.loadData(new Option(true)));
		// select all task from the day before onwards

		for (TaskData t : taskList) {
			mapContentToTask.put(t.getContent(), t);
		}
	}

	// this method is to execute a command
	public void execute(String _command) {
		extractCommandandTask(_command);
		executeCommand(command, task);
	}

	private void executeCommand(String command, Task task) {
		switch (command) {
		case "add":
			addTask(task);
			break;
		case "delete":
			deleteTask(task);
			break;
		case "modify":
			modifyTask(task);
			break;
		case "undo":
			undo();
			break;
		case "redo":
			redo();
			break;
		case "search":
			search(task);
			break;
		case "exit":
			exit();
		default:
			return;
		}
	}

	// add a task
	private void addTask(Task task) {
		TaskData t = toTaskDaTa(task);
		mapContentToTask.put(t.getContent(), t);
		taskList.add(t);
		currentTask.add(t);
		store.saveData(currentTask, true);
		currentTask.clear();
	}

	// delete a task described by content -> doesn't handle task with same
	// content
	private void deleteTask(Task task) {
		TaskData t = toTaskDaTa(task);
		TaskData toDelete = mapContentToTask.get(t.getContent());
		taskList.remove(toDelete);
		mapContentToTask.remove(toDelete.getContent());
		saveData();
	}

	// redo an action
	private void redo() {
		if (redoList.isEmpty())
			return;
		Action done = redoList.pop();
		actionList.push(done);
		String command = done.getCommand();
		Task task = done.getTask();
		executeCommand(command, task);
	}

	// undo. Currently undo supports undo add and delete
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
		case "modify":
			break;
		}

	}

	private void undoDelete(Action done) {
		addTask(done.getTask());
	}

	private void undoAdd(Action done) {
		deleteTask(done.getTask());
	}

	// modify a task

	public void modifyTask(Task task) {
		String newContent = task.getContent();
		LocalDateTime newStartTime = task.getStartDateTime();
		LocalDateTime newEndTime = task.getEndDateTime();
		String newCategory = task.getCategory();
		String newPriority = task.getPriority();
		boolean isDone_new = task.isDone();

		TaskData savedTask = mapContentToTask.get(newContent);

		if (newStartTime != null)
			savedTask.setStartDateTime(newStartTime);
		if (newEndTime != null)
			savedTask.setEndDateTime(newEndTime);
		if (newCategory != null)
			savedTask.setCategory(newCategory);
		if (newPriority != null)
			savedTask.setPriority(newPriority);
		savedTask.setDone(isDone_new);

		saveData();
	}

	// search for a task by key words or time

	private void search(Task task) {
		ArrayList<TaskData> searchResult = new ArrayList<TaskData>();

		String content = task.getContent();
		String[] words = content.split(" ");
		LocalDateTime startTime = task.getStartDateTime();
		LocalDateTime endTime = task.getEndDateTime();
		String category = task.getCategory();
		String priority = task.getPriority();

		ArrayList<TaskData> toSearch = store.loadData(new Option(startTime,
				endTime));

		for (TaskData t : toSearch) {
			String _content = t.getContent();
			String _category = t.getCategory();
			String _priority = t.getPriority();

			if (category != null && !_category.equalsIgnoreCase(category))
				continue;
			if (priority != null && !_priority.equalsIgnoreCase(priority))
				continue;

			boolean isContained = true;

			for (String s : words) {
				if (!_content.contains(s)) {
					isContained = false;
					break;
				}
			}
			if (isContained)
				searchResult.add(t);
		}
		System.out.print(displaySearch(searchResult));
	}

	private String displaySearch(ArrayList<TaskData> list) {
		String lines = "Search result:\n";
		if (!list.isEmpty()) {
			for (TaskData t : list) {
				lines += t.toString();
				lines += "\n";
			}
		}
		return lines;
	}

	// exit

	private void exit() {
		// store when exit
		saveData();
		System.exit(0);
	}

	//
	private void saveData() {
		store.saveData(taskList, false);
	}

	// return data to show to UI
	protected String dataToShow() {
		LocalDateTime now = LocalDateTime.now();
		int dateToday = now.getDayOfMonth();
		int monthToday = now.getMonthValue();
		int yearToday = now.getYear();
		LocalDateTime today = LocalDateTime.of(yearToday, monthToday,
				dateToday, 0, 0, 0);
		LocalDateTime tomorrow = today.plusSeconds(172799);
		ArrayList<TaskData> taskToShow = store.loadData(new Option(today,
				tomorrow));

		return showToUser(taskToShow);
	}

	private String showToUser(ArrayList<TaskData> taskToShow) {
		String text = "";
		for (TaskData t : taskToShow) {
			if (t.getPriority() != null)
				text += t.getPriority() + " ";
			if (t.getCategory() != null)
				text += t.getCategory() + " ";
			text += t.getContent();
			if (t.getStartDateTime() != null)
				text += "\n    From: " + t.getStartDateTime();
			if (t.getEndDateTime() != null)
				text += "\n    To: " + t.getEndDateTime();
			text += "\n";
		}
		return text;
	}

	// check if a date has task
	protected boolean hasTask(String date) {
		
		Task testTask = parser.getAction(date).getTask();
		LocalDateTime time = testTask.getStartDateTime();
		int dayOfMonth = time.getDayOfMonth();
		int month = time.getMonthValue();
		int year = time.getYear();
		LocalDateTime startTime = LocalDateTime.of(year, month, dayOfMonth, 0,
				0, 0);
		LocalDateTime endTime = LocalDateTime.of(year, month, dayOfMonth, 23,
				59, 59);
		
		ArrayList<TaskData> task = store.loadData(new Option(startTime, endTime));
		
		System.out.println(date + " " + startTime + " " + endTime + " " +!task.isEmpty());
		System.out.println(displaySearch(task));
		return !task.isEmpty();

	}

	// Translate a Task to TaskData for storage

	private TaskData toTaskDaTa(Task task) {
		String content = task.getContent();
		LocalDateTime startTime = task.getStartDateTime();
		LocalDateTime endTime = task.getEndDateTime();
		String category = task.getCategory();
		String priority = task.getPriority();
		boolean isDone = task.isDone();

		return new TaskData(content, category, priority, startTime, endTime,
				isDone);

	}

}
