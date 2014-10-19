package Logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import org.json.simple.parser.ParseException;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import Storage.*;
import Parser.*;

public class Logic {

	private String command;
	private Task task;
	private ArrayList<TaskData> taskList;
	private ArrayList<TaskData> taskToBeAdded;
	private HashMap<String, HashMap<DateInfo, TaskData>> taskIdentifier;
	private Stack<Action> actionList; // for undo and redo
	private Stack<Action> redoList;
	private TaskFileStorage storer;
	private Parser parser;
	private Action action;

	private ArrayList<TaskData> currentDisplayedTask;
	private ArrayList<TaskData> completedTask;
	private TaskFileStorage storerForCompleted;
	static Scanner sc = new Scanner(System.in);

	// ----------Constructor----------//

	public Logic() throws FileNotFoundException, IOException, ParseException {		
		storer = new TaskFileStorage("text.json");
		storerForCompleted = new TaskFileStorage("completed.json"); //For compeleted task
		command = null;
		task = null;
		taskList = new ArrayList<TaskData>();
		taskToBeAdded = new ArrayList<TaskData>();
		taskIdentifier = new HashMap<String, HashMap<DateInfo, TaskData>>();
		actionList = new Stack<Action>();
		redoList = new Stack<Action>();
		 
		currentDisplayedTask = new ArrayList<TaskData>();
		parser = new Parser();
		completedTask = new ArrayList<TaskData>();
		loadData();
	}

	// -------------Main-------------//
	public static void main(String[] args) throws ParseException {
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
		action = parser.getAction(_command);
		command = action.getCommand();
		task = action.getTask();

		// push command into undo stack, except undo and redo
		// currently cannot handle edit

	}

	// load all data saved in the file
	private void loadData() throws IOException, ParseException {
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
		
		completedTask = new ArrayList<TaskData>(storerForCompleted.loadData(new Option(true)));
		

		
	}

	// this method is to execute a command
	public void execute(String _command) throws IOException, ParseException {
		extractCommandandTask(_command);
		executeCommand(command, task);
	}

	private void executeCommand(String command, Task task) throws IOException,
			ParseException {
		switch (command) {
		case "add":
			addTask(toTaskData(task));
			actionList.push(action);
			break;
		case "delete":
			deleteTask(toTaskData(task));
			actionList.push(action);
			break;
		case "modify":
			modifyTask(task);
			actionList.push(action);
			break;
		case "undo":
			undo();
			break;
		case "redo":
			redo();
			break;
		case "search":
			search(toTaskData(task));
			break;
		case "exit":
			exit();
		default:
			return;
		}
	}

	// add a task
	private void addTask(TaskData task) {

		String content = task.getContent();
		if (taskIdentifier.containsKey(content)) {
			HashMap<DateInfo, TaskData> map = taskIdentifier.get(content);
			map.put(new DateInfo(task.getStartDateTime(), task.getEndDateTime()),
					task);
		} else {
			taskIdentifier.put(content, new HashMap<DateInfo, TaskData>());
			HashMap<DateInfo, TaskData> map = taskIdentifier.get(content);
			map.put(new DateInfo(task.getStartDateTime(), task.getEndDateTime()),
					task);
		}
		taskList.add(task);
		taskToBeAdded.add(task);
		currentDisplayedTask.add(0, task);
		storer.saveData(taskToBeAdded, true);
		taskToBeAdded.clear();
	}

	// delete a task described by content -> doesn't handle task with same
	// content
	private void deleteTask(TaskData task) {

		String content = task.getContent();
		if (isInteger(content)) deleteIndex(Integer.parseInt(content));
		if (taskIdentifier.containsKey(content)) {
			HashMap<DateInfo, TaskData> toDeleteList = taskIdentifier.get(content);
			TaskData toDelete = null;

			if (toDeleteList.size() == 1) {
				for (TaskData _task : toDeleteList.values()) {
					toDelete = _task;
				}
				taskIdentifier.remove(toDelete.getContent());
			} else if (toDeleteList.size() > 1) {
				LocalDateTime st = task.getStartDateTime();
				LocalDateTime et = task.getEndDateTime();
				if (st == null && et == null) {
					System.out.print("Provide start and end time\n"); // ask for
																		// date
																		// and
																		// time
																		// to
																		// specify

					String s = sc.nextLine();
					Task _task = parser.getAction(s).getTask();
					st = _task.getStartDateTime();
					et = _task.getEndDateTime();
				}
				DateInfo d = new DateInfo(st, et);
				if (toDeleteList.containsKey(d)) {
					toDelete = toDeleteList.get(d);
					toDeleteList.remove(d, toDelete);
				} else {
					System.out.print("Error-----------");
					return;
				}

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

		}

		saveData();
	}

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
	private void redo() throws IOException, ParseException {
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
		String command = done.getCommand();
		switch (command) {
		case "add":
			undoAdd(done);
			redoList.push(done);
			break;
		case "delete":
			undoDelete(done);
			redoList.push(done);
			break;
		case "modify":
			undoModify(done);
			redoList.push(done);
			break;
		}

	}

	private void undoDelete(Action done) {
		addTask(toTaskData(done.getTask()));
	}

	private void undoAdd(Action done) {
		deleteTask(toTaskData(done.getTask()));
	}

	private void undoModify(Action done) {
		modifyTask(done.getTask());

	}

	// modify a task

	private void modifyTask(Task task) {
		String content = task.getContent();

		if (!isInteger(content)) {
			modifyTask(task, content);
		} else {
			modifyIndex(task, Integer.parseInt(content));
		}
	}

	private void modifyTask(Task task, String content) {
		LocalDateTime newStartTime = task.getStartDateTime();
		LocalDateTime newEndTime = task.getEndDateTime();
		String newCategory = task.getCategory();
		String newPriority = task.getPriority();
		// boolean isDone_new = task.isDone(); // removed

		HashMap<DateInfo, TaskData> _listTaskToEdit = taskIdentifier
				.get(content);
		if (_listTaskToEdit.size() <= 0) {
			System.out.print("Error---------");
			return;
		}
		TaskData taskToModify = null;
		for (TaskData t : _listTaskToEdit.values()) {
			taskToModify = t;
			break;
		}

		LocalDateTime oldStartTime = taskToModify.getStartDateTime();
		LocalDateTime oldEndTime = taskToModify.getEndDateTime();
		String oldCategory = taskToModify.getCategory();
		String oldPriority = taskToModify.getPriority();
		// boolean isDone_old = savedTask.isDone(); // removed

		if (newStartTime != null) {
			taskToModify.setStartDateTime(newStartTime);
			task.setStartDateTime(oldStartTime);
		}
		if (newEndTime != null) {
			taskToModify.setEndDateTime(newEndTime);
			task.setEndDateTime(oldEndTime);
		}
		if (newCategory != null) {
			taskToModify.setCategory(newCategory);
			task.setCategory(oldCategory);
		}
		if (newPriority != null) {
			taskToModify.setPriority(newPriority);
			task.setCategory(oldPriority);
		}
		// savedTask.setDone(isDone_new); // removed
		// task.setDone(isDone_old);

		action = new Action("modify", task);

		saveData();
	}

	private void modifyIndex(Task task, int index) {
		int size = currentDisplayedTask.size();
		if (index < 1 || index > size) {
			System.out.print("Error-----------");
			return;
		} else {
			TaskData _task = currentDisplayedTask.get(index);
			String content = _task.getContent();
			modifyTask(task, content);
		}

	}

	// search for a task by key words or time

	protected String searchRes;

	private void search(TaskData task) throws IOException, ParseException {

		ArrayList<TaskData> searchResult = new ArrayList<TaskData>();

		String content = task.getContent();
		String[] words = content.split(" ");
		LocalDateTime startTime = task.getStartDateTime();
		LocalDateTime endTime = task.getEndDateTime();
		String category = task.getCategory();
		String priority = task.getPriority();

		ArrayList<TaskData> toSearch = new ArrayList<TaskData>();
		if (startTime != null && endTime != null) {
			startTime = startTime.minusSeconds(1);
			endTime = endTime.plusSeconds(1);
			for (TaskData _task : taskList) {

				/*
				 * option 1: highlight start day and end day
				 */
				LocalDateTime st = _task.getStartDateTime();
				LocalDateTime et = _task.getEndDateTime();
				if (st != null && st.isAfter(startTime) && st.isBefore(endTime)) {
					toSearch.add(_task);
					
				} else if (et != null && et.isAfter(startTime)
						&& et.isBefore(endTime)) {
					toSearch.add(_task);
					
				}

				
			}
		} else if (startTime != null ){
			startTime = startTime.minusSeconds(1);
			for (TaskData _task : taskList) {

				LocalDateTime st = _task.getStartDateTime();
				if (st != null && st.isAfter(startTime)) {
					toSearch.add(_task);
				}
			}
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
	protected String getTodayTask() throws IOException, ParseException {
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
			} else if (et != null && et.isAfter(today)
					&& et.isBefore(tomorrow)) {
				taskToShow.add(_task);
			}
		}


		return showToUser(taskToShow);
	}
	
	protected String dataToShow() throws IOException, ParseException {
		return showToUser(currentDisplayedTask);
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
	protected boolean hasTask(String date) throws IOException, ParseException {

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
			 * another option to choose 
			 * if (st != null && et != null) { if
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
	protected String getOverdue() {
		ArrayList<TaskData> overdue = new ArrayList<TaskData>();
		LocalDateTime now = LocalDateTime.now();
		for (TaskData _task : taskList) {
			LocalDateTime endTime = _task.getEndDateTime();
			if (endTime != null && endTime.isAfter(now)) {
				overdue.add(_task);
			}
		}
		return showToUser(overdue);
	}

	protected ArrayList<TaskData> showCategory(String _category) {
		ArrayList<TaskData> taskInCategory = new ArrayList<TaskData>();
		String[] categories = _category.split(" ");
		assert categories.length > 0;
		for (String cat : categories) {
			try {
				taskInCategory.addAll(storer.loadData(new Option(cat)));
			} catch (IOException | ParseException e) {
				continue;
			}
		}

		return null;

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