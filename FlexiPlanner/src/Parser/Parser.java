package Parser;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {

	private final List<String> addCommandWords = Arrays.asList("add", "schedule", "create", "remember");
	private final List<String> modifyCommandWords = Arrays.asList("modify", "edit", "reschedule", "change");
	private final List<String> deleteCommandWords = Arrays.asList("delete", "remove", "clear");
	private final List<String> searchCommandWords = Arrays.asList("display", "show", "find", "search");
	private final List<String> blockCommandWords = Arrays.asList("block", "reserve");
	private final List<String> otherCommandWords = Arrays.asList("exit", "undo", "redo");
	private final List<String> dayWords = Arrays.asList("today", "tomorrow", "yesterday", "tonight");
	private final List<String> timeOfDayWords = Arrays.asList("morning", "noon", "afternoon", "evening", "night", "midnight");
	private final List<String> dayOfWeekWords = Arrays.asList("monday","mon", "tuesday", "tue", "wednesday", "wed", "thursday", "thu", "friday", "fri", "saturday", "sat", "sunday", "sun");
	private final List<String> datePeriodWords = Arrays.asList("day", "week", "month", "year");
	private final List<String> keyWords = Arrays.asList("this", "next", "last");
	private final List<String> moreKeyWords = Arrays.asList("after", "before", "by");
	private final List<String> monthWords = Arrays.asList("jan", "january", "feb", "february", "mar", "march", "apr", "april", "may", "jun", "june", "jul", "july", "aug", "august", "sep", "september", "oct", "october", "nov", "november", "dec", "december");
	private final List<String> ordinalNumWords = Arrays.asList("st", "nd", "rd" ,"th");
	private final List<String> timeWords = Arrays.asList("am", "pm");
	private final List<String> uselessWords = Arrays.asList("the", "on", "from", "to", "@", "at");
	private final NumberFormat formatter = NumberFormat.getInstance();
	private final String CATEGORY_SYMBOL = "#";
	private final String PRIORITY_SYMBOL = "!";
	private final String PRIORITY_VERY_HIGH = "very high";
	private final String PRIORITY_HIGH = "high";
	private final String PRIORITY_NORMAL = "normal";
	private final int MAX_PRIORITY_LEVEL = 2;
	
	public Action getAction(String input) {
		
		MyStringList words = new MyStringList();
		words.addAll(Arrays.asList(input.split(" ")));
		return new Action(getCommand(words), getTask(words));
		
	}
	
	private String getCommand(MyStringList words) {
		
		for (String c : addCommandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return "add";
			}
		}
		for (String c : modifyCommandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return "modify";
			}
		}
		for (String c : deleteCommandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return "delete";
			}
		}
		for (String c : searchCommandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return "search";
			}
		}
		for (String c : blockCommandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return "block";
			}
		}
		for (String c : otherCommandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return c;
			}
		}
		if (isMarkedDone(words)) {
			return "mark done";
		}
		return "add";
		
	}
	
	private boolean isMarkedDone(MyStringList words) {
		
		return false;
		
	}
	
	private Task getTask(MyStringList words) {
		
		Task t = new Task();
		setCategory(words, t);
		setPriority(words, t);
		setDateTime(words, t);
		setContent(words, t);
		fixTaskData(t); //fix datetime and maybe more
		return t;
		
	}
	
	private void setCategory(MyStringList words, Task t) {
		
		for (String word : words) {
			if(word.startsWith(CATEGORY_SYMBOL)) {
				t.setCategory(word.substring(1));
				words.remove(words.indexOf(word));
				break;
			}
		}
		
	}
	
	private void setPriority(MyStringList words, Task t) {

		int priorityLevel = getPriorityLevel(words, MAX_PRIORITY_LEVEL);
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
	
	private int getPriorityLevel(MyStringList words, int priorityLevel) {
		
		if (priorityLevel == 0) {
			return priorityLevel;
		}
		String priorityString = "";
		for (int count = 0; count < priorityLevel; count++) {
			priorityString += PRIORITY_SYMBOL;
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
		return getPriorityLevel(words, priorityLevel - 1);
		
	}
	
	private void fixTaskData(Task t) {
		
		LocalDateTime startDateTime = t.getStartDateTime();
		LocalDateTime endDateTime = t.getEndDateTime();
		if (startDateTime != null && startDateTime.getYear() == 0) {
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
		
		if (ld != null) {
			LocalDateTime startDateTime = t.getStartDateTime();
			LocalDateTime endDateTime = t.getEndDateTime();
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
			LocalDateTime startDateTime = t.getStartDateTime();
			LocalDateTime endDateTime = t.getEndDateTime();
			if (startDateTime == null && endDateTime == null) {
				t.setStartDateTime(LocalDateTime.of(LocalDate.of(0, 1, 1), lt));
			} else if (endDateTime == null) {
				if (startDateTime.getHour() == 0 && startDateTime.getMinute() == 0 && startDateTime.getSecond() == 0) {
					t.setStartDateTime(LocalDateTime.of(LocalDate.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth()), lt));
				} else {
					t.setEndDateTime(LocalDateTime.of(LocalDate.of(0, 1, 1), lt));
				}
			} else {
				t.setEndDateTime(LocalDateTime.of(LocalDate.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth()), lt));
			}
		}
		
	}
	
	private int adjustDateTimeOfTask(Task t, MyStringList words, int index) {

		if (index >= 0) {
			String word = words.get(index).toLowerCase();
			if (moreKeyWords.contains(word)) {
				return changeDateTimeOfTask(t, words, index);
			}
		}
		return 0;
		
	}
	
	private LocalDate findDateWithKeyWord(MyStringList words, int index) {
		
		LocalDate ld = null;
		if (keyWords.contains(words.get(index).toLowerCase())) {
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
		int numOfWordsRemoved = 0;
		switch (word) {
			case "after" :
				numOfWordsRemoved = changeDateTimeWithPeriodWord(t, words, index - 1, 1);
				break;
			case "before" :
				numOfWordsRemoved = changeDateTimeWithPeriodWord(t, words, index - 1, -1);
				if (numOfWordsRemoved != 0) {
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
		words.remove(index);
		numOfWordsRemoved++;
		return numOfWordsRemoved;
		
	}
	
	private int changeDateTimeWithPeriodWord(Task t, MyStringList words, int index, int multiplier) {
		
		if (index >= 0 && datePeriodWords.contains(words.get(index))) {
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
					words.remove(index);
					return 1;
				case "week" :
					if (endDateTime != null) {
						t.setEndDateTime(endDateTime.plusWeeks(multiplier));
					} else {
						t.setStartDateTime(startDateTime.plusWeeks(multiplier));
					}
					words.remove(index);
					return 1;
				case "month" :
					if (endDateTime != null) {
						t.setEndDateTime(endDateTime.plusMonths(multiplier));
					} else {
						t.setStartDateTime(startDateTime.plusMonths(multiplier));
					}
					words.remove(index);
					return 1;
				case "year" :
					if (endDateTime != null) {
						t.setEndDateTime(endDateTime.plusYears(multiplier));
					} else {
						t.setStartDateTime(startDateTime.plusYears(multiplier));
					}
					words.remove(index);
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
		int hrToAdd = 0;
		boolean noAmPm = true;
		for (String tw : timeWords) {
			if (time.toLowerCase().endsWith(tw)) {
				noAmPm = false;
				if (tw.equals("pm")) {
					hrToAdd = 12;
				}
				time = time.substring(0, time.toLowerCase().lastIndexOf(tw));
				break;
			}
		}
		if (noAmPm && index + 1 < words.size()) {
			for (String tw : timeWords) {
				if (words.get(index + 1).equalsIgnoreCase(tw)) {
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
		} else if (time.contains(".")) {
			s.addAll(Arrays.asList(time.split(".")));
		} else {
			s.add(time);
		}
		LocalTime lt = getTime(s, hrToAdd);
		if (lt != null) {
			words.remove(index);
		}
		return lt;
		
	}
	
	private LocalDate findDateWithWord(MyStringList words, int index) {
		
		String word = words.get(index).toLowerCase();
		if (dayWords.contains(word)) {
			return getDateWithDayWord(words, index);
		} else if (dayOfWeekWords.contains(word)){
			return getDateWithDayOfWeekWord(words, index);
		}
		return null;
		
	}
	
	private LocalTime findTimeWithWord(MyStringList words, int index) {
		
		String word = words.get(index).toLowerCase();
		if (timeOfDayWords.contains(word)) {
			words.remove(index);
			return getTimeWithTimeOfDayWord(word);
		}
		return null;
		
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
		
		if (index < words.size() && datePeriodWords.contains(words.get(index))) {
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
				for (String onw : ordinalNumWords) {
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
			for (String m : monthWords) {
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
			for (String uw : uselessWords) {
				if(words.get(index).equalsIgnoreCase(uw)) {
					words.remove(index);
					return 1;
				}
			}
		}
		return 0;
		
	}
	
	private void setContent(MyStringList words, Task t) {
		
		StringBuilder sb = new StringBuilder();
		for (String w : words) {
			sb.append(w + " ");
		}
		t.setContent(sb.toString());
		
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
		formatter.parse(word, pos);
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
