package logic;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import logic.Logic.*;

import org.json.simple.parser.ParseException;

import parser.*;
import storage.*;

public class SearchTool {

	private final String message = "xxxxxxxxxxxxxxxxxxxx";

	public TaskData findTaskByContentandDate(TaskData task,
			HashMap<DateInfo, TaskData> toFindList) {

		TaskData taskFound = null;
		LocalDateTime st = task.getStartDateTime();
		LocalDateTime et = task.getEndDateTime();

		if (toFindList.size() == 1) {
			for (TaskData _task : toFindList.values()) {
				taskFound = _task;
			}

			LocalDateTime s = taskFound.getStartDateTime();
			LocalDateTime e = taskFound.getEndDateTime();
			if (st != null && !st.equals(s))
				taskFound = null;
			if (et != null && !et.equals(e))
				taskFound = null;

		} else if (toFindList.size() > 1) {
			if (st == null && et == null) {
				taskFound = new TaskData();
				taskFound.setContent(message);
				return taskFound;
			}
			DateInfo d = new DateInfo(st, et);
			if (toFindList.containsKey(d)) {
				taskFound = toFindList.get(d);
			}

		}
		return taskFound;
	}

	public TaskData findExactTask(TaskData task,
			HashMap<DateInfo, TaskData> toFindList) {
		TaskData taskFound = null;
		LocalDateTime st = task.getStartDateTime();
		LocalDateTime et = task.getEndDateTime();
		DateInfo d = new DateInfo(st, et);
		if (toFindList.containsKey(d)) {
			taskFound = toFindList.get(d);
		}
		if (taskFound == null)
			return null;
		if (!hasSameDateTime(task, taskFound))
			return null;
		return taskFound;
	}

	private boolean hasSameDateTime(TaskData t1, TaskData t2) {
		DateInfo d1 = new DateInfo(t1.getStartDateTime(), t1.getEndDateTime());
		DateInfo d2 = new DateInfo(t2.getStartDateTime(), t2.getEndDateTime());
		return d1.hashCode() == d2.hashCode();
	}

	public ArrayList<TaskData> search(ArrayList<TaskData> taskList, Task task)
			throws IOException, ParseException {
		if (taskList.isEmpty()) {
			return null;
		}

		String content = task.getContent();
		String[] words;
		if (content != null)
			words = content.split(" "); // keyword for searching
		else
			words = new String[] { "" };
		LocalDateTime startTime = task.getStartDateTime();
		LocalDateTime endTime = task.getEndDateTime(); // search in between
														// startTime and endTime
		String category = task.getCategory(); // search in category
		String priority = task.getPriority(); // search by priority

		ArrayList<TaskData> searchResult = new ArrayList<TaskData>();
		searchResult = filterByTime(taskList, startTime, endTime);
		if (category != null)
			searchResult = filterByCategory(searchResult, category.split(" "));
		if (priority != null)
			searchResult = filterByPriority(searchResult, priority);
		searchResult = filterByKeywords(searchResult, words);

		return searchResult;
	}

	private ArrayList<TaskData> filterByTime(ArrayList<TaskData> taskList,
			LocalDateTime startTime, LocalDateTime endTime) {
		ArrayList<TaskData> searchResult = new ArrayList<TaskData>();
		if (startTime != null && endTime != null) {
			startTime = startTime.minusSeconds(1);
			endTime = endTime.plusSeconds(1);
			for (TaskData _task : taskList) {
				LocalDateTime st = _task.getStartDateTime();
				LocalDateTime et = _task.getEndDateTime();
				if (st != null && st.isAfter(startTime) && st.isBefore(endTime)) {
					searchResult.add(_task);
				} else if (et != null && et.isAfter(startTime)
						&& et.isBefore(endTime)) {
					searchResult.add(_task);
				}
			}

		} else if (startTime != null) {
			startTime = startTime.minusSeconds(1);
			for (TaskData _task : taskList) {
				LocalDateTime st = _task.getStartDateTime();
				if (st != null && st.isAfter(startTime)) {
					searchResult.add(_task);
				}
			}

		} else {
			searchResult = taskList;
		}
		return searchResult;
	}

	private ArrayList<TaskData> filterByCategory(ArrayList<TaskData> taskList,
			String[] _category) {
		ArrayList<TaskData> searchResult = new ArrayList<TaskData>();
		List<String> _cat = Arrays.asList(_category);
		if (_category.length == 0)
			return taskList;
		else {
			for (TaskData t : taskList) {
				if (_cat.contains(t.getCategory()))
					searchResult.add(t);
			}
			return searchResult;
		}
	}

	private ArrayList<TaskData> filterByPriority(ArrayList<TaskData> taskList,
			String _priority) {
		ArrayList<TaskData> searchResult = new ArrayList<TaskData>();

		for (TaskData t : taskList) {
			if (t.getPriority().contains(_priority))
				searchResult.add(t);
		}
		return searchResult;

	}

	private ArrayList<TaskData> filterByKeywords(ArrayList<TaskData> taskList,
			String[] _words) {
		ArrayList<TaskData> searchResult = new ArrayList<TaskData>();
		for (String w : _words) {
			w = w.toLowerCase();
		}
		for (TaskData t : taskList) {
			boolean isContained = true;
			for (String word : _words) {
				if (!t.getContent().toLowerCase().contains(word)) {
					isContained = false;
					break;
				}
			}
			if (isContained)
				searchResult.add(t);
		}
		return searchResult;
	}

	

}
