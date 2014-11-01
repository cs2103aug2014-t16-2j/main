package logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import org.json.simple.parser.ParseException;

import parser.*;
import storage.*;

public class Logic {
	private Command command;
	private Task task;
	private HashMap<String, HashMap<DateInfo, TaskData>> taskIdentifier;
	private HashMap<String, TaskData> completedTaskIdentifier;
	private Stack<ActionEntry> actionList; // for undo and redo
	private Stack<ActionEntry> redoList;
	private Storage storer;
	private Parser parser;
	private Action action;
	private ActionEntry entry;
	private SearchTool searchTool;
	private ArrayList<TaskData> F2DisplayedList;
	private ArrayList<TaskData> taskList;
	private ArrayList<TaskData> completedTask;
	private ArrayList<TaskData> blockedList;
	String blockedPath = "blocked.json";
	String filePath = "text.json";
	String completedpath = "completed.json";
	boolean isSuspendedAction = false;
	Action suspendingAction;
	ArrayList<TaskData> F3DisplayedList;

	int currentDisplayList;
	static Scanner sc = new Scanner(System.in);

	// ------------------Constructor-------------------------//
	public Logic() throws FileNotFoundException, IOException, ParseException {
		storer = FileStorage.getInstance();
		// Sorry Duy! I applied singleton pattern for now so I can modify
		// storage without the need to modify
		// logic all the way. Hope you understand.
		// Because i feel not good modifying your part although I just wanna
		// make your work easier.
		// storerForCompleted = new FileStorage(); // For
		// compeleted
		// task
		storer.setupDatabase(filePath); // act upon changes made in storage
		storer.setupDatabase(completedpath); // act upon changes
		storer.setupDatabase(blockedPath); // made in storage
		command = null;
		task = null;
		taskList = new ArrayList<TaskData>();
		taskIdentifier = new HashMap<String, HashMap<DateInfo, TaskData>>();
		completedTaskIdentifier = new HashMap<String, TaskData>();
		actionList = new Stack<ActionEntry>();
		redoList = new Stack<ActionEntry>();
		F2DisplayedList = new ArrayList<TaskData>();
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
	// @author A0112066U
	private void loadData() throws IOException, ParseException {
		taskList = new ArrayList<TaskData>(storer.loadTasks(filePath));
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
		completedTask = new ArrayList<TaskData>(storer.loadTasks(completedpath));
		blockedList = storer.loadTasks(blockedPath);
	}

	// this method is to execute a command
	// @author A0112066U
	public String executeInputCommand(String _command) throws IOException,
			ParseException {
		if (_command == null || _command.isEmpty()) {
			_command = " ";
		}
		extractCommandandTask(_command);
		boolean isSuccessful;
		isSuccessful = executeCommand(command, task);
		if (isSuccessful)
			return "Successful";
		else if (isSuspendedAction)
			return "Please provide start and end time";
		else
			return "Error. ";
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
	private boolean executeCommand(Command command, Task task)
			throws IOException, ParseException {
		boolean isSuccessful;
		switch (command) {
		case ADD:
			isSuccessful = addTask(toTaskData(task));
			if (isSuccessful)
				actionList.push(new ActionEntry(action, null));
			return isSuccessful;
		case DELETE:
			isSuccessful = deleteTask(toTaskData(task), false);
			if (isSuccessful)
				actionList.push(new ActionEntry(action, null));
			return isSuccessful;
		case MODIFY:
			isSuccessful = modifyTask(task, null, false);
			if (isSuccessful)
				actionList.push(entry);
			return isSuccessful;
		case UNDO:
			isSuccessful = undo();
			return isSuccessful;
		case REDO:
			isSuccessful = redo();
			return isSuccessful;
		case SEARCH:
			isSuccessful = search(task);
			return isSuccessful;
		case MARK:
			isSuccessful = markAsDone(task, false);
			if (isSuccessful)
				actionList.push(new ActionEntry(action, null));
			return isSuccessful;
		case BLOCK:
			isSuccessful = block(toTaskData(task));
			if (isSuccessful)
				actionList.push(new ActionEntry(action, null));
			return isSuccessful;
		case UNBLOCK:
			isSuccessful = unblock(toTaskData(task));
			if (isSuccessful)
				actionList.push(new ActionEntry(action, null));
			return isSuccessful;
		case EXIT:
			return exit();
		default:
			return false;
		}
	}

	// add a task
	// @author A0112066U
	private boolean addTask(TaskData task) {
		String content = task.getContent();
		if (content == null || content.isEmpty()) {
			System.out.println("Empty input");
			return false;
		}
		if (taskIdentifier.containsKey(content)) {
			HashMap<DateInfo, TaskData> map = taskIdentifier.get(content);
			TaskData t = searchTool.findExactTask(task, map);
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
		F2DisplayedList.add(0, task);
		try {
			storer.saveTasks(filePath, taskList);
		} catch (Exception e) {
			System.out.println("Error while saving data");
			return false;
		}
		return true;
	}

	// delete a task described by content
	// @author A0112066U
	private boolean deleteTask(TaskData task, boolean unredo) {
		String content = task.getContent();
		if (isInteger(content))
			deleteIndex(Integer.parseInt(content), unredo);
		if (content == null || content.isEmpty()) {
			return false;
		}
		if (taskIdentifier.containsKey(content)) {
			HashMap<DateInfo, TaskData> toDeleteList = taskIdentifier
					.get(content);
			TaskData toDelete = null;
			LocalDateTime st = task.getStartDateTime();
			LocalDateTime et = task.getEndDateTime();
			if (unredo || st != null || et != null) {
				DateInfo d = new DateInfo(st, et);
				toDelete = toDeleteList.get(d);
				if (isSuspendedAction) {
					suspendingAction = null;
					isSuspendedAction = false;
				}
			} else {
				if (isSuspendedAction) {
					DateInfo d = new DateInfo(st, et);
					toDelete = toDeleteList.get(d);
					suspendingAction = null;
					isSuspendedAction = false;
				} else {
					toDelete = searchTool.findTaskByContentandDate(task,
							toDeleteList);
					if (toDelete.getContent().equals("xxxxxxxxxxxxxxxxxxxx")) {
						if (isSuspendedAction) {
							suspendingAction = null;
							isSuspendedAction = false;
						} else {
							suspendingAction = action;
							isSuspendedAction = true;
						}
						return false;
					}
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
				if (F2DisplayedList.contains(toDelete)) {
					F2DisplayedList.remove(toDelete);
				}
				Task t = new Task();
				t.setContent(toDelete.getContent());
				t.setCategory(toDelete.getCategory());
				t.setStartDateTime(toDelete.getStartDateTime());
				t.setEndDateTime(toDelete.getEndDateTime());
				t.setPriority(toDelete.getPriority());
				action = new Action(Command.DELETE, t);
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
	private void deleteIndex(int index, boolean unredo) {
		ArrayList<TaskData> displayedList = getDisplayedList();
		int size = displayedList.size();
		if (index < 1 || index > size) {
			System.out.print("Error-----------");
			return;
		} else {
			TaskData task = displayedList.get(index - 1);
			deleteTask(task, unredo);
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
		Command command = done.getCommand();
		Task task = done.getTask();
		TaskData _task = toTaskData(task);
		switch (command) {
		case ADD:
			return addTask(_task);
		case DELETE:
			return deleteTask(_task, true);
		case MODIFY:
			return modifyTask(task, t, true);
		case MARK:
			return markAsDone(task, true);
		case BLOCK:
			return block(_task);
		case UNBLOCK:
			return unblock(_task);
		default:
			return false;
		}
	}

	// @author A0112066U
	private boolean undo() {
		boolean isSuccessful = false;
		if (actionList.isEmpty()) {
			return isSuccessful;
		}
		ActionEntry x = actionList.pop();
		Action done = x.getAction();
		Command command = done.getCommand();
		switch (command) {
		case ADD:
			isSuccessful = undoAdd(done);
			break;
		case DELETE:
			isSuccessful = undoDelete(done);
			break;
		case MODIFY:
			isSuccessful = undoModify(done, x.getTask());
			break;
		case MARK:
			isSuccessful = undoMark(done);
			break;
		case BLOCK:
			isSuccessful = undoBlock(done);
			break;
		case UNBLOCK:
			isSuccessful = undoUnblock(done);
			break;
		default:
			isSuccessful = false;
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
		boolean isSuccessful = deleteTask(toTaskData(done.getTask()), true);
		if (isSuccessful)
			redoList.push(new ActionEntry(done, null));
		else
			actionList.push(new ActionEntry(done, null));
		return isSuccessful;
	}

	// @author A0112066U
	private boolean undoModify(Action done, Task t) {
		boolean isSuccessful = modifyTask(done.getTask(), t, true);
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

	private boolean undoBlock(Action done) {
		Task task = done.getTask();
		boolean isSuccessful = false;
		isSuccessful = unblock(toTaskData(task));
		if (isSuccessful) {
			redoList.push(new ActionEntry(done, null));
		} else {
			actionList.push(new ActionEntry(done, null));
		}
		return isSuccessful;
	}

	private boolean undoUnblock(Action done) {
		Task task = done.getTask();
		boolean isSuccessful = false;
		isSuccessful = block(toTaskData(task));
		if (isSuccessful) {
			redoList.push(new ActionEntry(done, null));
		} else {
			actionList.push(new ActionEntry(done, null));
		}
		return isSuccessful;
	}

	// modify a task
	// @author A0112066U
	private boolean modifyTask(Task task, Task t, boolean unredo) {
		String content = task.getContent();
		if (content == null || content.isEmpty()) {
			System.out.print("No task to modify.");
			return false;
		}
		if (!isInteger(content)) {
			return modifyTask(task, t, content, unredo);
		} else {
			return modifyIndex(task, Integer.parseInt(content), unredo);
		}
	}

	// @author A0112066U
	private boolean modifyTask(Task _task, Task t, String content,
			boolean unredo) {
		LocalDateTime newStartTime = _task.getStartDateTime();
		LocalDateTime newEndTime = _task.getEndDateTime();
		String newCategory = _task.getCategory();
		String newPriority = _task.getPriority();
		HashMap<DateInfo, TaskData> _listTaskToEdit = new HashMap<DateInfo, TaskData>();
		if (taskIdentifier.containsKey(content))
			_listTaskToEdit = taskIdentifier.get(content);
		LocalDateTime st = _task.getStartDateTime();
		LocalDateTime et = _task.getEndDateTime();
		TaskData toFind = new TaskData(content, null, null, null, null);
		TaskData taskToModify = null;

		if (unredo) {
			st = t.getStartDateTime();
			et = t.getEndDateTime();
			DateInfo d = new DateInfo(st, et);
			taskToModify = _listTaskToEdit.get(d);

		} else {
			if (isSuspendedAction) {
				DateInfo d = new DateInfo(st, et);
				taskToModify = _listTaskToEdit.get(d);
				Task itask = suspendingAction.getTask();
				newStartTime = itask.getStartDateTime();
				newEndTime = itask.getEndDateTime();
				newCategory = itask.getCategory();
				newPriority = itask.getPriority();
				suspendingAction = null;
			}
			if (t != null) {
				st = t.getStartDateTime();
				et = t.getEndDateTime();
				toFind.setStartDateTime(st);
				toFind.setEndDateTime(et);
			}
			if (!isSuspendedAction) {
				taskToModify = searchTool.findTaskByContentandDate(toFind,
						_listTaskToEdit);
				if (taskToModify != null
						&& taskToModify.getContent().equals(
								"xxxxxxxxxxxxxxxxxxxx")) {
					suspendingAction = new Action(command, _task);
					isSuspendedAction = true;
					return false;
				}
			}
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
			toFind.setContent(content);
			if (newCategory != null)
				toFind.setCategory(newCategory);
			else
				toFind.setCategory(oldCategory);
			if (newPriority != null)
				toFind.setPriority(newPriority);
			else
				toFind.setPriority(oldPriority);
			if (newStartTime != null)
				toFind.setStartDateTime(newStartTime);
			else
				toFind.setStartDateTime(oldEndTime);
			if (newEndTime != null)
				toFind.setEndDateTime(newEndTime);
			else
				toFind.setEndDateTime(oldEndTime);
			toFind = searchTool.findExactTask(toFind, _listTaskToEdit);
			if (toFind != null && toFind != taskToModify) {
				System.out.print("A same task already exists");
				return false;
			}
			if (newStartTime != null || unredo) {
				taskToModify.setStartDateTime(newStartTime);
				t.setStartDateTime(newStartTime);
				_task.setStartDateTime(oldStartTime);

			} else {
				t.setStartDateTime(oldStartTime);
				_task.setStartDateTime(oldStartTime);
			}
			if (newEndTime != null || unredo) {
				taskToModify.setEndDateTime(newEndTime);
				t.setEndDateTime(newEndTime);
				_task.setEndDateTime(oldEndTime);
			} else {
				t.setEndDateTime(oldEndTime);
				_task.setEndDateTime(oldEndTime);
			}
			if (newCategory != null || unredo) {
				taskToModify.setCategory(newCategory);
				t.setCategory(newCategory);
				_task.setCategory(oldCategory);
			} else {
				t.setCategory(oldCategory);
				_task.setCategory(oldCategory);
			}
			if (newPriority != null || unredo) {
				taskToModify.setPriority(newPriority);
				t.setPriority(newPriority);
				_task.setPriority(oldPriority);
			} else {
				t.setPriority(oldPriority);
				_task.setPriority(oldPriority);
			}
			_listTaskToEdit.remove(new DateInfo(oldStartTime, oldEndTime));

			newStartTime = taskToModify.getStartDateTime();
			newEndTime = taskToModify.getEndDateTime();

			_listTaskToEdit.put(new DateInfo(newStartTime, newEndTime),
					taskToModify);
			action = new Action(Command.MODIFY, _task);
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
	private boolean modifyIndex(Task task, int index, boolean unredo) {
		ArrayList<TaskData> displayedList = getDisplayedList();
		int size = displayedList.size();
		if (index < 1 || index > size) {
			System.out.print("Error-----------");
			return false;
		} else {
			TaskData _task = displayedList.get(index);
			String content = _task.getContent();
			return modifyTask(task, null, content, unredo);
		}
	}

	// search for a task by key words or time
	// @author A0112066U
	ArrayList<TaskData> searchRes;

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
	private boolean markAsDone(Task _task, boolean unredo) {
		boolean isSuccessful = false;
		String content = _task.getContent();
		if (isInteger(content)) {

		}
		HashMap<DateInfo, TaskData> _taskToEdit = taskIdentifier.get(content);
		TaskData task = null;
		LocalDateTime st = _task.getStartDateTime();
		LocalDateTime et = _task.getEndDateTime();
		if (unredo || st != null || et != null) {
			DateInfo d = new DateInfo(st, et);
			task = _taskToEdit.get(d);
			if (isSuspendedAction) {
				suspendingAction = null;
				isSuspendedAction = false;
			}
		} else {
			if (isSuspendedAction) {
				DateInfo d = new DateInfo(st, et);
				task = _taskToEdit.get(d);
				suspendingAction = null;
				isSuspendedAction = false;
			} else {
				task = searchTool.findTaskByContentandDate(toTaskData(_task),
						_taskToEdit);
				if (task.getContent().equals("xxxxxxxxxxxxxxxxxxxx")) {
					if (isSuspendedAction) {
						suspendingAction = null;
						isSuspendedAction = false;
					} else {
						suspendingAction = action;
						isSuspendedAction = true;
					}
					return false;
				}
			}
		}
		if (task != null) {
			if (_taskToEdit.size() == 1)
				taskIdentifier.remove(content);
			else {
				DateInfo d = new DateInfo(task.getStartDateTime(),
						task.getEndDateTime());
				_taskToEdit.remove(d);
			}
			taskList.remove(task);
			if (F2DisplayedList.contains(task)) {
				F2DisplayedList.remove(task);
			}
			String s = task.getStartDateTime() + "" + task.getEndDateTime();
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
	private boolean exit() {
		// store when exit
		try {
			saveData();
			saveCompletedTask();
		} catch (IOException e) {
			System.out.println("Error while saving data");
		}
		return true;
		// System.exit(0);
	}

	private boolean block(TaskData task) {
		LocalDateTime start = task.getStartDateTime();
		LocalDateTime end = task.getEndDateTime();
		if (start == null || end == null) {
			return false;
		}
		blockedList.add(task);
		ArrayList<TaskData> copy = new ArrayList<TaskData>(blockedList);
		for (TaskData _task : copy) {
			if (_task == task)
				continue;
			LocalDateTime _start = _task.getStartDateTime();
			LocalDateTime _end = _task.getEndDateTime();
			if (isClash(start, end, _start, _end)
					|| isSequential(start, end, _start, _end)) {
				_start = chooseStart(start, _start);
				_end = chooseEnd(end, _end);
				task.setStartDateTime(_start);
				task.setEndDateTime(_end);
				blockedList.remove(_task);
			}
		}
		storer.saveTasks(blockedPath, blockedList);
		return true;
	}

	private boolean unblock(TaskData task) {
		LocalDateTime start = task.getStartDateTime();
		LocalDateTime end = task.getEndDateTime();
		if (start == null || end == null) {
			return false;
		}
		ArrayList<TaskData> copy = new ArrayList<TaskData>(blockedList);
		for (TaskData _task : copy) {
			LocalDateTime _start = _task.getStartDateTime();
			LocalDateTime _end = _task.getEndDateTime();
			if (isClash(start, end, _start, _end)) {
				if (start.isBefore(_start) && end.isAfter(_end)) {
					blockedList.remove(_task);
					continue;
				}
				if (_start.isBefore(start) && _end.isAfter(end)) {
					_task.setEndDateTime(start);
					blockedList.remove(_task);
					_task.setEndDateTime(start);
					TaskData t = new TaskData();
					t.setStartDateTime(end);
					t.setEndDateTime(_end);
					blockedList.add(0, t);
					blockedList.add(0, _task);
					continue;
				}
				if (start.isBefore(_start) && end.isBefore(_end)) {
					blockedList.remove(_task);
					_task.setStartDateTime(end);
					blockedList.add(_task);
					continue;
				}
				if (_start.isBefore(start) && _end.isBefore(end)) {
					blockedList.remove(_task);
					_task.setEndDateTime(start);
					blockedList.add(_task);
					continue;
				}
			}
		}
		storer.saveTasks(blockedPath, blockedList);
		return true;
	}

	private LocalDateTime chooseStart(LocalDateTime start1, LocalDateTime start2) {
		return (start1.isBefore(start2)) ? start1 : start2;
	}

	private LocalDateTime chooseEnd(LocalDateTime end1, LocalDateTime end2) {
		return (end1.isAfter(end2)) ? end1 : end2;
	}

	private boolean isClash(LocalDateTime start1, LocalDateTime end1,
			LocalDateTime start2, LocalDateTime end2) {
		if (start1.isEqual(start2) || end1.isEqual(end2))
			return true;
		if (start1.isAfter(start2) && start1.isBefore(end2)
				|| start2.isAfter(start1) && start2.isBefore(end1))
			return true;
		if (start1.isAfter(start2) && end1.isBefore(end2)
				|| start2.isAfter(start1) && end2.isBefore(end1))
			return true;
		return false;
	}

	private boolean isSequential(LocalDateTime start1, LocalDateTime end1,
			LocalDateTime start2, LocalDateTime end2) {
		return start1.isEqual(end2) || start2.isEqual(end1);
	}

	// @author A0112066U
	private void saveData() throws IOException {
		storer.saveTasks(filePath, taskList);
	}

	private void saveCompletedTask() throws IOException {
		storer.saveTasks(completedpath, completedTask);
	}

	// return data to show to UI
	// @author A0112066U
	public ArrayList<TaskData> getTaskToCome() {
		LocalDateTime now = LocalDateTime.now();
		int dateToday = now.getDayOfMonth();
		int monthToday = now.getMonthValue();
		int yearToday = now.getYear();
		LocalDateTime today = LocalDateTime.of(yearToday, monthToday,
				dateToday, 0, 0, 0).minusSeconds(1);
		LocalDateTime tomorrow = today.plusSeconds(172801);
		ArrayList<TaskData> taskToCome = new ArrayList<TaskData>();
		for (TaskData _task : taskList) {
			LocalDateTime st = _task.getStartDateTime();
			LocalDateTime et = _task.getEndDateTime();
			if (st != null && st.isAfter(today) && st.isBefore(tomorrow)) {
				taskToCome.add(_task);
			} else if (et != null && et.isAfter(today) && et.isBefore(tomorrow)) {
				taskToCome.add(_task);
			}
		}
		return taskToCome;
	}

	// @author A0112066U
	protected String dataToShow() throws IOException, ParseException {
		return showToUser(F2DisplayedList);
	}

	// @author A0112066U
	private String showToUser(ArrayList<TaskData> taskToShow) {
		String text = "";
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		SimpleDateFormat f = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
		int i = 1;
		for (TaskData t : taskToShow) {
			text += i + ": ";
			text += t.getContent();
			if (t.getPriority() != null)
				text += "\n " + t.getPriority() + " priority ";
			if (t.getCategory() != null)
				text += "\n #" + t.getCategory() + " ";
			if (t.getStartDateTime() != null) {
				Date d;
				try {
					d = formater.parse(t.getStartDateTime() + "");
					String s = f.format(d);
					text += "\n From: " + s;
				} catch (java.text.ParseException e) {
				}
			}
			if (t.getEndDateTime() != null) {
				Date d;
				try {
					d = formater.parse(t.getEndDateTime() + "");
					String s = f.format(d);
					text += "\n To : " + s;
				} catch (java.text.ParseException e) {
				}
			}
			text += "\n";
			i++;
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
	public ArrayList<TaskData> getOverdue() {
		ArrayList<TaskData> overdue = new ArrayList<TaskData>();
		LocalDateTime now = LocalDateTime.now();
		for (TaskData _task : taskList) {
			LocalDateTime endTime = _task.getEndDateTime();
			if (endTime != null && endTime.isBefore(now)) {
				overdue.add(_task);
			}
		}
		return overdue;
	}

	// return category
	// @author A0112066U
	public String getCategory() {
		ArrayList<String> category = new ArrayList<String>();
		for (TaskData _task : taskList) {
			String cat = _task.getCategory();
			if (!category.contains(cat))
				category.add(cat);
		}
		String res = "";
		for (String cat : category) {
			res += cat + "\n";
		}
		return res;
	}

	// @author A0112066U
	public String getData(String s) throws IOException, ParseException {
		currentDisplayList = 2;
		return dataToShow();
	}

	public void clear() throws IOException {
		taskList.clear();
		saveData();
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
	private int overdueRow = 0;
	public ArrayList<DisplayedEntry> getRequiredTask(String userCommand) {
		Command cmd = null;
		if (userCommand != null && !userCommand.isEmpty()) {
			cmd = parser.getAction(userCommand).getCommand();
		}

		if (cmd != null && cmd.equals(Command.SEARCH)) {
			F3DisplayedList = new ArrayList<TaskData>(searchRes);
			overdueRow = 0;

		}
		if (cmd == null || cmd.equals("") || cmd.equals(Command.ADD)
				|| cmd.equals(Command.DELETE) || cmd.equals(Command.MODIFY)
				|| cmd.equals(Command.MARK) || cmd.equals(Command.UNDO)
				|| cmd.equals(Command.REDO)) {
			F3DisplayedList = new ArrayList<TaskData>();
			ArrayList<TaskData> overdue = getOverdue();
			overdueRow = overdue.size();
			F3DisplayedList.addAll(overdue);
			F3DisplayedList.addAll(getTaskToCome());

		}
		currentDisplayList = 3;
		ArrayList<DisplayedEntry> tobeShown = new ArrayList<DisplayedEntry>();
		for (TaskData t : F3DisplayedList) {
			tobeShown.add(toDisplayedEntry(t));
		}

		return tobeShown;
	}

	public int getOverdueRow() {
		return overdueRow;
	}

	private ArrayList<TaskData> getDisplayedList() {
		if (currentDisplayList == 2) {
			return F2DisplayedList;
		} else if (currentDisplayList == 3) {
			return F3DisplayedList;
		}
		return null;
	}

	private DisplayedEntry toDisplayedEntry(TaskData task) {
		return new DisplayedEntry(task);
	}
}