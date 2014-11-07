package parser;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

//@author A0111887Y
//This class translates an input String into Task data.
public class Parser {

	private final List<String> KEYWORDS_COMMAND_ADD = Arrays.asList("add", "schedule", "create");
	private final List<String> KEYWORDS_COMMAND_MODIFY = Arrays.asList("mod", "modify", "edit", "reschedule", "change");
	private final List<String> KEYWORDS_COMMAND_DELETE = Arrays.asList("del", "delete", "rm", "remove", "clr", "clear");
	private final List<String> KEYWORDS_COMMAND_SEARCH = Arrays.asList("display", "show", "find", "search");
	private final List<String> KEYWORDS_COMMAND_BLOCK = Arrays.asList("blk", "block", "reserve");
	private final List<String> KEYWORDS_COMMAND_UNBLOCK = Arrays.asList("unblk", "unblock", "unreserve", "free");
	private final List<String> KEYWORDS_COMMAND_OTHER = Arrays.asList("exit", "undo", "redo");
	private final List<String> KEYWORDS_DATE_DAY = Arrays.asList("today", "tomorrow", "yesterday", "tonight");
	private final List<String> KEYWORDS_DATE_DAY_OF_WEEK = Arrays.asList("monday","mon", "tuesday", "tue", "wednesday", "wed", "thursday", "thu", "friday", "fri", "saturday", "sat", "sunday", "sun");
	private final List<String> KEYWORDS_DATE_PERIOD = Arrays.asList("day", "week", "month", "year");
	private final List<String> KEYWORDS_DATE_MONTH = Arrays.asList("jan", "january", "feb", "february", "mar", "march", "apr", "april", "may", "jun", "june", "jul", "july", "aug", "august", "sep", "september", "oct", "october", "nov", "november", "dec", "december");
	private final List<String> KEYWORDS_DATE_ORDINAL_NUMBER = Arrays.asList("st", "nd", "rd" ,"th");
	private final List<String> KEYWORDS_TIME_OF_DAY = Arrays.asList("morning", "noon", "afternoon", "evening", "night", "midnight");
	private final List<String> KEYWORDS_TIME = Arrays.asList("am", "pm");
	private final List<String> KEYWORDS_PRIORITY = Arrays.asList("priority", "important", "unimportant");
	private final List<String> KEYWORDS_MARK = Arrays.asList("complete", "completed", "incomplete", "done", "undone");
	private final List<String> SECONDARY_KEYWORDS_DATE = Arrays.asList("this", "next", "last");
	private final List<String> SECONDARY_KEYWORDS_DATE_TIME = Arrays.asList("after", "before", "by");
	private final List<String> SECONDARY_KEYWORDS_MARK = Arrays.asList("not", "yet", "to", "be", "has", "been", "is");
	private final List<String> USELESS_WORDS = Arrays.asList("on", "from", "to", "@", "at");
	private final NumberFormat FORMATTER = NumberFormat.getIntegerInstance();
	private final String SYMBOL_CATEGORY = "#";
	private final String SYMBOL_PRIORITY = "!";
	private final String PRIORITY_VERY_HIGH = "very high";
	private final String PRIORITY_HIGH = "high";
	private final String PRIORITY_NORMAL = "normal";
	private final int MAX_PRIORITY_LEVEL = 2;
	private final String ERROR_INPUT_NULL = "Parser input null";

	//@author A0111887Y
	//This is the public method called to translate an input String into Task data.
	public Action getAction(String input) {
		
		MyStringList wordList = new MyStringList();
		try {
			wordList.addAll(Arrays.asList(input.split(" ")));
		} catch (NullPointerException e) {
			System.err.println(ERROR_INPUT_NULL);
			return null;
		}
		removeReminder(wordList);
		return new Action(getCommand(wordList), getTask(wordList));
		
	}

	//@author A0111887Y
	//This method removes the reminder in the input String.
	private void removeReminder(MyStringList wordList) {
		
		assert wordList != null;
		int startIndex = 0;
		int endIndex = 0;
		for (int index = 0; index < wordList.size(); index++) {
			if (wordList.get(index).startsWith("[")) {
				startIndex = index;
				break;
			}
		}
		for (int index = 0; index < wordList.size(); index++) {
			if (wordList.get(index).endsWith("]")) {
				if (index > startIndex) {
					endIndex = index;
					break;
				}
			}
		}
		if (endIndex != 0) {
			while (endIndex >= startIndex) {
				wordList.remove(startIndex);
				endIndex--;
			}
		}
		
	}

	//@author A0111887Y
	//This methods find the word representing a command and returns the Command.
	private Command getCommand(MyStringList wordList) {
		
		assert wordList != null;
		for (int index = 0; index < wordList.size(); index++) {
			String word = wordList.get(index).toLowerCase();
			if (KEYWORDS_COMMAND_ADD.contains(word)) {
				wordList.remove(index);
				return Command.ADD;
			}
			if (KEYWORDS_COMMAND_MODIFY.contains(word)) {
				wordList.remove(index);
				return Command.MODIFY;
			}
			if (KEYWORDS_COMMAND_DELETE.contains(word)) {
				wordList.remove(index);
				return Command.DELETE;
			}
			if (KEYWORDS_COMMAND_SEARCH.contains(word)) {
				wordList.remove(index);
				return Command.SEARCH;
			}
			if (KEYWORDS_COMMAND_BLOCK.contains(word)) {
				wordList.remove(index);
				return Command.BLOCK;
			}
			if (KEYWORDS_COMMAND_UNBLOCK.contains(word)) {
				wordList.remove(index);
				return Command.UNBLOCK;
			}
			if (KEYWORDS_COMMAND_OTHER.contains(word)) {
				wordList.remove(index);
				switch (word) {
					case "undo" :
						return Command.UNDO;
					case "redo" :
						return Command.REDO;
					case "exit" :
						return Command.EXIT;
				}
			}
		}
		if (isMarked(wordList)) {
			return Command.MARK;
		}
		return Command.ADD;
		
	}

	//@author A0111887Y
	//This method returns true if there are words that represent marking a Task as done.
	private boolean isMarked(MyStringList wordList) {
		
		assert wordList != null;
		if (wordList.containsIgnoreCase("mark")) {
			wordList.removeIgnoreCase("mark");
			return true;
		}
		for (int index = 0; index < wordList.size(); index++) {
			String word = wordList.get(index).toLowerCase();
			if (KEYWORDS_MARK.contains(word)) {
				return true;
			}
		}
		return false;
		
	}

	//@author A0111887Y
	//This method creates a Task object with data translated from user input
	private Task getTask(MyStringList wordList) {
		
		Task task = new Task();
		setCategory(wordList, task);
		setPriority(wordList, task);
		setDateTime(wordList, task);
		setDone(wordList, task);
		setContent(wordList, task);
		fixDateTime(task);
		fixContent(task);
		return task;
		
	}

	//@author A0111887Y
	//This method finds the Task category in the input String.
	private void setCategory(MyStringList wordList, Task task) {
		
		assert wordList != null && task != null;
		for (String word : wordList) {
			if(word.length() > 1 && word.startsWith(SYMBOL_CATEGORY)) {
				task.setCategory(word.substring(1));
				wordList.remove(wordList.indexOf(word));
				break;
			}
		}
		
	}

	//@author A0111887Y
	//This method finds the Task priority in the input String.
	private void setPriority(MyStringList wordList, Task task) {

		int priorityLevel = findPriorityLevelWithWord(wordList);
		if (priorityLevel == -1) {
			priorityLevel = findPriorityLevelWithSymbol(wordList, MAX_PRIORITY_LEVEL);
		}
		switch (priorityLevel) {
			case 2 :
				task.setPriority(PRIORITY_VERY_HIGH);
				break;
			case 1 :
				task.setPriority(PRIORITY_HIGH);
				break;
			case 0 :
				task.setPriority(PRIORITY_NORMAL);
		}
		
	}
	
	//@author A0111887Y
	//This method helps find the Task priority by searching for words.
	private int findPriorityLevelWithWord(MyStringList wordList) {
		
		assert wordList != null;
		for (int index = 0; index < wordList.size(); index++) {
			if (KEYWORDS_PRIORITY.contains(wordList.get(index).toLowerCase())) {
				return getPriorityLevelWithWord(wordList, index);
			}
		}
		return -1;
		
	}

	//@author A0111887Y
	//This method helps find the Task priority by searching for the number of "!" in the input String.
	private int findPriorityLevelWithSymbol(MyStringList wordList, int priorityLevel) {
		
		assert wordList != null;
		if (priorityLevel == 0) {
			return -1;
		}
		String priorityString = "";
		for (int count = 0; count < priorityLevel; count++) {
			priorityString += SYMBOL_PRIORITY;
		}
		for (int index = 0; index < wordList.size(); index++) {
			String word = wordList.get(index);
			if (word.endsWith(priorityString)) {
				word = word.substring(0, word.lastIndexOf(priorityString));
				if (word.isEmpty()) {
					wordList.remove(index);
				} else {
					wordList.set(index, word);
				}
				return priorityLevel;
			}
		}
		return findPriorityLevelWithSymbol(wordList, priorityLevel - 1);
		
	}

	//@author A0111887Y
	//This method sets the isDone boolean in Task to true if a Task is marked done in the input String.
	private void setDone(MyStringList wordList, Task task) {

		assert wordList != null && task != null;
		for (int index = 0; index < wordList.size(); index++) {
			String word = wordList.get(index).toLowerCase();
			if (KEYWORDS_MARK.contains(word)) {
				if (word.equals("done") || word.equals("complete") || word.equals("completed")) {
					task.setDone(true);
				}
				wordList.remove(index);
				if (index - 1 >= 0) {
					changeDone(wordList, index - 1, task);
				}
				break;
			}
		}
		
	}

	//@author A0111887Y
	//This method changes the boolean isDone depending on certain words and their order in the input String.
	private void changeDone(MyStringList wordList, int index, Task task) {
		
		assert wordList != null && task != null && index >= 0;
		String word = wordList.get(index).toLowerCase();
		boolean isDone;
		while (SECONDARY_KEYWORDS_MARK.contains(word)) {
			isDone = task.isDone();
			switch (word) {
				case "yet" :
					if (index - 1 >= 0 && wordList.get(index - 1).toLowerCase().equals("not")) {
						wordList.remove(index);
						index--;
						break;
					}
				case "to" :
				case "be" :
				case "not" :
					task.setDone(isDone ^= true);
				default :
					wordList.remove(index);
					index--;
			}
			if (index >= 0) {
				word = wordList.get(index).toLowerCase();
			} else {
				break;
			}
		}
		
	}

	//@author A0111887Y
	//This method fixes the startDateTime and endDateTime in Task object after all the dates and times in input String has been set.
	private void fixDateTime(Task task) {
		
		assert task != null;
		LocalDateTime startDateTime = task.getStartDateTime();
		LocalDateTime endDateTime = task.getEndDateTime();
		if (startDateTime != null) {
			if (startDateTime.getYear() == 0) {
				task.setStartDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(startDateTime.getHour(), startDateTime.getMinute())));
				startDateTime = task.getStartDateTime();
			}
			if (startDateTime.getSecond() == 1) {
				task.setStartDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth(), 0, 0));
			}
			if (endDateTime != null) {
				if (endDateTime.getYear() == 0) {
					task.setEndDateTime(startDateTime.withHour(endDateTime.getHour()).withMinute(endDateTime.getMinute()));
					endDateTime = task.getEndDateTime();
				}
				if (endDateTime.getSecond() == 1) {
					task.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth(), 23, 59));
					endDateTime = task.getEndDateTime();
				}
				if (startDateTime.isAfter(endDateTime)) {
					task.setEndDateTime(endDateTime.plusWeeks(1));
					endDateTime = task.getEndDateTime();
				}
			} else {
				task.setEndDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth(), 23, 59));
			}
		}
		
	}

	//@author A0111887Y
	//This method changes the content into index if the entire content is a positive non-zero numerical number.
	private void fixContent(Task task) {
		
		assert task != null;
		String content = task.getContent();
		if (content != null && isNumeric(content)) {
			int index = Integer.parseInt(content);
			if (index > 0) {
				task.setIndex(index);
			}
		}
		
	}

	//@author A0111887Y
	//This method finds the dates and times in the input String and sets them to Task object.
	private void setDateTime(MyStringList wordList, Task task) {
		
		assert wordList != null && task != null;
		for (int index = 0; index < wordList.size(); index++) {
			index = findDateTime(wordList, index, task);
		}
		
	}

	//@author A0111887Y
	//This method helps identify if a word is date or time.
	private int findDateTime(MyStringList wordList, int index, Task task) {
		
		assert wordList != null && task != null;
		if (index >= 0 && index < wordList.size()) {
			LocalDate date = null;
			LocalTime time = null;
			if (wordList.get(index).isEmpty()) {
				wordList.remove(index);
				return index--;
			}
			if (wordList.get(index).length() <= 4) {
				date = findDateWithDay(wordList, index);
				if (date == null) {
					date = findDateWithYear(wordList, index);
				}
			}
			if (date == null) {
				date = findDateWithMonth(wordList, index);
			}
			if (date == null) {
				date = findDate(wordList, index);
			}
			if (date == null) {
				date = findDateWithWord(wordList, index);
			}
			if (date == null) {
				date = findDateWithKeyWord(wordList, index);
			}
			if (date == null) {
				time = findTime(wordList, index);
			}
			if (date == null && time == null) {
				time = findTimeWithWord(wordList, index);
			}
			if (date != null || time != null) {
				setDateTimeToTask(task, date, time);
				index -= adjustDateTimeOfTask(wordList, index - 1, task);
				index -= 1 + removeUselessWord(wordList, index - 1);
			}
		}
		return index;
		
	}

	//@author A0111887Y
	//This method sets date and time to startDateTime and endDateTime in Task.
	private void setDateTimeToTask(Task task, LocalDate date, LocalTime time) {
		
		assert task != null && (date != null || time != null);
		LocalDateTime startDateTime = task.getStartDateTime();
		LocalDateTime endDateTime = task.getEndDateTime();
		if (date != null) {
			if (startDateTime == null && endDateTime == null) {
				task.setStartDateTime(LocalDateTime.of(date, LocalTime.of(0, 0, 1)));
			} else if (endDateTime == null) {
				if (startDateTime.getYear() == 0) {
					task.setStartDateTime(LocalDateTime.of(date, LocalTime.of(startDateTime.getHour(), startDateTime.getMinute())));
				} else {
					task.setEndDateTime(LocalDateTime.of(date, LocalTime.of(0, 0, 1)));
				}
			} else {
				task.setEndDateTime(LocalDateTime.of(date, LocalTime.of(endDateTime.getHour(), endDateTime.getMinute())));
			}
		} else {
			if (startDateTime == null && endDateTime == null) {
				task.setStartDateTime(LocalDateTime.of(LocalDate.of(0, 1, 1), time));
			} else if (endDateTime == null) {
				if (startDateTime.getSecond() == 1) {
					task.setStartDateTime(LocalDateTime.of(LocalDate.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth()), time));
				} else {
					task.setEndDateTime(LocalDateTime.of(LocalDate.of(0, 1, 1), time));
				}
			} else if (endDateTime.getSecond() == 1) {
				task.setEndDateTime(LocalDateTime.of(LocalDate.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth()), time));
			} else if (startDateTime.getSecond() == 1) {
				task.setStartDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth(), endDateTime.getHour(), endDateTime.getMinute()));
				task.setEndDateTime(LocalDateTime.of(LocalDate.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth()), time));
			} else {
				task.setEndDateTime(LocalDateTime.of(LocalDate.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth()), time));
			}
		}
		
	}

	//@author A0111887Y
	//This method changes the startDateTime and endDateTime in Task object depending on the specific keywords in input String.
	private int adjustDateTimeOfTask(MyStringList wordList, int index, Task task) {

		assert wordList != null && task != null;
		if (index >= 0) {
			String word = wordList.get(index).toLowerCase();
			if (SECONDARY_KEYWORDS_DATE_TIME.contains(word)) {
				return changeDateTimeOfTask(wordList, index, task);
			}
		}
		return 0;
		
	}

	//@author A0111887Y
	//This method helps to identify a date by checking for specific keywords.
	private LocalDate findDateWithKeyWord(MyStringList wordList, int index) {
		
		assert wordList != null && index >= 0;
		LocalDate date = null;
		if (SECONDARY_KEYWORDS_DATE.contains(wordList.get(index).toLowerCase())) {
			date = getDateWithDayOfWeekWord(wordList, index + 1);
			switch (wordList.get(index)) {
				case "this" :
					if (date == null) {
						date = getDateWithPeriodWord(wordList, index + 1, 0);
					}
					break;
				case "next" :
					if (date != null) {
						date = date.plusWeeks(1);
					} else {
						date = getDateWithPeriodWord(wordList, index + 1, 1);
					}
					break;
				case "last" :
					if (date != null) {
						date = date.plusWeeks(-1);
					} else {
						date = getDateWithPeriodWord(wordList, index + 1, -1);
					}
				default :
			}
			if (date != null) {
				wordList.remove(index);
			}
		}
		return date;
		
	}

	//@author A0111887Y
	//This method helps to change the startDateTime and endDateTime in Task object by identifying the keywords.
	private int changeDateTimeOfTask(MyStringList wordList, int index, Task task) {
		
		assert wordList != null && task != null && index >= 0;
		String word = wordList.get(index).toLowerCase();
		int numOfWordsToRemove = 0;
		switch (word) {
			case "after" :
				numOfWordsToRemove = changeDateTimeWithPeriodWord(wordList, index - 1, task, 1);
				break;
			case "before" :
				numOfWordsToRemove = changeDateTimeWithPeriodWord(wordList, index - 1, task, -1);
				if (numOfWordsToRemove != 0) {
					break;
				}
			case "by" :
				LocalDateTime startDateTime = task.getStartDateTime();
				LocalDateTime endDateTime = task.getEndDateTime();
				if (endDateTime == null) {
					task.setEndDateTime(startDateTime);
					task.setStartDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute())));
				}
		}
		numOfWordsToRemove++;
		for (int i = numOfWordsToRemove; i > 0; i--) {
			wordList.remove(index);
			index--;
		}
		if (index >= 0 && wordList.get(index).toLowerCase().equals("the")) {
			numOfWordsToRemove++;
			wordList.remove(index);
		}
		return numOfWordsToRemove;
		
	}

	//@author A0111887Y
	//This method helps change startDateTime and endDateTime in Task object by looking for more keywords.
	private int changeDateTimeWithPeriodWord(MyStringList wordList, int index, Task task, int multiplier) {
		
		assert wordList != null && task != null && (multiplier == 1 || multiplier == -1);
		if (index >= 0 && KEYWORDS_DATE_PERIOD.contains(wordList.get(index))) {
			String word = wordList.get(index).toLowerCase();
			LocalDateTime startDateTime = task.getStartDateTime();
			LocalDateTime endDateTime = task.getEndDateTime();
			switch (word) {
				case "day" :
					if (endDateTime != null) {
						task.setEndDateTime(endDateTime.plusDays(multiplier));
					} else {
						task.setStartDateTime(startDateTime.plusDays(multiplier));
					}
					return 1;
				case "week" :
					if (endDateTime != null) {
						task.setEndDateTime(endDateTime.plusWeeks(multiplier));
					} else {
						task.setStartDateTime(startDateTime.plusWeeks(multiplier));
					}
					return 1;
				case "month" :
					if (endDateTime != null) {
						task.setEndDateTime(endDateTime.plusMonths(multiplier));
					} else {
						task.setStartDateTime(startDateTime.plusMonths(multiplier));
					}
					return 1;
				case "year" :
					if (endDateTime != null) {
						task.setEndDateTime(endDateTime.plusYears(multiplier));
					} else {
						task.setStartDateTime(startDateTime.plusYears(multiplier));
					}
					return 1;
				default :
			}
		}
		return 0;
		
	}

	//@author A0111887Y
	//This method helps to identify a date by checking if the word is a numerical number and a valid day.
	private LocalDate findDateWithDay(MyStringList wordList, int index){
		
		assert wordList != null && index >= 0;
		if (index + 1 < wordList.size()) {
			int day = getDay(wordList.get(index));
			if (day > 0) {
				int month = getMonth(wordList.get(index + 1));
				if (month > 0) {
					int year;
					if (index + 2 < wordList.size()) {
						year = getYear(wordList.get(index + 2));
						if (year > 0) {
							if (isValidDate(year, month, day)) {
								wordList.remove(index + 2);
								wordList.remove(index + 1);
								wordList.remove(index);
								return LocalDate.of(year, month, day);
							}
						}
					}
					year = LocalDate.now().getYear();
					if (isValidDate(year, month, day)) {
						wordList.remove(index + 1);
						wordList.remove(index);
						return LocalDate.now().withMonth(month).withDayOfMonth(day);
					}
				}
			}
		}
		return null;
		
	}

	//@author A0111887Y
	//This method helps to identify a date by checking if the word is a numerical number and a valid month.
	private LocalDate findDateWithMonth(MyStringList wordList, int index){

		assert wordList != null && index >= 0;
		if (index + 1 < wordList.size()) {
			int month = getMonth(wordList.get(index));
			if (month > 0) {
				int day = getDay(wordList.get(index + 1));
				if (day > 0) {
					int year;
					if (index + 2 < wordList.size()) {
						year = getYear(wordList.get(index + 2));
						if (year > 0) {
							if (isValidDate(year, month, day)) {
								wordList.remove(index + 2);
								wordList.remove(index + 1);
								wordList.remove(index);
								return LocalDate.of(year, month, day);
							}
						}
					}
					year = LocalDate.now().getYear();
					if (isValidDate(year, month, day)) {
						wordList.remove(index + 1);
						wordList.remove(index);
						return LocalDate.of(year, month, day);
					}
				}
			}
		}
		return null;
		
	}

	//@author A0111887Y
	//This method helps o identify a date by checking if the word is a numerical number and a valid year.
	private LocalDate findDateWithYear(MyStringList wordList, int index){

		assert wordList != null && index >= 0;
		if (index + 2 < wordList.size()) {
			int year = getYear(wordList.get(index));
			if (year > 0) {
				int month = getMonth(wordList.get(index + 1));
				if (month > 0) {
					int day = getDay(wordList.get(index + 2));
					if (day > 0) {
						if (isValidDate(year, month, day)) {
							wordList.remove(index + 2);
							wordList.remove(index + 1);
							wordList.remove(index);
							return LocalDate.of(year, month, day);
						}
					}
				} else {
					int day = getDay(wordList.get(index + 1));
					if (day > 0) {
						month = getMonth(wordList.get(index + 2));
						if (month > 0) {
							if (isValidDate(year, month, day)) {
								wordList.remove(index + 2);
								wordList.remove(index + 1);
								wordList.remove(index);
								return LocalDate.of(year, month, day);
							}
						}
					}
				}
			}
		}
		return null;
		
	}

	//@author A0111887Y
	//This method helps identify a date in the format "DD-MM-YYYY", "YYYY\MM\DD", "MM/DD/YYYY", "DD-MM" etc..
	private LocalDate findDate(MyStringList wordList, int index) {

		assert wordList != null && index >= 0 && index < wordList.size();
		MyStringList stringList = new MyStringList();
		LocalDate date = null;
		if (wordList.get(index).contains("/")) {
			stringList.addAll(Arrays.asList(wordList.get(index).split("/")));
		} else if (wordList.get(index).contains("\\")) {
			stringList.addAll(Arrays.asList(wordList.get(index).split("\\\\")));
		} else if (wordList.get(index).contains("-")) {
			stringList.addAll(Arrays.asList(wordList.get(index).split("-")));
		}
		date = findDateWithDay(stringList, 0);
		if (date == null) {
			date = findDateWithMonth(stringList, 0);
		}
		if (date == null) {
			date = findDateWithYear(stringList, 0);
		}
		if (date != null) {
			wordList.remove(index);
		}
		return date;
		
	}

	//@author A0111887Y
	//This method helps to identify a time by checking for formats "HHam", "HHpm", "HH.MM", "HH:MM:SS", "HH:MM am" etc..
	private LocalTime findTime(MyStringList wordList, int index) {
		
		assert wordList != null && index >= 0 && index < wordList.size();
		String timeWord = wordList.get(index);
		MyStringList s = new MyStringList();
		LocalTime time = null;
		boolean isAM = false;
		boolean isPM = false;
		boolean mayBeTime = false;
		boolean removeNextWord = false;
		for (String tw : KEYWORDS_TIME) {
			if (timeWord.toLowerCase().endsWith(tw)) {
				mayBeTime = true;
				if (tw.equals("am")) {
					isAM = true;
				} else {
					isPM = true;
				}
				timeWord = timeWord.substring(0, timeWord.toLowerCase().lastIndexOf(tw));
				break;
			}
		}
		if (!isAM && !isPM && index + 1 < wordList.size()) {
			for (String tw : KEYWORDS_TIME) {
				if (wordList.get(index + 1).equalsIgnoreCase(tw)) {
					mayBeTime = true;
					if (tw.equals("am")) {
						isAM = true;
					} else {
						isPM = true;
					}
					removeNextWord = true;
					break;
				}
			}
		}
		if (timeWord.contains(":")) {
			s.addAll(Arrays.asList(timeWord.split(":")));
			mayBeTime = true;
		} else if (timeWord.contains(".")) {
			s.addAll(Arrays.asList(timeWord.split("\\.")));
			mayBeTime = true;
		} else if (mayBeTime) {
			s.add(timeWord);
		}
		if (mayBeTime) {
			time = getTime(s, isAM, isPM);
			if (time != null) {
				if (removeNextWord) {
					wordList.remove(index + 1);
				}
				wordList.remove(index);
			}
		}
		return time;
		
	}

	//@author A0111887Y
	//This method helps identify a date by checking for words such as "today", "monday" etc..
	private LocalDate findDateWithWord(MyStringList wordList, int index) {
		
		assert wordList != null && index >= 0 && index < wordList.size();
		String word = wordList.get(index).toLowerCase();
		if (KEYWORDS_DATE_DAY.contains(word)) {
			return getDateWithDayWord(wordList, index);
		} else if (KEYWORDS_DATE_DAY_OF_WEEK.contains(word)){
			return getDateWithDayOfWeekWord(wordList, index);
		}
		return null;
		
	}

	//@author A0111887Y
	//This method helps identify time by checking for words such as "morning", "midnight" etc..
	private LocalTime findTimeWithWord(MyStringList wordList, int index) {
		
		assert wordList != null && index >= 0 && index < wordList.size();
		String word = wordList.get(index).toLowerCase();
		if (KEYWORDS_TIME_OF_DAY.contains(word)) {
			wordList.remove(index);
			return getTimeWithTimeOfDayWord(word);
		}
		return null;
		
	}

	//@author A0111887Y
	//This methods helps find the Task priority by searching for specific keywords in input String.
	private int getPriorityLevelWithWord(MyStringList wordList, int index) {
		
		assert wordList != null;
		int priorityLevel = -1;
		if (index >= 0 && index < wordList.size()) {
			String word = wordList.get(index);
			if (word.equalsIgnoreCase("priority") && index - 1 >= 0) {
				word = wordList.get(index - 1);
				switch (word) {
					case "high" :
						wordList.remove(index);
						index--;
						if (index - 1 < 0 || !wordList.get(index - 1).equalsIgnoreCase("very")) {
							priorityLevel = 1;
							break;
						}
					case "top" :
						wordList.remove(index);
						index--;
						priorityLevel = 2;
						break;
					case "normal" :
					case "low" :
					case "lowest" :
						wordList.remove(index);
						index--;
						priorityLevel = 0;
				}
				if (priorityLevel > -1) {
					wordList.remove(index);
				}
			} else if (word.equalsIgnoreCase("important")) {
				if (index - 1 >= 0) {
					word = wordList.get(index - 1);
				}
				switch (word) {
					case "very" :
						wordList.remove(index);
						index--;
						priorityLevel = 2;
						break;
					case "not" :
						wordList.remove(index);
						index--;
						priorityLevel = 0;
						break;
					default :
						priorityLevel = 1;
				}
				wordList.remove(index);
			} else if (word.equalsIgnoreCase("unimportant")) {
				priorityLevel = 0;
				wordList.remove(index);
			}
		}
		return priorityLevel;
		
	}

	//@author A0111887Y
	//This method returns a date depending on the keywords in the input String.
	private LocalDate getDateWithDayWord(MyStringList wordList, int index) {
		
		assert wordList != null;
		if (index >= 0 && index < wordList.size()) {
			String word = wordList.get(index);
			wordList.remove(index);
			switch (word) {
				case "tonight" :
					wordList.add(index, "night");
				case "today" :
					return LocalDate.now();
				case "tomorrow" :
					return LocalDate.now().plusDays(1);
				case "yesterday" :
					return LocalDate.now().plusDays(-1);
				default :
			}
		}
		return null;
		
	}

	//@author A0111887Y
	//This method returns a date depending on the words in the input String.
	private LocalDate getDateWithDayOfWeekWord(MyStringList wordList, int index) {
		
		assert wordList != null;
		if (index >= 0 && index < wordList.size() && KEYWORDS_DATE_DAY_OF_WEEK.contains(wordList.get(index).toLowerCase())) {
			int day = getNumeric(wordList.get(index).toLowerCase());
			int daysToAdd = day - LocalDate.now().getDayOfWeek().getValue();
			if (daysToAdd < 0) {
				daysToAdd += 7;
			}
			wordList.remove(index);
			return LocalDate.now().plusDays(daysToAdd);
		}
		return null;
		
	}

	//@author A0111887Y
	//This method returns a time depending on the words in the input String.
	private LocalTime getTimeWithTimeOfDayWord(String word) {
		
		switch (word) {
			case "morning" :
				return LocalTime.of(5, 0);
			case "noon" :
				return LocalTime.of(12, 0);
			case "afternoon" :
				return LocalTime.of(13, 0);
			case "evening" :
				return LocalTime.of(17, 0);
			case "night" :
				return LocalTime.of(19, 0);
			case "midnight" :
				return LocalTime.of(23, 59);
			default :
				return null;
		}
		
	}

	//@author A0111887Y
	//This method returns a date depending on the words in the input String.
	private LocalDate getDateWithPeriodWord(MyStringList wordList, int index, int multiplier) {
		
		assert wordList != null && (multiplier == 0 || multiplier == 1 || multiplier == -1);
		if (index >= 0 && index < wordList.size() && KEYWORDS_DATE_PERIOD.contains(wordList.get(index).toLowerCase())) {
			String word = wordList.get(index).toLowerCase();
			wordList.remove(index);
			switch (word) {
				case "day" :
					return LocalDate.now().plusDays(multiplier);
				case "week" :
					return LocalDate.now().plusWeeks(multiplier);
				case "month" :
					return LocalDate.now().plusMonths(multiplier);
				case "year" :
					return LocalDate.now().plusYears(multiplier);
				default :
			}
		}
		return null;
		
	}

	//@author A0111887Y
	//This method returns the day number if the word is a valid numerical day, returns 0 if not.
	private int getDay(String word) {
		
		assert word != null;
		if (word.length() <= 4) {
			if (isNumeric(word)) {
				int num = Integer.parseInt(word);
				if (num <= 31 && num > 0){
					return num;
				}
			} else {
				for (String ordNum : KEYWORDS_DATE_ORDINAL_NUMBER) {
					if (word.endsWith(ordNum)) {
						return getDay(word.substring(0, word.lastIndexOf(ordNum)));
					}
				}
			}
		}
		return 0;
		
	}

	//@author A0111887Y
	//This method returns the month number if the word is a valid numerical month, returns 0 if not.
	private int getMonth(String word) {

		assert word != null;
		if (isNumeric(word)) {
			int num = Integer.parseInt(word);
			if (num <= 12 && num > 0){
				return num;
			}
		} else {
			for (String month : KEYWORDS_DATE_MONTH) {
				if (word.equalsIgnoreCase(month)) {
					return getNumeric(month);
				}
			}
		}
		return 0;
		
	}

	//@author A0111887Y
	//This method returns the year number if the word is a valid numerical year, returns 0 if not.
	private int getYear(String word) {

		assert word != null;
		if (isNumeric(word)) {
			int num = Integer.parseInt(word);
			if (num < 10000 && num > 999){
				return num;
			}
		}
		return 0;
		
	}

	//@author A0111887Y
	//This method returns a time if the lists of words represents a valid time.
	private LocalTime getTime(MyStringList stringList, boolean isAM, boolean isPM) {
		
		assert stringList != null;
		if (!stringList.isEmpty()) {
			for (int index = 0; index < stringList.size(); index++) {
				if (!isNumeric(stringList.get(index))) {
					return null;
				}
			}
			int hour = 0;
			int minute = 0;
			switch (stringList.size()) {
				case 3 :
				case 2 :
					minute = Integer.parseInt(stringList.get(1));
				case 1 :
					hour = Integer.parseInt(stringList.get(0));
			}
			if (hour == 12 && isAM) {
				hour = 0;
			} else if (hour < 12 && isPM) {
				hour += 12;
			}
			if (isValidTime(hour, minute)) {
				return LocalTime.of(hour, minute);
			}
		}
		return null;
		
	}

	//@author A0111887Y
	//This method helps to check if the combination of year, month and day gives a valid date.
	private boolean isValidDate(int year, int month, int day) {
		
		if (year > 0 && month > 0 && day > 0) {
			GregorianCalendar gc = new GregorianCalendar(year, month, 0);
			if (day <= gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH)) {
				return true;
			}
		}
		return false;
		
	}

	//@author A0111887Y
	//This method helps to check if the combination of hour and minute gives a valid time.
	private boolean isValidTime(int hour, int minute) {
		
		if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) {
			return true;
		}
		return false;
		
	}

	//@author A0111887Y
	//This method removes words that are not part of the content and not used to identify any Task data.
	private int removeUselessWord(MyStringList wordList, int index) {

		assert wordList != null;
		if (index >= 0 && index < wordList.size()) {
			for (String uw : USELESS_WORDS) {
				if(wordList.get(index).equalsIgnoreCase(uw)) {
					wordList.remove(index);
					return 1;
				}
			}
		}
		return 0;
		
	}

	//@author A0111887Y
	//This method set the remaining words as the Task content.
	private void setContent(MyStringList wordList, Task task) {
		
		assert wordList != null && task != null;
		if (wordList.size() != 0) {
			StringBuilder sb = new StringBuilder(wordList.get(0));
			for (int index = 1; index < wordList.size(); index++) {
				sb.append(" " + wordList.get(index));
			}
			task.setContent(sb.toString());
		}
		
	}

	//@author A0111887Y
	//This method returns the numeric representation of a word.
	private int getNumeric(String word) {
		
		switch(word){
			case "mon" :
			case "monday" :
			case "jan" :
			case "january" :
				return 1;
			case "tue" :
			case "tuesday" :
			case "feb" :
			case "february" :
				return 2;
			case "wed" :
			case "wednesday" :
			case "mar" :
			case "march" :
				return 3;
			case "thu" :
			case "thursday" :
			case "apr" :
			case "april" :
				return 4;
			case "fri" :
			case "friday" :
			case "may" :
				return 5;
			case "sat" :
			case "saturday" :
			case "jun" :
			case "june" :
				return 6;
			case "sun" :
			case "sunday" :
			case "jul" :
			case "july" :
				return 7;
			case "aug" :
			case "august" :
				return 8;
			case "sep" :
			case "september" :
				return 9;
			case "oct" :
			case "october" :
				return 10;
			case "nov" :
			case "november" :
				return 11;
			case "dec" :
			case "december" :
				return 12;
			default :
				return 0;
		}
		
	}

	//@author A0111887Y
	//This method helps to check if a word is numeric
	private boolean isNumeric(String word) {
		
		assert word != null;
		if (word.length() != 0) {
			ParsePosition pos = new ParsePosition(0);
			FORMATTER.parse(word, pos);
			if (word.length() == pos.getIndex()) {
				return true;
			}
		}
		return false;
		
	}
	
}

//@author A0111887Y
//This class is used to provide 2 additional methods for a String ArrayList.
@SuppressWarnings("serial")
class MyStringList extends ArrayList<String>{
	
	public boolean containsIgnoreCase(String param) {
		
		for (String s : this) {
			if (param.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
		
	}
	
	public void removeIgnoreCase(String param) {
		
		for (String s : this) {
			if (param.equalsIgnoreCase(s)) {
				this.remove(s);
				break;
			}
		}
	
	}
	
}