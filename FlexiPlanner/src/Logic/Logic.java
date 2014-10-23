package Logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import org.json.simple.parser.ParseException;

import Storage.*;
import Parser.*;

public class Logic {

	private String command;
	private Task task;
	private ArrayList<TaskData> taskList;
	private ArrayList<TaskData> taskToBeAdded;
	private HashMap<String, HashMap<DateInfo, TaskData>> taskIdentifier;
	private HashMap<String, TaskData> completedTaskIdentifier;
	private Stack<ActionEntry> actionList; // for undo and redo
	private Stack<ActionEntry> redoList;
	private FileStorage storer;
	private Parser parser;
	private Action action;
	private ActionEntry entry;
	private SearchTool searchTool;

	private ArrayList<TaskData> currentDisplayedTask;

	private ArrayList<TaskData> completedTask;
	private FileStorage storerForCompleted;

	String filePath = "text.json";
	String completedpath = "completed.json";
	boolean isSuspendedAction = false;
	Action suspendingAction;
	static Scanner sc = new Scanner(System.in);

	// ------------------Constructor-------------------------//

	public Logic() throws FileNotFoundException, IOException, ParseException {
		storer = new FileStorage();
		storerForCompleted = new FileStorage(); // For
												// compeleted
												// task
		command = null;
		task = null;
		taskList = new ArrayList<TaskData>();
		taskToBeAdded = new ArrayList<TaskData>();
		taskIdentifier = new HashMap<String, HashMap<DateInfo, TaskData>>();
		completedTaskIdentifier = new HashMap<String, TaskData>();
		actionList = new Stack<ActionEntry>();
		redoList = new Stack<ActionEntry>();
		currentDisplayedTask = new ArrayList<TaskData>();
		completedTask = new ArrayList<TaskData>();
		parser = new Parser();
		searchTool = new SearchTool();
		entry = new ActionEntry(action, null);
		loadData();
	}

	// -------------------------------Main-----------------------------------//
	public static void main(String[] args) {
		try {
			Logic logic;
			logic = new Logic();
			while (true) {
				String command = sc.nextLine();
				logic.executeInputCommand(command);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ---------------------------------Method-----------------------------//

	// load all data saved in the file
	//@author A0112066U
	private void loadData() throws IOException, ParseException {
		taskList = new ArrayList<TaskData>(storer.loadTasks(filePath,
				new Option(true)));
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
		completedTask = new ArrayList<TaskData>(storerForCompleted.loadTasks(
				completedpath, new Option(true)));

	}

	// this method is to execute a command
	//@author A0112066U
	public String executeInputCommand(String _command) throws IOException,
			ParseException {
		if (_command == null || _command.isEmpty()) {
			return "Please try with an input";
		} else {
			extractCommandandTask(_command);
			boolean isSuccessful;
			isSuccessful = executeCommand(command, task);
			if (isSuccessful)
				return "Successful";
			else if (isSuspendedAction)
				return "Please provide start and end time";
			else
				return "Error";
		}
	}

	// this method is to extract command and task from input command
	// using parser
	// @author A0112066U
	private void extractCommandandTask(String _command) {

		action = parser.getAction(_command);
		command = action.getCommand();
		task = action.getTask();
		if (isSuspendedAction) {
			command = suspendingAction.getCommand();
			Task t = suspendingAction.getTask();
			task.setContent(t.getContent());
		}

	}

	// @author A0112066U
	private boolean executeCommand(String command, Task task)
			throws IOException, ParseException {
		boolean isSuccessful;
		switch (command) {
		case "add":
			isSuccessful = addTask(toTaskData(task));
			if (isSuccessful)
				actionList.push(new ActionEntry(action, null));
			return isSuccessful;
		case "delete":
			isSuccessful = deleteTask(toTaskData(task));
			if (isSuccessful)
				actionList.push(new ActionEntry(action, null));
			return isSuccessful;
		case "modify":
			isSuccessful = modifyTask(task, null);
			if (isSuccessful)
				actionList.push(entry);
			return isSuccessful;
		case "undo":
			isSuccessful = undo();
			return isSuccessful;
		case "redo":
			isSuccessful = redo();
			return isSuccessful;
		case "search":
			isSuccessful = search(task);
			return isSuccessful;
		case "mark":
			isSuccessful = markAsDone(task);
			if (isSuccessful)
				actionList.push(new ActionEntry(action, null));
			return isSuccessful;
		case "exit":
			exit();
		default:
			return false;
		}
	}

	// add a task
	// @author A0112066U
	private boolean addTask(TaskData task) {

		String content = task.getContent();
		if (taskIdentifier.containsKey(content)) {
			HashMap<DateInfo, TaskData> map = taskIdentifier.get(content);
			TaskData t = searchTool.findTaskByContentandDate(task, map);
			// this is to determine whether the task has been added
			if (t != null) {
				System.out.println("Task has been created");
				return false;
			} else {
				map.put(new DateInfo(task.getStartDateTime(), task
						.getEndDateTime()), task);
			}
		} else {
			taskIdentifier.put(content, new HashMap<DateInfo, TaskData>());
			HashMap<DateInfo, TaskData> map = taskIdentifier.get(content);
			map.put(new DateInfo(task.getStartDateTime(), task.getEndDateTime()),
					task);
		}
		taskList.add(task);
		taskToBeAdded.add(task);
		currentDisplayedTask.add(0, task);
		try {
			storer.saveTasks(filePath, taskToBeAdded, true);
		} catch (Exception e) {
			System.out.println("Error while saving data");
			return false;
		}
		taskToBeAdded.clear();
		return true;

	}

	// delete a task described by content
	// @author A0112066U
	private boolean deleteTask(TaskData task) {

		String content = task.getContent();
		if (isInteger(content))
			deleteIndex(Integer.parseInt(content));
		if (taskIdentifier.containsKey(content)) {
			HashMap<DateInfo, TaskData> toDeleteList = taskIdentifier
					.get(content);
			TaskData toDelete = null;
			LocalDateTime st = task.getStartDateTime();
			LocalDateTime et = task.getEndDateTime();
			if (st != null || et != null) {
				DateInfo d = new DateInfo(st, et);
				toDelete = toDeleteList.get(d);
				if (isSuspendedAction) {
					suspendingAction = null;
					isSuspendedAction = false;
				}
			} else {
				toDelete = searchTool.findTaskByContentandDate(task,
						toDeleteList);
				if (toDelete.getContent().equals("xxxxxxxxxxxxxxxxxxxx")) {
					suspendingAction = action;
					isSuspendedAction = true;
					return false;
				}

			}
			if (toDelete != null) {
				if (toDeleteList.size() == 1)
					taskIdentifier.remove(content);
				else {
					DateInfo d = new DateInfo(toDelete.getStartDateTime(),
							toDelete.getEndDateTime());

					toDeleteList.remove(d);

				}
				taskList.remove(toDelete);
				if (currentDisplayedTask.contains(toDelete)) {
					currentDisplayedTask.remove(toDelete);
				}

				Task t = new Task();
				t.setContent(toDelete.getContent());
				t.setCategory(toDelete.getCategory());
				t.setStartDateTime(toDelete.getStartDateTime());
				t.setEndDateTime(toDelete.getEndDateTime());
				t.setPriority(toDelete.getPriority());
				action = new Action("delete", t);
			} else {
				return false;
			}
		}

		try {
			saveData();
		} catch (IOException e) {
			System.out.println("Error while saving data");
			return false;
		}
		return true;
	}

	// @author A0112066U
	private void deleteIndex(int index) {
		int size = currentDisplayedTask.size();
		if (index < 1 || index > size) {
			System.out.print("Error-----------");
			return;
		} else {
			TaskData task = currentDisplayedTask.get(index - 1);
			deleteTask(task);
		}
	}

	// redo an action
	// @author A0112066U
	private boolean redo() throws IOException, ParseException {
		if (redoList.isEmpty()) {
			return false;
		}
		ActionEntry ae = redoList.pop();
		Action done = ae.getAction();
		Task t = ae.getTask();
		actionList.push(ae);
		
		String command = done.getCommand();
		Task task = done.getTask();
		TaskData _task = toTaskData(task);
		switch (command) {
		case "add":
			return addTask(_task);
		case "delete":
			return deleteTask(_task);
		case "modify":
			return modifyTask(task, ae.getTask());
		case "mark":
			return markAsDone(task);
		}
		return false;
	}

	// @author A0112066U
	private boolean undo() {
		boolean isSuccessful = false;
		if (actionList.isEmpty()) {
			return isSuccessful;
		}
		ActionEntry x = actionList.pop();
		Action done = x.getAction();
		String command = done.getCommand();
		switch (command) {
		case "add":
			isSuccessful = undoAdd(done);
			break;
		case "delete":
			isSuccessful = undoDelete(done);
			break;
		case "modify":
			isSuccessful = undoModify(done, x.getTask());
			break;
		case "mark":
			isSuccessful = undoMark(done);
			break;
		}
		return isSuccessful;

	}

	// @author A0112066U
	private boolean undoDelete(Action done) {
		boolean isSuccessful = addTask(toTaskData(done.getTask()));
		if (isSuccessful)
			redoList.push(new ActionEntry(done, null));
		else
			actionList.push(new ActionEntry(done, null));
		return isSuccessful;
	}

	// @author A0112066U
	private boolean undoAdd(Action done) {
		boolean isSuccessful = deleteTask(toTaskData(done.getTask()));
		if (isSuccessful) 
			redoList.push(new ActionEntry(done, null));
		else
			actionList.push(new ActionEntry(done, null));
		return isSuccessful;
	}

	// @author A0112066U
	private boolean undoModify(Action done, Task t) {
		boolean isSuccessful = modifyTask(done.getTask(), t);		
		if (isSuccessful)
			redoList.push(entry);
		else
			actionList.push(entry);
		return isSuccessful;

	}

	// bug here
	// @author A0112066U
	private boolean undoMark(Action done) {
		Task task = done.getTask();
		boolean isSuccessful = false;
		String s = task.getStartDateTime() + "" + task.getEndDateTime();
		TaskData _task = completedTaskIdentifier.get(s);
		try {
			completedTask.remove(_task);
			isSuccessful = addTask(_task);
			saveCompletedTask();
		} catch (IOException e) {
			isSuccessful = false;
		}
		if (isSuccessful) {
			redoList.push(new ActionEntry(done, null));
		} else {
			actionList.push(new ActionEntry(done, null));
		}
		return isSuccessful;

	}

	// modify a task
	// @author A0112066U
	private boolean modifyTask(Task task, Task t) {
		String content = task.getContent();
		if (!isInteger(content)) {
			return modifyTask(task, t, content);
		} else {
			return modifyIndex(task, Integer.parseInt(content));
		}
	}

	// @author A0112066U
	private boolean modifyTask(Task _task, Task t, String content) {
		LocalDateTime newStartTime = _task.getStartDateTime();
		LocalDateTime newEndTime = _task.getEndDateTime();
		String newCategory = _task.getCategory();
		String newPriority = _task.getPriority();
		HashMap<DateInfo, TaskData> _listTaskToEdit = new HashMap<Logic.DateInfo, TaskData>();
		if (taskIdentifier.containsKey(content))
			_listTaskToEdit = taskIdentifier.get(content);

		

			LocalDateTime st = _task.getStartDateTime();
			LocalDateTime et = _task.getEndDateTime();

			TaskData toFind = new TaskData(content, null, null, null, null);
			TaskData taskToModify = null;
			if (isSuspendedAction) {
				toFind.setStartDateTime(newStartTime);
				toFind.setEndDateTime(newEndTime);
				Task itask = suspendingAction.getTask();
				newStartTime = itask.getStartDateTime();
				newEndTime = itask.getEndDateTime();
				suspendingAction = null;
			}
			if (t != null) {
				st = t.getStartDateTime();
				et = t.getEndDateTime();
				toFind.setStartDateTime(st);
				toFind.setEndDateTime(et);
			}

			taskToModify = searchTool.findTaskByContentandDate(toFind,
					_listTaskToEdit);

			if (taskToModify != null
					&& taskToModify.getContent().equals("xxxxxxxxxxxxxxxxxxxx")) {
				suspendingAction = new Action(command, _task);
				isSuspendedAction = true;
				return false;
			}
			isSuspendedAction = false;
			if (taskToModify != null) {
				LocalDateTime oldStartTime = taskToModify.getStartDateTime();
				LocalDateTime oldEndTime = taskToModify.getEndDateTime();
				String oldCategory = taskToModify.getCategory();
				String oldPriority = taskToModify.getPriority();
				if (t == null) {
					t = new Task();
					t.setContent(content);
				}
				toFind.setCategory(newCategory);
				toFind.setPriority(newPriority);
				toFind.setStartDateTime(newStartTime);
				toFind.setEndDateTime(newEndTime);
				
				toFind = searchTool.findTaskByContentandDate(toFind, _listTaskToEdit);
				if (toFind != null) {
					return false;
				}
				if (newStartTime != null) {
					taskToModify.setStartDateTime(newStartTime);
					t.setStartDateTime(newStartTime);
					_task.setStartDateTime(oldStartTime);
				}
				if (newEndTime != null) {
					taskToModify.setEndDateTime(newEndTime);
					t.setEndDateTime(newEndTime);
					_task.setEndDateTime(oldEndTime);
				}
				if (newCategory != null) {
					taskToModify.setCategory(newCategory);
					t.setCategory(newCategory);
					_task.setCategory(oldCategory);
				}
				if (newPriority != null) {
					taskToModify.setPriority(newPriority);
					t.setPriority(newPriority);
					_task.setCategory(oldPriority);
				}
				_listTaskToEdit.remove(new DateInfo(oldStartTime, oldEndTime));
				_listTaskToEdit.put(new DateInfo(newStartTime, newEndTime),
						taskToModify);
				action = new Action("modify", _task);
				entry = new ActionEntry(action, t);

			} else {
				
				return false;
			}
		

		try {
			saveData();
		} catch (IOException e) {
			System.out.println("Error while saving data");
			return false;
		}
		return true;
	}

	// @author A0112066U
	private boolean modifyIndex(Task task, int index) {
		int size = currentDisplayedTask.size();
		if (index < 1 || index > size) {
			System.out.print("Error-----------");
			return false;
		} else {
			TaskData _task = currentDisplayedTask.get(index);
			String content = _task.getContent();
			return modifyTask(task, null, content);
		}

	}

	// search for a task by key words or time
	// @author A0112066U
	protected String searchRes;

	private boolean search(Task task) throws IOException, ParseException {
		searchRes = null;
		if (!task.isDone()) {
			searchRes = searchTool.search(taskList, task);
		} else {
			searchRes = searchTool.search(completedTask, task);
		}
		if (searchRes != null) {
			return true;
		}
		return false;
	}

	// mark as done
	// @author A0112066U
	private boolean markAsDone(Task _task) {
		boolean isSuccessful = false;
		String content = _task.getContent();
		HashMap<DateInfo, TaskData> _taskToEdit = taskIdentifier.get(content);
		TaskData task = null;
		LocalDateTime st = _task.getStartDateTime();
		LocalDateTime et = _task.getEndDateTime();
		if (st != null || et != null) {
			DateInfo d = new DateInfo(st, et);
			task = _taskToEdit.get(d);
			suspendingAction = null;
			isSuspendedAction = false;
		} else {
			task = searchTool.findTaskByContentandDate(toTaskData(_task),
					_taskToEdit);
			if (task.getContent().equals("xxxxxxxxxxxxxxxxxxxx")) {
				suspendingAction = action;
				isSuspendedAction = true;
				return false;
			} else {
				suspendingAction = null;
				isSuspendedAction = false;
			}
		}
		if (task != null) {
			deleteTask(task);
			String s = task.getStartDateTime() + "" +task.getEndDateTime() ;
			completedTaskIdentifier.put(s, task);
			completedTask.add(task);
			_task.setCategory(task.getCategory());
			_task.setPriority(task.getPriority());
			_task.setStartDateTime(task.getStartDateTime());
			_task.setEndDateTime(task.getEndDateTime());
			_task.setDone(true);
			action = new Action(command, _task);
			isSuccessful = true;
		} else {
			return isSuccessful;
		}

		try {
			saveData();
			saveCompletedTask();
		} catch (IOException e) {
			System.out.println("Unsuccessful");
			return false;
		}
		return isSuccessful;
	}

	// exit
	// @author A0112066U
	private void exit() {
		// store when exit
		try {
			saveData();
			saveCompletedTask();
		} catch (IOException e) {
			System.out.println("Error while saving data");
		}
		System.exit(0);
	}

	// @author A0112066U
	private void saveData() throws IOException {
		storer.saveTasks(filePath, taskList, false);
	}

	private void saveCompletedTask() throws IOException {
		storer.saveTasks(completedpath, completedTask, false);
	}

	// return data to show to UI
	// @author A0112066U
	public String getTodayTask() throws IOException, ParseException {
		LocalDateTime now = LocalDateTime.now();
		int dateToday = now.getDayOfMonth();
		int monthToday = now.getMonthValue();
		int yearToday = now.getYear();
		LocalDateTime today = LocalDateTime.of(yearToday, monthToday,
				dateToday, 0, 0, 0).minusSeconds(1);
		LocalDateTime tomorrow = today.plusSeconds(86402);
		ArrayList<TaskData> taskToShow = new ArrayList<TaskData>();

		for (TaskData _task : taskList) {

			LocalDateTime st = _task.getStartDateTime();
			LocalDateTime et = _task.getEndDateTime();
			if (st != null && st.isAfter(today) && st.isBefore(tomorrow)) {
				taskToShow.add(_task);
			} else if (et != null && et.isAfter(today) && et.isBefore(tomorrow)) {
				taskToShow.add(_task);
			}
		}

		return showToUser(taskToShow);
	}

	// @author A0112066U
	protected String dataToShow() throws IOException, ParseException {
		return showToUser(currentDisplayedTask);
	}

	// @author A0112066U
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
	// @author A0112066U
	public boolean hasTask(String date) throws IOException, ParseException {

		Task testTask = parser.getAction(date).getTask();
		LocalDateTime time = testTask.getStartDateTime();
		int dayOfMonth = time.getDayOfMonth();
		int month = time.getMonthValue();
		int year = time.getYear();
		LocalDateTime startTime = LocalDateTime.of(year, month, dayOfMonth, 0,
				0, 0).minusSeconds(1);
		LocalDateTime endTime = LocalDateTime.of(year, month, dayOfMonth, 23,
				59, 59).plusSeconds(1);

		ArrayList<TaskData> task = new ArrayList<TaskData>();

		for (TaskData _task : taskList) {

			/*
			 * option 1: highlight start day and end day
			 */
			LocalDateTime st = _task.getStartDateTime();
			LocalDateTime et = _task.getEndDateTime();
			if (st != null && st.isAfter(startTime) && st.isBefore(endTime)) {
				task.add(_task);
				// System.out.print("1. " + task.toString());
			} else if (et != null && et.isAfter(startTime)
					&& et.isBefore(endTime)) {
				task.add(_task);
				// System.out.print("2. " + task.toString());
			}

			/*
			 * another option to choose if (st != null && et != null) { if
			 * ((st.isAfter(startTime) && st.isBefore(endTime)) ||
			 * (startTime.isAfter(st) && startTime.isBefore(et)))
			 * task.add(_task); } else if (st != null) { if
			 * (st.isAfter(startTime) && st.isBefore(endTime)) {
			 * task.add(_task); } } else { if (et.isAfter(startTime) &&
			 * et.isBefore(endTime)) { task.add(_task); } }
			 */
		}

		// System.out.println(date + " " + startTime + " " + endTime + " "
		// +!task.isEmpty());
		// System.out.println(displaySearch(task));
		return !task.isEmpty();
		// return false;

	}

	// Translate a Task to TaskData for storage
	// @author A0112066U

	private TaskData toTaskData(Task task) {
		String content = task.getContent();
		LocalDateTime startTime = task.getStartDateTime();
		LocalDateTime endTime = task.getEndDateTime();
		String category = task.getCategory();
		String priority = task.getPriority();
		// boolean isDone = task.isDone(); // removed

		return new TaskData(content, category, priority, startTime, endTime);

		// return new TaskData(content, category, priority, startTime, endTime,
		// isDone); // removed isDone

	}

	// return overdue task
	// @author A0112066U
	public String getOverdue() {
		ArrayList<TaskData> overdue = new ArrayList<TaskData>();
		LocalDateTime now = LocalDateTime.now();
		for (TaskData _task : taskList) {
			LocalDateTime endTime = _task.getEndDateTime();
			if (endTime != null && endTime.isBefore(now)) {
				overdue.add(_task);
			}
		}
		if (overdue.isEmpty())
			return "Great, You have completed every task!";
		return showToUser(overdue);
	}

	// @author A0112066U
	public String getData(String s) throws IOException, ParseException {
		String _command = s;
		if (s != null && !s.isEmpty()) {
			_command = parser.getAction(s).getCommand();
		}
		if (_command.toLowerCase().startsWith("search")) {
			return searchRes;
		}
		return dataToShow();
	}

	// This function check whether a string provided is an integer
	private static boolean isInteger(String index) {
		try {
			Integer.parseInt(index);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	// @author A0112066U
	public static class DateInfo {
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

		LocalDateTime getStartDateTime() {
			return start;
		}

		LocalDateTime getEndDateTime() {
			return end;
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

	private static class ActionEntry {
		private Action action;
		private Task task;

		ActionEntry(Action action, Task task) {
			this.action = action;
			this.task = task;
		}

		private Action getAction() {
			return action;
		}

		private Task getTask() {
			return task;
		}
	}
}