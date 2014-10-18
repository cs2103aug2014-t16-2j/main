package Logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import Storage.*;
import Parser.*;

public class Logic {

	private String command;
	private Task task;
	private ArrayList<TaskData> taskList;
	private ArrayList<TaskData> currentTask;
	private HashMap<String, HashMap<DateInfo, TaskData>> taskIdentifier;
	private Stack<Action> actionList; // for undo and redo
	private Stack<Action> redoList;
	private Storage storer;
	private Parser parser;

	static Scanner sc = new Scanner(System.in);

	// ----------Constructor----------//

	public Logic() throws FileNotFoundException, IOException {
		command = null;
		task = null;
		taskList = new ArrayList<TaskData>();
		currentTask = new ArrayList<TaskData>();
		taskIdentifier = new HashMap<String, HashMap<DateInfo, TaskData>>();
		actionList = new Stack<Action>();
		redoList = new Stack<Action>();
		storer = new TaskFileStorage("text.json"); // At the present, I load and save
											// for file
											// text
		parser = new Parser();
		loadData();
	}

	// -------------Main-------------//
	public static void main(String[] args) {
		try {
			Logic logic = new Logic();
			while (true) {
				String command = sc.nextLine();
				logic.execute(command);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		taskList = new ArrayList<TaskData>(storer.loadData(new Option(true)));
		// select all task from the day before onwards

		for (TaskData t : taskList) {
			String content = t.getContent();
			if (taskIdentifier.containsKey(content)) {
				HashMap<DateInfo, TaskData> map = taskIdentifier.get(content);
				map.put(new DateInfo(t.getStartDateTime(), t.getEndDateTime()),
						t);
			} else {
				taskIdentifier.put(content, new HashMap<DateInfo, TaskData>());
				HashMap<DateInfo, TaskData> map = taskIdentifier.get(content);
				map.put(new DateInfo(t.getStartDateTime(), t.getEndDateTime()),
						t);
			}

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
		String content = t.getContent();
		if (taskIdentifier.containsKey(content)) {
			HashMap<DateInfo, TaskData> map = taskIdentifier.get(content);
			map.put(new DateInfo(t.getStartDateTime(), t.getEndDateTime()), t);
		} else {
			taskIdentifier.put(content, new HashMap<DateInfo, TaskData>());
			HashMap<DateInfo, TaskData> map = taskIdentifier.get(content);
			map.put(new DateInfo(t.getStartDateTime(), t.getEndDateTime()), t);
		}
		taskList.add(t);
		currentTask.add(t);
		storer.saveData(currentTask, true);
		currentTask.clear();
	}

	// delete a task described by content -> doesn't handle task with same
	// content
	private void deleteTask(Task task) {
		TaskData t = toTaskDaTa(task);
		if (taskIdentifier.containsKey(t.getContent())) {
			HashMap<DateInfo, TaskData> toDeleteList = taskIdentifier.get(t
					.getContent());
			TaskData toDelete = null;
			assert toDeleteList.size() >= 1;
			if (toDeleteList.size() == 1) {
				for (TaskData _task : toDeleteList.values()) {
					toDelete = _task;
				}
				taskIdentifier.remove(toDelete.getContent());
			} else {
				System.out.print("Provide start and end time\n"); // ask for
																	// date and
																	// time to
																	// specify

				String s = sc.nextLine();
				Task _task = parser.getAction(s).getTask();
				LocalDateTime st = _task.getStartDateTime();
				LocalDateTime et = _task.getEndDateTime();
				DateInfo d = new DateInfo(st, et);
				if (toDeleteList.containsKey(d)) {
					toDelete = toDeleteList.get(d);
					toDeleteList.remove(d, toDelete);
				}
				else {
					System.out.print("Error-----------");
					return;
				}

			}
			taskList.remove(toDelete);
		}

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

	private void modifyTask(Task task) {
		String content = task.getContent();

		LocalDateTime newStartTime = task.getStartDateTime();
		LocalDateTime newEndTime = task.getEndDateTime();
		String newCategory = task.getCategory();
		String newPriority = task.getPriority();
		//boolean isDone_new = task.isDone(); // removed

		HashMap<DateInfo, TaskData> _taskToEdit = taskIdentifier.get(content);
		TaskData savedTask = null;
		for (TaskData t : _taskToEdit.values()) {
			savedTask = t;
			break;
		}

		if (newStartTime != null)
			savedTask.setStartDateTime(newStartTime);
		if (newEndTime != null)
			savedTask.setEndDateTime(newEndTime);
		if (newCategory != null)
			savedTask.setCategory(newCategory);
		if (newPriority != null)
			savedTask.setPriority(newPriority);
		//savedTask.setDone(isDone_new); // removed

		saveData();
	}

	// search for a task by key words or time

	protected String searchRes;

	private void search(Task task) {
		
		 
		ArrayList<TaskData> searchResult = new ArrayList<TaskData>();

		String content = task.getContent();
		String[] words = content.split(" ");
		LocalDateTime startTime = task.getStartDateTime();
		LocalDateTime endTime = task.getEndDateTime();
		String category = task.getCategory();
		String priority = task.getPriority();

		ArrayList<TaskData> toSearch;
		if (startTime != null && endTime != null) {
		toSearch = storer.loadData(new Option(startTime,
				endTime));
		} else  {
			toSearch = storer.loadData(new Option(true));
		}

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
		searchRes = displaySearch(searchResult);
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

	// mark as done
	private void markAsDone(Task _task) {
		String content = _task.getContent();
		HashMap<DateInfo, TaskData> _taskToEdit = taskIdentifier.get(content);
		TaskData task = null;
		for (TaskData t : _taskToEdit.values()) {
			task = t;
			break;
		}
		// task.setDone(true); // removed
		saveData();
	}

	// exit

	private void exit() {
		// store when exit
		saveData();
		System.exit(0);
	}

	//
	private void saveData() {
		storer.saveData(taskList, false);
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
		ArrayList<TaskData> taskToShow = storer.loadData(new Option(today,
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

		ArrayList<TaskData> task = storer.loadData(new Option(startTime,
				endTime));

		// System.out.println(date + " " + startTime + " " + endTime + " "
		// +!task.isEmpty());
		// System.out.println(displaySearch(task));
		// return !task.isEmpty();
		return false;

	}

	// Translate a Task to TaskData for storage

	private TaskData toTaskDaTa(Task task) {
		String content = task.getContent();
		LocalDateTime startTime = task.getStartDateTime();
		LocalDateTime endTime = task.getEndDateTime();
		String category = task.getCategory();
		String priority = task.getPriority();
		//boolean isDone = task.isDone(); // removed
		
		return new TaskData(content, category, priority, startTime, endTime);

		// return new TaskData(content, category, priority, startTime, endTime, isDone); // removed isDone

	}

	private class DateInfo {
		LocalDateTime start, end;

		DateInfo(LocalDateTime _start, LocalDateTime _end) {
			if (_start == null) {
				this.start = LocalDateTime.MIN;
			} else {
				this.start = _start;
			}
			if (_end == null) {
				this.end = LocalDateTime.MAX;
			} else {
				this.end = _end;
			}

		}

		@Override
		public boolean equals(Object t) {
			if (t instanceof DateInfo) {
				DateInfo time = (DateInfo) t;
				return this.start.equals(time.start)
						&& this.end.equals(time.end);
			} else
				return false;
		}

		@Override
		public int hashCode() {
			String s = start + "" + end;
			return s.hashCode();

		}
	}

}