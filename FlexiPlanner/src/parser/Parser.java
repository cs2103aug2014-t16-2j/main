package parser;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class translates an input String into Task data.
 * 
 * @author Choo Xin Min (A0111887Y)
 */

public class Parser {

	private final List<String> KEYWORDS_COMMAND_ADD = Arrays.asList("add", "schedule", "create", "remember");
	private final List<String> KEYWORDS_COMMAND_MODIFY = Arrays.asList("modify", "edit", "reschedule", "change");
	private final List<String> KEYWORDS_COMMAND_DELETE = Arrays.asList("delete", "remove", "clear");
	private final List<String> KEYWORDS_COMMAND_SEARCH = Arrays.asList("display", "show", "find", "search");
	private final List<String> KEYWORDS_COMMAND_BLOCK = Arrays.asList("block", "reserve");
	private final List<String> KEYWORDS_COMMAND_UNBLOCK = Arrays.asList("unblock", "unreserve", "free");
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
	private final NumberFormat FORMATTER = NumberFormat.getInstance();
	private final String SYMBOL_CATEGORY = "#";
	private final String SYMBOL_PRIORITY = "!";
	private final String PRIORITY_VERY_HIGH = "very high";
	private final String PRIORITY_HIGH = "high";
	private final String PRIORITY_NORMAL = "normal";
	private final int MAX_PRIORITY_LEVEL = 2;
	
	public Action getAction(String input) {
		
		MyStringList words = new MyStringList();
		words.addAll(Arrays.asList(input.split(" ")));
		removeReminder(words);
		return new Action(getCommand(words), getTask(words));
		
	}
	
	private void removeReminder(MyStringList words) {
		
		int startIndex = 0;
		int endIndex = 0;
		for (int index = 0; index < words.size(); index++) {
			if (words.get(index).startsWith("\"")) {
				startIndex = index;
				break;
			}
		}
		for (int index = 0; index < words.size(); index++) {
			if (words.get(index).endsWith("\"")) {
				if (index > startIndex) {
					endIndex = index;
					break;
				}
			}
		}
		if (endIndex != 0) {
			for (int index = startIndex; index < endIndex + 1; index++) {
				words.remove(index);
			}
		}
		
	}
	
	//This methods find the word representing a command and returns the command String.
	private Command getCommand(MyStringList words) {
		
		for (String c : KEYWORDS_COMMAND_ADD) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return Command.ADD;
			}
		}
		for (String c : KEYWORDS_COMMAND_MODIFY) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return Command.MODIFY;
			}
		}
		for (String c : KEYWORDS_COMMAND_DELETE) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return Command.DELETE;
			}
		}
		for (String c : KEYWORDS_COMMAND_SEARCH) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return Command.SEARCH;
			}
		}
		for (String c : KEYWORDS_COMMAND_BLOCK) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return Command.BLOCK;
			}
		}
		for (String c : KEYWORDS_COMMAND_UNBLOCK) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return Command.UNBLOCK;
			}
		}
		for (String c : KEYWORDS_COMMAND_OTHER) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				switch (c) {
					case "undo" :
						return Command.UNDO;
					case "redo" :
						return Command.REDO;
					case "exit" :
						return Command.EXIT;
				}
			}
		}
		if (isMarked(words)) {
			return Command.MARK;
		}
		return Command.ADD;
		
	}
	
	//This method returns true if there are words that represent marking a Task as done.
	private boolean isMarked(MyStringList words) {
		
		if (words.containsIgnoreCase("mark")) {
			words.removeIgnoreCase("mark");
			return true;
		}
		for (int index = 0; index < words.size(); index++) {
			String word = words.get(index).toLowerCase();
			if (KEYWORDS_MARK.contains(word)) {
				return true;
			}
		}
		return false;
		
	}
	
	//This method creates a Task object with data translated from user input
	private Task getTask(MyStringList words) {
		
		Task t = new Task();
		setCategory(words, t);
		setPriority(words, t);
		setDateTime(words, t);
		setDone(words, t);
		setContent(words, t);
		fixDateTime(t);
		fixContent(t);
		return t;
		
	}
	
	private void setCategory(MyStringList words, Task t) {
		
		for (String word : words) {
			if(word.startsWith(SYMBOL_CATEGORY)) {
				t.setCategory(word.substring(1));
				words.remove(words.indexOf(word));
				break;
			}
		}
		
	}
	
	private void setPriority(MyStringList words, Task t) {

		int priorityLevel = findPriorityLevelWithWord(words);
		if (priorityLevel == -1) {
			priorityLevel = findPriorityLevelWithSymbol(words, MAX_PRIORITY_LEVEL);
		}
		assert (priorityLevel < 2 && priorityLevel >= 0);
		switch (priorityLevel) {
			case 2 :
				t.setPriority(PRIORITY_VERY_HIGH);
				break;
			case 1 :
				t.setPriority(PRIORITY_HIGH);
				break;
			case 0 :
				t.setPriority(PRIORITY_NORMAL);
		}
		
	}
	
	private int findPriorityLevelWithWord(MyStringList words) {
		
		for (int index = 0; index < words.size(); index++) {
			if (KEYWORDS_PRIORITY.contains(words.get(index).toLowerCase())) {
				return getPriorityLevelWithWord(words, index);
			}
		}
		return -1;
		
	}
	
	private int findPriorityLevelWithSymbol(MyStringList words, int priorityLevel) {
		
		if (priorityLevel == 0) {
			return -1;
		}
		String priorityString = "";
		for (int count = 0; count < priorityLevel; count++) {
			priorityString += SYMBOL_PRIORITY;
		}
		for (int index = 0; index < words.size(); index++) {
			String word = words.get(index);
			if (word.endsWith(priorityString)) {
				word = word.substring(0, word.lastIndexOf(priorityString));
				if (word.isEmpty()) {
					words.remove(index);
				} else {
					words.set(index, word);
				}
				return priorityLevel;
			}
		}
		return findPriorityLevelWithSymbol(words, priorityLevel - 1);
		
	}
	
	private void setDone(MyStringList words, Task t) {

		for (int index = 0; index < words.size(); index++) {
			String word = words.get(index).toLowerCase();
			if (KEYWORDS_MARK.contains(word)) {
				if (word.equals("done") || word.equals("complete") || word.equals("completed")) {
					t.setDone(true);
				}
				words.remove(index);
				changeDone(words, index - 1, t);
				break;
			}
		}
		
	}
	
	private void changeDone(MyStringList words, int index, Task t) {
		
		String word = words.get(index).toLowerCase();
		boolean isDone;
		while (SECONDARY_KEYWORDS_MARK.contains(word)) {
			isDone = t.isDone();
			switch (word) {
				case "yet" :
					if (words.get(index - 1).toLowerCase().equals("not")) {
						words.remove(index);
						index--;
						break;
					}
				case "to" :
				case "be" :
				case "not" :
					t.setDone(isDone ^= true);
				default :
					words.remove(index);
					index--;
			}
			if (index >= 0) {
				word = words.get(index).toLowerCase();
			} else {
				break;
			}
		}
		
	}
	
	private void fixDateTime(Task t) {
		
		LocalDateTime startDateTime = t.getStartDateTime();
		LocalDateTime endDateTime = t.getEndDateTime();
		if (startDateTime != null) {
			if (startDateTime.getYear() == 0) {
				t.setStartDateTime(LocalDateTime.now().withHour(startDateTime.getHour()).withMinute(startDateTime.getMinute()).withSecond(startDateTime.getSecond()));
				startDateTime = t.getStartDateTime();
			}
			if (endDateTime != null) {
				if (endDateTime.getYear() == 0) {
					t.setEndDateTime(startDateTime.withHour(endDateTime.getHour()).withMinute(endDateTime.getMinute()).withSecond(endDateTime.getSecond()));
					endDateTime = t.getEndDateTime();
				}
				if (startDateTime.isAfter(endDateTime)) {
					t.setEndDateTime(endDateTime.plusWeeks(1));
					endDateTime = t.getEndDateTime();
				}
				if (endDateTime.getHour() == 0 && endDateTime.getMinute() == 0 && endDateTime.getSecond() == 0) {
					t.setEndDateTime(endDateTime.withHour(23).withMinute(59).withSecond(59));
				}
			} else {
				t.setEndDateTime(startDateTime.withHour(23).withMinute(59).withSecond(59));
			}
		}
		
	}
	
	private void fixContent(Task t) {
		
		String content = t.getContent();
		if (content != null && isNumeric(content)) {
			t.setIndex(Integer.parseInt(content));
		}
		
	}
	
	private void setDateTime(MyStringList words, Task t) {
		
		for (int index = 0; index < words.size(); index++) {
			index = findDateTime(words, t, index);
		}
		
	}

	private int findDateTime(MyStringList words, Task t, int index) {
		
		LocalDate ld = null;
		LocalTime lt = null;
		if (words.get(index).isEmpty()) {
			words.remove(index);
		}
		if (words.get(index).length() <= 4) {
			ld = findDateWithDay(words, index);
			if (ld == null) {
				ld = findDateWithYear(words, index);
			}
		}
		if (ld == null) {
			ld = findDateWithMonth(words, index);
		}
		if (ld == null) {
			ld = findDate(words, index);
		}
		if (ld == null) {
			ld = findDateWithWord(words, index);//get date with dayofweek or "next" or "last" or "by"
		}
		if (ld == null) {
			ld = findDateWithKeyWord(words, index);
		}
		if (ld == null) {
			lt = findTime(words, index);
		}
		if (ld == null && lt == null) {
			lt = findTimeWithWord(words, index);
		}
		if (ld != null || lt != null) {
			setDateTimeToTask(t, ld, lt);
			index -= adjustDateTimeOfTask(t, words, index - 1);
			index -= 1 + removeUselessWord(words, index - 1);
		}
		return index;
		
	}
	
	private void setDateTimeToTask(Task t, LocalDate ld, LocalTime lt) {
		
		LocalDateTime startDateTime = t.getStartDateTime();
		LocalDateTime endDateTime = t.getEndDateTime();
		if (ld != null) {
			if (startDateTime == null && endDateTime == null) {
				t.setStartDateTime(LocalDateTime.of(ld, LocalTime.of(0, 0)));
			} else if (endDateTime == null) {
				if (startDateTime.getYear() == 0) {
					t.setStartDateTime(LocalDateTime.of(ld, LocalTime.of(startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond())));
				} else {
					t.setEndDateTime(LocalDateTime.of(ld, LocalTime.of(0, 0)));
				}
			} else {
				t.setEndDateTime(LocalDateTime.of(ld, LocalTime.of(endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond())));
			}
		} else {
			if (startDateTime == null && endDateTime == null) {
				t.setStartDateTime(LocalDateTime.of(LocalDate.of(0, 1, 1), lt));
			} else if (endDateTime == null) {
				if (startDateTime.getHour() == 0 && startDateTime.getMinute() == 0 && startDateTime.getSecond() == 0) {
					t.setStartDateTime(LocalDateTime.of(LocalDate.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth()), lt));
				} else {
					t.setEndDateTime(LocalDateTime.of(LocalDate.of(0, 1, 1), lt));
				}
			} else if (endDateTime.getHour() == 0 && endDateTime.getMinute() == 0 && endDateTime.getSecond() == 0) {
				t.setEndDateTime(LocalDateTime.of(LocalDate.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth()), lt));
			} else if (startDateTime.getHour() == 0 && startDateTime.getMinute() == 0 && startDateTime.getSecond() == 0) {
				t.setStartDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth(), endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
				t.setEndDateTime(LocalDateTime.of(LocalDate.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth()), lt));
			}
		}
		
	}
	
	private int adjustDateTimeOfTask(Task t, MyStringList words, int index) {

		if (index >= 0) {
			String word = words.get(index).toLowerCase();
			if (SECONDARY_KEYWORDS_DATE_TIME.contains(word)) {
				return changeDateTimeOfTask(t, words, index);
			}
		}
		return 0;
		
	}
	
	private LocalDate findDateWithKeyWord(MyStringList words, int index) {
		
		LocalDate ld = null;
		if (SECONDARY_KEYWORDS_DATE.contains(words.get(index).toLowerCase())) {
			ld = getDateWithDayOfWeekWord(words, index + 1);
			switch (words.get(index)) {
				case "this" :
					if (ld == null) {
						ld = getDateWithPeriodWord(words, index + 1, 0);
					}
					break;
				case "next" :
					if (ld != null) {
						ld = ld.plusWeeks(1);
					} else {
						ld = getDateWithPeriodWord(words, index + 1, 1);
					}
					break;
				case "last" :
					if (ld != null) {
						ld = ld.plusWeeks(-1);
					} else {
						ld = getDateWithPeriodWord(words, index + 1, -1);
					}
				default :
			}
			if (ld != null) {
				words.remove(index);
			}
		}
		return ld;
		
	}
	
	private int changeDateTimeOfTask(Task t, MyStringList words, int index) {
		
		String word = words.get(index).toLowerCase();
		int numOfWordsToRemove = 0;
		switch (word) {
			case "after" :
				numOfWordsToRemove = changeDateTimeWithPeriodWord(t, words, index - 1, 1);
				break;
			case "before" :
				numOfWordsToRemove = changeDateTimeWithPeriodWord(t, words, index - 1, -1);
				if (numOfWordsToRemove != 0) {
					break;
				}
			case "by" :
				LocalDateTime startDateTime = t.getStartDateTime();
				LocalDateTime endDateTime = t.getEndDateTime();
				if (endDateTime == null) {
					t.setEndDateTime(startDateTime);
					t.setStartDateTime(LocalDateTime.now());
				}
		}
		numOfWordsToRemove++;
		for (int i = numOfWordsToRemove; i > 0; i--) {
			words.remove(index);
			index--;
		}
		if (words.get(index).toLowerCase().equals("the")) {
			numOfWordsToRemove++;
			words.remove(index);
		}
		return numOfWordsToRemove;
		
	}
	
	private int changeDateTimeWithPeriodWord(Task t, MyStringList words, int index, int multiplier) {
		
		if (index >= 0 && KEYWORDS_DATE_PERIOD.contains(words.get(index))) {
			String word = words.get(index).toLowerCase();
			LocalDateTime startDateTime = t.getStartDateTime();
			LocalDateTime endDateTime = t.getEndDateTime();
			switch (word) {
				case "day" :
					if (endDateTime != null) {
						t.setEndDateTime(endDateTime.plusDays(multiplier));
					} else {
						t.setStartDateTime(startDateTime.plusDays(multiplier));
					}
					return 1;
				case "week" :
					if (endDateTime != null) {
						t.setEndDateTime(endDateTime.plusWeeks(multiplier));
					} else {
						t.setStartDateTime(startDateTime.plusWeeks(multiplier));
					}
					return 1;
				case "month" :
					if (endDateTime != null) {
						t.setEndDateTime(endDateTime.plusMonths(multiplier));
					} else {
						t.setStartDateTime(startDateTime.plusMonths(multiplier));
					}
					return 1;
				case "year" :
					if (endDateTime != null) {
						t.setEndDateTime(endDateTime.plusYears(multiplier));
					} else {
						t.setStartDateTime(startDateTime.plusYears(multiplier));
					}
					return 1;
				default :
			}
		}
		return 0;
		
	}
	
	private LocalDate findDateWithDay(MyStringList words, int index){
		
		if (index + 1 < words.size()) {
			int day = getDay(words.get(index));
			if (day > 0) {
				int mth = getMonth(words.get(index + 1));
				if (mth > 0) {
					try {
						int yr = getYear(words.get(index + 2));
						if (yr > 0) {
							words.remove(index + 2);
							words.remove(index + 1);
							words.remove(index);
							return LocalDate.of(yr, mth, day);
						}
					} catch (Exception e) {
					}
					words.remove(index + 1);
					words.remove(index);
					return LocalDate.now().withMonth(mth).withDayOfMonth(day);
				}
			}
		}
		return null;
		
	}
	
	private LocalDate findDateWithMonth(MyStringList words, int index){

		if (index + 1 < words.size()) {
			int mth = getMonth(words.get(index));
			if (mth > 0) {
				int day = getDay(words.get(index + 1));
				if (day > 0) {
					try {
						int yr = getYear(words.get(index + 2));
						if (yr > 0) {
							words.remove(index + 2);
							words.remove(index + 1);
							words.remove(index);
							return LocalDate.of(yr, mth, day);
						}
					} catch (Exception e) {
					}
					words.remove(index + 1);
					words.remove(index);
					return LocalDate.now().withMonth(mth).withDayOfMonth(day);
				}
			}
		}
		return null;
		
	}
	
	private LocalDate findDateWithYear(MyStringList words, int index){

		if (index + 2 < words.size()) {
			int yr = getYear(words.get(index));
			if (yr > 0) {
				int mth = getMonth(words.get(index + 1));
				if (mth > 0) {
					int day = getDay(words.get( + 2));
					if (day > 0) {
						words.remove(index + 2);
						words.remove(index + 1);
						words.remove(index);
						return LocalDate.of(yr, mth, day);
					}
				} else {
					int day = getDay(words.get(index + 1));
					if (day > 0) {
						mth = getMonth(words.get(index + 2));
						if (mth > 0) {
							words.remove(index + 2);
							words.remove(index + 1);
							words.remove(index);
							return LocalDate.of(yr, mth, day);
						}
					}
				}
			}
		}
		return null;
		
	}
	
	private LocalDate findDate(MyStringList words, int index) {

		MyStringList s = new MyStringList();
		LocalDate ld = null;
		if (words.get(index).contains("/")) {
			s.addAll(Arrays.asList(words.get(index).split("/")));
		} else if (words.get(index).contains("\\")) {
			s.addAll(Arrays.asList(words.get(index).split("\\")));
		} else if (words.get(index).contains("-")) {
			s.addAll(Arrays.asList(words.get(index).split("-")));
		}
		ld = findDateWithDay(s, 0);
		if (ld == null) {
			ld = findDateWithMonth(s, 0);
		}
		if (ld == null) {
			ld = findDateWithYear(s, 0);
		}
		if (ld != null) {
			words.remove(index);
		}
		return ld;
		
	}
	
	private LocalTime findTime(MyStringList words, int index) {
		
		String time = words.get(index);
		MyStringList s = new MyStringList();
		LocalTime lt = null;
		int hrToAdd = 0;
		boolean noAmPm = true;
		boolean mayBeTime = false;
		for (String tw : KEYWORDS_TIME) {
			if (time.toLowerCase().endsWith(tw)) {
				noAmPm = false;
				mayBeTime = true;
				if (tw.equals("pm")) {
					hrToAdd = 12;
				}
				time = time.substring(0, time.toLowerCase().lastIndexOf(tw));
				break;
			}
		}
		if (noAmPm && index + 1 < words.size()) {
			for (String tw : KEYWORDS_TIME) {
				if (words.get(index + 1).equalsIgnoreCase(tw)) {
					mayBeTime = true;
					if (tw.equals("pm")) {
						hrToAdd = 12;
					}
					words.remove(index + 1);
					break;
				}
			}
		}
		if (time.contains(":")) {
			s.addAll(Arrays.asList(time.split(":")));
			mayBeTime = true;
		} else if (time.contains(".")) {
			s.addAll(Arrays.asList(time.split(".")));
			mayBeTime = true;
		} else if (mayBeTime) {
			s.add(time);
		}
		if (mayBeTime) {
			lt = getTime(s, hrToAdd);
			if (lt != null) {
				words.remove(index);
			}
		}
		return lt;
		
	}
	
	private LocalDate findDateWithWord(MyStringList words, int index) {
		
		String word = words.get(index).toLowerCase();
		if (KEYWORDS_DATE_DAY.contains(word)) {
			return getDateWithDayWord(words, index);
		} else if (KEYWORDS_DATE_DAY_OF_WEEK.contains(word)){
			return getDateWithDayOfWeekWord(words, index);
		}
		return null;
		
	}
	
	private LocalTime findTimeWithWord(MyStringList words, int index) {
		
		String word = words.get(index).toLowerCase();
		if (KEYWORDS_TIME_OF_DAY.contains(word)) {
			words.remove(index);
			return getTimeWithTimeOfDayWord(word);
		}
		return null;
		
	}
	
	private int getPriorityLevelWithWord(MyStringList words, int index) {
		
		int priorityLevel = -1;
		String word = words.get(index);
		if (word.equalsIgnoreCase("priority") && index - 1 >= 0) {
			word = words.get(index - 1);
			switch (word) {
				case "high" :
					words.remove(index);
					index--;
					if (!words.get(index - 1).equalsIgnoreCase("very")) {
						priorityLevel = 1;
						break;
					}
				case "top" :
					words.remove(index);
					index--;
					priorityLevel = 2;
					break;
				case "normal" :
				case "low" :
				case "lowest" :
					words.remove(index);
					index--;
					priorityLevel = 0;
			}
			if (priorityLevel > -1) {
				words.remove(index);
			}
		} else if (word.equalsIgnoreCase("important")) {
			if (index - 1 >= 0) {
				word = words.get(index - 1);
			}
			switch (word) {
				case "very" :
					words.remove(index);
					index--;
					priorityLevel = 2;
					break;
				case "not" :
					words.remove(index);
					index--;
					priorityLevel = 0;
					break;
				default :
					priorityLevel = 1;
			}
			words.remove(index);
		} else if (word.equalsIgnoreCase("unimportant")) {
			priorityLevel = 0;
			words.remove(index);
		}
		return priorityLevel;
		
	}
	
	private LocalDate getDateWithDayWord(MyStringList words, int index) {
		
		String word = words.get(index);
		words.remove(index);
		switch (word) {
			case "tonight" :
				words.add(index, "night");
			case "today" :
				return LocalDate.now();
			case "tomorrow" :
				return LocalDate.now().plusDays(1);
			case "yesterday" :
				return LocalDate.now().plusDays(-1);
			default :
		}
		return null;
		
	}
	
	private LocalDate getDateWithDayOfWeekWord(MyStringList words, int index) {
		
		if (index < words.size()) {
			int day = getNumeric(words.get(index).toLowerCase());
			int daysToAdd = day - LocalDate.now().getDayOfWeek().getValue();
			if (daysToAdd < 0) {
				daysToAdd += 7;
			}
			words.remove(index);
			return LocalDate.now().plusDays(daysToAdd);
		}
		return null;
		
	}
	
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
				return LocalTime.of(23, 59, 59);
			default :
				return null;
		}
		
	}
	
	private LocalDate getDateWithPeriodWord(MyStringList words, int index, int multiplier) {
		
		if (index < words.size() && KEYWORDS_DATE_PERIOD.contains(words.get(index))) {
			switch (words.get(index).toLowerCase()) {
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
	
	private int getDay(String s) {
		
		if (s.length() <= 4) {
			if (isNumeric(s)) {
				int num = Integer.parseInt(s);
				if (num <= 31 && num > 0){
					return num;
				}
			} else {
				for (String onw : KEYWORDS_DATE_ORDINAL_NUMBER) {
					if (s.endsWith(onw)) {
						return getDay(s.substring(0, s.lastIndexOf(onw)));
					}
				}
			}
		}
		return 0;
		
	}
	
	private int getMonth(String s) {
		
		if (isNumeric(s)) {
			int num = Integer.parseInt(s);
			if (num <= 12 && num > 0){
				return num;
			}
		} else {
			for (String m : KEYWORDS_DATE_MONTH) {
				if (s.equalsIgnoreCase(m)) {
					return getNumeric(m);
				}
			}
		}
		return 0;
		
	}
	
	private int getYear(String s) {
		
		if (isNumeric(s)) {
			int num = Integer.parseInt(s);
			if (num < 10000 && num > 999){
				return num;
			}
		}
		return 0;
		
	}
	
	private LocalTime getTime(MyStringList s, int hrToAdd) {
		
		if (!s.isEmpty()) {
			for (int i = 0; i < s.size(); i++) {
				if (!isNumeric(s.get(i))) {
					return null;
				}
			}
			int hr = 0;
			int min = 0;
			int sec = 0;
			switch (s.size()) {
				case 3 :
					sec = Integer.parseInt(s.get(2));
				case 2 :
					min = Integer.parseInt(s.get(1));
				case 1 :
					hr = Integer.parseInt(s.get(0));
			}
			if (hr != 12 && hr + hrToAdd <= 24) {
				hr += hrToAdd;
			}
			try {
				return LocalTime.of(hr, min, sec);
			} catch (DateTimeException dte) {
			}
		}
		return null;
		
	}
	
	private int removeUselessWord(MyStringList words, int index) {
		
		if (index >= 0) {
			for (String uw : USELESS_WORDS) {
				if(words.get(index).equalsIgnoreCase(uw)) {
					words.remove(index);
					return 1;
				}
			}
		}
		return 0;
		
	}
	
	private void setContent(MyStringList words, Task t) {
		
		if (words.size() != 0) {
			StringBuilder sb = new StringBuilder(words.get(0));
			for (int index = 1; index < words.size(); index++) {
				sb.append(" " + words.get(index));
			}
			t.setContent(sb.toString());
		}
		
	}
	
	private int getNumeric(String w) {
		
		switch(w){
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
	
	private boolean isNumeric(String word) {
		
		ParsePosition pos = new ParsePosition(0);
		FORMATTER.parse(word, pos);
		if (word.length() == pos.getIndex()) {
			return true;
		}
		return false;
	}
}

class MyStringList extends ArrayList<String>{
	
	public boolean containsIgnoreCase(String param) {
		
		for (String s : this) {
			if (param.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
		
	}
	
	public int indexOfIgnoreCase(String param) {
		
		for (String s : this) {
			if (param.equalsIgnoreCase(s)) {
				return this.indexOf(s);
			}
		}
		return -1;
		
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
