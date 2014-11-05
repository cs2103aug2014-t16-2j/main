package logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

import org.json.simple.parser.ParseException;

import commons.TaskData;
import parser.*;
import reminder.ReminderPatternParser;
import storage.*;

public class Logic {
	private static final String MSG_NOT_ALLOWED_MARK = "You cannot mark a completed task as done";
	private static final String MSG_NOT_ALLOWED_MODIFY = "You cannot modify a completed task";
	private static final String MSG_ERROR = "Error. ";
	private static final String MSG_ASK_FOR_TIME = "Please provide start and end time";
	private static final String MSG_SUCCESSFUL = "Successful. ";
	private static final String MSG_TIME_SPECIFIED = "You must specify start time and end time";
	private static final String MSG_CANNOT_UNDO = "Cannot undo anymore";
	private static final String MSG_CANNOT_REDO_ANYMORE = "Cannot redo anymore";
	private static final String MSG_INDEX_OUT_OF_BOUND = "Index out of bound";
	private static final String MSG_NO_TASK_FOUND = "No task found";
	private static final String MSG_NO_TASK_SPCIFIED = "No task spcified";
	private static final String MSG_ERROR_WHILE_SAVING_DATA = "Error while saving data";
	private static final String MSG_EXISTING_TASK = "This task has been existing";
	private static final String MSG_CLASHES = "This task clashes with a blocked slot";
	private static final String MSG_EMPTY_INPUT = "Empty input";
	private Command command;
	private Task task;
	private HashMap<String, HashMap<DateInfo, TaskData>> taskIdentifier;
	private HashMap<String, TaskData> completedTaskIdentifier;
	private Storage storer;
	private Parser parser;
	private Action action;
	private ActionEntry entry;
	private SearchTool searchTool;
	private ArrayList<TaskData> F2DisplayedList; // list of tasks displayed when
													// press F2
	private ArrayList<TaskData> F3DisplayedList; // list of tasks displayed when
													// press F3
	private ArrayList<TaskData> taskList;
	private ArrayList<TaskData> completedTask;
	private ArrayList<TaskData> blockedList;
	private String blockedPath = "Task Folder//blocked.json";
	private String taskFilePath = "Task Folder//text.json";
	private String completedTaskFilePath = "Task Folder//completed.json";
	private boolean isSuspendedAction = false;
	private Action suspendingAction;
	private ReminderPatternParser reminderParser;
	private LocalDateTime reminderDateTime;
	private Integer reminderMinutes;
	private int currentDisplayList;

	private boolean done = false;

	private Stack<ActionEntry> actionList; // for undo and redo
	private Stack<ActionEntry> redoList;
	private Stack<ArrayList<TaskData>> unblockSlot = new Stack<ArrayList<TaskData>>();
	private Stack<ArrayList<TaskData>> blockSlot = new Stack<ArrayList<TaskData>>();

	private ArrayList<TaskData> searchResult;
	private int overdueRow = 0;

	private String messageToUser;

	// ------------------Constructor-------------------------//
	public Logic() throws FileNotFoundException, IOException, ParseException {
		setupDatabase();
		initialiseVariables();
		loadData();
	}

	// ---------------------------------Method-----------------------------//
	/** This method sets up all file needed **/
	// @author A0112066U
	private void setupDatabase() {
		storer = FileStorage.getInstance();
		storer.setupDatabase(taskFilePath); // act upon changes made in storage
		storer.setupDatabase(completedTaskFilePath); // act upon changes
		storer.setupDatabase(blockedPath); // made in storage
	}

	/** This methods is to initialise all variables **/
	// @author A0112066U
	private void initialiseVariables() {
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
		reminderParser = new ReminderPatternParser();
		messageToUser = "";
	}

	/** This method is to load all data saved in the files **/
	// @author A0112066U
	private void loadData() throws IOException, ParseException {
		taskList = new ArrayList<TaskData>(storer.loadTasks(taskFilePath));
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
		completedTask = new ArrayList<TaskData>(
				storer.loadTasks(completedTaskFilePath));
		blockedList = storer.loadTasks(blockedPath);
	}

	/**
	 * this method is called by main to execute a command
	 * 
	 * @return message to user
	 */
	// @author A0112066U
	public String executeInputCommand(String _command) throws IOException,
			ParseException {
		if (_command == null || _command.isEmpty()) {
			_command = " ";
		}
		extractCommandandTask(_command);
		boolean isSuccessful;
		isSuccessful = executeCommand(command, task);
		String message;
		if (isSuccessful) {
			message = MSG_SUCCESSFUL + messageToUser;
		} else if (isSuspendedAction) {
			message = MSG_ASK_FOR_TIME;
		} else {
			message = MSG_ERROR + messageToUser;
		}
		messageToUser = "";
		return message;
	}

	/**
	 * this method is to extract command and task from input command using
	 * parser
	 */
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
		getReminderDateTime(_command, toTaskData(task)); // get reminder
															// date/time
	}

	/** get reminder date and time from reminder parser **/
	private void getReminderDateTime(String command, TaskData t) {
		Object obj = reminderParser.parse(command);
		if (obj == null) {
			reminderDateTime = null;
			reminderMinutes = null;
		} else if (obj instanceof LocalDateTime) {
			reminderDateTime = (LocalDateTime) obj;
		} else if (obj instanceof Integer) {
			reminderMinutes = (Integer) obj;
			if (reminderMinutes == -1) {
				reminderDateTime = LocalDateTime.MIN;
				reminderMinutes = null;
				return;
			}
			if ((t.getStartDateTime() == null) && (t.getEndDateTime() == null)) {
				reminderDateTime = null;
			} else if (t.getStartDateTime() != null) {
				reminderDateTime = t.getStartDateTime().minusMinutes(
						reminderMinutes);
			} else if (t.getEndDateTime() != null) {
				reminderDateTime = t.getEndDateTime().minusMinutes(
						reminderMinutes);
			}
		}
	}

	/**
	 * This method is to execute each specific type of command if successfulled
	 * executed, action is push to undo/redo stack
	 **/
	// @author A0112066U
	private boolean executeCommand(Command command, Task task)
			throws IOException, ParseException {
		boolean isSuccessful;
		switch (command) {
		case ADD:
			isSuccessful = addTask(toTaskData(task), false);
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
			isSuccessful = markAsDone(toTaskData(task), false);
			if (isSuccessful)
				actionList.push(new ActionEntry(action, null));
			return isSuccessful;
		case BLOCK:
			ArrayList<TaskData> block = new ArrayList<TaskData>();
			block.add(toTaskData(task));
			isSuccessful = block(block);
			if (isSuccessful)
				actionList.push(new ActionEntry(action, null));
			return isSuccessful;
		case UNBLOCK:
			ArrayList<TaskData> unblock = new ArrayList<TaskData>();
			unblock.add(toTaskData(task));
			isSuccessful = unblock(unblock);
			if (isSuccessful)
				actionList.push(new ActionEntry(action, null));
			return isSuccessful;
		case EXIT:
			return exit();
		default:
			return false;
		}
	}

	/** This method is for adding a task */
	// @author A0112066U
	private boolean addTask(TaskData task, boolean unredo) {
		String content = task.getContent();
		if (content == null || content.isEmpty()) {
			messageToUser = MSG_EMPTY_INPUT;
			return false;
		}
		if (done && unredo) {
			// in case undo delete a completed task
			completedTask.add(task);
		} else {
			if (task.getStartDateTime() != null
					&& task.getEndDateTime() != null
					&& isClashingWithBlockedSlots(task)) {
				messageToUser = MSG_CLASHES;
				return false;
			}
			if (task.getPriority() == null) {
				// priority is set normal as default if not specified
				task.setPriority("normal");
			}
			if (task.getCategory() == null) {
				// category is set none as default if not specified
				task.setCategory("none");
			}
			if (taskIdentifier.containsKey(content)) {
				HashMap<DateInfo, TaskData> map = taskIdentifier.get(content);

				TaskData t = searchTool.findExactTask(task, map);
				// this is to determine whether the task has been added
				if (t != null) { // if exist a same tasks
									// inform user
					messageToUser = MSG_EXISTING_TASK;
					return false;
				} else { // if not, add it in the map
					map.put(new DateInfo(task.getStartDateTime(), task
							.getEndDateTime()), task);
				}
			} else {
				taskIdentifier.put(content, new HashMap<DateInfo, TaskData>());
				HashMap<DateInfo, TaskData> map = taskIdentifier.get(content);
				map.put(new DateInfo(task.getStartDateTime(), task
						.getEndDateTime()), task);
			}
			/** set reminder **/
			if (reminderDateTime != null) {
				task.setRemindDateTime(reminderDateTime);
				task.setReminder();
			} else {
				System.out.println("Reminder date and time is not set!");
			}
			/** **/
			taskList.add(task);
			F2DisplayedList.add(0, task);
		}
		try {
			saveData();
			saveCompletedTask();
		} catch (Exception e) {
			messageToUser = MSG_ERROR_WHILE_SAVING_DATA;
			return false;
		}
		return true;
	}

	/** This method determines of a task clashes with a blocked time **/
	// @author A0112066

	private boolean isClashingWithBlockedSlots(TaskData task) {
		LocalDateTime start = task.getStartDateTime();
		LocalDateTime end = task.getEndDateTime();
		for (TaskData _task : blockedList) {
			LocalDateTime _start = _task.getStartDateTime();
			LocalDateTime _end = _task.getEndDateTime();
			if (isClash(start, end, _start, _end))
				return true;
		}
		return false;
	}

	/** This method is to delete a task described by content **/
	// @author A0112066U
	private boolean deleteTask(TaskData task, boolean unredo) {
		String content = task.getContent();
		if (isInteger(content)) {
			return deleteIndex(Integer.parseInt(content), unredo);
		}
		if (content == null || content.isEmpty()) {
			messageToUser = MSG_NO_TASK_SPCIFIED;
			return false;
		}
		if (done) { // if delete a completed task
			if (completedTask.contains(task)) {
				completedTask.remove(task);
				Task t = toTask(task, done);
				action = new Action(Command.DELETE, t);
			} else {
				messageToUser = MSG_NO_TASK_FOUND;
				return false;
			}

		} else { // if delete an uncompleted task
			if (taskIdentifier.containsKey(content)) {
				HashMap<DateInfo, TaskData> toDeleteList = taskIdentifier
						.get(content);
				TaskData toDelete = null;
				LocalDateTime st = task.getStartDateTime();
				LocalDateTime et = task.getEndDateTime();

				toDelete = getTaskToDelete(task, unredo, toDeleteList, st, et);
				if (toDelete != null) {
					if (toDeleteList.size() == 1)
						taskIdentifier.remove(content);
					else {
						DateInfo d = new DateInfo(toDelete.getStartDateTime(),
								toDelete.getEndDateTime());
						toDeleteList.remove(d);
					}
					toDelete.clearReminder(); // to kill the background reminder
												// app
					taskList.remove(toDelete);
					if (F2DisplayedList.contains(toDelete)) {
						F2DisplayedList.remove(toDelete);
					}
					Task t = toTask(toDelete, false);
					action = new Action(Command.DELETE, t);
				} else {
					messageToUser = MSG_NO_TASK_FOUND;
					return false;
				}
			} else {
				messageToUser = MSG_NO_TASK_FOUND;
				return false;
			}
		}
		try {
			saveData();
			saveCompletedTask();
		} catch (IOException e) {
			messageToUser = MSG_ERROR_WHILE_SAVING_DATA;
			return false;
		}
		return true;
	}

	/** This method determines the task to be deleted **/
	// @author A0112066
	private TaskData getTaskToDelete(TaskData task, boolean unredo,
			HashMap<DateInfo, TaskData> toDeleteList, LocalDateTime st,
			LocalDateTime et) {
		TaskData toDelete;
		toDelete = getTaskToMark(task, unredo, toDeleteList, st, et);
		return toDelete;
	}

	/**
	 * This method is for deleting the task specified by its index on display
	 * panel
	 **/
	// @author A0112066U
	private boolean deleteIndex(int index, boolean unredo) {
		ArrayList<TaskData> displayedList = getDisplayedList();
		int size = displayedList.size();
		if (index < 1 || index > size) {
			messageToUser = MSG_INDEX_OUT_OF_BOUND;
			return false;
		} else {
			TaskData task = displayedList.get(index - 1);
			return deleteTask(task, unredo);
		}
	}

	/** This method is to modify a task **/
	// @author A0112066U
	private boolean modifyTask(Task task, Task t, boolean unredo) {
		String content;
		if (t != null) {
			content = t.getContent();
		} else {
			content = task.getContent();
		}
		if (content == null || content.isEmpty()) {
			messageToUser = MSG_NO_TASK_SPCIFIED;
			return false;
		}
		if (done) {
			messageToUser = MSG_NOT_ALLOWED_MODIFY;
			return false;
		}
		if (task.getStartDateTime() != null && task.getEndDateTime() != null
				&& isClashingWithBlockedSlots(toTaskData(task))) {
			messageToUser = MSG_CLASHES;
			return false;
		}
		if (!isInteger(content.substring(0, 1))) {
			return modifyTask(task, t, content, unredo);
		} else {
			return modifyIndex(task, content, unredo);
		}
	}

	/**
	 * This method is to modify a task specified by its content into another
	 * tasks
	 **/
	// @author A0112066U
	private boolean modifyTask(Task _task, Task t, String content,
			boolean unredo) {
		LocalDateTime newStartTime = _task.getStartDateTime();
		LocalDateTime newEndTime = _task.getEndDateTime();
		String newCategory = _task.getCategory();
		String newPriority = _task.getPriority();
		String newContent = _task.getContent();

		if (newContent == null || newContent.isEmpty()) {
			_task.setContent(content);
		}
		newContent = _task.getContent();
		HashMap<DateInfo, TaskData> _listTaskToEdit = new HashMap<DateInfo, TaskData>();

		if (taskIdentifier.containsKey(content)) {
			_listTaskToEdit = taskIdentifier.get(content);
		}
		LocalDateTime st = _task.getStartDateTime();
		LocalDateTime et = _task.getEndDateTime();
		TaskData toFind = new TaskData(content, null, null, null, null);
		TaskData taskToModify = null;

		// start searching the task to modify
		if (unredo) {
			st = t.getStartDateTime();
			et = t.getEndDateTime();
			DateInfo d = new DateInfo(st, et);
			taskToModify = _listTaskToEdit.get(d);
		} else {
			if (isSuspendedAction) {
				DateInfo d = new DateInfo(st, et);
				taskToModify = _listTaskToEdit.get(d);
				Task suspendingTask = suspendingAction.getTask();
				newStartTime = suspendingTask.getStartDateTime();
				newEndTime = suspendingTask.getEndDateTime();
				newCategory = suspendingTask.getCategory();
				newPriority = suspendingTask.getPriority();
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
		// end of searching
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
			boolean isExisting = checkExisting(newStartTime, newEndTime,
					newCategory, newPriority, newContent, _listTaskToEdit,
					toFind, taskToModify, oldEndTime, oldCategory, oldPriority);
			if (isExisting) {
				messageToUser = MSG_EXISTING_TASK;
				return false;
			}
			deleteTask(taskToModify, false);
			doModify(_task, t, unredo, newStartTime, newEndTime, newCategory,
					newPriority, taskToModify, oldStartTime, oldEndTime,
					oldCategory, oldPriority);
			taskToModify.setContent(newContent);
			t.setContent(newContent);
			_task.setContent(content);
			addTask(taskToModify, unredo);
			action = new Action(Command.MODIFY, _task);
			entry = new ActionEntry(action, t);
		} else {
			messageToUser = MSG_NO_TASK_FOUND;
			return false;
		}
		try {
			saveData();
		} catch (IOException e) {
			messageToUser = MSG_ERROR_WHILE_SAVING_DATA;
			return false;
		}
		return true;
	}

	/** This method does modifying the task with known information **/
	// author A0112066
	private void doModify(Task _task, Task t, boolean unredo,
			LocalDateTime newStartTime, LocalDateTime newEndTime,
			String newCategory, String newPriority, TaskData taskToModify,
			LocalDateTime oldStartTime, LocalDateTime oldEndTime,
			String oldCategory, String oldPriority) {
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
	}

	/** This method checks if the task after modifying exists **/
	// @author A0112066
	private boolean checkExisting(LocalDateTime newStartTime,
			LocalDateTime newEndTime, String newCategory, String newPriority,
			String newContent, HashMap<DateInfo, TaskData> _listTaskToEdit,
			TaskData toFind, TaskData taskToModify, LocalDateTime oldEndTime,
			String oldCategory, String oldPriority) {
		toFind.setContent(newContent);
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
		boolean isExisting = toFind != null && toFind != taskToModify;
		return isExisting;
	}

	/**
	 * This method is for modifying the task specified by its index on display
	 * panel with new description
	 **/
	// @author A0112066U
	private boolean modifyIndex(Task task, String content, boolean unredo) {
		int index = Integer.parseInt(content.substring(0, 1));
		content = content.substring(1).trim();
		ArrayList<TaskData> displayedList = getDisplayedList();
		int size = displayedList.size();
		if (index < 1 || index > size) {
			messageToUser = MSG_INDEX_OUT_OF_BOUND;
			return false;
		} else {
			TaskData _task = displayedList.get(index - 1);
			Task t = new Task();
			t.setContent(_task.getContent());
			t.setStartDateTime(_task.getStartDateTime());
			t.setEndDateTime(_task.getEndDateTime());
			task.setContent(content);
			return modifyTask(task, t, t.getContent(), unredo);
		}
	}

	/** This method is to mark a task as done **/
	// @author A0112066U
	private boolean markAsDone(TaskData _task, boolean unredo) {
		boolean isSuccessful = false;
		String content = _task.getContent();
		if (isInteger(content)) {
			return markAsDoneByIndex(Integer.parseInt(content), unredo);
		}
		if (done) {
			messageToUser = MSG_NOT_ALLOWED_MARK;
			return false;
		}
		HashMap<DateInfo, TaskData> _taskToEdit = taskIdentifier.get(content);
		TaskData task = null;
		LocalDateTime st = _task.getStartDateTime();
		LocalDateTime et = _task.getEndDateTime();

		task = getTaskToMark(_task, unredo, _taskToEdit, st, et);

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
			Task t = toTask(task, true);
			action = new Action(command, t);
			isSuccessful = true;
		} else {
			return isSuccessful;
		}
		try {
			saveData();
			saveCompletedTask();
		} catch (IOException e) {
			messageToUser = MSG_ERROR_WHILE_SAVING_DATA;
			return false;
		}
		return isSuccessful;
	}

	/** This method is to find the task to modify **/
	// @author A0112066

	private TaskData getTaskToMark(TaskData _task, boolean unredo,
			HashMap<DateInfo, TaskData> _taskToEdit, LocalDateTime st,
			LocalDateTime et) {
		TaskData task;
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
				task = searchTool.findTaskByContentandDate(_task, _taskToEdit);
				if (task.getContent().equals("xxxxxxxxxxxxxxxxxxxx")) {
					if (isSuspendedAction) {
						suspendingAction = null;
						isSuspendedAction = false;
					} else {
						suspendingAction = action;
						isSuspendedAction = true;
					}
					task = null;
				}
			}
		}
		return task;
	}

	/**
	 * This method is for marking a task specified by its index on display panel
	 **/
	// @author A0112066

	private boolean markAsDoneByIndex(int index, boolean unredo) {
		ArrayList<TaskData> displayedList = getDisplayedList();
		int size = displayedList.size();
		if (index < 1 || index > size) {
			messageToUser = MSG_INDEX_OUT_OF_BOUND;
			return false;
		} else {
			TaskData task = displayedList.get(index - 1);
			return markAsDone(task, unredo);
		}

	}

	/** This method is to block a list of slots **/
	// @author A0112066

	private boolean block(ArrayList<TaskData> blocks) {
		ArrayList<TaskData> block = new ArrayList<TaskData>();
		for (TaskData _task : blocks) {
			LocalDateTime _start = _task.getStartDateTime();
			LocalDateTime _end = _task.getEndDateTime();
			if (_start == null || _end == null) {
				messageToUser = MSG_TIME_SPECIFIED;
				return false;
			}
			block.addAll(block(_task));
		}
		blockSlot.push(block);
		storer.saveTasks(blockedPath, blockedList);
		return true;
	}

	/** This method is to unblock a list of slots **/
	// @author A0112066
	private boolean unblock(ArrayList<TaskData> blocks) {

		ArrayList<TaskData> unblock = new ArrayList<TaskData>();
		for (TaskData _task : blocks) {
			LocalDateTime _start = _task.getStartDateTime();
			LocalDateTime _end = _task.getEndDateTime();
			if (_start == null || _end == null) {
				messageToUser = MSG_TIME_SPECIFIED;
				return false;
			}
			unblock.addAll(unblock(_task));
		}
		unblockSlot.push(unblock);
		storer.saveTasks(blockedPath, blockedList);
		return true;
	}

	/** This method is to block a slot **/
	// @author A0112066
	private ArrayList<TaskData> block(TaskData slot) {

		LocalDateTime st = slot.getStartDateTime();
		LocalDateTime et = slot.getEndDateTime();
		LocalDateTime start = slot.getStartDateTime();
		LocalDateTime end = slot.getEndDateTime();
		slot.setContent("Blocked slot");
		blockedList.add(slot);
		ArrayList<TaskData> copy = new ArrayList<TaskData>(blockedList);
		for (TaskData _task : copy) {
			if (_task == slot)
				continue;
			LocalDateTime _start = _task.getStartDateTime();
			LocalDateTime _end = _task.getEndDateTime();
			if (isClash(start, end, _start, _end)
					|| isSequential(start, end, _start, _end)) {
				_start = chooseStart(start, _start);
				_end = chooseEnd(end, _end);
				slot.setStartDateTime(_start);
				slot.setEndDateTime(_end);
				blockedList.remove(_task);
			}
			start = slot.getStartDateTime();
			end = slot.getEndDateTime();
		}
		ArrayList<TaskData> block = new ArrayList<TaskData>();
		block.add(new TaskData("Blocked slot", null, null, st, et));
		return block;
	}

	/** This method is to unblock a slot **/
	// @author A0112066
	private ArrayList<TaskData> unblock(TaskData task) {
		LocalDateTime start = task.getStartDateTime();
		LocalDateTime end = task.getEndDateTime();
		ArrayList<TaskData> copy = new ArrayList<TaskData>(blockedList);
		ArrayList<TaskData> unblocked = new ArrayList<TaskData>();
		for (TaskData _task : copy) {
			LocalDateTime _start = _task.getStartDateTime();
			LocalDateTime _end = _task.getEndDateTime();
			if (isClash(start, end, _start, _end)) {
				if ((start.isBefore(_start) || start.equals(_start))
						&& (end.isAfter(_end) || end.equals(_end))) {
					blockedList.remove(_task);
					unblocked.add(new TaskData(null, null, null, _start, _end));
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
					unblocked.add(new TaskData(null, null, null, start, end));
					continue;
				}
				if (start.isBefore(_start) && end.isBefore(_end)) {
					blockedList.remove(_task);
					_task.setStartDateTime(end);
					blockedList.add(_task);
					unblocked.add(new TaskData(null, null, null, _start, end));
					continue;
				}
				if (_start.isBefore(start) && _end.isBefore(end)) {
					blockedList.remove(_task);
					_task.setEndDateTime(start);
					blockedList.add(_task);
					unblocked.add(new TaskData(null, null, null, start, _end));
					continue;
				}
			}
		}
		return unblocked;
	}

	private LocalDateTime chooseStart(LocalDateTime start1, LocalDateTime start2) {
		return (start1.isBefore(start2)) ? start1 : start2;
	}

	private LocalDateTime chooseEnd(LocalDateTime end1, LocalDateTime end2) {
		return (end1.isAfter(end2)) ? end1 : end2;
	}

	/** This methos check if two periods of time clash with each other **/
	// @author A0112066

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

	/** This methos check if two periods of time are sequential **/
	// @author A0112066

	private boolean isSequential(LocalDateTime start1, LocalDateTime end1,
			LocalDateTime start2, LocalDateTime end2) {
		return start1.isEqual(end2) || start2.isEqual(end1);
	}

	/** This method is for redoing an action **/
	// @author A0112066U
	private boolean redo() throws IOException, ParseException {
		if (redoList.isEmpty()) {
			messageToUser = MSG_CANNOT_REDO_ANYMORE;
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
			if (task.isDone()) {
				this.done = true;
			}
			return addTask(_task, true);
		case DELETE:
			if (task.isDone()) {
				this.done = true;
			}
			return deleteTask(_task, true);
		case MODIFY:
			return modifyTask(task, t, true);
		case MARK:
			return markAsDone(_task, true);
		case BLOCK:
			ArrayList<TaskData> blockList = unblockSlot.pop();
			blockSlot.push(blockList);
			return block(blockList);
		case UNBLOCK:
			ArrayList<TaskData> unblockList = blockSlot.pop();
			unblockSlot.push(unblockList);
			return unblock(unblockList);
		default:
			return false;
		}
	}

	/** This method is for undoing an action **/
	// @author A0112066U
	private boolean undo() {
		boolean isSuccessful = false;
		if (actionList.isEmpty()) {
			messageToUser = MSG_CANNOT_UNDO;
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
		if (done.getTask().isDone()) {
			this.done = true;
		}
		boolean isSuccessful = addTask(toTaskData(done.getTask()), true);
		if (isSuccessful)
			redoList.push(new ActionEntry(done, null));
		else
			actionList.push(new ActionEntry(done, null));
		return isSuccessful;
	}

	// @author A0112066U
	private boolean undoAdd(Action done) {
		if (done.getTask().isDone()) {
			this.done = true;
		}
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

	// @author A0112066U
	private boolean undoMark(Action done) {
		Task task = done.getTask();
		boolean isSuccessful = false;
		String s = task.getStartDateTime() + "" + task.getEndDateTime();
		TaskData _task = completedTaskIdentifier.get(s);
		try {
			completedTask.remove(_task);
			isSuccessful = addTask(_task, true);
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
		boolean isSuccessful = false;
		ArrayList<TaskData> blockList = blockSlot.pop();
		isSuccessful = unblock(blockList);
		unblockSlot.push(blockList);
		if (isSuccessful) {
			redoList.push(new ActionEntry(done, null));
		} else {
			actionList.push(new ActionEntry(done, null));
		}
		return isSuccessful;
	}

	private boolean undoUnblock(Action done) {
		boolean isSuccessful = false;
		ArrayList<TaskData> blockList = unblockSlot.pop();
		isSuccessful = block(blockList);
		blockSlot.push(blockList);
		if (isSuccessful) {
			redoList.push(new ActionEntry(done, null));
		} else {
			actionList.push(new ActionEntry(done, null));
		}
		return isSuccessful;
	}

	/**
	 * This method is to search for a task or a block slot, return in
	 * searchResult
	 **/
	// @author A0112066U

	private boolean search(Task task) throws IOException, ParseException {
		searchResult = new ArrayList<TaskData>();
		String content = task.getContent();
		if (content != null
				&& !content.isEmpty()
				&& (content.startsWith("block") || content
						.startsWith("blocked"))) {
			searchResult = blockedList;
		} else {
			if (!task.isDone()) {
				searchResult = searchTool.search(taskList, task);
				done = false;
			} else {
				searchResult = searchTool.search(completedTask, task);
				done = true;
			}
		}
		if (searchResult != null) {
			return true;
		}

		overdueRow = 0;
		return false;
	}

	/** This method is to exit, data is saved and jframe is minimised to tray **/
	// @author A0112066U
	private boolean exit() {
		// store when exit
		try {
			saveData();
			saveCompletedTask();
		} catch (IOException e) {
			messageToUser = MSG_ERROR_WHILE_SAVING_DATA;
		}
		return true;
		// System.exit(0);
	}

	// @author A0112066U
	public void saveData() throws IOException {
		storer.saveTasks(taskFilePath, taskList);
	}

	// @author A0112066U
	private void saveCompletedTask() throws IOException {
		storer.saveTasks(completedTaskFilePath, completedTask);
	}

	/** This method returns tasks to come **/
	// @author A0112066U
	public ArrayList<TaskData> getTaskToCome() {
		LocalDateTime now = LocalDateTime.now();
		int dateToday = now.getDayOfMonth();
		int monthToday = now.getMonthValue();
		int yearToday = now.getYear();
		LocalDateTime today = LocalDateTime.of(yearToday, monthToday,
				dateToday, 0, 0, 0).minusSeconds(1);
		LocalDateTime tomorrow = today.plusSeconds(172801);
		today = now;
		ArrayList<TaskData> taskToCome = new ArrayList<TaskData>();
		for (TaskData _task : taskList) {
			LocalDateTime st = _task.getStartDateTime();
			LocalDateTime et = _task.getEndDateTime();
			if (st != null && st.isAfter(today) && st.isBefore(tomorrow)) {
				taskToCome.add(_task);
			} else if (et != null && et.isAfter(today) && et.isBefore(tomorrow)) {
				taskToCome.add(_task);
			} else if (st != null && et != null
					&& isClash(st, et, today, tomorrow)) {
				taskToCome.add(_task);
			}
		}
		return taskToCome;
	}

	/** This methods answers UI, checks if a date has task **/
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
			LocalDateTime st = _task.getStartDateTime();
			LocalDateTime et = _task.getEndDateTime();
			if (st != null && st.isAfter(startTime) && st.isBefore(endTime)) {
				task.add(_task);
			} else if (et != null && et.isAfter(startTime)
					&& et.isBefore(endTime)) {
				task.add(_task);
			} else if (st != null && et != null
					&& isClash(st, et, startTime, endTime)) {
				task.add(_task);
			}
		}
		return !task.isEmpty();
	}

	/** This method returns overdue task to UI **/
	// @author A0112066U
	public ArrayList<TaskData> getOverdue() {
		ArrayList<TaskData> overdue = new ArrayList<TaskData>();
		LocalDateTime now = LocalDateTime.now();
		Collections.sort(taskList);
		for (TaskData _task : taskList) {
			LocalDateTime endTime = _task.getEndDateTime();
			if (endTime != null && endTime.isBefore(now)) {
				overdue.add(_task);
			}
		}
		return overdue;
	}

	/** This methods return all categories to UI **/
	// @author A0112066U
	public String getCategory() {
		ArrayList<String> category = new ArrayList<String>();
		for (TaskData _task : taskList) {
			String cat = _task.getCategory();
			if (cat != null && !category.contains(cat))
				category.add(cat);
		}
		String res = "";
		for (String cat : category) {
			res += cat + "\n";
		}
		return res;
	}

	/** This methods return data of recently added tasks to UI **/
	// @author A0112066U
	public String getData(String s) throws IOException, ParseException {
		currentDisplayList = 2;
		return dataToShow();
	}

	// @author A0112066U
	private String dataToShow() throws IOException, ParseException {
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

	/* clear all data */
	// author A0112066
	public void clear() throws IOException {
		taskList.clear();
		saveData();
	}

	/** This method translates a Task to TaskData for storage **/
	// @author A0112066U
	private TaskData toTaskData(Task task) {
		String content = task.getContent();
		LocalDateTime startTime = task.getStartDateTime();
		LocalDateTime endTime = task.getEndDateTime();
		String category = task.getCategory();
		String priority = task.getPriority();
		return new TaskData(content, category, priority, startTime, endTime);
	}

	/** This method translates a TaskData to Task **/
	// @author A0112066U
	private Task toTask(TaskData task, boolean done) {
		Task t = new Task();
		t.setContent(task.getContent());
		t.setCategory(task.getCategory());
		t.setStartDateTime(task.getStartDateTime());
		t.setEndDateTime(task.getEndDateTime());
		t.setPriority(task.getPriority());
		t.setDone(done);
		return t;
	}

	/** This method is to check whether a string provided is an integer **/
	// @author A0112066
	private static boolean isInteger(String index) {
		try {
			Integer.parseInt(index);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/** This method returns to UI the list of tasks it requires **/
	// @author A0112066U

	public ArrayList<DisplayedEntry> getRequiredTask(String userCommand) {
		Command cmd = null;
		if (userCommand != null && !userCommand.isEmpty()) {
			cmd = parser.getAction(userCommand).getCommand();
		}
		if (cmd != null && cmd.equals(Command.SEARCH)) {

			F3DisplayedList = searchResult;
			overdueRow = 0;
		}
		if (cmd == null || cmd.equals("") || cmd.equals(Command.ADD)
				|| cmd.equals(Command.DELETE) || cmd.equals(Command.MODIFY)
				|| cmd.equals(Command.MARK) || cmd.equals(Command.UNDO)
				|| cmd.equals(Command.REDO)) {
			F3DisplayedList = new ArrayList<TaskData>();
			ArrayList<TaskData> overdue = getOverdue();
			overdueRow = overdue.size();
			ArrayList<TaskData> tasksToCome = getTaskToCome();
			F3DisplayedList.addAll(overdue);
			F3DisplayedList.addAll(tasksToCome);
			for (TaskData t : taskList) {
				if (!overdue.contains(t) && !tasksToCome.contains(t))
					F3DisplayedList.add(t);
			}
			done = false;
		}
		currentDisplayList = 3;
		ArrayList<DisplayedEntry> tobeShown = new ArrayList<DisplayedEntry>();
		if (F3DisplayedList != null) {
			for (TaskData t : F3DisplayedList) {
				tobeShown.add(toDisplayedEntry(t));
			}
		}
		return tobeShown;
	}

	public int getOverdueRow() {
		return overdueRow;
	}

	/** This method is to determined what list of tasks is being displayed **/
	// @author A0112066
	private ArrayList<TaskData> getDisplayedList() {
		if (currentDisplayList == 2) {
			return F2DisplayedList;
		} else if (currentDisplayList == 3) {
			return F3DisplayedList;
		}
		return null;
	}

	/**
	 * This method translates a TaskData to a DisplayedEntry for displaying in
	 * UI
	 **/
	// @author A0112066U
	private DisplayedEntry toDisplayedEntry(TaskData task) {
		return new DisplayedEntry(task);
	}
}