package Parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

public class Parser {

	private final List<String> addCommandWords = Arrays.asList("add", "schedule", "create", "remember");
	private final List<String> modifyCommandWords = Arrays.asList("modify", "edit", "reschedule", "change");
	private final List<String> deleteCommandWords = Arrays.asList("delete", "remove", "clear");
	private final List<String> searchCommandWords = Arrays.asList("display", "show", "find", "search");
	private final List<String> otherCommandWords = Arrays.asList("exit", "undo", "redo");
	private final List<String> dayWords = Arrays.asList("today", "tomorrow", "yesterday", "morning", "afternoon", "night", "tonight");
	private final List<String> dayOfWeekWords = Arrays.asList("monday","mon", "tuesday", "tue", "wednesday", "wed", "thursday", "thu", "friday", "fri", "saturday", "sat", "sunday", "sun");
	private final List<String> datePeriodWords = Arrays.asList("day", "month", "year");
	private final List<String> dateTimeKeyWords = Arrays.asList("after", "next", "by", "before", "last");
	private final List<String> monthWords = Arrays.asList("jan", "january", "feb", "febuary", "mar", "march", "apr", "april", "may", "jun", "june", "jul", "july", "aug", "august", "sep", "september", "oct", "october", "nov", "november", "dec", "december");
	private final List<String> ordinalNumWords = Arrays.asList("st", "nd", "rd" ,"th");
	private final List<String> timeWords = Arrays.asList("am", "pm");
	private final List<String> moreUselessWords = Arrays.asList("on", "from", "to", "@", "at");
	private final List<String> uselessWords = Arrays.asList("this");
	
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
		for (String c : otherCommandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return c;
			}
		}
		return "add";
		
	}
	
	private Task getTask(MyStringList words) {
		
		Task t = new Task();
		findDateTime(words, t);
		findContent(words, t);
		return t;
		
	}
	
	private void findDateTime(MyStringList words, Task t) {
		
		GregorianCalendar gc = new GregorianCalendar();
		for (int index = 0; index < words.size(); index++) {
			index = findDateTime(words, t, gc, index);
		}
		
	}

	private int findDateTime(MyStringList words, Task t, GregorianCalendar gc, int index) {
		
		LocalDate ld = null;
		LocalTime lt = null;
		if (words.get(index).isEmpty()) {
			words.remove(index);
		}
		if (words.get(index).length() <= 4) {
			ld = findDateWithDay(words, index, gc);
			if (ld == null) {
				ld = findDateWithYear(words, index, gc);
			}
		}
		if (ld == null) {
			ld = findDateWithMonth(words, index, gc);
		}
		if (ld == null) {
			ld = findDate(words, index, gc);
		}
		if (ld == null) {
			ld = findDateWithWord(words, index, gc);//get date with dayofweek or "next" or "last" or "by"
		}
		if (ld == null) {
			lt = findTime(words, index);
		}
		if (ld != null || lt != null) {
			LocalDateTime ldt = getDateTime(ld, lt);
			ldt = adjustDateTime(words, index, ldt);//adjust date with "after" or "before"
			setDateTimeToTask(ldt);
			index -= removeUselessWord(words, index);
			index -= 1 + removeMoreUselessWord(words, index);
		}
		return index;
		
	}
	
	private LocalDate findDateWithDay(MyStringList words, int index, GregorianCalendar gc){
		
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
					return LocalDate.of(gc.get(GregorianCalendar.YEAR), mth, day);
				}
			}
		}
		return null;
		
	}
	
	private LocalDate findDateWithMonth(MyStringList words, int index, GregorianCalendar gc){

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
					return LocalDate.of(gc.get(GregorianCalendar.YEAR), mth, day);
				}
			}
		}
		return null;
		
	}
	
	private LocalDate findDateWithYear(MyStringList words, int index, GregorianCalendar gc){

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
	
	private LocalDate findDate(MyStringList words, int index, GregorianCalendar gc) {

		MyStringList s = new MyStringList();
		LocalDate ld = null;
		if (words.get(index).contains("/")) {
			s.addAll(Arrays.asList(words.get(index).split("/")));
		} else if (words.get(index).contains("\\")) {
			s.addAll(Arrays.asList(words.get(index).split("\\")));
		} else if (words.get(index).contains("-")) {
			s.addAll(Arrays.asList(words.get(index).split("-")));
		}
		ld = findDateWithDay(s, 0, gc);
		if (ld == null) {
			ld = findDateWithMonth(s, 0, gc);
		}
		if (ld == null) {
			ld = findDateWithYear(s, 0, gc);
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
	
	private int getDay(String s) {
		
		if (s.length() <= 4) {
			try {
				int num = Integer.parseInt(s);
				if (num <= 31 && num > 0){
					return num;
				}
			} catch (Exception e) {
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
		
		try {
			int num = Integer.parseInt(s);
			if (num <= 12 && num > 0){
				return num;
			}
		} catch (Exception e) {
			for (String m : monthWords) {
				if (s.equalsIgnoreCase(m)) {
					return getNumericMonth(m);
				}
			}
		}
		return 0;
		
	}
	
	private int getYear(String s) {
		
		try {
			int num = Integer.parseInt(s);
			if (num < 10000 && num > 999){
				return num;
			}
		} catch (Exception e) {
		}
		return 0;
		
	}
	
	private LocalTime getTime(MyStringList s, int hrToAdd) {
		
		if (!s.isEmpty()) {
			try {
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
				return LocalTime.of(hr, min, sec);
			} catch (Exception e) {
			}
		}
		return null;
		
	}
	
	private int removeUselessWord(MyStringList words, int index) {
		
		if (index - 1 >= 0) {
			for (String uw : uselessWords) {
				if(words.get(index - 1).equalsIgnoreCase(uw)) {
					words.remove(index - 1);
					return 1;
				}
			}
		}
		return 0;
		
	}
	
	private int removeMoreUselessWord(MyStringList words, int index) {
		
		if (index - 1 >= 0) {
			for (String uw : moreUselessWords) {
				if(words.get(index - 1).equalsIgnoreCase(uw)) {
					words.remove(index - 1);
					return 1;
				}
			}
		}
		return 0;
		
	}
	
	private void findContent(MyStringList words, Task t) {
		
		StringBuilder sb = new StringBuilder();
		for (String w : words) {
			sb.append(w + " ");
		}
		t.setContent(sb.toString());
		
	}
	
	private LocalDate addDay(String s, LocalDate ld) {

		switch(s){
			case "day" :
				return ld.withDayOfMonth(ld.getDayOfMonth() + 1);
			case "month" :
				return ld.withMonth(ld.getMonth().getValue() + 1);
			case "year" :
				return ld.withYear(ld.getYear() + 1);
			default: return null;
		}
		
	}
	
	private int getNumericMonth(String m) {
		
		switch(m){
			case "jan":
			case "january": return 1;
			case "feb":
			case "febuary": return 2;
			case "mar":
			case "march": return 3;
			case "apr":
			case "april": return 4;
			case "may": return 5;
			case "jun":
			case "june": return 6;
			case "jul":
			case "july": return 7;
			case "aug":
			case "august": return 8;
			case "sep":
			case "september": return 9;
			case "oct":
			case "october": return 10;
			case "nov":
			case "november": return 11;
			case "dec":
			case "december": return 12;
			default: return 0;
		}
		
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