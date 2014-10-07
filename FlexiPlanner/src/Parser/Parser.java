package Parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
//import java.util.Scanner;

public class Parser {

	//private Scanner sc = new Scanner(System.in);
	private final List<String> addCommandWords = Arrays.asList("add", "schedule", "create");
	private final List<String> modifyCommandWords = Arrays.asList("modify", "edit", "reschedule", "change");
	private final List<String> deleteCommandWords = Arrays.asList("delete", "remove", "clear");
	private final List<String> otherCommandWords = Arrays.asList("exit", "undo", "redo");
	private final List<String> uselessWords = Arrays.asList("on", "from", "to", "@", "at");
	private final List<String> monthWords = Arrays.asList("jan", "january", "feb", "febuary", "mar", "march", "apr", "april", "may", "jun", "june", "jul", "july", "aug", "august", "sep", "september", "oct", "october", "nov", "november", "dec", "december");
	private final List<String> ordinalNumWords = Arrays.asList("st", "nd", "rd" ,"th");
	private final List<String> timeWords = Arrays.asList("am","pm");
	
	/*public static void main(String[] args){
		Parser p = new Parser();
		String input=p.sc.nextLine();
		MyStringList words = new MyStringList();
		words.addAll(Arrays.asList(input.split(" ")));
		Action a = new Action(p.getCommand(words), p.getTask(words));
		System.out.println(a.getCommand());
		System.out.println(a.getTask().getStartDateTime());
		System.out.println(a.getTask().getEndDateTime());
		System.out.println(a.getTask().getContent());
	}*/
	
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
			String w = words.get(index);
			LocalDate ld = null;
			LocalTime lt = null;
			if (w.isEmpty()) {
				words.remove(index);
			} else if (w.length() <= 4) {
				ld = findDateWithDay(words, index, gc);
				if (ld == null) {
					ld = findDateWithMonth(words, index, gc);
				}
				if (ld == null) {
					ld = findDateWithYear(words, index, gc);
				}
				if (ld == null) {
					lt = findTime(words, index);
				}
			} else {
				ld = findDate(words, index, gc);
				if (ld == null) {
					ld = findDateWithMonth(words, index, gc);
				}
				if (ld == null) {
					lt = findTime(words, index);
				}
			}
			if (ld != null) {
				t.setDate(ld);
				index -= 1 + removeUselessWord(words, index);
			}
			if (lt != null) {
				t.setTime(lt);
				index -= 1 + removeUselessWord(words, index);
			}
		}
		
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
		for (String tw : timeWords) {
			if (time.toLowerCase().endsWith(tw)) {
				if (tw.equals("pm")) {
					hrToAdd = 12;
				}
				time = time.substring(0, time.toLowerCase().lastIndexOf(tw));
				break;
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
	
	private void findContent(MyStringList words, Task t) {
		
		StringBuilder sb = new StringBuilder();
		for (String w : words) {
			sb.append(w + " ");
		}
		t.setContent(sb.toString());
		
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



